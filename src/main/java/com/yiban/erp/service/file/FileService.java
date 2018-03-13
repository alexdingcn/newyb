package com.yiban.erp.service.file;

import com.yiban.erp.constant.FileStatus;
import com.yiban.erp.constant.FileUploadType;
import com.yiban.erp.dao.FileInfoMapper;
import com.yiban.erp.dao.FileTypeMapper;
import com.yiban.erp.dao.FileUploadMapper;
import com.yiban.erp.entities.FileInfo;
import com.yiban.erp.entities.FileType;
import com.yiban.erp.entities.FileUpload;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 档案处理类
 */
@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private FileTypeMapper fileTypeMapper;
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private FileUploadMapper fileUploadMapper;

    @Value("${file.get.base.url}")
    private String fileBaseUrl; //读取或者获取文件的基础访问路径
    @Value("${file.save.base.location}")
    private String baseLocation; //本地保存文件的基础路径

    /**
     * 获取档案类型列表
     * @param companyId
     * @return
     */
    public List<FileType> getFileTypeList(Integer companyId) {
        if (companyId == null || companyId < 0) {
            return Collections.emptyList();
        }
        return fileTypeMapper.getAll(companyId);
    }

    /**
     * 新建档案类型
     * @param user
     * @param typeName
     * @return
     * @throws BizException
     */
    public FileType addFileType(User user, String typeName) throws BizException {
        if (user == null || user.getCompanyId() == null || StringUtils.isBlank(typeName)) {
            logger.warn("add file type but params is error.");
            throw new BizException(ErrorCode.FILE_TYPE_CREATE_PARAMS_ERROR);
        }
        String name = typeName.trim();
        FileType fileType = new FileType();
        fileType.setCompanyId(user.getCompanyId());
        fileType.setTypeName(name);
        fileType.setCreateBy(user.getNickname());
        fileType.setUpdateBy(user.getNickname());
        fileType.setCreateTime(new Date());
        fileType.setUpdateTime(new Date());
        try {
            int count = fileTypeMapper.insertSelective(fileType);
            if (count > 0) {
                return fileType;
            }else {
                logger.warn("insert database fail.");
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }catch (DuplicateKeyException e) {
            logger.warn("user:{} add file type {} but type name is exist.", user.getNickname(), name);
            throw new BizException(ErrorCode.FILE_TYPE_EXIST);
        }
    }

    /**
     * 获取档案列表
     * @param companyId 必须
     * @param fileType 档案类型
     * @param fileName 档案名称(模糊匹配)
     * @param page 页码
     * @param size 每页条数
     * @return
     */
    public List<FileInfo> getList(Integer companyId, String fileType, String fileName, Integer page, Integer size) {
        if (companyId == null) {
            logger.warn("get file info list but company id is null.");
            return Collections.emptyList();
        }
        Integer limit = (size == null || size <= 0) ? null : size;
        Integer offset = 0;
        if (limit != null) {
            offset = (page == null || page <= 0 ? 0 : page - 1) * limit;
        }
        return fileInfoMapper.getByFileTypeAndName(companyId, fileType, fileName, offset, limit);
    }

    public Integer getListCount(Integer companyId, String fileType, String fileName) {
        return fileInfoMapper.getByFileTypeAndNameCount(companyId, fileType, fileName);
    }

    public FileInfo addFileInfo(User user, FileInfo fileInfo) throws BizException {
        if (user == null || user.getCompanyId() == null) {
            logger.warn("add file info but user is null.");
            throw new BizRuntimeException(ErrorCode.USER_GET_FAIL);
        }
        FileInfo reqData = UtilTool.trimString(fileInfo);
        reqData.setFileNo(getFileNo(user.getCompanyId())); //设置档案编号
        if (!validateFileIfo(reqData)) {
            logger.warn("validate add file info fail. user:{}", user.getNickname());
            throw new BizException(ErrorCode.FILE_ADD_PARAMS_ERROR);
        }
        reqData.setFileStatus(FileStatus.NORMAL.name().toLowerCase());
        reqData.setCreateBy(user.getNickname());
        reqData.setCreateTime(new Date());
        reqData.setUpdateBy(user.getNickname());
        reqData.setUpdateTime(new Date());
        int count = 0;
        try {
            count =  fileInfoMapper.insertSelective(reqData);
        }catch (DuplicateKeyException e) {
            reqData.setFileNo(getFileNo(user.getCompanyId())); //因为唯一键失败时，重新获取档案编号再次插入
            count = fileInfoMapper.insertSelective(reqData);
        }
        if (count > 0 && reqData.getId() > 0) {
            logger.info("user:{} add file info success. id:{}", user.getNickname(), reqData.getId());
            return reqData;
        }else {
            logger.warn("user:{} add file info insert database fail.", user.getNickname());
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    private boolean validateFileIfo(FileInfo fileInfo) {
        if (fileInfo == null) {
            return false;
        }
        if (fileInfo.getFileNo() == null) {
            logger.warn("validate file info but file no is null");
            return false;
        }
        if (fileInfo.getFileName() == null) {
            logger.warn("validate file info but file name is null.");
            return false;
        }
        if (fileInfo.getFileType() == null) {
            logger.warn("validate file info but file type is null");
            return false;
        }
        return true;
    }

    /**
     * 生成档案编号的规则
     * 公司ID + yyMMddhhmmss + 3位随机数
     * @param companyId
     * @return
     */
    private String getFileNo(Integer companyId) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.valueOf(companyId));
        sb.append(UtilTool.DateFormat(new Date(), "yyMMddHHmmss"));
        sb.append(RandomStringUtils.random(3));
        return sb.toString();
    }

    /**
     * 删除档案
     * @param user
     * @param ids
     * @return
     */
    public int removeFileInfo(User user, List<Integer> ids) throws BizException {
        if (ids == null || ids.isEmpty()) {
            logger.warn("remove file info but id list is empty.");
            throw new BizException(ErrorCode.FILE_REMOVE_PARAMS_ERROR);
        }
        return fileInfoMapper.removeByIds(ids, user.getNickname(), new Date());
    }

    /**
     * 保存上传的档案文件
     * @param user
     * @param fileId
     * @param comment
     * @param file
     * @return
     */
    public String saveFile(User user, Integer fileId, String comment, MultipartFile file) throws BizException {
        FileInfo fileInfo = fileInfoMapper.selectByPrimaryKey(fileId);
        if (fileInfo == null || FileStatus.DELETE.name().equalsIgnoreCase(fileInfo.getFileStatus())) {
            logger.warn("save file but can not get the file info by id:{}", fileId);
            throw new BizException(ErrorCode.FILE_UPLOAD_PARAMS_ERROR);
        }
        String originalName = file.getOriginalFilename();
        if (!FileUploadType.validate(originalName)) {
            logger.warn("user:{} upload file but type is not enable. file original name:{}", user.getId(), originalName);
            throw new BizException(ErrorCode.FILE_UPLOAD_FILE_TYPE_ERROR);
        }
        String[] results = persistenceFile(user.getCompanyId(), fileId, file);
        String location = results[0];
        String url = results[1];

        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileId(fileId);
        fileUpload.setOriginalName(originalName);
        fileUpload.setComment(comment);
        fileUpload.setLocation(location);
        fileUpload.setLoadUrl(url);
        fileUpload.setCreateBy(user.getNickname());
        fileUpload.setCreateTime(new Date());
        fileUpload.setUpdateBy(user.getNickname());
        fileUpload.setUpdateTime(new Date());

        int count = fileUploadMapper.insertSelective(fileUpload);
        if (count > 0) {
            logger.info("user:{} upload file success. original name:{} url:{}", user.getId(), originalName, url);
            return url;
        }else {
            logger.warn("user:{} upload persistence file success but insert database fail.", user.getId());
            removeLocationFile(location); //如果入库失败，把本地文件也删除
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    /**
     * 持久化文件(写入磁盘)
     * @param file
     * @return  返回两个值的数组，第一个是文件的定位标识，第二个是访问文件的url路径
     *  如果成功，必须两个值都返回，失败直接抛出异常
     * @throws BizException
     */
    private String[] persistenceFile(Integer companyId, Integer fileId, MultipartFile file) throws BizException {
        //TODO 后续使用其他保存文件的方式，例如挂载阿里的NAS服务

        //上传文件的本地存储路径：baseLocation/companyId/yyyyMM/
        //上传的文件名重命名为: fileId_yyMMddHHmmss+2位随机数.xxx
        String originalName = file.getOriginalFilename();
        Date currentTime = new Date();
        String yearMonth = UtilTool.DateFormat(currentTime, "yyyyMM");
        String dateTimeStr = UtilTool.DateFormat(currentTime, "yyMMddHHmmss");
        StringBuilder fileName = new StringBuilder();
        fileName.append(String.valueOf(fileId));
        fileName.append("_");
        fileName.append(dateTimeStr);
        fileName.append(RandomStringUtils.random(2));
        if (originalName.lastIndexOf('.') > 0) {
            fileName.append(originalName.substring(originalName.lastIndexOf('.') - 1));
        }
        String location = companyId + File.separator + yearMonth + File.separator + fileName.toString();
        File saveFile = new File(baseLocation + File.separator + location);
        String url = fileBaseUrl + "/" + location;
        try {
            FileOutputStream os = new FileOutputStream(saveFile);
            BufferedOutputStream out = new BufferedOutputStream(os);
            out.write(file.getBytes());
            out.flush();
            out.close();
            os.close();

            String[] result = {location, url};
            return result;
        }catch (Exception e) {
            logger.error("write file have exception. ", e);
            throw new BizRuntimeException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    /**
     * 根据文件位置删除文件
     * @param location
     */
    private void removeLocationFile(String location) {
        try{
            File file = new File(baseLocation + File.separator + location);
            if (file.exists() && file.isFile()) {
                logger.info("remove location:{} file.", location);
                file.delete();
            }
        }catch (Exception e) {
            logger.error("remove location:{} file fail.", location);
        }
    }

    public void deleteFileUpload(User user, Integer id) throws BizException {
        FileUpload fileUpload = fileUploadMapper.selectByPrimaryKey(id);
        if (fileUpload == null) {
            logger.warn("user:{} request delete file upload record but get fail by id:{}", user.getId(), id);
            return;
        }
        //根据location删除持久化文件
        removeLocationFile(fileUpload.getLocation());
        int count = fileUploadMapper.deleteByPrimaryKey(id);
        if (count > 0) {
            logger.info("user:{} delete file upload record success, id:{}", user.getId(), id);
        }else {
            logger.warn("user:{} delete file upload record fail. id:{}", user.getId(), id);
            throw new BizRuntimeException(ErrorCode.FAILED_DELETE_FROM_DB);
        }
    }
}

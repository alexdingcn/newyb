package com.yiban.erp.service.goods;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dao.GoodsSpecMapper;
import com.yiban.erp.dto.GoodsSpecForm;
import com.yiban.erp.entities.GoodsSpec;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class GoodsSpecService {

    private static final int GOODS_SPEC_CACHE_DAYS = 7;

    private static final Logger logger = LoggerFactory.getLogger(GoodsSpecService.class);

    @Autowired
    private GoodsSpecMapper specMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public List<GoodsSpecForm> getListForm(Integer companyId) {
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        String cacheValue = operations.get(companyId.toString());
        List<GoodsSpecForm> forms = new ArrayList<>();
        if (StringUtils.isNotBlank(cacheValue)) {
            forms = JSON.parseArray(cacheValue, GoodsSpecForm.class);
        }
        if (forms != null && !forms.isEmpty()) {
            return forms;
        }
        forms = resetGoodsSpecCache(companyId);
        return forms;
    }

    private List<GoodsSpecForm> transferToForm(List<GoodsSpec> goodsSpecs) {
        Map<Long, GoodsSpecForm> tempMap = new HashMap<>();
        for (GoodsSpec spec : goodsSpecs) {
            if (spec.getParentId() == null) {
                continue;
            }
            if (!tempMap.containsKey(spec.getParentId())) {
                GoodsSpecForm form = new GoodsSpecForm();
                form.setParentId(spec.getParentId());
                form.setParentName(spec.getParentName());
                form.setParentNo(spec.getParentNo());
                List<GoodsSpec> subSpecs = new ArrayList<>();
                subSpecs.add(spec);
                form.setSubGoodsSpecs(subSpecs);

                tempMap.put(spec.getParentId(), form);
            }else {
                GoodsSpecForm form = tempMap.get(spec.getParentId());
                List<GoodsSpec> subSpecs = form.getSubGoodsSpecs();
                subSpecs.add(spec);
            }
        }

        //把map转换换到list
        List<GoodsSpecForm> forms = new ArrayList<>();
        for (Map.Entry<Long, GoodsSpecForm> entry : tempMap.entrySet()) {
            forms.add(entry.getValue());
        }
        return forms;
    }

    private List<GoodsSpecForm> resetGoodsSpecCache(Integer companyId) {
        List<GoodsSpec> goodsSpecs = specMapper.getByCompanyId(companyId);
        if (goodsSpecs.isEmpty()) {
            return Collections.emptyList();
        }
        //如果不为空值, 则根据数据组织出对应的结果集
        List<GoodsSpecForm> forms = transferToForm(goodsSpecs);
        String cacheStr = JSON.toJSONString(forms);
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(companyId.toString(), cacheStr, GOODS_SPEC_CACHE_DAYS, TimeUnit.DAYS);

        return forms;
    }


    public void save(GoodsSpecForm form, User user) throws BizException {
        if (form == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        if (StringUtils.isEmpty(form.getParentName()) || StringUtils.isEmpty(form.getParentNo())) {
            logger.warn("save good spec but parent name or no is null.");
            throw new BizException(ErrorCode.GOODS_SPEC_NAME_NO_NULL);
        }
        if (form.getSubGoodsSpecs() == null || form.getSubGoodsSpecs().isEmpty()) {
            logger.warn("save goods spec but sub goods spec is empty.");
            throw new BizException(ErrorCode.GOODS_SPEC_SUB_SPEC_EMPTY);
        }
        List<GoodsSpec> subGoodsSpecs = form.getSubGoodsSpecs();
        for (GoodsSpec spec : subGoodsSpecs) {
            if (StringUtils.isEmpty(spec.getSpecName()) || StringUtils.isEmpty(spec.getSpecNo())) {
                logger.warn("sub spec name or spec no is null.");
                throw new BizException(ErrorCode.GOODS_SPEC_NAME_NO_NULL);
            }
        }
        //如果存在有parentId,则进行修改，如果没有新建
        if (form.getParentId() == null) {
            //是创建的新规格，走创建逻辑
            createGoodsSpec(form, user);
        }else {
            //走修改逻辑
            updateGoodsSpec(form, user);
        }
    }

    @Transactional
    public void createGoodsSpec(GoodsSpecForm form, User user) {
        GoodsSpec parentSpec = new GoodsSpec();
        parentSpec.setCompanyId(user.getCompanyId());
        parentSpec.setSpecName(form.getParentName());
        parentSpec.setSpecNo(form.getParentNo());
        parentSpec.setCreatedBy(user.getNickname());
        parentSpec.setCreatedTime(new Date());
        int parentInsert = specMapper.insert(parentSpec);
        if (parentInsert<=0|| parentSpec.getId() == null) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("user:{} created goods parent spec success, id:{}", user.getId(), parentSpec.getId());
        //创建每一个子规格
        List<GoodsSpec> subGoodsSpecs = form.getSubGoodsSpecs();
        for (GoodsSpec spec : subGoodsSpecs) {
            spec.setCompanyId(user.getCompanyId());
            spec.setParentId(parentSpec.getId());
            spec.setParentName(parentSpec.getSpecName());
            spec.setParentNo(parentSpec.getSpecNo());
            spec.setCreatedBy(user.getNickname());
            spec.setCreatedTime(new Date());
        }
        int count = specMapper.insertBatch(subGoodsSpecs);
        logger.info("user:{} created goods sub spec count:{}", user.getId(), count);

        //重新获取公司的所有规格设置到缓存中
        resetGoodsSpecCache(user.getCompanyId());
    }

    @Transactional
    public void updateGoodsSpec(GoodsSpecForm form, User user) {
        //对于父级的，直接调用修改逻辑
        GoodsSpec parentSpec = new GoodsSpec();
        parentSpec.setId(form.getParentId());
        parentSpec.setSpecName(form.getParentName());
        parentSpec.setSpecNo(form.getParentNo());
        parentSpec.setUpdatedBy(user.getNickname());
        parentSpec.setUpdatedTime(new Date());

        int parentUpdate = specMapper.updateByPrimaryKeySelective(parentSpec);
        if (parentUpdate <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_UPDATE_FROM_DB);
        }
        logger.info("update Goods spec parent level success, id:{}", parentSpec.getId());

        List<GoodsSpec> subGoodsSpecs = form.getSubGoodsSpecs();
        for (GoodsSpec spec : subGoodsSpecs) {
            //如果ID不存在，直接创建，如果ID存在，进行修改
            spec.setParentName(parentSpec.getSpecName());
            spec.setParentNo(parentSpec.getSpecNo());
            spec.setParentId(parentSpec.getId());
            if (spec.getId() != null) {
                spec.setUpdatedBy(user.getNickname());
                spec.setUpdatedTime(new Date());
                specMapper.updateByPrimaryKeySelective(spec);
            }else {
                logger.info("create a new sub spec for parent id:{}", parentSpec.getId());
                spec.setCompanyId(user.getCompanyId());
                spec.setCreatedBy(user.getNickname());
                spec.setCreatedTime(new Date());
                specMapper.insert(spec);
            }
        }

        //重新获取公司的所有规格设置到缓存中
        resetGoodsSpecCache(user.getCompanyId());
    }

    public void remove(Long specId) throws BizException {
        //先获取数据记录
        GoodsSpec spec = specMapper.selectByPrimaryKey(specId);
        if (spec == null) {
            logger.warn("get goods spec fail by id:{}", specId);
            throw new BizException(ErrorCode.GOODS_SPEC_GET_FAIL);
        }
        //验证自身和其子规格是否有被使用，如果使用了，不能删除
        List<Long> specIdList = new ArrayList<>();
        specIdList.add(specId);
        if(spec.getParentId() == null) {
            //如果上级标识中是空值，则表明这个规格是父级的，需要验证当前父下的所有规格是否有使用的，如果存在有则不能删除
            //获取所有的子级
            List<GoodsSpec> subSpecs = specMapper.getSubSpecs(specId);
            if (subSpecs != null && !subSpecs.isEmpty()) {
                subSpecs.stream().forEach(item -> specIdList.add(item.getId()));
            }
        }

        //是否有产品使用了，如果使用了，不能删除
        boolean isUsed = specMapper.isGoodsSpecUsed(specIdList);
        if (isUsed) {
            logger.warn("have goods user the spec can not remove.");
            throw new BizException(ErrorCode.GOODS_SPEC_USED_CANNOT_REMOVE);
        }
        //如果没有使用的，直接删除
        specMapper.deleteByIds(specIdList);
        logger.info("delete goods spec success. ids:{}", JSON.toJSONString(specIdList));
        //更新缓存
        resetGoodsSpecCache(spec.getCompanyId());
    }
}

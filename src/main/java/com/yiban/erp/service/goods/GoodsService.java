package com.yiban.erp.service.goods;

import com.yiban.erp.constant.GoodsStatus;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.GoodsDetailMapper;
import com.yiban.erp.dao.GoodsInfoMapper;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dao.OptionsMapper;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GoodsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private OptionsMapper optionsMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private GoodsDetailMapper goodsDetailMapper;


    public void setGoodsOptionName(List<Goods> goodsList) {
        if (goodsList == null || goodsList.isEmpty()) {
            return;
        }
        //设置对应的Option的Name值
        Set<Long> optionIdSet = new HashSet<>();
        goodsList.stream().forEach(item -> {
            optionIdSet.addAll(item.getOptionIdList());
        });
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goodsList.stream().forEach(item -> item.setOptionName(options));
    }

    public void setGoodsOptionName(Goods goods) {
        if (goods == null) {
            return;
        }
        Set<Long> set = goods.getOptionIdList();
        if (set.size() < 0) {
            return;
        }
        Long[] ids = new Long[set.size()];
        set.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goods.setOptionName(options);
    }

    public List<Goods> getGoodsById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Goods> goodsList = goodsMapper.selectByIdList(ids);
        setGoodsOptionName(goodsList);
        return goodsList;
    }

    public Goods getGoodsById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        setGoodsOptionName(goods);
        return goods;
    }


    public void saveGoodsInfo(GoodsInfo goodsInfo, User user) throws BizException {
        if (StringUtils.isEmpty(goodsInfo.getName()) || goodsInfo.getUnit() == null || goodsInfo.getUseSpec() == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //如果存在有ID，则认为是修改，如果不存在ID，认为是添加
        if (goodsInfo.getId() != null) {
            updateGoodsInfo(goodsInfo, user);
        }else {
            createdGoodsInfo(goodsInfo, user);
        }
    }

    @Transactional
    public void createdGoodsInfo(GoodsInfo goodsInfo, User user) throws BizException {
        List<GoodsDetail> details = goodsInfo.getGoodsDetails();
        boolean useSpec = goodsInfo.getUseSpec() == null ? false : goodsInfo.getUseSpec();

        //直接把数据入库
        goodsInfo.setCompanyId(user.getCompanyId());
        goodsInfo.setUseSpec(useSpec);
        if (goodsInfo.getStatus() == null) {
            goodsInfo.setStatus(GoodsStatus.OFF_SALE.name());
        }
        goodsInfo.setGoodsNo(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.GOODS));
        goodsInfo.setEnable(true);
        goodsInfo.setCreatedBy(user.getNickname());
        goodsInfo.setCreatedTime(new Date());
        int count = goodsInfoMapper.insert(goodsInfo);
        if (count <= 0 || goodsInfo.getId() == null) {
            logger.warn("insert goods info fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        //如果是不分规格的，直接造一个detail的值然后入库，如果分规则，需要更加
        if (!useSpec) {
            GoodsDetail detail = new GoodsDetail();
            detail.setCompanyId(user.getCompanyId());
            detail.setStatus(GoodsStatus.NORMAL.name());
            detail.setSkuKey(getSkuKey(null, goodsInfo.getId()));
            detail.setBarCode(goodsInfo.getBarCode());
            detail.setBatchPrice(goodsInfo.getBatchPrice());
            detail.setRetailPrice(goodsInfo.getRetailPrice());
            detail.setInPrice(goodsInfo.getInPrice());
            detail.setCreatedBy(user.getNickname());
            detail.setCreatedTime(new Date());
            int detailCount = goodsDetailMapper.insert(detail);
            if (detailCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }else {
            for (GoodsDetail detail : details) {
                detail.setCompanyId(user.getCompanyId());
                detail.setStatus(GoodsStatus.NORMAL.name());
                if (detail.getBarCode() == null && goodsInfo.getBarCode() != null) {
                    detail.setBarCode(goodsInfo.getBarCode());
                }
                detail.setSkuKey(getSkuKey(detail, goodsInfo.getId()));
                detail.setCreatedBy(user.getNickname());
                detail.setCreatedTime(new Date());
            }
            int detailsCount = goodsDetailMapper.insertBatch(details);
            if (detailsCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }
    }

    private String getSkuKey(GoodsDetail detail, Long goodsInfoId) {
        StringBuilder sb = new StringBuilder();
        sb.append(goodsInfoId);
        if (detail == null) {
            return sb.toString();
        }
        //如果不为空，获取三个规格ID，先对规格的ID进行从小到大排序，然后再组合在一起
        long specOneId = detail.getSpecOneId() != null ? detail.getSpecOneId() : -1L;
        long specTwoId = detail.getSpecTwoId() != null ? detail.getSpecTwoId() : -1L;
        long specThreeId = detail.getSpecThreeId() != null ? detail.getSpecThreeId() : -1L;
        long[] sortArray = {specOneId, specTwoId, specThreeId};
        Arrays.sort(sortArray);
        for (long specId : sortArray) {
            if (specId > 0) {
                sb.append("-");
                sb.append(specId);
            }
        }
        return sb.toString();
    }

    @Transactional
    public void updateGoodsInfo(GoodsInfo goodsInfo, User user) throws BizException {

    }

}

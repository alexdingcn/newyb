package com.yiban.erp.service.goods;

import com.yiban.erp.dao.GoodsDetailMapper;
import com.yiban.erp.dao.GoodsInfoMapper;
import com.yiban.erp.dao.PriceRuleMapper;
import com.yiban.erp.dto.PriceUpdateReq;
import com.yiban.erp.entities.GoodsDetail;
import com.yiban.erp.entities.GoodsInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class GoodsPriceService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsPriceService.class);
    private static final String BATCH_PRICE = "batchPrice";
    private static final String RETAIL_PRICE = "retailPrice";
    private static final String IN_PRICE = "inPrice";

    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private GoodsDetailMapper goodsDetailMapper;
    @Autowired
    private PriceRuleMapper priceRuleMapper;

    @Transactional
    public List<GoodsDetail> updateBasePrice(PriceUpdateReq updateReq, User user) throws BizException {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(updateReq.getInfoId());
        if (goodsInfo == null || !goodsInfo.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get goods info fail by infoId:{}", updateReq.getInfoId());
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        List<Long> detailIds = updateReq.getDetailIds();
        boolean isAllDetailUpdate = (detailIds == null || detailIds.isEmpty()) ? true : false;
        // 获取详情记录
        List<GoodsDetail> details = goodsDetailMapper.getByGoodsInfoId(goodsInfo.getId(), false);
        final Map<Long, GoodsDetail> detailMap = new HashMap<>();
        details.stream().forEach(item -> detailMap.put(item.getId(), item));


        BigDecimal newBatchPrice = null;
        BigDecimal newRetailPrice = null;
        BigDecimal newInPrice = null;
        if (updateReq.getBatchPrice() != null && BigDecimal.ZERO.compareTo(updateReq.getBatchPrice()) <= 0) {
            newBatchPrice = updateReq.getBatchPrice();
            // 修改了批发价
            logger.info("info {} update batch price, oldPrice:{} newPrice:{}",
                    goodsInfo.getId(), goodsInfo.getBatchPrice(),newBatchPrice);
            goodsInfo.setBatchPrice(newBatchPrice);
        }
        if (updateReq.getRetailPrice() != null && BigDecimal.ZERO.compareTo(updateReq.getRetailPrice()) <= 0) {
            newRetailPrice = updateReq.getRetailPrice();
            logger.info("info {} update retail price, oldPrice:{} newPrice:{}",
                    goodsInfo.getId(), goodsInfo.getRetailPrice(), newRetailPrice);
            goodsInfo.setRetailPrice(newRetailPrice);
        }
        if (updateReq.getInPrice() != null && BigDecimal.ZERO.compareTo(updateReq.getInPrice()) <= 0) {
            newInPrice = updateReq.getInPrice();
            logger.info("info {} update in price, oldPrice:{} newPrice:{}",
                    goodsInfo.getId(), goodsInfo.getInPrice(), newInPrice);
            goodsInfo.setInPrice(newInPrice);
        }

        final List<GoodsDetail> updateDetails = getDetailUpdateList(isAllDetailUpdate, detailIds, newBatchPrice,
                newRetailPrice, newInPrice, detailMap);

        goodsInfo.setUpdatedBy(user.getNickname());
        goodsInfo.setUpdatedTime(new Date());

        //修改数据库
        int count = goodsInfoMapper.updatePrice(goodsInfo);
        logger.info("update info price count:{}", count);

        for (GoodsDetail detail : updateDetails) {
            goodsDetailMapper.updatePrice(detail);
        }

        //结果返回更新后的详情信息，用于前端刷新详情信息
        return goodsDetailMapper.getByGoodsInfoIds(Arrays.asList(goodsInfo.getId()));
    }

    private List<GoodsDetail> getDetailUpdateList(boolean isAll, List<Long> detailIds,
                                                  BigDecimal newBatchPrice, BigDecimal newRetailPrice, BigDecimal newInPrice,
                                                  Map<Long, GoodsDetail> detailMap) {
        List<GoodsDetail> result = new ArrayList<>();
        if (isAll) {
            for (Map.Entry<Long, GoodsDetail> entry : detailMap.entrySet()) {
                GoodsDetail detail = entry.getValue();
                setDetailNewBasePrice(detail, newBatchPrice, newRetailPrice, newInPrice);
                result.add(detail);
            }
        }else {
            for (Long detailId : detailIds) {
                GoodsDetail detail = detailMap.get(detailId);
                if (detail != null) {
                    setDetailNewBasePrice(detail, newBatchPrice, newRetailPrice, newInPrice);
                    result.add(detail);
                }
            }
        }
        return result;
    }

    private void setDetailNewBasePrice(GoodsDetail detail, BigDecimal newBatchPrice, BigDecimal newRetailPrice, BigDecimal newInPrice) {
        if (detail == null) {
            return;
        }
        if (newBatchPrice != null && BigDecimal.ZERO.compareTo(newBatchPrice) <= 0) {
            logger.info("{} update batch price, oldPrice:{} newPrice:{}",
                    detail.getId(), detail.getBatchPrice(), newBatchPrice);
            detail.setBatchPrice(newBatchPrice);
        }
        if (newRetailPrice != null && BigDecimal.ZERO.compareTo(newRetailPrice) <= 0) {
            logger.info("{} update retail price, oldPrice:{} newPrice:{}",
                    detail.getId(), detail.getRetailPrice(), newRetailPrice);
            detail.setRetailPrice(newRetailPrice);
        }
        if (newInPrice != null && BigDecimal.ZERO.compareTo(newInPrice) <= 0) {
            logger.info("{} update in price, oldPrice:{} newPrice:{}",
                    detail.getId(), detail.getInPrice(), newInPrice);
            detail.setInPrice(newInPrice);
        }
        return;
    }

}

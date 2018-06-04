package com.yiban.erp.service.goods;

import com.yiban.erp.dao.*;
import com.yiban.erp.dto.CustomerCategoryPrice;
import com.yiban.erp.dto.PriceQuery;
import com.yiban.erp.dto.PriceUpdateReq;
import com.yiban.erp.dto.SavePriceReq;
import com.yiban.erp.entities.*;
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
    @Autowired
    private CustomerCategoryMapper customerCategoryMapper;
    @Autowired
    private CustomerMapper customerMapper;


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

    public List<CustomerCategoryPrice> getCustomerCategoryPrice(List<Long> goodsDetailIds, User user) {
        if (goodsDetailIds == null || goodsDetailIds.isEmpty()) {
            logger.warn("goods detail ids is empty.");
            return Collections.emptyList();
        }
        List<CustomerCategory> categories = customerCategoryMapper.getAllByCompanyId(user.getCompanyId());
        List<CustomerCategoryPrice> categoryPrices = new ArrayList<>();
        for (CustomerCategory category : categories) {
            CustomerCategoryPrice price = new CustomerCategoryPrice();
            price.setCustomerCategoryId(category.getId());
            price.setCustomerCategoryName(category.getName());
            price.setBatchDiscount(category.getBatchDiscount() == null ? BigDecimal.valueOf(100.00) : category.getRetailDiscount());
            price.setRetailDiscount(category.getRetailDiscount() == null ? BigDecimal.valueOf(100.00) : category.getRetailDiscount());

            categoryPrices.add(price);
        }

        //如果detailIds 的长度为1的时候，获取这个商品详情的所有客户类别的价格配置信息
        if (goodsDetailIds.size() == 1) {
            List<PriceRule> priceRules = priceRuleMapper.getByGoodsId(goodsDetailIds.get(0));
            if (priceRules != null && !priceRules.isEmpty()) {
                Map<Integer, PriceRule> priceRuleMap = new HashMap<>(); //按照customerCategoryID分
                priceRules.stream().forEach(item -> {
                    if (item.getCustomerCategoryId() != null) {
                        priceRuleMap.put(item.getCustomerCategoryId(), item);
                    }
                });
                for (CustomerCategoryPrice price : categoryPrices) {
                    PriceRule rule  = priceRuleMap.get(price.getCustomerCategoryId());
                    if (rule != null) {
                        price.setBatchPrice(rule.getBatchPrice());
                        price.setRetailPrice(rule.getRetailPrice());
                    }
                }
            }
        }
        return categoryPrices;
    }

    public PriceRule getCustomerPrice(Long goodsId, Long customerId) {
        return priceRuleMapper.getByCustomer(goodsId, customerId);
    }

    public void categorySave(SavePriceReq priceReq, User user) throws BizException {
        if (priceReq.getGoodsDetailIds() == null || priceReq.getGoodsDetailIds().isEmpty()) {
            logger.warn("get goods detail ids is empty.");
            throw new BizException(ErrorCode.GOODS_DETAIL_GET_FAIL);
        }
        if (priceReq.getCustomerCategoryPrices() == null || priceReq.getCustomerCategoryPrices().isEmpty()) {
            logger.warn("get customer category prices is empty.");
            throw new BizException(ErrorCode.GOODS_PRICE_REQUEST_LIST_EMPTY);
        }
        List<Long> goodsIds = priceReq.getGoodsDetailIds();
        //一个个产品ID处理
        for (Long goodsId : goodsIds) {
            if (goodsId != null) {
                saveCategoryByGoodsId(goodsId, priceReq.getCustomerCategoryPrices(), user);
            }
        }
    }

    /**
     * 获取一个客户的特定价格，优先级为: 指定价 > 客户类型价 > 商品配置价，key为商品ID
     * @param query
     * @param user
     * @return
     * @throws BizException
     */
    public Map<Long, PriceRule> getCustomerPriceByGoodsList(PriceQuery query, User user) throws BizException {
        final Map<Long, PriceRule> result = new HashMap<>();
        if (query == null || query.getGoodsDetailIds() == null || query.getGoodsDetailIds().isEmpty()) {
            return result;
        }
        //先获取商品的配置价格信息
        List<GoodsDetail> goodsDetails = goodsDetailMapper.getByDetailIdList(query.getGoodsDetailIds());
        //根据ID，制作一个基础的结果集
        goodsDetails.stream().forEach(item -> {
            PriceRule priceItem = new PriceRule();
            priceItem.setGoodsId(item.getId());
            priceItem.setBatchPrice(item.getBatchPrice() == null ? BigDecimal.ZERO : item.getBatchPrice());
            priceItem.setRetailPrice(item.getRetailPrice() == null ? BigDecimal.ZERO : item.getRetailPrice());
            result.put(item.getId(), priceItem);
        });

        //如果客户ID存在，获取客户信息
        if (query.getCustomerId() != null) {
            Customer customer = customerMapper.selectByPrimaryKey(query.getCustomerId());
            if (customer != null) {
                setPriceMapByCustomer(result, customer, query.getGoodsDetailIds());
            }
        }
        return result;
    }

    /**
     * 根据客户ID或者客户类型，设置对应商品的指定价
     * @param result key:goodsId, value: batchPrice(商品批发价), retailPrice(商品零售价)
     * @param customer
     * @param goodsDetailIds
     */
    private void setPriceMapByCustomer(final Map<Long, PriceRule> result, Customer customer, List<Long> goodsDetailIds) {
        //根据客户ID和客户的类别，获取对应商品的配置价格信息
        List<PriceRule> priceRules = priceRuleMapper.getByGoodsIdAndCustomer(goodsDetailIds, customer.getId(), customer.getCategoryId());

        for (Map.Entry<Long, PriceRule> entry : result.entrySet()) {
            Long goodsId = entry.getKey();
            PriceRule priceRule = entry.getValue();

            //查询priceRules中对应的客户类型的价格, 类型价格如果存在，覆盖商品价格
            Optional<PriceRule> cateO = priceRules.stream()
                    .filter(item -> item.getGoodsId().equals(goodsId) && item.getCustomerCategoryId() != null && item.getCustomerCategoryId().equals(customer.getCategoryId()))
                    .findFirst();
            if (cateO.isPresent()) {
                //客户类型的价格如果存在，覆盖原来商品的价格
                PriceRule itemPrice = cateO.get();
                if (itemPrice != null && itemPrice.getBatchPrice() != null) {
                    priceRule.setBatchPrice(itemPrice.getBatchPrice());
                }
                if (itemPrice != null && itemPrice.getRetailPrice() != null) {
                    priceRule.setRetailPrice(itemPrice.getRetailPrice());
                }
            }
            //查询priceRules中对应的客户价格, 客户价格存在，覆盖原来的
            Optional<PriceRule> itemPriceO = priceRules.stream()
                    .filter(item -> item.getGoodsId().equals(goodsId) && customer.getId().equals(item.getCustomerId()))
                    .findFirst();
            if (itemPriceO.isPresent()) {
                //如果存在，相当于能获取到这个客户针对这个商品的指定价个,
                PriceRule itemPrice = itemPriceO.get();
                if (itemPrice != null && itemPrice.getBatchPrice() != null) {
                    priceRule.setBatchPrice(itemPrice.getBatchPrice());
                }
                if (itemPrice != null && itemPrice.getRetailPrice() != null) {
                    priceRule.setRetailPrice(itemPrice.getRetailPrice());
                }
            }
        }
    }

    @Transactional
    public void saveCategoryByGoodsId(Long goodsId, List<CustomerCategoryPrice> categoryPrices, User user) {
        List<PriceRule> newRule = new ArrayList<>();
        for (CustomerCategoryPrice categoryPrice : categoryPrices) {
            PriceRule rule = new PriceRule();
            if (categoryPrice.getCustomerCategoryId() == null) {
                continue;
            }
            rule.setCustomerCategoryId(categoryPrice.getCustomerCategoryId());
            if (categoryPrice.getBatchPrice() != null && BigDecimal.ZERO.compareTo(categoryPrice.getBatchPrice()) <= 0) {
                rule.setBatchPrice(categoryPrice.getBatchPrice());
            }
            if (categoryPrice.getRetailPrice() != null && BigDecimal.ZERO.compareTo(categoryPrice.getRetailPrice()) <= 0) {
                rule.setRetailPrice(categoryPrice.getRetailPrice());
            }
            rule.setGoodsId(goodsId);
            rule.setCreatedBy(user.getNickname());
            rule.setCreatedTime(new Date());
            newRule.add(rule);
        }

        //把原来的这个产品的所有客户类别的数据进行删除，然后在新建
        priceRuleMapper.removeCategorys(goodsId);

        priceRuleMapper.insertBatch(newRule);
    }

    public void customerSave(SavePriceReq priceReq, User user) throws BizException {
        if (priceReq.getGoodsDetailIds() == null || priceReq.getGoodsDetailIds().isEmpty()) {
            logger.warn("get goods detail ids is empty.");
            throw new BizException(ErrorCode.GOODS_DETAIL_GET_FAIL);
        }
        if (priceReq.getCustomerPrices() == null || priceReq.getCustomerPrices().isEmpty()) {
            logger.warn("get customer category prices is empty.");
            throw new BizException(ErrorCode.GOODS_PRICE_REQUEST_LIST_EMPTY);
        }
        List<Long> goodsIds = priceReq.getGoodsDetailIds();
        //一个个产品ID处理
        for (Long goodsId : goodsIds) {
            if (goodsId != null) {
                saveCustomerPrice(goodsId, priceReq.getCustomerPrices(), user);
            }
        }
    }

    @Transactional
    public void saveCustomerPrice(Long goodsId, List<PriceRule> priceRules, User user) {
        List<PriceRule> newPriceRules = new ArrayList<>();
        List<Long> customerIds = new ArrayList<>();
        for (PriceRule rule : priceRules) {
            PriceRule newRule = new PriceRule();
            newRule.setGoodsId(goodsId);
            if (rule.getCustomerId() == null) {
                continue;
            }
            newRule.setCustomerId(rule.getCustomerId());
            customerIds.add(rule.getCustomerId());

            if (rule.getBatchPrice() != null && rule.getBatchPrice().compareTo(BigDecimal.ZERO) >= 0) {
                newRule.setBatchPrice(rule.getBatchPrice());
            }
            if (rule.getRetailPrice() != null && rule.getRetailPrice().compareTo(BigDecimal.ZERO) >= 0) {
                newRule.setRetailPrice(rule.getRetailPrice());
            }
            newRule.setCreatedTime(new Date());
            newRule.setCreatedBy(user.getNickname());

            newPriceRules.add(newRule);
        }
        //先把原来改商品下的客户的价格删除后直接插入
        priceRuleMapper.removeCustomer(goodsId, customerIds);

        priceRuleMapper.insertBatch(newPriceRules);
    }
}

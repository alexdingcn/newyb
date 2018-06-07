package com.yiban.erp.service.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.constant.GoodsStatus;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.*;
import com.yiban.erp.dto.CurrentBalanceResp;
import com.yiban.erp.dto.GoodsQuery;
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
import java.util.stream.Collectors;

@Service
public class GoodsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private OptionsMapper optionsMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private GoodsDetailMapper goodsDetailMapper;
    @Autowired
    private GoodsAttributeRefMapper goodsAttributeRefMapper;
    @Autowired
    private GoodsAttrService goodsAttrService;
    @Autowired
    private GoodsBlackListMapper goodsBlackListMapper;
    @Autowired
    private RepertoryInfoMapper repertoryInfoMapper;
    @Autowired
    private RepertoryOutDetailMapper repertoryOutDetailMapper;
    @Autowired
    private BuyOrderMapper buyOrderMapper;


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

    public void setGoodsInfoOptionName(List<GoodsInfo> goodsInfoList) {
        if (goodsInfoList == null || goodsInfoList.isEmpty()) {
            return;
        }
        //设置对应的Option的Name值
        Set<Long> optionIdSet = new HashSet<>();
        goodsInfoList.stream().forEach(item -> {
            optionIdSet.addAll(item.getOptionIds());
        });
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goodsInfoList.stream().forEach(item -> item.setOptions(options));
    }

    public void setGoodsInfoOptionName(GoodsInfo goodsInfo) {
        if (goodsInfo == null || goodsInfo.getOptionIds() == null || goodsInfo.getOptionIds().isEmpty()) {
            return;
        }
        Set<Long> optionIdSet = goodsInfo.getOptionIds();
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goodsInfo.setOptions(options);
    }

    public List<Goods> getGoodsById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {;
            return Collections.emptyList();
        }
        List<Goods> goodsList = goodsInfoMapper.getChooseListDetailById(ids);
        setGoodsOptionName(goodsList);
        return goodsList;
    }

    public Long searchListCount(GoodsQuery query) {
        return goodsInfoMapper.searchListCount(query);
    }

    public List<GoodsInfo> searchList(GoodsQuery query) {
        List<GoodsInfo> infos = goodsInfoMapper.searchList(query);
        setGoodsInfoOptionName(infos);

        //如果query中的includeDetail 为true，查询对应的详情信息
        if (query.getIncludeDetail() != null && query.getIncludeDetail()) {
            setGoodsInfoDetails(infos);
        }
        //查询出默认属性值
        setAttributeRefs(infos);
        return infos;
    }

    private void setAttributeRefs(List<GoodsInfo> infos){
        if (infos == null || infos.isEmpty()) {
            return;
        }
        List<Long> infoIds = new ArrayList<>();
        Integer companyId = infos.get(0).getCompanyId();
        infos.stream().forEach(item -> infoIds.add(item.getId()));
        List<GoodsAttributeRef> attr = goodsAttributeRefMapper.getDefaultAttrRefs(infoIds,companyId);
        Map<Long, List<GoodsAttributeRef>> map = attr.stream().collect(Collectors.groupingBy(GoodsAttributeRef::getGoodsInfoId));
        infos.stream().forEach(item -> item.setAttributeRefs(map.get(item.getId())));
    }
    private void setGoodsInfoDetails(List<GoodsInfo> infos) {
        if (infos == null || infos.isEmpty()) {
            return;
        }
        List<Long> infoIds = new ArrayList<>();
        infos.stream().forEach(item -> infoIds.add(item.getId()));
        List<GoodsDetail> details = goodsDetailMapper.getByGoodsInfoIds(infoIds);
        Map<Long, List<GoodsDetail>> map = details.stream().collect(Collectors.groupingBy(GoodsDetail::getGoodsInfoId));
        infos.stream().forEach(item -> item.setGoodsDetails(map.get(item.getId())));
    }


    /**
     * LW:当前库存，
     * LB: last buy 最近采购价
     * CBQ: current buy count 当前采购在单数量
     * LS: last sale 最近一次销售价
     */
    public List<Goods> setGoodsExtra(Long customerId, Integer warehouseId, List<String> options, List<Goods> goods) {
        if (options == null || options.isEmpty() || goods.isEmpty()) {
            return goods;
        }
        List<Long> goodsIds = new ArrayList<>();
        goods.stream().forEach(item -> goodsIds.add(item.getId()));
        for (String option : options) {
            if (warehouseId != null && GoodsQuery.OPTION_LW.equalsIgnoreCase(option)) {
                //查询商品列表中的当前存库数据
                List<CurrentBalanceResp> balanceResp = repertoryInfoMapper.getBalance(warehouseId, goodsIds);
                Map<Long, CurrentBalanceResp> tempMap = new HashMap<>();
                balanceResp.stream().forEach(item -> tempMap.put(item.getGoodsId(), item));
                goods.stream().forEach(item -> {
                    CurrentBalanceResp resp = tempMap.get(item.getId());
                    item.setCurrRepQuatity(resp != null && resp.getBalance() != null ? resp.getBalance() : null);
                });
            }
            if (warehouseId != null && GoodsQuery.OPTION_LB.equalsIgnoreCase(option)) {
                //最近一次的采购价
                List<CurrentBalanceResp> lastPriceResp = repertoryInfoMapper.getLastBuyPrice(warehouseId, goodsIds);
                Map<Long, CurrentBalanceResp> tempMap = new HashMap<>();
                lastPriceResp.stream().forEach(item -> tempMap.put(item.getGoodsId(), item));
                goods.stream().forEach(item -> {
                    CurrentBalanceResp resp = tempMap.get(item.getId());
                    item.setLastBuyPrice(resp != null && resp.getLastPrice() != null ? resp.getLastPrice() : null);
                });
            }
            if (GoodsQuery.OPTION_CBQ.equalsIgnoreCase(option)) {
                //当前采购单申购的数量，只计算INIT 和 CHECKED 状态下的订单数量
                List<CurrentBalanceResp> orderResp = buyOrderMapper.getGoodsOrderCount(goodsIds);
                Map<Long, CurrentBalanceResp> tempMap = new HashMap<>();
                orderResp.stream().forEach(item -> tempMap.put(item.getGoodsId(), item));
                goods.stream().forEach(item -> {
                    CurrentBalanceResp resp = tempMap.get(item.getId());
                    item.setCurrBuyQuality(resp != null && resp.getOngoingCount() != null ? resp.getOngoingCount() : null);
                });
            }
            if (customerId != null && GoodsQuery.OPTION_LS.equalsIgnoreCase(option)) {
                //最近一次的销售价
                List<CurrentBalanceResp> lastBuyPrices = repertoryOutDetailMapper.getLastBuyPrice(customerId, goodsIds);
                Map<Long, CurrentBalanceResp> tempMap = new HashMap<>();
                lastBuyPrices.stream().forEach(item -> tempMap.put(item.getGoodsId(), item));
                goods.stream().forEach(item -> {
                    CurrentBalanceResp resp = tempMap.get(item.getId());
                    item.setLastSalePrice(resp != null && resp.getLastSalePrice() != null ? resp.getLastSalePrice() : null);
                });
            }
        }
        return goods;
    }

    public Long getChooseListDetailCount(GoodsQuery query) {
        return goodsInfoMapper.getChooseListDetailCount(query);
    }

    public List<Goods> getChooseListDetail(GoodsQuery query) {
        List<Goods> goods = goodsInfoMapper.getChooseListDetail(query);
        //设置option的值
        setGoodsOptionName(goods);
        //列表中忽略自定义属性的展示

        //如果请求参数中的options不为空时，查询对应的数据值
        setGoodsExtra(query.getCustomerId(), query.getWarehouseId(), query.getOptions(), goods);

        return goods;
    }

    public Goods getInfoByDetailId(Long detailId, User user) throws BizException {
        List<Goods> goodsList = getGoodsById(Arrays.asList(detailId));
        if (goodsList == null || goodsList.isEmpty()) {
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        //如果有，应该只有一个
        Goods goods = goodsList.get(0);
        //自定义属性
        goods.setAttributeRefs(getGoodsAttributeRef(goods.getGoodsInfoId(), user.getCompanyId()));
        return goods;
    }

    private List<GoodsAttributeRef> getGoodsAttributeRef(Long goodsInfoId, Integer companyId) {
        //自定义属性
        List<GoodsAttributeRef> attributeRefs = goodsAttributeRefMapper.getByGoodsInfoId(goodsInfoId);
        if (attributeRefs != null && !attributeRefs.isEmpty()) {
            Map<Long, GoodsAttribute> attributeMap = goodsAttrService.getGoodsAttMap(companyId);
            for (GoodsAttributeRef ref : attributeRefs) {
                GoodsAttribute attribute = attributeMap.get(ref.getAttId());
                if (attribute != null) {
                    ref.setAttName(attribute.getAttName());
                }
            }
        }
        return attributeRefs;
    }


    public GoodsInfo getGoodsInfoById(Long id) throws BizException {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(id);
        if (goodsInfo == null) {
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        List<GoodsDetail> details = goodsDetailMapper.getByGoodsInfoId(goodsInfo.getId(), false);
        goodsInfo.setGoodsDetails(details); //详情
        setGoodsInfoOptionName(goodsInfo); //option的值
        //自定义属性
        goodsInfo.setAttributeRefs(getGoodsAttributeRef(goodsInfo.getId(), goodsInfo.getCompanyId()));

        return goodsInfo;
    }


    public void saveGoodsInfo(GoodsInfo goodsInfo, User user) throws BizException {
        if (StringUtils.isEmpty(goodsInfo.getName()) || goodsInfo.getUnit() == null || goodsInfo.getUseSpec() == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //如果存在有ID，则认为是修改，如果不存在ID，认为是添加
        if (goodsInfo.getId() != null) {
            updateGoodsInfo(goodsInfo, user);
        } else {
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
            goodsInfo.setStatus(GoodsStatus.ON_SALE.name());
        }
        goodsInfo.setGoodsNo(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.GOODS));
        if (goodsInfo.getEnable() == null) {
            goodsInfo.setEnable(false);
        }
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
            detail.setGoodsInfoId(goodsInfo.getId());
            detail.setStatus(GoodsStatus.NORMAL.name());
            detail.setSkuKey(getSkuKey(null, goodsInfo.getId()));
            detail.setBarCode(goodsInfo.getBarCode());
            detail.setBatchPrice(goodsInfo.getBatchPrice());
            detail.setRetailPrice(goodsInfo.getRetailPrice());
            detail.setInPrice(goodsInfo.getInPrice());
            detail.setCreatedBy(user.getNickname());
            detail.setCreatedTime(new Date());
            detail.setLastUsedTime(null); //初始为空值
            detail.setUsedCount(0); //初始设置为0
            int detailCount = goodsDetailMapper.insert(detail);
            if (detailCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        } else {
            for (GoodsDetail detail : details) {
                detail.setCompanyId(user.getCompanyId());
                detail.setGoodsInfoId(goodsInfo.getId());
                detail.setStatus(GoodsStatus.NORMAL.name());
                if (detail.getBarCode() == null && goodsInfo.getBarCode() != null) {
                    detail.setBarCode(goodsInfo.getBarCode());
                }
                detail.setSkuKey(getSkuKey(detail, goodsInfo.getId()));
                detail.setCreatedBy(user.getNickname());
                detail.setCreatedTime(new Date());
                detail.setLastUsedTime(null); //初始为空值
                detail.setUsedCount(0); //初始设置为0
            }
            int detailsCount = goodsDetailMapper.insertBatch(details);
            if (detailsCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }

        //自定义属性如果存在，直接操作入库
        List<GoodsAttributeRef> attributeRefs = goodsInfo.getAttributeRefs();
        if (attributeRefs != null && !attributeRefs.isEmpty()) {
            setAttributeGoodsInfoId(attributeRefs, goodsInfo.getId());
            goodsAttributeRefMapper.insertBatch(attributeRefs);
        }

        // 黑名单
        if (StringUtils.isNotBlank(goodsInfo.getBlackList())) {
            JSONObject obj = JSON.parseObject(goodsInfo.getBlackList());
            GoodsBlackListWithBLOBs blackList = new GoodsBlackListWithBLOBs();
            blackList.setCustomerCategoryIds(obj.getString("customerCategoryIds"));
            blackList.setRegions(obj.getString("regions"));
            blackList.setCustomerIds(obj.getString("customerIds"));

            if (!blackList.isEmpty()) {
                blackList.setGoodsId(goodsInfo.getId());
                blackList.setCreatedBy(user.getId().toString());
                blackList.setCreatedTime(new Date());
                goodsBlackListMapper.insert(blackList);
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
        GoodsInfo oldGoodsInfo = goodsInfoMapper.selectByPrimaryKey(goodsInfo.getId());
        if (oldGoodsInfo == null || !oldGoodsInfo.getCompanyId().equals(user.getCompanyId())) {
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        //检查是否使用了多规格, 老的全部拉出来
        List<GoodsDetail> oldDetails = goodsDetailMapper.getByGoodsInfoId(goodsInfo.getId(), true);
        if (oldDetails == null || oldDetails.isEmpty()) {
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL); //至少会有一个
        }
        Map<String, GoodsDetail> oldMap = new HashMap<>();
        oldDetails.stream().forEach(item -> oldMap.put(item.getSkuKey(), item));
        boolean useSpec = goodsInfo.getUseSpec() == null ? false : goodsInfo.getUseSpec();
        if (!useSpec) {
            //未使用多规格
            updateUnUseSpec(goodsInfo, oldGoodsInfo, oldDetails, oldMap, user);
        } else {
            //使用了多规格，
            updateUseSpec(goodsInfo, oldDetails, oldMap, user);
        }

        //自定义属性，先直接一次删除全部的在全部插入进去
        goodsAttributeRefMapper.deleteByGoodsInfoId(goodsInfo.getId());
        List<GoodsAttributeRef> attributeRefs = goodsInfo.getAttributeRefs();
        if (attributeRefs != null && !attributeRefs.isEmpty()) {
            logger.info("insert goods attribute info.");
            setAttributeGoodsInfoId(attributeRefs, goodsInfo.getId());
            goodsAttributeRefMapper.insertBatch(attributeRefs);
        }

        // 黑名单
        if (StringUtils.isNotBlank(goodsInfo.getBlackList())) {
            GoodsBlackListWithBLOBs blackList = goodsBlackListMapper.selectByGoodsId(goodsInfo.getId());
            if (blackList == null) {
                blackList = new GoodsBlackListWithBLOBs();
                blackList.setCreatedBy(user.getId().toString());
                blackList.setCreatedTime(new Date());
            }

            JSONObject obj = JSON.parseObject(goodsInfo.getBlackList());
            blackList.setCustomerCategoryIds(obj.getString("customerCategoryIds"));
            blackList.setRegions(obj.getString("regions"));
            blackList.setCustomerIds(obj.getString("customerIds"));

            blackList.setGoodsId(goodsInfo.getId());
            blackList.setUpdatedBy(user.getId().toString());
            blackList.setUpdatedTime(new Date());
            if (blackList.getId() == null) {
                goodsBlackListMapper.insert(blackList);
            } else {
                if (blackList.isEmpty()) {
                    goodsBlackListMapper.deleteByPrimaryKey(blackList.getId());
                } else {
                    goodsBlackListMapper.updateByPrimaryKeyWithBLOBs(blackList);
                }
            }
        }
    }



    private void setAttributeGoodsInfoId(List<GoodsAttributeRef> attributeRefs, Long goodsInfoId) {
        for (GoodsAttributeRef ref : attributeRefs) {
            ref.setGoodsInfoId(goodsInfoId);
        }
    }

    @Transactional
    public void updateUseSpec(GoodsInfo goodsInfo, List<GoodsDetail> oldDetails, Map<String, GoodsDetail> oldDetailMap,
                              User user) throws BizException {
        //生成所有新规格的skuKey
        List<GoodsDetail> details = goodsInfo.getGoodsDetails();
        for (GoodsDetail detail : details) {
            detail.setCompanyId(user.getCompanyId());
            detail.setGoodsInfoId(goodsInfo.getId());
            detail.setStatus(GoodsStatus.NORMAL.name());
            if (detail.getBarCode() == null && goodsInfo.getBarCode() != null) {
                detail.setBarCode(goodsInfo.getBarCode());
            }
            detail.setSkuKey(getSkuKey(detail, goodsInfo.getId()));
        }
        Map<String, GoodsDetail> newSkuKeyMap = new HashMap<>();
        details.stream().forEach(item -> newSkuKeyMap.put(item.getSkuKey(), item));

        for (GoodsDetail oldItem : oldDetails) {
            GoodsDetail newItem = newSkuKeyMap.get(oldItem.getSkuKey());
            if (newItem != null) {
                //新的中还包含老的，
                continue;
            }
            //如果不包含老的了，说明需要删除老的，在删除前验证是否可以删除
            if (!GoodsStatus.DELETE.name().equalsIgnoreCase(oldItem.getStatus())
                    && oldItem.getUsedCount() != null && oldItem.getUsedCount() > 0) {
                logger.warn("old detail not include in new detail, but old detail useCount > 0 can not delete. old detail skuKey:{}", oldItem.getSkuKey());
                throw new BizException(ErrorCode.GOODS_OLD_SPEC_USED);
            }
        }
        logger.info("validate old detail true"); //没有抛错退出，认为验证都通过
        //把老的全部修改改到DELETE的状态
        goodsDetailMapper.deleteByGoodsInfoId(goodsInfo.getId(), user.getNickname(), new Date());
        //然后根据新的规格，如果在老的中，则修改，如果不在，则添加
        for (GoodsDetail newItem : details) {
            GoodsDetail oldDetail = oldDetailMap.get(newItem.getSkuKey());
            if (oldDetail != null) {
                oldDetail.setStatus(GoodsStatus.NORMAL.name());
                oldDetail.setSkuKey(newItem.getSkuKey());
                oldDetail.setBarCode(newItem.getBarCode());
                oldDetail.setBatchPrice(newItem.getBatchPrice());
                oldDetail.setRetailPrice(newItem.getRetailPrice());
                oldDetail.setInPrice(newItem.getInPrice());
                oldDetail.setUpdatedBy(user.getNickname());
                oldDetail.setUpdatedTime(new Date());
                goodsDetailMapper.updateByPrimaryKeySelective(oldDetail);
                logger.info("update detail success. goodsInfoId:{}", goodsInfo.getId());
            } else {
                //如果获取不到，说明原来是多规格的，现在变更为了单规格，根据key创建一个新的
                newItem.setCreatedTime(new Date());
                newItem.setCreatedBy(user.getNickname());
                goodsDetailMapper.insert(newItem);
                logger.info("update goods info created detail success");
            }
        }

        //然后直接修改产品信息
        goodsInfo.setCompanyId(user.getCompanyId());
        goodsInfo.setUpdatedBy(user.getNickname());
        goodsInfo.setUpdatedTime(new Date());
        goodsInfoMapper.updateByPrimaryKeySelective(goodsInfo);
    }

    @Transactional
    public void updateUnUseSpec(GoodsInfo goodsInfo, GoodsInfo oldGoodsInfo, List<GoodsDetail> oldDetails,
                                Map<String, GoodsDetail> oldDetailMap, User user) throws BizException {
        //如果新的没有使用多规格，看看老的是否使用了多规格
        if (oldGoodsInfo.getUseSpec() && isDetailsHaveUsed(oldDetails)) {
            //老的使用了多规格，且多规格中的值是存在有使用过的，如果有，则不能进行修改，相当于存在有多规格限制
            throw new BizException(ErrorCode.GOODS_OLD_SPEC_USED);
        }
        //如果验证通过，把原来的detail全部设置为DELETE状态. 然后建立新的
        goodsDetailMapper.deleteByGoodsInfoId(oldGoodsInfo.getId(), user.getNickname(), new Date());
        //新的skuKey
        String newSkuKey = getSkuKey(null, goodsInfo.getId());
        GoodsDetail oldDetail = oldDetailMap.get(newSkuKey);
        //如果获取到了，直接根据新值赋值给老的值进行修改
        if (oldDetail != null) {
            oldDetail.setStatus(GoodsStatus.NORMAL.name());
            oldDetail.setSkuKey(newSkuKey);
            oldDetail.setBarCode(goodsInfo.getBarCode());
            oldDetail.setBatchPrice(goodsInfo.getBatchPrice());
            oldDetail.setRetailPrice(goodsInfo.getRetailPrice());
            oldDetail.setInPrice(goodsInfo.getInPrice());
            oldDetail.setUpdatedBy(user.getNickname());
            oldDetail.setUpdatedTime(new Date());
            goodsDetailMapper.updateByPrimaryKeySelective(oldDetail);
            logger.info("update detail success. goodsInfoId:{}", goodsInfo.getId());
        } else {
            //如果获取不到，说明原来是多规格的，现在变更为了单规格，根据key创建一个新的
            GoodsDetail detail = makeNewGoodsDetail(goodsInfo, user);
            goodsDetailMapper.insert(detail);
            logger.info("update goods info created detail success");
        }

        //然后直接修改产品信息
        goodsInfo.setUpdatedBy(user.getNickname());
        goodsInfo.setUpdatedTime(new Date());
        goodsInfoMapper.updateByPrimaryKeySelective(goodsInfo);
    }

    private GoodsDetail makeNewGoodsDetail(GoodsInfo goodsInfo, User user) {
        GoodsDetail detail = new GoodsDetail();
        detail.setCompanyId(user.getCompanyId());
        detail.setGoodsInfoId(goodsInfo.getId());
        detail.setStatus(GoodsStatus.NORMAL.name());
        detail.setSkuKey(getSkuKey(null, goodsInfo.getId()));
        detail.setBarCode(goodsInfo.getBarCode());
        detail.setBatchPrice(goodsInfo.getBatchPrice());
        detail.setRetailPrice(goodsInfo.getRetailPrice());
        detail.setInPrice(goodsInfo.getInPrice());
        detail.setCreatedBy(user.getNickname());
        detail.setCreatedTime(new Date());
        detail.setLastUsedTime(null); //初始为空值
        detail.setUsedCount(0); //初始设置为0

        return detail;
    }

    private boolean isDetailsHaveUsed(List<GoodsDetail> details) {
        if (details == null || details.isEmpty()) {
            return false;
        }
        for (GoodsDetail item : details) {
            if (!GoodsStatus.DELETE.name().equalsIgnoreCase(item.getStatus())
                    && item.getUsedCount() != null && item.getUsedCount() > 0) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void remove(Long infoId, User user) throws BizException {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(infoId);
        if (goodsInfo == null || !goodsInfo.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get goods info fail by id:{}", infoId);
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        //验证当前商品下的所有详情是否有存在使用的情况，如果存在，不能删除
        boolean isGoodsUse = goodsInfoMapper.isGoodsUsed(infoId);
        if (isGoodsUse) {
            logger.warn("goods info is used, can not delete.");
            throw new BizException(ErrorCode.GOODS_USED_CANNOT_DELETE);
        }
        //如果没有关联的，直接修改到删除状态
        goodsInfo.setEnable(false);
        goodsInfo.setStatus(GoodsStatus.DELETE.name());
        goodsInfo.setUpdatedBy(user.getNickname());
        goodsInfo.setUpdatedTime(new Date());
        int count = goodsInfoMapper.updateByPrimaryKeySelective(goodsInfo);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_DELETE_FROM_DB);
        }
        logger.info("goods info is update to DELETE status, then update details status to DELETE");
        goodsDetailMapper.deleteByGoodsInfoId(goodsInfo.getId(), user.getNickname(), new Date());
    }

    @Transactional
    public void copyGoodsInfo(Long id, User user) throws BizException {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(id);
        if (goodsInfo == null || !goodsInfo.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get goods info fail by id:{}", id);
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        List<GoodsDetail> details = goodsDetailMapper.getByGoodsInfoId(goodsInfo.getId(), false);
        if (details == null || details.isEmpty()) {
            logger.warn("get goods info detail by goods info id result is empty");
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        //先直接对goodsInfo 做入库操作
        goodsInfo.setId(null);
        goodsInfo.setName(goodsInfo.getName() + "copy"); //名称在原来的商品名称上加一个copy
        goodsInfo.setGoodsNo(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.GOODS));
        goodsInfo.setCreatedBy(user.getNickname());
        goodsInfo.setCreatedTime(new Date());
        goodsInfo.setUpdatedBy(user.getNickname());
        goodsInfo.setUpdatedTime(new Date());
        int count = goodsInfoMapper.insert(goodsInfo);
        if (count <= 0 || goodsInfo.getId() == null) {
            logger.warn("copy goods info insert db fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("copy goods info insert info success, id:{}", goodsInfo.getId());
        //需要对每一个detail的goods_info_id 和 sku_key 重置
        for (GoodsDetail detail : details) {
            detail.setId(null);
            detail.setGoodsInfoId(goodsInfo.getId());
            detail.setSkuKey(getSkuKey(detail, goodsInfo.getId()));
            detail.setCreatedBy(user.getNickname());
            detail.setCreatedTime(new Date());
            detail.setUpdatedBy(user.getNickname());
            detail.setUpdatedTime(new Date());
            detail.setLastUsedTime(null); //初始为空值
            detail.setUsedCount(0); //初始设置为0
        }
        int detailCount = goodsDetailMapper.insertBatch(details);
        if (detailCount <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }

    @Transactional
    public void detailRemoveById(Long detailId, User user) throws BizException {
        GoodsDetail detail = goodsDetailMapper.selectByPrimaryKey(detailId);
        if (detail == null) {
            throw new BizException(ErrorCode.GOODS_DETAIL_GET_FAIL);
        }
        //如果usedCount大于0,则不能修改到删除状态，如果小于等于0，可以修改
        if (detail.getUsedCount() != null && detail.getUsedCount() > 0) {
            logger.warn("detail usedCount > 0 can not delete.");
            throw new BizException(ErrorCode.GOODS_DETAIL_USED_CANNOT_DELETE);
        }
        logger.info("user:{} request delete detail:{}", user.getId(), detail.getId());
        detail.setStatus(GoodsStatus.DELETE.name());
        detail.setUpdatedTime(new Date());
        detail.setUpdatedBy(user.getNickname());
        goodsDetailMapper.updateByPrimaryKeySelective(detail);
    }

    public List<GoodsAttribute> getDefaultAttr(Integer companyId){
        return goodsAttributeRefMapper.getDefaultAttr(companyId);
    }

    public List<GoodsAttributeRef> getDefaultAttrRef(Integer companyId){
        return goodsAttributeRefMapper.getDefaultAttrRef(companyId);
    }

    public boolean haveColdManageGoods(List<Long> detailIds) {
        //查询是否存在有冷链经营性商品
        List<GoodsInfo> goodsInfos = goodsInfoMapper.getColdManageByDetailIds(detailIds);
        return goodsInfos != null && !goodsInfos.isEmpty();
    }

    public boolean haveSpecialManageGoods(Collection<Long> detailIds) {
        //查询是否存在有冷链经营性商品
        List<GoodsInfo> goodsInfos = goodsInfoMapper.getSpecialManageByDetailIds(detailIds);
        return goodsInfos != null && !goodsInfos.isEmpty();
    }

    public List<GoodsInfo> getGoodsInfoListByIds(Collection<Long> detailIds) {
        List<GoodsInfo> goodsInfos = goodsInfoMapper.getGoodsInfoListByIds(detailIds);
        return goodsInfos;
    }


}

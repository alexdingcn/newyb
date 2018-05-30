package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepertoryInDetail {
    private Long id;

    private Long inOrderId;

    private Long goodsId;

    private BigDecimal receiveQuality;

    private BigDecimal bigQuality;

    private BigDecimal free;

    private BigDecimal price;

    private Date expDate;

    private Date productDate;

    private String batchCode;

    private BigDecimal amount;

    private String warehouseLocation;

    private BigDecimal rejectQuality;

    private String rejectComment;

    private BigDecimal taxRate;

    private BigDecimal inCount;

    private BigDecimal rightCount;

    private BigDecimal errorCount;

    private Boolean saleState;

    private BigDecimal surveyQuality;

    private Date surveyDate;

    private String surveyUser;

    private String surveyAddress;

    private String surveyResult;

    private String surveyTarget;

    private Boolean checkStatus;

    private String errorPlan;

    private String saleCert;

    private String errorReason;

    private Long checkTempMethod;

    private String checkTempMethodName;

    private String checkUser;

    private Date checkTime;

    private String checkResult;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    //采购单的采购数量
    private BigDecimal buyOrderQuality;

    private Goods goods;
    private RepertoryIn repertoryIn;
    private String refTypeName;
    private String warehouseName;
    private Date inDate;
    private Long supplierId;
    private String supplierName;
    private String supplierContactName;
    private String inStatus;

    private String goodsName;
    private String goodsNo;
    private String origin;
    private String jx;
    private String spec;
    private String factory;
    private String unitName;
    private String packUnitName;
    private BigDecimal bigPack;
    private String storageCondition;
    private Boolean specialManage;

    public RepertoryIn getRepertoryIn() {
        return repertoryIn;
    }

    public void setRepertoryIn(RepertoryIn repertoryIn) {
        this.repertoryIn = repertoryIn;
        if(repertoryIn!=null){
            this.refTypeName=repertoryIn.getRefTypeName();
            this.warehouseName=repertoryIn.getWarehouseName();
            this.inStatus=repertoryIn.getStatus();
            this.inDate=repertoryIn.getReceiveDate();
            this.supplierId=repertoryIn.getSupplierId();
            this.supplierName=repertoryIn.getSupplierName();
            this.supplierContactName=repertoryIn.getSupplierContactName();

        }

    }
    public void setGoods(Goods goods) {
        this.goods = goods;
        if (goods != null) {
            this.goodsName = goods.getName();
            this.factory = goods.getFactoryName();
            this.goodsNo=goods.getGoodsNo();
            this.origin = goods.getOrigin();
            this.jx = goods.getJxName();
            this.unitName = goods.getUnitName();
            this.packUnitName = goods.getPackUnitName();
            this.storageCondition = goods.getStorageConditionName();
        }

    }
    public String getInStatus() {
        return inStatus;
    }

    public void setInStatus(String inStatus) {
        this.inStatus = inStatus;
    }

    public Boolean getSpecialManage() {
        return this.goods == null ? null : this.goods.getSpecialManage();
    }

    public String getStorageCondition() {
        return this.goods == null ? null : this.goods.getStorageConditionName();
    }

    public String getGoodsName() {
        return this.goods == null ? null : this.goods.getName();
    }

    public String getOrigin() {
        return this.goods == null ? null : this.goods.getOrigin();
    }

    public String getJx() {
        return this.goods == null ? null : this.goods.getJxName();
    }

    public String getUnitName() {
        return this.goods == null ? null : this.goods.getUnitName();
    }

    public String getPackUnitName() {
        return this.goods == null ? null : this.goods.getPackUnitName();
    }

    public BigDecimal getBigPack() {
        return this.goods == null ? null : this.goods.getBigPack();
    }

    public Goods getGoods() {
        return goods;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInOrderId() {
        return inOrderId;
    }

    public void setInOrderId(Long inOrderId) {
        this.inOrderId = inOrderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public BigDecimal getReceiveQuality() {
        return receiveQuality;
    }

    public void setReceiveQuality(BigDecimal receiveQuality) {
        this.receiveQuality = receiveQuality;
    }

    public BigDecimal getBigQuality() {
        return bigQuality;
    }

    public void setBigQuality(BigDecimal bigQuality) {
        this.bigQuality = bigQuality;
    }

    public BigDecimal getFree() {
        return free;
    }

    public void setFree(BigDecimal free) {
        this.free = free;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Date getProductDate() {
        return productDate;
    }

    public void setProductDate(Date productDate) {
        this.productDate = productDate;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode == null ? null : batchCode.trim();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation == null ? null : warehouseLocation.trim();
    }

    public BigDecimal getRejectQuality() {
        return rejectQuality;
    }

    public void setRejectQuality(BigDecimal rejectQuality) {
        this.rejectQuality = rejectQuality;
    }

    public String getRejectComment() {
        return rejectComment;
    }

    public void setRejectComment(String rejectComment) {
        this.rejectComment = rejectComment == null ? null : rejectComment.trim();
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public BigDecimal getBuyOrderQuality() {
        return buyOrderQuality;
    }

    public void setBuyOrderQuality(BigDecimal buyOrderQuality) {
        this.buyOrderQuality = buyOrderQuality;
    }

    public BigDecimal getInCount() {
        if (this.inCount == null) {
            return this.getReceiveQuality();
        }
        return inCount;
    }

    public void setInCount(BigDecimal inCount) {
        this.inCount = inCount;
    }

    public BigDecimal getRightCount() {
        return rightCount;
    }

    public void setRightCount(BigDecimal rightCount) {
        this.rightCount = rightCount;
    }

    public BigDecimal getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(BigDecimal errorCount) {
        this.errorCount = errorCount;
    }

    public Boolean getSaleState() {
        return saleState;
    }

    public void setSaleState(Boolean saleState) {
        this.saleState = saleState;
    }

    public BigDecimal getSurveyQuality() {
        return surveyQuality;
    }

    public void setSurveyQuality(BigDecimal surveyQuality) {
        this.surveyQuality = surveyQuality;
    }

    public Date getSurveyDate() {
        return surveyDate;
    }

    public void setSurveyDate(Date surveyDate) {
        this.surveyDate = surveyDate;
    }

    public String getSurveyUser() {
        return surveyUser;
    }

    public void setSurveyUser(String surveyUser) {
        this.surveyUser = surveyUser == null ? null : surveyUser.trim();
    }

    public String getSurveyAddress() {
        return surveyAddress;
    }

    public void setSurveyAddress(String surveyAddress) {
        this.surveyAddress = surveyAddress == null ? null : surveyAddress.trim();
    }

    public String getSurveyResult() {
        return surveyResult;
    }

    public void setSurveyResult(String surveyResult) {
        this.surveyResult = surveyResult == null ? null : surveyResult.trim();
    }

    public String getSurveyTarget() {
        return surveyTarget;
    }

    public void setSurveyTarget(String surveyTarget) {
        this.surveyTarget = surveyTarget == null ? null : surveyTarget.trim();
    }

    public Boolean getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(Boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getErrorPlan() {
        return errorPlan;
    }

    public void setErrorPlan(String errorPlan) {
        this.errorPlan = errorPlan == null ? null : errorPlan.trim();
    }

    public String getSaleCert() {
        return saleCert;
    }

    public void setSaleCert(String saleCert) {
        this.saleCert = saleCert == null ? null : saleCert.trim();
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason == null ? null : errorReason.trim();
    }

    public Long getCheckTempMethod() {
        return checkTempMethod;
    }

    public void setCheckTempMethod(Long checkTempMethod) {
        this.checkTempMethod = checkTempMethod;
    }

    public String getCheckUser() {
        return checkUser;
    }

    public void setCheckUser(String checkUser) {
        this.checkUser = checkUser == null ? null : checkUser.trim();
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult == null ? null : checkResult.trim();
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy == null ? null : createBy.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy == null ? null : updateBy.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCheckTempMethodName() {
        return checkTempMethodName;
    }

    public void setCheckTempMethodName(String checkTempMethodName) {
        this.checkTempMethodName = checkTempMethodName;
    }

    public String getRefTypeName() {
        return refTypeName;
    }

    public void setRefTypeName(String refTypeName) {
        this.refTypeName = refTypeName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setJx(String jx) {
        this.jx = jx;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public void setPackUnitName(String packUnitName) {
        this.packUnitName = packUnitName;
    }

    public void setBigPack(BigDecimal bigPack) {
        this.bigPack = bigPack;
    }

    public void setStorageCondition(String storageCondition) {
        this.storageCondition = storageCondition;
    }

    public void setSpecialManage(Boolean specialManage) {
        this.specialManage = specialManage;
    }

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierContactName() {
        return supplierContactName;
    }

    public void setSupplierContactName(String supplierContactName) {
        this.supplierContactName = supplierContactName;
    }

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }
}
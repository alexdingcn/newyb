package com.yiban.erp.entities;

import java.math.BigDecimal;
import java.util.Date;

public class RepositoryOrderDetail {
    private Long id;

    private Long repositoryOrderId;

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

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private BigDecimal inCount; //入库量，直接设置为收货量
    private BigDecimal rightCount; //合格数量
    private BigDecimal errorCount; //不合格的数量

    private Date surveyDate;
    private BigDecimal surveyQuality; //抽样检查数量
    private String surveyUser;
    private String surveyAddress;
    private String surveyResult;
    private String surveyTarget;

    private Boolean checkStatus;
    private String errorPlan;
    private String saleCert;
    private String errorReason;
    private Long checkTempMethod;
    private String checkUser;
    private Date checkTime;
    private String checkResult;

    private Boolean saleState;

    private Goods goods;

    private String goodsName;
    private String origin;
    private String jx;
    private String spec;
    private String factory;
    private String unitName;
    private String packUnitName;
    private Integer bigPack;
    private String permit;
    private String storageCondition;
    private Boolean specialManaged;

    public Boolean getSpecialManaged() {
        return this.goods == null ? null : this.goods.getSpecialManaged();
    }

    public String getStorageCondition() {
        return this.goods == null ? null : this.goods.getStorageCondition();
    }

    public String getGoodsName() {
        return this.goods == null ? null : this.goods.getName();
    }

    public String getOrigin() {
        return this.goods == null ? null : this.goods.getOrigin();
    }

    public String getJx() {
        return this.goods == null ? null : this.goods.getJx();
    }

    public String getSpec() {
        return this.goods == null ? null : this.goods.getSpec();
    }

    public String getFactory() {
        return this.goods == null ? null : this.goods.getFactory();
    }

    public String getUnitName() {
        return this.goods == null ? null : this.goods.getUnitName();
    }

    public String getPackUnitName() {
        return this.goods == null ? null : this.goods.getPackUnitName();
    }

    public Integer getBigPack() {
        return this.goods == null ? null : this.goods.getBigPack();
    }

    public String getPermit() {
        return this.goodsName == null ? null : String.valueOf(this.goods.getPermitId());
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepositoryOrderId() {
        return repositoryOrderId;
    }

    public void setRepositoryOrderId(Long repositoryOrderId) {
        this.repositoryOrderId = repositoryOrderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
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

    public BigDecimal getInCount() {
        return inCount;
    }

    public void setInCount(BigDecimal inCount) {
        if (inCount == null) {
            this.inCount = this.receiveQuality;
        }else {
            this.inCount = inCount;
        }
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
        this.surveyUser = surveyUser;
    }

    public String getSurveyAddress() {
        return surveyAddress;
    }

    public void setSurveyAddress(String surveyAddress) {
        this.surveyAddress = surveyAddress;
    }

    public String getSurveyResult() {
        return surveyResult;
    }

    public void setSurveyResult(String surveyResult) {
        this.surveyResult = surveyResult;
    }

    public String getSurveyTarget() {
        return surveyTarget;
    }

    public void setSurveyTarget(String surveyTarget) {
        this.surveyTarget = surveyTarget;
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
        this.errorPlan = errorPlan;
    }

    public String getSaleCert() {
        return saleCert;
    }

    public void setSaleCert(String saleCert) {
        this.saleCert = saleCert;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
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
        this.checkUser = checkUser;
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
        this.checkResult = checkResult;
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

    public BigDecimal getRejectQuality() {
        return rejectQuality;
    }

    public void setRejectQuality(BigDecimal rejectQuality) {
        this.rejectQuality = rejectQuality;
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

    public BigDecimal getSurveyQuality() {
        return surveyQuality;
    }

    public void setSurveyQuality(BigDecimal surveyQuality) {
        this.surveyQuality = surveyQuality;
    }

    public Boolean getSaleState() {
        return saleState;
    }

    public void setSaleState(Boolean saleState) {
        this.saleState = saleState;
    }
}
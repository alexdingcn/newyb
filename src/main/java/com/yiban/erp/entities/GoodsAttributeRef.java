package com.yiban.erp.entities;

public class GoodsAttributeRef {
    private Long id;

    private Long goodsInfoId;

    private Long attId;

    private String attValue;

    private String attName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsInfoId() {
        return goodsInfoId;
    }

    public void setGoodsInfoId(Long goodsInfoId) {
        this.goodsInfoId = goodsInfoId;
    }

    public Long getAttId() {
        return attId;
    }

    public void setAttId(Long attId) {
        this.attId = attId;
    }

    public String getAttValue() {
        return attValue;
    }

    public void setAttValue(String attValue) {
        this.attValue = attValue == null ? null : attValue.trim();
    }

    public String getAttName() {
        return attName;
    }

    public void setAttName(String attName) {
        this.attName = attName;
    }

    @Override
    public String toString() {
        return "GoodsAttributeRef{" +
                "id=" + id +
                ", goodsInfoId=" + goodsInfoId +
                ", attId=" + attId +
                ", attValue='" + attValue + '\'' +
                ", attName='" + attName + '\'' +
                '}';
    }
}
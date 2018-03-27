package com.yiban.erp.constant;

public enum SellOrderStatus {

    TEMP_STORAGE, //暂挂
    INIT, //初始
    QUALITY_CHECKED, //出库质量审查完成
    SALE_CHECKED, //销售审查完成
    SHIPPING, //运输中
    SHIPPED, //运送完成
    DELETE, //删除的订单
    UNKNOWN; //未知状态


}

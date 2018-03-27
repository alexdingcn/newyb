package com.yiban.erp.exception;

import com.alibaba.fastjson.JSONObject;

public enum ErrorCode {
    FAILED_DELETE_FROM_DB(1000, "无法删除", ErrorDisplay.NOTICE),
    FAILED_INSERT_FROM_DB(1001, "无法添加", ErrorDisplay.NOTICE),
    FAILED_UPDATE_FROM_DB(1002, "无法修改", ErrorDisplay.NOTICE),
    FAILED_PINGYIN_EXCEPTION(1003, "获取拼音缩写失败"),

    // 11xx - 用户
    USER_NAME_NOT_EXISTED(1100, "用户名不存在"),
    LOGIN_PASSWORD_INVALID(1101, "密码错误"),
    LOGIN_USERNAME_MISSING(1102, "请输入用户名"),
    LOGIN_PASSWORD_MISSING(1103, "请输入密码"),
    USER_REGISTER_FAIL(1104, "用户创建失败"),
    USER_GET_FAIL(1105, "获取当前客户信息失败", ErrorDisplay.NOTICE),
    USER_ROLE_SAVE_PARAMS(1106, "保存用户角色信息参数错误", ErrorDisplay.NOTICE),

    // 12xx - 商品
    GOODS_CATEGORY_ID_MISSING(1201, "缺失商品分类ID"),
    GOODS_REMAINED_IN_CATEGORY(1200, "该分类下还有商品,请先移除商品后操作"),

    // 13xx - 采购
    BUY_ORDER_NOT_EXISTED(1300, "采购订单不存在"),
    BUY_ORDER_PARAMS_INVALID(1301, "采购订单参数错误"),
    BUY_ORDER_IS_CHECKED(1302, "采购订单已审核不可修改"),

    // 20xx - 客户
    CUSTOMER_GET_FAIL(2000, "获取客户信息失败"),
    CUSTOMER_DEL_PARAMS_EMPTY(2001, "请选择需要删除的客户"),
    CUSTOMER_CAT_HAVE_CUST(2002, "分组下存在有客户信息, 不能删除", ErrorDisplay.NOTICE),
    CUSTOMER_CAT_HAVE_CAT(2003, "分组下存在子分组, 不能删除", ErrorDisplay.MODAL),
    CUSTOMER_CAT_DEL_PARAMS(2004, "请选择需要删除的分组"),
    CUSTOMER_REQUIRE_PARAMS_ERROR(2005, "验证客户信息必输项失败"),
    CUSTOMER_CERT_PARAMS_ERROR(2006, "验证客户证件信息必输项失败"),
    CUSTOMER_CERT_IMAGE_NO_ERROR(2007, "证件信息的档案编号错误"),
    CUSTOMER_CERT_REMOVE_PARAMS(2008, "请选择需要删除的证件"),
    CUSTOMER_REP_PARAMS_ERROR(2009, "验证客户代表人信息必输项失败"),
    CUSTOMER_REP_REMOVE_PARAMS(2010, "请选择需要删除的代表人信息"),

    // 21xx - 档案
    FILE_TYPE_CREATE_FAIL(2101, "新增档案类型失败"),
    FILE_TYPE_CREATE_PARAMS_ERROR(2102, "类型名称不能为空"),
    FILE_TYPE_EXIST(2103, "文档类型已经存在"),
    FILE_ADD_PARAMS_ERROR(2104, "新建档案信息必输项验证失败"),
    FILE_REMOVE_PARAMS_ERROR(2105, "请选择需要删除的档案信息"),
    FILE_UPLOAD_PARAMS_ERROR(2106, "上传档案文件时获取档案编号失败"),
    FILE_UPLOAD_SIZE_ERROR(2107, "上传的档案附件超出允许上传的大小", ErrorDisplay.NOTICE),
    FILE_UPLOAD_FAIL(2108, "上传档案附件失败", ErrorDisplay.NOTICE),
    FILE_UPLOAD_FILE_TYPE_ERROR(2109, "上传的文件类型错误"),
    FILE_UPLOAD_REMOVE_PARAMS(2110, "请选择需要删除的文件"),
    FILE_GET_INFO_FAIL(2111, "获取档案信息失败"),

    // 22xx - 销售
    SELL_ORDER_PARAM_ERROR(2201, "必输项参数校验失败"),
    SELL_ORDER_DETAIL_EMPTY(2202, "没有需要保存的详情信息"),
    SELL_ORDER_DETAIL_GET_FAIL(2203, "获取订单详情失败"),
    SELL_ORDER_DETAIL_CAN_NOT_UPDATE(2204, "订单详情在订单审批过后不能修改"),
    SELL_ORDER_DETAIL_CAN_NOT_REMOVE(2205, "订单详情在订单审批过后不能删除"),
    SELL_ORDER_REVIEW_SUBMIT_PARAMS(2206, "获取审核参数失败"),
    SELL_ORDER_REVIEW_SUBMIT_ID_ERROR(2207, "存在获取不到需要审核的订单信息"),
    SELL_ORDER_REVIEW_STATUS_ERROR(2208, "已销售审核的订单状态不能取消质量审核", ErrorDisplay.NOTICE),
    SELL_ORDER_SHIP_PARAMS(2209, "运输记录必输项信息缺失"),
    SELL_ORDER_SHIP_NOT_FUND(2210, "获取运输记录失败"),
    SELL_ORDER_CUSTOMER_CANNOT_SELL_GOOD(2211, "客户不允许经营特殊管控商品, 而列表中存在特殊管控商品", ErrorDisplay.NOTICE),
    SELL_ORDER_REMOVE_HAVE_OK_DETAIL(2212, "存在质量审核通过的商品，不能进行删除订单", ErrorDisplay.NOTICE),
    SELL_ORDER_CHECK_SALE_HAVE_UNOK_DETAIL(2213, "存在出库质量检查未通过的商品，不能审核通过", ErrorDisplay.NOTICE),
    SELL_ORDER_QUANTITY_NOT_ENOUGH(2214, "存在库存不足的商品:", ErrorDisplay.MODAL),
    SELL_ORDER_SALE_CHECK_STATUS_ERROR(2215, "订单状态不是质检通过状态，不能进行审核通过", ErrorDisplay.NOTICE),

    // 23xx -承运公司
    SHIP_SAVE_PARAMS_ERROR(2301, "必输参数校验失败"),


    // 41xx - 贷款
    SEND_VERIFY_CODE_FAIL(4104, "发送验证码失败"),
    GET_FACEID_TOKEN_FAIL(4100, "无法获得token"),
    GET_FACEID_RESULT_FAIL(4101, "无法获得FaceID结果"),
    GET_BIZOCR_RESULT_FAIL(4102, "无法获得营业执照OCR结果"),
    MOBILE_MISSING(4105, "手机号缺失"),
    MOBILE_VERIFY_CODE_TOO_FREQUENT(4106, "验证码发送太频繁"),

    ACCESS_PERMISSION(9001, "无访问权限", ErrorDisplay.MODAL);

    private Integer code;
    private String message;
    private ErrorDisplay display;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    ErrorCode(Integer code, String message, ErrorDisplay display) {
        this.code = code;
        this.message = message;
        this.display = display;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ErrorDisplay getDisplay() {
        return display;
    }

    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("code", code);
        obj.put("message", message);
        if (display != null) {
            obj.put("display", display.name());
        }
        return obj.toString();
    }
}

package com.yiban.erpcustomer.exception;

import com.alibaba.fastjson.JSONObject;

public enum ErrorCode {
    FAILED_DELETE_FROM_DB(1000, "无法删除", ErrorDisplay.NOTICE),
    FAILED_INSERT_FROM_DB(1001, "无法添加", ErrorDisplay.NOTICE),
    FAILED_UPDATE_FROM_DB(1002, "无法修改", ErrorDisplay.NOTICE),
    FAILED_PINGYIN_EXCEPTION(1003, "获取拼音缩写失败"),
    MAKE_ORDER_NUMBER_PARAMS(1004, "生产订单号参数错误"),
    FAILED_INSERT_OR_UPDATE_FROM_DB(1005, "无法添加或修改", ErrorDisplay.NOTICE),

    // 11xx - 用户
    USER_NAME_NOT_EXISTED(1100, "用户名不存在"),
    LOGIN_PASSWORD_INVALID(1101, "密码错误"),
    LOGIN_USERNAME_MISSING(1102, "请输入用户名"),
    LOGIN_PASSWORD_MISSING(1103, "请输入密码"),
    USER_REGISTER_FAIL(1104, "用户创建失败"),
    USER_GET_FAIL(1105, "获取客户信息失败", ErrorDisplay.NOTICE),
    USER_ROLE_SAVE_PARAMS(1106, "保存用户角色信息参数错误", ErrorDisplay.NOTICE),
    USER_REGISTER_PARAMS(1107, "注册必输参数存在空值"),
    USER_REGISTER_IDCARD_ERROR(1108, "身份证号验证失败, 请确认是否输入正确"),
    USER_REGISTER_COMPANY_ENABLED(1109, "公司营业执照已注册，请联系运营方进行激活"),
    USER_REGISTER_COMPANY_EXIST(1110, "公司营业执照已注册，请联系公司管理员确认"),
    USER_REGISTER_NICKNAME_EXIST(1111, "用户名已经存在"),
    USER_REGISTER_EXCEPTION(1112, "管理员用户注册异常, 请联系运营方"),
    COMPANY_EXPIRED(1113, "试用账号已过期"),
    USER_LOGIN_UN_ACTIVATE(1114, "账号为未激活"),
    COMPANY_DISABLED(1115, "公司处于未激活状态，请联系运营方"),
    USER_MOBILE_EXIST(1116, "手机号已经存在, 不可再次注册"),
    USER_MOBILE_MISSING(1117, "手机号缺失"),

    // 12xx - 商品
    GOODS_CATEGORY_ID_MISSING(1201, "缺失商品分类ID"),
    GOODS_REMAINED_IN_CATEGORY(1200, "该分类下还有商品,请先移除商品后操作"),
    GOODS_CHILD_IN_CATEGORY(1200, "该分类下还有子分类, 请先移除对应的子分类后操作"),
    GOODS_CATEGORY_NAME_MISS(1202, "商品类别名称不能为空"),
    GOODS_ATTRIBUTE_NAME_ERROR(1203, "商品自定义字段名称不能为空"),
    GOODS_SPEC_NAME_NO_NULL(1204, "多规格的名称和序号不能为空"),
    GOODS_SPEC_SUB_SPEC_EMPTY(1205, "多规格的子规格不能为空列表"),
    GOODS_SPEC_GET_FAIL(1206, "获取商品多规格失败"),
    GOODS_SPEC_USED_CANNOT_REMOVE(1207, "多规格有商品在使用，不能删除"),
    GOODS_BRAND_USED_CANNOT_REMOVE(1208, "商品品牌有商品正在使用，不能删除"),
    GOODS_GET_RESULT_NULL(1209, "获取商品信息失败"),
    GOODS_USED_CANNOT_DELETE(1210, "商品存在使用的数据，不能删除"),
    GOODS_DETAIL_GET_FAIL(1211, "获取商品详情信息失败"),
    GOODS_DETAIL_USED_CANNOT_DELETE(1212, "当前产品详情信息存在使用信息，不能删除"),
    GOODS_OLD_SPEC_USED(1213, "商品需要删除的多规格信息存在使用情况，不能删除"),
    GOODS_ATTRIBUTE_GET_FAIL(1214, "商品自定义属性信息获取失败"),
    GOODS_ATTRIBUTE_USED(1215, "商品自定义属性正在使用，不能删除"),
    GOODS_PRICE_REQUEST_LIST_EMPTY(1216, "获取修改数据失败."),

    // 13xx - 采购
    BUY_ORDER_NOT_EXISTED(1300, "采购订单不存在"),
    BUY_ORDER_PARAMS_INVALID(1301, "采购订单参数错误"),
    BUY_ORDER_IS_CHECKED(1302, "采购订单已审核不可修改"),
    BUY_BACK_SUPPLIER_ERROR(1303, "采购退出申请的供应商与详情供应商不匹配"),
    BUY_BACK_QUANTITY_ERROR(1304, "采购退出详情的退出数量不能小于等于0"),
    BUY_BACK_PRICE_ERROR(1305, "采购退出详情的单价不能小于0"),
    BUY_BACK_ORDER_GET_FAIL(1306, "获取退出单信息失败"),
    BUY_BACK_ORDER_STATUS_CANNOT_CHECK(1307, "当前状态下不能对退出单审核"),
    BUY_BACK_CHECK_CANCEL_ERROR(1308, "退出单状态不是质量复核通过，不能进行取消复核操作"),
    BUY_ORDER_COLD_MANAGE_ERROR(1309, "存在有冷链经营类型产品，采购单的温控方式和运输方式必输"),
    BUY_ORDER_STATUS_CANNOT_REMOVE(1310, "采购订单当前状态下不能删除"),


    // 15xx - 盘点
    CHECK_PLAN_NO_GOODS(1500, "无符合盘点条件的商品"),
    CHECK_PLAN_TYPE_ERROR(1501, "暂未开放该类型盘点"),
    CHECK_PLAN_DETAIL_UPDATE_ERROR(1502, "执行盘点操作-更新失败"),
    CHECK_PLAN_DETAIL_MORE_ERROR(1503, "盘盈记录添加失败-盘盈数据异常"),
    CHECK_PLAN_PASS_ERROR(1504, "盘点单审核失败-盘点单中没有明细记录"),
    CHECK_PLAN_PASS_STATE_ERROR(1505, "盘点单审核失败-盘点单包含异常未处理或已经审核的明细"),
    CHECK_PLAN_PASS_REPET_ERROR(1506, "已审核过盘点单，无法重复审核"),
    CHECK_PLAN_PASS_OUT_ERROR(1507, "盘点审核，盘亏数量超过库存数量无法出库"),
    CHECK_PLAN_PASS_VALIDATE_ERROR(1510, "盘点审核失败-缺少必要参数"),
    CHECK_PLAN_FORM_MORE(1511, "盘点登记失败，系统内同一张单据存在多个盘点表"),
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
    CUSTOMER_GET_FAIL_OR_UNENABLE(2011, "获取客户信息失败或者客户已经禁用"),

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
    SELL_ORDER_WAREHOUSE_FROZEN(2215, "当前仓库正在盘库冻结，不能做出库操作"),
    SELL_ORDER_REMOVE_STATUS_ERROR(2216, "当前销售订单已经审核通过, 不能删除"),
    SELL_BACK_COST_AMOUNT_ERROR(2220, "销售退单的免零金额必须小于等于0"),
    SELL_BACK_ADD_DETAIL_EMPTY(2221, "销售退单产品详情不能为空"),
    SELL_BACK_ADD_DETAIL_QUANTITY_ERROR(2222, "销售退单详情的退货数量需要存在大于0的数量"),
    SELL_BACK_GET_OUT_RECORD_FAIL(2223, "销售退货单获取关联销售出库单失败"),
    SELL_BACK_ORDER_GET_FAIL(2224, "销售退货单信息获取失败"),
    SELL_BACK_CHECK_STATUS_ERROR(2225, "销售退货审核状态错误, 请联系运营方排查原因.", ErrorDisplay.MODAL),
    SELL_BACK_INIT_SALE_CHECK_ERROR(2226, "销售退货单当前状态不可操作销售经理审核"),
    SELL_BACK_INIT_QUALITY_CHECK_ERROR(2227, "销售退货单当前状态不可操作质量经理审核"),
    SELL_BACK_QUALITY_CHECK_ERROR(2228, "销售退货单当前状态不可操作质量复核验收"),
    SELL_BACK_QUALITY_CHECK_CANCEL_ERROR(2229, "销售退货单当前状态不可操作质量复核取消"),
    SELL_BACK_REMOVE_STATUS_ERROR(2230, "销售退货单已经通过了终审，不能做删除操作"),
    SELL_BACK_DETAIL_GET_FAIL(2231, "获取销售退货单详情信息失败."),
    SELL_BACK_RECEIVE_STATUS_ERROR(2232, "销售退货单当前状态不可操作收货确认."),
    SELL_BACK_RIGHT_QUANTITY_ERROR(2233, "合格数据错误."),
    SELL_TOTAL_AMOUNT_ERROR(2234, "销售订单总金额不能小于0"),
    SELL_FREE_AMOUNT_ERROR(2235, "免零金额不能小于0"),
    SELL_ORDER_ALREADY_PAID(2236, "订单已付清"),
    SELL_ORDER_COMPANY_NOT_MATCH(2237, "订单信息错误"),
    SELL_ORDER_UPDATE_PAYMENT_FAIL(2238, "更新支付信息错误"),


    // 23xx -承运公司
    SHIP_SAVE_PARAMS_ERROR(2301, "必输参数校验失败"),

    // 24xx -收货入库
    RECEIVE_SAVE_PRAMS_INVALID(2401, "入库质量检查单参数错误"),
    RECEIVE_ORDER_NOT_FOUND(2402, "获取入库质量单信息失败"),
    RECEIVE_ORDER_CANNOT_UPDATE(2403, "当前订单处于不可修改状态"),
    RECEIVE_QUERY_PARAM_ERROR(2404, "查询参数错误"),
    RECEIVE_ORDER_GET_FAIL(2405, "获取订单失败"),
    RECEIVE_ORDER_CAN_NOT_REMOVE(2406, "订单处于不可删除状态"),
    RECEIVE_BUY_ORDER_STATUS(2407, "采购订单不处于审核通过状态"),
    RECEIVE_CHECK_STATUS_ERROR(2408, "当前订单不处于可验收状态"),
    RECEIVE_DETAIL_REMOVE_STATUS(2409, "订单详情已经验证通过，不能删除"),
    RECEIVE_DETAIL_SAVE_PARAMS_ERROR(2410, "订单详情参数与订单信息不匹配", ErrorDisplay.NOTICE),
    RECEIVE_ORDER_STATUS_NOT_CHECKED(2411, "订单未处于验收通过的状态，不能审核通过"),
    RECEIVE_ORDER_DETAIL_EMPTY(2412, "获取订单商品信息失败"),
    RECEIVE_ORDER_WAREHOUSE_NULL(2413, "仓库信息错误，系统异常", ErrorDisplay.NOTICE),
    RECEIVE_ORDER_WAREHOUSE_FROZEN(2413, "当前仓库正在盘库冻结，不能做入库操作", ErrorDisplay.NOTICE),

    WAREHOUSE_LOCATION_EXIST(2501, "仓库位置已经存在, 不能添加同名库位"),

    //26
    REPERTORY_CHANGE_PARAMS_ERROR(2601, "必输参数校验失败"),
    OUT_ORDER_QUANTITY_ERROR(2602, "当前库存商品数量不足，不能做出库操作", ErrorDisplay.NOTICE),
    OUT_REVIEW_STATE_ERROR(2603, "出库复核失败,当前状态无法复核，", ErrorDisplay.NOTICE),
    OUT_UNREVIEW_STATE_ERROR(2604, "撤销复核失败，当前状态无法撤销", ErrorDisplay.NOTICE),
    OUT_ORDER_WAREHOUSE_FROZEN(2613, "当前仓库正在盘库冻结，不能做出库操作", ErrorDisplay.NOTICE),

    // 31xx财务账
    FINANCIAL_SELL_ORDER_ERROR(3101, "当前销售单不能生成往来账信息"),
    FINANCIAL_SELL_ORDER_EXIST(3102, "当前销售单已经存在对应的往来账流水，不能再次建立"),
    FINANCIAL_IN_ORDER_ERROR(3103, "当前入库单不能生成往来账信息"),
    FINANCIAL_BIZ_TYPE_ERROR(3104, "财务记账业务类型错误."),
    FINANCIAL_ACTION_ERROR(3105, "系统异常，不支持当前业务类型的操作"),
    FINANCIAL_CANCEL_REF_ID_EMPTY(3106, "取消操作必须存在关联单"),
    FINANCIAL_CANNOT_CANCEL(3017, "当前交易不能做取消操作"),
    FINANCIAL_GET_LOCK_FAIL(3019, "获取交易锁失败, 请联系运营人员查看具体原因"),
    FINANCIAL_GET_FAIL(3020, "获取往来账流水信息失败"),
    FINANCIAL_CANNOT_CANCEL_AGAIN(3021, "往来账流水已经做过取消操作，不能再次操作取消"),
    FINANCIAL_AMOUNT_ERROR(3022, "发生金额必须大于0"),
    FINANCIAL_CUST_ID_NULL(3023, "往来账户必输"),
    FINANCIAL_CUST_GET_FAIL(3023, "往来账户获取失败"),
    FINANCIAL_PRE_STATUS_CANNOT_CANCEL(3024, "预收/付款不在未使用的状态下不能做取消操作"),
    FINANCIAL_PRE_STATUS_CANNOT_OFFSET(3025, "预收/付款不在未使用的状态下不能做冲销操作"),
    FINANCIAL_OFFSET_REF_BIZNO_ERROR(3026, "冲销关联的流水号错误，获取不到对应往来账流水"),
    FINANCIAL_CUST_MUST_SAME(3027, "冲销交易关联的流水信息的往来账户必须与预收/付款记录的账户相同"),
    FINANCIAL_SELL_BACK_ORDER_ERROR(3028, "当前销售退货单不能生产往来账信息."),
    FINANCIAL_SELL_BACK_ORDER_EXIST(3029, "当前销售退货单已经生产过往来账，不能在次创建"),

    // 41xx - 贷款
    SEND_VERIFY_CODE_FAIL(4104, "发送验证码失败"),
    GET_FACEID_TOKEN_FAIL(4100, "无法获得token"),
    GET_FACEID_RESULT_FAIL(4101, "无法获得FaceID结果"),
    GET_BIZOCR_RESULT_FAIL(4102, "无法获得营业执照OCR结果"),
    MOBILE_MISSING(4105, "手机号缺失"),
    MOBILE_VERIFY_CODE_TOO_FREQUENT(4106, "验证码发送太频繁"),
    VERIFY_CODE_VALIDATE_FAIL(4107, "短信验证码错误"),


    // 50xx - 首页
    BANNER_NOT_EXISTED(5001, "广告不存在"),


    ACCESS_PERMISSION(9001, "无访问权限", ErrorDisplay.MODAL),
    PARAMETER_MISSING(9002, "缺少必填字段"),
    MESSAGE_GET_FAIL(9003, "获取消息信息失败"),
    LOGIN_STATUS_MISS(9004, "登录态失效", ErrorDisplay.NOTICE),
    COMPANY_MISS(9005, "获取公司信息失败", ErrorDisplay.MODAL),
    RABBITMQ_CONNECTION_FAIL(9006, "系统异常, 队列连接失败"),
    RABBITMQ_SEND_MESSAGE_FAIL(9007, "系统异常, 消息发送失败"),
    ;

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


## 医伴 ERP 表结构描述

### 仓库：warehouse

> 记录仓库的定义信息 

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
company_id|Integer|公司ID
name|String|仓库名称
status|String|仓库当前状态(NORMAL-正常, FROZEN-冻结, DELETE-删除)
description|String|描述信息
created_by|String|创建人nickname
created_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|最近一次修改时间


### 仓库位置：warehouse_location

> 记录一个仓库中的库存位置标识。

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
warehouse_id|Integer|仓库ID(fk)
location|String|库位
status|String|库位状态(目前没有定义具体)
comment|String|描述信息
create_by|String|创建人nickname
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|最近一次修改时间


### 库存：repertory_info

> 记录一个仓库的库存流水信息。

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID(fk)
warehouse_id|Integer|仓库ID(fk)
location|String|仓库库位
in_user_id|Long|入库员ID(创建记录的人员，也是入库单审核通过人员ID)
goods_id|Long|商品ID(fk)
factory_id|Long|厂商ID
batch_code|String|批次号
in_quantity|Integer|入库数量
quantity|Integer|当前库存量
buy_price|Decimal|采购价
is_exp|Boolean|是否失效(默认0-否)
sale_enable|Boolean|是否可以销售
product_date|Date|生产日期
exp_date|Date|有效期至
in_date|Date|入库时间
order_id|Long|入库单ID
supplier_id|Long|供应商ID
supplier_contact_id|Long|供应商代表
buyer_id|Long|采购员ID
store_state|Boolean|是否为代销
create_by|String|创建人nickname
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|最近一次修改时间


### 库存限额表：repertory_limit

> 按仓库和商品编号，定义每一个商品的预警阀值

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
warehouse_id|Integer|仓库ID
goods_id|Long|商品ID
normal_limit|Decimal(14,3)|正常库存位
low_limit|Decimal(14,3)|低库存位
hight_limit|Decimal(14,3)|高库存位
create_by|String|创建人nickname
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|最近一次修改时间


### 库存结存表：repertory_balance

> 统计库存数据信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)


### 入库订单表：repository_order

> 所有的入库单信息建立和质量审查过程中使用

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
ref_order_id|Long|上级来源订单ID(可以存在不同的入库原始订单)
order_number|String|入库单编号
ref_no|String|入库单自定义编号
ref_type|String|入库单类型
supplier_id|Long|供应商ID
supplier_contact_id|Long|供应上代表
buyer_id|Long|采购员ID
status|String|当前入库单状态
key_word|String|摘要
receive_date|Datetime|收货时间
pay_date|Datetime|支付时间
goods_bill_no|String|随货票号
temp_control_method|Long|温控方式
temp_control_status|Long|温控状况
receive_temp|Decimal|到货温度
check_temp|Decimal|验收温度
ship_company_id|Integer|承运单位ID
ship_method|Long|运输方式
ship_tool|Long|运输工具
ship_car_no|String|运输车牌号
ship_driver_name|String|驾驶员
ship_start_date|Datetime|运输启运时间
ship_end_date|Datetime|运输预计到货时间
ship_entrust_no|String|委托运输单号
ship_start_address|String|发运地点
warehouse_id|Integer|仓库点
buy_type|String|采购属性
term|Integer|账期
come_from|Sting|直调来货单位
bill_type|Long|发票种类
file_no|String|检验报告档案
create_by|String|创建人nickname
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|最近一次修改时间



### 入库订单详情：repository_order_detail

> 入库单详情信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
repository_order_id|Long|入库单ID
goods_id|Long|商品ID
receive_quality|Decimal(14,3)|收货数量
big_quality|Decimal(14,3)|大件数量
free|Decimal(14,3)|赠送数量
price|Decimal(14,3)|单价
exp_date|Date|有效期至
product_date|Date|生产日期
batch_code|String|批次号
amount|Decimal(14,3)|金额
warehouse_location|String|库位
reject_quality|Decimal(14,3)|拒收数量
reject_comment|String|拒收理由
tax_rate|Decimal(14,3)|税率
in_count|Decimal|入库数量
right_count|Decimal(14,3)|合格数量
error_count|Decimal(14,3)|不合格数量
survey_quality|Decimal(14,3)|抽查数量
survey_date|Datetime|抽样检查时间
survey_user|String|抽样检查人
survey_address|String|检查地址
survey_result|String|检查结果
survey_target|String|检查目标
check_status|Boolean|是否已经验收
error_plan|String|不合格商品处理方案
sale_cert|String|检查营销人员证书
error_reason|String|不合格原因
check_temp_method|Long|检验温控方式
check_user|String|检验员
check_time|Datetime|检验时间
check_result|String|检验结果
create_by|String|创建人nickname
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|最近一次修改时间


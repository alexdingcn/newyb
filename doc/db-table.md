
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
order_detail_id|Long|入库单明细ID
supplier_id|Long|供应商ID
supplier_contact_id|Long|供应商代表
buyer_id|Long|采购员ID
sale_state|Boolean|是否为代销
create_by|String|创建人nickname
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|最近一次修改时间


### 库存盘点计划表：repertory_check_plan

> 记录每次发起库存盘点启动信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
check_type|Integer|盘库类型 0-库存盘点  1-直接盘库  2-单品盘库
check_code|String|盘点单号
company_id|Integer|公司ID(fk)
warehouse_id|Integer|仓库ID
warehouse_location|String|库位
check_date|Datetime|盘点时间
make_user_id|Long|制单人
check_user_id|Long|审核人
state|Integer|状态 0:待处理 1:未审核 2审核 3生成盘盈入库单、盘亏出库单
comment|String|备注
check_response_user|String|盘库监督员
manager|String|经理
manager_note|String|经理意见
finance|String|财务
finance_note|String|财务意见
create_by|String|创建人nickname
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|最近一次修改时间


### 库存盘点计划表：repertory_check_plan_detail

> 记录每次发起库存盘点启动信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
check_plan_id|Long|盘库计划单ID
make_user_id|Long|盘查人
form_no|String|盘库表单号
form_status|String|表单确认状态 UNCHECK-未确认 CHECKED-已确认
repertory_info_id|Long|库存记录ID
goods_id|Long|商品ID
batch_code|String|批次号
acc_limit|Decimal(14,3)|账面数量
check_limit|Decimal(14,3)|盘点数量
check_note|String|盘点结果
check_status|Integer|盘亏盘盈状态 0-正常 1-盘盈 -1-盘亏
price|Decimal(14,3)|单价
amount|Decimal(14,3)|金额
product_date|Date|生产日期
exp_date|Date|有效期至
warehouse_location|String|货位号
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


### 入库订单表：repertory_in

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
total_quantity|Decimal(14,3)|总计入库数量
total_amount|Decimal(14,3)|总计金额




### 入库订单详情：repertory_in_detail

> 入库单详情信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
in_order_id|Long|入库单ID
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
buy_order_quality|Decimal(14,3)|采购数量


### 出库订单表：repertory_out

> 所有的出库单信息建立和质量审查过程中使用

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
warehouse_id|Integer|仓库ID
ref_type|String|出库方式
out_date|Date|出库日期
ref_order_id|Long|出库关联的ID
ref_order_number|String|出库单编号
customer_id|Integer|客户ID
customer_name|String|客户名称
customer_rep_i|Integer|客户代表ID
customer_rep_name|String|客户代表名称
go_to|String|去向
total_quantity|Decimal(14,3)|总计数量
total_amount|Decimal(14,3)|总计金额
make_order_user|String|制单人
check_order_user|String|审核人
check_date|Datetime|审核时间
file_no|String|档案信息
comment|String|描述
created_by|String|创建人
updated_by|String|修改人
created_time|Datetime|创建时间
updated_time|Datetime|修改时间



### 出库订单表：repertory_out_detail

> 所有的出库单信息建立和质量审查过程中使用

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
repertory_out_id|Long|出库单ID
repertory_info_id|Long|库存记录ID
goods_id|Long|商品ID
batch_code|String|批次号
location|String|库位
product_date|Date|生产日期
exp_date|Date|有效期至
quantity|Decimal(14,3)|数量
free|Decimal(14,3)|免费数量
price|Decimal(14,3)|价格
dis_price|Decimal(14,3)|折扣
amount|Decimal(14,3)|金额
tax_price|Decimal(14,3)|税率
check_user|String|检查员
check_date|Datetime|检查时间
check_result|String|检查结果
created_by|String|创建人
updated_by|String|修改人
created_time|Datetime|创建时间
updated_time|Datetime|修改时间



### 公司表：users

> 公司信息定义

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
name|String|公司名称
license|String|营业执照
enabled|Boolean|是否可用
created_time|Datetime|创建时间
updated_time|Datetime|修改时间
created_by|String|创建人
updated_by|String|修改人
account_amount|Decimal(14,3)|账户余额


### 用户表：users

> 用户表

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
nickname|String|用户名
mobile|String|手机号
realname|String|真实姓名
email|String|电子邮箱
phone|String|固定电话
idcard|String|身份证号
forget_token|String|忘记密码的token
address|String|地址
birthday|Date|生日
sex|Integer|性别 0:女 1:男 -1:异常
status|Integer|状态：1:启用 0:未激活 -1:禁用
super_user|Boolean|是否为超级管理员用户
comment|String|备注
created_by|String|创建人
updated_by|String|修改人
created_time|Datetime|创建时间
update_time|Datetime|修改时间


### 用户认证表：user_auths

> 用户认证信息登记表

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
user_id|Long|用户ID
identifier|String|身份唯一标识
identifier_type|String|身份标识类型：username/email/mobile/qq/weibo/weixin
credential|String|密码或者token
verified|Boolean|是否验证
created_by|String|创建人
updated_by|String|修改人
craeted_time|Datetime|创建时间
updated_time|Datetime|修改时间


### 用户角色表：user_roles

> 登记用户的角色信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
user_id|Long|用户ID
name|String|角色名称
can_write|Boolean|是否可写
can_update|Boolean|是否修改
can_audit|Boolean|是否可以审核
can_delete|Boolean|是否可以审核
created_by|String|创建人
updated_by|String|修改人
craeted_time|Datetime|创建时间
updated_time|Datetime|修改时间


### 用户菜单路由权限表：user_route

> 用于控制用户可见的菜单相，定义的route_name需要与路由定义的name相同,逻辑上路由定义的name是唯一的

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
user_id|Long|用户ID
route_name|String|路由名称
create_by|String|创建人
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|修改时间


### 商品类别表：goods_category

> 记录商品的分类信息表

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
name|String|类别名称
parent|Integer|上级类别(目前不启用)
sequence|Integer|排序编号
comment|String|备注
created_time|DateTime|创建时间
updated_time|Datetime|修改时间
created_by|String|创建人
updated_by|String|修改人
company_id|Integer|公司ID


### 商品信息表：goods

> 记录商品的信息表

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
category_id|Integer|商品类别ID
name|String|商品通俗名称
full_name|String|商用名称
origin|String|产地
spec|String|规格
serial|String|条形码
enable|Boolean|是否可用
is_proxy|Boolean|是否代销
factory_id|Long|厂商ID
unit|Long|计量单位
pack_unit|Long|大件计量单位
medium_pack|Decimal(14,3)|中件装量
big_pack|Decimal(14,3)|大件装量
pinyin|String|拼音
storage_condition|Long|存储条件
comment|String|备注
cert_no|String|注册证编号
cert_exp_date|Date|注册证有效期至
cert_file_no|String|注册证档案编号
brand_no|String|注册商标证件号
brand_exp_date|Date|注册商标证件有效期
brand_file_no|String|注册商标档案编号
permit_no|String|批准文号编号
permit_exp_date|Date|批准文号有效期
permit_file_no|String|批准文号档案编号
archive_no|String|档案编号
in_check|Boolean|入库检查
first_check|Boolean|首营检查
special_managed|Boolean|是否特殊管控
need_care|Boolean|是否需要护理
in_tax|Decimal(4,2)|进项税
out_tax|Decimal(4,2)|销项税
valid_months|Integer|有效期限(月份)
warning_days|Integer|预警天数
cantact|String|联系方式
cure_range|String|主治功能
sale_policy|String|销售策略
is_foreign|String|是否进口药
prescription_id|Long|处方/非处方属性
med_type_id|Long|中西药属性
is_shebao|Boolean|社保目录
specific_med_id|Long|特殊管理属性
jx_id|Long|剂型属性
base_med_id|Long|药基属性
func_cat_id|Long|功能分类属性
medication_id|Long|给药途径属性
care_time_id|Long|护养标志属性
gmp_type_id|Long|GMP属性
abc_type_id|Long|ABC属性
scope_id|Long|经营范围属性
new_type_id|Long|新特药属性
retail_price|Decimal(10,3)|零售价
batch_price|Decimal(10,3)|批发价
member_price|Decimal(10,3)|会员价
online_price|Decimal(10,3)|挂网价
split_price|Decimal(10,3)|拆零价
low_price|Decimal(10,3)|最低价
hight_price|Decimal(10,3)|最高价
created_by|String|创建人
update_by|String|修改人
created_time|Datetime|创建时间
updated_time|Datetime|修改时间



### 生产企业信息表：factory

> 记录生产企业信息的表

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
companyId|String|公司ID
name|String|生产企业名称
origin|String|产地
permit|String|生产许可证
permit_exp|Date|生产许可证有效期
license|String|营业执照
license_exp|Date|营业执照有效期
pinyin|String|拼音简称
address|String|详细地址
city|String|所在省市区(带城市编码)
postcode|String|邮编
phone|String|联系电话
fax|String|传真
email|String|电子邮件
contact|String|联系人
contact_phone|String|联系人电话
employee|String|负责人
is_gmp|Boolean|GMP认证
bank_name|String|开户行
bank_account|String|银行账号
tax_number|String|税号
comment|String|备注
created_time|Datetime|创建时间
created_by|String|创建人
updated_by|String|修改人
updated_time|String|修改时间
file_no|String|生产商档案编号



### 档案类型表：file_type

> 记录档案的类型信息

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
company_id|Integer|公司ID
type_name|String|类型名称
create_by|String|创建人
update_by|String|修改人
create_time|Datetime|创建时间
update_time|Datetime|修改时间



### 档案信息表：file_info

> 记录档案信息

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
company_id|Integer|公司ID
file_no|String|档案编号
file_status|String|档案状态
file_name|String|档案名称
file_type|String|档案类型
comment|String|描述
create_by|String|创建人
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|修改时间



### 档案详情信息表：file_upload

> 记录档案的上传文件的详情

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
file_id|Integer|档案ID
comment|String|文件备注
original_name|String|上传文件的名称
location|String|存档位置
load_url|String|访问路径
expiration|Datetime|过期时间
create_by|String|创建人
update_by|String|修改人
create_time|Datetime|创建时间
update_time|Datetime|修改时间


### 系统基础选择项信息表: options

> 记录各种自建的选择项的信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
type|String|类型
value|String|值
description|String|描述信息
enabled|Boolean|是否可用
created_by|String|创建人
updated_by|String|修改人
created_time|Datetime|创建时间
updated_time|Datetime|修改时间



### 消息信息表：message_info

> 记录各种消息信息的表

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
status|String|消息状态
role_type|String|可见的角色类型
title|String|消息标题
content|String|消息内容
create_by|String|创建人
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|修改时间



### 消息信息表：message_option

> 记录各种消息信息备注信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
user_id|Long|用户ID
message_id|Long|消息ID
option_status|String|状态
option_result|String|评论结果
create_by|String|创建人
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|修改时间



### 供应商信息表：supplier

> 记录供应商信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
name|String|供应商名称
pinyin|String|拼音
enabled|Boolean|是否可用(默认启用)
term|Integer|账期
city|String|归属地区,包含地区码
address|String|详细地址
postcode|String|邮编
fax|String|传真
phone|String|联系电话
email|String|电子邮件
contact|String|联系人
contact_phone|String|联系人电话
employee|String|负责人
legal_person|String|法人代表
discount|Decimal(5,2)|折扣率
bank_account|String|银行账户
bank_name|String|银行名称
bank_number|String|银行账号
tax_number|String|税号
billing_method_id|Long|结算方式
supplier_type_id|Long|类型
have_stamp|Boolean|是否有印章
have_bill_template|Boolean|是否有票据模板
check_first|Boolean|是否首营检查
is_factory|Boolean|是否生产企业
is_direct_supplier|Boolean|是否直调供应商
is_cold_business|Boolean|是否冷链经营
can_special|Boolean|是否可以经营特殊商品
warehouse_address|String|仓库地址
business_scope|String|经营范围
comment|String|备注
license|String|营业执照
license_exp|Date|营业执照有效期
organization_no|String|组织机构代码证
organization_exp|Date|组织机构代码证有效期
gsp_gmp_no|String|GSP/GMP证件号
gsp_gmp_exp|Date|GSP/GMP证有效期
quality_protocol_no|String|质量保证协议
quality_protocol_exp|Date|质量保证协议有效期
sale_protocol_no|String|购销协议
sale_protocol_exp|Date|购销协议有效期
legal_protocol_no|String|法人委托书
legal_protocol_exp|Date|法人委托书有效期
other_protocol_no|String|其他协议
other_protocol_exp|Date|其他协议有效期
file_no|String|档案编号
created_time|Datetime|创建时间
created_by|String|创建人
updated_by|String|修改人
updated_time|String|修改时间
account_amount|Decimal(14,3)|账户余额



### 供应商代表信息表：supplier_contact

> 记录供应商代表信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
supplier_id|Long|供应商ID
name|String|供应商代表名称
idcard|String|身份证号
enabled|Boolean|是否启用(默认启用)
phone|String|联系方式
city|String|所在城市(带城市编号)
business_scope|String|经营区域
comment|String|备注
created_time|Datetime|创建时间
created_by|String|创建人
updated_by|String|修改人
updated_time|String|修改时间



### 客户分类信息表：customer_category

> 记录供应商代表信息

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
company_id|Integer|公司ID
name|String|类型名称
parent_id|Integer|上级Id
sequence_no|Integer|排序
comment|String|描述
create_time|Datetime|创建时间
update_time|Datetime|修改时间
create_by|String|创建人
update_by|String|修改人


### 客户信息表：customer

> 记录客户信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
customer_no|String|系统编号
category_id|Integer|客户类型
name|String|客户名称
pinyin|String|拼音
status|String|状态(NORMAL, DELETE)
enabled|Boolean|是否启用
can_sale_special|Boolean|是否可以经营特殊产品
is_limit_special|Boolean|含麻黄碱药品限购
city|String|所在城市
address|String|详细地址
postcode|String|邮编
legal_person|String|法人代表
employee|String|负责人
contact_phone|String|联系电话
contact_fax|String|传真
email|String|邮箱
is_direction|Boolean|是否直调企业
sale_area|String|销售区域
sale_man|String|业务员
member_level|String|会员等级
class_att_one|String|分类属性1
class_att_two|String|分类属性2
stamp_template|String|印章模本编号
bill_template|String|票据模本编号
is_cold_business|Boolean|冷链经营
is_two_ticket|Boolean|是否两票制
business_scope|String|经营范围
account_term|Integer|账期
account_name|String|银行账户
bank_account|String|银行账号
bank_name|String|开户行
tax_account|String|税号
qua_check|String|资格审查
file_no|String|档案编号
supervise_no|String|电子监管码
comment|String|备注
create_time|Datetime|创建时间
update_time|Datetime|修改时间
create_by|String|创建人
update_by|String|修改人
account_amount|Decimal(14,3)|账户余额



### 客户代表信息表：customer

> 记录客户的代表人信息,收货地址信息

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
customer_id|Long|客户ID
name|String|名称
contact_phone|String|联系电话
repertory_address|String|收货地址
postcode|String|邮编
enabled|Boolean|是否启用
default_time|Datetime|设置默认值使用的时间
comment|String|备注
create_time|Datetime|创建时间
update_time|Datetime|修改时间
create_by|String|创建人
update_by|String|修改人



### 承运公司信息表：ship_company

> 记录承运公司基础信息

字段名|类型|描述
-|-|-
id|Integer|(自增类型ID pk)
company_id|Integer|公司ID
name|String|名称
enabled|Boolean|是否启用
license|String|执照编号
license_exp|Date|执照有效期至
file_no|String|档案编号
employee|String|负责人
phone|String|联系电话
fax|String|传真
email|String|邮箱
contact_user|String|联系人
contact_phone|String|联系人电话
postcode|String|邮编
address|String|详细地址
comment|String|备注
create_by|String|创建人
create_time|Datetime|创建时间
update_by|String|修改人
update_time|Datetime|修改时间


### 采购订单表：buy_order

> 记录采购订单的信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
company_id|Integer|公司ID
order_number|String|系统单号
supplier_id|Long|供应商ID
supplier_contact_id|Long|供应商代表
buyer_id|Long|采购员ID
status|String|当前状态
ship_method_id|Long|运输方式
ship_tool_id|Long|运输工具
temper_control_id|Long|温控方式
warehouse_id|Integer|仓库ID
eta||Datetime|预计到货时间
ref_no|String|自定义单号
comment|String|备注
created_by|String|创建人
updated_by|String|修改人
created_time|Datetime|创建时间
updated_time|Datetime|修改时间
check_by|String|审查员
check_result|String|审查结果
check_time|String|审查时间



### 采购订单详情表：buy_order_detail

> 记录采购订单详情信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
buy_order_id|Long|采购单ID
goods_id|Long|商品ID
goods_name|String|商品名称
quantity|Decimal(14,3)|采购数量
buy_price|Decimal(14,3)|采购单价
amount|Decimal(14,3)|金额
shipped_quantity|Decimal(14,3)|到货数量
already_fapiao|Boolean|是否收到发票
created_by|String|创建人
updated_by|String|修改人
created_time|Datetime|创建时间
updated_time|Datetime|修改时间



### 销售订单表：sell_order

> 记录销售订单的信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
commpany_id|Integer|公司ID
order_number|String|订单编号
ref_no|String|自定义单号
status|String|订单状态
customer_id|Long|客户ID
customer_rep_id|Integer|客户代表ID
sale_id|Long|销售员ID
warehouse_id|Integer|仓库ID
temper_control_id|Long|温控方式
create_order_date|Date|制单日
take_goods_user|String|提货员
pay_order_date|Date|预计付款日
mark_up_rate|Decimal(14,3)|加价率
ship_method|Long|运输方式
ship_tool|Long|运输工具
ship_company_id|Integer|承运公司
comment|String|备注
create_by|String|创建人
update_by|String|修改人
create_time|Datetime|创建时间
update_time|Datetime|修改时间
total_quantity|Decimal(14,3)|总计数量
total_amount|Decimal(14,3)|总计金额



### 销售订单详情表：sell_order_detail

> 记录销售订单详细信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
sell_order_id|Long|订单ID
repertory_id|Long|存库ID
quantity|Decimal(14,3)|销售数量
fix_price|Decimal(14,3)|定价
dis_price|Decimal(14,3)|折扣
free|Decimal(14,3)|赠送数量
real_price|Decimal(14,3)|实价
single_price|Decimal(14,3)|单价
amount|Decimal(14,3)|金额
tax_rate|Decimal(14,3)|税率
check_status|String|检查状态
check_result|String|检查结果
check_date|Datetime|检查时间
check_user|String|检查员
create_by|String|创建人
update_by|String|修改人
create_time|Datetime|创建时间
update_time|Datetime|修改时间



### 销售订单运输记录表：sell_order_ship

> 记录销售订单的运输记录信息

字段名|类型|描述
-|-|-
id|Long|(自增类型ID pk)
sell_order_id|Long|运输记录
ship_company_id|Integer|运输公司ID
issuance_date|Datetime|发货时间
ship_number|String|运输单号
temper_control_id|Long|温控方式
ship_tool_id|Long|运输工具
ship_method|Long|运输方式
ship_address|String|发货地址
receive_user_name|String|收货人
receive_user_phone|String|收货人电话
receive_address|String|收货地址
car_number|String|车牌号
ship_phone|String|承运电话
driver_name|String|驾驶员
driver_file_no|String|驾驶证档案
ship_temper|Decimal(6,3)|启运温度
ship_quantity|Decimal(6,3)|运输数量
operator|String|经办人
mileage|Decimal(10,3)|里程数
ship_start_time|Datetime|启运时间
ship_end_time|Datetime|到货时间
comment|String|备注
create_by|String|创建人
update_by|String|修改人
create_time|Datetime|创建时间
update_time|Datetime|修改时间


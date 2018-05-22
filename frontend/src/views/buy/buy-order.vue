<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
	<Row>
		<Card>
			<p slot="title" >
				<Icon type="document"></Icon> 采购入库制单
			</p>
			<div slot="extra">

				<ButtonGroup class="padding-left-20">
					<Button type="primary" icon="android-list" @click="saveBuyOrder" :loading="saving">保存</Button>
				</ButtonGroup>
			</div>

			<Form :label-width="85" :rules="ruleValidate" :model="buyOrder" ref="buyOrderForm">
				<Row>
					<Col span="6">
                        <FormItem label="供应商" prop="supplierId" >
                            <supplier-select v-model="buyOrder.supplierId" ></supplier-select>
                        </FormItem>
					</Col>
					<!-- <Col span="5">
                        <FormItem label="供应商代表" prop="supplierContactId" >
                            <supplier-contact-select v-model="buyOrder.supplierContactId" :disabled="!buyOrder.supplierId" :supplierId="buyOrder.supplierId"></supplier-contact-select>
                        </FormItem>
					</Col> -->
					<Col span="6">
                        <FormItem label="采购员" prop="buyerId">
                            <buyer-select v-model="buyOrder.buyerId" ></buyer-select>
                        </FormItem>
					</Col>
					<!-- <Col span="6">
                        <FormItem label="自定单号" prop="refNo">
                            <Input v-model="buyOrder.refNo" />
                        </FormItem>
					</Col> -->
                    <Col span="6">
                        <FormItem label="仓库点" prop="warehouseId">
                            <warehouse-select v-model="buyOrder.warehouseId" ></warehouse-select>
                        </FormItem>
					</Col>
				</Row>
				<Row>
                    <Col span="5">
						<FormItem label="预到货日期" prop="eta">
							<DatePicker type="date" v-model="buyOrder.eta" />
						</FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="运输方式" prop="shipMethodId">
                            <option-select v-model="buyOrder.shipMethodId" optionType="SHIP_METHOD"></option-select>
                        </FormItem>
					</Col>
					<Col span="5">
                        <FormItem label="运输工具" prop="shipToolId">
                            <option-select v-model="buyOrder.shipToolId" optionType="SHIP_TOOL"></option-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="温控方式" prop="temperControlId">
                            <option-select v-model="buyOrder.temperControlId" optionType="TEMPER_CONTROL"></option-select>
                        </FormItem>
					</Col>
				</Row>
				<Row>
					<Col span="8">
						<FormItem label="选择商品">
                            <good-select :disabled="!buyOrder.warehouseId" ref="goodsSelect" :warehouseId="buyOrder.warehouseId"
                            @on-change="onSelectGoods"></good-select>
						</FormItem>
					</Col>
				</Row>

				<Table border highlight-row
					   class="margin-top-8"
					   :columns="orderColumns" :data="orderItems"
					   ref="buyOrderTable" style="width: 100%;" size="small"
					   no-data-text="在商品输入框选择后添加"
					   @on-row-dblclick="handleRowDbClick">
					<div slot="footer">
						<h3 class="padding-left-20" >
							<b>合计金额:</b> ￥{{ totalAmount }}
						</h3>
					</div>
				</Table>

				<div class="margin-top-10">
				    <Input type="textarea" v-model="buyOrder.comment" :rows="2" placeholder="暂无备注信息"/>
				</div>
				
			</Form>
		</Card>
		<Modal v-model="closeConfirm"
			   title="是否继续下单"
			   @on-ok="clearData"
		       @on-cancel="closeTab">
			<p>是否继续添加下一笔订单?</p>
		</Modal>
	</Row>

</template>

<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';
    import supplierSelect from '@/views/selector/supplier-select.vue';
    import supplierContactSelect from '@/views/selector/supplier-contact-select.vue';
    import buyerSelect from '@/views/selector/buyer-select.vue';
    import optionSelect from '@/views/selector/option-select.vue';
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import goodSelect from '@/views/selector/good-select.vue';
    import goodsSpecTags from '../goods/goods-spec-tabs.vue';

    export default {
        name: 'buy_order',
        components: {
            supplierSelect,
            supplierContactSelect,
            buyerSelect,
            optionSelect,
            goodSelect,
            warehouseSelect,
            goodsSpecTags
        },
        data () {
            return {
                saving: false,
            	totalAmount: 0,
            	edittingRow: {},
                closeConfirm: false,
                orderItems: [],
                buyOrder: {
                    supplierId: null,
                	eta: moment().add(1, 'd').format('YYYY-MM-DD'),
                	orderItemIds: []
                },
                orderColumns: [
                    {
                        type: 'index',
                        title: '',
                        width: 30
                    },
                    {
                        title: '货号',
                        key: 'id',
                        width: 50
                    },
                    {
                        title: '商品名称',
                        key: 'name',
                        width: 200,
                        sortable: true
                    },
                    {
                        title: '产地',
                        key: 'origin',
                        width: 100
                    },
                    {
                        key: 'goodsSpecs',
                        title: '规格',
                        width: 120,
                        render: (h, params) =>　{
                            return h(goodsSpecTags, {
                                props: {
                                    tags: params.row.goodsSpecs,
                                    color: 'blue'
                                }
                            });
                        }
                    },
                    {
                        title: '生产企业',
                        key: 'factoryName',
                        width: 150
                    },
                    {
                        title: '单位',
                        key: 'unitName',
                        align: 'center',
                        width: 80
                    },

                    {
                        title: '数量',
                        key: 'quantity',
                        align: 'center',
                        width: 100,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
								  	value: self.orderItems[params.index][params.column.key],
                                    number: true
                                },
                                on: {
                                    'on-change' (event) {
                                        var row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
                                    },
 									'on-blur' (event) {
 										var row = self.orderItems[params.index];
 										var price = row['price'];
 										var qty = event.target.value;
                                        if (!isNaN(qty) && !isNaN(price)) {
                                            row.amount = (qty * price).toFixed(2);
                                            self.$set(self.orderItems, params.index, row);
                                        }
 									},
                                    'on-enter' (event) {
                                        var index = params.index * 2;
                                        var inputList = self.$refs.buyOrderTable.$el.querySelectorAll('input');
                                        if (inputList && index + 2 <= inputList.length) {
                                        // move to next
                                            inputList[index + 1].focus();
                                        }
                                    }
                                }
                            });
                        }
                    },
                    {
                        title: '采购单价',
                        key: 'price',
                        align: 'center',
                        width: 100,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
								  	value: self.orderItems[params.index][params.column.key],
                                    number: true
                                },
                                on: {
                                    'on-change' (event) {
                                        var row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
                                    },
 									'on-blur' (event) {
 										var row = self.orderItems[params.index];
 										var qty = row['quantity'];
 										var price = event.target.value;
                                        if (!isNaN(qty) && !isNaN(price)) {
                                            row.amount = (qty * price).toFixed(2);
                                            self.$set(self.orderItems, params.index, row);
                                        }
 									},
                                    'on-enter' (event) {
                                        var index = params.index * 2 + 1;
                                        var inputList = self.$refs.buyOrderTable.$el.querySelectorAll('input');
                                        if (inputList && index + 2 <= inputList.length) {
                                        // move to next line
                                            inputList[index + 1].focus();
                                        }
                                        if (index + 2 >= inputList.length) {
                                            var row = self.orderItems[params.index];
                                            var qty = row['quantity'];
                                            var price = event.target.value;
                                            if (!isNaN(qty) && !isNaN(price)) {
                                                row.amount = (qty * price).toFixed(2);
                                                self.$set(self.orderItems, params.index, row);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    },
                    {
                        title: '金额',
                        key: 'amount',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '整件单位',
                        key: 'packUnitName',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '大件装量',
                        key: 'bigPack',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '库存',
                        key: 'balance',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '在单数',
                        key: 'buyOrderCount',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '在单总数',
                        key: 'ongoingCount',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '最近采购价',
                        key: 'lastPrice',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '存储条件',
                        key: 'storageConditionName',
                        width: 120
                    },
                    {
                        title: '药基',
                        key: 'baseMedName',
                        width: 120
                    },
                    {
                        title: '批准文号',
                        key: 'permitNo',
                        width: 200
                    }
        	],
                ruleValidate: {
                    supplierId: [
                        { required: true, type: 'number', message: '请选择供应商' }
                    ],
                    buyerId: [
                        { required: true, type: 'number', message: '请选择采购员' }
                    ],
                    warehouseId: [
                        { required: true, type: 'number', message: '请选择仓库点' }
                    ],
                    orderItems: [
                        { required: true, type: 'array', array: {min: 1}, message: '请添加商品' }
                    ]
                }
            };
        },
        mounted () {
        },
        activated () {
                this.clearData();
        },
        watch: {
        	orderItems: function () {
        		this.totalAmount = this.orderItems.reduce(function (total, item) { return total + parseFloat(item.amount); }, 0);
        	}
        },
        methods: {
            moment: function () {
                return moment();
            },
            handleRowDbClick (row) {
            	this.$Modal.confirm({
                    title: '确认删除商品？',
                    content: '<p>确认删除商品 ' + row.name + '?</p>',
                    onOk: () => {
                        for (var i = 0; i < this.orderItems.length; i++) {
                            if (row.id === this.orderItems[i].id) {
                                this.orderItems.splice(i, 1);
                                this.buyOrder.orderItemIds.splice(i, 1);
                            }
                        }
                    },
                    onCancel: () => {
    
                    }
                });
            },
            onSelectGoods (goodsId, goods) {
                if (goodsId && goods) {
                    var index = this.buyOrder.orderItemIds.indexOf(goods.id);
                    if (index < 0) {
                        this.setGoodsBalance(goods);
                    } else {
                        this.$Message.warning('该商品已经添加');
                    }
                    this.$refs.goodsSelect.clearSingleSelect();
                }
            },

            setGoodsBalance(goods) {
                if(!goods.id) {
                    return;
                }
                //根据当前仓库和商品ID获取最近价格和库存信息
                let reqDate = {
                    warehouseId: this.buyOrder.warehouseId,
                    goodsIdList: JSON.stringify([goods.id])
                };
                util.ajax.get("/repertory/in/current/balance", {params: reqDate})
                    .then((response) => {
                        let data = response.data;
                        let record ='';
                        if(data) {
                            record = data[goods.id];
                        }
                        goods['lastPrice'] = record ? record.lastPrice : '';
                        goods['buyOrderCount'] = record ? record.buyOrderCount : '';
                        goods['balance'] = record ? record.balance : '';
                        goods['ongoingCount'] = record ? record.ongoingCount : '';
                        goods['amount'] = 0;
                        // 填入参考买入价
                        goods['price'] = goods['inPrice'];
                        console.log(goods);
                        this.orderItems.push(goods);
                        this.buyOrder.orderItemIds.push(goods.id);
                        var self = this;
                        setTimeout(function () {
                            self.$refs.buyOrderTable.$el.querySelector('.ivu-table-body tr:last-child input').focus();
                        }, 400);
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                        goods['amount'] = 0;
                        this.orderItems.push(goods);
                        this.buyOrder.orderItemIds.push(goods.id);
                        var self = this;
                        setTimeout(function () {
                            self.$refs.buyOrderTable.$el.querySelector('.ivu-table-body tr:last-child input').focus();
                        }, 400);
                    });
            },

            doSave () {
                this.$Modal.confirm({
                    title: '提交确认',
                    content: '请确认采购数据录入完全正确, 提交后不可修改.',
                    onCancel:() => {},
                    onOk:() => {
                        var self = this;
                        this.saving = true;
                        util.ajax.post('/buy/add', this.buyOrder)
                            .then(function (response) {
                                if (response.status === 200 && response.data) {
                                    self.buyOrder.id = response.data.orderId;
                                    self.buyOrder.status = response.data.status;
                                    self.$Message.info('采购入库订单保存成功');
                                    self.closeConfirm = true;
                                }
                                self.saving = false;
                            })
                            .catch(function (error) {
                                self.saving = false;
                                self.$Message.error('保存采购订单错误');
                                util.erorProcessor(self, error);
                            });
                    }
                });
            },
            clearData () {
                this.buyOrder = {
                    supplierId: null,
                    eta: moment().add(1, 'd').format('YYYY-MM-DD'),
                    orderItemIds: []
                };
                this.orderItems = [];
            },
            closeTab () {
                this.clearData();
                let pageName = util.closeCurrentTab(this);
                this.$router.push({
                    name: pageName
                });
            },
            saveBuyOrder () {
                this.buyOrder.orderItems = this.orderItems;
                this.$refs.buyOrderForm.validate((valid) => {
                    if (!valid) {
                        this.$Message.error('请检查输入!');
                    } else {
                        this.doSave();
                    }
                });
            }
        }
    };
</script>

<style>
.option-goods-spec {
  float: right;
  color: #999;
}
.ivu-form-item {
    margin-bottom: 15px;
}

.ivu-table-cell {
    padding-left: 5px;
    padding-right: 5px;
}
th .ivu-table-cell {
	white-space: nowrap;
}
.ivu-table-body, .ivu-table-tip { min-height: 300px; }
@media (max-height: 800px) {
    .ivu-table-body, .ivu-table-tip { min-height: 120px; }
}
</style>

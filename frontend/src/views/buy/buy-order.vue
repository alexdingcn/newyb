<style lang="less">
    @import '../../styles/common.less';
	@import './buy-order.less';
</style>

<template>
	<Row>
		<Card>
			<p slot="title" >
				<Icon type="document"></Icon> 采购入库制单
			</p>
			<div slot="extra">

				<ButtonGroup class="padding-left-20">
					<Button type="primary" icon="android-add-circle" @click="saveBuyOrder" :loading="saving">保存</Button>
				</ButtonGroup>
			</div>

			<Form :label-width="85" :rules="ruleValidate" :model="buyOrder" ref="buyOrderForm">
				<Row>
					<Col span="6">
					<FormItem label="供应商" prop="supplierId" >
						<Select
								v-model="buyOrder.supplierId"
								filterable
								clearable
								remote
								size="small"
								@on-change="onSelectSupplier"
								placeholder="供应商名称/拼音"
								:remote-method="querySupplier"
								:loading="supplierLoading">
							<Option v-for="option in supplierOptions" :value="option.id" :label="option.name" :key="option.id">{{option.name}}</Option>
						</Select>
					</FormItem>
					</Col>
					<Col span="5">
					<FormItem label="供应商代表" prop="supplierContactId" >
						<Select ref="supplierContactSelect"
								v-model="buyOrder.supplierContactId"
								clearable
								size="small"
								placeholder="供应商代表"
								:disabled="supplierContactOptions.length === 0"
								:loading="supplierContactLoading">
							<Option v-for="option in supplierContactOptions" :value="option.id" :label="option.name" :key="option.id">{{option.name}}</Option>
						</Select>
					</FormItem>
					</Col>
					<Col span="6">
					<FormItem label="采购员" prop="buyerId">
						<Select
								v-model="buyOrder.buyerId"
								clearable
								size="small"
								placeholder="采购员">
							<Option v-for="option in buyerOptions" :value="option.userId" :label="option.nickname" :key="option.userId">
								{{option.nickname}}
								{{option.realname||''}}
							</Option>
						</Select>
					</FormItem>
					</Col>
					<Col span="6">
					<FormItem label="自定单号" prop="refNo">
						<Input v-model="buyOrder.refNo" size="small"/>
					</FormItem>
					</Col>
				</Row>
				<Row>
					<Col span="6">
					<FormItem label="运输方式" prop="shipMethodId">
						<Select v-model="buyOrder.shipMethodId" size="small" placeholder="运输方式" clearable>
							<Option v-for="option in shipMethodOptions" :value="option.id" :label="option.value" :key="option.id">
								{{option.value}}
							</Option>
						</Select>
					</FormItem>
					</Col>
					<Col span="5">
					<FormItem label="运输工具" prop="shipToolId">
						<Select v-model="buyOrder.shipToolId" size="small" placeholder="运输工具" clearable>
							<Option v-for="option in shipToolOptions" :value="option.id" :label="option.value" :key="option.id">
								{{option.value}}
							</Option>
						</Select>
					</FormItem>
					</Col>
					<Col span="6">
					<FormItem label="温控方式" prop="temperControlId">
						<Select v-model="buyOrder.temperControlId" size="small" placeholder="温控方式" clearable>
							<Option v-for="option in temperControlOptions" :value="option.id" :label="option.value" :key="option.id">
								{{option.value}}
							</Option>
						</Select>
					</FormItem>
					</Col>
					<Col span="6">
					<FormItem label="仓库点" prop="warehouseId">
						<Select v-model="buyOrder.warehouseId" size="small" placeholder="仓库点" clearable>
							<Option v-for="option in warehouseOptions" :value="option.id" :label="option.name" :key="option.id">
								{{option.name}}
							</Option>
						</Select>
					</FormItem>
					</Col>
				</Row>
				<Row>
					<Col span="6">
						<FormItem label="选择商品">
							<Select
									ref="goodsSelect"
									filterable
									clearable
									remote
									placeholder="商品名称/拼音"
									size="small"
									@on-change="onSelectGoods"
									:remote-method="queryGoods"
									:loading="goodsLoading">
								<Option v-for="option in goodsOptions" :value="option.id" :label="option.name" :key="option.id">
									<span class="option-goods-name">{{ option.name }}</span>
									<span class="option-goods-spec">{{ option.spec }} | {{option.factory}}</span>
								</Option>
							</Select>
						</FormItem>
					</Col>
					<Col span="5">
						<FormItem label="预到货日期" prop="eta">
							<DatePicker type="date" v-model="buyOrder.eta" size="small"/>
						</FormItem>
					</Col>
					<Col span="12">
						<FormItem label="备注" prop="comment">
							<Input v-model="buyOrder.comment" size="small"/>
						</FormItem>
					</Col>
					<!--
                    <Col span="6">
                        <Button type="primary" icon="ios-list-outline" class="margin-left-5"></Button>
                    </Col>
                    -->
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

    export default {
        name: 'buy_order',
        data () {
            return {
				saving: false,
            	supplierLoading: false,
            	supplierOptions: [],
            	goodsLoading: false,
            	goodsOptions: [],
				buyerOptions: [],
				supplierContactLoading: false,
				supplierContactOptions: [],
				shipMethodOptions: [],
				shipToolOptions: [],
				temperControlOptions: [],
				warehouseOptions: [],
            	totalAmount: 0,
            	edittingRow: {},
				closeConfirm: false,
            	fapiaoTypes: [
            		{ value: 'PP', label:'普通发票'},
            		{ value: 'ZZS', label: '增值税发票'}
            	],
                searchFactoryVal: '',
                orderItems: [],
                buyOrder: {
					supplierId: null,
                	eta: moment().add(1,'d').format('YYYY-MM-DD'),
                	orderItemIds: []
                },
                orderColumns: [
                    {
                        type: 'index',
                        title: '',
                        align: 'center',
                        width: 30
                    },
                    {
						title: '货号',
						align: 'center',
						key: 'id',
						width: 50
					},
                    {
                        title: '商品名称',
                        key: 'name',
                        align: 'center',
                        width: 150,
                        sortable: true,
                        render: (h, params) => {
							return h('Button', {
								props: {
									type: 'text',
									size: 'small'
								},
								on: {
									click: () => {
										let argu = { goods_id: params.row.id };
										this.$router.push({
											name: 'goods-info',
											params: argu
										});
									}
								}
							}, params.row.name);
						}
					},
					{
						title: '产地',
						key: 'origin',
						align: 'center',
						width: 60
					},
					{
						title: '剂型',
						key: 'jx',
						align: 'center',
						width: 60
					},
					{
						title: '规格',
						key: 'spec',
						align: 'center',
						width: 80
					},
					{
						title: '生产企业',
						key: 'factory',
						align: 'center',
						width: 120
					},
					{
						title: '单位',
						key: 'unitName',
						align: 'center',
						width: 50
					},

					{
                        title: '数量',
                        key: 'quantity',
                        align: 'center',
                        width: 80,
                        render: (h, params) => {
                        	var self = this;
							return h('Input', {
								props: {
								  	value: self.orderItems[params.index][params.column.key]
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
										if (inputList && index+2 <= inputList.length) {
											// move to next
											inputList[index + 1].focus();
										}
									}
								}
							});
						}
					},
					{
                        title: '单价',
                        key: 'price',
                        align: 'center',
                        width: 80,
                        render: (h, params) => {
                        	var self = this;
							return h('Input', {
								props: {
								  	value: self.orderItems[params.index][params.column.key]
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
										if (inputList && index+2 <= inputList.length) {
											// move to next line
											inputList[index + 1].focus();
										}
										if (index+2 >= inputList.length) {
											var row = self.orderItems[params.index];
											var qty = row['quantity'];
											var price = event.target.value;
											if (!isNaN(qty) && !isNaN(price)) {
												row.amount = (qty * price).toFixed(2);
												self.$set(self.orderItems, params.index, row);
											}
										}
									},
								}
							});
						}
					},
					{
                        title: '金额',
                        key: 'amount',
                        align: 'center',
                        width: 80
					},
					{
						title: '整件单位',
						key: 'packUnitName',
						align: 'center',
						width: 60
					},
					{
						title: '大件装量',
						key: 'bigPack',
						align: 'center',
						width: 60
					},
					{
                        title: '库存',
                        key: 'balance',
                        align: 'center',
                        width: 100
					},
					{
                        title: '在单数',
                        key: 'ongoing',
                        align: 'center',
                        width: 100,
					},
					{
                        title: '最近采购价',
                        key: 'last',
                        align: 'center',
                        width: 100,
					},
					{
                        title: '批号',
                        key: 'batch',
                        align: 'center',
                        width: 100,
					},
					{
                        title: '有效期',
                        key: 'exp',
                        align: 'center',
                        width: 100,
					},
        	],
			ruleValidate: {
				supplierId: [
					{ required: true, type: 'number', message: '请选择供应商', trigger: 'blur' }
				],
				supplierContactId: [
					{ required: true, type: 'number',message: '请选择供应商代表', trigger: 'blur' },
				],
				buyerId: [
					{ required: true, type: 'number',message: '请选择采购员', trigger: 'blur' }
				],
				warehouseId: [
					{ required: true, type: 'number',message: '请选择仓库点', trigger: 'blur' }
				],
				orderItems: [
					{ required: true, type: 'array', range: {min:1}, message: '请添加商品', trigger: 'blur' }
				],
			}
        };
        },
        mounted() {
			this.queryBuyers();
			this.queryCommonOptions();
			this.queryWarehouseList();
        },
		activated() {
			this.clearData();
		},
        watch: {
        	orderItems: function () {
        		this.totalAmount = this.orderItems.reduce(function(total, item) { return total + parseFloat(item.amount); }, 0);
        	}
        },
        methods: {
			moment: function () {
				return moment();
			},
			queryWarehouseList() {
				var self = this;
				util.ajax.get('/warehouse/list')
						.then(function (response) {
							self.warehouseOptions = response.data;
							if (self.warehouseOptions.length === 1) {
								self.buyOrder.warehouseId = self.warehouseOptions[0].id;
							}
						})
						.catch(function (error) {
							console.log(error);
						});
			},
			querySupplier (query) {
				var self = this;
                if (query !== '') {
                    this.supplierLoading = true;
                    util.ajax.post('/supplier/search', {search: query})
                        .then(function (response) {
                        	self.supplierLoading = false;
                            self.supplierOptions = response.data;
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                } else {
                    this.supplierOptions = [];
                }
            },
			queryBuyers() {
				var self = this;
				util.ajax.get('/userrole/list', {params: {roleQuery: 'ROLE_BUYER;ROLE_BUYER_SPECIAL'} })
						.then(function (response) {
							if (response.status === 200 && response.data) {
								self.buyerOptions = response.data;
							}
						})
						.catch(function (error) {
							console.log(error);
						})
			},
			onSelectSupplier(item) {
				this.$refs.supplierContactSelect.clearSingleSelect();
				this.querySupplierContact(item);
			},
			querySupplierContact(supplierId) {
				var self = this;
				if (supplierId) {
					this.supplierContactLoading = true;
					util.ajax.get('/supplier/contact/list', {params: {supplierId: supplierId}})
						.then(function (response) {
							self.supplierContactLoading = false;
							self.supplierContactOptions = response.data;
                            if (self.supplierContactOptions.length === 1) {
                                self.buyOrder.supplierContactId = self.supplierContactOptions[0].id;
                            }
						})
						.catch(function (error) {
							console.log(error);
						});
				} else {
					this.supplierContactOptions = [];
				}
			},
            queryGoods (query) {
				var self = this;
                if (query !== '') {
                    this.goodsLoading = true;
                    util.ajax.get('/goods/list', 
                    	{ params: 
                    		{search: query, page: 1, size: 10}
                    	})
                        .then(function (response) {
                        	self.goodsLoading = false;
                            self.goodsOptions = response.data.data;
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                } else {
                    this.goodsOptions = [];
                }
            },
            handleRowDbClick(row) {
            	this.$Modal.confirm({
                    title: '确认删除商品？',
                    content: '<p>确认删除商品 ' + row.name + '?</p>',
                    onOk: () => {
                        for (var i=0; i< this.orderItems.length; i++) {
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
            onSelectGoods(goodsId) {
            	var goods = this.goodsOptions.filter( o => o.id === goodsId );
            	if (goods && goods.length == 1) {
            		var index = this.buyOrder.orderItemIds.indexOf(goodsId);
            		if (index < 0) {
            			var obj = goods[0];
            			obj['amount'] = 0;
            			this.orderItems.push(goods[0]);
            			this.buyOrder.orderItemIds.push(goodsId);
            			var self = this;
            			setTimeout(function() {
            				self.$refs.buyOrderTable.$el.querySelector(".ivu-table-body tr:last-child input").focus();
            			}, 400);
            		} else {
            			this.$Message.warning("该商品已经添加");
            		}
				}
				this.$refs.goodsSelect.clearSingleSelect();
            },
			queryCommonOptions() {
				var self = this;
				util.ajax.post('/options/list', ['SHIP_METHOD', 'SHIP_TOOL', 'TEMPER_CONTROL'])
						.then(function (response) {
							if (response.status === 200) {
								self.shipMethodOptions = response.data['SHIP_METHOD'];
								self.shipToolOptions = response.data['SHIP_TOOL'];
								self.temperControlOptions = response.data['TEMPER_CONTROL'];
								if (self.shipMethodOptions.length == 1) {
									self.buyOrder.shipMethodId = self.shipMethodOptions[0].id;
								}
								if (self.shipToolOptions.length == 1) {
									self.buyOrder.shipToolId = self.shipToolOptions[0].id;
								}
								if (self.temperControlOptions.length == 1) {
									self.buyOrder.temperControlId = self.temperControlOptions[0].id;
								}
							}
						})
						.catch(function (error) {
							console.log(error);
						});
			},
			doSave() {
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
							console.log(error);
							self.saving = false;
							self.$Message.error('保存采购订单错误');
//							self.$Message.error('保存采购订单错误 ' + error.data.message);
						});
			},
			clearData() {
				this.buyOrder = {
						supplierId: null,
						eta: moment().add(1,'d').format('YYYY-MM-DD'),
						orderItemIds: []
				}
			},
			closeTab() {
				this.clearData();
				let pageName = util.closeCurrentTab(this);
				this.$router.push({
					name: pageName,
				});
			},
			saveBuyOrder() {
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

</style>

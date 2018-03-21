<style lang="less">
    @import '../../styles/common.less';
	@import './buy-order.less';
</style>

<template>
	<Row>
		<Card>
			<p slot="title" >
			</p>
			<div slot="extra" style="width:600px">
				<Row>
					<Col span="8">
						<DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="订单日期" style="width:180px"></DatePicker>
					</Col>
					<Col span="6" class="padding-2">
						<Select v-model="query.supplierId"
								filterable
								clearable
								remote
								@on-change="onSelectSupplier"
								placeholder="供应商"
								:remote-method="querySupplier"
								:loading="supplierLoading">
							<Option v-for="option in supplierOptions" :value="option.id" :label="option.name" :key="option.id">{{option.name}}</Option>
						</Select>
					</Col>
					<Col span="5" class="padding-2">
						<Select v-model="query.status" placeholder="状态">
							<Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
						</Select>
                    </Col>
                    <Col span="5">
                        <ButtonGroup>
                            <Button type="primary" icon="ios-search" @click="queryOrderList"></Button>
                            <Button type="success" icon="checkmark-round">审核</Button>
                        </ButtonGroup>
					</Col>
				</Row>
			</div>
			<Table border highlight-row
				   :columns="orderListColumns" :data="orderList"
				   ref="buyOrderListTable" size="small"
				   no-data-text="使用右上方输入搜索条件">
			</Table>
		</Card>

		<Card class="margin-top-8">
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
		</Card>

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
				statusOptions: [{key: 'ALL', name:'所有'},{key: 'CHECKING', name:'未审批'},{key: 'CHECKED', name:'已审批'}],
                query: {
                    status: 'CHECKING',
                },
                dateRange: [],
                orderList: [],
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
            	fapiaoTypes: [
            		{ value: 'PP', label:'普通发票'},
            		{ value: 'ZZS', label: '增值税发票'}
            	],
                searchFactoryVal: '',
                orderItems: [],
                buyOrder: {
					supplierId: null,
                	orderItemIds: []
                },
                orderListColumns: [
                    {
                        key: 'id',
                        title: '#',
                        align: 'center',
                        width: 30
                    },
                    {
                        title: '订单日期',
                        align: 'center',
                        key: 'createdTime',
                        width: 80,
                        render:(h, params) => {
                            console.log(params);
                            return moment(params.row.createdTime).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '仓库点',
                        align: 'center',
                        key: 'warehouse',
                        width: 80
                    },
                    {
                        title: '供应商',
                        align: 'center',
                        key: 'supplier',
                        width: 100
                    },
                    {
                        title: '供应商代表',
                        align: 'center',
                        key: 'supplierContact',
                        width: 80
                    },
                    {
                        title: '制单人',
                        align: 'center',
                        key: 'createdBy',
                        width: 80
                    },
                    {
                        title: '审核结论',
                        align: 'center',
                        key: 'checkResult',
                        width: 100
                    },
                    {
                        title: '审核人',
                        align: 'center',
                        key: 'checkedBy',
                        width: 80
                    },
                    {
                        title: '审核日期',
                        align: 'center',
                        key: 'checkTime',
                        width: 80,
                        render:(h, params) => {
                            return moment(params.row.checkTime).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '订单号',
                        align: 'center',
                        key: 'orderNumber',
                        width: 100
                    },
                    {
                        title: '预计到货日',
                        align: 'center',
                        key: 'eta',
                        width: 80,
                        render:(h, params) => {
                            return moment(params.row.eta).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '备注',
                        align: 'center',
                        key: 'comment',
                        width: 100
                    },
                    {
                        title: '温控方式',
                        align: 'center',
                        key: 'temperControl',
                        width: 80
                    },
                    {
                        title: '运输工具',
                        align: 'center',
                        key: 'shipTools',
                        width: 80
                    },
                    {
                        title: '运输方式',
                        align: 'center',
                        key: 'shipMethod',
                        width: 80
                    },
                ],

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
					{ required: true, type: 'array', array: {min:1}, message: '请添加商品', trigger: 'blur' }
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
		},
        watch: {
        	orderItems: function () {
        		this.totalAmount = this.orderItems.reduce(function(total, item) { return total + parseFloat(item.amount); }, 0);
        	}
        },
        methods: {
            queryOrderList() {
                var self = this;
                if (this.dateRange && this.dateRange.length == 2) {
                    this.query['startDate'] = this.dateRange[0];
                    this.query['endDate'] = this.dateRange[1];
                }
                util.ajax.post('/buy/list', this.query)
                        .then(function (response) {
                            self.orderList = response.data;
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
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


        }
    };
</script>

<style>

</style>

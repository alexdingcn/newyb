<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
	<Row>
		<Card>
			<div slot="title" style="width:50%">
                <Steps :current="0">
                    <Step title="采购收货" content="检查外包装，确认数量"></Step>
                    <Step title="入库质量验收" content="抽样检查商品质量"></Step>
                    <Step title="入库单审核" content="审核后入库"></Step>
                </Steps>
			</div>
			<div slot="extra">
				<ButtonGroup class="padding-left-20">
                    <Button type="primary" icon="android-add-circle" @click="buyCheckOrderGetBtnClick" >载入采购单</Button>
					<Button type="success" icon="checkmark-round" @click="saveOrderBtnClick" :loading="saving">保存</Button>
                    <Button icon="bookmark" :loading="saving" @click="tempSaveOrderBtnClick"> 暂挂 </Button>
                    <Button type="info" icon="filing" @click="getTempOrderBtnClick" > 暂挂提取 </Button>
                    <Button type="ghost" icon="ios-printer" :loading="saving">打印</Button>
				</ButtonGroup>
			</div>

			<Form :label-width="85" :rules="ruleValidate" :model="order" ref="orderForm">
				<Row>
					<Col span="6">
                        <FormItem label="供应商" prop="supplierId" >
                            <Input v-if="editView" v-model="order.supplierName" size="small" :disabled="true"></Input>
                            <supplier-select v-if="!editView" v-model="order.supplierId" size="small" ></supplier-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="供应商代表" prop="supplierContactId" >
                            <supplier-contact-select v-model="order.supplierContactId" 
                                :supplierId="order.supplierId" :disabled="!order.supplierId" 
                                size="small"></supplier-contact-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="采购员" prop="buyerId">
                            <buyer-select v-model="order.buyerId" size="small"></buyer-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="自定单号" prop="refNo">
                            <Input v-model="order.refNo" size="small"/>
                        </FormItem>
					</Col>
				</Row>
                <Row>
					<Col span="6">
                        <FormItem label="收货日期" >
                            <DatePicker type="date" v-model="order.receiveDate" size="small"/>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="付款日期" >
                            <DatePicker type="date" v-model="order.payDate" size="small"/>
                        </FormItem>
					</Col>
                    <Col span="12">
                        <FormItem label="摘要" >
                            <Input v-model="order.keyWord" size="small"/>
                        </FormItem>
					</Col>
				</Row>
                <Row>
                    <Col span="6">
                        <FormItem label="随货同行票号" >
                            <Input v-model="order.goodBillNo" size="small"/>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="温控方式" >
                            <option-select optionType="TEMPER_CONTROL" v-model="order.tempControlMethod" size="small"></option-select>
                        </FormItem>
					</Col>
                    <Col span="6">
                        <FormItem label="到货温度(℃)" >
                            <Input :number="true" v-model="order.receiveTemp" size="small"/>
                        </FormItem>
					</Col>
                    <Col span="6">
                        <FormItem label="温控状态" >
                            <option-select optionType="TEMPER_STATUS" v-model="order.tempControlStatus" size="small"></option-select>
                        </FormItem>
					</Col>
				</Row>
                <Row>
					<Col span="6">
                        <FormItem label="启运时间" >
                            <DatePicker type="datetime" v-model="order.shipStartDate" format="yyyy-MM-dd HH:mm" size="small"/>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="到货时间" >
                            <DatePicker type="datetime" v-model="order.shipEndDate" format="yyyy-MM-dd HH:mm" size="small"/>
                        </FormItem>
					</Col>
                    <Col span="6">
                        <FormItem label="运输方式" prop="shipMethod">
                            <option-select optionType="SHIP_METHOD" v-model="order.shipMethod" size="small"></option-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="运输工具" prop="shipTool">
                            <option-select optionType="SHIP_TOOL" v-model="order.shipTool" size="small"></option-select>
                        </FormItem>
					</Col>
				</Row>
                <Row>
                    <Col span="6">
                        <FormItem label="运输车牌号" >
                            <Input v-model="order.shipCarNo" size="small" />
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="驾驶员" >
                            <Input v-model="order.shipDriverName" size="small" />
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="承运单位" >
                            <ship-company-select v-model="order.shipCompanyId" size="small"></ship-company-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="委托运输单号" >
                            <Input v-model="order.shipEntrustNo" size="small" />
                        </FormItem>
					</Col>
				</Row>
                <Row>
                    <Col span="12">
                        <FormItem label="发运地址" >
                            <Input v-model="order.shipStartAddress" size="small" />
                        </FormItem>
					</Col>
					<Col span="12">
                        <FormItem label="直调来货单位" >
                            <Input v-model="order.comeFrom" size="small" />
                        </FormItem>
					</Col>
				</Row>
                <Row>
                    <Col span="6">
                        <FormItem label="仓库点" prop="warehouseId">
                            <warehouse-select v-model="order.warehouseId" size="small"></warehouse-select>
                        </FormItem>
					</Col>
                    <Col span="6">
                        <FormItem label="采购属性" >
                            <option-select optionType="BUY_TYPE" v-model="order.buyType" size="small"></option-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="发票种类" >
                            <option-select optionType="BILL_TYPE" v-model="order.billType" size="small"></option-select>
                        </FormItem>
					</Col>
                    <Col span="6">
                        <FormItem label="账期" >
                            <Input number v-model="order.term" size="small" />
                        </FormItem>
					</Col>
				</Row>
                <Row>
                    <Col span="10">
                        <FormItem label="商品" >
                            <good-select ref="goodSelector" size="small" @on-change="onSelectGoods"></good-select>
                        </FormItem>
					</Col>
				</Row>

				<Table border highlight-row
					   class="margin-top-8"
					   :columns="orderColumns" :data="orderItems"
					   ref="detailTable" style="width: 100%;" size="small"
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

        <Modal v-model="receiveTemp" title="采购入库单暂挂提取" :mask-closable="false" width="70" footerHide>
            <in-temp :open="receiveTemp" @on-choosed="receiveTempChoose"></in-temp>
        </Modal>

        <Modal v-model="buyCheckOrder" title="采购单提取" :mask-closable="false" width="70" footerHide className="extract-buy-order">
            <buy-order-list @on-choosed="buyOrderChoose" :chooseModal="true" ></buy-order-list>
        </Modal>

        <warehouse-location-modal :openModal="locationModal" :warehouseId="order.warehouseId" @on-ok="chooseLocation" @on-close="locationModalClose"></warehouse-location-modal>

	</Row>

</template>

<script>
    import moment from 'moment';
    import util from '@/libs/util.js';
    import supplierSelect from "@/views/selector/supplier-select.vue";
    import supplierContactSelect from "@/views/selector/supplier-contact-select.vue";
    import buyerSelect from "@/views/selector/buyer-select.vue";
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import shipCompanySelect from "@/views/selector/ship-company-select.vue";
    import goodSelect from "@/views/selector/good-select.vue";
    import inTemp from "./in-temp.vue";
    import buyOrderList from "@/views/buy/buy-order-list.vue";
    import warehouseLocationModal from "@/views/selector/warehouse-location-modal.vue";
    import optionSelect from "@/views/selector/option-select.vue";
    import goodsSpecTags from '../goods/goods-spec-tabs.vue';

    export default {
        name: 'in-make',
        components: {
            supplierSelect,
            supplierContactSelect,
            buyerSelect,
            shipCompanySelect,
            warehouseSelect,
            goodSelect,
            inTemp,
            buyOrderList,
            warehouseLocationModal,
            optionSelect,
            goodsSpecTags
        },
        data () {
            const addWarehouseLocation = (h, location, rowData, index) => {
                let label = location ? location : '';
                return h('div', [
                    h('span', label),
                    h('Button', {
                        props: {
                            type: 'text',
                            size: 'small',
                            icon: 'edit'
                        },
                        on: {
                            'click': () => {
                                this.openChooseLocation(rowData, index);
                            }
                        }
                    })
                ]);
            };

            return {
                saving: false,
            	edittingRow: {},
                closeConfirm: false,
                orderItems: [],
                order: {
                    supplierId: null,
                    receiveDate: moment().format('YYYY-MM-DD'),
                	orderItemIds: []
                },
                currEditLocationRow: {},
                currDditLocationIndex: '',
                locationModal: false,
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
                        key: 'goodsId',
                        width: 100
                    },
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        align: 'center',
                        width: 150,
                        sortable: true,
                    },
                    {
                        title: '产地',
                        key: 'origin',
                        align: 'center',
                        width: 60
                    },
                    {
                        title: '规格',
                        key: 'goodsSpecs',
                        width: 120,
                        render: (h, params) =>　{
                            return h(goodsSpecTags, {
                                props: {
                                    tags: params.row.goods.goodsSpecs,
                                    color: 'blue'
                                }
                            });
                        }
                    },
                    {
                        title: '生产企业',
                        key: 'factoryName',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return h('span', params.row.goods.factoryName);
                        }
                    },
                    {
                        title: '单位',
                        key: 'unitName',
                        align: 'center',
                        width: 50
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
                        title: '采购数',
                        key: 'buyOrderQuality',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '本次收货数量',
                        key: 'receiveQuality',
                        align: 'center',
                        width: 130,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
                                    number: true,
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
                                        self.resetAmount(params.index);
 									}
                                }
                            });
                        }
                    },
                    {
                        title: '大件数量',
                        key: 'bigQuality',
                        align: 'center',
                        width: 130,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
                                    number: true,
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
 									}
                                }
                            });
                        }
                    },
                    {
                        title: '赠送数量',
                        key: 'free',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
                                    number: true,
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
                                        self.resetAmount(params.index);
 									}
                                }
                            });
                        }
                    },
                    {
                        title: '最近采购价',
                        key: 'lastPrice',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '单价',
                        key: 'price',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
                                    number: true,
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
                                        self.resetAmount(params.index);
 									}
                                }
                            });
                        }
                    },
                    {
                        title: '金额',
                        key: 'amount',
                        align: 'center',
                        width: 130,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
                                    number: true,
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
 									}
                                }
                            });
                        }
                    },
                    {
                        title: '生产日期',
                        key: 'productDate',
                        align: 'center',
                        width: 150,
                        render: (h, params) => {
                            let self = this;
                            return h('DatePicker', {
                                props: {
                                    type: 'date',
                                    placement: 'top',
                                    value: params.row.productDate
                                },
                                on: {
                                    'on-change': (date) =>{
                                        let row = self.orderItems[params.index];
                                        row[params.column.key] = date; 
                                    }
                                }
                            });
                        }
                    },
                    {
                        title: '有效期至',
                        key: 'expDate',
                        align: 'center',
                        width: 150,
                        render: (h, params) => {
                            let self = this;
                            return h('DatePicker', {
                                props: {
                                    type: 'date',
                                    placement: 'top',
                                    value: params.row.expDate
                                },
                                on: {
                                    'on-change': (date) =>{
                                        let row = self.orderItems[params.index];
                                        row[params.column.key] = date; 
                                    }
                                }
                            });
                        }
                    },
                    {
                        title: '批次号',
                        key: 'batchCode',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
 									}
                                }
                            });
                        }
                    },
                    {
                        title: '货位号',
                        key: 'warehouseLocation',
                        align: 'center',
                        width: 200,
                        render: (h, params) => {
                            return addWarehouseLocation(h, params.row.warehouseLocation, params.row, params.index);
                        }
                    },
                    {
                        title: '订单数',
                        key: 'buyOrderCount',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '当前库存',
                        key: 'balance',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '在单数',
                        key: 'ongoingCount',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '拒收数量',
                        key: 'rejectQuality',
                        align: 'center',
                        width: 100,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
                                    number: true,
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
 									}
                                }
                            });
                        }
                    },
                    {
                        title: '拒收原因',
                        key: 'rejectComment',
                        align: 'center',
                        width: 200,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
 									}
                                }
                            });
                        }
                    },
                    {
                        title: '税率',
                        key: 'taxRate',
                        align: 'center',
                        width: 100,
                        render: (h, params) => {
                        	var self = this;
                            return h('Input', {
                                props: {
                                    number: true,
								  	value: self.orderItems[params.index][params.column.key]
                                },
                                on: {
 									'on-blur' (event) {
 										let row = self.orderItems[params.index];
                                        row[params.column.key] = event.target.value;
 									}
                                }
                            });
                        }
                    }
        	    ],
                ruleValidate: {
                    supplierId: [
                        { required: true, type: 'number', message: '请选择供应商', trigger: 'blur' }
                    ],
                    supplierContactId: [
                        { required: true, type: 'number', message: '请选择供应商代表', trigger: 'blur' }
                    ],
                    buyerId: [
                        { required: true, type: 'number', message: '请选择采购员', trigger: 'blur' }
                    ],
                    warehouseId: [
                        { required: true, type: 'number', message: '请选择仓库点', trigger: 'blur' }
                    ],
                    details: [
                        { required: true, type: 'array', array: {min: 1}, message: '请添加商品', trigger: 'blur' }
                    ]
                },
                receiveTemp: false,
                editView: false,
                buyCheckOrder: false
            };
        },
        mounted () {
        },
        activated () {
                this.clearData();
        },
        computed: {
            totalAmount() {
                return this.orderItems.reduce(function (total, item) { return total + parseFloat(item.amount); }, 0);
            }
        },
        watch: {
        	// orderItems: function () {
        	// 	this.totalAmount = this.orderItems.reduce(function (total, item) { return total + parseFloat(item.amount); }, 0);
        	// }
        },
        methods: {
            moment: function () {
                return moment();
            },
            handleRowDbClick (row) {
            	this.$Modal.confirm({
                    title: '确认删除商品？',
                    content: '<p>确认删除商品 ' + row.goodsName + '?</p>',
                    onOk: () => {
                        for (var i = 0; i < this.orderItems.length; i++) {
                            if (row.id === this.orderItems[i].id) {
                                this.orderItems.splice(i, 1);
                                this.order.orderItemIds.splice(i, 1);
                            }
                        }
                    },
                    onCancel: () => {

                    }
                });
            },

            resetAmount(index) {
                let row = this.orderItems[index];
                let receiveQuality =
                    row["receiveQuality"] && !isNaN(row["receiveQuality"]) ? row["receiveQuality"] : 0;
                let price =
                    row["price"] && !isNaN(row["price"]) ? row["price"] : 0;
                let free = row["free"] && !isNaN(row["free"]) ? row["free"] : 0;
                let num = receiveQuality - free > 0 ? receiveQuality - free : 0;
                row.amount = (num * price).toFixed(2);
            },

            onSelectGoods (goodsId, goodItem) {
                if(!this.order.warehouseId || this.order.warehouseId<=0) {
                    this.$Modal.info({
                        title: '操作提示',
                        content: '请先选择对应仓库'
                    });
                    this.$refs.goodSelector.clearSingleSelect();
                    return;
                }
                if (!goodsId || !goodItem) {
                    return;
                }
                var index = this.order.orderItemIds.indexOf(goodsId);
                if (index < 0) {
                    //根据当前仓库和商品ID获取最近价格和库存信息
                    let reqDate = {
                        warehouseId: this.order.warehouseId,
                        goodsIdList: JSON.stringify([goodsId])
                    };
                    util.ajax.get("/repertory/in/current/balance", {params: reqDate})
                        .then((response) => {
                            let data = response.data;
                            let record ='';
                            if(data) {
                                record = data[goodsId];
                            }
                            this.addOrdderItem(goodItem, data);
                        })
                        .catch((error) => {
                            this.addOrdderItem(goodItem);
                            util.errorProcessor(this, error);
                        });
                } else {
                    this.$Message.warning('该商品已经添加');
                    this.$refs.goodSelector.clearSingleSelect();
                }
            },
            addOrdderItem(goodItem, record) {
                var obj = {
                    goodsId: goodItem.id,
                    goodsName: goodItem.name,
                    origin: goodItem.origin,
                    jx: goodItem.jxName,
                    spec: goodItem.spec,
                    factory: goodItem.factory,
                    unitName: goodItem.unitName,
                    packUnitName: goodItem.packUnitName,
                    bigPack: goodItem.bigPack,
                    receiveQuality: 0,
                    bigQuality: 0,
                    free: 0,
                    lastPrice: record ? record.lastPrice : '',
                    price: record ? record.lastPrice : 0,
                    expDate: '',
                    productDate: '',
                    batchCode: '',
                    amount: 0,
                    warehouseLocation: '',
                    buyOrderCount: record ? record.buyOrderCount : '',
                    balance: record ? record.balance : '',
                    ongoingCount: record ? record.ongoingCount : '',
                    rejectQuality: 0,
                    rejectComment: '',
                    taxRate: goodItem.inTax
                };
                this.orderItems.push(obj);
                this.order.orderItemIds.push(goodItem.id);
                this.$refs.goodSelector.clearSingleSelect();
            },
            doSave (status) {
                var self = this;
                this.saving = true;
                this.order['status'] = status;
                util.ajax.post('/repertory/in/save', this.order)
                    .then(function (response) {
                        self.saving = false;
                        self.$Message.info('采购入库订单保存成功');
                        self.closeConfirm = true;
                    })
                    .catch(function (error) {
                        self.saving = false;
                        util.errorProcessor(self, error);
                    });
            },
            clearData () {
                this.order = {
                    supplierId: null,
                    orderItemIds: []
                };
                this.orderItems = [];
                this.editView = false;
            },
            closeTab () {
                this.clearData();
                let pageName = util.closeCurrentTab(this);
                this.$router.push({
                    name: pageName
                });
            },
            saveOrderBtnClick () {
                this.order.details = this.orderItems;
                this.$refs.orderForm.validate((valid) => {
                    if (!valid) {
                        this.$Message.error('请检查必输项和是否已经添加商品!');
                        return;
                    } else {
                        this.$Modal.confirm({
                            title: '保存确认',
                            content: '确认输入的数据是否已经正确',
                            onOk: () => {
                                this.doSave('INIT');
                            },
                            onCancel: () => {
                            }
                        });
                    }
                });
            },
            tempSaveOrderBtnClick () {
                this.order.details = this.orderItems;
                this.$refs.orderForm.validate((valid) => {
                    if (!valid) {
                        this.$Message.error('请检查必输项和是否已经添加商品!');
                        return;
                    } else {
                         this.doSave('TEMP_STORAGE');
                    }
                });
            },

            getTempOrderBtnClick() {
                this.receiveTemp = true;
            },

            receiveTempChoose(data) {
                if (!data || !data.id) {
                    this.receiveTemp = false;
                    return;
                }
                data.receiveDate = data.receiveDate ? moment(data.receiveDate).format('YYYY-MM-DD') : '';
                data.payDate = data.payDate ? moment(data.payDate).format('YYYY-MM-DD') : '';
                data.shipStartDate = data.shipStartDate ? moment(data.shipStartDate).format('YYYY-MM-DD HH:mm') : '';
                data.shipEndDate = data.shipEndDate ? moment(data.shipEndDate).format('YYYY-MM-DD HH:mm') : '';

                this.editView = true;
                this.order = data;
                //需要对data detail中的日期做字符串处理功能
                for(let i=0; i<data.details.length; i++) {
                    // productDate/expDate; 
                    let productDate = data.details[i].productDate ? moment(data.details[i].productDate).format('YYYY-MM-DD') : '';
                    let expDate = data.details[i].expDate ? moment(data.details[i].expDate).format('YYYY-MM-DD') : '';
                    data.details[i].expDate = expDate;
                    data.details[i].productDate = productDate;
                }

                this.orderItems = data.details;
                let itemIds = [];
                if(this.orderItems && this.orderItems.length > 0) {
                    for (let i=0; i<this.orderItems.length; i++) {
                        let item = this.orderItems[i];
                        if(item && item.goodsId) {
                            itemIds.push(item.goodsId);
                        }
                    }
                }
                this.order['orderItemIds'] = itemIds;
                this.receiveTemp = false;
                //设置每一条明细的历史价格和订单数信息
                this.setCurrentBalanceRecord(itemIds);
            },

            setCurrentBalanceRecord(goodsIdList) {
                if (!goodsIdList || goodsIdList.length <= 0 || !this.order.warehouseId 
                    || !this.orderItems || this.orderItems.length <= 0) {
                    return;
                }
                let reqDate = {
                    warehouseId: this.order.warehouseId,
                    goodsIdList: JSON.stringify(goodsIdList)
                };
                let self = this;
                util.ajax.get("/repertory/in/current/balance", {params: reqDate})
                    .then((response) => {
                        let data = response.data;
                        self.orderItems.forEach((element, index) => {
                            let record = data[element.goodsId];
                            if (record) {
                                element['lastPrice'] = record.lastPrice ? record.lastPrice : '';
                                element['buyOrderCount'] = record.buyOrderCount ? record.buyOrderCount : '';
                                element['balance'] = record.balance ? record.balance : '';
                                element['ongoingCount'] = record.ongoingCount ? record.ongoingCount : '';
                            }
                            self.$set(this.orderItems, index, element)
                        });
                    })
                    .catch((error) => {
                        util.errorProcessor(self, error);
                    });
            },

            buyCheckOrderGetBtnClick() {
                this.buyCheckOrder = true;
            },

            buyOrderChoose(buyOrder) {
                if(!buyOrder || !buyOrder.id) {
                    this.$Message.warning('获取选取的订单信息失败');
                    return;
                }
                util.ajax.get("/repertory/in/buy/order/" + buyOrder.id) 
                    .then((response) => {
                        let data  = response.data;
                        if (data) {
                            this.buyCheckOrder = false;
                            this.editView = true;
                            this.order = data;
                            this.orderItems = data.details ? data.details : [];
                            let itemIds = [];
                            if(this.orderItems && this.orderItems.length > 0) {
                                for (let i=0; i<this.orderItems.length; i++) {
                                    let item = this.orderItems[i];
                                    if(item && item.goodsId) {
                                        itemIds.push(item.goodsId);
                                    }
                                }
                            }
                            this.order['orderItemIds'] = itemIds;
                            this.order['receiveDate'] = moment().format('YYYY-MM-DD');
                            this.receiveTemp = false;
                            //设置每一条明细的历史价格和订单数信息
                            this.setCurrentBalanceRecord(itemIds);
                        }
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    });
            },

            openChooseLocation(rowData, index) {
                if (!rowData || index === undefined || index < 0) {
                    this.$Message.error('获取编辑位置失败');
                    return;
                }
                this.currEditLocationRow = rowData;
                this.currDditLocationIndex = index;
                this.locationModal = true;
            },

            chooseLocation(data) {
                this.locationModal = false;
                this.currEditLocationRow.warehouseLocation = data.location;
                this.$set(this.orderItems, this.currDditLocationIndex, this.currEditLocationRow);
            },

            locationModalClose() {
                this.locationModal = false;
                this.currDditLocationIndex = '';
                this.currEditLocationRow = {};
            }
        }
    };
</script>

<style>
.ivu-form-item {
    margin-bottom: 0px;
}

.ivu-table-cell {
    padding-left: 5px;
    padding-right: 5px;
}


</style>

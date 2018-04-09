<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
	<Row>
		<Card>
			<p slot="title" >
				<Icon type="document"></Icon> 创建入库单
			</p>
			<div slot="extra">
				<ButtonGroup class="padding-left-20" size="small">
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
                            <Input v-if="editView" v-model="order.supplierName" :disabled="true"></Input>
                            <supplier-select v-if="!editView" v-model="order.supplierId" size="small" ></supplier-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="供应商代表" prop="supplierContactId" >
                            <supplier-contact-select v-model="order.supplierContactId" 
                                :supplierId="order.supplierId" 
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
                            <temper-control-select v-model="order.tempControlMethod" size="small"></temper-control-select>
                        </FormItem>
					</Col>
                    <Col span="6">
                        <FormItem label="到货温度(℃)" >
                            <Input :number="true" v-model="order.receiveTemp" size="small"/>
                        </FormItem>
					</Col>
                    <Col span="6">
                        <FormItem label="温控状态" >
                            <temper-status-select v-model="order.tempControlStatus"  size="small"></temper-status-select>
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
                            <ship-method-select v-model="order.shipMethod" size="small"></ship-method-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="运输工具" prop="shipTool">
                            <ship-tool-select v-model="order.shipTool" size="small" ></ship-tool-select>
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
                            <buy-type-select v-model="order.buyType" size="small"></buy-type-select>
                        </FormItem>
					</Col>
					<Col span="6">
                        <FormItem label="发票种类" >
                            <bill-type-select v-model="order.billType" size="small"></bill-type-select>
                        </FormItem>
					</Col>
                    <Col span="6">
                        <FormItem label="账期" >
                            <Input v-model="order.term" size="small" />
                        </FormItem>
					</Col>
				</Row>
                <Row>
                    <Col span="12">
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

        <Modal v-model="receiveTemp" title="采购入库单暂挂提取" :mask-closable="false" width="70">
            <in-temp :open="receiveTemp" @on-choosed="receiveTempChoose"></in-temp>
            <div slot="footer"></div>
        </Modal>

        <Modal v-model="buyCheckOrder" title="采购单提取" :mask-closable="false" width="70">
            <buy-order-list @on-choosed="buyOrderChoose" :chooseModal="true" ></buy-order-list>
            <div slot="footer"></div>
        </Modal>

	</Row>

</template>

<script>
    import moment from 'moment';
    import util from '@/libs/util.js';
    import supplierSelect from "@/views/selector/supplier-select.vue";
    import supplierContactSelect from "@/views/selector/supplier-contact-select.vue";
    import buyerSelect from "@/views/selector/buyer-select.vue";
    import shipToolSelect from "@/views/selector/ship-tool-select.vue";
    import shipMethodSelect from "@/views/selector/ship-method-select.vue";
    import temperControlSelect from "@/views/selector/temper-control-select.vue";
    import temperStatusSelect from "@/views/selector/temper-status-select.vue";
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import shipCompanySelect from "@/views/selector/ship-company-select.vue";
    import buyTypeSelect from "@/views/selector/buy-type-select.vue";
    import billTypeSelect from "@/views/selector/bill-type-select.vue";
    import goodSelect from "@/views/selector/good-select.vue";
    import inTemp from "./in-temp.vue";
    import buyOrderList from "@/views/buy/buy-order-list.vue";

    export default {
        name: 'in-make',
        components: {
            supplierSelect,
            supplierContactSelect,
            buyerSelect,
            shipToolSelect,
            shipMethodSelect,
            temperControlSelect,
            temperStatusSelect,
            shipCompanySelect,
            warehouseSelect,
            buyTypeSelect,
            billTypeSelect,
            goodSelect,
            inTemp,
            buyOrderList
        },
        data () {
            return {
                saving: false,
            	totalAmount: 0,
            	edittingRow: {},
                closeConfirm: false,
                orderItems: [],
                order: {
                    supplierId: null,
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
                        title: '本次收货数量',
                        key: 'receiveQuality',
                        align: 'center',
                        width: 130,
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
                        title: '金额',
                        key: 'amount',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '货位号',
                        key: 'warehouseLocation',
                        align: 'center',
                        width: 130,
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
                this.$set(this.orderItems, index, row);
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
                    jx: goodItem.jx,
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
                    amount: '',
                    warehouseLocation: '',
                    buyOrderCount: record ? record.buyOrderCount : 0,
                    balance: record ? record.balance : 0,
                    ongoingCount: record ? record.ongoingCount : 0,
                    rejectQuality: 0,
                    rejectComment: '',
                    taxRate: 0
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
                this.editView = true;
                this.order = data;
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
                            this.receiveTemp = false;
                            //设置每一条明细的历史价格和订单数信息
                            this.setCurrentBalanceRecord(itemIds);
                        }
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    });
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

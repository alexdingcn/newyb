<style lang="less">
    @import '../../styles/common.less';
    .option-goods-spec {
        float: right;
        color: #999;
    }
    .ivu-form-item {
        margin-bottom: 0px;
    }

    .ivu-table-cell {
        padding-left: 5px;
        padding-right: 5px;
    }
    th .ivu-table-cell {
        white-space: nowrap;
    }

</style>

<template>
    <Row>
        <Card>
            <p slot="title" >
                <Icon type="document"></Icon>入库制单
            </p>
            <div slot="extra">

                <ButtonGroup class="padding-left-20">
                    <Button type="primary" icon="android-add-circle" @click="buyCheckOrderGetBtnClick" >载入采购单</Button>&nbsp;&nbsp;
                    <Button type="primary" icon="android-add-circle" @click="saveInOrder" :loading="saving">保存</Button>
                </ButtonGroup>
            </div>

            <Form :label-width="85" :rules="ruleValidate" :model="inOrder" ref="InOrderForm">
                <Row>
                    <Col span="6">
                    <FormItem label="仓库点" prop="warehouseId">
                        <warehouse-select v-model="inOrder.warehouseId" size="small"></warehouse-select>
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem label="入库类型" prop="inType">
                        <warehouse-intype-select v-model="inOrder.inType" size="small"></warehouse-intype-select>
                    </FormItem>
                    </Col>

                    <Col span="6">
                    <FormItem label="供应商" prop="supplierId" >
                        <Select
                                v-model="inOrder.supplierId"
                                filterable
                                clearable
                                remote
                                size="small"
                                placeholder="供应商名称/拼音"
                                :remote-method="querySupplier"
                                :loading="supplierLoading">
                            <Option v-for="option in supplierOptions" :value="option.id" :label="option.name" :key="option.id">{{option.name}}</Option>
                        </Select>
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem label="入库人员" prop="inUserId">
                        <Select
                                v-model="inOrder.inUserId"
                                clearable
                                size="small"
                                placeholder="入库人员">
                            <Option v-for="option in buyerOptions" :value="option.userId" :label="option.nickname" :key="option.userId">
                                {{option.nickname}}
                                {{option.realname||''}}
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
                    <Col span="6">
                    <FormItem label="到货日期" prop="inDate">
                        <DatePicker type="date" v-model="inOrder.inDate" size="small"/>
                    </FormItem>
                    </Col>
                    <Col span="12">
                    <FormItem label="备注" prop="inNote">
                        <Input v-model="inOrder.inNote" size="small"/>
                    </FormItem>
                    </Col>

                </Row>

                <Table border highlight-row
                       class="margin-top-8"
                       :columns="orderColumns" :data="orderItems"
                       ref="inOrderTable" style="width: 100%;" size="small"
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
        <Modal v-model="buyCheckOrder" title="采购单提取" :mask-closable="false" width="70">
            <buy-order-in-list @on-choosed="buyOrderChoose" :chooseModal="true" ></buy-order-in-list>
            <div slot="footer"></div>
        </Modal>
    </Row>

</template>

<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';
    import warehouseIntypeSelect from "@/views/selector/warehouse-intype-select.vue";
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import buyOrderInList from "@/views/warehouse/buy-order-in-list.vue";
    export default {
        name: 'in_order',
        components: {
            warehouseIntypeSelect,
            warehouseSelect,
            buyOrderInList
        },
        data () {
            return {
                saving: false,
                supplierLoading: false,
                supplierOptions: [],
                goodsLoading: false,
                goodsOptions: [],
                buyerOptions: [],
                totalAmount: 0,
                edittingRow: {},
                closeConfirm: false,
                orderItems: [],
                inOrder: {
                    supplierId: null,
                    eta: moment().add(1, 'd').format('YYYY-MM-DD'),
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
                        sortable: true
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
                                        var inputList = self.$refs.inOrderTable.$el.querySelectorAll('input');
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
                                        var inputList = self.$refs.inOrderTable.$el.querySelectorAll('input');
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
                        width: 100
                    },
                    {
                        title: '最近采购价',
                        key: 'last',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '批号',
                        key: 'batch',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '有效期',
                        key: 'exp',
                        align: 'center',
                        width: 100
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
                    orderItems: [
                        { required: true, type: 'array', array: {min: 1}, message: '请添加商品', trigger: 'blur' }
                    ]
                },
                buyCheckOrder: false

            };
        },
        mounted () {
            this.queryBuyers();
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
            queryBuyers () {
                var self = this;
                util.ajax.get('/userrole/list', {params: {roleQuery: 'ROLE_BUYER;ROLE_BUYER_SPECIAL'} })
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.buyerOptions = response.data;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
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
            buyCheckOrderGetBtnClick() {
                this.buyCheckOrder = true;
            },
            buyOrderChoose(buyOrder) {
                if(!buyOrder || !buyOrder.id) {
                    this.$Message.warning('获取选取的订单信息失败');
                    return;
                }
                util.ajax.get("/receive/buy/order/" + buyOrder.id)
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
            },
            handleRowDbClick (row) {
                this.$Modal.confirm({
                    title: '确认删除商品？',
                    content: '<p>确认删除商品 ' + row.name + '?</p>',
                    onOk: () => {
                        for (var i = 0; i < this.orderItems.length; i++) {
                            if (row.id === this.orderItems[i].id) {
                                this.orderItems.splice(i, 1);
                                this.inOrder.orderItemIds.splice(i, 1);
                            }
                        }
                    },
                    onCancel: () => {

                    }
                });
            },
            onSelectGoods (goodsId) {
                var goods = this.goodsOptions.filter(o => o.id === goodsId);
                if (goods && goods.length == 1) {
                    var index = this.inOrder.orderItemIds.indexOf(goodsId);
                    if (index < 0) {
                        var obj = goods[0];
                        obj['amount'] = 0;
                        this.orderItems.push(goods[0]);
                        this.inOrder.orderItemIds.push(goodsId);
                        var self = this;
                        setTimeout(function () {
                            self.$refs.inOrderTable.$el.querySelector('.ivu-table-body tr:last-child input').focus();
                        }, 400);
                    } else {
                        this.$Message.warning('该商品已经添加');
                    }
                }
                this.$refs.goodsSelect.clearSingleSelect();
            },
            doSave () {
                var self = this;
                this.saving = true;
                util.ajax.post('/buy/add', this.inOrder)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.inOrder.id = response.data.orderId;
                            self.inOrder.status = response.data.status;
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
            clearData () {
                this.inOrder = {
                    supplierId: null,
                    eta: moment().add(1, 'd').format('YYYY-MM-DD'),
                    orderItemIds: []
                };
            },
            closeTab () {
                this.clearData();
                let pageName = util.closeCurrentTab(this);
                this.$router.push({
                    name: pageName
                });
            },
            saveInOrder () {
                this.inOrder.orderItems = this.orderItems;
                this.$refs.InOrderForm.validate((valid) => {
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

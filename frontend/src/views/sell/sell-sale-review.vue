<template>
    <div>
        <Card >
            <p slot="title">
                销售审核
            </p>
            <div slot="extra">
                <ButtonGroup size="small">
                    <Button type="success" icon="checkmark-round" :loading="sellOrderLoading" @click="reviewOkBtnClick">审核通过</Button>
                    <Button type="error" icon="close-round" :loading="sellOrderLoading" @click="removeBtnClick">删除订单</Button>
                </ButtonGroup>
            </div>
            <Form ref="searchForm" :model="query" :label-width="100">
                <Row>
                    <Col span="8">
                        <FormItem label="制单日期">
                            <DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="制单日期" style="width:180px"></DatePicker>
                        </FormItem>
                    </Col>
                    <Col span="8">
                        <FormItem label="客户">
                            <customer-select v-model="query.customerId"></customer-select>
                        </FormItem>
                    </Col>
                    <Col span="8"></Col>
                </Row>
                <Row>
                    <Col span="8">
                        <FormItem label="销售员">
                            <Select v-model="query.salerId" clearable filterable placeholder="销售员">
                                <Option v-for="item in salerList" :value="item.userId" :key="item.userId">{{ item.nickname }}{{item.realname ? (' - [' + item.realname + ']') : ''}}</Option>
                            </Select>
                        </FormItem>
                    </Col>
                    <Col span="8" >
                        <FormItem label="状态">
                            <Select v-model="query.status" placeholder="状态">
                                <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                            </Select>
                        </FormItem>
                    </Col>
                    <Col span="7" offset="1">
                        <Button type="primary" icon="ios-search" :loading="sellOrderLoading" @click="querySellOrderList"></Button>
                    </Col>
                </Row>
            </Form>
        </Card>
        <div >
            <Table border highlight-row disabled-hover height="250" style="width: 100%" 
                   :columns="orderListColumns" :data="orderList"
				   ref="sellOrderListTable" size="small"
                   :loading="sellOrderLoading" 
                   @on-row-click="handleSelectSellOrder"
				   no-data-text="使用右上方输入搜索条件">
			</Table>
        </div>

        <div class="table-div">
            <Table border highlight-row height="300" :loading="detailLoading" 
                   :columns="sellGoodColumns" :data="sellGoodList"
                   ref="sellGoodTable" style="width: 100%;" size="small"
                   no-data-text="点击上方订单后查看销售明细">
                <div slot="header">
                    <h3 class="padding-left-20" >
                        <b>合计数量:</b> ￥{{ totalCount }} <b class="margin-left-30">合计金额:</b> ￥{{ totalAmount }}
                    </h3>
                </div>
            </Table>
        </div>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import customerSelect from "@/views/customer/customer-select.vue";
import goodExpand from "@/views/good/good-expand.vue";

export default {
    name: 'sell-sale-review',
    components: {
        customerSelect,
        goodExpand
    },
    data() {
        return {
            statusOptions: [
                {key: 'ALL', name:'所有'},
                {key: 'INIT', name:'未质审完成'},
                {key: 'QUALITY_CHECKED', name:'已质审完成'},
                {key: 'SALE_CHECKED', name:'销售审核完成'}
            ],
            salerList: [],
            dateRange: [
                moment().add(-1,'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD'),
            ],
            query: {
                customerId: '',
                status: 'QUALITY_CHECKED',
                salerId: ''
            },
            sellOrderLoading: false,
            orderList: [],
            orderListColumns: [
                {
                    key: 'id',
                    title: '#',
                    align: 'center',
                    width: 60
                },
                {
                    title: '制单日',
                    key: 'createOrderDate',
                    align: "center",
                    width: 90,
                    sortable: true,
                    render:(h, params) => {
                        return moment(params.row.createOrderDate).format('YYYY-MM-DD');
                    }
                },
                {
                    title: '仓库点',
                    key: 'warehouseName',
                    align: "center",
                    width: 90
                },
                {
                    title: '客户',
                    key: 'customerName',
                    align: "center",
                    width: 150
                },
                {
                    title: '销售员',
                    key: 'salerId',
                    width: 90,
                    align: "center",
                    render: (h, params) => {
                        let itemArr = this.salerList.filter(item => item.id === params.row.salerId);
                        if (itemArr && itemArr[0]) {
                            let saler = itemArr[0];
                            return saler.nickname + (saler.realname ? ' - [' + saler.realname + ']' : ''); 
                        }else {
                            return params.row.salerId;
                        }
                    }
                },
                {
                    title: '制单人',
                    key: 'createBy',
                    align: "center",
                    width: 90
                },
                {
                    title: '提货员',
                    key: 'takeGoodsUser',
                    align: "center",
                    width: 90
                },
                {
                    title: '收款金额',
                    key: 'payAmount',
                    align: 'center',
                    width: 90
                },
                {
                    title: '销售单号',
                    key: 'orderNumber',
                    align: 'center',
                    width: 120
                },
                {
                    title: '收货人',
                    key: 'customerRepName',
                    align: "center",
                    width: 120
                },
                {
                    title: '收货电话',
                    key: 'customerRepContactPhone',
                    align: "center",
                    width: 100
                },
                {
                    title: '收货地址',
                    key: 'customerRepRepertoryAddress',
                    align: "center",
                    width: 150
                }
            ],
            detailLoading: false,
            sellGoodList: [],
            detailChooseItems: [],
            sellGoodColumns: [
                {
                    type: "expand",
                    width: 50,
                    render: (h, params) => {
                        let repertoryInfo = params.row.repertoryInfo;
                        let good = {};
                        if (repertoryInfo) {
                        good = repertoryInfo.goods;
                        }
                        let productDate = '';
                        if (repertoryInfo.productDate) {
                            productDate = moment(repertoryInfo.productDate).format('YYYY-MM-DD');
                        }
                        let expDate = '';
                        if (repertoryInfo.expDate) {
                            expDate = moment(repertoryInfo.expDate).format('YYYY-MM-DD');
                        }
                        return h(goodExpand, {
                            props: {
                                good: good,
                                repertoryInfo: repertoryInfo,
                                productDate: productDate,
                                expDate: expDate
                            }
                        });
                    }
                },
                {
                    title: '商品名称',
                    key: 'goodName',
                    width: 150
                },
                {
                    title: '生产企业',
                    key: 'factoryName',
                    width: 150
                },
                {
                    title: '剂型',
                    key: 'jx',
                    width: 100,
                    render: (h, params) => {
                        let repertoryInfo = params.row.repertoryInfo;
                        if (repertoryInfo) {
                            return h('span', repertoryInfo.jx);
                        }
                    }
                },
                {
                    title: '规格',
                    key: 'spec',
                    width: 100,
                    render: (h, params) => {
                        let repertoryInfo = params.row.repertoryInfo;
                        if (repertoryInfo) {
                            return h('span', repertoryInfo.spec);
                        }
                    }
                },
                {
                    title: '生产日期',
                    width: 120,
                    key: 'productData',
                    render: (h, params) => {
                        let repertoryInfo = params.row.repertoryInfo;
                        if (repertoryInfo && repertoryInfo.productDate) {
                            return moment(repertoryInfo.productDate).format('YYYY-MM-DD');
                        }
                    }
                },
                {
                    title: '有效期至',
                    key: 'expDate',
                    width: 120,
                    render: (h, params) => {
                        let repertoryInfo = params.row.repertoryInfo;
                        if (repertoryInfo && repertoryInfo.expDate) {
                            return moment(repertoryInfo.expDate).format('YYYY-MM-DD');
                        }
                    }
                },
                {
                    title: '单位',
                    key: 'unitName',
                    width: 90,
                    render: (h, params) => {
                        let repertoryInfo = params.row.repertoryInfo;
                        if (repertoryInfo) {
                            return h('span', repertoryInfo.unitName);
                        }
                    }
                },
                {
                    title: '数量',
                    width: 90,
                    key: 'quantity'
                },
                {
                    title: '价格',
                    width: 90,
                    key: 'realPrice'
                },
                {
                    title: '金额',
                    width: 100,
                    key: 'amount'
                },
                {
                    title: '质量审核状态',
                    width: 120,
                    key: 'checkStatus',
                    render: (h, params) => {
                        const checkStatus = params.row.checkStatus;
                        const color = !checkStatus ? 'blue' : checkStatus === 'OK' ? 'green' : 'red';
                        const text = !checkStatus ? '待审' : checkStatus === 'OK' ? '通过' : '拒绝';
                        return h('Tag', {
                            props: {
                                type: 'dot',
                                color: color
                            }
                        }, text);
                    }
                },
                {
                    title: '质量审核人',
                    width: 100,
                    key: 'checkUser',
                    render: (h, params) => {
                        const checkUser = params.row.checkUser ? params.row.checkUser : '';
                        return h('span', checkUser);
                    }
                },
                {
                    title: '质量审核日期',
                    width: 120,
                    key: 'checkDate',
                    render: (h, params) => {
                        let checkDate = params.row.checkDate;
                        return h('span', checkDate ? moment(checkDate).format('YYYY-MM-DD HH:mm') : '');
                    }
                },
                {
                    title: '质量审核结论',
                    width: 180,
                    key: 'checkResult',
                    render: (h, params) => {
                        const checkResult = params.row.checkResult ? params.row.checkResult : '';
                        return h('span', checkResult);
                    }
                }
            ],
            totalCount: 0,
            totalAmount: 0,
            chooseOrderItem: ''
        }
    },
    mounted() {
        this.initData();
    },
    watch: {
        sellGoodList(data) {
            if (data && data.length > 0)  {
                this.totalCount = data.reduce((totalCount, item) => {
                    let count = item.quantity ? item.quantity : 0;
                    return totalCount + count;
                }, 0);
                this.totalAmount = data.reduce((totalAmount, item) => {
                    let amount = item.amount ? item.amount : 0;
                    return totalAmount + parseFloat(amount);
                }, 0);
            }
        }
    },
    methods: {
        initData() {
            this.getSalerList();
        },
        getSalerList() {
            util.ajax
                .get("/userrole/list", { params: { roleQuery: "ROLE_SALER" } })
                .then(response => {
                this.salerList = response.data;
                })
                .catch(error => {
                util.errorProcessor(this, error);
                });
        },
        querySellOrderList() {
            let reqData = {
                goodId: this.query.goodId,
                salerId: this.query.salerId
            };
            let statusList = [];
            if (this.query.status === 'ALL') {
                statusList = ['INIT', 'QUALITY_CHECKED', 'SALE_CHECKED'];
            }else {
                statusList = [this.query.status];
            }
            reqData['statusList'] = statusList;
            reqData['startDate'] = this.dateRange[0];
            reqData['endDate'] = this.dateRange[1];
            this.sellOrderLoading = true;
            this.chooseOrderItem = '';
            this.sellGoodList = [];
            util.ajax.post("/sell/order/review/list", reqData)
                .then((response) => {
                    this.orderList = response.data;
                    this.sellOrderLoading = false;
                })
                .catch((error) => {
                    this.sellOrderLoading = false;
                    util.errorProcessor(this, error);
                })
        },
        handleSelectSellOrder(data) {
            if (!data || !data.id) {
                return;
            }
            this.chooseOrderItem = data;
            let sellOrderId = data.id;
            this.detailLoading = true;
            util.ajax.get("sell/order/review/detail", {params: {orderId: sellOrderId}})
                .then((response) => {
                    this.detailLoading = false;
                    let order = response.data;
                    if (order && order.details) {
                        this.sellGoodList = order.details;
                    }else {
                        this.sellGoodList = [];
                    }
                })
                .catch((error) => {
                    this.detailLoading = false;
                    util.errorProcessor(this, error);
                })
        },

        validateAction(action) {
            let actionLab = action === 'CHECK' ? '审核' : '删除';
            if (!this.chooseOrderItem || !this.chooseOrderItem.id) {
                this.$Modal.info({
                    title: '信息验证提示',
                    content: '请先选择需要' + actionLab + '的订单信息'
                });
                return false;
            }
            if (!this.sellGoodList || this.sellGoodList.length <= 0) {
                this.$Modal.error({
                    title: '信息验证提示',
                    content: '订单缺失商品信息,不能' + actionLab
                });
                return false;
            }
            //如果是审核，需要所有的商品都通过质量审核
            if (action === 'CHECK') {
                let items = this.sellGoodList.filter(item => item.checkStatus !== 'OK');
                if (items && items.length > 0) {
                    this.$Modal.warning({
                        title: '信息验证提示',
                        content: '存在质量审核未通过的商品，不能审核通过'
                    });
                    return false;
                }
                //检查订单是是否处于质检通过状态，如果不是，不能再次审核
                let orderStatus = this.chooseOrderItem.status;
                if (!orderStatus || orderStatus !== 'QUALITY_CHECKED') {
                    this.$Modal.warning({
                        title: '信息验证提示',
                        content: '订单不是质量检查通过的状态，不能进行审核'
                    });
                    return false;
                }
            }else {
                let items = this.sellGoodList.filter(item => item.checkStatus === 'OK');
                if (items && items.length > 0) {
                    this.$Modal.warning({
                        title: '信息验证提示',
                        content: '存在质量审核通过的商品，不能删除订单'
                    });
                    return false;
                }
            }
            return true;
        },
        reviewOkBtnClick() {
            let validate = this.validateAction('CHECK');
            if (!validate) {
                return;
            }
            let reqData = {
                orderId: this.chooseOrderItem.id
            };
            this.sellOrderLoading = true;
            util.ajax.post("/sell/order/review/sale/ok", reqData)
                .then((response) => {
                    this.sellOrderLoading = false;
                    this.querySellOrderList();
                    this.$Message.success('审核成功');
                })
                .catch((error) => {
                    this.sellOrderLoading = false;
                    if (error.response && error.response.data && error.response.data.code === 2214) {
                        //库存不足问题
                        let goodNames = error.response.data.data;
                        let nameLabel = '';
                        if (goodNames && Array.isArray(goodNames)) {
                            for(let i=0; i<goodNames.length; i++) {
                                nameLabel = nameLabel + goodNames[i] + ' ';
                            }
                        }
                        this.$Modal.error({
                            title: '库存不足提示',
                            content: '订单下的商品存在库存不足的情况, 请确认, 对应商品:' + nameLabel
                        });
                    }else {
                        util.errorProcessor(this, error);
                    }
                });
        },
        removeBtnClick() {
            let validate = this.validateAction('DELETE');
            if (!validate) {
                return;
            }
            this.$Modal.confirm({
                title: '删除提示',
                content: '订单删除后不可恢复，是否确认删除?',
                onOk: () => {
                    this.removeOrder();
                },
                onCancel: () => {

                }
            })
        },
        removeOrder() {
        this.sellOrderLoading = true;
        util.ajax.delete("/sell/order/remove/" + this.chooseOrderItem.id)
            .then((response) => {
                this.sellOrderLoading = false;
                this.querySellOrderList();
                this.$Message.success('删除成功');
            })
            .catch((error) => {
                this.sellOrderLoading = false;
                util.errorProcessor(this, error);
            });
        }

    }
}
</script>

<style>
.margin-top-8 {
    margin-top: 8px;
}
.margin-left-30 {
    margin-left: 30px;
}
.ivu-form-item {
    margin-bottom: 5px;
}
.search-div {
    background-color: #fff;
}
.table-div {
    background-color: #fff;
    margin-top: 10px;
}
</style>


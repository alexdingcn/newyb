<template>
    <div>
        <div class="search-div">
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
        </div>
        <div >
            <Table border highlight-row disabled-hover height="250" style="width: 100%" 
                   :columns="orderListColumns" :data="orderList"
				   ref="sellOrderListTable" size="small"
                   :loading="sellOrderLoading" 
                   @on-row-click="handleSelectBuyOrder"
				   no-data-text="使用右上方输入搜索条件">
			</Table>
        </div>

        <div class="table-div">
            <Row type="flex" justify="start">
                <ButtonGroup size="small">
                    <Button type="success" icon="checkmark-round" :loading="detailLoading" @click="reviewOkBtnClick">审核通过</Button>
                    <Button type="error" icon="close-round" :loading="detailLoading" @click="reviewCancelBtnClick">取消审核</Button>
                </ButtonGroup>
            </Row>
            <Table border highlight-row height="300" :loading="detailLoading" 
                   :columns="sellGoodColumns" :data="sellGoodList"
                   ref="sellGoodTable" style="width: 100%;" size="small"
                   @on-selection-change="detailSelectionChange"
                   no-data-text="点击上方订单后查看销售明细">
                <div slot="header">
                    <h3 class="padding-left-20" >
                        <b>合计数量:</b> ￥{{ totalCount }} <b class="margin-left-30">合计金额:</b> ￥{{ totalAmount }}
                    </h3>
                </div>
            </Table>
        </div>

        <Modal v-model="reviewOkModal" width="50" :mask-closable="false" title="审批通过意见登记">
            <div style="text-align:center">
                <Input v-model="checkComment" placeholder="审核意见" />
            </div>
            <div slot="footer">
                <Button type="success" @click="setCheckedOk">审核通过</Button>
            </div>
        </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import customerSelect from "@/views/customer/customer-select.vue";
import goodExpand from "@/views/good/good-expand.vue";

export default {
    name: 'sell-quality-review',
    components: {
        customerSelect,
        goodExpand
    },
    data() {
        return {
            statusOptions: [{key: 'ALL', name:'所有'},{key: 'INIT', name:'未审批'},{key: 'QUALITY_CHECKED', name:'已审批'}],
            salerList: [],
            dateRange: [
                moment().add(-1,'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD'),
            ],
            query: {
                customerId: '',
                status: 'INIT',
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
                    type: 'selection',
                    width: 60,
                    align: 'center'
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
                    title: '审核状态',
                    width: 100,
                    key: 'status',
                    render: (h, params) => {
                        let reviewOptions = params.row.reviewOptions;
                        if(reviewOptions && reviewOptions.length > 0) {
                            let items = reviewOptions.filter(item => item.reviewType === 'QUALITY_REVIEW');
                            if(items && items[0]) {
                                let itemStatus = items[0].reviewStatus;
                                return h('span', itemStatus === 'OK' ? '审核通过' : '');
                            }
                        }
                    }
                },
                {
                    title: '审核人',
                    width: 100,
                    key: 'reviewUserName',
                    render: (h, params) => {
                        let reviewOptions = params.row.reviewOptions;
                        if(reviewOptions && reviewOptions.length > 0) {
                            let items = reviewOptions.filter(item => item.reviewType === 'QUALITY_REVIEW');
                            if(items && items[0]) {
                                let updateBy = items[0].updateBy;
                                return h('span', updateBy ? updateBy : '');
                            }
                        }
                    }
                },
                {
                    title: '审核日期',
                    width: 120,
                    key: 'reviewDate',
                    render: (h, params) => {
                        let reviewOptions = params.row.reviewOptions;
                        if(reviewOptions && reviewOptions.length > 0) {
                            let items = reviewOptions.filter(item => item.reviewType === 'QUALITY_REVIEW');
                            if(items && items[0]) {
                                let updateTime = items[0].updateTime;
                                return h('span', updateTime ? moment(updateTime).format('YYYY-MM-DD HH:mm') : '');
                            }
                        }
                    }
                },
                {
                    title: '审核结论',
                    width: 180,
                    key: 'reviewComment',
                    render: (h, params) => {
                        let reviewOptions = params.row.reviewOptions;
                        if(reviewOptions && reviewOptions.length > 0) {
                            let items = reviewOptions.filter(item => item.reviewType === 'QUALITY_REVIEW');
                            if(items && items[0]) {
                                let reviewComment = items[0].reviewComment;
                                return h('span', reviewComment ? reviewComment : '');
                            }
                        }
                    }
                }
            ],
            totalCount: 0,
            totalAmount: 0,
            reviewOkModal: false,
            checkComment: ''
        }
    },
    mounted() {
        this.initData();
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
                statusList = ['INIT', 'QUALITY_CHECKED'];
            }else {
                statusList = [this.query.status];
            }
            reqData['statusList'] = statusList;
            reqData['startDate'] = this.dateRange[0];
            reqData['endDate'] = this.dateRange[1];
            this.sellOrderLoading = true;
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
        handleSelectBuyOrder(data) {
            if (!data || !data.id) {
                return;
            }
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

        detailSelectionChange(data) {
            this.detailChooseItems = data;
        },

        reviewOkBtnClick() {
            if (!this.detailChooseItems || this.detailChooseItems.length <= 0) {
                this.$Message.warning("请先选择需要审核的产品信息");
                return;
            }
            this.reviewOkModal = true;
            this.checkComment = '';
        },

        setCheckedOk() {
            let reqData = {
                reviewType: 'QUALITY_REVIEW',
                detailList: this.detailChooseItems,
                reviewStatus: 'OK',
                reviewComment: this.checkComment
            }
            this.detailLoading = true;
            util.ajax.post("/sell/order/review/ok", reqData)
                .then((response) => {
                    this.detailLoading = false;
                    let order = response.data;
                    if (order && order.details) {
                        this.sellGoodList = order.details;
                    }else {
                        this.sellGoodList = [];
                    }
                    this.$Message.success("审核成功");
                    this.reviewOkModal = false;
                })
                .catch((error) => {
                    this.detailLoading = false;
                    util.errorProcessor(this, error);
                })
        },

        reviewCancelBtnClick() {
            if (!this.detailChooseItems || this.detailChooseItems.length <= 0) {
                return;
            }
            let reqData = {
                reviewType: 'QUALITY_REVIEW',
                detailList: this.detailChooseItems
            };
            this.detailLoading = true;
            util.ajax.post("/sell/order/review/cancel", reqData)
                .then((response) => {
                    this.detailLoading = false;
                    let order = response.data;
                    if (order && order.details) {
                        this.sellGoodList = order.details;
                    }else {
                        this.sellGoodList = [];
                    }
                    this.$Message.success("取消成功");
                })
                .catch((error) => {
                    this.detailLoading = false;
                    util.errorProcessor(this, error);
                })
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


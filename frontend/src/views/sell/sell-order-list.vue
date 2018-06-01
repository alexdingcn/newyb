<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Card>
        <p slot="title">
            销售订单列表
        </p>
        <Row >
            <Form ref="searchForm" :model="searchFormItem" :label-width="90" class="sellOrderQueryForm">
                <Row type="flex" justify="center">
                    <i-col span="8" >
                        <FormItem label="客户">
                            <customer-select v-model="searchFormItem.customerId" ></customer-select>
                        </FormItem>
                    </i-col>
                    <i-col span="12" >
                        <FormItem label="制单日期">
                            <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="制单日期" style="width:200px"></DatePicker>
                        </FormItem>
                    </i-col>
                    <i-col span="4"></i-col>
                </Row>
                <Row type="flex" justify="center">
                    <i-col span="8" >
                        <FormItem label="销售单号">
                            <Input type="text" v-model="searchFormItem.orderNumber" />
                        </FormItem>
                    </i-col>
                    <i-col span="8" >
                        <FormItem label="销售员" >
                            <sale-select v-model="searchFormItem.saleId"></sale-select>
                        </FormItem>
                    </i-col>
                    <i-col span="3" offset="1" >
                        <Button icon="ios-search" type="primary" :loading="searching" @click="searchBtnClick">查询</Button>
                    </i-col>
                    <i-col span="4"></i-col>
                </Row>
            </Form>
        </Row>

        <Table border highlight-row :columns="tabColumns" :data="tabData" 
                :loading="searching"  
                no-data-text="点击上方查询按钮获取数据"
                ref="table" class="margin-top-10 sellOrderTbl" size="small">
        </Table>

        <Row type="flex" justify="end" class="margin-top-10">
            <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                @on-change="pageChange">
            </Page>
        </Row>

        <Modal v-model="showOrderDetailView" :width="70" :mask-closable="false" title="销售订单详细信息" :footerHide="true">
            <review-detail :sellOrderId="showDetailViewId" @on-cancel="showOrderDetailViewClose"></review-detail>
        </Modal>

        <Modal v-model="showShipDetailView" :width="75" :mask-closable="false" :title="shipDetailTitle" @on-cancel="ShowShipDetailViewClose" :footerHide="true">
            <sell-order-ship :order="selectedOrder" ></sell-order-ship>
        </Modal>

        <Modal v-model="paymentModal" :width="60" :mask-closable="false" :title="paymentModalTitle" :footerHide="true">
            <sell-order-payment :order="selectedOrder" @payment-updated="onPaymentUpdated" ></sell-order-payment>
        </Modal>
    </Card>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import reviewDetail from "./review-detail.vue";
import sellOrderShip from "./sell-order-ship.vue";
import sellOrderPayment from "./sell-order-payment.vue";
import customerSelect from "@/views/selector/customer-select.vue";
import saleSelect from "@/views/selector/sale-select.vue";

export default {
    name: 'sell-order-all',
    components: {
        reviewDetail,
        sellOrderShip,
        sellOrderPayment,
        customerSelect,
        saleSelect
    },

    data () {
        const statusShow = function(h, status) {
            let label = '';
            let color = '';
            switch (status) {
                case 'INIT':
                    label = '制单初始';
                    color = '#2d8cf0'
                    break;
                case 'QUALITY_CHECKED':
                    label = '质量审核完成';
                    color = '#ff9900'
                    break;
                case 'SALE_CHECKED':
                    label = '销售审核完成';
                    color = '#19be6b';
                    break;
                default:
                    label = '';
                    color = '';
                    break;
            }
            return h('Tag', {
                props: {
                    color: color
                }
            }, label);
        }

        const payStatusShow = function(h, totalAmt, paidAmt) {
            let label = '';
            let color = '';
            let remainingAmt = totalAmt - paidAmt;
            if (remainingAmt == 0) {
                label = '已支付';
                color = 'green'
            } else if (remainingAmt > 0 && paidAmt == 0) {
                label = '未支付';
                color = 'red';
            } else if (remainingAmt > 0 && paidAmt > 0) {
                label = '余￥' + remainingAmt.toFixed(2);
                color = 'yellow';
            } else if (remainingAmt < 0) {
                label = '超额支付￥' + -remainingAmt.toFixed(2);
                color = 'blue';
            } else {
                label = '';
                color = '';
            }
            return h('Tag', {
                props: {
                    color: color
                }
            }, label);
        }

        return {
            searchFormItem: {
                customerId: this.$route.query.customer_id ? this.$route.query.customer_id.toString() : '',
                orderNumber: '',
                saleId: ''
            },
            dateRange: [
                this.$route.query.start_date ? this.$route.query.start_date : moment().add(-1,'w').format('YYYY-MM-DD'),
                this.$route.query.end_date ? this.$route.query.end_date : moment().format('YYYY-MM-DD'),
            ],
            searching: false,
            tabData: [],
            tabColumns: [
                {
                    title: '查看',
                    width: 70,
                    fixed: 'left',
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'text',
                                size: 'small',
                                icon: 'eye'
                            },
                            on: {
                                click: () => {
                                    this.showReviewDetail(params.row.id);
                                }
                            }
                        });
                    }
                },
                {
                    title: '订单编号',
                    width: 180,
                    key: 'orderNumber',
                    align: 'center',
                    sortable: true
                },
                {
                    title: '制单日',
                    key: 'createOrderDate',
                    width: 120,
                    align: 'center',
                    sortable: true,
                    render: (h, params) => {
                        let createOrderDate = params.row.createOrderDate;
                        return h('span', createOrderDate ? moment(createOrderDate).format('YYYY-MM-DD') : '');
                    }
                },
                {
                    title: '仓库点',
                    key: 'warehouseName',
                    align: "center",
                    width: 100
                },
                {
                    title: '客户',
                    key: 'customerName',
                    width: 170,
                    align: 'center'
                },
                {
                    title: '状态',
                    key: 'status',
                    width: 150,
                    align: 'center',
                    render: (h, params) => {
                        return statusShow(h, params.row.status);
                    }
                },
                {
                    title: '付款状态',
                    key: 'payStatus',
                    width: 150,
                    align: 'center',
                    render: (h, params) => {
                        return payStatusShow(h, params.row.totalAmount, params.row.paidAmount);
                    }
                },
                {
                    title: '总计金额',
                    key: 'totalAmount',
                    width: 100,
                    render: (h, params) => {
                        return h('span', '￥'+params.row.totalAmount)
                    }
                },
                {
                    title: '总计数量',
                    key: 'totalQuantity',
                    width: 100
                },
                {
                    title: '免零金额',
                    key: 'freeAmount',
                    width: 120
                },
                {
                    title: '整单折扣率',
                    key: 'disRate',
                    width: 120
                },
                {
                    title: '销售员',
                    key: 'saleId',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                        let nickName = params.row.saleNickName;
                        let realName = params.row.saleRealName;
                        if (nickName && nickName) {
                            return h('span', realName + '---' + nickName);
                        }else if(nickName) {
                            return h('span', nickName);
                        }else {
                            return h('span', params.row.saleId);
                        }
                    }
                },
                {
                    title: '制单人',
                    key: 'createBy',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '提货员',
                    key: 'takeGoodsUser',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '收货人',
                    key: 'customerRepName',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '收货电话',
                    key: 'customerRepContactPhone',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '收货地址',
                    key: 'customerRepRepertoryAddress',
                    width: 220,
                    align: 'center'
                },
                {
                    title: '温控方式',
                    key: 'temperControlName',
                    align: 'center',
                    width: 120
                },
                {
                    title: '运输方式',
                    key: 'shipMethodName',
                    align: "center",
                    width: 110
                },
                {
                    title: '运输工具',
                    key: 'shipToolName',
                    align: "center",
                    width: 110
                },
                {
                    title: '操作',
                    align: 'center',
                    class: 'yy',
                    width: 180,
                    fixed: 'right',
                    render: (h, params) => {
                        return h('ButtonGroup', {
                            props: {
                                size: 'small'
                            }
                        }, [
                            h('Button', {
                                props: {
                                    type: 'primary',
                                    icon: "edit"
                                },
                                on: {
                                    click: () => {
                                        this.showPayment(params.row);
                                    }
                                }
                            }, '收款'),
                            h('Button', {
                                props: {
                                    type: 'ghost',
                                    icon: 'model-s'
                                },
                                on: {
                                    click: () => {
                                        this.openShowShipDetailView(params.row);
                                    }
                                }
                            }, '运输记录')
                        ]);
                    }
                }
            ],
            showOrderDetailView: false,
            showDetailViewId: -1,
            totalCount: 0,
            currentPage: 1,
            pageSize: 50,
            showShipDetailView: false,
            selectedOrder: {},
            shipDetailTitle: '',
            paymentModal: false,
            paymentModalTitle: ''
        };
    },
    mounted() {
        this.refreshTableData();
    },
    methods: {
        searchBtnClick () {
            this.currentPage = 1;
            this.refreshTableData();
        },
        refreshTableData () {
            let reqData = {
                customerId: this.searchFormItem.customerId,
                orderNumber: this.searchFormItem.orderNumber,
                saleId: this.searchFormItem.saleId,
                page: this.currentPage,
                size: this.pageSize
            };
            reqData['startDate'] = this.dateRange[0];
            reqData['endDate'] = this.dateRange[1];
            this.searching = true;
            let self = this;
            util.ajax.post("/sell/order/all/list", reqData)
                .then((response) => {
                    self.searching = false;
                    self.tabData = response.data.data;
                    self.totalCount = response.data.count;
                })
                .catch((error) => {
                    self.searching = false;
                    util.errorProcessor(self, error);
                });
            
        },
        showReviewDetail (orderId) {
            this.showOrderDetailView = true;
            this.showDetailViewId = orderId;
        },
        showOrderDetailViewClose () {
            this.showOrderDetailView = false;
        },
        pageChange (data) {
            this.currentPage = data;
            this.refreshTableData();
        },
        openShowShipDetailView (order) {
            this.selectedOrder = order;
            this.shipDetailTitle = '订单运输记录 - ' + order.orderNumber;
            this.showShipDetailView = true;
        },
        ShowShipDetailViewClose () {
            this.showShipDetailView = false;
        },
        showPayment(order) {
            this.selectedOrder = order;
            this.paymentModal = true;
            this.paymentModalTitle = '添加支付记录 - ' + order.orderNumber;
        },
        onPaymentUpdated(data) {
            this.refreshTableData();
        }
    }
};
</script>

<style >
.sellOrderQueryForm .ivu-form-item {
    margin-bottom: 5px;
}
.sellOrderTbl .ivu-btn-group {
    display: none;
}
.sellOrderTbl .ivu-table-row-hover .ivu-btn-group {
    display: block;
}
</style>


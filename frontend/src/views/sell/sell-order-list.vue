<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
        <div class="search-div">
            <Row >
                <Form ref="searchForm" :model="searchFormItem" :label-width="90">
                    
                    <Row type="flex" justify="center">
                        <Col span="8" >
                            <FormItem label="客户">
                                <customer-select size="small" v-model="searchFormItem.customerId" ></customer-select>
                            </FormItem>
                        </Col>
                        <Col span="12" >
                            <FormItem label="制单日期">
                                <DatePicker size="small" v-model="dateRange" type="daterange" placement="bottom-end" placeholder="制单日期" style="width:180px"></DatePicker>
                            </FormItem>
                        </Col>
                        <Col span="4"></Col>
                    </Row>
                    <Row type="flex" justify="center">
                        <Col span="8" >
                            <FormItem label="销售单号">
                                <Input  size="small" type="text" v-model="searchFormItem.orderNumber" ></Input>
                            </FormItem>
                        </Col>
                        <Col span="8" >
                            <FormItem label="销售员" >
                                <sale-select size="small" v-model="searchFormItem.salerId"></sale-select>
                            </FormItem>
                        </Col>
                        <Col span="3" offset="1" >
                            <Button size="small" icon="ios-search" type="primary" :loading="searching" @click="searchBtnClick">查询</Button>
                        </Col>
                        <Col span="4"></Col>
                    </Row>
                </Form>
            </Row>
        </div>
        <div class="table-div">
            <Row type="flex" justify="center" align="middle" >
                <Table border highlight-row :columns="tabColumns" :data="tabData" 
                        :loading="searching"  
                        no-data-text="点击上方查询按钮获取数据"
                        ref="table" style="width: 100%;" size="small">
                </Table>
            </Row>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
        </div>

        <Modal v-model="showOrderDetailView" :width="70" :mask-closable="false" title="销售订单详细信息">
            <review-detail :sellOrderId="showDetailViewId" @on-cancel="showOrderDetailViewClose"></review-detail>
            <div slot="footer"></div>
        </Modal>

        <Modal v-model="showShipDetailView" :width="75" :mask-closable="false" :title="shipDetailTitle" @on-cancel="ShowShipDetailViewClose">
            <sell-order-ship :orderId="shipOrderId" ></sell-order-ship>
            <div slot="footer"></div>
        </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import reviewDetail from "./review-detail.vue";
import sellOrderShip from "./sell-order-ship.vue";
import customerSelect from "@/views/selector/customer-select.vue";
import saleSelect from "@/views/selector/sale-select.vue";

export default {
    name: 'sell-order-all',
    components: {
        reviewDetail,
        sellOrderShip,
        customerSelect,
        saleSelect
    },

    data () {
        return {
            searchFormItem: {
                customerId: '',
                orderNumber: '',
                salerId: ''
            },
            dateRange: [
                moment().add(-1,'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD'),
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
                    width: 170,
                    key: 'orderNumber',
                    align: 'center',
                    sortable: true,
                    fixed: 'left'
                },
                {
                    title: '制单日',
                    key: 'createOrderDate',
                    width: 100,
                    align: 'center',
                    sortable: true,
                    render: (h, params) => {
                        let createOrderDate = params.row.createOrderDate;
                        return h('span', createOrderDate ? moment(createOrderDate).format('YYYY-MM-DD') : '');
                    }
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
                        return h('span', this.statusDescription(params.row.status));
                    }
                },
                {
                    title: '销售员',
                    key: 'salerId',
                    width: 100,
                    align: 'center'
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
                    title: '收款金额',
                    key: 'payAmount',
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
                    title: '补录运输记录',
                    align: 'center',
                    width: 100,
                    fixed: 'right',
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'primary',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    this.openShowShipDetailView(params.row.id, params.row.orderNumber);
                                }
                            }
                        }, '运输记录');
                    }
                }
            ],
            showOrderDetailView: false,
            showDetailViewId: -1,
            totalCount: 0,
            currentPage: 1,
            pageSize: 20,
            showShipDetailView: false,
            shipOrderId: -1,
            shipDetailTitle: ''
        };
    },
    methods: {

        statusDescription(data) {
            let result = '';
            switch (data) {
                case 'INIT':
                    result = '制单初始';
                    break;
                case 'QUALITY_CHECKED':
                    result = '出库质量审核完成';
                    break;
                case 'SALE_CHECKED':
                    result = '销售审核完成';
                    break;
                default:
                    result = '';
                    break;
            }
            return result;
        },
        searchBtnClick () {
            this.currentPage = 1;
            this.refreshTableData();
        },
        refreshTableData () {
            let reqData = {
                customerId: this.searchFormItem.customerId,
                orderNumber: this.searchFormItem.orderNumber,
                salerId: this.searchFormItem.salerId,
                page: this.currentPage,
                size: this.pageSize
            };
            reqData['startDate'] = this.dateRange[0];
            reqData['endDate'] = this.dateRange[1];
            this.searching = true;
            let self = this;
            util.ajax.post("/sell/order/all/list", reqData)
                .then((response) => {
                    self.tabData = response.data.data;
                    self.totalCount = response.data.count;
                })
                .catch((error) => {
                    util.errorProcessor(self, error);
                });
            this.searching = false;
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
        openShowShipDetailView (orderId, orderNumber) {
            this.shipOrderId = orderId;
            this.shipDetailTitle = '订单运输记录 ->' + orderNumber;
            this.showShipDetailView = true;
        },
        ShowShipDetailViewClose () {
            this.showShipDetailView = false;
        }
    }

};
</script>

<style>
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


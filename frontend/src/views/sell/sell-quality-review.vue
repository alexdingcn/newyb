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
                        <FormItem label="商品">
                            <good-select v-model="query.goodId"></good-select>
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
                    <Col span="8">
                            <Button type="primary" icon="ios-search" @click="querySellOrderList"></Button>
                    </Col>
                </Row>
            </Form>
        </div>
        <div >
            <Table border highlight-row disabled-hover height="250" style="width: 100%" 
                   :columns="orderListColumns" :data="orderList"
				   ref="sellOrderListTable" size="small"
                   @on-row-click="handleSelectBuyOrder"
				   no-data-text="使用右上方输入搜索条件">
			</Table>
        </div>

        <div class="table-div">
            <Row type="flex" justify="start">
                <ButtonGroup size="small">
                    <Button type="success" icon="checkmark-round">审核通过</Button>
                    <Button type="error" icon="close-round">取消审核</Button>
                </ButtonGroup>
            </Row>
            <Table border highlight-row height="300"
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
import goodSelect from "@/views/good/good-select.vue";
import goodExpand from "@/views/good/good-expand.vue";

export default {
    name: 'sell-quality-review',
    components: {
        goodSelect,
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
                goodId: '',
                status: 'INIT',
                salerId: ''
            },
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
                        let itemArr = this.salerList.filters(item => item.id === params.row.salerId);
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
                },
                {
                    title: '运输方式',
                    key: 'shipMethodName',
                    align: "center",
                    width: 90
                },
                {
                    title: '运输工具',
                    key: 'shipToolName',
                    align: "center",
                    width: 90
                }
            ],
            sellGoodList: [],
            sellGoodColumns: [
                {
                    type: "expand",
                    width: 50,
                    render: (h, params) => {
                        return h(goodExpand, {
                        props: {
                            detail: params.row.goods
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
                    width: 100
                },
                {
                    title: '规格',
                    key: 'spec',
                    width: 100
                },
                {
                    title: '生产日期',
                    width: 120,
                    key: 'productData',
                    render: (h, params) => {
                        return moment(params.row.productData).format('YYYY-MM-DD');
                    }
                },
                {
                    title: '有效期至',
                    key: 'expDate',
                    width: 120,
                    render: (h, params) => {
                        return moment(params.row.expDate).format('YYYY-MM-DD');
                    }
                },
                {
                    title: '单位',
                    key: 'unitName',
                    width: 90
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
                    key: 'status'
                },
                {
                    title: '审核人',
                    width: 100,
                    key: 'reviewUserName'
                },
                {
                    title: '审核日期',
                    width: 120,
                    key: 'reviewDate'
                },
                {
                    title: '审核结论',
                    width: 180,
                    key: 'reviewComment'
                }
            ],
            totalCount: 0,
            totalAmount: 0
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

        },
        handleSelectBuyOrder(data) {
            console.log(data);
        },
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


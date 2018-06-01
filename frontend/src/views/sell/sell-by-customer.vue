<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Card>
        <p slot="title">
            客户销售情况汇总
        </p>
        <Row >
            <Form ref="searchForm" :model="searchFormItem" :label-width="90">
                <Row type="flex">
                    <i-col span="8" >
                        <FormItem label="客户">
                            <customer-select v-model="searchFormItem.customerId" ></customer-select>
                        </FormItem>
                    </i-col>
                    <i-col span="5" >
                        <FormItem label="订单日期">
                            <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="制单日期" style="width:200px"></DatePicker>
                        </FormItem>
                    </i-col>
                    <i-col span="3" offset="1" >
                        <Button icon="ios-search" type="primary" :loading="searching" @click="searchBtnClick">查询</Button>
                    </i-col>
                </Row>
            </Form>
        </Row>

        <Table border highlight-row :columns="tabColumns" :data="tabData" 
                :loading="searching"  
                no-data-text="点击上方查询按钮获取数据"
                ref="table" class="margin-top-10" size="small">
        </Table>
    </Card>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import customerSelect from "@/views/selector/customer-select.vue";

export default {
    name: 'sell-by-customer',
    components: {
        customerSelect
    },

    data () {
        return {
            searchFormItem: {
                customerId: '',
            },
            dateRange: [
                moment().add(-1,'M').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD'),
            ],
            searching: false,
            tabData: [],
            tabColumns: [
                {
                    title: '客户',
                    key: 'customerName',
                    width: 200,
                    align: 'center',
                    render: (h, params) => {
                        var self = this;
                        return h('Button', {
                            props: {
                                type: 'text',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    let query = {
                                        customer_id: params.row.customerId,
                                        start_date: moment(this.dateRange[0]).format('YYYY-MM-DD'),
                                        end_date: moment(this.dateRange[1]).format('YYYY-MM-DD')
                                    };
                                    console.log(self.dateRange);
                                    this.$router.push({
                                        name: 'sell_list',
                                        query: query
                                    });
                                }
                            }
                        }, params.row.customerName);
                    }
                },
                {
                    title: '总订单数',
                    key: 'orderCount',
                    width: 100
                },
                {
                    title: '总商品数',
                    key: 'goodsCount',
                    width: 100
                },
                {
                    title: '总成交金额',
                    key: 'amount',
                    width: 100
                },
                {
                    title: '累计应收金额',
                    key: 'customerReceivable',
                    width: 120,
                    render: (h, params) => {
                        return h('span', params.row.customerReceivable && params.row.customerReceivable < 0 ? -params.row.customerReceivable : '');
                    }
                },
                {
                    title: '累计应付金额',
                    key: 'customerPayable',
                    width: 120,
                    render: (h, params) => {
                        return h('span', params.row.customerReceivable && params.row.customerReceivable > 0 ? params.row.customerReceivable : '');
                    }
                },
                {
                    title: '新增应收金额',
                    key: 'newReceivable',
                    width: 120
                },
                {
                    title: '周期内回款',
                    key: 'newReceivable',
                    width: 120
                }
            ]
        };
    },
    mounted() {
        this.refreshTableData();
    },
    methods: {
        searchBtnClick () {
            this.refreshTableData();
        },
        refreshTableData () {
            let reqData = {
                customerId: this.searchFormItem.customerId
            };
            reqData['startDate'] = this.dateRange[0];
            reqData['endDate'] = this.dateRange[1];
            this.searching = true;
            let self = this;
            util.ajax.post("/sell/order/customer/list", reqData)
                .then((response) => {
                    self.searching = false;
                    self.tabData = response.data;
                })
                .catch((error) => {
                    self.searching = false;
                    util.errorProcessor(self, error);
                });
        }
    }

};
</script>

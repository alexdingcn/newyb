<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Card>
        <p slot="title">
            商品销售情况汇总
        </p>
        <Row >
            <Form ref="searchForm" :model="searchFormItem" :label-width="90">
                <Row type="flex">
                    <i-col span="8" >
                        <FormItem label="商品">
                            <goods-select v-model="searchFormItem.goodsId" ></goods-select>
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
import goodSelect from "@/views/selector/good-select.vue";

export default {
    name: 'sell-by-goods',
    components: {
        goodSelect
    },

    data () {
        return {
            searchFormItem: {
                goodsId: '',
            },
            dateRange: [
                moment().add(-1,'M').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD'),
            ],
            searching: false,
            tabData: [],
            tabColumns: [
                {
                    title: '商品',
                    key: 'goodsName',
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
                                        goods_id: params.row.goodsId,
                                        start_date: moment(this.dateRange[0]).format('YYYY-MM-DD'),
                                        end_date: moment(this.dateRange[1]).format('YYYY-MM-DD')
                                    };
                                    this.$router.push({
                                        name: 'sell_list',
                                        query: query
                                    });
                                }
                            }
                        }, params.row.goodsName);
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
                goodsId: this.searchFormItem.goodsId
            };
            reqData['startDate'] = this.dateRange[0];
            reqData['endDate'] = this.dateRange[1];
            this.searching = true;
            let self = this;
            util.ajax.post("/sell/order/goods/list", reqData)
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

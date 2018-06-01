<style lang="less">
    @import "../../styles/common.less";
</style>

<template>
    <Card>
        <p slot="title">
            出库明细列表
        </p>
        <Row >
            <Form ref="searchForm" :model="searchFormItem" :label-width="90">
                <Row type="flex" justify="center">
                    <i-col span="5" >
                        <FormItem label="仓库点" prop="warehouseId">
                            <warehouse-select v-model="searchFormItem.warehouseId"></warehouse-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6" >
                        <FormItem label="出库日期">
                    <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="制单日期" style="width:200px"></DatePicker>
                </FormItem>
                    </i-col>
                    <i-col span="5" >
                         <FormItem label="出库类型">
                             <Select v-model="searchFormItem.outType" prop="outType">
                                 <Option v-for="option in storeOptions" :value="option.id" :label="option.name" :key="option.id">
                                     {{option.name}}
                                 </Option>
                             </Select>
                        </FormItem>
                    </i-col>
                    <i-col span="3" offset="1" >
                    <Button icon="ios-search" type="primary" :loading="searching" @click="searchBtnClick">查询</Button>
                      <Button icon="ios-download-outline" type="primary"  @click="exportExcel">导出</Button>

                    </i-col>

                </Row>

            </Form>
        </Row>

        <Table border highlight-row :columns="tabColumns" :data="tabData" id="outDetailtable"
               :loading="searching"
               no-data-text="点击上方查询按钮获取数据"
               ref="table" class="margin-top-10" size="small">
        </Table>
        <!--<Row type="flex" justify="end" class="margin-top-10">-->
            <!--<Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total-->
                  <!--@on-change="pageChange">-->
            <!--</Page>-->
        <!--</Row>-->

    </Card>
</template>

<script>
    import util from "@/libs/util.js";
    import moment from 'moment';
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import goodsSpecTags from '@/views/goods/goods-spec-tabs.vue';
    export default {
        name: 'out-order-list',
        components: {
            warehouseSelect,
            goodsSpecTags
        },
        data () {
            return {
                storeOptions: [{ id: "ALL", name: "全部" },{ id: "SELL_BATCH", name: "批发出库" },{ id: "CHECK_LOSE", name: "盘亏出库" },{ id: "MOVE_OUT", name: "转库出库" },{ id: "DIRECT_OUT", name: "直调出库" },{ id: "DAMAGE_OUT", name: "破损出库" }],
                searchFormItem: {
                    warehouseId:'',
                    outType:'ALL'
            },
            dateRange: [
                    this.$route.query.start_date ? this.$route.query.start_date : moment().add(-1,'w').format('YYYY-MM-DD'),
                    this.$route.query.end_date ? this.$route.query.end_date : moment().format('YYYY-MM-DD'),
                ],
                searching: false,
                tabData: [],
                tabColumns: [

                    {
                        type: "index",
                        align: "center",
                        width: 100
                    }
                    ,
                    {
                        title: "状态",
                        align: "center",
                        key: "status",
                        width: 120
                    },
                    {
                        title: "出库类型",
                        align: "center",
                        key: "refTypeName",
                        width: 120
                    }, {
                        title: "出库仓库",
                        align: "center",
                        key: "warehouseName",
                        width: 120
                    },
                    {
                      title: "出库日期",
                      align: "center",
                      key: "checkDate",
                      width: 160,
                        render: (h, params) => {
                            let checkDate = params.row.checkDate;
                            return h('span', checkDate ? moment(checkDate).format('YYYY-MM-DD HH:mm') : '');
                        }
                    },
                    {
                      title: "客户名称",
                      key: "customerName",
                      align: "center",
                      width: 150
                    },
                    {
                        title: "联系电话",
                        key: "customerTel",
                        align: "center",
                        width: 150
                    }
                    ,
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        width: 120
                    },
                      {
                          title: '剂型',
                          key: 'jx',
                          width: 120
                      },
                    {
                        key: 'goodsSpecs',
                        title: '规格',
                        width: 120,
                        render: (h, params) =>　{
                            try{
                                let goodsSpecs=  goodsSpecs=params.row.repertoryInfo.goods.goodsSpecs;
                                return h(goodsSpecTags, {
                                    props: {
                                        tags: goodsSpecs,
                                        color: 'blue'
                                    }
                                });
                            }catch (e) {
                                return h('span','');
                            }

                        }
                    },

                    {
                        title: '生产企业',
                        key: 'factoryName',
                        align: 'center',
                        width: 120
                    },

                    {
                      title: "单位",
                      key: "unitName",
                      align: "center",
                      width: 50
                    },
                      {
                          title: "数量",
                          key: "quantity",
                          align: "center",
                          width: 80
                      },

                      {
                          title: "free",
                          key: "free",
                          align: "center",
                          width: 80
                      },
                      {
                          title: "单价",
                          key: "price",
                          align: "center",
                          width: 80
                      },
                      {
                          title: "disPrice",
                          key: "disPrice",
                          align: "center",
                          width: 80
                      },
                      {
                          title: "数量",
                          key: "amount",
                          align: "center",
                          width: 80
                      },
                      {
                          title: "taxRate",
                          key: "taxRate",
                          align: "center",
                          width: 80
                      }
                ],
                showOrderDetailView: false,
                showDetailViewId: -1,
                totalCount: 0,
                currentPage: 1,
                pageSize: 50,
                showShipDetailView: false,
                shipOrder: {},
                shipDetailTitle: ''
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
            exportExcel () {
                this.$refs.table.exportCsv({
                    filename: '出库记录明细表'
                });
            },
            refreshTableData () {
                let reqData = {
                    warehouseId: this.searchFormItem.warehouseId,
                    page: this.currentPage,
                    size: this.pageSize
                };
                let outTypeList = [];
                if (this.searchFormItem.outType === 'ALL') {
                    outTypeList = ['SELL_BATCH','CHECK_LOSE','MOVE_OUT','DIRECT_OUT','DAMAGE_OUT'];
                }else {
                    outTypeList = [this.searchFormItem.outType];
                }
                reqData['outTypeList'] = outTypeList;
                reqData['startOutDate'] = this.dateRange[0];
                reqData['endOutDate'] = this.dateRange[1];
                this.searching = true;
                let self = this;
                console.log(reqData);
                util.ajax.post("/repertory/out/detaillist", reqData)
                    .then((response) => {
                        self.tabData = response.data;
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
            openShowShipDetailView (order) {
                this.shipOrder = order;
                this.showShipDetailView = true;
            },
            ShowShipDetailViewClose () {
                this.showShipDetailView = false;
            }
        }

    };
    

</script>

<style >
    .ivu-form-item {
        margin-bottom: 5px;
    }
</style>


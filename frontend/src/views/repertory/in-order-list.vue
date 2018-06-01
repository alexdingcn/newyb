<style lang="less">
    @import "../../styles/common.less";
</style>

<template>
    <Card>
        <p slot="title">
            入库明细列表
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
                        <FormItem label="入库日期">
                    <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="制单日期" style="width:200px"></DatePicker>
                </FormItem>
                    </i-col>
                    <i-col span="5" >
                         <FormItem label="入库类型">
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

        <Table border highlight-row :columns="tabColumns" :data="tabData"
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
        name: 'in-order-list',
        components: {
            warehouseSelect,
            goodsSpecTags
        },

        data () {
            return {
                storeOptions: [{ id: "ALL", name: "全部" },{ id: "BUY_ORDER", name: "采购入库" },{ id: "BUY_DIRECT", name: "直调入库" },{ id: "CHECK_SURPLUS", name: "盘盈入库" },{ id: "MOVE_IN", name: "转库入库" },{ id: "DIRECT_IN", name: "直调入库" }],
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
                    },
                    // {
                    //     title: "状态",
                    //     align: "center",
                    //     key: "inStatus",
                    //     width: 120
                    // },
                    {
                        title: "入库仓库",
                        align: "center",
                        key: "warehouseName",
                        width: 120
                    }, {
                      title: "入库日期",
                      align: "center",
                      key: "checkDate",
                      width: 160,
                        render: (h, params) => {
                            let checkDate = params.row.repertoryIn.receiveDate;
                            return h('span', checkDate ? moment(checkDate).format('YYYY-MM-DD') : '');
                        }
                    },
                    {
                        title: "供应商ID",
                        align: "center",
                        key: "supplierId",
                        width: 120
                    },
                    {
                        title: "来货单位",
                        align: "center",
                        key: "supplierName",
                        width: 120
                    },
                    {
                        title: "供应商代表",
                        align: "center",
                        key: "supplierContactName",
                        width: 120
                    },
                    {
                        title: "货号",
                        align: "center",
                        key: "goodsNo",
                        width: 120

                    },
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        width: 120
                    }, {
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
                                let goodsSpecs = params.row.goods.goodsSpecs ? params.row.goods.goodsSpecs : [];
                                return h(goodsSpecTags, {
                                    props: {
                                        tags: goodsSpecs,
                                        color: 'blue'
                                    }
                                });
                            }catch (e) {
                                return h('span', '');
                            }

                        }
                    },
                    {
                        title: '生产企业',
                        key: 'factoryName',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            try{
                                let factoryName = params.row.goods.factoryName;
                                return h('span', factoryName ? factoryName : '');
                            }catch (e) {
                                return h('span', '');
                            }
                        }
                    },
                    {
                        title: '基药',
                        key: 'base_med_id',
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
                        key: "receiveQuality",
                        align: "center",
                        width: 80
                    },
                    {
                        title: "单价",
                        key: "price",
                        align: "center",
                        width: 100
                    },

                    {
                        title: "金额",
                        key: "",
                        align: "center",
                        width: 100
                    },

                    {
                      title: "批号",
                      key: "batchCode",
                      align: "center",
                      width: 150
                    },
                    {
                        title: "有效期至",
                        key: "expDate",
                        align: "center",
                        width: 120,
                        render: (h, params) => {
                            let expDate = params.row.expDate;
                            return h('span', expDate ? moment(expDate).format('YYYY-MM-DD ') : '');
                        }
                    },
                    {
                        title: "现库存数",
                        key: "",
                        align: "center",
                        width: 150
                    },
                    {
                        title: "批准文号",
                        key: "cert1No",
                        align: "center",
                        width: 150,
                        render: (h, params) => {
                            try{
                                let cert1No = params.row.goods.cert1No;
                                return h('span', cert1No ? cert1No: '');
                            }catch (e) {
                                return h('span','');
                            }

                        }
                    }, {
                        title: "入库方式",
                        align: "center",
                        key: "refTypeName",
                        width: 120
                    },
                    {
                        title: "采购员",
                        key: "",
                        align: "center",
                        width: 100
                    },
                    {
                        title: "制单人",
                        key: "createBy",
                        align: "center",
                        width: 100
                    },
                    {
                        title: "备注",
                        key: "",
                        align: "center",
                        width: 100
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
                    filename: '入库记录明细表'
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
                    outTypeList = ['BUY_ORDER','BUY_DIRECT','CHECK_SURPLUS','MOVE_IN','DIRECT_IN'];
                }else {
                    outTypeList = [this.searchFormItem.outType];
                }
                reqData['outTypeList'] = outTypeList;
                reqData['startOutDate'] = this.dateRange[0];
                reqData['endOutDate'] = this.dateRange[1];
                this.searching = true;
                let self = this;
                console.log(reqData);
                util.ajax.post("/repertory/in/detaillist", reqData)
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

<style>
    .ivu-form-item {
        margin-bottom: 5px;
    }
</style>


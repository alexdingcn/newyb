<template>
    <div>
        <Card>
            <p slot="title">盘点表确认</p>
            <div slot="extra">
                <ButtonGroup>
                    <Button size="small" type="primary" icon="ios-search" :loading="orderLoading" @click="refreshOrder">查询</Button>
                </ButtonGroup>
                <ButtonGroup>
                    <Button size="small" type="primary" :loading="orderLoading" icon="android-checkbox-outline"  @click="checkPassForm">审核</Button>
                </ButtonGroup>
            </div>

            <Form ref="searchForm" :model="query" :label-width="100">
                <Row type="flex" justify="start">
                    <FormItem label="登记日期">
                        <DatePicker size="small" v-model="dateRange" type="daterange" placement="bottom-start" placeholder="盘点日期" style="width:180px"></DatePicker>
                    </FormItem>
                    <!--<FormItem label="仓库">-->
                        <!--<warehouse-select v-model="query.warehouseId" size="small"></warehouse-select>-->
                    <!--</FormItem>-->
                    <FormItem label="状态">
                        <Select size="small" v-model="query.formState" placeholder="状态">
                            <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                        </Select>
                    </FormItem>
                </Row>
            </Form>
            <div>
                <Table ref="orderTable" border highlight-row disabled-hover height="250" style="width: 100%"
                       :columns="orderListColumns" :data="orderList" size="small"
                       :loading="orderLoading"
                       @on-row-click="handleSelectOrder"
                       no-data-text="输入查询条件, 点击上方查询按钮进行查询">
                </Table>
            </div>

            <div class="detail-div">
                <Row type="flex" justify="end">
                    <ButtonGroup>
                    </ButtonGroup>
                </Row>

                <Table border highlight-row height="300" :loading="detailLoading"
                       :columns="detailColumns" :data="detailList" size="small"
                       ref="detailTable" style="width: 100%;"
                       no-data-text="点击上方订单后查看明细">
                    <div slot="footer">
                    </div>
                </Table>
            </div>
        </Card>
    </div>
</template>

<script>
    import util from "@/libs/util.js";
    import moment,{ isMoment } from 'moment';
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import goodSelect from "@/views/selector/good-select.vue";
    import table2excel from '@/libs/table2excel.js';

    export default {
        name: 'store_check_index',
        props: {
            chooseModal: {
                type: Boolean,
                default: false
            }
        },
        components: {
            warehouseSelect,
            goodSelect,
        },
        data() {
            return {
                statusOptions: [
                    {key: '', name: '所有'},
                    {key: 0, name: '待审核'},
                    {key: 1, name: '已审核'}
                ],
                checkTypeOptions:[{ id: 0, name:'库存盘点'},{ id: 1, name:'直接盘库'},{ id: 2, name:'单品盘点'}],
                query: {
                    warehouseId: '',
                    supplierId: '',
                    status: 'CHECKING'
                },
                dateRange: [
                    moment().add(-1,'w').format('YYYY-MM-DD'),
                    moment().format('YYYY-MM-DD')
                ],
                storeCheck:{},
                orderLoading: false,
                currEditLocationRow: {},
                currDditLocationIndex: '',
                locationModal: false,
                orderList: [],
                orderListColumns: [
                    {
                        title: '序号',
                        type: 'index',
                        width: 80
                    },
                    {
                        title: '状态',
                        align: 'center',
                        key: 'formState',
                        width: 100,
                        render: (h, params) => {
                            let state  = params.row.formState;
                            if (0==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '待审核');
                            }else if (1==state) {
                                return h('Tag', {props: {color: 'green'}}, '已审核');
                            }

                        }
                    },
                    {
                        title: '盘点表编号',
                        align: 'center',
                        key: 'formNo',
                        width: 200
                    },

                    {
                        title: '仓库',
                        key: 'warehouseName',
                        align: 'center',
                        width: 150
                    },
                    {
                        title: '制单人',
                        key: 'createBy',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '登记时间',
                        key: 'createTime',
                        align: 'center',
                        width: 150,
                        render: (h, params) => {
                            return h('span', moment(params.row.createTime).format('YYYY-MM-DD'));
                        }
                    },
                    {
                        title: '审核人',
                        key: 'updateBy',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '审核时间',
                        key: 'updateTime',
                        align: 'center',
                        width: 150,
                      render: (h, params) => {
                            return h('span', moment(params.row.updateTime).format('YYYY-MM-DD'));
                        }
                    },
                    {
                        title: '备注',
                        key: 'checkNote',
                        align: 'center',
                        width: 300
                    }
                ],
                currentChooseOrder: {},
                detailLoading: false,
                detailList: [],
                detailColumns: [
                    {
                        title: "序号",
                        type: 'index',
                        width: 60
                    },
                    {
                        title: '状态',
                        key: 'checkStatus',
                        align: 'center',
                        width: 120,
                        render: (h, params) =>{
                            let state = params.row.checkStatus;
                            if (state==undefined) {
                                return h('Tag', {props:{ color:'yellow'}}, '待处理');
                            }else if(0==state){
                                return h('Tag', {props:{ color:'blue'}}, '正常');
                            }else if(1==state){
                                return h('Tag', {props:{ color:'red'}}, '盘盈');
                            }else if(-1==state){
                                return h('Tag', {props:{ color:'red'}}, '盘亏');
                            }
                        }
                    },
                    // {
                    //     title: '货号',
                    //     align: 'center',
                    //     key: 'code',
                    //     width: 160
                    // },
                    {
                        title: '品名',
                        align: 'center',
                        key: 'goodsName',
                        width: 160
                    },
                    {
                        title: '剂型',
                        key: 'jx',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '规格',
                        key: 'spec',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '产地',
                        key: 'origin',
                        align: 'center',
                        width: 80
                    },


                    {
                        title: '生产企业',
                        key: 'factoryName',
                        align: 'center',
                        width: 160
                    },
                    {
                        title: '单位',
                        key: 'unitName',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '库存数量',
                        key: 'accLimit',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '盘点数量',
                        key: 'checkLimit',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '批号',
                        key: 'batchCode',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '生产日期',
                        key: 'productDate',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return h('span', moment(params.row.productDate).format('YYYY-MM-DD'));
                        }
                    },
                    {
                        title: '有效期至',
                        key: 'expDate',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return h('span', moment(params.row.expDate).format('YYYY-MM-DD'));
                        }
                    },
                    {
                        title: '单价',
                        key: 'price',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '货位号',
                        key: 'location',
                        align: 'center',
                        width: 80
                    }
                ],
                currChooseDetail: {},
                totalAmount: 0,
                totalReceiveCount: 0,
                totalInCount: 0,
                totalRightCount: 0,
                totalErrorCount: 0,
                totalSurveyQuality: 0,
                surveyModal: false,
                checkDetail: false,
                checkFormItem: {},
                checkModal: false,
                checkFileNo: '',
                checkFileModal: false
            }
        },
        mounted () {
        this.refreshOrder();
        },
        activated () {
        },
        watch: {
        },
        methods: {
            refreshOrder() {
                var self = this;
                let reqData = {
                    formState:this.query.formState,
                    startDate:this.dateRange[0],
                    endDate:  this.dateRange[1]
                };
                this.repertoryCheckItems = [];
                self.orderLoading = true;
                util.ajax.post('/repertory/check/orderPartDoList',reqData)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.orderLoading = false;
                            self.orderList = response.data.checkPartList;
                        }
                    })
                    .catch(function (error) {
                        self.orderLoading = false;
                        util.errorProcessor(this, error);
                    });

                this.currentChooseOrder = {};
                this.currChooseDetail = {};
                this.detailList = [];
            },
            handleSelectOrder(rowData) {
                if (!rowData || !rowData.id) {
                    this.currentChooseOrder = {};
                    this.detailList = [];
                    return;
                }
                this.currentChooseOrder = rowData;
                this.reloadOrderDetail();
            },

            surveyClose() {
                this.surveyModal = false;
            },
            surveySuccess() {
                this.surveyModal = false;
                this.reloadOrderDetail();
            },

            reloadOrderDetail() {
                var self=this;
                this.detailLoading = true;
                util.ajax.post('/repertory/check/getCheckFormDetailList?formId='+this.currentChooseOrder.id ).then(function (response) {
                    self.detailLoading = false;
                    let data = response.data;
                    if (data) {
                        self.detailList = data.checkDetailList;
                        this.currChooseDetail = {};
                        }
                    })
                    .catch((error) => {
                        self.detailLoading = false;
                    });

            },  checkPassForm () {

                this.$Modal.confirm({
                    title: '确认此记录符合盘点的真实情况？',
                    content: '<p>确认通过' +this.currentChooseOrder.formNo + '?</p>',
                    onOk: () => {
                        var self=this;
                         util.ajax.post('/repertory/check/checkFormPass',this.currentChooseOrder ).then(function (response) {
                            self.detailLoading = false;
                            if (response.status === 200 ) {
                                self.$Message.info('审核成功!');
                                for(let i=0; i<self.orderList.length; i++) {
                                    let row = self.orderList[i];
                                    if(self.currentChooseOrder.id === row.id) {
                                        row.formState =response.data.RepertoryCheckForm.formState;
                                        break;
                                    }
                                }
                            }
                        }).catch((error) => {
                            self.detailLoading = false;
                         });
                    },
                    onCancel: () => {
                    }
                });
            }
        }
    }
</script>

<style >
    .ivu-form-item {
        margin-bottom: 5px;
    }
    .detail-div {
        margin-top: 10px;
    }
    .detail-count-content {
        margin-left: 10px;
    }
    .detail-count-content-b {
        margin-left: 40px;
    }
    .add-goods-class {
        margin-left: 10px;
        width: 300px;
    }
</style>

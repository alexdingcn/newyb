<template>
    <div>
        <Card>
            <p slot="title">盘点制单</p>
            <div slot="extra">
                <ButtonGroup>
                    <Button size="small" type="primary" icon="ios-search" :loading="orderLoading" @click="refreshOrder">查询</Button>
                </ButtonGroup>
                <ButtonGroup>
                    <Button size="small" type="primary" :loading="orderLoading" icon="android-checkbox-outline"  @click="getInfo4Pass">审核</Button>
                </ButtonGroup>
            </div>

            <Form ref="searchForm" :model="query" :label-width="100">
                <Row type="flex" justify="start">
                    <FormItem label="盘点日期">
                        <DatePicker size="small" v-model="dateRange" type="daterange" placement="bottom-start" placeholder="盘点日期" style="width:180px"></DatePicker>
                    </FormItem>
                    <FormItem label="仓库">
                        <warehouse-select v-model="query.warehouseId" size="small"></warehouse-select>
                    </FormItem>
                    <FormItem label="类型">
                        <!--<supplier-select v-model="query.supplierId" size="small"></supplier-select>-->
                        <Select v-model="query.checkType" size="small"  prop="checkType"  >
                            <Option v-for="option in checkTypeOptions" :value="option.id" :label="option.name" :key="option.id">
                                {{option.name}}
                            </Option>
                        </Select>
                    </FormItem>

                    <FormItem label="状态">
                        <Select size="small" v-model="query.state" placeholder="状态">
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
                        <!--<Button type="info" size="small" icon="android-bulb" @click="samplingSurveyBtnClick"></Button>-->
                        <!--<Button type="success" size="small" icon="android-checkbox-outline"  @click="exportExcel()">导出excel</Button>-->
                        <!--<Button type="error" size="small" icon="close" @click="removeDetail">删除一条</Button>-->
                    </ButtonGroup>
                </Row>

                <Table border highlight-row height="300" :loading="detailLoading"
                       :columns="detailColumns" :data="detailList" size="small"
                       ref="detailTable" style="width: 100%;"
                       @on-row-click="handleSelectDetail"
                       no-data-text="点击上方订单后查看明细">
                    <div slot="footer">
                    </div>
                </Table>
            </div>
        </Card>

        <Modal v-model="passModalShow" width="500" :mask-closable="false">
            <p slot="header">
                <Icon type="ios-plus-outline"></Icon>
                <span>盘点计划执行信息</span>
            </p>
            <div >
                <Row>本次盘点物品数量共计：   {{ checkPass.subAmount }}件</Row>
                <Row>本次盘点物品金额共计： {{ checkPass.subMoney }}元</Row>
                <Row><h2>尚未盘点物品：{{ checkPass.unCheckAmount }}件</h2></Row>
                <Row>盘盈数量：{{ checkPass.checkMoreAmount }}件</Row>
                <Row>盘盈金额：{{ checkPass.checkMoreMoney }}元</Row>
                <Row>盘亏数量：{{ checkPass.loseAmount }}件</Row>
                <Row>盘亏金额：{{ checkPass.loseMoney }}元</Row>
                <Row>审核结果<Input span="8" v-model="checkPass.state" /></Row>
                <Row>备注<Input span="8" v-model="checkPass.comment" /></Row>
                <Row>总经理：<Input span="8" v-model="checkPass.manager" /></Row>
                <Row>总经理意见：<Input v-model="checkPass.managerNote" /></Row>
                <Row>财务：<Input v-model="checkPass.finance" /></Row>
                <Row>财务意见：<Input v-model="checkPass.financeNote" /></Row>
            </div>
            <div slot="footer">
                <Button type="primary" @click="handlePlanPass">提交</Button>
            </div>
        </Modal>
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
                passModalShow:false,
                checkPass:{},
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
                        key: 'state',
                        width: 120,
                        render: (h, params) => {
                            let state  = params.row.state;
                            if (0==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '待审核');
                            }else if (1==state) {
                                return h('Tag', {props: {color: 'green'}}, '已审核');
                            }

                        }
                    },
                    {
                        title: '盘点单号',
                        align: 'center',
                        key: 'checkCode',
                        width: 160
                    },
                    {
                        title: '盘点类型',
                        key: 'checkType',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            let checkType  = params.row.checkType;
                            if (0==checkType) {
                                return h('Tag', {props: {color: 'green'}}, '库存盘点');
                            }else if (1==checkType) {
                                return h('Tag', {props: {color: 'green'}}, '直接盘库');
                            }else if (2==checkType){
                                return h('Tag', {props: {color: 'green'}}, '单品盘点');
                            }
                        }
                    },
                    {
                        title: '仓库',
                        key: 'warehouseName',
                        align: 'center',
                        width: 100
                    },

                    {
                        title: '盘点时间',
                        key: 'checkDate',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.checkDate).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '制单人',
                        key: 'createBy',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '备注',
                        key: 'comment',
                        align: 'center',
                        width: 150
                    },
                    {
                        title: '总经理',
                        key: 'manager',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '总经理意见',
                        key: 'managerNote',
                        align: 'center',
                        width: 150
                    },
                    {
                        title: '财务经理',
                        key: 'finance',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '财务经理意见',
                        key: 'financeNote',
                        align: 'center',
                        width: 150
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
                            return moment(params.row.productDate).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '有效期至',
                        key: 'expDate',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.expDate).format('YYYY-MM-DD');
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
        watch: {

        },
        methods: {
            refreshOrder() {
                var self = this;
                let reqData = {
                    checkType:  this.query.checkType,
                    warehouseId: this.query.warehouseId,
                    state:this.query.state,
                    startDate:this.dateRange[0],
                    endDate:  this.dateRange[1]
                };
                this.repertoryCheckItems = [];
                self.orderLoading = true;
                util.ajax.post('/repertory/check/list',reqData)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.orderLoading = false;
                            self.orderList = response.data.data;
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

            handleSelectDetail(rowData) {
                if (!rowData || !rowData.id) {
                    this.currChooseDetail = {};
                }else {
                    this.currChooseDetail = rowData;
                }
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
                util.ajax.post('/repertory/check/orderinfoList?checkPlanId='+this.currentChooseOrder.id )
                    .then(function (response) {
                        self.detailLoading = false;
                        let data = response.data;
                        if (data) {
                            self.detailList = data.checkDetailList;
                            this.currChooseDetail = {};
                        }
                    })
                    .catch((error) => {
                        self.detailLoading = false;
                        // util.errorProcessor(self, error);
                    });

            },getInfo4Pass(){
                var self=this;
                if(this.currentChooseOrder.id!='' && this.currentChooseOrder.id>0){
                    util.ajax.post('/repertory/check/getInfo4Pass?checkPlanId='+this.currentChooseOrder.id )
                        .then(function (response) {
                            if (response.status === 200 ) {
                                self.checkPass.subAmount = response.data.subAmount;
                                self.checkPass.subMoney = response.data.subMoney;
                                self.checkPass.unCheckAmount = response.data.unCheckAmount;
                                self.checkPass.checkMoreAmount = response.data.checkMoreAmount;
                                self.checkPass.checkMoreMoney = response.data.checkMoreMoney;
                                self.checkPass.loseAmount = response.data.loseAmount;
                                self.checkPass.loseMoney = response.data.loseMoney;
                                self.passModalShow=true;
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                }else{
                    self.$Message.error('尚未选择盘点计划');
                }

            },handlePlanPass(){
                var self=this;
                util.ajax.post('/repertory/check/checkPlanPass' ,{
                    planId:this.currentChooseOrder.id+"",
                    state:this.checkPass.state,
                    comment:this.checkPass.comment,
                    manager:this.checkPass.manager,
                    managerNote:this.checkPass.managerNote,
                    finance:this.checkPass.finance,
                    financeNote:this.checkPass.financeNote
                })
                    .then(function (response) {
                        self.repertoryCheckDetailItems = [];
                        self.passModalShow=false;
                        if (response.status === 200 && response.data) {
                            self.$Message.info('审核通过！');

                            self.refreshOrder();
                        }
                    })
                    .catch(error => {
                        self.passModalShow=false;
                        util.errorProcessor(this, error);
                    });
            }


        }
    }
</script>

<style>
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

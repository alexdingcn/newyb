<template>
    <div>
        <Card>
            <p slot="title">再次复核</p>
            <div slot="extra">
                <ButtonGroup>
                    <Button size="small" type="primary" icon="ios-search" :loading="orderLoading" @click="refreshOrder">查询</Button>
                    <Button size="small" type="success" icon="ios-checkmark" @click="checkOneOrderBtn">双人复核</Button>
                    <Button size="small" type="warning"  icon="close" @click="unCheckOneOrderBtn">撤销双人复核</Button>
                    <!--<Button size="small" type="info"  icon="images" @click="showCheckFile">药监采集</Button>-->
                </ButtonGroup>
            </div>

            <Form ref="searchForm" :model="query" :label-width="100">
                <Row type="flex" justify="start">
                    <FormItem label="出库日期">
                        <DatePicker size="small" v-model="dateRange" type="daterange" placement="bottom-start" placeholder="收货日期" style="width:180px"></DatePicker>
                    </FormItem>
                    <FormItem label="出库仓库">
                        <warehouse-select v-model="query.warehouseId" size="small"></warehouse-select>
                    </FormItem>
                    <FormItem label="出库单状态">
                        <Select size="small" v-model="query.status" placeholder="状态">
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
                        <Button type="success" size="small" icon="checkmark" @click="checkOneDetailBtn">双人复核一条</Button>
                        <Button type="warning" size="small" icon="close" @click="unReviewOneDetailBtn">撤销一条</Button>
                        <!--<Button type="success" size="small" icon="android-checkbox-outline" @click="saveOroderDetail">保存详情</Button>-->
                    </ButtonGroup>
                </Row>
                <Table border highlight-row height="300" :loading="detailLoading"
                       :columns="detailColumns" :data="detailList" size="small"
                       ref="detailTable" style="width: 100%;"
                       @on-row-click="handleSelectDetail"
                       no-data-text="点击上方订单后查看明细">
                    <div slot="footer">
                        <h3 class="detail-count-content" >
                            <b>总金额:</b> {{ totalAmount }}
                            <b class="detail-count-content-b">出库数量:</b> {{ totalInCount }}
                        </h3>
                    </div>
                </Table>
            </div>
        </Card>

        <warehouse-location-modal :openModal="locationModal" :warehouseId="currentChooseOrder.warehouseId" @on-ok="chooseLocation" @on-close="locationModalClose"></warehouse-location-modal>
    </div>
</template>

<script>
    import util from "@/libs/util.js";
    import moment,{ isMoment } from 'moment';
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import goodSelect from "@/views/selector/good-select.vue";
    import warehouseLocationModal from "@/views/selector/warehouse-location-modal.vue";

    export default {
        name: 'out-review-next',
        components: {
            warehouseSelect,
            goodSelect,
            warehouseLocationModal
        },
        data() {

            const addWarehouseLocation = (h, location, rowData, index) => {
                let label = location ? location : '';
                return h('div', [
                    h('span', label),
                    h('Button', {
                        props: {
                            type: 'text',
                            size: 'small',
                            icon: 'edit'
                        },
                        on: {
                            'click': () => {
                                this.openChooseLocation(rowData, index);
                            }
                        }
                    })
                ]);
            };

            return {
                statusOptions: [

                    {key: 'REVIEW', name: '复核中'},
                    {key: 'REVIEW_NEXT', name: '双人复核'}
                ],
                query: {
                    warehouseId: '',
                    supplierId: '',
                    status: 'REVIEW'
                },
                dateRange: [
                    moment().add(-1,'w').format('YYYY-MM-DD'),
                    moment().format('YYYY-MM-DD')
                ],
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
                        key: 'status',
                        width: 120,
                        render: (h, params) => {
                            let state = params.row.status;
                            if (state== 'INIT') {
                                return h('Tag', {props:{ color:'red'}}, '待复核');
                            }else if(state=='REVIEW'){
                                return h('Tag', {props:{ color:'yellow'}}, '复核中');
                            }else if(state=='REVIEW_NEXT'){
                                return h('Tag', {props:{ color:'blue'}}, '双人复核');
                            }else if(state=='CHECKED'){
                                return h('Tag', {props:{ color:'green'}}, '已审核');
                            }
                        }
                    },
                    {
                        title: '出库时间',
                        key: 'outDate',
                        render: (h, params) => {
                            let receiveDate = params.row.outDate;
                            return receiveDate ? moment(receiveDate).format("YYYY-MM-DD") : '';
                        }
                    },
                    {
                        title: '出库类型',
                        key: 'refTypeName',
                    },
                    {
                        title: '出库仓库',
                        key: 'warehouseName',
                    },
                    {
                        title: '出库数量',
                        key: 'totalQuantity'
                    },
                    {
                        title: '总计金额',
                        key: 'totalAmount'
                    },
                    {
                        title: '制单人',
                        key: 'createBy'
                    },

                    {
                        title: '系统单号',
                        key: 'refOrderNumber',
                    }

                ],
                currentChooseOrder: {},
                detailLoading: false,
                detailList: [],
                detailColumns: [
                    {
                        title: "序号",
                        type: 'index',
                        width: 80
                    },
                    {
                        title: '状态',
                        key: 'status',
                        align: 'center',
                        width: 120,
                        render: (h, params) =>{
                            let state = params.row.status;
                            if (state==undefined) {
                                return h('Tag', {props:{ color:'red'}}, '待复核');
                            }else if('INIT'==state){
                                return h('Tag', {props:{ color:'red'}}, '待复核');
                            }else if('REVIEW'==state){
                                return h('Tag', {props:{ color:'yellow'}}, '复核中');
                            }else if('REVIEW_NEXT'==state){
                                return h('Tag', {props:{ color:'blue'}}, '双人复核');
                            }else if('CHECKED'==state){
                                return h('Tag', {props:{ color:'green'}}, '已审核');
                            }
                        }
                    },
                    {
                        title: "商品名称",
                        key: 'goodsName',
                        width: 160
                    },
                    {
                        title: "产地",
                        key: 'origin',
                        width: 120
                    },
                    {
                        title: "剂型",
                        key: 'jx',
                        width: 120
                    },
                    {
                        title: "规格",
                        key: 'spec',
                        width: 100
                    },
                    {
                        title: "生产企业",
                        key: 'factoryName',
                        width: 120
                    },
                    {
                        title: "批准文号",
                        key: 'permit',
                        width: 120
                    },

                    {
                        title: "出库数量",
                        key: 'quantity',
                        width: 120
                    },
                    {
                        title: "单位",
                        key: 'unitName',
                        width: 120
                    },
                    {
                        title: '单价',
                        width: 120,
                        key: 'price'
                    },
                    {
                        title: '金额',
                        width: 120,
                        key: 'amount'
                    },
                    {
                        title: "批号",
                        key: 'batchCode',
                        width: 140
                    },
                    {
                        title: "生产日期",
                        key: 'productDate',
                        width: 140,
                        render: (h, params) => {
                            return params.row.productDate ? moment(params.row.productDate).format('YYYY-MM-DD') : '';
                        }
                    },
                    {
                        title: "有效期至",
                        key: 'expDate',
                        width: 140,
                        render: (h, params) => {
                            return params.row.expDate ? moment(params.row.expDate).format('YYYY-MM-DD') : '';
                        }
                    },
                    {
                        title: "存储条件",
                        key: 'storageCondition',
                        width: 100
                    },
                    {
                        title: "特殊药品",
                        key: 'specialManage',
                        width: 120,
                        render (h, params) {
                            let specialManage = params.row.specialManage;
                            if (specialManage) {
                                return h('Tag', {props:{type:'dot', color:'red'}}, '是');
                            }else{
                                return h('Tag', {props:{type:'dot', color:'blue'}}, '否');
                            }
                        }
                    },
                    {
                        title: "库区",
                        key: 'warehouseLocation',
                        width: 140
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
            detailList(data) {
                if(!data || data.length <= 0) {
                    this.totalAmount = 0;
                    this.totalReceiveCount = 0;
                    this.totalInCount = 0;
                    this.totalRightCount = 0;
                    this.totalErrorCount = 0;
                    this.totalSurveyQuality = 0;
                }else {
                    this.totalAmount = data.reduce((total, item) => {return item.amount ? total + item.amount : total + 0}, 0);
                    this.totalReceiveCount = data.reduce((total, item) => {return item.receiveQuality ? total + item.receiveQuality : total + 0}, 0);
                    this.totalInCount = data.reduce((total, item) => {return item.inCount ? total + item.inCount : total + 0}, 0);
                    this.totalRightCount = data.reduce((total, item) => {return item.rightCount ? total + item.rightCount : total + 0}, 0);
                    this.totalErrorCount = data.reduce((total, item) => {return item.errorCount ? total + item.errorCount : total + 0}, 0);
                    this.totalSurveyQuality = data.reduce((total, item) => {return item.surveyQuality ? total + item.surveyQuality : total + 0}, 0);
                }
            }
        },
        methods: {
            refreshOrder() {
                let statusList = [];
                if (this.query.status === 'INIT') {
                    statusList = ['INIT'];
                }else if(this.query.status === 'REVIEW'){
                    statusList = ['REVIEW'];
                }else if(this.query.status === 'REVIEW_NEXT'){
                    statusList = ['REVIEW_NEXT'];
                }else if(this.query.status === 'CHECKED'){
                    statusList = ['CHECKED'];
                }else{
                    statusList=null;
                }
                let reqData = {
                    statusList: statusList,
                    warehouseId: this.query.warehouseId,
                    startReceiveDate: this.dateRange[0],
                    endReceiveDate: this.dateRange[1]
                };
                this.orderLoading = true;
                util.ajax.post("/repertory/out/list", reqData)
                    .then((response) => {
                        this.orderLoading = false;
                        this.orderList = response.data;
                    })
                    .catch((error) => {
                        this.orderLoading = false;
                        util.errorProcessor(this, error);
                    });
                this.currentChooseOrder = {};
                this.currChooseDetail = {};
                this.detailList = [];
            },
            //点击出库单查询明细
            handleSelectOrder(rowData) {
                if (!rowData || !rowData.id) {
                    this.currentChooseOrder = {};
                    this.detailList = [];
                    return;
                }
                this.currentChooseOrder = rowData;
                this.reloadOrderDetail();
                this.checkFileNo = '';
            },
            //点击选中出库单明细
            handleSelectDetail(rowData) {
                if (!rowData || !rowData.id) {
                    this.currChooseDetail = {};
                }else {
                    this.currChooseDetail = rowData;
                }
            },
            //重载明细--同时重载单据状态信息
            reloadOrderDetail() {
                this.detailLoading = true;
                let self=this;
                util.ajax.get('/repertory/out/detail/' + self.currentChooseOrder.id)
                    .then((response) => {
                        this.detailLoading = false;
                        let data = response.data;
                        if (data) {
                            //同时更新单据的状态信息
                            this.detailList = data.detailList;
                            if(data.repertoryOut.status!=self.currentChooseOrder.status){
                                //表单状态发生变化，更新表单
                                this.$Message.warning('出库单状态发生变化，更新表单');
                                this.refreshOrder();
                            }

                            this.currChooseDetail = {};
                        }
                    })
                    .catch((error) => {
                        this.detailLoading = false;
                        util.errorProcessor(this, error);
                    });
            },
            //复核一单
            checkOneOrderBtn() {
                if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
                    this.$Message.warning('请先选择一条需要复核的订单信息');
                    return;
                }
                this.checkFormItem = {
                    orderId: this.currentChooseOrder.id
                }
                util.ajax.put('/repertory/out/set/reviewNext', this.checkFormItem)
                    .then((response) => {
                        this.detailLoading = false;
                        this.$Message.success('复核成功');
                        this.refreshOrder();
                    })
                    .catch((error) => {
                        this.detailLoading = false;
                        util.errorProcessor(this, error);
                    })
            },
            //复核一条明细
            checkOneDetailBtn() {
                if (!this.currChooseDetail || !this.currChooseDetail.id) {
                    this.$Message.warning('请先选择需要复核的商品');
                    return;
                }
                this.checkFormItem = {
                    detailId: this.currChooseDetail.id,
                }
                util.ajax.put('/repertory/out/set/reviewNext', this.checkFormItem)
                    .then((response) => {
                        this.detailLoading = false;
                        this.$Message.success('复核成功');
                        this.reloadOrderDetail();
                    })
                    .catch((error) => {
                        this.detailLoading = false;
                        util.errorProcessor(this, error);
                    })
            },

            unCheckOneOrderBtn() {
                if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
                    this.$Message.warning('请先选择需要取消验证的订单');
                    return;
                }
                util.ajax.get('/repertory/out/set/unReviewNext/'+ this.currentChooseOrder.id)
                    .then((response) => {
                        this.$Message.success('取消成功');
                        this.reloadOrderDetail();
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    })
            },
            unReviewOneDetailBtn() {
                if (!this.currChooseDetail || !this.currChooseDetail.id) {
                    this.$Message.warning('请先选择需要取消验证的订单');
                    return;
                }
                util.ajax.get('/repertory/out/set/unReviewNextDetail/' + this.currChooseDetail.id)
                    .then((response) => {
                        this.$Message.success('取消成功');
                        this.reloadOrderDetail();
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    })
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

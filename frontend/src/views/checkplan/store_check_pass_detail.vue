<style lang="less">
    @import '../../styles/common.less';
    .option-goods-spec {
        float: right;
        color: #999;
    }
    .ivu-form-item {
        margin-bottom: 0px;
    }

    .ivu-table-cell {
        padding-left: 5px;
        padding-right: 5px;
    }
    th .ivu-table-cell {
        width-space: nowrap;
    }

</style>

<template>
    <div>

    <Row>
        <Card>
            <p slot="title" >
                <Icon type="document"></Icon> 库存盘点
            </p>

            <div slot="extra">
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="getInfo4Pass" >审核</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="backPlanIndex" >返回</Button>
                </ButtonGroup>
            </div>
            <Form :label-width="85" :model="storeCheck" ref="storeCheckForm">
                <Row>
                    <Col span="6">
                        <FormItem label="盘点单号" prop="checkCode">
                            {{ storeCheck.checkCode }}
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="制单人" prop="createdBy">
                            {{ storeCheck.createdBy }}
                        </FormItem>
                    </Col>

                    <Col span="6">
                        <FormItem label="盘点时间" prop="checkDate" >
                            {{ storeCheck.checkDate }}
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="当前状态" prop="state">
                            {{ storeCheck.state }}
                        </FormItem>
                    </Col>

                </Row>
                <Row>
                    <Col span="6">
                    <FormItem label="盘点类型" prop="checkType">
                        {{ storeCheck.checkType }}
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem label="盘点仓库" prop="warehouseName">
                        {{ storeCheck.warehouseName }}
                    </FormItem>
                    </Col>

                    <Col span="6">
                    <FormItem label="库区" prop="counterState" >
                        {{ storeCheck.counterState }}
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem label="备注" prop="note">
                        {{ storeCheck.note }}
                    </FormItem>
                    </Col>

                </Row>
                <Row>
                    <Col span="6">
                    <FormItem label="总经理" prop="checkType">
                        {{ storeCheck.manager }}
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem label="总经理意见" prop="managerNote">
                           {{ storeCheck.managerNote }}
                    </FormItem>
                    </Col>

                    <Col span="6">
                    <FormItem label="财务经理" prop="finance">
                        {{ storeCheck.finance }}
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem label="财务经理意见" prop="financeNote">
                        {{ storeCheck.financeNote }}
                    </FormItem>
                    </Col>

                </Row>

                <Table border highlight-row
                       class="margin-top-8"
                       :columns="repertoryColumns" :data="repertoryCheckDetailItems"
                       ref="storeCheckTable" style="width: 100%;" size="small"
                       no-data-text="当前条件下查询，无库存数据！">

                </Table>
            </Form>
        </Card>
    </Row>


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
    import moment from 'moment';
    import util from '@/libs/util.js';
    export default {
        name: 'store_check_do_detail',
        components: {
        },
        data () {
            return {
                saving: false,
                passModalShow:false,
                checkPass:{},
                repertoryCheckDetailItems: [],
                checkTypeOptions:[{ id: 0, name:'库存盘点'},{ id: 1, name:'直接盘库'},{ id: 2, name:'单品盘点'}],
                counterOptions:[{ id: 'ALL', name:'全部'}],
                checkPlanId: null,
                storeCheck: {

                },
            repertoryColumns: [
                    {
                        type: 'index',
                        title: 'id',
                        align: 'center',
                        width: 30
                    },{
                    title: '状态',
                    key: 'checkStatus',
                    align: 'center',
                    width: 80,
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
                        width: 80
                    },
                    {
                        title: '盘点数量',
                        key: 'checkLimit',
                        align: 'center',
                        width: 80
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
                        width: 80,
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

                ]

            };
        },
        methods: {
            init () {
                var self = this;
                let checkPlanId=this.$route.params.checkPlanId.toString();
                let warehouseName=this.$route.params.warehouseName.toString();
                this.checkPlanId=checkPlanId;
                if(checkPlanId>0){
                    util.ajax.post('/repertory/check/orderinfoList?checkPlanId='+checkPlanId )
                        .then(function (response) {
                            self.repertoryCheckDetailItems = [];
                            if (response.status === 200 && response.data) {
                                self.repertoryCheckDetailItems = response.data.checkDetailList;
                                self.storeCheck=response.data.data;

                                let checkType  = self.storeCheck.checkType;
                                if (0==checkType) {
                                    self.storeCheck.checkType="库存盘点";
                                }else if (1==checkType) {
                                    self.storeCheck.checkType="直接盘库";
                                }else if (2==checkType){
                                    self.storeCheck.checkType="单品盘点";
                                }

                                let state  = self.storeCheck.state;
                                if (0==state) {
                                    self.storeCheck.state="待处理";
                                }else if (1==state) {
                                    self.storeCheck.state="处理中";
                                }else if (2==state){
                                    self.storeCheck.state="已审核";
                                }

                                self.storeCheck.warehouseName=warehouseName;
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                    });
                }else{
                    alert("checkPlanId异常"+checkPlanId);
                }
            },
            backPlanIndex(){
                this.$router.push({
                    name: 'store_check_pass_list',
                    params:{}
                });

            },getInfo4Pass(){
                var self=this;
                util.ajax.post('/repertory/check/getInfo4Pass?checkPlanId='+self.checkPlanId )
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



            },handlePlanPass(){
                util.ajax.post('/repertory/check/checkPlanPass' ,{
                    planId:this.checkPlanId,
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
                            self.repertoryCheckDetailItems = response.data.checkDetailList;
                            self.storeCheck=response.data.data;
                        }
                    })
                    .catch(error => {
                        self.passModalShow=false;
                        util.errorProcessor(this, error);
                    });
            }

        },
        mounted () {
            this.init();
        },
        watch: {
            '$route' () {
                this.init();
            }
        }
    };
</script>
<style>

</style>

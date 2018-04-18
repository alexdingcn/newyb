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
    <Row>
        <Card>
            <p slot="title" >
                <Icon type="document"></Icon> 库存盘点
            </p>

            <div slot="extra">

                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="checkPlanPass" >审核通过</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="checkPlanException" >盘点异常修改</Button>
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
</template>
<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';
    export default {
        name: 'store_check_info',
        components: {
        },
        data () {
            return {
                saving: false,
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
                console.log("接受 的数据checkPlanId=="+this.$route.params.checkPlanId.toString())
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

            },checkPlanPass(){

                if( this.checkPlanId>0){
                    alert("this.checkPlanId 开始审核"+this.checkPlanId);
                    util.ajax.post('/repertory/check/getCheckPlanPassJSON',this.storeCheck)
                        .then(function (response) {
                            self.repertoryCheckDetailItems = [];
                            if (response.status === 200 && response.data) {
                                self.$Message.info('盘点单审核成功');
                                alert("返回库存盘点主页store_check_index");
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                }else{
                    alert("checkPlanId异常"+checkPlanId);
                }
            },checkPlanException(){
                if( this.checkPlanId>0){
                    self.$Message.info('盘点单审核成不通过');
                   // alert("this.checkPlanId 审核不通过"+this.checkPlanId);
                }else{
                    alert("checkPlanId异常"+checkPlanId);
                }
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

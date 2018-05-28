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
        white-space: nowrap;
    }
</style>

<template>
    <div>
        <Row>
            <Card>
                <p slot="title" >
                    <Icon type="document"></Icon>
                    执行盘点
                </p>
                <div slot="extra">
                    <ButtonGroup size="small" >
                        <Button type="primary" icon="android-add-circle" @click="getCheckPlan" >载入盘点表</Button>
                        <Button type="success" icon="checkmark" @click="saveCheckPart" >更新盘点表</Button>
                        <Button type="ghost" icon="reply" @click="backPartIndex" >返回</Button>
                    </ButtonGroup>
                </div>

                <Form :label-width="85" :rules="ruleValidate" :model="checkFormInfo" ref="CheckPartDetailForm">
                    <Row>
                        <Col span="6">
                        <FormItem label="盘点表编号" prop="formNo">
                            <Input v-model="checkFormInfo.formNo" size="small" :disabled=true></Input>
                        </FormItem>
                        </Col>
                        <Col span="6">
                        <FormItem label="备注" prop="checkNote">
                            <Input v-model="checkFormInfo.checkNote" size="small"   ></Input>
                        </FormItem>
                        </Col>
                        <Col span="10">
                        <FormItem label="添加已盘物品">
                            <Select
                                    ref="repertorySelect"
                                    filterable
                                    clearable
                                    remote
                                    placeholder="商品名称/拼音"
                                    size="small"
                                    @on-change="onSelectRepertory"
                                    :remote-method="queryRepertory"
                                    :loading="goodsLoading">
                                <Option v-for="option in goodsOptions" :value="option.id" :label="option.name" :key="option.id">
                                    <span class="option-goods-name">{{ option.goodsName }}| </span>
                                    <span class="option-goods-name">{{ option.batchCode }}| </span>
                                    <span class="option-goods-spec">{{ option.accLimit }}| </span>
                                    <span class="option-goods-spec">{{ option.spec }} | {{option.factory}}</span>

                                </Option>
                            </Select>
                        </FormItem>
                        </Col>
                    </Row>
                    <Table border highlight-row
                           class="margin-top-8"
                           height="400"
                           :columns="orderColumns" :data="orderItems"
                           ref="checkOrderTable" style="width: 100%;" size="small"
                           no-data-text="在商品输入框选择后添加"
                           @on-row-dblclick="handleRowDbClick"
                    >
                    </Table>
                </Form>
            </Card>
        </Row>

        <Modal v-model="checkMoreModalShow" width="500" :mask-closable="false">
            <p slot="header">
                <Icon type="ios-plus-outline"></Icon>
                <span>盘盈登记</span>
            </p>
            <div >
                <Row>盘点单ID：<Input span="8" v-model="checkMore.checkPlanId" disabled/></Row>
                <Row>库存ID：<Input span="8" v-model="checkMore.repertoryInfoId" disabled/></Row>
                <Row>名称：<Input span="8" v-model="checkMore.goodsName" /></Row>
                <Row>批号：<Input v-model="checkMore.batchCode" /></Row>
                <Row>盘盈：<Input v-model="checkMore.checkLimit" /></Row>
                <Row>单价：<Input v-model="checkMore.price" /></Row>
                <Row>
                    生产日期：
                    <DatePicker type="datetime" v-model="checkMore.productDate" format="yyyy-MM-dd HH:mm" size="small"/>
                </Row>
                <Row>
                    有效日期：
                    <DatePicker type="datetime" v-model="checkMore.expDate" format="yyyy-MM-dd HH:mm" size="small"/>
                </Row>
                <Row>备注：<Input v-model="checkMore.checkNote" /></Row>
            </div>
            <div slot="footer">
                <Button type="primary" @click="handleAddLocation">登记盘盈信息</Button>
            </div>
        </Modal>


        <Modal v-model="checkPlanListModel" title="盘点计划提取" :mask-closable="false" width="70">
            <check-plan-list  :chooseModal="true" @on-choosed="checkPlanChoose" ></check-plan-list>
            <div slot="footer"></div>
        </Modal>
    </div>

</template>


<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';
    import checkPlanList from "@/views/checkplan/check-plan-list.vue";

    export default {
        name: 'store_check_do_list',
        components: {
            checkPlanList,
        }, data () {
            return {
                saving: false,
                disableGoos:true,  //默认商品信息不可以添加
                checkMoreModalShow: false,
                checkPlanListModel: false,
                checkPlanId:null,
                checkMore: {
                    id: '',
                    goodsId: '',
                    checkPlanId:'',
                    accLimit:null

                },
                goodsOptions:[],
                goodsLoading: false,
                edittingRow: {},
                closeConfirm: false,
                orderItems: [],
                planDetailList:[],
                checkFormInfo:{
                    id:'',
                },
                orderColumns: [
                    {
                        type: 'index',
                        title: '',
                        align: 'center',
                        width: 30
                    },
                    {
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
                        title: '货号',
                        align: 'center',
                        key: 'code',
                        width: 60
                    },
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        align: 'center',
                        width: 150,
                        sortable: true

                    },
                    {
                        title: '批号',
                        key: 'batchCode',
                        align: 'center',
                        width: 150

                    },
                    {
                        title: '产地',
                        key: 'origin',
                        align: 'center',
                        width: 80
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
                        width: 120
                    },{

                        title: '有效期至',
                        key: 'expDate',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return h('span', moment(params.row.expDate).format('YYYY-MM-DD'));
                        }
                    },

                    {
                        title: '库存数量',
                        key: 'accLimit',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '盘点数量',
                        key: 'checkLimit',
                        align: 'center',
                        width: 100,
                        render: (h, params) => {
                            var self = this;
                            if(self.orderItems[params.index]["formStatus"]=="CHECKED"){
                                return h('span', self.orderItems[params.index][params.column.key]);
                            }else{
                                return h('Input', {
                                    props: {
                                        value: self.orderItems[params.index][params.column.key]
                                    },
                                    on: {'on-blur' (event) {
                                            let row = self.orderItems[params.index];
                                            row[params.column.key] = event.target.value;
                                            let checkLimit=event.target.value;
                                            self.checkMore.repertoryInfoId=row.repertoryInfoId;
                                            self.checkMore.id=row.id;
                                            self.checkMore.goodsId=row.goodsId;
                                            self.checkMore.goodsName=row.goodsName;
                                            self.checkMore.checkLimit=checkLimit;
                                            self.checkMore.accLimit=row.accLimit;
                                            self.checkMore.formId=self.checkFormInfo.id;
                                            self.checkMore.formNo=self.checkFormInfo.formNo;
                                            if(checkLimit>row.accLimit){
                                                self.$Modal.confirm({
                                                    title: '盘点数量超过原库存数量,请进行盘盈登记？',
                                                    content: '<p>确认盘盈商品 ' + row.goodsName + '?</p>',
                                                    onOk: () => {
                                                        self.checkMore.batchCode=row.batchCode;
                                                        self.checkMore.price=row.price;
                                                        self.checkMore.expDate=row.expDate;
                                                        self.checkMoreModalShow=true;
                                                    },
                                                    onCancel: () => {
                                                        self.$Message.error("修改或删除此盘点记录");
                                                    }
                                                });
                                            }
                                            else if(checkLimit<row.accLimit){
                                                self.$Modal.confirm({
                                                    title: '注意：盘点数量小于库存数量，确定盘亏吗？',
                                                    content: '<p>确认盘亏商品 ' + row.goodsName + '?</p>',
                                                    onOk: () => {
                                                        util.ajax.post('/repertory/check/doCheckDetail', self.checkMore)
                                                            .then(function (response) {
                                                                if (response.status === 200 && response.data) {
                                                                    row.checkStatus= response.data.planDetail.checkStatus;
                                                                    self.$Message.info('盘点明细保存成功');
                                                                }
                                                                self.saving = false;
                                                            })
                                                            .catch(function (error) {
                                                                console.log(error);
                                                                self.saving = false;
                                                            });

                                                    },
                                                    onCancel: () => {

                                                    }
                                                });

                                            }else {
                                                util.ajax.post('/repertory/check/doCheckDetail', self.checkMore)
                                                    .then(function (response) {
                                                        if (response.status === 200 && response.data) {
                                                            row.checkStatus= response.data.planDetail.checkStatus;
                                                            self.$Message.info('盘点明细保存成功');
                                                        }
                                                        self.saving = false;
                                                    })
                                                    .catch(function (error) {
                                                        console.log(error);
                                                        self.saving = false;
                                                    });

                                            }

                                        }
                                    }
                                });

                            }

                        }
                    },
                    {
                        title: '单位',
                        key: 'unitName',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '价格',
                        key: 'price',
                        align: 'center',
                        width: 80
                    },

                    {
                        title: '整件单位',
                        key: 'packUnitName',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '大件装量',
                        key: 'bigPack',
                        align: 'center',
                        width: 80
                    },{
                        title: '生产企业',
                        key: 'factoryName',
                        align: 'center',
                        width: 160
                    }

                ],
                ruleValidate: {
                    checkType: [
                        { required: true, type: 'number', message: '请选择盘点类型', trigger: 'blur' }
                    ]
                }
            };
        },
        methods: {
            moment: function () {
                return moment();
            },
            initPart(checkPlanId,warehouseName) {
                var self = this;
                //根据盘点计划和人员id查询本次盘点中该人员已录入的信息orderinfoList
                if(checkPlanId>0){
                    util.ajax.post('/repertory/check/orderinfoListByUser?checkPlanId='+checkPlanId )
                        .then(function (response) {
                            self.storeCheck.orderItemIds=[];
                            if (response.status === 200 && response.data) {
                                self.checkPlanId=checkPlanId;
                                self.checkMore.checkPlanId=self.checkPlanId;
                                self.storeCheck=response.data.data;
                                self.orderItems=response.data.checkDetailList;
                                self.checkFormInfo=response.data.checkFormInfo

                                // self.storeCheck.orderItemIds=[];
                                // for (var i = 0; i < self.orderItems.length; i++) {
                                //     self.storeCheck.orderItemIds.push(self.orderItems[i].id);
                                // }
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                }else{
                    alert("checkPlanId异常"+checkPlanId);
                }
            },backPartIndex(){
                this.$router.push({
                    name: 'store_check_do_list',
                    params:{checkPlanId: this.checkPlanId,warehouseName:this.storeCheck.warehouseName}
                });

            }, queryRepertory(query) {
                var self = this;
                if (query !== '') {
                    this.goodsLoading = true;
                    util.ajax.post('/repertory/check/orderinfoList4Search?checkPlanId='+this.checkPlanId+"&goodSearch="+query)
                        .then(function (response) {
                            if (response.status === 200 && response.data) {
                                self.goodsLoading = false;
                                self.goodsOptions = response.data.checkDetailList;
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });

                } else {
                    this.goodsOptions = [];
                }
            },
            handleRowDbClick (row) {
                this.$Modal.confirm({
                    title: '确认删除商品？',
                    content: '<p>确认删除商品 ' + row.name + '?</p>',
                    onOk: () => {
                        for (var i = 0; i < this.orderItems.length; i++) {
                            if (row.id === this.orderItems[i].id) {
                                this.orderItems.splice(i, 1);
                                this.buyOrder.orderItemIds.splice(i, 1);
                            }
                        }
                    },
                    onCancel: () => {

                    }
                });
            },
            onSelectRepertory (planDetailId) {
                var planDetail = this.goodsOptions.filter(o => o.id === planDetailId);
                alert(JSON.stringify(planDetail));
                //根据id选择出数据
                if (planDetail && planDetail.length == 1) {
                    var index = this.storeCheck.orderItemIds.indexOf(planDetailId);
                    if (index < 0) {
                        //var obj = planDetail[0];
                        this.orderItems.push( planDetail[0]);
                        this.storeCheck.orderItemIds.push(planDetailId);
                        var self = this;
                        setTimeout(function () {
                            self.$refs.checkOrderTable.$el.querySelector('.ivu-table-body tr:last-child input').focus();
                        }, 400);
                    } else {
                        this.$Message.warning('该商品已经添加');
                    }
                }
                this.$refs.repertorySelect.clearSingleSelect();
            },  clearData () {
                this.orderItems=[];

                this.storeCheck = {
                    orderItemIds: []
                };
            },
            saveCheckPart () {
                var self = this;
                if(""!=this.checkFormInfo.id && this.checkFormInfo.id>0){
                    this.saving = true;
                    util.ajax.post('/repertory/check/addOrUpdateForm', this.checkFormInfo)
                        .then(function (response) {
                            if (response.status === 200 && response.data) {
                                self.$Message.info('盘点表保存成功');
                            }
                            self.saving = false;
                        })
                        .catch(function (error) {
                            console.log(error);
                            self.saving = false;
                            self.$Message.error('保存盘点表错误'+error);
                        });
                }else{
                    this.$Message.error('请先载入一条盘点表记录再进行操作!');
                }


            }, handleAddLocation() {
                var self = this;
                util.ajax.post('/repertory/check/doCheckDetailMore', this.checkMore)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            //row.checkStatus= response.data.planDetail.checkStatus;
                            self.checkMoreModalShow=false;
                            self.$Message.info('盘盈登记成功');
                            //self.initPart();

                        }
                        self.saving = false;
                    })
                    .catch(function (error) {
                        console.log(error);
                        self.saving = false;
                    });
            },getCheckPlan(){
                this.checkPlanListModel = true;
            },checkPlanChoose(checkPlan) {
                this.checkPlanListModel=false;
                if(!checkPlan || !checkPlan.id) {
                    this.$Message.warning('获取选取的订单信息失败');
                    return;
                }
                this.initPart(checkPlan.id,checkPlan.warehouseName);
            }
        },mounted () {
            this.clearData();
        },  activated () {
            this.clearData();
        }
        // ,
        // watch: {
        //     '$route' () {
        //         this.initPart();
        //     }
        // }
    };
</script>

<style>

</style>

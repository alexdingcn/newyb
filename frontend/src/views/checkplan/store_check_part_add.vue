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
                <Icon type="document"></Icon>盘点制单
            </p>
            <div slot="extra">
                <!--<ButtonGroup >-->
                    <!--<Button type="primary" icon="android-add-circle" @click="checkMoreModalShow = true" >盘盈登记</Button>-->
                <!--</ButtonGroup>-->
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="saveCheckPart" >保存盘点表</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="backPartIndex" >返回</Button>
                </ButtonGroup>
            </div>

            <Form :label-width="85" :rules="ruleValidate" :model="storeCheck" ref="CheckPartDetailForm">
                <Row>
                    <Col span="6">
                        <FormItem label="盘点表编号" prop="formNo">
                            <Input v-model="storeCheck.formNo" size="small" :disabled=true></Input>
                        </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem label="备注" prop="checkNote">
                        <Input v-model="storeCheck.checkNote" size="small"   ></Input>
                    </FormItem>
                    </Col>
                    <Col span="18">
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
                <Row>



                </Row>

                <Table border highlight-row
                       class="margin-top-8"
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
    </div>

</template>


<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';

    export default {
        name: 'store_check_part_add',
        components: {

        }, data () {
                return {
                    saving: false,
                    disableGoos:true,  //默认商品信息不可以添加
                    checkMoreModalShow: false,
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
                    storeCheck: {
                        orderItemIds:[]
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
                                return moment(params.row.expDate).format('YYYY-MM-DD');
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
                                    return self.orderItems[params.index][params.column.key];
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
                                                               //
                                                            }
                                                            self.saving = false;
                                                        })
                                                        .catch(function (error) {
                                                            console.log(error);
                                                            self.saving = false;
                                                            //							self.$Message.error('保存采购订单错误 ' + error.data.message);
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
            initPart() {
                var self = this;
                let checkPlanId=this.$route.params.checkPlanId.toString();
                let warehouseName=this.$route.params.warehouseName.toString();
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
                                self.storeCheck.formNo=response.data.checkFormInfo.formNo;
                                self.storeCheck.checkNote=response.data.checkFormInfo.checkNote;
                                self.storeCheck.orderItemIds=[];
                                for (var i = 0; i < self.orderItems.length; i++) {
                                    self.storeCheck.orderItemIds.push(self.orderItems[i].id);
                                }
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
                this.storeCheck = {
                    orderItemIds: []
                };
            },
            doSave () {
                var self = this;
                this.saving = true;
                util.ajax.post('/repertory/check/add', this.storeCheck)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.$Message.info('采购入库订单保存成功');
                            self.$router.push({
                                name: 'store_check_index'
                            });
                        }
                        self.saving = false;
                    })
                    .catch(function (error) {
                        console.log(error);
                        self.saving = false;
                        self.$Message.error('保存采购订单错误'+error);
                    });
            },
            saveCheckPart () {
                //将状态更新为==待审核状态
                //检查检查表号。--检查是否有没有数量的记录
                //检查一条数据就将数据传入后台
                this.storeCheck.orderItems = this.orderItems;

                this.$refs.CheckOrderForm.validate((valid) => {
                    if (!valid) {
                        this.$Message.error('请检查输入!');
                    } else {
                        this.doSave();
                    }
                });
            }, handleAddLocation() {
                var self = this;
                util.ajax.post('/repertory/check/doCheckDetailMore', this.checkMore)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            //row.checkStatus= response.data.planDetail.checkStatus;
                            self.checkMoreModalShow=false;
                            self.$Message.info('盘盈登记成功');
                            self.initPart();

                        }
                        self.saving = false;
                    })
                    .catch(function (error) {
                        console.log(error);
                        self.saving = false;
                    });

                // util.ajax.post('/repertory/check/addCheckMore', this.checkMore)
                //     .then(function (response) {
                //         if (response.status === 200 && response.data) {
                //            // self.repertoryCheckItems = response.data.data;
                //            // 清除数据，刷新页面
                //         }
                //     })
                //     .catch(function (error) {
                //         console.log(error);
                //     });


            }
        },mounted () {
            this.initPart();
        },  activated () {
            this.clearData();
        },
        watch: {
            '$route' () {
                this.initPart();
            }
        }
    };
</script>

<style>

</style>

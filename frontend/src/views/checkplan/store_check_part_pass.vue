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

                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="saveCheckPart" >确认盘点表</Button>
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

                </Row>
                <Table border highlight-row
                       class="margin-top-8"
                       :columns="orderColumns" :data="orderItems"
                       ref="checkOrderTable" style="width: 100%;" size="small"
                       no-data-text="在商品输入框选择后添加"
                    >
                </Table>
            </Form>
        </Card>
    </Row>

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
                    checkPlanId:null,
                    checkFormId:null,
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
                            width: 100
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
                let formId=this.$route.params.formId.toString();
                let checkPlanId=this.$route.params.checkPlanId.toString();
                self.checkFormId=formId;
                //根据盘点计划和人员id查询本次盘点中该人员已录入的信息orderinfoList
                if(formId>0){
                    util.ajax.post('/repertory/check/orderinfoListFormId?formId='+formId )
                        .then(function (response) {
                            self.storeCheck.orderItemIds=[];
                            if (response.status === 200 && response.data) {
                                self.checkPlanId=checkPlanId;
                                self.checkMore.checkPlanId=self.checkPlanId;
                                self.orderItems=response.data.checkDetailList;
                                self.storeCheck.id=response.data.checkFormInfo.id;
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
                    name: 'store_check_part_index',
                    params:{checkPlanId: this.checkPlanId,warehouseName:this.storeCheck.warehouseName}
                });

            },
            saveCheckPart () {
                var self = this;
                this.saving = true;
                util.ajax.post('/repertory/check/checkFormPass', this.storeCheck)
                    .then(function (response) {
                        self.$Message.info('盘点表审核成功');
                        self.$router.push({
                            name: 'store_check_part_index',
                            params:{checkPlanId: self.checkPlanId,warehouseName:''}
                        });
                    })
                    .catch(function (error) {
                        console.log(error);
                        self.saving = false;
                        self.$Message.error('盘点表审核异常'+error);
                    });
            }
        },mounted () {
            this.initPart();
        },  activated () {
            this.initPart();
        }
    };
</script>

<style>

</style>

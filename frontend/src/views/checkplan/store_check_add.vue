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
                    <Icon type="document"></Icon>盘点制单
                </p>
                <div slot="extra">
                    <ButtonGroup >
                        <Button type="info" size="small" icon="android-bulb" @click="addCheckOrder">保存盘点计划</Button>
                        <Button type="success" size="small" icon="android-checkbox-outline"  @click="CheckPlanGetBtnClick">载入盘点计划</Button>
                        <!--<Button type="error" size="small" icon="android-checkbox-outline"  @click="removeCheckPlan">撤销盘点计划</Button>-->
                    </ButtonGroup>
                </div>

                <Form :label-width="85" :rules="ruleValidate" :model="storeCheck" ref="CheckOrderForm">
                    <Row>
                        <Col span="5">
                        <FormItem label="仓库点" prop="warehouseId">
                            <warehouse-select v-model="storeCheck.warehouseId" size="small" :disabled="disable_warehouse"></warehouse-select>
                        </FormItem>
                        </Col>
                        <Col span="5">
                        <FormItem label="盘点类型" prop="checkType">
                            <Select v-model="storeCheck.checkType" size="small"  prop="checkType"   @on-change="onSelectCheckType" :disabled="disable_checktype">
                                <Option v-for="option in checkTypeOptions" :value="option.id" :label="option.name" :key="option.id">
                                    {{option.name}}
                                </Option>
                            </Select>
                        </FormItem>
                        </Col>
                        <Col span="6">
                        <FormItem label="盘点日期" prop="checkDate" :disabled="disable_checkdate">
                            <DatePicker type="date" v-model="storeCheck.checkDate" size="small"/>
                        </FormItem>
                        </Col>

                    </Row>
                    <Row>
                        <Col span="5">
                        <FormItem label="添加单品">
                            <Select
                                    ref="goodsSelect"
                                    filterable
                                    clearable
                                    remote
                                    :disabled="disableGoos"
                                    placeholder="商品名称/拼音"
                                    size="small"
                                    @on-change="onSelectGoods"
                                    :remote-method="queryGoods"
                                    :loading="goodsLoading">
                                <Option v-for="option in goodsOptions" :value="option.id" :label="option.name" :key="option.id">
                                    <span class="option-goods-name">{{ option.name }}</span>
                                    <span class="option-goods-spec">{{ option.spec }} | {{option.factory}}</span>
                                </Option>
                            </Select>
                        </FormItem>
                        </Col>
                        <Col span="5">
                        <FormItem label="备注" prop="comment">
                            <Input v v-model="storeCheck.comment" size="small"    :disabled="disable_note"></Input>

                        </FormItem>
                        </Col>
                        <Col span="5">   <FormItem label="计划号：">{{checkPlanNo}}   </FormItem></Col>
                    </Row>
                    <Row>
                        <p class="ivu-card-head" >
                            添加盘点单品
                        </p>
                    </Row>
                    <Table border highlight-row
                           class="margin-top-8"
                           :columns="orderColumns" :data="orderItems"
                           :loading="detailLoading"
                           ref="checkOrderTable" style="width: 100%;"  height="300" size="small"
                           no-data-text="在商品输入框选择后添加"
                    >
                    </Table>

                </Form>
            </Card>

        </Row>

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
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import checkPlanList from "@/views/checkplan/check-plan-list.vue";
    export default {
        name: 'store_check_add',
        components: {
            warehouseSelect,
            checkPlanList,
        },
        data () {
            return {

                saving: false,
                detailLoading:false,
                disableGoos:true,
                disable_warehouse:false,
                disable_checktype:false,
                disable_checkdate:false,
                disable_note:false,

                goodsOptions:[],
                checkTypeOptions:[{ id: 0, name:'库存盘点'},{ id: 1, name:'直接盘库'},{ id: 2, name:'单品盘点'}],
                counterOptions:[{ id: 'ALL', name:'全部'}],
                goodsLoading: false,
                checkPlanListModel: false,
                edittingRow: {},
                checkPlanId:'',
                checkPlanNo:'',
                orderItems: [],
                storeCheck: {
                    id:'',
                    checkType:0,
                    checkDate:moment().format('YYYY-MM-DD'),
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
                        title: '批号',
                        align: 'center',
                        key: 'batchCode',
                        width: 120
                    },
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        align: 'center',
                        width: 150,
                        sortable: true

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
                    },
                    {
                        title: '库存',
                        key: 'accLimit',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '盘点数',
                        key: 'checkLimit',
                        align: 'center',
                        width: 100
                    }
                ],
                ruleValidate: {
                    checkType: [
                        { required: true, type: 'number', message: '请选择盘点类型', trigger: 'blur' }
                    ],
                    warehouseId: [
                        { required: true, type: 'number', message: '请选择仓库点', trigger: 'blur' }
                    ]
                }

            };
        },
        mounted () {


        },
        activated () {
        },
        watch: {
            // orderItems: function () {
            //     this.totalAmount = this.orderItems.reduce(function (total, item) { return total + parseFloat(item.amount); }, 0);
            // }
        },
        methods: {
            moment: function () {
                return moment();
            }, queryGoods (query) {
                var self = this;
                if (query !== '') {
                    this.goodsLoading = true;
                    util.ajax.get('/goods/list',
                        { params:
                                {search: query, page: 1, size: 10}
                        })
                        .then(function (response) {
                            self.goodsLoading = false;
                            self.goodsOptions = response.data.data;
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                } else {
                    this.goodsOptions = [];
                }
            },onSelectCheckType(){

                if(this.storeCheck.checkType == 2){
                    this.disableGoos =false;
                }else{
                    this.disableGoos =true;
                    this.orderItems= [];
                    this.storeCheck.orderItemIds=[];

                }
            },
            onSelectGoods (goodsId) {
                if(this.storeCheck.checkType!=2){
                    this.$Message.warning('该盘点模式不可以添加单品信息');
                    return;
                }
                var goods = this.goodsOptions.filter(o => o.id === goodsId);
                if (goods && goods.length == 1) {
                    var index = this.storeCheck.orderItemIds.indexOf(goodsId);
                    if (index < 0) {
                        var obj = goods[0];
                        obj['amount'] = 0;
                        this.orderItems.push(goods[0]);
                        this.storeCheck.orderItemIds.push(goodsId);
                        var self = this;
                        setTimeout(function () {
                            self.$refs.storeCheck.$el.querySelector('.ivu-table-body tr:last-child input').focus();
                        }, 400);
                    } else {
                        this.$Message.warning('该商品已经添加');
                    }
                }
                this.$refs.goodsSelect.clearSingleSelect();
            },
            doSave () {
                var self = this;
                this.saving = true;
                    util.ajax.post('/repertory/check/add', this.storeCheck)
                        .then(function (response) {
                            if (response.status === 200 ) {
                                self.checkPlanId=response.data.checkPlanId;
                                self.$Message.info("盘点计划创建/更新成功");
                                self.checkPlanNo=response.data.checkPlanNo;
                                self.refreshCheck( self.checkPlanId);
                            }
                            self.saving = false;
                        })
                        .catch(function (error) {
                            self.saving = false;
                            self.$Message.error('盘点单添加/更新失败 '+error);
                        });

            },
            addCheckOrder () {
                this.storeCheck.orderItems = this.orderItems;
                this.$refs.CheckOrderForm.validate((valid) => {
                    if (!valid) {
                        this.$Message.error('请检查输入!');
                    } else {
                        this.doSave();
                    }
                });
            },
            refreshCheck(checkPlanId){
                var self=this;
                this.detailLoading = true;
                util.ajax.post('/repertory/check/orderinfoList?checkPlanId='+checkPlanId)
                    .then(function (response) {
                        self.detailLoading = false;
                        let data = response.data;
                        if (data) {
                            self.checkPlanId=checkPlanId;
                            self.checkPlanNo=data.data.checkCode;
                            self.storeCheck.warehouseId=data.data.warehouseId;
                            self.storeCheck.checkType=data.data.checkType;
                            self.storeCheck.checkDate=data.data.checkDate;
                            self.storeCheck.comment=data.data.comment;
                            self.orderItems = data.checkDetailList;
                            if(data.data.checkType===0||data.data.checkType===1){
                                self.disableGoos=true;
                                self.disable_warehouse=true;
                                self.disable_checktype=true;
                                self.disable_checkdate=false;
                                self.disable_note=false;
                            } if (data.data.checkType===2){
                                self.disableGoos=false;
                                self.disable_warehouse=true;
                                self.disable_checktype=true;
                                self.disable_checkdate=false;
                                self.disable_note=false;
                            }
                        }
                    })
                    .catch((error) => {
                        self.detailLoading = false;
                    });
            }, CheckPlanGetBtnClick() {
                //提取盘点计划列表
                this.checkPlanListModel = true;
            },checkPlanChoose(checkPlan) {
            this.checkPlanListModel=false;
                if(!checkPlan || !checkPlan.id) {
                    this.$Message.warning('获取选取的订单信息失败');
                    return;
                }
                this.refreshCheck(checkPlan.id);
            },removeCheckPlan(){
                alert(this.checkPlanId+"==="+this.checkPlanNo);
            }
        }
    };
</script>

<style scoped>

</style>

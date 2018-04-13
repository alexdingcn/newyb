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
                <Icon type="document"></Icon>盘点制单
            </p>
            <div slot="extra">
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="addCheckOrder" >保存盘点单</Button>
                </ButtonGroup>
            </div>

            <Form :label-width="85" :rules="ruleValidate" :model="storeCheck" ref="CheckOrderForm">
                <Row>
                    <Col span="5">
                    <FormItem label="仓库点" prop="warehouseId">
                        <warehouse-select v-model="storeCheck.warehouseId" size="small"></warehouse-select>
                    </FormItem>
                    </Col>
                    <Col span="5">
                    <FormItem label="盘点类型" prop="checkType">
                        <Select v-model="storeCheck.checkType" size="small"  prop="checkType"   @on-change="onSelectCheckType">
                            <Option v-for="option in checkTypeOptions" :value="option.id" :label="option.name" :key="option.id">
                                {{option.name}}
                            </Option>
                        </Select>
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem label="盘点日期" prop="inDate">
                        <DatePicker type="date" v-model="storeCheck.checkDate" size="small"/>
                    </FormItem>
                    </Col>
                    <Col span="4">
                    <FormItem label="库区" >
                        <Select v-model="storeCheck.counterState" size="small"  prop="counter_state">
                            <Option v-for="option in counterOptions" :value="option.id" :label="option.name" :key="option.id">
                                {{option.name}}
                            </Option>
                        </Select>
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
                    <FormItem label="备注" prop="note">
                        <Input v v-model="storeCheck.note" size="small"></Input>

                    </FormItem>
                    </Col>
                </Row>
                <Row>
                    <p class="ivu-card-head" >
                        添加盘点单品
                    </p>
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

</template>


<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    export default {
        name: 'check_order',
        components: {
            warehouseSelect
        },
        data () {
            return {

                saving: false,
                disableGoos:true,  //默认商品信息不可以添加
                goodsOptions:[],
                checkTypeOptions:[{ id: 0, name:'库存盘点'},{ id: 1, name:'直接盘库'},{ id: 2, name:'单品盘点'}],
                counterOptions:[{ id: 'ALL', name:'全部'}],
                goodsLoading: false,
                edittingRow: {},
                closeConfirm: false,
                orderItems: [],
                storeCheck: {
                    counterState:'ALL',
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
                        title: '货号',
                        align: 'center',
                        key: 'id',
                        width: 60
                    },
                    {
                        title: '商品名称',
                        key: 'name',
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
                        key: 'factory',
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
                        key: 'balance',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '在单数',
                        key: 'ongoing',
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
                        //							self.$Message.error('保存采购订单错误 ' + error.data.message);
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
            }
        }
    };
</script>

<style>

</style>

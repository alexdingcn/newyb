<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
<div>
    <Row>
        <Card>
            <p slot="title" >
                <Icon type="document"></Icon> 转库出库制单
            </p>
            <div slot="extra">
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="saveOut" >保存</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="" >单据修改</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="" >数据引入</Button>
                </ButtonGroup>

                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="" >草稿导入</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="" >打印</Button>
                </ButtonGroup>
            </div>
            <Form :label-width="85" :model="changeStore" ref="changeStoreForm">
                <Row>
                    <Col span="5">
                    <FormItem label="转入仓库" prop="goToWarehouseId">
                        <warehouse-select v-model="changeStore.goToWarehouseId"  @on-change="onReceiveStoreChange"  size="small"></warehouse-select>
                    </FormItem>
                    </Col>

                    <Col span="5">
                    <FormItem label="自定义单号" prop="refOrderNumber">
                        <Input v-model="changeStore.refOrderNumber" />
                    </FormItem>
                    </Col>
                    <Col span="5">
                    <FormItem label="领料员" prop="customerName">
                        <Input v-model="changeStore.customerName" />
                    </FormItem>
                    </Col>
                    <Col span="5">
                    <FormItem label="出库日期" prop="outDate">
                        <DatePicker type="datetime" v-model="changeStore.outDate" format="yyyy-MM-dd HH:mm" size="small"/>
                    </FormItem>
                    </Col>

                </Row>
                <Row>
                    <Col span="10">
                    <FormItem label="单据摘要" prop="comment">
                        <Input v-model="changeStore.comment" />
                    </FormItem>
                    </Col>
                    <!--<Col span="5">-->
                    <!--<FormItem label="单据张数" prop="account">-->
                        <!--<Input v-model="changeStore.account" />-->
                    <!--</FormItem>-->
                    <!--</Col>-->
                </Row>
                <Table border highlight-row
                       class="margin-top-8"
                       :columns="changeStoreColumns" :data="changeStoreItems"
                       ref="changeStoreTable" style="width: 100%;" size="small"
                       @on-row-dblclick="handleOutRowDbClick"
                       no-data-text="当前条件下查询，无库存数据！">
                </Table>

            </Form>
        </Card>



    </Row>
    <br/>
    <Row>
        <Card>

            <p slot="title" >
                <Icon type="document"></Icon>库存明细(选择需要转库的物品，加入移库单)
            </p>
            <div slot="extra">
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="" >查询</Button>
                </ButtonGroup>

            </div>
            <Form :label-width="85" :model="queryStore" ref="queryStoreForm">
                <Row>
                    <Col span="5">
                        仓库：{{queryStore.warehouseName}}
                    </Col>
                    <Col span="10">
                    <FormItem label="商品速查" prop="goodsId">
                        <good-select v-model="queryStore.goodsId" size="small"></good-select>
                    </FormItem>
                    </Col>
                </Row>
                <Table border highlight-row
                       class="margin-top-8"
                       :columns="queryStoreColumns" :data="queryStoreItems"
                       ref="queryStoreTable" style="width: 100%;" size="small"
                       @on-row-dblclick="handleStoreRowDbClick"
                       no-data-text="当前条件下查询，无库存数据！">
                </Table>

                <Row class="margin-top-8">
                    <div style="float: right;">
                        <Page :total="totalAmount" :current="currentPage" @on-change="changePage" size="small" show-total></Page>
                    </div>
                </Row>
            </Form>
        </Card>

    </Row>


    <Modal  v-model="selectStoreModalShow" width="500" :mask-closable="false">
        <p slot="header">
            <Icon type="ios-plus-outline"></Icon>
            <span>选择移库仓库</span>
        </p>
        <div >
            <Row>
                <warehouse-select v-model="selectStore.warehouseId"  @on-change="onStoreChange" size="small"></warehouse-select>
            </Row>
        </div>
        <div slot="footer">
            <Button type="primary" @click="choseOutStore">确定</Button>
        </div>
    </Modal>
    <Modal v-model="closeConfirm"
           title="是否继续下单"
           @on-ok="clearData"
           @on-cancel="closeTab">
        <p>是否继续添加下一笔订单?</p>
    </Modal>
</div>
</template>
<script>
    import moment from 'moment';
    import util from '@/libs/util.js';
    import goodSelect from "@/views/selector/good-select.vue";
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    export default {
        name: 'cahneg-repertory-index',
        components: {
            warehouseSelect,
            goodSelect,
        },
        data () {
            return {
                saving: false,
                totalAmount: 0,
                currentPage: 1,
                changeStoreItems: [],
                queryStoreItems:[],
                selectStoreModalShow:false,
                closeConfirm: false,
                changeStore: {
                },
                queryStore: {
                    warehouseName:'',
                },
                selectStore:{
                    storeId:null
                },
                RepertoryOut:{
                    id:'',
                    warehouseId:'',
                    outDate:'',
                    refOrderNumber:'',
                    goToWarehouseId:'',
                    goTo:'',
                    customerName:'',
                    comment:'',
                    detail:[]
                },
                RepertoryOutDetail:{
                    repertoryId:'',
                    amount:'',
                    location:''
                },
                changeStoreColumns: [

                    {
                        title: '货号',
                        align: 'center',
                        key: 'code',
                        width:100
                    },
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        align: 'center',
                        width: 150

                    },
                    {
                        title: '产地',
                        key: 'origin',
                        align: 'center',
                        width: 100
                    },

                    {
                        title: '规格',
                        key: 'spec',
                        align: 'center',
                        width: 100
                    },
                    // {
                    //     title: '生产企业',
                    //     key: 'factoryName',
                    //     align: 'center',
                    //     width: 120
                    // },
                    // {
                    //     title: '存储条件',
                    //     key: 'storageCondition',
                    //     align: 'center',
                    //     width: 120
                    // },
                    // {
                    //     title: '基药',
                    //     key: 'base_med_id',
                    //     align: 'center',
                    //     width:80
                    // },
                    {
                        title: '单位',
                        key: 'unitName',
                        align: 'center',
                        width: 80
                    },

                    {
                        title: '数量',
                        key: 'quantity',
                        align: 'center',
                        width: 120

                    },
                    {
                        title: '转移数量',
                        key: 'outAmount',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            var self = this;
                            return h('Input', {
                                props: {
                                    value: self.changeStoreItems[params.index][params.column.key]
                                },
                                on: {'on-blur' (event) {
                                        let row = self.changeStoreItems[params.index];
                                        row[params.column.key] = event.target.value;
                                        let checkLimit=event.target.value;
                                        if(checkLimit>row.quantity){
                                            self.$Message.error('转移数量不能超出库存数量');
                                        } else if(checkLimit <= 0){
                                            self.$Message.error('转移数量不能小于等于0');
                                        }
                                    }
                                }
                            });
                        }
                    } , {
                        title: '转入货位号',
                        key: 'outLocation',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            var self = this;
                            return h('Input', {
                                props: {
                                   value: self.changeStoreItems[params.index][params.column.key]
                                },
                                on: {'on-blur' (event) {
                                        let row = self.changeStoreItems[params.index];
                                        row[params.column.key] = event.target.value;
                                        let checkLimit=event.target.value;

                                    }
                                }
                            });
                        }
                    },

                    {
                        title: '单价',
                        key: 'buyPrice',
                        align: 'center',
                        width: 80

                    },

                    // {
                    //     title: '金额',
                    //     key: '',
                    //     align: 'center',
                    //     width: 80
                    // },
                    // {
                    //     title: '税率',
                    //     key: 'out_tax',
                    //     align: 'center',
                    //     width: 80
                    // },
                    {
                        title: '批次号',
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
                            return moment(params.row.eta).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '有效日期',
                        key: 'expDate',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.eta).format('YYYY-MM-DD');
                        }
                    }
                    // ,{
                    //     title: '仓库点',
                    //     key: 'warehouseName',
                    //     align: 'center',
                    //     width: 100,
                    // }

                ] ,
                queryStoreColumns: [
                    {
                        title: '货号',
                        align: 'center',
                        key: 'code',
                        width:100
                    },
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        align: 'center',
                        width: 150

                    },
                    {
                        title: '产地',
                        key: 'origin',
                        align: 'center',
                        width: 100
                    },

                    {
                        title: '规格',
                        key: 'spec',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '生产企业',
                        key: 'factoryName',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '存储条件',
                        key: 'storageCondition',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '基药',
                        key: 'base_med_id',
                        align: 'center',
                        width:80
                    },
                    {
                        title: '单位',
                        key: 'unitName',
                        align: 'center',
                        width: 80
                    },

                    {
                        title: '数量',
                        key: 'quantity',
                        align: 'center',
                        width: 80

                    },
                    {
                        title: '单价',
                        key: 'buyPrice',
                        align: 'center',
                        width: 80

                    },

                    {
                        title: '金额',
                        key: '',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '税率',
                        key: 'out_tax',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '批次号',
                        key: 'batchCode',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '生产日期',
                        key: 'productDate',
                        align: 'center',
                        width: 80,
                        render: (h, params) => {
                            return moment(params.row.eta).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '有效日期',
                        key: 'expDate',
                        align: 'center',
                        width: 80,
                        render: (h, params) => {
                            return moment(params.row.eta).format('YYYY-MM-DD');
                        }
                    },

                    {
                        title: '仓库点',
                        key: 'warehouseName',
                        align: 'center',
                        width: 100,
                    }, {
                        title: '转出货位号',
                        key: 'location',
                        align: 'center',
                        width: 100,
                    }
                ],
                ruleValidate: {
                    supplierId: [
                        { required: true, type: 'number', message: '请选择供应商', trigger: 'blur' }
                    ],
                    supplierContactId: [
                        { required: true, type: 'number', message: '请选择供应商代表', trigger: 'blur' }
                    ],
                    buyerId: [
                        { required: true, type: 'number', message: '请选择采购员', trigger: 'blur' }
                    ],
                    warehouseId: [
                        { required: true, type: 'number', message: '请选择仓库点', trigger: 'blur' }
                    ],
                    orderItems: [
                        { required: true, type: 'array', array: {min: 1}, message: '请添加商品', trigger: 'blur' }
                    ]
                },

            };
        },
        mounted () {
            this. selectStoreModalShow=true;
        },
        activated () {
            this.clearData();
        },
        watch: {
            // repertoryCheckItems: function () {
            // }
        },
        methods: {
            moment: function () {
                return moment();
            },choseOutStore(){
                var self = this;
                this.queryStoreItems = [];
                this.queryStore.warehouseId= this.selectStore.warehouseId;
                this.queryStore.page=this.currentPage;
                util.ajax.post('/repertory/list', this.queryStore)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.selectStoreModalShow=false;
                            self.queryStoreItems = response.data.data;
                            self.totalAmount = response.data.total;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            queryRepertoryList () {
                var self = this;
                this.repertoryItems = [];
                this.queryStore.page=this.currentPage;
                util.ajax.post('/repertory/list', this.queryStore)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.queryStoreItems = response.data.data;
                            self.totalAmount = response.data.total;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                        self.$Message.error(error);
                    });
            },
            changePage (pageNumber) {
                this.currentPage = pageNumber;
                this.queryRepertoryList();
            },onStoreChange(data,item){
                this.queryStore.warehouseName=item.name;
            },onReceiveStoreChange(data,item){
               if(item.name===this.queryStore.warehouseName) {
                   this.changeStore.warehouseId='';
                   this.$Message.info('转入仓库不能与转出仓库相同');
               }
            },handleStoreRowDbClick(row){
                //库存记录点击

                for (var i = 0; i < this.changeStoreItems.length; i++) {
                    if (row.id === this.changeStoreItems[i].id) {
                        this.$Message.info('转移单已经存在此记录');
                        return true;
                    }
                }
                this.$Modal.confirm({
                    title: '确认转移此商品？',
                    content: '<p>确认转移商品 ' + row.goodsName + '?</p>',
                    onOk: () => {
                        this.changeStoreItems.splice(0,0,row);
                    },
                    onCancel: () => {
                    }
                });
            },
            handleOutRowDbClick(row){
              //移库出库记录表，双击删除
                this.$Modal.confirm({
                    title: '确认删除商品？',
                    content: '<p>确认删除商品 ' + row.name + '?</p>',
                    onOk: () => {
                        for (let i = 0; i < this.changeStoreItems.length; i++) {
                            if (row.id === this.changeStoreItems[i].id) {
                                this.changeStoreItems.splice(i, 1);
                            }
                        }
                    },
                    onCancel: () => {

                    }
                });
            },
            clearData () {
                this.RepertoryOut = {};
                this.RepertoryOut.outDetailList=[];
                this.changeStoreItems = [];
            },
            closeTab () {
                this.clearData();
                let pageName = util.closeCurrentTab(this);
                this.$router.push({
                    name: pageName
                });
            },
            saveOut(){
                //前台验证出库信息
                let self=this;
                this.RepertoryOut={};
                this.RepertoryOut.outDetailList=[];
                this.RepertoryOut.goToWarehouseId=this.changeStore.goToWarehouseId;
                this.RepertoryOut.warehouseId=this.selectStore.warehouseId;
                this.RepertoryOut.refOrderNumber=this.changeStore.refOrderNumber;
                this.RepertoryOut.outDate=this.changeStore.outDate;
                this.RepertoryOut.comment=this.changeStore.comment;
                for (let i = 0; i < this.changeStoreItems.length; i++) {
                    if(this.changeStoreItems[i].quantity < this.changeStoreItems[i].outAmount){
                        this.$Message.error(this.changeStoreItems[i].goodsName+'转移数量超过库存数量，无法进行转移，请修改后重新提交！');
                        return;
                    }else{
                        let RepertoryOutDetail={};
                        RepertoryOutDetail['repertoryInfoId']=this.changeStoreItems[i].id;
                        RepertoryOutDetail['quantity']=this.changeStoreItems[i].outAmount;
                        RepertoryOutDetail['location']=this.changeStoreItems[i].outLocation;
                        this.RepertoryOut.outDetailList.push(RepertoryOutDetail)
                    }
                    util.ajax.post('/repertory/out/changeRepertoryOut', this.RepertoryOut)
                        .then(function (response) {
                            if (response.status === 200 ) {
                                self.$Message.info('转移出库单创建成功');
                                self.closeConfirm = true;
                            }
                            self.saving = false;
                        })
                        .catch(function (error) {
                            console.log(error);
                            self.saving = false;
                            self.$Message.error('转移出库单错误');
                        });
                }

            }
        }
    };
</script>

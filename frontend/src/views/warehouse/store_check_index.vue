<style lang="less">
    @import '../../styles/common.less';
    @import './store_now.less';
</style>

<template>
    <Row>
        <Card>
            <p slot="title" >
                <Icon type="document"></Icon> 库存盘点
            </p>

            <div slot="extra">
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="addCheckOrder" >盘点启动</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="checkOrderInfo" >盘点单详情</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="doCheckOrder" >执行盘点</Button>
                </ButtonGroup>
                <!--<ButtonGroup >-->
                    <!--<Button type="primary" icon="android-add-circle" @click="" >盘点表审核</Button>-->
                <!--</ButtonGroup>-->
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="" >盘点审核</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="" >盘盈制单/审核</Button>
                </ButtonGroup>
            </div>
            <Form :label-width="85" :model="storeCheck" ref="storeCheckForm">
                <Row>
                    <Col span="5">
                        <FormItem label="盘点类型" prop="checkType">
                            <Select v-model="storeCheck.checkType" size="small"  prop="checkType">
                                <Option v-for="option in checkTypeOptions" :value="option.id" :label="option.name" :key="option.id">
                                    {{option.name}}
                                </Option>
                            </Select>
                        </FormItem>
                    </Col>
                    <Col span="5">
                        <FormItem label="仓库点" prop="warehouseId">
                            <warehouse-select v-model="storeCheck.warehouseId" size="small"></warehouse-select>
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
                    <Col span="6">
                        <FormItem label="盘点日期" >
                            <DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="盘点日期" style="width:180px"></DatePicker>
                        </FormItem>
                    </Col>
                    <Col span="3">
                        <Button type="primary" icon="android-search" @click="queryCheckListList()" :loading="saving">查询</Button>
                    </Col>

                </Row>

                <Table border highlight-row
                       class="margin-top-8"
                       :columns="repertoryColumns" :data="repertoryCheckItems"
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
    import warehouseSelect from "@/views/selector/warehouse-select.vue";
    import goodSelect from "@/views/selector/good-select.vue";
    export default {
        name: 'store_check_index',
        components: {
            warehouseSelect,
        },
        data () {
            return {
                saving: false,
                repertoryCheckItems: [],
                checkTypeOptions:[{ id: 0, name:'库存盘点'},{ id: 1, name:'直接盘库'},{ id: 2, name:'单品盘点'}],
                counterOptions:[{ id: 'ALL', name:'全部'}],
                storeCheck: {
                    warehouseId: null
                },
                repertoryColumns: [
                    {
                        type: 'selection',
                        title: '',
                        align: 'center',
                        width: 60
                    },
                    {
                        type: 'index',
                        title: 'id',
                        align: 'center',
                        width: 30
                    },
                    {
                        title: '状态',
                        align: 'center',
                        key: 'state',
                        width: 80,
                        render: (h, params) => {
                            let state  = params.row.state;
                            if (0==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '待处理');
                            }else if (1==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '盘点中');
                            }else if (2==state){
                                return h('Tag', {props: {color: 'green'}}, '已完成');
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
                        width: 80,
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
                        width: 80
                    },

                    {
                        title: '盘点时间',
                        key: 'checkDate',
                        align: 'center',
                        width: 80,
                        render: (h, params) => {
                                return moment(params.row.eta).format('YYYY-MM-DD');
                            }
                    },
                    {
                        title: '制单人',
                        key: 'createdBy',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '备注',
                        key: 'note',
                        align: 'center',
                        width: 150
                    },
                    {
                        title: '总经理',
                        key: 'manager',
                        align: 'center',
                        width: 80
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
                        width: 80
                    },
                    {
                        title: '财务经理意见',
                        key: 'financeNote',
                        align: 'center',
                        width: 150
                    }
                ]

            };
        },
        mounted () {
            this.queryCheckListList();
        },
        activated () {
            // this.clearData();
        },
        watch: {
            repertoryCheckItems: function () {
            }
        },
        methods: {
            moment: function () {
                return moment();
            },
            dateRange: [
                moment().add(-1, 'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD')
            ],
            addCheckOrder(){
                this.$router.push({
                    name: 'store_check_add'
                });
            },
            checkOrderInfo(){
                var self = this;
                var rows = this.$refs.storeCheckTable.getSelection();
                if (!rows || rows.length === 0) {
                    this.$Message.warning('请选择盘点单');
                } else if (rows.length > 1) {
                    this.$Message.warning('请一次选择一条盘点单');
                } else if (rows.length == 1) {
                    this.$router.push({
                        name: 'store_check_info',
                        params:{checkOrderId: rows[0].id,warehouseName:rows[0].warehouseName}
                    });
                }
            },doCheckOrder(){
                var self = this;
                var rows = this.$refs.storeCheckTable.getSelection();
                if (!rows || rows.length === 0) {
                    this.$Message.warning('请选择盘点单');
                } else if (rows.length > 1) {
                    this.$Message.warning('请一次选择一条盘点单');
                } else if (rows.length == 1) {
                    this.$router.push({
                        name: 'store_check_part_index',
                        params:{checkOrderId: rows[0].id,warehouseName:rows[0].warehouseName}
                    });
                }
            },
            queryCheckListList () {
                var self = this;

                let startDate = this.dateRange[0];
                let endDate = this.dateRange[1];
                this.storeCheck.startDate=startDate;
                this.storeCheck.endDate=endDate;
                this.repertoryCheckItems = [];
                util.ajax.post('/repertory/check/list', this.storeCheck)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.repertoryCheckItems = response.data.data;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        }
    };
</script>
<style>

</style>

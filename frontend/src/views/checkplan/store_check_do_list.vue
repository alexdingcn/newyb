<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <Row>
        <Card>
            <p slot="title" >
                <Icon type="document"></Icon> 待盘点计划
            </p>

            <div slot="extra">
                <!--<ButtonGroup >-->
                    <!--<Button type="primary" icon="android-add-circle" @click="showDoDetail" >盘点表确认</Button>-->
                <!--</ButtonGroup>-->
                <ButtonGroup >
                <Button type="primary" icon="android-add-circle" @click="doCheckOrder" >制定/执行盘点表</Button>
                </ButtonGroup>
                <!--<ButtonGroup >-->
                <!--<Button type="primary" icon="android-add-circle" @click="" >盘点表审核</Button>-->
                <!--</ButtonGroup>-->
                <!--<ButtonGroup >-->
                <!--<Button type="primary" icon="android-add-circle" @click="checkOrderInfo" >盘点审核</Button>-->
                <!--</ButtonGroup>-->
            </div>
            <Form :label-width="85" :model="storeCheck" ref="storeCheckForm">


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
        name: 'store_check_do_list',
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
                        title: '状态',
                        align: 'center',
                        key: 'state',
                        width: 120,
                        render: (h, params) => {
                            let state  = params.row.state;
                            if (0==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '待审核');
                            }else if (1==state) {
                                return h('Tag', {props: {color: 'green'}}, '已审核');
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
                        width: 120,
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
                        width: 100
                    },

                    {
                        title: '盘点时间',
                        key: 'checkDate',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.checkDate).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '制单人',
                        key: 'createBy',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '备注',
                        key: 'comment',
                        align: 'center',
                        width: 150
                    },
                    {
                        title: '总经理',
                        key: 'manager',
                        align: 'center',
                        width: 120
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
                        width: 120
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
            this.queryDoList();
        },
        activated () {
            this.queryDoList();
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
            showDoDetail(){
                var self = this;
                var rows = this.$refs.storeCheckTable.getSelection();
                if (!rows || rows.length === 0) {
                    this.$Message.warning('请选择盘点单');
                } else if (rows.length > 1) {
                    this.$Message.warning('请一次选择一条盘点单');
                } else if (rows.length == 1) {
                    this.$router.push({
                        //name: 'store_check_do_detail',
                        name: 'store_check_part_index',
                        params:{checkPlanId:rows[0].id,warehouseName:rows[0].warehouseName}
                    });
                }
            },
            queryDoList () {
                var self = this;
                let startDate = this.dateRange[0];
                let endDate = this.dateRange[1];
                this.storeCheck.startDate=startDate;
                this.storeCheck.endDate=endDate;
                this.repertoryCheckItems = [];
                util.ajax.get('/repertory/check/doList',{ params: {
                        checkType:  this.storeCheck.checkType,
                        warehouseId: this.storeCheck.warehouseId,
                        startDate: this.storeCheck.startDate,
                        endDate: this.storeCheck.endDate
                    }})
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.repertoryCheckItems = response.data.data;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },onSelectCheckType(item){
                this.$refs.checkTypeSelect.clearSingleSelect();
                this.storeCheck.checkType = item;
            },doCheckOrder(){
                var self = this;
                var rows = this.$refs.storeCheckTable.getSelection();
                if (!rows || rows.length === 0) {
                    this.$Message.warning('请选择盘点单');
                } else if (rows.length > 1) {
                    this.$Message.warning('请一次选择一条盘点单');
                } else if (rows.length == 1) {
                    this.$router.push({
                        name: 'store_check_part_add',
                        params:{checkPlanId: rows[0].id,warehouseName:rows[0].warehouseName}
                    });
                }
            }
        }
    };
</script>
<style>

</style>

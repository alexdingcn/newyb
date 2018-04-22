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
                <Icon type="document"></Icon> 盘点表
            </p>

            <div slot="extra">

                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="showCheckPartDetail" >确认详细信息</Button>
                </ButtonGroup>
                <!--<ButtonGroup >-->
                    <!--<Button type="primary" icon="android-add-circle" @click="" >盘点表审核</Button>-->
                <!--</ButtonGroup>-->
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
                       :columns="repertoryColumns" :data="repertoryCheckPartItems"
                       ref="storeCheckTable" style="width: 100%;" size="small"
                       no-data-text="当前巡检单下尚无盘点表数据！">

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
        name: 'store_check_part_index',
        components: {
        },
        data () {
            return {
                saving: false,
                repertoryCheckPartItems: [],
                checkTypeOptions:[{ id: 0, name:'库存盘点'},{ id: 1, name:'直接盘库'},{ id: 2, name:'单品盘点'}],
                counterOptions:[{ id: 'ALL', name:'全部'}],
                storeCheck: {
                    checkPlanId: null
                },
                repertoryColumns: [
                    {
                        type: 'selection',
                        key: 'id',
                        align: 'center',
                        width: 60
                    },
                    // {
                    //     type: 'index',
                    //     title: 'id',
                    //     align: 'center',
                    //     width: 30
                    // },
                    {
                        title: '状态',
                        align: 'center',
                        key: 'formState',
                        width: 120,
                        render: (h, params) => {
                            let state  = params.row.formState;
                            if (0==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '待确认');
                            }else if (1==state) {
                                return h('Tag', {props: {color: 'green'}}, '已确认');
                            }
                        }
                    },
                    {
                        title: '盘点表编号',
                        align: 'center',
                        key: 'formNo',
                        width: 200
                    },
                    {
                        title: '制表人',
                        align: 'center',
                        key: 'createBy',
                        width: 120
                    },

                    {
                        title: '制表时间',
                        key: 'createTime',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.createTime).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '审核人',
                        key: 'updateBy',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '审核时间',
                        key: 'updateTime',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.updateTime).format('YYYY-MM-DD');
                        }
                    },

                    {
                        title: '审核结果',
                        key: 'checkNote',
                        align: 'center',
                        width: 400
                    }

                ]

            };
        },

        methods: {
            initPartIndex () {
                var self = this;
                let checkPlanId=this.$route.params.checkPlanId.toString();
                let warehouseName=this.$route.params.warehouseName.toString();
                if(checkPlanId>0){
                    util.ajax.post('/repertory/check/orderPartList?checkPlanId='+checkPlanId )
                        .then(function (response) {
                            self.repertoryCheckPartItems = [];
                            if (response.status === 200 && response.data) {
                                self.repertoryCheckPartItems = response.data.checkPartList;
                                self.storeCheck=response.data.data;
                                self.storeCheck.checkPlanId=checkPlanId;
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
                                }if (1==state){
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


            },showCheckPartDetail(){


                var self = this;
                var rows = this.$refs.storeCheckTable.getSelection();
                if (!rows || rows.length === 0) {
                    this.$Message.warning('请选择盘点表记录');
                } else if (rows.length > 1) {
                    this.$Message.warning('请一次选择一条盘点表记录');
                } else if (rows.length == 1) {
                    if(rows[0].formState==1){
                        this.$Message.warning('该盘点表已经确认');
                    }else{
                        this.$router.push({
                            name: 'store_check_part_pass',
                            params:{
                                formId:rows[0].id,
                                checkPlanId:this.storeCheck.checkPlanId
                            }
                        });
                    }


                }
            }
        }, mounted () {
        this.initPartIndex();
        }, activated () {
           // this.clearData();
        },
        watch: {
            '$route'() {
                this.initPartIndex();
            }
        }
    };
</script>
<style>

</style>

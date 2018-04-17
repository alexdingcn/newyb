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
                    <Button type="primary" icon="android-add-circle" @click="addCheckPart" >盘点表录入</Button>
                </ButtonGroup>
                <ButtonGroup >
                    <Button type="primary" icon="android-add-circle" @click="" >盘点表审核</Button>
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
                        type: 'index',
                        title: 'id',
                        align: 'center',
                        width: 30
                    }, {
                        title: '状态',
                        align: 'center',
                        key: 'state',
                        width: 120,
                        render: (h, params) => {
                            let state  = params.row.state;
                            if (0==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '待审核');
                            }else if (1==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '已完成');
                            }
                        }
                    },
                    {
                        title: '盘点表编号',
                        align: 'center',
                        key: 'checkNo',
                        width: 200
                    },
                    {
                        title: '制表人',
                        align: 'center',
                        key: 'createdBy',
                        width: 120
                    },

                    {
                        title: '制表时间',
                        key: 'createdTime',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '审核人',
                        key: 'processUserName',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '审核时间',
                        key: 'prcoessDate',
                        align: 'center',
                        width: 120
                    },

                    {
                        title: '审核结果',
                        key: 'processResult',
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


            },addCheckPart(){
                this.$router.push({
                    name: 'store_check_part_add',
                    params:{checkPlanId: this.storeCheck.id,warehouseName:this.storeCheck.warehouseName}
                });
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

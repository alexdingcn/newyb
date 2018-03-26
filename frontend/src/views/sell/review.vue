<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>

        <div class="search-div">
            <Row >
                <Form ref="searchForm" :model="searchFormItem" :rules="searchFormValidate" :label-width="90">
                    <Row type="flex" justify="center">
                        <Col span="8" >
                            <FormItem label="审批类型" prop="reviewType">
                                <Select size="small" v-model="searchFormItem.reviewType" clearable filterable >
                                    <Option v-for="item in reviewTypeList" :value="item.value" :key="item.value">{{ item.description }}</Option>
                                </Select> 
                            </FormItem>
                        </Col>
                        <Col span="16" >
                            <FormItem label="制单日期">
                                <DatePicker  size="small" v-model="searchFormItem.startDate" type="date" placeholder="请选择制单日期" ></DatePicker> 
                                至 
                                <DatePicker  size="small" v-model="searchFormItem.endDate" type="date" placeholder="请选择制单日期" ></DatePicker> 
                            </FormItem>
                        </Col>
                    </Row>
                    <Row type="flex" justify="center">
                        <Col span="8" >
                            <FormItem label="销售单号">
                                <Input  size="small" type="text" v-model="searchFormItem.orderNumber" ></Input>
                            </FormItem>
                        </Col>
                        <Col span="8" >
                            <FormItem label="销售员" >
                                <Select  size="small" v-model="searchFormItem.salerId" clearable filterable >
                                    <Option v-for="item in salerList" :value="item.userId" :key="item.userId">{{ item.nickname }}{{item.realname ? (' - [' + item.realname + ']') : ''}}</Option>
                                </Select> 
                            </FormItem>
                        </Col>
                        <Col span="3" offset="1" >
                            <Button size="small" icon="ios-search" type="primary" :loading="searching" @click="searchBtnClick">查询</Button>
                        </Col>
                        <Col span="4"></Col>
                    </Row>
                </Form>
            </Row>
        </div>
        <div class="table-div">
            <Row type="flex" justify="start">
                <Button size="small" type="success" icon="checkmark" :loading="searching" @click="submitBtnClick" >审核通过</Button>
            </Row>
            <Row type="flex" justify="center" align="middle" >
                <Table border highlight-row :columns="tabColumns" :data="tabData" 
                        :loading="searching" 
                        @on-selection-change="tableSelectDataChange"
                        ref="table" style="width: 100%;" size="small">
                </Table>
            </Row>
        </div>

        <Modal v-model="showOrderDetailView" :width="70" :mask-closable="false" title="销售订单详细信息">
            <review-detail :sellOrderId="showDetailViewId" @on-cancel="showOrderDetailViewClose"></review-detail>
            <div slot="footer"></div>
        </Modal>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import reviewDetail from './review-detail.vue';

export default {
    name: 'sell-review',
    components: {
        reviewDetail
    },
    data () {
        return {
            reviewTypeList: [],
            salerList: [],
            searchFormItem: {
                reviewType: '',
                startDate: '',
                endDate: '',
                orderNumber: '',
                salerId: ''
            },
            searchFormValidate: {
                reviewType: [
                    {required: true, message: '审批类型必须选择', trigger: 'change'}
                ]
            },
            searching: false,
            tabData: [],
            tabColumns: [
                {
                    title: '查看',
                    width: 70,
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'text',
                                size: 'small',
                                icon: 'eye'
                            },
                            on: {
                                click: () => {
                                    this.showReviewDetail(params.row.id);
                                }
                            }
                        });
                    }
                },
                {
                    type: 'selection',
                    width: 60,
                    align: 'center'
                },
                {
                    title: '订单编号',
                    key: 'orderNumber',
                    align: 'center',
                    sortable: true
                },
                {
                    title: '制单日',
                    key: 'createOrderDate',
                    align: 'center',
                    sortable: true
                },
                {
                    title: '客户',
                    key: 'customerName',
                    align: 'center'
                },
                {
                    title: '销售员',
                    key: 'salerId',
                    align: 'center'
                },
                {
                    title: '制单人',
                    key: 'createBy',
                    align: 'center'
                },
                {
                    title: '提货员',
                    key: 'takeGoodsUser',
                    align: 'center'
                },
                {
                    title: '收款金额',
                    key: 'payAmount',
                    align: 'center'
                },
                {
                    title: '收货人',
                    key: 'customerRepName',
                    align: 'center'
                },
                {
                    title: '收货电话',
                    key: 'customerRepContactPhone',
                    align: 'center'
                },
                {
                    title: '收货地址',
                    key: 'customerRepRepertoryAddress',
                    align: 'center'
                }
            ],
            tabSelectData: [],
            showOrderDetailView: false,
            showDetailViewId: -1
        };
    },
    mounted () {
        this.initData();
    },
    methods: {
        initData () {
            this.getOptions();
            this.getSalerList();
        },
        getOptions () {
            let reqData = ['SELL_ORDER_REVIEW'];
            util.ajax.post('/options/list', reqData)
                .then((response) => {
                    let data = response.data;
                    if (data && data.SELL_ORDER_REVIEW) {
                        this.reviewTypeList = data.SELL_ORDER_REVIEW;
                    }
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },
        getSalerList () {
            util.ajax.get('/userrole/list', {params: {roleQuery: 'ROLE_SALER'}})
                .then((response) => {
                    this.salerList = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },
        searchBtnClick () {
            this.$refs.searchForm.validate(valid => {
                if (!valid) {
                    this.$Message.warning('审批类型必须选择');
                } else {
                    this.refreshTableData();
                }
            });
        },
        refreshTableData () {
            let reqData = {
                status: this.searchFormItem.reviewType,
                orderNumber: this.searchFormItem.orderNumber,
                salerId: this.searchFormItem.salerId
            };
            let startDate = this.searchFormItem.startDate;
            let endDate = this.searchFormItem.endDate;
            if (startDate && startDate !== '' && (startDate instanceof Date)) {
                reqData.startDate = startDate.getTime();
            }
            if (endDate && endDate !== '' && (endDate instanceof Date)) {
                reqData.endDate = endDate.getTime();
            }
            this.searching = true;
            util.ajax.get('/sell/order/all/list', {params: reqData})
                .then((response) => {
                    this.tabData = response.data.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
            this.searching = false;
        },
        showReviewDetail (orderId) {
            this.showOrderDetailView = true;
            this.showDetailViewId = orderId;
        },
        showOrderDetailViewClose () {
            this.showOrderDetailView = false;
        },
        tableSelectDataChange (data) {
            this.tabSelectData = data;
        },
        submitBtnClick () {
            if (!this.tabSelectData || this.tabSelectData.length <= 0 || !this.searchFormItem.reviewType) {
                this.$Message.warning('请确认审批类型和选择需要审批通过的数据');
                return;
            }
            this.searching = true;
            let orderIdList = [];
            for (let i = 0; i < this.tabSelectData.length; i++) {
                orderIdList.push(this.tabSelectData[i].id);
            }
            let reqData = {
                reviewType: this.searchFormItem.reviewType,
                orderIdList: orderIdList
            };
            console.log(reqData);
            util.ajax.post('/sell/order/review/submit', reqData)
                .then((response) => {
                    this.$Message.success('审核提交成功');
                    this.refreshTableData();
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
            this.searching = false;
        }
    }
};
</script>

<style>
.ivu-form-item {
    margin-bottom: 5px;
}
.search-div {
    background-color: #fff;
}
.table-div {
    background-color: #fff;
    margin-top: 10px;
}

</style>


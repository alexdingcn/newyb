
<template>
    <div>
        <Card >
            <p slot="title">
                <Icon type="erlenmeyer-flask"></Icon>
                销售退出质量复核
            </p>
            <div slot="extra">
                <ButtonGroup size="small">
                    <Button type="primary" icon="search" :loading="loading" @click="queryBackOrderList" >查询</Button>
                    <Button type="success" icon="checkmark" :loading="loading" @click="checkOrderBtn">复核一单</Button>
                  <Button type="warning" icon="close" :loading="loading"  @click="checkOrderCancel">取消复核</Button>
                    <Button type="error" icon="trash-a" :loading="loading" @click="removeBackOrder">删除</Button>
                </ButtonGroup>
            </div>
            <Form ref="searchForm" :label-width="100">
              <Row type="flex" justify="start">
                  <i-col span="6">
                      <FormItem label="制单日期">
                          <DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="制单日期" style="width:180px"></DatePicker>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="仓库">
                          <warehouse-select v-model="warehouseId"></warehouse-select>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="客户">
                          <customer-select v-model="customerId"></customer-select>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="销售员">
                          <sale-select v-model="saleId"></sale-select>
                      </FormItem>
                  </i-col>
              </Row>
            </Form>

            <Table border highlight-row disabled-hover height="300" style="margin-bottom: 2em;"
                   :columns="orderColumns" :data="orderList" :loading="loading" 
				   ref="orderTable" size="small"
                   @on-row-click="handleSelectOrder" 
				   no-data-text="使用右上方输入搜索条件">
			</Table>

            <Row type="flex" justify="end">
                <ButtonGroup size="small">
                    <Button type="success" icon="checkmark" :loading="detailLoading" @click="checkOneDetailBtn">验收一条</Button>
                    <Button type="error" icon="close" :loading="detailLoading" @click="unCheckOneDetailBtn">取消验收一条</Button>
                </ButtonGroup>
            </Row>
            <Table border highlight-row disabled-hover height="350"
                   :columns="detailColumns" :data="details" :loading="detailLoading" 
                   @on-row-click="handlerSelectDetail" 
				   ref="detailTable" size="small"
				   no-data-text="点击上方订单后查看明细">
			</Table>
        </Card>

        <Modal v-model="checkModal" title="验收结果" :mask-closable="false" width="55">
          <Form ref="checkForm" :model="checkFormItem" :label-width="100">
              <Row v-if="showCheckDetail">
                  <i-col span="8">
                    <FormItem label="商品名称">
                        <Input v-model="checkFormItem.goodsName" :readonly="true"/>
                    </FormItem>
                  </i-col>
                  <i-col span="8">
                    <FormItem label="规格">
                        <Input  v-model="checkFormItem.spec" :readonly="true"/>
                    </FormItem>
                  </i-col>
                  <i-col span="8">
                    <FormItem label="生产企业">
                        <Input v-model="checkFormItem.factoryName" :readonly="true"/>
                    </FormItem>
                  </i-col>
              </Row>
              <Row v-if="showCheckDetail">
                  <i-col span="8">
                    <FormItem label="退货数量">
                        <Input :number="true" v-model="checkFormItem.backQuantity" :readonly="true"/>
                    </FormItem>
                  </i-col>
                  <i-col span="8">
                    <FormItem label="合格数量" :error="rightQuantityError">
                        <Input :number="true" v-model="checkFormItem.rightQuantity" @on-change="rightQuantityChange" />
                    </FormItem>
                  </i-col>
                  <i-col span="8">
                    <FormItem label="不合格数量">
                        <Input :number="true" v-model="checkFormItem.badQuantity" :readonly="true" />
                    </FormItem>
                  </i-col>
              </Row>
              <Row v-if="showCheckDetail">
                  <i-col span="12">
                    <FormItem label="不合格品处置方案">
                        <Input v-model="checkFormItem.badPlan" />
                    </FormItem>
                  </i-col>
                  <i-col span="12">
                    <FormItem label="不合格原因">
                        <Input v-model="checkFormItem.badReason" />
                    </FormItem>
                  </i-col>
              </Row>
              <Row>
                  <i-col span="8">
                    <FormItem label="温控方式验收">
                        <option-select optionType="TEMPER_CONTROL" v-model="checkFormItem.checkTempMethod"></option-select>
                    </FormItem>
                  </i-col>
                  <i-col span="8">
                    <FormItem label="验收时间">
                        <DatePicker format="yyyy-MM-dd HH:mm:ss" type="datetime" v-model="checkFormItem.checkTime" />
                    </FormItem>
                  </i-col>
                  <i-col span="8">
                    <FormItem label="验收结果">
                        <Input v-model="checkFormItem.checkResult" />
                    </FormItem>
                  </i-col>
              </Row>
          </Form>
          <Row slot="footer">
                <ButtonGroup>
                    <Button type="success" icon="checkmark" :loading="checkLoading" @click="checkSuccess">确认验收</Button>
                    <Button type="ghost" icon="reply" :loading="checkLoading" @click="checkCancel">取消</Button>
                </ButtonGroup>
            </Row>
      </Modal>

    </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';
import warehouseSelect from '@/views/selector/warehouse-select.vue';
import customerSelect from '@/views/selector/customer-select.vue';
import saleSelect from '@/views/selector/sale-select.vue';
import optionSelect from '@/views/selector/option-select.vue';
import goodsSpecTags from '@/views/goods/goods-spec-tabs.vue';

export default {
    name: 'sell-back-quality-check',
    components: {
        warehouseSelect,
        customerSelect,
        saleSelect,
        optionSelect,
        goodsSpecTags
    },
    data() {
        const statusShow = function(h, row) {
            let status = row.status;
            let label = '';
            let color = '';
            switch(status) {
                case 'INIT':
                    label = '初始制单';
                    color = '#5cadff';
                    break;
                case 'INIT_SALE_CHECKED': 
                    label = '销售已审';
                    color = '#2d8cf0';
                    break;
                case 'INIT_QUALITY_CHECKED': 
                    label = '质管已审待收货';
                    color = '#ff9900';
                    break;
                case 'BACK_RECEIVE': 
                    label = '退货已收货';
                    color = 'rgb(107, 175, 158)';
                    break;
                case 'QUALITY_CHECKED': 
                    label = '质量复核完成';
                    color = '#19be6b';
                    break;
                case 'FINAL_CHECKED': 
                    label = '终审完成';
                    color = '#ed3f14';
                    break;
            }
            return h('Tag', {
                props: {
                    type: 'dot',
                    color: color
                }
            }, label);
        };
        return {
            loading: false,
            dateRange: [
                moment().add(-1, 'w').format('YYYY-MM-DD'),
                moment().add(1, 'd').format('YYYY-MM-DD')
            ],
            warehouseId: '',
            customerId: '',
            saleId: '',
            orderList: [],
            orderColumns: [
                {
                    title: '制单时间',
                    key: 'createdTime',
                    width: 140,
                    render: (h, params) => {
                        return params.row.createdTime ? moment(params.row.createdTime).format('YYYY-MM-DD HH:mm') : '';
                    }
                },
                {
                    title: '当前状态',
                    key: 'status',
                    width: 160,
                    render: (h, params) => {
                        return statusShow(h, params.row);
                    }
                },
                {
                    title: '关联销售单号',
                    key: 'refOrderNo',
                    width: 180
                },
                {
                    title: '客户',
                    key: 'customerName',
                    width: 180
                },
                {
                    title: '客户代表',
                    key: 'customerRepName',
                    width: 180
                },
                {
                    title: '仓库点',
                    key: 'warehouseName',
                    width: 140
                },
                {
                    title: '销售员',
                    key: 'saleName',
                    width: 140
                },
                {
                    title: '退货总金额',
                    key: 'totalAmount',
                    width: 150,
                    render: (h, params) => {
                        return h('strong', {
                            style: {
                                color: 'red'
                            }
                        }, params.row.totalAmount);
                    }
                },
                {
                    title: '退货总成本价',
                    key: 'totalCostAmount',
                    width: 150,
                    render: (h, params) => {
                        return h('strong', {
                            style: {
                                color: 'red'
                            }
                        }, params.row.totalCostAmount);
                    }
                },
                {
                    title: '免零金额',
                    key: 'freeAmount',
                    width: 150,
                    render: (h, params) => {
                        return h('strong', {
                            style: {
                                color: 'red'
                            }
                        }, params.row.freeAmount);
                    }
                },
                {
                    title: '退货原因',
                    key: 'backReason',
                    width: 200
                },
                {
                    title: '系统单号',
                    key: 'orderNumber',
                    width: 180,
                },
                {
                    title: '制单人',
                    key: 'createdBy',
                    width: 120
                },
                {
                    title: '销售经理',
                    key: 'backSaleUser',
                    width: 120,
                },
                {
                    title: '销售经理意见',
                    key: 'backSaleResult',
                    width: 200
                },
                {
                    title: '质管经理',
                    key: 'backQualityUser',
                    width: 120,
                },
                {
                    title: '质管经理意见',
                    key: 'backQualityResult',
                    width: 200
                }
            ],
            detailLoading: false,
            details: [],
            detailColumns: [
                {
                    title: '货号',
                    key: 'goodsId',
                    width: 120
                },
                {
                    title: '商品名称',
                    key: 'goodsName',
                    width: 200
                },
                {
                    title: '批次号',
                    key: 'batchCode',
                    width: 150
                },
                {
                    title: '产地',
                    key: 'origin',
                    width: 150
                },
                // {
                //     title: '剂型',
                //     key: 'jx',
                //     width: 120
                // },
                {
                    title: '规格',
                    key: 'spec',
                    width: 120,
                    render: (h, params) =>　{
                        return h(goodsSpecTags, {
                            props: {
                                tags: params.row.goods.goodsSpecs,
                                color: 'blue'
                            }
                        });
                    }
                },
                {
                    title: '生产企业',
                    key: 'factoryName',
                    width: 150,
                    render: (h, params) => {
                        return h('span', params.row.goods.factoryName);
                    }
                },
                {
                    title: '单位',
                    key: 'unitName',
                    width: 80
                },
                {
                    title: '退货数量',
                    key: 'backQuantity',
                    width: 120
                },
                {
                    title: '合格数量',
                    key: 'rightQuantity',
                    width: 120
                },
                {
                    title: '不合格数量',
                    key: 'badQuantity',
                    width: 120
                },
                {
                    title: '退货单价',
                    key: 'backPrice',
                    width: 120
                },
                {
                    title: '金额',
                    key: 'amount',
                    width: 120
                },
                {
                    title: '退款成本',
                    key: 'costAmount',
                    width: 120
                },
                {
                    title: '有效期至',
                    key: 'expDate',
                    width: 150,
                    render: (h, params) => {
                        return params.row.expDate ? moment(params.row.expDate).format('YYYY-MM-DD') : '';
                    }
                },
                {
                    title: '生产日期',
                    key: 'productDate',
                    width: 150,
                    render: (h, params) => {
                        return params.row.productDate ? moment(params.row.productDate).format('YYYY-MM-DD') : '';
                    }
                },
                {
                    title: '库位',
                    key: 'location',
                    width: 120
                },
                {
                    title: '税率',
                    key: 'taxRate',
                    width: 100
                },
                {
                    title: '是否已验收',
                    key: 'checkStatus',
                    width: 120,
                    render: (h, params) => {
                        let checkStatus = params.row.checkStatus;
                        let label = checkStatus ? '是' : '否';
                        let color = checkStatus ? 'green' : 'red';
                        return h('Tag', {
                            props: {
                                type: 'dot',
                                color: color
                            }
                        }, label);
                    }
                },
                {
                    title: '验收温控方式',
                    key: 'checkTempMethodName',
                    width: 100
                },
                {
                    title: '验收员',
                    key: 'checkUser',
                    width: 120
                }, 
                {
                    title: '验收时间',
                    key: 'checkTime',
                    width: 150,
                    render: (h, params) => {
                        return params.row.checkTime ? moment(params.row.checkTime).format('YYYY-MM-DD HH:mm') : '';
                    }
                },
                {
                    title: '验收结果',
                    key: 'checkResult',
                    width: 200
                }
            ],
            currOrderRow: {},
            currDetailRow: {},
            checkFormItem: {
                backOrderId: '',
                detailId: '',
                goodsName: '',
                spec: '',
                factoryName: '',
                backQuantity: '',
                rightQuantity: '',
                badQuantity: '',
                badPlan: '',
                badReason: '',
                checkTempMethod: '',
                checkTime: moment().format('YYYY-MM-DD HH:mm:ss'),
                checkResult: ''
            },
            showCheckDetail: false,
            checkModal: false,
            checkLoading: false,
            rightQuantityError: ''
        }
    },
    mounted() {
        this.queryBackOrderList();
    },
    methods: {
        queryBackOrderList() {
            let reqData = {
                createdStartTime: this.dateRange[0],
                createdEndTime: this.dateRange[1],
                warehouseId: this.warehouseId,
                customerId: this.customerId,
                saleId: this.saleId,
                statusList: ['BACK_RECEIVE', 'QUALITY_CHECKED']
            };
            this.loading = true;
            let self = this;
            util.ajax.post('/sell/back/list', reqData)
                .then((response) => {
                    self.loading = false;
                    self.orderList = response.data;
                    self.currOrderRow = {};
                    self.details = [];
                    self.$refs.orderTable.clearCurrentRow();
                })
                .catch((error) => {
                    self.loading = false;
                    util.errorProcessor(self, error);
                })
        },
        queryBackOrderDetails(orderId) {
            if(!orderId) {
                return;
            }
            this.detailLoading = true;
            let self = this;
            util.ajax.get('/sell/back/'+ orderId + '/details')
                .then((response) => {
                    self.detailLoading = false;
                    self.details = response.data;
                    self.currDetailRow = {};
                    this.$refs.detailTable.clearCurrentRow();
                })
                .catch((error) => {
                    self.detailLoading = false;
                    util.errorProcessor(self, error);
                })
        },
        handleSelectOrder(row, index) {
            this.currOrderRow = row;
            if(!row.id) {
                this.currOrderRow = {};
                this.details = [];
                this.$refs.orderTable.clearCurrentRow();
            }else {
                this.queryBackOrderDetails(row.id);
            }
        },
        handlerSelectDetail(row, index) {
            this.currDetailRow = row;
            if(!row.id) {
                this.currDetailRow = {};
                this.$refs.detailTable.clearCurrentRow();
            }
        },
        rightQuantityChange() {
            this.rightQuantityError = '';
            let rightQuantity = this.checkFormItem.rightQuantity && !isNaN(this.checkFormItem.rightQuantity) 
                ? this.checkFormItem.rightQuantity : 0;
            let backQuantity = this.checkFormItem.backQuantity;
            if (rightQuantity < 0 || rightQuantity > backQuantity) {
                this.rightQuantityError = '合格数量不能小于0且不能大于退货数量.';
                return;
            }
            this.checkFormItem.badQuantity = backQuantity - rightQuantity;
        },
        checkOneDetailBtn() {
            if(!this.currDetailRow.id) {
                this.$Message.info('请先选择需要验收的详情信息');
                return;
            }
            this.checkFormItem = {
                backOrderId: this.currOrderRow.id,
                detailId: this.currDetailRow.id,
                goodsName: this.currDetailRow.goodsName,
                spec: this.currDetailRow.spec,
                factoryName: this.currDetailRow.factoryName,
                backQuantity: this.currDetailRow.backQuantity,
                rightQuantity: this.currDetailRow.rightQuantity,
                badQuantity: this.currDetailRow.badQuantity,
                badPlan: this.currDetailRow.badPlan,
                badReason: this.currDetailRow.badReason,
                checkTempMethod: this.currDetailRow.checkTempMethod,
                checkTime: this.currDetailRow.checkTime ? this.currDetailRow.checkTime : moment().format('YYYY-MM-DD HH:mm:ss'),
                checkResult: this.currDetailRow.checkResult
            };
            this.rightQuantityError = '';
            this.showCheckDetail = true;
            this.checkModal = true;
        },
        unCheckOneDetailBtn() {
            if (!this.currDetailRow.id) {
                this.$Message.info('请先选择需要取消验收的详情');
                return;
            }
            if (!this.currDetailRow.checkStatus) {
                this.$Message.info('当前详情还没有验收通过，不需要取消.');
                return;
            }
            let reqData = {
                backOrderId: this.currOrderRow.id,
                detailId: this.currDetailRow.id
            };
            let self = this;
            this.$Modal.confirm({
                title: '取消验收确认',
                content: '是否确认取消验收当前选择的商品信息？',
                onCancel: () => {},
                onOk: () => {
                    self.detailLoading = true;
                    util.ajax.put('/sell/back/quality/check/cancel', reqData)
                        .then((response) => {
                            self.detailLoading = false;
                            self.$Message.success('取消验收成功');
                            self.queryBackOrderDetails(self.currOrderRow.id);
                        })
                        .catch((error) => {
                            self.detailLoading = false;
                            util.errorProcessor(self, error);
                        })
                }
            });
        },
        checkOrderBtn() {
            if(!this.currOrderRow.id) {
                this.$Message.info('请先选择需要验收的退货单信息');
                return;
            }
            this.checkFormItem = {
                backOrderId: this.currOrderRow.id,
                badPlan: this.currDetailRow.badPlan,
                badReason: this.currDetailRow.badReason,
                checkTempMethod: this.currDetailRow.checkTempMethod,
                checkTime: this.currDetailRow.checkTime ? this.currDetailRow.checkTime : moment().format('YYYY-MM-DD HH:mm:ss'),
                checkResult: this.currDetailRow.checkResult
            };
            this.rightQuantityError = '';
            this.showCheckDetail = false;
            this.checkModal = true;
        },

        checkOrderCancel() {
            if (!this.currOrderRow.id) {
                this.$Message.info('请先选择需要取消验收的订单信息.');
                return;
            }
            let reqData = {
                backOrderId: this.currOrderRow.id
            };
            let self = this;
            this.$Modal.confirm({
                title: '取消验收确认',
                content: '是否确认需要取消验收当前选择的订单？',
                onCancel: () => {},
                onOk: () => {
                    self.loading = true;
                    util.ajax.put('/sell/back/quality/check/cancel', reqData)
                        .then((response) => {
                            self.loading = false;
                            self.$Message.success('取消验收成功');
                            self.queryBackOrderList();
                        })
                        .catch((error) => {
                            self.loading = false;
                            util.errorProcessor(self, error);
                        })
                }
            }); 
        },
        checkSuccess() {
            let self = this;
            this.checkLoading = true;
            this.checkFormItem.status = 'QUALITY_CHECKED';
            util.ajax.post('/sell/back/check', this.checkFormItem)
                .then((response) => {
                    self.checkLoading = false;
                    self.$Message.success('验收成功');
                    self.checkCancel();
                    if (self.checkFormItem.detailId) {
                        self.queryBackOrderDetails(self.checkFormItem.backOrderId);
                    }else {
                        self.queryBackOrderList();
                    }
                })
                .catch((error) => {
                    self.checkLoading = false;
                    util.errorProcessor(self, error);
                })
        },
        checkCancel() {
            this.checkFormItem = {
                backOrderId: '',
                detailId: '',
                goodsName: '',
                spec: '',
                factoryName: '',
                backQuantity: '',
                rightQuantity: '',
                badQuantity: '',
                badPlan: '',
                badReason: '',
                checkTempMethod: '',
                checkTime: moment().format('YYYY-MM-DD HH:mm'),
                checkResult: ''
            };
            this.rightQuantityError = '';
            this.showCheckDetail = false;
            this.checkModal = false;
        },

        removeBackOrder() {
            if(!this.currOrderRow.id) {
                this.$Message.info('请先选择需要删除的订单！');
                return;
            }
            let self = this;
            this.$Modal.confirm({
                title: '删除确认',
                content: '是否确认删除:' + self.currOrderRow.orderNumber + '这笔销售退货单，一经删除则不能再恢复.',
                onCancel: () => {},
                onOk: () => {
                    self.loading = true;
                    util.ajax.delete('/sell/back/delete/' + self.currOrderRow.id)
                        .then((response) => {
                            self.loading = false;
                            self.$Message.success('销售退货单删除成功!');
                            self.queryBackOrderList();
                        })
                        .catch((error) => {
                            self.loading = false;
                            util.errorProcessor(self, error);
                        })
                }
            });
        }
    }
  
}
</script>

<style >

</style>

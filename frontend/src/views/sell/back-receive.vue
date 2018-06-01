<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Card >
            <p slot="title">
                <Icon type="android-car"></Icon>
                销售退出收货
            </p>
            <div slot="extra">
                <ButtonGroup size="small">
                    <Button type="primary" icon="search" :loading="loading" @click="queryBackOrderList">查询</Button>
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

            <Table border highlight-row disabled-hover height="350"
                   :columns="detailColumns" :data="details" :loading="detailLoading" 
				   ref="detailTable" size="small"
				   no-data-text="点击上方订单后查看明细">
			</Table>
        </Card>

        <Modal v-model="backReceiveModal" width="50" :mask-closable="false" title="销售退单收货登记" >
            <Form ref="receiveForm" :model="receiveForm" :label-width="90">
                <Row>
                    <i-col span="12">
                        <FormItem label="系统单号">
                            <Input v-model="receiveForm.orderNumber" readonly />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="送货单号">
                            <Input v-model="receiveForm.shipNo" />
                        </FormItem>
                    </i-col>
                </Row>
                <Row>
                    <i-col span="12">
                        <FormItem label="运输日期">
                            <DatePicker v-model="receiveForm.shipDate" type="date" placement="bottom-end" placeholder="运输日期"></DatePicker>
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="收货日期">
                            <DatePicker v-model="receiveForm.receiveDate" type="date" placement="bottom-end" placeholder="收货日期"></DatePicker>
                        </FormItem>
                    </i-col>
                </Row>
                <Row>
                    <i-col span="12">
                        <FormItem label="运输方式">
                            <option-select optionType="SHIP_METHOD" v-model="receiveForm.shipMethod"></option-select>
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="运输工具">
                            <option-select optionType="SHIP_TOOL" v-model="receiveForm.shipTool"></option-select>
                        </FormItem>
                    </i-col>
                </Row>
                <Row>
                    <i-col span="12">
                        <FormItem label="车牌号">
                            <Input v-model="receiveForm.shipCarNo" />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="驾驶员姓名">
                            <Input v-model="receiveForm.shipDriverName" />
                        </FormItem>
                    </i-col>
                </Row>
                <Row>
                    <i-col span="12">
                        <FormItem label="承运单位">
                            <ship-company-select v-model="receiveForm.shipCompanyId" ></ship-company-select>
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="发货地址">
                            <Input v-model="receiveForm.shipStartAddress" />
                        </FormItem>
                    </i-col>
                </Row>
                <Row>
                    <i-col span="12">
                        <FormItem label="收货温度">
                            <Input v-model="receiveForm.receiveTemper" number />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="到货数量">
                            <Input v-model="receiveForm.receiveQuantity" />
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
            <Row slot="footer">
                <ButtonGroup>
                    <Button type="success" icon="checkmark" :loading="receiveLoading" @click="receiveSubmit">确认收货</Button>
                    <Button type="ghost" icon="reply" :loading="receiveLoading" @click="receiveCancel">取消</Button>
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
import shipCompanySelect from '@/views/selector/ship-company-select.vue';

export default {
name: 'sell-back-receive',
components: {
    warehouseSelect,
    customerSelect,
    saleSelect,
    optionSelect,
    shipCompanySelect
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
            },
            {
                title: '退货收货',
                key: 'action',
                width: 100,
                fixed: 'right',
                render: (h,params) => {
                    let self = this;
                    let row = params.row;
                    return h('Button', {
                        props: {
                            type: 'primary',
                            size: 'small',
                            icon: 'edit'
                        },
                        on: {
                            click: () => {
                                self.receiveForm = {
                                    id: row.id,
                                    orderNumber: row.orderNumber,
                                    shipNo: row.shipNo,
                                    shipDate: row.shipDate ? row.shipDate : moment().format('YYYY-MM-DD'),
                                    receiveDate: row.receiveDate ? row.receiveDate : moment().format('YYYY-MM-DD'),
                                    shipMethod: row.shipMethod,
                                    shipTool: row.shipTool,
                                    shipCarNo: row.shipCarNo,
                                    shipDriverName: row.shipDriverName,
                                    shipCompanyId: row.shipCompanyId,
                                    shipStartAddress: row.shipStartAddress,
                                    receiveTemper: row.receiveTemper,
                                    receiveQuantity: row.receiveQuantity ? row.receiveQuantity : row.totalQuantity,
                                };
                                self.backReceiveModal = true;
                            }
                        }
                    }, '登记收货');
                }
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
            {
                title: '剂型',
                key: 'jx',
                width: 120
            },
            {
                title: '规格',
                key: 'spec',
                width: 120
            },
            {
                title: '生产企业',
                key: 'factoryName',
                width: 150
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
            }
        ],
        currOrderRow: {},
        backReceiveModal: false,
        receiveForm: {
            id: '',
            orderNumber: '',
            shipNo: '',
            shipDate: moment().format('YYYY-MM-DD'),
            receiveDate: moment().format('YYYY-MM-DD'),
            shipMethod: '',
            shipTool: '',
            shipCarNo: '',
            shipDriverName: '',
            shipCompanyId: '',
            shipStartAddress: '',
            receiveTemper: '',
            receiveQuantity: ''
        },
        receiveLoading: false
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
            statusList: ['INIT_QUALITY_CHECKED', 'BACK_RECEIVE', 'QUALITY_CHECKED']
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

    receiveCancel() {
        this.receiveForm = {
            id: '',
            orderNumber: '',
            shipNo: '',
            shipDate: moment().format('YYYY-MM-DD'),
            receiveDate: moment().format('YYYY-MM-DD'),
            shipMethod: '',
            shipTool: '',
            shipCarNo: '',
            shipDriverName: '',
            temperControlId: '',
            shipStartAddress: '',
            receiveTemper: '',
            receiveQuantity: ''
        };
        this.backReceiveModal = false;
    },

    receiveSubmit() {
        let self = this;
        if (!this.receiveForm.id) {
            this.$Message.warning('获取订单标识失败, 请刷新页面后重试');
            return;    
        }
        this.$Modal.confirm({
            title: '收货确认',
            content: '是否已经确认收到该笔退货单的货物并登记信息输入完整？',
            onCancel: () => {},
            onOk: () => {
                self.receiveLoading = true;
                util.ajax.put('/sell/back/receive', self.receiveForm)
                    .then((response) => {
                        self.receiveLoading = false;
                        self.$Message.success('收货信息保存成功.');
                        self.queryBackOrderList();
                        self.receiveCancel();
                    })
                    .catch((error) => {
                        self.receiveLoading = false;
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




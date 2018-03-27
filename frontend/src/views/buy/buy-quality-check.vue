<template>
  <div>
      <Card>
          <p slot="title">入库质量验收</p>
          <div slot="extra">
              <ButtonGroup>
                <Button size="small" type="primary" icon="ios-search" >查询</Button>
                <Button size="small" >抽样检查</Button>
                <Button size="small" type="success" icon="ios-checkmark" >验收</Button>
                <Button size="small" type="error" icon="close-round">取消验收</Button>
              </ButtonGroup>
          </div>
          
        <Form ref="searchForm" :model="query" :label-width="100">
                <Row type="flex" justify="start">
                <FormItem label="收货日期">
                    <DatePicker size="small" v-model="dateRange" type="daterange" placement="bottom-end" placeholder="收货日期" style="width:180px"></DatePicker>
                </FormItem>
                <FormItem label="仓库">
                    <Select size="small"  v-model="query.warehouseId" filterable clearable
                            placeholder="请选择仓库点" >
                            <Option v-for="item in warehouseList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                        </Select>
                </FormItem>
                <FormItem label="供应商">
                    <supplier-select v-model="query.supplierId" size="small"></supplier-select>
                </FormItem>
                <FormItem label="状态">
                    <Select size="small" v-model="query.status" placeholder="状态">
                            <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                    </Select>
                </FormItem>
                </Row>
        </Form>
          <div>
              <Table ref="orderTable" border highlight-row disabled-hover height="250" style="width: 100%" 
                    :columns="orderListColumns" :data="orderList" size="small" 
                    :loading="orderLoading" 
                    @on-row-click="handleSelectOrder" 
				    no-data-text="输入查询条件, 点击上方查询按钮进行查询">
                </Table>
          </div>

          <div class="detail-div">
              <ButtonGroup>
                  <Button size="small" type="ghost">增加商品</Button>
                  <Button size="small">增加批次</Button>
                  <Button size="small" type="error">删除</Button>
                  <Button size="small" type="success">保存</Button>
              </ButtonGroup>
              <Table border highlight-row height="300" :loading="detailLoading" 
                   :columns="detailColumns" :data="detailList" size="small" 
                   ref="detailTable" style="width: 100%;" 
                   no-data-text="点击上方订单后查看明细">
                <div slot="footer">
                    <h3 class="padding-left-20" >
                        <b>到货数量:</b> {{ totalReceiveCount }} 
                        <b class="margin-left-30">入库数量:</b> {{ totalInCount }}
                        <b class="margin-left-30">合格数量:</b> {{ totalRightCount }}
                        <b class="margin-left-30">不合格数量:</b> {{ totalErrorCount }}
                        <b class="margin-left-30">采集数量:</b> {{ totalCheckCount }}
                    </h3>
                </div>
            </Table>
          </div>
      </Card>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import supplierSelect from "@/views/selector/supplier-select.vue";

export default {
    name: 'buy-quality-check',
    components: {
        supplierSelect
    },
    data() {
        return {
            statusOptions: [
                {key: 'ALL', name: '所有'},
                {key: 'CHECKING', name: '未验收'},
                {key: 'CHECKED', name: '已验收'}
            ],
            query: {
                warehouseId: '',
                supplierId: '',
                status: 'CHECKING'
            },
            dateRange: [
                moment().add(-1,'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD')
            ],
            warehouseList: [],
            orderLoading: false,
            orderList: [],
            orderListColumns: [
                {
                    title: '序号',
                    type: 'index',
                    width: 60
                },
                {
                    title: '收货时间',
                    key: 'receiveDate',
                    width: 100,
                    render: (h, params) => {
                        let receiveDate = params.row.receiveDate;
                        if(receiveDate) {
                            return moment(receiveDate).format("YYYY-MM-DD");
                        }else {
                            return '';
                        }
                    }
                },
                {
                    title: '入库仓库',
                    key: 'warehouseName',
                    width: 120
                },
                {
                    title: '供应商',
                    key: 'supplierName',
                    width: 150
                },
                {
                    title: '供应商代表',
                    key: 'supplierContactName',
                    width: 150
                },
                {
                    title: '采购员',
                    key: 'buyerName',
                    width: 100
                },
                {
                    title: '金额',
                    key: 'amount',
                    width: 100
                },
                {
                    title: '备注',
                    key: 'comment',
                    width: 150
                },
                {
                    title: '收货员',
                    key: 'receiveUserName',
                    width: 100
                },
                {
                    title: '验收员',
                    key: 'checkUserName',
                    width: 100
                },
                {
                    title: '到货温度',
                    key: 'receiveTemp',
                    width: 100
                },
                {
                    title: '验收温度',
                    key: 'checkTemp',
                    width: 100
                },
                {
                    title: '系统单号',
                    key: 'orderNumber',
                    width: 120
                },
                {
                    title: '入库方式',
                    key: 'buyType',
                    width: 120
                },
                {
                    title: '到货时间',
                    key: 'receiveDate',
                    width: 120,
                    render: (h, params) => {
                        let receiveDate = params.row.receiveDate;
                        if(receiveDate) {
                            return moment(receiveDate).format("YYYY-MM-DD");
                        }else {
                            return '';
                        }
                    }
                }
            ],
            detailLoading: false,
            detailList: [],
            detailColumns: [
                {
                    title: "序号",
                    type: 'index',
                    width: 60
                },
                {
                    title: "商品名称",
                    type: 'goodName',
                    width: 150
                },
                {
                    title: "产地",
                    type: 'origin',
                    width: 150
                },
                {
                    title: "剂型",
                    type: 'jx',
                    width: 100
                },
                {
                    title: "规格",
                    type: 'spece',
                    width: 100
                },
                {
                    title: "生产企业",
                    type: 'factoryName',
                    width: 150
                },
                {
                    title: "批准文号",
                    type: 'permit',
                    width: 150
                },
                {
                    title: "存储条件",
                    type: 'storageCondition',
                    width: 150
                },
                {
                    title: "检验报告",
                    type: 'checkRecord',
                    width: 120
                },
                {
                    title: "特殊药品",
                    type: 'specialManaged',
                    width: 120
                },
                {
                    title: "单位",
                    type: 'unitName',
                    width: 120
                },
                {
                    title: "电子监管码",
                    type: 'eCheckCode',
                    width: 120
                },
                {
                    title: "批号",
                    type: 'batchCode',
                    width: 120
                },
                {
                    title: "生产日期",
                    type: 'productDate',
                    width: 150,
                    render: (h, params) => {
                        let productDate = params.row.productDate;
                        return productDate ? moment(productDate).format("YYYY-MM-DD") : '';
                    }
                },
                {
                    title: "有效期至",
                    type: 'expDate',
                    width: 150,
                    render: (h, params) => {
                        let expDate = params.row.expDate;
                        return expDate ? moment(expDate).format("YYYY-MM-DD") : '';
                    }
                },
                {
                    title: "到货数量",
                    type: 'receiveCount',
                    width: 100
                },
                {
                    title: "入库数量",
                    type: 'inCount',
                    width: 100
                },
                {
                    title: "合格数量",
                    type: 'rightCount',
                    width: 100
                },
                {
                    title: "不合格数量",
                    type: 'errorCount',
                    width: 100
                },
                {
                    title: "采集数量",
                    type: 'checkCount',
                    width: 100
                },
                {
                    title: "库区",
                    type: 'warehouseLocation',
                    width: 100
                },
                {
                    title: "验收状态",
                    type: 'checkStatus',
                    width: 100
                },
                {
                    title: "温控方式验收",
                    type: 'checkTempMethod',
                    width: 100
                },
                {
                    title: "验收意见",
                    type: 'checkResult',
                    width: 150
                },
                {
                    title: "验收员",
                    type: 'checkUser',
                    width: 150
                },
                {
                    title: "验收时间",
                    type: 'checkTime',
                    width: 150,
                    render: (h, params) => {
                        let checkTime = params.row.checkTime;
                        return checkTime ? moment(checkTime).format('YYYY-MM-DD HH:mm') : '';
                    }
                }
            ],
            totalReceiveCount: 0,
            totalInCount: 0,
            totalRightCount: 0,
            totalErrorCount: 0,
            totalCheckCount: 0
        }
    },
    methods: {

        handleSelectOrder() {}
    }
}
</script>

<style>
.ivu-form-item {
    margin-bottom: 5px;
}
.detail-div {
    margin-top: 10px;
}

</style>

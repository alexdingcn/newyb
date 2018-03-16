<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="ios-cart"></Icon>
                销售订单制单
            </p> 
            <ButtonGroup slot="extra" size="small">
                <Button type="success" icon="ios-checkmark"> 保存 </Button>
                <Button type="primary" icon="bookmark"> 暂挂 </Button>
                <Button type="info" icon="filing"> 暂挂提取 </Button>
                <Button type="ghost" icon="ios-printer">打印</Button>
            </ButtonGroup>

            <Row type="flex" justify="center">
                <Form ref="sellOrderForm" :model="sellOrderFormData" :label-width="100">
                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="客户" >
                                <Input type="text" readonly v-model="sellOrderFormData.customerName" placeholder="请输入客户名称">
                                    <Button slot="append" icon="ios-search" @click="searchCustomerBtn"></Button>
                                </Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="客户代表" >
                                <Select v-model="sellOrderFormData.customerRepId" filterable clearable placeholder="请选择客户代表" @on-change="customerRepSelChange">
                                    <Option v-for="item in customerRepList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="销售人员" >
                                <Select v-model="sellOrderFormData.salerId" filterable clearable placeholder="请选择销售人员">
                                    <Option v-for="item in salerList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="收货电话" >
                                <Input type="text" readonly v-model="sellOrderFormData.contactPhone" ></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="收货地址" >
                                <Input type="text" readonly v-model="sellOrderFormData.repertoryAddress" ></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="湿控方式" >
                                <Input type="text" v-model="sellOrderFormData.wetMode" placeholder="请输入湿控方式"></Input>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="制单日期" >
                                <DatePicker v-model="sellOrderFormData.createOrderDate" type="date" placeholder="请选择制单日期" ></DatePicker>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="收款日期" >
                                <DatePicker v-model="sellOrderFormData.payOrderDate" type="date" placeholder="请选择收款日期" ></DatePicker>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="收款方式" >
                                <Select v-model="sellOrderFormData.payMode" filterable clearable placeholder="请选择收款方式">
                                    <Option v-for="item in payModeList" :value="item.value" :key="item.value">{{ item.desc }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="加价率" >
                                <InputNumber v-model="sellOrderFormData.markUpRate" :precision="2" style="width:100%"></InputNumber>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="收款金额" >
                                <InputNumber v-model="sellOrderFormData.payAmount" :precision="2" style="width:100%"
                                    :formatter="value => `$ ${value}`.replace(/B(?=(d{3})+(?!d))/g, ',')" 
                                    :parser="value => value.replace(/$s?|(,*)/g, '')"
                                     placeholder="请输入收款金额"></InputNumber>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="抹零" >
                                <InputNumber v-model="sellOrderFormData.notSmallAmount" :precision="2" style="width:100%"></InputNumber>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="提货人" >
                                <Select v-model="sellOrderFormData.consignee" filterable clearable placeholder="请选择提货人">
                                    <Option v-for="item in consigneeList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="16">
                            <FormItem label="备注" >
                                <Input type="text" v-model="sellOrderFormData.comment" placeholder="请输入备注"></Input>
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
            </Row>
            <Tabs type="card">
                <TabPane label="商品信息" name="goodInfo">
                    <Row type="flex" justify="start">
                        <ButtonGroup size="small">
                            <Button type="primary" >添加商品</Button>
                            <Button type="info"> 修改商品 </Button>
                            <Button type="error"> 删除商品 </Button>
                        </ButtonGroup>
                    
                        <Table ref="sellOrderGoodTable" border highlight-row style="width:100%"
                            :columns="sellOrderGoodTableColumn" size="small" 
                            :data="sellOrderGoodTableData" 
                            >
                        </Table>
                    </Row>
                </TabPane>
                <TabPane label="运输登记" name="transportRecord">
                    <Form ref="transportRecordForm" :model="transportRecordFormItem" :label-width="100">
                        <Row type="flex" justify="start">
                            <Col span="6">
                                <FormItem label="发货日期">
                                    <DatePicker v-model="transportRecordFormItem.issuanceDate" type="date" placeholder="请选择发货日期" ></DatePicker>
                                </FormItem>
                            </Col>
                            <Col span="6">
                                <FormItem label="货单号">
                                    <Input type="text" v-model="transportRecordFormItem.transportNo" placeholder="请输入货单号"></Input>
                                </FormItem>
                            </Col>
                            <Col span="12">
                                <FormItem label="发货地址">
                                    <Input type="text" v-model="transportRecordFormItem.issuanceAddress" placeholder="请输入发货地址"></Input>
                                </FormItem>
                            </Col>
                        </Row>
                        <Row type="flex" justify="start">
                            <Col span="6">
                                <FormItem label="存储条件">
                                    <Input type="text" v-model="transportRecordFormItem.storageCondition" placeholder="请输入存储条件"></Input>
                                </FormItem>
                            </Col>
                            <Col span="6">
                                <FormItem label="运输工具">
                                    <Input type="text" v-model="transportRecordFormItem.conveyance" placeholder="请输入运输工具"></Input>
                                </FormItem>
                            </Col>
                            <Col span="12">
                                <FormItem label="承运单位">
                                    <Select v-model="sellOrderFormData.bearCompany" filterable clearable placeholder="请选择承运单位">
                                        <Option v-for="item in bearCompanyList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                    </Select>
                                </FormItem>
                            </Col>
                        </Row>
                        <Row type="flex" justify="start">
                            <Col span="6">
                                <FormItem label="车牌号">
                                    <Input type="text" v-model="transportRecordFormItem.carNumber" placeholder="请输入车牌号"></Input>
                                </FormItem>
                            </Col>
                            <Col span="6">
                                <FormItem label="运输方式">
                                    <Input type="text" v-model="transportRecordFormItem.transportMode" placeholder="请输入运输方式"></Input>
                                </FormItem>
                            </Col>
                            <Col span="6">
                                <FormItem label="承运电话">
                                    <Input type="text" v-model="transportRecordFormItem.bearPhone" placeholder="请输入承运电话"></Input>
                                </FormItem>
                            </Col>
                            <Col span="5">
                                <FormItem label="驾驶员">
                                    <Select v-model="sellOrderFormData.carDriver" filterable clearable placeholder="请选择承运单位">
                                        <Option v-for="item in carDriverList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                    </Select>
                                </FormItem>
                            </Col>
                            <Col span="1">
                                <Button type="info" size="small">驾照</Button>
                            </Col>
                        </Row>
                        <Row type="flex" justify="start">
                            <Col span="6">
                                <FormItem label="起运温度(℃)">
                                    <InputNumber v-model="transportRecordFormItem.transTemperature" :precision="2" style="width: 100%"></InputNumber>
                                </FormItem>
                            </Col>
                            <Col span="6">
                                <FormItem label="件数">
                                    <InputNumber v-model="transportRecordFormItem.goodsCount" :min="0" style="width: 100%"></InputNumber>
                                </FormItem>
                            </Col>
                            <Col span="6">
                                <FormItem label="委托经办人">
                                    <Input type="text" v-model="transportRecordFormItem.operatorName" placeholder="请输入委托经办人"></Input>
                                </FormItem>
                            </Col>
                            <Col span="6">
                                <FormItem label="运行里程(km)">
                                    <InputNumber v-model="transportRecordFormItem.runMileage" :precision="2" :min="0" style="width: 100%"></InputNumber>
                                </FormItem>
                            </Col>
                        </Row>
                        <Row type="flex" justify="start">
                            <Col span="6">
                                <FormItem label="计划启运时间">
                                    <DatePicker v-model="transportRecordFormItem.planStartTime" placement="top" 
                                        type="datetime" format="yyyy-MM-dd HH:mm" placeholder="请选择计划启运时间" >
                                    </DatePicker>
                                </FormItem>
                            </Col>
                            <Col span="6">
                                <FormItem label="计划到货时间">
                                    <DatePicker v-model="transportRecordFormItem.planEndTime" placement="top" 
                                        type="datetime" format="yyyy-MM-dd HH:mm" placeholder="请选择计划到货时间" >
                                    </DatePicker>
                                </FormItem>
                            </Col>
                            <Col span="12">
                                <FormItem label="备注">
                                    <Input type="text" v-model="transportRecordFormItem.comment" ></Input>
                                </FormItem>
                            </Col>
                        </Row>
                    </Form>
                    <Row type="flex" justify="center">
                        <Button type="success" >保存</Button>
                    </Row>
                </TabPane>
            </Tabs>
        </Card>

        <customer-search 
            :showModal="customerSearchModal" 
            @modal-close="customerSearchModalClose" 
            @choosed="customerSearchChoosed">
        </customer-search>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import customerSearch from "@/views/customer/customer-search.vue";

export default {
  name: 'sell_order_make',
  components: {
      customerSearch
  },
  data() {
      return {
          payModeList: [
              {value: 'CASH', desc: '现金'},
              {value: 'REMIT', desc: '汇款'},
              {value: 'ONLINE', desc: '在线支付'}
          ],
          consigneeList: [],
          sellOrderFormData: {
              customerId: '',
              customerName: '',
              customerRepId: -1,
              contactPhone: '',
              repertoryAddress: '',
              salerId: '',
              wetMode: '',
              createOrderDate: '',
              payOrderDate: '',
              payMode: '',
              markUpRate: 0,
              payAmount: 0,
              notSmallAmount: 0,
              consignee: '',
              comment: ''
          },
          sellOrderGoodTableData:[],
          sellOrderGoodTableColumn: [
              {
                  type: 'index',
                  width: 80,
                  title: '序号',
                  align: 'center'
              },
              {
                  title: '货号',
                  key: 'goodNo',
                  align: 'center',
                  sortable: true
              },
              {
                  title: '药名',
                  key: 'goodName',
                  align: 'center',
                  sortable: true
              },
              {
                  title: '剂型',
                  key: 'goodType',
                  align: 'center'
              },
              {
                  title: '规格',
                  key: 'goodguige',
                  align: 'center'
              },
              {
                  title: '生产企业',
                  key: 'shengchange',
                  align: 'center'
              },
              {
                  title: '基药',
                  key: 'jiyao',
                  align: 'center'
              },
              {
                  title: '单位',
                  key: 'danwei',
                  align: 'center'
              },
              {
                  title: '件装量',
                  key: 'jiangNumber',
                  align: 'center'
              },
              {
                  title: '监管',
                  key: 'jianguan',
                  align: 'center'
              },
              {
                  title: '批次号',
                  key: 'jianguan',
                  align: 'center'
              },
              {
                  title: '有效期至',
                  key: 'expiration',
                  align: 'center'
              },
              {
                  title: '存储条件',
                  key: 'cunxu',
                  align: 'center'
              },
              {
                  title: '数量',
                  key: 'shuliang',
                  align: 'center'
              },
              {
                  title: '定价',
                  key: 'dingjia',
                  align: 'center'
              },
              {
                  title: '折扣',
                  key: 'zhekou',
                  align: 'center'
              },
              {
                  title: '赠送',
                  key: 'zengsong',
                  align: 'center'
              },
              {
                  title: '实价',
                  key: 'shijian',
                  align: 'center'
              },
              {
                  title: '件单价',
                  key: 'jiandanjia',
                  align: 'center'
              },
              {
                  title: '金额',
                  key: 'amount',
                  align: 'center'
              },
              {
                  title: '税率',
                  key: 'shuili',
                  align: 'center'
              }
          ],
          customerSearchModal: false,
          customerRepList: [],
          salerList: [],
          bearCompanyList: [],
          carDriverList: [],
          transportRecordFormItem: {
              issuanceDate: '',
              transportNo: '',
              issuanceAddress: '',
              storageCondition: '',
              conveyance: '',
              bearCompany: '',
              carNumber: '',
              transportMode: '',
              bearPhone: '',
              carDriver: '',
              transTemperature: 20,
              goodsCount: 0,
              operatorName: '',
              runMileage: 0,
              planStartTime: '',
              planEndTime: '',
              comment: ''
          }
      }
  },
  methods: {
      searchCustomerBtn() {
          this.customerSearchModal = true;
      },
      customerSearchModalClose() {
        this.customerSearchModal = false;
      },
      customerSearchChoosed(data) {
          if (data && data.id) {
              this.sellOrderFormData.customerId = data.id;
              this.sellOrderFormData.customerName = data.name;
              this.sellOrderFormData.customerRepId = -1;
              this.customerRepList = [];
              this.refreshCustomerRepList(data.id);
          }
      },
      refreshCustomerRepList(customerId) {
          let reqData = {
              customerId: customerId
          };
          util.ajax.get("/customer/rep/list", {params: reqData})
            .then((response) => {
                this.customerRepList = response.data;
            })
            .catch((error) => {
                util.errorProcessor(this, error);
            });
      },
      customerRepSelChange(data) {
          if(data && data.id) {
              this.sellOrderFormData.customerRepId = data.id;
              this.sellOrderFormData.contactPhone = data.contactPhone;
              this.sellOrderGoodTableData.repertoryAddress = data.repertoryAddress;
          }else {
              this.sellOrderFormData.customerRepId = -1;
              this.sellOrderFormData.contactPhone = '';
              this.sellOrderGoodTableData.repertoryAddress = '';
          }
      }

  }
}
</script>

<style>

</style>


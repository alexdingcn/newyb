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
                <Button type="success" icon="ios-checkmark" :loading="sellOrderSaveBtnLoading" @click="sellOrderSaveBtnClick"> 保存 </Button>
                <Button type="info" icon="filing"> 修改提取 </Button>
                <Button type="ghost" icon="ios-printer">打印</Button>
            </ButtonGroup>

            <Row type="flex" justify="center">
                <Form ref="sellOrderForm" :model="sellOrderFormData" :label-width="100" :rules="sellOrderFormValidate">
                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="客户" prop="customerName">
                                <Input type="text" readonly v-model="sellOrderFormData.customerName" placeholder="请输入客户名称">
                                    <Button slot="append" icon="ios-search" @click="searchCustomerBtn"></Button>
                                </Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="客户代表" prop="customerRepId">
                                <Select v-model="sellOrderFormData.customerRepId" filterable clearable placeholder="请选择客户代表" @on-change="customerRepSelChange">
                                    <Option v-for="item in customerRepList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="销售人员" prop="salerId">
                                <Select v-model="sellOrderFormData.salerId" filterable clearable placeholder="请选择销售人员">
                                    <Option v-for="item in salerList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="收货电话" prop="contactPhone">
                                <Input type="text" readonly v-model="sellOrderFormData.contactPhone" ></Input>
                            </FormItem>
                        </Col>
                        <Col span="16">
                            <FormItem label="收货地址" prop="repertoryAddress">
                                <Input type="text" readonly v-model="sellOrderFormData.repertoryAddress" ></Input>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="自定单号" >
                                <Input type="text" v-model="sellOrderFormData.refNo" ></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="制单日期" >
                                <DatePicker v-model="sellOrderFormData.createOrderDate" type="date" placeholder="请选择制单日期" ></DatePicker>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="提货人" >
                                <Input type="text" v-model="sellOrderFormData.takeGoodsUser" placeholder="请输入提货人"></Input>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="湿控方式" >
                                <Select v-model="sellOrderFormData.temperControlId" filterable clearable placeholder="请选择湿控方式">
                                    <Option v-for="item in temperControlList" :value="item.id" :key="item.id">{{ item.value }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="运输方式" >
                                <Select v-model="sellOrderFormData.shipMethod" filterable clearable placeholder="请选择运输方式">
                                    <Option v-for="item in shipMethodList" :value="item.id" :key="item.id">{{ item.value }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="运输工具" >
                                <Select v-model="sellOrderFormData.shipTool" filterable clearable placeholder="请选择运输工具">
                                    <Option v-for="item in shipToolList" :value="item.id" :key="item.id">{{ item.value }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="收款日期" >
                                <DatePicker v-model="sellOrderFormData.payOrderDate" type="date" placeholder="请选择收款日期" ></DatePicker>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="收款方式" >
                                <Select v-model="sellOrderFormData.payMethod" filterable clearable placeholder="请选择收款方式">
                                    <Option v-for="item in payMethodList" :value="item.id" :key="item.id">{{ item.value }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="收款档案编号" >
                                <Input type="text" v-model="sellOrderFormData.payFileNo" ></Input>
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
                                <InputNumber v-model="sellOrderFormData.payAmount" :precision="2" style="width:100%"></InputNumber>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="抹零" >
                                <InputNumber v-model="sellOrderFormData.notSmallAmount" :precision="2" style="width:100%"></InputNumber>
                            </FormItem>
                        </Col>
                    </Row>

                    <Row type="flex" justify="center">
                        <Col span="24">
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
import dataConver from "@/libs/data-conver.js";
import customerSearch from "@/views/customer/customer-search.vue";

export default {
  name: 'sell_order_make',
  components: {
      customerSearch
  },
  data() {
      return {
          payMethodList: [],
          temperControlList: [],
          shipMethodList: [],
          shipToolList: [],
          sellOrderFormData: {
              id: '',
              customerId: '',
              customerName: '',
              customerRepId: -1,
              contactPhone: '',
              repertoryAddress: '',
              salerId: '',
              refNo: '',
              temperControlId: '',
              shipMethod: '',
              shipTool: '',
              createOrderDate: '',
              takeGoodsUser: '',
              payOrderDate: '',
              payMethod: '',
              payFileNo: '',
              markUpRate: 0,
              payAmount: 0,
              notSmallAmount: 0,
              comment: ''
          },
          sellOrderFormValidate: {
              customerName: [
                  {required: true, message: '客户必输', trigger: 'change'}
              ],
              customerRepId: [
                  {required: true, type: 'number', message: '客户代表人必输', trigger: 'change'}
              ],
              salerId: [
                  {required: true, type: 'number', message: '销售员必输', trigger: 'change'}
              ],
              contactPhone: [
                  {required: true, message: '客户代表人联系方式缺失', trigger: 'blur'}
              ],
              repertoryAddress: [
                  {required: true, message: '客户代表人收货地址缺失', trigger: 'blur'}
              ]
          },
          sellOrderSaveBtnLoading: false,
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
          currChooseCustomer: null,
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
  mounted() {
      this.initData();
  },
  methods: {
      initData() {
          this.initOptions();
          this.getSalserList();
      },

      initOptions() {
          let reqData = ['TEMPER_CONTROL', 'PAY_METHOD', 'SHIP_METHOD', 'SHIP_TOOL'];
          util.ajax.post("/options/list", reqData)
            .then((response) => {
              let data = response.data;
              if (data && data.TEMPER_CONTROL) {
                  this.temperControlList = data.TEMPER_CONTROL;
              }
              if (data && data.PAY_METHOD) {
                  this.payMethodList = data.PAY_METHOD;
              }
              if (data && data.SHIP_METHOD) {
                  this.shipMethodList = data.SHIP_METHOD;
              }
              if (data && data.SHIP_TOOL) {
                  this.shipToolList = data.SHIP_TOOL;
              }
          })
          .catch((error) => {
              util.errorProcessor(this, error);
          })
      },
      getSalserList() {
          let result = [
              {
                  id: 1,
                  name: '销售员1'
              },
              {
                  id: 2,
                  name: '销售员2'
              }
          ];
          this.salerList = result;
      },
      searchCustomerBtn() {
          this.customerSearchModal = true;
      },
      customerSearchModalClose() {
        this.customerSearchModal = false;
      },
      customerSearchChoosed(data) {
          this.currChooseCustomer = data;
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
                let defaultItem = this.getCustomerRepDefault();
                if (defaultItem && defaultItem.id) {
                    this.setCustomerRepInfo(defaultItem);
                }
            })
            .catch((error) => {
                util.errorProcessor(this, error);
            });
      },
      getCustomerRepDefault() {
          if (!this.customerRepList || this.customerRepList.length <= 0) {
              return null;
          }
          for (let i=0; i<this.customerRepList.length; i++) {
              let item = this.customerRepList[i];
              if (item.isDefault) {
                  return item;
              }
          }
      },
      customerRepSelChange(data) {
          let customerRep = dataConver.selectObjectById(data, this.customerRepList);
          this.setCustomerRepInfo(customerRep);
      },
      setCustomerRepInfo(customerRep) {
          if(customerRep && customerRep.id) {
              this.sellOrderFormData.customerRepId = customerRep.id;
              this.sellOrderFormData.contactPhone = customerRep.contactPhone;
              this.sellOrderFormData.repertoryAddress = customerRep.repertoryAddress;
          }else {
              this.sellOrderFormData.customerRepId = -1;
              this.sellOrderFormData.contactPhone = '';
              this.sellOrderFormData.repertoryAddress = '';
          }
      },

      sellOrderSaveBtnClick() {
          this.sellOrderSaveBtnLoading = true;
          this.$refs.sellOrderForm.validate(valid => {
              if (!valid) {
                  this.$Message.warning("请检查表单必输项是否完整");
                  this.sellOrderSaveBtnLoading = false;
                  return;
              }
              if (!this.sellOrderFormData.customerId) {
                this.$Message.warning("获取客户信息失败，请重新选择用户");
                this.sellOrderSaveBtnLoading = false;
                return;
              }
              if (!this.sellOrderFormData.customerRepId || this.sellOrderFormData.customerRepId < 0) {
                  this.$Message.warning("请选择客户代表人信息");
                  this.sellOrderSaveBtnLoading = false;
                  return;
              }
              let isEdit = (this.sellOrderFormData.id && this.sellOrderFormData.id > 0); //编辑模式
              if (isEdit) {
                  util.ajax.post("/sell/order/update", this.sellOrderFormData)
                    .then((response) => {
                        this.$Message.success('保存成功');
                        this.orderFormChangeToEditModel(response.data);
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    });
              }else {
                  util.ajax.post("/sell/order/add", this.sellOrderFormData)
                    .then((response) => {
                        this.$Message.success('保存成功');
                        this.orderFormChangeToEditModel(response.data);
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    });
              }
              this.sellOrderSaveBtnLoading = false;
          });
      },
      orderFormChangeToEditModel(data) {
          this.sellOrderFormData = data;
          console.log("change to edit model");
          console.log(data);
          console.log(this.currChooseCustomer);
          if (data.customerId !== this.currChooseCustomer.id) {
              this.$Notice.error({title: '系统异常', desc: '获取客户信息错误, 请确认选择的客户正确'});
          }
          this.sellOrderFormData.customerName = this.currChooseCustomer.name; //设置customerName
          if (data.customerRepId) {
              this.customerRepSelChange(data.customerRepId); //触发下显示客户代表的收货地址和联系方式
          }
      }



  }
}
</script>

<style>

</style>


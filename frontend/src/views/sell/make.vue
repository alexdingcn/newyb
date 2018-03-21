<style lang="less">
@import "../../styles/common.less";
@import "./make.less";
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
                <Button type="info" icon="filing" @click="getOldSellOrderBtnClick"> 修改提取 </Button>
                <Button type="ghost" icon="ios-printer">打印</Button>
            </ButtonGroup>

            <Row type="flex" justify="center">
                <Form ref="sellOrderForm" :model="sellOrderFormData" :label-width="100" :rules="sellOrderFormValidate">
                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="客户" prop="customerName">
                                <Input type="text" readonly v-model="sellOrderFormData.customerName" placeholder="请输入客户名称">
                                    <Button slot="append" icon="ios-search" @click="searchCustomerBtn" :disabled="editeOnlyDisable"></Button>
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
            <Tabs type="line">
                <TabPane label="商品信息" name="goodInfo">
                    <Row type="flex" justify="start">
                        <ButtonGroup size="small">
                            <Button type="primary" :disabled="!editeOnlyDisable" @click="addGoodBtnClick">添加商品</Button>
                            <Button type="success" :disabled="!editeOnlyDisable" :loading="saveGoodBtnLoading" @click="saveGoodBtnClick">保存数据</Button>
                            <Button type="ghost" :disabled="!editeOnlyDisable" :loading="saveGoodBtnLoading" @click="refreshGoodBtnClick">刷新数据</Button>
                        </ButtonGroup>
                    </Row>
                    <Row type="flex" justify="start">
                        <can-edit-table refs="sellOrderGoodTable" 
                            :loading="saveGoodBtnLoading" 
                            v-model="goodTableData" 
                            :hover-show="true" 
                            :edit-incell="true" width="1000"
                            :columns-list="goodTableColumn" 
                            @on-delete="goodRemoveItem">
                        </can-edit-table>
                    </Row>
                </TabPane>
            </Tabs>
        </Card>

        <customer-search 
            :showModal="customerSearchModal" 
            @modal-close="customerSearchModalClose" 
            @choosed="customerSearchChoosed">
        </customer-search>

        <sell-order-search 
            :showModal="sellOrderSearchModal" 
            status="INIT" 
            @modal-close="orderSearchModalClose"
            @choosed="orderSearchChoosed">
        </sell-order-search>

        <good-search 
            :showModal="goodSearchModal" 
            @modal-close="goodSearchModalClose" 
            @choosed="goodSearchChoosed">
        </good-search>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";
import customerSearch from "@/views/customer/customer-search.vue";
import sellOrderSearch from "@/views/sell/sell-order-search.vue";
import goodSearch from "@/views/good/good-search.vue";
import goodExpand from "@/views/good/good-expand.vue";
import canEditTable from '@/views/tables/components/canEditTable.vue';

export default {
  name: 'sell_order_make',
  components: {
      customerSearch,
      sellOrderSearch,
      goodSearch,
      goodExpand,
      canEditTable
  },
  data() {
      return {
          payMethodList: [],
          temperControlList: [],
          shipMethodList: [],
          shipToolList: [],
          editeOnlyDisable: false,
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
          sellOrderSearchModal: false,

          goodSearchModal: false,
          saveGoodBtnLoading: false,
          goodTableData:[],
          goodTableColumn: [
              {
                  type: 'expand',
                  width: 50,
                  fixed: 'left',
                  render: (h, params) => {
                    return h(goodExpand, {
                        props: {
                            detail: params.row.goods
                        }
                    });
                  }
              },
              {
                  key: 'sellOrderId',
                  title: '订单ID',
                  align: 'center',
                  width: 100,
                  fixed: 'left'
              },
              {
                  title: '药名',
                  key: 'goodName',
                  align: 'center',
                  sortable: true,
                  width: 150,
                  fixed: 'left'
              },
              {
                  title: '数量',
                  key: 'quantity',
                  align: 'center',
                  editable: true,
                  width: 150
              },
              {
                  title: '定价',
                  key: 'fixPrice',
                  align: 'center',
                  editable: true,
                  width: 150
              },
              {
                  title: '折扣',
                  key: 'disPrice',
                  align: 'center',
                  editable: true,
                  width: 150
              },
              {
                  title: '赠送',
                  key: 'free',
                  align: 'center',
                  editable: true,
                  width: 150
              },
              {
                  title: '实价',
                  key: 'realPrice',
                  align: 'center',
                  editable: true,
                  width: 150
              },
              {
                  title: '件单价',
                  key: 'singlePrice',
                  align: 'center',
                  editable: true,
                  width: 150
              },
              {
                  title: '金额',
                  key: 'amount',
                  align: 'center',
                  editable: true,
                  width: 150
              },
              {
                  title: '税率',
                  key: 'taxRate',
                  align: 'center',
                  editable: true,
                  width: 150
              },
              {
                  title: '操作',
                  align: 'center',
                  width: 150,
                  key: 'handle',
                  handle: ['delete']
              }
          ],
          currChooseCustomer: null,
          customerSearchModal: false,
          customerRepList: [],
          salerList: []
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
      refreshCustomerRepList(customerId, customerRepId) {
          let reqData = {
              customerId: customerId
          };
          console.log('get customer rep list.');
          util.ajax.get("/customer/rep/list", {params: reqData})
            .then((response) => {
                this.customerRepList = response.data;
                console.log('already get customer list.');
                if (this.customerRepList && this.customerRepList.length > 0) {
                    this.chooseCustomerRep(customerRepId);
                }
            })
            .catch((error) => {
                util.errorProcessor(this, error);
            });
      },
      customerRepSelChange(data) {
          if (data) {
              this.chooseCustomerRep(data);
          }
      },
      chooseCustomerRep(customerRepId) {
          if (customerRepId && customerRepId > 0) {
            let customerRep = dataConver.selectObjectById(customerRepId, this.customerRepList);
            this.setCustomerRepInfo(customerRep);
          }else {
            let defaultItem = this.getCustomerRepDefault();
            if (defaultItem && defaultItem.id) {
                  this.setCustomerRepInfo(defaultItem);
              }
          }
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
      orderSearchModalClose() {
          this.sellOrderSearchModal = false;
      },
      orderSearchChoosed(data) {
          if (data && data.id && data.id > 0) {
              this.currChooseCustomer = data.customer;
              this.orderFormChangeToEditModel(data);
          }
      },
      getOldSellOrderBtnClick() {
        this.sellOrderSearchModal = true;
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
          if (data.customerId !== this.currChooseCustomer.id) {
              this.$Notice.error({title: '系统异常', desc: '获取客户信息错误, 请确认选择的客户正确'});
          }
          this.editeOnlyDisable = true; //编辑模式下的客户信息不能修改
          this.sellOrderFormData.customerName = this.currChooseCustomer.name; //设置customerName
          this.refreshCustomerRepList(data.customerId, data.customerRepId);
          this.refreshGoodsData(data.id);
      },
      
      addGoodBtnClick() {
          this.goodSearchModal = true;
      },
      goodSearchModalClose() {
          this.goodSearchModal = false;
      },
      goodSearchChoosed(goodDetail) {
          let item = {
              id: '',
              sellOrderId: this.sellOrderFormData.id,
              goodId: goodDetail.id,
              goodName: goodDetail.name,
              quantity: 0,
              fixPrice: 0,
              disPrice: 0,
              free: 0,
              realPrice: 0,
              singlePrice: 0,
              amount: 0,
              taxRate: 0,
              goods: goodDetail
          };
          this.goodTableData.push(item);
      },

      refreshGoodsData(sellOrderId) {
          if (sellOrderId && sellOrderId > 0) {
              let reqData = {sellOrderId: sellOrderId};
              this.saveGoodBtnLoading = true;
              util.ajax.get("/sell/detail/list", {params: reqData})
                .then((response) => {
                    this.goodTableData = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
            this.saveGoodBtnLoading = false;
          }
      },
      goodRemoveItem(data, index, removeItem) {
          if (removeItem && removeItem.id && removeItem.id > 0) {
              //联动删除数据库中的值
              util.ajax.delete("/sell/detail/remove/" + removeItem.id)
                .then((response) => {
                    let count = response.data.count;
                    this.$Message.success('成功删除' + (count ? count : 0) + '条记录');
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
          }
      },
      saveGoodBtnClick() {
          if (!this.goodTableData || this.goodTableData.length <= 0) {
              this.$Message.warning('请先新增商品信息');
              return;
          }
          this.saveGoodBtnLoading = true;
          util.ajax.post("/sell/detail/save", this.goodTableData)
            .then((response) => {
                let success = response.data.success;
                let fail = response.data.fail;
                let detais = response.data.detailList;
                this.$Message.success('成功保存' + success + '条记录, 失败' + fail + '条');
                this.goodTableData = detais;
            })
            .catch((error) => {
                util.errorProcessor(this, error);
            });
          this.saveGoodBtnLoading = false;
      },
      refreshGoodBtnClick(){
          this.refreshGoodsData(this.sellOrderFormData.id);
      }

  }
}
</script>

<style>

</style>


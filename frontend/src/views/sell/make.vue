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
                <Button type="success" icon="ios-checkmark" :loading="sellOrderSaveLoading" @click="sellOrderSaveBtnClick"> 保存 </Button>
                <Button icon="bookmark" :loading="sellOrderSaveLoading" @click="tempStorageOrderBtnClick"> 暂挂 </Button>
                <Button type="info" icon="filing" @click="getOldSellOrderBtnClick"> 暂挂提取 </Button>
                <Button type="ghost" icon="ios-printer">打印</Button>
            </ButtonGroup>

            <Row type="flex" justify="center">
                <Form ref="sellOrderForm" :model="sellOrderFormData" :label-width="100" :rules="sellOrderFormValidate">
                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="客户" prop="customerId">
                                <customer-select v-if="!editeOnlyDisable" :disabled="editeOnlyDisable" v-model="sellOrderFormData.customerId" @on-change="customerChange"></customer-select>
                                <Input v-else type="text" :disabled="editeOnlyDisable" v-model="sellOrderFormData.customerName"></Input>
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
                              <sale-select v-model="sellOrderFormData.salerId" ></sale-select>
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
                              <temper-control-select v-model="sellOrderFormData.temperControlId"></temper-control-select>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="运输方式" >
                              <ship-method-select v-model="sellOrderFormData.shipMethod"></ship-method-select>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="运输工具" >
                              <ship-tool-select v-model="sellOrderFormData.shipTool"></ship-tool-select>
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
                              <pay-method-select v-model="sellOrderFormData.payMethod"></pay-method-select>
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
                        <Col span="8">
                            <FormItem label="仓库点" prop="warehouseId">
                                <warehouse-select v-model="sellOrderFormData.warehouseId" :disabled="warehouseDisable" @on-change="warehouseChange"></warehouse-select>
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
            <Tabs type="line">
                <TabPane label="商品信息" name="goodInfo">
                    <Row type="flex" justify="start">
                        <ButtonGroup size="small">
                            <Button type="primary"  @click="addGoodBtnClick">添加商品</Button>
                        </ButtonGroup>
                    </Row>
                    <Table border highlight-row :loading="saveGoodBtnLoading" 
                        :columns="goodTableColumn" :data="goodTableData" 
                        no-data-text="在保存订单信息后点击添加商品按钮添加"
                        ref="sellOrderGoodTable" style="width: 100%;" size="small"
                        @on-row-dblclick="handleRowDbClick">
                        <div slot="footer">
                            <h3 class="padding-left-20" >
                                <b>合计数量: {{totalQuantity}}</b>  <b class="margin-left-50">合计金额:</b> ￥{{ totalAmount }} 
                                <span class="margin-left-50"> 提示: 金额 = (数量 - 赠送) x 实价 x (1 - 折扣%) </span>
                            </h3>
                        </div>
                    </Table>
                </TabPane>
            </Tabs>
        </Card>

        <sell-order-search 
            :showModal="sellOrderSearchModal" 
            status="TEMP_STORAGE" 
            @modal-close="orderSearchModalClose"
            @choosed="orderSearchChoosed">
        </sell-order-search>

        <sell-good-search 
            :showModal="goodSearchModal" 
            :warehouse="chooseWarehouse" 
            @modal-close="goodSearchModalClose" 
            @choosed="goodSearchChoosed">
        </sell-good-search>

        <Modal v-model="goodHistoryModal" width="75" :mask-closable="false" title="商品销售历史价格">
            <sell-good-history :excludedOrderId="historyExcludeId" :detailList="historyDetails"></sell-good-history>
            <div slot="footer"></div>
        </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import dataConver from "@/libs/data-conver.js";
import customerSelect from "@/views/selector/customer-select.vue";
import sellOrderSearch from "@/views/sell/sell-order-search.vue";
import sellGoodSearch from "@/views/sell/sell-good-search.vue";
import goodExpand from "@/views/good/good-expand.vue";
import sellGoodHistory from "./sell-good-history.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import saleSelect from "@/views/selector/sale-select.vue";
import temperControlSelect from "@/views/selector/temper-control-select.vue";
import payMethodSelect from "@/views/selector/pay-method-select.vue";
import shipMethodSelect from "@/views/selector/ship-method-select.vue";
import shipToolSelect from "@/views/selector/ship-tool-select.vue";

export default {
  name: "sell_order_make",
  components: {
    customerSelect,
    sellOrderSearch,
    sellGoodSearch,
    goodExpand,
    sellGoodHistory,
    warehouseSelect,
    saleSelect,
    temperControlSelect,
    payMethodSelect,
    shipToolSelect,
    shipMethodSelect
  },
  data() {
    return {
      editeOnlyDisable: false,
      sellOrderFormData: {
        id: "",
        customerId: "",
        customerName: "",
        customerRepId: -1,
        contactPhone: "",
        repertoryAddress: "",
        salerId: "",
        refNo: "",
        temperControlId: "",
        shipMethod: "",
        shipTool: "",
        createOrderDate: "",
        takeGoodsUser: "",
        payOrderDate: "",
        payMethod: "",
        payFileNo: "",
        markUpRate: 0,
        payAmount: 0,
        notSmallAmount: 0,
        warehouseId: "",
        comment: "",
        details: ''
      },
      sellOrderFormValidate: {
        customerId: [
          {
            required: true,
            type: "number",
            message: "客户必输",
            trigger: "change"
          }
        ],
        customerRepId: [
          {
            required: true,
            type: "number",
            message: "客户代表人必输",
            trigger: "change"
          }
        ],
        salerId: [
          {
            required: true,
            type: "number",
            message: "销售员必输",
            trigger: "change"
          }
        ],
        contactPhone: [
          { required: true, message: "客户代表人联系方式缺失", trigger: "blur" }
        ],
        repertoryAddress: [
          { required: true, message: "客户代表人收货地址缺失", trigger: "blur" }
        ],
        warehouseId: [
          {
            required: true,
            type: "number",
            message: "仓库点必输",
            trigger: "change"
          }
        ]
      },
      sellOrderSaveLoading: false,
      removeSellOrderLoading: false,
      sellOrderSearchModal: false,
      chooseWarehouse: {},
      goodSearchModal: false,
      saveGoodBtnLoading: false,
      warehouseDisable: false,
      goodTableData: [],
      goodTableColumn: [
        {
          type: "expand",
          width: 50,
          fixed: "left",
          render: (h, params) => {
            let repertoryInfo = params.row.repertoryInfo;
            let good = {};
            if (repertoryInfo) {
              good = repertoryInfo.goods;
            }
            let productDate = '';
            if (repertoryInfo.productDate) {
                productDate = moment(repertoryInfo.productDate).format('YYYY-MM-DD');
            }
            let expDate = '';
            if (repertoryInfo.expDate) {
                expDate = moment(repertoryInfo.expDate).format('YYYY-MM-DD');
            }
            return h(goodExpand, {
              props: {
                good: good,
                repertoryInfo: repertoryInfo,
                productDate: productDate,
                expDate: expDate
              }
            })
          }
        },
        {
          title: "药名",
          key: "goodName",
          align: "center",
          sortable: true,
          fixed: "left"
        },
        {
          title: "库存量",
          key: "repertoryQuantity",
          align: "center"
        },
        {
          title: "数量",
          key: "quantity",
          align: "center",
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                value: self.goodTableData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.goodTableData[params.index];
                  row[params.column.key] = event.target.value;
                  self.resetGoodSDataAmount(params.index);
                }
              }
            });
          }
        },
        {
          title: "定价",
          key: "fixPrice",
          align: "center"
        },
        {
          title: "折扣",
          key: "disPrice",
          align: "center",
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                value: self.goodTableData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.goodTableData[params.index];
                  row[params.column.key] = event.target.value;
                  self.resetGoodSDataAmount(params.index);
                }
              }
            });
          }
        },
        {
          title: "赠送",
          key: "free",
          align: "center",
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                value: self.goodTableData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.goodTableData[params.index];
                  row[params.column.key] = event.target.value;
                  self.resetGoodSDataAmount(params.index);
                }
              }
            });
          }
        },
        {
          title: "实价",
          key: "realPrice",
          align: "center",
          width: 150,
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                value: self.goodTableData[params.index][params.column.key],
                icon: "eye"
              },
              on: {
                "on-blur"(event) {
                  let row = self.goodTableData[params.index];
                  row[params.column.key] = event.target.value;
                  row["singlePrice"] = event.target.value;
                  self.resetGoodSDataAmount(params.index);
                },
                "on-click"(event) {
                  let goodId = params.row.goodId;
                  self.openRealPriceHistory(goodId);
                }
              }
            });
          }
        },
        {
          title: "件单价",
          key: "singlePrice",
          align: "center"
        },
        {
          title: "金额",
          key: "amount",
          align: "center"
        },
        {
          title: "税率",
          key: "taxRate",
          align: "center",
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                value: self.goodTableData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.goodTableData[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        }
      ],
      totalQuantity: 0,
      totalAmount: 0,
      currChooseCustomer: null,
      customerRepList: [],
      goodHistory: {},
      historyExcludeId: "",
      historyDetails: [],
      goodHistoryModal: false
    };
  },
  watch: {
    goodTableData(data) {
      if (data && data.length > 0) {
        this.warehouseDisable = true;
        this.totalQuantity = data.reduce((count, item) => {
          if (item.quantity && !isNaN(item.quantity)) {
            return count + parseInt(item.quantity);
          }
        }, 0);
        this.totalAmount = data.reduce((total, item) => {
          if (item.amount && !isNaN(item.amount)) {
            return total + parseFloat(item.amount);
          }
        }, 0);
      }else {
        this.warehouseDisable = false;
        this.totalQuantity = 0;
        this.totalAmount = 0;
      }
    }
  },
  methods: {
    customerChange(customerId, customer) {
      this.currChooseCustomer = customer;
      if (customer && customer.id) {
        this.sellOrderFormData.customerId = customer.id;
        this.sellOrderFormData.customerRepId = -1;
        this.customerRepList = [];
        this.refreshCustomerRepList(customer.id);
      }
    },
    refreshCustomerRepList(customerId, customerRepId) {
      let reqData = {
        customerId: customerId
      };
      util.ajax
        .get("/customer/rep/list", { params: reqData })
        .then(response => {
          this.customerRepList = response.data;
          if (this.customerRepList && this.customerRepList.length > 0) {
            this.chooseCustomerRep(customerRepId);
          }
        })
        .catch(error => {
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
        let customerRep = dataConver.selectObjectById(
          customerRepId,
          this.customerRepList
        );
        this.setCustomerRepInfo(customerRep);
      } else {
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
      for (let i = 0; i < this.customerRepList.length; i++) {
        let item = this.customerRepList[i];
        if (item.isDefault) {
          return item;
        }
      }
    },
    setCustomerRepInfo(customerRep) {
      if (customerRep && customerRep.id) {
        this.sellOrderFormData.customerRepId = customerRep.id;
        this.sellOrderFormData.contactPhone = customerRep.contactPhone;
        this.sellOrderFormData.repertoryAddress = customerRep.repertoryAddress;
      } else {
        this.sellOrderFormData.customerRepId = -1;
        this.sellOrderFormData.contactPhone = "";
        this.sellOrderFormData.repertoryAddress = "";
      }
    },
    tempStorageOrderBtnClick() {
      //暂存订单信息
      this.$refs.sellOrderForm.validate(valid => {
        if(!valid) {
          this.$Message.warning('必输信息需要输入');
          return;
        }
        if (!this.goodTableData || this.goodTableData.length <= 0) {
          this.$Message.warning('需要添加商品');
          return;
        }
        let saleGoodVidate = this.customerCanSaleGoodValidate();
        if (!saleGoodVidate) {
          return; //客户不能经营特殊管理产品
        }
        //两步资料完整，可以直接保存
        this.saveSellOrder('TEMP_STORAGE');
      });
    },
    saveSellOrder(status) {
      this.sellOrderSaveLoading = true;
      this.sellOrderFormData.status = status;
      this.sellOrderFormData.details = this.goodTableData;
      util.ajax.post("/sell/order/save", this.sellOrderFormData)
        .then((response) => {
          this.sellOrderSaveLoading = false;
          this.$Message.success('操作成功');
          this.orderFormChangeToAddModel(); //转化到初始状态
        })
        .catch((error) => {
          this.sellOrderSaveLoading = false;
          util.errorProcessor(this, error);
        })
    },

    sellOrderSaveBtnClick() {
      //保存订单信息
      this.$refs.sellOrderForm.validate(valid => {
        if(!valid) {
          this.$Message.warning('必输信息需要输入');
          return;
        }
        if (!this.goodTableData || this.goodTableData.length <= 0) {
          this.$$Message.warning('需要添加商品');
          return;
        }
        let saleGoodVidate = this.customerCanSaleGoodValidate();
        if (!saleGoodVidate) {
          return; //客户不能经营特殊管理产品
        }
        this.$Modal.confirm({
          title: '保存提交确认',
          content: '确认数据是否完整正确，提交后不能修改.',
          onOk: () => {
            this.saveSellOrder('INIT');
          },
          onCancel: () => {
          }
        })
      });
    },

    customerCanSaleGoodValidate() {
      //检查客户是否是可经营特殊管控的商品的，如果不是，需要检查选择的商品列表中是否存在特殊管控的商品
      if (!this.currChooseCustomer || !this.currChooseCustomer.canSaleSpecial) {
        for (let i=0; i<this.goodTableData.length; i++) {
          let goodItem = this.goodTableData[i].repertoryInfo ? this.goodTableData[i].repertoryInfo.goods : null;
          if (goodItem && goodItem !== null && goodItem.SpecialManaged) {
              this.$Modal.error(
                {
                  title: '客户商品选择限制', 
                  content: this.currChooseCustomer.name + '不能经营特殊管控商品, 但是商品列表中存在管控商品:' + goodItem.name
                });
              return false;
          }
        }
      }
      return true;
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

    orderFormChangeToEditModel(data) {
      this.sellOrderFormData = data;
      if (data.customerId !== this.currChooseCustomer.id) {
        this.$Notice.error({
          title: "系统异常",
          desc: "获取客户信息错误, 请确认选择的客户正确"
        });
      }
      this.sellOrderFormData.customerName = this.currChooseCustomer.name;
      this.editeOnlyDisable = true; //编辑模式下的客户信息不能修改
      this.warehouseDisable = true;
      this.refreshCustomerRepList(data.customerId, data.customerRepId);
      this.refreshGoodsData(data.id);
    },
    orderFormChangeToAddModel() {
      this.editeOnlyDisable = false;
      this.warehouseDisable = false;
      this.sellOrderFormData = {
        id: "",
        customerId: "",
        customerName: "",
        customerRepId: '',
        contactPhone: "",
        repertoryAddress: "",
        salerId: "",
        refNo: "",
        temperControlId: "",
        shipMethod: "",
        shipTool: "",
        createOrderDate: "",
        takeGoodsUser: "",
        payOrderDate: "",
        payMethod: "",
        payFileNo: "",
        markUpRate: 0,
        payAmount: 0,
        notSmallAmount: 0,
        warehouseId: "",
        comment: "",
        details: ''
      };
      this.goodTableData = [];
    },
    refreshGoodsData(sellOrderId) {
      if (sellOrderId && sellOrderId > 0) {
        let reqData = { sellOrderId: sellOrderId };
        this.saveGoodBtnLoading = true;
        util.ajax
          .get("/sell/detail/list", { params: reqData })
          .then(response => {
            this.goodTableData = response.data;
            this.getGoodHistoryPrice();
          })
          .catch(error => {
            util.errorProcessor(this, error);
          });
        this.saveGoodBtnLoading = false;
      }
    },
    warehouseChange(warehouseId, warehouseItem) {
      this.chooseWarehouse = warehouseItem;
    },

    addGoodBtnClick() {
      if(!this.sellOrderFormData.warehouseId || this.sellOrderFormData.warehouseId <= 0) {
        this.$Message.warning('请先选择对应仓库点');
        return;
      }
      this.goodSearchModal = true;
    },
    goodSearchModalClose() {
      this.goodSearchModal = false;
    },
    goodSearchChoosed(repertoryList) {
      if (!repertoryList || repertoryList.length <= 0) {
        return;
      }
      let self = this;
      let chooseList = repertoryList.filter(item => {
        if (self.goodTableData) {
          for (let i = 0; i < self.goodTableData.length; i++) {
            let temp = self.goodTableData[i];
            if (temp.repertoryId === item.id) {
              return false;
            }
          }
          return true;
        }
      });
      if (!chooseList || chooseList.length <= 0) {
        return;
      }
      let addList = chooseList.map(item => {
        let temp = {
          id: "",
          sellOrderId: self.sellOrderFormData.id,
          repertoryId: item.id,
          goodId: item.goodId,
          goodName: item.goodName,
          repertoryQuantity: item.quantity,
          quantity: 0,
          fixPrice: item.salePrice,
          disPrice: 0,
          free: 0,
          realPrice: item.salePrice,
          singlePrice: item.salePrice,
          amount: 0,
          taxRate: 0,
          repertoryInfo: item
        };
        return temp;
      });
      addList.map(item => this.goodTableData.push(item));
      this.getGoodHistoryPrice();
    },
    getGoodHistoryPrice() {
      let list = this.goodTableData;
      if (!list || list.length <= 0) {
        return;
      }
      let goodIds = [];
      for (let i = 0; i < list.length; i++) {
        if (list[i] && list[i].goodId) {
          let record = list[i].repertoryInfo;
          if (record && record.goodId) {
            goodIds.push(record.goodId);
          }
        }
      }
      let customerId = this.sellOrderFormData.customerId;
      let reqData = {
        goodIds: JSON.stringify(goodIds),
        customerId: customerId
      };
      util.ajax
        .get("/sell/detail/history", { params: reqData })
        .then(response => {
          this.goodHistory = response.data;
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    openRealPriceHistory(goodId) {
      this.historyExcludeId = this.sellOrderFormData.id;
      if (!goodId || !this.goodHistory || !this.goodHistory[goodId]) {
        this.historyDetails = [];
      } else {
        let details = this.goodHistory[goodId];
        if (details && details.length > 0) {
          this.historyDetails = details;
        } else {
          this.historyDetails = [];
        }
      }
      this.goodHistoryModal = true;
    },
    resetGoodSDataAmount(index) {
      let row = this.goodTableData[index];
      let realPrice =
        row["realPrice"] && !isNaN(row["realPrice"]) ? row["realPrice"] : 0;
      let disPrice =
        row["disPrice"] && !isNaN(row["disPrice"]) ? row["disPrice"] : 0;
      let free = row["free"] && !isNaN(row["free"]) ? row["free"] : 0;
      let quantity =
        row["quantity"] && !isNaN(row["quantity"]) ? row["quantity"] : 0;
      let num = quantity - free > 0 ? quantity - free : 0;
      row.amount = (num * realPrice * (1 - disPrice / 100)).toFixed(2);
      this.$set(this.goodTableData, index, row);
    },

    
    handleRowDbClick(data, index) {
      this.$Modal.confirm({
          title: '确认删除商品？',
          content: '<p>确认删除商品 ' + data.goodName + '?</p>',
          onOk: () => {
            if (data.id) {
              this.goodRemoveItem(data.id);
            }
            this.goodTableData.splice(index, 1);
          },
          onCancel: () => {
              
          }
      });
    },
    goodRemoveItem(removeId) {
      if (removeId) {
        //联动删除数据库中的值
        util.ajax
          .delete("/sell/detail/remove/" + removeId)
          .then(response => {
            this.$Message.success("删除成功");
          })
          .catch(error => {
            util.errorProcessor(this, error);
          });
      }
    }
    
  }
};
</script>

<style>

</style>


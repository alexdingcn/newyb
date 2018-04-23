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
                <Button type="success" icon="ios-checkmark" :loading="sellOrderSaveLoading" @click="sellOrderSaveBtnClick"> 保存 </Button>
                <Button icon="bookmark" :loading="sellOrderSaveLoading" @click="tempStorageOrderBtnClick"> 暂挂 </Button>
                <Button type="info" icon="filing" @click="getOldSellOrderBtnClick"> 暂挂提取 </Button>
                <Button type="ghost" icon="ios-printer">打印</Button>
            </ButtonGroup>

            <Form ref="sellOrderForm" :model="sellOrderFormData" :label-width="100" :rules="sellOrderFormValidate">
                <Row type="flex" justify="start">
                    <Col span="6">
                        <FormItem label="客户" prop="customerId">
                            <customer-select v-if="!editeOnlyDisable" :disabled="editeOnlyDisable" v-model="sellOrderFormData.customerId" @on-change="customerChange"></customer-select>
                            <Input v-else type="text" :disabled="editeOnlyDisable" v-model="sellOrderFormData.customerName"></Input>
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="客户代表" prop="customerRepId">
                            <Select v-model="sellOrderFormData.customerRepId" filterable clearable placeholder="请选择客户代表" @on-change="customerRepSelChange">
                                <Option v-for="item in customerRepList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                            </Select>
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="销售人员" prop="saleId">
                          <sale-select v-model="sellOrderFormData.saleId" ></sale-select>
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="加价率" >
                            <Input v-model="sellOrderFormData.markUpRate" number ></Input>
                        </FormItem>
                    </Col>
                </Row>

                <Row type="flex" justify="start">
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

                <Row type="flex" justify="start">
                    <Col span="6">
                        <FormItem label="自定单号" >
                            <Input type="text" v-model="sellOrderFormData.refNo" ></Input>
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="制单日期" >
                            <DatePicker v-model="sellOrderFormData.createOrderDate" type="date" placeholder="请选择制单日期" ></DatePicker>
                        </FormItem>
                    </Col>
                    <Col span="6">
                      <FormItem label="收款期限" >
                            <DatePicker v-model="sellOrderFormData.payOrderDate" type="date" placeholder="请选择收款期限" ></DatePicker>
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="提货人" >
                            <Input type="text" v-model="sellOrderFormData.takeGoodsUser" placeholder="请输入提货人"></Input>
                        </FormItem>
                    </Col>
                </Row>

                <Row type="flex" justify="start">
                    <Col span="6">
                        <FormItem label="温控方式" >
                          <option-select optionType="TEMPER_CONTROL" v-model="sellOrderFormData.temperControlId"></option-select>
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="运输方式" >
                          <option-select optionType="SHIP_METHOD" v-model="sellOrderFormData.shipMethod"></option-select>
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="运输工具" >
                          <option-select optionType="SHIP_TOOL" v-model="sellOrderFormData.shipTool"></option-select>
                        </FormItem>
                    </Col>
                    <Col span="6">
                        <FormItem label="承运公司" >
                          <ship-company-select v-model="sellOrderFormData.shipCompanyId"></ship-company-select>
                        </FormItem>
                    </Col>
                </Row>

                <Row type="flex" justify="start">
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
            <Tabs type="line">
                <TabPane label="商品信息" name="goodInfo">
                    <Row type="flex" justify="start">
                        <ButtonGroup size="small">
                            <Button type="primary" :disabled="!sellOrderFormData.warehouseId"  @click="addGoodBtnClick">添加商品</Button>
                        </ButtonGroup>
                    </Row>
                    <Table border highlight-row :loading="saveGoodBtnLoading" 
                        :columns="detailsColumns" :data="detailsData" 
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
import optionSelect from "@/views/selector/option-select.vue";
import shipCompanySelect from "@/views/selector/ship-company-select.vue";

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
    optionSelect,
    shipCompanySelect
  },
  data() {
    return {
      editeOnlyDisable: false,
      sellOrderFormData: {
        customerRepId: -1,
        createOrderDate: moment().format('YYYY-MM-DD'),
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
        saleId: [
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
      detailsData: [],
      detailsColumns: [
        {
          type: "expand",
          width: 50,
          render: (h, params) => {
            let repertoryInfo = params.row.repertoryInfo;
            let goods = {};
            if (repertoryInfo) {
              goods = repertoryInfo.goods;
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
                good: goods,
                repertoryInfo: repertoryInfo,
                productDate: productDate,
                expDate: expDate
              }
            })
          }
        },
        {
          title: "商品名称",
          key: "goodsName",
          align: "center",
          sortable: true,
          width: 180
        },
        {
          title: "生产企业",
          key: "factoryName",
          align: "center",
          width: 180,
          render: (h, params) => {
            let goods = params.row.repertoryInfo.goods;
            if(goods) {
              return goods.factory;
            }else {
              return '';
            }
          }
        },
        {
          title: "产地",
          key: "origin",
          align: "center",
          width: 120,
          render: (h, params) => {
            let goods = params.row.repertoryInfo.goods;
            if(goods) {
              return goods.origin;
            }else {
              return '';
            }
          }
        },
        {
          title: '批次号',
          key: 'batch',
          align: 'center',
          width: 160,
          render: (h, params) => {
            return params.row.repertoryInfo.batchCode;
          }
        },
        {
          title: "规格",
          key: "spec",
          align: "center",
          width: 120,
          render: (h, params) => {
            let goods = params.row.repertoryInfo.goods;
            if(goods) {
              return goods.spec;
            }else {
              return '';
            }
          }
        },
        {
          title: "剂型",
          key: "jx",
          align: "center",
          width: 120,
          render: (h, params) => {
            let goods = params.row.repertoryInfo.goods;
            if(goods) {
              return goods.jxName;
            }else {
              return '';
            }
          }
        },
        {
          title: "库存量",
          key: "repertoryQuantity",
          width: 100,
          align: "center"
        },
        {
          title: "销售数量",
          key: "quantity",
          align: "center",
          width: 120,
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.detailsData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.detailsData[params.index];
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
          width: 100,
          align: "center"
        },
        {
          title: "折扣",
          key: "disPrice",
          align: "center",
          width: 120,
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.detailsData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.detailsData[params.index];
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
          width: 120,
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.detailsData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.detailsData[params.index];
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
                number: true,
                value: self.detailsData[params.index][params.column.key],
                icon: "eye"
              },
              on: {
                "on-blur"(event) {
                  let row = self.detailsData[params.index];
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
          width: 100,
          align: "center"
        },
        {
          title: "金额",
          key: "amount",
          width: 120,
          align: "center"
        },
        {
          title: "税率",
          key: "taxRate",
          align: "center",
          width: 120,
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.detailsData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.detailsData[params.index];
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
    detailsData: function() {
      if(this.detailsData && this.detailsData.length > 0) {
        this.warehouseDisable = true;
        this.totalQuantity = this.detailsData.reduce((total, item) => {
          if (item.quantity && !isNaN(item.quantity)) {
            return total + parseFloat(item.quantity);
          }
        }, 0);
        this.totalAmount = this.detailsData.reduce((total, item) => {
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
        if (!this.detailsData || this.detailsData.length <= 0) {
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
      this.sellOrderFormData.details = this.detailsData;
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
        if (!this.detailsData || this.detailsData.length <= 0) {
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
        for (let i=0; i<this.detailsData.length; i++) {
          let detailItem = this.detailsData[i].repertoryInfo ? this.detailsData[i].repertoryInfo.goods : null;
          if (detailItem && detailItem !== null && detailItem.SpecialManaged) {
              this.$Modal.error(
                {
                  title: '客户商品选择限制', 
                  content: this.currChooseCustomer.name + '不能经营特殊管控商品, 但是商品列表中存在管控商品:' + detailItem.goodsName
                });
              return false;
          }
          if (detailItem.repertoryQuantity < detailItem.quantity) {
            this.$Modal.error(
                {
                  title: '商品库存量不足', 
                  content: detailItem.goodsName + '库存量不足'
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
      this.refreshDetailsData(data.id);
    },
    orderFormChangeToAddModel() {
      this.editeOnlyDisable = false;
      this.warehouseDisable = false;
      this.sellOrderFormData = {
        createOrderDate: moment().format('YYYY-MM-DD'),
      };
      this.detailsData = [];
    },
    refreshDetailsData(sellOrderId) {
      if (sellOrderId && sellOrderId > 0) {
        let reqData = { sellOrderId: sellOrderId };
        this.saveGoodBtnLoading = true;
        util.ajax
          .get("/sell/detail/list", { params: reqData })
          .then(response => {
            this.detailsData = response.data;
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
        if (self.detailsData) {
          for (let i = 0; i < self.detailsData.length; i++) {
            let temp = self.detailsData[i];
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
        let goods = item.goods;
        let temp = {
          sellOrderId: self.sellOrderFormData.id,
          repertoryId: item.id,
          goodsId: item.goodsId,
          goodsName: item.goodsName,
          repertoryQuantity: item.quantity,
          quantity: 0,
          fixPrice: goods ? goods.batchPrice : 0,
          disPrice: 0,
          free: 0,
          realPrice: goods ? goods.batchPrice : 0,
          singlePrice: goods ? goods.batchPrice : 0,
          amount: 0,
          taxRate: goods ? goods.outTax : 0,
          repertoryInfo: item
        };
        return temp;
      });
      addList.map(item => this.detailsData.push(item));
      this.getGoodHistoryPrice();
    },
    getGoodHistoryPrice() {
      let list = this.detailsData;
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
      let row = this.detailsData[index];
      let realPrice =
        row["realPrice"] && !isNaN(row["realPrice"]) ? row["realPrice"] : 0;
      let disPrice =
        row["disPrice"] && !isNaN(row["disPrice"]) ? row["disPrice"] : 0;
      let free = row["free"] && !isNaN(row["free"]) ? row["free"] : 0;
      let quantity =
        row["quantity"] && !isNaN(row["quantity"]) ? row["quantity"] : 0;
      let num = quantity - free > 0 ? quantity - free : 0;
      row.amount = (num * realPrice * (1 - disPrice / 100)).toFixed(2);
      this.$set(this.detailsData, index, row);
    },
    
    handleRowDbClick(data, index) {
      this.$Modal.confirm({
          title: '确认删除商品？',
          content: '<p>确认删除商品 ' + data.goodsName + '?</p>',
          onOk: () => {
            if (data.id) {
              this.detailRemoveItem(data.id);
            }
            this.detailsData.splice(index, 1);
          },
          onCancel: () => {}
      });
    },
    detailRemoveItem(removeId) {
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

.ivu-form-item {
    margin-bottom: 20px;
}

</style>


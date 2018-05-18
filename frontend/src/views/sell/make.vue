<style lang="less">
@import "../../styles/common.less";
@import "sell-order.less";
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="ios-cart"></Icon> 销售订单制单
            </p> 
            <ButtonGroup slot="extra">
                <Button type="success" icon="ios-checkmark" :loading="sellOrderSaveLoading" @click="sellOrderSaveBtnClick"> 保存 </Button>
                <Button icon="bookmark" :loading="sellOrderSaveLoading" @click="tempStorageOrderBtnClick"> 暂挂 </Button>
                <Button type="info" icon="filing" @click="getOldSellOrderBtnClick"> 暂挂提取 </Button>
                <Button type="ghost" icon="ios-printer">打印</Button>
            </ButtonGroup>

            <Form ref="sellOrderForm" :model="sellOrderFormData" :label-width="100" :rules="sellOrderFormValidate">
                <Row type="flex" justify="start">
                    <i-col span="8">
                        <FormItem label="客户" prop="customerId">
                            <customer-select v-if="!editMode" :disabled="editMode" v-model="sellOrderFormData.customerId" @on-change="customerChange">
                                <div slot="helpContent">
                                    <span>同一个商品可能对于不同的客户既定的报价不同，所以请先选择客户，再选择商品，自动匹配既定的报价。</span>
                                </div>
                            </customer-select>
                            <Input v-else type="text" :disabled="editMode" v-model="sellOrderFormData.customerName"></Input>
                        </FormItem>
                    </i-col>

                    <i-col span="8">
                        <FormItem label="收款期限" >
                            <DatePicker v-model="sellOrderFormData.payOrderDate" type="date" placeholder="请选择收款期限" ></DatePicker>
                        </FormItem>
                    </i-col>

                </Row>

                <Row type="flex" justify="start">
                    <i-col span="8">
                        <FormItem label="销售人员" prop="saleId">
                            <sale-select v-model="sellOrderFormData.saleId" ></sale-select>
                        </FormItem>
                    </i-col>
                    <i-col span="8">
                        <FormItem label="自定单号" >
                            <Input type="text" v-model="sellOrderFormData.refNo" ></Input>
                        </FormItem>
                    </i-col>
                    <!--<i-col span="6">-->
                        <!--<FormItem label="制单日期" >-->
                            <!--<DatePicker v-model="sellOrderFormData.createOrderDate" type="date" placeholder="请选择制单日期" ></DatePicker>-->
                        <!--</FormItem>-->
                    <!--</i-col>-->
                    <!--<i-col span="6">-->
                        <!--<FormItem label="加价率" >-->
                            <!--<Input v-model="sellOrderFormData.markUpRate" number ></Input>-->
                        <!--</FormItem>-->
                    <!--</i-col>-->
                    <!--<i-col span="6">-->
                        <!--<FormItem label="提货人" >-->
                            <!--<Input type="text" v-model="sellOrderFormData.takeGoodsUser" placeholder="请输入提货人"></Input>-->
                        <!--</FormItem>-->
                    <!--</i-col>-->
                </Row>

                <Row type="flex" justify="start">
                    <i-col span="8">
                        <FormItem label="仓库点" prop="warehouseId">
                            <warehouse-select v-model="sellOrderFormData.warehouseId" :disabled="warehouseDisable" @on-change="warehouseChange"></warehouse-select>
                        </FormItem>
                    </i-col>
                    <i-col span="16">
                        <FormItem label="备注" >
                            <Input type="text" v-model="sellOrderFormData.comment" placeholder="请输入备注"></Input>
                        </FormItem>
                    </i-col>
                </Row>

                <h3 class="margin-top-10">
                    商品信息
                    <ButtonGroup size="small">
                        <Button type="primary" :disabled="!sellOrderFormData.warehouseId"  @click="addGoodBtnClick">添加商品</Button>
                    </ButtonGroup>
                </h3>
                <Table border highlight-row :loading="saveGoodBtnLoading"
                       :columns="detailsColumns" :data="detailsData"
                       no-data-text="在保存订单信息后点击添加商品按钮添加"
                       ref="sellOrderGoodTable" style="min-height: 300px" size="small"
                       class="margin-top-8"
                       @on-row-dblclick="handleRowDbClick">
                    <div slot="footer">
                        <h3 class="padding-left-20" >
                            <b>合计数量: {{totalQuantity}}</b>  <b class="margin-left-50">合计金额:</b> ￥{{ totalAmount }}
                            <span class="margin-left-50"> 提示: 金额 = (数量 - 赠送) x 实价 x (1 - 折扣%) </span>
                        </h3>
                    </div>
                </Table>

                <h3 class="margin-top-10">配送和交货</h3>
                <Row type="flex" justify="start">
                    <i-col span="6">
                        <FormItem label="温控方式" >
                            <option-select optionType="TEMPER_CONTROL" v-model="sellOrderFormData.temperControlId"></option-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="运输方式" >
                            <option-select optionType="SHIP_METHOD" v-model="sellOrderFormData.shipMethod"></option-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="运输工具" >
                            <option-select optionType="SHIP_TOOL" v-model="sellOrderFormData.shipTool"></option-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="承运公司" >
                            <ship-company-select v-model="sellOrderFormData.shipCompanyId"></ship-company-select>
                        </FormItem>
                    </i-col>
                </Row>
                <Row type="flex" justify="start">
                    <Col span="20">
                        <FormItem label="送货地址" prop="customerRepId">

                            <RadioGroup v-model="sellOrderFormData.customerRepId"
                                        vertical class="address-selection width-100"
                                        @on-change="onChangeAddr">
                                <Radio v-for="item in customerRepList" :label="item.id" :key="item.id">
                                    <span>{{ item.name }} {{ item.contactPhone }} {{ item.repertoryAddress }} </span>
                                    <Tag type="border" v-if="item.isDefault">默认地址</Tag>
                                    <Button type="text" class="set-default-addrbtn invisible" @click="setDefaultRep(item.id)" v-if="!item.isDefault">设成默认地址</Button>
                                    <Button type="text" class="edit-address-btn invisible fr" @click="editCustomerRep(item)">修改</Button>
                                </Radio>
                            </RadioGroup>
                            <Button type="ghost" icon="plus" @click="addNewRep">新增配送地址</Button>
                        </FormItem>

                        <customer-rep ref="repModal" @on-closed="refreshCustomerRepList"></customer-rep>
                    </Col>
                    <!--<i-col span="8">-->
                        <!--<FormItem label="收货电话" prop="contactPhone">-->
                            <!--<Input type="text" readonly v-model="sellOrderFormData.contactPhone" ></Input>-->
                        <!--</FormItem>-->
                    <!--</i-col>-->
                    <!--<i-col span="10">-->
                        <!--<FormItem label="收货地址" prop="repertoryAddress">-->
                            <!--<Input type="text" readonly v-model="sellOrderFormData.repertoryAddress" ></Input>-->
                        <!--</FormItem>-->
                    <!--</i-col>-->
                </Row>
            </Form>
        </Card>

        <sell-order-search 
            :showModal="sellOrderSearchModal" 
            status="TEMP_STORAGE" 
            @modal-close="orderSearchModalClose"
            @choosed="orderSearchChoosed">
        </sell-order-search>

        <Modal v-model="selectRepertoryModal" width="60" :mask-closable="false" title="选择库存商品" >
          <repertory-info-select :warehouse="chooseWarehouse" @on-choosed="repertoryInfoChoonsed" ></repertory-info-select>
          <span slot="footer"></span>
        </Modal>

        <Modal v-model="goodHistoryModal" width="75" :mask-closable="false" title="客户商品销售历史价格">
            <sell-good-history :excludedOrderId="historyExcludeId" :customerId="historyCustomerId" :goodsId="historyGoodsId"></sell-good-history>
            <div slot="footer"></div>
        </Modal>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import dataConver from "@/libs/data-conver.js";
import customerSelect from "@/views/selector/customer-select.vue";
import sellOrderSearch from "@/views/sell/sell-order-search.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";
import sellGoodHistory from "./sell-good-history.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import saleSelect from "@/views/selector/sale-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import shipCompanySelect from "@/views/selector/ship-company-select.vue";
import repertoryInfoSelect from "@/views/selector/repertory-info-select.vue";
import customerRep from "../customer/customer-rep.vue";
import goodsSepcTags from '../goods/goods-spec-tabs.vue';

export default {
  name: "sell_order_make",
  components: {
    goodsSepcTags,
    customerSelect,
    customerRep,
    sellOrderSearch,
    repertoryInfoSelect,
    goodsExpand,
    sellGoodHistory,
    warehouseSelect,
    saleSelect,
    optionSelect,
    shipCompanySelect
  },
  data() {
    return {
      editMode: false,
      categoryModal: false,
      sellOrderFormData: {
        customerId: "",
        customerRepId: "",
        saleId: "",
        warehouseId: "",
        payOrderDate: moment()
          .add("7", "d")
          .format("YYYY-MM-DD"),
        createOrderDate: moment().format("YYYY-MM-DD")
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
      selectRepertoryModal: false,
      saveGoodBtnLoading: false,
      warehouseDisable: false,
      detailsData: [],
      detailsColumns: [
        {
          type: "expand",
          width: 50,
          render: (h, params) => {
            let goods = params.row.goods;
            let productDate = params.row.productDate
              ? moment(params.row.productDate).format("YYYY-MM-DD")
              : "";
            let expDate = params.row.expDate
              ? moment(params.row.expDate).format("YYYY-MM-DD")
              : "";
            return h(goodExpand, {
              props: {
                good: goods,
                productDate: productDate,
                expDate: expDate
              }
            });
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
              return params.row.goods.factoryName;
          }
        },
        {
          title: "产地",
          key: "origin",
          align: "center",
          width: 120
        },
        {
          title: "批次号",
          key: "batchCode",
          align: "center",
          width: 160
        },
        {
          title: "规格",
          key: "spec",
          align: "center",
          width: 120,
          render: (h, params) =>　{
              return h(goodsSepcTags, {
                  props: {
                      tags: params.row.goods.goodsSpecs,
                      color: 'blue'
                  }
              });
          }
        },
        {
          title: "库存量",
          key: "repertoryQuantity",
          width: 100,
          align: "center"
        },
        // {
        //   title: "在单数",
        //   key: "onWayQuantity",
        //   width: 100,
        //   align: "center"
        // },
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
          title: "折扣 %",
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
                  let goodsId = params.row.goodsId;
                  self.openRealPriceHistory(
                    self.sellOrderFormData.customerId,
                    goodsId
                  );
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
      historyCustomerId: "",
      historyGoodsId: "",
      goodHistoryModal: false
    };
  },
  watch: {
    detailsData: function() {
      if (this.detailsData && this.detailsData.length > 0) {
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
      } else {
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
        this.refreshCustomerRepList();
      }
    },
    onChangeAddr(customerRep) {
      console.log(customerRep);
    },
    refreshCustomerRepList() {
      if (!this.sellOrderFormData || !this.sellOrderFormData.customerId) {
        return;
      }
      let reqData = {
        customerId: this.sellOrderFormData.customerId,
        enabled: true
      };
      let self = this;
      util.ajax
        .get("/customer/rep/list", { params: reqData })
        .then(response => {
          this.customerRepList = response.data;
          if (this.customerRepList && this.customerRepList.length > 0) {
            if (!self.sellOrderFormData.customerRepId || self.sellOrderFormData.customerRepId <= 0) {
              for (var i = 0; i < this.customerRepList.length; i++) {
                if (this.customerRepList[i].isDefault) {
                  self.sellOrderFormData.customerRepId = this.customerRepList[i].id;
                  break;
                }
              }
            }
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
        if (!valid) {
          this.$Message.warning("必输信息需要输入");
          return;
        }
        if (!this.detailsData || this.detailsData.length <= 0) {
          this.$Message.warning("需要添加商品");
          return;
        }
        let saleGoodVidate = this.customerCanSaleGoodValidate();
        if (!saleGoodVidate) {
          return; //客户不能经营特殊管理产品
        }
        //两步资料完整，可以直接保存
        this.saveSellOrder("TEMP_STORAGE");
      });
    },
    saveSellOrder(status) {
      this.sellOrderSaveLoading = true;
      this.sellOrderFormData.status = status;
      this.sellOrderFormData.details = this.detailsData;
      util.ajax
        .post("/sell/order/save", this.sellOrderFormData)
        .then(response => {
          this.sellOrderSaveLoading = false;
          this.$Message.success("操作成功");
          this.orderFormChangeToAddModel(); //转化到初始状态
        })
        .catch(error => {
          this.sellOrderSaveLoading = false;
          util.errorProcessor(this, error);
        });
    },

    sellOrderSaveBtnClick() {
      //保存订单信息
      this.$refs.sellOrderForm.validate(valid => {
        if (!valid) {
          this.$Message.warning("必输信息需要输入");
          return;
        }
        if (!this.detailsData || this.detailsData.length <= 0) {
          this.$$Message.warning("需要添加商品");
          return;
        }
        let saleGoodVidate = this.customerCanSaleGoodValidate();
        if (!saleGoodVidate) {
          return; //客户不能经营特殊管理产品
        }
        //查询是否存在小于等于0的销售数量
        for (let i = 0; i < this.detailsData.length; i++) {
          let item = this.detailsData[i];
          let quantity = item.quantity;
          if (!quantity || isNaN(quantity) || quantity <= 0) {
            this.$Modal.warning({
              title: "销售数量提示",
              content: "产品:" + item.goodsName + "销售数量需要大于0"
            });
            return;
          }
          let free = item.free ? item.free : 0;
          if (quantity - free < 0) {
            this.$Modal.warning({
              title: "销售数量提示",
              content:
                "产品:" +
                item.goodsName +
                "销售数量需要大于赠送数量, 销售数包含赠送数量."
            });
            return;
          }
        }
        let self = this;
        this.$Modal.confirm({
          title: "保存提交确认",
          content: "请确认数据是否正确，提交后不能修改.",
          onOk: () => {
            self.saveSellOrder("INIT");
          },
          onCancel: () => {}
        });
      });
    },

    customerCanSaleGoodValidate() {
      //检查客户是否是可经营特殊管控的商品的，如果不是，需要检查选择的商品列表中是否存在特殊管控的商品
      if (!this.currChooseCustomer || !this.currChooseCustomer.canSaleSpecial) {
        for (let i = 0; i < this.detailsData.length; i++) {
          let detailItem = this.detailsData[i];
          let goods = detailItem.goods;
          if (goods && goods.SpecialManaged) {
            this.$Modal.error({
              title: "客户商品选择限制",
              content:
                this.currChooseCustomer.name +
                "不能经营特殊管控商品, 但是商品列表中存在管控商品:" +
                goods.name
            });
            return false;
          }
          let repQuantity = detailItem.repertoryQuantity
            ? detailItem.repertoryQuantity
            : 0;
          let onWayQuantity = detailItem.onWayQuantity
            ? detailItem.onWayQuantity
            : 0;
          if (repQuantity - onWayQuantity < detailItem.quantity) {
            this.$Modal.error({
              title: "商品库存量不足",
              content: detailItem.goodsName + "库存量不足"
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
      this.editMode = true; //编辑模式下的客户信息不能修改
      this.warehouseDisable = true;
      this.refreshCustomerRepList();
      this.refreshDetailsData(data.id);
    },
    orderFormChangeToAddModel() {
      this.editMode = false;
      this.warehouseDisable = false;
      this.sellOrderFormData = {
        customerId: "",
        customerRepId: "",
        saleId: "",
        warehouseId: "",
        createOrderDate: moment().format("YYYY-MM-DD")
      };
      this.detailsData = [];
      this.$refs.sellOrderForm.resetFields();
    },
    refreshDetailsData(sellOrderId) {
      if (sellOrderId && sellOrderId > 0) {
        let reqData = { sellOrderId: sellOrderId };
        this.saveGoodBtnLoading = true;
        util.ajax
          .get("/sell/detail/list", { params: reqData })
          .then(response => {
            this.saveGoodBtnLoading = false;
            this.detailsData = response.data;
          })
          .catch(error => {
            this.saveGoodBtnLoading = false;
            util.errorProcessor(this, error);
          });
      }
    },
    warehouseChange(warehouseId, warehouseItem) {
      this.chooseWarehouse = warehouseItem;
    },

    addGoodBtnClick() {
      if (
        !this.sellOrderFormData.warehouseId ||
        this.sellOrderFormData.warehouseId <= 0
      ) {
        this.$Message.warning("请先选择对应仓库点");
        return;
      }
      this.selectRepertoryModal = true;
    },
    repertoryInfoChoonsed(repertoryList) {
      this.selectRepertoryModal = false;
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
          onWayQuantity: item.onWayQuantity,
          quantity: 0,
          fixPrice: goods ? goods.batchPrice : 0,
          disPrice: 0,
          free: 0,
          realPrice: goods ? goods.batchPrice : 0,
          singlePrice: goods ? goods.batchPrice : 0,
          amount: 0,
          taxRate: goods ? goods.outTax : 0,
          batchCode: item.batchCode,
          productDate: item.productDate,
          expDate: item.expDate,
          location: item.location,
          factoryName: goods.factory,
          origin: goods.origin,
          jx: goods.jxName,
          spec: goods.spec,
          unitName: goods.unitName,
          permitNo: goods.permitNo,
          goods: goods
        };
        return temp;
      });
      addList.map(item => this.detailsData.push(item));
    },

    openRealPriceHistory(customerId, goodsId) {
      if (!customerId) {
        this.$Message.info("请先选择客户");
        return;
      }
      this.historyExcludeId = this.sellOrderFormData.id;
      this.historyCustomerId = customerId;
      this.historyGoodsId = goodsId;
      this.goodHistoryModal = true;
    },

    resetGoodSDataAmount(index) {
      let row = this.detailsData[index];
      let realPrice = row["realPrice"] && !isNaN(row["realPrice"]) ? row["realPrice"] : 0;
      let disPrice = row["disPrice"] && !isNaN(row["disPrice"]) ? row["disPrice"] : 0;
      let free = row["free"] && !isNaN(row["free"]) ? row["free"] : 0;
      let quantity = row["quantity"] && !isNaN(row["quantity"]) ? row["quantity"] : 0;
      let num = quantity - free > 0 ? quantity - free : 0;
      row.amount = (num * realPrice * (1 - disPrice / 100)).toFixed(2);
      this.$set(this.detailsData, index, row);
    },

    handleRowDbClick(data, index) {
      this.$Modal.confirm({
        title: "确认删除商品？",
        content: "<p>确认删除商品 " + data.goodsName + "?</p>",
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
    },
    setDefaultRep(repId) {
      if (repId) {
        var self = this;
        util.ajax
          .post("/customer/rep/default/" + repId)
          .then(response => {
            self.refreshCustomerRepList();
          })
          .catch(error => {
            util.errorProcessor(this, error);
          });
      }
    },
    editCustomerRep(rep) {
      if (rep) {
        var editData = JSON.parse(JSON.stringify(rep));
        this.$refs.repModal.show(this.sellOrderFormData.customerId, editData);
      }
    },
    addNewRep() {
      if (this.sellOrderFormData.customerId) {
        this.$refs.repModal.show(this.sellOrderFormData.customerId);
      } else {
        this.$Message.warning("请先选择客户");
      }
    }
  }
};
</script>

<style>
</style>


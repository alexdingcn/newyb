<style lang="less">
@import "../../styles/common.less";
</style>

<template>
	<Row :gutter="10">
        <i-col :span="showSider ? '4' : '0'">
            <Card>
                <p slot="title">
                    未审核采购单
                    <Tooltip transfer placement="right-start">
                        <Icon type="ios-help-outline"></Icon>
                        <div slot="content" >
                            <p>展现采购单录入后未审核通过的列表, 可以提取修改和删除操作</p>
                        </div>
                    </Tooltip>
                </p>
                <div slot="extra">
                    <a href="javascript:void(0)" @click="reloadUncheckData" style="margin-right: 5px;" >
                        <Icon type="refresh"></Icon>
                    </a>
                </div>
                
                <Table stripe highlight-row :loading="uncheckTabLoading" 
                        :columns="uncheckColumns" :data="uncheckData" ref="uncheckTable" 
                        style="width: 100%;" class="uncheck-table" height="750" 
                        size="small">
                </Table>
            </Card>
        </i-col>
        <i-col :span="showSider ? '20' : '24'">
            <Card>
                <p slot="title" >
                    <a href="javascript:void(0)" @click="changeSiderShow" style="margin-right: 5px;" >
                        <Icon v-if="showSider" type="chevron-left"></Icon>
                        <Icon v-else type="chevron-right"></Icon>
                    </a>

                    <Icon type="document"></Icon> 采购入库制单
                </p>
                <div slot="extra">

                    <ButtonGroup class="padding-left-20">
                        <Button type="success" icon="android-list" @click="saveBuyOrder" :loading="saving">保存</Button>
                    </ButtonGroup>
                </div>

                <Form :label-width="85" :rules="ruleValidate" :model="buyOrder" ref="buyOrderForm">
                    <Row>
                        <i-col span="6">
                            <FormItem label="供应商" prop="supplierId" >
                                <supplier-select v-model="buyOrder.supplierId"  @on-change="supplierChange" ></supplier-select>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="仓库点" prop="warehouseId">
                                <warehouse-select v-model="buyOrder.warehouseId" ></warehouse-select>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="采购员" prop="buyerId">
                                <buyer-select v-model="buyOrder.buyerId" ></buyer-select>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="8">
                            <FormItem label="选择商品">
                                <good-select :disabled="!buyOrder.warehouseId" ref="goodsSelect" :warehouseId="buyOrder.warehouseId" options="LW;LB;CBQ" 
                                @on-change="onSelectGoods"></good-select>
                            </FormItem>
                        </i-col>
                    </Row>

                    <Table border highlight-row
                        class="margin-top-8"
                        :columns="orderColumns" :data="orderItems"
                        ref="buyOrderTable" size="small"
                        no-data-text="在商品输入框选择后添加"
                        @on-row-dblclick="handleRowDbClick">
                        <div slot="footer">
                            <h3 class="padding-left-20" >
                                <b>合计金额:</b> ￥{{ totalAmount }}
                            </h3>
                        </div>
                    </Table>

                    <h3 v-if="isMedicine" class="margin-top-10">配送</h3>
                    <Row v-if="isMedicine">
                        <i-col span="5">
                            <FormItem label="温控方式" prop="temperControlId">
                                <option-select v-model="buyOrder.temperControlId" optionType="TEMPER_CONTROL"></option-select>
                            </FormItem>
                        </i-col>
                        <i-col span="5">
                            <FormItem label="运输方式" prop="shipMethodId">
                                <option-select v-model="buyOrder.shipMethodId" optionType="SHIP_METHOD"></option-select>
                            </FormItem>
                        </i-col>
                        <i-col span="5">
                            <FormItem label="运输工具" prop="shipToolId">
                                <option-select v-model="buyOrder.shipToolId" optionType="SHIP_TOOL"></option-select>
                            </FormItem>
                        </i-col>
                        <i-col span="5">
                            <FormItem label="预到货日期" prop="eta">
                                <DatePicker type="date" v-model="buyOrder.eta" />
                            </FormItem>
                        </i-col>
                    </Row>

                    <div class="margin-top-10">
                        <Input type="textarea" v-model="buyOrder.comment" :rows="2" placeholder="暂无备注信息"/>
                    </div>
                    
                </Form>
            </Card>
        </i-col>
		
		<Modal v-model="closeConfirm"
			   title="是否继续下单"
			   @on-ok="clearData"
		       @on-cancel="closeTab">
			<p>是否继续添加下一笔订单?</p>
		</Modal>
	</Row>

</template>

<script>
import axios from "axios";
import moment from "moment";
import util from "@/libs/util.js";
import supplierSelect from "@/views/selector/supplier-select.vue";
import supplierContactSelect from "@/views/selector/supplier-contact-select.vue";
import buyerSelect from "@/views/selector/buyer-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import goodSelect from "@/views/selector/good-select.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";

export default {
  name: "buy_order",
  components: {
    supplierSelect,
    supplierContactSelect,
    buyerSelect,
    optionSelect,
    goodSelect,
    warehouseSelect,
    goodsSpecTags
  },
  data() {
    return {
      saving: false,
      totalAmount: 0,
      edittingRow: {},
      closeConfirm: false,
      orderItems: [],
      buyOrder: {
        supplierId: null,
        eta: moment()
          .add(1, "d")
          .format("YYYY-MM-DD"),
        orderItemIds: []
      },
      orderColumns: [
        {
          type: "index",
          title: "",
          width: 30
        },
        {
          title: "商品名称",
          key: "goodsName",
          width: 200,
          sortable: true,
          render: (h, params) => {
            let goodsName = params.row.goods.name;
            return h("span", goodsName);
          }
        },
        {
          title: "产地",
          key: "origin",
          width: 100,
          render: (h, params) => {
            let origin = params.row.goods.origin;
            return h("span", origin);
          }
        },
        {
          key: "goodsSpecs",
          title: "规格",
          width: 120,
          render: (h, params) => {
            let goodsSpecs = params.row.goods.goodsSpecs
              ? params.row.goods.goodsSpecs
              : [];
            return h(goodsSpecTags, {
              props: {
                tags: goodsSpecs,
                color: "blue"
              }
            });
          }
        },
        {
          title: "商品条码",
          key: "barCode",
          width: 150,
          render: (h, params) => {
            let barCode =
              params.row.goods && params.row.goods.barCode
                ? params.row.goods.barCode
                : "";
            return h("span", barCode);
          }
        },
        {
          title: "生产企业",
          key: "factoryName",
          width: 150,
          render: (h, params) => {
            let factoryName = params.row.goods.factoryName;
            return h("span", factoryName);
          }
        },
        {
          title: "单位",
          key: "unitName",
          align: "center",
          width: 80,
          render: (h, params) => {
            let unitName = params.row.goods.unitName;
            return h("span", unitName);
          }
        },
        {
          title: "数量",
          key: "quantity",
          align: "center",
          width: 100,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                value: self.orderItems[params.index][params.column.key],
                number: true
              },
              on: {
                "on-blur"(event) {
                  var row = self.orderItems[params.index];
                  var price = row["buyPrice"];
                  var qty = event.target.value;
                  row[params.column.key] = qty;
                  if (qty != "" && !isNaN(qty) && !isNaN(price)) {
                    row.amount = (qty * price).toFixed(2);
                    self.$set(self.orderItems, params.index, row);
                  }
                },
                "on-keydown"(event) {
                  if (event.keyCode === 13 || event.keyCode === 9) {
                    var index = params.index * 2;
                    var inputList = self.$refs.buyOrderTable.$el.querySelectorAll(
                      "input"
                    );
                    if (inputList && index + 2 <= inputList.length) {
                      inputList[index + 1].focus();
                    }
                  }
                }
              }
            });
          }
        },
        {
          title: "采购单价",
          key: "buyPrice",
          align: "center",
          width: 100,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                value: self.orderItems[params.index][params.column.key],
                number: true
              },
              on: {
                "on-blur"(event) {
                  var row = self.orderItems[params.index];
                  var qty = row["quantity"];
                  var price = event.target.value;
                  row[params.column.key] = price;
                  if (price != "" && !isNaN(qty) && !isNaN(price)) {
                    row.amount = (qty * price).toFixed(2);
                    self.$set(self.orderItems, params.index, row);
                  }
                },
                "on-keydown"(event) {
                  console.log(event.keyCode);
                  if (event.keyCode === 13 || event.keyCode === 9) {
                    var index = params.index * 2 + 1;
                    var inputList = self.$refs.buyOrderTable.$el.querySelectorAll(
                      "input"
                    );
                    if (inputList && index + 2 <= inputList.length) {
                      // move to next line
                      inputList[index + 1].focus();
                    }
                    if (index + 2 >= inputList.length) {
                      var row = self.orderItems[params.index];
                      var qty = row["quantity"];
                      var price = event.target.value;
                      if (!isNaN(qty) && !isNaN(price)) {
                        //row.amount = (qty * price).toFixed(2);
                        self.$set(self.orderItems, params.index, row);
                      }
                    }
                  }
                }
              }
            });
          }
        },
        {
          title: "金额",
          key: "amount",
          align: "center",
          width: 100
        },
        {
          title: "最近采购价",
          key: "lastBuyPrice",
          align: "center",
          width: 100,
          render: (h, params) => {
            let lastBuyPrice = params.row.goods.lastBuyPrice
              ? params.row.goods.lastBuyPrice
              : "";
            return h("span", lastBuyPrice);
          }
        },
        {
          title: "整件单位",
          key: "packUnitName",
          align: "center",
          width: 100,
          render: (h, params) => {
            let packUnitName = params.row.goods.packUnitName;
            return h("span", packUnitName);
          }
        },
        {
          title: "大件装量",
          key: "bigPack",
          align: "center",
          width: 80,
          render: (h, params) => {
            let bigPack = params.row.goods.bigPack;
            return h("span", bigPack);
          }
        },
        {
          title: "库存",
          key: "currRepQuatity",
          align: "center",
          width: 100,
          render: (h, params) => {
            let currRepQuatity = params.row.goods.currRepQuatity
              ? params.row.goods.currRepQuatity
              : "";
            return h("span", currRepQuatity);
          }
        },
        {
          title: "在单数量",
          key: "currBuyQuality",
          align: "center",
          width: 100,
          render: (h, params) => {
            let currBuyQuality = params.row.goods.currBuyQuality
              ? params.row.goods.currBuyQuality
              : "";
            return h("span", currBuyQuality);
          }
        }
      ],
      ruleValidate: {
        supplierId: [
          { required: true, type: "number", message: "请选择供应商" }
        ],
        buyerId: [{ required: true, type: "number", message: "请选择采购员" }],
        warehouseId: [
          { required: true, type: "number", message: "请选择仓库点" }
        ],
        details: [
          {
            required: true,
            type: "array",
            array: { min: 1 },
            message: "请添加商品"
          }
        ]
      },
      showSider: true,
      uncheckTabLoading: false,
      uncheckData: [],
      uncheckColumns: [
        {
          title: "采购单",
          key: "id",
          render: (h, params) => {
            let self = this;
            let status = params.row.status;
            let statusLabel = status === "REJECTED" ? "审核拒绝" : "待审核";
            let statusColor = status === "REJECTED" ? "#ed3f14" : "#2b85e4";
            let statusH = h(
              "span",
              {
                class: {
                  statusClass: true
                },
                style: {
                  color: statusColor
                }
              },
              statusLabel
            );
            let buttonH = h(
              "ButtonGroup",
              {
                props: {
                  size: "small"
                }
              },
              [
                h(
                  "Button",
                  {
                    props: {
                      type: "info"
                    },
                    on: {
                      click: () => {
                        self.editBuyOrder(params.row);
                      }
                    }
                  },
                  "修改"
                ),
                h(
                  "Button",
                  {
                    props: {
                      type: "error"
                    },
                    on: {
                      click: () => {
                        self.removeBuyOrder(params.row.id);
                      }
                    }
                  },
                  "删除"
                )
              ]
            );

            let orderNumnber = params.row.orderNumber;
            let supplier = params.row.supplier;
            let createdTime = moment(params.row.createdTime).format(
              "YYYY-MM-DD HH:mm"
            );
            let createdBy = params.row.createdBy;
            let warehouse = params.row.warehouse;
            return h(
              "div",
              {
                style: {
                  margin: "0.5em"
                }
              },
              [
                h(
                  "h5",
                  {
                    style: {
                      color: "#9ea7b4",
                      fontSize: "12px"
                    }
                  },
                  orderNumnber
                ),
                h("h4", supplier + "[" + warehouse + "]"),
                h(
                  "h5",
                  {
                    style: {
                      color: "#9ea7b4",
                      fontSize: "12px"
                    }
                  },
                  createdTime + "[" + createdBy + "]"
                ),
                h("hr", { size: "1", style: { color: "#e9eaec" } }),
                h("div", [statusH, buttonH])
              ]
            );
          }
        }
      ],
      haveCheck: false,
      isMedicine: false,
      supplierColdManage: false,
      supplierSpecialManage: false
    };
  },
  mounted() {
    this.init();
  },
  activated() {
    this.clearData();
  },
  watch: {
    orderItems: function() {
      this.totalAmount = this.orderItems.reduce(function(total, item) {
        return total + parseFloat(item.amount);
      }, 0);
    }
  },
  methods: {
    init() {
      //先获取系统的流程配置参数,
      this.loadSystemConfig();
    },

    loadSystemConfig() {
      //获取一些系统配置中的采购单的审核流程，主要确认是否显示侧边栏目和加载侧边栏的数据
      util.ajax
        .get("/config/list")
        .then(response => {
          let data = response.data;
          let config = data["BUY_CHECK"];
          if (config.keyValue === "open") {
            this.haveCheck = true;
            this.reloadUncheckData();
            this.showSider = true;
          } else {
            this.haveCheck = false;
            this.showSider = false;
          }
          let config2 = data["COMPANY_TYPE"];
          if ("medicine" === config2.keyValue) {
            this.isMedicine = true;
          } else {
            this.isMedicine = false;
          }
        })
        .catch(error => {
          console.log(error);
          util.errorProcessor(this, error);
        });
    },
    moment: function() {
      return moment();
    },
    handleRowDbClick(row) {
      this.$Modal.confirm({
        title: "确认删除商品？",
        content: "<p>确认删除商品 " + row.goods.name + "?</p>",
        onOk: () => {
          for (var i = 0; i < this.orderItems.length; i++) {
            if (row.goodsId === this.orderItems[i].goodsId) {
              this.orderItems.splice(i, 1);
              this.buyOrder.orderItemIds.splice(i, 1);
            }
          }
        },
        onCancel: () => {}
      });
    },
    onSelectGoods(goodsId, goods) {
      if (goodsId && goods) {
        var index = this.buyOrder.orderItemIds.indexOf(goods.id);
        if (index < 0) {
          //验证下供应商和商品是否存在特殊监管条件
          if (goods.specialManage && !this.supplierSpecialManage) {
            this.$Modal.info({
              title: "药品特殊经营监管提示",
              content:
                "商品：" +
                goods.name +
                "存在“药品特殊经营”标识，而供应商没有该资质，不能添加该商品."
            });
            this.$refs.goodsSelect.clearSingleSelect();
            return;
          }
          if (goods.coldManage && !this.supplierColdManage) {
            this.$Modal.info({
              title: "冷链经营管理监管提示",
              content:
                "商品：" +
                goods.name +
                "存在“冷链经营管理”标识，而供应商没有该资质，不能添加该商品."
            });
            this.$refs.goodsSelect.clearSingleSelect();
            return;
          }
          this.setBuyOrderDetails(goods);
        } else {
          this.$Message.warning("该商品已经添加");
        }
        this.$refs.goodsSelect.clearSingleSelect();
      }
    },

    setBuyOrderDetails(goods) {
      if (!goods.id) {
        return;
      }
      //制造一个采购单详情的记录信息
      let item = {
        goodsId: goods.id,
        goodsName: goods.name,
        quantity: 0,
        buyPrice: goods.inPrice,
        amount: 0,
        goods: goods
      };
      console.log(item);
      this.orderItems.push(item);
      this.buyOrder.orderItemIds.push(goods.id);
      let self = this;
      setTimeout(function() {
        self.$refs.buyOrderTable.$el
          .querySelector(".ivu-table-body tr:last-child input")
          .focus();
      }, 400);
    },

    clearData() {
      this.buyOrder = {
        supplierId: null,
        eta: moment()
          .add(1, "d")
          .format("YYYY-MM-DD"),
        orderItemIds: []
      };
      this.orderItems = [];
      this.$refs.buyOrderForm.resetFields();
    },
    closeTab() {
      this.clearData();
      let pageName = util.closeCurrentTab(this);
      this.$router.push({
        name: pageName
      });
    },

    doSave() {
      var self = this;
      this.$Modal.confirm({
        title: "提交确认",
        content:
          "请确认采购数据录入完全正确, 提交到下一步: " +
          (this.haveCheck ? "“采购单审核”" : "“采购收货”"),
        onCancel: () => {},
        onOk: () => {
          self.saving = true;
          util.ajax
            .post("/buy/add", self.buyOrder)
            .then(function(response) {
              self.saving = false;
              console.log(response);
              if (response.status === 200 && response.data) {
                self.buyOrder.id = response.data.orderId;
                self.buyOrder.status = response.data.status;
                self.$Message.success("采购入库订单保存成功");
                self.closeConfirm = true;
                self.reloadUncheckData();
              }
            })
            .catch(function(error) {
              self.saving = false;
              util.errorProcessor(self, error);
            });
        }
      });
    },

    validateSpecialManage() {
      // 药品特殊管理标识，如果商品中存在有，需要验证供应商是否有对应的资质
      let result = {
        code: 1,
        message: ""
      };
      let haveSpecial = false;
      let goodsName = "";
      for (let i = 0; i < this.orderItems.length; i++) {
        let goods = this.orderItems[i].goods;
        if (goods && goods.specialManage) {
          haveSpecial = true;
          goodsName = goods.name;
          break;
        }
      }
      if (!haveSpecial) {
        return result; //没有这样的商品，直接返回
      }
      //存在特殊管理药品, 需要验证供应商是否也有这个资质
      if (!this.supplierSpecialManage) {
        result.code = -1;
        result.message =
          "商品：" +
          goodsName +
          "存在有“药品特殊管理”标识, 而供应商没有“药品特殊经营管理”资质";
        return result;
      }
      return result;
    },

    validateColdManage() {
      //冷链经营标识，需要验证供应商是否也存在“冷链经营”资质, 且：温控方式，到货温度，温控状态，运输方式为必输项
      //先检查下商品中是否存在有冷链经营标识
      let result = {
        code: 1,
        message: ""
      };
      let haveCold = false;
      let goodsName = "";
      for (let i = 0; i < this.orderItems.length; i++) {
        let goods = this.orderItems[i].goods;
        if (goods && goods.coldManage) {
          haveCold = true;
          goodsName = goods.name;
          break;
        }
      }
      if (!haveCold) {
        return result; //没有这样的商品，直接返回
      }
      //存在冷链经营商品, 需要验证供应商是否也有这个资质
      if (!this.supplierColdManage) {
        result.code = -1;
        result.message =
          "商品：" +
          goodsName +
          "存在有“冷链经营”标识, 而供应商没有“冷链经营”资质";
        return result;
      }
      //如果供应商也有资质，则验证：温控方式，到货温度，温控状态，运输方式为必输项

      if (!this.buyOrder.temperControlId || !this.buyOrder.shipMethodId) {
        result.code = -2;
        result.message =
          "商品：" +
          goodsName +
          "存在有“冷链经营”标识, 温控方式、运输方式为必输项";
        return result;
      }
      //都能验证通过的话，才能通过
      return result;
    },

    saveBuyOrder() {
      this.buyOrder.details = this.orderItems;
      let self = this;
      this.$refs.buyOrderForm.validate(valid => {
        if (!valid) {
          self.$Message.error("请检查输入!");
        } else {
          console.log("validate cold manage.");
          //如果商品存在有“药品特殊监管”标识，需要供应商也存在有这个资质
          let specialValidate = self.validateSpecialManage();
          if (specialValidate.code < 0) {
            self.$Modal.info({
              title: "商品药品特殊经营管理提示",
              content: specialValidate.message
            });
            return;
          }

          //验证是否存在冷链管理商品，如果存在，需要验证是否已经输入温控方式，冷链管理商品必输温控方式
          let coldValidate = self.validateColdManage();
          if (coldValidate.code < 0) {
            self.$Modal.info({
              title: "商品冷链经营管理提示",
              content: coldValidate.message
            });
            return;
          }
          self.doSave();
        }
      });
    },

    changeSiderShow() {
      this.showSider = !this.showSider;
    },

    editBuyOrder(row) {
      if (!row.id || row.id <= 0) {
        return;
      }
      let json = JSON.parse(JSON.stringify(row));
      //设置当前单子的供应商冷链和特殊药品标识
      this.supplierColdManage = json.supplierColdManage ? true : false;
      this.supplierSpecialManage = json.supplierSpecialManage ? true : false;

      json.eta = json.eta ? moment(json.eta).format("YYYY-MM-DD") : "";
      console.log(json);
      this.buyOrder = json;
      let self = this;
      this.orderItems = [];
      this.buyOrder.orderItemIds = [];
      //获取该笔订单的详情信息
      util.ajax
        .get("/buy/orderdetail/" + json.id)
        .then(response => {
          let data = response.data;
          console.log(data);
          if (data && data.length > 0) {
            self.orderItems = data;
            for (let i = 0; i < data.length; i++) {
              self.buyOrder.orderItemIds.push(data[i].goodsId);
            }
          }
        })
        .catch(error => {
          util.errorProcessor(self, error);
        });
    },

    reloadUncheckData() {
      let query = {
        statusNames: ["INIT", "REJECTED"]
      };
      let self = this;
      this.uncheckTabLoading = true;
      util.ajax
        .post("/buy/list", query)
        .then(function(response) {
          console.log(response.data);
          self.uncheckTabLoading = false;
          if (response.status === 200 && response.data) {
            self.uncheckData = response.data;
          }
          self.$refs.uncheckTable.clearCurrentRow();
        })
        .catch(function(error) {
          self.uncheckTabLoading = false;
          util.errorProcessor(self, error);
        });
    },

    removeBuyOrder(buyOrderId) {
      console.log("remove buy order id:" + buyOrderId);
      let self = this;
      this.$Modal.confirm({
        title: "删除订单确认",
        content: "是否确认删除采购单",
        onOk: () => {
          //删除操作请求
          self.uncheckTabLoading = true;
          util.ajax
            .delete("/buy/remove/" + buyOrderId)
            .then(response => {
              self.uncheckTabLoading = false;
              self.$Message.success("采购单删除成功");
              self.reloadUncheckData();
            })
            .catch(error => {
              self.uncheckTabLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    },

    supplierChange(supplierId, supplier) {
      //赋值特殊管理标识
      console.log(supplier);
      if (supplier && supplier.id) {
        this.supplierColdManage = supplier.coldBusiness ? true : false;
        this.supplierSpecialManage = supplier.canSpecial ? true : false;
      }
    }
  }
};
</script>

<style >
.option-goods-spec {
  float: right;
  color: #999;
}
.ivu-form-item {
  margin-bottom: 15px;
}

.ivu-table-cell {
  padding-left: 5px;
  padding-right: 5px;
}
th .ivu-table-cell {
  white-space: nowrap;
}
.ivu-table-body,
.ivu-table-tip {
  min-height: 300px;
}
@media (max-height: 800px) {
  .ivu-table-body,
  .ivu-table-tip {
    min-height: 120px;
  }
}

.uncheck-table .statusClass {
  display: block;
}

.uncheck-table .ivu-table-row-hover .statusClass {
  display: none;
}

.uncheck-table .ivu-btn-group {
  display: none;
}

.uncheck-table .ivu-table-row-hover .ivu-btn-group {
  display: block;
}
</style>

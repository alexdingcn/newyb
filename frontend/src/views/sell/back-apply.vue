<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
      <Row :gutter="10">
          <i-col :span="showSider ? '4' : '0'">
            <Card>
                <p slot="title">
                    未审核销售单
                    <Tooltip transfer placement="right-start">
                        <Icon type="ios-help-outline"></Icon>
                        <div slot="content" >
                            <p>展现销售单录入后未审核通过的列表, 可以提取修改和删除操作</p>
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
                <p slot="title">
                  <a href="javascript:void(0)" @click="changeSiderShow" style="margin-right: 5px;" >
                      <Icon v-if="showSider" type="chevron-left"></Icon>
                      <Icon v-else type="chevron-right"></Icon>
                  </a>
                    <Icon type="log-in"></Icon>
                    销售退货制单
                </p>
                <div slot="extra"> 
                    <ButtonGroup >
                        <Button type="primary" icon="plus" :loading="loading" @click="getSaleOrderShow">销售单提取</Button>
                        <Button type="warning" icon="arrow-swap" @click="backAllQuantity">整单退货</Button>
                        <Button type="success" icon="checkmark" :loading="loading" @click="saveSubmit">确认保存</Button>
                    </ButtonGroup>
                </div>

                <h3 style="margin-bottom: 0.8em;">销售退货审批流程:</h3>
                <Row type="flex" justify="start">
                    <Steps :current="0">
                        <Step title="制单" content="制作销售退货申请单"></Step>
                        <Step v-if="bmFlow" title="退出单审核" content="销售经理审核确认"></Step>
                        <Step v-if="qaFlow" title="退出单审核" content="质管经理审核确认"></Step>
                        <Step title="退货收货" content="销售退货收货"></Step>
                        <Step v-if="reQCFlow" title="退货单质量复核" content="质检员对退出单明细进行质量复核"></Step>
                        <Step v-if="fnFlow" title="退货单终审" content="对销售退货单进行终审，通过后变更库存"></Step>
                    </Steps>
                </Row>
                <hr size="1" style="margin-top: 0.8em; margin-bottom: 2.0em; width: 90%; color: #dddee1"/>

                <Form ref="applyForm" :model="applyForm" :label-width="100">
                    <Row class="row-margin-bottom">
                        <i-col span="6">
                            <FormItem label="客户" >
                                <i-input type="text" disabled v-model="applyForm.customerName"></i-input>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="客户代表" >
                                <i-input type="text" disabled v-model="applyForm.customerRepName"></i-input>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="销售员" >
                                <sale-select v-model="applyForm.saleId" disabled></sale-select>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row class="row-margin-bottom">
                        <i-col span="6">
                            <FormItem label="免零" :error="freeAmountError">
                                <i-input number v-model="applyForm.freeAmount"></i-input>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="仓库点">
                                <warehouse-select v-model="applyForm.warehouseId" disabled></warehouse-select>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="销售单号" :error="refOrderError">
                                <i-input type="text" disabled v-model="applyForm.refOrderNo"></i-input>
                            </FormItem>
                        </i-col>
                    </Row>

                    <h4 style="margin-top:20px; margin-bottom:5px;">
                        <Row type="flex" justify="start">
                            <span>总退货数量: <strong style="color:red;">{{totalBackQuantity}}</strong></span>
                            <span class="margin-left-50">总金额: <strong style="color:red;">{{totalAmount}}</strong></span>
                            <span class="margin-left-50">总退货成本: <strong style="color:red;">{{totalCostAmount}}</strong></span>
                        </Row>
                    </h4>
                    <Table ref="details" border highlight-row :loading="loading" 
                        no-data-text="选择商品添加或者从销售单导入" style="width: 100%;" size="small" 
                        :columns="detailsColumns" :data="details" 
                    >
                    </Table>

                    <h3 class="margin-top-10">配送和交货</h3>
                    <Row class="row-margin-bottom">
                      <i-col span="6">
                          <FormItem label="温控方式" >
                              <option-select optionType="TEMPER_CONTROL" v-model="applyForm.temperControlId"></option-select>
                          </FormItem>
                      </i-col>
                      <i-col span="6">
                          <FormItem label="运输方式" >
                              <option-select optionType="SHIP_METHOD" v-model="applyForm.shipMethod"></option-select>
                          </FormItem>
                      </i-col>
                      <i-col span="6">
                          <FormItem label="送货单号">
                              <i-input type="text" v-model="applyForm.shipNo"></i-input>
                          </FormItem>
                      </i-col>
                      <i-col span="6">
                          <FormItem label="承运公司" >
                              <ship-company-select v-model="applyForm.shipCompanyId"></ship-company-select>
                          </FormItem>
                      </i-col>
                  </Row>
                  <Row class="row-margin-bottom">
                      <i-col span="12">
                          <FormItem label="退货原因">
                              <i-input type="text" v-model="applyForm.backReason"></i-input>
                          </FormItem>
                      </i-col>
                      <i-col span="12">
                          <FormItem label="备注信息">
                              <i-input type="text" v-model="applyForm.comment"></i-input>
                          </FormItem>
                      </i-col>
                  </Row>
               </Form>
            </Card>
          </i-col>
      </Row>

      <Modal v-model="selectSellOrderModal" width="75" footerHide :mask-closable="false" title="销售单提取" >
          <sell-sale-revivew view-method="SELECT" @on-choose="saleOrderChoose"></sell-sale-revivew>
      </Modal>

      <Modal v-model="goodsExpandModal" width="60" :mask-closable="false" title="商品详情信息" :footerHide="true">
        <goods-expand ref="goodsExpand" :goodsSpecs="expandGoodsSpecs" :productDate="expandProductDate" :expDate="expandExpDate"></goods-expand>
      </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import saleSelect from "@/views/selector/sale-select.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import goodSelect from "@/views/selector/good-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import sellSaleRevivew from "./sell-sale-review.vue";
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";
import shipCompanySelect from "@/views/selector/ship-company-select.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";

export default {
  name: "sell-back-apply",
  components: {
    saleSelect,
    warehouseSelect,
    goodSelect,
    optionSelect,
    sellSaleRevivew,
    goodsSpecTags,
    shipCompanySelect,
    goodsExpand
  },
  data() {
    return {
      customerRepList: [],
      loading: false,
      applyForm: {
        refOrderId: "",
        refOrderNo: "",
        customerId: "",
        customerName: "",
        customerRepId: "",
        customerRepName: "",
        saleId: "",
        warehouseId: "",
        temperControlId: "",
        shipMethod: "",
        shipCompanyId: "",
        freeAmount: 0,
        details: []
      },
      refOrderError: "",
      freeAmountError: "",
      goodsExpandModal: false,
      expandGoodsSpecs: [],
      expandProductDate: "",
      expandExpDate: "",
      details: [],
      detailsColumns: [
        {
          title: "序号",
          type: "index",
          width: 60
        },
        {
          title: "商品名称",
          key: "goodsName",
          width: 180,
          render: (h, params) => {
            let self = this;
            return h(
              "Button",
              {
                props: {
                  type: "text",
                  icon: "eye"
                },
                on: {
                  click: () => {
                    self.expandProductDate = params.row.productDate
                      ? moment(params.row.productDate).format("YYYY-MM-DD")
                      : "";
                    self.expandExpDate = params.row.expDate
                      ? moment(params.row.expDate).format("YYYY-MM-DD")
                      : "";
                    self.expandGoodsSpecs = params.row.goods.goodsSpecs
                      ? params.row.goods.goodsSpecs
                      : [];
                    let goodsId = params.row.goods.id;
                    self.$refs.goodsExpand.loadGoodsData(goodsId);
                    self.goodsExpandModal = true;
                  }
                }
              },
              params.row.goods.name
            );
          }
        },
        {
          title: "生产企业",
          key: "factoryName",
          width: 180,
          render: (h, params) => {
            return h("span", params.row.goods.factoryName);
          }
        },
        {
          title: "产地",
          key: "origin",
          width: 100,
          render: (h, params) => {
            return h("span", params.row.goods.origin);
          }
        },
        {
          title: "规格",
          key: "spec",
          width: 120,
          render: (h, params) => {
            return h(goodsSpecTags, {
              props: {
                tags: params.row.goods.goodsSpecs,
                color: "blue"
              }
            });
          }
        },
        {
          title: "单位",
          key: "unitName",
          width: 100,
          render: (h, params) => {
            return h("span", params.row.goods.unitName);
          }
        },
        {
          title: "批次号",
          key: "batchCode",
          width: 150
        },
        {
          title: "销售数量",
          key: "saleQuantity",
          width: 120
        },
        {
          title: "已退数量",
          key: "alreadyBackQuantity",
          width: 120
        },
        {
          title: "退货数量",
          key: "backQuantity",
          width: 140,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.details[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.details[params.index];
                  row[params.column.key] = event.target.value;
                  self.resetAmount(params.index);
                }
              }
            });
          }
        },
        {
          title: "单价",
          key: "backPrice",
          width: 140
        },
        {
          title: "金额",
          key: "amount",
          width: 140
        },
        {
          title: "退货成本",
          key: "costAmount",
          width: 140,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.details[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.details[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "生产日期",
          key: "productDate",
          align: "center",
          width: 180,
          render: (h, params) => {
            return h(
              "span",
              params.row.productDate
                ? moment(params.row.productDate).format("YYYY-MM-DD")
                : ""
            );
          }
        },
        {
          title: "有效期至",
          key: "expDate",
          align: "center",
          width: 180,
          render: (h, params) => {
            return h(
              "span",
              params.row.productDate
                ? moment(params.row.expDate).format("YYYY-MM-DD")
                : ""
            );
          }
        },
        {
          title: "货位",
          key: "location",
          width: 120
        }
      ],
      currEditLocationRow: "",
      currEditLocationIndex: "",
      currCustomer: {},
      selectSellOrderModal: false,
      showSider: true,
      uncheckTabLoading: false,
      uncheckData: [],
      uncheckColumns: [
        {
          title: "销售退货申请单",
          key: "id",
          render: (h, params) => {
            let self = this;
            let status = params.row.status;
            let statusLabel = "";
            let statusColor = "";
            if (status === "INIT") {
              statusLabel = "初始制作";
              statusColor = "#5cadff";
            } else if (status === "INIT_SALE_CHECKED") {
              statusLabel = "销售审核待收货";
              statusColor = "#ff9900";
            } else if (status === "INIT_QUALITY_CHECKED") {
              statusLabel = "质管审核待收货";
              statusColor = "#ed3f14";
            } else if (status === "BACK_RECEIVE") {
              statusLabel = "已收货";
              statusColor = "rgb(107, 175, 158)";
            } else if (status === "QUALITY_CHECKED") {
              statusLabel = "待终审";
              statusColor = "#19be6b";
            }
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
                  vertical: false,
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
                        self.editOrder(params.row);
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
                        self.removeOrder(params.row);
                      }
                    }
                  },
                  "删除"
                )
              ]
            );
            let orderNumnber = params.row.orderNumber;
            let customerName = params.row.customerName
              ? params.row.customerName
              : "";
            let createdTime = moment(params.row.createdTime).format(
              "YYYY-MM-DD HH:mm"
            );
            let createdBy = params.row.createdBy;
            let warehouseName = params.row.warehouseName;
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
                h("h4", customerName + "[" + warehouseName + "]"),
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
      bmFlow: true,
      qaFlow: true,
      reQCFlow: true,
      fnFlow: true
    };
  },
  computed: {
    totalBackQuantity: function() {
      return this.details.reduce((total, item) => {
        let backQuantity = item.backQuantity ? item.backQuantity : 0;
        return total + parseFloat(backQuantity);
      }, 0);
    },
    totalAmount: function() {
      return this.details.reduce((total, item) => {
        let amount = item.amount ? item.amount : 0;
        return total + parseFloat(amount);
      }, 0);
    },
    totalCostAmount: function() {
      return this.details.reduce((total, item) => {
        let costAmount = item.costAmount ? item.costAmount : 0;
        return total + parseFloat(costAmount);
      }, 0);
    }
  },
  mounted() {
    this.systemConfig();
    this.reloadUncheckData();
  },
  methods: {
    systemConfig() {
      // 获取系统流程
      util.ajax
        .get("/config/list")
        .then(response => {
          let data = response.data;
          let config = data["SALE_BACK_SM_CHECK"];
          if ("open" === config.keyValue) {
            this.bmFlow = true;
          } else {
            this.bmFlow = false;
          }
          let config1 = data["SALE_BACK_QA_CHECK"];
          if ("open" === config1.keyValue) {
            this.qaFlow = true;
          } else {
            this.qaFlow = false;
          }
          let config2 = data["SALE_BACK_QUALITY_CHECK"];
          if ("open" === config2.keyValue) {
            this.reQCFlow = true;
          } else {
            this.reQCFlow = false;
          }
          let config3 = data["SALE_BACK_FINAL_CHECK"];
          if ("open" === config3.keyValue) {
            this.fnFlow = true;
          } else {
            this.fnFlow = false;
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    changeSiderShow() {
      this.showSider = !this.showSider;
    },

    reloadUncheckData() {
      this.uncheckTabLoading = true;
      let reqData = {
        statusList: [
          "INIT",
          "INIT_SALE_CHECKED",
          "INIT_QUALITY_CHECKED",
          "BACK_RECEIVE",
          "QUALITY_CHECKED"
        ]
      };
      util.ajax
        .post("/sell/back/list", reqData)
        .then(response => {
          this.uncheckTabLoading = false;
          this.uncheckData = response.data;
        })
        .catch(error => {
          this.uncheckTabLoading = false;
          util.errorProcessor(this, error);
        });
    },

    editOrder(row) {
      console.log(row);
      let data = JSON.parse(JSON.stringify(row));
      this.applyForm = data;
      //获取详情信息
      this.loading = true;
      util.ajax
        .get("/sell/back/" + data.id + "/details")
        .then(response => {
          this.loading = false;
          this.details = response.data;
        })
        .catch(error => {
          this.loading = false;
          util.errorProcessor(this, error);
        });
    },

    removeOrder(row) {
      let self = this;
      this.$Modal.confirm({
        title: "删除确认",
        content: "是否确认删除这笔销售退货申请单, 删除后不可恢复!",
        onOk: () => {
          self.uncheckTabLoading = true;
          util.ajax
            .delete("/sell/back/delete/" + row.id)
            .then(response => {
              self.uncheckTabLoading = false;
              self.$Message.success("删除成功");
              self.reloadUncheckData();
            })
            .catch(error => {
              self.uncheckTabLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    },

    resetAmount(index) {
      let row = this.details[index];
      if (!row) {
        return;
      }
      let backQuantity =
        row.backQuantity && !isNaN(row.backPrice) ? row.backQuantity : 0;
      let backPrice =
        row.backPrice && !isNaN(row.backPrice) ? row.backPrice : 0;
      row.amount = (backQuantity * backPrice).toFixed(2);
      this.$set(this.details, index, row);
    },

    saveSubmit() {
      this.freeAmountError = "";
      this.refOrderError = "";
      if (!this.applyForm.refOrderId) {
        this.refOrderError = "系统异常，获取关联订单失败, 请刷新页面重新操作.";
        return;
      }
      if (this.applyForm.freeAmount && this.applyForm.freeAmount < 0) {
        this.freeAmountError = "免零金额不能为小于0的数";
        return;
      }
      for (let i = 0; i < this.details.length; i++) {
        let item = this.details[i];
        let backQuantity = item.backQuantity ? item.backQuantity : 0;
        let saleQuantity = item.saleQuantity ? item.saleQuantity : 0;
        let alreadyBackQuantity = item.alreadyBackQuantity
          ? item.alreadyBackQuantity
          : 0;
        if (saleQuantity - alreadyBackQuantity < backQuantity) {
          this.$Modal.warning({
            title: "退货数量错误提示",
            content:
              "商品：" + item.goodsName + "的退货数不能超出销售单销售数量."
          });
          return;
        }
      }
      let self = this;
      this.applyForm.details = this.details;
      this.$Modal.confirm({
        title: "保存确认",
        content: "是否已经确认需要退货的商品信息和金额输入完整并且正确?",
        onOk: () => {
          self.loading = true;
          util.ajax
            .post("/sell/back/save", self.applyForm)
            .then(response => {
              self.loading = false;
              self.$Message.success("退货单提交保存成功.");
              self.clearForm();
              self.reloadUncheckData();
            })
            .catch(error => {
              self.loading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    },

    clearForm() {
      this.freeAmountError = "";
      this.refOrderError = "";
      this.applyForm = {
        refOrderId: "",
        refOrderNo: "",
        customerId: "",
        customerName: "",
        customerRepId: "",
        customerRepName: "",
        saleId: "",
        warehouseId: "",
        temperControlId: "",
        shipMethod: "",
        shipCompanyId: "",
        freeAmount: 0,
        details: []
      };
      this.details = [];
      this.$refs.applyForm.resetFields();
    },

    getSaleOrderShow() {
      this.selectSellOrderModal = true;
    },

    saleOrderChoose(saleOrder, orderDetails) {
      this.selectSellOrderModal = false;
      if (!saleOrder.id || !orderDetails || orderDetails.length <= 0) {
        return;
      }
      this.applyForm = {
        refOrderId: saleOrder.id,
        refOrderNo: saleOrder.orderNumber,
        customerId: saleOrder.customerId,
        customerName: saleOrder.customerName,
        customerRepId: saleOrder.customerRepId,
        customerRepName: saleOrder.customerRepName,
        saleId: saleOrder.saleId,
        warehouseId: saleOrder.warehouseId,
        temperControlId: saleOrder.temperControlId,
        shipMethod: saleOrder.shipMethod,
        shipCompanyId: saleOrder.shipCompanyId,
        freeAmount: 0
      };
      //详情
      this.details = []; //清除原有的
      for (let i = 0; i < orderDetails.length; i++) {
        let detail = orderDetails[i];
        let goods = detail.goods;
        console.log(goods);
        let item = {
          sellDetailId: detail.id,
          repertoryId: detail.repertoryId,
          goodsId: goods.id,
          batchCode: detail.batchCode,
          productDate: detail.productDate
            ? moment(detail.productDate).format("YYYY-MM-DD")
            : "",
          expDate: detail.expDate
            ? moment(detail.expDate).format("YYYY-MM-DD")
            : "",
          saleQuantity: detail.quantity,
          alreadyBackQuantity: detail.backQuantity ? detail.backQuantity : 0,
          backQuantity: 0,
          backPrice: detail.realPrice,
          amount: 0,
          costAmount: 0,
          location: detail.location,
          goods: goods
        };
        this.details.push(item);
      }
    },

    backAllQuantity() {
      for (let i = 0; i < this.details.length; i++) {
        let row = this.details[i];
        let saleQuantity = row.saleQuantity ? row.saleQuantity : 0;
        let alreadyBackQuantity = row.alreadyBackQuantity
          ? row.alreadyBackQuantity
          : 0;
        row.backQuantity = saleQuantity - alreadyBackQuantity;
        this.resetAmount(i);
      }
    }
  }
};
</script>

<style>
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


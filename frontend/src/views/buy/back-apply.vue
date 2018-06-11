<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row :gutter="10">
          <i-col :span="showSider ? '4' : '0'">
            <Card>
                <p slot="title">
                    未审核采购退出单
                    <Tooltip transfer placement="right-start">
                        <Icon type="ios-help-outline"></Icon>
                        <div slot="content" >
                            <p>展现采购退出单录入后未审核通过的列表, 可以提取修改和删除操作</p>
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
                    <Icon type="log-out"></Icon>
                    采购退出申请
                </p>
                <div slot="extra">
                    <ButtonGroup>
                        <Button type="primary" icon="plus" @click="showInOrderModal">载入采购入库单</Button>
                        <Button type="warning" icon="arrow-swap" @click="backAllQuantity">整单退出</Button>
                        <Button type="success" icon="checkmark" :loading="saveLoading" @click="applySave" >确认保存</Button>
                    </ButtonGroup>
                </div>

                <h3 style="margin-bottom: 0.8em;">采购退出单审批流程:</h3>
                <Row type="flex" justify="start">
                    <Steps :current="0">
                        <Step title="制单" content="制作采购退出申请单"></Step>
                        <Step v-if="bmFlow" title="退出单审核" content="采购经理审核确认"></Step>
                        <Step v-if="qaFlow" title="退出单审核" content="质管经理审核确认"></Step>
                        <Step v-if="reQCFlow" title="退出单质量检验" content="质检员对退出单明细进行质量复核"></Step>
                        <Step title="退出单终审" content="对采购退出单进行终审，通过后变更库存"></Step>
                    </Steps>
                </Row>
                <hr size="1" style="margin-top: 0.8em; margin-bottom: 2.0em; width: 90%; color: #dddee1"/>

                <Form ref="applyForm" :model="formItem" :label-width="100" >
                    <Row class="row-margin-bottom">
                        <i-col span="6">
                            <FormItem label="供应商">
                                <Input type="text" v-model="formItem.supplierName" disabled placeholder="根据选择的采购入库单自动补充" />
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="供应商代表">
                                <Input type="text" v-model="formItem.supplierContactName" disabled placeholder="根据选择的采购入库单自动补充"/>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="采购员" >
                                <buyer-select v-model="formItem.buyerId" disabled ></buyer-select>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row class="row-margin-bottom">
                        <i-col span="6">
                            <FormItem label="仓库点" >
                                <warehouse-select ref="warehouseSelect" v-model="formItem.warehouseId" disabled></warehouse-select>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="入库单号">
                                <Input type="text" disabled v-model="formItem.inOrderNumber"/>
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="入库收货时间">
                                <DatePicker type="datetime" disabled format="yyyy-MM-dd HH:mm" placeholder="入库收货时间" style="width: 100%"
                                    v-model="formItem.inReceiveTime"></DatePicker>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row class="row-margin-bottom">
                        <i-col span="6">
                            <FormItem label="退货日期">
                                <DatePicker type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="退货日期" v-model="formItem.backTime"></DatePicker>
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="退货原因">
                                <Input type="text" placeholder="请输入退货原因" v-model="formItem.keyWord" />
                            </FormItem>
                        </i-col>
                    </Row>

                    <h4 style="margin-top:20px; margin-bottom:5px;">
                        <Row type="flex" justify="start">
                            <span>总退货数量: <strong style="color:red;">{{totalQuantity}}</strong></span>
                            <span class="margin-left-50">总金额: <strong style="color:red;">{{totalAmount}}</strong></span>
                        </Row>
                    </h4>
                    <Table ref="table" border highlight-row size="small" 
                        no-data-text="选择采购入库单加入对应商品" :loading="saveLoading" 
                        :columns="tableColumns" :data="tableData">
                    </Table>

                </Form>
            </Card>
        </i-col>
      </Row>

      <Modal v-model="selectOrderModal" width="80" footerHide :mask-closable="false" title="采购入库单提取" >
            <in-order-check view-method="SELECT" @on-choose="inOrderChoose"></in-order-check>
        </Modal>

      <Modal v-model="goodsExpandModal" width="60" :mask-closable="false" title="商品详情信息" :footerHide="true">
        <goods-expand ref="goodsExpand" :goodsSpecs="expandGoodsSpecs" :productDate="expandProductDate" :expDate="expandExpDate"></goods-expand>
      </Modal>

  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import buyerSelect from "@/views/selector/buyer-select.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";
import inOrderCheck from "@/views/repertory/in-check.vue";

export default {
  name: "back-apply",
  components: {
    warehouseSelect,
    buyerSelect,
    goodsExpand,
    goodsSpecTags,
    inOrderCheck
  },
  data() {
    return {
      saveLoading: false,
      currWarehouse: {},
      formItem: {
        warehouseId: "",
        supplierId: "",
        supplierContactId: "",
        supplierName: "",
        supplierContactName: "",
        inOrderId: "",
        inOrderNumber: "",
        inReceiveTime: "",
        buyerId: "",
        backTime: moment().format("YYYY-MM-DD HH:mm:ss"),
        keyWord: "",
        details: []
      },
      goodsExpandModal: false,
      expandGoodsSpecs: [],
      expandProductDate: "",
      expandExpDate: "",
      tableData: [],
      tableColumns: [
        {
          title: "商品名称",
          key: "goodsName",
          width: 200,
          sortable: true,
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
          title: "批次号",
          key: "batchCode",
          width: 150
        },
        {
          title: "产地",
          key: "origin",
          width: 100,
          render: (h, params) => {
            return h("span", {}, params.row.goods.origin);
          }
        },
        {
          title: "规格",
          key: "spec",
          width: 120,
          render: (h, params) => {
            return h(goodsSpecTags, {
              props: {
                tags: params.row.goods.goodsSpecs
                  ? params.row.goods.goodsSpecs
                  : [],
                color: "blue"
              }
            });
          }
        },
        {
          title: "生产企业",
          key: "factoryName",
          width: 180,
          render: (h, params) => {
            return h("span", {}, params.row.goods.factoryName);
          }
        },
        {
          title: "单位",
          key: "unitName",
          width: 80,
          render: (h, params) => {
            return h("span", {}, params.row.goods.unitName);
          }
        },
        {
          title: "当前库存量",
          key: "quantity",
          width: 100
        },
        {
          title: "在单数量",
          key: "onWayQuantity",
          width: 100
        },
        {
          title: "采购数量",
          key: "inQuantity",
          width: 100
        },
        {
          renderHeader: (h, params) => {
            return h(
              "Tooltip",
              {
                props: {
                  transfer: true,
                  placement: "top"
                }
              },
              [
                h("span", "退出数量"),
                h("Icon", {
                  props: { type: "ios-help-outline", size: "15" },
                  style: { marginLeft: "5px" }
                }),
                h(
                  "div",
                  {
                    slot: "content"
                  },
                  [
                    h(
                      "h4",
                      { style: { color: "#ed3f14" } },
                      "注意：退出数量最大为当前库存量."
                    )
                  ]
                )
              ]
            );
          },
          key: "backQuantity",
          width: 120,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                value: self.tableData[params.index][params.column.key],
                number: true
              },
              on: {
                "on-blur"(event) {
                  var row = self.tableData[params.index];
                  row[params.column.key] = event.target.value;
                  var price = row["buyPrice"];
                  var qty = event.target.value;
                  if (!isNaN(qty) && !isNaN(price)) {
                    row.amount = (qty * price).toFixed(2);
                  }
                }
              }
            });
          }
        },
        {
          title: "采购单价",
          key: "buyPrice",
          width: 120
        },
        {
          title: "金额",
          key: "amount",
          width: 120
        },
        {
          title: "生产日期",
          width: 120,
          key: "productDate",
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
          width: 120,
          render: (h, params) => {
            return h(
              "span",
              params.row.expDate
                ? moment(params.row.expDate).format("YYYY-MM-DD")
                : ""
            );
          }
        },
        {
          title: "库位",
          key: "location",
          width: 120
        }
      ],
      showSider: true,
      uncheckTabLoading: false,
      uncheckData: [],
      uncheckColumns: [
        {
          title: "采购退出单",
          key: "id",
          render: (h, params) => {
            let self = this;
            let status = params.row.status;
            let statusLabel = "";
            let statusColor = "";
            if (status === "BACK_INIT") {
              statusLabel = "初建待审";
              statusColor = "#5cadff";
            } else if (status === "BACK_BUY_CHECK") {
              statusLabel = "采购员已审";
              statusColor = "#ff9900";
            } else if (status === "BACK_QUALITY_CHECK") {
              statusLabel = "质管已审";
              statusColor = "#ed3f14";
            } else if (status === "BACK_QUALITY_RECHECK") {
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
                        self.removeOrder(params.row.id);
                      }
                    }
                  },
                  "删除"
                )
              ]
            );

            let orderNumnber = params.row.orderNumber;
            let supplierName =
              params.row.customer && params.row.supplierName
                ? params.row.supplierName
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
                h("h4", supplierName + "[" + warehouseName + "]"),
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
      selectOrderModal: false,
      bmFlow: true,
      qaFlow: true,
      reQCFlow: true
    };
  },
  computed: {
    totalQuantity: function() {
      let result = 0;
      result = this.tableData.reduce((total, item) => {
        let backQuantity = item.backQuantity ? item.backQuantity : 0;
        return total + parseFloat(backQuantity);
      }, 0);
      return result;
    },
    totalAmount: function() {
      let result = 0;
      result = this.tableData.reduce((total, item) => {
        let amount = item.amount ? item.amount : 0;
        return total + parseFloat(amount);
      }, 0);
      return result;
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
          let config = data["BUY_BACK_BM_CHECK"];
          if ("open" === config.keyValue) {
            this.bmFlow = true;
          } else {
            this.bmFlow = false;
          }
          let config1 = data["BUY_BACK_QA_CHECK"];
          if ("open" === config1.keyValue) {
            this.qaFlow = true;
          } else {
            this.qaFlow = false;
          }
          let config2 = data["BUY_BACK_QUALITY_CHECK"];
          if ("open" === config2.keyValue) {
            this.reQCFlow = true;
          } else {
            this.reQCFlow = false;
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
      let reqData = {
        searchStatus: [
          "BACK_INIT",
          "BACK_BUY_CHECK",
          "BACK_QUALITY_CHECK",
          "BACK_QUALITY_RECHECK"
        ]
      };
      this.uncheckTabLoading = true;
      util.ajax
        .post("/buy/back/list", reqData)
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
      this.clearData(); //先清理原来的数据后重新赋值
      //提取采购退货单修改
      let data = JSON.parse(JSON.stringify(row));
      //收货时间和退货时间格式化
      data.inReceiveTime = data.inReceiveTime
        ? moment(data.inReceiveTime).format("YYYY-MM-DD HH:mm")
        : "";
      data.backTime = data.backTime
        ? moment(data.backTime).format("YYYY-MM-DD HH:mm:ss")
        : "";
      this.formItem = data;
      //然后获取退单详情信息
      this.saveLoading = true;
      util.ajax
        .get("/buy/back/" + data.id + "/detail")
        .then(response => {
          console.log(response.data);
          this.saveLoading = false;
          this.tableData = response.data;
        })
        .catch(error => {
          this.saveLoading = false;
          util.errorProcessor(this, error);
        });
    },

    removeOrder(inBackId) {
      //删除采购退货单
      let self = this;
      this.$Modal.confirm({
        title: "删除确认",
        content: "是否确认删除退出单, 删除后不可恢复?",
        onOk: () => {
          self.uncheckTabLoading = true;
          util.ajax
            .delete("/buy/back/" + inBackId)
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

    showInOrderModal() {
      this.selectOrderModal = true;
    },

    inOrderChoose(inOrder) {
      this.clearData(); //先清理数据，在重新赋值
      this.selectOrderModal = false;
      console.log(inOrder);
      if (!inOrder.id || inOrder.id <= 0) {
        this.$Message.info("选择的采购入库单信息缺失.");
        return;
      }
      this.formItem = {
        warehouseId: inOrder.warehouseId,
        supplierId: inOrder.supplierId,
        supplierContactId: inOrder.supplierContactId,
        supplierName: inOrder.supplierName,
        supplierContactName: inOrder.supplierContactName,
        inOrderId: inOrder.id,
        inOrderNumber: inOrder.orderNumber,
        inReceiveTime: inOrder.receiveDate
          ? moment(inOrder.receiveDate).format("YYYY-MM-DD HH:mm")
          : "",
        buyerId: inOrder.buyerId,
        backTime: moment().format("YYYY-MM-DD HH:mm:ss"),
        keyWord: "",
        details: []
      };
      //根据采购单ID获取采购单的库存信息
      this.getDetailsByRefOrderId(inOrder.id);
    },

    getDetailsByRefOrderId(inOrderId) {
      this.tableData = [];
      this.saveLoading = true;
      let reqData = {
        refType: "BUY_ORDER",
        refOrderId: inOrderId
      };
      util.ajax
        .post("/repertory/refOrder/list", reqData)
        .then(response => {
          this.saveLoading = false;
          let data = response.data;
          if (data && data.length > 0) {
            this.setDetailsData(data);
          }
        })
        .catch(error => {
          console.log(error);
          this.saveLoading = false;
          util.errorProcessor(this, error);
        });
    },

    setDetailsData(data) {
      //根据库存明细，制作退货单的明细
      console.log(data);
      for (let i = 0; i < data.length; i++) {
        let dataItem = data[i];
        let goods = dataItem.goods;
        if (!goods || !goods.id) {
          return;
        }
        let item = {
          repertoryInfoId: dataItem.id,
          batchCode: dataItem.batchCode,
          goodsId: dataItem.goodsId,
          goods: goods,
          quantity: dataItem.quantity,
          onWayQuantity: dataItem.onWayQuantity,
          inQuantity: dataItem.inQuantity,
          backQuantity: 0,
          buyPrice: dataItem.buyPrice,
          amount: 0,
          productDate: dataItem.productDate,
          expDate: dataItem.expDate,
          location: dataItem.location
        };
        this.tableData.push(item);
      }
    },

    backAllQuantity() {
      //整单退出的概念，也就是退出数量=库存量
      for (let i = 0; i < this.tableData.length; i++) {
        let row = this.tableData[i];
        let quantity = row.quantity ? row.quantity : 0;
        let buyPrice = row.buyPrice ? row.buyPrice : 0;
        row.backQuantity = quantity;
        let amount = (row.backQuantity * buyPrice).toFixed(2);
        row.amount = amount;
        this.$set(this.tableData, i, row);
      }
    },

    applySave() {
      this.formItem.backTime = this.formItem.backTime
        ? moment(this.formItem.backTime)
        : moment();
      this.formItem.details = this.tableData;
      for (let i = 0; i < this.formItem.details.length; i++) {
        let item = this.formItem.details[i];
        let quantity = item.quantity ? item.quantity : 0;
        if (quantity - item.backQuantity < 0) {
          this.$Modal.info({
            title: "退出数量提示",
            content:
              "商品：“" + item.goods.name + "”对应的退出数量不能大于库存量"
          });
          return;
        }
      }
      //验证通过后，直接弹出确认框
      let self = this;
      this.$Modal.confirm({
        title: "采购退出申请保存确认",
        content: "采购退出商品信息是否已经确认填写完成并且数量正确?",
        onOk: () => {
          self.saveLoading = true;
          util.ajax
            .post("/buy/back/save", self.formItem)
            .then(response => {
              self.saveLoading = false;
              self.$Message.success("采购退出申请保存成功.");
              self.clearData();
              self.reloadUncheckData();
            })
            .catch(error => {
              self.saveLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    },

    clearData() {
      this.formItem = {
        warehouseId: "",
        supplierId: "",
        supplierContactId: "",
        supplierName: "",
        supplierContactName: "",
        inOrderId: "",
        inOrderNumber: "",
        inReceiveTime: "",
        buyerId: "",
        backTime: moment().format("YYYY-MM-DD HH:mm:ss"),
        keyWord: "",
        details: []
      };
      this.tableData = [];
    }
  }
};
</script>

<style >
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


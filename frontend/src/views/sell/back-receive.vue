<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Card >
            <p slot="title">
                <Icon type="android-car"></Icon>
                销售退出收货
            </p>

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

            <Form ref="searchForm" :label-width="100">
              <Row type="flex" justify="start">
                  <i-col span="6">
                      <FormItem label="制单日期">
                          <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="制单日期" style="width:180px"></DatePicker>
                      </FormItem>
                  </i-col>
                  <i-col span="5">
                      <FormItem label="仓库">
                          <warehouse-select v-model="warehouseId"></warehouse-select>
                      </FormItem>
                  </i-col>
                  <i-col span="5">
                      <FormItem label="客户">
                          <customer-select v-model="customerId"></customer-select>
                      </FormItem>
                  </i-col>
                  <i-col span="5">
                      <FormItem label="销售员">
                          <sale-select v-model="saleId"></sale-select>
                      </FormItem>
                  </i-col>
                  <i-col span="3">
                      <Row type="flex" justify="end">
                        <Button type="primary" icon="search" :loading="loading" @click="queryBackOrderList">查询</Button>
                      </Row>
                  </i-col>
              </Row>
          </Form>

          <Table border highlight-row disabled-hover height="300" style="margin-bottom: 2em;"
                   :columns="orderColumns" :data="orderList" :loading="loading" 
				   ref="orderTable" size="small"
                   @on-row-click="handleSelectOrder" 
				   no-data-text="使用右上方输入搜索条件">
			</Table>

            <Table border highlight-row disabled-hover height="350"
                   :columns="detailColumns" :data="details" :loading="detailLoading" 
				   ref="detailTable" size="small"
				   no-data-text="点击上方订单后查看明细">
			</Table>
        </Card>

        <Modal v-model="backReceiveModal" width="50" :mask-closable="false" title="销售退单收货登记" >
            <Form ref="receiveForm" :model="receiveForm" :label-width="90">
                <Row class="row-margin-bottom">
                    <i-col span="12">
                        <FormItem label="系统单号">
                            <Input v-model="receiveForm.orderNumber" readonly />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="送货单号">
                            <Input v-model="receiveForm.shipNo" />
                        </FormItem>
                    </i-col>
                </Row>
                <Row class="row-margin-bottom">
                    <i-col span="12">
                        <FormItem label="运输日期">
                            <DatePicker v-model="receiveForm.shipDate" type="date" placement="bottom-end" placeholder="运输日期"></DatePicker>
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="收货日期">
                            <DatePicker v-model="receiveForm.receiveDate" type="date" placement="bottom-end" placeholder="收货日期"></DatePicker>
                        </FormItem>
                    </i-col>
                </Row>
                <Row class="row-margin-bottom">
                    <i-col span="12">
                        <FormItem label="运输方式">
                            <option-select optionType="SHIP_METHOD" v-model="receiveForm.shipMethod"></option-select>
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="运输工具">
                            <option-select optionType="SHIP_TOOL" v-model="receiveForm.shipTool"></option-select>
                        </FormItem>
                    </i-col>
                </Row>
                <Row class="row-margin-bottom">
                    <i-col span="12">
                        <FormItem label="车牌号">
                            <Input v-model="receiveForm.shipCarNo" />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="驾驶员姓名">
                            <Input v-model="receiveForm.shipDriverName" />
                        </FormItem>
                    </i-col>
                </Row>
                <Row class="row-margin-bottom">
                    <i-col span="12">
                        <FormItem label="承运单位">
                            <ship-company-select v-model="receiveForm.shipCompanyId" ></ship-company-select>
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="发货地址">
                            <Input v-model="receiveForm.shipStartAddress" />
                        </FormItem>
                    </i-col>
                </Row>
                <Row class="row-margin-bottom">
                    <i-col span="12">
                        <FormItem label="收货温度">
                            <Input v-model="receiveForm.receiveTemper" number />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="到货数量">
                            <Input v-model="receiveForm.receiveQuantity" />
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
            <Row slot="footer">
                <ButtonGroup>
                    <Button type="success" icon="checkmark" :loading="receiveLoading" @click="receiveSubmit">确认收货</Button>
                    <Button type="ghost" icon="reply" :loading="receiveLoading" @click="receiveCancel">取消</Button>
                </ButtonGroup>
            </Row>
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
import customerSelect from "@/views/selector/customer-select.vue";
import saleSelect from "@/views/selector/sale-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import shipCompanySelect from "@/views/selector/ship-company-select.vue";
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";

export default {
  name: "sell-back-receive",
  components: {
    warehouseSelect,
    customerSelect,
    saleSelect,
    optionSelect,
    shipCompanySelect,
    goodsSpecTags,
    goodsExpand
  },
  data() {
    const statusShow = function(h, row) {
      let status = row.status;
      let label = "";
      let color = "";
      switch (status) {
        case "INIT":
          label = "初始制单";
          color = "#5cadff";
          break;
        case "INIT_SALE_CHECKED":
          label = "销售已审";
          color = "#2d8cf0";
          break;
        case "INIT_QUALITY_CHECKED":
          label = "质管已审待收货";
          color = "#ff9900";
          break;
        case "BACK_RECEIVE":
          label = "退货已收货";
          color = "rgb(107, 175, 158)";
          break;
        case "QUALITY_CHECKED":
          label = "质量复核完成";
          color = "#19be6b";
          break;
        case "FINAL_CHECKED":
          label = "终审完成";
          color = "#ed3f14";
          break;
      }
      return h(
        "Tag",
        {
          props: {
            type: "dot",
            color: color
          }
        },
        label
      );
    };
    return {
      loading: false,
      dateRange: [
        moment()
          .add(-1, "w")
          .format("YYYY-MM-DD"),
        moment()
          .add(1, "d")
          .format("YYYY-MM-DD")
      ],
      warehouseId: "",
      customerId: "",
      saleId: "",
      orderList: [],
      orderColumns: [
        {
          title: "系统单号",
          key: "orderNumber",
          width: 180
        },
        {
          title: "制单时间",
          key: "createdTime",
          width: 140,
          render: (h, params) => {
            let label = params.row.createdTime
              ? moment(params.row.createdTime).format("YYYY-MM-DD HH:mm")
              : "";
            return h("span", label);
          }
        },
        {
          title: "当前状态",
          key: "status",
          width: 160,
          render: (h, params) => {
            return statusShow(h, params.row);
          }
        },
        {
          title: "关联销售单号",
          key: "refOrderNo",
          width: 180
        },
        {
          title: "客户",
          key: "customerName",
          width: 180
        },
        {
          title: "客户代表",
          key: "customerRepName",
          width: 180
        },
        {
          title: "仓库点",
          key: "warehouseName",
          width: 140
        },
        {
          title: "销售员",
          key: "saleName",
          width: 140
        },
        {
          title: "退货总金额",
          key: "totalAmount",
          width: 150,
          render: (h, params) => {
            return h(
              "strong",
              {
                style: {
                  color: "red"
                }
              },
              params.row.totalAmount
            );
          }
        },
        {
          title: "退货总成本价",
          key: "totalCostAmount",
          width: 150,
          render: (h, params) => {
            return h(
              "strong",
              {
                style: {
                  color: "red"
                }
              },
              params.row.totalCostAmount
            );
          }
        },
        {
          title: "免零金额",
          key: "freeAmount",
          width: 150,
          render: (h, params) => {
            return h(
              "strong",
              {
                style: {
                  color: "red"
                }
              },
              params.row.freeAmount
            );
          }
        },
        {
          title: "退货原因",
          key: "backReason",
          width: 200
        },
        {
          title: "制单人",
          key: "createdBy",
          width: 120
        },
        {
          title: "销售经理",
          key: "backSaleUser",
          width: 120
        },
        {
          title: "销售经理意见",
          key: "backSaleResult",
          width: 200
        },
        {
          title: "质管经理",
          key: "backQualityUser",
          width: 120
        },
        {
          title: "质管经理意见",
          key: "backQualityResult",
          width: 200
        },
        {
          title: "退货收货",
          key: "action",
          width: 100,
          fixed: "right",
          render: (h, params) => {
            let self = this;
            let row = params.row;
            return h(
              "Button",
              {
                props: {
                  type: "primary",
                  size: "small",
                  icon: "edit"
                },
                on: {
                  click: () => {
                    self.receiveForm = {
                      id: row.id,
                      orderNumber: row.orderNumber,
                      shipNo: row.shipNo,
                      shipDate: row.shipDate
                        ? moment(row.shipDate).format("YYYY-MM-DD")
                        : moment().format("YYYY-MM-DD"),
                      receiveDate: row.receiveDate
                        ? moment(row.receiveDate).format("YYYY-MM-DD")
                        : moment().format("YYYY-MM-DD"),
                      shipMethod: row.shipMethod,
                      shipTool: row.shipTool,
                      shipCarNo: row.shipCarNo,
                      shipDriverName: row.shipDriverName,
                      shipCompanyId: row.shipCompanyId,
                      shipStartAddress: row.shipStartAddress,
                      receiveTemper: row.receiveTemper,
                      receiveQuantity: row.receiveQuantity
                        ? row.receiveQuantity
                        : row.totalQuantity
                    };
                    self.backReceiveModal = true;
                  }
                }
              },
              "登记收货"
            );
          }
        }
      ],
      goodsExpandModal: false,
      expandGoodsSpecs: [],
      expandProductDate: "",
      expandExpDate: "",
      detailLoading: false,
      details: [],
      detailColumns: [
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
          width: 140
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
          width: 140
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
      currOrderRow: {},
      backReceiveModal: false,
      receiveForm: {
        id: "",
        orderNumber: "",
        shipNo: "",
        shipDate: moment().format("YYYY-MM-DD"),
        receiveDate: moment().format("YYYY-MM-DD"),
        shipMethod: "",
        shipTool: "",
        shipCarNo: "",
        shipDriverName: "",
        shipCompanyId: "",
        shipStartAddress: "",
        receiveTemper: "",
        receiveQuantity: ""
      },
      receiveLoading: false,
      bmFlow: true,
      qaFlow: true,
      reQCFlow: true,
      fnFlow: true
    };
  },
  mounted() {
    this.systemConfig();
    this.queryBackOrderList();
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

    queryBackOrderList() {
      let reqData = {
        createdStartTime: this.dateRange[0],
        createdEndTime: this.dateRange[1],
        warehouseId: this.warehouseId,
        customerId: this.customerId,
        saleId: this.saleId,
        statusList: [
          "INIT",
          "INIT_SALE_CHECKED",
          "INIT_QUALITY_CHECKED",
          "BACK_RECEIVE",
          "QUALITY_CHECKED"
        ]
      };
      this.loading = true;
      let self = this;
      util.ajax
        .post("/sell/back/list", reqData)
        .then(response => {
          self.loading = false;
          self.orderList = response.data;
          self.currOrderRow = {};
          self.details = [];
          self.$refs.orderTable.clearCurrentRow();
        })
        .catch(error => {
          self.loading = false;
          util.errorProcessor(self, error);
        });
    },
    queryBackOrderDetails(orderId) {
      if (!orderId) {
        return;
      }
      this.detailLoading = true;
      let self = this;
      util.ajax
        .get("/sell/back/" + orderId + "/details")
        .then(response => {
          self.detailLoading = false;
          self.details = response.data;
        })
        .catch(error => {
          self.detailLoading = false;
          util.errorProcessor(self, error);
        });
    },
    handleSelectOrder(row, index) {
      this.currOrderRow = row;
      if (!row.id) {
        this.currOrderRow = {};
        this.details = [];
        this.$refs.orderTable.clearCurrentRow();
      } else {
        this.queryBackOrderDetails(row.id);
      }
    },

    receiveCancel() {
      this.receiveForm = {
        id: "",
        orderNumber: "",
        shipNo: "",
        shipDate: moment().format("YYYY-MM-DD"),
        receiveDate: moment().format("YYYY-MM-DD"),
        shipMethod: "",
        shipTool: "",
        shipCarNo: "",
        shipDriverName: "",
        temperControlId: "",
        shipStartAddress: "",
        receiveTemper: "",
        receiveQuantity: ""
      };
      this.backReceiveModal = false;
    },

    receiveSubmit() {
      let self = this;
      if (!this.receiveForm.id) {
        this.$Message.warning("获取订单标识失败, 请刷新页面后重试");
        return;
      }
      this.$Modal.confirm({
        title: "收货确认",
        content: "是否已经确认收到该笔退货单的货物并登记信息输入完整？",
        onOk: () => {
          self.receiveLoading = true;
          util.ajax
            .put("/sell/back/receive", self.receiveForm)
            .then(response => {
              self.receiveLoading = false;
              self.$Message.success("收货信息保存成功.");
              self.queryBackOrderList();
              self.receiveCancel();
            })
            .catch(error => {
              self.receiveLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    }
  }
};
</script>

<style >
</style>





<template>
    <div>
        <Card >
            <p slot="title">
                <Icon type="android-checkmark-circle"></Icon>
                销售退出终审
            </p>
            <div slot="extra">
                <ButtonGroup>
                    <Button type="primary" icon="search" :loading="loading" @click="queryBackOrderList">查询</Button>
                    <Button type="success" icon="checkmark" :loading="loading" @click="finalCheck">终审复核</Button>
                </ButtonGroup>
            </div>

            <Form ref="searchForm" :label-width="100">
              <Row type="flex" justify="start">
                  <i-col span="6">
                      <FormItem label="制单日期">
                          <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="制单日期" style="width:180px"></DatePicker>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="仓库">
                          <warehouse-select v-model="warehouseId"></warehouse-select>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="客户">
                          <customer-select v-model="customerId"></customer-select>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="销售员">
                          <sale-select v-model="saleId"></sale-select>
                      </FormItem>
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
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";

export default {
  name: "sell-back-final-check",
  components: {
    warehouseSelect,
    customerSelect,
    saleSelect,
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
          title: "是否已验收",
          key: "checkStatus",
          width: 120,
          render: (h, params) => {
            let checkStatus = params.row.checkStatus;
            let label = checkStatus ? "是" : "否";
            let color = checkStatus ? "green" : "red";
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
          }
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
          title: "合格数量",
          key: "rightQuantity",
          width: 120
        },
        {
          title: "不合格数量",
          key: "badQuantity",
          width: 120
        },
        {
          title: "退货单价",
          key: "backPrice",
          width: 120
        },
        {
          title: "金额",
          key: "amount",
          width: 120
        },
        {
          title: "退款成本",
          key: "costAmount",
          width: 120
        },
        {
          title: "有效期至",
          key: "expDate",
          width: 150,
          render: (h, params) => {
            let label = params.row.expDate
              ? moment(params.row.expDate).format("YYYY-MM-DD")
              : "";
            return h("span", label);
          }
        },
        {
          title: "生产日期",
          key: "productDate",
          width: 150,
          render: (h, params) => {
            let label = params.row.productDate
              ? moment(params.row.productDate).format("YYYY-MM-DD")
              : "";
            return h("span", label);
          }
        },
        {
          title: "库位",
          key: "location",
          width: 120
        },
        {
          title: "验收温控方式",
          key: "checkTempMethodName",
          width: 100
        },
        {
          title: "验收员",
          key: "checkUser",
          width: 120
        },
        {
          title: "验收时间",
          key: "checkTime",
          width: 150,
          render: (h, params) => {
            let label = params.row.checkTime
              ? moment(params.row.checkTime).format("YYYY-MM-DD HH:mm")
              : "";
            return h("span", label);
          }
        },
        {
          title: "验收结果",
          key: "checkResult",
          width: 200
        }
      ],
      currOrderRow: {}
    };
  },
  mounted() {
    this.queryBackOrderList();
  },
  methods: {
    queryBackOrderList() {
      let reqData = {
        createdStartTime: this.dateRange[0],
        createdEndTime: this.dateRange[1],
        warehouseId: this.warehouseId,
        customerId: this.customerId,
        saleId: this.saleId,
        statusList: ["BACK_RECEIVE", "QUALITY_CHECKED"]
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

    finalCheck() {
      if (!this.currOrderRow.id) {
        this.$Message.info("请先选择需要审核的退货单信息.");
        return;
      }
      let reqData = {
        backOrderId: this.currOrderRow.id,
        status: "FINAL_CHECKED"
      };
      let self = this;
      this.$Modal.confirm({
        title: "终审确认",
        content: "是否确认退货单数据完全正确，提交后不可修改？",
        onOk: () => {
          self.loading = true;
          util.ajax
            .post("/sell/back/check", reqData)
            .then(response => {
              self.loading = false;
              self.$Message.success("审核成功.");
              self.queryBackOrderList();
            })
            .catch(error => {
              self.loading = false;
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


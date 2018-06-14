
<template>
    <div>
        <Card >
            <p slot="title">
                <Icon type="funnel"></Icon>
                销售退出申请审核
            </p>
            <div slot="extra">
                <ButtonGroup >
                    <Button type="primary" icon="search" :loading="loading" @click="queryBackOrderList">查询</Button>
                    <Button type="success" icon="checkmark" :loading="loading" @click="saleManagerCheck">销售审核</Button>
                    <Button type="warning" icon="checkmark-circled" :loading="loading" @click="qualityManagerCheck">质量审核</Button>
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

        <Modal v-model="checkResultModal" width="35" :mask-closable="false" :title="modalTitle" >
            <Form ref="checkForm" :model="checkForm" :label-width="90">
                <Row>
                    <FormItem label="审核意见" error="checkResultError">
                        <Input type="text" v-model="checkForm.checkResult" />
                    </FormItem>
                </Row>
            </Form>
            <Row slot="footer">
                <ButtonGroup>
                    <Button type="success" icon="checkmark" :loading="checkLoading" @click="checkSubmit">提交</Button>
                    <Button type="ghost" icon="reply" :loading="checkLoading" @click="checkCancel">取消</Button>
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
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";

export default {
  name: "sell-back-apply-check",
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
          label = "质管已审";
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
      checkResultModal: false,
      modalTitle: "",
      checkForm: {
        backOrderId: "",
        status: "",
        checkResult: ""
      },
      checkResultError: "",
      checkLoading: false
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
    saleManagerCheck() {
      if (!this.currOrderRow.id) {
        this.$Message.info("请先选择对应需要审核的退货单!");
        return;
      }
      this.checkResultError = "";
      this.modalTitle = "销售经理审核";
      this.checkForm = {
        backOrderId: this.currOrderRow.id,
        status: "INIT_SALE_CHECKED",
        checkResult: ""
      };
      this.checkResultModal = true;
    },
    qualityManagerCheck() {
      if (!this.currOrderRow.id) {
        this.$Message.info("请先选择对应需要审核的退货单!");
        return;
      }
      this.checkResultError = "";
      this.modalTitle = "质量经理审核";
      this.checkForm = {
        backOrderId: this.currOrderRow.id,
        status: "INIT_QUALITY_CHECKED",
        checkResult: ""
      };
      this.checkResultModal = true;
    },
    checkCancel() {
      this.modalTitle = "";
      this.checkForm = {
        backOrderId: "",
        status: "",
        checkResult: ""
      };
      this.checkResultError = "";
      this.checkResultModal = false;
    },
    checkSubmit() {
      if (!this.checkForm.backOrderId || !this.checkForm.status) {
        this.$Message.warning("获取退货单标识失败，请刷新页面后重试!");
        return;
      }
      if (!this.checkForm.checkResult) {
        this.checkResultError = "请输入审核意见";
        return;
      }
      let self = this;
      self.checkLoading = true;
      util.ajax
        .post("/sell/back/check", self.checkForm)
        .then(response => {
          self.checkLoading = false;
          self.$Message.success("审核意见保存成功");
          self.queryBackOrderList();
          self.checkCancel();
        })
        .catch(error => {
          self.checkLoading = false;
          util.errorProcessor(self, error);
        });
    }
  }
};
</script>

<style >
</style>

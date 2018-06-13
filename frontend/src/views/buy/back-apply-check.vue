
<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="funnel"></Icon>
              采购退出申请审核
          </p>
          <div slot="extra">
              <ButtonGroup >
                  <Button type="primary" icon="search" :loading="loading" @click="refreshOrderTable">查询</Button>
                  <Button type="success" icon="checkmark" :loading="loading" @click="buyCheckHandle" >采购审核</Button>
                  <Button type="warning" icon="checkmark-circled" :loading="loading" @click="qualityCheckHandle">质量审核</Button>
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
                  <FormItem label="审核意见">
                      <Input type="text" v-model="checkForm.checkResult" />
                  </FormItem>
              </Row>
          </Form>
          <Row slot="footer">
              <ButtonGroup>
                  <Button type="success" icon="checkmark" :loading="checkLoading" @click="checkSubmit">提交</Button>
                  <Button type="ghost" icon="reply" @click="checkCancel">取消</Button>
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
import goodsExpand from "@/views/goods/goods-expand.vue";
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";

export default {
  name: "back-apply-check",
  components: {
    warehouseSelect,
    goodsExpand,
    goodsSpecTags
  },
  data() {
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
            return h(
              "span",
              params.row.createdTime
                ? moment(params.row.createdTime).format("YYYY-MM-DD HH:mm")
                : ""
            );
          }
        },
        {
          title: "退货时间",
          key: "backTime",
          width: 140,
          render: (h, params) => {
            return h(
              "span",
              params.row.backTime
                ? moment(params.row.backTime).format("YYYY-MM-DD HH:mm")
                : ""
            );
          }
        },
        {
          title: "商品去向(供应商)",
          key: "supplierName",
          width: 200
        },
        {
          title: "供应商代表",
          key: "supplierContactName",
          width: 180
        },
        {
          title: "出库仓库",
          key: "warehouseName",
          width: 140
        },
        {
          title: "当前状态",
          key: "status",
          width: 170,
          render: (h, params) => {
            let status = params.row.status;
            let label = "";
            let color = "";
            switch (status) {
              case "BACK_INIT":
                label = "初始制单";
                color = "#5cadff";
                break;
              case "BACK_BUY_CHECK":
                label = "采购经理已审";
                color = "#2d8cf0";
                break;
              case "BACK_QUALITY_CHECK":
                label = "质管经理已审";
                color = "#ff9900";
                break;
              case "BACK_QUALITY_RECHECK":
                label = "已质量复审";
                color = "#19be6b";
                break;
              case "BACK_FINAL_CHECK":
                label = "已终审完成";
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
          }
        },
        {
          title: "总数量",
          key: "totalQuantity",
          width: 120,
          render: (h, params) => {
            //显示为一个负数
            let label = params.row.totalQuantity
              ? "-" + params.row.totalQuantity
              : "";
            return h(
              "strong",
              {
                style: {
                  color: "red"
                }
              },
              label
            );
          }
        },
        {
          title: "总金额",
          key: "totalAmount",
          width: 120,
          render: (h, params) => {
            let label = params.row.totalAmount
              ? "-" + params.row.totalAmount
              : "";
            return h(
              "strong",
              {
                style: {
                  color: "red"
                }
              },
              label
            );
          }
        },
        {
          title: "采购员",
          key: "buyerName",
          width: 120
        },
        {
          title: "退货原因",
          key: "keyWord",
          width: 200
        },
        {
          title: "采购经理",
          key: "backBuyUser",
          width: 120
        },
        {
          title: "采购经理意见",
          key: "backBuyResult",
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
          title: "退货数量",
          key: "backQuantity",
          width: 120
        },
        {
          title: "单价",
          key: "buyPrice",
          width: 120
        },
        {
          title: "金额",
          key: "amount",
          width: 120
        },
        {
          title: "有效期至",
          key: "expDate",
          width: 150,
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
          title: "生产日期",
          key: "productDate",
          width: 150,
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
          title: "库位",
          key: "location",
          width: 120
        }
      ],
      currOrderRow: {},
      checkResultModal: false,
      modalTitle: "",
      checkForm: {
        backId: "",
        type: "",
        checkResult: ""
      },
      checkLoading: false
    };
  },
  mounted() {
    this.refreshOrderTable();
  },
  methods: {
    refreshOrderTable() {
      let reqData = {
        createdStartTime: this.dateRange[0],
        createdEndTime: this.dateRange[1],
        warehouseId: this.warehouseId,
        searchStatus: ["BACK_INIT", "BACK_BUY_CHECK", "BACK_QUALITY_CHECK"]
      };
      this.loading = true;
      util.ajax
        .post("/buy/back/list", reqData)
        .then(response => {
          this.loading = false;
          this.orderList = response.data;
          this.currOrderRow = {};
          this.details = [];
          this.$refs.orderTable.clearCurrentRow();
        })
        .catch(error => {
          this.loading = false;
          util.errorProcessor(this, error);
        });
    },

    handleSelectOrder(data) {
      this.currOrderRow = data;
      if (!data.id) {
        this.details = [];
        return;
      }
      this.detailLoading = true;
      util.ajax
        .get("/buy/back/" + data.id + "/detail")
        .then(response => {
          this.detailLoading = false;
          this.details = response.data;
        })
        .catch(error => {
          this.detailLoading = false;
          util.errorProcessor(this, error);
        });
    },
    buyCheckHandle() {
      if (!this.currOrderRow.id) {
        this.$Message.info("请先选择需要审核的退出单");
        return;
      }
      this.checkForm = {
        backId: this.currOrderRow.id,
        type: "BACK_BUY_CHECK",
        checkResult: ""
      };
      this.modalTitle = "采购经理审核意见";
      this.checkResultModal = true;
    },
    qualityCheckHandle() {
      if (!this.currOrderRow.id) {
        this.$Message.info("请先选择需要审核的退出单");
        return;
      }
      this.checkForm = {
        backId: this.currOrderRow.id,
        type: "BACK_QUALITY_CHECK",
        checkResult: ""
      };
      this.modalTitle = "采购经理审核意见";
      this.checkResultModal = true;
    },
    checkSubmit() {
      if (!this.checkForm.backId || !this.checkForm.type) {
        this.$Message.warning("获取退款单信息失败，请刷新后重新尝试");
        return;
      }
      if (!this.checkForm.checkResult) {
        this.$Message.warning("请输入审核意见");
        return;
      }
      let self = this;
      this.checkLoading = true;
      util.ajax
        .post("/buy/back/check", this.checkForm)
        .then(response => {
          this.checkLoading = false;
          this.$Message.success("审核保存成功");
          self.checkCancel();
          self.refreshOrderTable();
        })
        .catch(error => {
          this.checkLoading = false;
          util.errorProcessor(self, error);
        });
    },
    checkCancel() {
      this.checkResultModal = false;
      this.modalTitle = "";
      this.checkForm = {
        backId: "",
        type: "",
        checkResult: ""
      };
    }
  }
};
</script>

<style >
</style>


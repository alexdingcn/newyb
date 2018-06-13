<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div class="order-view-detail">
        <Row class="title-show">
            <span>销售单号: <strong>{{orderDetail.orderNumber}}</strong></span>
            <hr size="1" />
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">订单编号:</span>
                <span class="label-value">{{orderDetail.orderNumber}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">制单日期:</span>
                <span class="label-value">{{orderDetail.createOrderDate | dateFormat}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">客户:</span>
                <span class="label-value">{{orderDetail.customerName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">客户代表:</span>
                <span class="label-value">{{orderDetail.customerRepName}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">总计数量:</span>
                <span class="label-value">{{orderDetail.totalQuantity}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">总计金额:</span>
                <span class="label-value" style="color: #ed3f14;">¥{{orderDetail.totalAmount}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">免零金额:</span>
                <span class="label-value" style="color: #ed3f14;">¥{{orderDetail.freeAmount}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">整单折扣率(%):</span>
                <span class="label-value" style="color: #ed3f14;">{{orderDetail.disRate}}%</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">销售员:</span>
                <span class="label-value">{{orderDetail.saleNickName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">收货电话:</span>
                <span class="label-value">{{orderDetail.customerRepContactPhone}}</span>
            </i-col>
            <i-col span="12">
                <span class="label-key">收货地址:</span>
                <span class="label-value">{{orderDetail.customerRepRepertoryAddress}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">制单人:</span>
                <span class="label-value">{{orderDetail.createBy}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">湿控方式:</span>
                <span class="label-value">{{orderDetail.temperControlName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">运输方式:</span>
                <span class="label-value">{{orderDetail.shipMethodName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">运输工具:</span>
                <span class="label-value">{{orderDetail.shipToolName}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
          <i-col span="24">
                <span class="label-key">备注信息:</span>
                <span class="label-value">{{orderDetail.comment}}</span>
            </i-col>
        </Row>

        <Row class="title-show margin-top-15">
            <span>订单详情信息</span>
            <hr size="1" />
        </Row>
        <Table ref="detailTable" size="small" border highlight-row style="width: 100%;" 
            :columns="tabColumns" :data="detailsData">
        </Table>

        <Modal v-model="goodsExpandModal" width="60" :mask-closable="false" title="商品详情信息" :footerHide="true" class="file-upload-modal">
            <goods-expand ref="goodsExpand" :goodsSpecs="expandGoodsSpecs" :productDate="expandProductDate" :expDate="expandExpDate"></goods-expand>
        </Modal>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";

export default {
  name: "review-detail",
  props: {
    sellOrderId: Number
  },
  components: {
    goodsSpecTags,
    goodsExpand
  },
  data() {
    return {
      goodsExpandModal: false,
      expandGoodsSpecs: [],
      expandProductDate: "",
      expandExpDate: "",
      orderDetail: "",
      detailsData: [],
      tabColumns: [
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
          width: 120,
          render: (h, params) => {
            return h("span", params.row.goods.origin);
          }
        },
        {
          title: "批次号",
          key: "batchCode",
          width: 160
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
          title: "单位",
          key: "unitName",
          width: 120,
          render: (h, params) => {
            return h("span", params.row.goods.unitName);
          }
        },
        {
          title: "生产日期",
          width: 120,
          key: "productData",
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
          title: "库存量",
          key: "repertoryQuantity",
          width: 120
        },
        {
          title: "销售数量",
          width: 120,
          key: "quantity"
        },
        {
          title: "价格",
          width: 100,
          key: "realPrice"
        },
        {
          title: "折扣",
          key: "disPrice",
          width: 100
        },
        {
          title: "赠送",
          key: "free",
          width: 100
        },
        {
          title: "金额",
          width: 120,
          key: "amount"
        },
        {
          title: "税率",
          key: "taxRate",
          width: 100
        },
        {
          title: "质量审核状态",
          width: 120,
          key: "checkStatus",
          render: (h, params) => {
            const checkStatus = params.row.checkStatus;
            const color = !checkStatus
              ? "blue"
              : checkStatus === "OK" ? "green" : "red";
            const text = !checkStatus
              ? "待审"
              : checkStatus === "OK" ? "通过" : "拒绝";
            return h(
              "Tag",
              {
                props: {
                  type: "dot",
                  color: color
                }
              },
              text
            );
          }
        },
        {
          title: "质量审核人",
          width: 100,
          key: "checkUser",
          render: (h, params) => {
            const checkUser = params.row.checkUser ? params.row.checkUser : "";
            return h("span", checkUser);
          }
        },
        {
          title: "质量审核日期",
          width: 120,
          key: "checkDate",
          render: (h, params) => {
            let checkDate = params.row.checkDate;
            return h(
              "span",
              checkDate ? moment(checkDate).format("YYYY-MM-DD HH:mm") : ""
            );
          }
        },
        {
          title: "质量审核结论",
          width: 180,
          key: "checkResult",
          render: (h, params) => {
            const checkResult = params.row.checkResult
              ? params.row.checkResult
              : "";
            return h("span", checkResult);
          }
        }
      ]
    };
  },
  watch: {
    sellOrderId(id) {
      console.log("sell review detail id:" + id);
      this.initData(id);
    }
  },
  mounted() {
    this.initData(this.sellOrderId);
  },
  filters: {
    dateFormat(data) {
      if (!data && isNaN(data)) {
        return "";
      }
      return moment(data).format("YYYY-MM-DD");
    }
  },
  methods: {
    initData(id) {
      if (id && id > 0) {
        util.ajax
          .get("/sell/order/review/detail", { params: { orderId: id } })
          .then(response => {
            this.orderDetail = response.data;
            if (this.orderDetail && this.orderDetail.details) {
              this.detailsData = this.orderDetail.details;
            }
          })
          .catch(error => {
            util.errorProcessor(this, error);
          });
      } else {
        this.orderDetail = {};
        this.detailsData = [];
      }
    }
  }
};
</script>

<style >
.title-show > span {
  font-size: 1.25em;
  font-weight: bold;
  color: #495060;
}

.label-key {
  width: 110px;
  text-align: right;
}
.label-value {
  font-weight: bold;
}
</style>


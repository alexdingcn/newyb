<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div class="order-view-detail">
        <Row class="title-show">
            <span>采购入库单号: <strong>{{order.orderNumber}}</strong></span>
            <hr size="1" />
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">系统单号:</span>
                <span class="label-value">{{order.orderNumber}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">收货日期:</span>
                <span class="label-value">{{order.receiveDate | dateFormat}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">入库方式:</span>
                <span class="label-value">{{order.refTypeName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">入库仓库:</span>
                <span class="label-value">{{order.warehouseName}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">供应商:</span>
                <span class="label-value">{{order.supplierName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">供应商代表:</span>
                <span class="label-value">{{order.supplierContactName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">采购员:</span>
                <span class="label-value">{{order.saleNickName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">收货员:</span>
                <span class="label-value">{{order.receiveUser||order.createBy}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">总计数量:</span>
                <span class="label-value">{{order.totalQuantity}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">总计金额:</span>
                <span class="label-value" style="color: #ed3f14;">¥{{order.totalAmount}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">运输公司:</span>
                <span class="label-value">{{order.shipCompanyName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">运输单号:</span>
                <span class="label-value">{{order.shipEntrustNo}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">温控方式:</span>
                <span class="label-value">{{order.tempControlMethodName}}</span>
            </i-col>
            <i-col span="12">
                <span class="label-key">备注信息:</span>
                <span class="label-value">{{order.keyWord}}</span>
            </i-col>
        </Row>

        <Table border highlight-row size="small" style="width: 100%;" 
                :columns="detailColumns" :data="details">
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
  name: "inViewDetail",
  props: {
    inOrderId: Number | String
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
      order: {},
      details: [],
      detailColumns: [
        {
          type: "index",
          width: 50
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
          title: "产地",
          key: "origin",
          width: 140,
          render: (h, params) => {
            return h("span", params.row.goods.origin);
          }
        },
        {
          title: "规格",
          key: "goodsSpecs",
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
          title: "生产企业",
          key: "factoryName",
          align: "center",
          width: 120,
          render: (h, params) => {
            return h("span", params.row.goods.factoryName);
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
          title: "批号",
          key: "batchCode",
          width: 140
        },
        {
          title: "生产日期",
          key: "productDate",
          width: 140,
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
          width: 140,
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
          title: "库区",
          key: "warehouseLocation",
          width: 150
        },
        {
          title: "采购数量",
          key: "buyOrderQuality",
          width: 130
        },
        {
          title: "拒收",
          width: 150,
          key: "rejectQuality",
          render: (h, params) => {
            let rejectQuality = params.row.rejectQuality
              ? params.row.rejectQuality
              : "0";
            let rejectComment = params.row.rejectComment
              ? params.row.rejectComment
              : "";
            return h("div", [
              h("h4", "数量:" + rejectQuality),
              h("span", "原因:" + rejectComment)
            ]);
          }
        },
        {
          title: "收货数量",
          width: 130,
          key: "receiveQuality"
        },
        {
          title: "赠送数量",
          width: 120,
          key: "free"
        },
        {
          title: "单价",
          width: 120,
          key: "price"
        },
        {
          title: "金额",
          width: 140,
          key: "amount"
        },
        {
          title: "入库数量",
          key: "inCount",
          width: 140
        },
        {
          title: "合格数量",
          key: "rightCount",
          width: 140
        },
        {
          title: "不合格数量",
          key: "errorCount",
          width: 120
        },
        {
          title: "抽样数量",
          key: "surveyQuality",
          width: 140,
          render: (h, params) => {
            let surveyQuality = params.row.surveyQuality
              ? params.row.surveyQuality
              : 0;
            let surveyDate = params.row.surveyDate
              ? moment(params.row.surveyDate).format("YYYY-MM-DD HH:mm")
              : "";
            let surveyUser = params.row.surveyUser ? params.row.surveyUser : "";
            return h("div", [
              h("h4", "数量:" + surveyQuality),
              h("span", surveyUser + "  " + surveyDate)
            ]);
          }
        },
        {
          title: "验收温控方式",
          key: "checkTempMethodName",
          width: 140
        },
        {
          title: "验收意见",
          key: "checkResult",
          width: 150
        },
        {
          title: "验收员",
          key: "checkUser",
          width: 140
        },
        {
          title: "验收时间",
          key: "checkTime",
          width: 140,
          render: (h, params) => {
            let checkTime = params.row.checkTime;
            return h(
              "span",
              checkTime ? moment(checkTime).format("YYYY-MM-DD HH:mm") : ""
            );
          }
        }
      ]
    };
  },
  filters: {
    dateFormat(data) {
      if (!data && isNaN(data)) {
        return "";
      }
      return moment(data).format("YYYY-MM-DD");
    }
  },
  watch: {
    inOrderId(value) {
      this.loadBackOrder(value);
    }
  },
  mounted() {
    this.loadBackOrder(this.inOrderId);
  },
  methods: {
    loadBackOrder(inOrderId) {
      if (inOrderId && inOrderId > 0) {
        util.ajax
          .get("/repertory/in/order/view/" + inOrderId)
          .then(response => {
            let data = response.data;
            if (data) {
              this.order = data;
              this.details = data.details;
            }
          })
          .catch(error => {
            util.errorProcessor(this, error);
          });
      }
    }
  }
};
</script>

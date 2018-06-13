<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div class="order-view-detail">
        <Row class="title-show">
            <span>采购退回单号: <strong>{{order.orderNumber}}</strong></span>
            <hr size="1" />
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">系统单号:</span>
                <span class="label-value">{{order.orderNumber}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">制单时间:</span>
                <span class="label-value">{{order.createdTime | dateFormat}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">退货时间:</span>
                <span class="label-value">{{order.backTime | dateFormat}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">仓库点:</span>
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
                <span class="label-key">入库单号:</span>
                <span class="label-value">{{order.inOrderNumber}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">入库收货时间:</span>
                <span class="label-value">{{order.inReceiveTime | dateFormat}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">采购员:</span>
                <span class="label-value">{{order.buyerName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">总计数量:</span>
                <span class="label-value">{{order.totalQuantity}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">总计金额:</span>
                <span class="label-value" style="color: #ed3f14;">¥{{order.totalAmount}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="12">
                <span class="label-key">退回原因:</span>
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
  name: "buyBackViewDetail",
  props: {
    buyBackId: Number | String
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
        },
        {
          title: "复核员",
          key: "checkUser",
          width: 120
        },
        {
          title: "复核意见",
          key: "checkResult",
          width: 180
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
    buyBackId(value) {
      this.loadBackOrder(value);
    }
  },
  mounted() {
    this.loadBackOrder(this.buyBackId);
  },
  methods: {
    loadBackOrder(buyBackId) {
      if (buyBackId && buyBackId > 0) {
        util.ajax
          .get("/buy/back/view/" + buyBackId)
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

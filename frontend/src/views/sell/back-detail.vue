<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div class="order-view-detail">
        <Row class="title-show">
            <span>销售退货单号: <strong>{{order.orderNumber}}</strong></span>
            <hr size="1" />
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">系统单号:</span>
                <span class="label-value">{{order.orderNumber}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">制单日期:</span>
                <span class="label-value">{{order.createdTime | dateFormat}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">客户:</span>
                <span class="label-value">{{order.customerName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">客户代表:</span>
                <span class="label-value">{{order.customerRepName}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">关联销售单号:</span>
                <span class="label-value">{{order.refOrderNo}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">仓库点:</span>
                <span class="label-value">{{order.warehouseName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">销售员:</span>
                <span class="label-value">{{order.saleName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">制单员:</span>
                <span class="label-value">{{order.createdBy}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">退货总数量:</span>
                <span class="label-value">{{order.totalQuantity}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">退货总金额:</span>
                <span class="label-value" style="color: #ed3f14;">¥{{order.totalAmount}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">退货总成本价:</span>
                <span class="label-value" style="color: #ed3f14;">¥{{order.totalCostAmount}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">免零金额:</span>
                <span class="label-value" style="color: #ed3f14;">¥{{order.freeAmount}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">收货日期:</span>
                <span class="label-value">{{order.receiveDate | dateFormat}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">运输公司:</span>
                <span class="label-value" >{{order.shipCompanyName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">运输单号:</span>
                <span class="label-value" >{{order.shipNo}}</span>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <i-col span="24">
                <span class="label-key">退货原因:</span>
                <span class="label-value">{{order.backReason}}</span>
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
  name: "sellBackDetail",
  props: {
    backId: Number | String
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
    backId(value) {
      this.loadBackOrder(value);
    }
  },
  mounted() {
    this.loadBackOrder(this.backId);
  },
  methods: {
    loadBackOrder(backId) {
      if (backId && backId > 0) {
        util.ajax
          .get("/sell/back/view/" + backId)
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

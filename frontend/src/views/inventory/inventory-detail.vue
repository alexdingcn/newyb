<style lang="less">
@import "../../styles/common.less";

.label-key {
  width: 110px;
  text-align: right;
}
.label-value {
  font-weight: bold;
  font-size: 1.05em;
}
.title-splic-line {
  margin-top: 5px;
  margin-bottom: 10px;
  width: 70%;
}
.order-info {
  background-color: #eaf4fe;
  margin-bottom: 1.5em;
  padding: 0.7em 1.4em;
}
.table-footer-row {
  font-size: 1.1em;
  font-weight: 600;
}
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <FontIcon type="icon-qianshoushenpitongguo"></FontIcon>
                盘点单详情 
            </p>
            <div slot="extra">
                <ButtonGroup>
                    <Button v-if="inventory.status === 'UNCHECK'" type="success" icon="checkmark" :loading="checkLoading" @click="checkHandle" >审核通过</Button>
                    <Button type="ghost" icon="ios-cloud-download-outline" :loading="downLoadLoading">导出</Button>
                </ButtonGroup>
            </div>

            <div>
                <h3>
                    盘点单信息
                </h3>
                <hr size="1" class="title-splic-line"/>
                <div class="order-info">
                    <Row class="row-margin-bottom">
                        <i-col span="6">
                            <span class="label-key">盘点单号:</span>
                            <span class="label-value">{{inventory.orderNumber}}</span>
                        </i-col>
                        <i-col span="6">
                            <span class="label-key">盘点单名称:</span>
                            <span class="label-value">{{inventory.orderName}}</span>
                        </i-col>
                        <i-col span="6">
                            <span class="label-key">仓库点:</span>
                            <span class="label-value">{{inventory.warehouseName}}</span>
                        </i-col>
                    </Row>
                    <Row class="row-margin-bottom">
                        <i-col span="6">
                            <span class="label-key">状态:</span>
                            <span class="label-value">{{inventory.status | statusFilter}}</span>
                        </i-col>
                        <i-col span="6">
                            <span class="label-key">制单人:</span>
                            <span class="label-value">{{inventory.createdBy}}</span>
                        </i-col>
                        <i-col span="6">
                            <span class="label-key">制单时间:</span>
                            <span class="label-value">{{inventory.createdTime | timeFilter}}</span>
                        </i-col>
                    </Row>
                    <Row class="row-margin-bottom">
                        <i-col span="6">
                            <span class="label-key">审批人:</span>
                            <span class="label-value">{{inventory.checkUserName}}</span>
                        </i-col>
                        <i-col span="6">
                            <span class="label-key">审批时间:</span>
                            <span class="label-value">{{inventory.checkTime | timeFilter}}</span>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="24">
                            <span class="label-key">备注信息:</span>
                            <span class="label-value">{{inventory.comment}}</span>
                        </i-col>
                    </Row>
                </div>

                <h3>
                    盘点商品明细
                </h3>
                <hr size="1" class="title-splic-line"/>
                <Table ref="inventorysTable" highlight-row height="520" 
                    :columns="detailsColumns" :data="details">
                    <div slot="footer">
                        <Row class="table-footer-row">
                            <span style="margin-left: 30px; margin-right: 50px;">总计条数: <span style="color: #ed3f14;">{{totalCount}}</span></span>
                            <span style="margin-right: 50px;">盘盈条数: <span style="color: #ed3f14;">{{profitCount}}</span></span>
                            <span style="margin-right: 50px;">盘亏条数: <span style="color: #ed3f14;">{{lossCount}}</span></span>
                            <span style="margin-right: 50px;">总计库存数量: <span style="color: #ed3f14;">{{totalQuantity}}</span></span>
                            <span style="margin-right: 50px;">总计盘点数量: <span style="color: #ed3f14;">{{totalInventoryQuantity}}</span></span>
                            <span style="margin-right: 50px;">总计盘盈数量: <span style="color: #ed3f14;">{{profitQuantity}}</span></span>
                            <span style="margin-right: 50px;">总计盘亏数量: <span style="color: #ed3f14;">{{lossQuantity}}</span></span>
                        </Row>
                    </div>
                </Table>
            </div>
             <Spin size="large" fix v-if="spinShow"></Spin>
        </Card>

        <Modal v-model="goodsExpandModal" width="60" :mask-closable="false" title="商品详情信息" :footerHide="true">
            <goods-expand ref="goodsExpand" :goodsSpecs="expandGoodsSpecs" :productDate="expandProductDate" :expDate="expandExpDate"></goods-expand>
        </Modal>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import goodsExpand from "@/views/goods/goods-expand.vue";
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";

export default {
  name: "inventoryDetail",
  components: {
    goodsExpand,
    goodsSpecTags
  },
  data() {
    return {
      spinShow: false,
      inventory: {},
      details: [],
      detailsColumns: [
        {
          type: "index",
          width: 80
        },
        {
          title: "商品名称",
          key: "goodsName",
          width: 200,
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
          title: "供应商",
          key: "supplierName",
          width: 200
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
          title: "产地",
          key: "origin",
          width: 120,
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
          title: "批次号",
          key: "batchCode",
          width: 160
        },
        {
          title: "库位",
          key: "location",
          width: 130
        },
        {
          title: "库存量",
          key: "repertoryQuantity",
          width: 120
        },
        {
          title: "盘点数量",
          key: "inventoryQuantity",
          width: 120
        },
        {
          title: "盘盈/盘亏",
          key: "changeQuantity",
          width: 120,
          render: (h, params) => {
            let changeQuantity = params.row.changeQuantity
              ? parseFloat(params.row.changeQuantity)
              : 0;
            let color = "#495060";
            if (changeQuantity > 0) {
              color = "#19be6b";
            } else if (changeQuantity < 0) {
              color = "#ed3f14";
            }
            return h("strong", { style: { color: color } }, changeQuantity);
          }
        },
        {
          title: "入库单价",
          key: "buyPrice",
          width: 120,
          render: (h, params) => {
            let buyPrice = params.row.buyPrice ? params.row.buyPrice : "";
            return h("span", "¥" + buyPrice);
          }
        },
        {
          title: "金额",
          key: "amount",
          width: 120,
          render: (h, params) => {
            let price = params.row.buyPrice
              ? parseFloat(params.row.buyPrice)
              : 0;
            let repertoryQuantity = params.row.repertoryQuantity
              ? parseFloat(params.row.repertoryQuantity)
              : 0;
            let result = (price * repertoryQuantity).toFixed(2);
            return h("span", "¥" + result);
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
        }
      ],
      goodsExpandModal: false,
      expandGoodsSpecs: [],
      expandProductDate: "",
      expandExpDate: "",
      totalCount: 0,
      profitCount: 0,
      lossCount: 0,
      totalQuantity: 0,
      totalInventoryQuantity: 0,
      profitQuantity: 0,
      lossQuantity: 0,
      checkLoading: false,
      downLoadLoading: false
    };
  },
  filters: {
    statusFilter(data) {
      if (data === "UNCHECK") {
        return "待审核";
      } else if (data === "CANCEL") {
        return "已取消";
      } else if (data === "CHECKED") {
        return "已审核";
      } else {
        return data;
      }
    },
    timeFilter(data) {
      return data ? moment(data).format("YYYY-MM-DD HH:mm") : "";
    }
  },
  watch: {
    details() {
      this.totalCount = this.details.length;
      this.totalQuantity = 0;
      this.profitCount = 0;
      this.lossCount = 0;
      this.totalInventoryQuantity = 0;
      if (!this.details || this.details.length <= 0) {
        this.totalCount = 0;
        this.totalQuantity = 0;
        this.profitCount = 0;
        this.lossCount = 0;
        this.totalInventoryQuantity = 0;
        return;
      }
      for (let i = 0; i < this.details.length; i++) {
        let item = this.details[i];
        let itemQuantity =
          item && item.repertoryQuantity
            ? parseFloat(item.repertoryQuantity)
            : 0;
        let inventoryQuantity = item.inventoryQuantity
          ? parseFloat(item.inventoryQuantity)
          : 0;
        this.totalQuantity = this.totalQuantity + itemQuantity;
        this.totalInventoryQuantity =
          this.totalInventoryQuantity + inventoryQuantity;
        let changeQuantity = item.changeQuantity
          ? parseFloat(item.changeQuantity)
          : 0;
        if (changeQuantity > 0) {
          this.profitCount++;
          this.profitQuantity = this.profitQuantity + changeQuantity;
        } else if (changeQuantity < 0) {
          this.lossCount++;
          this.lossQuantity = this.lossQuantity + changeQuantity;
        }
      }
    }
  },
  activated() {
    this.init();
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      let inventoryId = this.$route.params.inventoryId;
      console.log(inventoryId);
      if (!inventoryId || inventoryId <= 0) {
        this.$Modal.warning({
          title: "错误提示",
          content: "获取盘点单编号失败! 请关闭当前页签后重新打开"
        });
        return;
      }
      //获取盘点单详情
      this.inventory = {};
      this.details = [];
      this.spinShow = true;
      util.ajax
        .get("/inventory/view/" + inventoryId)
        .then(response => {
          this.spinShow = false;
          console.log(response.data);
          this.inventory = response.data;
          this.details = this.inventory.details;
        })
        .catch(error => {
          this.spinShow = false;
          util.errorProcessor(this, error);
        });
    },

    checkHandle() {
      if (!this.inventory || !this.inventory.id) {
        this.$Modal.warning({
          title: "错误提示",
          content: "获取盘点单编号失败! 请关闭当前页签后重新打开"
        });
        return;
      }
      if (this.inventory.status !== "UNCHECK") {
        this.$Modal.info({
          title: "审核提示",
          content: "当前状态不能做审核操作."
        });
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "审核确认",
        content: "是否确认信息正确，提交后不能修改，并且生成对应的盘盈盘亏记录",
        onOk: () => {
          self.checkLoading = true;
          util.ajax
            .post("/inventory/check/" + self.inventory.id)
            .then(response => {
              self.checkLoading = false;
              self.$Message.success("审核成功");
              self.init();
            })
            .catch(error => {
              self.checkLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    }
  }
};
</script>


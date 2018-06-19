<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="ios-flask"></Icon>
                销售出库质量审核
            </p>
            <Row slot="extra" style="min-width: 750px;">
                <i-col span="12">
                  <Row type="flex" justify="start">
                    <Steps :current="0">
                        <Step title="销售出库质量审核" ></Step>
                        <Step title="销售审核" ></Step>
                      </Steps>
                  </Row>
                </i-col>
                <i-col span="12">
                  <Row type="flex" justify="end">
                    <ButtonGroup >
                        <Button type="primary" icon="ios-search" :loading="sellOrderLoading" @click="querySellOrderList">查询</Button>
                      <Button type="success" icon="ios-checkmark" :loading="sellOrderLoading" @click="reviewOkBtnClick"> 审核 </Button>
                      <Button type="ghost" icon="reply" :loading="sellOrderLoading" @click="reviewCancelBtnClick"> 取消审核 </Button>
                    </ButtonGroup>
                  </Row>
                </i-col>
            </Row>

            <Form ref="searchForm" :model="query" :label-width="100">
                <Row class="row-margin-bottom">
                    <i-col span="6">
                        <FormItem label="制单日期">
                            <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="制单日期" style="width:180px"></DatePicker>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="客户">
                            <customer-select v-model="query.customerId" @on-change="querySellOrderList"></customer-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="销售员">
                            <sale-select v-model="query.saleId" @on-change="querySellOrderList"></sale-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6" >
                        <FormItem label="状态">
                            <Select v-model="query.status" placeholder="状态" @on-change="querySellOrderList">
                                <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                            </Select>
                        </FormItem>
                    </i-col>
                </Row>
            </Form>

            <Table border highlight-row disabled-hover height="250" style="width: 100%" 
                   :columns="orderListColumns" :data="orderList"
				   ref="sellOrderListTable" size="small"
                   :loading="sellOrderLoading" 
                   @on-row-click="handleSelectSellOrder"
				   no-data-text="使用右上方输入搜索条件">
			</Table>

            <Row type="flex" justify="start" class="margin-top-20 margin-bottom-5">
                <h3 class="padding-left-20" >
                    <b>合计数量:</b> {{ totalCount }} <b class="margin-left-30">合计金额:</b> ￥{{ totalAmount }}
                </h3>
            </Row>
            <Table border highlight-row height="300" :loading="detailLoading" 
                   :columns="sellDetailColumns" :data="sellDetailList"
                   ref="sellGoodTable" style="width: 100%;" size="small"
                   no-data-text="点击上方订单后查看销售明细">
            </Table>
        </Card>

        <Modal v-model="reviewOkModal" width="50" :mask-closable="false" title="审批通过意见登记">
            <Form ref="checkForm" :model="checkForm" :label-width="100">
                <Row >
                    <FormItem label="审核人" :error="checkUserError">
                        <Input v-model="checkForm.checkUser" placeholder="张三;李四" style="width: 40%;"/>
                        <Tooltip transfer >
                            <Icon type="ios-information-outline" size="20"></Icon>
                            <div slot="content" >
                                <strong>用于登记销售质量审核员姓名</strong><br/>
                                <strong>提示:如果是多人验审,请使用“;”号分割多个人的姓名, 例如：张三;李四</strong><br/>
                                <strong style="color: red;">注意：如果商品列表中存在有“特殊管理药品”标识的商品，至少需要双人验审</strong>
                            </div>
                        </Tooltip>
                    </FormItem>
                </Row>
                <Row style="margin-top: 1.5em">
                    <FormItem label="审核意见" :error="checkStatusError">
                        <Select v-model="checkForm.checkStatus" placeholder="请选择" @on-change="checkStatusChange" style="width: 40%">
                            <Option v-for="option in checkStatusOptionList" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                        </Select>
                    </FormItem>
                </Row>
                <Row style="margin-top: 1.5em;">
                    <FormItem label="审核结果描述">
                        <Input v-model="checkForm.checkResult" placeholder="审核结果描述" />
                    </FormItem>
                </Row>
            </Form>
            <Row slot="footer">
                <ButtonGroup>
                    <Button type="success" icon="checkmark" @click="setCheckedOk" :loading="checkSubimitLoading">提交</Button>
                    <Button type="ghost" icon="reply" @click="setCheckedCancel" >取消</Button>
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
import customerSelect from "@/views/selector/customer-select.vue";
import saleSelect from "@/views/selector/sale-select.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";

export default {
  name: "sell-quality-review",
  components: {
    customerSelect,
    saleSelect,
    goodsSpecTags,
    goodsExpand
  },
  data() {
    const stautsShow = function(h, status) {
      let label = "";
      let color = "";
      switch (status) {
        case "INIT":
          label = "待质审";
          color = "#2d8cf0";
          break;
        case "QUALITY_CHECKED":
          label = "待终审";
          color = "#ff9900";
          break;
        case "QUALITY_REJECT":
          label = "质审拒绝";
          color = "#ed3f14";
          break;
        case "SALE_CHECKED":
          label = "审核完成";
          color = "#19be6b";
          break;
        default:
          label = "";
          color = "";
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
      checkStatusOptionList: [
        { key: "OK", name: "通过", defaultResult: "出库质量审核通过" },
        { key: "REJECT", name: "拒绝", defaultResult: "出库质量审核未通过" }
      ],
      statusOptions: [
        { key: "ALL", name: "所有" },
        { key: "INIT", name: "待质审" },
        { key: "QUALITY_REJECT", name: "质审拒绝" },
        { key: "QUALITY_CHECKED", name: "待终审" }
      ],
      dateRange: [
        moment()
          .add(-1, "w")
          .format("YYYY-MM-DD"),
        moment().format("YYYY-MM-DD")
      ],
      query: {
        status: "INIT"
      },
      sellOrderLoading: false,
      currSellOrder: {},
      orderList: [],
      orderListColumns: [
        {
          title: "销售单号",
          key: "orderNumber",
          align: "center",
          width: 180
        },
        {
          title: "制单日",
          key: "createOrderDate",
          align: "center",
          width: 120,
          sortable: true,
          render: (h, params) => {
            let label = params.row.createOrderDate
              ? moment(params.row.createOrderDate).format("YYYY-MM-DD")
              : "";
            return h("span", label);
          }
        },
        {
          title: "仓库点",
          key: "warehouseName",
          align: "center",
          width: 140
        },
        {
          title: "客户",
          key: "customerName",
          align: "center",
          width: 180
        },
        {
          title: "销售员",
          key: "saleId",
          width: 140,
          align: "center",
          render: (h, params) => {
            let nickName = params.row.saleNickName;
            let realName = params.row.saleRealName;
            if (nickName && nickName) {
              return h("span", realName + "---" + nickName);
            } else if (nickName) {
              return h("span", nickName);
            } else {
              return h("span", params.row.saleId);
            }
          }
        },
        {
          title: "当前状态",
          key: "status",
          width: 150,
          render: (h, params) => {
            return stautsShow(h, params.row.status);
          }
        },
        {
          title: "制单人",
          key: "createBy",
          align: "center",
          width: 140
        },
        {
          title: "免零金额",
          key: "freeAmount",
          width: 120
        },
        {
          title: "整单折扣率",
          key: "disRate",
          width: 120
        },
        {
          title: "总计金额",
          key: "totalAmount",
          width: 120
        },
        {
          title: "总计数量",
          key: "totalQuantity",
          width: 120
        },
        {
          title: "收货人",
          key: "customerRepName",
          align: "center",
          width: 150
        },
        {
          title: "收货电话",
          key: "customerRepContactPhone",
          align: "center",
          width: 150
        },
        {
          title: "收货地址",
          key: "customerRepRepertoryAddress",
          align: "center",
          width: 200
        },
        {
          title: "温控方式",
          key: "temperControlName",
          align: "center",
          width: 120
        },
        {
          title: "运输方式",
          key: "shipMethodName",
          align: "center",
          width: 120
        },
        {
          title: "运输工具",
          key: "shipToolName",
          align: "center",
          width: 120
        }
      ],
      detailLoading: false,
      sellDetailList: [],
      detailChooseItems: [],
      sellDetailColumns: [
        {
          title: "商品名称",
          key: "goodsName",
          sortable: true,
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
              params.row.goodsName
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
                tags: params.row.goods.goodsSpecs,
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
            let label = params.row.productDate
              ? moment(params.row.productDate).format("YYYY-MM-DD")
              : "";
            return h("span", label);
          }
        },
        {
          title: "有效期至",
          key: "expDate",
          width: 120,
          render: (h, params) => {
            let label = params.row.expDate
              ? moment(params.row.expDate).format("YYYY-MM-DD")
              : "";
            return h("span", label);
          }
        },
        {
          title: "库存量",
          key: "repertoryQuantity",
          width: 120
        },
        {
          title: "销售在单数量",
          key: "onWayQuantity",
          width: 100
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
          title: "审核状态",
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
          title: "审核人",
          width: 100,
          key: "checkUser",
          render: (h, params) => {
            const checkUser = params.row.checkUser ? params.row.checkUser : "";
            return h("span", checkUser);
          }
        },
        {
          title: "审核日期",
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
          title: "审核结论",
          width: 180,
          key: "checkResult",
          render: (h, params) => {
            const checkResult = params.row.checkResult
              ? params.row.checkResult
              : "";
            return h("span", checkResult);
          }
        }
      ],
      totalCount: 0,
      totalAmount: 0,
      reviewOkModal: false,
      checkForm: {
        sellOrderId: "",
        checkUser: "",
        checkStatus: "",
        checkResult: ""
      },
      checkUserError: "",
      checkStatusError: "",
      checkSubimitLoading: false,
      goodsExpandModal: false,
      expandGoodsSpecs: [],
      expandProductDate: "",
      expandExpDate: ""
    };
  },
  watch: {
    sellDetailList(data) {
      if (data && data.length > 0) {
        this.totalCount = data.reduce((totalCount, item) => {
          let count = item.quantity ? item.quantity : 0;
          return totalCount + count;
        }, 0);
        this.totalAmount = data.reduce((totalAmount, item) => {
          let amount = item.amount ? item.amount : 0;
          return totalAmount + parseFloat(amount);
        }, 0);
      }
    }
  },
  mounted() {
    this.querySellOrderList();
  },
  methods: {
    querySellOrderList() {
      let reqData = {
        customerId: this.query.customerId,
        saleId: this.query.saleId
      };
      let statusList = [];
      if (this.query.status === "ALL") {
        statusList = ["INIT", "QUALITY_REJECT", "QUALITY_CHECKED"];
      } else {
        statusList = [this.query.status];
      }
      reqData["statusList"] = statusList;
      reqData["startDate"] = this.dateRange[0];
      reqData["endDate"] = this.dateRange[1];
      this.sellOrderLoading = true;
      util.ajax
        .post("/sell/order/review/list", reqData)
        .then(response => {
          this.sellOrderLoading = false;
          this.orderList = response.data;
          this.currSellOrder = {};
          this.sellDetailList = [];
        })
        .catch(error => {
          this.sellOrderLoading = false;
          util.errorProcessor(this, error);
        });
    },
    handleSelectSellOrder(data) {
      this.currSellOrder = data;
      if (!data || !data.id) {
        this.sellDetailList = [];
        return;
      }
      let sellOrderId = data.id;
      this.detailLoading = true;
      util.ajax
        .get("sell/order/review/detail", { params: { orderId: sellOrderId } })
        .then(response => {
          this.detailLoading = false;
          let order = response.data;
          if (order && order.details) {
            this.sellDetailList = order.details;
          } else {
            this.sellDetailList = [];
          }
        })
        .catch(error => {
          this.detailLoading = false;
          util.errorProcessor(this, error);
        });
    },

    checkStatusChange(data) {
      let items = this.checkStatusOptionList.filter(item => item.key === data);
      if (items && items[0]) {
        this.checkForm.checkResult = items[0].defaultResult;
      } else {
        this.checkForm.checkResult = "";
      }
    },

    reviewOkBtnClick() {
      if (!this.currSellOrder || !this.currSellOrder.id) {
        this.$Message.info("请先选择需要审核的订单");
        return;
      }
      if (this.currSellOrder.status != "INIT") {
        this.$Message.info("选择的订单不在待质审状态，不能进行审核");
        return;
      }
      this.checkStatusError = "";
      this.checkUserError = "";
      this.checkForm = {
        sellOrderId: this.currSellOrder.id,
        checkUser: "",
        checkStatus: "",
        checkResult: ""
      };
      this.reviewOkModal = true;
    },

    setCheckedCancel() {
      this.checkStatusError = "";
      this.checkUserError = "";
      this.checkForm = {
        sellOrderId: "",
        checkUser: "",
        checkStatus: "",
        checkResult: ""
      };
      this.reviewOkModal = false;
    },

    setCheckedOk() {
      this.checkUserError = "";
      this.checkStatusError = "";
      if (!this.checkForm.checkUser) {
        this.checkUserError = "请输入审核人姓名";
        return;
      }
      if (!this.checkForm.checkStatus) {
        this.checkStatusError = "请选择审核意见";
        return;
      }
      //先验证下商品中是否存在有特殊监管药品，如果存在，验证人需要是双人
      let haveSpecial = false;
      let specialGoodsName = "";
      for (let i = 0; i < this.sellDetailList.length; i++) {
        let goods = this.sellDetailList[i].goods;
        if (goods && goods.specialManage) {
          haveSpecial = true;
          specialGoodsName = goods.name;
          break;
        }
      }
      let checkUser = this.checkForm.checkUser;
      if (
        haveSpecial &&
        (!checkUser ||
          (checkUser.indexOf(";") <= 0 && checkUser.indexOf("；") <= 0))
      ) {
        this.checkUserError =
          "商品: “" +
          specialGoodsName +
          "”属于特殊管制商品, 审核人需要是双人审核";
        return;
      }
      this.checkSubimitLoading = true;
      util.ajax
        .post("/sell/order/review/quality/check", this.checkForm)
        .then(response => {
          this.checkSubimitLoading = false;
          this.$Message.success("审核成功");
          this.setCheckedCancel();
          this.querySellOrderList();
        })
        .catch(error => {
          this.checkSubimitLoading = false;
          util.errorProcessor(this, error);
        });
    },

    reviewCancelBtnClick() {
      if (!this.currSellOrder || !this.currSellOrder.id) {
        this.$Message.info("请先选择需要取消审核的订单");
        return;
      }
      if (
        this.currSellOrder.status !== "QUALITY_CHECKED" &&
        this.currSellOrder.status !== "QUALITY_REJECT"
      ) {
        this.$Message.info("当前订单当前状态不能取消审核");
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "取消确认",
        content: "是否确认取消审核选中的销售订单?",
        onOk: () => {
          self.sellOrderLoading = true;
          util.ajax
            .put("/sell/order/review/quality/cancel/" + this.currSellOrder.id)
            .then(response => {
              self.sellOrderLoading = false;
              self.$Message.success("取消成功");
              self.querySellOrderList();
            })
            .catch(error => {
              self.sellOrderLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    }
  }
};
</script>

<style >
.margin-top-8 {
  margin-top: 8px;
}
.margin-left-30 {
  margin-left: 30px;
}
.ivu-form-item {
  margin-bottom: 5px;
}
.search-div {
  background-color: #fff;
}
.table-div {
  background-color: #fff;
  padding-top: 10px;
  margin-top: 10px;
}
</style>


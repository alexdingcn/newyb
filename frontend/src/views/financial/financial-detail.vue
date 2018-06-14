<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <div style="margin-bottom: 3.5em;">
            <h3>当前交易流水详情</h3>
            <hr size="1" style="width: 95%; margin-top: 0.5em; margin-bottom: 1.5em"/>
            <Table highlight-row size="small" border 
                :columns="flowsColumns" :data="selfFlows">
            </Table>
        </div>

        <div style="margin-bottom: 3.5em;">
            <h3>当前交易关联单详情</h3>
            <hr size="1" style="width: 95%; margin-top: 0.5em; margin-bottom: 1.5em"/>
            <div style="padding:10px 10px; border-left: solid #dddee1 3px;">
                <sell-review-detail v-if="refOrderType === 'SELL_ORDER'" :sellOrderId="refOrderId"></sell-review-detail>

                <sell-back-detail v-if="refOrderType === 'SELL_BACK_ORDER'" :backId="refOrderId"></sell-back-detail>

                <in-order-view-detail v-if="refOrderType === 'BUY_IN_ORDER'" :inOrderId="refOrderId"></in-order-view-detail>

                <buy-back-view-detail v-if="refOrderType === 'BUY_BACK_ORDER'" :buyBackId="refOrderId"></buy-back-view-detail>
                
                <Table v-if="refOrderType === 'FINANCIAL_FLOW'" highlight-row size="small" border 
                    :columns="flowsColumns" :data="refFlows">
                </Table>
            </div>
        </div>

        <div style="margin-bottom: 3.5em;">
            <Tabs>
                <TabPane label="相同关联流水列表">
                    <Alert type="success">
                        <h4>拥有与当前交易流水相同的关联流水号的交易列表</h4>
                    </Alert>
                    <Table highlight-row size="small" border
                        :loading="loading" height="200"
                        :columns="flowsColumns" :data="sameRefNoFlows">
                    </Table>
                </TabPane>
                <TabPane label="关联流水号为当前流水列表">
                    <Alert type="success">
                        <h4>关联流水号为当前交易流水号的交易列表</h4>
                    </Alert>
                    <Table highlight-row size="small" border
                        :loading="loading" height="200"
                        :columns="flowsColumns" :data="sameBizNoFlows">
                    </Table>
                </TabPane>
            </Tabs>
        </div>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import sellReviewDetail from "@/views/sell/review-detail.vue";
import sellBackDetail from "@/views/sell/back-detail.vue";
import inOrderViewDetail from "@/views/repertory/in-view-detail.vue";
import buyBackViewDetail from "@/views/buy/back-view-detail.vue";

export default {
  name: "financialDetail",
  props: {
    detail: Object
  },
  components: {
    sellReviewDetail,
    sellBackDetail,
    inOrderViewDetail,
    buyBackViewDetail
  },
  data() {
    return {
      allBizType: [
        {
          bizType: "BUY_IN",
          label: "采购入库",
          cancel: false,
          canReceive: true,
          canPay: true
        },
        {
          bizType: "BUY_BACK",
          label: "采购退货",
          cancel: false,
          canReceive: true,
          canPay: true
        },
        {
          bizType: "SELL_BACK",
          label: "销售退货",
          cancel: false,
          canReceive: true,
          canPay: true
        },
        {
          bizType: "SELL_BATCH",
          label: "批发销售",
          cancel: false,
          canReceive: true,
          canPay: true
        },
        {
          bizType: "SELL_BACK_FREE",
          label: "销售退货免零",
          cancel: false,
          canReceive: true,
          canPay: true
        },
        {
          bizType: "RECEIVE",
          label: "收款",
          cancel: true,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "PAY",
          label: "付款",
          cancel: true,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "PRE_PAID",
          label: "预付款",
          cancel: true,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "PRE_RECEIVE",
          label: "预收款",
          cancel: true,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "RECORD_RECEIVE",
          label: "记账应收",
          cancel: true,
          canReceive: true,
          canPay: false
        },
        {
          bizType: "RECORD_PAY",
          label: "记账应付",
          cancel: true,
          canReceive: false,
          canPay: true
        },
        {
          bizType: "OFFSET",
          label: "冲销",
          cancel: false,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "RECEIVE_CANCEL",
          label: "收款取消",
          cancel: false,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "PAY_CANCEL",
          label: "付款取消",
          cancel: false,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "PRE_PAID_CANCEL",
          label: "预付款取消",
          cancel: false,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "PRE_RECEIVE_CANCEL",
          label: "预收款取消",
          cancel: false,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "RECORD_RECEIVE_CANCEL",
          label: "记账应收取消",
          cancel: false,
          canReceive: false,
          canPay: false
        },
        {
          bizType: "RECORD_PAID_CANCEL",
          label: "记账应付取消",
          cancel: false,
          canReceive: false,
          canPay: false
        }
      ],
      loading: false,
      selfFlows: [],
      flowsColumns: [
        {
          type: "index",
          width: 60
        },
        {
          title: "流水号",
          key: "bizNo",
          width: 200
        },
        {
          title: "发生日期",
          key: "logDate",
          sortable: true,
          width: 140,
          render: (h, params) => {
            return h(
              "span",
              params.row.logDate
                ? moment(params.row.logDate).format("YYYY-MM-DD HH:mm")
                : ""
            );
          }
        },
        {
          title: "往来账户",
          key: "logAccount",
          sortable: true,
          width: 200
        },
        {
          title: "记账类型",
          key: "bizType",
          width: 120,
          render: (h, params) => {
            let label = this.bizTypeLabel(params.row.bizType);
            return h("span", label);
          }
        },
        {
          renderHeader: (h, params) => {
            return h(
              "Tooltip",
              {
                props: {
                  transfer: true,
                  placement: "top"
                }
              },
              [
                h("span", "关联流水号"),
                h("Icon", {
                  props: { type: "ios-help-outline", size: "15" },
                  style: { marginLeft: "5px" }
                }),
                h(
                  "div",
                  {
                    slot: "content"
                  },
                  [
                    h(
                      "h4",
                      "本次交易的关联到的交易流水号，主要为系统订单流水号或可以为往来账流水号.主要用于做关联信息"
                    )
                  ]
                )
              ]
            );
          },
          key: "bizRefNo",
          width: 200
        },
        {
          title: "摘要",
          key: "keyWord",
          width: 170
        },
        {
          renderHeader: (h, params) => {
            return h(
              "Tooltip",
              {
                props: {
                  transfer: true,
                  placement: "top"
                }
              },
              [
                h("span", "发生金额"),
                h("Icon", {
                  props: { type: "ios-help-outline", size: "15" },
                  style: { marginLeft: "5px" }
                }),
                h(
                  "div",
                  {
                    slot: "content"
                  },
                  [h("h4", "本次流水发生的交易金额,体现为大于0.")]
                )
              ]
            );
          },
          key: "logAmount",
          width: 120
        },
        {
          renderHeader: (h, params) => {
            return h(
              "Tooltip",
              {
                props: {
                  transfer: true,
                  placement: "top"
                }
              },
              [
                h("span", "应收"),
                h("Icon", {
                  props: { type: "ios-help-outline", size: "15" },
                  style: { marginLeft: "5px" }
                }),
                h(
                  "div",
                  {
                    slot: "content"
                  },
                  [h("h4", "本次流水交易的应收账款金额.")]
                )
              ]
            );
          },
          key: "inAmount",
          width: 120
        },
        {
          renderHeader: (h, params) => {
            return h(
              "Tooltip",
              {
                props: {
                  transfer: true,
                  placement: "top"
                }
              },
              [
                h("span", "应付"),
                h("Icon", {
                  props: { type: "ios-help-outline", size: "15" },
                  style: { marginLeft: "5px" }
                }),
                h(
                  "div",
                  {
                    slot: "content"
                  },
                  [h("h4", "本次流水交易的应付账款金额.")]
                )
              ]
            );
          },
          key: "outAmount",
          width: 120
        },
        {
          renderHeader: (h, params) => {
            return h(
              "Tooltip",
              {
                props: {
                  transfer: true,
                  placement: "top"
                }
              },
              [
                h("span", "应收账款余额"),
                h("Icon", {
                  props: { type: "ios-help-outline", size: "15" },
                  style: { marginLeft: "5px" }
                }),
                h(
                  "div",
                  {
                    slot: "content"
                  },
                  [h("h4", "公司当前累计总应收账款.")]
                )
              ]
            );
          },
          key: "surplusInAmount",
          width: 160
        },
        {
          renderHeader: (h, params) => {
            return h(
              "Tooltip",
              {
                props: {
                  transfer: true,
                  placement: "top"
                }
              },
              [
                h("span", "应付账款余额"),
                h("Icon", {
                  props: { type: "ios-help-outline", size: "15" },
                  style: { marginLeft: "5px" }
                }),
                h(
                  "div",
                  {
                    slot: "content"
                  },
                  [h("h4", "公司当前累计总应付账款.")]
                )
              ]
            );
          },
          key: "surplusOutAmount",
          width: 160
        },
        {
          renderHeader: (h, params) => {
            return h(
              "Tooltip",
              {
                props: {
                  transfer: true,
                  placement: "top"
                }
              },
              [
                h("span", "往来账户余额"),
                h("Icon", {
                  props: { type: "ios-help-outline", size: "15" },
                  style: { marginLeft: "5px" }
                }),
                h(
                  "div",
                  {
                    slot: "content"
                  },
                  [
                    h(
                      "h4",
                      { style: { color: "#ed3f14" } },
                      "往来账户余额，代表了当前往来账客户与公司账目关系,主要看最新一条记录的余额."
                    ),
                    h("p", { style: { color: "#ed3f14" } }, "提示信息: "),
                    h(
                      "p",
                      { style: { color: "#ed3f14" } },
                      "大于0代表公司对往来账户存在应付账款;"
                    ),
                    h(
                      "p",
                      { style: { color: "#ed3f14" } },
                      "小于0代表公司对往来账户存在应收账款;"
                    )
                  ]
                )
              ]
            );
          },
          key: "custAmount",
          width: 150
        },
        {
          title: "收/付款方式",
          key: "payMethodName",
          width: 110
        },
        {
          title: "记账凭证号",
          key: "docNo",
          width: 170
        },
        {
          title: "档案编号",
          key: "fileNo",
          width: 170
        },
        {
          title: "结算账号",
          key: "companyAccount",
          width: 170
        },
        {
          title: "往来账户账号",
          key: "custAccount",
          width: 170
        },
        {
          title: "操作员",
          key: "createdBy",
          width: 120
        },
        {
          title: "记账时间",
          sortable: true,
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
        }
      ],
      sameRefNoFlows: [],
      sameBizNoFlows: [],
      refFlows: [],
      refOrderType: "",
      refOrderId: ""
    };
  },
  watch: {
    detail(value) {
      //先清空后重插
      this.selfFlows = [];
      this.sameRefNoFlows = [];
      this.sameBizNoFlows = [];
      this.refFlows = [];
      this.refOrderType = "";
      this.refOrderId = "";
      if (value && value.id) {
        this.selfFlows.push(value);
        //查询具体详情信息
        this.loadDetails(value.id);

        //根据关联单号的字符标识，识别出关联的是那种类型的订单
        let bizRefNo = value.bizRefNo;
        if (!bizRefNo) {
          return;
        } else if (bizRefNo.indexOf("IC") >= 0) {
          this.refOrderType = "BUY_IN_ORDER"; //采购入库订单
          this.refOrderId = value.bizRefId;
        } else if (bizRefNo.indexOf("BB") >= 0) {
          this.refOrderType = "BUY_BACK_ORDER"; //采购退货
          this.refOrderId = value.bizRefId;
        } else if (bizRefNo.indexOf("SO") >= 0) {
          this.refOrderType = "SELL_ORDER"; // 销售订单
          this.refOrderId = value.bizRefId;
        } else if (bizRefNo.indexOf("SG") >= 0) {
          this.refOrderType = "SELL_BACK_ORDER"; //销售退货单
          this.refOrderId = value.bizRefId;
        } else if (bizRefNo.indexOf("AC") >= 0) {
          {
            this.refOrderType = "FINANCIAL_FLOW"; //其他类型直接关联财务流水
            this.refOrderId = value.bizRefId;
            this.loadRefFlow(value.bizRefId);
          }
        } else {
          return;
        }
      }
    }
  },
  methods: {
    loadRefFlow(flowId) {
      this.refFlows = [];
      util.ajax
        .get("/financial/flow/" + flowId)
        .then(response => {
          let data = response.data;
          this.refFlows.push(data);
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    loadDetails(flowId) {
      console.log(flowId);
      this.loading = true;
      util.ajax
        .get("/financial/flow/" + flowId + "/detail")
        .then(response => {
          console.log(response.data);
          this.loading = false;
          this.sameRefNoFlows = response.data.sameRefNoFlows;
          this.sameBizNoFlows = response.data.sameBizNoFlows;
        })
        .catch(error => {
          this.loading = false;
          util.errorProcessor(this, error);
        });
    },

    bizTypeLabel(bizType) {
      if (!bizType) {
        return "";
      }
      for (let i = 0; i < this.allBizType.length; i++) {
        let item = this.allBizType[i];
        if (item["bizType"] === bizType) {
          return this.allBizType[i].label;
        }
      }
      return "";
    }
  }
};
</script>


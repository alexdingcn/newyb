<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Card class="sell-list">
        <p slot="title">
            <Icon type="navicon"></Icon>
            销售订单列表
        </p>
        <div slot="extra">
            <Button icon="ios-search" type="success" :loading="searching" @click="searchBtnClick">查询</Button>
            <Button icon="ios-cloud-download-outline" type="ghost" :loading="searching" @click="exportToExcel">导出</Button>
        </div>
        <Row >
            <Form ref="searchForm" :model="searchFormItem" :label-width="65" class="sellOrderQueryForm">
                <Row type="flex">
                    <i-col span="5" >
                        <FormItem label="客户">
                            <customer-select v-model="searchFormItem.customerId" ></customer-select>
                        </FormItem>
                    </i-col>


                    <i-col span="5" >
                        <FormItem label="制单日期">
                            <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="制单日期" style="width:200px"></DatePicker>
                        </FormItem>
                    </i-col>
                    <i-col span="5" >
                        <FormItem label="销售单号">
                            <Input type="text" v-model="searchFormItem.orderNumber" />
                        </FormItem>
                    </i-col>

                    <i-col span="4" >
                        <FormItem label="销售员" >
                            <sale-select v-model="searchFormItem.saleId"></sale-select>
                        </FormItem>
                    </i-col>
                    <i-col span="4" >
                        <FormItem label="来源" >
                            <warehouse-select v-model="searchFormItem.warehouseId"></warehouse-select>
                        </FormItem>
                    </i-col>
                    <i-col span="5" >
                        <FormItem label="状态" >
                            <Select v-model="searchFormItem.status" placeholder="销售订单状态" filterable clearable>
                                <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                            </Select>
                        </FormItem>
                    </i-col>
                    <i-col span="5" >
                        <FormItem label="是否开票" >
                            <Select v-model="searchFormItem.billStatus" placeholder="是否开票" filterable clearable>
                                <Option v-for="option in BillOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                            </Select>
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
        </Row>

        <Table border highlight-row :columns="tabColumns" :data="tabData" 
                :loading="searching" 
                no-data-text="点击上方查询按钮获取数据"
                ref="table" class="margin-top-10 sellOrderTbl table-action" size="small">
        </Table>

        <Row type="flex" justify="end" class="margin-top-10">
            <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                @on-change="pageChange">
            </Page>
        </Row>

        <Modal v-model="showOrderDetailView" :width="70" :mask-closable="false" title="销售订单详细信息" :footerHide="true">
            <review-detail :sellOrderId="showDetailViewId" @on-cancel="showOrderDetailViewClose"></review-detail>
        </Modal>

        <Modal v-model="showShipDetailView" :width="75" :mask-closable="false" :title="shipDetailTitle" @on-cancel="ShowShipDetailViewClose" :footerHide="true">
            <sell-order-ship :order="selectedOrder" ></sell-order-ship>
        </Modal>

        <Modal v-model="paymentModal" :width="60" :mask-closable="false" :title="paymentModalTitle" :footerHide="true">
            <sell-order-payment :order="selectedOrder" @payment-updated="onPaymentUpdated" ></sell-order-payment>
        </Modal>
        <Modal v-model="invoiceModal" title="发票信息" :mask-closable="false" width="40">
            <Form ref="form" :model="formData" :label-width="90" >
                <Row class="row-margin-bottom">
                    <FormItem label="发票抬头">
                        <Input type="text" placeholder="发票抬头" v-model="formData.invoiceTitle" style="width: 75%;"/>
                    </FormItem>
                </Row>
                <Row class="row-margin-bottom">
                    <FormItem label="发票类型">
                        <option-select optionType="BILL_TYPE" v-model="formData.billType" ></option-select>
                    </FormItem>
                </Row>
                <Row class="row-margin-bottom">
                    <FormItem label="税率">
                        <Input type="text" placeholder="税率" v-model="formData.taxRate" style="width: 75%" />
                    </FormItem>
                </Row>
            </Form>
            <Row type="flex" justify="end" slot="footer" >
                <ButtonGroup size="small">
                    <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveInvoice" >保存开票信息</Button>
                    <Button type="ghost" icon="reply" @click="modalCancel" >取消</Button>
                </ButtonGroup>
            </Row>
        </Modal>
    </Card>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import reviewDetail from "./review-detail.vue";
import sellOrderShip from "./sell-order-ship.vue";
import sellOrderPayment from "./sell-order-payment.vue";
import customerSelect from "@/views/selector/customer-select.vue";
import saleSelect from "@/views/selector/sale-select.vue";
import actionButton from "@/views/my-components/action-button.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
export default {
  name: "sell-order-all",
  components: {
    reviewDetail,
    sellOrderShip,
    sellOrderPayment,
    customerSelect,
    saleSelect,
    warehouseSelect,
      optionSelect,
    actionButton
  },

  data() {
    const statusShow = function(h, status) {
      let label = "";
      let color = "";
      switch (status) {
        case "TEMP_STORAGE":
          label = "制单暂存";
          color = "#80848f";
          break;
        case "INIT":
          label = "制单初始";
          color = "#2d8cf0";
          break;
        case "QUALITY_CHECKED":
          label = "质量审核完成";
          color = "#ff9900";
          break;
        case "QUALITY_REJECT":
          label = "质审拒绝";
          color = "#ed3f14";
          break;
        case "SALE_CHECKED":
          label = "销售审核完成";
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
            color: color
          }
        },
        label
      );
    };

    const payStatusShow = function(h, totalAmt, paidAmt) {
      let label = "";
      let color = "";
      let remainingAmt = totalAmt - paidAmt;
      if (remainingAmt == 0) {
        label = "已支付";
        color = "green";
      } else if (remainingAmt > 0 && paidAmt == 0) {
        label = "未支付";
        color = "red";
      } else if (remainingAmt > 0 && paidAmt > 0) {
        label = "余￥" + remainingAmt.toFixed(2);
        color = "yellow";
      } else if (remainingAmt < 0) {
        label = "超额支付￥" + -remainingAmt.toFixed(2);
        color = "blue";
      } else {
        label = "";
        color = "";
      }
      return h(
        "Tag",
        {
          props: {
            color: color
          }
        },
        label
      );
    };
      const stautsInvoice = function(h, status) {
          let label = "";
          let color = "";
          switch (status) {
              case "FINISH":
                  label = "已开票";
                  color = "#19be6b";
                  break;
              default:
                  label = "未开票";
                  color = "#ff9900";
                  break;
          }
          return h(
              "Tag",
              {
                  props: {
                      color: color
                  }
              },
              label
          );
      };
    return {
        statusOptions: [{key: 'TEMP_STORAGE', name: '制单暂存'}, {key: 'INIT', name: '制单初始'}, {key: 'QUALITY_CHECKED', name: '质量审核完成'},
            {key: 'QUALITY_REJECT', name: '质审拒绝'},{key: 'SALE_CHECKED', name: '销售审核完成'}
        ],
        BillOptions:[{key: 'FINISH', name: '已开票'}, {key: 'INIT', name: '未开票'}],
      searchFormItem: {
        customerId: this.$route.query.customer_id
          ? this.$route.query.customer_id.toString()
          : "",
        orderNumber: "",
        saleId: ""
      },
      dateRange: [
        this.$route.query.start_date
          ? this.$route.query.start_date
          : moment()
              .add(-1, "w")
              .format("YYYY-MM-DD"),
        this.$route.query.end_date
          ? this.$route.query.end_date
          : moment().format("YYYY-MM-DD")
      ],
      searching: false,
        invoiceModal:false,
      tabData: [],
      tabColumns: [
        {
          title: "订单编号",
          width: 180,
          key: "orderNumber",
          align: "center",
          sortable: true,
          render: (h, params) => {
            let actions = [
              {
                type: "primary",
                icon: "social-yen",
                label: "收款",
                disabled: params.row.status !== "SALE_CHECKED",
                data: params.row,
                action: this.showPayment,
                param: params.row
              },
              {
                type: "ghost",
                icon: "model-s",
                label: "运输记录",
                data: params.row,
                action: this.openShowShipDetailView,
                param: params.row
              }
            ];
            let orderNumberBtn = h(
              "Button",
              {
                props: {
                  type: "text",
                  size: "small",
                  icon: "eye"
                },
                on: {
                  click: () => {
                    this.showReviewDetail(params.row.id);
                  }
                }
              },
              params.row.orderNumber
            );
            return (
              "div",
              [
                orderNumberBtn,
                h(actionButton, {
                  class: { rowAction: true },
                  props: {
                    data: actions
                  }
                })
              ]
            );
          }
        },
        {
          title: "制单日",
          key: "createOrderDate",
          width: 120,
          align: "center",
          sortable: true,
          render: (h, params) => {
            let createOrderDate = params.row.createOrderDate;
            return h(
              "span",
              createOrderDate
                ? moment(createOrderDate).format("YYYY-MM-DD")
                : ""
            );
          }
        },
        {
          title: "仓库点",
          key: "warehouseName",
          align: "center",
          width: 100
        },
        {
          title: "客户",
          key: "customerName",
          width: 170,
          align: "center"
        },
        {
          title: "状态",
          key: "status",
          width: 150,
          align: "center",
          render: (h, params) => {
            return statusShow(h, params.row.status);
          }
        },
        {
          title: "付款状态",
          key: "payStatus",
          width: 150,
          align: "center",
          render: (h, params) => {
            return payStatusShow(
              h,
              params.row.totalAmount,
              params.row.paidAmount
            );
          }
        },
          {
              title: "是否开票",
              key: "billStatus",
              width: 150,
              render: (h, params) => {
                  //return stautsInvoice(h, params.row.billStatus);
                  if(params.row.billStatus=="FINISH"){
                      return h('Button', {
                          props: {
                              type: 'primary',
                          },
                          on: {
                              click: () => {
                                  this.queryInvoice(params.row);
                              }
                          }
                      }, '已开票');
                  }else{
                      return h('Button', {
                          props: {
                              type: 'error',
                          },
                          on: {
                              click: () => {
                                  this.addInvoice(params.row);
                              }
                          }
                      }, '未开票');
                  }

              }
          },
        {
          title: "总计金额",
          key: "totalAmount",
          width: 100,
          render: (h, params) => {
            return h("span", "￥" + params.row.totalAmount);
          }
        },
        {
          title: "总计数量",
          key: "totalQuantity",
          width: 100
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
          title: "销售员",
          key: "saleId",
          width: 120,
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
          title: "制单人",
          key: "createBy",
          width: 100,
          align: "center"
        },
        {
          title: "提货员",
          key: "takeGoodsUser",
          width: 100,
          align: "center"
        },
        {
          title: "收货人",
          key: "customerRepName",
          width: 100,
          align: "center"
        },
        {
          title: "收货电话",
          key: "customerRepContactPhone",
          width: 120,
          align: "center"
        },
        {
          title: "收货地址",
          key: "customerRepRepertoryAddress",
          width: 220,
          align: "center"
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
          width: 110
        },
        {
          title: "运输工具",
          key: "shipToolName",
          align: "center",
          width: 110
        }
      ],

        formData: {
            id: '',
            invoiceTitle: '',
            billType: '',
            taxRate: ''
        },
        saveLoading: false,
      showOrderDetailView: false,
      showDetailViewId: -1,
      totalCount: 0,
      currentPage: 1,
      pageSize: 50,
      showShipDetailView: false,
      selectedOrder: {},
      shipDetailTitle: "",
      paymentModal: false,
      paymentModalTitle: ""
    };
  },
  mounted() {
    this.refreshTableData();
  },
  methods: {
    searchBtnClick() {
      this.currentPage = 1;
      this.refreshTableData();
    },
    refreshTableData() {
      let reqData = {
        customerId: this.searchFormItem.customerId,
        orderNumber: this.searchFormItem.orderNumber,
        saleId: this.searchFormItem.saleId,
        warehouseId:this.searchFormItem.warehouseId,
        page: this.currentPage,
        size: this.pageSize
      };

        if(this.searchFormItem.billStatus!=null && this.searchFormItem.billStatus!=""){
            reqData["billStatus"]=this.searchFormItem.billStatus;
        }
        if(this.searchFormItem.status!=null && this.searchFormItem.status!=""){
            reqData["status"]=this.searchFormItem.status;
        }

      reqData["startDate"] = this.dateRange[0];
      reqData["endDate"] = this.dateRange[1];
      this.searching = true;
      let self = this;
      util.ajax
        .post("/sell/order/all/list", reqData)
        .then(response => {
          self.searching = false;
          self.tabData = response.data.data;
          self.totalCount = response.data.count;
        })
        .catch(error => {
          self.searching = false;
          util.errorProcessor(self, error);
        });
    },
    showReviewDetail(orderId) {
      this.showOrderDetailView = true;
      this.showDetailViewId = orderId;
    },
    showOrderDetailViewClose() {
      this.showOrderDetailView = false;
    },
    pageChange(data) {
      this.currentPage = data;
      this.refreshTableData();
    },
    openShowShipDetailView(order) {
      this.selectedOrder = order;
      this.shipDetailTitle = "订单运输记录 - " + order.orderNumber;
      this.showShipDetailView = true;
    },
    ShowShipDetailViewClose() {
      this.showShipDetailView = false;
    },
    showPayment(order) {
      this.selectedOrder = order;
      this.paymentModal = true;
      this.paymentModalTitle = "添加支付记录 - " + order.orderNumber;
    },
    onPaymentUpdated(data) {
      this.refreshTableData();
    },
    exportToExcel() {
      let reqData = {
        customerId: this.searchFormItem.customerId,
        orderNumber: this.searchFormItem.orderNumber,
        saleId: this.searchFormItem.saleId
      };
      var self = this;

      util.ajax
        .post("/export/sell/order", reqData, {
          responseType: "blob"
        })
        .then(response => {
          console.log(response);
          if (response.status === 200 && response.data) {
            var headers = response.headers;
            var blob = new Blob([response.data], {
              type: headers["content-type"]
            });
            var link = document.createElement("a");
            link.href = (window.URL || window.webkitURL).createObjectURL(blob);
            link.download =
              "销售订单导出" + moment().format("YYYYMMDDHHmm") + ".xlsx";

            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
        })
        .catch(error => {
          util.errorProcessor(self, error);
        });
    },queryInvoice(row){
          this.selectedOrder=row;
          this.formData = row;
          this.invoiceModal = true;

      },addInvoice(row){
          this.selectedOrder=row;
          let customer_id = row.customerId;
          util.ajax
              .get("/customer/" +customer_id, { params: { stat: true } })
              .then(response => {
                  if (response.status === 200 && response.data) {
                      console.log(response);

                      this.formData = response.data.customer;
                  }
              })
              .catch(error => {
                 // util.errorProcessor(this, error);
      });
          this.invoiceModal = true;





      },saveInvoice(){
          var self = this;
          this.saveLoading = true;
          let tempData=this.formData;

          this.formData = {
              sellOrderId: this.selectedOrder.id,
              invoiceTitle: tempData.invoiceTitle,
              billType: tempData.billType,
              taxRate: tempData.taxRate
          };
          util.ajax.post('sell/order/invoice/save', this.formData )
              .then((response) => {
                  self.saveLoading = false;
                  if (response.status === 200 && response.data) {
                      this.invoiceModal = false;
                      self.refreshTableData();
                  }
              })
              .catch((error) => {
                  self.saveLoading = false;
                  util.errorProcessor(self, error);
              });
      }, modalCancel() {
          this.formData = {
              id: '',
              invoiceTitle: '',
              billType: '',
              taxRate: ''
          };
          this.$refs.form.resetFields();
          this.brandModal = false;
      },
  }
};
</script>

<style lang="less">
.sell-list {
  .ivu-card-head {
    background-color: #3d74ad;
    border-radius: 4px 4px 0 0;
    p {
      color: white;
    }
  }
  .ivu-card-extra {
    top: 10px;
    .ivu-btn-ghost {
      background-color: white;
    }
  }
}
.sellOrderQueryForm .ivu-form-item {
  margin-bottom: 5px;
}
.sellOrderTbl {
  .ivu-table-cell {
    padding-left: 2px;
    padding-right: 2px;
  }
  .ivu-btn-group {
    display: none;
  }
  .ivu-table-row-hover .ivu-btn-group {
    display: inline-block;
  }
}
</style>


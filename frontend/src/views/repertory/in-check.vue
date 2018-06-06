
<template>
  <div>
      <Card>
          <div v-if="viewMethod === 'CHECK'" slot="title" style="width:50%">
              <Alert show-icon>入库审核，这是入库前的最后一步，审核通过后库存会发生相应变化</Alert>
          </div>
          <div slot="extra">
              <ButtonGroup>
                <Button type="primary" icon="ios-search" :loading="orderLoading" @click="refreshOrder">查询</Button>
                <Button v-if="viewMethod === 'CHECK'" type="success" icon="ios-checkmark" @click="checkOk" >入库审核通过</Button>
                <Button v-if="viewMethod === 'CHECK'" type="info" icon="images" @click="showCheckFile">检验档案</Button>
                <Button v-if="viewMethod === 'SELECT'" type="success" icon="checkmark" @click="selectOrder">选择</Button>
              </ButtonGroup>
          </div>
          
          <Form ref="searchForm" :model="query" :label-width="80">
                <Row type="flex" justify="start">
                    <FormItem label="收货日期">
                        <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="收货日期" style="width:180px"></DatePicker>
                    </FormItem>
                    <FormItem label="仓库">
                        <warehouse-select v-model="query.warehouseId" @on-change="refreshOrder" ></warehouse-select>
                    </FormItem>
                    <FormItem label="供应商">
                        <supplier-select v-model="query.supplierId" @on-change="refreshOrder" ></supplier-select>
                    </FormItem>
                    <FormItem label="状态" v-if="viewMethod === 'CHECK'">
                        <Select v-model="query.status" placeholder="状态" @on-change="refreshOrder" >
                                <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                        </Select>
                    </FormItem>
                </Row>
          </Form>
          <div>
              <Table ref="orderTable" border highlight-row disabled-hover height="250"
                    :columns="orderListColumns" :data="orderList" size="small" 
                    :loading="orderLoading" 
                    @on-row-click="handleSelectOrder" 
				    no-data-text="输入查询条件, 点击上方查询按钮进行查询">
                </Table>
          </div>
          <Row type="flex" justify="start" style="margin-top:15px;">
              <h4 class="detail-count-content" >
                    <b class="detail-count-content-b">总金额:</b> {{ totalAmount }}
                    <b class="detail-count-content-b">入库数量:</b> {{ totalInCount }}
                    <b class="detail-count-content-b">不合格数量:</b> {{ totalErrorCount }}
                </h4>
          </Row>
          <Table border highlight-row height="300" :loading="detailLoading" 
                :columns="detailColumns" :data="detailList" size="small" 
                ref="detailTable" style="width: 100%;" 
                no-data-text="点击上方订单后查看明细">
        </Table>
      </Card>

      <Modal v-model="checkFileModal" title="检验报告档案" :mask-closable="false" width="50">
          <file-detail :fileNo="checkFileNo" @add-file-success="addFileSuccess" ></file-detail>
          <div slot="footer"></div>
      </Modal>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";

export default {
  name: "in-check",
  components: {
    goodsSpecTags,
    warehouseSelect,
    supplierSelect,
    fileDetail
  },
  props: {
    viewMethod: {
      type: String,
      default: "CHECK",
      validator: function(value) {
        return value === "CHECK" || value === "SELECT";
      }
    }
  },
  data() {
    return {
      orderLoading: false,
      detailLoading: false,
      statusOptions: [
        { key: "ALL", name: "所有" },
        { key: "CHECKED", name: "未审查" },
        { key: "IN_CHECKED", name: "已审查" }
      ],
      dateRange: [
        moment()
          .add(-1, "w")
          .format("YYYY-MM-DD"),
        moment().format("YYYY-MM-DD")
      ],
      query: {
        status: "CHECKED"
      },
      orderList: [],
      orderListColumns: [
        {
          type: "index",
          width: 50
        },
        {
          title: "系统单号",
          width: 190,
          key: "orderNumber"
        },
        {
          title: "收货时间",
          key: "receiveDate",
          width: 120,
          render: (h, params) => {
            let receiveDate = params.row.receiveDate;
            return h(
              "span",
              receiveDate ? moment(receiveDate).format("YYYY-MM-DD") : ""
            );
          }
        },
        {
          title: "状态",
          key: "status",
          width: 120,
          render: (h, params) => {
            let color =
              params.row.status === "IN_CHECKED" ? "green" : "#ff9900";
            let label =
              params.row.status === "IN_CHECKED" ? "已审查" : "未审查";
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
          title: "入库方式",
          key: "refTypeName",
          width: 120
        },
        {
          title: "入库仓库",
          key: "warehouseName",
          width: 120
        },
        {
          title: "供应商",
          key: "supplierName",
          width: 170
        },
        {
          title: "供应商代表",
          key: "supplierContactName",
          width: 120
        },
        {
          title: "采购员",
          key: "saleNickName",
          width: 150,
          render: (h, params) => {
            let saleNickName = params.row.saleNickName;
            let saleRealName = params.row.saleRealName;
            if (saleNickName && saleRealName) {
              return h("span", saleNickName + " - [" + saleRealName + "]");
            } else {
              return h("span", saleNickName);
            }
          }
        },
        {
          title: "总计入库数量",
          width: 120,
          key: "totalQuantity"
        },
        {
          title: "总计金额",
          width: 120,
          key: "totalAmount"
        },
        {
          title: "收货员",
          key: "receiveUser",
          width: 150,
          render: (h, params) => {
            let receiveUser = params.row.receiveUser;
            if (!receiveUser) {
              return h("span", params.row.createBy);
            } else {
              return h("span", receiveUser);
            }
          }
        },
        {
          title: "到货温度",
          width: 120,
          key: "receiveTemp"
        },
        {
          title: "验收温度",
          width: 120,
          key: "checkTemp"
        },
        {
          title: "采购属性",
          width: 120,
          key: "buyTypeName"
        },
        {
          title: "到货时间",
          width: 140,
          key: "shipEndDate",
          render: (h, params) => {
            let shipEndDate = params.row.shipEndDate;
            return h(
              "span",
              shipEndDate ? moment(shipEndDate).format("YYYY-MM-DD") : ""
            );
          }
        }
      ],
      detailList: [],
      detailColumns: [
        {
          type: "index",
          width: 50
        },
        {
          title: "质检状态",
          key: "checkStatus",
          width: 140,
          render(h, params) {
            let checkStatus = params.row.checkStatus;
            if (checkStatus) {
              return h(
                "Tag",
                { props: { type: "dot", color: "green" } },
                "已验收"
              );
            } else {
              return h(
                "Tag",
                { props: { type: "dot", color: "red" } },
                "未验收"
              );
            }
          }
        },
        {
          title: "商品名称",
          key: "goodsName",
          width: 160,
          render: (h, params) => {
            return h("span", params.row.goods.name);
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
          width: 120
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
      ],
      totalAmount: 0,
      totalInCount: 0,
      totalErrorCount: 0,
      currentChooseOrder: {},
      checkFileNo: "",
      checkFileModal: false
    };
  },
  mounted() {
    this.refreshOrder();
  },
  watch: {
    detailList(data) {
      if (!data || data.length <= 0) {
        this.totalInCount = 0;
        this.totalErrorCount = 0;
        this.totalAmount = 0;
      } else {
        this.totalAmount = data.reduce((total, item) => {
          return item.amount ? total + item.amount : total + 0;
        }, 0);
        this.totalInCount = data.reduce((total, item) => {
          return item.inCount ? total + item.inCount : total + 0;
        }, 0);
        this.totalErrorCount = data.reduce((total, item) => {
          return item.errorCount ? total + item.errorCount : total + 0;
        }, 0);
      }
    }
  },
  methods: {
    refreshOrder() {
      let statusList = [];
      if (this.query.status === "CHECKED") {
        statusList = ["CHECKED"];
      } else if (this.query.status == "IN_CHECKED") {
        statusList = ["IN_CHECKED"];
      } else {
        statusList = ["INIT", "CHECKED", "IN_CHECKED"];
      }
      if (this.viewMethod === "SELECT") {
        statusList = ["IN_CHECKED"]; //采购单选择查询时，只查询已经审核通过的
      }
      let reqData = {
        statusList: statusList,
        warehouseId: this.query.warehouseId,
        supplierId: this.query.supplierId,
        startReceiveDate: this.dateRange[0],
        endReceiveDate: this.dateRange[1]
      };
      this.orderLoading = true;
      util.ajax
        .post("/repertory/in/list", reqData)
        .then(response => {
          this.orderLoading = false;
          this.orderList = response.data;
        })
        .catch(error => {
          this.orderLoading = false;
          util.errorProcessor(this, error);
        });
      this.currentChooseOrder = {};
      this.detailList = [];
    },

    handleSelectOrder(rowData) {
      if (!rowData || !rowData.id) {
        this.currentChooseOrder = {};
        this.detailList = [];
        return;
      }
      this.currentChooseOrder = rowData;
      this.reloadOrderDetail();
    },

    reloadOrderDetail() {
      this.detailLoading = true;
      util.ajax
        .get("/repertory/in/detail/" + this.currentChooseOrder.id)
        .then(response => {
          this.detailLoading = false;
          let data = response.data;
          if (data) {
            this.detailList = data;
            this.currentChooseOrder.details = this.detailList;
          }
        })
        .catch(error => {
          this.detailLoading = false;
          util.errorProcessor(this, error);
        });
    },

    checkOk() {
      if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
        this.$Message.warning("请先选择需要审核的订单");
        return;
      }
      if (this.currentChooseOrder.status != "CHECKED") {
        this.$Message.info("当前状态不能做审核操作");
        return;
      }
      let self = this;
      self.orderLoading = true;
      let reqData = {
        orderId: this.currentChooseOrder.id
      };
      //验收审核通过完成后，提示是否确认信息，然后提交
      this.$Modal.confirm({
        title: "入库审核确认",
        content: "请确认商品数据正确，提交审核后库存将会发生相应变动",
        onOk: () => {
          util.ajax
            .put("/repertory/in/set/incheck", reqData)
            .then(response => {
              self.orderLoading = false;
              self.$Message.success("审核成功");
              self.refreshOrder();
            })
            .catch(error => {
              self.orderLoading = false;
              util.errorProcessor(self, error);
            });
        },
        onCancel: () => {}
      });
    },

    showCheckFile() {
      if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
        this.$Message.warning("请先选择对应订单信息");
        return;
      }
      let fileNo = this.currentChooseOrder.fileNo;
      this.checkFileNo = fileNo;
      this.checkFileModal = true;
    },

    addFileSuccess(data) {
      if (!data || !data.fileNo) {
        this.$Notice.error({
          title: "系统错误",
          desc: "添加档案成功后获取档案信息失败, 请联系技术人员查询原因."
        });
        return;
      }
      let reqData = {
        orderId: this.currentChooseOrder.id,
        fileNo: data.fileNo
      };
      util.ajax
        .put("/repertory/in/order/fileNo", reqData)
        .then(response => {
          this.currentChooseOrder.fileNo = data.fileNo;
          for (let i = 0; i < this.orderList.length; i++) {
            let row = this.orderList[i];
            if (this.currentChooseOrder.id === row.id) {
              row.fileNo = data.fileNo;
              break;
            }
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
          this.$Notice.error({
            title: "系统错误",
            desc: "添加档案成功后绑定档案信息失败, 请联系技术人员查询原因."
          });
        });
    },

    selectOrder() {
      if (
        !this.currentChooseOrder.id ||
        !this.currentChooseOrder.details ||
        this.currentChooseOrder.details.length <= 0
      ) {
        this.$Message.info("请先选择存在详情的采购入库单.");
        return;
      }
      //出发事件，事件返回订单和订单详情信息
      this.$emit(
        "on-choose",
        this.currentChooseOrder,
        this.currentChooseOrder.details
      );
      this.$nextTick(() => {
        this.currentChooseOrder = {};
      });
    }
  }
};
</script>

<style>
</style>

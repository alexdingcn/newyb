<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Row>
        <Card>
            <p slot="title" >
                <Icon type="document"></Icon> 入库管理
            </p>
            <div slot="extra" style="width:1000px">
                <Row type="flex" justify="end">
                    <i-col span="6">
                        <ButtonGroup>
                            <Button  icon="plus-round" >新增</Button>
                            <Button icon="checkmark-round" @click="showCheckModal">审核</Button>
                            <Button icon="trash-a" @click="showCheckModal">删除</Button>
                        </ButtonGroup>
                    </i-col>
                    <i-col span="5">
                    <DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="订单日期" style="width:180px"></DatePicker>
                    </i-col>
                    <i-col span="4" class="padding-2">
                    <supplier-select v-model="query.supplierId"></supplier-select>
                    </i-col>
                    <i-col span="2" class="padding-2" >
                    <Select v-model="query.status" placeholder="状态">
                        <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                    </Select>
                    </i-col>
                    <i-col span="2" >
                    <Button type="primary" icon="ios-search" @click="queryOrderList"></Button>
                    </i-col>
                </Row>
            </div>
            <Table border highlight-row disabled-hover
                   :columns="orderListColumns" :data="orderList"
                   ref="buyOrderListTable" size="small"
                   @on-row-click="handleSelectBuyOrder"
                   @on-row-dblclick="choosedItem"
                   no-data-text="使用右上方输入搜索条件">
            </Table>
        </Card>
    </Row>

</template>

<script>
import axios from "axios";
import moment from "moment";
import util from "@/libs/util.js";
import supplierSelect from "@/views/selector/supplier-select.vue";

export default {
  name: "buy_order",
  props: {
    chooseModal: {
      type: Boolean,
      default: false
    }
  },
  components: {
    supplierSelect
  },
  data() {
    return {
      statusOptions: [
        { key: "ALL", name: "所有" },
        { key: "INIT", name: "未审批" },
        { key: "CHECKED", name: "已审批" }
      ],
      query: {
        status: "INIT",
        supplierId: ""
      },
      dateRange: [
        moment()
          .add(-1, "w")
          .format("YYYY-MM-DD"),
        moment().format("YYYY-MM-DD")
      ],
      orderList: [],
      goodsLoading: false,
      totalAmount: 0,
      orderItems: [],
      checkResult: "",
      orderTitle: "",
      checkModalShow: false,
      orderListColumns: [
        {
          type: this.chooseModal ? "" : "selection",
          width: this.chooseModal ? 0 : 60,
          align: "center"
        },
        {
          title: "状态",
          align: "center",
          key: "status",
          width: 120,
          render: (h, params) => {
            const row = params.row;
            const color =
              row.status === "INIT"
                ? "blue"
                : row.status === "CHECKED" ? "green" : "red";
            const text =
              row.status === "INIT"
                ? "待审"
                : row.status === "CHECKED" ? "已审" : "拒绝";

            return h(
              "Tag",
              {
                props: {
                  color: color
                }
              },
              text
            );
          }
        },

        {
          title: "入库类型",
          align: "center",
          key: "",
          width: 120
        },
        {
          title: "仓库点",
          align: "center",
          key: "warehouse",
          width: 150
        },
        {
          title: "供应商",
          align: "center",
          key: "supplier",
          width: 150
        },
        {
          title: "制单人",
          align: "center",
          key: "createdBy",
          width: 100
        },
        {
          title: "审核人",
          align: "center",
          key: "checkedBy",
          width: 120
        },
        {
          title: "订单日期",
          align: "center",
          key: "createdTime",
          width: 120,
          render: (h, params) => {
            return moment(params.row.createdTime).format("YYYY-MM-DD");
          }
        },
        {
          title: "审核日期",
          align: "center",
          key: "checkTime",
          width: 120,
          render: (h, params) => {
            return moment(params.row.checkTime).format("YYYY-MM-DD");
          }
        },
        {
          title: "订单号",
          align: "center",
          key: "orderNumber",
          width: 200
        }
      ],

      currchooseItem: ""
    };
  },
  mounted() {
    this.queryOrderList();
  },
  activated() {},
  watch: {
    orderItems: function() {
      this.totalAmount = this.orderItems.reduce(function(total, item) {
        return total + parseFloat(item.amount);
      }, 0);
    }
  },
  methods: {
    rowClassName(row, index) {
      if (row.status) {
        return "table-row-" + row.status.toLowerCase();
      }
      return "";
    },
    queryOrderList() {
      var self = this;
      this.orderList = [];
      this.orderItems = [];
      if (this.dateRange && this.dateRange.length == 2) {
        this.query["startDate"] = this.dateRange[0];
        this.query["endDate"] = this.dateRange[1];
      }
      if (this.chooseModal) {
        this.query["status"] = "CHECKED"; //选择模式下,只查询审核通过的订单类型
      }
      util.ajax
        .post("/buy/list", this.query)
        .then(function(response) {
          if (response.status === 200 && response.data) {
            self.orderList = response.data;
            if (self.orderList && self.orderList.length > 0) {
              self.handleSelectBuyOrder(self.orderList[0]);
            }
          }
        })
        .catch(function(error) {
          util.errorProcessor(self, error);
        });
    },

    handleSelectBuyOrder(row) {
      this.currchooseItem = row;
      var self = this;
      util.ajax
        .get("/buy/orderdetail/" + row.id)
        .then(function(response) {
          if (response.status === 200 && response.data) {
            self.orderItems = response.data;
          }
        })
        .catch(function(error) {
          util.errorProcessor(self, error);
        });
    },
    showCheckModal() {
      var rows = this.$refs.buyOrderListTable.getSelection();
      if (!rows || rows.length === 0) {
        this.$Message.warning("请选择订单");
      } else if (rows.length > 1) {
        this.$Message.warning("请一次选择一条订单");
      } else {
        this.orderTitle =
          "订单ID:" + rows[0].id + " 供应商:" + rows[0].supplier;
        this.checkModalShow = true;
      }
    },
    setChecked(result) {
      var self = this;
      var rows = this.$refs.buyOrderListTable.getSelection();
      if (!rows || rows.length === 0) {
        this.$Message.warning("请选择订单");
      } else if (rows.length > 1) {
        this.$Message.warning("请一次选择一条订单");
      } else if (rows.length == 1) {
        util.ajax
          .post("/buy/status", {
            orderId: rows[0].id,
            orderStatus: result ? "CHECKED" : "REJECTED",
            checkResult: this.checkResult
          })
          .then(function(response) {
            self.checkModalShow = false;
            if (response.status === 200) {
              self.queryOrderList();
            }
          })
          .catch(function(error) {
            self.checkModalShow = false;
            util.errorProcessor(self, error);
          });
      }
    },

    choosedItem() {
      if (!this.currchooseItem || !this.currchooseItem.id) {
        this.$Message.warning("请先选择对应订单信息");
        return;
      }
      this.$emit("on-choosed", this.currchooseItem);
    }
  }
};
</script>

<style >
.ivu-table .table-row-checking {
  background-color: #2db7f5;
  color: #fff;
}
</style>

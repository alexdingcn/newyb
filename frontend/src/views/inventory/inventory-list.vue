<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <FontIcon type="icon-cangkucangchu"></FontIcon> 盘点单列表
            </p>

            <div slot="extra">
                <ButtonGroup>
                    <Button type="primary" icon="plus" @click="gotoAddPage" >新增盘点单</Button>
                </ButtonGroup>
            </div>

            <Row class="row-margin-bottom margin-left-10">
              <i-input v-model="searchValue" style="width:250px;" placeholder="盘点单号/盘点名称" icon="search" @on-click="refreshTables"></i-input>
              <warehouse-select v-model="searchWarehouseId" style="width:200px; margin-left:15px;" @on-change="refreshTables"></warehouse-select>
              <Select v-model="searchStatus" placeholder="状态" @on-change="refreshTables" style="width:150px; margin-left:15px;">
                  <Option v-for="item in statusList" :key="item.value" :value="item.value">{{item.label}}</Option>
              </Select>
              <DatePicker type="daterange" v-model="searchDate" placement="bottom-start" placeholder="制单时间" style="width:180px; margin-left:15px;" @on-change="refreshTables"></DatePicker>
              <Button type="info" icon="search" @click="refreshTables" style="margin-left:15px;"></Button>
            </Row>

            <Table ref="inventorysTable" highlight-row :loading="tableLoading" 
                :columns="tableColumns" :data="tableData" class="table-action"
            ></Table>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
            
        </Card>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import actionButton from "@/views/my-components/action-button.vue";

export default {
  name: "inventoryList",
  components: {
    warehouseSelect
  },
  data() {
    return {
      statusList: [
        { value: "ALL", label: "全部" },
        { value: "UNCHECK", label: "待审核" },
        { value: "CHECKED", label: "已审核" },
        { value: "CANCEL", label: "已取消" }
      ],
      searchValue: "",
      searchWarehouseId: "",
      searchStatus: "",
      searchDate: [
        moment()
          .add(-1, "M")
          .format("YYYY-MM-DD"),
        moment().format("YYYY-MM-DD")
      ],
      tableLoading: false,
      tableData: [],
      tableColumns: [
        {
          type: "index",
          width: 80
        },
        {
          title: "盘点单号",
          key: "orderNumber",
          width: 200,
          render: (h, params) => {
            let orderNumber = params.row.orderNumber;
            let buttonArr = [
              {
                type: "primary",
                icon: "eye",
                label: "详情/审核",
                data: params.row,
                action: this.showDetail,
                param: params.row
              }
            ];
            return h("div", [
              h("span", orderNumber),
              h(actionButton, {
                class: { rowAction: true },
                props: {
                  data: buttonArr
                }
              })
            ]);
          }
        },
        {
          title: "盘点名称",
          key: "orderName",
          width: 200
        },
        {
          title: "仓库点",
          key: "warehouseName",
          width: 180
        },
        {
          title: "制单人",
          key: "createBy",
          width: 120
        },
        {
          title: "状态",
          key: "status",
          width: 150,
          render: (h, params) => {
            let status = params.row.status;
            let label = "";
            let color = "";
            if (status === "UNCHECK") {
              label = "待审核";
              color = "#ff9900";
            } else if (status === "CHECKED") {
              label = "已审核";
              color = "#19be6b";
            } else if (status === "CANCEL") {
              label = "已取消";
              color = "#ed3f14";
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
          }
        },
        {
          title: "审核人",
          key: "checkUserName",
          width: 120
        },
        {
          title: "审核时间",
          key: "checkTime",
          width: 150,
          render: (h, params) => {
            let result = params.row.checkTime
              ? moment(params.row.checkTime).format("YYYY-MM-DD HH:mm")
              : "";
            return h("span", result);
          }
        },
        {
          title: "备注",
          key: "comment"
        }
      ],
      totalCount: 0,
      currentPage: 1,
      pageSize: 30
    };
  },
  activated() {
    this.refreshTables();
  },
  mounted() {
    this.refreshTables();
  },
  methods: {
    pageChange(data) {
      this.currentPage = data;
      this.refreshTables();
    },
    refreshTables() {
      let statuses = ["UNCHECK", "CHECKED", "CANCEL"];
      if (this.searchStatus && this.searchStatus != "ALL") {
        statuses = [this.searchStatus];
      }
      let reqData = {
        searchValue: this.searchValue,
        warehouseId: this.searchWarehouseId,
        statusList: statuses,
        startDate: this.searchDate[0],
        endDate: this.searchDate[1],
        page: this.currentPage,
        size: this.pageSize
      };
      console.log(reqData);
      this.tableLoading = true;
      util.ajax
        .post("/inventory/list", reqData)
        .then(response => {
          this.tableLoading = false;
          console.log(response);
          this.tableData = response.data.data;
          this.totalCount = response.data.totalCount;
        })
        .catch(error => {
          this.tableLoading = false;
          util.errorProcessor(this, error);
        });
    },
    showDetail(row) {
      console.log(row);
      let params = {
        inventoryId: row.id
      };
      this.$router.push({
        name: "inventory-detail",
        params: params
      });
    },

    gotoAddPage() {
      this.$router.push({
        name: "inventory-add"
      });
    }
  }
};
</script>


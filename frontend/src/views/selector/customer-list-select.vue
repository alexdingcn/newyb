<template>
    <div>
        <Row :gutter="16">
            <i-col span="8">
                <i-input v-model="customerQuery" clearable
                       @on-enter="queryCustomers"
                       @on-change="clearQueryInput"
                       placeholder="客户/编号/拼音/联系人">
                    <span slot="append">
                        <Button type="primary" size="small" icon="ios-search" @click="searchCustomer"></Button>
                    </span>
                </i-input>
            </i-col>
            <i-col span="5">
                <Select v-model="customerTypeQuery" @on-change="onCustomerCategoryChange">
                    <Option v-for="item in customerCategoryList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                </Select>
            </i-col>
        </Row>
        <Table ref="customerSelect" size="small" border
               :columns="customerColumns" :data="customerList"
               :loading="customerLoading"
               @on-row-click="chooseCustomer"
                class="margin-top-10 customer-select-table">
        </Table>
        <Row class="margin-top-8">
            <div style="float: right;">
                <Page :total="totalCustomerCount" :current="currentPage" :page-size="pageSize" @on-change="changePage" size="small" show-total></Page>
            </div>
        </Row>
    </div>
</template>

<script>
import util from "@/libs/util.js";

export default {
  name: "customerListSelect",

  props: ["disabled", "size"],
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      totalCustomerCount: 0,
      customerQuery: "",
      customerTypeQuery: "",
      selectSize: this.size,
      customerLoading: false,
      customerList: [],
      customerCategoryList: [],
      customerColumns: [
        {
          type: "index",
          width: 60,
          align: "center"
        },
        {
          key: "customerNo",
          title: "客户编号"
        },
        {
          key: "categoryName",
          title: "客户类型"
        },
        {
          key: "name",
          title: "名称"
        },
        {
          key: "employee",
          title: "联系人"
        },
        {
          key: "contactPhone",
          title: "联系人电话"
        },
        {
          title: "操作",
          key: "action",
          width: 120,
          render: (h, params) => {
            return h(
              "Button",
              {
                props: {
                  type: "text",
                  size: "small"
                }
              },
              "选择"
            );
          }
        }
      ]
    };
  },
  methods: {
    reload() {
      this.currentPage = 1;
      this.totalCustomerCount = 0;
      this.queryCustomers();
      this.queryCustomerCategoryList();
    },
    changePage(page) {
      this.currentPage = page;
      this.queryCustomers();
    },
    queryCustomers() {
      var self = this;
      let queryObj = {
        page: this.currentPage,
        size: this.pageSize
      };

      if (this.customerQuery !== "") {
        queryObj["search"] = this.customerQuery;
      }
      if (this.customerTypeQuery > 0) {
        queryObj["categoryId"] = this.customerTypeQuery;
      }

      this.customerLoading = true;
      util.ajax
        .get("/customer/list", { params: queryObj })
        .then(function(response) {
          console.log(response.data);
          self.customerLoading = false;
          self.totalCustomerCount = response.data.count;
          self.customerList = response.data.data;
        })
        .catch(function(error) {
          self.customerLoading = false;
          util.errorProcessor(self, error);
        });
    },
    onCustomerCategoryChange() {
      this.queryCustomers();
    },
    queryCustomerCategoryList() {
      var self = this;
      util.ajax
        .get("/customer/category/list")
        .then(function(response) {
          self.customerCategoryList = response.data;
        })
        .catch(function(error) {
          util.errorProcessor(self, error);
        });
    },
    searchCustomer() {
      this.queryCustomers();
    },
    clearSingleSelect() {
      this.$refs.customerSelect.clearSingleSelect();
    },
    clearQueryInput(event) {
      if (event.target.value === "") {
        this.queryCustomers();
      }
    },
    chooseCustomer(row, index) {
      this.$emit("on-choosed", row);
    },
    onChange(data) {
      let customers = this.customerList.filter(item => item.id === data);
      let customer = customers[0] ? customers[0] : "";
      this.$emit("input", data);
      this.$emit("on-change", data, customer);
    }
  }
};
</script>
<style >
.customer-select-table td {
  cursor: pointer;
}
</style>

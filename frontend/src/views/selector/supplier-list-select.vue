<template>
    <div>
        <Row :gutter="16">
            <i-col span="8">
                <i-input v-model="supplierQuery" clearable
                       @on-enter="querySuppliers"
                       @on-change="clearQueryInput"
                       placeholder="供应商/编号/拼音/联系人">
                    <span slot="append">
                        <Button type="primary" size="small" icon="ios-search" @click="searchsupplier"></Button>
                    </span>
                </i-input>
            </i-col>
        </Row>
        <Table ref="supplierSelect" size="small" border
               :columns="supplierColumns" :data="supplierList"
               :loading="supplierLoading"
               @on-row-click="choosesupplier"
                class="margin-top-10 supplier-select-table">
        </Table>
        <Row class="margin-top-8">
            <div style="float: right;">
                <Page :total="totalsupplierCount" :current="currentPage" :page-size="pageSize" @on-change="changePage" size="small" show-total></Page>
            </div>
        </Row>
    </div>
</template>

<script>
import util from "@/libs/util.js";

export default {
  name: "supplierListSelect",

  props: ["disabled", "size"],
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      totalsupplierCount: 0,
      supplierQuery: "",
      selectSize: this.size,
      supplierLoading: false,
      supplierList: [],
      supplierColumns: [
        {
          type: "index",
          width: 60,
          align: "center"
        },
        {
          key: "name",
          title: "名称"
        },
        {
          key: "address",
          title: "地址"
        },
        {
          key: "contact",
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
      this.totalsupplierCount = 0;
      this.querySuppliers();
    },
    changePage(page) {
      this.currentPage = page;
      this.querySuppliers();
    },
    querySuppliers() {
      var self = this;
      let queryObj = {
        page: this.currentPage,
        size: this.pageSize
      };

      if (this.supplierQuery !== "") {
        queryObj["search"] = this.supplierQuery;
      }

      this.supplierLoading = true;
      util.ajax
        .get("/supplier/list", { params: queryObj })
        .then(function(response) {
          self.supplierLoading = false;
          self.supplierList = response.data.data;
          self.totalsupplierCount = response.data.count;
        })
        .catch(function(error) {
          self.supplierLoading = false;
          util.errorProcessor(self, error);
        });
    },
    searchsupplier() {
      this.querySuppliers();
    },

    clearQueryInput(event) {
      if (event.target.value === "") {
        this.querySuppliers();
      }
    },
    choosesupplier(row, index) {
      this.$emit("on-choosed", row);
    },
    onChange(data) {
      let suppliers = this.supplierList.filter(item => item.id === data);
      let supplier = suppliers[0] ? suppliers[0] : "";
      this.$emit("input", data);
      this.$emit("on-change", data, supplier);
    }
  }
};
</script>
<style >
.supplier-select-table td {
  cursor: pointer;
}
</style>

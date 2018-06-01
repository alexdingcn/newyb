<template>
    <div>
        <Row :gutter="16">
            <i-col span="8">
                <i-input v-model="factoryQuery" clearable
                       @on-enter="queryFactorys"
                       @on-change="clearQueryInput"
                       placeholder="生产企业/拼音/联系人/联系人电话">
                    <span slot="append">
                        <Button type="primary" size="small" icon="ios-search" @click="searchfactory"></Button>
                    </span>
                </i-input>
            </i-col>
        </Row>
        <Table ref="factorySelect" size="small" border
               :columns="factoryColumns" :data="factoryList"
               :loading="factoryLoading"
               @on-row-click="choosefactory"
                class="margin-top-10 factory-select-table">
        </Table>
        <Row class="margin-top-8">
            <div style="float: right;">
                <Page :total="totalfactoryCount" :current="currentPage" :page-size="pageSize" @on-change="changePage" size="small" show-total></Page>
            </div>
        </Row>
    </div>
</template>

<script>
import util from "@/libs/util.js";

export default {
  name: "factoryListSelect",

  props: ["disabled", "size"],
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      totalfactoryCount: 0,
      factoryQuery: "",
      selectSize: this.size,
      factoryLoading: false,
      factoryList: [],
      factoryColumns: [
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
          key: "cityName",
          title: "城市"
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
      this.totalfactoryCount = 0;
      this.queryFactorys();
    },
    changePage(page) {
      this.currentPage = page;
      this.queryFactorys();
    },
    queryFactorys() {
      var self = this;
      let queryObj = {
        page: this.currentPage,
        size: this.pageSize
      };

      if (this.factoryQuery !== "") {
        queryObj["search"] = this.factoryQuery;
      }

      this.factoryLoading = true;
      util.ajax
        .get("/factory/list", { params: queryObj })
        .then(function(response) {
          self.factoryLoading = false;
          self.factoryList = response.data.data;
          self.totalfactoryCount = response.data.count;
        })
        .catch(function(error) {
          self.factoryLoading = false;
          util.errorProcessor(self, error);
        });
    },
    searchfactory() {
      this.queryFactorys();
    },

    clearQueryInput(event) {
      if (event.target.value === "") {
        this.queryFactorys();
      }
    },
    choosefactory(row, index) {
      this.$emit("on-choosed", row);
    },
    onChange(data) {
      let factorys = this.factoryList.filter(item => item.id === data);
      let factory = factorys[0] ? factorys[0] : "";
      this.$emit("input", data);
      this.$emit("on-change", data, factory);
    }
  }
};
</script>
<style >
.factory-select-table td {
  cursor: pointer;
}
</style>

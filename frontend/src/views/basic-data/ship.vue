<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
    <Row>
        <i-col span="7">
            <Card>
                <p slot="title">运输公司信息</p>
                <div slot="extra">
                    <Button type="success" size="small" icon="plus-round" @click="addBtnClick">新建承运公司</Button>
                </div>
                <div>
                    <Row>
                        <i-input type="text" v-model="searchVal" @on-enter="searchBtnClick" placeholder="名称/执照号">
                            <Button slot="append" type="text" icon="search" :loading="searching" @click="searchBtnClick"></Button>
                        </i-input>
                    </Row>
                    <Row type="flex" justify="center">
                        <Table border highlight-row :columns="tabColumns" :data="tabData" 
                                :loading="searching" 
                                @on-row-click="tableRowClick"
                                ref="table" style="width: 100%;" size="small">
                        </Table>
                    </Row>
                    <Row type="flex" justify="end">
                        <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                            @on-change="pageChange">
                        </Page>
                    </Row>
                </div>
            </Card>
        </i-col>
        <i-col span="16" style="margin-left:5px;">
            <ship-info :shipId="currShipId" @save-ok="shipSave" ></ship-info>
        </i-col>
    </Row>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import shipInfo from "./ship-info.vue";

export default {
  name: "ship",
  components: {
    shipInfo
  },
  data() {
    return {
      searchVal: "",
      searching: false,
      tabData: [],
      totalCount: 0,
      currentPage: 1,
      pageSize: 50,
      tabColumns: [
        {
          title: "名称",
          key: "name",
          width: 200
        },
        {
          title: "是否禁用",
          key: "enabled",
          width: 120,
          render: (h, params) => {
            let color = params.row.enabled ? "green" : "red";
            let label = params.row.enabled ? "启用" : "禁用";
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
          title: "负责人",
          key: "employee",
          width: 120
        }
      ],
      currShipId: ""
    };
  },
  mounted() {
    this.refreshShipList();
  },
  methods: {
    searchBtnClick() {
      this.currentPage = 1;
      this.refreshShipList();
    },
    pageChange(data) {
      this.currentPage = data;
      this.refreshShipList();
    },
    refreshShipList() {
      let reqData = {
        search: this.searchVal,
        page: this.currentPage,
        size: this.pageSize
      };
      this.searching = true;
      util.ajax
        .get("/ship/list", { params: reqData })
        .then(response => {
          this.searching = false;
          this.tabData = response.data.data;
          this.totalCount = response.data.count;
          this.addBtnClick();
        })
        .catch(error => {
          this.searching = false;
          util.errorProcessor(this, error);
        });
    },

    addBtnClick() {
      this.currShipId = "";
      this.$refs.table.clearCurrentRow();
    },

    tableRowClick(row) {
      this.currShipId = row.id;
    },

    shipSave(data, action) {
      if (action === "edit") {
        let self = this;
        this.tabData.forEach((item, index) => {
          if (data.id === item.id) {
            self.$set(self.tabData, index, data);
          }
        });
      } else {
        this.refreshShipList();
      }
    }
  }
};
</script>

<style >
</style>


<style lang="less">
@import "../../styles/common.less";
/*@import './good.less';*/
</style>

<template>
    <div class="access">
        <Row :gutter="10">
            <i-col span="10">
                <Card>
                    <p slot="title">
                        <Icon type="ios-flask-outline"></Icon> 生产企业
                    </p>
                    <div slot="extra">
                        <ButtonGroup size="small" class="padding-left-20">
                            <Button type="primary" icon="android-add-circle" @click="addFactory">添加</Button>
                            <Button type="error" icon="android-remove-circle"  @click="delFactory">删除</Button>
                        </ButtonGroup>
                    </div>
                    <Row type="flex" justify="end">
                        <i-input v-model="searchFactoryVal" placeholder="企业名称/联系人/拼音简称" clearable 
                            @on-enter="validateSearch">
                            <Button slot="append" type="primary" shape="circle" icon="ios-search" @click="searchFactory"></Button>
                        </i-input>
                    </Row>
                    <Row type="flex" justify="center" align="middle" class="advanced-router margin-top-8">
                        <Table border highlight-row :columns="orderColumns" :loading="tabLoading" 
                            @on-row-click="tabRowClick" 
                            :data="factoryData" ref="factoryTable" style="width: 100%;" size="small"></Table>
                    </Row>
                </Card>
            </i-col>
            <i-col span="14">
                <factory-info ref="facinfo" :factoryId="currFactoryId" @save-ok="factorySaveOk" ></factory-info>
            </i-col>
        </Row>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import factoryInfo from "./factory-info.vue";

export default {
  name: "factory",
  components: {
    factoryInfo
  },
  data() {
    return {
      searchFactoryVal: "",
      tabLoading: false,
      currFactoryId: "",
      factoryData: [],
      orderColumns: [
        {
          type: "selection",
          width: 60
        },
        {
          title: "名称",
          key: "name",
          sortable: true,
          width: 200
        },
        {
          title: "产地",
          key: "origin",
          sortable: true,
          width: 120
        },
        {
          title: "拼音简称",
          key: "pinyin",
          align: "center",
          width: 150,
          sortable: true
        }
      ]
    };
  },
  mounted() {
    this.loadFactoryList();
    //this.$refs.factoryinfo.resetFields();
  },
  computed: {},
  methods: {
    validateSearch: function(e) {
      this.searchFactory();
    },
    searchFactory() {
      var self = this;

      self.tabLoading = true;
      util.ajax
        .post("/factory/search", { search: this.searchFactoryVal })
        .then(function(response) {
          self.tabLoading = false;
          self.factoryData = response.data;
          self.currFactoryId = "";
          self.$refs.factoryTable.clearCurrentRow();
        })
        .catch(function(error) {
          self.tabLoading = false;
          util.errorProcessor(self, error);
        });
    },
    loadFactoryList() {
      var self = this;
      self.tabLoading = true;
      util.ajax
        .get("/factory/list")
        .then(function(response) {
          self.tabLoading = false;
          if (response.status === 200) {
            self.factoryData = response.data.data;
            self.currFactoryId = "";
            self.$refs.factoryTable.clearCurrentRow();
          }
        })
        .catch(function(error) {
          self.tabLoading = false;
          util.errorProcessor(self, error);
        });
    },
    addFactory() {
      this.currFactoryId = "";
      this.$refs.factoryTable.clearCurrentRow();
    },
    delFactory() {
      var row = this.$refs.factoryTable.getSelection();
      if (row && row.length > 0) {
        var self = this;
        var delArr = [];
        for (var i = 0; i < row.length; i++) {
          delArr.push(row[i].id);
        }
        self.$Modal.confirm({
          title: "删除确认",
          content: "确认删除选中的厂家信息?",
          onOk: () => {
            util.ajax
              .post("/factory/remove", { ids: delArr })
              .then(function(response) {
                self.loadFactoryList();
              })
              .catch(function(error) {
                util.errorProcessor(self, error);
              });
          },
          onCancel: () => {}
        });
      } else {
        this.$Message.warning("请选择一个生产企业后操作");
      }
    },
    tabRowClick(row) {
      this.currFactoryId = row.id;
    },
    factorySaveOk(data, action) {
      this.$refs.facinfo.clearss();
      if (action === "edit") {
        if (data && data.id) {
          let self = this;
          self.factoryData.forEach((item, index) => {
            if (item.id === data.id) {
              self.$set(self.factoryData, index, data);
            }
          });
        }
      } else {
        this.loadFactoryList();
        //this.searchFactory();
      }
    }
  }
};
</script>

<style >
</style>

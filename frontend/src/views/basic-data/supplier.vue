<style lang="less">
@import "../../styles/common.less";
@import "./supplier.less";
</style>

<template>
    <div class="access">
        <Row>
            <i-col span="8">
                <Card>
                    <p slot="title">
                        <Icon type="android-contacts"></Icon> 供应商
                    </p>
                    <div slot="extra">
                        <ButtonGroup class="padding-left-20">
                            <Button type="primary" icon="android-add-circle" @click="addSupplier">添加</Button>
                            <Button type="error" icon="android-remove-circle"  @click="delSupplier">删除</Button>
                        </ButtonGroup>
                    </div>

                    <Row type="flex" justify="end">
                        <i-input v-model="searchSupplierVal" placeholder="企业名称/联系人/拼音简称" clearable style="width: 300px"
                            @on-enter="validateSearch">
                            <Button slot="append" type="primary" shape="circle" icon="ios-search" @click="searchSupplier"></Button>
                        </i-input>
                    </Row>
                    <Row type="flex" justify="center" align="middle" class="advanced-router margin-top-8">
                        <Table border highlight-row :columns="orderColumns" 
                            @on-row-click="chooseItem"
                            :data="supplierData" ref="supplierTable" style="width: 100%;" size="small"></Table>
                    </Row>
                </Card>
            </i-col>
            <i-col span="15" style="margin-left: 10px;">
                <supplier-info :supplierId="currSupplierId" @save-ok="supplierSaveOk"></supplier-info>
            </i-col>            
        </Row>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import supplierInfo from "@/views/basic-data/supplier-info.vue";

export default {
  name: "supplier",
  components: {
    supplierInfo
  },
  data() {
    return {
      searchSupplierVal: "",
      supplierData: [],
      orderColumns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          title: "名称",
          key: "name",
          width: 150,
          sortable: true
        },
        {
          title: "是否可用",
          key: "enabled",
          width: 120,
          sortable: true,
          render: (h, params) => {
            let color = params.row.enabled ? "green" : "red";
            let lable = params.row.enabled ? "可用" : "禁用";
            return h(
              "Tag",
              {
                props: {
                  type: "dot",
                  color: color
                }
              },
              lable
            );
          }
        },
        {
          title: "拼音简称",
          key: "pinyin",
          width: 120,
          sortable: true
        },
        {
          title: "负责人",
          key: "employee",
          width: 120
        },
        {
          title: "联系人",
          key: "contact",
          width: 200,
          sortable: true
        },
        {
          title: "联系人电话",
          key: "contactPhone",
          width: 200
        }
      ],
      currSupplierId: ""
    };
  },
  mounted() {
    this.searchSupplier();
  },

  methods: {
    validateSearch: function(e) {
      this.searchSupplier();
    },
    searchSupplier() {
      var self = this;
      util.ajax
        .post("/supplier/search", { search: this.searchSupplierVal })
        .then(function(response) {
          self.supplierData = response.data;
          self.addSupplier();
        })
        .catch(function(error) {
          util.errorProcessor(self, error);
        });
    },
    addSupplier() {
      this.currSupplierId = "";
      this.$refs.supplierTable.clearCurrentRow();
    },
    delSupplier() {
      var row = this.$refs.supplierTable.getSelection();
      if (row && row.length > 0) {
        var self = this;
        self.$Modal.confirm({
          title: "删除确认",
          content: "是否确认删除选择的供应商信息？",
          onOk: () => {
            var delArr = [];
            for (var i = 0; i < row.length; i++) {
              delArr.push(row[i].id);
            }
            util.ajax
              .post("/supplier/remove", { ids: delArr })
              .then(function(response) {
                self.searchSupplier();
              })
              .catch(function(error) {
                util.errorProcessor(self, error);
              });
          },
          onCancel: () => {}
        });
      } else {
        this.$Message.warning("请选择一个供应商后操作");
      }
    },

    chooseItem(row) {
      this.currSupplierId = row.id;
    },

    supplierSaveOk(data, action) {
      if (action === "edit") {
        let self = this;
        this.supplierData.forEach((item, index) => {
          if (item.id == data.id) {
            self.$set(self.supplierData, index, data);
          }
        });
      } else {
        this.searchSupplier();
      }
    }
  }
};
</script>

<style >
</style>

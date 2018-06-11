<style lang="less">
@import "../../styles/common.less";
@import "./goods-list.less";
</style>

<template>
  <Row class="goods-list" :gutter="10">
          <i-col :span="showSider ? '4' : '0'">
            <goods-category :is-sider="true" v-show="showSider" @on-choose="categoryChoose" ></goods-category>
          </i-col>
          <i-col :span="showSider ? '20' : '24'">
              <Card class="goods-tbl">
                    <p slot="title">
                        <a href="javascript:void(0)" @click="changeSiderShow" style="margin-right: 5px;" >
                            <Icon v-if="showSider" type="chevron-left"></Icon>
                            <Icon v-else type="chevron-right"></Icon>
                        </a>
                        <Icon type="bag"></Icon>
                        商品列表
                    </p>
                    <div slot="extra">
                        <Button type="success" icon="plus" @click="createGoods">新增商品</Button>
                    </div>
                    <Row type="flex" class="row-margin-bottom" :gutter="5">
                        <i-col span="6">
                        <Input type="text" v-model="searchValue" placeholder="商品名称/拼音/编号" icon="search" @on-click="refreshGoodsList"/>
                        </i-col>
                        <i-col span="3">
                        <goods-brand-select v-model="searchGoodsBrandId" @on-change="refreshGoodsList"></goods-brand-select>
                        </i-col>
                        <i-col span="3">
                        <Select v-model="searchEnable" placeholder="状态"  @on-change="refreshGoodsList">
                            <Option v-for="item in enableList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                        </Select>
                        </i-col>
                        <i-col span="6">
                        <supplier-select v-model="searchSupplierId"  @on-change="refreshGoodsList"></supplier-select>
                        </i-col>
                        <i-col span="6">
                        <factory-select v-model="searchFactoryId"  @on-change="refreshGoodsList"></factory-select>
                        </i-col>
                    </Row>

                    <Table stripe highlight-row :loading="tableLoading" 
                            :columns="tableCulumns" :data="tableData" ref="goodsTable" 
                            size="small" class="table-action">
                    </Table>
                    <Row type="flex" justify="end" class="margin-top-10">
                        <Page :total="totalCount" :current="currentPage" :page-size="pageSize" @on-change="changePage" size="small" show-total></Page>
                    </Row>

                </Card>
          </i-col>

          <Modal v-model="goodsModal" title="商品信息维护" :footerHide="true" :mask-closable="false" width="75">
              <goods-info ref="goodsInfoModal" :goodsInfoId="editId" @save-ok="goodsSaveOk" ></goods-info>
          </Modal>
          
  </Row>
</template>

<script>
import util from "@/libs/util.js";
import lodash from "lodash";
import goodsCategory from "./goods-category.vue";
import goodsBrandSelect from "@/views/selector/goods-brand-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import factorySelect from "@/views/selector/factory-select.vue";
import goodsInfo from "./goods-info.vue";
import factoryVue from "../basic-data/factory.vue";
import actionButton from "@/views/my-components/action-button.vue";

export default {
  name: "goods-list",
  components: {
    goodsCategory,
    goodsBrandSelect,
    supplierSelect,
    factorySelect,
    goodsInfo,
    actionButton
  },
  data() {
    return {
      enableList: [
        { value: "ALL", label: "全部" },
        { value: 1, label: "启用" },
        { value: 0, label: "禁用" }
      ],
      showSider: false,
      searchCategoryId: "",
      searchValue: "",
      searchGoodsBrandId: "",
      searchSupplierId: "",
      searchFactoryId: "",
      searchEnable: "",
      goodsModal: false,
      tableLoading: false,
      tableData: [],
      num: 0,
      defaultAttr: [], //存放默认属性
      tableCulumns: [
        {
          type: "selection",
          width: 60
        },
        {
          title: "商品编码/名称",
          type: "name",
          width: 250,
          render: (h, params) => {
            let actions = [
              {
                type: "primary",
                icon: "edit",
                label: "修改",
                data: params.row,
                action: this.updateGoods,
                param: params.row.id
              },
              {
                type: "success",
                icon: "ios-copy",
                label: "复制",
                data: params.row,
                action: this.copyGoods,
                param: params.row
              },
              {
                type: "error",
                icon: "ios-trash",
                label: "删除",
                data: params.row,
                action: this.removeGoods,
                param: params.row
              }
            ];
            return h("div", [
              h(
                "h5",
                {
                  style: {
                    color: "#9ea7b4",
                    fontSize: "12px"
                  }
                },
                params.row.goodsNo
              ),
              h("h4", params.row.name),
              h(actionButton, {
                class: { rowAction: true },
                props: {
                  data: actions
                }
              })
            ]);
          }
        },
        {
          title: "多规格",
          key: "specCount",
          width: 90,
          render: (h, params) => {
            let useSpec = params.row.useSpec;
            if (!useSpec) {
              let specDesc = params.row.specDesc;
              return h("span", specDesc);
            } else {
              return h("span", "多规格共" + params.row.detailsSize + "种");
            }
          }
        },
        {
          title: "单位",
          key: "unitName",
          width: 70
        },
        {
          title: "是否可用",
          key: "enable",
          width: 120,
          render: (h, params) => {
            let label = params.row.enable ? "启用" : "禁用";
            let color = params.row.enable ? "green" : "red";
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
          title: "批发价",
          key: "batchPrice",
          width: 100
        },
        {
          title: "市场价",
          key: "retailPrice",
          width: 100
        },
        {
          title: "参考进货价",
          key: "inPrice",
          width: 100
        },
        {
          title: "生产企业",
          key: "factoryName",
          width: 180
        },
        {
          title: "供应商",
          key: "supplierName",
          width: 180
        },
        {
          title: "分类",
          key: "categoryName",
          width: 100
        },
        {
          title: "品牌",
          key: "brandName",
          width: 100
        }
      ],
      totalCount: 0,
      currentPage: 1,
      pageSize: 20,
      editId: ""
    };
  },
  mounted() {
    this.getDefaultAttr();
    this.refreshGoodsList();
  },
  watch: {
    searchCategoryId: function(data) {
      this.refreshGoodsList();
    },
    searchValue: lodash.debounce(function() {
      this.refreshGoodsList();
    }, 800)
  },
  methods: {
    changeSiderShow() {
      this.showSider = !this.showSider;
    },
    categoryChoose(categoryId) {
      this.searchCategoryId = categoryId;
    },
    getDefaultAttr() {
      util.ajax
        .get("/goods/defaultAttr")
        .then(response => {
          this.defaultAttr = response.data;
          for (var i = 0; i < this.defaultAttr.length; i++) {
            this.tableCulumns.push({
              title: this.defaultAttr[i].attName,
              key: "attributeRefs",
              width: 100,
              render: (h, params) => {
                const data = params.row.attributeRefs;
                var text = "";
                if (data) {
                  //console.log("data.length--"+data.length);
                  //console.log("data.title----"+params.column.title);
                  for (var m = 0; m < data.length; m++) {
                    if (data[m].attName == params.column.title) {
                      text = data[m].attValue;
                      // console.log("m---"+m+"text----"+text);
                    }
                  }
                }
                //console.log("text----"+text);
                //console.log(data && data[0] ? data[0].attValue : "");
                return h("span", text);
              }
            });
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    refreshGoodsList() {
      let reqData = {
        categoryId: this.searchCategoryId,
        brandId: this.searchGoodsBrandId,
        supplierId: this.searchSupplierId,
        factoryId: this.searchFactoryId,
        enable: this.searchEnable === "ALL" ? "" : this.searchEnable,
        search: this.searchValue,
        page: this.currentPage,
        pageSize: this.pageSize
      };
      this.tableLoading = true;
      util.ajax
        .post("/goods/list", reqData)
        .then(response => {
          console.log(response.data);
          this.tableLoading = false;
          this.tableData = response.data.data;
          this.totalCount = response.data.count;
        })
        .catch(error => {
          this.tableLoading = false;
          util.errorProcessor(this, error);
        });
    },
    changePage(data) {
      this.currentPage = data;
      this.refreshGoodsList();
    },
    createGoods() {
      this.$refs.goodsInfoModal.addViewOpen();
      this.goodsModal = true;
    },
    goodsSaveOk() {
      this.refreshGoodsList();
      this.goodsModal = false;
    },

    updateGoods(id) {
      this.editId = ""; //重置，可以导致再次点击时能刷新数据
      if (!id) {
        return;
      }
      this.editId = id;
      this.goodsModal = true;
    },
    copyGoods(row) {
      if (!row.id) {
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "商品复制确认",
        content: "是否确认以商品：" + row.name + "为模板复制多一个产品？",
        onOk: () => {
          self.tableLoading = true;
          util.ajax
            .put("/goods/copy/" + row.id)
            .then(response => {
              self.tableLoading = false;
              self.$Message.success("商品复制成功");
              self.refreshGoodsList();
            })
            .catch(error => {
              self.tableLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    },
    removeGoods(row) {
      if (!row.id) {
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "删除商品确认",
        content: "是否确认删除商品：" + row.name,
        onOk: () => {
          self.tableLoading = true;
          util.ajax
            .delete("/goods/remove/" + row.id)
            .then(response => {
              self.tableLoading = false;
              self.$Message.success("商品删除成功");
              self.refreshGoodsList();
            })
            .catch(error => {
              self.tableLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    }
  }
};
</script>


<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <FontIcon type="icon-caiwu-xianxing"></FontIcon>
                商品价格维护
            </p>
            <div slot="extra"></div>

            <Row type="flex" justify="start" class="row-margin-bottom">
                <goods-category-select v-model="searchCategoryId" @on-change="refreshGoodsList" style="width: 230px"></goods-category-select>
                <Input type="text" v-model="searchValue" placeholder="商品名称/拼音/编号" icon="search" @on-click="refreshGoodsList" style="width: 250px; margin-left:10px; margin-right:10px;"/>
                <goods-brand-select v-model="searchGoodsBrandId" @on-change="refreshGoodsList" style="width: 150px; margin-left:10px; margin-right:10px;"></goods-brand-select>
                <Select v-model="searchEnable" placeholder="状态"  @on-change="refreshGoodsList" style="width: 90px; margin-right:10px;">
                    <Option v-for="item in enableList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                </Select>
                <supplier-select v-model="searchSupplierId"  @on-change="refreshGoodsList" style="width: 230px"></supplier-select>
            </Row>

            <Table stripe highlight-row :loading="tableLoading" 
                    :columns="tableCulumns" :data="tableData" ref="goodsTable" 
                    style="width: 100%;" size="small">
            </Table>
            <Row type="flex" justify="end">
                <Page :total="totalCount" :current="currentPage" @on-change="changePage" size="small" show-total></Page>
            </Row>
        </Card>

        <Modal v-model="levelModal" title="商品等级/指定价设置" :footerHide="true" :mask-closable="false" width="55">
             <goods-price-rule ref="goodsPriceRule" :detailIds="setDetailIds" toPane="levelPrice" :baseBatchPrice="setBaseBatchPrice" :baseRetailPrice="setBaseRetailPrice"></goods-price-rule>            
        </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import lodash from "lodash";
import goodsCategorySelect from "@/views/selector/goods-category-select.vue";
import goodsBrandSelect from "@/views/selector/goods-brand-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import goodsSpecPrice from "./goods-spec-price.vue";
import goodsPriceRule from "./goods-price-rule.vue";

export default {
  name: "goods-price",
  components: {
    goodsCategorySelect,
    goodsBrandSelect,
    supplierSelect,
    goodsSpecPrice,
    goodsPriceRule
  },
  data() {
    return {
      enableList: [
        { value: "ALL", label: "全部" },
        { value: 1, label: "启用" },
        { value: 0, label: "禁用" }
      ],
      searchCategoryId: "",
      searchValue: "",
      searchGoodsBrandId: "",
      searchEnable: "",
      searchSupplierId: "",
      currentPage: 1,
      pageSize: 30,
      totalCount: 0,
      tableLoading: false,
      tableData: [],
      tableCulumns: [
        {
          title: "#",
          type: "expand",
          width: 50,
          render: (h, params) => {
            let self = this;
            let details =
              params.row.goodsDetails && params.row.goodsDetails.length > 0
                ? params.row.goodsDetails
                : [];
            return h(goodsSpecPrice, {
              props: {
                infoId: params.row.id,
                details: details,
                infoBatchPrice: params.row.batchPrice
                  ? params.row.batchPrice
                  : 0,
                infoRetailPrice: params.row.retailPrice
                  ? params.row.retailPrice
                  : 0,
                infoInPrice: params.row.inPrice ? params.row.inPrice : 0
              },
              on: {
                change: (
                  newDetails,
                  newBatchPrice,
                  newRetailPrice,
                  newInPrice
                ) => {
                  let row = params.row;
                  row.goodsDetails = newDetails;
                  row.batchPrice = newBatchPrice;
                  row.retailPrice = newRetailPrice;
                  row.inPrice = newInPrice;
                }
              }
            });
          }
        },
        {
          title: "序号",
          type: "index",
          width: 60
        },
        {
          title: "商品名称",
          type: "name",
          minWidth: 200,
          render: (h, params) => {
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
              h("h4", params.row.name)
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
          width: 100
        },
        {
          title: "是否可用",
          key: "enable",
          minWidth: 120,
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
          title: "基础批发价",
          key: "batchPrice",
          minWidth: 150,
          render: (h, params) => {
            let self = this;
            let useSpec = params.row.useSpec;
            if (useSpec) {
              return h("span", params.row.batchPrice);
            } else {
              return h("Input", {
                props: {
                  // type: 'number',
                  number: true,
                  value: self.tableData[params.index][params.column.key]
                },
                style: {
                  width: "100%"
                },
                on: {
                  "on-blur"(event) {
                    let row = self.tableData[params.index];
                    let oldValue = parseFloat(row.batchPrice);
                    let newValue = parseFloat(event.target.value);
                    row[params.column.key] = event.target.value;
                    if (newValue < 0 || isNaN(newValue)) {
                      row[params.column.key] = 0;
                    }
                    if (oldValue !== newValue && !isNaN(newValue)) {
                      self.updateBasePrice("batchPrice", row.id, newValue);
                    }
                  }
                }
              });
            }
          }
        },
        {
          title: "基础市场价",
          key: "retailPrice",
          minWidth: 150,
          render: (h, params) => {
            let self = this;
            let useSpec = params.row.useSpec;
            if (useSpec) {
              return h("span", params.row.retailPrice);
            } else {
              return h("Input", {
                props: {
                  // type: 'number',
                  number: true,
                  value: self.tableData[params.index][params.column.key]
                },
                style: {
                  width: "100%"
                },
                on: {
                  "on-blur"(event) {
                    let row = self.tableData[params.index];
                    let oldValue = parseFloat(row.retailPrice);
                    let newValue = parseFloat(event.target.value);
                    row[params.column.key] = event.target.value;
                    if (newValue < 0 || isNaN(newValue)) {
                      row[params.column.key] = 0;
                    }
                    if (oldValue !== newValue && !isNaN(newValue)) {
                      self.updateBasePrice("retailPrice", row.id, newValue);
                    }
                  }
                }
              });
            }
          }
        },
        {
          title: "参考进货价",
          key: "inPrice",
          minWidth: 150,
          render: (h, params) => {
            let self = this;
            let useSpec = params.row.useSpec;
            if (useSpec) {
              return h("span", params.row.inPrice);
            } else {
              return h("Input", {
                props: {
                  // type: 'text',
                  number: true,
                  value: self.tableData[params.index][params.column.key]
                },
                style: {
                  width: "100%"
                },
                on: {
                  "on-blur"(event) {
                    let row = self.tableData[params.index];
                    let oldValue = parseFloat(row.inPrice);
                    let newValue = parseFloat(event.target.value);
                    row[params.column.key] = event.target.value;
                    if (newValue < 0 || isNaN(newValue)) {
                      row[params.column.key] = 0;
                    }
                    if (oldValue !== newValue && !isNaN(newValue)) {
                      self.updateBasePrice("inPrice", row.id, newValue);
                    }
                  }
                }
              });
            }
          }
        },
        {
          title: "等级价/指定价",
          key: "setFixedPrice",
          width: 120,
          render: (h, params) => {
            let self = this;
            let useSpec = params.row.useSpec;
            if (useSpec) {
              return "";
            } else {
              return h(
                "a",
                {
                  attrs: {
                    href: "javascript:void(0)"
                  },
                  on: {
                    click: () => {
                      self.openLevelModal(params.row);
                    }
                  }
                },
                "去设置"
              );
            }
          }
        },
        {
          title: "供应商",
          key: "supplierName",
          minWidth: 170
        },
        {
          title: "分类/品牌",
          key: "brandName",
          minWidth: 150,
          render: (h, params) => {
            let categoryName = params.row.categoryName;
            let brandName = params.row.brandName;
            return h("div", [h("h5", categoryName), h("h5", brandName)]);
          }
        }
      ],
      levelModal: false,
      setDetailIds: [],
      setBaseBatchPrice: 0,
      setBaseRetailPrice: 0
    };
  },
  mounted() {
    this.refreshGoodsList();
  },
  watch: {
    searchValue: lodash.debounce(function() {
      this.refreshGoodsList();
    }, 800)
  },
  methods: {
    changePage(data) {
      this.currentPage = data;
      this.refreshGoodsList();
    },
    refreshGoodsList() {
      let reqData = {
        categoryId: this.searchCategoryId,
        brandId: this.searchGoodsBrandId,
        supplierId: this.searchSupplierId,
        enable: this.searchEnable === "ALL" ? "" : this.searchEnable,
        search: this.searchValue,
        page: this.currentPage,
        pageSize: this.pageSize,
        includeDetail: true
      };
      this.tableLoading = true;
      util.ajax
        .post("/goods/list", reqData)
        .then(response => {
          this.tableLoading = false;
          let data = response.data.data;
          this.totalCount = response.data.count;
          for (let i = 0; i < data.length; i++) {
            if (!data[i].useSpec) {
              data[i]._disableExpand = true;
            }
          }
          this.tableData = data;
        })
        .catch(error => {
          this.tableLoading = false;
          util.errorProcessor(this, error);
        });
    },

    updateBasePrice(type, infoId, newValue) {
      console.log(
        "base price change, type:" +
          type +
          ", id:" +
          infoId +
          ", newValue:" +
          newValue
      );
      if (!infoId || isNaN(newValue) || newValue < 0) {
        return;
      }
      let reqData = {
        infoId: infoId
      };
      if (type === "batchPrice") {
        reqData.batchPrice = newValue;
      } else if (type === "retailPrice") {
        reqData.retailPrice = newValue;
      } else if (type === "inPrice") {
        reqData.inPrice = newValue;
      } else {
        this.$Message.info("参数错误");
        return;
      }
      console.log(reqData);
      util.ajax
        .put("/goods/price/update", reqData)
        .then(response => {
          this.$Message.success("价格修改成功");
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    openLevelModal(row) {
      if (!row.id) {
        return;
      }
      let detailIdList = [];
      for (let i = 0; i < row.goodsDetails.length; i++) {
        let item = row.goodsDetails[i];
        detailIdList.push(item.id);
      }
      this.setDetailIds = detailIdList;
      this.setBaseBatchPrice = row.batchPrice;
      this.setBaseRetailPrice = row.retailPrice;

      this.$nextTick(() => {
        //调用组件中的初始化数据
        this.$refs.goodsPriceRule.initData();
        this.levelModal = true;
      });
    },
    levelModalCancel() {
      this.levelModal = false;
    }
  }
};
</script>



<template>
  <div>
      <Row>
          <i-col span="10">
              <p>仓库点: {{showTitle}} </p>
          </i-col>
          <i-col span="14">
              <Row type="flex" justify="end">
                  <ButtonGroup size="small">
                        <Button type="primary" icon="search" @click="searchBtnClicked">查询</Button>
                        <Button type="success" icon="checkmark" @click="ok">确认选择</Button>
                    </ButtonGroup>
              </Row>
          </i-col>
      </Row>
        <hr size='1' style="width:100%; margin-top:5px; margin-bottom:20px;" />
        <Row>
            <Form ref="searchForm" :model="formItem" :label-width="80">
                <Row type="flex">
                    <i-col span="8" v-show="useBatchCode">
                        <FormItem label="批次号">
                            <Input v-model="formItem.batchCode" />
                        </FormItem>
                    </i-col>
                    <i-col span="8">
                        <FormItem label="商品">
                            <good-select v-model="formItem.goodsId"></good-select>
                        </FormItem>
                    </i-col>
                    <i-col span="8">
                        <FormItem label="供应商">
                            <supplier-select v-model="formItem.supplierId"></supplier-select>
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
            <Row >
                <span>已选中的商品: <strong style="color: red;">{{chooseGoods}} </strong></span> 
            </Row>

            <Table border stripe highlight-row :columns="tabColumns" :data="tabData" 
                :loading="tableLoading" height="590"
                @on-selection-change="tabSelectChange" 
                no-data-text="点击上方查询按钮查询对应数据"
                ref="repertoryTable" style="width: 100%;" size="small">
            </Table>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="tableCurrPageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
        </Row>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import goodSelect from "@/views/selector/good-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";

export default {
  name: "repertory-info-select",
  components: {
    goodSelect,
    goodsSpecTags,
    supplierSelect
  },
  props: {
    warehouse: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      showTitle: "",
      formItem: {
        goodsId: "",
        batchCode: "",
        supplierId: ""
      },
      useBatchCode: true,
      tabData: [],
      tableLoading: false,
      totalCount: 0,
      currentPage: 1,
      tableCurrPageSize: 50,
      tabCurrChooseList: []
    };
  },
  watch: {
    warehouse(data) {
      if (data) {
        this.showTitle = data.name;
        this.totalCount = 0;
        this.tabData = [];
        this.tabCurrChooseList = [];
      }
    }
  },
  computed: {
    tabColumns() {
      let tabColumns = [];
      tabColumns.push({
        type: "selection",
        width: 60
      });
      tabColumns.push({
        title: "商品名称",
        key: "goodsName",
        width: 200,
        sortable: true
      });
      tabColumns.push({
        title: "品牌",
        key: "brandName",
        width: 150,
        sortable: true,
        render: (h, params) => {
          return h(
            "span",
            params.row.goods.brandName ? params.row.goods.brandName : ""
          );
        }
      });
      if (this.useBatchCode) {
        tabColumns.push({
          title: "批次号",
          key: "batchCode",
          width: 140
        });
      }
      tabColumns.push({
        title: "产地",
        key: "origin",
        width: 120,
        render: (h, params) => {
          return h(
            "span",
            params.row.goods.origin ? params.row.goods.origin : ""
          );
        }
      });
      if (this.useBatchCode) {
        tabColumns.push({
          title: "规格",
          key: "goodsSpecs",
          width: 120,
          render: (h, params) => {
            return h(goodsSpecTags, {
              props: {
                tags: params.row.goods.goodsSpecs
                  ? params.row.goods.goodsSpecs
                  : [],
                color: "blue"
              }
            });
          }
        });
      }
      tabColumns.push({
        title: "生产企业",
        key: "factoryName",
        align: "center",
        width: 120,
        render: (h, params) => {
          return h(
            "span",
            params.row.goods.factoryName ? params.row.goods.factoryName : ""
          );
        }
      });
      tabColumns.push({
        title: "供应商",
        key: "supplierName",
        width: 200
      });
      tabColumns.push({
        title: "单位",
        key: "unitName",
        width: 100,
        render: (h, params) => {
          return h(
            "span",
            params.row.goods.unitName ? params.row.goods.unitName : ""
          );
        }
      });
      tabColumns.push({
        title: "库存数量",
        key: "quantity",
        width: 120
      });
      tabColumns.push({
        title: "当前在单数",
        key: "onWayQuantity",
        width: 120
      });
      if (this.useBatchCode) {
        tabColumns.push({
          title: "库位",
          key: "location",
          width: 120
        });
        tabColumns.push({
          title: "有效期至",
          key: "expDate",
          width: 120,
          render: (h, params) => {
            return h(
              "span",
              params.row.expDate
                ? moment(params.row.expDate).format("YYYY-MM-DD")
                : ""
            );
          }
        });
        tabColumns.push({
          title: "生产日期",
          key: "productDate",
          width: 120,
          render: (h, params) => {
            return h(
              "span",
              params.row.productDate
                ? moment(params.row.productDate).format("YYYY-MM-DD")
                : ""
            );
          }
        });
      }
      return tabColumns;
    },

    chooseGoods() {
      if (!this.tabCurrChooseList || this.tabCurrChooseList.length <= 0) {
        return "";
      } else {
        let label = "";
        for (let i = 0; i < this.tabCurrChooseList.length; i++) {
          let item = this.tabCurrChooseList[i];
          if (item.batchCode) {
            label = label + item.goodsName + "[" + item.batchCode + "]; ";
          } else {
            label = label + item.goodsName + "; ";
          }
        }
        return label;
      }
    }
  },
  methods: {
    searchBtnClicked(ubc) {
      if (ubc != undefined) {
        this.useBatchCode = ubc;
      } else {
        this.useBatchCode = true;
      }
      let reqData = {
        warehouseId: this.warehouse.id,
        goodsId: this.formItem.goodsId,
        batchCode: this.formItem.batchCode,
        supplierId: this.formItem.supplierId,
        minQuantity: 0,
        useBatchCode: this.useBatchCode,
        page: this.currentPage,
        size: this.tableCurrPageSize
      };

      this.tableLoading = true;
      util.ajax
        .post("/repertory/select", reqData)
        .then(response => {
          this.tableLoading = false;
          this.totalCount = response.data.count;
          this.tabData = response.data.data;
        })
        .catch(error => {
          this.tableLoading = false;
          util.errorProcessor(this, error);
        });
    },
    pageChange(data) {
      this.currentPage = data;
      this.searchBtnClicked();
    },
    tabSelectChange(data) {
      this.tabCurrChooseList = data;
    },

    ok() {
      this.$emit("on-choosed", this.tabCurrChooseList);
    }
  }
};
</script>



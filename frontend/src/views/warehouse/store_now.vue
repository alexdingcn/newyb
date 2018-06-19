<style lang="less">
@import "../../styles/common.less";
@import "./store_now.less";
</style>

<template>
<Row>
    <Card>
        <p slot="title" >
            <Icon type="document"></Icon> 实时库存查询
        </p>

        <div slot="extra">
            <ButtonGroup class="padding-left-20">
                <Button type="primary" icon="android-search" @click="queryRepertoryList" :loading="saving">查询</Button>
            </ButtonGroup>
        </div>

        <Form :label-width="80" :model="storeNow" ref="storeNowForm">
          <Row class="row-margin-bottom">
            <i-col span="6">
              <FormItem label="仓库点" prop="warehouseId">
                <warehouse-select v-model="storeNow.warehouseId"></warehouse-select>
              </FormItem>
            </i-col>

            <i-col span="4">
              <FormItem label="库区" >
                <Select v-model="storeNow.store_state" prop="store_state">
                  <Option v-for="option in storeOptions" :value="option.id" :label="option.name" :key="option.id">
                  {{option.name}}
                  </Option>
                </Select>
              </FormItem>
            </i-col>
            <i-col span="4">
              <FormItem label="库存状态" >
                <Select v-model="storeNow.counter_state" prop="counter_state">
                  <Option v-for="option in counterOptions" :value="option.id" :label="option.name" :key="option.id">
                  {{option.name}}
                  </Option>
                </Select>
              </FormItem>
            </i-col>
            <i-col span="4">
              <FormItem label="可售状态" >
                <Select v-model="storeNow.sale_state" prop="sale_state">
                  <Option v-for="option in saleOptions" :value="option.id" :label="option.name" :key="option.id">
                  {{option.name}}
                  </Option>
                </Select>
              </FormItem>
            </i-col>
            <i-col span="4">
              <FormItem label="零库存品种" >
                <checkbox v-model="storeNow.is_zero_store"/>显示
              </FormItem>
            </i-col>
            <i-col span="1">
              <Button type="text" :icon="showExpandedFilter ? 'android-arrow-dropup-circle' : 'android-arrow-dropdown-circle'" @click="showExpandedFilter = !showExpandedFilter"></Button>
            </i-col>
          </Row>

          <Row class="row-margin-bottom" v-show="showExpandedFilter">
              <i-col span="6">
                  <FormItem label="供应商" prop="supplierId">
                      <supplier-select v-model="storeNow.supplierId" ></supplier-select>
                  </FormItem>
              </i-col>
              <i-col span="6">
                  <FormItem label="厂商" prop="factoryId">
                      <factory-select v-model="storeNow.factoryId" ></factory-select>
                  </FormItem>
              </i-col>
              <i-col span="5">
                  <FormItem label="采购员" prop="buyerId">
                      <buyer-select v-model="storeNow.in_user_id"></buyer-select>
                  </FormItem>
              </i-col>
              <i-col span="3">
                  <FormItem label="库龄大于" >
                      <Input v-model="storeNow.keedays" placeholder="天"/>
                  </FormItem>
              </i-col>
          </Row>

          <Row class="row-margin-bottom" v-show="showExpandedFilter">
              <i-col span="6">
              <FormItem label="商品速查" prop="goodsId">
                  <good-select v-model="storeNow.goodsId"></good-select>
              </FormItem>
              </i-col>
          </Row>

          <Table border highlight-row
              class="margin-top-8"
              :columns="repertoryColumns" :data="repertoryItems"
              ref="storeNowTable" style="width: 100%;"
              no-data-text="当前查询条件下无库存数据！">

          </Table>
          <Row class="table-footer-label">
              <i-col span="6">实时库存数量： {{storeAmount}}</i-col>
              <i-col span="6">库存商品总金额: {{storeMoney}}</i-col>
              <i-col span="6">含税价格: {{storeTaxMoney}}</i-col>
              <i-col span="6">毛利: {{storeProfitMoney}}</i-col>
          </Row>
          <Row class="margin-top-8">
              <div style="float: right;">
                  <Page :total="totalAmount" :current="currentPage" @on-change="changePage" show-total></Page>
              </div>
          </Row>
        </Form>
    </Card>
    </Row>
    </template>
<script>
import axios from "axios";
import moment from "moment";
import util from "@/libs/util.js";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import factorySelect from "@/views/selector/factory-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import goodSelect from "@/views/selector/good-select.vue";
import buyerSelect from "@/views/selector/buyer-select.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";

export default {
  name: "store_now",
  components: {
    goodsSpecTags,
    warehouseSelect,
    factorySelect,
    supplierSelect,
    goodSelect,
    buyerSelect
  },
  data() {
    return {
      saving: false,
      storeAmount: 0,
      storeMoney: 0,
      storeTaxMoney: 0,
      storeProfitMoney: 0,
      showExpandedFilter: false,
      totalAmount: 0,
      currentPage: 1,
      repertoryItems: [],
      counterOptions: [{ id: "ALL", name: "全部" }],
      storeOptions: [{ id: "ALL", name: "全部" }],
      saleOptions: [{ id: "ALL", name: "全部" }],
      storeNow: {
        warehouseId: null,
        factoryId: null
      },
      repertoryColumns: [
        {
          type: "index",
          align: "center",
          width: 30
        },
        {
          title: "商品编号",
          align: "center",
          key: "goodsNo",
          width: 180
        },
        {
          title: "商品名称",
          key: "goodsName",
          align: "center",
          width: 150
        },
        {
          title: "产地",
          key: "origin",
          align: "center",
          width: 60
        },
        {
          title: "规格",
          key: "goodsSpecs",
          width: 120,
          render: (h, params) => {
            return h(goodsSpecTags, {
              props: {
                tags: params.row.goods ? params.row.goods.goodsSpecs : "",
                color: "blue"
              }
            });
          }
        },
        {
          title: "生产企业",
          key: "factoryName",
          align: "center",
          width: 120,
          render: (h, params) => {
            return params.row.goods ? params.row.goods.factoryName : "";
          }
        },
        {
          title: "批次号",
          key: "batchCode",
          align: "center",
          width: 80
        },
        {
          title: "生产日期",
          key: "productDate",
          align: "center",
          width: 80,
          render: (h, params) => {
            return moment(params.row.eta).format("YYYY-MM-DD");
          }
        },
        {
          title: "有效日期",
          key: "expDate",
          align: "center",
          width: 80,
          render: (h, params) => {
            return moment(params.row.eta).format("YYYY-MM-DD");
          }
        },
        {
          title: "单位",
          key: "unitName",
          align: "center",
          width: 50
        },

        {
          title: "数量",
          key: "quantity",
          align: "center",
          width: 80
        },
        {
          title: "在单数",
          key: "",
          align: "center",
          width: 100
        },
        {
          title: "采购价",
          key: "buyPrice",
          align: "center",
          width: 80
        },
        // {
        //   title: "金额",
        //   key: "",
        //   align: "center",
        //   width: 80
        // },
        // {
        //   title: "税率",
        //   key: "out_tax",
        //   align: "center",
        //   width: 60
        // },
        // {
        //   title: "无税单价",
        //   key: "",
        //   align: "center",
        //   width: 60
        // },
        // {
        //   title: "无税金额",
        //   key: "",
        //   align: "center",
        //   width: 60
        // },
        // {
        //   title: "税额",
        //   key: "",
        //   align: "center",
        //   width: 60
        // },
        // {
        //   title: "销价",
        //   key: "",
        //   align: "center",
        //   width: 60
        // },
        // {
        //   title: "销价金额",
        //   key: "",
        //   align: "center",
        //   width: 60
        // },

        {
          title: "最近采购价",
          key: "buy_price",
          align: "center",
          width: 100
        },
        {
          title: "供应商",
          key: "supplierName",
          align: "center",
          width: 120
        },
        {
          title: "储存条件",
          key: "storageConditionName",
          align: "center",
          width: 100
        },
        {
          title: "仓库点",
          key: "warehouseName",
          align: "center",
          width: 100
        },
        {
          title: "货位号",
          key: "location",
          align: "center",
          width: 100
        }
      ]
    };
  },
  mounted() {
    this.queryRepertoryList();
  },
  activated() {
    // this.clearData();
  },
  watch: {
    repertoryItems: function() {}
  },
  methods: {
    moment: function() {
      return moment();
    },
    getStoreSunInfo() {},
    queryRepertoryList() {
      var self = this;
      this.repertoryItems = [];
      this.storeNow.page = this.currentPage;
      util.ajax
        .post("/repertory/list", this.storeNow)
        .then(function(response) {
          if (response.status === 200 && response.data) {
            self.repertoryItems = response.data.data;
            self.totalAmount = response.data.total;
            self.storeAmount = response.data.storeAmount;
            self.storeMoney = response.data.storeMoney;
          }
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    changePage(pageNumber) {
      this.currentPage = pageNumber;
      this.queryRepertoryList();
    }
  }
};
</script>
<style >
</style>

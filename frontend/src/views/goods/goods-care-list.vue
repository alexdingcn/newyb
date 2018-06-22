<style lang="less">
@import "../../styles/common.less";
</style>
<template>
<div>
        <Row>
            <i-col span="12">
        <Card>
            <p slot="title">
                <Icon type="person-stalker"></Icon> 商品列表
            </p>
            <div slot="extra">
                <Row type="flex" justify="end">
                    <Input style="width: 200px;" @on-change="refreshGoodList" v-model="searchValue" icon="search" placeholder="商品名/编号" />
                </Row>
            </div>
            <Table ref="goodTab" size="small" highlight-row height="800" :loading="tabLoading"
                :columns="goodColumns" :data="goodsList" @on-row-click="chooseGood" class="table-action">
            </Table>
            <Row type="flex" justify="end" class="margin-top-10">
                <Page :total="totalCount" :current="currentPage" :page-size="pageSize" @on-change="changePage" size="small" show-total></Page>
            </Row>
        </Card>
        </i-col>
        <i-col span="11" style="padding-left: 30px;">
            <Card>
            <p slot="title">
                <Icon type="person-stalker"></Icon> 待养护商品
            </p>
            <div slot="extra">
                <Row type="flex" justify="end">
                  <Date-picker type="date" placeholder="选择日期" style="width: 200px" v-model= "endNextDate" @on-change = "refreshCareList"></Date-picker>
                </Row>
            </div>
            <Table ref="careTab" size="small" highlight-row height="800" :loading="careTabLoading"
                :columns="needCareColumns" :data="needCareList" >
            </Table>
        </Card>    
        </i-col>
        </Row>
        <Modal v-model="goodsModal" title="商品养护记录" :footerHide="true" :mask-closable="false" width="800">
                <goodsCareRecord ref="carelist" :goodsId="searchId"></goodsCareRecord>
        </Modal>
        <Modal v-model="careModal" title="商品养护" :footerHide="true" :mask-closable="false" width="400">
                <goodsCare ref="carelist" :goodId="editId" :name="deitName" @save-ok="careSaveOk"></goodsCare>
        </Modal>
</div>
</template>

<script>
import util from "@/libs/util.js";
import bus from "@/libs/bus";
import goodsCareRecord from "./goods-care-record.vue";
import goodsCare from "./goods-care.vue";
import actionButton from "@/views/my-components/action-button.vue";
import moment from "moment";

export default {
  name: "goodscarelist",
  components: {
    goodsCareRecord,
    goodsCare,
    actionButton
  },
  data() {
    return {
      searchValue: "",
      currGood: {},
      tabLoading: false,
      careTabLoading: true,
      goodsList: [],
      needCareList: [],
      goodsModal: false,
      careModal: false,
      needCareColumns: [
        {
          title: "商品名称",
          key: "name",
          align: "center"
        },
        {
          title: "上次养护",
          key: "createDate",
          align: "center",
          render: (h, params) => {
            let createDate = params.row.createDate;
            return h(
              "span",
              createDate ? moment(createDate).format("YYYY-MM-DD") : ""
            );
          }
        },
        {
          title: "下次养护",
          key: "nextDate",
          align: "center",
          render: (h, params) => {
            let nextDate = params.row.nextDate;
            return h(
              "span",
              nextDate ? moment(nextDate).format("YYYY-MM-DD") : ""
            );
          }
        }
      ],
      goodColumns: [
        {
          title: "商品名称",
          key: "name",
          render: (h, params) => {
            let actions = [
              {
                type: "success",
                icon: "ios-list-outline",
                label: "养护记录",
                data: params.row,
                action: this.queryCareRecord,
                param: params.row.id
              },
              {
                type: "primary",
                icon: "android-add-circle",
                label: "添加养护",
                data: params.row,
                action: this.addCare,
                param: params.row
              }
            ];
            return h("div", [
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
          title: "商品编码",
          key: "goodsNo",
          align: "center"
        },
        {
          title: "生产企业",
          key: "fname",
          align: "center"
        }
      ],
      lastTime: 0,
      totalCount: 0,
      currentPage: 1,
      pageSize: 30,
      editId: "",
      deitName: "",
      searchId: ""
    };
  },
  watch: {
    /**goodId: function(id) {
      console.log("idididididid"+id);
    }*/
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.refreshGoodList();
      this.refreshCareList();
    },
    changePage(data) {
      this.currentPage = data;
      this.refreshGoodList();
    },
    careSaveOk() {
      this.careModal = false;
      this.init();
    },
    queryCareRecord(id) {
      this.searchId = "";
      if (!id) {
        return;
      }
      this.searchId = id;
      this.goodsModal = true;
    },
    addCare(id, name) {
      this.editName = "";
      this.editId = ""; //重置，可以导致再次点击时能刷新数据
      if (!id || !name) {
        return;
      }
      this.editId = id;
      this.editName = name;
      this.careModal = true;
    },
    refreshCareList() {
      let req = {
          nextDate : this.endNextDate,
      }
      console.log("req------"+req.nextDate);
      util.ajax
        .get("/goods_care/careList",{params: req})
        .then(response => {
          if (response.status == 200) {
            this.careTabLoading = false;
            this.needCareList = response.data.data;
          }
        })
        .catch(error => {
          this.careTabLoading = false;
          util.errorProcessor(this, error);
        });
    },
    refreshGoodList() {
      let reqData = {
        search: this.searchValue,
        page: this.currentPage,
        size: this.pageSize
      };
      this.tabLoading = true;
      var self = this;
      util.ajax
        .get("/goods_care/list", { params: reqData })
        .then(response => {
          self.tabLoading = false;
          self.goodsList = response.data.data;
          self.totalCount = response.data.count;
        })
        .catch(error => {
          this.tabLoading = false;
          util.errorProcessor(this, error);
        });
    },

    searchValueChange(event) {
      let self = this;
      self.lastTime = event.timeStamp;
      setTimeout(function() {
        if (self.lastTime - event.timeStamp === 0) {
          self.searchUserList();
        }
      }, 500);
    },
    chooseGood(data, index) {
      this.currGood = data;
      this.goodsId = this.currGood.id;
      this.$refs.carelist.goodsId = this.goodsId;
    }
  }
};
</script>

<style >
</style>

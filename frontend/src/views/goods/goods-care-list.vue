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
        <i-col span="12">
                
    </i-col>
        </Row>
        <Modal v-model="goodsModal" title="商品养护记录" :footerHide="true" :mask-closable="false" width="800">
                <goodsCareRecord ref="carelist" ></goodsCareRecord>
        </Modal>
        <Modal v-model="careModal" title="商品养护" :footerHide="true" :mask-closable="false" width="400">
                <goodsCare ref="carelist" ></goodsCare>
        </Modal>
</div>
</template>

<script>
import util from "@/libs/util.js";
import bus from "@/libs/bus";
import goodsCareRecord from "./goods-care-record.vue";
import goodsCare from "./goods-care.vue";
import actionButton from "@/views/my-components/action-button.vue";

export default {
  name: "goodscarelist",
  components: {
    goodsCareRecord,
    goodsCare,
    actionButton
  },
  props: {
    goodId: {
      type: String | Number,
      default: ""
    },
    name: {
        type: String, 
        default: ""
    }
  },
  data() {
    return {
      searchValue: "",
      currGood: {},
      tabLoading: false,
      goodsList: [],
      goodsModal: false,
      careModal: false,
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
      pageSize: 30
    };
  },
  watch: {
    goodId: function(id) {
      console.log("idididididid"+id);
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.refreshGoodList();
    },
    changePage(data) {
      this.currentPage = data;
      this.refreshGoodList();
    },
    queryCareRecord() {
      this.goodsModal = true;
    },
    addCare() {
      this.careModal = true;
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

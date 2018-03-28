<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Modal v-model="isShowModal" :width="75" :mask-closable="false" :closable="false" :title="showTitle">
          <Form ref="searchForm" :model="formItem" :label-width="100">
                <Row type="flex" justify="center">
                    <Col span="8">
                        <FormItem label="名称/拼音">
                            <Input size="small" type="text" v-model="formItem.search" clearable ></Input>
                        </FormItem>
                    </Col>
                    <Col span="8">
                        <FormItem label="厂商">
                            <factory-select size="small" v-model="formItem.factoryId" ></factory-select>
                        </FormItem>
                    </Col>
                    <Col span="2" offset="1">
                        <Button type="primary" size="small" icon="ios-search" @click="searchBtnClicked">查询</Button>
                    </Col>
                    <Col span="5"></Col>
                </Row>
            </Form>

            <Row type="flex" justify="center" align="middle" class="margin-top-10">
                <Table border highlight-row :columns="tabColumns" :data="tabData" 
                    :loading="tableLoading" height="300" 
                    @on-selection-change="tabSelectChange" 
                    no-data-text="点击上方查询按钮查询对应数据"
                    ref="goodSearchTable" style="width: 100%;" size="small">
                </Table>
            </Row>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="tableCurrPageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
            <Row type="flex" justify="center" >
                <span>已选择: <strong style="color: red;">{{choonseNumber}} </strong>种商品</span> 
            </Row>

            <div slot="footer">
                <Row >
                    <Col span="5" offset="6">
                        <Button size="small" type="primary" @click="ok" long>
                            <span >确定</span>
                        </Button>
                    </Col>
                    <Col span=5 class="padding-left-10">
                        <Button size="small" @click="closedModal" long>取消</Button>
                    </Col>
                </Row>
            </div>
      </Modal>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import factorySelect from "@/views/selector/factory-select.vue";

export default {
  name: 'sell-good-search',
  components: {
      factorySelect
  },
  props: {
        showModal: {
            type: Boolean,
            default: false
        },
        warehouse: {
            type: Object,
            required: true
        }
   },
  data() {
      return {
          isShowModal: false,
          showTitle: '',
          formItem: {
              search: '',
              factoryId: ''
          },
          tabData: [],
          tableLoading: false,
          tabColumns: [
              {
                  type: "selection",
                  width: 60,
                  fixed: "left"
              },
              {
                  title: '货号',
                  key: 'code',
                  width: 120,
                  sortable: true,
                  fixed: 'left'
              },
              {
                  title: '商品名称',
                  key: 'goodName',
                  width: 200,
                  sortable: true,
                  fixed: 'left',
                  render: (h, params) => {
                      let good = params.row.goods;
                      if (good) {
                          return h('span', good.name);
                      }else {
                          return h('span', '获取产品信息失败');
                      }
                  }
              },
              {
                  title: '剂型',
                  key: 'jx',
                  width: 100,
                  render: (h, params) => {
                      let good = params.row.goods;
                      if (good) {
                          return h('span', good.jx);
                      }else {
                          return h('span', '');
                      }
                  }
              },
              {
                  title: '规格',
                  key: 'spec',
                  width: 100,
                  render: (h, params) => {
                      let good = params.row.goods;
                      if (good) {
                          return h('span', good.spec);
                      }else {
                          return h('span', '');
                      }
                  }
              },
              {
                  title: '生产企业',
                  key: 'factoryName',
                  width: 200,
                  render: (h, params) => {
                      let good = params.row.goods;
                      if (good) {
                          return h('span', good.factory);
                      }else {
                          return h('span', '');
                      }
                  }
              },
              {
                  title: '单位',
                  key: 'unitName',
                  width: 80,
                  render: (h, params) => {
                      let good = params.row.goods;
                      if (good) {
                          return h('span', good.unitName);
                      }else {
                          return h('span', '');
                      }
                  }
              }, 
              {
                  title: '数量',
                  key: 'quantity',
                  width: 120
              },
              {
                  title: '有效期至',
                  key: 'expDate',
                  width: 120, 
                  render: (h, params) => {
                    return moment(params.row.expDate).format('YYYY-MM-DD');
                  }
              },
              {
                  title: '生产日期',
                  key: 'productDate',
                  width: 120, 
                  render: (h, params) => {
                    return moment(params.row.productDate).format('YYYY-MM-DD');
                  }
              },
              {
                  title: '批次号',
                  key: 'batchCode',
                  width: 120
              }
          ],
          totalCount: 0,
          currentPage: 1,
          tableCurrPageSize: 10,
          tabCurrChooseList: [],
          choonseNumber: 0,
      }
  },
  watch: {
        showModal(data) {
            if(data) {
                this.isShowModal = data;
            }
        },
        warehouse(data) {
            if(data) {
                 this.showTitle = "选取仓库:" + data.name + "商品";
                this.initData();
            }
        },
  },
  methods: {
      dateFormat(data) {
          if (!data && isNaN(data)) {
              return '';
          }
          return dataConver.formatDate(new Date(data), 'yyyy-MM-dd');
        },
      initData() {
          this.totalCount = 0;
          this.tabData = [];
      },
      searchBtnClicked() {
          let reqData = {
              warehouseId: this.warehouse.id,
              goodSearch: this.formItem.search,
              factoryId: this.formItem.factoryId,
              page: this.currentPage,
              size: this.tableCurrPageSize
          };
          util.ajax.get("/repertory/list", {params: reqData})
            .then((response) => {
                this.totalCount = response.data.total;
                this.tabData = response.data.data;
            })
            .catch((error) => {
                util.errorProcessor(this, error);
            });
      },
      pageChange(data) {
          this.currentPage = data;
          this.searchBtnClicked();
      },
      tabSelectChange(data) {
          this.tabCurrChooseList = data;
          if (this.tabCurrChooseList) {
              this.choonseNumber = this.tabCurrChooseList.length;
          }else {
              this.choonseNumber = 0;
          }
      },

      ok() {
            this.isShowModal = false;
            this.$emit('modal-close');
            this.$emit('choosed', this.tabCurrChooseList);
      },

      closedModal() {
          this.isShowModal = false;
          this.$emit('modal-close');
      },
      
  }
}
</script>

<style>
.ivu-form-item {
    margin-bottom: 5px;
}

</style>


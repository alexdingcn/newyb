<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Modal v-model="isShowModal" :width="60" :mask-closable="false" :closable="false" title="查询获取商品">
          <Form ref="searchForm" :model="formItem" :label-width="100">
                <Row type="flex" justify="center">
                    <Col span="8">
                        <FormItem label="商品分组">
                            <Select v-model="formItem.catId" clearable >
                                <Option v-for="item in categorys" :value="item.id" :key="item.id">{{ item.name }}</Option>
                            </Select>
                        </FormItem>
                    </Col>
                    <Col span="8">
                        <FormItem label="名称/拼音">
                            <Input type="text" v-model="formItem.search" clearable ></Input>
                        </FormItem>
                    </Col>
                    <Col span="8">
                        <FormItem label="厂商">
                            <factory-select v-model="formItem.factoryId"></factory-select>
                        </FormItem>
                    </Col>
                </Row>
                <Row type="flex" justify="center" >
                    <Button type="primary" size="small" icon="ios-search" @click="searchBtnClicked">查询</Button>
                </Row>
            </Form>

            <Row type="flex" justify="center" align="middle" class="margin-top-20">
                <Table border highlight-row :columns="tabColumns" :data="tabData" 
                    :loading="tableLoading" 
                    @on-row-click="tableRowClick" 
                    ref="goodSearchTable" style="width: 100%;" size="small">
                </Table>
            </Row>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="tableCurrPageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
            <Row type="flex" justify="center" align="middle">
                <div>当前选择的商品: <strong style="color: red;">{{currChooseShow}}</strong></div>
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
import factorySelect from "@/views/factory/factory-select.vue";

export default {
  name: 'good-search',
  components: {
      factorySelect
  },
  props: {
        showModal: {
            type: Boolean,
            default: false
        }
   },
  data() {
      return {
          isShowModal: false,
          formItem: {
              catId: '',
              search: '',
              factoryId: ''
          },
          categorys: [],
          tabData: [],
          tableLoading: false,
          tabColumns: [
              {
                  title: '货号',
                  key: 'code',
                  align: 'center'
              },
              {
                  title: '商品名称',
                  key: 'name',
                  align: 'center',
                  sortable: true,
              },
              {
                  title: '类别',
                  key: 'categoryName',
                  align: 'center'
              },
              {
                  title: '剂型',
                  key: 'jx',
                  align: 'center'
              },
              {
                  title: '规格',
                  key: 'spec',
                  align: 'center'
              },
              {
                  title: '生产企业',
                  key: 'factory',
                  align: 'center'
              }
          ],
          totalCount: 0,
          currentPage: 1,
          tableCurrPageSize: 10,
          currChooseShow: '',
          currChooseItem: null
      }
  },
  watch: {
        showModal(data) {
            if(data) {
                this.isShowModal = data;
                this.initData();
            }
        },
        currChooseItem(data) {
            if (!data || data === null) {
                this.currChooseShow = '';
            }else {
                this.currChooseShow = data.name + " " + (data.code ? data.code : '');
            }
        }
  },
  methods: {
      initData() {
          this.getGoodCategoryList();
      },
      getGoodCategoryList() {
          util.ajax.get("/good/category/list")
            .then((response) => {
                this.categorys = response.data;
            })
            .catch((error) => {
                util.errorProcessor(this, error);
            });
      },
      searchBtnClicked() {
          let reqData = {
              catId: this.formItem.catId,
              search: this.formItem.search,
              factoryId: this.formItem.factoryId,
              page: this.currentPage,
              size: this.tableCurrPageSize
          };
          util.ajax.get("/goods/list", {params: reqData})
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
      tableRowClick(data) {
          this.currChooseItem = data;
      },

      ok() {
            this.isShowModal = false;
            this.$emit('modal-close');
            this.$emit('choosed', this.currChooseItem);
      },

      closedModal() {
          this.isShowModal = false;
          this.$emit('modal-close');
      }
  }
}
</script>

<style>
.ivu-form-item {
    margin-bottom: 5px;
}

</style>


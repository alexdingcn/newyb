
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
            <Form ref="searchForm" :model="formItem" :label-width="100">
                <Row type="flex" justify="center">
                    <i-col span="8">
                        <FormItem label="商品">
                            <good-select v-model="formItem.goodsId"></good-select>
                        </FormItem>
                    </i-col>
                    <i-col span="8">
                        <FormItem label="批次号">
                            <Input v-model="formItem.batchCode" />
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

            <Table border highlight-row :columns="tabColumns" :data="tabData" 
                :loading="tableLoading" height="350" 
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
import moment from 'moment';
import goodSelect from "@/views/selector/good-select.vue";
import supplierSelect from '@/views/selector/supplier-select.vue';
import goodsSpecTags from '../goods/goods-spec-tabs.vue';

export default {
  name: 'repertory-info-select',
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
          showTitle: '',
          formItem: {
              goodsId: '',
              batchCode: '',
              supplierId: ''
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
                  key: 'goodsId',
                  width: 120,
                  sortable: true
              },
              {
                  title: '商品名称',
                  key: 'goodsName',
                  width: 200,
                  sortable: true
              },
              {
                  title: '批次号',
                  key: 'batchCode',
                  width: 140
              },
              {
                  title: '产地',
                  key: 'origin',
                  width: 120
              },
                {
                    title: '规格',
                    key: 'goodsSpecs',
                    width: 120,
                    render: (h, params) =>　{
                        return h(goodsSpecTags, {
                            props: {
                                tags: params.row.goods.goodsSpecs ? params.row.goods.goodsSpecs : [],
                                color: 'blue'
                            }
                        });
                    }
                },
                {
                    title: '生产企业',
                    key: 'factoryName',
                    align: 'center',
                    width: 120,
                    render: (h, params) => {
                        return h('span', params.row.goods.factoryName ? params.row.goods.factoryName : '');
                    }
                },
              {
                  title: '供应商',
                  key: 'supplierName',
                  width: 200
              },
              {
                  title: '单位',
                  key: 'unitName',
                  width: 100
              },
              {
                  title: '库存数量',
                  key: 'quantity',
                  width: 120
              },
              {
                  title: '当前在单数',
                  key: 'onWayQuantity',
                  width: 120,
              },
              {
                  title: '有效期至',
                  key: 'expDate',
                  width: 120, 
                  render: (h, params) => {
                    return h('span', params.row.expDate ? moment(params.row.expDate).format('YYYY-MM-DD') : '');
                  }
              },
              {
                  title: '生产日期',
                  key: 'productDate',
                  width: 120, 
                  render: (h, params) => {
                    return h('span', params.row.productDate ? moment(params.row.productDate).format('YYYY-MM-DD') : '');
                  }
              },
              {
                  title: '批准文号',
                  key: 'permitNo',
                  width: 140
              },
              {
                  title: '存储条件',
                  key: 'storageConditionName',
                  width: 120
              }
          ],
          totalCount: 0,
          currentPage: 1,
          tableCurrPageSize: 50,
          tabCurrChooseList: [],
          currAttributes: [],
      }
  },
  watch: {
        warehouse(data) {
            if(data) {
                 this.showTitle = data.name;
                 this.totalCount = 0;
                 this.tabData = [];
                 this.tabCurrChooseList = [];
            }
        }
  },
  computed: {
      chooseGoods() {
          if (!this.tabCurrChooseList || this.tabCurrChooseList.length <= 0) {
              return "";
          }else {
              let label = "";
              for (let i=0; i<this.tabCurrChooseList.length; i++) {
                  let item = this.tabCurrChooseList[i];
                  label = label + item.goodsName + '[' + item.batchCode + ']; ';
              }
              return label;
          }
      }
  },
  methods: {
      searchBtnClicked() {
          let reqData = {
              warehouseId: this.warehouse.id,
              goodsId: this.formItem.goodsId,
              batchCode: this.formItem.batchCode,
              supplierId: this.formItem.supplierId,
              minQuantity: 0,
              page: this.currentPage,
              size: this.tableCurrPageSize,
              currAttributes:this.currAttributes,
          };
          util.ajax.post("/repertory/select", reqData)
            .then((response) => {
                this.totalCount = response.data.count;
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
      },

      ok() {
          this.$emit('on-choosed', this.tabCurrChooseList);
      }
      
  }
}
</script>

<style scoped>
.ivu-form-item {
    margin-bottom: 5px;
}

</style>


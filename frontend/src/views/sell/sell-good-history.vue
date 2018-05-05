<template>
    <div>
        <Table ref="table" :columns="tabColumns" :data="tabData" :loading="loading"
            border highlight-row size="small" style="width: 100%;" 
        >
        </Table>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';

export default {
  name: 'sell-good-history',
  props: {
      excludedOrderId: Number|String,
      customerId: Number|String,
      goodsId: Number|String
  },
  data() {
      return {
          details: [],
          tabData: [],
          loading: false,
          tabColumns: [
              {
                  title: '序号',
                  type: 'index',
                  width: 80,
              },
              {
                  title: '商品',
                  key: 'goodsName',
                  width: 200
              },
              {
                  title: '生产厂家',
                  key: 'factoryName',
                  width: 180
              },
              {
                  title: '批次号',
                  key: 'batchCode',
                  width: 180
              },
              {
                  title: '产地',
                  key: 'origin',
                  width: 150,
              },
              {
                  title: '剂型',
                  key: 'jx',
                  width: 150
              },
              {
                  title: '规格',
                  key: 'spec',
                  width: 150
              },
              {
                  title: '制单日',
                  key: 'createOrderDate',
                  width: 150,
                  render: (h, params) => {
                      return moment(params.row.createOrderDate).format('YYYY-MM-DD');
                  }
              },
              {
                  title: '销售员',
                  key: 'salerNickName',
                  width: 170,
                  render: (h, params) => {
                      let nickName = params.row.saleNickName;
                      let realName = params.row.saleRealName;
                      if (realName) {
                          return h('span', nickName + ' - [' + realName + ']');
                      }else {
                          return h('span', nickName);
                      }
                  }
              },
              {
                  title: '数量',
                  key: 'quantity',
                  width: 120
              },
              {
                  title: '实价',
                  key: 'realPrice',
                  width: 120
              },
              {
                  title: '定价',
                  key: 'fixPrice',
                  width: 120
              },
              {
                  title: '折扣',
                  key: 'disPrice',
                  width: 120
              },
              {
                  title: '金额',
                  key: 'amount',
                  width: 120
              },
              {
                  title: '批准文号',
                  key: 'permitNo',
                  width: 170
              },
              {
                  title: '存储条件',
                  key: 'storageConditionName',
                  width: 150
              }
          ]
      }
  },
  watch: {
      goodsId(goodsId) {
          this.refsetData();
      },
  },
  methods: {
      refsetData() {
          if (!this.goodsId || !this.customerId) {
              this.tabData = [];
          }
          let reqData = {
              customerId: this.customerId,
              goodsId: this.goodsId
          }
          this.loading = true;
          util.ajax.get('/sell/detail/history', {params: reqData})
            .then((response) => {
                this.loading = false;
                this.details = response.data;
                this.setShowTableData(this.details);
            })
            .catch((error) => {
                this.loading = false;
                util.errorProcessor(this, error);
            })
      },
      setShowTableData(details) {
          this.tabData = [];
          for (let i=0; i < details.length; i++) {
              let detail = details[i];
              if (this.excludedOrderId === detail.sellOrderId) {
                  continue; //排除
              }
              this.tabData.push(detail);
          }
      }
  }
}
</script>
<style>

.ivu-table td {
    height: 2.5em;
}

</style>



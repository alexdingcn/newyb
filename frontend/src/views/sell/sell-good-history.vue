<template>
    <div>
        <Table ref="table" :columns="tabColumns" :data="tabData" 
        border highlight-row size="small" style="width: 100%;" 
        >
        </Table>
    </div>
</template>

<script>
import dataConver from "@/libs/data-conver.js";

export default {
  name: 'sell-good-history',
  props: {
      excludedOrderId: Number|String,
      detailList: Array
  },
  data() {
      return {
          tabData: [],
          tabColumns: [
              {
                  title: '序号',
                  type: 'index'
              },
              {
                  title: '客户',
                  key: 'customerName'
              },
              {
                  title: '商品',
                  key: 'goodName'
              },
              {
                  title: '制单日',
                  key: 'createOrderDate',
                  render: (h, params) => {
                      return h('span', this.dateFormat(params.row.createOrderDate));
                  }
              },
              {
                  title: '销售员',
                  key: 'salerNickName',
                  render: (h, params) => {
                      let nickName = params.row.salerNickName;
                      let realName = params.row.salerRealName;
                      if (realName) {
                          return h('span', nickName + ' - [' + realName + ']');
                      }else {
                          return h('span', nickName);
                      }
                  }
              },
              {
                  title: '数量',
                  key: 'quantity'
              },
              {
                  title: '实价',
                  key: 'realPrice'
              },
              {
                  title: '定价',
                  key: 'fixPrice'
              },
              {
                  title: '折扣',
                  key: 'disPrice'
              },
              {
                  title: '金额',
                  key: 'amount'
              }
          ]
      }
  },
  watch: {
      detailList(data) {
          this.refsetData(data);
      }
  },
  methods: {
      dateFormat(data) {
          if (!data && isNaN(data)) {
              return '';
          }
          return dataConver.formatDate(new Date(data), 'yyyy-MM-dd');
        },
      refsetData(data) {
          if (!data || data.length <=0 ) {
              this.tabData = [];
          }
          let list = new Array();
          if (this.excludedOrderId && this.excludedOrderId > 0) {
              for (let i=0; i<data.length; i++) {
                  let item = data[i];
                  if (item.sellOrderId !== this.excludedOrderId) {
                      list.push(item);
                  }
              }
              this.tabData = list;
          }else {
              this.tabData = data;
          }
      }
  }
}
</script>
<style>

</style>



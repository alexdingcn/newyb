<template>
    <div>
        <Table ref="table" :columns="tabColumns" :data="tabData" 
        border highlight-row size="small" style="width: 100%;" 
        >
        </Table>
    </div>
</template>

<script>
import moment from 'moment';

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
                      return moment(params.row.createOrderDate).format('YYYY-MM-DD');
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
              },
              {
                  title: '生产企业',
                  key: 'factoryName',
              },
              {
                  title: '剂型',
                  key: 'jx'
              },
              {
                  title: '规格',
                  key: 'spec'
              },
              {
                  title: '货号',
                  key: 'code'
              },
          ]
      }
  },
  watch: {
      detailList(data) {
          this.refsetData(data);
      }
  },
  methods: {
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
              this.setShowTableData(list);
          }else {
              this.setShowTableData(data);
          }
      },
      setShowTableData(sellDetailList) {
          this.tabData = [];
          for (let i=0; i<sellDetailList.length; i++) {
              let detail = sellDetailList[i];
              let repertoryInfo = detail.repertoryInfo;
              if (repertoryInfo) {
                  let good = repertoryInfo.goods;
                  let tabItem = {
                      customerName: detail.customerName,
                      goodName: repertoryInfo.goodName,
                      createOrderDate: detail.createOrderDate,
                      salerNickName: detail.salerNickName,
                      salerRealName: detail.salerRealName,
                      quantity: detail.quantity,
                      realPrice: detail.realPrice,
                      fixPrice: detail.fixPrice,
                      disPrice: detail.disPrice,
                      amount: detail.amount,
                      factoryName: good ? good.factory : '',
                      jx: good ? good.jx : '',
                      spec: good ? good.spec : '',
                      code: repertoryInfo.code
                  }
                  this.tabData.push(tabItem);
              }
          }
      }
  }
}
</script>
<style>

</style>



<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <card>
            <p slot="title">
                <Icon type="map"></Icon>
               商品溯源
            </p>
        <Form :label-width="85" :model="source"  ref="sourceForm" >
            <Row>
                <i-col  span="6">
                    <FormItem label="商品速查" props="goodsId">
                      <good-select v-model="source.goodsId"  @on-change="searchBlur"></good-select>
                    </FormItem>
                </i-col>
                <i-col span="6">
                    <FormItem label="批次号" >
                      <Select v-model="source.batch" :goodsId="source.goodsId" :disabled="!source.goodsId" placeholder="商品批次号" style="width:260px">
                          <Option v-for="item in batchList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                      </Select>
                    </FormItem>
                  </i-col>
                <i-col span="6">
                    <FormItem >
                      <Button type="primary" icon="ios-search">查询</Button>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col  span="6">
                    <FormItem label="库存" props="goodsId">
                      <input v-model="source.good" disabled size="small" style="width: 260px"/>
                    </FormItem>
                </i-col>
                <i-col span="6">
                    <FormItem label="在单数" >
                      <input v-model="source.good" disabled size="small" style="width: 260px"/>
                    </FormItem>
                  </i-col>
            </Row>
        </Form>
        <div>
            <Row>
                <i-col span="12">
                    <h3 class="margin-top-10">
                      <Icon type="map"></Icon>
                        采购历史
                    </h3>

                    <Table stripe :columns="buyHistoryCol" :data="buyHistoryData"></Table>

                </i-col>
                <i-col span="12" class="padding-left-20">
                   <h3 class="margin-top-10">
                      <Icon type="ios-pulse-strong"></Icon>
                        销售情况
                    </h3>
                    <Table stripe :columns="sellCol" :data="sellData"></Table>
                </i-col>
            </Row>
        </div>
        
        </card>
        
    </div>
</template>
<script>
import goodSelect from "@/views/selector/good-select.vue";
import util from "@/libs/util.js";

export default {
  name: "goodsSource",
  components: {
    goodSelect
  },
  data() {
    return {
      buyHistoryData: [],
      source: {},
      sellData: [],
      batchList: {},
      buyHistoryCol: [
        {
          title: "收货数量"
        },
        {
          title: "赠送数量"
        },
        {
          title: "金额"
        },
        {
          title: "入库数量"
        },
        {
          title: "采购数量"
        },
        {
          title: "采购时间"
        }
      ],
      sellCol: [
        {
          title: "销售时间"
        },
        {
          title: "销售总数量"
        },
        {
          title: "销售总金额"
        }
      ]
    };
  },
  methods: {
    /**supplierChange(supplierId, supplier) {
      //赋值特殊管理标识
      console.log(supplier);
      if (supplier && supplier.id) {
        this.supplierColdManage = supplier.coldBusiness ? true : false;
        this.supplierSpecialManage = supplier.canSpecial ? true : false;
      }
    },**/
    searchBlur(data) {
      //console.log("source.goodsId---" + data);
      let requestData = {
        goodId: data
      };
      console.log("requestData---" + requestData);
      util.ajax
        .get("/goods/source/getBatch", { params: requestData })
        .then(function(response) {
          this.batchList = response.data;
        })
        .catch(function(error) {
          util.errorProcessor(this, error);
        });
    }
  }
};
</script>

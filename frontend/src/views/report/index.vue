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
            <div slot="extra" style="min-width: 750px;">
                <Row>
                    <i-col span="12">
                        <Row type="flex" justify="start">
                            <Steps :current="0">
                                <Step title="商品销毁" ></Step>
                                <Step title="出库审核" ></Step>
                            </Steps>
                        </Row>
                    </i-col>
                    <i-col span="12">
                        <Row type="flex" justify="end">
                            <ButtonGroup class="padding-left-20">
                                <Button type="primary" icon="ios-search" @click="searchInfo">查询</Button>
                                <Button  type="error" @click="destory">销毁</Button>
                            </ButtonGroup>
                        </Row>
                    </i-col>
                </Row>
                
            </div>
        <Form :label-width="85" :model="sourceForm"  ref="sourceForm" >
            <Row>
                <i-col  span="6">
                    <FormItem label="商品速查" props="goodsId">
                        <Select ref="warehouseSelect" v-model="sourceForm.goodsId" @on-change="searchBlur" filterable clearable  style="width:260px"
                                placeholder="商品名称">
                            <Option v-for="item in goodList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>
                </i-col>

                <i-col span="6">
                    <FormItem label="批次号" props="batch" >
                      <Select v-model="sourceForm.batch" :goodsId="sourceForm.goodsId" :disabled="!sourceForm.goodsId" clearable placeholder="商品批次号" style="width:260px">
                          <Option v-for="item in batchList" :value="item" :key="item">{{ item }}</Option>
                      </Select>
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

                    <Table stripe :columns="buyHistoryCol" :width="650" :data="buyHistoryData" :height="600">
                        <div slot="footer">
                            <h3 class="padding-left-20" >
                                <b>总库存: </b>{{repertory}} 
                            </h3>
                        </div>
                    </Table>

                </i-col>
                <i-col span="12" class="padding-left-20">
                   <h3 class="margin-top-10">
                      <Icon type="ios-pulse-strong"></Icon>
                        销售情况
                    </h3>
                    <Table stripe :columns="sellCol" :data="sellData" :height="600">
                        <div slot="footer">
                            <h3 class="padding-left-20" >
                                <b>在単数: </b>{{onWayQuantity}}
                            </h3>
                        </div>
                    </Table>
                </i-col>
            </Row>
        </div>  
        </card>
        
    </div>
</template>
<script>
//import goodSelect from "@/views/selector/good-select.vue";
import util from "@/libs/util.js";
import moment from "moment";

export default {
  name: "goodsSource",
  components: {
    //goodSelect
  },
  data() {
    return {
      buyHistoryData: [],
      sourceForm: {},
      sellData: [],
      batchList: {},
      goodId: "",
      repertory: "",
      onWayQuantity: "",
      goodList: [],
      RepertoryOut: {
        id: "",
        warehouseId: "",
        outDate: "",
        refOrderNumber: "",
        customerName: "",
        comment: "",
        detail: []
      },
      buyHistoryCol: [
        {
          title: "批次号",
          key: "batchCode",
          align: "center",
          fixed: 'left',
          width:140,
        },
        {
          title: "采购数量",
          key: "buyOrderQuality",
          align: "center",
          width:100,
        },
        {
          title: "入库数量",
          key: "inCount",
          align: "center",
          width:100,
        },
        {
          title: "库存量",
          key: "quantity",
          align: "center",
          width:80,
        },
        {
          title: "采购价",
          key: "buyPrice",
          align: "center",
          width:80,
        },
        {
          title: "入库时间",
          key: "createTime",
          align: "center",
          width:140,
          render: (h, params) => {
            let createTime = params.row.createTime;
            return h(
              "span",
              createTime ? moment(createTime).format("YYYY-MM-DD HH:mm") : ""
            );
          }
        },
        {
          title: "供应商",
          key: "shipName",
          align: "center",
          width:100,
        },
        {
          title: "采购员",
          key: "realName",
          align: "center",
          width:80,
        }
      ],
      sellCol: [
        {
          title: "批次号",
          key: "batchCode",
          align: "center"
        },
        {
          title: "客户",
          key: "customerName",
          align: "center"
        },
        {
          title: "承运公司",
          key: "shipCompanyName",
          align: "center"
        },
        {
          title: "销售数量",
          key: "totalQuantity",
          align: "center"
        },
        {
          title: "销售金额",
          key: "totalAmount",
          align: "center"
        },
        {
          title: "已付金额",
          key: "paidAmount",
          align: "center"
        },
        {
          title: "销售员",
          key: "realName",
          align: "center"
        }
      ]
    };
  },
  mounted() {
    this.queryGood();
  },
  methods: {
    queryGood() {
      util.ajax
        .get("/goods/source/goodsList")
        .then(response => {
          this.goodList = response.data.goodsList;
        })
        .catch(function(error) {
          util.errorProcessor(this, error);
        });
    },
    searchInfo() {
      let requestData = {
        goodId: this.sourceForm.goodsId,
        batchCode: this.sourceForm.batch
      };
      util.ajax
        .get("/goods/source/searchInfo", { params: requestData })
        .then(response => {
          this.repertory = response.data.repertory;
          this.onWayQuantity = response.data.onWayQuantity;
          this.buyHistoryData = response.data.Buy;
          this.sellData = response.data.sell;
        })
        .catch(function(error) {
          util.errorProcessor(this, error);
        });
    },
    searchBlur(data) {
      let requestData = {
        goodId: data
      };
      util.ajax
        .get("/goods/source/getBatch", { params: requestData })
        .then(response => {
          this.batchList = response.data.data;
          this.goodId = response.data.goodId;
        })
        .catch(function(error) {
          util.errorProcessor(this, error);
        });
    },
    destory() {
      this.$Modal.confirm({
        title: "商品销毁确认",
        content: "是否确认进行商品销毁，销毁后不能撤回",
        onOk: () => {
          let putData = {
            goodId: this.sourceForm.goodsId,
            batchCode: this.sourceForm.batch
          };
          util.ajax
            .get("/goods/source/destory", { params: putData })
            .then(response => {
              if (response.status === 200) {
                this.$Message.success("销毁成功!");
              }
            })
            .catch(error => {
              util.errorProcessor(this, error);
            });
        },
        onCancel: () => {}
      });
    }
  }
};
</script>

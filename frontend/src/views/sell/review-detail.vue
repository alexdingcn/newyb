<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div class="padding-left-100">
        <Row type="flex" justify="center">
            <Col span="24">
                <Row class="title-show">
                    <span>{{orderDetail.orderNumber}}</span>
                    <hr/>
                </Row>
                <Row class="content-row">
                    <Col span="6">
                        <span class="label-key">订单编号:</span>
                        <span class="label-value">{{orderDetail.orderNumber}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">制单日期:</span>
                        <span class="label-value">{{orderDetail.createOrderDate | dateFormat}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">客户:</span>
                        <span class="label-value">{{orderDetail.customerName}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">客户代表:</span>
                        <span class="label-value">{{orderDetail.customerRepName}}</span>
                    </Col>
                </Row>
                <Row class="content-row">
                    <Col span="6">
                        <span class="label-key">销售员:</span>
                        <span class="label-value">{{orderDetail.saleNickName}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">收货电话:</span>
                        <span class="label-value">{{orderDetail.customerRepContactPhone}}</span>
                    </Col>
                    <Col span="12">
                        <span class="label-key">收货地址:</span>
                        <span class="label-value">{{orderDetail.customerRepRepertoryAddress}}</span>
                    </Col>
                </Row>
                <Row class="content-row">
                    <Col span="6">
                        <span class="label-key">提货人:</span>
                        <span class="label-value">{{orderDetail.takeGoodsUser}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">湿控方式:</span>
                        <span class="label-value">{{orderDetail.temperControlName}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">收款日期:</span>
                        <span class="label-value">{{orderDetail.payOrderDate | dateFormat}}</span>
                    </Col>
                     <Col span="6">
                        <span class="label-key">加价率:</span>
                        <span class="label-value">{{orderDetail.markUpRate}}</span>
                    </Col>
                </Row>
                <Row class="content-row">
                    <Col span="6">
                        <span class="label-key">制单人:</span>
                        <span class="label-value">{{orderDetail.createBy}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">运输方式:</span>
                        <span class="label-value">{{orderDetail.shipMethodName}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">运输工具:</span>
                        <span class="label-value">{{orderDetail.shipToolName}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">备注信息:</span>
                        <span class="label-value">{{orderDetail.comment}}</span>
                    </Col>
                </Row>

                <div v-for="(item, index) in detailsData" :key="item.id">
                    <Row class="title-show margin-top-20">
                        <span>订单商品详细->{{index + 1}}</span>
                        <hr/>
                    </Row>
                    <Row class="content-row">
                        <Col span="6">
                            <span class="label-key">商品名称:</span>
                            <span class="label-value">{{item.goodsName}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">仓库:</span>
                            <span class="label-value">{{orderDetail.warehouseName}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">库位:</span>
                            <span class="label-value">{{item.repertoryInfo.location || ''}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">批次号:</span>
                            <span class="label-value">{{item.repertoryInfo.batchCode || ''}}</span>
                        </Col>
                    </Row>
                    <Row class="content-row">
                        <Col span="6">
                            <span class="label-key">销售数量:</span>
                            <span class="label-value">{{item.quantity}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">定价:</span>
                            <span class="label-value">{{item.fixPrice}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">实价:</span>
                            <span class="label-value">{{item.realPrice}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">折扣:</span>
                            <span class="label-value">{{item.disPrice}}</span>
                        </Col>
                    </Row>
                    <Row class="content-row">
                        <Col span="6">
                            <span class="label-key">赠送:</span>
                            <span class="label-value">{{item.free}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">件单价:</span>
                            <span class="label-value">{{item.singlePrice}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">金额:</span>
                            <span class="label-value">{{item.amount}}</span>
                        </Col>
                        <Col span="6">
                            <span class="label-key">税率:</span>
                            <span class="label-value">{{item.taxRate}}</span>
                        </Col>
                    </Row>
                    <good-expand :good="getGoodDetail(item)" 
                        :repertoryInfo="item.repertoryInfo" 
                        :productDate="getGoodProductDate(item.repertoryInfo)" 
                        :expDate="getGoodExpDate(item.repertoryInfo)"></good-expand>
                </div>

            </Col>
        </Row>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import goodExpand from "@/views/good/good-expand.vue";

export default {
  name: 'review-detail',
  props: {
      sellOrderId: Number
  },
  components: {
      goodExpand
  },
  data() {
      return {
          orderDetail: '',
          detailsData: [],
      }
  },
  watch: {
      sellOrderId(id) {
          this.initData(id);
      }
  },
  filters: {
      dateFormat(data) {
          if (!data && isNaN(data)) {
              return '';
          }
          return moment(data).format('YYYY-MM-DD');
      }
  },
  methods: {
      initData(id) {
          util.ajax.get("/sell/order/review/detail", {params: {orderId: id}})
            .then((response) => {
                this.orderDetail = response.data;
                if (this.orderDetail && this.orderDetail.details) {
                    this.detailsData = this.orderDetail.details;
                }
            })
            .catch(error => {
                util.errorProcessor(this, error);
            });
      },

      getGoodDetail(sellDetail) {
          if (!sellDetail) {
              return {};
          }
          let repertoryInfo = sellDetail.repertoryInfo;
          if (repertoryInfo) {
              return repertoryInfo.goods;
          }else {
              return {};
          }
      },
      getGoodProductDate(repertoryInfo) {
          if(!repertoryInfo) {
              return '';
          }
          let productDate = repertoryInfo.productDate;
          return productDate ? moment(productDate).format('YYYY-MM-DD') : '';
      },
      getGoodExpDate(repertoryInfo) {
          if(!repertoryInfo) {
              return '';
          }
          let expDate = repertoryInfo.expDate;
          return expDate ? moment(expDate).format('YYYY-MM-DD') : '';
      }
  }
}
</script>

<style>
.title-show > span {
    font-size: 1.25em;
    font-weight: bold;
    color: #80848f;
}

.title-show > hr {
    width: 100%;
    height: 2px;
    color: #5cadff;
    margin-bottom: 10px;
}

.label-key {
    width: 110px;
    text-align: right;
}
.label-value {
    font-weight:bold;
}
</style>


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
                        <span class="label-key">总计数量:</span>
                        <span class="label-value">{{orderDetail.totalQuantity}}</span>
                    </Col>
                    <Col span="6">
                        <span class="label-key">总计金额:</span>
                        <span class="label-value">{{orderDetail.totalAmount}}</span>
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
                <h3 style="margin-top:20px;">订单详情信息</h3>
                <hr size="1" style="width:100%; margin-bottom: 10px;"/>
                <Table ref="detailTable" size="small" border highlight-row style="width: 100%;" 
                    :columns="tabColumns" :data="detailsData">
                </Table>
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
          tabColumns: [
              {
                  title: '货号',
                  key: 'goodsId',
                  width: 100
              },
              {
                  title: '商品名称',
                  key: 'goodsName',
                  width: 160,
              },
              {
                  title: '生产企业',
                  key: 'factoryName',
                  width: 160,
              },
              {
                  title: '生产企业',
                  key: 'factoryName',
                  width: 160,
              },
              {
                    title: '批次号',
                    key: 'batch',
                    align: 'center',
                    width: 160,
                    render: (h, params) => {
                        return params.row.repertoryInfo.batchCode;
                    }
                },
                {
                    title: '剂型',
                    key: 'jx',
                    width: 100,
                    render: (h, params) => {
                        let goods = params.row.repertoryInfo.goods;
                        if(goods) {
                            return goods.jxName;
                        }else {
                            return '';
                        }
                    }
                },
                {
                    title: '规格',
                    key: 'spec',
                    width: 100,
                    render: (h, params) => {
                        let goods = params.row.repertoryInfo.goods;
                        if(goods) {
                            return goods.spec;
                        }else {
                            return '';
                        }
                    }
                },
                {
                    title: '生产日期',
                    width: 120,
                    key: 'productData',
                    render: (h, params) => {
                        let repertoryInfo = params.row.repertoryInfo;
                        if (repertoryInfo && repertoryInfo.productDate) {
                            return moment(repertoryInfo.productDate).format('YYYY-MM-DD');
                        }
                    }
                },
                {
                    title: '有效期至',
                    key: 'expDate',
                    width: 120,
                    render: (h, params) => {
                        let repertoryInfo = params.row.repertoryInfo;
                        if (repertoryInfo && repertoryInfo.expDate) {
                            return moment(repertoryInfo.expDate).format('YYYY-MM-DD');
                        }
                    }
                },
                {
                    title: '单位',
                    key: 'unitName',
                    width: 90,
                    render: (h, params) => {
                        let goods = params.row.repertoryInfo.goods;
                        if(goods) {
                            return goods.unitName;
                        }else {
                            return '';
                        }
                    }
                },
                {
                    title: '数量',
                    width: 90,
                    key: 'quantity'
                },
                {
                    title: '价格',
                    width: 90,
                    key: 'realPrice'
                },
                {
                    title: "折扣",
                    key: "disPrice",
                    align: "center",
                    width: 100
                },
                {
                    title: "赠送",
                    key: "free",
                    align: "center",
                    width: 100
                },
                {
                    title: '金额',
                    width: 100,
                    key: 'amount'
                },
                {
                    title: "税率",
                    key: "taxRate",
                    align: "center",
                    width: 100
                },
                {
                    title: '库位',
                    key: 'location',
                    width: 100,
                    render: (h, params) => {
                        return params.row.repertoryInfo.location;
                    }
                },
                {
                    title: '质量审核状态',
                    width: 120,
                    key: 'checkStatus',
                    render: (h, params) => {
                        const checkStatus = params.row.checkStatus;
                        const color = !checkStatus ? 'blue' : checkStatus === 'OK' ? 'green' : 'red';
                        const text = !checkStatus ? '待审' : checkStatus === 'OK' ? '通过' : '拒绝';
                        return h('Tag', {
                            props: {
                                type: 'dot',
                                color: color
                            }
                        }, text);
                    }
                },
                {
                    title: '质量审核人',
                    width: 100,
                    key: 'checkUser',
                    render: (h, params) => {
                        const checkUser = params.row.checkUser ? params.row.checkUser : '';
                        return h('span', checkUser);
                    }
                },
                {
                    title: '质量审核日期',
                    width: 120,
                    key: 'checkDate',
                    render: (h, params) => {
                        let checkDate = params.row.checkDate;
                        return h('span', checkDate ? moment(checkDate).format('YYYY-MM-DD HH:mm') : '');
                    }
                },
                {
                    title: '质量审核结论',
                    width: 180,
                    key: 'checkResult',
                    render: (h, params) => {
                        const checkResult = params.row.checkResult ? params.row.checkResult : '';
                        return h('span', checkResult);
                    }
                }
          ]
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



<template>
  <div>
      <Card>
          <p slot="title">采购入库单暂挂提取</p>
          <div slot="extra">
              <ButtonGroup size="small">
                  <Button type="primary" icon="ios-search" :loading="orderLoading" @click="refreshTempStatusOrder" >查询</Button>
                  <Button type="success" icon="checkmark-round" :loading="orderLoading" @click="getConfirm" >提取选取</Button>
                  <Button type="error" icon="close-round" :loading="orderLoading" @click="removeOrder" >删除订单</Button>
              </ButtonGroup>
          </div>
        
          <Row type="flex" justify="start">
              <span style="width:100px; margin-right:10px;">收货日期</span>
              <DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="收货日期" style="width:200px"></DatePicker>
          </Row>
          <Table ref="orderTable" border highlight-row disabled-hover height="250" 
            size="small" :columns="orderColumns" :data="orderData" :loading="orderLoading" 
            @on-row-click="handleSelectOrder"
            no-data-text="使用右上方输入搜索条件">
          </Table>

          <div style="margin-top:10px;">
             <Table ref="detailTable" border highlight-row disabled-hover height="280" 
                size="small" :columns="detailColumns" :data="detailData" 
                no-data-text="点击上方订单数据显示">
            </Table> 
          </div>
      </Card>
  </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';

export default {
    name: 'buy-receive-temp',
    components: {},
    data() {
        return {
            dateRange: [
                moment().add(-1, 'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD')
            ],
            orderLoading: false,
            orderData: [],
            orderColumns: [
                {
                    type: 'index',
                    title: '序号',
                    width: 80
                },
                {
                    title: '收货日期',
                    key: 'receiveDate',
                    render: (h, params) => {
                        let receiveDate = params.row.receiveDate;
                        return receiveDate ? moment(receiveDate).format('YYYY-MM-DD') : '';
                    }
                },
                {
                    title: '供应商',
                    key: 'supplierName'
                },
                {
                    title: '供应商代表',
                    key: 'supplierContactName'
                },
                {
                    title: '采购员',
                    key: 'saleNickName',
                },
                {
                    title: '仓库点',
                    key: 'warehouseName'
                },
                {
                    title: '系统单号',
                    key: 'orderNumber'
                },
                {
                    title: '自定义单号',
                    key: 'refNo'
                }
            ],
            detailData: [],
            detailColumns: [
                {
                    type: 'index',
                    title: '序号',
                },
                {
                    title: '货号',
                    key: 'goodsId'
                },
                {
                    title: '商品名称',
                    key: 'goodsName'
                },
                {
                    title: '产地',
                    key: 'origin'
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
                    title: '生产企业',
                    key: 'factory'
                },
                {
                    title: '单位',
                    key: 'unitName'
                },
                {
                    title: '整件单位',
                    key: 'packUnitName'
                },
                {
                    title: '大件装量',
                    key: 'bigPack'
                },
                {
                    title: '单价',
                    key: 'price'
                },
                {
                    title: '金额',
                    key: 'amount'
                },
                {
                    title: '批次号',
                    key: 'batchCode'
                }
            ],
            chooseItem: {},
            chooseIndex: -1
        }
    },
    methods: {
        refreshTempStatusOrder() {
            let startDate = this.dateRange[0];
            let endDate = this.dateRange[1];
            let reqData = {
                statusList: ['TEMP_STORAGE'],
                startReceiveDate: startDate,
                endReceiveDate: endDate
            };
            this.orderLoading = true;
            util.ajax.post('/receive/list', reqData)
                .then((response) => {
                    this.orderLoading = false;
                    this.orderData = response.data;
                })
                .catch((error) => {
                    this.orderLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        handleSelectOrder(row, index) {
            if(!row || !row.id) {
                this.chooseItem = {};
                this.chooseIndex = -1;
                this.detailData = [];
            }else {
                this.chooseItem = row;
                this.chooseIndex = index;
                this.detailData = row.details;
            }
        },

        removeOrder() {
            if (!this.chooseItem || !this.chooseItem.id) {
                this.$Message.warning('请先选择需要删除的订单');
                return;
            }
            let self = this;
            this.$Modal.confirm({
                title: '操作确认提醒',
                content: '是否确认删除订单信息，删除后不可恢复',
                onOk: () => {
                    util.ajax.delete("/receive/remove/" + self.chooseItem.id)
                        .then((response) => {
                            self.$Message.success('删除成功');
                            self.orderData.splice(this.chooseIndex, 1);
                        })
                        .catch((error) => {
                            util.errorProcessor(self, error);
                        });
                },
                onCancel: () => {
                }
            });
        },

        getConfirm() {
            if (!this.chooseItem || !this.chooseItem.id) {
                this.$Message.warning('请先选择需要提取的订单');
                return;
            }
            this.$emit('on-choosed', this.chooseItem);
        }
    }
};
</script>
<style>

</style>




<template>
  <div>
      <Card>
          <p slot="title">入库审查</p>
          <div slot="extra">
              <ButtonGroup>
                <Button size="small" type="primary" icon="ios-search" :loading="orderLoading" @click="refreshOrder">查询</Button>
                <Button size="small" type="success" icon="ios-checkmark" @click="checkOk" >审查通过</Button>
                <Button size="small" type="info"  icon="images" @click="showCheckFile">检验档案</Button>
              </ButtonGroup>
          </div>
          
          <Form ref="searchForm" :model="query" :label-width="100">
                <Row type="flex" justify="start">
                    <FormItem label="收货日期">
                        <DatePicker size="small" v-model="dateRange" type="daterange" placement="bottom-start" placeholder="收货日期" style="width:180px"></DatePicker>
                    </FormItem>
                    <FormItem label="仓库">
                        <warehouse-select v-model="query.warehouseId" size="small"></warehouse-select>
                    </FormItem>
                    <FormItem label="供应商">
                        <supplier-select v-model="query.supplierId" size="small"></supplier-select>
                    </FormItem>
                    <FormItem label="状态">
                        <Select size="small" v-model="query.status" placeholder="状态">
                                <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                        </Select>
                    </FormItem>
                </Row>
          </Form>
          <div>
              <Table ref="orderTable" border highlight-row disabled-hover height="250" style="width: 100%" 
                    :columns="orderListColumns" :data="orderList" size="small" 
                    :loading="orderLoading" 
                    @on-row-click="handleSelectOrder" 
				    no-data-text="输入查询条件, 点击上方查询按钮进行查询">
                </Table>
          </div>
          <Row type="flex" justify="start" style="margin-top:15px;">
              <h4 class="detail-count-content" >
                    <b class="detail-count-content-b">总金额:</b> {{ totalAmount }}
                    <b class="detail-count-content-b">入库数量:</b> {{ totalInCount }}
                    <b class="detail-count-content-b">不合格数量:</b> {{ totalErrorCount }}
                </h4>
          </Row>
          <Table border highlight-row height="300" :loading="detailLoading" 
                :columns="detailColumns" :data="detailList" size="small" 
                ref="detailTable" style="width: 100%;" 
                no-data-text="点击上方订单后查看明细">
        </Table>
      </Card>

      <Modal v-model="checkFileModal" title="检验报告档案" :mask-closable="false" width="50">
          <file-detail :fileNo="checkFileNo" @add-file-success="addFileSuccess" ></file-detail>
          <div slot="footer"></div>
      </Modal>
  </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";

export default {
    name: 'in-check',
    components: {
        warehouseSelect,
        supplierSelect,
        fileDetail
    },
    data() {
        return {
            orderLoading: false,
            detailLoading: false,
            statusOptions: [
                {key: 'ALL', name: '所有'},
                {key: 'CHECKED', name: '未审查'},
                {key: 'IN_CHECKED', name: '已审查'}
            ],
            dateRange: [
                moment().add(-1,'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD')
            ],
            query: {
                status: 'CHECKED'
            },
            orderList: [],
            orderListColumns: [
                {
                    title: '序号',
                    type: 'index',
                    width: 60
                },
                {
                    title: '收货时间',
                    key: 'receiveDate',
                    render: (h, params) => {
                        let receiveDate = params.row.receiveDate;
                        return receiveDate ? moment(receiveDate).format("YYYY-MM-DD") : '';
                    }
                },
                {
                    title: '状态',
                    key: 'status',
                    width: 120,
                    render: (h, params) => {
                        let color = params.row.status === 'IN_CHECKED' ? 'green' : '#ff9900';
                        let label = params.row.status === 'IN_CHECKED' ? '已审查' : '未审查';
                        return  h('Tag', {
                            props: {
                                type: 'dot',
                                color: color
                            }
                        }, label);
                    }
                },
                {
                    title: '入库方式',
                    key: 'refTypeName'
                },
                {
                    title: '入库仓库',
                    key: 'warehouseName',
                },
                {
                    title: '供应商',
                    key: 'supplierName',
                },
                {
                    title: '供应商代表',
                    key: 'supplierContactName',
                },
                {
                    title: '采购员',
                    key: 'saleNickName',
                    render: (h, params) => {
                        let saleNickName = params.row.saleNickName;
                        let saleRealName = params.row.saleRealName;
                        if (saleNickName && saleRealName) {
                            return h('span', saleNickName + ' - [' + saleRealName + ']');
                        }else {
                            return h('span', saleNickName);
                        }
                    }
                },
                {
                    title: '总计入库数量',
                    key: 'totalQuantity'
                },
                {
                    title: '总计金额',
                    key: 'totalAmount'
                },
                {
                    title: '收货员',
                    key: 'createBy'
                },
                {
                    title: '到货温度',
                    key: 'receiveTemp'
                },
                {
                    title: '验收温度',
                    key: 'checkTemp'
                },
                {
                    title: '系统单号',
                    key: 'orderNumber'
                },
                {
                    title: '采购属性',
                    key: 'buyTypeName',
                },
                {
                    title: '到货时间',
                    key: 'shipEndDate',
                    render: (h, params) => {
                        let shipEndDate = params.row.shipEndDate;
                        return shipEndDate ? moment(shipEndDate).format("YYYY-MM-DD") : '';
                    }
                }
            ],
            detailList: [],
            detailColumns: [
                {
                    title: "序号",
                    type: 'index',
                    width: 60
                },
                {
                    title: "商品名称",
                    key: 'goodsName',
                    width: 160
                },
                {
                    title: "产地",
                    key: 'origin',
                    width: 160
                },
                {
                    title: "剂型",
                    key: 'jx',
                    width: 120
                },
                {
                    title: "规格",
                    key: 'spec',
                    width: 100
                },
                {
                    title: "生产企业",
                    key: 'factoryName',
                    width: 120
                },
                {
                    title: "批准文号",
                    key: 'permit',
                    width: 120
                },
                {
                    title: "存储条件",
                    key: 'storageCondition',
                    width: 100
                },
                {
                    title: "特殊药品",
                    key: 'specialManaged',
                    width: 120,
                    render (h, params) {
                        let specialManaged = params.row.specialManaged;
                        if (specialManaged) {
                            return h('Tag', {props:{type:'dot', color:'red'}}, '是');
                        }else{
                            return h('Tag', {props:{type:'dot', color:'blue'}}, '否');
                        }
                    }
                },
                {
                    title: "单位",
                    key: 'unitName',
                    width: 120
                },
                {
                    title: "批号",
                    key: 'batchCode',
                    width: 140
                },
                {
                    title: "生产日期",
                    key: 'productDate',
                    width: 140,
                    render: (h, params) => {
                        return params.row.productDate ? moment(params.row.productDate).format('YYYY-MM-DD') : '';
                    }
                },
                {
                    title: "有效期至",
                    key: 'expDate',
                    width: 140,
                    render: (h, params) => {
                        return params.row.expDate ? moment(params.row.expDate).format('YYYY-MM-DD') : '';
                    }
                },
                {
                    title: '单价',
                    width: 120,
                    key: 'price'
                },
                {
                    title: '金额',
                    width: 120,
                    key: 'amount'
                },
                {
                    title: "到货数量",
                    width: 140,
                    key: 'receiveQuality'
                },
                {
                    title: '赠送数量',
                    width: 140,
                    key: 'free'
                },
                {
                    title: "入库数量",
                    key: 'inCount',
                    width: 140
                },
                {
                    title: "合格数量",
                    key: 'rightCount',
                    width: 140
                },
                {
                    title: "不合格数量",
                    key: 'errorCount',
                    width: 140
                },
                {
                    title: "采集数量",
                    key: 'surveyQuality',
                    width: 140,
                    render: (h, params) => {
                        let surveyQuality = params.row.surveyQuality;
                        let surveyDate = params.row.surveyDate ? moment(params.row.surveyDate).format('YYYY-MM-DD HH:mm') : '';
                        let surveyUser = params.row.surveyUser;
                        if (!surveyQuality) {
                            return h('span', '');
                        }
                        return h('Tooltip', {
                            props: {
                                placement:"top",
                                content: '抽样员:' + surveyUser + ', 抽样时间:' + surveyDate
                            }
                        }, [
                            h('span', surveyQuality),
                        ]);
                    }
                },
                {
                    title: "库区",
                    key: 'warehouseLocation',
                    width: 140
                },
                {
                    title: "验收状态",
                    key: 'checkStatus',
                    width: 140,
                    render (h, params) {
                        let checkStatus = params.row.checkStatus;
                        if (checkStatus) {
                            return h('Tag', {props:{type:'dot', color:'green'}}, '已验收');
                        }else{
                            return h('Tag', {props:{type:'dot', color:'red'}}, '未验收');
                        }
                    }
                },
                {
                    title: "温控方式验收",
                    key: 'checkTempMethod',
                    width: 140
                },
                {
                    title: "验收意见",
                    key: 'checkResult',
                    width: 140
                },
                {
                    title: "验收员",
                    key: 'checkUser',
                    width: 140
                },
                {
                    title: "验收时间",
                    key: 'checkTime',
                    width: 140,
                    render: (h, params) => {
                        let checkTime = params.row.checkTime;
                        return checkTime ? moment(checkTime).format('YYYY-MM-DD HH:mm') : '';
                    }
                }
            ],
            totalAmount: 0,
            totalInCount: 0,
            totalErrorCount: 0,
            currentChooseOrder: {},
            checkFileNo: '',
            checkFileModal: false
        }
    },
    watch: {
        detailList(data) {
            if(!data || data.length <= 0) {
                this.totalInCount = 0;
                this.totalErrorCount = 0;
                this.totalAmount = 0;
            }else {
                this.totalAmount = data.reduce((total, item) => {return item.amount ? total + item.amount : total + 0 }, 0);
                this.totalInCount = data.reduce((total, item) => {return item.inCount ? total + item.inCount : total + 0}, 0);
                this.totalErrorCount = data.reduce((total, item) => {return item.errorCount ? total + item.errorCount : total + 0}, 0);
            }
        }
    },
    methods: {

        refreshOrder() {
            let statusList = [];
            if (this.query.status === 'CHECKED') {
                statusList = ['CHECKED'];
            }else if (this.query.status == 'IN_CHECKED') {
                statusList = ['IN_CHECKED'];
            }else {
                statusList = ['INIT', 'CHECKED', 'IN_CHECKED'];
            }
            let reqData = {
                statusList: statusList,
                warehouseId: this.query.warehouseId,
                supplierId: this.query.supplierId,
                startReceiveDate: this.dateRange[0],
                endReceiveDate: this.dateRange[1]
            };
            this.orderLoading = true;
            util.ajax.post("/repertory/in/list", reqData)
                .then((response) => {
                    this.orderLoading = false;
                    this.orderList = response.data;
                })
                .catch((error) => {
                    this.orderLoading = false;
                    util.errorProcessor(this, error);
                });
            this.currentChooseOrder = {};
            this.detailList = [];
        },

        handleSelectOrder(rowData) {
            if (!rowData || !rowData.id) {
                this.currentChooseOrder = {};
                this.detailList = [];
                return;
            }
            this.currentChooseOrder = rowData;
            this.reloadOrderDetail();
        },

        reloadOrderDetail() {
            this.detailLoading = true;
            util.ajax.get('/repertory/in/detail/' + this.currentChooseOrder.id)
                .then((response) => {
                    this.detailLoading = false;
                    let data = response.data;
                    if (data) {
                        this.detailList = data;
                        this.currentChooseOrder.details = this.detailList;
                    }
                })
                .catch((error) => {
                    this.detailLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        checkOk() {
            if(!this.currentChooseOrder || !this.currentChooseOrder.id) {
                this.$Message.warning('请先悬着需要审核的订单');
                return;
            }
            //验证订单的详情是否意见全部通过，如果是，才能提交
            let details = this.currentChooseOrder.details;
            if (!details || details.length <= 0) {
                this.$Message.warning('订单没有对应的产品详情信息,不能审核通过');
                return;
            }
            for(let i=0; i<details.length; i++) {
                let item = details[i];
                if (!item || !item.checkStatus) {
                    this.$Modal.warning({
                        title: '信息提醒',
                        content: '订单存在有未验收通过的产品，不能审核通过'
                    });
                    return;
                }
            }
            let self = this;
            self.orderLoading = true;
            let reqData = {
                orderId: this.currentChooseOrder.id
            };
            //验收审核通过完成后，提示是否确认信息，然后提交
            this.$Modal.confirm({
                title: '审核信息确认',
                content: '是否已确认订单详情数据正确，提交审核通过后不能修改',
                onOk: () => {
                    util.ajax.put('/repertory/in/set/incheck', reqData)
                        .then((response) => {
                            self.orderLoading = false;
                            self.$Message.success('审查成功');
                            self.refreshOrder();
                        })
                        .catch((error) => {
                            self.orderLoading = false;
                            util.errorProcessor(self, error);
                        })
                },
                onCancel: () => {}
            });

        },

        showCheckFile() {
            if(!this.currentChooseOrder || !this.currentChooseOrder.id) {
                this.$Message.warning('请先选择对应订单信息');
                return;
            }
            let fileNo = this.currentChooseOrder.fileNo;
            this.checkFileNo = fileNo;
            this.checkFileModal = true;
        },

        addFileSuccess(data) {
            if (!data || !data.fileNo) {
                this.$Notice.error({
                    title: '系统错误',
                    desc: '添加档案成功后获取档案信息失败, 请联系技术人员查询原因.'
                });
                return;
            }
            let reqData = {
                orderId: this.currentChooseOrder.id,
                fileNo: data.fileNo
            };
            util.ajax.put('/repertory/in/order/fileNo', reqData)
                .then((response) => {
                    this.currentChooseOrder.fileNo = data.fileNo;
                    for(let i=0; i<this.orderList.length; i++) {
                        let row = this.orderList[i];
                        if(this.currentChooseOrder.id === row.id) {
                            row.fileNo = data.fileNo;
                            break;
                        }
                    }
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                    this.$Notice.error({
                        title: '系统错误',
                        desc: '添加档案成功后绑定档案信息失败, 请联系技术人员查询原因.'
                    });
                })
        }

    }

}
</script>

<style>

</style>

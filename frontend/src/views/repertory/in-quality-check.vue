<style lang="less">
    @import '../../styles/common.less';
</style>
<template>
  <div>
      <Card>
          <p slot="title">入库质量验收</p>
          <div slot="extra">
              <ButtonGroup>
                <Button size="small" type="primary" icon="ios-search" :loading="orderLoading" @click="refreshOrder">查询</Button>
                <Button size="small" type="success" icon="ios-checkmark" @click="checkOneOrderBtn">验收一单</Button>
                <Button size="small" type="warning"  icon="close" @click="unCheckOneOrderBtn">取消验收一单</Button>
                <Button size="small" type="info"  icon="images" @click="showCheckFile">检验档案</Button>
              </ButtonGroup>
          </div>
          
        <Form ref="searchForm" :model="query" :label-width="100">
                <Row type="flex" justify="start">
                <Col span="5">
                    <FormItem label="订单日期">
                        <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="收货日期" style="width:180px"></DatePicker>
                    </FormItem>
                </Col>
                <Col span="5">
                    <FormItem label="仓库">
                    <warehouse-select v-model="query.warehouseId"></warehouse-select>
                    </FormItem>
                </Col>
                <Col span="6">
                    <FormItem label="供应商">
                        <supplier-select v-model="query.supplierId"></supplier-select>
                    </FormItem>
                </Col>
                <Col span="6">
                    <FormItem label="状态">
                        <Select v-model="query.status" placeholder="状态">
                                <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                        </Select>
                    </FormItem>
                </Col>
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

          <div class="detail-div">
                <Row type="flex" justify="end">
                    <ButtonGroup>
                        <Button type="info" icon="android-bulb" @click="samplingSurveyBtnClick">抽样检查</Button>
                        <Button type="success" icon="checkmark" @click="checkOneDetailBtn">验收一条</Button>
                        <Button type="warning" icon="close" @click="unCheckOneDetailBtn">取消验收一条</Button>
                        <Button type="success" icon="android-checkbox-outline" @click="saveOroderDetail">保存详情</Button>
                        <Button type="error" icon="close" @click="removeDetail">删除一条</Button>
                    </ButtonGroup>
                </Row>

              <Table border highlight-row height="300" :loading="detailLoading" 
                   :columns="detailColumns" :data="detailList" size="small" 
                   ref="detailTable" class="margin-top-10"
                   @on-row-click="handleSelectDetail" 
                   no-data-text="点击上方订单后查看明细">
                <div slot="footer">
                    <h3 class="detail-count-content" >
                        <b>总金额:</b> {{ totalAmount }} 
                        <b class="detail-count-content-b">收货数量:</b> {{ totalReceiveCount }}
                        <b class="detail-count-content-b">入库数量:</b> {{ totalInCount }}
                        <b class="detail-count-content-b">合格数量:</b> {{ totalRightCount }}
                        <b class="detail-count-content-b">不合格数量:</b> {{ totalErrorCount }}
                        <b class="detail-count-content-b">采集数量:</b> {{ totalSurveyQuality }}
                    </h3>
                </div>
            </Table>
          </div>
      </Card>
      
      <Modal v-model="surveyModal" title="商品抽样检查" :mask-closable="false" width="50">
          <in-survey-check :order="currentChooseOrder" :detail="currChooseDetail" 
                @close="surveyClose" 
                @survey-success="surveySuccess">
            </in-survey-check>
          <div slot="footer"></div>
      </Modal>

      <Modal v-model="checkModal" title="验收结果" :mask-closable="false" width="50" @on-ok="checkSuccess" @on-cancel="checkCancel">
          <Form ref="checkForm" :model="checkFormItem" :label-width="100">
              <Row v-if="checkDetail">
                  <Col span="12">
                    <FormItem label="采购数量">
                        <Input :number="true" v-model="checkFormItem.buyOrderQuality" :readonly="true"/>
                    </FormItem>
                  </Col>
                  <Col span="12">
                    <FormItem label="到货数量">
                        <Input :number="true" v-model="checkFormItem.receiveQuality" :readonly="true"/>
                    </FormItem>
                  </Col>
              </Row>
              <Row v-if="checkDetail">
                  <Col span="12">
                    <FormItem label="入库数量">
                        <Input :number="true" v-model="checkFormItem.inCount" :readonly="true"/>
                    </FormItem>
                  </Col>
                  <Col span="12">
                    <FormItem label="抽样数量">
                        <Input :number="true" v-model="checkFormItem.surveyQuality" :readonly="true"/>
                    </FormItem>
                  </Col>
              </Row>

              <Row v-if="checkDetail">
                  <Col span="12">
                    <FormItem label="合格数量">
                        <Input :number="true" v-model="checkFormItem.rightCount" @on-change="checkRightCountChange" />
                    </FormItem>
                  </Col>
                  <Col span="12">
                    <FormItem label="不合格数量">
                        <Input :number="true" v-model="checkFormItem.errorCount" :readonly="true"/>
                    </FormItem>
                  </Col>
              </Row>
              <Row v-if="checkDetail">
                  <Col span="12">
                    <FormItem label="不合格品处置方案">
                        <Input v-model="checkFormItem.errorPlan" />
                    </FormItem>
                  </Col>
                  <Col span="12">
                    <FormItem label="营销人员证书">
                        <Input v-model="checkFormItem.saleCert" />
                    </FormItem>
                  </Col>
              </Row>
              <Row v-if="checkDetail">
                  <Col span="24">
                    <FormItem label="不合格品原因">
                        <Input v-model="checkFormItem.errorReason" />
                    </FormItem>
                  </Col>
              </Row>
              <Row>
                  <Col span="8">
                    <FormItem label="温控方式验收">
                        <temper-control-select v-model="checkFormItem.checkTempMethod"></temper-control-select>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="验收员">
                        <Input v-model="checkFormItem.checkUser" />
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="验收时间">
                        <DatePicker format="yyyy-MM-dd HH:mm" type="datetime" v-model="checkFormItem.checkTime" />
                    </FormItem>
                  </Col>
              </Row>
              <Row>
                  <Col span="24">
                    <FormItem label="验收结果">
                        <Input v-model="checkFormItem.checkResult" />
                    </FormItem>
                  </Col>
              </Row>
          </Form>
      </Modal>

      <Modal v-model="checkFileModal" title="检验报告档案" :mask-closable="false" width="50">
          <file-detail :fileNo="checkFileNo" @add-file-success="addFileSuccess" ></file-detail>
          <div slot="footer"></div>
      </Modal>

      <warehouse-location-modal :openModal="locationModal" :warehouseId="currentChooseOrder.warehouseId" @on-ok="chooseLocation" @on-close="locationModalClose"></warehouse-location-modal>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment,{ isMoment } from 'moment';
import supplierSelect from "@/views/selector/supplier-select.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import inSurveyCheck from "./in-survey-check.vue";
import temperControlSelect from "@/views/selector/temper-control-select.vue";
import goodSelect from "@/views/selector/good-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";
import warehouseLocationModal from "@/views/selector/warehouse-location-modal.vue";

export default {
    name: 'in-quality-check',
    components: {
        supplierSelect,
        warehouseSelect,
        inSurveyCheck,
        temperControlSelect,
        goodSelect,
        fileDetail,
        warehouseLocationModal
    },
    data() {

        const addWarehouseLocation = (h, location, rowData, index) => {
            let label = location ? location : '';
            return h('div', [
                h('span', label),
                h('Button', {
                    props: {
                        type: 'text',
                        size: 'small',
                        icon: 'edit'
                    },
                    on: {
                        'click': () => {
                            this.openChooseLocation(rowData, index);
                        }
                    }
                })
            ]);
        };

        return {
            statusOptions: [
                {key: 'ALL', name: '所有'},
                {key: 'CHECKING', name: '未验收'},
                {key: 'CHECKED', name: '已验收'}
            ],
            query: {
                warehouseId: '',
                supplierId: '',
                status: 'CHECKING'
            },
            dateRange: [
                moment().add(-1,'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD')
            ],
            orderLoading: false,
            currEditLocationRow: {},
            currDditLocationIndex: '',
            locationModal: false,
            orderList: [],
            orderListColumns: [
                {
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
                    title: '入库方式',
                    key: 'refTypeName',
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
                    key: 'receiveTemp',
                },
                {
                    title: '验收温度',
                    key: 'checkTemp',
                    render: (h, params) => {
                        var self = this;
                        let oldCheckTemp = params.row.checkTemp;
                        return h('Input', {
                            props: {
                                number: true,
                                value : self.orderList[params.index][params.column.key]
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.orderList[params.index];
                                    row[params.column.key] = event.target.value;
                                    if (event.target.value != oldCheckTemp && !isNaN(event.target.value)) {
                                        self.chanageOrderCheckTemp(params.row.id, event.target.value);
                                    }
                                }
                            }
                        });
                    }
                },
                {
                    title: '系统单号',
                    key: 'orderNumber',
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
            currentChooseOrder: {},
            detailLoading: false,
            detailList: [],
            detailColumns: [
                {
                    type: 'index',
                    width: 60
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
                    width: 140,
                    render: (h, params) => {
                        if (params.row.checkStatus) {
                            return h('span', params.row.batchCode);
                        }else {
                            let self = this;
                            return h('Input', {
                                props: {
                                    value : self.detailList[params.index][params.column.key]
                                },
                                on: {
                                    'on-blur' (event) {
                                        let row = self.detailList[params.index];
                                        row[params.column.key] = event.target.value;
                                    }
                                }
                            });
                        }
                    }
                },
                {
                    title: "生产日期",
                    key: 'productDate',
                    width: 140,
                    render: (h, params) => {
                        if (params.row.checkStatus) {
                            return params.row.productDate ? moment(params.row.productDate).format('YYYY-MM-DD') : '';
                        }else {
                            let self = this;
                            return h('DatePicker', {
                                props: {
                                    type: 'date',
                                    placement: 'top',
                                    value: params.row.productDate
                                },
                                on: {
                                    'on-change': (date) =>{
                                        let row = self.detailList[params.index];
                                        row[params.column.key] = date; 
                                    }
                                }
                            });
                        }
                    }
                },
                {
                    title: "有效期至",
                    key: 'expDate',
                    width: 140,
                    render: (h, params) => {
                        if (params.row.checkStatus) {
                            return params.row.expDate ? moment(params.row.expDate).format('YYYY-MM-DD') : '';
                        }else {
                            let self = this;
                            return h('DatePicker', {
                                props: {
                                    type: 'date',
                                    placement: 'top',
                                    value: params.row.expDate
                                },
                                on: {
                                    'on-change': (date) =>{
                                        let row = self.detailList[params.index];
                                        row[params.column.key] = date; 
                                    }
                                }
                            });
                        }
                    }
                },
                {
                    title: '采购数量',
                    key: 'buyOrderQuality',
                    width: 140
                },
                {
                    title: '拒收数量',
                    width: 120,
                    key: 'rejectQuality'
                },
                {
                    title: '拒收原因',
                    width: 150,
                    key: 'rejectComment'
                },
                {
                    title: "收货数量",
                    width: 140,
                    key: 'receiveQuality',
                    render: (h, params) => {
                        if (params.row.checkStatus) {
                            return h('span', params.row.receiveQuality);
                        }else {
                            let self = this;
                            return h('Input', {
                                props: {
                                    number: true,
                                    value : self.detailList[params.index][params.column.key]
                                },
                                on: {
                                    'on-blur' (event) {
                                        let row = self.detailList[params.index];
                                        row[params.column.key] = event.target.value;
                                        row['inCount'] = row[params.column.key];
                                        let price = params.row.price ? params.row.price : 0;
                                        let free = params.row.free ? params.row.free : 0;
                                        let count = (event.target.value - free) > 0 ? (event.target.value - free) : 0;
                                        row['amount'] =  count * price;
                                    }
                                }
                            });
                        }
                    }
                },
                {
                    title: '赠送数量',
                    width: 140,
                    key: 'free'
                },
                {
                    title: '单价',
                    width: 120,
                    key: 'price'
                },
                {
                    title: '金额',
                    width: 140,
                    key: 'amount',
                    render: (h, params) => {
                        var self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.detailList[params.index][params.column.key]
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.detailList[params.index];
                                    row[params.column.key] = event.target.value;
                                }
                            }
                        });
                    }
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
                    title: "抽样数量",
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
                    width: 200,
                    render: (h, params) => {
                        if (params.row.checkStatus) {
                            return h('span', params.row.warehouseLocation);
                        }else {
                            return addWarehouseLocation(h, params.row.warehouseLocation, params.row, params.index);
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
            currChooseDetail: {},
            totalAmount: 0,
            totalReceiveCount: 0,
            totalInCount: 0,
            totalRightCount: 0,
            totalErrorCount: 0,
            totalSurveyQuality: 0,
            surveyModal: false,
            checkDetail: false,
            checkFormItem: {},
            checkModal: false,
            checkFileNo: '',
            checkFileModal: false
        }
    },
    watch: {
        detailList(data) {
            if(!data || data.length <= 0) {
                this.totalAmount = 0;
                this.totalReceiveCount = 0;
                this.totalInCount = 0;
                this.totalRightCount = 0;
                this.totalErrorCount = 0;
                this.totalSurveyQuality = 0;
            }else {
                this.totalAmount = data.reduce((total, item) => {return item.amount ? total + item.amount : total + 0}, 0);
                this.totalReceiveCount = data.reduce((total, item) => {return item.receiveQuality ? total + item.receiveQuality : total + 0}, 0);
                this.totalInCount = data.reduce((total, item) => {return item.inCount ? total + item.inCount : total + 0}, 0);
                this.totalRightCount = data.reduce((total, item) => {return item.rightCount ? total + item.rightCount : total + 0}, 0);
                this.totalErrorCount = data.reduce((total, item) => {return item.errorCount ? total + item.errorCount : total + 0}, 0);
                this.totalSurveyQuality = data.reduce((total, item) => {return item.surveyQuality ? total + item.surveyQuality : total + 0}, 0);
            }
        }
    },
    methods: {
        refreshOrder() {
            let statusList = [];
            if (this.query.status === 'CHECKING') {
                statusList = ['INIT'];
            }else if (this.query.status == 'CHECKED') {
                statusList = ['CHECKED'];
            }else {
                statusList = ['INIT', 'CHECKED'];
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
            this.currChooseDetail = {};
            this.detailList = [];
        },
        chanageOrderCheckTemp(orderId, newCheckTemp) {
            if(!orderId || !newCheckTemp) {
                return;
            }
            let reqData = {
                orderId: orderId,
                checkTemp: newCheckTemp
            };
            util.ajax.put('/repertory/in/set/checkTemp', reqData)
                .then((response) => {
                    this.$Message.success('验收温度设定成功');
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },
        handleSelectOrder(rowData) { 
            if (!rowData || !rowData.id) {
                this.currentChooseOrder = {};
                this.detailList = [];
                return;
            }
            this.currentChooseOrder = rowData;
            this.reloadOrderDetail();
            this.checkFileNo = '';
        },

        handleSelectDetail(rowData) {
            if (!rowData || !rowData.id) {
                this.currChooseDetail = {};
            }else {
                this.currChooseDetail = rowData;
            }
        },

        samplingSurveyBtnClick() {
            if (!this.currChooseDetail || !this.currChooseDetail.id) {
                this.$Message.warning('请先选择需要抽样验收的商品');
                return;
            }
            this.surveyModal = true;
        },
        surveyClose() {
            this.surveyModal = false;
        },
        surveySuccess() {
            this.surveyModal = false;
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
                        this.currChooseDetail = {};
                    }
                })
                .catch((error) => {
                    this.detailLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        checkOneOrderBtn() {
            if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
                this.$Message.warning('请先选择一条需要验收的订单信息');
                return;
            }
            this.checkFormItem = {
                orderId: this.currentChooseOrder.id,
                checkTime: moment().format("YYYY-MM-DD HH:mm")
            }
            this.checkDetail = false;
            this.checkModal = true;
        },
        checkOneDetailBtn() {
            if (!this.currChooseDetail || !this.currChooseDetail.id) {
                this.$Message.warning('请先选择一条需要验收的产品信息');
                return;
            }
            let receiveQuality = this.currChooseDetail.receiveQuality ? this.currChooseDetail.receiveQuality : 0;
            let rightCount = this.currChooseDetail.rightCount ? this.currChooseDetail.rightCount : receiveQuality;
            if(rightCount > receiveQuality) {
                this.$Message.error('合格数量不能大于收货数量');
                return;
            }
            let inCount = rightCount;
            let errorCount = receiveQuality - rightCount;
            this.checkFormItem = {
                detailId: this.currChooseDetail.id,
                buyOrderQuality: this.currChooseDetail.buyOrderQuality,
                receiveQuality: receiveQuality,
                surveyQuality: this.currChooseDetail.surveyQuality,
                inCount: rightCount,
                rightCount: rightCount,
                errorCount: errorCount,
                checkTime: moment().format("YYYY-MM-DD HH:mm")
            }
            this.checkDetail = true;
            this.checkModal = true;
        },
        checkRightCountChange() {
            let receiveQuality = this.checkFormItem.receiveQuality;
            let rightCount = this.checkFormItem.rightCount ? this.checkFormItem.rightCount : receiveQuality;
            let inCount = rightCount;
            let errorCount = receiveQuality - rightCount;
            this.checkFormItem.inCount = inCount;
            this.checkFormItem.errorCount = errorCount;
        },
        checkCancel() {
            this.checkFormItem = {
                orderId: '',
                detailId: ''
            };
            this.checkModal = false;
        },
        checkSuccess() {
            if (this.checkFormItem.checkTime) {
                this.checkFormItem.checkTime = moment(this.checkFormItem.checkTime, 'YYYY-MM-DD HH:mm');
            }
            this.detailLoading = true;
            util.ajax.put('/repertory/in/set/check', this.checkFormItem)
                .then((response) => {
                    this.detailLoading = false;
                    this.$Message.success('验收成功');
                    this.reloadOrderDetail();
                })
                .catch((error) => {
                    this.detailLoading = false;
                    util.errorProcessor(this, error);
                })
        },
        unCheckOneOrderBtn() {
            if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
                this.$Message.warning('请先选择需要取消验证的订单');
                return;
            }
            util.ajax.put('/repertory/in/set/check/order/' + this.currentChooseOrder.id)
                .then((response) => {
                    this.$Message.success('取消成功');
                    this.reloadOrderDetail();
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        },
        unCheckOneDetailBtn() {
            if (!this.currChooseDetail || !this.currChooseDetail.id) {
                this.$Message.warning('请先选择需要取消验证的订单');
                return;
            }
            util.ajax.put('/repertory/in/set/check/detail/' + this.currChooseDetail.id)
                .then((response) => {
                    this.$Message.success('取消成功');
                    this.reloadOrderDetail();
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        },

        removeDetail() {
            if (!this.currChooseDetail || !this.currChooseDetail.id) {
                this.$Message.warning('请先选择需要删除的商品记录');
                return;
            }
            if (this.currChooseDetail.checkStatus) {
                this.$Message.warning('已经验证通过的数据不能删除.');
                return;
            }
            let detailItem = this.currChooseDetail;
            this.$Modal.confirm({
                title: '删除确认',
                content: '是否确认删除商品:' + detailItem.goodsName  + '，删除后不可恢复!',
                onOk: () => {
                    util.ajax.delete('/repertory/in/detail/remove/' + detailItem.id)
                        .then((response) => {
                            this.$Message.success('删除成功');
                            this.reloadOrderDetail();
                        })
                        .catch((error) => {util.errorProcessor(this, error);})
                },
                onCancel: () => {}
            });
        },

        saveOroderDetail() {
            if(!this.detailList || this.detailList.length <= 0) {
                this.$Message.warning('订单没有商品数据需要保存');
                return;
            }
            if(!this.currentChooseOrder || !this.currentChooseOrder.id) {
                this.$Message.warning('获取选定的订单失败，请重新选择订单');
                return;
            }
            let reqData = {
                orderId: this.currentChooseOrder.id,
                detailList: this.detailList
            };
            this.detailLoading = true;
            util.ajax.post('/repertory/in/set/save/detail', reqData)
                .then((response) => {
                    this.detailLoading = false;
                    this.$Message.success('保存成功');
                    this.reloadOrderDetail();
                })
                .catch((error) => {
                    this.detailLoading = false; 
                    util.errorProcessor(this, error);
                })
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
        },

        openChooseLocation(rowData, index) {
                if (!rowData || index === undefined || index < 0) {
                    this.$Message.error('获取编辑位置失败');
                    return;
                }
                this.currEditLocationRow = rowData;
                this.currDditLocationIndex = index;
                this.locationModal = true;
            },

            chooseLocation(data) {
                this.locationModal = false;
                this.currEditLocationRow.warehouseLocation = data.location;
                this.$set(this.detailList, this.currDditLocationIndex, this.currEditLocationRow);
            },

            locationModalClose() {
                this.locationModal = false;
                this.currDditLocationIndex = '';
                this.currEditLocationRow = {};
            }
    }
}
</script>

<style>
.ivu-form-item {
    margin-bottom: 5px;
}
.detail-div {
    margin-top: 10px;
}
.detail-count-content {
    margin-left: 10px;
}
.detail-count-content-b {
    margin-left: 40px;
}
.add-goods-class {
    margin-left: 10px;
    width: 300px;
}
</style>

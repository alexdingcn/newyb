
<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="shuffle"></Icon>
              往来账记账
          </p>
          <div slot="extra" >
              <Row type="flex" justify="end">
              </Row>
                <ButtonGroup size="small">
                    <Button type="primary" icon="search" :loading="loading" @click="refreshFlowsData">查询</Button>
                    <Button type="info" icon="bookmark" :loading="loading" @click="recordAction" >记账</Button>
                    <Button type="success" icon="arrow-right-c" :loading="loading" @click="receiveAction" >收款</Button>
                    <Button type="warning" icon="arrow-left-c" :loading="loading" @click="payAction" >付款</Button>
                    <Button type="error" icon="android-close" :loading="loading" @click="cancelAction" >取消操作</Button>
                    <Button type="ghost" icon="edit" :loading="loading" @click="editAction" >修改资料</Button>
                </ButtonGroup>
          </div>
          <Row type="flex" justify="start" style="margin-bottom: 20px;">
                <Col span="5">
                    <span>发生日期:</span>
                    <DatePicker v-model="logDateRange" type="daterange" placement="bottom-start" placeholder="发生日期" style="width:180px"></DatePicker>
                </Col>
                <Col span="5">
                    <span>记账日期:</span>
                    <DatePicker v-model="recordDateRange" type="daterange" placement="bottom-start" placeholder="记账日期" style="width:180px"></DatePicker>
                </Col>
                <Col span="6">
                    <span>往来账户:</span>
                    <Input type="text" v-model="custName" style="width:220px" readonly icon="close-circled" @on-focus="openChooseCustModal" @on-click="clearSearchCust"></Input>
                </Col>
           </Row>
        
            <Table ref="flowTable" highlight-row size="small" border :row-class-name="flowRowClass" :loading="loading" 
                :columns="flowsColumns" :data="flowsData" @on-row-click="chooseFlowRow">
            </Table>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
      </Card>

      <Modal v-model="custModal" :mask-closable="false" title="选择往来账户" >
          <Tabs style="height: 400px;">
            <TabPane label="客户" name="customer">
                <customer-select ref="customerSelect" @on-change="searchCustomerChange"></customer-select>
            </TabPane>
            <TabPane label="供应商" name="supplier">
                <supplier-select ref="supplierSelect" @on-change="searchSupplierChange"></supplier-select>
            </TabPane>
          </Tabs>
          <div slot="footer"></div>
      </Modal>

      <Modal v-model="actionModal" width="45" :mask-closable="false" :title="actionTitle" >
          <Form ref="actionForm" :model="actionFormItem" :label-width="100">
            <Row v-if="actionType != 'RECORD'">
                <Col span="24">
                    <FormItem label="关联的往来账流水号" :label-width="150" prrop="bizRefNo">
                        <Input type="text" readonly v-model="actionFormItem.bizRefNo" icon="close-circled" @on-click="clearBizRefNo"/><br/>
                        <strong style="color:red;">提示:如需要关联具体的某一笔流水进行收/付款操作, 请先选中对应流水记录后再操作, 如不需要关联，请清除关联的流水号!</strong>
                    </FormItem>
                </Col>
            </Row> 
            <Row>
                <Col span="12">
                    <FormItem label="账户类型" prrop="custType" :error="actionCustTypeError">
                        <Select v-model="actionFormItem.custType" filterable clearable placeholder="请选择客户类型" @on-change="custTypeChange">
                            <Option v-for="custTypeItem in allCustType" :key="custTypeItem.custType" :value="custTypeItem.custType">{{custTypeItem.label}}</Option>
                        </Select>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="往来账户" prop="custId" v-if="actionFormItem.custType" :error="actionCustIdError">
                        <customer-select v-model="actionFormItem.custId" v-if="actionFormItem.custType==='CUSTOMER'" @on-change="actionCustomerChange"></customer-select>
                        <supplier-select  v-model="actionFormItem.custId" v-if="actionFormItem.custType==='SUPPLIER'" @on-change="actionSupplierChange"></supplier-select>
                    </FormItem>
                </Col>
            </Row>
            <Row>
                <Col span="12">
                    <FormItem label="账户账号" prrop="custAccount">
                        <Input v-model="actionFormItem.custAccount" />
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="账户余额" prop="custAmount">
                        <Input v-model="actionFormItem.custAmount" readonly />
                    </FormItem>
                </Col>
            </Row>
            <Row>
                <Col span="12">
                    <FormItem  v-if="actionType === 'RECORD'" label="记账方式" prrop="bizType">
                        <Select v-model="actionFormItem.bizType" filterable clearable placeholder="请选择记账方式" >
                            <Option v-for="item in allRecordType" :key="item.bizType" :value="item.bizType">{{item.label}}</Option>
                        </Select>
                    </FormItem>
                    <FormItem v-if="actionType != 'RECORD'" label="收/付款方式" prrop="payMethod">
                        <option-select optionType="PAY_METHOD" v-model="actionFormItem.payMethod"></option-select>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="发生金额" prop="logAmount" :error="actionLogAmountError">
                        <Input v-model="actionFormItem.logAmount" number />
                    </FormItem>
                </Col>
            </Row>
            <Row >
                <Col span="12">
                    <FormItem label="发生日期" prrop="logDate" :error="actionLogDateError">
                        <DatePicker type="date" v-model="actionFormItem.logDate"></DatePicker>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="结算账号" prop="companyAccount">
                        <Input v-model="actionFormItem.companyAccount" />
                    </FormItem>
                </Col>
            </Row>
            <Row >
                <Col span="12">
                    <FormItem label="凭证号" prrop="docNo">
                        <Input type="text" v-model="actionFormItem.docNo" />
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="档案编号" prop="fileNo">
                        <Input v-model="actionFormItem.fileNo">
                            <Button slot="append" type="text" icon="upload" @click="updateFileFor('ACTION', actionFormItem.fileNo)"></Button>
                        </Input>
                    </FormItem>
                </Col>
            </Row>
            <Row >
                <Col span="24">
                    <FormItem label="摘要" prrop="keyWord">
                        <Input v-model="actionFormItem.keyWord" />
                    </FormItem>
                </Col>
            </Row>
          </Form>

          <Row slot="footer" type="flex" justify="end">
              <ButtonGroup shape="circle" size="small">
                  <Button type="success" icon="checkmark" @click="actionSave" :loading="actionLoading" >确认保存</Button>
                  <Button type="ghost" icon="reply" @click="actionCancel">取消</Button>
              </ButtonGroup>
          </Row>
      </Modal>

      <Modal v-model="cancelModal" width="45" :mask-closable="false" :title="cancelModalTitle" >
          <Form ref="cancelForm" :model="cancelFormItem" :label-width="100">
            <Row>
                <Col span="16">
                    <FormItem label="取消的往来账流水号" :label-width="150" >
                        <Input type="text" readonly v-model="cancelFormItem.bizNo" readonly />
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="记账类型">
                        <Select v-model="cancelFormItem.bizType" disabled >
                            <Option v-for="item in allBizType" :key="item.bizType" :value="item.bizType">{{item.label}}</Option>
                        </Select>
                    </FormItem>
                </Col>
            </Row> 
            <Row>
                <Col span="12">
                    <FormItem label="账户类型" prrop="custType">
                        <Select v-model="cancelFormItem.custType" disabled >
                            <Option v-for="custTypeItem in allCustType" :key="custTypeItem.custType" :value="custTypeItem.custType">{{custTypeItem.label}}</Option>
                        </Select>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="往来账户" >
                        <Input v-model="cancelFormItem.logAccount" readonly />
                    </FormItem>
                </Col>
            </Row>
            <Row>
                <Col span="12">
                    <FormItem label="账户账号" prrop="custAccount">
                        <Input v-model="cancelFormItem.custAccount" readonly/>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="账户余额" prop="custAmount">
                        <Input v-model="cancelFormItem.custAmount" readonly />
                    </FormItem>
                </Col>
            </Row>
            <Row>
                <Col span="12">
                    <FormItem label="发生金额" prop="logAmount" >
                        <Input v-model="cancelFormItem.logAmount" number readonly />
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="发生日期" >
                        <DatePicker type="date" v-model="cancelFormItem.logDate" disabled></DatePicker>
                    </FormItem>
                </Col>
            </Row>
            <Row >
                <Col span="12">
                    <FormItem label="应收">
                        <Input v-model="cancelFormItem.inAmount" readonly/>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="应付" >
                        <Input v-model="cancelFormItem.outAmount" readonly/>
                    </FormItem>
                </Col>
            </Row>
            <Row >
                <Col span="12">
                    <FormItem label="结算账号" prop="companyAccount">
                        <Input v-model="cancelFormItem.companyAccount" readonly/>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="摘要" prrop="keyWord">
                        <Input v-model="cancelFormItem.keyWord" readonly/>
                    </FormItem>
                </Col>
            </Row>
            <Row >
                <Col span="12">
                    <FormItem label="凭证号" prrop="docNo">
                        <Input type="text" v-model="cancelFormItem.docNo" readonly/>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="档案编号" prop="fileNo">
                        <Input v-model="cancelFormItem.fileNo" readonly></Input>
                    </FormItem>
                </Col>
            </Row>
            <h3 style="magin-top:20px;">录入取消操作的信息</h3>
            <hr size="1" style="width: 90%; margin-bottom:10px;"/>
            <Row >
                <Col span="12">
                    <FormItem label="凭证号">
                        <Input type="text" v-model="cancelDocNo" />
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="档案编号">
                        <Input v-model="cancelFileNo">
                            <Button slot="append" type="text" icon="upload" @click="updateFileFor('CANCEL', cancelFileNo)"></Button>
                        </Input>
                    </FormItem>
                </Col>
            </Row>
            <Row >
                <Col span="24">
                    <FormItem label="摘要">
                        <Input v-model="cancelKeyWord" />
                    </FormItem>
                </Col>
            </Row>
          </Form>

          <Row slot="footer" type="flex" justify="end">
              <ButtonGroup shape="circle" size="small">
                  <Button type="success" icon="checkmark" @click="cancelSave" :loading="cancelLoading" >确认提交</Button>
                  <Button type="ghost" icon="reply" @click="cancelActionCancel">取消</Button>
              </ButtonGroup>
          </Row>
      </Modal>

      <Modal v-model="updateModal" width="45" :mask-closable="false" title="维护往来账信息" >
          <Form ref="updateForm" :model="updateFormItem" :label-width="100">
                <Row>
                    <Col span="16">
                        <FormItem label="取消的往来账流水号" :label-width="150" >
                            <Input type="text" readonly v-model="updateFormItem.bizNo" readonly />
                        </FormItem>
                    </Col>
                    <Col span="8">
                        <FormItem label="记账类型">
                            <Select v-model="updateFormItem.bizType" disabled >
                                <Option v-for="item in allBizType" :key="item.bizType" :value="item.bizType">{{item.label}}</Option>
                            </Select>
                        </FormItem>
                    </Col>
                </Row> 
                <Row>
                    <Col span="12">
                        <FormItem label="账户类型" prrop="custType">
                            <Select v-model="updateFormItem.custType" disabled >
                                <Option v-for="custTypeItem in allCustType" :key="custTypeItem.custType" :value="custTypeItem.custType">{{custTypeItem.label}}</Option>
                            </Select>
                        </FormItem>
                    </Col>
                    <Col span="12">
                        <FormItem label="往来账户" >
                            <Input v-model="updateFormItem.logAccount" readonly />
                        </FormItem>
                    </Col>
                </Row>
                <h3 style="margin-top:20px;">可维护信息</h3>
                <hr size="1" style="width: 90%; margin-bottom:10px;"/>
                <Row >
                    <Col span="12">
                        <FormItem label="结算账号" prop="companyAccount">
                            <Input v-model="updateFormItem.companyAccount"/>
                        </FormItem>
                    </Col>
                    <Col span="12">
                        <FormItem label="往来账户账号" prrop="custAccount">
                            <Input v-model="updateFormItem.custAccount" />
                        </FormItem>
                    </Col>
                </Row>
                <Row >
                    <Col span="12">
                        <FormItem label="凭证号">
                            <Input type="text" v-model="updateFormItem.docNo" />
                        </FormItem>
                    </Col>
                    <Col span="12">
                        <FormItem label="档案编号">
                            <Input v-model="updateFormItem.fileNo" readonly>
                                <Button slot="append" type="text" icon="upload" @click="updateFileFor('UPDATE', updateFormItem.fileNo)"></Button>
                            </Input>
                        </FormItem>
                    </Col>
                </Row>
                <Row >
                    <Col span="12">
                        <FormItem label="摘要">
                            <Input type="text" v-model="updateFormItem.keyWord" />
                        </FormItem>
                    </Col>
                </Row>
          </Form>

            <Row slot="footer" type="flex" justify="end">
                <ButtonGroup shape="circle" size="small">
                    <Button type="success" icon="checkmark" @click="updateSave" :loading="updateLoading" >确认提交</Button>
                    <Button type="ghost" icon="reply" @click="updateActionCancel">取消</Button>
                </ButtonGroup>
            </Row>
      </Modal>

      <Modal v-model="fileUploadModal" title="档案上传" :mask-closable="false" width="50" class="file-upload-modal">
            <file-detail :fileNo="uploadfileNo" @add-file-success="addFileSuccess" ></file-detail>
            <div slot="footer"></div>
        </Modal>

  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import customerSelect from "@/views/selector/customer-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";

export default {
    name: 'financial-pre-paid',
    components: {
        customerSelect,
        supplierSelect,
        optionSelect,
        fileDetail
    },
    data() {
        return {
            allBizType: [
                {bizType: 'BUY_IN', label: '采购入库', cancel: false},
                {bizType: 'BUY_BACK', label: '采购退货', cancel: false},
                {bizType: 'SELL_BACK', label: '销售退货', cancel: false},
                {bizType: 'SELL_BATCH', label: '批发销售', cancel: false},
                {bizType: 'SELL_BACK_FREE', label: '销售退货免零', cancel: false},
                {bizType: 'RECEIVE', label: '收款', cancel: true},
                {bizType: 'PAY', label: '付款', cancel: true},
                {bizType: 'PRE_PAID', label: '预付款', cancel: true},
                {bizType: 'PRE_RECEIVE', label: '预收款', cancel: true},
                {bizType: 'RECORD_RECEIVE', label: '记账应收', cancel: true},
                {bizType: 'RECORD_PAY', label: '记账应付', cancel: true},
                {bizType: 'OFFSET', label: '冲销', cancel: false},
                {bizType: 'RECEIVE_CANCEL', label: '收款取消', cancel: false},
                {bizType: 'PAY_CANCEL', label: '付款取消', cancel: false},
                {bizType: 'PRE_PAID_CANCEL', label: '预付款取消', cancel: false},
                {bizType: 'PRE_RECEIVE_CANCEL', label: '预收款取消', cancel: false},
                {bizType: 'RECORD_RECEIVE_CANCEL', label: '记账应收取消', cancel: false},
                {bizType: 'RECORD_PAID_CANCEL', label: '记账应付取消', cancel: false}
            ],
            allCustType: [{custType: 'CUSTOMER', label: '客户'}, {custType: 'SUPPLIER', label: '供应商'}],
            allRecordType: [
                {bizType: 'RECORD_RECEIVE', label: '记账应收'},
                {bizType: 'RECORD_PAY', label: '记账应付'}
            ],
            logDateRange: [
                moment().add(-1, 'M').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD')
            ],
            recordDateRange: [
                moment().add(-1, 'M').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD')
            ],
            fileUploadModal: false,
            uploadfileNo: '',
            updateType: '',
            custType: '',
            custName: '',
            custId: '',
            totalCount: 0,
            currentPage: 1,
            pageSize: 50,
            loading: false,
            flowsData: [],
            flowsColumns: [
                {
                    title: '#',
                    type: 'index',
                    width: 100
                },
                {
                    title: '流水号',
                    key: 'bizNo',
                    width: 200,
                },
                {
                    title: '发生日期',
                    key: 'logDate',
                    sortable: true,
                    width: 120,
                    render: (h, params) => {
                        return params.row.logDate ? moment(params.row.logDate).format('YYYY-MM-DD') : '';
                    }
                },
                {
                    title: '往来账户',
                    key: 'logAccount',
                    sortable: true,
                    width: 200
                },
                {
                    title: '记账类型',
                    key: 'bizType',
                    width: 120,
                    render: (h, params) => {
                        let label = this.bizTypeLabel(params.row.bizType);
                        return h('span', label);
                    }
                },
                {
                    title: '关联流水号',
                    key: 'bizRefNo',
                    width: 200,
                },
                {
                    title: '摘要',
                    key: 'keyWord',
                    width: 170
                },
                {
                    title: '发生金额',
                    key: 'logAmount',
                    width: 120
                },
                {
                    title: '应收',
                    key: 'inAmount',
                    width: 120
                },
                {
                    title: '应付',
                    key: 'outAmount',
                    width: 120
                },
                {
                    title: '应收账款余额',
                    key: 'surplusInAmount',
                    width: 160
                },
                {
                    title: '应付账款余额',
                    key: 'surplusOutAmount',
                    width: 160
                },
                {
                    // 大于0代表客户欠公司的钱，小于0代表公司欠客户钱
                    title: '往来账户余额',
                    key: 'custAmount',
                    width: 150
                },
                {
                    title: '收/付款方式',
                    key: 'payMethodName',
                    width: 110
                },
                {
                    title: '结算账号',
                    key: 'companyAccount',
                    width: 170
                },
                {
                    title: '往来账户账号',
                    key: 'custAccount',
                    width: 170
                },
                {
                    title: '操作员',
                    key: 'createdBy',
                    width: 120,
                },
                {
                    title: '记账时间',
                    sortable: true,
                    key: 'createdTime',
                    width: 140,
                    render: (h, params) => {
                        return params.row.createdTime ? moment(params.row.createdTime).format('YYYY-MM-DD HH:mm') : '';
                    }
                },
                {
                    title: '记账凭证号',
                    key: 'docNo',
                    width: 170
                },
                {
                    title: '档案编号',
                    key: 'fileNo',
                    width: 170,
                    render: (h, params) => {
                        let fileNo = params.row.fileNo;
                        if (!fileNo) {
                            return '';
                        }else {
                            return h('Button',{
                                props: {
                                    type: 'text'
                                },
                                on: {
                                    'click': () => {
                                        this.updateFileFor('SEE', fileNo);
                                    }
                                }
                            }, fileNo);
                        }
                        
                    }
                }
            ],
            currChooseFlow: {},
            custModal: false,
            actionTitle: '',
            actionModal: false,
            actionFormItem: {
                bizRefId: '',
                bizRefNo: '',
                custId: '',
                logAccount: '',
                custAccount: '',
                custAmount: '',
                bizType: '',
                logDate: moment().format('YYYY-MM-DD')
            },
            actionLoading: false,
            actionType: '',
            actionCustIdError: '',
            actionCustTypeError: '',
            actionLogAmountError: '',
            actionLogDateError: '',
            cancelFormItem: {},
            cancelDocNo: '',
            cancelFileNo: '',
            cancelKeyWord: '',
            cancelLoading: false,
            cancelModal: false,
            cancelModalTitle: '',
            updateModal: false,
            updateFormItem: {},
            updateLoading: false
        }
    },
    mounted() {
        this.refreshFlowsData();
    },
    methods: {
        flowRowClass(row, index) {
            let bizType = row.bizType;
            switch(bizType) {
                case 'OFFSET':
                case 'RECEIVE_CANCEL':
                case 'PAY_CANCEL':
                case 'PRE_PAID_CANCEL': 
                case 'PRE_RECEIVE_CANCEL':
                case 'RECORD_RECEIVE_CANCEL': 
                case 'RECORD_PAID_CANCEL': 
                    return 'flow-table-red-row';
            }
        },
        bizTypeLabel(bizType) {
            if (!bizType) {
                return '';
            }
            for (let i=0; i< this.allBizType.length; i++) {
                let item = this.allBizType[i];
                if (item['bizType'] === bizType) {
                    return this.allBizType[i].label;
                }
            }
            return '';
        },
        clearSearchCust() {
            this.custType = '';
            this.custId = '';
            this.custName = '';
        },
        pageChange(data) {
            this.currentPage = data;
            this.refreshFlowsData();
        },
        refreshFlowsData() {
            let reqData = {
                logStartDate: this.logDateRange[0],
                logEndDate: this.logDateRange[1],
                createdStartTime: this.recordDateRange[0],
                createdEndTime: this.recordDateRange[1],
                custType: this.custType,
                custId: this.custId,
                page: this.currentPage,
                pageSize: this.pageSize
            };
            this.loading = true;
            util.ajax.post('/financial/flow/list', reqData) 
                .then((response) => {
                    this.loading = false;
                    this.totalCount = response.data.count;
                    this.flowsData = response.data.data;
                    this.currChooseFlow = {};
                    this.$refs.flowTable.clearCurrentRow();
                })
                .catch((error) => {
                    this.loading = false;
                    util.errorProcessor(this, error);
                });
        },
        chooseFlowRow(row) {
            this.currChooseFlow = row;
        },
        openChooseCustModal() {
            this.custModal = true;
        },
        searchCustomerChange(id, cust) {
            if (cust.id) {
                this.custId = cust.id;
                this.custName = cust.name;
                this.custType = 'CUSTOMER';
                this.custModal = false;
            }else {
                this.custId = '';
                this.custName = '';
                this.custType = '';
            }
        },
        searchSupplierChange(id, cust) {
            if (cust.id) {
                this.custId = cust.id;
                this.custName = cust.name;
                this.custType = 'SUPPLIER';
                this.custModal = false;
            }else {
                this.custId = '';
                this.custName = '';
                this.custType = '';
            }
        },
        recordAction() {
            this.actionFormItem = {
                bizRefId: '',
                bizRefNo: '',
                custId: '',
                logAccount: '',
                custAccount: '',
                custAmount: '',
                logDate: moment().format('YYYY-MM-DD'),
                bizType: ''
            };
            this.actionTitle = '记账';
            this.actionModal = true;
            this.actionType = 'RECORD';
        },
        receiveAction() {
            this.actionFormItem = {
                bizRefId: this.currChooseFlow.id,
                bizRefNo: this.currChooseFlow.bizNo,
                custId: '',
                logAccount: '',
                custAccount: '',
                custAmount: '',
                logDate: moment().format('YYYY-MM-DD'),
                bizType: 'RECEIVE'
            };
            this.actionTitle = '收款';
            this.actionModal = true;
            this.actionType = 'RECEIVE';
        },
        payAction() {
            this.actionFormItem = {
                bizRefId: this.currChooseFlow.id,
                bizRefNo: this.currChooseFlow.bizNo,
                custId: '',
                logAccount: '',
                custAccount: '',
                custAmount: '',
                logDate: moment().format('YYYY-MM-DD'),
                bizType: 'PAY'
            };
            this.actionTitle = '付款';
            this.actionModal = true;
            this.actionType = 'PAY';
        },
        findOneOnBizTypes(bizType) {
            for(let i=0; i<this.allBizType.length; i++) {
                let item = this.allBizType[i];
                if (item.bizType === bizType) {
                    return item;
                }
            }
            return null;
        },
        cancelAction() {
            if (!this.currChooseFlow || !this.currChooseFlow.id) {
                this.$Modal.warning({
                    title: '操作提示',
                    content: '请先选中需要取消的流水信息！'
                });
                return;
            }
            //判断当前流水是否可以做取消操作
            let bizTypeItem = this.findOneOnBizTypes(this.currChooseFlow.bizType);
            if (!bizTypeItem || !bizTypeItem.cancel) {
                this.$Modal.warning({
                    title: '操作提示',
                    content: '当前流水类型：' + bizTypeItem.label + '不能做取消操作.'
                });
                return;
            }
            //打开显示流水信息模板
            this.cancelModalTitle = '取消->' + bizTypeItem.label + ', 流水号:' + this.currChooseFlow.bizNo;
            this.cancelModal = true;
            this.cancelFormItem = this.currChooseFlow;
        },
        editAction() {
            if (!this.currChooseFlow || !this.currChooseFlow.id) {
                this.$Modal.warning({
                    title: '操作提示',
                    content: '请先选中需要修改的流水信息！'
                });
                return;
            }
            this.updateFormItem = this.currChooseFlow;
            this.updateModal = true;
        },
        updateActionCancel() {
            this.updateFormItem = {};
            this.updateModal = false;
        },
        actionCancel() {
            this.actionTitle = '';
            this.actionModal = false;
            this.actionFormItem = {
                bizRefId: '',
                bizRefNo: '',
                custId: '',
                logAccount: '',
                custAccount: '',
                custAmount: '',
                logDate: moment().format('YYYY-MM-DD'),
                bizType: ''
            };
        },
        cancelActionCancel() {
            this.cancelFormItem = {};
            this.cancelDocNo = '';
            this.cancelFileNo = '';
            this.cancelKeyWord = '';
            this.cancelModalTitle = '';
            this.cancelModal = false;
        },
        custTypeChange(data) {
            this.actionFormItem.custId = '';
            this.actionFormItem.logAccount = '';
            this.actionFormItem.custAccount = '';
            this.actionFormItem.custAmount = '';
        },
        actionCustomerChange(customerId, customer) {
            if (customer.id) {
                this.actionFormItem.custId = customer.id;
                this.actionFormItem.logAccount = customer.name;
                this.actionFormItem.custAccount = customer.bankAccount;
                this.actionFormItem.custAmount = customer.accountAmount ? customer.accountAmount : 0;
            }else {
                this.actionFormItem.custId = '';
                this.actionFormItem.logAccount = '';
                this.actionFormItem.custAccount = '';
                this.actionFormItem.custAmount = '';
            }
        },
        actionSupplierChange(supplierId, supplier) {
            if (supplier.id) {
                this.actionFormItem.custId = supplier.id;
                this.actionFormItem.logAccount = supplier.name;
                this.actionFormItem.custAccount = supplier.bankNumber;
                this.actionFormItem.custAmount = supplier.accountAmount ? supplier.accountAmount : 0;
            }else {
                this.actionFormItem.custId = '';
                this.actionFormItem.logAccount = '';
                this.actionFormItem.custAccount = '';
                this.actionFormItem.custAmount = '';
            }
        },
        clearBizRefNo() {
            this.actionFormItem.bizRefId = '';
            this.actionFormItem.bizRefNo = '';
        },
        updateFileFor(updateType, fileNo) {
            this.updateType = updateType;
            this.fileUploadModal = true;
            this.uploadfileNo = fileNo;
        },
        addFileSuccess(data) {
            if (this.updateType === 'ACTION') {
                this.actionFormItem.fileNo = data.fileNo;
            }else if (this.updateType === 'CANCEL') {
                this.cancelFileNo = data.fileNo;
            }else if (this.updateType === 'UPDATE') {
                this.updateFormItem.fileNo = data.fileNo;
            }
        },
        isBizTypeOk(bizType) {
            if (!bizType) {
                return false;
            }
            for (let i=0; i<this.allBizType.length; i++) {
                let item = this.allBizType[i];
                if (item.bizType === bizType) {
                    return true;
                }
            }
            return false;
        },
        actionSave() {
            this.actionCustIdError = '';
            this.actionCustTypeError = '';
            this.actionLogAmountError = '';
            this.actionLogDateError = '';
            //只验证部分必输项后，直接提交给后台校验
            if(!this.isBizTypeOk(this.actionFormItem.bizType)) {
                this.$Message.error('获取具体业务类型失败.');
                return;
            }
            if (!this.actionFormItem.custId ||!this.actionFormItem.logAccount) {
                this.actionCustIdError = "获取往来账户信息失败";
                this.$Message.warning('获取往来账户信息失败');
                return;
            }
            if (this.actionFormItem.custType != 'CUSTOMER' && this.actionFormItem.custType != 'SUPPLIER') {
                this.actionCustTypeError = '获取往来账户类型失败';
                return;
            }
            if (!this.actionFormItem.logAmount || this.actionFormItem.logAmount <= 0) {
                this.actionLogAmountError = '发生金额不能为空和必须大于0';
                return;
            }
            if (!this.actionFormItem.logDate) {
                this.actionFormItem.actionLogDateError = '发生日期必输';
                return;
            }
            this.actionLoading = true;
            let tipContent = '是否确认输入的数据正确，并且提交保存？';
            if (this.actionFormItem.bizType === 'RECORD_RECEIVE') {
                tipContent = '是否确认输入的数据正确，对账户：' + this.actionFormItem.logAccount + '做一笔应收款记账？';
            }else if (this.actionFormItem.bizType === 'RECORD_PAY') {
                tipContent = '是否确认输入的数据正确，对账户：' + this.actionFormItem.logAccount + '做一笔应付款记账？';
            }else if (this.actionFormItem.bizType === 'RECEIVE') {
                tipContent = '是否确认输入的数据正确，已经确认收到账户：' + this.actionFormItem.logAccount + this.actionFormItem.logAmount + '的金额？';
            }else if (this.actionFormItem.bizType === 'PAY') {
                tipContent = '是否确认输入的数据正确，已经确认付给账户：' + this.actionFormItem.logAccount + this.actionFormItem.logAmount + '的金额？';
            }
            let self = this;
            this.$Modal.confirm({
                title: '保存确认',
                content: tipContent,
                onCancel: () => {self.actionLoading = false;},
                onOk: () => {
                    util.ajax.post('/financial/flow/add', self.actionFormItem)
                        .then((response) => {
                            self.actionLoading = false;
                            self.$Message.success('保存成功');
                            self.actionModal = false;
                            self.refreshFlowsData();
                        })
                        .catch((error) => {
                            self.actionLoading = false;
                            util.errorProcessor(self, error);
                        });
                }
            });
        },
        cancelSave() {
            if (!this.cancelFormItem.id) {
                this.$Message.error('获取取消的账务流水失败，请重新获取.');
                return;
            }
            let contentLabel = '是否确定需要取消往来账：' + this.cancelFormItem.bizNo + '？';
            let self = this;
            this.cancelLoading = true;
            this.$Modal.confirm({
                title: '取消操作确认',
                content: contentLabel,
                onCancel:()=>{self.cancelLoading = false;},
                onOk:() => {
                    let reqData = {
                        id: self.cancelFormItem.id,
                        docNo: self.cancelDocNo,
                        fileNo: self.cancelFileNo,
                        keyWord: self.cancelFileNo
                    };
                    util.ajax.post('/financial/flow/cancel', reqData)
                        .then((response) => {
                            self.cancelLoading = false;
                            self.$Message.success('保存成功');
                            self.cancelModal = false;
                            self.refreshFlowsData();
                        })
                        .catch((error) => {
                            self.cancelLoading = false;
                            util.errorProcessor(self, error);
                        })
                }
            });
        },
        updateSave() {
            if (!this.updateFormItem.id) {
                this.$Message.error('获取修改的账务流水失败，请重新获取.');
                return;
            }
            let self = this;
            self.updateLoading = true;
            this.$Modal.confirm({
                title: '修改操作确认',
                content: '是否已经确认修改的已经正确填写完成？',
                onCancel: () => {self.updateLoading = false;},
                onOk:() => {
                    util.ajax.post('/financial/flow/update', this.updateFormItem)
                        .then((response) => {
                            self.updateLoading = false;
                            self.$Message.success('保存成功');
                            self.updateModal = false;
                            self.refreshFlowsData();
                        })
                        .catch((error) => {
                            self.updateLoading = false;
                            util.errorProcessor(self, error);
                        });
                }
            });
        }
        
    }
}
</script>

<style>
.ivu-table td {
    height: 2.5em;
}
.ivu-table .flow-table-red-row td{
    color: red;
}
.file-upload-modal {
    position: fixed;
    z-index: 3000;
}

</style>



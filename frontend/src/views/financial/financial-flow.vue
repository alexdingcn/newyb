
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
                            <Button slot="append" type="text" icon="upload" @click="updateFileFor('ACTION')"></Button>
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
                  <Button type="success" icon="checkmark" @click="actionSave" >确认保存</Button>
                  <Button type="ghost" icon="reply" @click="actionCancel">取消</Button>
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
                {bizType: 'BUY_IN', label: '采购入库'},
                {bizType: 'BUY_BACK', label: '采购退货'},
                {bizType: 'SELL_BACK', label: '销售退货'},
                {bizType: 'SELL_BATCH', label: '批发销售'},
                {bizType: 'RECEIVE', label: '收款'},
                {bizType: 'PAY', label: '付款'},
                {bizType: 'PRE_PAID', label: '预付款'},
                {bizType: 'PRE_RECEIVE', label: '预收款'},
                {bizType: 'RECORD_RECEIVE', label: '记账应收'},
                {bizType: 'RECORD_PAY', label: '记账应付'},
                {bizType: 'OFFSET', label: '冲销'},
                {bizType: 'RECEIVE_CANCEL', label: '收款取消'},
                {bizType: 'PAY_CANCEL', label: '付款取消'},
                {bizType: 'PRE_PAID_CANCEL', label: '预付款取消'},
                {bizType: 'PRE_RECEIVE_CANCEL', label: '预收款取消'},
                {bizType: 'RECORD_RECEIVE_CANCEL', label: '记账应收取消'},
                {bizType: 'RECORD_PAID_CANCEL', label: '记账应付取消'}
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
                    width: 170
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
            actionType: '',
            actionCustIdError: '',
            actionCustTypeError: '',
            actionLogAmountError: '',
            actionLogDateError: '',
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
        cancelAction() {
            
        },
        editAction() {
            
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
        updateFileFor(updateType) {
            this.updateType = updateType;
            this.fileUploadModal = true;
        },
        addFileSuccess(data) {
            if (this.updateType === 'ACTION') {
                this.actionFormItem.fileNo = data.fileNo;
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
            this.loading = true;
            console.log(this.actionFormItem);
            this.$Modal.confirm({
                title: '保存确认',
                content: '是否确认输入的数据正确，并且提交保存？',
                onCancel: () => {},
                onOk: () => {
                    util.ajax.post('/financial/flow/add', this.actionFormItem)
                        .then((response) => {
                            this.loading = false;
                            this.$Message.success('保存成功');
                            this.actionModal = false;
                            this.refreshFlowsData();
                        })
                        .catch((error) => {
                            this.loading = false;
                            util.errorProcessor(this, error);
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



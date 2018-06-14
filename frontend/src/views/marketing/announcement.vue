<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="ios-compose-outline"></Icon>
              首页广告位
          </p>
          <div slot="extra">
              <ButtonGroup size="small">
                  <Button type="primary" icon="search" :loading="loading" @click="refreshTableData">查询</Button>
                  <Button type="success" icon="plus" :loading="loading" @click="addBtnClick">添加</Button>
                  <Button type="warning" icon="shuffle" :loading="loading" @click="offsetBtnClick">冲销</Button>
                  <Button type="error" icon="close" :loading="loading" @click="cancelBtnClick">取消预收款</Button>
              </ButtonGroup>
          </div>

          <Row type="flex" justify="start" style="margin-bottom: 20px;">
                <i-col span="6">
                    <span>发生日期:</span>
                    <DatePicker v-model="logDateRange" type="daterange" placement="bottom-start" placeholder="发生日期" style="width:180px"></DatePicker>
                </i-col>
                <i-col span="7">
                    <span>客户:</span>
                    <customer-select v-model="searchCustId" style="width:220px"></customer-select>
                </i-col>
                <i-col span="6">
                    <span>状态:</span>
                    <Select v-model="searchStatus" style="width:180px">
                        <Option v-for="item in statusList" :key="item.value" :value="item.value">{{item.label}}</Option>
                    </Select>
                </i-col>
           </Row>

           <Table ref="preTable" highlight-row size="small" border :loading="loading" 
                :columns="tableColumns" :data="tableData" @on-row-click="chooseRow">
            </Table>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>

            <Modal v-model="addRecordModal" title="新建预收款" :mask-closable="false" width="45" >
                <Form ref="addForm" :model="addFormItem" :label-width="100">
                    <Row >
                        <i-col span="12">
                            <FormItem label="发生日期" error="addLogDateError">
                                <DatePicker type="date" v-model="addFormItem.logDate"></DatePicker>
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="客户" error="addCustIdError">
                                <customer-select v-model="addFormItem.custId"  @on-change="addCustomerChange"></customer-select>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row >
                        <i-col span="12">
                            <FormItem label="客户账号">
                                <Input v-model="addFormItem.custAccount" />
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="客户余额" >
                                <Input v-model="addFormItem.custAmount" readonly/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row >
                        <i-col span="12">
                            <FormItem label="发生金额" error="addLogAmountError">
                                <Input v-model="addFormItem.logAmount" number />
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="支收方式" >
                                <option-select optionType="PAY_METHOD" v-model="addFormItem.payMethod"></option-select>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row >
                        <i-col span="12">
                            <FormItem label="凭证号" >
                                <Input v-model="addFormItem.docNo" />
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="档案文件" >
                                <Input v-model="addFormItem.fileNo" readonly icon="upload" @on-click="openUploadFile('ADD', addFormItem.fileNo)" />
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row >
                        <i-col span="24">
                            <FormItem label="摘要" >
                                <Input v-model="addFormItem.keyWord" />
                            </FormItem>
                        </i-col>
                    </Row>
                </Form>

                <Row slot="footer" type="flex" justify="end">
                    <ButtonGroup shape="circle" size="small">
                        <Button type="success" icon="checkmark" @click="actionSave" :loading="addSubmitLoading" >确认保存</Button>
                        <Button type="ghost" icon="reply" @click="addCancel">取消</Button>
                    </ButtonGroup>
                </Row>
            </Modal>

            <Modal v-model="offsetModal" title="预收款冲销" :mask-closeable="false" width="40">
                <Form ref="offsetForm" :model="offsetFormItem" :label-width="150">
                    <Row>
                        <i-col span="12">
                            <FormItem label="做冲销的预收款流水">
                                <Input v-model="offsetFormItem.bizNo" readonly />
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="客户">
                                <Input v-model="offsetFormItem.custName" readonly />
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="12">
                            <FormItem label="冲销金额">
                                <Input v-model="offsetFormItem.logAmount" readonly />
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="客户账户余额">
                                <Input v-model="offsetFormItem.custAmount" readonly />
                            </FormItem>
                        </i-col>
                    </Row>
                    <h3 style="margin-top:20px;">冲销关联信息</h3>
                    <hr size="1" style="width:90%;margin-bottom:10px;" />
                    <Row >
                        <i-col span="24">
                            <FormItem label="冲销的往来账流水号" :label-width="150">
                                <Input v-model="offsetFormItem.refBizNo" /><br/>
                                <strong style="color:red;">提示:建议关联往来账流水，比如销售出库的往来账流水号, 也可以没有关联, 直接对账户做冲销交易.</strong>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row >
                        <i-col span="24">
                            <FormItem label="冲销流水的摘要信息" :label-width="150">
                                <Input v-model="offsetFormItem.keyWord" />
                            </FormItem>
                        </i-col>
                    </Row>
                </Form>
                <Row slot="footer" type="flex" justify="end">
                    <ButtonGroup shape="circle" size="small">
                        <Button type="success" icon="checkmark" @click="offsetSubmit" :loading="offsetSubmitLoading" >确认保存</Button>
                        <Button type="ghost" icon="reply" @click="offsetModalCancel">取消</Button>
                    </ButtonGroup>
                </Row>
            </Modal>

            <Modal v-model="fileUploadModal" title="档案上传" :mask-closable="false" width="50" class="file-upload-modal">
                <file-detail :fileNo="uploadfileNo" @add-file-success="addFileSuccess" ></file-detail>
                <div slot="footer"></div>
            </Modal>

      </Card>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import customerSelect from "@/views/selector/customer-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";

export default {
  name: "financial-pre-paid",
  components: {
    customerSelect,
    optionSelect,
    fileDetail
  },
  data() {
    return {
      statusList: [
        { value: "INIT", label: "未使用" },
        { value: "OFFSET", label: "已冲销" },
        { value: "CANCEL", label: "已取消" }
      ],
      logDateRange: [
        moment()
          .add(-1, "M")
          .format("YYYY-MM-DD"),
        moment().format("YYYY-MM-DD")
      ],
      searchCustId: "",
      searchStatus: "INIT",
      loading: false,
      currChooseRow: {},
      tableData: [],
      tableColumns: [
        {
          type: "index",
          title: "序号",
          width: 80
        },
        {
          title: "流水号",
          key: "bizNo",
          width: 200
        },
        {
          title: "发生日期",
          key: "logDate",
          sortable: true,
          width: 120,
          render: (h, params) => {
            return h(
              "span",
              params.row.logDate
                ? moment(params.row.logDate).format("YYYY-MM-DD")
                : ""
            );
          }
        },
        {
          title: "往来账户",
          key: "custName",
          sortable: true,
          width: 200
        },
        {
          title: "预收款金额",
          key: "logAmount",
          width: 120
        },
        {
          title: "支收方式",
          key: "payMethodName",
          width: 110
        },
        {
          title: "状态",
          key: "status",
          width: 140,
          render: (h, params) => {
            let status = params.row.status;
            let label = "";
            let color = "";
            switch (status) {
              case "INIT":
                label = "未使用";
                color = "#19be6b";
                break;
              case "OFFSET":
                label = "已冲销";
                color = "#ff9900";
                break;
              case "CANCEL":
                label = "已取消";
                color = "#ed3f14";
                break;
            }
            return h(
              "Tag",
              {
                props: {
                  type: "dot",
                  color: color
                }
              },
              label
            );
          }
        },
        {
          title: "客户账号",
          key: "custAccount",
          width: 170
        },
        {
          title: "摘要",
          key: "keyWord",
          width: 170
        },
        {
          title: "记账时间",
          sortable: true,
          key: "createdTime",
          width: 140,
          render: (h, params) => {
            return h(
              "span",
              params.row.createdTime
                ? moment(params.row.createdTime).format("YYYY-MM-DD HH:mm")
                : ""
            );
          }
        },
        {
          title: "记账凭证号",
          key: "docNo",
          width: 170
        },
        {
          title: "档案编号",
          key: "fileNo",
          width: 170,
          render: (h, params) => {
            let fileNo = params.row.fileNo;
            if (!fileNo) {
              return "";
            } else {
              return h(
                "Button",
                {
                  props: {
                    type: "text"
                  },
                  on: {
                    click: () => {
                      this.updateFileFor("SEE", fileNo);
                    }
                  }
                },
                fileNo
              );
            }
          }
        },
        {
          title: "操作员",
          key: "createdBy",
          width: 120
        }
      ],
      totalCount: 0,
      currentPage: 1,
      pageSize: 50,
      fileUploadModal: false,
      uploadfileNo: "",
      uploadFileType: "",
      addSubmitLoading: false,
      addRecordModal: false,
      addFormItem: {
        logDate: moment().format("YYYY-MM-DD"),
        custId: "",
        custName: "",
        custAccount: "",
        custAmount: ""
      },
      addLogDateError: "",
      addCustIdError: "",
      addLogAmountError: "",
      offsetModal: false,
      offsetSubmitLoading: false,
      offsetFormItem: {
        bizType: "PRE_RECEIVE",
        preRecordId: "",
        refBizNo: "",
        keyWord: "",
        bizNo: "",
        custName: "",
        logAmount: "",
        custAmount: ""
      }
    };
  },
  mounted() {
    this.refreshTableData();
  },
  methods: {
    pageChange(data) {
      this.currentPage = data;
      this.refreshTableData();
    },
    refreshTableData() {
      let reqData = {
        logStartDate: this.logDateRange[0],
        logEndDate: this.logDateRange[1],
        custId: this.searchCustId,
        status: this.searchStatus,
        page: this.currentPage,
        pageSize: this.pageSize,
        preBizType: "PRE_RECEIVE"
      };
      this.loading = true;
      util.ajax
        .post("/financial/pre/list", reqData)
        .then(response => {
          this.loading = false;
          this.tableData = response.data.data;
          this.totalCount = response.data.count;
          this.currChooseRow = {};
          this.$refs.preTable.clearCurrentRow();
        })
        .catch(error => {
          this.loading = false;
          util.errorProcessor(this, error);
        });
    },
    chooseRow(row) {
      this.currChooseRow = row;
    },
    openUploadFile(type, fileNo) {
      this.uploadFileType = type;
      this.uploadfileNo = fileNo;
      this.fileUploadModal = true;
    },
    addFileSuccess(data) {
      if (this.uploadFileType === "ADD") {
        this.addFormItem.fileNo = data.fileNo;
      }
    },
    addCustomerChange(id, customer) {
      this.addFormItem.custId = customer.id;
      this.addFormItem.custName = customer.name;
      (this.addFormItem.custAccount = customer.bankAccount),
        (this.addFormItem.custAmount = customer.accountAmount);
    },

    addBtnClick() {
      this.addFormItem = {
        logDate: moment().format("YYYY-MM-DD"),
        custId: "",
        custName: "",
        custAccount: "",
        custAmount: ""
      };
      this.addLogDateError = "";
      this.addCustIdError = "";
      this.addLogAmountError = "";
      this.addRecordModal = true;
    },

    addCancel() {
      this.addFormItem = {
        logDate: moment().format("YYYY-MM-DD"),
        custId: "",
        custName: "",
        custAccount: "",
        custAmount: ""
      };
      this.addLogDateError = "";
      this.addCustIdError = "";
      this.addLogAmountError = "";
      this.addRecordModal = false;
    },

    actionSave() {
      this.addLogDateError = "";
      this.addCustIdError = "";
      this.addLogAmountError = "";
      if (!this.addFormItem.logDate) {
        this.addLogDateError = "发生日期必输";
        return;
      }
      if (!this.addFormItem.custId) {
        this.addCustIdError = "客户必须选择";
        return;
      }
      if (!this.addFormItem.logAmount || this.addFormItem.logAmount <= 0) {
        this.addLogAmountError = "发生金额必输且大于0";
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "预收款添加确认",
        content:
          "是否已经确认收到客户:" +
          self.addFormItem.custName +
          "金额" +
          self.addFormItem.logAmount +
          "元",
        onCancel: () => {},
        onOk: () => {
          self.addSubmitLoading = true;
          this.addFormItem.preBizType = "PRE_RECEIVE"; //固定
          util.ajax
            .post("/financial/pre/add", self.addFormItem)
            .then(response => {
              self.addSubmitLoading = false;
              self.$Message.success("添加预收款记录成功");
              self.addRecordModal = false;
              self.refreshTableData();
            })
            .catch(error => {
              self.addSubmitLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    },

    cancelBtnClick() {
      if (!this.currChooseRow.id) {
        this.$Message.warning("请先选择需要取消的预收款记录");
        return;
      }
      if (this.currChooseRow.status !== "INIT") {
        this.$Modal.warning({
          title: "取消操作提醒",
          content: "只有处于未使用状态的预收款才能进行取消操作!"
        });
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "取消操作确认",
        content:
          "是否确认取消:" +
          self.currChooseRow.bizNo +
          "这笔预收款，取消后不能再做其他操作！",
        onCancel: () => {},
        onOk: () => {
          self.loading = true;
          self.currChooseRow.preBizType = "PRE_RECEIVE";
          util.ajax
            .post("/financial/pre/cancel", self.currChooseRow)
            .then(response => {
              self.loading = false;
              self.$Message.success("取消操作保存成功");
              self.refreshTableData();
            })
            .catch(error => {
              self.loading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    },

    offsetBtnClick() {
      if (!this.currChooseRow || !this.currChooseRow.id) {
        this.$Message.warning("请先选择需要做冲销交易的预收款记录.");
        return;
      }
      //先根据custId获取供应商信息
      let self = this;
      util.ajax
        .get("/customer/" + this.currChooseRow.custId)
        .then(response => {
          let customer = response.data.customer;
          if (!customer || !customer.id) {
            self.$Message.warning("获取客户信息失败.");
            return;
          } else {
            //获取到客户信息打开冲销的面板
            self.offsetFormItem = {
              bizType: "PRE_RECEIVE",
              preRecordId: self.currChooseRow.id,
              refBizNo: "",
              keyWord: "",
              bizNo: self.currChooseRow.bizNo,
              custName: customer.name,
              logAmount: self.currChooseRow.logAmount,
              custAmount: customer.accountAmount
            };
            self.offsetModal = true;
          }
        })
        .catch(error => {
          util.errorProcessor(self, error);
        });
    },

    offsetModalCancel() {
      this.offsetFormItem = {
        bizType: "PRE_RECEIVE",
        preRecordId: "",
        refBizNo: "",
        keyWord: "",
        bizNo: "",
        custName: "",
        logAmount: "",
        custAmount: ""
      };
      this.offsetModal = false;
    },

    offsetSubmit() {
      if (
        this.offsetFormItem.bizType !== "PRE_RECEIVE" ||
        !this.offsetFormItem.preRecordId
      ) {
        this.$Modal.error({
          title: "系统异常",
          content: "获取需要冲销的流水信息失败."
        });
        return;
      }
      //判断下当前流水是否处于未使用状态
      if (this.currChooseRow.status !== "INIT") {
        this.$Modal.warning({
          title: "冲销交易提示",
          content: "只有处于未使用状态的预收款才能进行冲销操作."
        });
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "冲销数据确认",
        content:
          "是否确认对客户：" +
          self.offsetFormItem.custName +
          "冲销" +
          self.offsetFormItem.logAmount +
          "元预收款, 冲销后不可取消",
        onCancel: () => {},
        onOk: () => {
          self.offsetSubmitLoading = true;
          util.ajax
            .post("/financial/pre/offset", self.offsetFormItem)
            .then(response => {
              self.offsetSubmitLoading = false;
              self.$Message.success("冲销交易保存成功");
              self.offsetModalCancel();
              self.refreshTableData();
            })
            .catch(error => {
              self.offsetSubmitLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    }
  }
};
</script>

<style >
.ivu-table td {
  height: 2.5em;
}
.file-upload-modal {
  position: fixed;
  z-index: 1000;
}
</style>



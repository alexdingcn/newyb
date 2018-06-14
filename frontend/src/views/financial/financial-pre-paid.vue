<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="ios-compose-outline"></Icon>
              预付款登记
          </p>
          <div slot="extra">
              <ButtonGroup >
                  <Button type="primary" icon="search" :loading="loading" @click="refreshTableData">查询</Button>
                  <Button type="success" icon="plus" :loading="loading" @click="addBtnClick">添加</Button>
              </ButtonGroup>
          </div>

          <Form ref="searchForm" :label-width="100">
            <Row class="row-margin-bottom">
                <i-col span="6">
                    <FormItem label="发生日期">
                        <DatePicker v-model="logDateRange" type="daterange" placement="bottom-start" placeholder="发生日期" ></DatePicker>
                    </FormItem>
                </i-col>
                <i-col span="6">
                    <FormItem label="供应商">
                        <supplier-select v-model="searchCustId" style="width:220px"></supplier-select>
                    </FormItem>
                </i-col>
                <i-col span="6">
                    <FormItem label="状态">
                        <Select v-model="searchStatus" style="width:180px">
                            <Option v-for="item in statusList" :key="item.value" :value="item.value">{{item.label}}</Option>
                        </Select>
                    </FormItem>
                </i-col>
            </Row>
          </Form>

           <Table ref="preTable" highlight-row size="small" border :loading="loading" 
                :columns="tableColumns" :data="tableData" class="table-action">
            </Table>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>

            <Modal v-model="addRecordModal" title="新建预付款" :mask-closable="false" width="45" >
                <Form ref="addForm" :model="addFormItem" :label-width="100">
                    <Row >
                        <i-col span="12">
                            <FormItem label="发生日期" error="addLogDateError">
                                <DatePicker type="date" v-model="addFormItem.logDate"></DatePicker>
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="供应商" error="addCustIdError">
                                <supplier-select v-model="addFormItem.custId" ref="addSupplierSelect" @on-change="addSupplierChange"></supplier-select>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row >
                        <i-col span="12">
                            <FormItem label="供应商账号">
                                <Input v-model="addFormItem.custAccount" />
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="供应商余额" >
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
                            <FormItem label="支付方式" >
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
                    <ButtonGroup shape="circle" >
                        <Button type="success" icon="checkmark" @click="actionSave" :loading="addSubmitLoading" >确认保存</Button>
                        <Button type="ghost" icon="reply" @click="addCancel">取消</Button>
                    </ButtonGroup>
                </Row>
            </Modal>

            <Modal v-model="offsetModal" title="预付款冲销" :mask-closeable="false" width="40">
                <Form ref="offsetForm" :model="offsetFormItem" :label-width="150">
                    <Row class="row-margin-bottom">
                        <i-col span="12">
                            <FormItem label="做冲销的预付款流水">
                                <Input v-model="offsetFormItem.bizNo" readonly />
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="供应商">
                                <Input v-model="offsetFormItem.custName" readonly />
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row class="row-margin-bottom">
                        <i-col span="12">
                            <FormItem label="冲销金额">
                                <Input v-model="offsetFormItem.logAmount" readonly />
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="供应商账户余额">
                                <Input v-model="offsetFormItem.custAmount" readonly />
                            </FormItem>
                        </i-col>
                    </Row>
                    <h4>冲销流水的摘要信息</h4>
                    <div class="row-margin-bottom">
                        <Input type="textarea" v-model="offsetFormItem.keyWord" :rows="2" placeholder="请输入冲销流水的摘要信息"/>
                    </div>
                    <Alert type="error">
                      <h4 style="color:red;">提示: 冲销流水的摘要信息，建议记录一下冲销金额针对的是那些交易流水, 或者记录订单的系统编号, 用于记录当前冲销流水涉及的的交易。</h4>
                      <p style="margin-top:0.5em; margin-bottom: 0.5em">如下图获取往来账交易流水号</p>
                      <img src="@/images/financial-pre-eg.png" style="width: 99%;" />
                    </Alert>
                </Form>
                <Row slot="footer" type="flex" justify="end">
                    <ButtonGroup shape="circle">
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
import supplierSelect from "@/views/selector/supplier-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";
import actionButton from "@/views/my-components/action-button.vue";

export default {
  name: "financial-pre-paid",
  components: {
    supplierSelect,
    optionSelect,
    fileDetail,
    actionButton
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
          width: 200,
          render: (h, params) => {
            let status = params.row.status;
            let buttonArr = [
              {
                type: "warning",
                icon: "shuffle",
                label: "冲销",
                disabled: status != "INIT",
                data: params.row,
                action: this.offsetBtnClick,
                param: params.row
              },
              {
                type: "error",
                icon: "close",
                label: "取消预付款",
                disabled: status != "INIT",
                data: params.row,
                action: this.cancelBtnClick,
                param: params.row
              }
            ];
            return h("div", [
              h("span", params.row.bizNo),
              h(actionButton, {
                class: { rowAction: true },
                props: {
                  data: buttonArr
                }
              })
            ]);
          }
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
          title: "预付款金额",
          key: "logAmount",
          width: 120
        },
        {
          title: "支付方式",
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
          title: "供应商账号",
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
        bizType: "PRE_PAID",
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
        preBizType: "PRE_PAID"
      };
      this.loading = true;
      util.ajax
        .post("/financial/pre/list", reqData)
        .then(response => {
          this.loading = false;
          this.tableData = response.data.data;
          this.totalCount = response.data.count;
          this.$refs.preTable.clearCurrentRow();
        })
        .catch(error => {
          this.loading = false;
          util.errorProcessor(this, error);
        });
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
    addSupplierChange(id, supplier) {
      this.addFormItem.custId = supplier.id;
      this.addFormItem.custName = supplier.name;
      (this.addFormItem.custAccount = supplier.bankNumber),
        (this.addFormItem.custAmount = supplier.accountAmount);
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
        this.addCustIdError = "供应商必须选择";
        return;
      }
      if (!this.addFormItem.logAmount || this.addFormItem.logAmount <= 0) {
        this.addLogAmountError = "发生金额必输且大于0";
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "预付款添加确认",
        content:
          "是否已经确认付给供应商:" +
          self.addFormItem.custName +
          "金额" +
          self.addFormItem.logAmount +
          "元",
        onCancel: () => {},
        onOk: () => {
          self.addSubmitLoading = true;
          this.addFormItem.preBizType = "PRE_PAID"; //固定
          util.ajax
            .post("/financial/pre/add", self.addFormItem)
            .then(response => {
              self.addSubmitLoading = false;
              self.$Message.success("添加预付款记录成功");
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

    cancelBtnClick(rowData) {
      if (!rowData) {
        this.$Message.warning("请先选择需要取消的预付款记录");
        return;
      }
      if (rowData.status !== "INIT") {
        this.$Modal.warning({
          title: "取消操作提醒",
          content: "只有处于未使用状态的预付款才能进行取消操作!"
        });
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "取消操作确认",
        content:
          "是否确认取消:" +
          rowData.bizNo +
          "这笔预付款，取消后不能再做其他操作！",
        onCancel: () => {},
        onOk: () => {
          self.loading = true;
          rowData.preBizType = "PRE_PAID";
          util.ajax
            .post("/financial/pre/cancel", rowData)
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

    offsetBtnClick(rowData) {
      if (!rowData || !rowData.id) {
        this.$Message.warning("获取冲销交易的预付款记录失败");
        return;
      }
      //先根据custId获取供应商信息
      let self = this;
      util.ajax
        .get("/supplier/" + rowData.custId)
        .then(response => {
          let supplier = response.data;
          if (!supplier || !supplier.id) {
            self.$Message.warning("获取供应商信息失败.");
            return;
          } else {
            //获取到供应商信息打开冲销的面板
            self.offsetFormItem = {
              bizType: "PRE_PAID",
              preRecordId: rowData.id,
              keyWord: "",
              bizNo: rowData.bizNo,
              custName: supplier.name,
              logAmount: rowData.logAmount,
              custAmount: supplier.accountAmount
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
        bizType: "PRE_PAID",
        preRecordId: "",
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
        this.offsetFormItem.bizType !== "PRE_PAID" ||
        !this.offsetFormItem.preRecordId
      ) {
        this.$Modal.error({
          title: "系统异常",
          content: "获取需要冲销的流水信息失败."
        });
        return;
      }
      let self = this;
      this.$Modal.confirm({
        title: "冲销数据确认",
        content:
          "是否确认对供应商：" +
          self.offsetFormItem.custName +
          "冲销" +
          self.offsetFormItem.logAmount +
          "元预付款, 冲销后不可取消",
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



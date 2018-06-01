<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row>
          <Row>
            <div class="margin-bottom-10">
                <strong class="margin-left-5 margin-right-10">档案类型</strong>
                <Select v-model="fileTypeSelVal" clearable style="width:200px" @on-change="fileTypeSelectChange">
                    <Option v-for="item in fileTypeList" :value="item.typeName" :key="item.id">{{ item.typeName }}</Option>
                </Select>
                <Button type="primary" size="small" icon="plus" @click="addFileTypeBtnClick">新建档案类型</Button>
            </div>
          </Row>
          <Row>
            <i-col span="10">
                <Card>
                    <p slot="title">
                        <Icon type="ios-paper"></Icon> 档案信息 
                    </p>
                    <ButtonGroup size="small" slot="extra">
                        <Button type="primary" icon="android-add-circle" @click="addFileInfoBtnClick">添加</Button>
                        <Button type="error" :disabled="delFileInfoBtnDisable" 
                            :loading="delFileInfoBtnLoading" 
                            icon="android-remove-circle" @click="delFileInfoBtnClick">删除</Button>
                    </ButtonGroup>

                    <Row type="flex" justify="center">
                        <i-input v-model="searchValue">
                            <Select v-model="searchSelectVal" slot="prepend" style="width: 100px;" >
                                <Option value="byFileName">名称</Option>
                                <Option value="byFileNo">编号</Option>
                            </Select>
                            <Button slot="append" icon="ios-search" @click="searchBtnClick"></Button>
                        </i-input>
                    </Row>
                    
                    <Table ref="fileTable" border highlight-row :loading="fileInfoTabLoading"
                        :columns="fileTabColumns" :data="fileTabData" size="small" 
                        @on-row-click="fileInfoTabRowClick" 
                        @on-selection-change="fileInfoTabSelecttionChange">
                    </Table>
                    <Page :total="fileInfoTotalCount" size="small" show-total :current="fileInfoTabCurrPage" 
                        :page-size="fileInfoTabPageSize" @on-change="fileInfoTabPageChange"></Page>
                </Card>
            </i-col>
            <i-col span="14">
                <file-detail :fileNo="editFileNo" @add-file-success="loadFileInfoList"></file-detail>
            </i-col>
          </Row>
      </Row>

      <Modal v-model="fileTypeModalVisible" title="新建档案类型" :closable="false">
          <Row type="flex" justify="center">
            <Form ref="fileTypeForm" :model="fileTypeFormItem" :rules="fileTypeRuleValidate" label-position="right" :label-width="100">
                <FormItem label="档案类型名称" prop="typeName">
                    <Input type="text" v-model="fileTypeFormItem.typeName" placeholder="请输入档案类型名称"/>
                </FormItem>
            </Form>
          </Row>
          <div slot="footer">
            <Row >
                <i-col span="6" offset="6">
                    <Button type="primary" :loading="fileTypeSubmitLoading" @click="fileTypeSubmit" long>
                        <span v-if="!fileTypeSubmitLoading">提交</span>
                        <span v-else>正在提交...</span>
                    </Button>
                </i-col>
                <i-col span=6 class="padding-left-10">
                    <Button @click="closedfileTypeModal" long>取消</Button>
                </i-col>
            </Row>
          </div>
      </Modal>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";
import fileView from "./file-view.vue";
import fileDetail from "./file-detail.vue";

export default {
  name: "basic_data_file",
  components: {
    fileView,
    fileDetail
  },
  data() {
    return {
      fileTypeList: [],
      fileTypeModalVisible: false,
      searchSelectVal: "byFileName",
      searchValue: "",
      fileTypeSelVal: "",
      fileTypeFormItem: {
        typeName: ""
      },
      fileTypeRuleValidate: {
        typeName: [
          { required: true, message: "档案类型名称必输", trigger: "blur" }
        ]
      },
      fileTypeSubmitLoading: false,
      fileInfoTabLoading: false,
      fileInfoTotalCount: 0,
      fileInfoTabPageSize: 20,
      fileInfoTabCurrPage: 1,
      fileTabData: [],
      fileTabColumns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          type: "index",
          title: "序号",
          align: "center",
          width: 70
        },
        {
          title: "档案编号",
          key: "fileNo",
          align: "center",
          sortable: true
        },
        {
          title: "档案名称",
          key: "fileName",
          align: "center",
          sortable: true
        }
      ],
      delFileInfoBtnDisable: true,
      delFileInfoBtnLoading: false,
      editFileNo: "",
      fileInfoTabSelectedData: []
    };
  },
  mounted() {
    this.loadFileTypeList();
    this.addFileInfoBtnClick();
  },
  watch: {
    fileInfoTabSelectedData() {
      if (
        this.fileInfoTabSelectedData &&
        this.fileInfoTabSelectedData.length > 0
      ) {
        this.delFileInfoBtnDisable = false;
      } else {
        this.delFileInfoBtnDisable = true;
      }
    }
  },
  methods: {
    loadFileTypeList() {
      util.ajax
        .get("/file/filetype/list")
        .then(response => {
          this.fileTypeList = response.data;
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    addFileTypeBtnClick() {
      this.fileTypeModalVisible = true;
    },

    fileTypeSubmit() {
      this.$refs.fileTypeForm.validate(valid => {
        if (!valid) {
          this.$Message.info("档案类型名称必输");
        } else {
          this.fileTypeSubmitLoading = true;
          util.ajax
            .post("/file/filetype/add", this.fileTypeFormItem)
            .then(response => {
              this.fileTypeList = response.data;
              this.fileTypeModalVisible = false;
              this.loadFileTypeList();
            })
            .catch(error => {
              util.errorProcessor(this, error);
            });
          this.fileTypeSubmitLoading = false;
        }
      });
    },

    closedfileTypeModal() {
      this.$refs.fileTypeForm.resetFields();
      this.fileTypeModalVisible = false;
    },

    fileTypeSelectChange(data) {
      this.fileTypeSelVal = data;
      this.loadFileInfoList();
    },

    loadFileInfoList() {
      let reqData = {
        page: this.fileInfoTabCurrPage,
        size: this.fileInfoTabPageSize
      };
      if (this.fileTypeSelVal && this.fileTypeSelVal != "") {
        reqData.fileType = this.fileTypeSelVal;
      }
      if (this.searchSelectVal === "byFileName" && this.searchValue) {
        reqData.fileName = this.searchValue;
      }
      if (this.searchSelectVal === "byFileNo" && this.searchValue) {
        reqData.fileNo = this.searchValue;
      }
      this.fileInfoTabLoading = true;
      util.ajax
        .get("/file/list", { params: reqData })
        .then(response => {
          this.fileTabData = response.data.data;
          this.fileInfoTotalCount = response.data.count;
          this.fileInfoTabSelectedData = [];
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
      this.fileInfoTabLoading = false;
    },

    searchBtnClick() {
      this.loadFileInfoList();
    },

    fileInfoTabPageChange(data) {
      this.fileInfoTabCurrPage = data;
      this.loadFileInfoList();
    },

    addFileInfoBtnClick() {
      this.editFileNo = "";
    },

    delFileInfoBtnClick() {
      if (
        this.fileInfoTabSelectedData &&
        this.fileInfoTabSelectedData.length > 0
      ) {
        this.delFileInfoBtnLoading = true;
        this.fileInfoTabLoading = true;
        let delIds = [];
        for (let i = 0; i < this.fileInfoTabSelectedData.length; i++) {
          delIds.push(this.fileInfoTabSelectedData[i].id);
        }
        util.ajax
          .post("/file/remove", delIds)
          .then(response => {
            this.$Message.success("成功删除" + response.data.count + "条记录");
            this.loadFileInfoList();
          })
          .catch(error => {
            util.errorProcessor(this, error);
          });
        this.delFileInfoBtnLoading = false;
        this.fileInfoTabLoading = false;
        this.delFileInfoBtnDisable = true;
        this.editFileNo = "";
      } else {
        this.$Message.info("请先选择需要删除的档案信息");
      }
    },

    fileInfoTabRowClick(data) {
      if (data && data.id && data.id > 0 && data.fileNo) {
        this.editFileNo = data.fileNo;
      } else {
        this.editFileNo = "";
      }
    },

    fileInfoTabSelecttionChange(data) {
      this.fileInfoTabSelectedData = data;
    }
  }
};
</script>

<style >
</style>



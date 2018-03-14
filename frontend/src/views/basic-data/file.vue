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
            <Col span="11">
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
                        <Input v-model="searchValue">
                            <Select v-model="searchSelectVal" slot="prepend" style="width: 100px;" >
                                <Option value="byFileName">名称</Option>
                                <Option value="byFileNo">编号</Option>
                            </Select>
                            <Button slot="append" icon="ios-search" @click="searchBtnClick"></Button>
                        </Input>
                    </Row>
                    
                    <Table ref="fileTable" border highlight-row :loading="fileInfoTabLoading"
                        :columns="fileTabColumns" :data="fileTabData" size="small" 
                        @on-row-click="fileInfoTabRowClick" 
                        @on-selection-change="fileInfoTabSelecttionChange">
                    </Table>
                    <Page :total="fileInfoTotalCount" size="small" show-total :current="fileInfoTabCurrPage" 
                        :page-size="fileInfoTabPageSize" @on-change="fileInfoTabPageChange"></Page>
                </Card>
            </Col>
            <Col span="13">
                <Card>
                    <p slot="title">
                        <Icon type="document"></Icon> {{fileInforCardTitleName}}
                    </p>
                    <div slot="extra">
                        <Button type="primary" :disabled="fileInfoFormEditDisable" :loading="fileInfoSubmitLoading" @click="submitFileInfoBtnClick">提交</Button>
                    </div>

                    <Row type="flex" justify="center">
                        <Form ref="fileInfoForm" :model="fileInfoFormItem" :rules="fileInfoFormValidate" label-position="right" :label-width="100" style="width:70%;">
                            <FormItem label="档案类型" prop="fileType">
                                <Select v-model="fileInfoFormItem.fileType" :disabled="fileInfoFormEditDisable">
                                    <Option v-for="item in fileTypeList" :value="item.typeName" :key="item.id">{{ item.typeName }}</Option>
                                </Select>
                            </FormItem>
                            <FormItem label="档案编号">
                                <Input type="text" v-model="fileInfoFormItem.fileNo" placeholder="由系统自动生成" readonly></Input>
                            </FormItem>
                            <FormItem label="档案名称" prop="fileName">
                                <Input type="text" v-model="fileInfoFormItem.fileName" :readonly="fileInfoFormEditDisable"></Input>
                            </FormItem>
                            <FormItem label="备注">
                                <Input type="text" v-model="fileInfoFormItem.comment" :readonly="fileInfoFormEditDisable"></Input>
                            </FormItem>
                            <FormItem label="操作员">
                                <Input type="text" v-model="fileInfoFormItem.updateBy" readonly></Input>
                            </FormItem>
                            <FormItem label="操作时间">
                                <DatePicker type="datetime" v-model="fileInfoFormItem.updateTime" format="yyyy-MM-dd HH:mm" readonly></DatePicker>
                            </FormItem>
                        </Form>
                    </Row>
                    <Row >
                        <Button type="primary" :disabled="!fileInfoFormEditDisable" size="small" 
                            @click="addFileUploadBtnClick">
                            <Icon type="upload"></Icon>
                            上传档案文件
                        </Button>
                    </Row>
                    <Table ref="uploadTable" border highlight-row 
                        :columns="uploadTabColumns" :data="uploadTabData" size="small">
                    </Table>
                </Card>
            </Col>
          </Row>
      </Row>

      <Modal v-model="fileTypeModalVisible" title="新建档案类型" :closable="false">
          <Row type="flex" justify="center">
            <Form ref="fileTypeForm" :model="fileTypeFormItem" :rules="fileTypeRuleValidate" label-position="right" :label-width="100">
                <FormItem label="档案类型名称" prop="typeName">
                    <Input type="text" v-model="fileTypeFormItem.typeName" placeholder="请输入档案类型名称"></Input>
                </FormItem>
            </Form>
          </Row>
          <div slot="footer">
            <Row >
                <Col span="6" offset="6">
                    <Button type="primary" :loading="fileTypeSubmitLoading" @click="fileTypeSubmit" long>
                        <span v-if="!fileTypeSubmitLoading">提交</span>
                        <span v-else>正在提交...</span>
                    </Button>
                </Col>
                <Col span=6 class="padding-left-10">
                    <Button @click="closedfileTypeModal" long>取消</Button>
                </Col>
            </Row>
          </div>
      </Modal>

      <Modal v-model="fileUploadModalVisible" title="上传档案文件" :closable="false">
          <Row type="flex" justify="center">
              <Form ref="fileUploadForm" :model="fileUploadFormData" label-position="right" :label-width="100">
                  <FormItem label="描述信息">
                    <Input type="text" v-model="fileUploadFormData.comment" placeholder="请输入描述信息"></Input>
                  </FormItem>
              </Form>
          </Row>
          <Row type="flex" justify="center">
            <Upload ref="uploadComment" multiple :before-upload="handleBeforeUpload" 
                    action="//jsonplaceholder.typicode.com/posts/">
                <Button type="ghost" icon="ios-cloud-upload-outline">选择需要上传的文件</Button>
            </Upload>
          </Row>
          <Row type="flex" justify="center" v-for="(value, index) in needUploadFiles" :key="index">
                <strong>{{ value.file.name }}</strong>
                <Button size="small" shape="circle" :type="value.btnType" :loading="value.loading" @click="chooseFileBtn(index)">
                    <Icon :type="value.icon"></Icon>
                </Button>
          </Row>
          
          <div slot="footer">
            <Row >
                <Col span="6" offset="6">
                    <Button type="primary" :loading="fileUploadSubmitLoading" @click="fileUploadSubmit" long>
                        <span v-if="!fileUploadSubmitLoading">提交</span>
                        <span v-else>正在提交...</span>
                    </Button>
                </Col>
                <Col span=6 class="padding-left-10">
                    <Button @click="closedfileUploadModal" long>取消</Button>
                </Col>
            </Row>
          </div>
      </Modal>

  </div>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: "basic_data_file",
    data() {
        return {
            fileTypeList: [],
            fileTypeModalVisible: false,
            searchSelectVal: 'byFileName',
            searchValue: '',
            fileTypeSelVal: '',
            fileTypeFormItem: {
                typeName: ''
            },
            fileTypeRuleValidate: {
                typeName: [
                    { required: true, message: "档案类型名称必输", trigger: 'blur' }
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
                    type: 'selection',
                    width: 60,
                    align: 'center'
                },
                {
                    type: 'index',
                    title: '序号',
                    align: 'center',
                    width: 70,
                },
                {
                    title: '档案编号',
                    key: 'fileNo',
                    align: 'center',
                    sortable: true
                },
                {
                    title: '档案名称',
                    key: 'fileName',
                    align: 'center',
                    sortable: true
                }
            ],
            fileInforCardTitleName: '',
            delFileInfoBtnDisable: true,
            delFileInfoBtnLoading: false,
            fileInfoTabCurrClickData: {},
            fileInfoTabSelectedData: [],
            fileInfoFormItem: {
                id: '',
                fileType: '',
                fileNo: '',
                fileName: '',
                comment: '',
                updateBy: '',
                updateTime: ''
            },
            fileInfoFormValidate: {
                fileType: [
                    { required: true, message: "档案类型必输", trigger: 'blur' }
                ],
                fileName: [
                    { required: true, message: "档案名称必输", trigger: 'blur' }
                ]
            },
            fileInfoFormView: 'add',
            fileInfoSubmitLoading: false,
            uploadTabData:[],
            uploadTabColumns:[
                {
                    title: '描述',
                    key: 'comment',
                    align: 'center'
                },
                {
                    title: '文件路径',
                    key: 'loadUrl',
                    align: 'center'
                },
                {
                    title: '操作',
                    key: 'action',
                    width: 100,
                    align: 'center',
                    render: (h, params) => {
                        h('Button', {
                                    props: {
                                        type: 'error',
                                        size: 'small',
                                        icon: 'close-circled'
                                    },
                                    on: {
                                        click: () => {
                                            this.removeFileUpload(params.row.id, params.row.index);
                                        }
                                    }
                            }, '删除');
                    }
                }
            ],
            fileUploadModalVisible: false,
            fileUploadFormData: {
                fileId: '',
                comment: ''
            },
            fileUploadSubmitLoading: false,
            needUploadFiles:[]
        }
    },
    mounted() {
            this.loadFileTypeList();
            this.fileInfoFormChangeToAddView();
    },
    computed: {
        fileInfoFormEditDisable() {
            if (this.fileInfoFormView === 'add') {
                return false;
            }else {
                return true;
            }
        }
    },
    watch: {
        fileInfoTabCurrClickData() {
            if (this.fileInfoTabCurrClickData && this.fileInfoTabCurrClickData.id) {
                this.fileInfoFormChangeToEditView(this.fileInfoTabCurrClickData)
            }
        },
        fileInfoTabSelectedData() {
            if (this.fileInfoTabSelectedData && this.fileInfoTabSelectedData.length > 0) {
                this.delFileInfoBtnDisable = false;
            }else {
                this.delFileInfoBtnDisable = true;
            }
        }
    },
    methods:{
        
        loadFileTypeList() {
            util.ajax.get("/file/filetype/list")
                .then((response) => {
                    this.fileTypeList = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        },

        addFileTypeBtnClick() {
            this.fileTypeModalVisible = true;
        },

        fileTypeSubmit() {
            this.$refs.fileTypeForm.validate(valid => {
                if (!valid) {
                    this.$Message.info('档案类型名称必输');
                    return;
                }else {
                    this.fileTypeSubmitLoading = true;
                    util.ajax.post("/file/filetype/add", this.fileTypeFormItem)
                        .then((response) => {
                            this.fileTypeList = response.data;
                            this.fileTypeModalVisible = false;
                            this.loadFileTypeList();
                        })
                        .catch((error) => {
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
            if (this.fileTypeSelVal && this.fileTypeSelVal != '') {
                reqData.fileType = this.fileTypeSelVal;
            }
            if (this.searchSelectVal === 'byFileName' && this.searchValue) {
                reqData.fileName = this.searchValue;
            }
            if (this.searchSelectVal === 'byFileNo' && this.searchValue) {
                reqData.fileNo = this.searchValue;
            }
            this.fileInfoTabLoading = true;
            util.ajax.get("/file/list", {params: reqData})
                .then((response) => {
                    this.fileTabData = response.data.data;
                    this.fileInfoTotalCount = response.data.count;
                })
                .catch((error) => {
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
            this.fileInfoFormChangeToAddView();
        },

        delFileInfoBtnClick() {
            if (this.fileInfoTabSelectedData && this.fileInfoTabSelectedData.length > 0) {
                this.delFileInfoBtnLoading = true;
                this.fileInfoTabLoading = true;
                let delIds = [];
                for (let i=0; i<this.fileInfoTabSelectedData.length; i++) {
                    delIds.push(this.fileInfoTabSelectedData[i].id);
                }
                util.ajax.post("/file/remove", delIds)
                    .then((response) => {
                        this.$Message.success('成功删除' + response.data.count + '条记录');
                        this.loadFileInfoList();
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    });
                this.delFileInfoBtnLoading = false;
                this.fileInfoTabLoading = false;
                this.delFileInfoBtnDisable = true;
                this.fileInfoFormChangeToAddView();
            }else {
                this.$Message.info('请先选择需要删除的档案信息');
            }
        },

        fileInfoTabRowClick(data) {
            this.fileInfoTabCurrClickData = data;
            if (data && data.id && data.id > 0) {
                this.fileInfoFormChangeToEditView(data);
            }
        },

        fileInfoTabSelecttionChange(data) {
            this.fileInfoTabSelectedData = data;
        },

        removeFileUpload(data, index) {
            if (!data) {
                this.$Message.warn('获取需要删除的附件标识失败');
                return;
            }
            let reqData = {fileUploadId: data};
            util.ajax.delete("/file/upload/remove", reqData, {headers:{'Content-Type': 'application/json'}})
                .then((response) => {
                    this.$Message.success('删除成功');
                    this.uploadTabData.splice(index, 1); //在列表中删除
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        },

        fileInfoFormChangeToEditView(data) {
            this.fileInforCardTitleName = data.fileName;
            this.fileInfoFormItem =  data;
            this.uploadTabData = data.fileUploads;
            this.fileInfoFormView = 'edit';
        },

        fileInfoFormChangeToAddView() {
            this.fileInforCardTitleName = '新增档案信息';
            this.fileInfoFormItem = {},
            this.uploadTabData = [],
            this.fileInfoFormView = 'add';
        },

        submitFileInfoBtnClick() {
            this.fileInfoSubmitLoading = true;
            this.$refs.fileInfoForm.validate(valid => {
                if (!valid) {
                    this.$Message.info('请检查必输项');
                    this.fileInfoSubmitLoading = false;
                    return;
                }else {
                    let reqData = this.fileInfoFormItem;
                    util.ajax.post("/file/add", reqData)
                        .then((response) => {
                            this.$Message.success('新建档案信息成功');
                            this.loadFileInfoList();
                            this.fileInfoFormChangeToEditView(response.data);
                        })
                        .catch((error) => {
                            util.errorProcessor(this, error);
                        })
                    this.fileInfoSubmitLoading = false;
                }
            });
        },

        addFileUploadBtnClick() {
            this.fileUploadModalVisible = true;
            this.fileUploadFormData = {
                fileId: this.fileInfoFormItem.id,
                comment: ''
            };
        },

        handleBeforeUpload(file) {
            let reader = new FileReader()
            reader.readAsDataURL(file)
            const _this = this;
            let item = {
                loading: false,
                btnType: 'error',
                icon: 'close-round',
                file: null
            }
            reader.onloadend = (e) => {
                file.url = reader.result
                item.file = file;
            }
            this.needUploadFiles.push(item);
            return false;
        },

        chooseFileBtn(data) {
            this.needUploadFiles.splice(data, 1);
        },

        fileUploadSubmit() {
            if (!this.needUploadFiles || this.needUploadFiles.length < 0) {
                this.$Message.info('请先选择需要上传的文件');
                return;
            }
            this.fileUploadSubmitLoading = true;
            for (let i=0; i<this.needUploadFiles.length; i++) {
                let item = this.needUploadFiles[i];
                let reqData = {
                    fileId: this.fileUploadFormData.fileId,
                    comment: this.fileUploadFormData.comment,
                    file: item.file.url
                };
                console.log(item.file.url);
                console.log(reqData);
                item.loading = true;
                util.ajax.post("/file/upload/add", reqData, {headers: {'Content-Type': 'multipart/form-data'}})
                    .then((response) => {
                        item.btnType = 'success';
                        item.icon = 'checkmark';
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    });
                item.loading = false;
            }
            this.fileUploadSubmitLoading = false;
        },

        closedfileUploadModal() {
            this.fileUploadFormData = {};
            this.fileUploadModalVisible = false;
        }

    }

  
}
</script>

<style>

</style>



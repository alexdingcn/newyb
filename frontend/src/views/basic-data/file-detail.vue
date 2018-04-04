
<template>
  <div>
      <Card>
        <p slot="title">
            <Icon type="document"></Icon> {{titleName || ''}}
        </p>
        <div slot="extra">
            <Button type="primary" :disabled="isEditView" :loading="submitLoading" @click="submitFileInfoBtnClick">提交</Button>
        </div>

        <Row type="flex" justify="center">
            <Form ref="fileInfoForm" :model="formData" :rules="formValidate" label-position="right" :label-width="90">
                <Row>
                    <Col span="12">
                        <FormItem label="档案类型" prop="fileType">
                            <file-type-select size="small" v-model="formData.fileType" v-if="!isEditView"></file-type-select>
                            <Input v-if="isEditView" v-model="formData.fileType" />
                        </FormItem>
                    </Col>
                    <Col span="12">
                        <FormItem label="档案编号">
                            <Input size="small" type="text" v-model="formData.fileNo" placeholder="由系统自动生成" readonly />
                        </FormItem>
                    </Col>
                </Row>
                <Row>
                    <Col span="12">
                        <FormItem label="档案名称" prop="fileName">
                            <Input size="small" type="text" v-model="formData.fileName" :readonly="isEditView" />
                        </FormItem>
                    </Col>
                    <Col span="12">
                        <FormItem label="操作时间">
                            <DatePicker size="small" type="datetime" v-model="formData.updateTime" format="yyyy-MM-dd HH:mm" readonly></DatePicker>
                        </FormItem>
                    </Col>
                </Row>
                <Row>
                    <Col span="24">
                        <FormItem label="备注">
                            <Input size="small" type="text" v-model="formData.comment" :readonly="isEditView" />
                        </FormItem>
                    </Col>
                </Row>
            </Form>
        </Row>
        <Row type="flex" justify="end">
            <Button type="primary" :disabled="!isEditView" size="small" 
                @click="addFileUploadBtnClick">
                <Icon type="upload"></Icon>
                上传档案文件
            </Button>
        </Row>
        <Table ref="uploadTable" border highlight-row height="250" no-data-text="点击上传档案按钮添加数据"
            :columns="uploadTabColumns" :data="uploadTabData" size="small">
        </Table>
    </Card>
    <file-view :change="fileViewChange" :fileInfo="formData"></file-view>

    <Modal v-model="fileUploadModalVisible" title="上传档案文件" :mask-closable="false" :closable="false">
          <Row type="flex" justify="center">
              <Form ref="fileUploadForm" :model="fileUploadFormData" label-position="right" :label-width="90">
                  <FormItem label="描述信息">
                    <Input type="text" v-model="fileUploadFormData.comment" placeholder="请输入描述信息"></Input>
                  </FormItem>
              </Form>
          </Row>
          <Row type="flex" justify="center">
            <Upload ref="uploadModal" multiple :before-upload="handleBeforeUpload" :show-upload-list="false"
                    :max-size="uploadMaxSize" 
                    :format="uploadFormat" 
                    :action="uploadAction">
                <Button type="ghost" icon="ios-cloud-upload-outline">选择需要上传的文件</Button>
            </Upload>
          </Row>
          <Row type="flex" justify="center" v-for="(item, index) in chooseUploadFiles" :key="index">
            <strong class="margin-right-5 margin-top-10">{{item.name}}</strong>
            <a class="margin-top-10" href="#" :disabled="item.disabled" @click="removeChooseUploadFile(index)"><Icon size="20" :type="item.icon" :color="item.color"></Icon></a>
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
import util from "@/libs/util.js";
import fileView from "@/views/basic-data/file-view.vue";
import fileTypeSelect from "@/views/selector/file-type-select.vue";

export default {
    name: 'file-detail',
    components: {
        fileView,
        fileTypeSelect
    },
    props: {
        fileNo: String
    },
    data() {
        return {
            titleName: '',
            isEditView: false,
            submitLoading: false,
            fileViewChange: 0,
            formData: {
                id: '',
                fileType: '',
                fileNo: '',
                fileName: '',
                comment: '',
                updateTime: ''
            },
            formValidate: {
                fileType: [
                    { required: true, message: '档案类型必输', trigger: 'change' }
                ],
                fileName: [
                    { required: true, message: '档案名称必输', trigger: 'blur' }
                ]
            },
            uploadTabData: [],
            uploadTabColumns: [
                {
                    title: '描述',
                    key: 'comment',
                    align: 'center'
                },
                {
                    title: '位置标识',
                    key: 'location',
                    align: 'center'
                },
                {
                    title: '操作',
                    key: '',
                    width: 100,
                    align: 'center',
                    render: (h, params) => {
                        return h('Button', {
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
            uploadMaxSize: 10240,
            uploadFormat: ['png', 'jpg', 'jpeg', 'git', 'tiff', 'pdf', 'doc', 'docx', 'xlsx', 'xls', 'csv', 'txt', 'zip', 'rar', 'tar'],
            chooseUploadFiles: [],
            uploadAction: `${util.baseUrl}/file/upload/add`,
            fileUploadSubmitLoading: false
        }
    },
    watch: {
        fileNo(value) {
            console.log('fileNo=' + value);
            if (!value || value === '') {
                this.isEditView = false;
                this.fileInfoFormChangeToAddView();
            } else {
                this.isEditView = true;
                this.fileInfoFormChangeToEditView(value);
            }
        },
    },
    methods: {
        submitFileInfoBtnClick () {
            this.submitLoading = true;
            this.$refs.fileInfoForm.validate(valid => {
                if (!valid) {
                    this.$Message.warning('请检查必输项');
                    this.submitLoading = false;
                } else {
                    let reqData = this.formData;
                    util.ajax.post('/file/add', reqData)
                        .then((response) => {
                            this.submitLoading = false;
                            this.$Message.success('新建档案信息成功');
                            this.fileInfoFormChangeToEditView(response.data.fileNo);
                            this.$emit('add-file-success', response.data);
                        })
                        .catch((error) => {
                            this.submitLoading = false;
                            util.errorProcessor(this, error);
                        });
                }
            });
        },

        fileInfoFormChangeToEditView (fileNo) {
            if (!fileNo) {
                this.$Message.warning('获取档案标识失败');
                return;
            }
            let reqData = {fileNo: fileNo};
            util.ajax.get('/file/fileno', {params: reqData})
                .then((response) => {
                    let result = response.data;
                    this.titleName = result.fileName;
                    this.formData = result;
                    this.fileViewChange = this.fileViewChange + 1;
                    this.uploadTabData = result.fileUploads;
                    this.isEditView = true;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        fileInfoFormChangeToAddView() {
            this.titleName = '新增档案信息';
            this.formData = {};
            this.uploadTabData = [];
            this.isEditView = false;
        },

        addFileUploadBtnClick () {
            this.fileUploadModalVisible = true;
            this.fileUploadFormData = {
                fileId: this.formData.id,
                comment: ''
            };
        },

        removeFileUpload (data, index) {
            if (!data) {
                this.$Message.warning('获取需要删除的附件标识失败');
                return;
            }
            util.ajax.delete('/file/upload/remove/' + data)
                .then((response) => {
                    this.$Message.success('删除成功');
                    this.fileInfoFormChangeToEditView(this.formData.fileNo); // 刷新
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        handleBeforeUpload (file) {
            let item = {
                name: file.name,
                icon: 'close-circled',
                color: 'red',
                disabled: false,
                file: file,
                isUpload: false
            };
            this.chooseUploadFiles.push(item);
            return false;
        },

        removeChooseUploadFile (data) {
            this.chooseUploadFiles.splice(data, 1);
            this.$refs.uploadModal.fileList.splice(data, 1);
        },

        fileUploadSubmit () {
            if (!this.chooseUploadFiles || this.chooseUploadFiles.length <= 0) {
                this.$Message.info('请先选择需要上传的文件');
                return;
            }
            this.fileUploadSubmitLoading = true;
            for (let i = 0; i < this.chooseUploadFiles.length; i++) {
                let chooseItem = this.chooseUploadFiles[i];
                if (chooseItem.isUpload) {
                    continue;
                }
                chooseItem.disabled = true;
                chooseItem.color = '';
                let reqData = new FormData();
                reqData.append('fileId', this.fileUploadFormData.fileId);
                reqData.append('comment', this.fileUploadFormData.comment);
                reqData.append('file', chooseItem.file);
                util.ajax.post('/file/upload/add', reqData, {headers: {'Content-Type': 'multipart/form-data'}})
                    .then((response) => {
                        chooseItem.icon = 'checkmark';
                        chooseItem.color = 'green';
                        chooseItem.isUpload = true;
                    })
                    .catch((error) => {
                        chooseItem.icon = 'close';
                        util.errorProcessor(this, error);
                    });
            }
            this.fileUploadSubmitLoading = false;
        },

        closedfileUploadModal () {
            this.fileInfoFormChangeToEditView(this.formData.fileNo); // 刷新
            this.chooseUploadFiles = [];
            this.$refs.uploadModal.clearFiles();
            this.fileUploadModalVisible = false;
            this.fileUploadFormData = {};
        },

    
    }


  
}
</script>

<style>
.ivu-form-item {
    margin-bottom: 8px;
}
</style>

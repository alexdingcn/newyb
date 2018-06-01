
<template>
  <div class="file-info-div">
        <Row class="file-info-title">
            <i-col span="12">
                <Row type="flex" justify="start">
                    <h3><Icon type="document"></Icon> {{titleName || ''}}</h3>
                </Row>
            </i-col>
            <i-col span="12">
                <Row type="flex" justify="end">
                    <Button size="small" type="primary" :disabled="isEditView" :loading="submitLoading"
                            icon="checkmark" @click="submitFileInfoBtnClick">提交</Button>
                </Row>
            </i-col>
        </Row>
        <hr class="file-info-hr"/>
        <Row type="flex" justify="center">
            <Form ref="fileInfoForm" :model="formData" :rules="formValidate" label-position="right" :label-width="90">
                <Row>
                    <i-col span="12">
                        <FormItem label="档案类型" prop="fileType">
                            <file-type-select size="small" v-model="formData.fileType" v-if="!isEditView"></file-type-select>
                            <Input v-if="isEditView" v-model="formData.fileType" />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="档案编号">
                            <Input size="small" type="text" v-model="formData.fileNo" placeholder="由系统自动生成" disabled />
                        </FormItem>
                    </i-col>
                </Row>
                <Row>
                    <i-col span="12">
                        <FormItem label="档案名称" prop="fileName">
                            <Input size="small" type="text" v-model="formData.fileName" :readonly="isEditView" />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="操作时间">
                            <DatePicker size="small" type="datetime" v-model="formData.updateTime" format="yyyy-MM-dd HH:mm" disabled></DatePicker>
                        </FormItem>
                    </i-col>
                </Row>
                <Row>
                    <i-col span="24">
                        <FormItem label="备注">
                            <Input size="small" type="text" v-model="formData.comment" :readonly="isEditView" />
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
        </Row>
       
       <div v-if="formData.id > 0" >
            <div  >
                <Row class="file-info-title" style="margin-top: 10px;" type="flex" justify="start">
                    <h3>上传档案文件</h3>
                </Row>
                <hr class="file-info-hr"/>
                <file-upload :fileId="fileId" @upload-success="fileUploadSuccess"></file-upload>
            </div>

            <Table class="file-info-table" ref="uploadTable" border highlight-row height="250" no-data-text="点击上传档案按钮添加数据"
                :columns="uploadTabColumns" :data="uploadTabData" size="small">
            </Table>

            <file-view :change="fileViewChange" :fileInfo="formData"></file-view>
        </div>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import fileView from "@/views/basic-data/file-view.vue";
import fileUpload from "@/views/basic-data/file-upload.vue";
import fileTypeSelect from "@/views/selector/file-type-select.vue";

export default {
    name: 'file-detail',
    components: {
        fileView,
        fileUpload,
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
            fileId: '',
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
        }
    },
    mounted() {
        this.fileInfoFormChangeToAddView();
    },
    watch: {
        fileNo(value) {
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
                    result.updateTime = result.updateTime ? moment(result.updateTime).format('YYYY-MM-dd HH:mm') : '';
                    this.titleName = result.fileName;
                    this.formData = result;
                    this.fileViewChange = this.fileViewChange + 1;
                    this.uploadTabData = result.fileUploads;
                    this.isEditView = true;
                    this.fileId = result.id;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        fileInfoFormChangeToAddView() {
            this.titleName = '新增档案信息';
            this.fileId = '';
            this.formData = {};
            this.uploadTabData = [];
            this.isEditView = false;
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

        fileUploadSuccess() {
            console.log('uploadSuccess event');
            this.fileInfoFormChangeToEditView(this.formData.fileNo); // 刷新
        }

    }
}
</script>

<style >
.ivu-form-item {
    margin-bottom: 8px;
}
.file-info-div {
    background-color: #fff;
    margin-left: 20px;
    padding-top: 10px;
}
.file-info-title {
    margin: 0px 20px;
}
.file-info-hr {
    height: 2px;
    border: 0;
    background-color: #5cadff;
    margin-left: 20px;
    margin-right: 20px;
    margin-top: 3px;
    margin-bottom: 10px;
}
.file-info-table {
    margin: 10px 20px;
}
</style>

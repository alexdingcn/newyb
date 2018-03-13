<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row>
          <Row>
            <div class="margin-bottom-10">
                <strong class="margin-left-5 margin-right-10">档案类型</strong>
                <Select v-model="fileTypeSelVal" clearable style="width:200px">
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
                    <GroupButton size="small" slot="extra">
                        <Button type="primary" icon="android-add-circle" @click="addFileInfoBtnClick">添加</Button>
                        <Button type="error" icon="android-remove-circle" @click="delFileInfoBtnClick">删除</Button>
                    </GroupButton>

                    <Table ref="fileTable" border highlight-row :loading="fileInfoTabLoading"
                        :columns="fileTabColumns" :data="fileTabData" size="small" 
                        @on-row-click="fileInfoTabRowClick" 
                        @on-selection-change="fileInfoTabSelecttionChange">
                    </Table>
                </Card>
            </Col>
            <Col span="13">
                <Card>
                    <p slot="title">
                        <Icon type="document"></Icon> 档案 <strong>{{currChooseFileName | ''}}</strong> 附件
                    </p>

                    <Row type="flex" justify="center">
                        <Form ref="infoShowForm" :model="infoShowFormItem" label-position="right" :label-width="100" style="width:70%;">
                            <FormItem label="档案类型">
                                <Select v-model="infoShowFormItem.fileType" disabled>
                                    <Option v-for="item in fileTypeList" :value="item.typeName" :key="item.id">{{ item.typeName }}</Option>
                                </Select>
                            </FormItem>
                            <FormItem label="档案编号">
                                <Input type="text" v-model="infoShowFormItem.fileNo" disabled></Input>
                            </FormItem>
                            <FormItem label="档案名称">
                                <Input type="text" v-model="infoShowFormItem.fileName" disabled></Input>
                            </FormItem>
                            <FormItem label="备注">
                                <Input type="text" v-model="infoShowFormItem.comment" disabled></Input>
                            </FormItem>
                            <FormItem label="操作员">
                                <Input type="text" v-model="infoShowFormItem.updateBy" disabled></Input>
                            </FormItem>
                            <FormItem label="操作时间">
                                <DatePicker type="datetime" v-model="infoShowFormItem.updateTime" format="yyyy-MM-dd HH:mm" disabled></DatePicker>
                            </FormItem>
                        </Form>

                        <Table ref="uploadTable" border highlight-row 
                            :columns="uploadTabColumns" :data="uploadTabData" size="small">
                        </Table>
                    </Row>
                    
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
            <Row slot="footer">
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
            fileTypeFormItem: {
                typeName: ''
            },
            fileTypeRuleValidate: [
                { required: true, message: "档案类型名称必输", trigger: 'blur' }
            ],
            fileTypeSubmitLoading: false,
            fileInfoTabLoading: false,
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
            currChooseFileName: '',
            fileIinfoTabCurrClickData: {},
            fileInfoTabSelectedData: [],
            infoShowFormItem: {
                id: '',
                fileType: '',
                fileNo: '',
                fileName: '',
                comment: '',
                updateBy: '',
                updateTime: ''
            },
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
            ]



        }
    },
    mounted() {
            this.loadFileTypeList();
    },
    watch: {
        fileIinfoTabCurrClickData() {
            if (this.fileIinfoTabCurrClickData) {
                this.currChooseFileName = this.fileIinfoTabCurrClickData.fileName;
                this.infoShowFormItem =  this.fileIinfoTabCurrClickData;
                this.uploadTabData = this.fileIinfoTabCurrClickData.fileUploads;
            }else {
                this.currChooseFileName = '';
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

        addFileInfoBtnClick() {},

        delFileInfoBtnClick() {},

        fileInfoTabRowClick(data) {
            this.fileIinfoTabCurrClickData = data;
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
        }

    }

  
}
</script>

<style>

</style>



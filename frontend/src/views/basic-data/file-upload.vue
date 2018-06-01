
<template>
  <div>
      <Row>
          <i-col span="13">
                <Row style="margin-left:30px" type="flex" justify="start">
                    <Upload ref="uploadModal" multiple :before-upload="handleBeforeUpload" :show-upload-list="false"
                            :max-size="uploadMaxSize" 
                            :format="uploadFormat" 
                            :action="uploadAction">
                        <Button type="ghost" icon="ios-cloud-upload-outline">选择需要上传的文件</Button>
                    </Upload>
                </Row>
                <Row style="margin-left:30px" type="flex" justify="start">
                    <Input style="margin-top:7px; margin-bottom:7px; width:100%;" 
                        size="small" v-model="comment" 
                        placeholder="请输入描述信息" />
                </Row>
                <Row style="margin-left:30px; " type="flex" justify="start">
                    <ButtonGroup>
                        <Button type="success" size="small" icon="upload" 
                            :disabled="uploadBtnDisabled" 
                            :loading="fileUploadSubmitLoading" @click="fileUploadSubmit" >
                            <span v-if="!fileUploadSubmitLoading">开始上传</span>
                            <span v-else>正在提交...</span>
                        </Button>
                        <Button type="warning" size="small" icon="checkmark-round" 
                            :disabled="!uploadBtnDisabled"
                            @click="allSuccessUpload">确认已成功上传</Button>
                    </ButtonGroup>
                </Row>
          </i-col>
          <i-col style="padding-left: 10px;" span="11">
                <Row type="flex" justify="start" v-for="(item, index) in chooseUploadFiles" :key="index">
                    <strong class="margin-right-5 margin-top-10">{{item.name}}</strong>
                    <a class="margin-top-10" href="#" :disabled="item.disabled" @click="removeChooseUploadFile(index)"><Icon size="20" :type="item.icon" :color="item.color"></Icon></a>
                </Row>
          </i-col>
        </Row>
  </div>
</template>

<script>
import util from "@/libs/util.js";

export default {
    name: 'file-upload',
    props: {
        fileId: String|Number
    },
    data() {
        return {
            comment: '',
            uploadMaxSize: 10240,
            uploadFormat: ['png', 'jpg', 'jpeg', 'git', 'tiff', 'pdf', 'doc', 'docx', 'xlsx', 'xls', 'csv', 'txt', 'zip', 'rar', 'tar'],
            uploadAction: `${util.baseUrl}/file/upload/add`,
            chooseUploadFiles: [],
            fileUploadSubmitLoading: false,
            uploadBtnDisabled: false,
        }
    },
    watch: {
        
    },
    methods: {
        handleBeforeUpload (file) {
            if (!file || file.size <= 0) {
                this.$Message.warning('文件大小为0不能上传');
                return false;
            }
            let item = {
                name: file.name,
                icon: 'close-circled',
                color: 'red',
                disabled: false,
                file: file,
                isUpload: false
            };
            this.chooseUploadFiles.push(item);
            return false; //阻止直接上传，使用手动上传
        },

        removeChooseUploadFile (data) {
            this.chooseUploadFiles.splice(data, 1);
            this.$refs.uploadModal.fileList.splice(data, 1);
        },

        fileUploadSubmit () {
            if (!this.fileId || this.fileId === '') {
                this.$Message.error('获取档案标识错误，请刷新页面重新尝试.');
                return;
            }
            if (!this.chooseUploadFiles || this.chooseUploadFiles.length <= 0) {
                this.$Message.warning('请先选择需要上传的文件');
                return;
            }
            this.fileUploadSubmitLoading = true;
            this.uploadBtnDisabled = true; //开始上传后，在点击确认上传前不能再次点击
            for (let i = 0; i < this.chooseUploadFiles.length; i++) {
                let chooseItem = this.chooseUploadFiles[i];
                if (chooseItem.isUpload || !chooseItem.file || chooseItem.file.size <= 0) {
                    continue;
                }
                chooseItem.disabled = true;
                chooseItem.color = '';
                let reqData = new FormData();
                reqData.append('fileId', this.fileId);
                reqData.append('comment', this.comment);
                reqData.append('file', chooseItem.file);
                util.ajax.post('/file/upload/add', reqData, {headers: {'Content-Type': 'multipart/form-data'}})
                    .then((response) => {
                        this.fileUploadSubmitLoading = false;
                        chooseItem.icon = 'checkmark';
                        chooseItem.color = 'green';
                        chooseItem.isUpload = true;
                    })
                    .catch((error) => {
                        this.fileUploadSubmitLoading = false;
                        chooseItem.icon = 'close';
                        util.errorProcessor(this, error);
                    });
            }
        },
        allSuccessUpload() {
            this.uploadBtnDisabled = false; //开始上传后，在点击确认上传后可以再次上传
            this.chooseUploadFiles = []; //成功上传完成后，清理缓存，否则会再次上传
            this.$refs.uploadModal.clearFiles();
            this.comment = '';
            this.$emit('upload-success');
        }
    }
}
</script>


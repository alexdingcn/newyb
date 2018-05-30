
<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="android-car"></Icon>
                {{shipFormItem.name || '运输公司信息'}}
            </p>
            <div slot="extra">
                <Button type="success" icon="checkmark" v-if="shipFormItem.id" :loading="saveLoading" @click="saveShipInfo('edit')">保存</Button>
                <Button type="primary" icon="plus" v-if="!shipFormItem.id" :loading="saveLoading" @click="saveShipInfo('add')">提交</Button>
            </div>
            <div>
                <Form ref="shipForm" :model="shipFormItem" :rules="shipValidate" :label-width="100">
                    <Row type="flex" justify="center">
                        <Col span="10">
                            <FormItem label="名称" prop="name">
                                <Input type="text" v-model="shipFormItem.name" placeholder="请输入名称"></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="负责人">
                                <Input type="text" v-model="shipFormItem.employee" placeholder="请输入负责人"></Input>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="是否启用">
                                <Checkbox v-model="shipFormItem.enabled"></Checkbox>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="固定电话">
                                <Input type="text" v-model="shipFormItem.phone" placeholder="请输入固定电话"></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="传真">
                                <Input type="text" v-model="shipFormItem.fax" placeholder="请输入传真"></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="电子邮件">
                                <Input type="text" v-model="shipFormItem.email" placeholder="请输入电子邮件"></Input>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="联系人">
                                <Input type="text" v-model="shipFormItem.contactUser" placeholder="请输入联系人"></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="联系电话">
                                <Input type="text" v-model="shipFormItem.contactPhone" placeholder="请输入联系电话"></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="邮编">
                                <Input type="text" v-model="shipFormItem.posecode" placeholder="请输入邮编"></Input>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row type="flex" justify="center">
                        <Col span="8">
                            <FormItem label="营业执照">
                                <Input type="text" v-model="shipFormItem.license" placeholder="请输入营业执照"></Input>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="执照到期日">
                                <DatePicker v-model="shipFormItem.licenseExp" type="date" 
                                    format="yyyy-MM-dd" placeholder="请选择执照到期日" ></DatePicker>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="档案编号">
                                <Input type="text" v-model="shipFormItem.fileNo" readonly>
                                    <Button slot="append" type="text" icon="upload" @click="uploadFileInfo"></Button>
                                </Input>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row type="flex" justify="center">
                        <Col span="12">
                            <FormItem label="地址">
                                <Input type="text" v-model="shipFormItem.address" placeholder="请输入地址"></Input>
                            </FormItem>
                        </Col>
                        <Col span="12">
                            <FormItem label="备注">
                                <Input type="text" v-model="shipFormItem.comment" placeholder="请输入备注"></Input>
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
            </div>
        </Card>

        <Modal v-model="fileUploadModal" title="承运公司档案上传" :mask-closable="false" width="50" >
            <file-detail :fileNo="shipFormItem.fileNo" @add-file-success="addFileSuccess" ></file-detail>
            <div slot="footer"></div>
        </Modal>
    </div>
    
</template>

<script>
import util from "@/libs/util.js";
import fileDetail from "@/views/basic-data/file-detail.vue";

export default {
    name: 'ship-info',
    props: {
        shipId: {
            type: String|Number,
            default: ''
        }
    },
    components: {
        fileDetail
    },
    data() {
        return {
            saveLoading: false,
            shipFormItem: {},
            shipValidate: {
                name: [
                    {required: true, message: '名称必输', trigger: 'blur'}
                ]
            },
            fileUploadModal: false,

        }
    },
    mounted() {
        this.refreshShipInfo();
    },
    watch: {
        shipId() {
            this.refreshShipInfo();
        }
    },
    methods: {
        refreshShipInfo() {
            if(this.shipId) {
                this.saveLoading = true;
                util.ajax.get("/ship/" + this.shipId)
                    .then((response) => {
                        this.saveLoading = false;
                        this.shipFormItem = response.data;
                    })
                    .catch((error) => {
                        this.saveLoading = false;
                        util.errorProcessor(this, error);
                        this.shipFormItem = {};
                    })
            }else {
                this.shipFormItem = {};
            }
        },

        uploadFileInfo() {
            this.fileUploadModal = true;
        },

        addFileSuccess(data) {
            this.shipFormItem.fileNo = data.fileNo;
        },

        saveShipInfo (action) {
            this.$refs.shipForm.validate(valid => {
                if (!valid) {
                    this.$Message.warning('请检查必输项信息');
                    return;
                }
                this.saveLoading = true;
                util.ajax.post('/ship/save', this.shipFormItem)
                    .then((response) => {
                        this.saveLoading = false;
                        this.$Message.success('保存成功');
                        this.shipFormItem = response.data;
                        this.$emit('save-ok', response.data, action);
                    })
                    .catch((error) => {
                        this.saveLoading = false;
                        util.errorProcessor(this, error);
                    });
            });
        }

    }
}
</script>
<style scoped>
.ivu-form-item {
    margin-bottom: 20px;
}
</style>


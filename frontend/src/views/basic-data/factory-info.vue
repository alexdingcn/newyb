<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <Row>
            <Card>
                <p slot="title">
                    <Icon type="compose"></Icon>
                     {{ formItem.name || '生产企业' }}
                </p>

                <div slot="extra">
                    <Button type="text" @click="searchRelatedGoods" v-show="formItem.id" icon="search">查看企业名下商品</Button>
                    <Button type="success" @click="submitFactory('edit')" :loading="loading" v-show="formItem.id">保存</Button>
                    <Button type="primary" @click="submitFactory('add')" :loading="loading" v-show="!formItem.id">提交</Button>
                </div>

                <Form :model="formItem" :rules="ruleValidate" :label-width="80">
                    <h3>基本信息</h3>
                    <hr style="width: 50%; margin-bottom:15px;" size="1"/>
                    <Row>
                        <Col span="8">
                            <FormItem label="企业名称" prop="name">
                                <Input v-model="formItem.name" placeholder="生产企业名称" @on-blur="onChangeName"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="产地" prop="origin">
                                <Input v-model="formItem.origin" placeholder="例如 陕西"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="拼音代码" prop="pinyin">
                                <Input v-model="formItem.pinyin" placeholder="例如 SXRZ"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="8">
                            <FormItem label="城市" prop="placeCodes">
                                <al-cascader v-model="placeCodeList" level="2"/>
                            </FormItem>
                        </Col>
                        <Col span="11">
                            <FormItem label="详细地址" prop="address">
                                <Input v-model="formItem.address" placeholder="例如 某大道1号1203室"/>
                            </FormItem>
                        </Col>
                        <Col span="5">
                            <FormItem label="邮编" prop="postcode">
                                <Input v-model="formItem.postcode"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="8">
                            <FormItem label="联系人" prop="contact">
                                <Input v-model="formItem.contact"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="联系人电话" prop="contactPhone">
                                <Input v-model="formItem.contactPhone"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="负责人" prop="employee">
                                <Input v-model="formItem.employee"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="8">
                            <FormItem label="固定电话" prop="phone">
                                <Input v-model="formItem.phone"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="传真" prop="fax">
                                <Input v-model="formItem.fax"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="电子邮件" prop="email">
                                <Input v-model="formItem.email"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="16">
                            <FormItem label="备注" prop="comment">
                                <Input v-model="formItem.comment"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="档案编号" prop="fileNo">
                                <Input type="text" v-model="formItem.fileNo" readonly icon="upload" @on-click="uploadFile" />
                            </FormItem>
                        </Col>
                    </Row>

                    <h3 style="margin-top:10px;">证件</h3>
                    <hr style="width: 50%; margin-bottom:15px;" size="1"/>
                    <Row>
                        <Col span="8">
                            <FormItem label="生产许可证" prop="permit">
                                <Input v-model="formItem.permit"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="许可证期限" prop="permitExp">
                                <DatePicker type="date" placement="top-start" placeholder="许可证到期日" v-model="formItem.permitExp"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="8">
                            <FormItem label="营业执照号" prop="license">
                                <Input v-model="formItem.license"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="执照到期日" prop="licenseExp">
                                <DatePicker type="date" placement="top-start" placeholder="营业执照到期日" v-model="formItem.licenseExp"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="GMP认证">
                                <Checkbox v-model="formItem.isGmp" />
                            </FormItem>
                        </Col>
                    </Row>

                    <h3 style="margin-top:10px;">银行帐户</h3>
                    <hr style="width: 50%; margin-bottom:15px;" size="1"/>
                    <Row>
                        <Col span="8">
                            <FormItem label="开户银行" prop="bankName">
                                <Input v-model="formItem.bankName"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="银行帐号" prop="bankAccount">
                                <Input v-model="formItem.bankAccount"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="税号" prop="taxNumber">
                                <Input v-model="formItem.taxNumber"/>
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
            </Card>
            <Spin size="large" fix v-if="spinShow"></Spin>
        </Row>

        <Modal v-model="fileUploadModal" title="生产企业档案" :mask-closable="false" width="50" class="file-upload-modal">
            <file-detail :fileNo="formItem.fileNo" @add-file-success="addFileSuccess" ></file-detail>
            <div slot="footer"></div>
        </Modal>

    </div>
</template>

<script>
import Vue from 'vue';
import moment from 'moment';
import iviewArea from 'iview-area';
import util from '@/libs/util.js';
import fileDetail from '@/views/basic-data/file-detail.vue';
import factoryVue from './factory.vue';

Vue.use(iviewArea);

export default {
    name: 'factory-info',
    props: {
        factoryId: {
            type: String|Number,
            default: ''
        }
    },
    components: {
        fileDetail
    },
    data () {
        return {
            spinShow: false,
            loading: false,
            formItem: {
                placeCodes: []
            },
            placeCodeList: [],
            categoryList: [],
            ruleValidate: {
                name: [
                    {required: true, message: '名称不能为空', trigger: 'blur'}
                ]
            },
            fileUploadModal: false,
        };
    },
    watch: {
        factoryId: function(data) {
            this.init();
        }
    },
    methods: {
        init () {
            var self = this;
            self.spinShow = true;
            if (this.factoryId && this.factoryId > 0) {
                util.ajax.get('/factory/' + this.factoryId)
                    .then(function (response) {
                        self.spinShow = false;
                        let data = response.data;
                        data.permitExp = data.permitExp ? moment(data.permitExp).format('YYYY-MM-DD') : '';
                        data.licenseExp = data.licenseExp ? moment(data.licenseExp).format('YYYY-MM-DD') : '';
                        self.formItem = data;
                        var placeCodes = [];
                        var rawCodes = response.data.placeCodes;
                        if (rawCodes) {
                            for (var i = 0; i < rawCodes.length; i++) {
                                placeCodes.push(rawCodes[i].code);
                            }
                        }
                        self.placeCodeList = placeCodes;
                    })
                    .catch(function (error) {
                        self.spinShow = false;
                        util.errorProcessor(self, error);
                    });
            } else {
                self.spinShow = false;
                self.formItem = {
                    pinyin: ''
                };
            }
        },
        submitFactory (action) {
            var self = this;
            this.spinShow = true;
            self.loading = true;
            this.formItem.placeCodes = this.placeCodeList;
            util.ajax.post('/factory/save', this.formItem)
                .then(function (response) {
                    self.spinShow = false;
                    self.loading = false;
                    let data = response.data;
                    data.permitExp = data.permitExp ? moment(data.permitExp).format('YYYY-MM-DD') : '';
                    data.licenseExp = data.licenseExp ? moment(data.licenseExp).format('YYYY-MM-DD') : '';
                    self.formItem = data;
                    self.$Message.success('生产企业' + self.formItem.name + '保存成功');
                    self.$emit('save-ok', self.formItem, action);
                })
                .catch(function (error) {
                    self.spinShow = false;
                    self.loading = false;
                    util.errorProcessor(self, error);
                });
        },
        onChangeName () {
            if (this.formItem.name && this.formItem.name.length > 0) {
                var self = this;
                util.ajax.post('/util/pinyinAbbr', { name: this.formItem.name })
                    .then(function (response) {
                        self.formItem.pinyin = response.data;
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            }
        },
        searchRelatedGoods () {
            let argu = { factory_id: this.$route.params.factory_id };
            this.$router.push({
                name: 'basic_data_good',
                params: argu
            });
        },
        uploadFile() {
            this.fileUploadModal = true;
        },
        addFileSuccess(data) {
            this.formItem.fileNo = data.fileNo;
        }

    },
    mounted () {
        this.init();
    },
    activated () {
        this.init();
    }
};
</script>

<style>
.ivu-form-item {
    margin-bottom: 20px;
}
.file-upload-modal {
    position: fixed;
    z-index: 1000;
}
</style>


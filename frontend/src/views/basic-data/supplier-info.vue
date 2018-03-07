<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <Row>
            <Card>
                <p slot="title">
                    <Icon type="compose"></Icon>
                     {{ formItem.name || '供应商' }}
                </p>

                <div slot="extra">
                    <Button type="text" @click="searchRelatedGoods" v-show="formItem.id" icon="search">查看该企业供应商品</Button>
                    <Button type="success" @click="submitSupplier" v-show="formItem.id">保存</Button>
                    <Button type="primary" @click="submitSupplier" v-show="!formItem.id">提交</Button>
                </div>

                <Form :model="formItem" :rules="ruleValidate" :label-width="100">
                    <Row>
                        <Col span="6">
                            <FormItem label="企业名称" prop="name">
                                <Input v-model="formItem.name" placeholder="供应商名称" @on-blur="onChangeName"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="简称" >
                                <Input v-model="formItem.shortName" placeholder="简称"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="产地" prop="origin">
                                <Input v-model="formItem.origin" placeholder="例如 陕西"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="拼音代码" prop="pinyin">
                                <Input v-model="formItem.pinyin" placeholder="例如 SXRZ"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="7">
                            <FormItem label="城市" prop="placeCodes">
                                <al-cascader v-model="formItem.placeCodes" level="2" />
                            </FormItem>
                        </Col>
                        <Col span="12">
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
                        <Col span="6">
                            <FormItem label="联系人" prop="contact">
                                <Input v-model="formItem.contact"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="联系人电话" prop="contactPhone">
                                <Input v-model="formItem.contactPhone"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="负责人" prop="employee">
                                <Input v-model="formItem.employee"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="6">
                            <FormItem label="固定电话" prop="phone">
                                <Input v-model="formItem.phone"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="传真" prop="fax">
                                <Input v-model="formItem.fax"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="电子邮件" prop="email">
                                <Input v-model="formItem.email"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="12">
                            <FormItem label="仓库地址" prop="warehouseAddr">
                                <Input v-model="formItem.warehouseAddr"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="电子监管码" prop="digitalAuditCode">
                                <Input v-model="formItem.digitalAuditCode"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="12">
                            <FormItem label="经营范围" prop="businessScope">
                                <Input v-model="formItem.businessScope"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="档案号" prop="archiveNumber">
                                <Input v-model="formItem.archiveNumber"/>
                            </FormItem>
                        </Col>
                    </Row>

                    <h3>证件</h3>
                    <Row>
                        <Col span="8">
                            <FormItem label="生产许可证" prop="permit">
                                <Input v-model="formItem.permit"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="许可证期限" prop="permitExp">
                                <DatePicker type="date" placeholder="许可证到期日" v-model="formItem.permitExp"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <Upload action="post">
                                <Button type="primary" icon="ios-cloud-upload-outline">上传文件</Button>
                            </Upload>
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
                                <DatePicker type="date" placeholder="营业执照到期日" v-model="formItem.licenseExp"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="6">
                            <FormItem label="检查首营档案">
                                <Checkbox v-model="formItem.needFirstCheck" />
                            </FormItem>
                        </Col>
                        <Col span="12">
                            <FormItem label="审查结论" prop="comment">
                                <Input v-model="formItem.comment"/>
                            </FormItem>
                        </Col>
                    </Row>

                    <h3>银行帐户</h3>
                    <Row>
                        <Col span="6">
                            <FormItem label="开户银行" prop="bankName">
                                <Input v-model="formItem.bankName"/>
                            </FormItem>
                        </Col>
                        <Col span="8">
                            <FormItem label="银行帐号" prop="bankAccount">
                                <Input v-model="formItem.bankAccount"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="税号" prop="taxNumber">
                                <Input v-model="formItem.taxNumber"/>
                            </FormItem>
                        </Col>
                    </Row>

                </Form>
            </Card>
        </Row>


    </div>
</template>

<script>
import axios from 'axios'
import Vue from 'vue';
import iviewArea from 'iview-area';
import util from '@/libs/util.js';

Vue.use(iviewArea);

export default {
    name: 'goods-info',
    data () {
        return {
            formItem: {},
            categoryList: [],
            ruleValidate: {
                name: [
                    {required: true, message: '名称不能为空', trigger: 'blur'}
                ],
                address: [
                    {required: true, message: '地址不能为空', trigger: 'blur'}
                ],
            }
        };
    },
    methods: {
        init () {
            var self = this;
            if (this.$route.params && this.$route.params.supplier_id) {
                util.ajax.get('/supplier/' + this.$route.params.supplier_id)
                    .then(function (response) {
                        self.formItem = response.data;
                        util.title(response.data.name);
                        util.setCurrentPageTitle(self, response.data.name, true);
                        var placeCodes = [];
                        var rawCodes = response.data.placeCodes;
                        if (rawCodes) {
                            for (var i = 0; i < rawCodes.length; i++) {
                                placeCodes.push(rawCodes[i].code);
                            }
                        }
                        self.formItem.placeCodes = placeCodes;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            } else {
                util.title('新建供应商');
                util.setCurrentPageTitle(this, '新建', true);
                this.formItem = {
                    pinyin: ''
                };
            }
        },
        submitSupplier() {
            var self = this;
            util.ajax.post('/supplier/add', this.formItem)
                    .then(function (response) {
                        self.formItem.id = response.data;
                        self.$Message.info("供应商" + self.formItem.name + "保存成功");
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
        },
        onChangeName() {
            if (this.formItem.name !== '') {
                util.setCurrentPageTitle(this, this.formItem.name, true);
                var self = this;
                util.ajax.post('/util/pinyinAbbr', { name: this.formItem.name })
                    .then(function (response) {
                        self.formItem.pinyin = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
            }
        },
        searchRelatedGoods() {
            let argu = { supplier_id: this.$route.params.supplier_id };
            this.$router.push({
                name: 'basic_data_good',
                params: argu
            });
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

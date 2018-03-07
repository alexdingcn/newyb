<style lang="less">
    @import '../../styles/common.less';
    @import './supplier.less';
</style>

<template>
	<div>
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

            <Tabs value="cert" type="card" @on-click="changeTabs">
                <TabPane label="证件" name="cert" icon="document-text">
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
                </TabPane>

                <TabPane label="业务代表" name="agent" icon="person-stalker" :disabled="!formItem.id">
                    <Card :bordered="false" dis-hover>
                        <p slot="title">
                            <Icon type="information-circled"></Icon>
                            <span class="help">业务代表是指上游单位和我方接洽的业务员</span>
                        </p>
                        <div slot="extra">
                            <ButtonGroup>
                                <Button type="primary" icon="android-add-circle" @click="addContact" size="small">添加</Button>
                            </ButtonGroup>
                        </div>
                        <Row type="flex" justify="center" align="middle">
                            <Table border :columns="contactColumns" :data="contactData" ref="contactTable" size="small"></Table>
                        </Row>
                    </Card>
                    
                </TabPane>

                <TabPane label="银行帐户" name="bank" icon="card">
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
                </TabPane>
            </Tabs>
        </Form>
    </Card>
    	<Modal v-model="addContactModal" width="360">
			<p slot="header">
				<Icon type="ios-plus-outline"></Icon>
				<span>新增联系人</span>
			</p>
			<div>
				<Form :model="contact" :label-width="60">
					<FormItem label="姓名" prop="name">
						<Input v-model="contact.name"/>
					</FormItem>
					<FormItem label="电话" prop="phone">
						<Input v-model="contact.phone"/>
					</FormItem>
					<FormItem label="身份证" prop="idcard">
						<Input v-model="contact.idcard"/>
					</FormItem>
					<FormItem label="备注" prop="comment">
						<Input v-model="contact.comment"/>
					</FormItem>
				</Form>
			</div>
			<div slot="footer">
				<Button type="primary" @click="saveContact">保存</Button>
			</div>
		</Modal>
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
        	addContactModal: false,
        	contactData: [],
        	contact: {},
            formItem: {},
            categoryList: [],
            contactColumns: [
                {
                    key: 'name',
                    title: '姓名',
                    align: 'center',
                },
                {
                    key: 'phone',
                    title: '电话',
                    align: 'center',
                },
                {
                    key: 'idcard',
                    title: '身份证号',
                    align: 'center',
                },
                {
                    key: 'enable',
                    title: '启用',
                    align: 'center',
                    width: 100,
                },
                {
                    key: 'comment',
                    title: '备注',
                    align: 'center',
                    width: 200,
                },
                {
                    title: '操作',
                    align: 'center',
                    width: 200,
                    render: (h, params) => {
                            return h('div', [
                                h('Button', {
                                    props: {
                                        type: 'text',
                                        size: 'small'
                                    }
                                }, '修改'),
                                h('Button', {
                                    props: {
                                        type: 'text',
                                        size: 'small'
                                    },
                                    on: {
										click: () => {
											var self = this;
											util.ajax.post('/supplier/contact/remove/' + params.row.id)
												.then(function (response) {
													self.loadContacts();
												})
												.catch(function (error) {
													console.log(error);
												});
										}
									}
                                }, '删除')
                            ]);
                        }
                },
            ],
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
        mounted() {
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
        },
        addContact() {
			this.addContactModal = true;
        },
        changeTabs(tabName) {
        	console.log(this.formItem.id);
        	if (tabName === 'agent') {
        		this.loadContacts();
        	}
        },
        loadContacts() {
        	var self = this;
        	if (this.formItem.id) {
        		util.ajax.get('/supplier/contact/list', { params: {supplierId: this.formItem.id} })
        			.then(function (response) {
                        self.contactData = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
        	}
        },
        saveContact() {
        	var self = this;
        	if (this.formItem.id) {
        		this.contact.supplierId = this.formItem.id;
        		util.ajax.post('/supplier/contact/add', this.contact)
        			.then(function (response) {
                        self.$Message.info("供应商业务代表" + self.contact.name + "保存成功");
                        self.addContactModal = false;
                        self.loadContacts();
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
        	} else {
        		this.$Message.warning("请先保存供应商");
        	}
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

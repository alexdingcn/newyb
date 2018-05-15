<template>
    <Modal v-model="showRepModal" :mask-closable="false" title="客户地址" @on-cancel="onCancel">
        <Form ref="repForm" :model="repData" :label-width="100" :rules="repFormValidate">
            <FormItem label="姓名" prop="name">
                <Input type="text" v-model="repData.name"/>
            </FormItem>
            <FormItem label="联系电话" prop="contactPhone">
                <Input type="text" v-model="repData.contactPhone"/>
            </FormItem>
            <FormItem label="收货地址" prop="repertoryAddress">
                <Input type="text" v-model="repData.repertoryAddress"/>
            </FormItem>
            <FormItem label="邮编">
                <Input type="text" v-model="repData.postcode"/>
            </FormItem>
            <FormItem label="是否启用">
                <Checkbox v-model="repData.enabled"></Checkbox>
            </FormItem>
            <FormItem label="是否默认使用">
                <Checkbox v-model="repData.isDefault"></Checkbox>
            </FormItem>
            <FormItem label="备注">
                <Input type="text" v-model="repData.comment"/>
            </FormItem>
        </Form>
        <div slot="footer">
            <Button type="success" :loading="repSubmitBtnLoading" v-if="repData.id" @click="doEditCustomerRep">保存
            </Button>
            <Button type="primary" :loading="repSubmitBtnLoading" v-if="!repData.id" @click="doAddCustomerRep">提交
            </Button>
        </div>
    </Modal>

</template>


<script>
    import util from '@/libs/util.js';
    export default {
        name: 'customer-rep',
        props: {
            value: {
                type: Object,
                default: null
            },
            showModal: {
                type: Boolean,
                default: false
            },
            customer: {
                type: Number | String,
                default: null
            }
        },
        data() {
            return {
                showRepModal: false,
                repSubmitBtnLoading: false,
                customerId: 0,
                repData: {},
                repFormValidate: {
                    name: [
                        {required: true, message: '代表人姓名必输', trigger: 'blur'}
                    ],
                    contactPhone: [
                        {required: true, message: '联系方式必输', trigger: 'blur'}
                    ],
                    repertoryAddress: [
                        {required: true, message: '收货地址必输', trigger: 'blur'}
                    ]
                }
            }
        },
        watch: {
            showModal (val) {
                this.showRepModal = val;
            },
            value (val) {
                this.repData = val;
            },
            customer (val) {
                this.customerId = val;
            }
        },
        methods: {
            show(customerId, data) {
                if (data) {
                    this.repData = data;
                } else {
                    this.repData = {};
                }
                if (customerId) {
                    this.customerId = customerId;
                }
                this.showRepModal = true;
            },
            onCancel() {
                this.$emit('on-closed', this.repData);
            },
            doAddCustomerRep () {
                let customerId = this.customerId;
                this.repSubmitBtnLoading = true;
                let self = this;
                this.$refs.repForm.validate(valid => {
                    if (!valid) {
                        self.$Message.warning('请检查表单必输项信息');
                        self.repSubmitBtnLoading = false;
                    }
                    else {
                        self.repData.customerId = customerId;
                        util.ajax.post('/customer/rep/add', this.repData)
                                .then(function (response) {
                                    self.repSubmitBtnLoading = false;
                                    self.$Message.success('新建客户代表人信息成功');
                                    self.showRepModal = false;
                                    self.$emit('on-closed', self.repData);
                                })
                                .catch(function (error) {
                                    self.repSubmitBtnLoading = false;
                                    util.errorProcessor(self, error);
                                })
                        ;
                    }
                });
            },

            doEditCustomerRep () {
                let customerId = this.customerId;
                this.repSubmitBtnLoading = true;
                let self = this;
                if (!customerId) {
                    this.$Notice.info({title: '操作提醒', desc: '请先提交客户基本信息建立客户'});
                    this.repSubmitBtnLoading = false;
                    return;
                }
                if (!this.repData.id || this.repData.id === '') {
                    this.$Notice.warn({title: '系统异常', desc: '获取需要修改的代表人编号失败，请重新选择'});
                    this.repSubmitBtnLoading = false;
                    return;
                }
                util.ajax.post('/customer/rep/update', this.repData)
                        .then(function (response) {
                            self.repSubmitBtnLoading = false;
                            self.$Message.success('保存代表人信息成功');
                            self.showRepModal = false;
                            self.$emit('on-closed', self.repData);
                        })
                        .catch(function (error) {
                            self.repSubmitBtnLoading = false;
                            util.errorProcessor(this, error);
                        });
            }
        }
    }
</script>
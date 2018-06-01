<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Modal v-model="isShowModal" :closable="false" :title="modalTitle">
        <Form ref="custCatForm" :model="custCatFormData" :rules="ruleValidate" label-position="right" :label-width="100" class="margin-right-10">
            <FormItem label="类别名称" prop="name">
                <Input type="text" v-model="custCatFormData.name" placeholder="输入客户类名称"/>
            </FormItem>
            <FormItem label="上级类别">
                <Select v-model="custCatFormData.parentId" placeholder="选择上级类别">
                    <Option v-for="item in parentCateList" :value="item.id" :key="item.id">{{ item.name }} </Option>
                </Select>
            </FormItem>
            <FormItem label="批发价折扣(%)">
                <InputNumber v-model="custCatFormData.batchDiscount" :min="0" style="width:50%;"></InputNumber>
            </FormItem>
            <FormItem label="市场价折扣(%)">
                <InputNumber v-model="custCatFormData.retailDiscount" :min="0" style="width:50%;"></InputNumber>
            </FormItem>
            <FormItem label="备注">
                <Input v-model="custCatFormData.comment" />
            </FormItem>
        </Form>
        <div slot="footer">
          <Row >
            <i-col span="6" offset="6">
              <Button type="primary" :loading="loading" @click="ok" long>
                <span v-if="!loading">提交</span>
                <span v-else>正在提交...</span>
              </Button>
            </i-col>
            <i-col span=6 class="padding-left-10">
              <Button @click="closedModal" long>取消</Button>
            </i-col>
          </Row>
        </div>
    </Modal>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'customer-category',
    props: {
        action: {
            type: String,
            required: true,
            validator: function (value) {
                return value === 'add' || value === 'edit';
            }
        },
        categorys: {
            type: Array,
            default: []
        },
        editeData: {
            type: Object,
            default: null
        },
        showModal: {
            type: Boolean,
            default: false
        }
    },
    data () {
        const validNameExist = (rule, value, callback) => {
            if (this.custCatFormData.name && this.custCatFormData.name.toString().trim() != '') {
                callback();
            } else {
                this.custCatFormData.name = '';
                callback(new Error('类别名称不能为空'));
            }
        };

        return {
            isShowModal: false,
            loading: false,
            custCatFormData: {
                id: '',
                categoryNo: '',
                name: '',
                parentId: -1,
                batchDiscount: 100,
                retailDiscount: 100,
                comment: ''
            },
            ruleValidate: {
                name: [
                    { required: true, message: '类别名称不能为空', trigger: 'blur' },
                    { validator: validNameExist, trigger: 'blur' }
                ],
            }
        };
    },
    computed: {
        parentCateList () {
            return [
                {
                    id: -1,
                    name: '顶级'
                },
                ...this.categorys
            ];
        },
        modalTitle () {
            let updName = '';
            if (this.action === 'edit' && this.editeData) {
                updName = '修改 ' + this.editeData.name + '(' + this.editeData.id + ')' + '信息';
            }
            return this.action === 'add' ? '新建客户分组' : updName;
        }
    },
    watch: {
        showModal (data) {
            this.isShowModal = data;
        },
        isShowModal (data) {
            if (!data) {
                this.$emit('dialog-closed');
            } else {
                this.initUpdData();
            }
        }
    },
    methods: {
        initUpdData () {
            if (this.action === 'edit' && this.editeData) {
                this.custCatFormData.id = this.editeData.id;
                this.custCatFormData.name = this.editeData.name;
                this.custCatFormData.parentId = this.editeData.parentId;
                this.custCatFormData.comment = this.editeData.comment;
                this.custCatFormData.batchDiscount = this.editeData.batchDiscount;
                this.custCatFormData.retailDiscount = this.editeData.retailDiscount;
            }
        },

        ok () {
            this.loading = true;
            this.$refs.custCatForm.validate(valid => {
                if (!valid) {
                    this.loading = false;
                } else {
                    let actionVal = this.action;
                    let categoryData = this.custCatFormData;
                    if (actionVal === 'add') {
                        this.doAddCategory(categoryData);
                    } else if (actionVal === 'edit') {
                        this.doUpdateCategory(categoryData);
                    } else {
                        this.$Message.error('系统异常, 操作类型错误');
                    }
                    this.loading = false;
                }
            });
        },

        closedModal () {
            this.$refs.custCatForm.resetFields();
            this.isShowModal = false;
        },

        submitSuccessEvent () {
            let resultData = {
                action: this.action,
                formData: this.custCatFormData
            };
            this.$emit('category-submit', resultData);
        },

        doAddCategory (data) {
            util.ajax
                .post('/customer/category/add', data)
                .then((respones) => {
                    this.$Message.success('新建客户类成功');
                    this.submitSuccessEvent();
                    this.isShowModal = false;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        doUpdateCategory (data) {
            util.ajax
                .post('/customer/category/update', data)
                .then((respones) => {
                    this.$Message.success('客户类信息修改成功');
                    this.submitSuccessEvent();
                    this.isShowModal = false;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        }
    }
};
</script>

<style >

</style>



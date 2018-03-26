<style lang="less">
    @import './register.less';
</style>

<template>
    <div class="register" @keydown.enter="handleSubmit">
        <div class="register-con">
            <Card>
                    <Form ref="formCustom" :model="formCustom" :rules="ruleCustom" :label-width="110">
                        <Form-item label="企业名称" prop="company">
                            <Input v-model="formCustom.company" placeholder="请输入"></Input>
                        </Form-item>
                        <small class="help">需与当地政府颁发的商业许可证或企业注册证上的企业名称完全一致，信息审核成功后，企业名称不可修改</small>

                        <Form-item label="营业执照注册号" prop="businessLicense">
                            <Input v-model="formCustom.businessLicense" placeholder="请输入"></Input>
                        </Form-item>
                        <small class="help">请输入15位营业执照号或18位的统一社会信用代码</small>

                        <Form-item label="管理员姓名" prop="realName">
                            <Input v-model="formCustom.realName" ></Input>
                        </Form-item>
                        <Form-item label="管理员身份证号" prop="idCard">
                            <Input v-model="formCustom.idCard" placeholder="请输入"></Input>
                        </Form-item>
                        <hr/>

                        <Form-item label="用户名" prop="userName">
                            <Input v-model="formCustom.userName" ></Input>
                        </Form-item>
                        <Form-item label="密码" prop="password">
                            <Input v-model="formCustom.password" type="password"></Input>
                        </Form-item>
                        <Form-item label="管理员手机号" prop="mobile">
                            <Input v-model="formCustom.mobile" placeholder="请输入"></Input>
                        </Form-item>
                        <Button type="primary" @click="handleSubmit('formCustom')" long :loading="loading">提交
                        </Button>
                    </Form>
            </Card>
        </div>
    </div>
</template>
<script>
    import Cookies from 'js-cookie';
    import util from '@/libs/util.js';

    export default {
        name: 'type',
        data () {
            const validatebusinessLicense = (rule, value, callback) => {
                if (value.toString().length !== 15 && value.toString().length !== 18) {
                    callback(new Error('请输入正确的营业执照注册号'));
                } else {
                    callback();
                }
            };
            return {
                token: true,
                loading: false,
                formCustom: {
                    company: '',
                    businessLicense: '',
                    realName: '',
                    idCard: '',
                    userName: '',
                    password: '',
                    mobile: ''
                },
                ruleCustom: {
                    company: [
                        {message: '企业名称不能为空', trigger: 'blur'}
                    //                        {validator: validatebusinessLicense, trigger: 'blur'}
                    ],
                    businessLicense: [
                        {message: '营业执照注册号不能为空', trigger: 'blur'}
                    //                        {validator: validatebusinessLicense, trigger: 'blur'}
                    ],
                    userName: [
                        {required: true, message: '用户名不能为空', trigger: 'blur'}
                    ],
                    password: [
                        {required: true, message: '密码不能为空', trigger: 'blur'}
                    ],
                    idCard: [
                        {message: '身份证号不能为空', trigger: 'blur'},
                        {
                            message: '身份证号格式不正确',
                            trigger: 'blur',
                            pattern: /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{4}$/
                        }
                    ],
                    mobile: [
                        {required: true, message: '手机号不能为空', trigger: 'blur'},
                        {message: '手机号格式不正确', len: 11, trigger: 'blur', pattern: /^[1][3,4,5,7,8][0-9]{9}$/}
                    ]
                }
            };
        },
        methods: {
            handleSubmit (name) {
                this.loading = true;
                this.$refs[name].validate((valid) => {
                    if (valid) {
                        var self = this;
                        util.ajax.post('/register', this.formCustom)
                            .then(function (response) {
                                self.loading = false;
                                Cookies.set('user', self.formCustom.userName);
                                self.$router.replace({
                                    name: 'login'
                                });
                            })
                            .catch(function (error) {
                                console.log(error);
                                self.loginResponse = error.message;
                                self.loading = false;
                            });
                    } else {
                        this.loading = false;
                        this.$Message.error('表单验证失败!');
                    }
                });
            },
            save () {

            }
        },
        created () {

        }
    };
</script>

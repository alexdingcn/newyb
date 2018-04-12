<style lang="less">
    @import './register.less';
</style>

<template>
    <div class="register" @keydown.enter="handleSubmit">
        <div class="register-con">
            <Card>
                    <Form ref="formCustom" :model="formCustom" :rules="ruleCustom" :label-width="110">
                        <small class="help">注意：<strong>需与当地政府颁发的商业许可证或企业注册证上的企业名称完全一致，信息审核成功后，企业名称不可修改</strong></small>
                        <Form-item style="margin-top: 10px;" label="企业名称" prop="company">
                            <Input v-model="formCustom.company" placeholder="请输入公司名称"></Input>
                        </Form-item>
                        <Form-item label="营业执照注册号" prop="businessLicense">
                            <Input v-model="formCustom.businessLicense" placeholder="请输入营业执照号"></Input>
                        </Form-item>
                        <Form-item label="管理员姓名" prop="realName">
                            <Input v-model="formCustom.realName" ></Input>
                        </Form-item>
                        <Form-item label="管理员身份证号" prop="idCard">
                            <Input v-model="formCustom.idCard" placeholder="请输入"></Input>
                        </Form-item>
                        <hr/>

                        <Form-item style="margin-top: 15px;" label="用户名" prop="userName">
                            <Input v-model="formCustom.userName" @on-blur="validateUserName"></Input>
                            <Alert type="error" v-show="userNameMessage" show-icon>{{ userNameMessage }}</Alert>
                        </Form-item>
                        <Form-item label="密码" prop="password">
                            <Input v-model="formCustom.password" type="password"></Input>
                        </Form-item>
                        <Form-item label="管理员手机号" prop="mobile">
                            <Input v-model="formCustom.mobile" placeholder="请输入手机号">
                                    <Button slot="append" @click="handleVerifyCode" :disabled="formCustom.mobile === '' || countDown > 0">{{verifyText}}</Button>
                            </Input>
                        </Form-item>
                        <FormItem label="验证码" prop="verifyCode">
                            <Input v-model="formCustom.verifyCode"  placeholder="验证码" :maxlength="6" />
                        </FormItem>

                        <Alert type="error" v-show="loginResponse" show-icon>{{ loginResponse }}</Alert>

                        <Button type="primary" :disabled="!submitEnabled" @click="handleSubmit('formCustom')" long :loading="loading">提交
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
                    mobile: '',
                    verifyCode: ''
                },
                loginResponse: '',
                userNameMessage: '',
                countDown: 0,
                submitEnabled: false,
                ruleCustom: {
                    company: [
                        {required: true, message: '企业名称不能为空', trigger: 'blur'}
                    //                        {validator: validatebusinessLicense, trigger: 'blur'}
                    ],
                    businessLicense: [
                        {required: true, message: '营业执照注册号不能为空', trigger: 'blur'}
                    //                        {validator: validatebusinessLicense, trigger: 'blur'}
                    ],
                    realName: [
                        {required: true, message: '管理员姓名不能为空', trigger: 'blur'}
                    ],
                    userName: [
                        {required: true, message: '用户名不能为空', trigger: 'blur'}
                    ],
                    password: [
                        {required: true, message: '密码不能为空', trigger: 'blur'}
                    ],
                    idCard: [
                        {required: true, message: '身份证号不能为空', trigger: 'blur'},
                    ],
                    mobile: [
                        {required: true, message: '手机号不能为空', trigger: 'blur'},
                        {message: '手机号格式不正确', len: 11, trigger: 'blur', pattern: /^[1][3,4,5,7,8][0-9]{9}$/}
                    ],
                    verifyCode: [
                        {required: true, message: '短信验证码必输', trigger: 'blur'}
                    ]
                }
            };
        },
        computed: {
            verifyText: function () {
                return this.countDown > 0 ? this.countDown + 's后重新获取' : '获取验证码';
            }
        },
        methods: {

            countDownTimer: function () {
                if (this.countDown > 0) {
                    this.countDown--;
                    setTimeout(this.countDownTimer, 1000);
                }
            },

            handleVerifyCode () {
                var self = this;
                this.countDown = 60;
                this.countDownTimer();
                util.ajax.post('/phone/verifycode', {
                        mobile: self.formCustom.mobile,
                        bizNo: self.formCustom.businessLicense
                    })
                    .then(function (response) {
                        if (response.status === 200) {
                            self.submitEnabled = true;
                            self.countDown = 60;
                        }
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            },

            validateUserName() {
                let value = this.formCustom.userName;
                this.userNameMessage = '';
                if(!value) {
                    this.userNameMessage = '用户名必输';
                    return
                }
                util.ajax.get('/user/valid/nickname', {params: {nickname: value}})
                    .then((response) => {
                        //TO NOTHING
                    })
                    .catch((error) => {
                        let result = error.response ? error.response.data : '';
                        if (result && result.message) {
                            this.userNameMessage = result.message;
                        }else {
                            this.userNameMessage = '用户名检验失败';
                        }
                    });
            },

            handleSubmit (name) {
                self.loginResponse = '';
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
                                self.loading = false;
                                let result = error.response ? error.response.data : '';
                                if (result && result.message) {
                                    self.loginResponse = result.message;
                                }else {
                                    self.loginResponse = '注册失败';
                                }
                            });
                    } else {
                        this.loading = false;
                        this.$Message.error('表单验证失败!');
                    }
                });
            }
        }
    };
</script>

<style>
.ivu-form-item {
    margin-bottom: 10px;
}
</style>


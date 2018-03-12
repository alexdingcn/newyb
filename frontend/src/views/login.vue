<style lang="less">
    @import './login.less';
</style>

<template>
    <div class="login" @keydown.enter="handleSubmit">
        <div class="login-con">
            <Card :bordered="false">
                <p slot="title">
                    <Icon type="log-in"></Icon>
                    欢迎登录
                </p>
                <div class="form-con">
                    <Form ref="loginForm" :model="form" :rules="rules">
                        <FormItem prop="username">
                            <Input v-model="form.username" placeholder="请输入用户名/手机号">
                                <span slot="prepend">
                                    <Icon :size="16" type="person"></Icon>
                                </span>
                            </Input>
                        </FormItem>
                        <FormItem prop="password">
                            <Input type="password" v-model="form.password" placeholder="请输入密码">
                                <span slot="prepend">
                                    <Icon :size="14" type="locked"></Icon>
                                </span>
                            </Input>
                        </FormItem>

                        <FormItem>
                            <Button @click="handleSubmit" type="primary" long>登录</Button>
                        </FormItem>
                        <Alert type="error" v-show="loginResponse" show-icon>{{ loginResponse }}</Alert>

                        <Row>
                            <Col span="12">
                                <Checkbox-group v-model="form.remember">
                                    <Checkbox label="记住我"></Checkbox>
                                </Checkbox-group>
                            </Col>
                            <Col span="12">
                                <a style="float:right" @click="toRegister">新用户注册</a>
                            </Col>
                        </Row>
                    </Form>

                </div>
            </Card>
        </div>
    </div>
</template>

<script>
import Cookies from 'js-cookie';
import util from '@/libs/util.js';
import Qs from 'qs'

export default {
    data () {
        return {
            loginResponse: '',
            form: {
                username: Cookies.get('user'),
                password: ''
            },
            rules: {
                userName: [
                    { required: true, message: '账号不能为空', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '密码不能为空', trigger: 'blur' }
                ]
            }
        };
    },
    methods: {
        handleSubmit () {
            this.$refs.loginForm.validate((valid) => {
                if (valid) {
                    var self = this;
                    var data = Qs.stringify(this.form);
                    util.ajax.post('/login', data, {
                                headers:{'Content-Type':'application/x-www-form-urlencoded'}
                            })
                            .then(function (response) {
                                self.$store.commit('setToken', 'token');
                                Cookies.set('user', self.form.username);
                                self.$store.commit('setAvator', 'https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3448484253,3685836170&fm=27&gp=0.jpg');
                                if (self.form.username === 'iview_admin') {
                                    Cookies.set('access', 0);
                                } else {
                                    Cookies.set('access', 1);
                                }
                                self.$router.push({
                                    name: 'home_index'
                                });

                            })
                            .catch(function (error) {
                                self.loginResponse = error.message;
                            });
                }
            });
        },
        toRegister() {
            this.$router.push('/register');
        }
    }
};
</script>

<style>

</style>

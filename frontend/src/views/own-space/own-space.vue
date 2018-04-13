<style lang="less">
    @import './own-space.less';
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="person"></Icon>
                个人信息
            </p>
            <div>
                <Form 
                    ref="userForm"
                    :model="userDetail" 
                    :label-width="100" 
                    label-position="right"
                >
                    <FormItem label="用户名：" prop="nickname">
                        <strong> {{ userDetail.nickname }} </strong>
                    </FormItem>
                    <FormItem label="公司：">
                        <strong>{{ userDetail.companyName }}</strong>
                    </FormItem>
                    <FormItem label="用户手机：" prop="mobile">
                        <strong>{{ userDetail.mobile }} </strong>
                        <Button type="text" icon="edit" @click="editMobileBtnClick"></Button>
                    </FormItem>
                    <FormItem label="真实姓名：" >
                        <div style="display:inline-block;width:250px;">
                            <Input v-model="userDetail.realname" ></Input>
                        </div>
                    </FormItem>
                    <FormItem label="电子邮箱：" >
                        <div style="display:inline-block;width:250px;">
                            <Input v-model="userDetail.email" ></Input>
                        </div>
                    </FormItem>
                    <FormItem label="固定电话：" >
                        <div style="display:inline-block;width:250px;">
                            <Input v-model="userDetail.phone" ></Input>
                        </div>
                    </FormItem>
                    <FormItem label="身份证号：" >
                        <div style="display:inline-block;width:250px;">
                            <Input v-model="userDetail.idcard" ></Input>
                        </div>
                    </FormItem>
                    <FormItem label="联系地址：" >
                        <div style="display:inline-block;width:400px;">
                            <Input v-model="userDetail.address" ></Input>
                        </div>
                    </FormItem>
                    
                    <FormItem label="登录密码：">
                        <Button type="text" size="small" @click="showEditPassword">修改密码</Button>
                    </FormItem>
                    <div>
                        <Button type="text" style="width: 100px;" @click="cancelEditUserInfor">取消</Button>
                        <Button type="primary" style="width: 100px;" :loading="save_loading" @click="saveEdit">保存</Button>
                    </div>
                </Form>
            </div>
        </Card>
        <Modal v-model="editPasswordModal" :closable='false' :mask-closable=false :width="500">
            <h3 slot="header" style="color:#2D8CF0">修改密码</h3>
            <Form ref="editPasswordForm" :model="editPasswordForm" :label-width="100" label-position="right" :rules="passwordValidate">
                <FormItem label="原密码" prop="oldPass" :error="oldPassError">
                    <Input type="password" v-model="editPasswordForm.oldPass" placeholder="请输入现在使用的密码" ></Input>
                </FormItem>
                <FormItem label="新密码" prop="newPass">
                    <Input type="password" v-model="editPasswordForm.newPass" placeholder="请输入新密码，至少6位字符" ></Input>
                </FormItem>
                <FormItem label="确认新密码" prop="rePass">
                    <Input type="password" v-model="editPasswordForm.rePass" placeholder="请再次输入新密码" ></Input>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="text" @click="cancelEditPass">取消</Button>
                <Button type="primary" :loading="savePassLoading" @click="saveEditPass">保存</Button>
            </div>
        </Modal>

        <Modal v-model="editMobileModal" :closable='false' :mask-closable=false :width="400">
            <h3 slot="header" style="color:#2D8CF0">修改手机号</h3>
            <Form ref="editMobileForm" :model="editMobileForm" :label-width="100" label-position="right" :rules="mobileValidate">
                <FormItem label="新手机号" prop="mobile">
                    <Input v-model="editMobileForm.mobile" placeholder="请输入新手机号码" @on-blur="validMobileExist" >
                        <Button slot="append" @click="handleVerifyCode" :disabled="sendVerifyCodeDisabled">{{verifyText}}</Button>
                    </Input>
                </FormItem>
                <FormItem label="验证码" prop="verifyCode">
                    <Input v-model="editMobileForm.verifyCode" placeholder="请输入短信验证码" ></Input>
                </FormItem>
            </Form>
            <Alert type="error" v-show="valdMessage" show-icon>{{ valdMessage }}</Alert>
            <div slot="footer">
                <Button type="text" @click="cancelEditMobile">取消</Button>
                <Button type="primary" :loading="saveMobileLoading" @click="saveEditMobile" >保存</Button>
            </div>
        </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";

export default {
    name: 'ownspace_index',
    data () {
        const valideRePassword = (rule, value, callback) => {
            if (value !== this.editPasswordForm.newPass) {
                callback(new Error('两次输入密码不一致'));
            } else {
                callback();
            }
        };
        const valideMobile = (rule, value, callback) => {
            if (!value) {
                callback(new Error('新手机号必输'));
            }else {
                var re = /^1[0-9]{10}$/;
                if (!re.test(value)) {
                    callback(new Error('请输入正确格式的手机号'));
                } else {
                    callback();
                }
            }
        }
        return {
            userDetail: {},
            save_loading: false,
            editPasswordModal: false, // 修改密码模态框显示
            savePassLoading: false,
            oldPassError: '',
            editPasswordForm: {
                oldPass: '',
                newPass: '',
                rePass: ''
            },
            passwordValidate: {
                oldPass: [
                    { required: true, message: '请输入原密码', trigger: 'blur' }
                ],
                newPass: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 6, message: '请至少输入6个字符', trigger: 'blur' },
                    { max: 32, message: '最多输入32个字符', trigger: 'blur' }
                ],
                rePass: [
                    { required: true, message: '请再次输入新密码', trigger: 'blur' },
                    { validator: valideRePassword, trigger: 'blur' }
                ]
            },
            editMobileModal: false,
            saveMobileLoading: false,
            countDown: 0,
            editMobileForm: {
                mobile: '',
                verifyCode: ''
            },
            valdMessage: '',
            mobileValidate: {
               mobile: [
                   { required: true, message: '请输入新手机号', trigger: 'blur' },
                   { validator: valideMobile, trigger: 'blur' }
               ],
               verifyCode: [
                   { required: true, message: '验证码必输', trigger: 'blur' }
               ]
            }
        };
    },
    computed: {
        verifyText: function () {
            return this.countDown > 0 ? this.countDown + 's后重新获取' : '获取验证码';
        },
        sendVerifyCodeDisabled() {
              let result = true;
              var re = /^1[0-9]{10}$/;
              if (this.editMobileForm.mobile && re.test(this.editMobileForm.mobile) && this.countDown <= 0 && !this.valdMessage) {
                  result = false;
              }
              return result;
        }
    },
    methods: {
        showEditPassword () {
            this.editPasswordModal = true;
        },
        cancelEditPass () {
            this.editPasswordModal = false;
        },
        saveEditPass () {
            this.$refs['editPasswordForm'].validate((valid) => {
                if (valid) {
                    this.savePassLoading = true;
                    let reqData = {
                        userId: this.userDetail.id,
                        oldPass: this.editPasswordForm.oldPass,
                        newPass: this.editPasswordForm.newPass
                    };
                    util.ajax.post('/user/update/password', reqData)
                        .then((response) => {
                            this.$Message.success('修改成功, 需要重新登录');
                            this.$router.push({
                                name: 'login'
                            });
                        })
                        .catch((error) => {
                            util.errorProcessor(this, error);
                        });
                } else {
                    this.$Message.warning('数据格式错误');
                }
            });
        },

        cancelEditUserInfor () {
            this.$store.commit('removeTag', 'ownspace_index');
            localStorage.pageOpenedList = JSON.stringify(this.$store.state.app.pageOpenedList);
            let lastPageName = '';
            if (this.$store.state.app.pageOpenedList.length > 1) {
                lastPageName = this.$store.state.app.pageOpenedList[1].name;
            } else {
                lastPageName = this.$store.state.app.pageOpenedList[0].name;
            }
            this.$router.push({
                name: lastPageName
            });
        },
        
        saveEdit () {
            if (!this.userDetail || !this.userDetail.id) {
                this.$Message.warning('获取用户详情失败, 请刷新页面重试');
                return;
            }
            this.save_loading = true;
            util.ajax.put('user/save', this.userDetail)
                .then((response) => {
                    this.save_loading = false;
                    this.$Message.success('保存成功');
                    this.userDetail = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        countDownTimer: function () {
            if (this.countDown > 0) {
                this.countDown--;
                setTimeout(this.countDownTimer, 1000);
            }
        },

        validMobileExist() {
            this.valdMessage = undefined;
            let mobile = this.editMobileForm.mobile;
            if(!mobile) {
                return;
            }
            let re = /^1[0-9]{10}$/;
            if (!re.test(mobile)) {
                return;
            }
            util.ajax.get('/user/valid/mobile', {params: {mobile: mobile}})
                .then((response) => {
                    this.valdMessage = undefined;
                })
                .catch((error) => {
                    this.valdMessage = error.response.data.message;
                })
        },

        handleVerifyCode () {
            var self = this;
            this.countDown = 60;
            this.countDownTimer();
            util.ajax.post('/phone/verifycode', {
                    mobile: self.editMobileForm.mobile,
                })
                .then(function (response) {
                    if (response.status === 200) {
                        self.countDown = 60;
                    }
                })
                .catch(function (error) {
                    util.errorProcessor(self, error);
                });
        },

        editMobileBtnClick() {
            this.editMobileModal = true;
        },

        cancelEditMobile() {
            this.editMobileForm = {};
            this.editMobileModal = false;
        },

        saveEditMobile() {
            this.$refs.editMobileForm.validate(valid => {
                if (!valid) {
                    this.$Message.warning('必输项信息缺失');
                    return;
                } else {
                    let reqData = {
                        userId: this.userDetail.id,
                        mobile: this.editMobileForm.mobile,
                        verifyCode: this.editMobileForm.verifyCode
                    }
                    this.saveMobileLoading = true;
                    util.ajax.put('/user/update/mobile', reqData)
                        .then((response) => {
                            this.saveMobileLoading = false;
                            this.userDetail.mobile = this.editMobileForm.mobile;
                            this.cancelEditMobile();
                            this.$Message.success('修改成功');
                        })
                        .catch((error) => {
                            this.saveMobileLoading = false;
                            util.errorProcessor(this, error);
                        });
                }
            })
        },
        
        init () {
            let storeUser = this.$store.state.user.userDetail;
            if (!storeUser || !storeUser.id) {
                //获取不到用户信息，直接跳转到登录
                this.$router.push({
                    name: 'login'
                });
                return;
            }
            //获取用户详情信息
            util.ajax.get('/user/detail', {params: {userId: storeUser.id}})
                .then((response) => {
                    this.userDetail = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        }
    },
    mounted () {
        this.init();
    }
};
</script>

<style>

</style>

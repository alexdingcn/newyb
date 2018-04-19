<style lang="less">
    @import '../../styles/common.less';
    body {
        overflow: auto;
    }
    .bizApply .ivu-form-item {
        margin-bottom: 10px;
    }
</style>


<template>
    <div class="bizApply">
        <div class="logo-con margin-top-10 center">
            <img src="https://www.yibanmed.com/images/logo2.0.png" key="min-logo"/>
        </div>

        <Form ref="formInline" :model="bizLicenseForm" :label-width="80" :rule="ruleValidate"
              style="padding: 10px;" v-show="formShow">

            <RadioGroup v-model="bizLicenseForm.type" size="large" style="margin-bottom: 10px;  ">
                <Radio label="company">
                    <Icon type="ios-medkit"></Icon>
                    <span>厂商</span>
                </Radio>
                <Radio label="distributor">
                    <Icon type="android-contacts"></Icon>
                    <span>代理商</span>
                </Radio>
            </RadioGroup>

            <h2>公司信息</h2>
            <FormItem label="营业执照号">
                <Input type="text" v-model="bizLicenseForm.license" placeholder="营业执照号" size="large"/>
            </FormItem>

            <FormItem label="公司名称">
                <Input type="text" v-model="bizLicenseForm.companyName" placeholder="公司名称" size="large"/>
            </FormItem>

            <FormItem label="法人">
                <Input type="text" v-model="bizLicenseForm.legalPerson" placeholder="法人" size="large"/>
            </FormItem>

            <FormItem label="身份证">
                <Input type="text" v-model="bizLicenseForm.legalIdcard" placeholder="身份证" size="large"/>
            </FormItem>

            <h2>申请人</h2>

            <FormItem label="联系人姓名">
                <Input v-model="bizLicenseForm.contact" placeholder="联系人姓名" size="large"/>
            </FormItem>

            <FormItem label="联系人手机">
                <Input type="text" v-model="bizLicenseForm.contactMobile" placeholder="联系人手机" size="large" maxlength="11" />
                <Button type="info" @click="handleVerifyCode" class="margin-top-8" :disabled="bizLicenseForm.contactMobile === '' || countDown > 0">{{verifyText}}</Button>
            </FormItem>
            <FormItem label="验证码">
                <Input type="text" v-model="bizLicenseForm.verifyCode"  placeholder="验证码" size="large" maxlength="6" />
            </FormItem>

            <h2>网站帐号</h2>
            <FormItem label="登录帐号">
                <Input v-model="bizLicenseForm.loginAccount" placeholder="英文/数字/下划线" size="large" />
            </FormItem>
            <FormItem label="密码">
                <Input type="password" v-model="bizLicenseForm.password" placeholder="6-20个字符" size="large" />
            </FormItem>
            <FormItem label="确认密码">
                <Input type="password" v-model="bizLicenseForm.confirmPassword" placeholder="确认密码" size="large" />
            </FormItem>
            <Alert type="warning" v-show="errorText != ''">{{ errorText }}</Alert>

            <Button type="success" @click="handleCheck" long :disabled="!submitEnabled" size="large">提交</Button>
        </Form>

        <div v-show="!formShow" >
            <Alert type="success" show-icon class="margin-20">
                你的申请我们已经收到
                <template slot="desc">客户经理会尽快与您联系, 感谢您的关注!</template>
            </Alert>
        </div>
    </div>

</template>

<script>
    import util from '@/libs/util.js';
    import qs from 'qs';

    export default {
        name: 'loan-apply-biz',
        data () {
            return {
                errorText: '',
                formShow: true,
                submitEnabled: false,
                uploadAction: `${util.baseUrl}/loan/bizlic/ocr`,
                bizLicenseForm: {
                    type: 'company',
                    legalPerson: '',
                    companyName: '',
                    legalIdcard: '',
                    contact: '',
                    contactMobile: '',
                    loginAccount: '',
                    password: '',
                    confirmPassword: ''
                },
                contactMobile: '',
                ruleValidate: [],
                countDown: 0,
            };
        },
        computed: {
            verifyText: function () {
                return this.countDown > 0 ? this.countDown + 's后重新获取' : '获取验证码';
            }
        },
        mounted () {

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
                util.ajax.post('https://www.yibanmed.com/Controller/AddUpDataSource.ashx', qs.stringify({
                            phone: this.bizLicenseForm.contactMobile,
                            GetAction: 'SendPhoneCode'
                        }), {
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            }
                        })
                        .then(function (response) {
                            if (response.status === 200) {
                                if (response.data.Result === true) {
                                    self.submitEnabled = true;
                                    self.countDown = 60;
                                } else {
                                    self.errorText = response.data.Msg;
                                }
                            } else {
                                self.errorText = '服务器连接异常';
                            }
                        })
                        .catch(function (error) {
                            util.errorProcessor(self, error);
                        });
            },
            handleCheck() {
                var self = this;
                var postData = {};
                if (this.bizLicenseForm.type === 'company') {
                    postData['RegisterType'] = 'RegiComp';
                } else {
                    postData['RegisterType'] = 'RegiDis';
                }
                postData['GetAction'] = 'SubmitCheckNo1';

                var postDetail = [];
                postDetail.push({"ContorlId":"txt_Phone","Value":this.bizLicenseForm.contactMobile});
                postDetail.push({"ContorlId":"txt_Phone","Value":this.bizLicenseForm.contactMobile});
                postDetail.push({"ContorlId":"txt_PhoneCode","Value":this.bizLicenseForm.verifyCode});
                postData['Value'] = JSON.stringify({ "Data": postDetail });

                util.ajax.post(
                        'https://www.yibanmed.com/Handler/RegisterCheck.ashx',
                        qs.stringify(postData),
                        {
                            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                        }
                        )
                        .then(function (response) {
                            if (response.status === 200) {
                                if (response.data.Result === true) {
                                    self.handleSubmit();
                                } else {
                                    self.errorText = response.data.Msg;
                                }
                            } else {
                                self.errorText = '服务器连接异常';
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
            },
            handleSubmit () {
                var self = this;
                var postData = {};
                postData['GetAction'] = 'SubmitCheckNo2';
                if (this.bizLicenseForm.type === 'company') {
                    postData['RegisterType'] = 'RegiComp';
                } else {
                    postData['RegisterType'] = 'RegiDis';
                    postData['Compid'] = 0;
                }

                var postDetail = [];
                postDetail.push({"ContorlId":"txt_Phone","Value":this.bizLicenseForm.contactMobile});
                postDetail.push({"ContorlId":"txt_CompName","Value":this.bizLicenseForm.companyName});
                postDetail.push({"ContorlId":"txt_Leading","Value":this.bizLicenseForm.legalPerson});
                postDetail.push({"ContorlId":"txt_Licence","Value":this.bizLicenseForm.legalIdcard});
                postDetail.push({"ContorlId":"txt_creditCode","Value":this.bizLicenseForm.license});

                postDetail.push({"ContorlId":"txt_TrueName","Value":this.bizLicenseForm.contact});
                postDetail.push({"ContorlId":"txt_Account","Value":this.bizLicenseForm.loginAccount});
                postDetail.push({"ContorlId":"txt_PassWord","Value":this.bizLicenseForm.password});
                postDetail.push({"ContorlId":"txt_CheckPassWord","Value":this.bizLicenseForm.confirmPassword});

                postData['Value'] = JSON.stringify({ "Data": postDetail });

                util.ajax.post(
                            'https://www.yibanmed.com/Handler/RegisterCheck.ashx',
                            qs.stringify(postData),
                            {
                                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                            }
                        )
                        .then(function (response) {
                            console.log(response);
                            if (response.status === 200) {
                                if (response.data.Result === true) {
                                    self.formShow = false;
                                } else {
                                    self.errorText = response.data.Msg;
                                }
                            } else {
                                self.errorText = '服务器连接异常';
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
            },
        }

    };
</script>

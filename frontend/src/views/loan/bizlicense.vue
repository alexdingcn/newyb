<style lang="less">
    @import '../../styles/common.less';
    body {
        overflow: auto;
    }
</style>


<template>
    <Form ref="formInline" :model="bizLicenseForm" :label-width="80" :rule="ruleValidate"
          style="padding: 10px;">
        <Row class="logo-con">
            <img src="../../images/logo.png" key="min-logo"/>
        </Row>

        <h2>公司信息</h2>
        <FormItem label="营业执照号">
            <Input type="text" v-model="bizLicenseForm.license" placeholder="营业执照号" />
        </FormItem>

        <FormItem label="公司名称">
            <Input type="text" v-model="bizLicenseForm.name" placeholder="公司名称"/>
        </FormItem>

        <FormItem label="法人" v-show="bizLicenseForm.legalPerson != ''">
            <Input type="text" v-model="bizLicenseForm.legalPerson" placeholder="法人"/>
        </FormItem>

        <FormItem>
            <Upload :show-upload-list="false" :max-size="10240" :format="['png', 'jpg', 'jpeg', 'tiff']"
                    :action="uploadAction" :on-success="handleUploadSuccess" :on-format-error="handleFormatError"
                    :on-progress="handleProgress"
                    :on-exceeded-size="handleMaxSize">
                <p>图片格式JPG/JPEG/PNG</p>
                <Button type="ghost" icon="ios-cloud-upload-outline">{{ ocrBtnText }} </Button>
            </Upload>
        </FormItem>

        <h2>申请金额</h2>
        <FormItem label="融资金额">
            <Input v-model="bizLicenseForm.applyAmount" placeholder="申请金额(万)"/>
        </FormItem>
        <FormItem label="期限">
            <Select v-model="bizLicenseForm.applyMonths">
                <Option value='3'>3个月</Option>
                <Option value='6'>6个月</Option>
                <Option value='12'>12个月</Option>
            </Select>
        </FormItem>

        <h2>申请人</h2>
        <FormItem label="身份证">
            <Input type="text" v-model="bizLicenseForm.contactIdcard" placeholder="身份证" readonly/>
        </FormItem>
        <FormItem label="联系人姓名">
            <Input v-model="bizLicenseForm.contact" placeholder="联系人姓名"/>
        </FormItem>
        <FormItem label="联系人手机">
            <Input v-model="bizLicenseForm.contactMobile" placeholder="联系人手机"/>
            <Button type="info" @click="handleVerifyCode" class="margin-top-8" :disabled="bizLicenseForm.contactMobile === ''">发送验证码</Button>
        </FormItem>
        <FormItem label="验证码">
            <Input v-model="bizLicenseForm.verifyCode"  placeholder="验证码"/>
        </FormItem>
        <Button type="success" @click="handleSubmit" long :disabled="!submitEnabled">提交</Button>
    </Form>
</template>

<script>
    import Cookies from 'js-cookie';
    import util from '@/libs/util.js';

    export default {
        name: 'loan-apply-biz',
        data () {
            return {
                submitEnabled: false,
                uploadAction: `${util.baseUrl}/loan/bizlic/ocr`,
                ocrBtnText: '或 上传营业执照自动识别',
                bizLicenseForm: {
                    legalPerson: '',
                    name: '',
                    contactIdcard: '',
                    contact: '',
                    contactMobile: '',
                    applyMonths: '6'
                },
                contactMobile: '',
                ruleValidate: [],
            };
        },
        mounted () {
            this.getFaceResult();
        },
        methods: {
            handleProgress() {
                this.ocrBtnText = '识别中...';
            },
            handleFormatError (file) {
                this.$Message.warning('上传文件格式错误,请选择jpg或者png格式');
            },
            handleMaxSize (file) {
                this.$Message.warning('文件过大, 请上传2M内的图片');
            },
            handleUploadSuccess(response, file) {
                this.ocrBtnText = '或 上传营业执照自动识别';
                this.bizLicenseForm.license = response.reg_num;
                this.bizLicenseForm.name = response.name;
                this.bizLicenseForm.legalPerson = response.person;
            },
            getFaceResult () {
                var self = this;
                let bizNo = Cookies.get('face_token');
                util.ajax.post('/loan/face/result', {bizNo: bizNo})
                    .then(function (response) {
                        if (response.status === 200) {
                            if (response.data && response.data.idcard) {
                                self.bizLicenseForm.contactIdcard = response.data.idcard.idcard_number;
                                self.bizLicenseForm.contact = response.data.idcard.idcard_name;
                            }
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            handleVerifyCode() {
                var self = this;
                util.ajax.post('/phone/verifycode', {
                        mobile: this.bizLicenseForm.contactMobile,
                        bizNo: this.bizLicenseForm.license
                    })
                    .then(function (response) {
                        if (response.status === 200) {
                            self.submitEnabled = true;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            handleSubmit () {
                util.ajax.post('/loan/wx/apply', this.bizLicenseForm)
                        .then(function (response) {
                            if (response.status === 200) {
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                
//                bizLicenseForm
            },
        }

    };
</script>


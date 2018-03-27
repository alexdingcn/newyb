<style lang="less">
    @import '../../styles/common.less';
</style>


<template>
    <Form ref="formInline" :model="bizLicenseForm" :label-width="30" :rule="ruleValidate"
          style="padding: 10px;">
        <Row class="logo-con">
            <img src="../../images/logo.png" key="min-logo"/>
        </Row>
        <h2>融资申请表</h2>

        <Row class="margin-top-8" type="flex" justify="center" align="middle">
            <Col span="6">身份证</Col>
            <Col span="18">
                <Input type="text" v-model="bizLicenseForm.idcard" placeholder="身份证" size="large" readonly/>
            </Col>
        </Row>
        <Row class="margin-top-8" type="flex" justify="center" align="middle">
            <Col span="6">营业执照号</Col>
            <Col span="18">
                <Input type="text" v-model="bizLicenseForm.licenseNumber" placeholder="营业执照号" size="large"/>
            </Col>
        </Row>
        <Row class="margin-top-8">
            <Upload action="//jsonplaceholder.typicode.com/posts/">
                <Button type="ghost" icon="ios-cloud-upload-outline">或 上传营业执照</Button>
            </Upload>
        </Row>
        <Row class="margin-top-20">
            <Col span="18">
                <Input v-model="bizLicenseForm.amount" placeholder="申请融资金额(万)" size="large"/>
            </Col>
        </Row>
        <Row class="margin-top-8">
            <Col span="18">
            <Select v-model="bizLicenseForm.period" size="large">
                <Option value='3'>3个月</Option>
                <Option value='6'>6个月</Option>
                <Option value='12'>12个月</Option>
            </Select>
            </Col>
        </Row>
        <Row class="margin-top-8">
            <Col span="18">
            <Input v-model="bizLicenseForm.contact" placeholder="联系人姓名" size="large"/>
            </Col>
        </Row>
        <Row class="margin-top-8" :gutter="10">
            <Col span="16">
            <Input v-model="bizLicenseForm.contactMobile" placeholder="联系人手机" size="large"/>
            </Col>
            <Col span="8" >
            <Button type="info" @click="handleSubmit">发送验证码</Button>
            </Col>
        </Row>
        <Row class="margin-top-8" :gutter="10">
            <Col span="16">
            <Input v-model="bizLicenseForm.verifyCode" placeholder="验证码" size="large"/>
            </Col>
        </Row>
        <hr/>
        <Row class="margin-top-8" type="flex" align="middle">
            <Button type="primary" @click="handleSubmit">提交</Button>
        </Row>
    </Form>
</template>

<script>
    import Cookies from 'js-cookie';
    import util from '@/libs/util.js';

    export default {
        name: 'loan-apply-biz',
        data () {
            return {
                bizLicenseForm: {
                    idcard: '',
                    contact: '',
                    period: '6'
                },
                ruleValidate: [],
            };
        },
        mounted () {
            this.getFaceResult();
        },
        methods: {
            getFaceResult () {
                var self = this;
                let bizNo = Cookies.get('face_token');
                util.ajax.post('/loan/face/result', {bizNo: bizNo})
                        .then(function (response) {
                            if (response.status === 200) {
                                if (response.data && response.data.idcard) {
                                    self.bizLicenseForm.idcard = response.data.idcard.idcard_number;
                                    self.bizLicenseForm.contact = response.data.idcard.idcard_name;
                                }
                            }
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
            },
            handleSubmit () {
//                bizLicenseForm
            },
        }

    };
</script>


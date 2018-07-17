<style lang="less">
    @import '../../styles/common.less';
    body {
        overflow: auto;
    }
    .bizApply .ivu-form-item {
        margin-bottom: 10px;
    }
    .blue-header { background-color: #3670C5; color: white; height: 50px; line-height: 50px; }
    .back-btn { color: white; }
</style>


<template>
    <div class="bizApply layout">
        <Layout>
            <Header class="blue-header padding-side-10">
                <Row>
                    <i-col span="3">
                        <Button type="text" class="back-btn" @click="historyGoBack">
                            <Icon type="chevron-left" size="24" ></Icon>
                        </Button>
                    </i-col>
                    <i-col span="18" class="center">
                        <h2>申请金融服务</h2>
                    </i-col>
                </Row>
            </Header>
            <Content><!--class="padding-10"-->
                <Form ref="formInline" :model="bizLicenseForm" :label-width="80" :rule="ruleValidate"  v-bind:style="{padding: paddingPX}" v-show="formShow">
                    <FormItem label="公司名称">
                        <Input type="text" v-model="bizLicenseForm.name" placeholder="公司名称" size="large"/>
                    </FormItem>

                    <FormItem label="法人" v-show="bizLicenseForm.legalPerson != ''">
                        <Input type="text" v-model="bizLicenseForm.legalPerson" placeholder="法人" size="large"/>
                    </FormItem>

                    <FormItem label="融资金额">
                        <Input type="tel" v-model="bizLicenseForm.applyAmount" placeholder="申请金额(万)" size="large"/>
                    </FormItem>
                    <FormItem label="期限">
                        <Select v-model="bizLicenseForm.applyMonths" size="large">
                            <Option value='3'>3个月</Option>
                            <Option value='6'>6个月</Option>
                            <Option value='12'>12个月</Option>
                        </Select>
                    </FormItem>

                    <FormItem label="联系人姓名">
                        <Input v-model="bizLicenseForm.contact" placeholder="联系人姓名" size="large"/>
                    </FormItem>
                    <FormItem label="联系人手机">
                        <Input type="tel" v-model="bizLicenseForm.contactMobile" placeholder="联系人手机" size="large" maxlength=11 />
                        <Button type="info" @click="handleVerifyCode" class="margin-top-8" :disabled="bizLicenseForm.contactMobile === '' || countDown > 0">{{verifyText}}</Button>
                    </FormItem>
                    <FormItem label="验证码">
                        <Input type="tel" v-model="bizLicenseForm.verifyCode"  placeholder="验证码" size="large" maxlength=6 />
                    </FormItem>
                    <Button type="success" @click="handleSubmit" long :disabled="!submitEnabled" size="large">提交</Button>
                </Form>
                <div v-show="!formShow" >
                    <Alert type="success" show-icon class="margin-20">
                        你的申请我们已经收到
                        <template slot="desc">客户经理会尽快与您联系, 感谢您的关注!</template>
                    </Alert>
                </div>

            </Content>
        </Layout>


    </div>

</template>

<script>
    import Cookies from 'js-cookie';
    import util from '@/libs/util.js';

    export default {
        name: 'loan-apply-biz',
        data () {
            return {
                clientWidth:'',
                paddingPX:'',
                //paddingPXR:'',
                formShow: true,
                submitEnabled: false,
                uploadAction: `${util.baseUrl}/loan/bizlic/ocr`,
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
                countDown: 0,
                fileControl: document.getElementById('fileControl')
            };
        },
        computed: {
            verifyText: function () {
                return this.countDown > 0 ? this.countDown + 's后重新获取' : '获取验证码';
            }
        },
        mounted () {
            this.clientWidth = window.screen.width;
            console.log("---"+this.clientWidth);
            this.paddingPX = this.clientWidth< 500 ? '10px':'10px 400px';
            //this.$refs.formInline.style.padding = '300px';
            //this.paddingPXL = this.clientWidth< 500 ? '30px':'200px';
            this.initFileControl();
            this.getFaceResult();
        },
        methods: {
            historyGoBack() {
                history.go(-1);
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
                    mobile: this.bizLicenseForm.contactMobile,
                    bizNo: this.bizLicenseForm.license
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
            handleSubmit () {
                var self = this;
                util.ajax.post('/loan/wx/apply', this.bizLicenseForm)
                    .then(function (response) {
                        if (response.status === 200) {
                            self.formShow = false;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },

            initFileControl () {
                // 100 kb
                var self = this;
                var maxSize = 100 * 1024;
                this.fileControl = document.getElementById('fileControl');
                this.fileControl.addEventListener('change', function (e) {
                    var files = Array.prototype.slice.call(fileControl.files);
                    if (files.length === 0) {
                        console.log('请选择图片');
                        return;
                    }
                    if (files.length > 1) {
                        console.log('只能选择一张图片');
                        return;
                    }
                    var file = files[0];
                    var reader = new FileReader();
                    // ~~用于将字符串转化为整数，MB前的逻辑是为了保留一位小数取整，参考：https://github.com/whxaxes/node-test/issues/11
                    var fileSize = file.size / 1024 > 1024 ? (~~(10 * file.size / 1024 / 1024) / 10) + 'MB' : ~~(file.size / 1024) + 'KB';
                    reader.onload = function () {
                        var result = this.result;
                        var img = new Image();
                        img.src = result;
                        // if image file size is not greater than 100 kb, upload it directly
                        if (result.length <= maxSize) {
                            img = null;
                            this.upload(result, file.type);
                            return;
                        }
                        // compress image firstly after uploaded completely, and then upload it
                        if (img.complete) {
                            callback();
                        } else {
                            img.onload = callback;
                        }
                        function callback () {
                            var data = self.compress(img);
                            self.upload(data, file.type);
                            img = null;
                        }
                    };
                    reader.readAsDataURL(file);
                }, false);
            },

            // compress large image using canvas
            compress (objImg) {
                // 用于压缩图片的canvas
                var canvas = document.createElement('canvas');
                var ctx = canvas.getContext('2d');
                // 瓦片canvas
                var tCanvas = document.createElement('canvas');
                var tctx = tCanvas.getContext('2d');

                var initSize = objImg.src.length;
                var width = objImg.width;
                var height = objImg.height;
                // 如果图片大于400万像素，计算压缩比并将大小压至400万以下
                var ratio;
                if ((ratio = width * height / 8000000) > 1) {
                    ratio = Math.sqrt(ratio);
                    width /= ratio;
                    height /= ratio;
                } else {
                    ratio = 1;
                }
                canvas.width = width;
                canvas.height = height;
                // 铺底色
                ctx.fillStyle = '#fff';
                ctx.fillRect(0, 0, canvas.width, canvas.height);
                // 如果图片像素大于100万则使用瓦片绘制
                var count;
                if ((count = width * height / 4000000) > 1) {
                    // 计算要分成多少块瓦片
                    count = ~~(Math.sqrt(count) + 1);
                    var nw = ~~(width / count);
                    var nh = ~~(height / count);
                    tCanvas.width = nw;
                    tCanvas.height = nh;
                    for (var i = 0; i < count; i++) {
                        for (var j = 0; j < count; j++) {
                            tctx.drawImage(objImg, i * nw * ratio, j * nh * ratio, nw * ratio, nh * ratio, 0, 0, nw, nh);
                            ctx.drawImage(tCanvas, i * nw, j * nh, nw, nh);
                        }
                    }
                } else {
                    ctx.drawImage(objImg, 0, 0, width, height);
                }
                // 进行最小压缩
                var nData = canvas.toDataURL('image/jpeg', 0.5);
                console.log('压缩前：' + initSize);
                console.log('压缩后：' + nData.length);
                console.log('压缩率：' + ~~(100 * (initSize - nData.length) / initSize) + '%');
                tCanvas.width = tCanvas.height = canvas.width = canvas.height = 0;
                return nData;
            },
            // 图片上传，将base64的图片转成二进制对象，塞进formdata上传
            upload (basestr, type) {
                var self = this;
                var fileWrapper = document.querySelector('.file-wrapper');
                // 去除mime type，atob() 函数用来解码一个已经被base-64编码过的数据
                var text = window.atob(basestr.split(',')[1]);
                var buffer = new Uint8Array(text.length);
                var percent = 0, loop = null;
                for (var i = 0, len = text.length; i < len; i++) {
                    buffer[i] = text.charCodeAt(i);
                }
                var blob = this.getBlob([buffer], type);
                var xhr = new XMLHttpRequest();
                var formData = this.getFormData();
                formData.append('imagefile', blob);
                xhr.open('post', this.uploadAction);
                xhr.onreadystatechange = function () {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        var jsonData = JSON.parse(xhr.responseText);
                        //                        var imageData = jsonData[0] || {};

                        self.bizLicenseForm.license = jsonData.reg_num;
                        self.bizLicenseForm.name = jsonData.name;
                        self.bizLicenseForm.legalPerson = jsonData.person;

                        clearInterval(loop);
                    }
                };
                // 数据发送进度，前50%展示该进度
                xhr.upload.addEventListener('progress', function (e) {
                    if (loop) { return; }
                    percent = ~~(100 * e.loaded / e.total) / 2;
                    console.log(percent);
                    if (percent === 50) {
                        mockProgress();
                    }
                }, false);
                function mockProgress () {
                    if (loop) { return; }
                    loop = setInterval(function () {
                        percent++;
                        console.log(percent);
                        if (percent >= 99) {
                            clearInterval(loop);
                        }
                    }, 100);
                }
                xhr.send(formData);
            },
            // 获取blob对象的兼容性写法
            getBlob (buffer, format) {
                try {
                    return new Blob(buffer, { type: format });
                } catch (e) {
                    var bb = new (window.BlobBuilder || window.WebKitBlobBuilder || window.MSBlobBuilder)();
                    buffer.forEach(function (buf) {
                        bb.append(buf);
                    });
                    return bb.getBlob(format);
                }
            },
            // 获取formdata
            getFormData () {
                var isNeedShim = ~navigator.userAgent.indexOf('Android') &&
                        ~navigator.vendor.indexOf('Google') &&
                        ~navigator.userAgent.indexOf('Chrome') &&
                        navigator.userAgent.match(/AppleWebKit\/(\d+)/).pop() <= 534;
                return isNeedShim ? new this.FormDataShim() : new FormData();
            },
            // formData 补丁，给不支持formdata上传blob的android机打补丁
            // 构造函数
            FormDataShim () {
                console.warn('using dormdata shim');
                var o = this,
                    parts = [],
                    boundary = new Array(21).join('-') + (+new Data() * (1e6 * Math.random())).toString(36),
                    oldSend = XMLHttpRequest.prototype.send;
                this.append = function (name, value, fileName) {
                    parts.push('--' + boundary + '\r\nContent-Disposition: form-data; name="' + name + '"');
                    if (value instanceof Blob) {
                        parts.push('; filename="' + (fileName || 'blob') + '"\r\nContent-Type: ' + value.type + '\r\n\r\n');
                        parts.push(value);
                    } else {
                        parts.push('\r\n\r\n' + value);
                    }
                    parts.push('\r\n');
                };
                // 重写XHR send()方法
                XMLHttpRequest.prototype.send = function (val) {
                    var fr,
                        data,
                        oXHR = this;
                    if (val === o) {
                        // Append the final boundary string
                        parts.push('--' + boundary + '--\r\n');
                        // Create the blob
                        data = this.getBlob(parts);
                        // Set up and read the blob into an array to be sent
                        fr = new FileReader();
                        fr.onload = function () {
                            oldSend.call(oXHR, fr.result);
                        };
                        fr.onerror = function (err) {
                            throw err;
                        };
                        fr.readAsArrayBuffer(data);
                        // Set the multipart content type and boudary
                        this.setRequestHeader('Content-Type', 'multipart/form-data; boundary=' + boundary);
                        XMLHttpRequest.prototype.send = oldSend;
                    } else {
                        oldSend.call(this, val);
                    }
                };
            }
        }

    };
</script>

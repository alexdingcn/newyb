<style lang="less">
@import "./register.less";
</style>

<template>
    <div class="register" :style="{ backgroundImage: 'url(' + bgImage + ')' }">
        <div class="register-con">

            <Card>
                <p slot="title">
                    <Icon type="ios-personadd"></Icon>
                    注册医站通
                </p>
                <div slot="extra">
                    <Icon type="chevron-left"></Icon>
                    已经有账户?
                    <a href="javascript:;" @click.prevent="toLogin">
                        登录
                    </a>
                </div>


                <Form ref="formCustom" :model="formCustom" :rules="ruleCustom" :label-width="110">
                    <small class="help">注意：<strong>需与当地政府颁发的商业许可证或企业注册证上的企业名称完全一致，信息审核成功后，企业名称不可修改</strong></small>
                    <Form-item style="margin-top: 10px;" label="企业名称" prop="company">
                        <Input v-model="formCustom.company" placeholder="请输入公司名称"/>
                    </Form-item>
                    <Form-item label="手机" prop="mobile">
                        <i-input v-model="formCustom.mobile" placeholder="请输入手机号">
                          <Button slot="append" @click="handleVerifyCode"
                                  :disabled="formCustom.mobile === '' || countDown > 0">{{verifyText}}
                          </Button>
                        </i-input>
                    </Form-item>
                    <FormItem label="验证码" prop="verifyCode">
                        <Input v-model="formCustom.verifyCode" placeholder="验证码" :maxlength="6"/>
                    </FormItem>

                    <Form-item label="密码" prop="password">
                        <Input v-model="formCustom.password" type="password"/>
                    </Form-item>

                    <Form-item v-show="loginResponse">
                        <Alert type="error"  show-icon>{{ loginResponse }}</Alert>
                    </Form-item>

                    <Form-item>
                        <Checkbox v-model="formCustom.agreeChecked" class="agree">我已阅读并同意<a target="_blank" href="https://www.yibanmed.com">《服务条款》</a></Checkbox>
                    </Form-item>

                    <FormItem>
                        <Button type="primary" :disabled="!submitEnabled" @click="handleSubmit('formCustom')" long
                                :loading="loading">提交
                        </Button>
                    </FormItem>
                </Form>
            </Card>
        </div>
    </div>
</template>
<script>
import Cookies from "js-cookie";
import util from "@/libs/util.js";

export default {
  name: "type",
  data() {
    return {
      bgImage:
        "http://www.bing.com/az/hprichbg/rb/FearlessGirl_ZH-CN8770808173_1366x768.jpg",
      token: true,
      loading: false,
      formCustom: {
        company: "",
        password: "",
        mobile: "",
        verifyCode: "",
        agreeChecked: false
      },
      loginResponse: "",
      userNameMessage: "",
      countDown: 0,
      submitEnabled: false,
      ruleCustom: {
        company: [
          { required: true, message: "企业名称不能为空", trigger: "blur" }
        ],
        password: [
          { required: true, message: "密码不能为空", trigger: "blur" }
        ],
        mobile: [
          { required: true, message: "手机号不能为空", trigger: "blur" },
          {
            message: "手机号格式不正确",
            len: 11,
            trigger: "blur",
            pattern: /^[1][3,4,5,7,8][0-9]{9}$/
          }
        ],
        verifyCode: [
          { required: true, message: "短信验证码必输", trigger: "blur" }
        ]
      }
    };
  },
  beforeMount() {
    this.loadBackground();
  },
  computed: {
    verifyText: function() {
      return this.countDown > 0 ? this.countDown + "s后重新获取" : "获取验证码";
    }
  },
  methods: {
    loadBackground: function() {
      var self = this;
      util.ajax
        .get("https://www.bing.com/HPImageArchive.aspx", {
          params: {
            format: "js",
            idx: 1,
            n: 10
          }
        })
        .then(function(response) {
          if (response.status === 200) {
            var imageCount = response.data.images.length;
            var idx = Math.floor(Math.random() * imageCount);
            self.bgImage =
              "https://www.bing.com/" + response.data.images[idx].url;
          }
        })
        .catch(function(error) {
          console.log(error);
          util.errorProcessor(self, error);
        });
    },

    countDownTimer: function() {
      if (this.countDown > 0) {
        this.countDown--;
        setTimeout(this.countDownTimer, 1000);
      }
    },

    handleVerifyCode() {
      var self = this;
      this.countDown = 60;
      this.countDownTimer();
      util.ajax
        .post("/phone/verifycode", {
          mobile: self.formCustom.mobile
        })
        .then(function(response) {
          if (response.status === 200) {
            self.submitEnabled = true;
            self.countDown = 60;
          }
        })
        .catch(function(error) {
          util.errorProcessor(self, error);
        });
    },

    validateUserName() {
      let value = this.formCustom.userName;
      this.userNameMessage = "";
      if (!value) {
        this.userNameMessage = "用户名必输";
        return;
      }
      util.ajax
        .get("/user/valid/nickname", { params: { nickname: value } })
        .then(response => {
          //TO NOTHING
        })
        .catch(error => {
          let result = error.response ? error.response.data : "";
          if (result && result.message) {
            this.userNameMessage = result.message;
          } else {
            this.userNameMessage = "用户名检验失败";
          }
        });
    },

    toLogin() {
      this.$router.push("/login");
    },

    handleSubmit(name) {
      self.loginResponse = "";
      this.loading = true;
      this.$refs[name].validate(valid => {
        if (valid) {
          var self = this;
          util.ajax
            .post("/register", this.formCustom)
            .then(function(response) {
              self.loading = false;
              Cookies.set("user", self.formCustom.mobile);
              self.$router.replace({
                name: "login"
              });
            })
            .catch(function(error) {
              self.loading = false;
              let result = error.response ? error.response.data : "";
              if (result && result.message) {
                self.loginResponse = result.message;
              } else {
                self.loginResponse = "注册失败";
              }
            });
        } else {
          this.loading = false;
          this.$Message.error("表单验证失败!");
        }
      });
    }
  }
};
</script>


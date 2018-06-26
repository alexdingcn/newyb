<style lang="less">
@import "./login.less";
</style>

<template>
    <div class="login" @keydown.enter="handleSubmit" :style="{ backgroundImage: 'url(' + bgImage + ')' }">
        <div class="login-con">
            <Card :bordered="false">
                <p slot="title">
                    <Icon type="log-in"></Icon>
                    欢迎登录
                </p>
                <div class="form-con">
                    <Form ref="loginForm" :model="form" :rules="rules">
                        <FormItem prop="username">
                            <i-input v-model="form.username" placeholder="请输入用户名/手机号">
                                <span slot="prepend">
                                    <Icon :size="16" type="person"></Icon>
                                </span>
                            </i-input>
                        </FormItem>
                        <FormItem prop="password">
                            <i-input type="password" v-model="form.password" placeholder="请输入密码">
                                <span slot="prepend">
                                    <Icon :size="14" type="locked"></Icon>
                                </span>
                            </i-input>
                        </FormItem>

                        <FormItem>
                            <Button @click="handleSubmit" type="primary" long :loading="loading">登录</Button>
                        </FormItem>
                        <Alert type="error" v-show="loginResponse" show-icon>{{ loginResponse }}</Alert>

                        <Row>
                            <i-col span="12">
                                <Checkbox-group v-model="form.remember">
                                    <Checkbox label="记住我"></Checkbox>
                                </Checkbox-group>
                            </i-col>
                            <i-col span="12">
                                <a style="float:right" @click="toRegister">新用户注册</a>
                            </i-col>
                        </Row>
                    </Form>

                </div>
            </Card>
        </div>
    </div>
</template>

<script>
import Cookies from "js-cookie";
import util from "@/libs/util.js";
import Qs from "qs";

export default {
  data() {
    return {
      bgImage: "",
      loginResponse: "",
      loading: false,
      form: {
        username: Cookies.get("user"),
        password: ""
      },
      rules: {
        userName: [
          { required: true, message: "账号不能为空", trigger: "blur" }
        ],
        password: [{ required: true, message: "密码不能为空", trigger: "blur" }]
      }
    };
  },
  beforeMount() {
    this.loadBackground();
  },
  methods: {
    loadBackground: function() {
      var self = this;
      util.ajax
        .get("/login/dynamic-bg", {
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
          self.bgImage =
            "https://www.bing.com/az/hprichbg/rb/OakTreeMaize_ZH-CN10523296117_1920x1080.jpg";
        });
    },
    handleSubmit() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          var self = this;
          var data = Qs.stringify(this.form);
          this.loading = true;
          util.ajax
            .post("/login", data, {
              headers: { "Content-Type": "application/x-www-form-urlencoded" }
            })
            .then(function(response) {
              console.log(response);

              self.loading = false;
              let result = response.data;
              let userDetail = result ? result.userDetail : "";
              let jwt = result ? result.jwt : "";
              let authPages = result ? result.authPages : [];
              if (userDetail && jwt && authPages) {
                self.$store.commit("setToken", jwt);
                self.$store.commit("setUserDetail", userDetail);
                self.$store.commit("setAccessRoutes", authPages);
                Cookies.set("user", userDetail.nickname);
                if (userDetail.avatarUrl) {
                  self.$store.commit(
                    "setAvator",
                    userDetail.avatarUrl + "?x-oss-process=style/resize200"
                  );
                }
                self.$router.push({
                  name: "home_index"
                });
              } else {
                self.loginResponse = "系统异常, 请联系技术人员";
              }
            })
            .catch(function(error) {
              self.loading = false;
              let result = error.response ? error.response.data : "";
              if (result && result.message) {
                console.log(result);
                self.loginResponse = "系统异常,请稍候再试";
              } else {
                self.loginResponse = "登录异常";
              }
            });
        }
      });
    },
    toRegister() {
      this.$router.push("/register");
    }
  }
};
</script>

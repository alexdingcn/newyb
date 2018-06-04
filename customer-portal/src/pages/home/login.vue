<template>
<div class="login-container">
    <div class="center">
    <img src="https://www.yibanmed.com/images/logo2.0.png"/>
    </div>
    <div class="login-form">
        <mt-field placeholder="手机/用户名" type="email" v-model="form.username"></mt-field>
        <mt-field placeholder="请输入密码" type="password" v-model="form.password" ></mt-field>
    </div>
    <div class="login-btns">
        <mt-button type="primary" size="large" @click.native="submit">登录并绑定微信账号</mt-button>
        <mt-button plain size="large" style="margin-top:15px" :disabled="loggingIn">忘记密码</mt-button>
    </div>
</div>
</template>

<script>
import util from "@/libs/util.js";
import { Toast } from "mint-ui";
import Qs from "qs";

export default {
  name: "login",
  data() {
    return {
      form: {
        username: "",
        password: ""
      },
      loggingIn: false,
      error: ""
    };
  },
  methods: {
    submit() {
      this.loggingIn = true;
      var self = this;
      var data = Qs.stringify(this.form);
      util.ajax
        .post("/login", data, {
          headers: { "Content-Type": "application/x-www-form-urlencoded" }
        })
        .then(function(response) {
          if (response.status === 200) {
            self.$router.push({
              path: "home"
            });
          }
        })
        .catch(function(error) {
          if (error.response) {
            Toast({
              message: error.response.data.message,
              position: "top"
            });
          }
        });
    }
  }
};
</script>

<style type="less" scoped>
.center {
  text-align: center;
}
.login-container {
  margin-top: 50px;
  padding: 40px;
}
.login-form {
  margin: 20px 0;
}
.login-btns {
  padding: 0 15px 15px;
}
</style>
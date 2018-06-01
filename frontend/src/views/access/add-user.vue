
<template>
  <div>
        <Row :gutter="16">
            <i-col span="12">
                <user-list ref="userList" @choose-user="chooseUser" :showAddBtn="true"></user-list>
            </i-col>
            <i-col span="11">
                <Card>
                    <p slot="title">
                        <Icon type="person"></Icon>
                        {{showTitle || '用户信息'}}
                    </p>
                    <div slot="extra">
                        <ButtonGroup size="small" >
                            <Button type="primary" icon="plus" @click="createUserBtnClick">新建用户</Button>
                            <Button type="success" icon="checkmark" @click="save" :loading="saveLoading">保存</Button>
                        </ButtonGroup>
                    </div>
                    <div>
                        <Form ref="userForm" :model="userForm" :rules="userFormRule" :label-width="100">
                            <FormItem label="用户名：" prop="nickname" :error="nicknameError" >
                                <Input v-model="userForm.nickname" :maxlength="30" :disabled="editView" @on-blur="validateNickNameExist" />
                            </FormItem>
                            <FormItem label="用户手机：" prop="mobile" :error="mobileError" >
                                <Input v-model="userForm.mobile" :maxlength="11" :disabled="editView" @on-blur="validMobileExist"/>
                            </FormItem>
                            <FormItem label="真实姓名：" prop="realname">
                                <Input v-model="userForm.realname" />
                            </FormItem>
                            <FormItem label="初始登录密码：" :error="passwordError">
                                <Input type="password" v-model="userForm.password" :disabled="editView"/>
                            </FormItem>
                            <FormItem label="电子邮箱：" >
                                <Input v-model="userForm.email" />
                            </FormItem>
                            <FormItem label="固定电话：" >
                                <Input v-model="userForm.phone" />
                            </FormItem>
                            <FormItem label="身份证号：" >
                                <Input v-model="userForm.idcard" />
                            </FormItem>
                            <FormItem label="联系地址：" >
                                <Input v-model="userForm.address" />
                            </FormItem>
                            
                        </Form>
                    </div>
                </Card>
            </i-col>
        </Row>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import userList from "./user-list.vue";

export default {
  name: "add-user",
  components: {
    userList
  },
  data() {
    const valideMobile = (rule, value, callback) => {
      if (!value) {
        callback(new Error("新手机号必输"));
      } else {
        var re = /^1[0-9]{10}$/;
        if (!re.test(value)) {
          callback(new Error("请输入正确格式的手机号"));
        } else {
          callback();
        }
      }
    };
    return {
      currUser: {},
      editView: false,
      showTitle: "",
      userForm: {},
      saveLoading: false,
      passwordError: "",
      nicknameError: "",
      mobileError: "",
      userFormRule: {
        nickname: [
          { required: true, message: "请输入用户名", trigger: "blur" }
        ],
        mobile: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          { validator: valideMobile, trigger: "blur" }
        ],
        realname: [
          { required: true, message: "请输入真实姓名", trigger: "blur" }
        ]
      }
    };
  },
  watch: {
    currUser(data) {
      if (data && data.id > 0) {
        let nickname = data.nickname ? data.nickname : "";
        let realname = data.realname ? data.realname : "";
        this.showTitle = nickname + (realname ? "[" + realname + "]" : "");
        this.editView = true;
        this.userForm = {
          id: data.id,
          nickname: data.nickname,
          realname: data.realname,
          mobile: data.mobile,
          password: "",
          email: data.email,
          phone: data.phone,
          address: data.address
        };
      }
    }
  },
  methods: {
    chooseUser(user) {
      this.currUser = user;
    },

    createUserBtnClick() {
      this.editView = false;
      this.showTitle = "新建用户";
      this.userForm = {};
      this.currUser = {};
      this.$refs.userList.clearCurrentUser();
    },

    validateNickNameExist() {
      let nickname = this.userForm.nickname;
      if (!nickname) {
        return;
      }
      util.ajax
        .get("/user/valid/nickname", { params: { nickname: nickname } })
        .then(response => {
          this.nicknameError = "";
        })
        .catch(error => {
          this.nicknameError = error.response.data.message;
        });
    },

    validMobileExist() {
      let mobile = this.userForm.mobile;
      if (!mobile) {
        return;
      }
      let re = /^1[0-9]{10}$/;
      if (!re.test(mobile)) {
        this.mobileError = "手机号码格式错误";
        return;
      }
      util.ajax
        .get("/user/valid/mobile", { params: { mobile: mobile } })
        .then(response => {
          this.mobileError = "";
        })
        .catch(error => {
          this.mobileError = error.response.data.message;
        });
    },

    save() {
      let self = this;
      self.passwordError = "";
      this.$refs.userForm.validate(valid => {
        if (!valid) {
          self.$Message.warning("必输项信息验证未通过, 请按要求输入信息");
          return;
        }
        if (!self.userForm.id && !self.userForm.password) {
          self.passwordError = "新建用户需要输入初始密码.";
          return;
        }
        this.$Modal.confirm({
          title: "保存确认",
          content: "是否已经确认数据正确？",
          onOk: () => {
            self.doSave();
          },
          onCancel: () => {}
        });
      });
    },

    doSave() {
      this.saveLoading = true;
      this.userForm.credential = this.userForm.password;
      util.ajax
        .put("/user/save", this.userForm)
        .then(response => {
          this.$Message.success(
            "保存成功, 如果是新建的用户，需要在权限管理页面进行激活并分配权限."
          );
          this.saveLoading = false;
          this.createUserBtnClick();
          this.$refs.userList.refreshUserList();
        })
        .catch(error => {
          this.saveLoading = false;
          util.errorProcessor(this, error);
        });
    }
  }
};
</script>

<style >
.ivu-form-item {
  margin-bottom: 20px;
}
</style>

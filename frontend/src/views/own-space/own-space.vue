<style lang="less">
@import "./own-space.less";
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="person"></Icon>
                个人信息
            </p>
            <div slot="extra">
            </div>
            <div>
                <Form 
                    ref="userForm"
                    :model="userDetail" 
                    :label-width="100" 
                    label-position="right"
                >

                    <FormItem label="头像：" >
                        <div class="own-space-avatar" v-if="avatarUrl != ''">
                            <img :src="avatarUrl" >
                            <div class="own-space-avatar-cover">
                                <Icon type="ios-eye-outline" @click.native="avatarVisible = true"></Icon>
                            </div>
                        </div>
                        <Upload ref="upload"
                                :show-upload-list="false"
                                :default-file-list="defaultList"
                                :on-success="handleSuccess"
                                :format="['jpg','jpeg','png']"
                                :max-size="2048"
                                :on-format-error="handleFormatError"
                                :on-exceeded-size="handleMaxSize"

                                type="drag"
                                action=""
                                :before-upload="handleUpload"
                                style="display: inline-block;width:58px;">
                            <div style="width: 58px;height:58px;line-height: 58px;">
                                <Icon type="camera" size="20"></Icon>
                            </div>
                        </Upload>
                        <Modal title="查看头像" v-model="avatarVisible">
                            <img :src="avatarUrl" style="width: 100%">
                        </Modal>
                    </FormItem>

                    <FormItem label="用户名：" prop="nickname">
                        <strong> {{ userDetail.nickname }} </strong>
                        <Button type="text" icon="edit" @click="editNicknameBtnClick"></Button>
                        <Button type="text" size="small" @click="showEditPassword" style="color:#2d8cf0; margin-left:10px;">修改密码</Button>
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
                            <Input v-model="userDetail.realname" />
                        </div>
                    </FormItem>
                    <FormItem label="电子邮箱：" >
                        <div style="display:inline-block;width:250px;">
                            <Input v-model="userDetail.email" />
                        </div>
                    </FormItem>
                    <FormItem label="固定电话：" >
                        <div style="display:inline-block;width:250px;">
                            <Input v-model="userDetail.phone" />
                        </div>
                    </FormItem>
                    <FormItem label="身份证号：" >
                        <div style="display:inline-block;width:250px;">
                            <Input v-model="userDetail.idcard" />
                        </div>
                    </FormItem>
                    <FormItem label="联系地址：" >
                        <div style="display:inline-block;width:400px;">
                            <Input v-model="userDetail.address" />
                        </div>
                    </FormItem>
                </Form>
                <hr size="1" style="margin-top:0.5em; margin-bottom:1em; width: 40%;" />
                <ButtonGroup>
                  <Button type="text" icon="reply" @click="cancelEditUserInfor">取消</Button>
                  <Button type="success" icon="checkmark" :loading="save_loading" @click="saveEdit">保存</Button>
                </ButtonGroup>
            </div>
        </Card>

        <Modal v-model="editNickNameModal" :closable='false' :mask-closable=false :width="500">
          <h3 slot="header" style="color:#2D8CF0">修改用户名</h3>
          <Form ref="editNicknameForm" :model="editNicknameForm" :label-width="100" label-position="right">
            <FormItem label="新用户名" :error="nicknameError">
                <Input v-model="editNicknameForm.nickname" placeholder="请输入新的用户名称" @on-blur="validateNickname" />
            </FormItem>
          </Form>
          <div slot="footer">
                <Button type="text" icon="reply" @click="cancelEditNickname">取消</Button>
                <Button type="success" icon="checkmark" :loading="saveNicknameLoading" @click="saveEditNickname">保存</Button>
            </div>
        </Modal>

        <Modal v-model="editPasswordModal" :closable='false' :mask-closable=false :width="500">
            <h3 slot="header" style="color:#2D8CF0">修改密码</h3>
            <Form ref="editPasswordForm" :model="editPasswordForm" :label-width="100" label-position="right" :rules="passwordValidate">
                <FormItem label="原密码" prop="oldPass" :error="oldPassError">
                    <Input type="password" v-model="editPasswordForm.oldPass" placeholder="请输入现在使用的密码" />
                </FormItem>
                <FormItem label="新密码" prop="newPass">
                    <Input type="password" v-model="editPasswordForm.newPass" placeholder="请输入新密码，至少6位字符" />
                </FormItem>
                <FormItem label="确认新密码" prop="rePass">
                    <Input type="password" v-model="editPasswordForm.rePass" placeholder="请再次输入新密码" />
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="text" icon="reply" @click="cancelEditPass">取消</Button>
                <Button type="success" icon="checkmark" :loading="savePassLoading" @click="saveEditPass">保存</Button>
            </div>
        </Modal>

        <Modal v-model="editMobileModal" :closable='false' :mask-closable=false :width="400">
            <h3 slot="header" style="color:#2D8CF0">修改手机号</h3>
            <Form ref="editMobileForm" :model="editMobileForm" :label-width="100" label-position="right" :rules="mobileValidate">
                <FormItem label="新手机号" prop="mobile">
                    <i-input v-model="editMobileForm.mobile" placeholder="请输入新手机号码" @on-blur="validMobileExist" >
                        <Button slot="append" @click="handleVerifyCode" :disabled="sendVerifyCodeDisabled">{{verifyText}}</Button>
                    </i-input>
                </FormItem>
                <FormItem label="验证码" prop="verifyCode">
                    <Input v-model="editMobileForm.verifyCode" placeholder="请输入短信验证码" />
                </FormItem>
            </Form>
            <Alert type="error" v-show="validMessage" show-icon>{{ validMessage }}</Alert>
            <div slot="footer">
                <Button type="text" icon="reply" @click="cancelEditMobile">取消</Button>
                <Button type="success" icon="checkmark" :loading="saveMobileLoading" @click="saveEditMobile" >保存</Button>
            </div>
        </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";

export default {
  name: "ownspace_index",
  data() {
    const valideRePassword = (rule, value, callback) => {
      if (value !== this.editPasswordForm.newPass) {
        callback(new Error("两次输入密码不一致"));
      } else {
        callback();
      }
    };
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
      defaultList: [],
      avatarVisible: false,
      imgName: "",
      avatarUrl: "",
      userDetail: {},
      save_loading: false,
      editPasswordModal: false, // 修改密码模态框显示
      savePassLoading: false,
      oldPassError: "",
      editPasswordForm: {
        oldPass: "",
        newPass: "",
        rePass: ""
      },
      passwordValidate: {
        oldPass: [{ required: true, message: "请输入原密码", trigger: "blur" }],
        newPass: [
          { required: true, message: "请输入新密码", trigger: "blur" },
          { min: 6, message: "请至少输入6个字符", trigger: "blur" },
          { max: 32, message: "最多输入32个字符", trigger: "blur" }
        ],
        rePass: [
          { required: true, message: "请再次输入新密码", trigger: "blur" },
          { validator: valideRePassword, trigger: "blur" }
        ]
      },
      editMobileModal: false,
      saveMobileLoading: false,
      countDown: 0,
      editMobileForm: {
        mobile: "",
        verifyCode: ""
      },
      validMessage: "",
      mobileValidate: {
        mobile: [
          { required: true, message: "请输入新手机号", trigger: "blur" },
          { validator: valideMobile, trigger: "blur" }
        ],
        verifyCode: [{ required: true, message: "验证码必输", trigger: "blur" }]
      },
      editNickNameModal: false,
      nicknameError: "",
      saveNicknameLoading: false,
      editNicknameForm: {
        nickname: ""
      }
    };
  },
  computed: {
    verifyText: function() {
      return this.countDown > 0 ? this.countDown + "s后重新获取" : "获取验证码";
    },
    sendVerifyCodeDisabled() {
      let result = true;
      var re = /^1[0-9]{10}$/;
      if (
        this.editMobileForm.mobile &&
        re.test(this.editMobileForm.mobile) &&
        this.countDown <= 0 &&
        !this.validMessage
      ) {
        result = false;
      }
      return result;
    }
  },
  methods: {
    handleUpload(file) {
      let reqData = new FormData();
      var self = this;
      reqData.append("fileId", this.fileId);
      reqData.append("comment", this.comment);
      reqData.append("file", file);
      util.ajax
        .post("/file/upload", reqData, {
          headers: { "Content-Type": "multipart/form-data" }
        })
        .then(response => {
          if (response.status === 200) {
            self.avatarUrl = response.data.url;
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
      return false;
    },
    handleSuccess(res, file) {},
    handleFormatError(file) {
      this.$Notice.warning({
        title: "The file format is incorrect",
        desc: "文件格式必须是jpg或者png"
      });
    },
    handleMaxSize(file) {
      this.$Notice.warning({
        title: "Exceeding file size limit",
        desc: "文件 " + file.name + " 太大,请勿超过2M."
      });
    },

    editNicknameBtnClick() {
      this.editNickNameModal = true;
    },

    cancelEditNickname() {
      this.editNicknameForm.nickname = "";
      this.nicknameError = "";
      this.editNickNameModal = false;
    },

    saveEditNickname() {
      this.nicknameError = "";
      if (
        !this.editNicknameForm.nickname ||
        this.editNicknameForm.nickname.trim() == ""
      ) {
        this.nicknameError = "用户名不能为空";
        return;
      }
      let reqData = {
        userId: this.userDetail.id,
        nickname: this.editNicknameForm.nickname
      };
      this.saveNicknameLoading = true;
      util.ajax
        .post("/user/update/nickname", reqData)
        .then(response => {
          this.saveNicknameLoading = false;
          this.userDetail.nickname = reqData.nickname;
          this.cancelEditNickname();
          this.$Message.success("用户名更新成功, 可以使用用户名登录.");
        })
        .catch(error => {
          this.saveNicknameLoading = false;
          util.errorProcessor(this, error);
        });
    },

    validateNickname() {
      let value = this.editNicknameForm.nickname;
      this.nicknameError = "";
      if (!value || value.trim() == "") {
        this.nicknameError = "用户名必输";
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
            this.nicknameError = result.message;
          } else {
            this.nicknameError = "用户名检验失败";
          }
        });
    },

    showEditPassword() {
      this.editPasswordModal = true;
    },
    cancelEditPass() {
      this.editPasswordModal = false;
    },
    saveEditPass() {
      this.$refs["editPasswordForm"].validate(valid => {
        if (valid) {
          this.savePassLoading = true;
          let reqData = {
            userId: this.userDetail.id,
            oldPass: this.editPasswordForm.oldPass,
            newPass: this.editPasswordForm.newPass
          };
          util.ajax
            .post("/user/update/password", reqData)
            .then(response => {
              this.savePassLoading = false;
              this.$Message.success("修改成功, 需要重新登录");
              this.$router.push({
                name: "login"
              });
            })
            .catch(error => {
              this.savePassLoading = false;
              util.errorProcessor(this, error);
            });
        } else {
          this.$Message.warning("数据格式错误");
        }
      });
    },

    cancelEditUserInfor() {
      this.$store.commit("removeTag", "ownspace_index");
      localStorage.pageOpenedList = JSON.stringify(
        this.$store.state.app.pageOpenedList
      );
      let lastPageName = "";
      if (this.$store.state.app.pageOpenedList.length > 1) {
        lastPageName = this.$store.state.app.pageOpenedList[1].name;
      } else {
        lastPageName = this.$store.state.app.pageOpenedList[0].name;
      }
      this.$router.push({
        name: lastPageName
      });
    },

    saveEdit() {
      if (!this.userDetail || !this.userDetail.id) {
        this.$Message.warning("获取用户详情失败, 请刷新页面重试");
        return;
      }
      this.save_loading = true;
      this.userDetail.avatarUrl = this.avatarUrl;
      util.ajax
        .put("user/save", this.userDetail)
        .then(response => {
          this.save_loading = false;
          this.$Message.success("保存成功");
          this.userDetail = response.data;
          this.$store.commit(
            "setAvator",
            this.userDetail.avatarUrl + "?x-oss-process=style/resize200"
          );
          this.$store.commit("setUserDetail", this.userDetail);
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    countDownTimer: function() {
      if (this.countDown > 0) {
        this.countDown--;
        setTimeout(this.countDownTimer, 1000);
      }
    },

    validMobileExist() {
      this.validMessage = undefined;
      let mobile = this.editMobileForm.mobile;
      if (!mobile) {
        return;
      }
      let re = /^1[0-9]{10}$/;
      if (!re.test(mobile)) {
        return;
      }
      util.ajax
        .get("/user/valid/mobile", { params: { mobile: mobile } })
        .then(response => {
          this.validMessage = undefined;
        })
        .catch(error => {
          this.validMessage = error.response.data.message;
        });
    },

    handleVerifyCode() {
      var self = this;
      this.countDown = 60;
      this.countDownTimer();
      util.ajax
        .post("/phone/verifycode", {
          mobile: self.editMobileForm.mobile
        })
        .then(function(response) {
          if (response.status === 200) {
            self.countDown = 60;
          }
        })
        .catch(function(error) {
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
          this.$Message.warning("必输项信息缺失");
          return;
        } else {
          let reqData = {
            userId: this.userDetail.id,
            mobile: this.editMobileForm.mobile,
            verifyCode: this.editMobileForm.verifyCode
          };
          this.saveMobileLoading = true;
          util.ajax
            .put("/user/update/mobile", reqData)
            .then(response => {
              this.saveMobileLoading = false;
              this.userDetail.mobile = this.editMobileForm.mobile;
              this.cancelEditMobile();
              this.$Message.success("修改成功");
            })
            .catch(error => {
              this.saveMobileLoading = false;
              util.errorProcessor(this, error);
            });
        }
      });
    },

    init() {
      let storeUser = this.$store.state.user.userDetail;
      if (!storeUser || !storeUser.id) {
        //获取不到用户信息，直接跳转到登录
        this.$router.push({
          name: "login"
        });
        return;
      }
      //获取用户详情信息
      util.ajax
        .get("/user/detail", { params: { userId: storeUser.id } })
        .then(response => {
          this.userDetail = response.data;
          if (this.userDetail && this.userDetail.avatarUrl) {
            this.avatarUrl = this.userDetail.avatarUrl;
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    }
  },
  mounted() {
    this.init();
  }
};
</script>

<style >
</style>

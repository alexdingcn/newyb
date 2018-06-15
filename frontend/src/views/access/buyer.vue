<template>
    <Row>
        <i-col >
            <Card>
                <p slot="title">
                    <Icon type="ios-flask-outline"></Icon> 采购员
                </p>
                <div slot="extra">
                </div>
                <Alert type="success">
                    <p><strong>如何添加采购员: </strong></p>
                    <ul style="padding-left:30px;">
                        <li>左侧列出的是所有用户，如果是新客户，请点击<Button type="text" size="small" @click="addUser">添加用户</Button>
                        </li>
                        <li>然后选择左侧用户移动到右侧后点击保存</li>
                    </ul>
                </Alert>
                <Transfer
                        :data="allUsers"
                        :target-keys="buyerKeys"
                        :list-style="listStyle"
                        :render-format="render3"
                        :titles="['所有用户', '采购员']"
                        :operations="['移除','加入']"
                        filterable

                        @on-change="handleMove">
                    <div :style="{float: 'right', margin: '5px'}">
                        <Button type="ghost" size="small" icon="refresh" :loading="loading" @click="reloadData">刷新</Button>
                        <Button type="success" size="small" icon="checkmark" :loading="loading" @click="saveBuyer">保存</Button>
                    </div>
                </Transfer>
            </Card>
        </i-col>
    </Row>
</template>
<script>
import util from "@/libs/util.js";

export default {
  data() {
    return {
      loading: false,
      allUsers: [],
      buyerKeys: [],
      buyers: {},
      listStyle: {
        width: "300px",
        height: "400px"
      }
    };
  },
  methods: {
    getUserList() {
      var self = this;
      self.loading = true;
      util.ajax
        .get("/user/list")
        .then(function(response) {
          self.loading = false;
          if (response.status === 200 && response.data) {
            var list = response.data;
            let uList = [];
            for (let i = 0; i < list.length; i++) {
              uList.push({
                key: list[i].id,
                label: "<b>" + list[i].nickname + "</b>",
                description:
                  "[ " +
                  (list[i].realname ? "姓名:" + list[i].realname + " " : "") +
                  "手机:" +
                  list[i].mobile +
                  " ]",
                disabled: false
              });
            }
            self.allUsers = uList;
            self.getBuyerList();
          }
        })
        .catch(function(error) {
          self.loading = false;
          util.errorProcessor(self, error);
        });
    },
    getBuyerList() {
      var self = this;
      self.loading = true;
      util.ajax
        .get("/userrole/list", {
          params: { roleQuery: "ROLE_BUYER;ROLE_BUYER_SPECIAL" }
        })
        .then(function(response) {
          self.loading = false;
          if (response.status === 200 && response.data) {
            var list = response.data;
            let uList = [];
            for (let i = 0; i < list.length; i++) {
              uList.push(list[i].userId);
              self.buyers[list[i].userId] = list[i];
            }
            self.buyerKeys = self.allUsers
              .filter(item => uList.indexOf(item.key) >= 0)
              .map(item => item.key);
          }
        })
        .catch(function(error) {
          self.loading = false;
          util.errorProcessor(self, error);
        });
    },
    handleMove(newTargetKeys) {
      this.buyerKeys = newTargetKeys;
      if (this.buyers) {
        var newBuyers = {};
        for (let i = 0; i < newTargetKeys.length; i++) {
          var userId = newTargetKeys[i];
          var existed = this.buyers[userId];
          if (existed) {
            newBuyers[userId] = existed;
          } else {
            newBuyers[userId] = { userId: userId, name: "ROLE_BUYER" };
          }
        }
        this.buyers = newBuyers;
      }
    },
    render3(item) {
      return item.label + " - " + item.description;
    },
    reloadData() {
      this.getUserList();
    },
    addUser() {
      this.$router.push({
        name: "add_user"
      });
    },
    saveBuyer() {
      var params = new Array();
      var self = this;
      for (var key in this.buyers) {
        params.push(this.buyers[key]);
      }
      let reqData = {
        roleTypes: ["ROLE_BUYER", "ROLE_BUYER_SPECIAL"],
        roles: params
      };
      self.loading = true;
      util.ajax
        .post("/userrole/save", reqData)
        .then(function(response) {
          self.loading = false;
          if (response.status === 200 && response.data) {
            self.$Message.success("保存采购员列表成功");
            self.getUserList();
          }
        })
        .catch(function(error) {
          self.loading = false;
          util.errorProcessor(self, error);
        });
    }
  },
  mounted() {
    this.getUserList();
  }
};
</script>
<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="ios-list"></Icon>
                权限维护
            </p>
            <div>
                <Row>
                    <i-col span="12">
                        <user-list ref="userList" @choose-user="chooseUser"></user-list>
                    </i-col>
                    <i-col span="10" style="margin-left: 50px;">
                        <Card>
                            <Spin size="large" fix v-if="spinShow"></Spin>
                            <p slot="title">
                                <Icon type="person"></Icon> {{showUserTitle || ''}}
                            </p>
                            <div slot="extra">
                                <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveDataStep1">确认保存</Button>
                            </div>
                            <div>
                                <h3>用户状态</h3>
                                <RadioGroup v-model="userStatus" style="padding-left: 25px;">
                                    <Radio :label="1">
                                        <Icon type="checkmark-circled" color="green"></Icon>
                                        <span>激活</span>
                                    </Radio>
                                    <Radio :label="0">
                                        <Icon type="close-circled" color="#ff9900"></Icon>
                                        <span>未激活</span>
                                    </Radio>
                                    <Radio :label="-1">
                                        <Icon type="minus-circled" color="red"></Icon>
                                        <span>离职</span>
                                    </Radio>
                                </RadioGroup>
                            </div>
                            <div style="margin-top: 10px;">
                                <h3>可见页面</h3>
                                <div style="padding-left: 25px;">
                                    <Tree ref="routeTree" :data="routeTreeData" show-checkbox multiple></Tree>
                                </div>
                            </div>
                        </Card>
                    </i-col>
                </Row>
            </div>
        </Card>
    </div>
</template>

<script>
import { appRouter } from "@/router/router";
import util from "@/libs/util.js";
import userList from "./user-list.vue";

export default {
  name: "access_index",
  components: {
    userList
  },
  data() {
    return {
      currUser: {},
      userStatus: "",
      routeTreeData: [],
      treeLoading: false,
      spinShow: false,
      saveLoading: false,
      showUserTitle: ""
    };
  },
  mounted() {
    this.init();
  },
  watch: {
    currUser: function(data) {
      this.userStatus = data.status;
      if (data) {
        let nickName = this.currUser ? this.currUser.nickname : "";
        let realname = this.currUser ? this.currUser.realname : "";
        this.showUserTitle = nickName + (realname ? "[" + realname + "]" : "");
      }
      this.selectedTreeLeaf(data);
    }
  },
  methods: {
    init() {
      this.createMenuTree();
    },

    createMenuTree() {
      let treeData = [
        {
          title: "所有",
          expand: true,
          children: []
        }
      ];
      let treeDataClildren = [];
      appRouter.forEach(item => {
        if (item && item.children && item.children.length > 0) {
          // 父节点不设置value，只针对子节点的设置value
          let parent = {
            title: item.title,
            expand: true,
            checked: false
          };
          let itemChildren = item.children;
          let childList = [];
          itemChildren.forEach(childItem => {
            let child = {
              title: childItem.title,
              value: childItem.name,
              checked: false
            };
            childList.push(child);
          });
          parent.children = childList;
          treeDataClildren.push(parent);
        }
      });
      treeData[0].children = treeDataClildren;
      this.routeTreeData = treeData;
    },

    chooseUser(data) {
      this.currUser = data;
    },

    selectedTreeLeaf(user) {
      this.spinShow = true;
      if (!user || !user.id) {
        //所有的选择全部清空
        this.clearTreeCheck();
        this.spinShow = false;
      } else {
        //查询用户的当前所有的可以见菜单，然后刷新对应的菜单
        util.ajax
          .get("/user/route/list", { params: { userId: user.id } })
          .then(response => {
            this.checkedTreeByUserRoutes(response.data);
            this.spinShow = false;
          })
          .catch(error => {
            util.errorProcessor(this, error);
            this.clearTreeCheck();
            this.spinShow = false;
          });
      }
    },

    checkedTreeByUserRoutes(userRoutes) {
      this.clearTreeCheck(); //先把树的所有选择项清空在重新勾选
      if (!userRoutes || userRoutes.length <= 0) {
        return;
      }
      let menuList = this.routeTreeData[0].children;
      userRoutes.forEach(item => {
        menuList.forEach(menu => {
          let menuClhidList = menu.children;
          if (menuClhidList && menuClhidList.length > 0) {
            let leaf = menuClhidList.find(child => {
              return item.routeName === child.value;
            });
            if (leaf) {
              leaf.checked = true;
            }
          }
        });
      });
    },

    clearTreeCheck() {
      let choosedData = this.$refs.routeTree.getCheckedNodes();
      if (choosedData && choosedData.length > 0) {
        choosedData.forEach(item => {
          item.checked = false;
        });
      }
    },

    saveDataStep1() {
      if (!this.currUser || !this.currUser.id) {
        this.$Message.warning("请先选择需要修改的用户");
        return;
      }
      this.spinShow = true;
      this.saveLoading = true;
      let self = this;
      if (self.userStatus === -1) {
        self.$Modal.confirm({
          title: "离职状态确认",
          content:
            "是否确认用户" +
            self.currUser.nickname +
            "已经离职，离职状态提交后不可恢复! ",
          onOk: () => {
            self.saveDataStep3(); //直接到第三步提交
          },
          onCancel: () => {
            this.spinShow = false;
            this.saveLoading = false;
          }
        });
      } else {
        self.saveDataStep2();
      }
    },

    saveDataStep2() {
      if (!this.currUser || !this.currUser.id) {
        this.$Message.warning("请先选择需要修改的用户");
        this.spinShow = false;
        this.saveLoading = false;
        return;
      }
      let self = this;
      self.$Modal.confirm({
        title: "保存提醒",
        content: "权限数据是否已经修改正确",
        onOk: () => {
          self.saveDataStep3();
        },
        onCancel: () => {
          this.spinShow = false;
          this.saveLoading = false;
        }
      });
    },

    saveDataStep3() {
      if (!this.currUser || !this.currUser.id) {
        this.$Message.warning("请先选择需要修改的用户");
        this.spinShow = false;
        this.saveLoading = false;
        return;
      }
      //所有修改权限的数据
      let accessRoutes = [];
      let checkedTreeData = this.$refs.routeTree.getCheckedNodes();
      if (checkedTreeData && checkedTreeData.length > 0) {
        checkedTreeData.forEach(item => {
          if (item.value) {
            accessRoutes.push(item.value);
          }
        });
      }
      let reqData = {
        userId: this.currUser.id,
        status: this.userStatus,
        routeNames: accessRoutes
      };
      let self = this;
      util.ajax
        .put("/user/save/access", reqData)
        .then(response => {
          self.spinShow = false;
          this.saveLoading = false;
          self.$Message.success("保存成功");
          //重新设置下当前选择用户的状态
          self.currUser.status = self.userStatus;
          self.$refs.userList.resetRowUserTab(self.currUser);
        })
        .catch(error => {
          self.spinShow = false;
          this.saveLoading = false;
          util.errorProcessor(self, error);
        });
    }
  }
};
</script>

<style lang="less">
</style>

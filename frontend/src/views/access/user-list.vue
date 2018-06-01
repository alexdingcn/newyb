
<template>
    <div>
        <Card>
            <p slot="title">
                <Icon type="person-stalker"></Icon> 用户列表
            </p>
            <div slot="extra">
                <Row type="flex" justify="end">
                    <Input style="width: 300px;" @on-keyup="searchValueChange" v-model="searchValue" icon="search" placeholder="用户名/手机号/真实姓名" />
                </Row>
            </div>
            <Table ref="userTab" size="small" highlight-row height="600" :loading="tabLoading"
                :columns="userColumns" :data="userList" @on-row-click="chooseUser">
            </Table>
        </Card>
    </div>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'user-list',
    data() {
        return {
            searchValue: '',
            currUser: {},
            tabLoading: false,
            allUserList: [],
            userList: [],
            userColumns: [
                {
                    title: '用户名',
                    key: 'nickname',
                    align: 'center'
                },
                {
                    title: '手机号',
                    key: 'mobile',
                    align: 'center'
                },
                {
                    title: '姓名',
                    key: 'realname',
                    align: 'center'
                },
                {
                    title: '当前状态',
                    key: 'status',
                    align: 'center',
                    render: (h, params) => {
                        const row = params.row;
                        const color = row.status === 1 ? 'green' : (row.status ===0 ? 'red' : '#bbbec4');
                        const text = row.status === 1 ? '已激活' : (row.status === 0 ? '未激活' : '离职');
                        return h('Tag', {
                            props: {
                                type: 'dot',
                                color: color
                            }
                        }, text);
                    }
                }
            ],
            lastTime: 0
        }
    },
    watch: {
        currUser: function(data) {
            this.$emit('choose-user', data);
        }
    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            this.refreshUserList();
        },

        refreshUserList() {
            this.tabLoading = true;
            util.ajax.get('/user/list')
                .then((response) => {
                    this.tabLoading = false;
                    this.allUserList = response.data;
                    this.userList = this.allUserList;
                    this.currUser = {},
                    this.searchValue = '';
                    this.$refs.userTab.clearCurrentRow();
                })
                .catch((error) => {
                    this.tabLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        searchValueChange(event) {
            let self = this;
            self.lastTime = event.timeStamp;
            setTimeout(function() {
                if(self.lastTime - event.timeStamp === 0) {
                    self.searchUserList();
                }
            }, 500);
        },

        searchUserList() {
            let searchList = [];
             let value = this.searchValue;
             if (!value) {
                 this.userList = this.allUserList;
             }
             searchList = this.allUserList.filter((item) => {
                 let nickname = item.nickname;
                 let mobile = item.mobile;
                 let realname = item.realname;
                 if (nickname && typeof nickname === 'string' && nickname.indexOf(value) >= 0) {
                     return item;
                 }
                 if (mobile && typeof mobile === 'string' && mobile.indexOf(value) >= 0) {
                     return item;
                 }
                 if (realname && typeof realname === 'string' && realname.indexOf(value) >= 0) {
                     return item;
                 }
             });
             this.userList = searchList;
             this.currUser = {};
        },

        chooseUser(data, index) {
            this.currUser = data;
            
        },

        clearCurrentUser() {
            this.currUser = {};
            this.$refs.userTab.clearCurrentRow();
        },

        resetRowUserTab(user) {
            if (!user) {
                return;
            }
            let self = this;
            self.allUserList.forEach((item, index) => {
                if (item.id === user.id) {
                    self.$set(self.allUserList, index, user);
                }
            });
            self.userList.forEach((item, index) => {
                if(item.id === user.id) {
                    self.$set(self.userList, index, user);
                }
            });
        }

    }
}
</script>

<style >

</style>

<template>
    <Card>
        <p slot="title">
            <Icon type="ios-flask-outline"></Icon> 采购员
        </p>
        <div slot="extra">
            <ButtonGroup class="padding-left-20">
                <Button type="primary" icon="android-add-circle" @click="saveBuyer">保存</Button>
            </ButtonGroup>
        </div>
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
                <Button type="ghost" size="small" @click="reloadData">刷新</Button>
            </div>
        </Transfer>
    </Card>

</template>
<script>
    import util from '@/libs/util.js';

    export default {
        data () {
            return {
                allUsers: [],
                buyerKeys: [],
                buyers: {},
                listStyle: {
                    width: '300px',
                    height: '400px'
                }
            }
        },
        methods: {
            getUserList () {
                var self = this;
                util.ajax.get('/user/list')
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            var list = response.data;

                            let uList = [];
                            for (let i = 0; i < list.length; i++) {
                                uList.push({
                                    key: list[i].id,
                                    label: "<b>" + list[i].nickname + "</b>",
                                    description: '[ ' + (list[i].realname ? '姓名:'
                                                     + list[i].realname + ' ': '')
                                                     + '手机:' + list[i].mobile + ' ]',
                                    disabled: false
                                });
                            }
                            self.allUsers = uList;
                            self.getBuyerList();
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
            },
            getBuyerList () {
                var self = this;
                util.ajax.get('/userrole/list', {params: {roleQuery: 'ROLE_BUYER;ROLE_BUYER_SPECIAL'} })
                        .then(function (response) {
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
                        .catch(function (error) {
                            console.log(error);
                        })
            },
            handleMove (newTargetKeys) {
                this.buyerKeys = newTargetKeys;
                if (this.buyers) {
                    var newBuyers = {};
                    for (let i=0; i< newTargetKeys.length; i++) {
                        var userId = newTargetKeys[i];
                        var existed = this.buyers[userId];
                        if (existed) {
                            newBuyers[userId] = existed;
                        } else {
                            newBuyers[userId] = {userId: userId, name:'ROLE_BUYER'}
                        }
                    }
                    this.buyers = newBuyers;
                }
            },
            render3 (item) {
                return item.label + ' - ' + item.description;
            },
            reloadData () {
                this.getUserList();
            },
            saveBuyer() {
                var params = new Array();
                var self = this;
                for (var key in this.buyers) {
                    params.push(this.buyers[key]);
                }
                let reqData = {
                    roleTypes:  ['ROLE_BUYER', 'ROLE_BUYER_SPECIAL'],
                    roles: params
                };
                util.ajax.post('/userrole/save', reqData)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.$Message.info("保存采购员列表成功");
                            self.getUserList();
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
            }
        },
        mounted () {
            this.getUserList();
        },
    }
</script>
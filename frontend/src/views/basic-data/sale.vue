<template>
    <Card>
        <p slot="title">
            <Icon type="ios-cart"></Icon> 销售员
        </p>
        <div slot="extra">
            <ButtonGroup class="padding-left-20">
                <Button type="primary" icon="android-add-circle" @click="saveBuyer">保存</Button>
            </ButtonGroup>
        </div>
        <Transfer
                :data="allUsers"
                :target-keys="salerKeys"
                :list-style="listStyle"
                :render-format="render3"
                :titles="['所有用户', '销售员']"
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
                salerKeys: [],
                salers: {},
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
                            self.getSalerList();
                        }
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            },
            getSalerList () {
                var self = this;
                util.ajax.get('/userrole/list', {params: {roleQuery: 'ROLE_SALER'} })
                        .then(function (response) {
                            if (response.status === 200 && response.data) {
                                var list = response.data;
                                let uList = [];
                                for (let i = 0; i < list.length; i++) {
                                    uList.push(list[i].userId);
                                    self.salers[list[i].userId] = list[i];
                                }
                                self.salerKeys = self.allUsers
                                                .filter(item => uList.indexOf(item.key) >= 0)
                                                .map(item => item.key);
                            }
                        })
                        .catch(function (error) {
                            util.errorProcessor(self, error);
                        });
            },
            handleMove (newTargetKeys) {
                this.salerKeys = newTargetKeys;
                if (this.salers) {
                    var newSalers = {};
                    for (let i=0; i< newTargetKeys.length; i++) {
                        var userId = newTargetKeys[i];
                        var existed = this.salers[userId];
                        if (existed) {
                            newSalers[userId] = existed;
                        } else {
                            newSalers[userId] = {userId: userId, name:'ROLE_SALER'}
                        }
                    }
                    this.salers = newSalers;
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
                for (var key in this.salers) {
                    params.push(this.salers[key]);
                }
                let reqData = {
                    roleTypes:  ['ROLE_SALER'],
                    roles: params
                };
                util.ajax.post('/userrole/save', reqData)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.$Message.info("保存销售员列表成功");
                            self.getUserList();
                        }
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    })
            }
        },
        mounted () {
            this.getUserList();
        },
    }
</script>
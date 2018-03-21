<template>
  <div>
      <Card>
          <p slot="title">
            <Icon type="ios-cart-outline"></Icon> 销售员
            </p>
        <div slot="extra">
        </div>
        <Row type="flex" justify="center" >
            <Col span="7">
                <Card class="check-card">
                    <div slot="title">
                        所有用户
                    </div>
                    <div slot="extra">
                        <Button type="ghost" size="small" icon="refresh">刷新</Button>
                    </div>
                    <Row style="margin-top:-10px; margin-bottom: 5px;">
                        <Input size="small" v-model="searchAll" icon="search" placeholder="请输入用户名搜索" style="width: 100%"></Input>
                    </Row>
                    <Table height="300" size="small" ref="allUserTab" :data="allUsers" :columns="allUserColumns">
                    </Table>
                </Card>
            </Col>
            <Col span="2"  style="padding-top: 150px;">
                <Row type ="flex" justify="start" >
                    <Button type="ghost" size="small" icon="arrow-right-c">加入</Button>
                </Row>
                <Row>
                    <Button type="ghost" size="small" icon="arrow-left-c">移除</Button>
                </Row>
            </Col>
            <Col span="7" style="margin-left: -25px;">
                <Card class="check-card">
                    <div slot="title">
                        销售员
                    </div>
                    <div slot="extra">
                        <Button type="ghost" size="small" icon="refresh">刷新</Button>
                    </div>
                    <Row style="margin-top:-10px; margin-bottom: 5px;">
                        <Input size="small" v-model="searchAll" icon="search" placeholder="请输入用户名搜索" style="width: 100%"></Input>
                    </Row>
                    <Table height="300" size="small" ref="salerUserTab" :data="allUsers" :columns="allUserColumns">
                    </Table>
                </Card>
            </Col>
            <Col span="8">
                <Card>
                    <p slot="title">
                        <Icon type="person"></Icon> 销售信息维护
                    </p>
                    <div slot="extra">
                        <Button size="small" type="success" icon="checkmark-round" > 保存 </Button>
                    </div>
                </Card>
            </Col>
        </Row>
      </Card>
  </div>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'sale',
    data () {
        return {
            searchAll: '',
            allUsers: [],
            allUserColumns: [
                {
                    type: "selection",
                    width: 50
                },
                {
                    title: "用户",
                    key: "nickname",
                    render: (h, params) => {
                        let realName = params.row.realname;
                        let phone = params.row.mobile;
                        let after = '';
                        let showLabel = '';
                        if (realName) {
                            if(phone) {
                                after = realName + ':' + phone;
                            }else {
                                after = realName;
                            }
                        }else {
                            after = phone;
                        }
                        if (after !== '') {
                            showLabel = params.row.nickname + ' - [' + after + ']'; 
                        }else {
                            showLabel = params.row.nickname;
                        }
                        return h('span', showLabel);
                    }
                },
            ],
            saleUserKeys: [],
            salers: {}
            
        }
    },
    mounted () {
        this.getUserList();
    },
    methods: {
        getUserList () {
            var self = this;
            util.ajax.get('/user/list')
                .then(function (response) {
                    if (response.status === 200 && response.data) {
                        self.allUsers = response.data;
                        self.getSalerList();
                    }
                })
                .catch(function (error) {
                    util.errorProcessor(this, error);
                });
        },
        getSalerList() {

        },
        reloadData () {
                this.getUserList();
        },
    }



}
</script>

<style>
.check-card {
    width: 320px;
    height: 400px;
}

</style>


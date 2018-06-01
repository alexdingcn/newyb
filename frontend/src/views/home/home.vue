<style lang="less">
@import "./home.less";
@import "../../styles/common.less";
</style>
<template>
    <div class="home-main">

        <Modal v-model="loggedUser.companyExpiredTime > 0 && showTrialWarning" width="360">
            <p slot="header" style="color:#f60;text-align:center">
                <Icon type="alert-circled"></Icon>
                <span>试用马上到期</span>
            </p>
            <div style="text-align:center">
                <p>您的试用马上到期, 请在{{ loggedUser.companyExpiredTime | formatDate }}前升级为正式版</p>
            </div>
            <div slot="footer">
                <Button type="error" size="large" @click="showTrialWarning = false">知道了</Button>
            </div>
        </Modal>

        <Modal v-model="showUpdateHistory" class="updateModal" :closable="false" 
            @on-ok="onUpdateClose"  @on-cancel="onUpdateClose">
            <p slot="header">
                <Icon type="ios-information-outline"></Icon>
                <span>更新历史</span>
            </p>
            <Carousel ref="updateHistoryList" v-model="updateHistoryIdx" :autoplay="updateHistories.length > 1" :autoplay-speed=4000>
                <CarouselItem v-for="(history, index) in updateHistories" :key="index">
                    <div class="update-carousel">
                        <h2>{{ history.title }}</h2>
                        <div v-html="history.content"></div>
                        <img :src="history.imageUrl" v-if="history.imageUrl"/>
                    </div>
                </CarouselItem>
            </Carousel>
        </Modal>

        <Alert type="error" v-show="loggedUser.companyExpiredTime > 0" show-icon closable>
            <p>您的系统使用期限即将到期，请尽快续费购买，以免影响您的正常使用！</p>
            <span slot="close">升级</span>
        </Alert>
        <Card>
            <Row :gutter="10">
                <i-col span="3">
                    <Row class-name="made-child-con-middle" type="flex" align="middle">
                        <img class="avator-img" :src="avatorPath" />
                    </Row>
                </i-col>
                <i-col span="12">
                    <div>
                        <b class="card-user-infor-name">{{loggedUser.realname || loggedUser.mobile}}</b>
                        <p>{{loggedUser.nickname}}</p>
                    </div>
                    <Row class="margin-top-8">
                        <p class="notwrap">上次登录时间: {{ loggedUser.lastLoginTime | formatTime }} </p>
                    </Row>
                </i-col>
                <i-col span="5" v-show="loggedUser.companyExpiredTime > 0">
                    <p>当前版本:试用版</p>
                    <br/>
                    <p>到期日: {{ loggedUser.companyExpiredTime | formatTime }} </p>
                </i-col>
                <i-col span="4">
                    <!-- <ButtonGroup>
                        <Button>升级</Button>
                        <Button type="primary">充值</Button>
                    </ButtonGroup> -->
                </i-col>
            </Row>
        </Card>

        <Row :gutter="10" class="margin-top-10">
            <i-col :md="24" :lg="16">
                <Row :gutter="5">
                    <i-col :xs="24" :sm="12" :md="4" :style="{marginBottom: '10px'}">
                    <infor-card
                            id-name="user_created_count"
                            :end-val="count.init"
                            iconType="android-checkmark-circle"
                            color="#2d8cf0"
                            intro-text="待审核"
                    ></infor-card>
                    </i-col>
                    <i-col :xs="24" :sm="12" :md="4" :style="{marginBottom: '10px'}">
                    <infor-card
                            id-name="visit_count"
                            :end-val="count.quality_checked"
                            iconType="eject"
                            color="#64d572"
                            intro-text="待出库"
                    ></infor-card>
                    </i-col>
                    <i-col :xs="24" :sm="12" :md="4" :style="{marginBottom: '10px'}">
                    <infor-card
                            id-name="collection_count"
                            :end-val="count.sale_checked"
                            iconType="navigate"
                            color="#9b72ff"
                            intro-text="待发货"
                    ></infor-card>
                    </i-col>
                    <i-col :xs="24" :sm="12" :md="4" :style="{marginBottom: '10px'}">
                    <infor-card
                            id-name="transfer_count"
                            :end-val="count.settled"
                            iconType="social-yen"
                            color="#57a3f3"
                            intro-text="待确认收款"
                    ></infor-card>
                    </i-col>
                    <i-col :xs="24" :sm="12" :md="4" :style="{marginBottom: '10px'}">
                    <infor-card
                            id-name="transfer_count"
                            :end-val="count.returning"
                            iconType="arrow-return-left"
                            color="#f25e43"
                            intro-text="退货待审核"
                    ></infor-card>
                    </i-col>
                    <i-col :xs="24" :sm="12" :md="4" :style="{marginBottom: '10px'}">
                    <infor-card
                            id-name="transfer_count"
                            :end-val="count.returned"
                            iconType="pull-request"
                            color="#f25e43"
                            intro-text="待确认退货"
                    ></infor-card>
                    </i-col>
                </Row>
                <Row :gutter="10">
                    <i-col :md="24" :lg="12" :style="{marginBottom: '10px'}">
                        <Card>
                            <p slot="title" class="card-title">
                                <Icon type="android-map"></Icon>
                                近4周销量统计
                            </p>
                            <div class="data-source-row">
                                <sale-volume :content="amountStats"></sale-volume>
                            </div>
                        </Card>
                    </i-col>
                    <i-col :md="24" :lg="12" :style="{marginBottom: '10px'}">
                        <Card>
                            <p slot="title" class="card-title">
                                <Icon type="ios-pulse-strong"></Icon>
                                近4周商品销售统计
                            </p>
                            <div class="data-source-row">
                                <data-source-pie :content="goodsStats"></data-source-pie>
                            </div>
                        </Card>
                    </i-col>
                </Row>
            </i-col>
            <i-col :md="24" :lg="8">
                <Row class-name="home-page-row1" :gutter="10">
                    <i-col :md="12" :lg="24" :style="{marginBottom: '10px'}">
                        <Card>
                            <p slot="title" class="card-title">
                                <Icon type="android-checkbox-outline"></Icon>
                                待办事项
                            </p>
                            <a type="text" slot="extra" @click.prevent="addNewToDoItem">
                                <Icon type="plus-round"></Icon>
                            </a>
                            <Modal
                                v-model="showAddNewTodo"
                                title="添加新的待办事项"
                                @on-ok="addNew"
                                @on-cancel="cancelAdd">
                                <Row type="flex" justify="center">
                                    <Input v-model="newToDoItemValue" icon="compose" placeholder="请输入..." style="width: 300px" />
                                </Row>
                                <Row slot="footer">
                                    <Button type="text" @click="cancelAdd">取消</Button>
                                    <Button type="primary" @click="addNew">确定</Button>
                                </Row>
                            </Modal>
                            <div class="to-do-list-con">
                                <div v-for="(item, index) in toDoList" :key="'todo-item' + (toDoList.length - index)" class="to-do-item">
                                    <to-do-list-item :content="item.title"></to-do-list-item>
                                </div>
                            </div>
                        </Card>
                    </i-col>
                </Row>
            </i-col>

        </Row>
        <Row :gutter="10" class="margin-top-10">
            <i-col :md="24" :lg="16">
                <Card :padding="0">
                    <p slot="title" class="card-title">
                        <Icon type="map"></Icon>
                        商品销售地理分布
                    </p>
                    <div class="map-con">
                        <i-col span="10">
                        <map-data-table :cityData="cityData" height="281" :style-obj="{margin: '12px 0 0 11px'}"></map-data-table>
                        </i-col>
                        <i-col span="14" class="map-incon">
                        <Row type="flex" justify="center" align="middle">
                            <home-map :city-data="cityData"></home-map>
                        </Row>
                        </i-col>
                    </div>
                </Card>
            </i-col>
            <i-col :md="24" :lg="8">
                <Card>
                    <p slot="title" class="card-title">
                        <Icon type="android-wifi"></Icon>
                        今日采购/销售金额
                    </p>
                    <div class="data-source-row">
                        <user-flow></user-flow>
                    </div>
                </Card>
            </i-col>
        </Row>
        <Row class="margin-top-10">
            <Card>
                <p slot="title" class="card-title">
                    <Icon type="ios-shuffle-strong"></Icon>
                    上周每日销售量（分商品）
                </p>
                <div class="line-chart-con">
                    <service-requests></service-requests>
                </div>
            </Card>
        </Row>
    </div>
</template>

<script>
import Cookies from 'js-cookie';
import Vue from 'vue';
import cityData from './map-data/get-city-value.js';
import homeMap from './components/map.vue';
import dataSourcePie from './components/dataSourcePie.vue';
import saleVolume from './components/saleVolume.vue';
import serviceRequests from './components/serviceRequests.vue';
import userFlow from './components/userFlow.vue';
import countUp from './components/countUp.vue';
import inforCard from './components/inforCard.vue';
import mapDataTable from './components/mapDataTable.vue';
import toDoListItem from './components/toDoListItem.vue';
import util from '@/libs/util.js';
import moment from 'moment';

Vue.filter('formatDate', function(value) {
    if (value) {
        return moment(value).format('YYYY/MM/DD');
    }
});
Vue.filter('formatTime', function(value) {
    if (value) {
        return moment(value).format('YYYY/MM/DD HH:mm:ss');
    }
});

export default {
    name: 'home',
    components: {
        homeMap,
        dataSourcePie,
        saleVolume,
        serviceRequests,
        userFlow,
        countUp,
        inforCard,
        mapDataTable,
        toDoListItem
    },
    data () {
        return {
            showTrialWarning: true,
            loggedUser: JSON.parse(localStorage.getItem('userDetail')),
            toDoList: [],
            count: {
                init: 0,
                sale_checked: 0,
                quality_checked: 0,
                settled: 0,
                returning: 0,
                returned: 0
            },
            cityData: cityData,
            showAddNewTodo: false,
            newToDoItemValue: '',
            amountStats: [],
            goodsStats: [],
            updateHistories: [],
            updateHistoryIdx: 0,
            lastUpdateIdx: 0,
            showUpdateHistory: false
        };
    },
    mounted() {
        this.getUpdates();
        this.loadOrderStats();
        this.loadAmountStats();
        this.loadGoodsStats();
    },
    computed: {
        avatorPath () {
            return localStorage.avatorImgPath;
        }
    },
    methods: {
        getUpdates() {
            var self = this;
            var lastViewUpdate = Cookies.get('last-updateid') || '';
            util.ajax.get('/home/updates?lt=' + lastViewUpdate)
                .then(function (response) {
                    if (response.status === 200 && response.data && response.data.length > 0) {
                        self.updateHistories = response.data;
                        self.showUpdateHistory = true;
                        self.lastUpdateIdx = response.data[response.data.length - 1].id;
                        self.$nextTick(() => { self.$refs.updateHistoryList.handleResize(); } );
                    }
                })
                .catch(function (error) {
                    console.log(error);
                    //util.errorProcessor(self, error);
                });
        },
        onUpdateClose() {
            Cookies.set('last-updateid', this.lastUpdateIdx);
        },
        loadOrderStats() {
            var self = this;
            util.ajax.post('/home/orderstats')
                .then(function (response) {
                    if (response.status === 200 && response.data) {
                        for (var i=0; i<response.data.length; i++) {
                            self.count[response.data[i].status.toLowerCase()] = response.data[i].count;
                        }
                    }
                })
                .catch(function (error) {
                    util.errorProcessor(self, error);
                });
        },
        loadAmountStats() {
            var self = this;
            util.ajax.post('/home/amountstats', {
                    startDate: moment().add(-4, 'w').format('YYYY-MM-DD'),
                    endDate: moment().add(1, 'd').format('YYYY-MM-DD')
                })
                .then(function (response) {
                    if (response.status === 200 && response.data) {
                        self.amountStats = response.data;
                    }
                })
                .catch(function (error) {
                    util.errorProcessor(self, error);
                });
        },
        loadGoodsStats() {
            var self = this;
            util.ajax.post('/home/goodsstats', {
                        startDate: moment().add(-4, 'w').format('YYYY-MM-DD'),
                        endDate: moment().add(1, 'd').format('YYYY-MM-DD')
                    })
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.goodsStats = response.data;
                        }
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
        },
        addNewToDoItem () {
            this.showAddNewTodo = true;
        },
        addNew () {
            if (this.newToDoItemValue.length !== 0) {
                this.toDoList.unshift({
                    title: this.newToDoItemValue
                });
                setTimeout(() => {
                    this.newToDoItemValue = '';
                }, 200);
                this.showAddNewTodo = false;
            } else {
                this.$Message.error('请输入待办事项内容');
            }
        },
        cancelAdd () {
            this.showAddNewTodo = false;
            this.newToDoItemValue = '';
        }
    }
};
</script>

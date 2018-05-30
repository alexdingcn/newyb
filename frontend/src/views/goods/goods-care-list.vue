
<template>
<div>
        <Row>
            <Col span="11">
        <Card>
            <p slot="title">
                <Icon type="person-stalker"></Icon> 商品列表
            </p>
            <div slot="extra">
                <Row type="flex" justify="end">
                    <Input style="width: 200px;" @on-change="refreshGoodList" v-model="searchValue" icon="search" placeholder="商品名/编号" />
                </Row>
            </div>
            <Table ref="goodTab" size="small" highlight-row height="800" :loading="tabLoading"
                :columns="goodColumns" :data="goodsList" @on-row-click="chooseGood" >
            </Table>
            <Row type="flex" justify="end" class="margin-top-10">
                <Page :total="totalCount" :current="currentPage" :page-size="pageSize" @on-change="changePage" size="small" show-total></Page>
            </Row>
        </Card>
        </Col>
        <Col span="11">
                <goodsCareRecord ref="carelist" ></goodsCareRecord>
    </Col>
        </Row>
</div>
</template>

<script>
import util from '@/libs/util.js';
import bus from '@/libs/bus';
import goodsCareRecord from './goods-care-record.vue';

export default {
    name: 'goodscarelist',
    components:{
         goodsCareRecord,
    },
    data() {
        return {
            searchValue: '',
            currGood: {},
            tabLoading: false,
            goodsList: [],
            goodColumns: [
                {
                    title: '商品名称',
                    key: 'name',
                },
                {
                    title: '商品编码',
                    key: 'goodsNo',
                    align: 'center'
                },
                {
                    title: '生产企业',
                    key: 'fname',
                    align: 'center'
                },   
            ],
            lastTime: 0,
            totalCount: 0,
            currentPage: 1,
            pageSize: 30,
        }
    },
    watch: {
         currGood: function(data) {
             //this.$refs.carelist.init(data);
        }
    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            this.refreshGoodList();
        },
        changePage(data) {
            this.currentPage = data;
            this.refreshGoodList();
        },
        refreshGoodList() {
            let reqData = {
                search: this.searchValue,
                page: this.currentPage,
                size: this.pageSize
            };
            this.tabLoading = true;
            var self = this;
            util.ajax.get('/goods_care/list' ,{params: reqData})
                .then((response) => {
                    self.tabLoading = false;
                    self.goodsList = response.data.data;
                    self.totalCount = response.data.count;
                    
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
        chooseGood(data, index) {
            this.currGood = data;
            this.goodsId=this.currGood.id;
            this.$refs.carelist.goodsId =this.goodsId;
        },

    }
}
</script>

<style scoped>

</style>

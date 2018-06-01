<template>
    <div>
        <Row :gutter="16">
            <i-col span="8">
                <i-input v-model="goodsQuery" clearable
                       @on-enter="queryGoods"
                       @on-change="clearQueryInput"
                       placeholder="商品名称/拼音">
                    <span slot="append">
                        <Button type="primary" size="small" icon="ios-search" @click="searchGoods"></Button>
                    </span>
                </i-input>
            </i-col>
            <i-col span="5">
                <Select v-model="goodsTypeQuery" @on-change="onGoodsCategoryChange">
                    <Option v-for="item in goodsCategoryList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                </Select>
            </i-col>
        </Row>
        <Table ref="goodsSelect" size="small" border
               :columns="goodsColumns" :data="goodsList"
               :loading="goodsLoading"
               @on-row-click="chooseGoods"
                class="margin-top-10 goods-select-table">
        </Table>
        <Row class="margin-top-8">
            <div style="float: right;">
                <Page :total="totalGoodsCount" :current="currentPage" :page-size="pageSize" @on-change="changePage" size="small" show-total></Page>
            </div>
        </Row>
    </div>
</template>

<script>
    import util from '@/libs/util.js';
    import goodsSpecTags from '../goods/goods-spec-tabs.vue';

    export default {
        name: 'goodsListSelect',
        components: {
            goodsSpecTags
        },
        props: {
            warehouseId: {type:String|Number, default:''}, 
            disabled: Boolean|String, 
            size: String,
            options: {type:String, default: ''}
        },
        data () {
            return {
                currentPage: 1,
                pageSize: 10,
                totalGoodsCount: 0,
                goodsQuery: '',
                goodsTypeQuery: '',
                selectSize: this.size,
                goodsLoading: false,
                goodsList: [],
                goodsCategoryList: [],
                goodsColumns: [
                    {
                        type: 'index',
                        width: 60,
                        align: 'center'
                    },
                    {
                        key: 'name',
                        title: '商品名称',
                        minWidth: 170
                    },
                    {
                        key: 'categoryName',
                        title: '商品类型',
                        minWidth: 150
                    },
                    {
                        key: 'origin',
                        title: '产地',
                        width: 120
                    },
                    {
                        key: 'goodsSpecs',
                        title: '规格',
                        minWidth: 200,
                        render: (h, params) =>　{
                            return h(goodsSpecTags, {
                                props: {
                                    tags: params.row.goodsSpecs,
                                    color: 'blue'
                                }
                            });
                        }
                    },
                    {
                        key: 'supplierName',
                        title: '供应商',
                        minWidth: 150,
                    },
                    {
                        key: 'factoryName',
                        title: '生产企业',
                        minWidth: 150
                    },
                    {
                        key: 'unitName',
                        title: '单位',
                        width: 100
                    },
                    {
                        key: 'packUnitName',
                        title: '整件单位',
                        width: 100
                    },
                    {
                        key: 'packUnit',
                        title: '大件装量',
                        width: 100
                    },
                    {
                        key: 'inPrice',
                        title: '参考采购价',
                        minWidth: 110,
                        render: (h, params) => {
                            return h('span', { }, '￥' + (params.row.inPrice ? params.row.inPrice.toFixed(2): '0.00'));
                        }
                    },
                    {
                        key: 'currRepQuatity',
                        title: '当前库存',
                        minWidth: 110,
                    },
                    {
                        key: 'lastBuyPrice',
                        title: '最近采购价',
                        minWidth: 110,
                        render: (h, params) => {
                            return h('span', { },  params.row.lastBuyPrice ? '￥' + params.row.lastBuyPrice.toFixed(2): '');
                        }
                    },
                    {
                        title: '操作',
                        key: 'action',
                        width: 120,
                        render: (h, params) => {
                            return h('Button', {
                                props: {
                                    type: 'text',
                                    size: 'small'
                                }
                            }, '选择');
                        }
                    }
                ]
            };
        },
        methods: {
            reload() {
                this.currentPage = 1;
                this.totalGoodsCount = 0;
                this.queryGoods();
                this.queryGoodsCategoryList();
            },
            changePage(page) {
                this.currentPage = page;
                this.queryGoods();
            },
            queryGoods () {
                var self = this;
                let queryObj = {
                    page: this.currentPage,
                    size: this.pageSize
                };

                if (this.goodsQuery !== '') {
                    queryObj['search'] = this.goodsQuery;
                }
                if (this.goodsTypeQuery > 0) {
                    queryObj['catId'] = this.goodsTypeQuery;
                }
                // 最新库存, 最后采购价
                if (this.warehouseId) {
                    queryObj['options'] = this.options;
                    queryObj['warehouseId'] = this.warehouseId;
                }

                this.goodsLoading = true;
                util.ajax.get('/goods/list',
                        {params: queryObj})
                        .then(function (response) {
                            if (response.status === 200) {
                                self.goodsLoading = false;
                                self.totalGoodsCount = response.data.total;
                                self.goodsList = response.data.data;
                            }
                        })
                        .catch(function (error) {
                            self.goodsLoading = false;
                            util.errorProcessor(self, error);
                        });
            },
            onGoodsCategoryChange() {
                this.queryGoods();
            },
            queryGoodsCategoryList() {
                var self = this;
                util.ajax.get('/goods/category/list')
                        .then(function (response) {
                            self.goodsCategoryList = response.data;
                        })
                        .catch(function (error) {
                            util.errorProcessor(self, error);
                        });
            },
            searchGoods() {
                this.queryGoods();
            },
            clearSingleSelect() {
                this.$refs.goodsSelect.clearSingleSelect();
            },
            clearQueryInput(event) {
                if (event.target.value === '') {
                    this.queryGoods();
                }
            },
            chooseGoods(row, index) {
                this.$emit('on-choosed', row);
            },
            onChange (data) {
                let goodss = this.goodsList.filter(item => item.id === data);
                let goods = goodss[0] ? goodss[0] : '';
                this.$emit('input', data);
                this.$emit('on-change', data, goods);
            }
        }

    };
</script>
<style >
    .goods-select-table td {
        cursor: pointer;
    }
</style>

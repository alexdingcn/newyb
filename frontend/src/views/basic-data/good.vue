<style lang="less">
    @import '../../styles/common.less';
    @import './good.less';
</style>

<template>
    <div class="layout">
        <Layout :style="{minHeight: '100vh'}">
            <Sider collapsible :collapsed-width="150" v-model="isCollapsed" :style="{background: '#fff'}">
                <Card :bordered="false" dis-hover>
                    <p slot="title">
                        <Icon type="android-options"></Icon> 商品分类
                    </p>

                    <div class="good-category-con">
                        <ButtonGroup slot="extra">
                            <Button type="primary" icon="android-add-circle" size="small" @click="addCategory">添加</Button>
                            <Button type="success" icon="edit" size="small" @click="addCategory" :disabled="disableDelCategory"></Button>
                            <Button type="error" icon="android-remove-circle" size="small" @click="delCategory" :disabled="disableDelCategory"></Button>
                        </ButtonGroup>

                        <Tree :data="goodCat" @on-select-change="onTreeNodeSelected" ref="goodCatTree"></Tree>
                    </div>
                </Card>
            </Sider>
            <Layout>
                <Card class="margin-left-10">
                    <p slot="title">
                        <Icon type="ios-information"></Icon>
                        商品 {{ selectedCategory }}
                    </p>
                    <ButtonGroup slot="extra">
                    	<Button type="primary" icon="android-add-circle" @click="addGoods">添加</Button>
                        <Button type="error" icon="android-remove-circle" @click="delGoods">删除</Button>
                        <Button type="ghost" icon="ios-download-outline" @click="exportData">导出当前页数据</Button>
                    </ButtonGroup>

                    <Row type="flex" align="middle" class="advanced-router margin-top-8">
                        <Table border highlight-row :columns="orderColumns" :data="goodsData" ref="goodsTable" style="width: 100%;" size="small"></Table>
                    </Row>
                    <Row class="margin-top-8">
                        <div style="float: right;">
                            <Page :total="totalGoodsCount" :current="currentPage" @on-change="changePage" size="small" show-total></Page>
                        </div>
                    </Row>

                </Card>
            </Layout>
        </Layout>
    </div>
</template>

<script>
import axios from 'axios';
import util from '@/libs/util.js';

export default {
    name: 'access_index',
    data () {
        return {
            totalGoodsCount: 0,
            currentPage: 1,
            goodCat: [],
            selectedCategory: '',
            disableDelCategory: true,
            orderColumns: [
                {
                    type: 'selection',
                    width: 60,
                    align: 'center',
                    fixed: 'left'
                },
                {
                    type: 'index',
                    title: '',
                    width: 60,
                    fixed: 'left'
                },
                {
                    title: '商品名称',
                    key: 'name',
                    align: 'center',
                    width: 200,
                    sortable: true,
                    fixed: 'left',
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'text',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    let argu = { goods_id: params.row.id };
                                    this.$router.push({
                                        name: 'goods-info',
                                        params: argu
                                    });
                                }
                            }
                        }, params.row.name);
                    }
                },
                {
                    title: '产地',
                    key: 'origin',
                    width: 100,
                    align: 'center',
                    sortable: true
                },
                {
                    title: '剂型',
                    key: 'jx',
                    width: 80,
                    align: 'center',
                },
                {
                    title: '规格',
                    key: 'spec',
                    width: 200,
                    align: 'center',
                    sortable: true
                },
                {
                    title: '生产企业',
                    key: 'factory',
                    width: 200,
                    align: 'center'
                },
                {
                    title: '库存',
                    key: 'factory',
                    width:100,
                    align: 'center'
                },
                {
                    title: '在单数',
                    key: 'factory',
                    align: 'center'
                },
            ],
            goodsData: []
        };
    },
    mounted() {
        this.loadTree();
    },
    computed: {

    },
    methods: {
        loadTree () {
            var self = this;
            util.ajax.get('/good/category/tree')
                    .then(function (response) {
                        self.goodCat = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
        },
        loadGoodsData () {
            var self = this;
            util.ajax.get('/goods/list', { params : {
                page: this.currentPage
            }})
                .then(function (response) {
                    self.goodsData = response.data.data;
                    self.totalGoodsCount = response.data.total;
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        onTreeNodeSelected (items) {
            if (items && items.length > 0) {
                this.disableDelCategory = false;
                this.selectedCategory = items[0].title;
                this.loadGoodsData();
            } else {
                this.disableDelCategory = true;
            }
        },
        doAddCategory (newCategory) {
            var self = this;
            if (!newCategory) {
                this.$Message.warning("商品分类名称不能为空");
                return;
            }

            util.ajax.post('/good/category/add', { name: newCategory })
                .then(function (response) {
                    self.loadTree();
                })
                .catch(function (error) {
                    console.log(error);
                })
        },
        addCategory () {
            var inputVal = '';
            this.$Modal.confirm({
                onOk: () => {
                    this.doAddCategory(inputVal);
                },
                render: (h) => {
                    return h('Input', {
                        props: {
                            autofocus: true,
                            maxlength: 20,
                            placeholder: '请输入商品分类名称'
                        },
                        on: {
                            input: (val) => {
                                inputVal = val;
                            }
                        }
                    })
                }
            });
        },
        delCategory () {
            var self = this;
            util.ajax.post('/good/category/remove', { name: this.selectedCategory })
                    .then(function (response) {
                        self.loadTree();
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
        },
		exportData() {
			this.$refs.goodsTable.exportCsv({
                    filename: '原始数据'
                });
		},
        addGoods() {
            let argu = { goods_category: this.selectedCategory };
            this.$router.push({
                name: 'goods-info',
                params: argu
            });
        },
        delGoods() {
            var row = this.$refs.goodsTable.getSelection();
            if (row && row.length > 0) {
                var self = this;
                util.ajax.post('/goods/remove/' + row[0].id)
                        .then(function (response) {
                            self.loadTree();
                        })
                        .catch(function (error) {
                            console.log(error);
                        })
            } else {
                this.$Message.warning("请选择一个商品后操作");
            }
        },
        changePage(pageNumber) {
            this.currentPage = pageNumber;
            this.loadGoodsData();
        }
    }
};
</script>

<style>

</style>

<style lang="less">
    @import '../../styles/common.less';
    @import './good.less';
</style>

<template>
    <div class="layout">
        <Layout :style="{minHeight: '100vh'}">
            <Sider collapsible defaultCollapsed :collapsed-width="200" :style="{background: '#fff'}" v-show="sidebarVisible">
                <Card :bordered="false" dis-hover>
                    <p slot="title">
                        <Icon type="android-options"></Icon> 商品分类
                    </p>

                    <div class="good-category-con">
                        <ButtonGroup slot="extra">
                            <Button type="primary" icon="android-add-circle" size="small" @click="addCategory">添加</Button>
                            <Button type="success" icon="edit" size="small" @click="editCategory" :disabled="disableDelCategory"></Button>
                            <Button type="error" icon="android-remove-circle" size="small" @click="delCategory" :disabled="disableDelCategory"></Button>
                        </ButtonGroup>

                        <Tree :data="goodCat" @on-select-change="onTreeNodeSelected" ref="goodCatTree"></Tree>
                    </div>
                </Card>
            </Sider>
            <Layout>
                <Card class="margin-left-10">
                    <p slot="title">
                       <font-icon type="icon-shangpin" ></font-icon>
                       商品 {{ selectedCategory.title }}
                    </p>
                    <div slot="extra">
                        <Input v-model="searchGoodsVal" icon="search" placeholder="商品名称/拼音简称" style="width: 300px" @on-click="loadGoodsData"></Input>
                        <ButtonGroup >
                            <Button type="success" icon="android-add-circle" @click="addGoods">添加</Button>
                            <Button type="primary" icon="edit" @click="editGoods">修改</Button>
                            <Button type="error" icon="android-remove-circle" @click="delGoods">删除</Button>
                            <Button type="ghost" icon="ios-download-outline" @click="exportData">导出当前页数据</Button>
                        </ButtonGroup>
                    </div>

                    <Table border stripe highlight-row :loading="goodsTableLoading" 
                            :columns="orderColumns" :data="goodsData" ref="goodsTable" 
                            @on-row-click="goodsTabRowClick"
                            @on-row-dblclick="goodsTabRowDblClick" 
                            style="width: 100%;" size="small">
                    </Table>
                    <Row class="margin-top-8">
                        <div style="float: right;">
                            <Page :total="totalGoodsCount" :current="currentPage" @on-change="changePage" size="small" show-total></Page>
                        </div>
                    </Row>

                </Card>
            </Layout>
        </Layout>

        <Modal v-model="checkModalShow" :mask-closable="false" width="80" :footerHide="true" title="商品信息">
            <goods-info :goodsId="currentGoodsId" @save-ok="saveGoodsOk" ></goods-info>
        </Modal>

    </div>
</template>

<script>
import util from '@/libs/util.js';
import goodsInfo from './goods-info.vue';
import _ from 'lodash';

export default {
    name: 'goods',
    components: {
        goodsInfo
    },
    data () {
        return {
            sidebarVisible: true,
            searchFactoryId: 0,
            searchGoodsVal: '',
            totalGoodsCount: 0,
            currentPage: 1,
            goodCat: [],
            selectedCategory: {},
            disableDelCategory: true,
            goodsTableLoading: false,
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
                    title: '货号',
                    key: 'id',
                    width: 80,
                    fixed: 'left'
                },
                {
                    title: '商品名称',
                    key: 'name',
                    width: 220,
                    sortable: true,
                    fixed: 'left'
                },
                {
                    title: '类别',
                    key: 'categoryName',
                    width: 100
                },
                {
                    title: '产地',
                    width: 150,
                    key: 'origin'
                },
                {
                    title: '计量单位',
                    width: 90,
                    key: 'unitName'
                },
                {
                    title: '剂型',
                    width: 120,
                    key: 'jxName'
                },
                {
                    title: '规格',
                    key: 'spec',
                    sortable: true,
                    width: 120
                },
                {
                    title: '生产企业',
                    key: 'factoryName',
                    width: 160,
                },
                {
                    title: '是否可用',
                    key: 'enable',
                    width: 100,
                    render: (h, params) => {
                        return h('span', params.row.enable ? '是' : '否');
                    }
                },
                {
                    title: '是否代销',
                    key: 'proxy',
                    width: 100,
                    render: (h, params) => {
                        return h('span', params.row.proxy ? '是' : '否');
                    }
                },
                {
                    title: '重点养护',
                    key: 'needCare',
                    width: 100,
                    render: (h, params) => {
                        return h('span', params.row.needCare ? '是':'否');
                    }
                },
                {
                    title: '条形码',
                    width: 120,
                    key: 'serial'
                },
                {
                    title: '进项税%',
                    width: 100,
                    key: 'inTax'
                },
                {
                    title: '销项税%',
                    width: 100,
                    key: 'outTax'
                },
                {
                    title: '预警天数',
                    width: 100,
                    key: 'warningDays'
                },
                {
                    title: '效期(月)',
                    width: 100,
                    key: 'validMonths'
                },
                {
                    title: '批准文号',
                    width: 120,
                    key: 'permitNo'
                },
                {
                    title: '注册商标',
                    width: 120,
                    key: 'brandNo'
                },
                {
                    title: '注册证',
                    width: 120,
                    key: 'certNo'
                },
                {
                    title: '最低价',
                    width: 100,
                    key: 'lowPrice'
                },
                {
                    title: '最高价',
                    width: 100,
                    key: 'hightPrice'
                },
                {
                    title: '批发价',
                    width: 100,
                    key: 'batchPrice'
                }
            ],
            goodsData: [],
            checkModalShow: false,
            currentGoodsId: ''
        };
    },
    mounted () {
        this.loadTree();
        this.loadGoodsData();
    },
    methods: {
        loadTree () {
            var self = this;
            util.ajax.get('/good/category/tree')
                .then(function (response) {
                    self.goodCat = response.data;
                    self.selectedCategory = {};
                    self.disableDelCategory = true;
                })
                .catch(function (error) {
                    util.errorProcessor(self, error);
                });
        },
        loadGoodsData () {
            var self = this;
            this.goodsTableLoading = true;
            // read factory id
            if (this.$route.params && this.$route.params.factory_id) {
                this.searchFactoryId = this.$route.params.factory_id;
                this.sidebarVisible = false;
            }
            util.ajax.get('/goods/list', { params: {
                page: this.currentPage,
                search: this.searchGoodsVal,
                factoryId: this.searchFactoryId,
                catId: this.selectedCategory.id
            }})
                .then(function (response) {
                    self.goodsTableLoading = false;
                    self.goodsData = response.data.data;
                    self.totalGoodsCount = response.data.total;
                    self.currentGoodsId = '';
                    self.$refs.goodsTable.clearCurrentRow();
                })
                .catch(function (error) {
                    self.goodsTableLoading = false;
                    util.errorProcessor(self, error);
                });
        },
        onTreeNodeSelected (items) {
            if (items && items.length > 0) {
                this.disableDelCategory = false;
                this.selectedCategory = items[0];
                this.loadGoodsData();
            } else {
                this.disableDelCategory = true;
            }
        },
        
        doEditCategory(newCategory) {
            if (!this.selectedCategory.id || !newCategory) {
                this.$$Message.warning('获取数据失败');
                return;
            }
            let reqData = {
                id: this.selectedCategory.id,
                name: newCategory
            };
            let self = this;
            util.ajax.put('/good/category/edit', reqData)
                .then((response) => {
                    self.loadTree();
                })
                .catch((error) => {
                    util.errorProcessor(self, error);
                })
        },

        editCategory() {
            if(!this.selectedCategory || !this.selectedCategory.id) {
                this.$Message.warning('请先选择需要修改的产品组类别');
                return;
            }
            var inputVal = '';
            let self = this;
            this.$Modal.confirm({
                onOk: () => {
                    self.doEditCategory(inputVal);
                },
                render: (h) => {
                    return h('Input', {
                        props: {
                            autofocus: true,
                            maxlength: 20,
                            value: self.selectedCategory.title
                        },
                        on: {
                            input: (val) => {
                                inputVal = val;
                            }
                        }
                    });
                }
            });
        },

        doAddCategory (newCategory) {
            var self = this;
            if (!newCategory) {
                this.$Message.warning('商品分类名称不能为空');
                return;
            }
            util.ajax.post('/good/category/add', { name: newCategory })
                .then(function (response) {
                    self.loadTree();
                })
                .catch(function (error) {
                    util.errorProcessor(self, error);
                });
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
                    });
                }
            });
        },
        delCategory () {
            var self = this;
            util.ajax.post('/good/category/remove', { id: this.selectedCategory.id })
                .then(function (response) {
                    self.loadTree();
                })
                .catch(function (error) {
                    util.errorProcessor(self, error);
                });
        },
        exportData () {
            this.$refs.goodsTable.exportCsv({
                filename: '商品数据'
            });
        },
        addGoods () {
            this.currentGoodsId = '';
            this.checkModalShow = true;
        },
        editGoods () {
            if (!this.currentGoodsId) {
                this.$Message.warning('请先选择对应的商品信息');
                return;
            }
            this.checkModalShow = true;
        },
        saveGoodsOk() {
            this.loadGoodsData();
            this.checkModalShow = false;
        },
        delGoods () {
            var row = this.$refs.goodsTable.getSelection();
            if (row && row.length > 0) {
                var self = this;
                this.$Modal.confirm({
                    title: '商品删除确认',
                    content: '是否确认删除选择商品?',
                    onOk: () => {
                        util.ajax.post('/goods/remove/' + row[0].id)
                            .then(function (response) {
                                self.loadTree();
                            })
                            .catch(function (error) {
                                self.errorProcessor(self, error);
                            });
                    },
                    onCancel: () => {}
                });
            } else {
                this.$Message.warning('请选择一个商品后操作');
            }
        },
        changePage (pageNumber) {
            this.currentPage = pageNumber;
            this.loadGoodsData();
        },
        goodsTabRowClick(data) {
            this.currentGoodsId = data.id;
        },
        goodsTabRowDblClick(data) {
            this.currentGoodsId = data.id;
            this.editGoods();
        }
    },
    watch: {
        searchGoodsVal: _.debounce(function () {
            this.loadGoodsData();
        }, 1000)
    }
};
</script>

<style>

</style>

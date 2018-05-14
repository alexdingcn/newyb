<style lang="less">
    @import '../../styles/common.less';
    @import './good.less';
</style>

<template>
    <div class="layout">
        <Layout :style="{minHeight: '100vh'}">
            <Card :bordered="false" dis-hover>
                <p slot="title">
                    <Icon type="android-options"></Icon> 商品分类
                    <Tooltip placement="right-start">
                        <Icon type="ios-help-outline"></Icon>
                        <div slot="content">
                            <span>1、设置商品分类的类目内容，最多支持5级的商品分类</span><br/>
                            <span>2、客户通过商品分类快速查找商品</span><br/>
                            <span>3、您也通过商品分类来管理商品，统计不同商品分类下的销售情况，所以请合理设置商品分类的类目内容</span><br/>
                            <span>比如：一级：儿科用药   二级：咳嗽/消化类/维生素类</span>
                        </div>
                    </Tooltip>
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
        </Layout>

        <Modal v-model="checkModalShow" title="商品基础信息维护" :mask-closable="false" width="80">
            <goods-info :goodsId="currentGoodsId" @save-ok="saveGoodsOk" ></goods-info>
            <div slot="footer"></div>
        </Modal>

    </div>
</template>

<script>
import util from '@/libs/util.js';
import goodsInfo from './goods-info.vue';
import _ from 'lodash';

export default {
    name: 'goods-category',
    data () {
        return {
            sidebarVisible: true,
            totalGoodsCount: 0,
            currentPage: 1,
            goodCat: [],
            selectedCategory: {},
            disableDelCategory: true,
            checkModalShow: false,
            currentGoodsId: ''
        };
    },
    mounted () {
        this.loadTree();
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


        goodsTabRowClick(data) {
            this.currentGoodsId = data.id;
        },
        goodsTabRowDblClick(data) {
            this.currentGoodsId = data.id;
            this.editGoods();
        }
    }
};
</script>

<style>

</style>

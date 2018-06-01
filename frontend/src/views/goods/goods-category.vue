<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div >
        <Card :bordered="false" dis-hover>
            <p slot="title">
                <Icon type="android-options"></Icon> 商品分类
                <Tooltip transfer placement="right-start">
                    <Icon type="ios-help-outline"></Icon>
                    <div slot="content" >
                        <span>1、设置商品分类的类目内容，最多支持5级的商品分类</span><br/>
                        <span>2、客户通过商品分类快速查找商品</span><br/>
                        <span>3、您也通过商品分类来管理商品，统计不同商品分类下的销售情况，所以请合理设置商品分类的类目内容</span><br/>
                        <span>比如：一级：儿科用药   二级：咳嗽/消化类/维生素类</span>
                    </div>
                </Tooltip>
            </p>
            <div slot="extra">
                <ButtonGroup v-if="!isSider" >
                    <Button type="primary" icon="android-add-circle" @click="addCategory">新建</Button>
                    <Button type="success" icon="edit"  @click="editCategory" :disabled="disableDelCategory">修改</Button>
                    <Button type="error" icon="android-remove-circle" @click="delCategory" :loading="dataLoading" :disabled="disableDelCategory">删除</Button>
                    <Button type="ghost" icon="refresh" :loading="dataLoading" @click="loadTree" >刷新</Button>
                </ButtonGroup>
                <a v-else href="javascript:void(0)" @click="gotoGoodsCategory">去维护</a>
            </div>
            

            <div class="good-category-con">
                <Tree :data="goodCatTreeData" @on-select-change="onTreeNodeSelected" ref="goodCatTree"></Tree>
            </div>
        </Card>

        <Modal v-model="categoryModal" title="商品分类维护" :mask-closable="false" width="40">
            <Form ref="form" :model="formData" :label-width="90" :rules="formRules">
                <Row class="row-margin-bottom">
                    <FormItem label="分类名称" prop="name">
                        <Input type="text" placeholder="分类名称" v-model="formData.name" style="width: 75%;"/>
                    </FormItem>
                </Row>
                <Row class="row-margin-bottom">
                    <FormItem label="分类上级">
                        <Select v-model="formData.parent" filterable clearable placeholder="分类上级"  style="width: 75%;">
                            <Option v-for="item in goodsCatList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                        </Select>
                    </FormItem>
                </Row>
            </Form>
            <Row type="flex" justify="end" slot="footer" >
              <ButtonGroup>
                  <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveSubmit" >确定保存</Button>
                  <Button type="ghost" icon="reply" @click="modalCancel" >取消</Button>
              </ButtonGroup>
          </Row>
        </Modal>

    </div>
</template>

<script>
import util from '@/libs/util.js';
import dataConver from '@/libs/data-conver.js';
import canEditTableVue from '../tables/components/canEditTable.vue';

export default {
    name: 'goods-category',
    props: {
        isSider: {
            type: Boolean,
            default: false
        }
    },
    data () {
        return {
            dataLoading: false,
            goodCatTreeData: [],
            goodsCatList: [],
            selectedCategory: {},
            disableDelCategory: true,
            formData: {
                id: '',
                name: '',
                parent: -1,
            },
            formRules: {
                name: [
                    {required: true, message: '分类名称不能为空', trigger: 'blur'}
                ]
            },
            categoryModal: false,
            saveLoading: false,
        };
    },
    mounted () {
        this.loadTree();
    },
    methods: {
        loadTree () {
            var self = this;
            this.dataLoading = true;
            util.ajax.get('/goods/category/list')
                .then(function (response) {
                    self.dataLoading = false;
                    self.goodsCatList = response.data;
                    self.selectedCategory = {};
                    self.disableDelCategory = true;
                    let attr = {
                        rootId: -1,
                        idKey: 'id',
                        titleKey: 'name',
                        parentKey: 'parent',
                        expand: true
                    };
                    let allChild = dataConver.arryToIviewTreeData(self.goodsCatList, attr);
                    self.goodCatTreeData = [
                        {
                            id: -1,
                            title: '所有',
                            expand: attr.expand,
                            children: allChild
                        }
                    ];
                })
                .catch(function (error) {
                    self.dataLoading = false;
                    util.errorProcessor(self, error);
                });
        },

        onTreeNodeSelected (items) {
            if (items && items.length > 0) {
                this.selectedCategory = items[0];
                if (this.selectedCategory.id > 0) {
                    this.disableDelCategory = false;
                }
            } else {
                this.selectedCategory = {};
                this.disableDelCategory = true;
            }
            //发出一个选择的事件，并且返回ID
            this.$emit('on-choose', this.selectedCategory.id);
        },
        
        editCategory() {
            if (!this.selectedCategory.id || this.selectedCategory.id <= 0) {
                return;
            }
            let category = {};
            for(let i=0; i<this.goodsCatList.length; i++) {
                let item = this.goodsCatList[i];
                if (this.selectedCategory.id === item.id) {
                    category = item;
                    break;
                }
            }
            if (!category.id || category.id <= 0) {
                return;
            }
            this.formData = category;
            this.categoryModal = true;
        },
        addCategory () {
            this.categoryModal = true;
        },
        modalCancel() {
            this.formData = {
                id: '',
                name: '',
                parent: -1,
            };
            this.$refs.form.resetFields();
            this.categoryModal = false;
        },
        saveSubmit() {
            let self = this;
            self.saveLoading = true;
            util.ajax.post('/goods/category/save', self.formData)
                .then(function (response) {
                    self.saveLoading = false;
                    self.modalCancel();
                    self.loadTree();
                })
                .catch(function (error) {
                    self.saveLoading = false;
                    util.errorProcessor(self, error);
                })
        },
        delCategory () {
            var self = this;
            console.log(self.selectedCategory);
            this.$Modal.confirm({
                title: '删除确认',
                content: '是否确认删除商品分类：' + self.selectedCategory.title + '？',
                onCancel: () => {},
                onOk: () => {
                    self.dataLoading = true;
                    util.ajax.delete('/goods/category/remove/' + self.selectedCategory.id)
                        .then(function (response) {
                            self.dataLoading = false;
                            self.$Message.success('删除成功');
                            self.loadTree();
                        })
                        .catch(function (error) {
                            self.dataLoading = false;
                            util.errorProcessor(self, error);
                        });
                }
            });
        },

        gotoGoodsCategory() {
            this.$router.push({
                name: 'goods-category'
            });
        }
    }
};
</script>

<style >

</style>

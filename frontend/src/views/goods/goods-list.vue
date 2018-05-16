<style lang="less">
    @import '../../styles/common.less';
    @import './goods-list.less';
</style>

<template>
  <div class="goods-list">
          <div class="list-sider" :style="{width: showSider ? '200px' : '0px'}">
            <goods-category v-show="showSider" @on-choose="categoryChoose" ></goods-category>
          </div>
          <div class="list-body" :style="{left: showSider ? '200px': '0px'}">
              <Card>
                    <p slot="title">
                        <a href="javascript:void(0)" @click="changeSiderShow" style="margin-right: 5px;" >
                            <Icon v-if="showSider" type="chevron-left"></Icon>
                            <Icon v-else type="chevron-right"></Icon>
                        </a>
                        <Icon type="bag"></Icon>
                        商品列表
                    </p>
                    <Row class="row-margin-bottom">
                        <i-col span="19">
                            <Row type="flex" justify="start">
                                <Input type="text" v-model="searchValue" placeholder="商品名称/拼音/编号" icon="search" @on-click="refreshGoodsList" style="width: 250px"/>
                                <goods-brand-select v-model="searchGoodsBrandId" @on-change="refreshGoodsList" style="width: 150px; margin-left:10px; margin-right:10px;"></goods-brand-select>
                                <Select v-model="searchEnable" placeholder="状态"  @on-change="refreshGoodsList" style="width: 90px; margin-right:10px;">
                                    <Option v-for="item in enableList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                                </Select>
                                <supplier-select v-model="searchSupplierId"  @on-change="refreshGoodsList" style="width: 230px"></supplier-select>
                            </Row>
                        </i-col>
                        <i-col span="5">
                            <Row type="flex" justify="end">
                                <Button type="primary" icon="plus" @click="createGoods">新增商品</Button>
                            </Row>
                        </i-col>
                    </Row>

                    <Table stripe highlight-row :loading="tableLoading" 
                            :columns="tableCulumns" :data="tableData" ref="goodsTable" 
                            style="width: 100%;" size="small">
                    </Table>
                    <Row type="flex" justify="end">
                        <Page :total="totalCount" :current="currentPage" @on-change="changePage" size="small" show-total></Page>
                    </Row>

                </Card>
          </div>

          <Modal v-model="goodsModal" title="商品信息维护" :footerHide="true" :mask-closable="false" width="60">
              <goods-info :goodsInfoId="editId" @save-ok="goodsSaveOk" ></goods-info>
          </Modal>
          
  </div>
</template>

<script>
import util from '@/libs/util.js';
import lodash from 'lodash';
import goodsCategory from './goods-category.vue';
import goodsBrandSelect from '@/views/selector/goods-brand-select.vue';
import supplierSelect from '@/views/selector/supplier-select.vue';
import goodsInfo from './goods-info.vue';
import factoryVue from '../basic-data/factory.vue';

export default {
    name: 'goods-list',
    components: {
        goodsCategory,
        goodsBrandSelect,
        supplierSelect,
        goodsInfo,
    },
    data() {
        const actionButton = (h, icon, color, clickCall, data) => {
            return h('Button', {
                    props: {
                        type: 'text',
                        size: 'small',
                        icon: icon
                    },
                    on: {
                        click: (data) => {clickCall(data)}
                    }
                }, '');
        }
        return {
            enableList: [{value:'ALL', label:'全部'}, {value: 1, label: '启用'}, {value: 0, label: '禁用'}],
            showSider: false,
            searchCategoryId: '',
            searchValue: '',
            searchGoodsBrandId: '',
            searchSupplierId: '',
            searchEnable: '',
            goodsModal: false,
            tableLoading: false,
            tableData:[],
            tableCulumns: [
                {
                    title: '',
                    type: 'selection',
                    width: 60
                },
                {
                    title: '商品名称',
                    type: 'name',
                    minWidth: 200,
                    render: (h, params) => {
                        return h('div',[
                            h('h5', {
                                style: {
                                    color: '#9ea7b4',
                                    fontSize: '12px'
                                }
                            }, params.row.goodsNo),
                            h('h4', params.row.name)
                        ]);
                    }
                },
                {
                    title: '多规格',
                    key: 'specCount',
                    width: 90,
                    render: (h, params) => {
                        let useSpec = params.row.useSpec;
                        if (!useSpec) {
                            return '';
                        }else {
                            return h('span', params.row.detailsSize + '种');
                        }
                    }
                },
                {
                    title: '单位',
                    key: 'unitName',
                    width: 100
                },
                {
                    title: '是否可用',
                    key: 'enable',
                    minWidth: 120,
                    render: (h, params) => {
                        let label = params.row.enable ? '启用' : '禁用';
                        let color = params.row.enable ? 'green' : 'red';
                        return h('Tag', {
                            props:{
                                type: 'dot',
                                color: color
                            }
                        }, label);
                    }
                },
                {
                    title: '批发价',
                    key: 'batchPrice',
                    width: 120
                },
                {
                    title: '市场价',
                    key: 'retailPrice',
                    width: 120
                },
                {
                    title: '参考进货价',
                    key: 'inPrice',
                    width: 120
                },
                {
                    title: '供应商',
                    key: 'supplierName',
                    minWidth: 180
                },
                {
                    title: '分类/品牌',
                    key: 'brandName',
                    minWidth: 150,
                    render: (h, params) => {
                        let categoryName = params.row.categoryName;
                        let brandName = params.row.brandName;
                        return h('div',[
                            h('h5', categoryName),
                            h('h5', brandName)
                        ]);
                    }
                },
                {
                    title: '操作',
                    key: 'action',
                    width: 120,
                    render: (h, params) => {
                        let self = this;
                        return h('div', [
                            h('Button', {
                                props: {
                                    type: 'text',
                                    size: 'small',
                                    icon: 'edit'
                                },
                                on: {
                                    click: (data) => {self.updateGoods(params.row.id)}
                                }
                            }, ''),
                            h('Button', {
                                props: {
                                    type: 'text',
                                    size: 'small',
                                    icon: 'ios-copy'
                                },
                                on: {
                                    click: (data) => {self.copyGoods(params.row)}
                                }
                            }, ''),
                            h('Button', {
                                props: {
                                    type: 'text',
                                    size: 'small',
                                    icon: 'ios-trash'
                                },
                                on: {
                                    click: (data) => {self.removeGoods(params.row)}
                                }
                            }, '')
                        ]);
                    }
                }
            ],
            totalCount: 0,
            currentPage: 1,
            pageSize: 30,
            editId: ''
        }
    },
    mounted() {
      this.refreshGoodsList();
    },
    watch: {
        searchCategoryId: function(data) {
            this.refreshGoodsList();
        },
        searchValue: function(data) {
            lodash.debounce(function() {
                this.refreshGoodsList();
            }, 700);
        }
    },
    methods: {
        changeSiderShow() {
            this.showSider = !this.showSider;
        },
        categoryChoose(categoryId) {
            this.searchCategoryId = categoryId;
        },
        refreshGoodsList() {
            let reqData = {
                categoryId: this.searchCategoryId,
                brandId: this.searchGoodsBrandId,
                supplierId: this.searchSupplierId,
                enable: this.searchEnable === 'ALL' ? '' : this.searchEnable,
                search: this.searchValue,
                page: this.currentPage,
                pageSize: this.pageSize
            };
            this.tableLoading = true;
            util.ajax.post('/goods/list', reqData)
                .then((response) => {
                    this.tableLoading = false;
                    this.tableData = response.data.data;
                    this.totalCount = response.data.count;
                })
                .catch((error) => {
                    this.tableLoading = false;
                    util.errorProcessor(this, error);
                });
        },
        changePage(data) {
            this.currentPage = data;
            this.refreshGoodsList();
        },
        createGoods() {
            this.goodsModal = true;
        },
        goodsSaveOk() {
            this.refreshGoodsList();
            this.goodsModal = false;
            
        },

        updateGoods(id) {
            this.editId = ''; //重置，可以导致再次点击时能刷新数据
            if(!id) {
                return;
            }
            this.editId = id;
            this.goodsModal = true;
        },
        copyGoods(row) {
            if (!row.id) {
                return;
            }
            let self  = this;
            this.$Modal.confirm({
                title: '商品复制确认',
                content: '是否确认以商品：' + row.name + '为模板复制多一个产品？',
                onOk: () => {
                    self.tableLoading = true;
                    util.ajax.put('/goods/copy/' + row.id)
                        .then((response) => {
                            self.tableLoading = false;
                            self.$Message.success('商品复制成功');
                            self.refreshGoodsList();
                        })
                        .catch((error) => {
                            self.tableLoading = false;
                            util.errorProcessor(self, error);
                        });
                }
            })
        },
        removeGoods(row) {
            if (!row.id) {
                return;
            }
            let self = this;
            this.$Modal.confirm({
                title: '删除商品确认',
                content: '是否确认删除商品：' + row.name,
                onOk: () => {
                    self.tableLoading = true;
                    util.ajax.delete('/goods/remove/' + row.id)
                        .then((response) => {
                            self.tableLoading = false;
                            self.$Message.success('商品删除成功');
                            self.refreshGoodsList();
                        })
                        .catch((error) => {
                            self.tableLoading = false;
                            util.errorProcessor(self, error);
                        });
                }
            })
        }

    }

  
}
</script>


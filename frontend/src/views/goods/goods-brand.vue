<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="aperture"></Icon>
              商品品牌
          </p>
          <Row>
              <i-col span="12">
                  <Row type="flex" justify="start">
                      <Input type="text" v-model="searchValue" placeholder="品牌名称/拼音/编号" @on-clik="refreshGoodsBrands" icon="search" style="width: 250px" />
                  </Row>
              </i-col>
              <i-col span="12">
                  <Row type="flex" justify="end">
                      <ButtonGroup size="small">
                          <Button type="primary" icon="plus" @click="addBrand">新增品牌</Button>
                      </ButtonGroup>
                  </Row>
              </i-col>
          </Row>
              
          <Table ref="table" highlight-row :loading="tableLoading" 
                :columns="tableColumns" :data="tableData" 
                style="width: 100%;" size="small" 
                no-data-text="点击上方“新增品牌”按钮进行新建"
          ></Table>
          <Row type="flex" justify="end">
              <Page :total="totalCount" :current="currentPage" @on-change="changePage" size="small" show-total></Page>
          </Row>
      </Card>

      <Modal v-model="brandModal" title="商品品牌信息维护" :mask-closable="false" width="40">
          <Form ref="form" :model="formData" :label-width="90" :rules="formRules">
              <Row class="row-margin-bottom">
                  <FormItem label="品牌名称" prop="brandName">
                      <Input type="text" placeholder="品牌名称" v-model="formData.brandName" @on-blur="onChangeName" style="width: 75%;"/>
                  </FormItem>
              </Row>
              <Row class="row-margin-bottom">
                  <FormItem label="品牌编号">
                      <Input type="text" placeholder="品牌编号" v-model="formData.brandNo" style="width: 75%" />
                  </FormItem>
              </Row>
              <Row class="row-margin-bottom">
                  <FormItem label="拼音">
                      <Input type="text" placeholder="pinyin" v-model="formData.pinyin" style="width: 75%" />
                  </FormItem>
              </Row>
              <Row class="row-margin-bottom">
                  <FormItem label="排序号">
                      <InputNumber :min="1" number placeholder="排序号,值越大排在越前面" v-model="formData.sortNo" style="width: 75%" />
                  </FormItem>
              </Row>
              <Row class="row-margin-bottom">
                  <FormItem label="是否可用">
                      <Checkbox v-model="formData.enabled"></Checkbox>
                  </FormItem>
              </Row>
          </Form>
          <Row type="flex" justify="end" slot="footer" >
              <ButtonGroup size="small">
                  <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveSubmit" >确定保存</Button>
                  <Button type="ghost" icon="reply" @click="modalCancel" >取消</Button>
              </ButtonGroup>
          </Row>
      </Modal>
  </div>
</template>

<script>
import util from '@/libs/util.js';
import lodash from 'lodash';

export default {
    name: 'goods-brand',
    data() {
        return {
            tableLoading: false,
            tableData: [],
            tableColumns: [
                {
                    title: '',
                    type: 'index',
                    width: 60
                },
                {
                    title: '品牌名称',
                    key: 'brandName',
                    minWidth: 250 
                },
                {
                    title: '品牌编号',
                    key: 'brandNo',
                    minWidth: 150
                },
                {
                    title: '拼音',
                    key: 'pinyin',
                    minWidth: 200,
                },
                {
                    title: '是否可用',
                    key: 'enabled',
                    minWidth: 150,
                    render: (h, params) => {
                        let label = params.row.enabled ? '可用' : '禁用';
                        let color = params.row.enabled ? 'green' : 'red';
                        return h('Tag', {
                            props: {
                                type: 'dot',
                                color: color
                            }
                        }, label);
                    }
                },
                {
                    title: '排序值',
                    key: 'sortNo',
                    minWidth: 150
                },
                {
                    title: '操作',
                    key: 'action',
                    width: 150,
                    render: (h, params) => {
                        return h('ButtonGroup', {
                                props: {
                                    size: 'small'
                                }
                            }, [
                                h('Button', {
                                    props: {
                                        type: 'primary',
                                        icon: "edit"
                                    },
                                    on: {
                                        click: () => {
                                            this.edit(params.row);
                                        }
                                    }
                                }, '修改'),
                                h('Button', {
                                    props: {
                                        type: 'error',
                                        icon: 'close'
                                    },
                                    on: {
                                        click: () => {
                                            this.removeItem(params.row);
                                        }
                                    }
                                }, '删除')
                            ]);
                    }
                }
            ],
            totalCount: 0,
            currentPage: 1,
            pageSize: 50,
            searchValue: '',
            brandModal: false,
            formData: {
                id: '',
                brandName: '',
                brandNo: '',
                pinyin: '',
                sortNo: 500,
                enabled: true
            },
            formRules: {
                brandName: [
                    {required: true, message: '品牌名称不能为空', trigger: 'blur'}
                ]
            },
            saveLoading: false,
        }
    },
    watch: {
        searchValue: lodash.debounce(function () {
            this.refreshGoodsBrands();
        }, 500)
    },
    mounted() {
        this.refreshGoodsBrands();
    },
    methods: {
        refreshGoodsBrands() {
            let reqData = {
                search: this.searchValue,
                page: this.currentPage,
                pageSize: this.pageSize
            };
            this.tableLoading = true;
            util.ajax.post('/goods/brand/list', reqData)
                .then(response => {
                    this.tableLoading = false;
                    this.tableData = response.data.data;
                    this.totalCount = response.data.count;
                })
                .catch(error => {
                    this.tableLoading = false;
                    util.errorProcessor(this, error);
                })
        },
        changePage(data) {
            this.currentPage = data;
            this.refreshGoodsBrands();
        },

        onChangeName () {
            if (this.formData.brandName && this.formData.brandName.length > 0) {
                var self = this;
                util.ajax.post('/util/pinyinAbbr', { name: this.formData.brandName })
                    .then(function (response) {
                        self.formData.pinyin = response.data;
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            }
        },

        addBrand() {
            this.brandModal = true;
        },
        edit(row) {
            this.formData = row;
            this.brandModal = true;
        },
        modalCancel() {
            this.formData = {
                id: '',
                brandName: '',
                brandNo: '',
                pinyin: '',
                sortNo: 500,
                enabled: true
            };
            this.$refs.form.resetFields();
            this.brandModal = false;
        },
        saveSubmit() {
            let self = this;
            this.$refs.form.validate(validate => {
                if(!validate) {
                    return;
                }
                self.saveLoading = true;
                util.ajax.post('/goods/brand/save', self.formData)
                    .then(response => {
                        self.saveLoading = false;
                        self.$Message.success('保存成功');
                        self.modalCancel();
                        self.refreshGoodsBrands();
                    })
                    .catch(error => {
                        self.saveLoading = false;
                        util.errorProcessor(self, error);
                    })
            });
        },
        removeItem(row) {
            if(!row.id) {
                return;
            }
            let self = this;
            this.$Modal.confirm({
                title: '删除确认',
                content: '是否确认删除' + row.brandName + '？',
                onCancel: () => {},
                onOk: () => {
                    self.tableLoading = true;
                    util.ajax.delete('/goods/brand/delete/' + row.id)
                        .then(response => {
                            self.tableLoading = false;
                            self.$Message.success('删除成功');
                            self.refreshGoodsBrands();
                        })
                        .catch(error => {
                            self.tableLoading = false;
                            util.errorProcessor(self, error);
                        })
                }
            });
        }
    }
  
}
</script>


<style >

</style>


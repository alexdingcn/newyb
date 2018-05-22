<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="network"></Icon>
              商品多规格设置
            </p>
          <div slot="extra" >
              <ButtonGroup>
                  <Button type="success" icon="plus" @click="add" >新增多规格</Button>
              </ButtonGroup>
          </div>
          <Table ref="table" highlight-row :loading="tableLoading" 
                :columns="tableColumns" :data="tableData" 
                style="width: 100%;" size="small" 
                no-data-text="点击上方“新增多规格”按钮进行新建"
          ></Table>
      </Card>

      <Modal v-model="specModal" title="商品多规格维护" :mask-closable="false" width="40">
          <Alert>
              规格编号设置建议：在类型的规格编号中，建议使用两位字符(数字或字母),在多规格值中的每一个值的规格编号建议使用类型编号+1(2,3)的模式.
              <h5>例如: 规格“颜色”的编号为“A1”, 则颜色规格值的规格编号设置如下模式</h5>
              <ul>
                  <li>“白色”：“A11”</li>
                  <li>“红色”：“A12”</li>
                  <li>“蓝色”：“A13”</li>
              </ul>
          </Alert>
          <Form ref="form" :model="specForm" :label-width="90">
              <h3>多规格类型</h3>
              <Row>
                  <FormItem label="多规格名称">
                      <Input type="text" style="width: 250px" v-model="specForm.parentName" />
                  </FormItem>
              </Row>
              <Row>
                  <FormItem label="多规格编号">
                      <Input type="text" style="width: 250px" v-model="specForm.parentNo" />
                  </FormItem>
              </Row>
              <h3>多规格值</h3>
              <Table ref="modalTable" highlight-row :loading="tableLoading" 
                :columns="modalColumns" :data="specForm.subGoodsSpecs" 
                size="small" style="width:100%;"
               ></Table>
               <Row type="flex" justify="start">
                   <a href="javascript:void(0)" style="margin-top:10px;" @click="addSpecName"><Icon type="plus"></Icon>继续添加</a>
               </Row>
          </Form>
          <Row type="flex" justify="end" slot="footer" >
              <ButtonGroup size="small">
                  <Button type="success" icon="checkmark" :loading="saveLoading" @click="specModalSaveSubmit" >确定保存</Button>
                  <Button type="ghost" icon="reply" @click="specModalCancel" >取消</Button>
              </ButtonGroup>
          </Row>
      </Modal>
  </div>
</template>

<script>
import util from '@/libs/util.js';
import goodsSpecTags from './goods-spec-tabs.vue';

export default {
    name: 'goods-spec',
    components: {
        goodsSpecTags
    },
    data() {
        return {
            tableLoading: false,
            tableData: [],
            tableColumns: [
                {
                    title: '#',
                    type: 'index',
                    width: 60
                },
                {
                    title: '规格编号',
                    key: 'parentNo',
                    minWidth: 150
                },
                {
                    title: '规格名称',
                    key: 'parentName',
                    minWidth: 150,
                },
                {
                    title: '规格可选值',
                    key: 'subGoodsSpecs',
                    minWidth: 200,
                    render: (h, params) =>　{
                        return h(goodsSpecTags, {
                                props: {
                                    tags: params.row.subGoodsSpecs,
                                    color: 'blue'
                                }
                            });
                    }
                },
                {
                    title: '操作',
                    key: 'action',
                    width: 150,
                    render: (h, params) =>　{
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
                                        this.removeItem(params.row.parentId);
                                    }
                                }
                            }, '删除')
                        ]);
                    }
                }
            ],
            tableLoading: false,
            specModal: false,
            specForm: {
                parentId: '',
                parentName: '',
                parentNo: '',
                subGoodsSpecs: []
            },
            isAddModal: true,
            modalColumns: [
                {
                    title: '',
                    type: 'index',
                    width: 60
                },
                {
                    title: '规格值',
                    key: 'specName',
                    render: (h, params) => {
                        let self= this;
                        return h('Input', {
                            props: {
                                value: self.specForm.subGoodsSpecs[params.index][params.column.key]
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.specForm.subGoodsSpecs[params.index];
                                    row[params.column.key] = event.target.value;
                                }
                            }
                        });
                    }
                },
                {
                    title: '规格编号',
                    key: 'specNo',
                    render: (h, params) => {
                        let self= this;
                        return h('Input', {
                            props: {
                                value: self.specForm.subGoodsSpecs[params.index][params.column.key]
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.specForm.subGoodsSpecs[params.index];
                                    row[params.column.key] = event.target.value;
                                }
                            }
                        });
                    }
                },
                {
                    title: '操作',
                    key: 'action',
                    width: 120,
                    render: (h, params) => {
                        return h('ButtonGroup', {
                                props: {
                                    size: 'small'
                                }
                            }, [
                                h('Button', {
                                    props: {
                                        type: 'error',
                                        icon: 'close'
                                    },
                                    on: {
                                        click: () => {
                                            this.removeSubItem(params.row.id, params.index);
                                        }
                                    }
                                }, '删除')
                            ]);
                    }
                }
            ],
            saveLoading: false
        }
    },
    mounted() {
        this.refreshTableData();
    },
    methods: {
        refreshTableData() {
            this.tableLoading = true;
            util.ajax.get('/goods/spec/list')
                .then((response) => {
                    console.log(response);
                    this.tableLoading = false;
                    this.tableData = response.data;
                })
                .catch((error) => {
                    this.tableLoading = false;
                    util.errorProcessor(this, error);
                })
        },

        removeItem(id) {
            if (!id) {
                return;
            }
            let self = this;
            this.$Modal.confirm({
                title: '删除确认',
                content: '是否确认删除商品多规格',
                onCancel: () => {},
                onOk: () => {
                    self.tableLoading = true;
                    util.ajax.delete('/goods/spec/remove/' + id)
                        .then((response) => {
                            self.tableLoading = false;
                            self.$Message.success('删除成功');
                            self.refreshTableData();
                        })
                        .catch((error) => {
                            self.tableLoading = false;
                            util.errorProcessor(self, error);
                        })
                }
            });
        },

        add() {
            this.specForm = {
                parentId: '',
                parentName: '',
                parentNo: '',
                subGoodsSpecs: [
                    {id: '', specNo: '', specName: ''},
                    {id: '', specNo: '', specName: ''}
                ]
            };
            this.isAddModal = true;
            this.specModal = true;
        },

        addSpecName() {
            let item = {id: '', specNo: '', specName: ''};
            this.specForm.subGoodsSpecs.push(item);
        },

        removeSubItem(id, index) {
            if (id && id>0) {
                // 删除后台数据
                let self = this;
                util.ajax.delete('/goods/spec/remove/' + id)
                    .then(response => {
                        self.$Message.success('删除成功');
                        self.specForm.subGoodsSpecs.splice(index, 1);
                        self.refreshTableData();
                    })
                    .catch(error => {
                        util.errorProcessor(self, error);
                    })
            }else {
                this.specForm.subGoodsSpecs.splice(index, 1);
            }
        },

        specModalCancel() {
            this.specForm = {
                parentId: '',
                parentName: '',
                parentNo: '',
                subGoodsSpecs: [
                    {id: '', specNo: '', specName: ''},
                    {id: '', specNo: '', specName: ''}
                ]
            };
            this.isAddModal = true;
            this.specModal = false;
        },

        edit(row) {
            this.specForm = row;
            this.isAddModal = false;
            this.specModal = true;
        },

        specModalSaveSubmit() {
            this.saveLoading = true;
            util.ajax.post('/goods/spec/save', this.specForm)
                .then((response) => {
                    this.saveLoading = false;
                    this.$Message.success('保存成功');
                    this.specModalCancel();
                    this.refreshTableData();
                })
                .catch((error) => {
                    this.saveLoading = false;
                    util.errorProcessor(this, error);
                })
        }

    }
  
}
</script>


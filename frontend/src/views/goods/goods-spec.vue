<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
  <div>
      <Card>
          <p slot="title">商品多规格设置</p>
          <div slot="extra" >
              <ButtonGroup size="small">
                  <Button type="success" icon="plus" @click="add" >新增多规格</Button>
              </ButtonGroup>
          </div>
          <Table ref="table" highlight-row :loading="tableLoading" 
                :columns="tableColumns" :data="tableData" 
                style="width: 100%;" size="small" 
          ></Table>
      </Card>

      <Modal v-model="specModal" title="商品基础信息维护" :mask-closable="false" width="40">
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
                size="small" style="width:625px;"
               ></Table>
               <Row type="flex" justify="start">
                   <a href="javascript:void(0)" style="margin-top:10px;" @click="addSpecValue"><Icon type="plus"></Icon>继续添加</a>
               </Row>
          </Form>
          <Row type="flex" justify="end" slot="footer" >
              <ButtonGroup size="small">
                  <Button type="success" icon="checkmark" >确定保存</Button>
                  <Button type="ghost" icon="reply" @click="specModalCancel" >取消</Button>
              </ButtonGroup>
          </Row>
      </Modal>
  </div>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'goods-spec',
    data() {
        const remove = function(h, rows) {
            
        }

        return {
            tableLoading: false,
            tableData: [],
            tableColumns: [
                {
                    title: '序号',
                    type: 'index',
                    width: 60
                },
                {
                    title: '多规格编号',
                    key: 'parentNo',
                    width: 100
                },
                {
                    title: '多规格名称',
                    key: 'parentName',
                    width: 150,
                },
                {
                    title: '多规格值',
                    key: 'specValusList',
                },
                {
                    title: '操作',
                    key: 'action',
                    width: 130,
                    render: (h, params) =>　{
                        return h('ButtonGroup', {
                            props: {
                                size: 'small'
                            }
                        }, [
                            h('Button', {
                                props: {
                                    type: 'warning',
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
                    title: '多规格值',
                    key: 'specValue',
                    width: 220,
                    render: (h, params) => {
                        let self= this;
                        return h('Input', {
                            props: {
                                value: self.specForm.subGoodsSpecs[params.index][params.column.key]
                            },
                            on: {
                                "on-change"(event) {
                                    let row = self.specForm.subGoodsSpecs[params.index];
                                    row[params.column.key] = event.target.value;
                                }
                            }
                        });
                    }
                },
                {
                    title: '多规格编号',
                    key: 'specNo',
                    width: 220,
                    render: (h, params) => {
                        let self= this;
                        return h('Input', {
                            props: {
                                value: self.specForm.subGoodsSpecs[params.index][params.column.key]
                            },
                            on: {
                                "on-change"(event) {
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
                                            this.removeValueItem(params.row.id, params.index);
                                        }
                                    }
                                }, '删除')
                            ]);
                    }
                }
            ]
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
                    this.tableLoading = false;
                    this.tableData = response.data;
                })
                .catch((error) => {
                    this.tableLoading = false;
                    util.errorProcessor(this, error);
                })
        },

        removeItem(parentId) {
            if (!parentId) {
                return;
            }
            let self = this;
            this.$Model.confirm({
                title: '删除确认',
                content: '是否确认删除商品多规格',
                onCancel: () => {},
                onOk: () => {
                    self.tableLoading = true;
                    util.ajax.delete('/goods/spec/remove/' + parentId)
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
                    {id: '', specNo: '', specValue: ''},
                    {id: '', specNo: '', specValue: ''}
                ]
            };
            this.isAddModal = true;
            this.specModal = true;
        },

        addSpecValue() {
            let item = {id: '', specNo: '', specValue: ''};
            this.specForm.subGoodsSpecs.push(item);
        },

        removeValueItem(id, index) {
            this.specForm.subGoodsSpecs.splice(index, 1);
            if (id && id>0) {
                // 删除后天数据
            }
        },

        specModalCancel() {
            this.specForm = {
                parentId: '',
                parentName: '',
                parentNo: '',
                subGoodsSpecs: [
                    {id: '', specNo: '', specValue: ''},
                    {id: '', specNo: '', specValue: ''}
                ]
            };
            this.isAddModal = true;
            this.specModal = false;
        },

        edit(row) {
            this.specForm = row;
            this.isAddModal = false;
            this.specModal = true;
        }

    }
  
}
</script>


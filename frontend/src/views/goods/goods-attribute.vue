<style lang="less">
       @import '../../styles/common.less';
</style>

<template>
  <div>
      <Card style="width: 60%;">
          <p slot="title">
              <Icon type="ios-pricetags"></Icon>
              商品自定义属性
          </p>
          <div slot="extra">
              <ButtonGroup size="small">
                  <Button type="primary" icon="plus" @click="add" >新增自定义属性</Button>
              </ButtonGroup>
          </div>

           <Alert type="success" class="row-margin-bottom" >
                自定义字段设置说明：用于补充默认商品字段中不足的地方，设置后，可在“商品详情页”增加一个自定义文本字段进行描述，
                <p>如：医药类产品的“主治功能”，设置后，可在每个“商品详情页”的自定义字段增加一个自定义的“主治功能”的字段。</p>
            </Alert>
            <Table ref="table" highlight-row :loading="tableLoading" 
                :columns="tableColumns" :data="tableData" 
                style="width: 100%;" size="small" 
                no-data-text="点击上方“新增自定义属性”按钮进行新建"
            ></Table>
      </Card>

      <Modal v-model="modal" title="商品自定义属性维护" :mask-closable="false" width="30">
          <Form ref="form" :model="attForm" :label-width="90">
              <Row>
                  <FormItem label="属性名称" error="attNameError">
                      <Input type="text" style="width: 200px" v-model="attForm.attName" />
                  </FormItem>
              </Row>
              <Row>
                  <FormItem label="是否默认" prop="enable">
                    <RadioGroup v-model="attForm.enableDefault">
                        <Radio :label="true">
                            <span>是</span>
                        </Radio>
                        <Radio :label="false">
                            <span>否</span>
                        </Radio>
                    </RadioGroup>
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
import util from "@/libs/util.js";

export default {
  name: "goods-attribute",
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
                  title: '系统编号',
                  key: 'id',
                  width: 150,
        },
        {
                  title: '自定义属性名称',
                  key: 'attName'
        },
        {
          title: "是否默认",
          key: "enableDefault",
          render: (h, params) => { 
            const row = params.row;
            const text = row.enableDefault === true ? '是' : '';           
                        return h('span',text);
                    }
        },
        {
          title: "操作",
          key: "action",
          width: 150,
          render: (h, params) => {
            return h(
              "ButtonGroup",
              {
                props: {
                  size: "small"
                }
              },
              [
                h(
                  "Button",
                  {
                    props: {
                      type: "primary",
                      icon: "edit"
                    },
                    on: {
                      click: () => {
                        this.edit(params.row);
                      }
                    }
                  },
                  "修改"
                ),
                h(
                  "Button",
                  {
                    props: {
                      type: "error",
                      icon: "close"
                    },
                    on: {
                      click: () => {
                        this.removeItem(params.row.id);
                      }
                    }
                  },
                  "删除"
                )
              ]
            );
          }
        }
      ],
      modal: false,
      attForm: {
        id: "",
        attName: ""
      },
      attNameError: "",
      saveLoading: false
    };
  },
  mounted() {
    this.refreshGoodsAttribute();
  },
  methods: {
    refreshGoodsAttribute() {
      this.tableLoading = true;
      util.ajax
        .get("/goods/attribute/list")
        .then(response => {
          this.tableLoading = false;
          this.tableData = response.data;
        })
        .catch(error => {
          this.tableLoading = false;
          util.errorProcessor(this, error);
        });
    },
    modalCancel() {
      this.attForm = {
        id: "",
        attName: ""
      };
      this.attNameError = "";
      this.modal = false;
    },

    add() {
      this.modal = true;
    },
    edit(row) {
      this.attForm = row;
      this.modal = true;
    },

    saveSubmit() {
      this.attNameError = "";
      if (!this.attForm.attName) {
        this.attNameError = "属性名称必需";
        return;
      }
      this.saveLoading = true;
      util.ajax
        .post("/goods/attribute/save", this.attForm)
        .then(response => {
          this.saveLoading = false;
          this.$Message.success("保存成功.");
          this.refreshGoodsAttribute();
          this.modalCancel();
        })
        .catch(error => {
          this.saveLoading = false;
          util.errorProcessor(this, error);
        });
    },

    removeItem(id) {
      this.tableLoading = true;
      util.ajax
        .delete("/goods/attribute/remove/" + id)
        .then(response => {
          this.tableLoading = false;
          this.$Message.success("删除成功");
          this.refreshGoodsAttribute();
        })
        .catch(error => {
          this.tableLoading = false;
          util.errorProcessor(this, error);
        });
    }
  }
};
</script>


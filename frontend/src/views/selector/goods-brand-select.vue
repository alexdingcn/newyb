<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <Select ref="brandSelect" v-model="id" transfer filterable clearable :disabled="disabled" placeholder="请选择商品品牌" @on-change="onChange">
            <Option v-for="item in optionList" :value="item.id" :key="item.id">{{ item.brandName }} | {{item.brandNo}}</Option>
            <Option value="add">
                <a href="javascript:void(0)" @click="openAddModal"><Icon type="plus"></Icon>新增品牌</a>
            </Option>
        </Select>

        <Modal v-model="brandModal" title="商品品牌信息维护" :mask-closable="false" width="40" class="file-upload-low">
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

export default {
    name: 'goods-brand-select',
    props: ['value', 'size', 'disabled'],
    data() {
        return {
            id: '',
            optionList: [],
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
    mounted() {
        this.initList();
    },
    watch: {
        value: function (newValue) {
            this.id = newValue;
        }
    },
    methods: {
        initList() {
            let reqData = {
                enabled: true
            };
            util.ajax
                .post("/goods/brand/list", reqData)
                .then(response => {
                    this.optionList = response.data.data;
                })
                .catch(error => {
                    util.errorProcessor(this, error);
                });
        },
        onChange (data) {
            if (data === 'add') {
                this.openAddModal();
                return;
            }
            let items = this.optionList.filter(item => item.id === data);
            let item = '';
            if(items && items[0]) {
                item = items[0];
            }
            this.$emit('input', data);
            this.$emit('on-change', data, item);
        },

        openAddModal() {
            this.id = '',
            this.$refs.brandSelect.clearSingleSelect();
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
                        self.initList();
                    })
                    .catch(error => {
                        self.saveLoading = false;
                        util.errorProcessor(self, error);
                    })
            });
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
    }
  
}
</script>



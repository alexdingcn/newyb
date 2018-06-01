
<template>
    <div>
        <Select ref="typeSelect" :transfer="true" v-model="id" filterable clearable :disabled="disabled" placeholder="请选择档案类型" 
            :size="size" 
            @on-change="onChange">
            <Option v-for="item in optionList" :value="item.typeName" :key="item.typeName">{{item.typeName}}</Option>
            <Option value="add">
                <a href="javascript:void(0)" @click="openAddModal"><Icon type="plus"></Icon>新增档案类型</a>
            </Option>
        </Select>

        <Modal v-model="addModal" title="新增档案类型" :mask-closable="false" width="30" class="add-type-modal">
            <Form ref="addForm" :model="addForm" :label-width="100">
                <FormItem label="类型名称" >
                    <Input type="text" v-model="addForm.typeName" />
                </FormItem>
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
    name: "file-type-select",
    props: ['value', 'size', 'disabled'],
    data() {
        return {
            id: '',
            optionList: [],
            addModal: false,
            addForm: {
                typeName: ''
            },
            saveLoading: false
        }
    },
    mounted() {
        this.initList();
    },
    watch: {
        value(newValue) {
            this.id = newValue;
        }
    },
    methods: {
        initList() {
            var self = this;
            util.ajax.get('/file/filetype/list')
                .then(function (response) {
                    if (response.status === 200 && response.data) {
                        self.optionList = response.data;
                    }
                })
                .catch(function (error) {
                    util.errorProcessor(self, error);
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
            this.$refs.typeSelect.clearSingleSelect();
            this.addModal = true;
        },

        saveSubmit() {
            if (!this.addForm.typeName) {
                this.$Message.info('档案类型必需');
                return;
            }
            this.saveLoading = true;
            util.ajax.post('/file/filetype/add', this.addForm)
                .then((response) => {
                    this.saveLoading = false;
                    this.optionList = response.data;
                    this.modalCancel();
                })
                .catch((error) => {
                    this.saveLoading = false;
                    util.errorProcessor(this, error);
                });
        },
        modalCancel() {
            this.addForm = {
                typeName: ''
            };
            this.$refs.addForm.resetFields();
            this.addModal = false;
        },
    }
}
</script>

<style >

.add-type-modal {
    position: fixed;
    z-index: 3020;
}

</style>
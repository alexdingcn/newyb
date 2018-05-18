
<template>
    <div>
        <Select transfer v-model="id" filterable clearable :disabled="disabled" placeholder="请选择档案类型" 
            :size="size" 
            @on-change="onChange">
            <Option v-for="item in optionList" :value="item.typeName" :key="item.typeName">{{item.typeName}}</Option>
        </Select>

        <Modal v-model="brandModal" title="新增档案类型" :mask-closable="false" width="40" class="file-upload-modal">
            <Form ref="addForm" :model="addForm" :label-width="100">

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
            let items = this.optionList.filter(item => item.id === data);
            let item = '';
            if(items && items[0]) {
                item = items[0];
            }
            this.$emit('input', data);
            this.$emit('on-change', data, item);
        }
    }
}
</script>

<style>

</style>
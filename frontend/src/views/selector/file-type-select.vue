
<template>
  <Select v-model="id" filterable clearable filterable :disabled="disabled" placeholder="请选择档案类型" 
        :size="size" 
        @on-change="onChange">
        <Option v-for="item in optionList" :value="item.typeName" :key="item.typeName">{{item.typeName}}</Option>
    </Select>
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
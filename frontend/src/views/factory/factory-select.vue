<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Select v-model="id" placeholder="企业名称/联系人/拼音简称搜索"
            filterable clearable remote :remote-method="searchMethod" 
            :loading="searchLoading" 
            @on-change="onChange" :size="selectSize">
        <Option v-for="item in resultList" :value="item.id" :key="item.id"> {{item.name}} </Option>
    </Select>
</template>

<script>
import util from "@/libs/util.js"

export default {
    name: 'factory-select',
    props:['value', 'size'],
    data() {
        return {
            selectSize: this.size,
            id: null,
            resultList: [],
            searchLoading: false
        }
    },

    methods: {
        searchMethod(searchStr) {
            if (!searchStr) {
                this.resultList = [];
                return;
            }
            this.searchLoading = true;
            util.ajax.post("/factory/search", {search: searchStr})
                .then((response) => {
                    this.resultList = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
            this.searchLoading = false;
        },

        onChange(data) {
            let factory = this.resultList.filter(item => item.id === data);
            this.$emit("input", data);
            this.$emit("on-change", data, factory);
        }
    }
}
</script>

<style>

</style>


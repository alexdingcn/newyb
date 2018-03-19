<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Select v-model="id" placeholder="企业名称/联系人/拼音简称搜索"
                filterable clearabel remote :remote-method="searchMethod" 
                :loading="searchLoading" 
                @on-change="onChange" :size="selectSize">
            <Option v-for="item in resultList" :value="item.id" :key="item.id"> {{item.name}} </Option>
        </Select>
    </div>
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
            resultList: [{id: '', name: '清除'}],
            searchLoading: false
        }
    },

    methods: {
        searchMethod(searchStr) {
            if (!searchStr) {
                this.resultList = [{id: '', name: '清除'}];
                return;
            }
            this.searchLoading = true;
            util.ajax.post("/factory/search", {search: searchStr})
                .then((response) => {
                    this.resultList = [{id: '', name: '清除'}, ...response.data];
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
            this.searchLoading = false;
        },

        onChange(data) {
            this.$emit("input", data);
            this.$emit("on-change", data);
        }
    }
}
</script>

<style>

</style>


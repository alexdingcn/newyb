<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Select v-model="id" placeholder="企业名称/联系人/拼音简称搜索"
            filterable clearable 
            @on-change="onChange">
        <Option v-for="item in resultList" :value="item.id" :key="item.id"> {{item.name}} </Option>
    </Select>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'factory-select',
    props: ['value', 'size'],
    data () {
        return {
            selectSize: this.size,
            id: null,
            resultList: [],
            searchLoading: false
        };
    },
    watch: {
        value(newValue) {
            this.id = newValue;
        }
    },
    mounted() {
        this.searchMethod();
    },
    methods: {
        searchMethod () {
            // if (!searchStr) {
            //     this.resultList = [];
            //     return;
            // }
            // this.searchLoading = true;
            util.ajax.get('/factory/list')
                .then((response) => {
                    this.resultList = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        onChange (data) {
            let factorys = this.resultList.filter(item => item.id === data);
            let factory = factorys[0] ? factorys[0] : '';
            this.$emit('input', data);
            this.$emit('on-change', data, factory);
        }
    }
};
</script>

<style>

</style>



<template>
   <Select v-model="id" filterable clearable :disabled="disabled" placeholder="请选择商品品牌" @on-change="onChange">
        <Option v-for="item in optionList" :value="item.id" :key="item.id">{{ item.brandName }} | {{item.brandNo}}</Option>
    </Select>
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



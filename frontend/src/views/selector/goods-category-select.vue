
<template>
   <Select v-model="id" transfer filterable clearable :disabled="disabled" placeholder="请选择商品类别" @on-change="onChange">
        <Option v-for="item in optionList" :value="item.id" :key="item.id">{{ item.name }}</Option>
    </Select>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'goods-category-select',
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
            util.ajax
                .get("/goods/category/list")
                .then(response => {
                    this.optionList = response.data;
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



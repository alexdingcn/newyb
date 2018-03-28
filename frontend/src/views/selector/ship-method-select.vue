
<template>
   <Select v-model="id" :size="size" filterable clearable :disabled="disabled" placeholder="请选择运输方式" @on-change="onChange">
        <Option v-for="item in optionList" :value="item.id" :key="item.id">{{ item.value }}</Option>
    </Select>
</template>

<script>
import util from "@/libs/util.js";

export default {
    name: "ship-method-select",
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
            let reqData = ["SHIP_METHOD"];
            util.ajax
                .post("/options/list", reqData)
                .then(response => {
                    this.optionList = response.data.SHIP_METHOD;
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

<style>

</style>


<template>
   <Select v-model="id" filterable clearable :size="size" :disabled="disabled" placeholder="请选择温控状态" @on-change="onChange">
        <Option v-for="item in optionList" :value="item.id" :key="item.id">{{ item.value }}</Option>
    </Select>
</template>

<script>
import util from "@/libs/util.js";

export default {
    name: "temper-status-select",
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
            let reqData = [
                "TEMPER_STATUS"
            ];
            util.ajax
                .post("/options/list", reqData)
                .then(response => {
                    this.optionList = response.data.TEMPER_STATUS;
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

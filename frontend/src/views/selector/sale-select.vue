
<template>
   <Select v-model="id" :size="size" filterable clearable :disabled="disabled" placeholder="请选择销售人员" @on-change="onChange">
        <Option v-for="item in optionList" :value="item.userId" :key="item.userId">{{ item.nickname }}{{item.realname ? (' - [' + item.realname + ']') : ''}}</Option>
    </Select>
</template>

<script>
import util from "@/libs/util.js";

export default {
    name: "sale-select",
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
            util.ajax
                .get("/userrole/list", { params: { roleQuery: "ROLE_SALE" } })
                .then(response => {
                    this.optionList = response.data;
                })
                .catch(error => {
                    util.errorProcessor(this, error);
                });
        },
        onChange (data) {
            let items = this.optionList.filter(item => item.userId === data);
            let saleUser = {};
            if(items && items[0]) {
                saleUser = items[0];
            }
            this.$emit('input', data);
            this.$emit('on-change', data, saleUser);
        }
    }
}
</script>

<style >

</style>

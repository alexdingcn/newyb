
<template>
   <Select v-model="id" filterable clearable :disabled="disabled"
        :loading="selectLoading" placeholder="供应商代表" :size="size" 
        @on-change="onChange">
        <Option v-for="item in optionList" :value="item.id" :disabled="!item.enabled" :key="item.id">{{ item.name }}</Option>
    </Select>
</template>

<script>
import util from "@/libs/util.js";

export default {
    name: "supplier-contact-select",
    props: ['value', 'size', 'disabled', "supplierId"],
    data() {
        return {
            id: '',
            selectLoading: false,
            optionList: []
        }
    },
    watch: {
        value(newValue) {
            this.id = newValue;
        },
        supplierId(supplierId) {
            if(!supplierId || supplierId <= 0) {
                this.optionList = [];
            }else {
                this.initList(supplierId);
            }
        }
    },
    methods: {
        initList(supplierId) {
            let self = this;
            this.selectLoading = true;
            util.ajax.get('/supplier/contact/list', {params: {supplierId: supplierId}})
                .then(function (response) {
                    self.selectLoading = false;
                    self.optionList = response.data;
                    if (self.optionList.length === 1) {
                        self.id = self.optionList[0].id;
                    }
                })
                .catch(function (error) {
                    self.selectLoading = false;
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

<style >

</style>

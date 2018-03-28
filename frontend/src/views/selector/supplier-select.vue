<template>
  
    <Select
            v-model="supplierId"
            filterable
            clearable
            remote
            :size="selectSize"
            @on-change="onSelectSupplier"
            placeholder="供应商名称/拼音"
            :remote-method="querySupplier"
            :loading="supplierLoading">
        <Option v-for="option in supplierOptions" :value="option.id" :label="option.name" :key="option.id">{{option.name}}</Option>
    </Select>

</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'supplier-select',
    props: ['value', 'size'],
    data () {
        return {
            supplierId: '',
            selectSize: this.size,
            supplierLoading: false,
            supplierOptions: []
        };
    },

    methods: {
        querySupplier (query) {
            var self = this;
            if (query !== '') {
                this.supplierLoading = true;
                util.ajax.post('/supplier/search', {search: query})
                    .then(function (response) {
                        self.supplierLoading = false;
                        self.supplierOptions = response.data;
                    })
                    .catch(function (error) {
                        self.supplierLoading = false;
                        util.errorProcessor(self, error);
                    });
            } else {
                this.supplierOptions = [];
            }
        },
        onSelectSupplier (data) {
            let items = this.goodsOptions.filter(item => item.id === data);
            let item = '';
            if(items && items[0]) {
                item = items[0];
            }
            this.$emit('input', data);
            this.$emit('on-change', data, item);
        }
    }

};
</script>
<style>
</style>

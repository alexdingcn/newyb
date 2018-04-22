<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Select ref="custSelect" v-model="customerIdValue" :disabled="selectDisable" :placeholder="placeholderShow"
            filterable clearable remote :remote-method="searchByName" 
            :loading="searchLoading" 
            @on-change="onChange" :size="selectSize">
        <Option v-for="item in customerList" :value="item.id" :key="item.id" :disabled="!item.enabled"> {{item.name}} </Option>
    </Select>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'customer-select',
    props: ['value', 'size', 'disabled'],
    data () {
        return {
            selectSize: this.size,
            customerIdValue: '',
            selectDisable: false,
            customerList: [],
            searchLoading: false,
            placeholderShow: '输入姓名/简称/拼音搜索'
        };
    },
    watch: {
        disabled (data) {
            if (data) {
                this.selectDisable = true;
            } else {
                this.selectDisable = false;
            }
        },
        value(newValue) {
            this.customerIdValue = newValue;
        }
    },
    methods: {
        searchByName (name) {
            if (!name || name === '' || name.trim() === '') {
                this.customerList = [];
                return;
            }
            this.searchLoading = true;
            let reqData = {name: name};
            util.ajax.get('/customer/search/name', {params: reqData})
                .then((response) => {
                    this.customerList = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
            this.searchLoading = false;
        },

        onChange (data) {
            let customers = this.customerList.filter(item => item.id === data);
            let customer = {};
            if (customers && customers.length > 0) {
                customer = customers[0];
            }
            this.$emit('input', data);
            this.$emit('on-change', data, customer);
        }
    }

};
</script>


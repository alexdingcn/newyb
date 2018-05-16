<style lang="less">
@import "../../styles/common.less";
@import "customer-select.less";
</style>

<template>
    <div :class="wrapClasses">
        <div class="ivu-input-group-prepend">
            <Button type="primary" size="small" icon="ios-people-outline" @click="showCustomerSelectModal"></Button>
        </div>
        <Select ref="custSelect" v-model="customerIdValue"
                :disabled="selectDisable" :placeholder="placeholderShow"
                filterable clearable remote :remote-method="searchByName"
                :loading="searchLoading"
                @on-change="onChange" :size="selectSize">
            <Option v-for="item in customerList"
                    :value="item.id" :key="item.id" :disabled="!item.enabled"> {{item.name}} </Option>
        </Select>

        <Tooltip transfer v-if="hasHelpSlot">
            <Icon type="ios-information-outline"></Icon>
            <slot name="helpContent" slot="content">
            </slot>
        </Tooltip>

        <Modal v-model="selectCustModal" width="60" :mask-closable="false" title="选择客户" class="cust-modal" transfer>
            <customerListSelect ref="custSelectModal" @on-choosed="customerSelected" ></customerListSelect>
            <div slot="footer">
                <Button type="text" @click="selectCustModal = false">取消</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import customerListSelect from './customer-list-select.vue';

const prefixCls = 'ivu-input';

export default {
    name: 'customer-select',
    components: {
        customerListSelect
    },
    props: ['value', 'size', 'disabled'],
    data () {
        return {
            selectCustModal: false,
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
    computed: {
        hasHelpSlot () {
            return !!this.$slots.helpContent
        },
        wrapClasses () {
            return [
                `${prefixCls}-wrapper`,
                {
                    [`${prefixCls}-group`]: true,
                    [`${prefixCls}-group-with-prepend`]: true
                }
            ];
        },
    },
    methods: {
        showCustomerSelectModal() {
            this.selectCustModal = true;
            this.$refs.custSelectModal.reload();
        },
        customerSelected(item) {
            this.customerList = new Array();
            this.customerList.push(item);
            this.$nextTick(() => { this.customerIdValue = item.id; });
            this.selectCustModal = false;
        },
        searchByName (name) {

            if (!name || name === '' || name.trim() === '') {
                this.customerList = [];
                return;
            }
            this.searchLoading = true;
            let reqData = {name: name};
            util.ajax.get('/customer/search/name', {params: reqData})
                .then((response) => {
                    this.searchLoading = false;
                    this.customerList = response.data;
                })
                .catch((error) => {
                    this.searchLoading = false;
                    util.errorProcessor(this, error);
                });
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
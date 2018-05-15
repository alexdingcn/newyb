<style lang="less">
@import "customer-select.less";
</style>
<template>
    <div :class="wrapClasses">
        <div class="ivu-input-group-prepend">
            <Button type="primary" size="small" icon="android-contacts" @click="showSupplierSelectModal"></Button>
        </div>
        <Select v-model="supplierId"
                filterable
                clearable
                remote
                :size="selectSize"
                @on-change="onSelectSupplier"
                placeholder="供应商名称/拼音"
                :remote-method="querySupplier"
                :loading="supplierLoading">
            <Option v-for="option in supplierOptions" :value="option.id" :label="option.name" :disabled="!option.enabled" :key="option.id">{{option.name}}</Option>
        </Select>

        <Modal v-model="selectSupplierModal" width="60" :mask-closable="false" title="选择供应商" class="supplier-modal">
            <supplierListSelect ref="supplierSelectModal" @on-choosed="supplierSelected" ></supplierListSelect>
            <div slot="footer">
                <Button type="text" @click="selectSupplierModal = false">取消</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import supplierListSelect from './supplier-list-select.vue';

const prefixCls = 'ivu-input';

export default {
    name: 'supplier-select',

    props: {
        value: Number|String,
        size: String
    },
    components: {
        supplierListSelect
    },
    data () {
        return {
            selectSupplierModal: false,
            supplierId: '',
            selectSize: this.size,
            supplierLoading: false,
            supplierOptions: []
        };
    },
    computed: {
        wrapClasses () {
            return [
                `${prefixCls}-wrapper`,
                {
                    [`${prefixCls}-group`]: true,
                    [`${prefixCls}-group-with-prepend`]: true
                }
            ];
        }
    },
    watch: {
        value(data) {
            this.supplierId = data;
        }
    },
    methods: {
        showSupplierSelectModal() {
            this.selectSupplierModal = true;
            this.$refs.supplierSelectModal.reload();
        },
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
            this.supplierId = data;
            let items = this.supplierOptions.filter(item => item.id === data);
            let item = '';
            if(items && items[0]) {
                item = items[0];
            }
            this.$emit('input', data);
            this.$emit('on-change', data, item);
        },
        supplierSelected(item) {
            this.supplierOptions = new Array();
            this.supplierOptions.push(item);
            this.$nextTick(() => { this.supplierId = item.id; });
            this.selectSupplierModal = false;
        }
    }

};
</script>
<style>
</style>

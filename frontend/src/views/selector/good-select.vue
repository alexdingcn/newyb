<template>
  
  <Select
        ref="goodsSelect"
        v-model="id" 
        filterable
        :clearable="true"
        remote
        placeholder="商品名称/拼音"
        :disabled="disabled" 
        :size="selectSize"
        @on-change="onChange"
        :remote-method="queryGoods"
        :loading="goodsLoading">
        <Option v-for="option in goodsOptions" :value="option.id" :label="option.name" :key="option.id" :disabled="!option.enable">
            <span class="option-goods-name">{{ option.name }}</span>
            <span class="option-goods-spec">{{option.jx}} | {{ option.spec }} | {{option.factory}}</span>
        </Option>
    </Select>

</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'good-select',
    props: ['value', 'disabled', 'size'],
    data () {
        return {
            id: '',
            selectSize: this.size,
            goodsLoading: false,
            goodsOptions: []
        };
    },
    watch: {
        value(newValue) {
            this.id = newValue;
        }
    },
    methods: {
        queryGoods (query) {
            var self = this;
            if (query !== '') {
                this.goodsLoading = true;
                util.ajax.get('/goods/list',
                    { params:
                        {search: query, page: 1, size: 80}
                    })
                    .then(function (response) {
                        self.goodsLoading = false;
                        self.goodsOptions = response.data.data;
                    })
                    .catch(function (error) {
                        self.goodsLoading = false;
                        util.errorProcessor(self, error);
                    });
            } else {
                this.goodsOptions = [];
            }
        },
        clearSingleSelect() {
            this.id = '';
            this.$refs.goodsSelect.clearSingleSelect();
        },
        onChange (data) {
            let goods = this.goodsOptions.filter(item => item.id === data);
            let good = goods[0] ? goods[0] : '';
            this.$emit('input', data);
            this.$emit('on-change', data, good);
        }
    }

};
</script>
<style>
.option-goods-spec {
  float: right;
  color: #999;
}
</style>

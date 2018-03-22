<template>
  
  <Select
        ref="goodsSelect"
        filterable
        clearable
        remote
        placeholder="商品名称/拼音"
        :size="selectSize"
        @on-change="onChange"
        :remote-method="queryGoods"
        :loading="goodsLoading">
        <Option v-for="option in goodsOptions" :value="option.id" :label="option.name" :key="option.id">
            <span class="option-goods-name">{{ option.name }}</span>
            <span class="option-goods-spec">{{ option.spec }} | {{option.factory}}</span>
        </Option>
    </Select>

</template>

<script>
import util from "@/libs/util.js";

export default {
    name: 'good-select',
    props:['value', 'size'],
    data() {
        return {
            selectSize: this.size,
            goodsLoading: false,
            goodsOptions: []
        }
    },

    methods: {
        queryGoods (query) {
            var self = this;
            if (query !== '') {
                this.goodsLoading = true;
                util.ajax.get('/goods/list', 
                    { params: 
                        {search: query, page: 1, size: 50}
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
        onChange(data) {
            let good = this.goodsOptions.filter(item => item.id === data);
            this.$emit("input", data);
            this.$emit("on-change", data, good);
        }
    }
  
}
</script>
<style>
.option-goods-spec {
  float: right;
  color: #999;
}
</style>

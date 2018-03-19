<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Select v-model="customerId" 
                filterable clearabel remote :remote-method="searchByName" 
                :loading="searchLoading" 
                @on-change="onChange" :size="selectSize">
            <Option v-for="item in customerList" :value="item.id" :key="item.id"> {{item.name}} </Option>
        </Select>
    </div>
</template>

<script>
import util from "@/libs/util.js"

export default {
    name: 'customer-select',
    props:['value', 'size'],
    data() {
        return {
            selectSize: this.size,
            customerId: null,
            customerList: [
                {
                    id: '',
                    name: '清除'
                }
            ],
            searchLoading: false
        }
    },
    watch:{
        
    },
    methods: {
        searchByName(name) {
            if(!name || name === '' || name.trim() === '') {
                this.customerList = [{id: '', name: '清除'}];
                return;
            }
            this.searchLoading = true;
            let reqData = {name: name};
            util.ajax.get("/customer/search/name", {params: reqData})
                .then((response) => {
                    this.customerList = [{id: '', name: '清除'}, ...response.data];
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
            this.searchLoading = false;
        },

        onChange(data) {
            this.$emit("input", data);
            this.$emit("on-change", data);
        }
    }
  
}
</script>


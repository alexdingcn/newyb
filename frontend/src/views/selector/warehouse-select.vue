
<template>
  <Select ref="warehouseSelect" v-model="warehouseId" filterable clearable :disabled="disabled" :size="size" 
        placeholder="请选择仓库点" @on-change="onChange">
        <Option v-for="item in warehouseList" :value="item.id" :key="item.id">{{ item.name }}</Option>
   </Select>
</template>

<script>
import util from "@/libs/util.js";

export default {
  name: "warehouse-select",
  props: ["value", "size", "disabled"],
  data() {
    return {
      warehouseId: "",
      warehouseList: []
    };
  },
  mounted() {
    this.getWarehouseList();
  },
  watch: {
    value(newValue) {
      this.warehouseId = newValue;
    }
  },
  methods: {
    getWarehouseList() {
      util.ajax
        .get("/warehouse/list")
        .then(response => {
          this.warehouseList = response.data;
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    clearSingleSelect() {
      this.warehouseId = "";
      this.$refs.warehouseSelect.clearSingleSelect();
    },
    onChange(data) {
      let items = this.warehouseList.filter(item => item.id === data);
      let item = "";
      if (items && items[0]) {
        item = items[0];
      }
      this.$emit("input", data);
      this.$emit("on-change", data, item);
    }
  }
};
</script>

<style >
</style>

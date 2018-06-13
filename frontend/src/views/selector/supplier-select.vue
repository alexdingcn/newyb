<style lang="less">
@import "customer-select.less";
</style>
<template>
    <div :class="wrapClasses">
        <div class="ivu-input-group-prepend">
            <Button type="primary" size="small" icon="android-contacts" @click="showSupplierSelectModal"></Button>
        </div>
        <Select v-model="supplierId"
                ref="supplierSelectCtrl"
                filterable
                clearable
                remote
                :size="selectSize"
                @on-change="onSelectSupplier"
                @on-query-change="onSelectQueryChange"
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
import util from "@/libs/util.js";
import supplierListSelect from "./supplier-list-select.vue";

const prefixCls = "ivu-input";

export default {
  name: "supplier-select",
  props: ["value", "size", "disabled"],
  components: {
    supplierListSelect
  },
  data() {
    return {
      selectSupplierModal: false,
      supplierId: "",
      selectSize: this.size,
      supplierLoading: false,
      supplierOptions: []
    };
  },
  computed: {
    wrapClasses() {
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
      console.log("supplier search:" + data);
      if (
        this.supplierOptions === undefined ||
        this.supplierOptions.length == 0 ||
        (this.supplierOptions.length === 1 &&
          this.supplierOptions[0].id != data)
      ) {
        this.querySupplier();
      }
    }
  },
  mounted() {
    console.log("suppler mounted " + this.value);
    if (this.value && this.value > 0) {
      this.supplierId = this.value;
      if (
        this.supplierOptions === undefined ||
        this.supplierOptions.length == 0
      ) {
        this.querySupplier();
      }
    }
  },
  methods: {
    showSupplierSelectModal() {
      this.selectSupplierModal = true;
      this.$refs.supplierSelectModal.reload();
    },
    onSelectQueryChange(query) {
      if (query === "") {
        this.$refs.supplierSelectCtrl.setQuery(
          this.$refs.supplierSelectCtrl.selectedSingle
        );
      }
    },
    querySupplier(query) {
      console.log(query);
      var self = this;
      if (query && query !== "") {
        this.supplierLoading = true;
        util.ajax
          .post("/supplier/search", { search: query })
          .then(function(response) {
            if (response.status === 200) {
              self.supplierLoading = false;
              self.supplierOptions = response.data;
            }
          })
          .catch(function(error) {
            self.supplierLoading = false;
            util.errorProcessor(self, error);
          });
      } else if (this.supplierId && this.supplierId !== "") {
        util.ajax
          .get("/supplier/" + this.supplierId)
          .then(function(response) {
            if (response.status === 200) {
              self.supplierLoading = false;
              if (response.data) {
                self.supplierOptions = [response.data];
                self.$refs.supplierSelectCtrl.setQuery(response.data.name);
                self.$emit("input", response.data ? response.data.id : "");
                self.$emit("on-change", response.data.id, response.data);
              }
            }
          })
          .catch(function(error) {
            self.supplierLoading = false;
            util.errorProcessor(self, error);
          });
      }
    },
    onSelectSupplier(data) {
      this.supplierId = data;
      let items = this.supplierOptions.filter(item => item.id === data);
      let item = "";
      if (items && items[0]) {
        item = items[0];
      }
      this.$emit("input", data);
      this.$emit("on-change", data, item);
    },
    supplierSelected(item) {
      this.supplierOptions = new Array();
      this.supplierOptions.push(item);
      this.$nextTick(() => {
        this.onSelectSupplier(item.id);
      });
      this.selectSupplierModal = false;
    }
  }
};
</script>
<style >
</style>

<style lang="less">
@import "customer-select.less";
</style>

<template>
      <div :class="wrapClasses">
        <div class="ivu-input-group-prepend">
            <Button type="primary" size="small" icon="ios-book" @click="showGoodsSelectModal"></Button>
        </div>
        <Select
            ref="goodsSelect"
            v-model="goodsId" 
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
                <span class="option-goods-spec">{{ option.specDesc }} | {{option.factoryName}}</span>
            </Option>
        </Select>

        <Tooltip transfer v-if="hasHelpSlot">
            <Icon type="ios-information-outline"></Icon>
            <slot name="helpContent" slot="content">
            </slot>
        </Tooltip>

        <Modal v-model="selectGoodsModal" width="70" :mask-closable="false" title="选择商品" class="goods-modal">
            <goodsListSelect ref="goodsSelectModal" :warehouseId="warehouseId" @on-choosed="goodsSelected" :options="options" ></goodsListSelect>
            <div slot="footer">
                <Button type="text" @click="selectGoodsModal = false">取消</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import goodsListSelect from "./good-list-select.vue";
const prefixCls = "ivu-input";
export default {
  name: "good-select",
  props: ["value", "warehouseId", "disabled", "size", "options"],
  components: {
    goodsListSelect
  },
  data() {
    return {
      goodsId: "",
      selectGoodsModal: false,
      selectSize: this.size,
      goodsLoading: false,
      goodsOptions: []
    };
  },
  watch: {
    value(newValue) {
      this.goodsId = newValue;
    }
  },
  computed: {
    hasHelpSlot() {
      return !!this.$slots.helpContent;
    },
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
  methods: {
    showGoodsSelectModal() {
      if (!this.disabled) {
        this.selectGoodsModal = true;
        this.$refs.goodsSelectModal.reload();
      }
    },
    goodsSelected(item) {
      this.goodsOptions = new Array();
      this.goodsOptions.push(item);
      this.$nextTick(() => {
        this.goodsId = item.id;
      });
      this.selectGoodsModal = false;
      this.$emit("on-change", item.id, item);
    },
    queryGoods(query) {
      var self = this;
      if (query !== "") {
        let requestData = {
          search: query,
          page: 1,
          size: 80
        };
        if (this.warehouseId) {
          requestData["warehouseId"] = this.warehouseId;
          requestData["options"] = this.options;
        }
        this.goodsLoading = true;
        util.ajax
          .get("/goods/list", { params: requestData })
          .then(function(response) {
            self.goodsLoading = false;
            self.goodsOptions = response.data.data;
          })
          .catch(function(error) {
            self.goodsLoading = false;
            util.errorProcessor(self, error);
          });
      } else {
        this.goodsOptions = [];
      }
    },
    clearSingleSelect() {
      this.goodsId = "";
      this.$refs.goodsSelect.clearSingleSelect();
    },
    onChange(data) {
      let goods = this.goodsOptions.filter(item => item.id === data);
      let good = goods[0] ? goods[0] : "";
      this.$emit("input", data);
      this.$emit("on-change", data, good);
    }
  }
};
</script>
<style >
.option-goods-spec {
  float: right;
  color: #999;
}
</style>
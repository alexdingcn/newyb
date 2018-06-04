<style lang="less">
@import "customer-select.less";
</style>
<template>
    <div :class="wrapClasses">
        <div class="ivu-input-group-prepend">
            <Button type="primary" size="small" icon="ios-flower" @click="showFactorySelectModal"></Button>
        </div>
        <Select v-model="factoryId"
                ref="factorySelectCtrl"
                filterable
                clearable
                remote
                :size="selectSize"
                @on-change="onSelectFactory"
                @on-query-change="onSelectQueryChange"
                placeholder="生产企业名称/联系人/拼音简称搜索"
                :remote-method="queryFactory"
                :loading="searchLoading">
            <Option v-for="item in resultList" :value="item.id" :key="item.id"> {{item.name}} </Option>
        </Select>

        <Modal v-model="selectFactoryModal" width="60" :mask-closable="false" title="选择生产企业" class="factory-modal">
            <factoryListSelect ref="factorySelectModal" @on-choosed="factorySelected" ></factoryListSelect>
            <div slot="footer">
                <Button type="text" @click="selectFactoryModal = false">取消</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import factoryListSelect from "./factory-list-select.vue";

const prefixCls = "ivu-input";

export default {
  name: "factory-select",
  props: {
    value: Number | String,
    size: String
  },
  components: {
    factoryListSelect
  },
  data() {
    return {
      selectFactoryModal: false,
      selectSize: this.size,
      factoryId: null,
      resultList: [],
      searchLoading: false
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
    value(newValue) {
      this.factoryId = newValue;
      if (
        this.resultList === undefined ||
        this.resultList.length == 0 ||
        (this.resultList.length === 1 && this.resultList[0].id != newValue)
      ) {
        this.queryFactory();
      }
    }
  },
  methods: {
    showFactorySelectModal() {
      this.selectFactoryModal = true;
      this.$refs.factorySelectModal.reload();
    },
    onSelectQueryChange(query) {
      if (query === "") {
        this.$refs.factorySelectCtrl.setQuery(
          this.$refs.factorySelectCtrl.selectedSingle
        );
      }
    },
    queryFactory(query) {
      var self = this;
      if (query && query !== "") {
        this.searchLoading = true;
        util.ajax
          .post("/factory/search", { search: query })
          .then(function(response) {
            if (response.status === 200) {
              self.searchLoading = false;
              self.resultList = response.data;
            }
          })
          .catch(function(error) {
            self.searchLoading = false;
            util.errorProcessor(self, error);
          });
      } else if (this.factoryId !== "") {
        util.ajax
          .get("/factory/" + this.factoryId)
          .then(function(response) {
            if (response.status === 200) {
              self.searchLoading = false;
              if (response.data) {
                self.resultList = [response.data];
                self.$refs.factorySelectCtrl.setQuery(response.data.name);
              }
            }
          })
          .catch(function(error) {
            self.searchLoading = false;
            util.errorProcessor(self, error);
          });
      }
    },
    onSelectFactory(data) {
      this.factoryId = data;
      console.log("this.resultList---" + this.resultList);
      let factories = this.resultList.filter(item => item.id === data);
      let factory = factories[0] ? factories[0] : "";

      this.$emit("input", data);
      this.$emit("on-change", data, factory);
    },
    factorySelected(item) {
      this.resultList = new Array();
      this.resultList.push(item);
      this.$nextTick(() => {
        this.onSelectFactory(item.id);
      });
      this.selectFactoryModal = false;
    }
  }
};
</script>

<style >
</style>


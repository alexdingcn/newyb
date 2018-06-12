<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <ButtonGroup v-if="data.length > 0" size="small">
        <Button v-for="(item, index) in data" :key="index" :disabled="item.disabled" :type="item.type" :icon="item.icon" @click="buttonItemClick(item)">{{item.label || ""}}</Button>
    </ButtonGroup>
</template>

<script>
export default {
  name: "actionButton",
  props: {
    data: {
      type: Array,
      default: []
    }
  },
  methods: {
    buttonItemClick(item) {
      this.$emit("click", item); //触发一个点击事件，返回点击按钮的内容
      let action = item.action;
      if (action && typeof action === "function") {
        let actionParams = item.param;
        action(actionParams, item); //尝试调用上级的方法
      }
    }
  }
};
</script>



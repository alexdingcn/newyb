<style lang="less">
@import "../../styles/common.less";

.show-content {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 6;
  -webkit-box-orient: vertical;
  line-height: 1.5em;
  height: 9em;
}

.show-div {
  background-color: #ffffff;
  height: 9em;
  width: 450px;
}

.show-div-font {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
  font-size: 1.1em;
  p {
    padding-bottom: 0.5em;
  }
}
</style>

<template>
    <div>
        <text-editor v-model="value" :height="300"></text-editor>

        <hr size="2" style="width: 100%; margin-top: 10px; margin-bottom: 20px;"/>

        <template>
            <div @click="showDetail" class="show-div show-div-font">
                <p class="show-content" v-html="value"></p>
            </div>
        </template>

        <Modal v-model="showModal" width="80" :footerHide="true" :mask-closable="false" title="明细" >
            <div class="show-div-font">
                <p v-html="value"></p>
            </div>
            
        </Modal>
    </div>
</template>

<script>
import textEditor from "@/views/my-components/text-editor/text-editor";

export default {
  name: "sell-report",
  components: {
    textEditor
  },
  data() {
    return {
      value: "",
      showModal: false
    };
  },
  methods: {
    showDetail() {
      this.showModal = true;
    }
  }
};
</script>


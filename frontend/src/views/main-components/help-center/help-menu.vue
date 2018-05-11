
<template>
    <div @click="handlerOpenHelp" class="help-con">
        <Tooltip content="帮助中心" placement="bottom">
            <Icon :size="20" :color="isOpen ? '#2d8cf0' : ''" type="help-circled"></Icon>
        </Tooltip>
    </div>
</template>

<script>
export default {
  name: 'help-menu',
  props: {
      openHelp: {
          type: Boolean,
          default: false
      }
  },
  data() {
      return {
          isOpen: this.openHelp
      }
  },
  methods: {
      handlerOpenHelp () {
          this.isOpen = !this.isOpen;
          this.$emit('open-change', this.isOpen);
      }
  }
}
</script>

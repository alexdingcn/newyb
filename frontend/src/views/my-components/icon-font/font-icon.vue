
<template lang="html">
  <i v-if="type" :class="classes" :style="styles"></i>
</template>

<script>
const prefixCls = 'iconfont';
export default {
    name: 'font-icon',
    props: {
        type: {
            type: String,
            default: ''
        },
        size: {
            type: String|Number,
            default: ''
        },
        color: {
            type: String,
            default: ''
        }
    },
    computed: {
            classes () {
                return `${prefixCls} ${this.type}`;
            },
            styles () {
                let style = {};
                if (this.size) {
                    style['font-size'] = `${this.size}px`;
                }
                if (this.color) {
                    style.color = this.color;
                }
                return style;
            }
        }
};
</script>
<style lang="less">
@import "../../styles/common.less";
@import "./config.less";
</style>

<template>
    <Card @click.native="onClick" class="sider-tag" :style="{border: this.active? '1.5px solid #93c0ee' : ''}">
        <Row style="width:100%">
            <i-col span="6" hide-trigger>
                <Icon :type="icon" size="50"></Icon>
            </i-col>
            <i-col span="18">
                <p class="content-title">{{title}}</p>
                <p class="content-desc">{{desc}}</p>
            </i-col>
        </Row>
    </Card>
</template>

<script>
export default {
    name: 'configSiderTag',
    props: {
        icon: {
            type: String,
            default: 'android-locate'
        },
        title: {
            type: String,
            required: true
        },
        desc: {
            type: String,
            required: true
        },
        active: {
            type: Boolean,
            default: false
        }
    },
    methods: {
        onClick() {
            this.$emit('click');
        }
    }
    
}
</script>



<style lang="less">
    @import './styles/infor-card.less';
</style>

<template>
    <Card :padding="0">

        <i-col class="infor-card-icon-con" :style="{backgroundColor: color, color: 'white'}">
            <Row class="height-100" type="flex" align="middle" justify="center">
                <Icon :type="iconType" :size="iconSize"></Icon>
                <span class="infor-intro-text">{{ introText }}</span>
            </Row>
        </i-col>

        <div class="infor-card-con">
            <Row type="flex" align="middle" justify="center" class="height-100">
                <count-up
                    class="infor-card-count user-created-count"
                    :id-name="idName"
                    :end-val="endVal"
                    :color="color"
                    :countSize="countSize"
                    :countWeight="countWeight">

                </count-up>
            </Row>
        </div>
    </Card>
</template>

<script>
import countUp from './countUp.vue';

export default {
    name: 'inforCard',
    components: {
        countUp
    },
    props: {
        idName: String,
        endVal: Number,
        color: String,
        iconType: String,
        introText: String,
        countSize: {
            type: String,
            default: '30px'
        },
        countWeight: {
            type: Number,
            default: 700
        },
        iconSize: {
            type: Number,
            default: 20
        }
    }
};
</script>


<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <div v-if="showNav">
            <Row>
                <h2 class="nav-title">商品相关</h2>
                <Row>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                        <config-sider-tag :active="currActive === 'companyType'" 
                            title="公司业务类型" 
                            desc="针对公司主营业务的选择不同的类型信息, 控制商品中的一些特殊信息" 
                            @click="configNavClick('companyType')">
                        </config-sider-tag>
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                </Row>
            </Row>
            <Row>
                <h2 class="nav-title">订单相关</h2>
                <Row>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                </Row>
            </Row>
        </div> 
        <div v-if="!showNav">
            <config-company-type ref="companyType" v-if="currActive === 'companyType'" @back="back"></config-company-type>
        </div>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import configSiderTag from './config-sider-tag.vue';
import configCompanyType from './components/config-company-type.vue';

export default {
    name: 'config',
    components:{
        configSiderTag,
        configCompanyType
    },
    data() {
        return {
            currActive: '',
            showNav: true,
        }
    },
    mounted() {},
    methods: {
        configNavClick(key) {
            console.log('config nav click, key:' + key);
            this.currActive = key;
            this.showNav = false;
            let self = this;
            this.$nextTick(() => {
                self.$refs[key].init();
            });
        },
        back() {
            this.showNav = true;
        }
    }
}
</script>


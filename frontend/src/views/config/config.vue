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
            <div>
                <h2 class="nav-title">订单相关</h2>
                <Row>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                        <config-sider-tag :active="currActive === 'orderFlow'" 
                            title="订单流程设置" 
                            desc="针对不同的业务特征，设置对应业务流程，提高效率" 
                            icon="arrow-swap"
                            @click="configNavClick('orderFlow')">
                        </config-sider-tag>
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                        <config-sider-tag :active="currActive === 'salePrice'" 
                            title="销售特批价调整" 
                            desc="制作销售单时，是否启用销售订单特定价格调整功能" 
                            icon="android-checkmark-circle" 
                            @click="configNavClick('salePrice')">
                        </config-sider-tag>
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                    <i-col :xs="12" :sm="8" :md="6" :lg="5">
                    </i-col>
                </Row>
            </div>
        </div> 
        <div v-if="!showNav">
            <config-company-type ref="companyType" v-if="currActive === 'companyType'" @back="back"></config-company-type>
            <config-order-flow ref="orderFlow" v-if="currActive === 'orderFlow'" @back="back" ></config-order-flow>
            <config-sale-price ref="salePrice" v-if="currActive === 'salePrice'" @back="back" ></config-sale-price>
        </div>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import configSiderTag from "./config-sider-tag.vue";
import configCompanyType from "./components/config-company-type.vue";
import configOrderFlow from "./components/config-order-flow.vue";
import configSalePrice from "./components/config-sale-price.vue";

export default {
  name: "config",
  components: {
    configSiderTag,
    configCompanyType,
    configOrderFlow,
    configSalePrice
  },
  data() {
    return {
      currActive: "",
      showNav: true
    };
  },
  mounted() {},
  methods: {
    configNavClick(key) {
      console.log("config nav click, key:" + key);
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
};
</script>


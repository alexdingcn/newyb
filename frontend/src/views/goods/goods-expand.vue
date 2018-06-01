<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <h3>商品基础信息</h3>
        <hr size="1" style="width:80%; margin-bottom:1.2em;"/>
        <Row class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">商品名称: </span>
                <span class="label-value">{{goods.name}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">商品类别: </span>
                <span class="label-value">{{goods.categoryName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">品牌: </span>
                <span class="label-value">{{goods.brandName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">商品条码: </span>
                <span class="label-value">{{goods.barCode}}</span>
            </i-col>
        </Row>
        <Row  class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">供应商: </span>
                <span class="label-value">{{goods.supplierName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">生产企业: </span>
                <span class="label-value">{{goods.factoryName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">产地: </span>
                <span class="label-value">{{goods.origin}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">规格: </span>
                <goods-spec-tags color="blue" :tags="goodsSpecs"></goods-spec-tags>
            </i-col>
        </Row>
        <Row  class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">计量单位: </span>
                <span class="label-value">{{goods.unitName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">生成日期: </span>
                <span class="label-value">{{productDate}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">到期日期: </span>
                <span class="label-value">{{expDate}}</span>
            </i-col>
        </Row>
        <Row  class="row-margin-bottom">
            <i-col span="6">
                <span class="label-key">中件单位: </span>
                <span class="label-value">{{goods.mediumUnitName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">中件单位换算: </span>
                <span class="label-value">{{goods.mediumPack}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">大件单位: </span>
                <span class="label-value">{{goods.packUnitName}}</span>
            </i-col>
            <i-col span="6">
                <span class="label-key">大件单位换算: </span>
                <span class="label-value">{{goods.bigPack}}</span>
            </i-col>
        </Row>

        <h3>商品证件信息</h3>
        <hr size="1" style="width:80%; margin-bottom:1.2em;"/>
        <Row  class="row-margin-bottom">
            <i-col span="8">
                <span class="label-key">证件1名称: </span>
                <span class="label-value">{{goods.cert1Name}}</span>
            </i-col>
            <i-col span="8">
                <span class="label-key">证件1编号: </span>
                <span class="label-value">{{goods.cert1No}}</span>
            </i-col>
            <i-col span="8">
                <span class="label-key">证件1有效期: </span>
                <span class="label-value">{{goods.cert1ExpDate | dateFormate}}</span>
            </i-col>
        </Row>
        <Row  class="row-margin-bottom">
            <i-col span="8">
                <span class="label-key">证件2名称: </span>
                <span class="label-value">{{goods.cert2Name}}</span>
            </i-col>
            <i-col span="8">
                <span class="label-key">证件2编号: </span>
                <span class="label-value">{{goods.cert2No}}</span>
            </i-col>
            <i-col span="8">
                <span class="label-key">证件2有效期: </span>
                <span class="label-value">{{goods.cert2ExpDate | dateFormate}}</span>
            </i-col>
        </Row>
        <Row  class="row-margin-bottom">
            <i-col span="8">
                <span class="label-key">证件3名称: </span>
                <span class="label-value">{{goods.cert3Name}}</span>
            </i-col>
            <i-col span="8">
                <span class="label-key">证件3编号: </span>
                <span class="label-value">{{goods.cert3No}}</span>
            </i-col>
            <i-col span="8">
                <span class="label-key">证件3有效期: </span>
                <span class="label-value">{{goods.cert3ExpDate | dateFormate}}</span>
            </i-col>
        </Row>
        
        <h3 >商品自定义属性</h3>
        <hr size="1" style="width:80%; margin-bottom:1.2em;"/>
        <Row  class="row-margin-bottom" v-for="item in goods.attributeRefs" :key="item.attId">
            <i-col span="12">
                <span class="label-key">{{item.attName}}: </span>
                <span class="label-value">{{item.attValue}}</span>
            </i-col>
        </Row>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';
import goodsSpecTags from './goods-spec-tabs.vue';

export default {
    name: 'good-expand',
    components: {
        goodsSpecTags
    },
    props: {
        goodsSpecs: {
            type: Array,
            default: []
        },
        productDate: String,
        expDate: String
    },
    data() {
        return {
            goods:{}
        }
    },
    filters: {
        dateFormate(data) {
            return data ? moment(data).format('YYYY-MM-DD') : '';
        }
    },
    methods: {
        loadGoodsData(goodsId) {
            if (!goodsId) {
                this.goods = {};
            }
            util.ajax.get("/goods/info", {params: {detailId: goodsId}})
                .then((response) => {
                    this.goods = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        }
    }

};
</script>

<style >
.label-key {
    width: 110px;
    text-align: right;
}
.label-value {
    font-weight:bold;
}
</style>


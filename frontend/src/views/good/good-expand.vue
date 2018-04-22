<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Row >
            <Col span="4">
                <span class="label-key">通用名称: </span>
                <span class="label-value">{{good.name}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">商品类别: </span>
                <span class="label-value">{{good.categoryName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">货号: </span>
                <span class="label-value">{{good.id}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">商品条码: </span>
                <span class="label-value">{{good.serial}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">商品名称: </span>
                <span class="label-value">{{good.fullName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">生产企业: </span>
                <span class="label-value">{{good.factory}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">产地: </span>
                <span class="label-value">{{good.origin}}</span>
            </Col>
            <Col span="12">
                <span class="label-key">主治功能: </span>
                <span class="label-value">{{good.cureRange}}</span>
            </Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">剂型: </span>
                <span class="label-value">{{good.jxName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">规格: </span>
                <span class="label-value">{{good.spec}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">计量单位: </span>
                <span class="label-value">{{good.unitName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">整件单位: </span>
                <span class="label-value">{{good.packUnitName}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">储藏条件: </span>
                <span class="label-value">{{good.storageConditionName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">药基: </span>
                <span class="label-value">{{good.baseMedName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">是否进口: </span>
                <span class="label-value">{{good.foreign ? '是' : '否'}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">中/西药属性: </span>
                <span class="label-value">{{good.medTypeName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">处方/非处方属性: </span>
                <span class="label-value">{{good.prescriptionName}}</span>
            </Col>
            <Col span="4"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">批准文号: </span>
                <span class="label-value">{{good.permitNo}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">注册商标: </span>
                <span class="label-value">{{good.brandNo}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">生产日期: </span>
                <span class="label-value"> {{productDate}} </span>
            </Col>
            <Col span="4">
                <span class="label-key">有效期至: </span>
                <span class="label-value"> {{expDate}} </span>
            </Col>
            <Col span="4">
                <span class="label-key">是否特殊管控: </span>
                <span class="label-value">{{good.specialManaged ? '是': '否'}}</span>
            </Col>
            <Col span="4"></Col>
        </Row>
    </div>
</template>

<script>
import moment from 'moment';
export default {
    name: 'good-expand',
    props: {
        good: Object,
        repertoryInfo: Object,
        productDate: String,
        expDate: String
    }
};
</script>

<style>
.label-key {
    width: 110px;
    text-align: right;
}
.label-value {
    font-weight:bold;
}
</style>


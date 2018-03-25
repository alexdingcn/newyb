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
                <span class="label-value">{{good.code}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">商品编号: </span>
                <span class="label-value">{{good.serial}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">商品名称: </span>
                <span class="label-value">{{good.fullName}}</span>
            </Col>
            <Col span="6">
                <span class="label-key">生产企业: </span>
                <span class="label-value">{{good.factory}}</span>
            </Col>
            <Col span="6">
                <span class="label-key">产地: </span>
                <span class="label-value">{{good.origin}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">剂型: </span>
                <span class="label-value">{{good.jx}}</span>
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
                <span class="label-value">{{good.storageCondition}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">储藏描述: </span>
                <span class="label-value">{{good.storageDescription}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">是否进口: </span>
                <span class="label-value">{{good.isForeign ? '是' : '否'}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">中西药: </span>
                <span class="label-value">{{good.isTcm ? '中药': '西药'}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">批准文号: </span>
                <span class="label-value">{{good.permitId}}</span>
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
                <span class="label-key">是否处方药: </span>
                <span class="label-value">{{good.isPrescription ? '是' : '否'}}</span>
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
}
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


<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Row >
            <Col span="4">
                <span class="label-key">通用名称: </span>
                <span class="label-value">{{detail.name}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">商品类别: </span>
                <span class="label-value">{{detail.categoryName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">货号: </span>
                <span class="label-value">{{detail.code}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">商品编号: </span>
                <span class="label-value">{{detail.serial}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">商品名称: </span>
                <span class="label-value">{{detail.fullName}}</span>
            </Col>
            <Col span="6">
                <span class="label-key">生产企业: </span>
                <span class="label-value">{{detail.factory}}</span>
            </Col>
            <Col span="6">
                <span class="label-key">产地: </span>
                <span class="label-value">{{detail.origin}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">剂型: </span>
                <span class="label-value">{{detail.jx}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">规格: </span>
                <span class="label-value">{{detail.spec}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">计量单位: </span>
                <span class="label-value">{{detail.unitName}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">整件单位: </span>
                <span class="label-value">{{detail.packUnitName}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">储藏条件: </span>
                <span class="label-value">{{detail.storageCondition}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">储藏描述: </span>
                <span class="label-value">{{detail.storageDescription}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">是否进口: </span>
                <span class="label-value">{{detail.isForeign ? '是' : '否'}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">中西药: </span>
                <span class="label-value">{{detail.isTcm ? '中药': '西药'}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
        <Row >
            <Col span="4">
                <span class="label-key">批准文号: </span>
                <span class="label-value">{{detail.permitId}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">有效期至: </span>
                <span class="label-value"> </span>
            </Col>
            <Col span="4">
                <span class="label-key">是否处方药: </span>
                <span class="label-value">{{detail.isPrescription ? '是' : '否'}}</span>
            </Col>
            <Col span="4">
                <span class="label-key">是否特殊管控: </span>
                <span class="label-value">{{detail.specialManaged ? '是': '否'}}</span>
            </Col>
            <Col span="8"></Col>
        </Row>
    </div>
</template>

<script>
export default {
    name: 'good-expand',
    props: {
        detail: Object
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


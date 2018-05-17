<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <h3>批量设置: </h3>
        <Row class="row-margin-bottom" :gutter="25">
            <i-col span="6">
                <span>批发价:</span>
                <InputNumber :min="0" v-model="batchPrices" style="width:80%;"></InputNumber>
            </i-col>
            <i-col span="6">
                <span>市场价:</span>
                <InputNumber :min="0" v-model="retailPrices" style="width:80%;"></InputNumber>
            </i-col>
            <i-col span="6">
                <span>参考进货价:</span>
                <InputNumber :min="0" v-model="inPrices" style="width:80%;"></InputNumber>
            </i-col>
        </Row>
        <Table stripe highlight-row :loading="tableLoading" 
                :columns="tableCulumns" :data="details" ref="goodsDetailTable" 
                style="width: 100%;" size="small">
        </Table>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import goodsSpecTabs from './goods-spec-tabs.vue';

export default {
    name: 'goods-spec-price',
    props:{
        details: {
            type: Array,
            default: []
        },
        infoBatchPrice: {
            type: Number,
            default: 0
        },
        infoRetailPrice: {
            type: Number,
            default: 0
        },
        infoInPrice: {
            type: Number,
            default: 0
        },
    },
    components:{
        goodsSpecTabs
    },
    data() {
        return {
            tableLoading: false,
            tableCulumns: [
                {
                    title: '',
                    type: 'selection',
                    width: 60
                },
                {
                    title: '#',
                    type: 'index',
                    width: 60
                },
                {
                    title: '规格',
                    key: 'goodsSpecs',
                    width: 200,
                    render: (h, params) => {
                        return h(goodsSpecTabs, {
                            props:{
                                tags: params.row.goodsSpecs,
                                color: '#2d8cf0'
                            }
                        });
                    }
                },
                {
                    title: '基础批发价',
                    key: 'batchPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        let useSpec = params.row.useSpec;
                        if (useSpec) {
                            return h('span', params.row.batchPrice);
                        }else {
                            return h('InputNumber', {
                                props: {
                                    min: 0,
                                    value: self.details[params.index][params.column.key]
                                },
                                style: {
                                    width: '100%'
                                },
                                on: {
                                    'on-blur' (event) {
                                        let row = self.details[params.index];
                                        row[params.column.key] = event.target.value;
                                    }
                                }
                            });
                        }
                    }
                },
                {
                    title: '基础市场价',
                    key: 'retailPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        let useSpec = params.row.useSpec;
                        if (useSpec) {
                            return h('span', params.row.retailPrice);
                        }else {
                            return h('InputNumber', {
                                props: {
                                    min: 0,
                                    value: self.details[params.index][params.column.key]
                                },
                                style: {
                                    width: '100%'
                                },
                                on: {
                                    'on-blur' (event) {
                                        let row = self.details[params.index];
                                        row[params.column.key] = event.target.value;
                                    }
                                }
                            });
                        }
                    }
                },
                {
                    title: '参考进货价',
                    key: 'inPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        let useSpec = params.row.useSpec;
                        if (useSpec) {
                            return h('span', params.row.inPrice);
                        }else {
                            return h('InputNumber', {
                                props: {
                                    min: 0,
                                    value: self.details[params.index][params.column.key]
                                },
                                style: {
                                    width: '100%'
                                },
                                on: {
                                    'on-blur' (event) {
                                        let row = self.details[params.index];
                                        row[params.column.key] = event.target.value;
                                    }
                                }
                            });
                        }
                    }
                },
                {
                    title: '等级价',
                    key: 'levelPrice',
                    width: 100,
                    render: (h, params) => {
                        let self = this;
                        let useSpec = params.row.useSpec;
                        if (useSpec) {
                            return '';
                        }else {
                            return h('a', {
                                props: {
                                    href: 'javascript:void(0)'
                                },
                                on: {
                                    click: () => {
                                        // self.openLevelModal();
                                    }
                                }
                            }, '去设置');
                        }
                    }
                },
                {
                    title: '指定价',
                    key: 'fixedPrice',
                    width: 100,
                    render: (h, params) => {
                        let self = this;
                        let useSpec = params.row.useSpec;
                        if (useSpec) {
                            return '';
                        }else {
                            return h('a', {
                                props: {
                                    href: 'javascript:void(0)'
                                },
                                on: {
                                    click: () => {
                                        // self.openfixedModal();
                                    }
                                }
                            }, '去设置');
                        }
                    }
                }
            ],
            batchPrices: this.infoBatchPrice,
            retailPrices: this.infoRetailPrice,
            inPrices: this.infoInPrice,
        }
    },
    methods: {
        
    }
    
}
</script>

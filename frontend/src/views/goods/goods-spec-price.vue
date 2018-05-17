<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <h3>批量设置: </h3>
        <Row class="row-margin-bottom" :gutter="25">
            <i-col span="5">
                <span>批发价:</span>
                <Input number v-model="batchPrices" style="80%">
                    <Button slot="append" size="small" icon="checkmark" @click="updateBasePrice('batchPrice', -1, batchPrices)"></Button>
                </Input>
            </i-col>
            <i-col span="5">
                <span>市场价:</span>
                <Input number v-model="retailPrices" style="80%">
                    <Button slot="append" size="small" icon="checkmark" @click="updateBasePrice('retailPrice', -1, retailPrices)"></Button>
                </Input>
            </i-col>
            <i-col span="5">
                <span>参考进货价:</span>
                <Input :min="0" v-model="inPrices" style="80%">
                    <Button slot="append" size="small" icon="checkmark" @click="updateBasePrice('inPrice', -1, inPrices)"></Button>
                </Input>
            </i-col>
        </Row>
        <Row class="row-margin-bottom">
            <a href="javascript:void(0)" style="margin-right: 25px" >
                <Icon type="ios-people"></Icon>设置规格等级价
            </a>
            <a href="javascript:void(0)" style="margin-right: 25px" >
                <Icon type="ios-person"></Icon>设置规格指定价
            </a>
        </Row>
        <Table stripe highlight-row :loading="tableLoading" 
                :columns="tableCulumns" :data="tableData" ref="goodsDetailTable" 
                @on-selection-change="chooseDetails" 
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
        infoId: {
            type: String|Number,
            required: true
        },
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
            tableData: this.details,
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
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.tableData[params.index][params.column.key]
                            },
                            style: {
                                width: '100%'
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.tableData[params.index];
                                    let oldValue = parseFloat(row.batchPrice);
                                    let newValue = parseFloat(event.target.value);
                                    row[params.column.key] = event.target.value;
                                    if (newValue < 0 || isNaN(newValue)) {
                                        row[params.column.key] = 0;
                                    }
                                    if (oldValue !== newValue && !isNaN(newValue)) {
                                            self.updateBasePrice('batchPrice', row.id, newValue);
                                    }
                                }
                            }
                        });
                    }
                },
                {
                    title: '基础市场价',
                    key: 'retailPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.tableData[params.index][params.column.key]
                            },
                            style: {
                                width: '100%'
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.tableData[params.index];
                                    let oldValue = parseFloat(row.retailPrice);
                                    let newValue = parseFloat(event.target.value);
                                    row[params.column.key] = event.target.value;
                                    if (newValue < 0 || isNaN(newValue)) {
                                        row[params.column.key] = 0;
                                    }
                                    if (oldValue !== newValue && !isNaN(newValue)) {
                                            self.updateBasePrice('retailPrice', row.id, newValue);
                                    }
                                }
                            }
                        });
                    }
                },
                {
                    title: '参考进货价',
                    key: 'inPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.tableData[params.index][params.column.key]
                            },
                            style: {
                                width: '100%'
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.tableData[params.index];
                                    let oldValue = parseFloat(row.inPrice);
                                    let newValue = parseFloat(event.target.value);
                                    row[params.column.key] = event.target.value;
                                    if (newValue < 0 || isNaN(newValue)) {
                                        row[params.column.key] = 0;
                                    }
                                    if (oldValue !== newValue && !isNaN(newValue)) {
                                            self.updateBasePrice('inPrice', row.id, newValue);
                                    }
                                }
                            }
                        });
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
            currDetails: [],
        }
    },
    methods: {
        chooseDetails(data) {
            this.currDetails = data;
        },

        updateBasePrice(type, detailId, newValue) {
            if (isNaN(newValue)  || newValue < 0) {
                this.$Message.info('价格必须是大于等于0的数据');
                return;
            }
            let reqData = {
                infoId: this.infoId
            };
            if (type === 'batchPrice') {
                reqData.batchPrice = newValue;
            }else if (type === 'retailPrice') {
                reqData.retailPrice = newValue;
            }else if (type === 'inPrice') {
                reqData.inPrice = newValue;
            }else {
                this.$Message.info('参数错误');
                return;
            }
            let ids = []; //修改的详情Id
            if (!detailId || detailId < 0) {
                //按选择项进行修改
                if (this.currDetails.length <=0) {
                    this.$Message.info('请先选择需要修改的详情信息');
                    return;
                }
                for(let i=0; i<this.currDetails.length; i++) {
                    let row = this.currDetails[i];
                    ids.push(row.id);
                }
            }else {
                //修改具体某一条记录
                ids.push(detailId);
            }
            reqData.detailIds = ids;
            console.log(reqData)
            util.ajax.put('/goods/price/update', reqData)
                .then((response) => {
                    this.$Message.success('价格修改成功');
                    this.tableData = response.data;
                    //把更新后的数据反映到父级数据，更新父级数据
                    this.$emit('change', this.tableData, this.batchPrices, this.retailPrices, this.inPrices);
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },
        
    }
    
}
</script>

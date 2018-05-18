<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <Tabs v-model="currPane" :animated="false">
            <TabPane name="levelPrice" label="等级价配置">
                <Row class="row-margin-bottom">
                    <i-col span="10" >
                        <Row type="flex" justify="start">
                            <span style="margin-right:30px;">基础批发价: <strong>{{baseBatchPrice}}</strong></span>
                            <span style="margin-right:30px;">基础市场价: <strong>{{baseRetailPrice}}</strong></span>
                        </Row>
                    </i-col>
                    <i-col span="14" >
                        <Row type="flex" justify="end" >
                            <ButtonGroup >
                                <Button type="primary" icon="calculator" @click="autoCountBatchPrice">自动计算折扣批发价</Button>
                                <Button type="info" icon="calculator" @click="autoCountRetailPrice">自动计算折扣市场价</Button>
                                <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveCustomerCategoryPrice">确认保存</Button>
                            </ButtonGroup>
                        </Row>
                    </i-col>
                </Row>
                <Table stripe highlight-row :loading="saveLoading" 
                        :columns="levelCulumns" :data="levelData" ref="levelTable" 
                        no-data-text="还没有维护客户分类信息，请先维护客户分类信息"
                        style="width: 100%;" size="small">
                </Table>
            </TabPane>
            <TabPane name="fixedPrice" label="指定价配置">
                <Row class="row-margin-bottom" :gutter="20">
                    <i-col span="12">
                        <span style="margin-right:20px;">基础批发价: <strong>{{baseBatchPrice}}</strong></span>
                        <span style="margin-right:20px;">基础市场价: <strong>{{baseRetailPrice}}</strong></span>
                    </i-col>
                    <i-col span="12">
                        <Row type="flex" justifiy="end">
                            <customer-select ref="customerSelect" v-model="addCustomerId" @on-change="onCustomerSelected" style="width:80%"></customer-select>
                            <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveCustomerPrice">确认保存</Button>
                        </Row>
                    </i-col>
                </Row>
                <Table stripe highlight-row :loading="saveLoading" 
                        :columns="fixedCulumns" :data="fiexdData" ref="fixedTable" 
                        no-data-text="请通过上面客户选择框选择客户"
                        style="width: 100%;" size="small">
                </Table>
            </TabPane>
        </Tabs>
    </div>
    
</template>

<script>
import util from '@/libs/util.js';
import customerSelect from '@/views/selector/customer-select.vue';


export default {
    name: 'goods-price-rule',
    props: {
        detailIds: {
            type: Array,
            default: []
        },
        toPane: {
            type: String,
            default: 'levelPrice'
        },
        baseBatchPrice: {
            type: Number,
            required: true
        },
        baseRetailPrice: {
            type: Number,
            required: true
        }
    },
    components: {
        customerSelect
    },
    data() {
        return {
            currPane: this.toPane,
            saveLoading: false,
            customerCategoryList: [],
            levelData: [],
            levelCulumns: [
                {
                    title: '客户类别编号',
                    key: 'customerCategoryId',
                    width: 120
                },
                {
                    title: '客户类别',
                    key: 'customerCategoryName',
                    minWidth: 170,
                },
                {
                    title: '批发折扣率',
                    key: 'batchDiscount',
                    width: 130,
                    render: (h, params) => {
                        let label = params.row.batchDiscount + '%';
                        return h('span', label);
                    }
                },
                {
                    title: '市场折扣率',
                    key: 'retailDiscount',
                    width: 130,
                    render: (h, params) => {
                        let label = params.row.retailDiscount + '%';
                        return h('span', label);
                    }
                },
                {
                    title: '批发折扣价',
                    key: 'batchPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.levelData[params.index][params.column.key]
                            },
                            style: {
                                width: '100%'
                            },
                            on: {
                                'on-blur' (event) {
                                    params.row[params.column.key] = event.target.value;
                                    self.$set(self.levelData, params.index, params.row);
                                }
                            }
                        });
                    }
                },
                {
                    title: '市场折扣价',
                    key: 'retailPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.levelData[params.index][params.column.key]
                            },
                            style: {
                                width: '100%'
                            },
                            on: {
                                'on-blur' (event) {
                                    params.row[params.column.key] = event.target.value;
                                    self.$set(self.levelData, params.index, params.row);
                                }
                            }
                        });
                    }
                }
            ],

            fiexdData: [],
            fixedCulumns: [
                {
                    title: '客户ID编号',
                    key: 'customerId',
                    width: 120
                },
                {
                    title: '客户名称',
                    key: 'customerName',
                    minWidth: 170,
                },
                {
                    title: '批发折扣价',
                    key: 'batchPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.fiexdData[params.index][params.column.key]
                            },
                            style: {
                                width: '100%'
                            },
                            on: {
                                'on-blur' (event) {
                                    params.row[params.column.key] = event.target.value;
                                    self.$set(self.fiexdData, params.index, params.row);
                                }
                            }
                        });
                    }
                },
                {
                    title: '市场折扣价',
                    key: 'retailPrice',
                    minWidth: 150,
                    render: (h, params) => {
                        let self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.fiexdData[params.index][params.column.key]
                            },
                            style: {
                                width: '100%'
                            },
                            on: {
                                'on-blur' (event) {
                                    params.row[params.column.key] = event.target.value;
                                    self.$set(self.fiexdData, params.index, params.row);
                                }
                            }
                        });
                    }
                }
            ],
            addCustomerId: '',
        }
    },
    methods: {
        initData() {
            this.loadCustomerCategoryRule();
            this.fiexdData = [];
            this.currPane = 'levelPrice';
            this.addCustomerId = '';
        },
        loadCustomerCategoryRule() {
            console.log(this.detailIds);
            if (this.detailIds.length <= 0) {
                this.$Notice.error({
                    title: '参数数据错误',
                    desc: '获取商品详情编号失败'
                });
                return;
            }
            //注意，如果details查询的是多个detail的值时,返回的是所有客户信息而已，不包含价格记录信息
            let request = {
                goodsDetailIds: this.detailIds
            }
            this.saveLoading = true;
            util.ajax.post('/goods/price/category/list', request)
                .then((response) => {
                    this.saveLoading = false;
                    this.levelData = response.data;
                })
                .catch((error) => {
                    this.saveLoading = false;
                    util.errorProcessor(this, error);
                })
        },

        onCustomerSelected(customerId, customer) {
            if (!customer || !customer.id) {
                return;
            }
            //看看列表中是否已经存在了，如果已经存在了，不再添加
            for (let i=0; i<this.fiexdData.length; i++) {
                if (customer.id === this.fiexdData[i].customerId) {
                    //已经存在
                    return;
                }
            }

            let item = {
                customerId: customer.id,
                customerName: customer.name,
                batchPrice: '',
                retailPrice: '',
            }
           
            //如果detailIds的长度为1的时候，去把后台历史数据取出
            if (this.detailIds.length === 1 && this.detailIds[0] > 0) {
                let request = {
                    customerId: customer.id,
                    goodsId: this.detailIds[0]
                }
                util.ajax.get("/goods/price/customer/", {params: request})
                    .then((response) => {
                        let data = response.data;
                        console.log(data);
                        if (data && data.id) {
                            item.batchPrice = data.batchPrice;
                            item.retailPrice = data.retailPrice;
                        }
                        console.log(item);
                        this.fiexdData.push(item);
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                        this.fiexdData.push(item); //后台获取失败的情况下也加上
                    });
            }else {
                this.fiexdData.push(item);
            }
        },

        autoCountBatchPrice() {
            //计算所有客户类中的批发折扣价
            for(let i=0; i<this.levelData.length; i++) {
                let item = this.levelData[i];
                let batchDiscount = parseFloat(item.batchDiscount);
                let baseBatch = parseFloat(this.baseBatchPrice);
                item.batchPrice = (baseBatch * batchDiscount / 100).toFixed(2);

                this.$set(this.levelData, i, item);
            }
        },

        autoCountRetailPrice() {
            //计算所有客户类中的市场折扣价
            for(let i=0; i<this.levelData.length; i++) {
                let item = this.levelData[i];
                let retailDiscount = parseFloat(item.retailDiscount);
                let baseRetail = parseFloat(this.baseRetailPrice);
                item.retailPrice = (baseRetail * retailDiscount / 100).toFixed(2);

                this.$set(this.levelData, i, item);
            }
        },

        saveCustomerCategoryPrice() {
            let request = {
                goodsDetailIds: this.detailIds,
                customerCategoryPrices: this.levelData
            };
            this.saveLoading = true;
            util.ajax.post('/goods/price/category/save', request)
                .then((response) => {
                    this.saveLoading = false;
                    this.$Message.success('保存成功');
                })
                .catch((error) => {
                    this.saveLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        saveCustomerPrice() {
            let request = {
                goodsDetailIds: this.detailIds,
                customerPrices: this.fiexdData
            };
            console.log(request);
            this.saveLoading = true;
            util.ajax.post('/goods/price/customer/save', request)
                .then((response) => {
                    this.saveLoading = false;
                    this.$Message.success('保存成功');
                })
                .catch((error) => {
                    this.saveLoading = false;
                    util.errorProcessor(this, error);
                });
        }
    }


}
</script>


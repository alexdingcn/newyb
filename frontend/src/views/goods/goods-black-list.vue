    
<style lang="less">
    @import './goods-list.less';
</style>

<template>
    <div class="layout">
        <Layout>
            <Sider hide-trigger class="bg-white block-sidebar">
                <RadioGroup v-model="selectedBlackListType" vertical>
                    <Radio label="block-customer-category">
                        <span>客户类型屏蔽</span>
                    </Radio>
                    <Radio label="block-region">
                        <span>地区屏蔽</span>
                    </Radio>
                    <Radio label="block-customer">
                        <span>客户屏蔽</span>
                    </Radio>
                </RadioGroup>
            </Sider>
            <Layout v-if="selectedBlackListType == 'block-customer-category'" class="block-content">
                <Header>
                    <h3>                                  
                        <Icon type="ribbon-b"></Icon>
                        以下客户类型看不到该商品
                    </h3>
                    </Header>
                <Content>
                    请选择客户类型：
                    <div style="border-bottom: 1px solid #e9e9e9;padding-bottom:6px;margin-bottom:6px;">
                        <!-- <Checkbox
                            :indeterminate="indeterminate"
                            :value="checkAll"
                            @click.prevent.native="handleCheckAll">全选</Checkbox> -->
                    </div>
                    <CheckboxGroup v-model="goodsBlackList.customerCategoryIds" @on-change="onChangeCategory">
                        <Checkbox v-for="category in customerCategoryList" :key="category.id" :label="category.id">{{ category.name }}</Checkbox>
                    </CheckboxGroup>
                </Content>
            </Layout>
            <Layout v-if="selectedBlackListType == 'block-region'" class="block-content">
                <Header>
                    <h3>
                        <Icon type="map"></Icon>
                        以下区域看不到该商品
                    </h3>
                </Header>
                <Content>
                    <Row :gutter="16">
                        <i-col span="12">
                            <al-cascader v-model="addRegionQuery" level="1"/>
                        </i-col>
                        <i-col span="5">
                            <Button type="success" @click="addRegionToBlackList">添加</Button>
                        </i-col>
                    </Row>
                    <Table :columns="regionColumns" :data="goodsBlackList.regions" class="margin-top-10" stripe></Table>
                </Content>
            </Layout>
            <Layout v-if="selectedBlackListType == 'block-customer'" class="block-content">
                <Header>
                    <h3>
                        <Icon type="ios-close"></Icon>
                        以下客户看不到该商品
                    </h3>
                </Header>
                <Content>
                    <Row :gutter="16">
                        <i-col span="12">
                            <customer-select ref="customerSelect" v-model="addCustomerId" @on-change="onCustomerSelected"></customer-select>
                        </i-col>
                        <i-col span="5">
                            <Button type="success" @click="addCustomerToBlackList">添加</Button>
                        </i-col>
                    </Row>
                    <Table :columns="customerColumns" :data="goodsBlackList.customerIds" class="margin-top-10" stripe></Table>
                </Content>
            </Layout>
        </Layout>
    </div>
</template>
<script>
import util from '@/libs/util.js';
import customerSelect from "@/views/selector/customer-select.vue";

export default {
    name: 'goods-black-list',
    props: ['value'],
    data() {
        return {
            addCustomerId: 0,
            addCustomerQuery: '',
            addRegionQuery: [],
            goodsBlackList: {
                customerCategoryIds: [],
                regions: [],
                customerIds: []
            },
            customerCategoryList: [],
            selectedBlackListType: 'block-customer-category',

            regionColumns: [
                    {
                        type: 'index',
                    },
                    {
                        title: '地区',
                        key: 'name'
                    },
                    {
                        title: '操作',
                        key: 'action',
                        align: 'center',
                        render: (h, params) => {
                            return h('Button', {
                                props: {
                                    type: 'error',
                                    size: 'small'
                                },
                                on: {
                                    click: () => {
                                        this.removeRegion(params.index)
                                    }
                                }
                            }, '删除');
                        }
                    }
                ],
            customerColumns: [
                    {
                        type: 'index',
                    },
                    {
                        title: '客户ID',
                        key: 'id'
                    },
                    {
                        title: '客户名',
                        key: 'name'
                    },
                    {
                        title: '操作',
                        key: 'action',
                        align: 'center',
                        render: (h, params) => {
                            return h('Button', {
                                props: {
                                    type: 'error',
                                    size: 'small'
                                },
                                on: {
                                    click: () => {
                                        this.removeCustomer(params.index)
                                    }
                                }
                            }, '删除');
                        }
                    }
                ],
        }
    },
    components: {
        customerSelect
    },

    watch: {
        value (val) {
            if (val) {
                if (val.customerCategoryIds) {
                    this.goodsBlackList.customerCategoryIds = val.customerCategoryIds;
                }
                if (val.customerIds) {
                    this.goodsBlackList.customerIds = val.customerIds;
                }
                if (val.regions) {
                    this.goodsBlackList.regions = val.regions;
                }
            }
        }
    },
    mounted() {
        this.loadCustomerCategory();
    },
    methods: {
        loadCustomerCategory() {
            util.ajax.get('/customer/category/list')
                .then((response) => {
                    console.log(response.data);
                    this.customerCategoryList = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        },
        onChangeCategory() {
            this.setValue();
        },
        addCustomerToBlackList() {
            if (this.addCustomerId) {
                // TODO: existed?
                this.goodsBlackList.customerIds.push({
                    id: this.addCustomerId, 
                    name: this.addCustomerQuery
                });
                this.setValue();
            }
        },
        addRegionToBlackList() {
            if (this.addRegionQuery && this.addRegionQuery.length === 2) {
                // TODO: existed?
                this.goodsBlackList.regions.push({
                    code: JSON.stringify(this.addRegionQuery),
                    name: this.addRegionQuery[0].name + this.addRegionQuery[1].name
                });
                this.setValue();
            }
        },
        onCustomerSelected(customerId, customer) {
            if (customerId) {
                this.addCustomerQuery = customer.name;
                this.addCustomerToBlackList();
            }
        },
        removeRegion (index) {
            if (this.goodsBlackList && this.goodsBlackList.regions) {
                this.goodsBlackList.regions.splice(index, 1);
                this.setValue();
            }
        },
        removeCustomer (index) {
            if (this.goodsBlackList && this.goodsBlackList.customerIds) {
                this.goodsBlackList.customerIds.splice(index, 1);
                this.setValue();
            }
        },
        setValue () {
            this.$nextTick(() => {
                this.$emit('input', this.goodsBlackList);
                this.$emit('on-change', this.goodsBlackList);
            });
        },
    }
}
</script>

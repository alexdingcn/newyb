<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Modal v-model="isShowModal" :width="60" :mask-closable="false" :closable="false" title="查询获取客户">
            <Form ref="searchForm" :model="formItem" :label-width="100">
                <Row type="flex" justify="center">
                    <i-col span="12">
                        <FormItem label="客户分组">
                            <Select v-model="formItem.categoryId" clearable >
                                <Option v-for="item in categorys" :value="item.id" :key="item.id">{{ item.name }}</Option>
                            </Select>
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="客户编号">
                            <Input type="text" v-model="formItem.customerNo" />
                        </FormItem>
                    </i-col>
                </Row>
                <Row type="flex" justify="center" >
                    <i-col span="12">
                        <FormItem label="客户名称">
                            <Input type="text" v-model="formItem.customerName" />
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="简称">
                            <Input type="text" v-model="formItem.shorName" />
                        </FormItem>
                    </i-col>
                </Row>
                <Row type="flex" justify="center" >
                    <Button type="primary" size="small" icon="ios-search" class="margin-let-10" @click="searchBtnClicked">查询</Button>
                </Row>
            </Form>

            <Row type="flex" justify="center" align="middle" class="margin-top-20">
                <Table border highlight-row :columns="orderColumns" :data="customersData" 
                    :loading="customerTableLoading" 
                    @on-row-click="tableRowClick" 
                    ref="customersTable" style="width: 100%;" size="small">
                </Table>
            </Row>
            <Row type="flex" justify="end">
                <Page size="small" :total="customersCount" :current="currentPage" :page-size="tableCurrPageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
            <Row type="flex" justify="center" align="middle">
                <div>当前选择客户: <strong>{{currChooseShow}}</strong></div>
            </Row>

            <div slot="footer">
                <Row >
                    <i-col span="6" offset="6">
                        <Button size="small" type="primary" @click="ok" long>
                            <span >确定</span>
                        </Button>
                    </i-col>
                    <i-col span=6 class="padding-left-10">
                        <Button size="small" @click="closedModal" long>取消</Button>
                    </i-col>
                </Row>
            </div>
        </Modal>

        <Modal v-model="showCustomerDetailModal" title="客户详情展示" :width="75" @on-cancel="showCustomerDetailModalCancel">
            <customer-info action="see" :categorys="categorys" :editCustomer="seeCustomer"></customer-info>
            <div slot="footer"></div>
        </Modal>
    </div>
    

</template>

<script>
import util from '@/libs/util.js';
import customerInfo from '@/views/customer/customer-info.vue';

export default {
    name: 'customer-search',
    props: {
        showModal: {
            type: Boolean,
            default: false
        }
    },
    components: {
        customerInfo
    },
    data () {
        return {
            isShowModal: false,
            categorys: [],
            formItem: {
                categoryId: '',
                customerNo: '',
                customerName: '',
                shorName: ''
            },
            currentPage: 1,
            customersCount: 0,
            tableCurrPageSize: 20,
            customersData: [],
            customerTableLoading: false,
            currChooseShow: '',
            currChooseItem: null,
            orderColumns: [
                {
                    type: 'index',
                    width: 60,
                    title: '序号',
                    align: 'center'
                },
                {
                    title: '客户编号',
                    key: 'customerNo',
                    align: 'center',
                    sortable: true
                },
                {
                    title: '客户名称',
                    key: 'name',
                    align: 'center',
                    sortable: true
                },
                {
                    title: '是否禁用',
                    key: 'disable',
                    align: 'center',
                    render: (h, params) => {
                        let isTrue = params.row.disable;
                        return h('div', [
                            h('Icon', {
                                props: {
                                    type: isTrue ? 'close-circled' : 'checkmark-circled',
                                    color: isTrue ? '#e96500' : '#00a854'
                                }
                            }),
                            h('strong', isTrue ? '已禁用' : '已启用')
                        ]);
                    }
                },
                {
                    title: '可经营特殊管理药品',
                    key: 'canSaleSpecial',
                    align: 'center',
                    render: (h, params) => {
                        let isTrue = params.row.canSaleSpecial;
                        return h('strong', isTrue ? '可以' : '禁止');
                    }
                },
                {
                    title: '含麻黄碱药品限购',
                    key: 'limitSpecial',
                    align: 'center',
                    render: (h, params) => {
                        return h('strong', params.row.limitSpecial ? '是' : '否');
                    }
                },
                {
                    title: '查看详情',
                    align: 'center',
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                size: 'small',
                                type: 'primary',
                                icon: 'eye'
                            },
                            on: {
                                click: () => {
                                    this.showCustomerDetail(params.row);
                                }
                            }
                        }, '详情');
                    }
                }
            ],
            showCustomerDetailModal: false,
            seeCustomer: null
        };
    },
    watch: {
        showModal (data) {
            if (data) {
                this.isShowModal = data;
                this.initData();
            }
        },
        currChooseItem (data) {
            if (!data || data === null) {
                this.currChooseShow = '';
            } else {
                this.currChooseShow = data.name + ' ' + data.customerNo;
            }
        }
    },
    methods: {
        initData () {
            util.ajax.get('/customer/category/list')
                .then((res) => {
                    this.categorys = res.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                }
                );
        },

        searchBtnClicked () {
            let reqData = this.formItem;
            reqData.page = this.currentPage;
            reqData.size = this.tableCurrPageSize;
            this.customerTableLoading = true;
            util.ajax.get('/customer/list', {params: reqData})
                .then((response) => {
                    this.customersData = response.data.data;
                    this.customersCount = response.data.count;
                    this.currChooseItem = null;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                }
                );
            this.customerTableLoading = false;
        },

        showCustomerDetail (data) {
            this.showCustomerDetailModal = true;
            this.seeCustomer = data;
        },
        showCustomerDetailModalCancel () {
            this.showCustomerDetailModal = false;
            this.seeCustomer = null;
        },

        pageChange (data) {
            this.currentPage = data;
            this.searchBtnClicked();
        },

        tableRowClick (data) {
            this.currChooseItem = data;
        },

        ok () {
            this.isShowModal = false;
            this.$emit('modal-close');
            this.$emit('choosed', this.currChooseItem);
        },

        closedModal () {
            this.isShowModal = false;
            this.$emit('modal-close');
        }
    }

};
</script>

<style >
.ivu-form-item {
    margin-bottom: 5px;
}
</style>



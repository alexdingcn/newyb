<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Form ref="form" :model="formItem" :label-width="80" >
            <Row type="flex" justify="start">
                <i-col span="8">
                    <FormItem label="收款日期">
                        <DatePicker v-model="formItem.createdTime" type="datetime" 
                            format="yyyy-MM-dd HH:mm:ss"></DatePicker>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="收款方式">
                        <Select v-model="formItem.payMethod">
                            <Option v-for="item in payMethods" :value="item.value" :key="item.value">{{ item.label }}</Option>
                        </Select>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="收款金额">
                        <Input type="text" v-model="payAmount" placeholder="请输入金额"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row :gutter="16">
                <i-col span="12">
                    <FormItem label="备注">
                        <Input type="text" v-model="formItem.comment" />
                    </FormItem>
			    </i-col>
                <i-col span="8">
                    <Button type="success" icon="ios-checkmark" @click="setAllPaid">全款结清</Button>
                    <Button type="primary" icon="ios-plus" :loading="saveLoading" @click="addPayment">添加</Button>
                </i-col>
            </Row>
        </Form>

        <h3>支付历史</h3>
        <Row type="flex" justify="center">
            <Table ref="table" size="small" :data="tabData" :columns="tabColumns" 
                border highlight-row :loading="tabLoading" style="width: 100%;">
            </Table>
        </Row>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';
import optionSelect from "@/views/selector/option-select.vue";

export default {
    name: 'sell-order-payment',
    props: {
        order: {
            type: Object,
            required: true
        }
    },
    components: {
        optionSelect
    },
    data () {
        return {
            formItem: {
                orderId: this.order.id,
                createdTime: moment().format('YYYY-MM-DD HH:mm:ss'),
            },
            payAmount: 0,
            tabLoading: false,
            saveLoading: false,
            tabData: [],
            payMethods: [
                { value: '1', label: '现金' },
                { value: '2', label: '支付宝' },
                { value: '3', label: '微信' },
                { value: '4', label: '银行转账' },
            ],
            tabColumns: [
                {
                    type: 'index',
                    width: 50
                },
                {
                    title: '收款时间',
                    key: 'createdTime',
                    width: 150,
                    align: 'center',
                    render: (h, params) => {
                        let createdTime = params.row.createdTime;
                        return h('span', createdTime ? moment(createdTime).format('YYYY-MM-DD HH:mm:ss') : '');
                    }
                },
                {
                    title: '收款金额',
                    key: 'payAmount',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '收款方式',
                    key: 'payMethod',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                        for (var i = 0; i < this.payMethods.length; i++) {
                            if (this.payMethods[i].value === params.row.payMethod) {
                                return h('span', this.payMethods[i].label);
                            }
                        }
                    }
                },
                {
                    title: '备注',
                    key: 'comment',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '操作',
                    width: 200,
                    align: 'center',
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'ghost',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    
                                }
                            }
                        }, '查看');
                    }
                }
            ],
        };
    },
    watch: {
        order (data) {
            this.refreshTableData();
        }
    },
    methods: {
        initFormItem () {
            this.payAmount = 0;
            this.formItem = {
                orderId: this.order.id,
                status: 'paid',
                createdTime: moment().format('YYYY-MM-DD HH:mm:ss')
            };
        },

        refreshTableData () {
            if (!this.order.id || this.order.id < 0) {
                return;
            }
            this.initFormItem();
            this.tabLoading = true;
            var self = this;
            util.ajax.post('/sell/payment/list', {sellOrderId: this.order.id} )
                .then((response) => {
                    self.tabLoading = false;
                    self.tabData = response.data.data;
                })
                .catch((error) => {
                    self.tabLoading = false;
                    util.errorProcessor(self, error);
                });
        },

        addPayment() {
            if (!this.payAmount || this.payAmount == 0) {
                this.$Message.info("请输入收款金额");
            }
            this.formItem.payAmount = this.payAmount;
            var self = this;
            this.saveLoading = true;
            util.ajax.post('/sell/payment/add', this.formItem )
                .then((response) => {
                    self.refreshTableData();
                    self.saveLoading = false;
                    this.$emit('payment-updated', this.formItem);
                })
                .catch((error) => {
                    self.saveLoading = false;
                    util.errorProcessor(self, error);
                });
        },
        setAllPaid() {
            this.payAmount = this.order.totalAmount - this.order.paidAmount;
        }
    }

};
</script>

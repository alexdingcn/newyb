<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Card>
            <p>
                <Icon type="login-in"></Icon>
                销售退货制单
            </p>
            <div slot="extra"> 
                <ButtonGroup size="small">
                    <Button type="primary" icon="plus" :loading="loading" >销售单提取</Button>
                    <Button type="success" icon="checkmark" :loading="loading" @click="saveSubmit">确认保存</Button>
                </ButtonGroup>
            </div>

            <Form ref="applyForm" :model="applyForm" :label-width="100">
                <Row class="row-margin-bottom">
                    <i-col span="6">
                        <FormItem label="客户" :error="customerError">
                            <customer-select v-if="!applyForm.haveSellOrder" v-model="applyForm.customerId" @on-change="customerChange"></customer-select>
                            <i-input v-else type="text" :disabled="applyForm.haveSellOrder" v-model="applyForm.customerName"></i-input>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="销售员" :error="saleError">
                            <sale-select v-model="applyForm.saleId" @on-change="saleChange"></sale-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="温控方式" >
                          <option-select optionType="TEMPER_CONTROL" v-model="applyForm.temperControlId"></option-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="是否为销售单提取" :label-width="120">
                            <Checkbox v-model="applyForm.haveSellOrder" disabled ></Checkbox>
                        </FormItem>
                    </i-col>
                </Row>
                <Row class="row-margin-bottom">
                    <i-col span="6">
                        <FormItem label="客户代表">
                            <Select v-model="applyForm.customerRepId" filterable clearable placeholder="请选择客户代表" :disabled="!applyForm.customerId" @on-change="customerRepChange" >
                                <Option v-for="item in customerRepList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                            </Select>
                        </FormItem>
                    </i-col>
                    <i-col span="8">
                        <FormItem label="退货原因">
                            <i-input type="text" v-model="applyForm.backReason"></i-input>
                        </FormItem>
                    </i-col>
                    <i-col span="8">
                        <FormItem label="备注信息">
                            <i-input type="text" v-model="applyForm.comment"></i-input>
                        </FormItem>
                    </i-col>
                </Row>
                <Row class="row-margin-bottom">
                    <i-col span="6">
                        <FormItem label="仓库点" :error="warehouseError">
                            <warehouse-select v-model="applyForm.warehouseId" @on-change="warehouseChanage" :disabled="applyForm.haveSellOrder"></warehouse-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="送货单号">
                            <i-input type="text" v-model="applyForm.shipNo"></i-input>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="商品" :error="goodsError">
                            <good-select ref="goodsSelect" v-model="goodsId" :disabled="applyForm.haveSellOrder" @on-change="goodsSelectChange"></good-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="免零" :error="freeAmountError">
                            <i-input number v-model="applyForm.freeAmount"></i-input>
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
            
            <h4 style="margin-top:20px; margin-bottom:5px;">
                <Row type="flex" justify="start">
                    <span>总退货数量: <strong style="color:red;">{{totalBackQuantity}}</strong></span>
                    <span class="margin-left-50">总金额: <strong style="color:red;">{{totalAmount}}</strong></span>
                    <span class="margin-left-50">总退货成本: <strong style="color:red;">{{totalCostAmount}}</strong></span>
                </Row>
            </h4>
            <Table ref="details" border highlight-row :loading="loading" 
                no-data-text="选择商品添加或者从销售单导入" style="width: 100%;" size="small" 
                :columns="detailsColumns" :data="details" 
            >
            </Table>
            <h4 style="margin-top:5px; color: #80848f;">退货商品详情(注意: 退货数量为0的在提交时会自动去除,只保留存在退货数量大于0的产品!)</h4>
        </Card>


        <warehouse-location-modal :openModal="locationModal" :warehouseId="applyForm.warehouseId" @on-ok="chooseLocation" @on-close="locationModalClose"></warehouse-location-modal>

    </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';
import customerSelect from '@/views/selector/customer-select.vue';
import saleSelect from '@/views/selector/sale-select.vue';
import warehouseSelect from '@/views/selector/warehouse-select.vue';
import goodSelect from '@/views/selector/good-select.vue';
import warehouseLocationModal from "@/views/selector/warehouse-location-modal.vue";
import optionSelect from '@/views/selector/option-select.vue';
import inTempVue from '../repertory/in-temp.vue';

export default {
    name: 'sell-back-apply',
    components: {
        customerSelect,
        saleSelect,
        warehouseSelect,
        goodSelect,
        warehouseLocationModal,
        optionSelect
    },
    data() {
        const addWarehouseLocation = (h, location, rowData, index) => {
            let label = location ? location : '';
            return h('div', [
                h('span', label),
                h('Button', {
                    props: {
                        type: 'text',
                        size: 'small',
                        icon: 'edit'
                    },
                    on: {
                        'click': () => {
                            this.openChooseLocation(rowData, index);
                        }
                    }
                })
            ]);
        }
        return {
            customerRepList: [],
            loading: false,
            applyForm: {
                customerId: '',
                saleId: '',
                haveSellOrder: false,
                warehouseId: '',
                freeAmount: 0,
                details: []
            },
            goodsId: '',
            customerError: '',
            saleError: '',
            warehouseError: '',
            goodsError: '',
            freeAmountError: '',
            details: [],
            detailsColumns: [
                {
                    title: '序号',
                    type: 'index',
                    width: 80
                },
                {
                    title: '货号',
                    key: 'goodsId',
                    width: 100 
                },
                {
                    title: "商品名称",
                    key: "goodsName",
                    width: 180
                },
                {
                    title: "生产企业",
                    key: "factoryName",
                    width: 180
                },
                {
                    title: "产地",
                    key: "origin",
                    width: 120
                },
                {
                    title: "规格",
                    key: "spec",
                    width: 120
                },
                {
                    title: "剂型",
                    key: "jx",
                    width: 120
                },
                {
                    title: '单位',
                    key: 'unitName',
                    width: 100
                },
                {
                    title: '批次号',
                    key: 'batchCode',
                    width: 180,
                    render: (h, params) => {
                        let self = this;
                        if (self.applyForm.haveSellOrder) {
                            return params.row.batchCode;
                        }else {
                            return h("Input", {
                                props: {
                                    value: self.details[params.index][params.column.key]
                                },
                                on: {
                                    "on-blur"(event) {
                                        let row = self.details[params.index];
                                        row[params.column.key] = event.target.value;
                                    }
                                }
                            });
                        }
                    }
                },
                {
                    title: '生产日期',
                    key: 'productDate',
                    align: 'center',
                    width: 180,
                    render: (h, params) => {
                        let self = this;
                        return h('DatePicker', {
                            props: {
                                type: 'date',
                                placement: 'top',
                                value: params.row.productDate
                            },
                            on: {
                                'on-change': (date) =>{
                                    let row = self.details[params.index];
                                    row[params.column.key] = date; 
                                }
                            }
                        });
                    }
                },
                {
                    title: '有效期至',
                    key: 'expDate',
                    align: 'center',
                    width: 180,
                    render: (h, params) => {
                        let self = this;
                        return h('DatePicker', {
                            props: {
                                type: 'date',
                                placement: 'top',
                                value: params.row.expDate
                            },
                            on: {
                                'on-change': (date) =>{
                                    let row = self.details[params.index];
                                    row[params.column.key] = date; 
                                }
                            }
                        });
                    }
                },
                {
                    title: '销售数量',
                    key: 'saleQuantity',
                    width: 120
                },
                {
                    title: '已退数量',
                    key: 'alreadyBackQuantity',
                    width: 120,
                },
                {
                    title: '退货数量',
                    key: 'backQuantity',
                    width: 140,
                    render: (h, params) => {
                        var self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.details[params.index][params.column.key]
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.details[params.index];
                                    row[params.column.key] = event.target.value;
                                    self.resetAmount(params.index);
                                }
                            }
                        });
                    }
                },
                {
                    title: '单价',
                    key: 'backPrice',
                    width: 140,
                    render: (h, params) => {
                        var self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.details[params.index][params.column.key]
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.details[params.index];
                                    row[params.column.key] = event.target.value;
                                    self.resetAmount(params.index);
                                }
                            }
                        });
                    }
                },
                {
                    title: '金额',
                    key: 'amount',
                    width: 140
                },
                {
                    title: '退货成本',
                    key: 'costAmount',
                    width: 140,
                    render: (h, params) => {
                        var self = this;
                        return h('Input', {
                            props: {
                                number: true,
                                value: self.details[params.index][params.column.key]
                            },
                            on: {
                                'on-blur' (event) {
                                    let row = self.details[params.index];
                                    row[params.column.key] = event.target.value;
                                }
                            }
                        });
                    }
                },
                {
                    title: '货位',
                    key: 'location',
                    width: 150,
                    render: (h, params) => {
                        return addWarehouseLocation(h, params.row.location, params.row, params.index);
                    }
                },
                {
                    title: '存储条件',
                    key: 'storageConditionName',
                    width: 120
                },
                {
                    title: '药基',
                    key: 'baseMedName',
                    width: 120
                },
                {
                    title: '批准文号',
                    key: 'permitNo',
                    width: 200
                }
            ],
            locationModal: false,
            currEditLocationRow: '',
            currEditLocationIndex: '',
            currCustomer: {}
        }
    },
    computed: {
        totalBackQuantity: function() {
            return this.details.reduce((total, item) => {
                let backQuantity = item.backQuantity ? item.backQuantity : 0;
                return total + parseFloat(backQuantity);
            }, 0);
        },
        totalAmount: function() {
            return this.details.reduce((total, item) => {
                let amount = item.amount ? item.amount : 0;
                return total + parseFloat(amount);
            }, 0);
        },
        totalCostAmount: function() {
            return this.details.reduce((total, item) => {
                let costAmount = item.costAmount ? item.costAmount : 0;
                return total + parseFloat(costAmount);
            }, 0);
        }
    },
    watch: {
        applyForm: (data) => {
            if (data.customerId) {
                this.customerError = '';
            }
            if (data.saleId) {
                this.saleError = '';
            }
            if (data.warehouseId) {
                this.warehouseError = '';
            }
            if (data.freeAmount <= 0) {
                this.freeAmountError = '';
            }
        },
        details: (data) => {
            if (data && data.length > 0) {
                this.goodsError = '';
            }
        },
        totalBackQuantity: (data) => {
            if (data && data > 0) {
                this.goodsError = '';
            }
        },
        
    },
    methods: {
        openChooseLocation(rowData, index) {
            if (!rowData || index === undefined || index < 0) {
                this.$Message.error('获取编辑位置失败');
                return;
            }
            this.currEditLocationRow = rowData;
            this.currEditLocationIndex = index;
            this.locationModal = true;
        },
        chooseLocation(data) {
            this.locationModal = false;
            this.currEditLocationRow.location = data.location;
            this.$set(this.details, this.currEditLocationIndex, this.currEditLocationRow);
        },
        locationModalClose() {
            this.locationModal = false;
            this.currEditLocationIndex = '';
            this.currEditLocationRow = {};
        },
        refreshCustomerRepList(customerId, customerRepId) {
            let reqData = {
                customerId: customerId
            };
            let self = this;
            util.ajax
                .get("/customer/rep/list", { params: reqData })
                .then(response => {
                    self.customerRepList = response.data;
                    if (customerRepId) {
                        this.nextTick().then(() => {
                            self.applyForm.customerRepId = customerId;
                        })
                    }
                })
                .catch(error => {
                    util.errorProcessor(self, error);
                });
        },
        customerChange(customerId, customer) {
            this.currCustomer = customer;
            if (customer && customer.id) {
                this.applyForm.customerId = customer.id;
                this.applyForm.customerName = customer.name;
                this.applyForm.customerRepId = '';
                this.customerRepList = [];
                this.refreshCustomerRepList(customer.id);
            }
            this.details = []; //客户或者仓库的变动，都把详情清空
        },
        customerRepChange(customerRepId) {
            if(!customerRepId) {
                return;
            }
            let customerRep = {};
            for(let i=0;i<this.customerRepList.length; i++) {
                let item = this.customerRepList[i];
                if (customerRepId === item.id) {
                    customerRep = item;
                    break;
                }
            }
            if (customerRep && customerRep.id) {
                this.applyForm.customerRepName = customerRep.name;
            }
        },
        warehouseChanage(id, warehouse) {
            this.applyForm.warehouseName = warehouse.name;
            this.details = [];
        },
        saleChange(saleId, saleUser) {
            if (saleUser.id) {
                this.applyForm.saleName = saleUser.nickname;
            }
        },
        goodsSelectChange(goodsId, goods) {
            if (!this.applyForm.customerId || !this.applyForm.warehouseId) {
                this.$refs.goodsSelect.clearSingleSelect();
                this.$Message.info('请先选择客户和仓库点!');
                return;
            }
            //商品选择时，添加商品信息
            if (!goods || !goods.id) {
                this.$refs.goodsSelect.clearSingleSelect();
                return;
            }
            let item = {
                goodsId: goods.id,
                goodsName: goods.name,
                factoryName: goods.factory,
                origin: goods.origin,
                spec: goods.spec,
                jx: goods.jxName,
                unitName: goods.unitName,
                batchCode: '',
                productDate: '',
                expDate: '',
                saleQuantity: '',
                alreadyBackQuantity: '',
                backQuantity: 0,
                backPrice: 0,
                amount: 0,
                costAmount: 0,
                location: '',
                storageConditionName: goods.storageConditionName,
                baseMedName: goods.baseMedName,
                permitNo: goods.permitNo
            };
            this.details.push(item);
            this.$refs.goodsSelect.clearSingleSelect();
        },
        resetAmount(index) {
            let row = this.details[index];
            if (!row) {
                return;
            }
            let backQuantity = row.backQuantity && !isNaN(row.backPrice) ? row.backQuantity : 0;
            let backPrice = row.backPrice && !isNaN(row.backPrice) ? row.backPrice : 0;
            row.amount = (backQuantity * backPrice).toFixed(2);
            this.$set(this.details, index, row);
        },

        saveSubmit() {
            this.customerError = '';
            this.saleError = '';
            this.warehouseError = '';
            this.goodsError = '';
            if (!this.applyForm.customerId) {
                this.customerError = '请先选择对应的客户!';
                return;
            }
            if (!this.applyForm.saleId) {
                this.saleError = '请选择销售员';
                return;
            }
            if (!this.applyForm.warehouseId) {
                this.warehouseError = '请选择对应仓库点';
                return;
            }
            if (this.totalBackQuantity <= 0) {
                this.goodsError = '选择的退货商品数量需要大于0';
                return;
            }
            if (this.applyForm.freeAmount > 0) {
                this.freeAmountError = '免零金额不能大于0';
                return;
            }
            if (this.applyForm.haveSellOrder) {
                if (!this.applyForm.refOrderId) {
                    this.$$Message.error('系统异常，获取关联订单失败, 请刷新页面重新操作.');
                    return;
                }
                for (let i=0; i < this.details.length; i++) {
                    let item = this.details[i];
                    let backQuantity = item.backQuantity ? item.backQuantity : 0;
                    let saleQuantity = item.saleQuantity ? item.saleQuantity : 0;
                    let alreadyBackQuantity = item.alreadyBackQuantity ? item.alreadyBackQuantity : 0;
                    if ((saleQuantity-alreadyBackQuantity) < backQuantity) {
                        this.$Modal.warning({
                            title: '退货数量错误提示',
                            content: '商品：' + item.goodsName + '的退货数不能超出销售单销售数量.'
                        });
                        return;
                    }
                }
            }
            let self = this;
            this.applyForm.details = this.details;
            this.$Modal.confirm({
                title: '保存确认',
                content: '是否已经确认需要退货的商品信息输入完整并且正确? 提交保存后不可再修改!',
                onCancel: () => {},
                onOk: () => {
                    self.loading = true;
                    util.ajax
                        .post('/sell/back/add', self.applyForm)
                        .then((response) => {
                            self.loading = false;
                            self.$Message.success('退货单提交保存成功.');
                            self.clearForm();
                        })
                        .catch((error) => {
                            self.loading = false;
                            util.errorProcessor(self, error);
                        })
                }
            });
        },

        clearForm() {
            this.applyForm = {
                customerId: '',
                saleId: '',
                haveSellOrder: false,
                warehouseId: '',
                freeAmount: 0,
                details: []
            };
            this.details = [];
            this.customerError = '';
            this.saleError = '';
            this.warehouseError = '';
            this.goodsError = '';
            this.$refs.applyForm.resetFields();
        }

    }
  
}
</script>

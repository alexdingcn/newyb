<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        
        <Form ref="form" :model="formItem" :label-width="100" :rules="formValidate">
		<Row type="flex" justify="start">
			<Col span="6">
				<FormItem label="发货时间">
					<DatePicker v-model="formItem.issuanceDate" type="datetime" 
                        format="yyyy-MM-dd HH:mm" placeholder="请选择发货日期" ></DatePicker>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="货单号">
					<Input type="text" v-model="formItem.shipNumber" placeholder="请输入货单号"></Input>
				</FormItem>
			</Col>
			<Col span="12">
				<FormItem label="发货地址">
					<Input type="text" v-model="formItem.shipAddress" placeholder="请输入发货地址"></Input>
				</FormItem>
			</Col>
		</Row>
		<Row type="flex" justify="start">
			<Col span="6">
				<FormItem label="存储条件">
                    <temper-control-select v-model="formItem.storageMethod"></temper-control-select>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="运输工具">
                    <ship-tool-select v-model="formItem.shipToolId"></ship-tool-select>
				</FormItem>
			</Col>
			<Col span="12">
				<FormItem label="承运单位" prop="shipCompanyId">
                    <ship-company-select v-model="formItem.shipCompanyId"></ship-company-select>
				</FormItem>
			</Col>
		</Row>
		<Row type="flex" justify="start">
			<Col span="6">
				<FormItem label="车牌号">
					<Input type="text" v-model="formItem.carNumber" placeholder="请输入车牌号"></Input>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="运输方式">
                    <ship-method-select v-model="formItem.shipMethod"></ship-method-select>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="承运电话">
					<Input type="text" v-model="formItem.shipPhone" placeholder="请输入承运电话"></Input>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="驾驶员">
					<Input type="text" v-model="formItem.driverName" placeholder="请输入驾驶员"></Input>
				</FormItem>
			</Col>
		</Row>
		<Row type="flex" justify="start">
            <Col span="6">
				<FormItem label="驾驶证档案编号">
					<Input type="text" v-model="formItem.driverFileNo" placeholder="请输入驾驶证档案编号"></Input>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="起运温度(℃)">
					<Input type="text" v-model="formItem.shipTemper" placeholder="请输入车牌号"></Input>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="件数">
					<InputNumber v-model="formItem.shipQuantity" :min="0"></InputNumber>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="委托经办人">
					<Input type="text" v-model="formItem.operator" placeholder="请输入委托经办人"></Input>
				</FormItem>
			</Col>
		</Row>
		<Row type="flex" justify="start">
            <Col span="6">
				<FormItem label="运行里程(km)">
					<InputNumber v-model="formItem.mileage" :min="0"></InputNumber>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="计划启运时间">
					<DatePicker v-model="formItem.shipStartTime" 
						type="datetime" format="yyyy-MM-dd HH:mm" placeholder="请选择计划启运时间" >
					</DatePicker>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="计划到货时间">
					<DatePicker v-model="formItem.shipEndTime" 
						type="datetime" format="yyyy-MM-dd HH:mm" placeholder="请选择计划到货时间" >
					</DatePicker>
				</FormItem>
			</Col>
			<Col span="6">
				<FormItem label="备注">
					<Input type="text" v-model="formItem.comment" ></Input>
				</FormItem>
			</Col>
		</Row>
        <Row type="flex" justify="center">
            <Button type="primary" icon="checked" :loading="tabLoading" @click="addShipRecord">添加</Button>
        </Row>
	</Form>

    <Row class="title-show">
        <hr/>
    </Row>
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
import temperControlSelect from "@/views/selector/temper-control-select.vue";
import shipToolSelect from "@/views/selector/ship-tool-select.vue";
import shipMethodSelect from "@/views/selector/ship-method-select.vue";
import shipCompanySelect from "@/views/selector/ship-company-select.vue";

export default {
    name: 'sell-order-ship',
    props: {
        orderId: {
            type: Number,
            required: true
        }
    },
    components: {
        temperControlSelect,
        shipToolSelect,
        shipMethodSelect,
        shipCompanySelect
    },
    data () {
        return {
            formItem: {
                id: '',
                sellOrderId: this.orderId,
                issuanceDate: '',
                shipNumber: '',
                shipAddress: '',
                storageMethod: '',
                shipToolId: '',
                shipCompanyId: '',
                carNumber: '',
                shipMethod: '',
                shipPhone: '',
                driverName: '',
                driverFileNo: '',
                shipTemper: '',
                shipQuantity: 0,
                operator: '',
                mileage: 0,
                shipStartTime: '',
                shipEndTime: '',
                comment: ''
            },
            formValidate: {
                shipCompanyId: [
                    {required: true, type: 'number', message: '承运单位必输', trigger: 'change'}
                ]
            },
            tabLoading: false,
            tabData: [],
            tabColumns: [
                {
                    title: '货单号',
                    key: 'shipNumber',
                    width: 120,
                    align: 'center',
                    fixed: 'left'
                },
                {
                    title: '发货时间',
                    key: 'issuanceDate',
                    width: 120,
                    align: 'center',
                    fixed: 'left',
                    render: (h, params) => {
                        let issuanceDate = params.row.issuanceDate;
                        return issuanceDate ? moment(issuanceDate).format('YYYY-MM-DD HH:mm') : '';
                    }
                },
                {
                    title: '承运公司',
                    key: 'shipCompanyName',
                    width: 200,
                    align: 'center'
                },
                {
                    title: '发货地址',
                    key: 'shipAddress',
                    width: 200,
                    align: 'center'
                },
                {
                    title: '存储条件',
                    key: 'storageMethodName',
                    width: 150,
                    align: 'center'
                },
                {
                    title: '运输工具',
                    key: 'shipToolName',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '运输方式',
                    key: 'shipMethodName',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '承运电话',
                    key: 'shipPhone',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '经办人',
                    key: 'operator',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '车牌号',
                    key: 'carNumber',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '驾驶员',
                    key: 'driverName',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '驾驶员档案',
                    key: 'driverFileNo',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '起运温度',
                    key: 'shipTemper',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '数量',
                    key: 'shipQuantity',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '运行里程(km)',
                    key: 'mileage',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '计划启运时间',
                    key: 'shipStartTime',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                        let shipStartTime = params.row.shipStartTime;
                        return shipStartTime ? moment(shipStartTime).format('YYYY-MM-DD HH:mm') : '';
                    }
                },
                {
                    title: '计划到货时间',
                    key: 'shipEndTime',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                        let shipEndTime = params.row.shipEndTime;
                        return shipEndTime ? moment(shipEndTime).format('YYYY-MM-DD HH:mm') : '';
                    }
                },
                {
                    title: '备注',
                    key: 'comment',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '操作',
                    width: 100,
                    align: 'center',
                    fixed: 'right',
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'error',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    this.removeShipRecord(params.row.id);
                                }
                            }
                        }, '删除');
                    }
                }
            ]
        };
    },
    watch: {
        orderId (data) {
            this.refreshTableData();
        }
    },
    methods: {
        addShipRecord () {
            this.$refs.form.validate(valid => {
                if (!valid) {
                    this.$Message.warning('请检查必输项信息');
                    return;
                }
                this.tabLoading = true;
                this.formItem.sellOrderId = this.orderId;
                util.ajax.post('/sell/order/ship/save', this.formItem)
                    .then((response) => {
                        this.$Message.success('新建成功');
                        this.refreshTableData();
                        this.initFormItem();
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    });
                this.tabLoading = false;
            });
        },

        initFormItem () {
            this.formItem = {
                id: '',
                sellOrderId: this.orderId,
                issuanceDate: '',
                shipNumber: '',
                shipAddress: '',
                storageMethod: '',
                shipToolId: '',
                shipCompanyId: '',
                carNumber: '',
                shipMethod: '',
                shipPhone: '',
                driverName: '',
                driverFileNo: '',
                shipTemper: '',
                shipQuantity: 0,
                operator: '',
                mileage: 0,
                shipStartTime: '',
                shipEndTime: '',
                comment: ''
            };
        },

        refreshTableData () {
            if (!this.orderId || this.orderId < 0) {
                return;
            }
            this.tabLoading = true;
            util.ajax.get('/sell/order/ship/list', {params: {orderId: this.orderId}})
                .then((response) => {
                    this.tabData = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
            this.tabLoading = false;
        },

        removeShipRecord (id) {
            if (!id) {
                return;
            }
            util.ajax.delete('/sell/order/ship/remove/' + id)
                .then((reponse) => {
                    this.$Message.success('删除成功');
                    this.refreshTableData();
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        }
    }

};
</script>

<style>
.title-show > hr {
    width: 100%;
    height: 2px;
    color: #5cadff;
    margin-top: 10px;
}

</style>


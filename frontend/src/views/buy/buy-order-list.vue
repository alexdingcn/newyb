<style lang="less">
    @import '../../styles/common.less';
	@import './buy-order.less';
</style>

<template>
	<Row>
		<Card>
			<p slot="title" >
			</p>
			<div slot="extra" style="width:600px">
				<Row>
					<Col span="8">
						<DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="订单日期" style="width:180px"></DatePicker>
					</Col>
					<Col span="6" class="padding-2">
                        <supplier-select v-model="query.supplierId"></supplier-select>
					</Col>
					<Col span="5" class="padding-2">
						<Select v-model="query.status" placeholder="状态">
							<Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
						</Select>
                    </Col>
                    <Col span="5">
                            <Button type="primary" icon="ios-search" @click="queryOrderList"></Button>
                            <Button  icon="checkmark-round" @click="showCheckModal">审核</Button>
					</Col>
				</Row>
			</div>
			<Table border highlight-row disabled-hover height="250"
                   :columns="orderListColumns" :data="orderList"
				   ref="buyOrderListTable" size="small"
                   @on-row-click="handleSelectBuyOrder"
				   no-data-text="使用右上方输入搜索条件">
			</Table>
		</Card>

		<Card class="margin-top-8">
            <Table border highlight-row height="300"
                   class="margin-top-8"
                   :columns="orderColumns" :data="orderItems"
                   ref="buyOrderTable" style="width: 100%;" size="small"
                   no-data-text="点击上方订单后查看采购明细">
                <div slot="header">
                    <h3 class="padding-left-20" >
                        <b>合计金额:</b> ￥{{ totalAmount }}
                    </h3>
                </div>
            </Table>
		</Card>

        <Modal v-model="checkModalShow" width="360">
            <p slot="header">
                <Icon type="checkmark"></Icon>
                <span>审核 {{ orderTitle }} </span>
            </p>
            <div style="text-align:center">
                <Input v-model="checkResult" placeholder="审核意见" style="width: 200px"/>
            </div>
            <div slot="footer">
                <Button type="success" @click="setChecked(true)">审核通过</Button>
                <Button type="error" @click="setChecked(false)">审核拒绝</Button>
            </div>
        </Modal>
	</Row>

</template>

<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';
    import supplierSelect from "@/views/selector/supplier-select.vue";

    export default {
        name: 'buy_order',
        components: {
            supplierSelect
        },
        data () {
            return {
                statusOptions: [{key: 'ALL', name: '所有'}, {key: 'CHECKING', name: '未审批'}, {key: 'CHECKED', name: '已审批'}],
                query: {
                    status: 'CHECKING',
                    supplierId: ''
                },
                dateRange: [
                    moment().add(-1, 'w').format('YYYY-MM-DD'),
                    moment().format('YYYY-MM-DD')
                ],
                orderList: [],
            	goodsLoading: false,
            	totalAmount: 0,
                orderItems: [],
                checkResult: '',
                orderTitle: '',
                checkModalShow: false,
                orderListColumns: [
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center'
                    },
                    {
                        key: 'id',
                        title: '#',
                        align: 'center',
                        width: 30
                    },
                    {
                        title: '订单日期',
                        align: 'center',
                        key: 'createdTime',
                        width: 80,
                        render: (h, params) => {
                            return moment(params.row.createdTime).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '仓库点',
                        align: 'center',
                        key: 'warehouse',
                        width: 80
                    },
                    {
                        title: '供应商',
                        align: 'center',
                        key: 'supplier',
                        width: 100
                    },
                    {
                        title: '供应商代表',
                        align: 'center',
                        key: 'supplierContact',
                        width: 80
                    },
                    {
                        title: '制单人',
                        align: 'center',
                        key: 'createdBy',
                        width: 80
                    },
                    {
                        title: '状态',
                        align: 'center',
                        key: 'status',
                        width: 90,
                        render: (h, params) => {
                            const row = params.row;
                            const color = row.status === 'CHECKING' ? 'blue' : row.status === 'CHECKED' ? 'green' : 'red';
                            const text = row.status === 'CHECKING' ? '待审' : row.status === 'CHECKED' ? '已审' : '拒绝';

                            return h('Tag', {
                                props: {
                                    type: 'dot',
                                    color: color
                                }
                            }, text);
                        }
                    },
                    {
                        title: '审核结论',
                        align: 'center',
                        key: 'checkResult',
                        width: 100
                    },
                    {
                        title: '审核人',
                        align: 'center',
                        key: 'checkedBy',
                        width: 80
                    },
                    {
                        title: '审核日期',
                        align: 'center',
                        key: 'checkTime',
                        width: 80,
                        render: (h, params) => {
                            return moment(params.row.checkTime).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '订单号',
                        align: 'center',
                        key: 'orderNumber',
                        width: 100
                    },
                    {
                        title: '预计到货日',
                        align: 'center',
                        key: 'eta',
                        width: 80,
                        render: (h, params) => {
                            return moment(params.row.eta).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '备注',
                        align: 'center',
                        key: 'comment',
                        width: 100
                    },
                    {
                        title: '温控方式',
                        align: 'center',
                        key: 'temperControl',
                        width: 80
                    },
                    {
                        title: '运输工具',
                        align: 'center',
                        key: 'shipTools',
                        width: 80
                    },
                    {
                        title: '运输方式',
                        align: 'center',
                        key: 'shipMethod',
                        width: 80
                    }
                ],

                orderColumns: [
                    {
                        type: 'index',
                        title: '',
                        align: 'center',
                        width: 30
                    },
                    {
                        title: '货号',
                        align: 'center',
                        key: 'goodsId',
                        width: 50
                    },
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        align: 'center',
                        width: 150,
                        sortable: true,
                        render: (h, params) => {
                            return h('Button', {
                                props: {
                                    type: 'text',
                                    size: 'small'
                                },
                                on: {
                                    click: () => {
                                        let argu = { goods_id: params.row.goodsId };
                                        this.$router.push({
                                            name: 'goods-info',
                                            params: argu
                                        });
                                    }
                                }
                            }, params.row.goodsName);
                        }
                    },
                    {
                        title: '产地',
                        key: 'origin',
                        align: 'center',
                        width: 60
                    },
                    {
                        title: '剂型',
                        key: 'jx',
                        align: 'center',
                        width: 60
                    },
                    {
                        title: '规格',
                        key: 'spec',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '生产企业',
                        key: 'factory',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '单位',
                        key: 'unitName',
                        align: 'center',
                        width: 50
                    },

                    {
                        title: '数量',
                        key: 'quantity',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '单价',
                        key: 'buyPrice',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '金额',
                        key: 'amount',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '大件装量',
                        key: 'bigPack',
                        align: 'center',
                        width: 60
                    },
                    {
                        title: '库存',
                        key: 'balance',
                        align: 'center',
                        width: 100
                    },
                    {
                        title: '零售价',
                        key: 'retailPrice',
                        align: 'center',
                        width: 100
                    }
        	]
            };
        },
        mounted () {
            this.queryOrderList();
        },
    activated () {

    },
        watch: {
        	orderItems: function () {
        		this.totalAmount = this.orderItems.reduce(function (total, item) { return total + parseFloat(item.amount); }, 0);
        	}
        },
        methods: {
            rowClassName (row, index) {
                if (row.status) {
                    return 'table-row-' + row.status.toLowerCase();
                }
                return '';
            },
            queryOrderList () {
                var self = this;
                this.orderList = [];
                this.orderItems = [];
                if (this.dateRange && this.dateRange.length == 2) {
                    this.query['startDate'] = this.dateRange[0];
                    this.query['endDate'] = this.dateRange[1];
                }
                util.ajax.post('/buy/list', this.query)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.orderList = response.data;
                            if (self.orderList && self.orderList.length > 0) {
                                self.handleSelectBuyOrder(self.orderList[0]);
                            }
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },

            handleSelectBuyOrder (row) {
                var self = this;
                util.ajax.get('/buy/orderdetail/' + row.id)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.orderItems = response.data;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            showCheckModal () {
                var rows = this.$refs.buyOrderListTable.getSelection();
                if (!rows || rows.length === 0) {
                    this.$Message.warning('请选择订单');
                } else if (rows.length > 1) {
                    this.$Message.warning('请一次选择一条订单');
                } else {
                    this.orderTitle = '订单ID:' + rows[0].id + ' 供应商:' + rows[0].supplier;
                    this.checkModalShow = true;
                }
            },
            setChecked (result) {
                var self = this;
                var rows = this.$refs.buyOrderListTable.getSelection();
                if (!rows || rows.length === 0) {
                    this.$Message.warning('请选择订单');
                } else if (rows.length > 1) {
                    this.$Message.warning('请一次选择一条订单');
                } else if (rows.length == 1) {
                    util.ajax.post('/buy/status', {
                        orderId: rows[0].id,
                        orderStatus: result ? 'CHECKED' : 'REJECTED',
                        checkResult: this.checkResult
                    })
                        .then(function (response) {
                            self.checkModalShow = false;
                            if (response.status === 200) {
                                self.queryOrderList();
                            }
                        })
                        .catch(function (error) {
                            self.checkModalShow = false;
                            console.log(error);
                        });
                }
            }
        }
    };
</script>

<style>
    .ivu-table .table-row-checking {
        background-color: #2db7f5;
        color: #fff;
    }
</style>

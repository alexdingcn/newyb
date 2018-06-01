<style lang="less">
    @import '../../styles/common.less';
    .option-goods-spec {
        float: right;
        color: #999;
    }
    .ivu-form-item {
        margin-bottom: 0px;
    }

    .ivu-table-cell {
        padding-left: 5px;
        padding-right: 5px;
    }
    th .ivu-table-cell {
        white-space: nowrap;
    }

</style>

<template>
	<Row>
		<Card>
			<p slot="title" >
			</p>
			<div slot="extra" style="width:600px">
				<Row type="flex" justify="end">
					<i-col span="8">
						<DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="订单日期" style="width:180px"></DatePicker>
					</i-col>
					<i-col span="6" class="padding-2">
                        <supplier-select v-model="query.supplierId"></supplier-select>
					</i-col>
					<i-col span="5" class="padding-2" v-if="!chooseModal">
						<Select v-model="query.status" placeholder="状态">
							<Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
						</Select>
                    </i-col>
                    <i-col span="5" >
                            <Button type="primary" icon="ios-search" @click="queryOrderList"></Button>
                            <Button v-if="!chooseModal" icon="checkmark-round" @click="showCheckModal">审核</Button>
                            <Button v-if="chooseModal" icon="checkmark-round" @click="choosedItem" >选择</Button>
					</i-col>
				</Row>
			</div>
			<Table border highlight-row disabled-hover height="250"
                   :columns="orderListColumns" :data="orderList"
				   ref="buyOrderListTable" size="small"
                   @on-row-click="handleSelectBuyOrder"
                   @on-row-dblclick="choosedItem"
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
        props: {
            chooseModal: {
                type: Boolean,
                default: false
            }
        },
        components: {
            supplierSelect
        },
        data () {
            return {
                statusOptions: [{key: 'ALL', name: '所有'}, {key: 'INIT', name: '未审批'}, {key: 'CHECKED', name: '已审批'}],
                query: {
                    status: 'INIT',
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
                        type: this.chooseModal ? '' : 'selection',
                        width: this.chooseModal ? 0 : 60,
                        align: 'center',
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
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.createdTime).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '仓库点',
                        align: 'center',
                        key: 'warehouse',
                        width: 150
                    },
                    {
                        title: '供应商',
                        align: 'center',
                        key: 'supplier',
                        width: 150
                    },
                    {
                        title: '供应商代表',
                        align: 'center',
                        key: 'supplierContact',
                        width: 150
                    },
                    {
                        title: '制单人',
                        align: 'center',
                        key: 'createdBy',
                        width: 100
                    },
                    {
                        title: '状态',
                        align: 'center',
                        key: 'status',
                        width: 120,
                        render: (h, params) => {
                            const row = params.row;
                            const color = row.status === 'INIT' ? 'blue' : row.status === 'CHECKED' ? 'green' : 'red';
                            const text = row.status === 'INIT' ? '待审' : row.status === 'CHECKED' ? '已审' : '拒绝';

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
                        width: 150
                    },
                    {
                        title: '审核人',
                        align: 'center',
                        key: 'checkedBy',
                        width: 120
                    },
                    {
                        title: '审核日期',
                        align: 'center',
                        key: 'checkTime',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.checkTime).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '订单号',
                        align: 'center',
                        key: 'orderNumber',
                        width: 120
                    },
                    {
                        title: '预计到货日',
                        align: 'center',
                        key: 'eta',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.eta).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '备注',
                        align: 'center',
                        key: 'comment',
                        width: 150
                    },
                    {
                        title: '温控方式',
                        align: 'center',
                        key: 'temperControl',
                        width: 100
                    },
                    {
                        title: '运输工具',
                        align: 'center',
                        key: 'shipTools',
                        width: 100
                    },
                    {
                        title: '运输方式',
                        align: 'center',
                        key: 'shipMethod',
                        width: 100
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
                        sortable: true
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
                        key: 'factoryName',
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
                ],
                currchooseItem: ''
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
                if(this.chooseModal) {
                    this.query['status'] = 'CHECKED'; //选择模式下,只查询审核通过的订单类型
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
                        util.errorProcessor(self, error);
                    });
            },

            handleSelectBuyOrder (row) {
                this.currchooseItem = row;
                var self = this;
                util.ajax.get('/buy/orderdetail/' + row.id)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.orderItems = response.data;
                        }
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
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
                            util.errorProcessor(self, error);
                        });
                }
            },

            choosedItem() {
                if (!this.currchooseItem || !this.currchooseItem.id) {
                    this.$Message.warning('请先选择对应订单信息');
                    return;
                }
                this.$emit('on-choosed', this.currchooseItem);
            }

        }
    };
</script>

<style >
    .ivu-table .table-row-checking {
        background-color: #2db7f5;
        color: #fff;
    }
</style>

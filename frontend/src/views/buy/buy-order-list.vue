<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
	<Row>
		<Card>
			<p slot="title">
              <Icon type="funnel"></Icon>
              采购审核
          </p>
			<div slot="extra" style="width:600px">
				<Row type="flex" justify="end">
					<Col span="8">
						<DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="订单日期" style="width:180px"></DatePicker>
					</Col>
					<Col span="6" class="padding-2">
                        <supplier-select v-model="query.supplierId"></supplier-select>
					</Col>
					<Col span="5" class="padding-2" v-if="!chooseModal">
						<Select v-model="query.status" placeholder="状态">
							<Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
						</Select>
                    </Col>
                    <Col span="5" >
                            <Button type="primary" icon="ios-search" @click="queryOrderList"></Button>
                            <Button v-if="!chooseModal" icon="checkmark-round" @click="showCheckModal">审核</Button>
                            <Button v-if="chooseModal" icon="checkmark-round" @click="choosedItem" >选择</Button>
					</Col>
				</Row>
			</div>
			<Table border highlight-row disabled-hover height="250"
                   :columns="orderListColumns" :data="orderList" :loading="orderLoading" 
				   ref="buyOrderListTable" size="small"
                   @on-row-click="handleSelectBuyOrder" 
                   @on-row-dblclick="dblChoosedItem" 
				   no-data-text="使用右上方输入搜索条件">
			</Table>
		</Card>

		<Card class="margin-top-8">
            <Table border highlight-row height="300"
                   class="margin-top-8" :loading="detailLoading" 
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
                        key: 'id',
                        title: '#',
                        align: 'center',
                        width: 100
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
                        width: 170
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
                        width: 150,
                        render: (h, params) => {
                            const row = params.row;
                            let color = 'blue';
                            let text = '待审';
                            switch (row.status) {
                                case 'INIT': 
                                    color = 'blue';
                                    text = '待审';
                                    break;
                                case 'CHECKED': 
                                    color = 'green';
                                    text = '审核通过';
                                    break;
                                case 'REJECTED':
                                    color = 'red';
                                    text = '审核拒绝';
                                    break;
                                case 'SHIPPED': 
                                    color = '#ff9900';
                                    text = '已收货';
                                    break;
                                default: 
                                     color = '#80848f';
                                     text = '未知状态';
                                     break;
                            };
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
                        key: 'checkBy',
                        width: 120
                    },
                    {
                        title: '审核日期',
                        align: 'center',
                        key: 'checkTime',
                        width: 120,
                        render: (h, params) => {
                            return params.row.checkTime ? moment(params.row.checkTime).format('YYYY-MM-DD') : '';
                        }
                    },
                    {
                        title: '订单号',
                        align: 'center',
                        key: 'orderNumber',
                        width: 150
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
                        key: 'shipTool',
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
                        width: 60
                    },
                    {
                        title: '货号',
                        align: 'center',
                        key: 'goodsId',
                    },
                    {
                        title: '商品名称',
                        key: 'goodsName',
                        align: 'center',
                        sortable: true
                    },
                    {
                        title: '产地',
                        key: 'origin',
                        align: 'center',
                        render: (h, params) => {
                            return params.row.goods.origin;
                        }
                    },
                    {
                        title: '剂型',
                        key: 'jxName',
                        align: 'center',
                        render: (h, params) => {
                            return params.row.goods.jxName;
                        }
                    },
                    {
                        title: '规格',
                        key: 'spec',
                        align: 'center',
                        render: (h, params) => {
                            return params.row.goods.spec;
                        }
                    },
                    {
                        title: '生产企业',
                        key: 'factoryName',
                        align: 'center',
                        render: (h, params) => {
                            return params.row.goods.factoryName;
                        }
                    },
                    {
                        title: '单位',
                        key: 'unitName',
                        align: 'center',
                        render: (h, params) => {
                            return params.row.goods.unitName;
                        }
                    },

                    {
                        title: '数量',
                        key: 'quantity',
                        align: 'center'
                    },
                    {
                        title: '单价',
                        key: 'buyPrice',
                        align: 'center'
                    },
                    {
                        title: '金额',
                        key: 'amount',
                        align: 'center'
                    },
                    {
                        title: '大件装量',
                        key: 'bigPack',
                        align: 'center',
                        render: (h, params) => {
                            return params.row.goods.bigPack;
                        }
                    },
                    {
                        title: '零售价',
                        key: 'retailPrice',
                        align: 'center',
                        render: (h, params) => {
                            return params.row.goods.retailPrice;
                        }
                    },
                    {
                        title: '批发价',
                        key: 'batchPrice',
                        align: 'center',
                        render: (h, params) => {
                            return params.row.goods.batchPrice;
                        }
                    },
                    {
                        title: '当前库存',
                        key: 'balance',
                        align: 'center',
                        width: 100
                    }
                ],
                currchooseItem: '',
                orderLoading: false,
                detailLoading: false
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
                this.orderLoading = true;
                util.ajax.post('/buy/list', this.query)
                    .then(function (response) {
                        self.orderLoading = false;
                        if (response.status === 200 && response.data) {
                            self.orderList = response.data;
                        }
                        self.currchooseItem = {};
                        self.$refs.buyOrderListTable.clearCurrentRow();
                    })
                    .catch(function (error) {
                        self.orderLoading = false;
                        util.errorProcessor(self, error);
                    });
            },

            handleSelectBuyOrder (row) {
                this.currchooseItem = row;
                var self = this;
                self.detailLoading = true;
                util.ajax.get('/buy/orderdetail/' + row.id)
                    .then(function (response) {
                        self.detailLoading = false;
                        if (response.status === 200 && response.data) {
                            self.orderItems = response.data;
                        }
                    })
                    .catch(function (error) {
                        self.detailLoading = false;
                        util.errorProcessor(self, error);
                    });
            },

            showCheckModal () {
                if (!this.currchooseItem || !this.currchooseItem.id) {
                    this.$Message.warning('请选择订单');
                } else {
                    this.orderTitle = '订单ID:' + this.currchooseItem.id + ' 供应商:' + this.currchooseItem.supplier;
                    this.checkModalShow = true;
                }
            },

            setChecked (result) {
                if (!this.currchooseItem || !this.currchooseItem.id) {
                    this.$Message.warning('请选选择需要审核的订单');
                    return;
                }
                let self = this;
                this.$Modal.confirm({
                    title: '审批确认',
                    content: '是否确认采购订单信息正确，确认提交审核结果？',
                    onCancel: () => {},
                    onOk: () => {
                        util.ajax.post('/buy/status', {
                            orderId: self.currchooseItem.id,
                            orderStatus: result ? 'CHECKED' : 'REJECTED',
                            checkResult: this.checkResult
                            })
                            .then(function (response) {
                                self.checkModalShow = false;
                                self.$Message.success('审核结果提交成功');
                                if (response.status === 200) {
                                    self.queryOrderList();
                                }
                            })
                            .catch(function (error) {
                                self.checkModalShow = false;
                                util.errorProcessor(self, error);
                            });
                    }
                });
            },

            dblChoosedItem(data) {
                this.currchooseItem = data;
                this.choosedItem();
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

<style>
    .ivu-table .table-row-checking {
        background-color: #2db7f5;
        color: #fff;
    }
</style>

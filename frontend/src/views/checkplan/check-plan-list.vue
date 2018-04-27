<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
	<Row>
		<Card>
			<p slot="title" >
			</p>
			<div slot="extra" style="width: 1000px;">
				<Row >
					<Col span="6">
						<DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="盘点日期" style="width:180px"></DatePicker>
					</Col>
					<Col span="3" class="">
                        <warehouse-select v-model="query.warehouseId" placeholder="仓库"  size="small"></warehouse-select>
					</Col>
                    <Col span="3" class="">
                        <Select v-model="query.checkType" size="small"  prop="checkType"  placeholder="类型">
                            <Option v-for="option in checkTypeOptions" :value="option.id" :label="option.name" :key="option.id">
                                {{option.name}}
                            </Option>
                        </Select>
                    </Col>
					<Col span="3" class="padding-2">
                        <Select size="small" v-model="query.state" placeholder="状态">
                            <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                        </Select>
                    </Col>
                    <Col span="3" >
                        <Button type="primary" icon="ios-search" @click="queryOrderList"></Button>
                        <Button v-if="chooseModal" icon="checkmark-round" @click="choosedItem" >选择</Button>
					</Col>
				</Row>
			</div>
			<Table border highlight-row disabled-hover height="600"
                   :columns="orderListColumns" :data="orderList" :loading="orderLoading"
				   ref="orderTable" size="small"
                   @on-row-click="handleSelectOrder"
				   no-data-text="使用右上方输入搜索条件">
			</Table>
		</Card>
	</Row>

</template>

<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';
    import warehouseSelect from "@/views/selector/warehouse-select.vue";

    export default {
        name: 'check-plan-list',
        props: {
            chooseModal: {
                type: Boolean,
                default: false
            }
        },
        components: {
            warehouseSelect
        },
        data () {
            return {
                statusOptions: [
                    {key: '', name: '所有'},
                    {key: 0, name: '待审核'},
                    {key: 1, name: '已审核'}
                ],
                checkTypeOptions:[{ id: 0, name:'库存盘点'},{ id: 1, name:'直接盘库'},{ id: 2, name:'单品盘点'}],
                query:{},
                dateRange: [
                    moment().add(-1, 'w').format('YYYY-MM-DD'),
                    moment().format('YYYY-MM-DD')
                ],
                orderList: [],
            	goodsLoading: false,
                orderListColumns: [

                    {
                        title: '序号',
                        type: 'index',
                        width: 80
                    },
                    {
                        title: '状态',
                        align: 'center',
                        key: 'state',
                        width: 120,
                        render: (h, params) => {
                            let state  = params.row.state;
                            if (0==state) {
                                return h('Tag', {props: {color: 'yellow'}}, '待审核');
                            }else if (1==state) {
                                return h('Tag', {props: {color: 'green'}}, '已审核');
                            }

                        }
                    },
                    {
                        title: '盘点单号',
                        align: 'center',
                        key: 'checkCode',
                        width: 160
                    },
                    {
                        title: '盘点类型',
                        key: 'checkType',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            let checkType  = params.row.checkType;
                            if (0==checkType) {
                                return h('Tag', {props: {color: 'green'}}, '库存盘点');
                            }else if (1==checkType) {
                                return h('Tag', {props: {color: 'green'}}, '直接盘库');
                            }else if (2==checkType){
                                return h('Tag', {props: {color: 'green'}}, '单品盘点');
                            }
                        }
                    },
                    {
                        title: '仓库',
                        key: 'warehouseName',
                        align: 'center',
                        width: 100
                    },

                    {
                        title: '盘点时间',
                        key: 'checkDate',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            return moment(params.row.checkDate).format('YYYY-MM-DD');
                        }
                    },
                    {
                        title: '制单人',
                        key: 'createBy',
                        align: 'center',
                        width: 80
                    },
                    {
                        title: '备注',
                        key: 'comment',
                        align: 'center',
                        width: 150
                    },
                    {
                        title: '总经理',
                        key: 'manager',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '总经理意见',
                        key: 'managerNote',
                        align: 'center',
                        width: 150
                    },
                    {
                        title: '财务经理',
                        key: 'finance',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '财务经理意见',
                        key: 'financeNote',
                        align: 'center',
                        width: 150
                    }
                ],
                currchooseItem: '',
                orderLoading: false,
            };
        },
        mounted () {
            this.queryOrderList();
        },
        activated () {

        },
        watch: {
        },
        methods: {
            queryOrderList () {
                var self = this;
                let reqData = {
                    checkType:  this.query.checkType,
                    warehouseId: this.query.warehouseId,
                    state:this.query.state,
                    startDate:this.dateRange[0],
                    endDate:  this.dateRange[1]
                };
                this.repertoryCheckItems = [];
                self.orderLoading = true;
                util.ajax.post('/repertory/check/list',reqData)
                    .then(function (response) {
                        if (response.status === 200 && response.data) {
                            self.orderLoading = false;
                            self.orderList = response.data.data;
                        }
                    })
                    .catch(function (error) {
                        self.orderLoading = false;
                        util.errorProcessor(this, error);
                    });
            },
            handleSelectOrder(rowData) {
                if (!rowData || !rowData.id) {
                    return;
                }
                this.currchooseItem = rowData;

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

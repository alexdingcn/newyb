<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Modal v-model="isShowModal" :width="75" :closable="false" title="查询获取销售订单">
            <Row type="flex" justify="center">
                <Col span="18" offset="1">
                    <Form ref="searchForm" :model="formItem" :label-width="100" >
                        <Row type="flex" justify="center">
                            <Col span="12">
                                <FormItem label="客户">
                                    <customer-select v-model="formItem.customerId" ></customer-select>
                                </FormItem>
                            </Col>
                            <Col span="12">
                                <FormItem label="销售员">
                                    <Select v-model="formItem.salerId" clearable filterable>
                                        <Option v-for="item in salerList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                    </Select>
                                </FormItem>
                            </Col>
                        </Row>
                        <Row type="flex" justify="center" style="margin-top:-20px;">
                            <Col span="12">
                                <FormItem label="自定订号">
                                    <Input type="text" v-model="formItem.refNo" ></Input>
                                </FormItem>
                            </Col>
                            <Col span="12">
                                <FormItem label="制单日期">
                                    <DatePicker v-model="formItem.createOrderDate" type="date" placeholder="请选择制单日期" ></DatePicker>
                                </FormItem>
                            </Col>
                        </Row>
                        <Row type="flex" justify="center" style="margin-top:-10px;">
                            <Button type="primary" icon="ios-search" class="margin-let-10" @click="searchBtnClicked">查询</Button>
                        </Row>
                    </Form>
                </Col>
            </Row>
            

            <Row type="flex" justify="center" align="middle" class="margin-top-20">
                <Table border highlight-row :columns="tabColumns" :data="tabData" 
                    :loading="tableLoading" 
                    @on-row-click="tableRowClick"  
                    ref="customersTable" style="width: 100%;" size="small">
                </Table>
            </Row>
            <Row type="flex" justify="end">
                <Page :total="totalCount" :current="currentPage" :page-size="tableCurrPageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
            <Row type="flex" justify="center" align="middle">
                <div>当前选择的订单: <strong style="color: red;">{{currChooseShow}}</strong></div>
            </Row>

            <div slot="footer">
                <Row >
                    <Col span="6" offset="6">
                        <Button type="primary" @click="ok" long>
                            <span >确定</span>
                        </Button>
                    </Col>
                    <Col span=6 class="padding-left-10">
                        <Button @click="closedModal" long>取消</Button>
                    </Col>
                </Row>
            </div>
        </Modal>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";
import customerSelect from "@/views/customer/customer-select.vue";

export default {
    name: 'sell-order-search',
    props: {
        showModal: {
            type: Boolean,
            default: false
        },
        status: {
            type: String
        }
    },
    components: {
        customerSelect
    },
    data() {
        return {
            isShowModal: false,
            formItem: {
                customerId: '',
                salerId: '',
                refNo: '',
                createOrderDate: ''
            },
            salerList:[],
            currentPage: 1,
            totalCount: 0,
            tableCurrPageSize: 10,
            tabData: [],
            tableLoading: false,
            currChooseShow: '',
            currChooseItem: null,
            tabColumns: [
                {
                    title: '订单编号',
                    key: "orderNumber",
                    align: "center"
                },
                {
                    title: '客户名称',
                    key: "customer.name",
                    align: "center",
                    sortable: true
                },
                {
                    title: '销售人员',
                    key: "salerId",
                    align: "center",
                },
                {
                    title: '自定单号',
                    key: "refNo",
                    align: "center"
                },
                {
                    title: '制单日期',
                    key: "createOrderDate",
                    align: "center",
                    sortable: true,
                    render: (h, params) => {
                        let date = params.row.createOrderDate;
                        if (!date || isNaN(date)) {
                            return h('span', '');
                        }else {
                            return h('span', dataConver.formatDate(new Date(date), 'yyyy-MM-dd'));
                        }
                    }
                },
                {
                    title: '收款日期',
                    key: "payOrderDate",
                    align: "center",
                    sortable: true,
                    render: (h, params) => {
                        let date = params.row.payOrderDate;
                        if (!date || isNaN(date)) {
                            return h('span', '');
                        }else {
                            return h('span', dataConver.formatDate(new Date(date), 'yyyy-MM-dd'));
                        }
                    }
                }
            ]
        }
    },
    watch: {
        showModal(data) {
            if(data) {
                this.isShowModal = data;
                this.initData();
            }
        },
        currChooseItem(data) {
            if (!data || data === null) {
                this.currChooseShow = '';
            }else {
                this.currChooseShow = '订单编号=' + data.orderNumber;
            }
        }
    },
    methods: {
        initData() {
            //初始化销售员的数据
            this.salerList = [
                {
                  id: 1,
                  name: '销售员1'
              },
              {
                  id: 2,
                  name: '销售员2'
              }
            ];
        },

        searchBtnClicked() {
            let reqData = this.formItem;
            reqData.page = this.currentPage;
            reqData.size = this.tableCurrPageSize;
            reqData.status = this.status;
            this.tableLoading = true;
            if (reqData.createOrderDate === "") {
                reqData.createOrderDate = null;
            }
            console.log(reqData);
            util.ajax.get('/sell/order/list', {params: reqData})
                .then((response) => {
                    this.tabData = response.data.data;
                    this.totalCount = response.data.count;
                    this.currChooseItem = null;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                }
            );
            this.tableLoading = false;
        },

        pageChange(data) {
            this.currentPage = data;
            this.searchBtnClicked();
        },

        tableRowClick(data) {
            this.currChooseItem = data;
        },

        ok() {
            this.isShowModal = false;
            this.$emit('modal-close');
            this.$emit('choosed', this.currChooseItem);
        },

        closedModal() {
            this.isShowModal = false;
            this.$emit('modal-close');
        }
    }
  
}
</script>

<style>

</style>



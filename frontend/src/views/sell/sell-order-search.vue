<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Modal v-model="isShowModal" :width="75" :mask-closable="false" :closable="false" title="查询获取暂挂订单">
            <Form ref="searchForm" :model="formItem" :label-width="100" >
                <Row >
                    <i-col span="8">
                        <FormItem label="客户">
                            <customer-select size="small" v-model="formItem.customerId" ></customer-select>
                        </FormItem>
                    </i-col>
                    <i-col span="8">
                        <FormItem label="销售员">
                            <sale-select size="small" v-model="formItem.saleId"></sale-select>
                        </FormItem>
                    </i-col>
                    <!-- <i-col span="6">
                        <FormItem label="自定订号">
                            <Input size="small" type="text" v-model="formItem.refNo" />
                        </FormItem>
                    </i-col> -->
                    <i-col span="8">
                        <FormItem label="制单日期">
                            <DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="制单日期"></DatePicker>
                        </FormItem>
                    </i-col>
                </Row>
                <Row type="flex" justify="center" >
                    <Button size="small" type="primary" icon="ios-search" class="margin-let-10" @click="searchBtnClicked">查询</Button>
                </Row>
            </Form>

            <Row type="flex" justify="center" align="middle" class="margin-top-20">
                <Table border highlight-row :columns="tabColumns" :data="tabData" 
                    :loading="tableLoading" 
                    @on-row-click="tableRowClick"  
                    ref="customersTable" style="width: 100%;" size="small">
                </Table>
            </Row>
            <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="tableCurrPageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
            <Row type="flex" justify="center" align="middle">
                <div>当前选择的订单: <strong style="color: red;">{{currChooseShow}}</strong></div>
            </Row>

            <div slot="footer">
                <Row >
                    <ButtonGroup>
                        <Button size="small" icon="checkmark-round" type="primary" @click="ok">确认选取</Button>
                        <Button size="small" icon="minus-round" @click="closedModal" >取消选取</Button>
                        <Button size="small" icon="close-round" type="error" @click="removeItem">删除选定</Button>
                    </ButtonGroup>
                </Row>
            </div>
        </Modal>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from 'moment';
import customerSelect from "@/views/selector/customer-select.vue";
import saleSelect from "@/views/selector/sale-select.vue";

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
        customerSelect,
        saleSelect
    },
    data() {
        return {
            isShowModal: false,
            formItem: {
                customerId: '',
                saleId: '',
                refNo: '',
            },
            dateRange: [
                moment().add(-1, 'w').format('YYYY-MM-DD'),
                moment().format('YYYY-MM-DD')
            ],
            currentPage: 1,
            totalCount: 0,
            tableCurrPageSize: 10,
            tabData: [],
            tableLoading: false,
            currChooseShow: '',
            currChooseItem: {},
            tabColumns: [
                {
                    title: '订单编号',
                    key: "orderNumber",
                    align: "center",
                    width: 200
                },
                {
                    title: '客户名称',
                    key: "customerName",
                    align: "center",
                    width: 200,
                    sortable: true
                },
                {
                    title: '销售人员',
                    key: "saleId",
                    align: "center",
                    width: 180,
                    render: (h, params) => {
                        let saleNickName = params.row.saleNickName ? params.row.saleNickName : '';
                        let saleRealName = params.row.saleRealName ? params.row.saleRealName : '';
                        return h('span', saleNickName + (saleRealName ? '---' + saleRealName : ''));
                    }
                },
                {
                    title: '自定单号',
                    key: "refNo",
                    align: "center",
                    width: 200
                },
                {
                    title: '制单日期',
                    key: "createOrderDate",
                    align: "center",
                    sortable: true,
                    width: 180,
                    render: (h, params) => {
                        let date = params.row.createOrderDate;
                        return h('span', date ? moment(date).format('YYYY-MM-DD') : '');
                    }
                },
                {
                    title: '收款日期',
                    key: "payOrderDate",
                    align: "center",
                    sortable: true,
                    width: 180,
                    render: (h, params) => {
                        let date = params.row.payOrderDate;
                        return h('span', date ? moment(date).format('YYYY-MM-DD') : '');
                    }
                },
                {
                    title: '仓库点',
                    key: 'warehouseName',
                    width: 170
                }

            ]
        }
    },
    watch: {
        showModal(data) {
            if(data) {
                this.isShowModal = data;
                this.searchBtnClicked();
            }
        },
        currChooseItem(data) {
            if (!data || !data.id) {
                this.currChooseShow = '';
            }else {
                this.currChooseShow = '订单编号=' + data.orderNumber;
            }
        }
    },
    methods: {
        searchBtnClicked() {
            let reqData = {
                customerId: this.formItem.customerId,
                saleId: this.formItem.saleId,
                refNo: this.formItem.refNo,
                page: this.currentPage,
                pageSize: this.tableCurrPageSize,
                status: this.status,
                formCreateOrderDate: this.dateRange[0],
                toCreateOrderDate: this.dateRange[1]
            };
            this.tableLoading = true;
            util.ajax.post('/sell/order/list', reqData)
                .then((response) => {
                    this.tableLoading = false;
                    this.tabData = response.data.data;
                    this.totalCount = response.data.count;
                    this.currChooseItem = {};
                    this.$refs.customersTable.clearCurrentRow();
                })
                .catch((error) => {
                    this.tableLoading = false;
                    util.errorProcessor(this, error);
                }
            );
        },

        pageChange(data) {
            this.currentPage = data;
            this.searchBtnClicked();
        },

        tableRowClick(data) {
            this.currChooseItem = data;
        },

        removeItem() {
            if(!this.currChooseItem || !this.currChooseItem.id) {
                this.$Message.warning('请先选取需要删除的记录');
                return;
            }
            let removeId = this.currChooseItem.id;
            this.$Modal.confirm({
                title: '删除确认提示',
                content: '确认删除记录，删除后不可恢复.',
                onOk: () => {
                    util.ajax.delete("/sell/order/remove/" + removeId)
                        .then((response) => {
                            this.sellOrderLoading = false;
                            this.searchBtnClicked();
                            this.$Message.success('删除成功');
                        })
                        .catch((error) => {
                            this.sellOrderLoading = false;
                            util.errorProcessor(this, error);
                        });
                },
                onCancel: () => {}
            });
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

<style >
.ivu-form-item {
    margin-bottom: 5px;
}

</style>



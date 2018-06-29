
<template>
  <div>
      <Card>
          <p slot="title">出库审核</p>
          <div slot="extra">
              <ButtonGroup>
                <Button size="small" type="primary" icon="ios-search" :loading="orderLoading" @click="refreshOrder">查询</Button>
                <Button size="small" type="success" icon="ios-checkmark" @click="checkOk" >审查通过</Button>
                <!--<Button size="small" type="info"  icon="images" @click="showCheckFile">检验档案</Button>-->
              </ButtonGroup>
          </div>
          
          <Form ref="searchForm" :model="query" :label-width="100">
                <Row type="flex" justify="start">
                    <FormItem label="出库日期">
                        <DatePicker size="small" v-model="dateRange" type="daterange" placement="bottom-start" placeholder="收货日期" style="width:180px"></DatePicker>
                    </FormItem>
                    <FormItem label="仓库">
                        <warehouse-select v-model="query.warehouseId" size="small"></warehouse-select>
                    </FormItem>
                    <FormItem label="供应商">
                        <supplier-select v-model="query.supplierId" size="small"></supplier-select>
                    </FormItem>
                    <FormItem label="状态">
                        <Select size="small" v-model="query.status" placeholder="状态">
                                <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                        </Select>
                    </FormItem>
                </Row>
          </Form>
          <div>
              <Table ref="orderTable" border highlight-row disabled-hover height="300" style="width: 100%" 
                    :columns="orderListColumns" :data="orderList" size="small" 
                    :loading="orderLoading"  
                    @on-row-click="handleSelectOrder" 
				    no-data-text="输入查询条件, 点击上方查询按钮进行查询">
                </Table>
          </div>
          <Row type="flex" justify="start" style="margin-top:15px;">
              <h4 class="detail-count-content" >
                    <b class="detail-count-content-b">总金额:</b> {{ totalAmount }}
                    <b class="detail-count-content-b">出库数量:</b> {{ totalOutCount }}
                    <!--<b class="detail-count-content-b">不合格数量:</b> {{ totalErrorCount }}-->
                </h4>
          </Row>
          <Table border highlight-row height="300" :loading="detailLoading" 
                :columns="detailColumns" :data="detailList" size="small" 
                ref="detailTable" style="width: 100%;" 
                no-data-text="点击上方订单后查看明细">
        </Table>
      </Card>

      <Modal v-model="checkFileModal" title="检验报告档案" :mask-closable="false" width="50">
          <file-detail :fileNo="checkFileNo" @add-file-success="addFileSuccess" ></file-detail>
          <div slot="footer"></div>
      </Modal>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";

export default {
  name: "in-check",
  components: {
    warehouseSelect,
    supplierSelect,
    fileDetail,
    goodsSpecTags,
  },
  data() {
    return {
      orderLoading: false,
      detailLoading: false,
      statusOptions: [
        { key: "REVIEW", name: "待审查" },
        { key: "CHECKED", name: "已审查" }
      ],
      dateRange: [
        moment()
          .add(-1, "w")
          .format("YYYY-MM-DD"),
        moment().format("YYYY-MM-DD")
      ],
      query: {
        status: "REVIEW"
      },
      orderList: [],
      orderListColumns: [
        {
          title: "序号",
          type: "index",
          width: 80,
          align: "center"
        },
        {
          title: "状态",
          key: "status",
          align: "center",
          width: 100,
          render: (h, params) => {
            let color = params.row.status === "CHECKED" ? "green" : "#ff9900";
            let label = params.row.status === "CHECKED" ? "已审查" : "待审查";
            return h(
              "Tag",
              {
                props: {
                  color: color
                }
              },
              label
            );
          }
        },
        {
          title: "出库方式",
          align: "center",
          key: "refTypeName",
          width: 100
        },
        {
          title: "出库仓库",
          align: "center",
          key: "warehouseName",
          width: 100
        },
        {
          title: "领料员",
          align: "center",
          key: "customerName",
          width: 100
        },

        {
          title: "制单人",
          align: "center",
          key: "makeOrderUser",
          width: 100
        },
        {
          title: "出库数量",
          align: "center",
          key: "totalQuantity",
          width: 120
        },
        {
          title: "总计金额",
          align: "center",
          key: "totalAmount",
          width: 120
        },
        {
          title: "系统单号",
          align: "center",
          key: "refOrderNumber",
          width: 180
        },

        {
          title: "出库日期",
          align: "center",
          key: "outDate",
          width: 120,
          render: (h, params) => {
            let outDate = params.row.outDate;
            return h(
              "span",
              outDate ? moment(outDate).format("YYYY-MM-DD") : ""
            );
          }
        },
        {
          title: "备注",
          align: "center",
          key: "comment"
        }
      ],
      detailList: [],
      detailColumns: [
        {
          title: "序号",
          align: "center",
          type: "index",
          width: 80
        },
        {
          title: "商品名称",
          align: "center",
          key: "goodsName",
          width: 160
        },
        {
          title: "产地",
          align: "center",
          key: "origin",
          width: 160
        },
        /**{
          title: "剂型",
          align: "center",
          key: "jxName",
          width: 120
        },*/
        {
          title: "规格",
          align: "center",
          key: "spec",
          width: 100,
          render: (h, params) => {
            return h(goodsSpecTags, {
              props: {
                tags: params.row.goods ? params.row.goods.goodsSpecs : "",
                color: "blue"
              }
            });
          }
        },
        {
          title: "生产企业",
          align: "center",
          key: "factoryName",
          width: 180
        },
        {
          title: "批准文号",
          align: "center",
          key: "permit",
          width: 120
        },

        {
          title: "出库数量",
          align: "center",
          key: "quantity",
          width: 120
        },
        {
          title: "单位",
          align: "center",
          key: "unitName",
          width: 120
        },
        {
          title: "单价",
          width: 120,
          align: "center",
          key: "price"
        },
        {
          title: "金额",
          width: 120,
          align: "center",
          key: "amount"
        },
        {
          title: "批号",
          key: "batchCode",
          align: "center",
          width: 140
        },
        {
          title: "生产日期",
          key: "productDate",
          align: "center",
          width: 140,
          render: (h, params) => {
            return h("span",params.row.productDate
              ? moment(params.row.productDate).format("YYYY-MM-DD")
              : "");
          }
        },
        {
          title: "有效期至",
          key: "expDate",
          align: "center",
          width: 140,
          render: (h, params) => {
            return h(
              "span",
              params.row.expDate
                ? moment(params.row.expDate).format("YYYY-MM-DD")
                : ""
            );
          }
        },
        {
          title: "存储条件",
          key: "storageCondition",
          align: "center",
          width: 100
        },
        {
          title: "特殊药品",
          key: "specialManage",
          align: "center",
          width: 120,
          render(h, params) {
            let specialManage = params.row.specialManage;
            if (specialManage) {
              return h("Tag", { props: { type: "dot", color: "red" } }, "是");
            } else {
              return h("Tag", { props: { type: "dot", color: "blue" } }, "否");
            }
          }
        },
        {
          title: "转出仓库",
          align: "center",
          key: "warehouseName",
          width: 140
        }
        // ,
        // {
        //     title: "验收状态",
        //     key: 'checkStatus',
        //     width: 140,
        //     render (h, params) {
        //         let checkStatus = params.row.checkStatus;
        //         if (checkStatus) {
        //             return h('Tag', {props:{type:'dot', color:'green'}}, '已验收');
        //         }else{
        //             return h('Tag', {props:{type:'dot', color:'red'}}, '未验收');
        //         }
        //     }
        // },
        //
        // {
        //     title: "验收意见",
        //     key: 'checkResult',
        //     width: 140
        // },
        // {
        //     title: "验收员",
        //     key: 'checkUser',
        //     width: 140
        // },
        // {
        //     title: "验收时间",
        //     key: 'checkTime',
        //     width: 140,
        //     render: (h, params) => {
        //         let checkTime = params.row.checkTime;
        //         return checkTime ? moment(checkTime).format('YYYY-MM-DD HH:mm') : '';
        //     }
        // }
      ],
      totalAmount: 0,
      totalOutCount: 0,
      totalErrorCount: 0,
      currentChooseOrder: {},
      checkFileNo: "",
      checkFileModal: false
    };
  },
  watch: {
    detailList(data) {
      if (!data || data.length <= 0) {
        this.totalOutCount = 0;
        this.totalErrorCount = 0;
        this.totalAmount = 0;
      } else {
        this.totalAmount = data.reduce((total, item) => {
          return item.amount ? total + item.amount : total + 0;
        }, 0);
        this.totalOutCount = data.reduce((total, item) => {
          return item.quantity ? total + item.quantity : total + 0;
        }, 0);
        //this.totalErrorCount = data.reduce((total, item) => {return item.errorCount ? total + item.errorCount : total + 0}, 0);
      }
    }
  },
  methods: {
    refreshOrder() {
      let statusList = [];
      if (this.query.status === "CHECKED") {
        statusList = ["CHECKED"];
      } else if (this.query.status == "REVIEW") {
        statusList = ["REVIEW"];
      }
      let reqData = {
        statusList: statusList,
        warehouseId: this.query.warehouseId,
        //   supplierId: this.query.supplierId,
        startReceiveDate: this.dateRange[0],
        endReceiveDate: this.dateRange[1]
      };
      this.orderLoading = true;
      util.ajax
        .post("/repertory/out/list", reqData)
        .then(response => {
          this.orderLoading = false;
          this.orderList = response.data;
        })
        .catch(error => {
          this.orderLoading = false;
          util.errorProcessor(this, error);
        });
      this.currentChooseOrder = {};
      this.detailList = [];
    },

    handleSelectOrder(rowData) {
      if (!rowData || !rowData.id) {
        this.currentChooseOrder = {};
        this.detailList = [];
        return;
      }
      this.currentChooseOrder = rowData;
      this.reloadOrderDetail();
    },

    reloadOrderDetail() {
      this.detailLoading = true;
      util.ajax
        .get("/repertory/out/detail/" + this.currentChooseOrder.id)
        .then(response => {
          this.detailLoading = false;
          let data = response.data;
          if (data) {
            this.detailList = data.detailList;
            this.currentChooseOrder.details = this.detailList;
          }
        })
        .catch(error => {
          this.detailLoading = false;
          util.errorProcessor(this, error);
        });
    },

    checkOk() {
      if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
        this.$Message.warning("请选择需要审核的订单");
        return;
      }
      //验证订单的详情是否意见全部通过，如果是，才能提交
      let details = this.currentChooseOrder.details;
      if (!details || details.length <= 0) {
        this.$Message.warning("订单没有对应的产品详情信息,不能审核通过");
        return;
      }
      // for(let i=0; i<details.length; i++) {
      //     let item = details[i];
      //     if (!item || !item.checkStatus) {
      //         this.$Modal.warning({
      //             title: '信息提醒',
      //             content: '订单存在有未验收通过的产品，不能审核通过'
      //         });
      //         return;
      //     }
      // }
      let self = this;
      self.orderLoading = true;
      let reqData = {
        orderId: this.currentChooseOrder.id
      };
      //验收审核通过完成后，提示是否确认信息，然后提交
      this.$Modal.confirm({
        title: "审核信息确认",
        content: "是否已确认订单详情数据正确，提交审核通过后不能修改",
        onOk: () => {
          util.ajax
            .put("/repertory/out/set/outcheck", reqData)
            .then(response => {
              self.orderLoading = false;
              self.$Message.success("审查成功");
              self.refreshOrder();
            })
            .catch(error => {
              self.orderLoading = false;
              util.errorProcessor(self, error);
            });
        },
        onCancel: () => {}
      });
    }
  }
};
</script>

<style >
</style>

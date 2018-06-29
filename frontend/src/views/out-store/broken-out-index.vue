<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Row >
          <i-col :span="showSider ? '4' : '0'">
            <Card>
              <p slot="title">
                    未审核损耗出库单
                    <Tooltip transfer placement="right-start">
                        <Icon type="ios-help-outline"></Icon>
                        <div slot="content" >
                            <p>展现损耗出库后未审核通过的列表, 可以进行修改和删除操作</p>
                        </div>
                    </Tooltip>
                </p>
                <div slot="extra">
                    <a href="javascript:void(0)"  style="margin-right: 5px;" @click="reloadUncheckData">
                        <Icon type="refresh"></Icon>
                    </a>
                </div>
                <Table stripe highlight-row :loading="uncheckTabLoading"  
                        :columns="uncheckColumns" :data="uncheckData" ref="uncheckTable" 
                        style="width: 100%;" class="uncheck-table" height="750" 
                        size="small">
                </Table>

            </Card>
          </i-col>
          <i-col :span="showSider ? '20' : '24'">
            <Card>
                <p slot="title" >
                  <a href="javascript:void(0)" @click="changeSiderShow" style="margin-right: 5px;" >
                      <Icon v-if="showSider" type="chevron-left"></Icon>
                      <Icon v-else type="chevron-right"></Icon>
                  </a>
                    <Icon type="document"></Icon>损耗出库
                </p>
                <div slot="extra">
                   <ButtonGroup >
                        <Button type="primary" icon="android-add-circle" @click="saveOut" >保存</Button>
                   </ButtonGroup>
                </div>
                <Form :label-width="85" :model="changeStore" ref="changeStoreForm">
                    <Row>

                        <i-col span="5">
                        <FormItem label="自定义单号" prop="refOrderNumber">
                            <Input v-model="changeStore.refOrderNumber" />
                        </FormItem>
                        </i-col>

                        <i-col span="5">
                        <FormItem label="出库日期" prop="outDate">
                            <DatePicker type="datetime" v-model="changeStore.outDate" format="yyyy-MM-dd" size="small"/>
                        </FormItem>
                        </i-col>


                        <i-col span="10">
                        <FormItem label="单据摘要" prop="comment">
                            <Input v-model="changeStore.comment" />
                        </FormItem>
                        </i-col>
                        <!--<i-col span="5">-->
                        <!--<FormItem label="单据张数" prop="account">-->
                        <!--<Input v-model="changeStore.account" />-->
                        <!--</FormItem>-->
                        <!--</i-col>-->
                    </Row>
                    <Table border highlight-row
                           class="margin-top-8"
                           :columns="changeStoreColumns" :data="changeStoreItems"
                           ref="changeStoreTable" style="width: 100%;" size="small"
                           @on-row-dblclick="handleOutRowDbClick"
                           no-data-text="当前条件下查询，无库存数据！">
                    </Table>

                </Form>
            </Card>


        <br/>
            <Card>

                <p slot="title" >
                    <Icon type="document"></Icon>库存明细(选择需要报损的物品，加入出库单)
                </p>
                <!--<div slot="extra">
                    <ButtonGroup >
                        <Button type="primary" icon="android-add-circle" @click="queryRepertoryList">查询</Button>
                    </ButtonGroup>
                </div>-->
                <Form :label-width="85" :model="queryStore" ref="queryStoreForm">
                    <Row>
                        <i-col span="5">
                        <FormItem label="仓库：" prop="warehouseId">
                            <warehouse-select v-model="selectStore.warehouseId"   @on-change="queryRepertoryList" size="small"></warehouse-select><!--@on-change="onStoreChange"-->
                        </FormItem>
                        </i-col>
                        <!--<i-col span="10">
                        <FormItem label="商品速查" prop="goodsId">
                            <good-select v-model="queryStore.goodsId" size="small"></good-select>
                        </FormItem>
                        </i-col>-->
                    </Row>
                    <Table border highlight-row
                           class="margin-top-8"
                           :columns="queryStoreColumns" :data="queryStoreItems"
                           ref="queryStoreTable" style="width: 100%;" size="small"
                           @on-row-dblclick="handleStoreRowDbClick"
                           no-data-text="当前条件下查询，无库存数据！">
                    </Table>

                    <Row class="margin-top-8">
                        <div style="float: right;">
                            <Page :total="totalAmount" :current="currentPage" :page-size="pageSize" @on-change="changePage" size="small" show-total></Page>
                        </div>
                    </Row>
                </Form>
            </Card>
          </i-col>
        </Row>


        <!--<Modal  v-model="selectStoreModalShow" width="500" :mask-closable="false">-->
            <!--<p slot="header">-->
                <!--<Icon type="ios-plus-outline"></Icon>-->
                <!--<span>选择仓库</span>-->
            <!--</p>-->
            <!--<div >-->
                <!--<Row>-->
                    <!--<warehouse-select v-model="selectStore.warehouseId"  @on-change="onStoreChange" size="small"></warehouse-select>-->
                <!--</Row>-->
            <!--</div>-->
            <!--<div slot="footer">-->
                <!--<Button type="primary" @click="choseOutStore">确定</Button>-->
            <!--</div>-->
        <!--</Modal>-->
        <Modal v-model="closeConfirm"
               title="是否继续报损"
               @on-ok="clearData"
               @on-cancel="closeTab">
            <p>是否继续添加下一笔报损单?</p>
        </Modal>
    </div>
</template>
<script>
import moment from "moment";
import util from "@/libs/util.js";
import goodSelect from "@/views/selector/good-select.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";
export default {
  name: "cahneg-repertory-index",
  components: {
    warehouseSelect,
    goodSelect,
    goodsSpecTags,
  },
  data() {
    return {
      showSider: true,
      saving: false,
      totalAmount: 0,
      currentPage: 1,
      pageSize: 15,
      changeStoreItems: [],
      queryStoreItems: [],
      //selectStoreModalShow:false,
      closeConfirm: false,
      changeStore: {},
      queryStore: {
        warehouseName: ""
      },
      selectStore: {
        storeId: null
      },
      RepertoryOut: {
        id: "",
        warehouseId: "",
        outDate: "",
        refOrderNumber: "",
        customerName: "",
        comment: "",
        detail: []
      },
      RepertoryOutDetail: {
        repertoryId: "",
        amount: ""
      },
      uncheckTabLoading: false,
      uncheckData: [],
      uncheckColumns: [
        {
          title: "出库单",
          key: "id",
          render: (h, params) => {
            let self = this;
            let status = params.row.status;
            let statusLabel = "";
            let statusColor = "";
            /**if (status === "TEMP_STORAGE") {
              statusLabel = "暂存";
              statusColor = "#5cadff";
            } else*/ if (
              status === "INIT"
            ) {
              statusLabel = "待复核";
              statusColor = "#ff9900";
            } else if (status === "REVIEW_REJECT") {
              statusLabel = "复核拒绝";
              statusColor = "#ed3f14";
            } else if (status === "REVIEW") {
              statusLabel = "待终审";
              statusColor = "#19be6b";
            }
            let statusH = h(
              "span",
              {
                class: {
                  statusClass: true
                },
                style: {
                  color: statusColor
                }
              },
              statusLabel
            );
            let buttonH = h(
              "ButtonGroup",
              {
                props: {
                  size: "small"
                }
              },
              [
                h(
                  "Button",
                  {
                    props: {
                      type: "info"
                    },
                    on: {
                      click: () => {
                        self.editOutOrder(params.row.id);
                      }
                    }
                  },
                  "修改"
                ),
                h(
                  "Button",
                  {
                    props: {
                      type: "error"
                    },
                    on: {
                      click: () => {
                        self.removeOutOrder(
                          params.row.id,
                          params.row.orderNumber
                        );
                      }
                    }
                  },
                  "删除"
                )
              ]
            );

            let orderNumnber = params.row.orderNumber;
            let reviewOrderUser = params.row.reviewOrderUser
              ? params.row.reviewOrderUser + "："
              : " ";
            let reviewOrderResult = params.row.reviewOrderResult
              ? params.row.reviewOrderResult
              : " ";
            let review = reviewOrderUser + reviewOrderResult;
            let createTime = moment(params.row.createdTime).format(
              "YYYY-MM-DD HH:mm"
            );
            let createBy = params.row.createdBy;
            //let warehouseName = params.row.warehouseName;
            return h(
              "div",
              {
                style: {
                  margin: "0.5em"
                }
              },
              [
                h(
                  "h5",
                  {
                    style: {
                      color: "#9ea7b4",
                      fontSize: "12px"
                    }
                  },
                  orderNumnber
                ),
                h("h4", review),
                h(
                  "h5",
                  {
                    style: {
                      color: "#9ea7b4",
                      fontSize: "12px"
                    }
                  },
                  createTime + "[" + createBy + "]"
                ),
                h("hr", { size: "1", style: { color: "#e9eaec" } }),
                h("div", [statusH, buttonH])
              ]
            );
          }
        }
      ],
      changeStoreColumns: [
        {
          title: "仓库",
          align: "center",
          key: "warehouseName",
          width: 100
        },
        {
          title: "库位",
          align: "center",
          key: "location",
          width: 100
        },
        {
          title: "商品名称",
          key: "goodsName",
          align: "center",
          width: 150
        },
        {
          title: "产地",
          key: "origin",
          align: "center",
          width: 100,
        },

        {
          title: "规格",
          key: "spec",
          align: "center",
          width: 150,
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
          title: "单位",
          key: "unitName",
          align: "center",
          width: 80
        },

        {
          title: "数量",
          key: "quantity",
          align: "center",
          width: 120
        },
        {
          title: "损耗数量",
          key: "outAmount",
          align: "center",
          width: 120,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                value: self.changeStoreItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.changeStoreItems[params.index];
                  row[params.column.key] = event.target.value;
                  let checkLimit = event.target.value;
                  if (checkLimit > row.quantity) {
                    self.$Message.error("报损数量不能超出库存数量");
                  } else if (checkLimit <= 0) {
                    self.$Message.error("报损数量不能小于等于0");
                  }
                }
              }
            });
          }
        },
        {
          title: "单价",
          key: "buyPrice",
          align: "center",
          width: 80
        },

        {
          title: "批次号",
          key: "batchCode",
          align: "center",
          width: 120
        },
        /**{
          title: "生产日期",
          key: "productDate",
          align: "center",
          width: 120,
          render: (h, params) => {
            return h("span", moment(params.row.eta).format("YYYY-MM-DD"));
          }
        },*/
        {
          title: "有效日期",
          key: "expDate",
          align: "center",
          width: 120,
          render: (h, params) => {
            return h("span", moment(params.row.eta).format("YYYY-MM-DD"));
          }
        }
      ],
      queryStoreColumns: [
        /**{
          title: "货号",
          align: "center",
          key: "code",
          width: 100
        },*/
        {
          title: "商品名称",
          key: "goodsName",
          align: "center",
          width: 150
        },
        {
          title: "产地",
          key: "origin",
          align: "center",
          width: 100
        },

        {
          title: "规格",
          key: "spec",
          align: "center",
          width: 150,
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
          key: "factoryName",
          align: "center",
          width: 120
        },
        {
          title: "存储条件",
          key: "storageCondition",
          align: "center",
          width: 120
        },
        {
          title: "基药",
          key: "base_med_id",
          align: "center",
          width: 80
        },
        {
          title: "单位",
          key: "unitName",
          align: "center",
          width: 80
        },

        {
          title: "数量",
          key: "quantity",
          align: "center",
          width: 80
        },
        {
          title: "单价",
          key: "buyPrice",
          align: "center",
          width: 80
        },

        {
          title: "金额",
          key: "",
          align: "center",
          width: 80
        },
        {
          title: "税率",
          key: "out_tax",
          align: "center",
          width: 80
        },
        {
          title: "批次号",
          key: "batchCode",
          align: "center",
          width: 120
        },
        /**{
          title: "生产日期",
          key: "productDate",
          align: "center",
          width: 80,
          render: (h, params) => {
            return h("span", moment(params.row.eta).format("YYYY-MM-DD"));
          }
        },*/
        {
          title: "有效日期",
          key: "expDate",
          align: "center",
          width: 80,
          render: (h, params) => {
            return h("span", moment(params.row.eta).format("YYYY-MM-DD"));
          }
        },

        {
          title: "仓库点",
          key: "warehouseName",
          align: "center",
          width: 100
        },
        {
          title: "货位号",
          key: "location",
          align: "center",
          width: 100
        }
      ],
      ruleValidate: {
        supplierId: [
          {
            required: true,
            type: "number",
            message: "请选择供应商",
            trigger: "blur"
          }
        ],
        supplierContactId: [
          {
            required: true,
            type: "number",
            message: "请选择供应商代表",
            trigger: "blur"
          }
        ],
        buyerId: [
          {
            required: true,
            type: "number",
            message: "请选择采购员",
            trigger: "blur"
          }
        ],
        warehouseId: [
          {
            required: true,
            type: "number",
            message: "请选择仓库点",
            trigger: "blur"
          }
        ],
        orderItems: [
          {
            required: true,
            type: "array",
            array: { min: 1 },
            message: "请添加商品",
            trigger: "blur"
          }
        ]
      }
    };
  },
  mounted() {
    // this. selectStoreModalShow=true;
    this.reloadUncheckData();
  },
  activated() {
    this.clearData();
  },
  watch: {
    // repertoryCheckItems: function () {
    // }
  },
  methods: {
    moment: function() {
      return moment();
    },
    editOutOrder(id) {
      let req = {
        id: id
      };
      util.ajax
        .get("/repertory/out/getOutOrderDamage", { params: req })
        .then(response => {
          if (response.status === 200) {
            this.changeStoreItems = response.data.data;
            if (response.data.data.length > 0) {
              this.changeStore.refOrderNumber =
                response.data.data[0].refOrderNumber;
              this.changeStore.outDate = moment(
                response.data.data[0].outDate
              ).format("YYYY-MM-DD");
              this.changeStore.comment = response.data.data[0].comment;
            }
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    removeOutOrder(id, orderNumber) {
      this.$Modal.confirm({
        title: "确认删除报损单？",
        content: "<p>确认删除报损单 " + orderNumber + "?</p>",
        onOk: () => {
          this.removeConfirm(id);
        },
        onCancel: () => {}
      });
    },
    removeConfirm(id) {
      let req = {
        id: id
      };
      util.ajax
        .put("/repertory/out/deleteOrder/" + id)
        .then(response => {
          if (response.status == 200) {
            this.$Message.success("删除成功！");
            this.reloadUncheckData();
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    changeSiderShow() {
      this.showSider = !this.showSider;
    },
    reloadUncheckData() {
      this.uncheckTabLoading = true;
      util.ajax
        .get("/repertory/out/getUnchecked/" + "DAMAGE_OUT")
        .then(response => {
          if (response.status === 200 && response.data) {
            this.uncheckData = response.data.data;
            this.uncheckTabLoading = false;
          }
        })
        .catch(error => {
          this.uncheckTabLoading = false;
          util.errorProcessor(this, error);
        });
    },
    queryRepertoryList() {
      var self = this;
      /**this.repertoryItems = [];
       this.queryStore.page = this.currentPage;*/
      let requestData = {
        warehouseId: this.selectStore.warehouseId,
        //goodsId: this.queryStore.goodsId,
        page: this.currentPage,
        size: this.pageSize
      };
      util.ajax
        .post("/repertory/list", requestData)
        .then(function(response) {
          if (response.status === 200 && response.data) {
            self.queryStoreItems = response.data.data;
            self.totalAmount = response.data.total;
          }
        })
        .catch(function(error) {
          console.log(error);
          self.$Message.error(error);
        });
    },
    changePage(pageNumber) {
      this.currentPage = pageNumber;
      this.queryRepertoryList();
    },
    onStoreChange(data, item) {
      var self = this;
      this.queryStoreItems = [];
      this.changeStoreItems = [];
      this.queryStore.warehouseId = item.id;
      this.queryStore.page = this.currentPage;
      util.ajax
        .post("/repertory/list", this.queryStore)
        .then(function(response) {
          if (response.status === 200 && response.data) {
            //清空单据已有数据

            self.queryStoreItems = response.data.data;
            self.totalAmount = response.data.total;
          }
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    /**onReceiveStoreChange(data, item) {
      if (item.name === this.queryStore.warehouseName) {
        this.changeStore.warehouseId = "";
        this.$Message.info("转入仓库不能与转出仓库相同");
      }
    },*/
    handleStoreRowDbClick(row) {
      //库存记录点击
      for (var i = 0; i < this.changeStoreItems.length; i++) {
        if (row.id === this.changeStoreItems[i].id) {
          this.$Message.info("报损单已经存在此记录");
          return true;
        } else if (row.warehouseId != this.changeStoreItems[i].warehouseId) {
          this.$Message.info("报损单中只能报损同一仓库商品！");
          return true;
        }
      }
      this.$Modal.confirm({
        title: "确认报损此商品？",
        content: "<p>确认报损商品 " + row.goodsName + "?</p>",
        onOk: () => {
          this.changeStoreItems.splice(0, 0, row);
        },
        onCancel: () => {}
      });
    },
    handleOutRowDbClick(row) {
      //移库出库记录表，双击删除
      this.$Modal.confirm({
        title: "确认删除商品？",
        content: "<p>确认删除商品 " + row.name + "?</p>",
        onOk: () => {
          for (let i = 0; i < this.changeStoreItems.length; i++) {
            if (row.id === this.changeStoreItems[i].id) {
              this.changeStoreItems.splice(i, 1);
            }
          }
        },
        onCancel: () => {}
      });
    },
    clearData() {
      this.RepertoryOut = {};
      this.RepertoryOut.outDetailList = [];
      this.changeStoreItems = [];
    },
    closeTab() {
      this.clearData();
      let pageName = util.closeCurrentTab(this);
      this.$router.push({
        name: pageName
      });
    },
    saveOut() {
      //前台验证出库信息
      if (
        this.changeStoreItems.length == undefined ||
        this.changeStoreItems.length <= 0
      ) {
        this.$Message.error("报损出库单明细不能为空");
        return;
      }
      let self = this;
      this.RepertoryOut = {};
      this.RepertoryOut.outDetailList = [];
      this.RepertoryOut.warehouseId = this.changeStoreItems[0].warehouseId;
      this.RepertoryOut.id = this.changeStoreItems[0].repertoryOutId;
      this.RepertoryOut.refOrderNumber = this.changeStore.refOrderNumber;
      this.RepertoryOut.outDate = this.changeStore.outDate;
      this.RepertoryOut.comment = this.changeStore.comment;

      for (let i = 0; i < this.changeStoreItems.length; i++) {
        if (
          this.changeStoreItems[i].quantity < this.changeStoreItems[i].outAmount
        ) {
          this.$Message.error(
            this.changeStoreItems[i].goodsName +
              "报损数量超过库存数量，无法进行报损，请修改后重新提交！"
          );
          return;
        }
      }
      for (let i = 0; i < this.changeStoreItems.length; i++) {
        let RepertoryOutDetail = {};
        RepertoryOutDetail["repertoryInfoId"] = this.changeStoreItems[i].id;
        RepertoryOutDetail["quantity"] = this.changeStoreItems[i].outAmount;
        RepertoryOutDetail["price"] = this.changeStoreItems[i].buyPrice;
        this.RepertoryOut.outDetailList.push(RepertoryOutDetail);
        util.ajax
          .post("/repertory/out/loseRepertoryOut", this.RepertoryOut)
          .then(function(response) {
            if (response.status === 200) {
              self.$Message.info("报损出库单创建成功");
              this.reloadUncheckData();
              self.closeConfirm = true;
            }
            self.saving = false;
          })
          .catch(function(error) {
            console.log(error);
            self.saving = false;
            self.$Message.error("报损出库单错误");
          });
      }
    }
  }
};
</script>
<style lang="less">
@import "../../styles/common.less";
.uncheck-table .statusClass {
  display: block;
}

.uncheck-table .ivu-table-row-hover .statusClass {
  display: none;
}

.uncheck-table .ivu-btn-group {
  display: none;
}

.uncheck-table .ivu-table-row-hover .ivu-btn-group {
  display: block;
}
</style>



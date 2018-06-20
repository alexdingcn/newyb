<style lang="less">
@import "../../styles/common.less";

.table-footer-row {
  margin-top: 10px;
  font-size: 1.1em;
  font-weight: 600;
}
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

<template>
    <div>
        <Row :gutter="10">
            <i-col :span="showSider ? '4' : '0'">
                <Card>
                    <p slot="title">
                        未审核盘点单
                        <Tooltip transfer placement="right-start">
                            <Icon type="ios-help-outline"></Icon>
                            <div slot="content" >
                                <p>展现盘点单录入后未审核通过的列表, 可以提取修改和取消操作</p>
                            </div>
                        </Tooltip>
                    </p>
                    <div slot="extra">
                        <a href="javascript:void(0)" @click="reloadUncheckData" style="margin-right: 5px;" >
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
                    <p slot="title">
                        <a href="javascript:void(0)" @click="changeSiderShow" style="margin-right: 5px;" >
                            <Icon v-if="showSider" type="chevron-left"></Icon>
                            <Icon v-else type="chevron-right"></Icon>
                        </a>
                        <Icon type="compose"></Icon>
                        新增盘点单
                    </p>
                    <div slot="extra">
                        <ButtonGroup>
                            <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveHaddle">确认保存</Button>
                            <Button type="ghost" icon="reply" :loading="saveLoading" @click="resetForm">重置</Button>
                        </ButtonGroup>
                    </div>

                    <Form ref="addForm" :model="addForm" :rules="addFormRules" :label-width="90">
                        <Row class="row-margin-bottom">
                            <i-col span="6">
                                <FormItem label="仓库点" prop="warehouseId">
                                    <warehouse-select v-model="addForm.warehouseId" @on-change="warehouseChange" ></warehouse-select>
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="盘点名称" prop="orderName">
                                    <i-input v-model="addForm.orderName"></i-input>
                                </FormItem>
                            </i-col>
                        </Row>
                        
                        <h3 class="margin-top-10">
                            盘点商品明细
                            <ButtonGroup size="small">
                                <Button :disabled="!addForm.warehouseId" type="primary" icon="plus" @click="addGoodBtnClick">选择库存商品</Button>
                            </ButtonGroup>
                        </h3>
                        <Table ref="inventorysTable" highlight-row :loading="tableLoading" height="550" 
                            :columns="tableColumns" :data="tableData">
                            <div slot="footer">
                                <Row class="table-footer-row">
                                    <span style="margin-left: 30px; margin-right: 50px;">总计条数: <span style="color: #ed3f14;">{{totalCount}}</span></span>
                                    <span style="margin-right: 50px;">总计库存数量: <span style="color: #ed3f14;">{{totalQuantity}}</span></span>
                                    <span style="margin-right: 50px;">总计金额: <span style="color: #ed3f14;">¥{{totalAmount}}</span></span>
                                    <span style="margin-right: 50px;">总计盘点数量: <span style="color: #ed3f14;">{{totalInventoryQuantity}}</span></span>
                                </Row>
                            </div>
                        </Table>

                        <h3 class="margin-top-10">备注信息</h3>
                        <div class="margin-top-10 row-margin-bottom">
                            <Input type="textarea" v-model="addForm.comment" :rows="2" placeholder="暂无备注信息"/>
                        </div>
                    </Form>
                </Card>
            </i-col>
        </Row>

        <Modal v-model="selectRepertoryModal" width="80" :footerHide="true" :mask-closable="false" title="选择库存商品" >
            <repertory-info-select ref="repertorySelect" :warehouse="chooseWarehouse" @on-choosed="choosedGoods" ></repertory-info-select>
        </Modal>

        <Modal v-model="goodsExpandModal" width="60" :mask-closable="false" title="商品详情信息" :footerHide="true">
            <goods-expand ref="goodsExpand" :goodsSpecs="expandGoodsSpecs" :productDate="expandProductDate" :expDate="expandExpDate"></goods-expand>
        </Modal>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import repertoryInfoSelect from "@/views/selector/repertory-info-select.vue";
import goodsExpand from "@/views/goods/goods-expand.vue";
import goodsSpecTags from "@/views/goods/goods-spec-tabs.vue";

export default {
  name: "inventoryAdd",
  components: {
    warehouseSelect,
    repertoryInfoSelect,
    goodsExpand,
    goodsSpecTags
  },
  data() {
    return {
      saveLoading: false,
      addForm: {
        warehouseId: "",
        orderName: "",
        comment: ""
      },
      addFormRules: {
        warehouseId: [
          {
            required: true,
            type: "number",
            message: "仓库点必输",
            trigger: "change"
          }
        ],
        orderName: [
          {
            required: true,
            message: "盘点单名称必输",
            trigger: "blur"
          }
        ]
      },
      chooseWarehouse: {},
      tableLoading: false,
      tableData: [],
      tableColumns: [
        {
          type: "index",
          width: 80
        },
        {
          title: "商品名称",
          key: "goodsName",
          width: 200,
          render: (h, params) => {
            let self = this;
            return h(
              "Button",
              {
                props: {
                  type: "text",
                  icon: "eye"
                },
                on: {
                  click: () => {
                    self.expandProductDate = params.row.productDate
                      ? moment(params.row.productDate).format("YYYY-MM-DD")
                      : "";
                    self.expandExpDate = params.row.expDate
                      ? moment(params.row.expDate).format("YYYY-MM-DD")
                      : "";
                    self.expandGoodsSpecs = params.row.goods.goodsSpecs
                      ? params.row.goods.goodsSpecs
                      : [];
                    let goodsId = params.row.goods.id;
                    self.$refs.goodsExpand.loadGoodsData(goodsId);
                    self.goodsExpandModal = true;
                  }
                }
              },
              params.row.goods.name
            );
          }
        },
        {
          title: "供应商",
          key: "supplierName",
          width: 200
        },
        {
          title: "生产企业",
          key: "factoryName",
          width: 180,
          render: (h, params) => {
            return h("span", {}, params.row.goods.factoryName);
          }
        },
        {
          title: "产地",
          key: "origin",
          width: 120,
          render: (h, params) => {
            return h("span", {}, params.row.goods.origin);
          }
        },
        {
          title: "规格",
          key: "spec",
          width: 120,
          render: (h, params) => {
            return h(goodsSpecTags, {
              props: {
                tags: params.row.goods.goodsSpecs
                  ? params.row.goods.goodsSpecs
                  : [],
                color: "blue"
              }
            });
          }
        },
        {
          title: "批次号",
          key: "batchCode",
          width: 160
        },
        {
          title: "库位",
          key: "location",
          width: 130
        },
        {
          title: "库存量",
          key: "repertoryQuantity",
          width: 120
        },
        {
          title: "盘点数量",
          key: "inventoryQuantity",
          width: 150,
          render: (h, params) => {
            let self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.tableData[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.tableData[params.index];
                  row[params.column.key] = event.target.value;
                  self.$set(self.tableData, params.index, row);
                }
              }
            });
          }
        },
        {
          title: "入库单价",
          key: "buyPrice",
          width: 120,
          render: (h, params) => {
            let buyPrice = params.row.buyPrice ? params.row.buyPrice : "";
            return h("span", "¥" + buyPrice);
          }
        },
        {
          title: "金额",
          key: "amount",
          width: 120,
          render: (h, params) => {
            let price = params.row.buyPrice
              ? parseFloat(params.row.buyPrice)
              : 0;
            let repertoryQuantity = params.row.repertoryQuantity
              ? parseFloat(params.row.repertoryQuantity)
              : 0;
            let result = (price * repertoryQuantity).toFixed(2);
            return h("span", "¥" + result);
          }
        },
        {
          title: "生产日期",
          key: "productDate",
          width: 150,
          render: (h, params) => {
            return h(
              "span",
              params.row.productDate
                ? moment(params.row.productDate).format("YYYY-MM-DD")
                : ""
            );
          }
        },
        {
          title: "有效期至",
          key: "expDate",
          width: 150,
          render: (h, params) => {
            return h(
              "span",
              params.row.expDate
                ? moment(params.row.expDate).format("YYYY-MM-DD")
                : ""
            );
          }
        }
      ],
      goodsExpandModal: false,
      expandGoodsSpecs: [],
      expandProductDate: "",
      expandExpDate: "",
      totalCount: 0,
      totalQuantity: 0,
      totalAmount: 0,
      totalInventoryQuantity: 0,
      selectRepertoryModal: false,
      haveChooseRepertoryIds: [],
      showSider: true,
      uncheckTabLoading: false,
      uncheckData: [],
      uncheckColumns: [
        {
          title: "盘点单",
          key: "id",
          render: (h, params) => {
            let self = this;
            let status = params.row.status;
            let statusLabel = "";
            let statusColor = "";
            if (status === "UNCHECK") {
              statusLabel = "未审核";
              statusColor = "#ff9900";
            } else if (status === "CANCEL") {
              statusLabel = "已取消";
              statusColor = "#ed3f14";
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
                        self.editOrder(params.row);
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
                        self.cancelOrder(params.row.id);
                      }
                    }
                  },
                  "取消"
                )
              ]
            );

            let orderNumnber = params.row.orderNumber;
            let orderName = params.row.orderName ? params.row.orderName : "";
            let createdTime = moment(params.row.createdTime).format(
              "YYYY-MM-DD HH:mm"
            );
            let createdBy = params.row.createdBy;
            let warehouseName = params.row.warehouseName;
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
                h("h4", orderName + "[" + warehouseName + "]"),
                h(
                  "h5",
                  {
                    style: {
                      color: "#9ea7b4",
                      fontSize: "12px"
                    }
                  },
                  createdTime + "[" + createdBy + "]"
                ),
                h("hr", { size: "1", style: { color: "#e9eaec" } }),
                h("div", [statusH, buttonH])
              ]
            );
          }
        }
      ]
    };
  },
  watch: {
    tableData() {
      this.totalCount = this.tableData.length;
      this.totalQuantity = 0;
      this.totalAmount = 0;
      this.totalInventoryQuantity = 0;
      if (!this.tableData || this.tableData.length <= 0) {
        this.totalCount = 0;
        this.totalQuantity = 0;
        this.totalAmount = 0;
        this.totalInventoryQuantity = 0;
        return;
      }
      for (let i = 0; i < this.tableData.length; i++) {
        let item = this.tableData[i];
        let itemQuantity =
          item && item.repertoryQuantity
            ? parseFloat(item.repertoryQuantity)
            : 0;
        let amount = item && item.amount ? parseFloat(item.amount) : 0;
        let inventoryQuantity = item.inventoryQuantity
          ? parseFloat(item.inventoryQuantity)
          : 0;
        this.totalQuantity = this.totalQuantity + itemQuantity;
        this.totalAmount = this.totalAmount + amount;
        this.totalInventoryQuantity =
          this.totalInventoryQuantity + inventoryQuantity;
      }
    }
  },
  mounted() {
    this.reloadUncheckData();
  },
  methods: {
    changeSiderShow() {
      this.showSider = !this.showSider;
    },

    reloadUncheckData() {
      console.log("reloadUncheckData");
      let reqData = {
        statusList: ["UNCHECK"]
      };
      this.uncheckTabLoading = true;
      util.ajax
        .post("/inventory/list", reqData)
        .then(response => {
          this.uncheckTabLoading = false;
          console.log(response);
          this.uncheckData = response.data.data;
        })
        .catch(error => {
          this.uncheckTabLoading = false;
          util.errorProcessor(this, error);
        });
    },

    cancelOrder(orderId) {
      console.log("request cancel action by orderId " + orderId);
      let self = this;
      this.$Modal.confirm({
        title: "取消确认",
        content: "是否确认取消该笔盘点单，取消后不能修改?",
        onOk: () => {
          util.ajax
            .delete("/inventory/cancel/" + orderId)
            .then(response => {
              self.$Message.success("取消成功");
              self.reloadUncheckData();
            })
            .catch(error => {
              util.errorProcessor(self, error);
            });
        }
      });
    },

    editOrder(row) {
      console.log(row);
      this.resetForm(); //先清空后在赋值
      let data = JSON.parse(JSON.stringify(row));
      this.addForm = data;
      //获取详情
      this.reloadDetails(data.id);
    },

    reloadDetails(orderId) {
      util.ajax
        .get("/inventory/" + orderId + "/details")
        .then(response => {
          console.log(response.data);
          let dataList = response.data;
          if (!dataList || dataList.length <= 0) {
            return;
          }
          for (let i = 0; i < dataList.length; i++) {
            this.haveChooseRepertoryIds.push(dataList[i].repertoryId);
          }
          this.tableData = dataList;
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    warehouseChange(warehouseId, warehouse) {
      this.chooseWarehouse = warehouse && warehouse.id ? warehouse : {};
    },

    addGoodBtnClick() {
      if (!this.addForm.warehouseId || this.addForm.warehouseId <= 0) {
        this.$Message.warning("请先选择对应仓库点");
        return;
      }
      this.$refs.repertorySelect.searchBtnClicked();
      this.selectRepertoryModal = true;
    },

    choosedGoods(dataList) {
      console.log(dataList);
      if (!dataList || dataList.length <= 0) {
        return;
      }
      for (let i = 0; i < dataList.length; i++) {
        let item = dataList[i];
        if (
          !item ||
          !item.id ||
          this.haveChooseRepertoryIds.indexOf(item.id) >= 0
        ) {
          continue;
        }
        let repertoryQuantity = item.quantity ? parseFloat(item.quantity) : 0;
        let buyPrice = item.buyPrice ? parseFloat(item.buyPrice) : 0;
        let amount = (repertoryQuantity * buyPrice).toFixed(2);
        let data = {
          repertoryId: item.id,
          goodsId: item.goodsId,
          goods: item.goods,
          supplierId: item.supplierId,
          supplierName: item.supplierName,
          batchCode: item.batchCode,
          location: item.location,
          repertoryQuantity: repertoryQuantity,
          inventoryQuantity: repertoryQuantity,
          buyPrice: buyPrice,
          amount: amount,
          productDate: item.productDate,
          expDate: item.expDate
        };
        this.haveChooseRepertoryIds.push(item.id);
        this.tableData.push(data);
      }
      this.selectRepertoryModal = false;
    },

    resetForm() {
      this.addForm = {
        warehouseId: "",
        orderName: "",
        comment: ""
      };
      this.tableData = [];
      this.haveChooseRepertoryIds = [];
      this.totalCount = 0;
      this.totalQuantity = 0;
      this.totalAmount = 0;
      this.totalInventoryQuantity = 0;
      this.$refs.addForm.resetFields();
    },

    saveHaddle() {
      let self = this;
      this.$refs.addForm.validate(valid => {
        if (!valid) {
          return;
        } else {
          if (!self.tableData || self.tableData.length <= 0) {
            self.$Modal.info({
              title: "保存提示",
              content: "请先添加对应的盘点商品明细"
            });
            return;
          }
          self.$Modal.confirm({
            title: "保存确认",
            content: "是否确认输入的数据正确，提交并保存?",
            onOk: () => {
              self.addForm.details = self.tableData;
              console.log(self.addForm);
              self.saveLoading = true;
              util.ajax
                .post("/inventory/save", self.addForm)
                .then(response => {
                  self.saveLoading = false;
                  self.$Message.success("保存成功");
                  self.reloadUncheckData();
                  self.resetForm();
                })
                .catch(error => {
                  self.saveLoading = false;
                  util.errorProcessor(self, error);
                });
            }
          });
        }
      });
    }
  }
};
</script>



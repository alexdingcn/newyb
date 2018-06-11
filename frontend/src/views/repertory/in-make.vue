<style lang="less">
@import "../../styles/common.less";
</style>

<template>
	<div>
        <Row :gutter="10">
            <i-col :span="showSider ? '4' : '0'">
                <Card>
                    <p slot="title">
                        未审核采购单
                        <Tooltip transfer placement="right-start">
                            <Icon type="ios-help-outline"></Icon>
                            <div slot="content" >
                                <p>展现采购单录入后未审核通过的列表</p>
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
                    <Row slot="title" style="width:50%">
                        <div>
                            <a href="javascript:void(0)" @click="changeSiderShow" >
                                <Icon v-if="showSider" type="chevron-left"></Icon>
                                <Icon v-else type="chevron-right"></Icon>
                            </a>
                        </div>
                        <div  class="margin-left-20">
                            <Steps :current="0">
                                <Step title="采购收货" :content="currFlowContent"></Step>
                                <Step v-if="haveQAFlow" title="入库质量验收" content="抽样检查商品质量"></Step>
                                <Step v-if="haveFNFlow" title="入库单审核" content="审核后入库"></Step>
                            </Steps>
                        </div>
                    </Row>
                    <div slot="extra">
                        <ButtonGroup class="padding-left-20">
                            <Button type="primary" icon="android-add-circle" @click="buyCheckOrderGetBtnClick" >载入采购单</Button>
                            <Button type="success" icon="checkmark-round" @click="saveOrderBtnClick" :loading="saving">保存</Button>
                            <Button icon="bookmark" :loading="saving" @click="tempSaveOrderBtnClick"> 暂存 </Button>
                        </ButtonGroup>
                    </div>

                    <Form :label-width="90" :rules="ruleValidate" :model="order" ref="orderForm">
                        <Row class="row-margin-bottom">
                            <i-col span="6">
                                <FormItem label="供应商" prop="supplierId" >
                                    <supplier-select v-model="order.supplierId" @on-change="supplierChange" ></supplier-select>
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="供应商代表" prop="supplierContactId" >
                                    <supplier-contact-select v-model="order.supplierContactId" 
                                        :supplierId="order.supplierId" :disabled="!order.supplierId" 
                                        ></supplier-contact-select>
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="采购员" prop="buyerId">
                                    <buyer-select v-model="order.buyerId" ></buyer-select>
                                </FormItem>
                            </i-col>
                        </Row>
                        
                        <Row class="row-margin-bottom">
                            <i-col span="6">
                                <FormItem label="仓库点" prop="warehouseId">
                                    <warehouse-select v-model="order.warehouseId" ></warehouse-select>
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="采购属性" >
                                    <option-select optionType="BUY_TYPE" v-model="order.buyType" ></option-select>
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="直调来货单位" >
                                    <Input v-model="order.comeFrom" />
                                </FormItem>
                            </i-col>
                        </Row>
                        
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="商品" >
                                    <good-select ref="goodSelector" @on-change="onSelectGoods" :disabled="!order.warehouseId" :warehouseId="order.warehouseId" options="LW;LB;CBQ" ></good-select>
                                </FormItem>
                            </i-col>
                        </Row>

                        <Table border highlight-row
                            class="margin-top-8"
                            :columns="orderColumns" :data="orderItems"
                            ref="detailTable" style="width: 100%;" size="small"
                            no-data-text="在商品输入框选择后添加"
                            @on-row-dblclick="handleRowDbClick">
                            <div slot="footer">
                                <h3 class="padding-left-20" >
                                    <b>合计金额:</b> ￥{{ totalAmount }}
                                </h3>
                            </div>
                        </Table>

                        <div  v-if="isMedicine">
                            <h3 class="margin-top-20">到货信息</h3>
                            <Row class="row-margin-bottom">
                                <i-col span="6">
                                    <FormItem label="温控方式" >
                                        <option-select optionType="TEMPER_CONTROL" v-model="order.tempControlMethod" ></option-select>
                                    </FormItem>
                                </i-col>
                                <i-col span="6">
                                    <FormItem label="到货温度(℃)" >
                                        <Input :number="true" v-model="order.receiveTemp" />
                                    </FormItem>
                                </i-col>
                                <i-col span="6">
                                    <FormItem label="温控状态" >
                                        <option-select optionType="TEMPER_STATUS" v-model="order.tempControlStatus" ></option-select>
                                    </FormItem>
                                </i-col>
                            </Row>
                            <Row class="row-margin-bottom">
                                <i-col span="6">
                                    <FormItem label="收货日期" >
                                        <DatePicker transfer type="date" v-model="order.receiveDate" format="yyyy-MM-dd" />
                                    </FormItem>
                                </i-col>
                                <i-col span="6">
                                    <FormItem label="收货人员" >
                                        <Row>
                                            <Input v-model="order.receiveUser" style="width: 90%;" placeholder="张三;李四" />
                                            <Tooltip transfer >
                                                <Icon type="ios-information-outline" size="20"></Icon>
                                                <div slot="content" >
                                                    <strong>用于登记收货人员姓名</strong><br/>
                                                    <strong>提示:如果是多人收货,请使用“;”号分割多个人的姓名, 例如：张三;李四</strong><br/>
                                                    <strong style="color: red;">注意：如果商品列表中存在有“特殊管理药品”标识的商品，至少需要双人收货, 并且供应商也需要有“特殊管理药品”经营权</strong>
                                                </div>
                                            </Tooltip>
                                        </Row>
                                    </FormItem>
                                </i-col>
                            </Row>

                            <h3 class="margin-top-20">运输配送</h3>
                            <Row class="row-margin-bottom">
                                <i-col span="6">
                                    <FormItem label="运输方式" prop="shipMethod">
                                        <option-select optionType="SHIP_METHOD" v-model="order.shipMethod" ></option-select>
                                    </FormItem>
                                </i-col>
                                <i-col span="6">
                                    <FormItem label="运输工具" prop="shipTool">
                                        <option-select optionType="SHIP_TOOL" v-model="order.shipTool" ></option-select>
                                    </FormItem>
                                </i-col>
                                <i-col span="6">
                                    <FormItem label="承运单位" >
                                        <ship-company-select v-model="order.shipCompanyId" ></ship-company-select>
                                    </FormItem>
                                </i-col>
                            </Row>
                            <Row class="row-margin-bottom">
                                <i-col span="6">
                                    <FormItem label="运输车牌号" >
                                        <Input v-model="order.shipCarNo"  />
                                    </FormItem>
                                </i-col>
                                <i-col span="6">
                                    <FormItem label="驾驶员" >
                                        <Input v-model="order.shipDriverName"  />
                                    </FormItem>
                                </i-col>
                                <i-col span="6">
                                    <FormItem label="运输单号" >
                                        <Input v-model="order.shipEntrustNo"  />
                                    </FormItem>
                                </i-col>
                            </Row>
                            <Row class="row-margin-bottom">
                                <i-col span="6">
                                    <FormItem label="启运时间" >
                                        <DatePicker type="datetime" transfer v-model="order.shipStartDate" format="yyyy-MM-dd HH:mm" />
                                    </FormItem>
                                </i-col>
                                <i-col span="6">
                                    <FormItem label="到货时间" >
                                        <DatePicker type="datetime" transfer v-model="order.shipEndDate" format="yyyy-MM-dd HH:mm" />
                                    </FormItem>
                                </i-col>
                                <i-col span="10">
                                    <FormItem label="发运地址" >
                                        <Input v-model="order.shipStartAddress"  />
                                    </FormItem>
                                </i-col>
                            </Row>
                        </div>

                        <h3 class="margin-top-20">开票信息</h3>
                        <Row class="row-margin-bottom">
                            <i-col span="6">
                                <FormItem label="付款日期" >
                                    <DatePicker transfer type="date" v-model="order.payDate" />
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="发票种类" >
                                    <option-select optionType="BILL_TYPE" v-model="order.billType" ></option-select>
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="账期" >
                                    <Input number v-model="order.term" />
                                </FormItem>
                            </i-col>
                        </Row>

                        <div class="margin-top-10">
                            <Input type="textarea" v-model="order.keyWord" :rows="2" placeholder="暂无摘要信息"/>
                        </div>

                    </Form>
                </Card>
            </i-col>
        </Row>


		<Modal v-model="closeConfirm"
			   title="是否继续下单"
			   @on-ok="clearData"
		       @on-cancel="closeTab">
			<p>是否继续添加下一笔订单?</p>
		</Modal>

        <Modal v-model="buyCheckOrder" title="采购单提取" :mask-closable="false" width="75" footerHide className="extract-buy-order">
            <buy-order-list @on-choosed="buyOrderChoose" :chooseModal="true" ></buy-order-list>
        </Modal>

        <warehouse-location-modal :openModal="locationModal" :warehouseId="order.warehouseId" @on-ok="chooseLocation" @on-close="locationModalClose"></warehouse-location-modal>

	</div>

</template>

<script>
import moment from "moment";
import util from "@/libs/util.js";
import supplierSelect from "@/views/selector/supplier-select.vue";
import supplierContactSelect from "@/views/selector/supplier-contact-select.vue";
import buyerSelect from "@/views/selector/buyer-select.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import shipCompanySelect from "@/views/selector/ship-company-select.vue";
import goodSelect from "@/views/selector/good-select.vue";
import buyOrderList from "@/views/buy/buy-order-list.vue";
import warehouseLocationModal from "@/views/selector/warehouse-location-modal.vue";
import optionSelect from "@/views/selector/option-select.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";

export default {
  name: "in-make",
  components: {
    supplierSelect,
    supplierContactSelect,
    buyerSelect,
    shipCompanySelect,
    warehouseSelect,
    goodSelect,
    buyOrderList,
    warehouseLocationModal,
    optionSelect,
    goodsSpecTags
  },
  data() {
    const addWarehouseLocation = (h, location, rowData, index) => {
      let label = location ? location : "";
      return h("div", [
        h("span", label),
        h("Button", {
          props: {
            type: "text",
            size: "small",
            icon: "edit"
          },
          on: {
            click: () => {
              this.openChooseLocation(rowData, index);
            }
          }
        })
      ]);
    };

    return {
      saving: false,
      edittingRow: {},
      closeConfirm: false,
      orderItems: [],
      order: {
        supplierId: null,
        receiveDate: moment().format("YYYY-MM-DD"),
        orderItemIds: []
      },
      currEditLocationRow: {},
      currDditLocationIndex: "",
      locationModal: false,
      orderColumns: [
        {
          type: "index",
          title: "",
          width: 30
        },
        {
          title: "商品名称",
          key: "goodsName",
          width: 150,
          sortable: true,
          render: (h, params) => {
            return h("span", params.row.goods.name);
          }
        },
        {
          title: "产地",
          key: "origin",
          width: 80,
          render: (h, params) => {
            return h("span", params.row.goods.origin);
          }
        },
        {
          title: "规格",
          key: "goodsSpecs",
          width: 120,
          render: (h, params) => {
            return h(goodsSpecTags, {
              props: {
                tags: params.row.goods.goodsSpecs,
                color: "blue"
              }
            });
          }
        },
        {
          title: "生产企业",
          key: "factoryName",
          width: 120,
          render: (h, params) => {
            return h("span", params.row.goods.factoryName);
          }
        },
        {
          title: "单位",
          key: "unitName",
          width: 80,
          render: (h, params) => {
            return h("span", params.row.goods.unitName);
          }
        },
        {
          title: "大件单位",
          key: "packUnitName",
          width: 80,
          render: (h, params) => {
            return h("span", params.row.goods.packUnitName);
          }
        },
        {
          title: "大件装量",
          key: "bigPack",
          width: 80,
          render: (h, params) => {
            return h("span", params.row.goods.bigPack);
          }
        },
        {
          title: "批次号",
          key: "batchCode",
          width: 120,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "采购数",
          key: "buyOrderQuality",
          width: 100
        },
        {
          title: "本次收货数量",
          key: "receiveQuality",
          width: 130,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                  self.resetAmount(params.index);
                }
              }
            });
          }
        },
        {
          title: "大件数量",
          key: "bigQuality",
          width: 130,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "赠送数量",
          key: "free",
          align: "center",
          width: 120,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                  self.resetAmount(params.index);
                }
              }
            });
          }
        },
        {
          title: "最近采购价",
          key: "lastBuyPrice",
          width: 100,
          render: (h, params) => {
            let lastBuyPrice = params.row.goods.lastBuyPrice
              ? params.row.goods.lastBuyPrice
              : "";
            return h("span", lastBuyPrice);
          }
        },
        {
          title: "单价",
          key: "price",
          width: 120,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                  self.resetAmount(params.index);
                }
              }
            });
          }
        },
        {
          title: "金额",
          key: "amount",
          width: 130,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "生产日期",
          key: "productDate",
          width: 150,
          render: (h, params) => {
            let self = this;
            return h("DatePicker", {
              props: {
                type: "date",
                placement: "top",
                value: params.row.productDate
              },
              on: {
                "on-change": date => {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = date;
                },
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "有效期至",
          key: "expDate",
          width: 150,
          render: (h, params) => {
            let self = this;
            return h("DatePicker", {
              props: {
                type: "date",
                placement: "top",
                value: params.row.expDate
              },
              on: {
                "on-change": date => {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = date;
                },
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "货位号",
          key: "warehouseLocation",
          width: 150,
          render: (h, params) => {
            return addWarehouseLocation(
              h,
              params.row.warehouseLocation,
              params.row,
              params.index
            );
          }
        },
        {
          title: "拒收数量",
          key: "rejectQuality",
          align: "center",
          width: 120,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "拒收原因",
          key: "rejectComment",
          align: "center",
          width: 200,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "税率",
          key: "taxRate",
          width: 100,
          render: (h, params) => {
            var self = this;
            return h("Input", {
              props: {
                number: true,
                value: self.orderItems[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderItems[params.index];
                  row[params.column.key] = event.target.value;
                }
              }
            });
          }
        },
        {
          title: "商品条码",
          key: "barCode",
          width: 150,
          render: (h, params) => {
            let barCode =
              params.row.goods && params.row.goods.barCode
                ? params.row.goods.barCode
                : "";
            return h("span", barCode);
          }
        },
        {
          title: "当前库存",
          key: "currRepQuatity",
          width: 100,
          render: (h, params) => {
            let currRepQuatity = params.row.goods.currRepQuatity
              ? params.row.goods.currRepQuatity
              : "";
            return h("span", currRepQuatity);
          }
        },
        {
          title: "在单数量",
          key: "currBuyQuality",
          width: 100,
          render: (h, params) => {
            let currBuyQuality = params.row.goods.currBuyQuality
              ? params.row.goods.currBuyQuality
              : "";
            return h("span", currBuyQuality);
          }
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
        details: [
          {
            required: true,
            type: "array",
            array: { min: 1 },
            message: "请添加商品",
            trigger: "blur"
          }
        ]
      },
      editView: false,
      buyCheckOrder: false,
      isMedicine: false,
      showSider: true,
      uncheckTabLoading: false,
      currFlowContent: "检查外包装，确认数量和批次信息",
      uncheckData: [],
      uncheckColumns: [
        {
          title: "收货入库单",
          key: "id",
          minWihth: 250,
          render: (h, params) => {
            let self = this;
            let status = params.row.status;
            let statusLabel = "";
            let statusColor = "";
            if (status === "TEMP_STORAGE") {
              statusLabel = "暂存";
              statusColor = "#5cadff";
            } else if (status === "INIT") {
              statusLabel = "待质审";
              statusColor = "#ff9900";
            } else if (status === "CHECKED") {
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
                        self.editUncheckOrder(params.row);
                      }
                    }
                  },
                  "修改"
                ),
                h(
                  "Button",
                  {
                    props: {
                      type: "error",
                      disabled: status != "TEMP_STORAGE"
                    },
                    on: {
                      click: () => {
                        self.removeOrder(params.row.id);
                      }
                    }
                  },
                  "删除"
                )
              ]
            );

            let orderNumnber = params.row.orderNumber;
            let supplierName = params.row.supplierName;
            let createTime = moment(params.row.createTime).format(
              "YYYY-MM-DD HH:mm"
            );
            let saleNickName = params.row.saleNickName;
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
                h("h4", supplierName + "[" + warehouseName + "]"),
                h(
                  "h5",
                  {
                    style: {
                      color: "#9ea7b4",
                      fontSize: "12px"
                    }
                  },
                  createTime + "[" + saleNickName + "]"
                ),
                h("hr", { size: "1", style: { color: "#e9eaec" } }),
                h("div", [statusH, buttonH])
              ]
            );
          }
        }
      ],
      haveQAFlow: false,
      haveFNFlow: false,
      supplierColdManage: false,
      supplierSpecialManage: false
    };
  },
  mounted() {
    this.init();
  },
  activated() {
    this.clearData();
  },
  computed: {
    totalAmount() {
      return this.orderItems.reduce(function(total, item) {
        return total + parseFloat(item.amount);
      }, 0);
    }
  },
  watch: {},
  methods: {
    init() {
      //先获取系统的流程配置参数,
      this.loadSystemConfig();
      this.reloadUncheckData();
    },

    loadSystemConfig() {
      //获取一些系统配置中的信息
      util.ajax
        .get("/config/list")
        .then(response => {
          let data = response.data;
          //公司主营业务类型
          let config = data["COMPANY_TYPE"];
          if ("medicine" === config.keyValue) {
            this.isMedicine = true;
          } else {
            this.isMedicine = false;
          }
          //质量检查流程是否需要
          let flowQA = data["BUY_QUALITY_CHECK"];
          if ("open" === flowQA.keyValue) {
            this.haveQAFlow = true;
          } else {
            this.haveQAFlow = false;
          }
          //终审流程是否需要
          let flowFN = data["BUY_FINAL_CHECK"];
          if ("open" === flowFN.keyValue) {
            this.haveFNFlow = true;
          } else {
            this.haveFNFlow = false;
          }
          if (!this.haveQAFlow && !this.haveFNFlow) {
            this.currFlowContent =
              "检查外包装，确认数量和批次信息, 保存后直接入库且修改库存";
          }
        })
        .catch(error => {
          console.log(error);
          util.errorProcessor(this, error);
        });
    },

    changeSiderShow() {
      this.showSider = !this.showSider;
    },

    reloadUncheckData() {
      let reqData = {
        statusList: ["TEMP_STORAGE", "INIT", "CHECKED"]
      };
      console.log("load sider table data.");
      this.uncheckTabLoading = true;
      util.ajax
        .post("/repertory/in/list", reqData)
        .then(response => {
          this.uncheckTabLoading = false;
          this.uncheckData = response.data;
        })
        .catch(error => {
          this.uncheckTabLoading = false;
          util.errorProcessor(this, error);
        });
    },

    moment: function() {
      return moment();
    },
    handleRowDbClick(row) {
      this.$Modal.confirm({
        title: "确认删除商品？",
        content: "<p>是否确认删除选择的商品 " + row.goods.name + "?</p>",
        onOk: () => {
          for (var i = 0; i < this.orderItems.length; i++) {
            if (row.id === this.orderItems[i].id) {
              this.orderItems.splice(i, 1);
              this.order.orderItemIds.splice(i, 1);
            }
          }
        },
        onCancel: () => {}
      });
    },

    resetAmount(index) {
      let row = this.orderItems[index];
      let receiveQuality =
        row["receiveQuality"] && !isNaN(row["receiveQuality"])
          ? row["receiveQuality"]
          : 0;
      let price = row["price"] && !isNaN(row["price"]) ? row["price"] : 0;
      let free = row["free"] && !isNaN(row["free"]) ? row["free"] : 0;
      let num = receiveQuality - free > 0 ? receiveQuality - free : 0;
      row.amount = (num * price).toFixed(2);
    },

    onSelectGoods(goodsId, goods) {
      if (!this.order.warehouseId || this.order.warehouseId <= 0) {
        this.$Modal.info({
          title: "操作提示",
          content: "请先选择对应仓库"
        });
        this.$refs.goodSelector.clearSingleSelect();
        return;
      }
      if (!goodsId || !goods) {
        return;
      }
      var index = this.order.orderItemIds.indexOf(goods.id);
      if (index < 0) {
        if (goods.specialManage && !this.supplierSpecialManage) {
          this.$Modal.info({
            title: "药品特殊经营监管提示",
            content:
              "商品：" +
              goods.name +
              "存在“药品特殊经营”标识，而供应商没有该资质，不能添加该商品."
          });
          return;
        }
        if (goods.coldManage && !this.supplierColdManage) {
          this.$Modal.info({
            title: "冷链经营管理监管提示",
            content:
              "商品：" +
              goods.name +
              "存在“冷链经营管理”标识，而供应商没有该资质，不能添加该商品."
          });
          return;
        }
        //根据选择的商品信息，组织入库详情信息
        this.addOrdderItem(goods);
      } else {
        this.$Message.warning("该商品已经添加");
        this.$refs.goodSelector.clearSingleSelect();
      }
    },
    addOrdderItem(goods) {
      var item = {
        goodsId: goods.id,
        goods: goods,
        receiveQuality: 0,
        bigQuality: 0,
        free: 0,
        price: goods.inPrice ? goods.inPrice : 0,
        expDate: "",
        productDate: "",
        batchCode: "",
        amount: 0,
        warehouseLocation: "",
        rejectQuality: 0,
        rejectComment: "",
        taxRate: goods.inTax ? goods.inTax : 0
      };
      this.orderItems.push(item);
      this.order.orderItemIds.push(goods.id);
      this.$refs.goodSelector.clearSingleSelect();
    },
    doSave(status) {
      var self = this;
      this.saving = true;
      this.order["status"] = status;
      util.ajax
        .post("/repertory/in/save", this.order)
        .then(function(response) {
          self.saving = false;
          self.$Message.info("采购入库订单保存成功");
          self.reloadUncheckData();
          self.closeConfirm = true;
        })
        .catch(function(error) {
          self.saving = false;
          util.errorProcessor(self, error);
        });
    },
    clearData() {
      this.order = {
        supplierId: null,
        orderItemIds: []
      };
      this.orderItems = [];
      this.editView = false;
      this.$refs.orderForm.resetFields();
    },
    closeTab() {
      this.clearData();
      let pageName = util.closeCurrentTab(this);
      this.$router.push({
        name: pageName
      });
    },

    validateSpecialManage() {
      //药品特殊经营管理类型的，需要验证是否以及双人审核，并且需要验证供应商是否也存在“特殊药品经营”资质
      //先检查下商品中是否存在有特殊药品管理标识
      let result = {
        code: 1,
        message: ""
      };
      let haveSpecial = false;
      let goodsName = "";
      for (let i = 0; i < this.orderItems.length; i++) {
        let goods = this.orderItems[i].goods;
        if (goods && goods.specialManage) {
          haveSpecial = true;
          goodsName = goods.name;
          break;
        }
      }
      if (haveSpecial) {
        //存在特殊管理药品, 需要验证供应商是否也有这个资质
        console.log(this.supplierSpecialManage);
        if (!this.supplierSpecialManage) {
          result.code = -1;
          result.message =
            "商品：" +
            goodsName +
            "存在有“药品特殊管理”标识, 而供应商没有“药品特殊经营管理”资质";
          return result;
        }
        //如果供应商也有资质，则验证是否已经存在双人收货，特殊管理药品需要双人收货
        let receiveUser = this.order.receiveUser;
        if (
          !receiveUser ||
          (receiveUser.indexOf(";") <= 0 && receiveUser.indexOf("；") <= 0)
        ) {
          result.code = -2;
          result.message =
            "商品：" +
            goodsName +
            "存在有“药品特殊管理”标识, 收货员需要双人收货，需要在收货员项中输入如：张三;李四 方式的双人收货员的姓名";
          return result;
        }
      }
      //都能验证通过的话，才能通过
      return result;
    },

    validateColdManage() {
      //冷链经营标识，需要验证供应商是否也存在“冷链经营”资质, 且：温控方式，到货温度，温控状态，运输方式为必输项
      //先检查下商品中是否存在有冷链经营标识
      let result = {
        code: 1,
        message: ""
      };
      let haveCold = false;
      let goodsName = "";
      for (let i = 0; i < this.orderItems.length; i++) {
        let goods = this.orderItems[i].goods;
        if (goods && goods.coldManage) {
          haveCold = true;
          goodsName = goods.name;
          break;
        }
      }
      if (!haveCold) {
        return result; //没有这样的商品，直接返回
      }
      //存在冷链经营商品, 需要验证供应商是否也有这个资质
      if (!this.supplierColdManage) {
        result.code = -1;
        result.message =
          "商品：" +
          goodsName +
          "存在有“冷链经营”标识, 而供应商没有“冷链经营”资质";
        return result;
      }
      //如果供应商也有资质，则验证：温控方式，到货温度，温控状态，运输方式为必输项

      if (
        !this.order.tempControlMethod ||
        this.order.receiveTemp === "" ||
        this.order.receiveTemp === undefined ||
        !this.order.tempControlStatus ||
        !this.order.shipMethod
      ) {
        result.code = -2;
        result.message =
          "商品：" +
          goodsName +
          "存在有“冷链经营”标识, 温控方式、到货温度、温控状态、运输方式为必输项";
        return result;
      }
      //都能验证通过的话，才能通过
      return result;
    },

    saveOrderBtnClick() {
      this.order.details = this.orderItems;
      //验证药品类型商品的特殊经营管理类商品，需要双人收货的信息验证
      let specialValidate = this.validateSpecialManage();
      if (specialValidate.code < 0) {
        this.$Modal.info({
          title: "商品药品特殊经营管理提示",
          content: specialValidate.message
        });
        return;
      }

      //验证是否冷链经营的必输信息以及供应商也需要冷链经营标识
      let coldValidate = this.validateColdManage();
      if (coldValidate.code < 0) {
        this.$Modal.info({
          title: "商品冷链经营管理提示",
          content: coldValidate.message
        });
        return;
      }

      let self = this;
      this.$refs.orderForm.validate(valid => {
        if (!valid) {
          self.$Message.error("请检查必输项和是否已经添加商品!");
          return;
        } else {
          let contentLabel = "是否确认输入的数据完全正确? ";
          if (self.haveQAFlow) {
            contentLabel = contentLabel + "提交到下一步：入库质量验收";
          } else if (self.haveFNFlow) {
            contentLabel = contentLabel + "提交到下一步：入库审核";
          } else {
            contentLabel = contentLabel + "提交后直接变更库存, 不能再修改.";
          }
          self.$Modal.confirm({
            title: "保存确认提示",
            content: contentLabel,
            onOk: () => {
              self.doSave("INIT");
            }
          });
        }
      });
    },
    tempSaveOrderBtnClick() {
      this.order.details = this.orderItems;
      this.$refs.orderForm.validate(valid => {
        if (!valid) {
          this.$Message.error("请检查必输项和是否已经添加商品!");
          return;
        } else {
          this.doSave("TEMP_STORAGE");
        }
      });
    },

    buyCheckOrderGetBtnClick() {
      this.buyCheckOrder = true;
    },

    buyOrderChoose(buyOrder) {
      if (!buyOrder || !buyOrder.id) {
        this.$Message.warning("获取选取的订单信息失败");
        return;
      }
      util.ajax
        .get("/repertory/in/buy/order/" + buyOrder.id)
        .then(response => {
          let data = response.data;
          console.log(data);
          if (data) {
            this.buyCheckOrder = false;
            this.supplierColdManage = data.supplierColdManage ? true : false;
            this.supplierSpecialManage = data.supplierSpecialManage
              ? true
              : false;
            console.log(
              this.supplierSpecialManage + ", " + this.supplierColdManage
            );
            data.receiveDate = data.receiveDate
              ? moment(data.receiveDate).format("YYYY-MM-DD")
              : moment().format("YYYY-MM-DD");
            data.payDate = data.payDate
              ? moment(data.payDate).format("YYYY-MM-DD")
              : "";
            data.shipStartDate = data.shipStartDate
              ? moment(data.shipStartDate).format("YYYY-MM-DD HH:mm")
              : "";
            data.shipEndDate = data.shipEndDate
              ? moment(data.shipEndDate).format("YYYY-MM-DD HH:mm")
              : "";
            this.editView = true;
            this.order = data;
            this.setEditOrderDetail(data.details);
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    openChooseLocation(rowData, index) {
      if (!rowData || index === undefined || index < 0) {
        this.$Message.error("获取编辑位置失败");
        return;
      }
      this.currEditLocationRow = rowData;
      this.currDditLocationIndex = index;
      this.locationModal = true;
    },

    chooseLocation(data) {
      this.locationModal = false;
      this.currEditLocationRow.warehouseLocation = data.location;
      console.log(this.currEditLocationRow);
      this.$set(
        this.orderItems,
        this.currDditLocationIndex,
        this.currEditLocationRow
      );
    },

    locationModalClose() {
      this.locationModal = false;
      this.currDditLocationIndex = "";
      this.currEditLocationRow = {};
    },

    editUncheckOrder(row) {
      this.clearData();
      //提取修改
      console.log(row);
      if (!row || !row.id) {
        return;
      }
      let json = JSON.parse(JSON.stringify(row)); //创建一个新的对象来修改

      //设置当前单子的供应商冷链和特殊药品标识
      this.supplierColdManage = json.supplierColdManage ? true : false;
      this.supplierSpecialManage = json.supplierSpecialManage ? true : false;
      json.receiveDate = json.receiveDate
        ? moment(json.receiveDate).format("YYYY-MM-DD")
        : moment().format("YYYY-MM-DD");
      json.payDate = json.payDate
        ? moment(json.payDate).format("YYYY-MM-DD")
        : "";
      json.shipStartDate = json.shipStartDate
        ? moment(json.shipStartDate).format("YYYY-MM-DD HH:mm")
        : "";
      json.shipEndDate = json.shipEndDate
        ? moment(json.shipEndDate).format("YYYY-MM-DD HH:mm")
        : "";

      this.editView = true;
      this.order = json;

      //获取入库单详情
      util.ajax
        .get("/repertory/in/detail/" + this.order.id)
        .then(response => {
          console.log(response.data);
          this.setEditOrderDetail(response.data);
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    setEditOrderDetail(details) {
      if (!details || details.length <= 0) {
        this.orderItems = [];
        this.order["orderItemIds"] = [];
      }
      for (let i = 0; i < details.length; i++) {
        //没有值的数据需要初始化存在输入框的值
        details[i].batchCode = details[i].batchCode ? details[i].batchCode : "";
        details[i].receiveQuality = details[i].receiveQuality
          ? details[i].receiveQuality
          : 0;
        details[i].bigQuality = details[i].bigQuality
          ? details[i].bigQuality
          : 0;
        details[i].free = details[i].free ? details[i].free : 0;
        details[i].price = details[i].price ? details[i].price : 0;
        details[i].amount = details[i].amount ? details[i].amount : 0;
        details[i].warehouseLocation = details[i].warehouseLocation
          ? details[i].warehouseLocation
          : "";
        details[i].rejectQuality = details[i].rejectQuality
          ? details[i].rejectQuality
          : 0;
        details[i].rejectComment = details[i].rejectComment
          ? details[i].rejectComment
          : "";
        details[i].taxRate = details[i].taxRate ? details[i].taxRate : 0;

        // productDate/expDate;
        let productDate = details[i].productDate
          ? moment(details[i].productDate).format("YYYY-MM-DD")
          : "";
        let expDate = details[i].expDate
          ? moment(details[i].expDate).format("YYYY-MM-DD")
          : "";
        details[i].expDate = expDate;
        details[i].productDate = productDate;
      }

      this.orderItems = details;
      let itemIds = [];
      if (this.orderItems && this.orderItems.length > 0) {
        for (let i = 0; i < this.orderItems.length; i++) {
          let item = this.orderItems[i];
          if (item && item.goodsId) {
            itemIds.push(item.goodsId);
          }
        }
      }
      this.order["orderItemIds"] = itemIds;
    },

    removeOrder(orderId) {
      //删除入库单,只能删除暂存状态的入库单
      console.log(orderId);
      let self = this;
      this.$Modal.confirm({
        title: "删除确认提醒",
        content: "是否确认删除订单信息，删除后不可恢复",
        onOk: () => {
          util.ajax
            .delete("/repertory/in/remove/" + orderId)
            .then(response => {
              self.$Message.success("删除成功");
              self.reloadUncheckData();
            })
            .catch(error => {
              util.errorProcessor(self, error);
            });
        }
      });
    },

    supplierChange(supplierId, supplier) {
      //赋值特殊管理标识
      console.log(supplier);
      if (supplier && supplier.id) {
        this.supplierColdManage = supplier.coldBusiness ? true : false;
        this.supplierSpecialManage = supplier.canSpecial ? true : false;
      }
    }
  }
};
</script>

<style>
.ivu-form-item {
  margin-bottom: 0px;
}

.ivu-table-cell {
  padding-left: 5px;
  padding-right: 5px;
}
.ivu-table-body,
.ivu-table-tip {
  min-height: 300px;
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

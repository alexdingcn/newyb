<style lang="less">
@import "../../styles/common.less";
</style>
<template>
  <div>
      <Card>
            <div slot="title" style="width:40%">
                <Steps :current="0">
                    <Step title="入库质量验收" :content="currFlowContent"></Step>
                    <Step v-if="haveFNFlow" title="入库单审核" content="审核后入库"></Step>
                </Steps>
		        </div>
            <div slot="extra">
                <ButtonGroup>
                    <Button type="primary" icon="ios-search" :loading="orderLoading" @click="refreshOrder">查询</Button>
                    <Button type="success" icon="checkmark" @click="checkOneOrderBtn">验收一单</Button>
                    <Button v-if="haveFNFlow" type="warning"  icon="close" @click="unCheckOneOrderBtn">取消验收一单</Button>
                    <Button type="info"  icon="images" @click="showCheckFile">检验档案</Button>
                </ButtonGroup>
            </div>
          
            <Form ref="searchForm" :model="query" :label-width="100">
                <Row type="flex" justify="start">
                    <i-col span="5">
                        <FormItem label="订单日期">
                            <DatePicker v-model="dateRange" type="daterange" placement="bottom-start" placeholder="收货日期" style="width:180px"></DatePicker>
                        </FormItem>
                    </i-col>
                    <i-col span="5">
                        <FormItem label="仓库">
                        <warehouse-select v-model="query.warehouseId"  @on-change="refreshOrder"></warehouse-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="供应商">
                            <supplier-select v-model="query.supplierId" @on-change="refreshOrder"></supplier-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="状态">
                            <Select v-model="query.status" placeholder="状态" @on-change="refreshOrder">
                                    <Option v-for="option in statusOptions" :value="option.key" :label="option.name" :key="option.key">{{option.name}}</Option>
                            </Select>
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
            <div>
                <Table ref="orderTable" border highlight-row disabled-hover height="250" style="width: 100%" 
                        :columns="orderListColumns" :data="orderList" size="small" 
                        :loading="orderLoading" 
                        @on-row-click="handleSelectOrder" 
                        no-data-text="输入查询条件, 点击上方查询按钮进行查询">
                    </Table>
            </div>

            <div class="detail-div">
                    <Row type="flex" justify="end">
                        <ButtonGroup>
                            <Button type="info" icon="android-bulb" @click="samplingSurveyBtnClick">抽样检查</Button>
                            <Button type="success" icon="checkmark" @click="checkOneDetailBtn">验收一条</Button>
                            <Button v-if="haveFNFlow" type="warning" icon="close" @click="unCheckOneDetailBtn">取消验收一条</Button>
                        </ButtonGroup>
                    </Row>

                <Table border highlight-row height="300" :loading="detailLoading" 
                    :columns="detailColumns" :data="detailList" size="small" 
                    ref="detailTable" class="margin-top-10"
                    @on-row-click="handleSelectDetail" 
                    no-data-text="点击上方订单后查看明细">
                    <div slot="footer">
                        <h3 class="detail-count-content" >
                            <b>总金额:</b> {{ totalAmount }} 
                            <b class="detail-count-content-b">收货数量:</b> {{ totalReceiveCount }}
                            <b class="detail-count-content-b">入库数量:</b> {{ totalInCount }}
                            <b class="detail-count-content-b">合格数量:</b> {{ totalRightCount }}
                            <b class="detail-count-content-b">不合格数量:</b> {{ totalErrorCount }}
                            <b class="detail-count-content-b">采集数量:</b> {{ totalSurveyQuality }}
                        </h3>
                    </div>
                </Table>
            </div>
      </Card>
      
      <Modal v-model="surveyModal" title="商品抽样检查" :footerHide="true" :mask-closable="false" width="50">
          <in-survey-check :order="currentChooseOrder" :detail="currChooseDetail" 
                @close="surveyClose" 
                @survey-success="surveySuccess">
            </in-survey-check>
          <div slot="footer"></div>
      </Modal>

      <Modal v-model="checkModal" title="验收结果" :mask-closable="false" width="50" >
          <Form ref="checkForm" :model="checkFormItem" :label-width="100">
              <Row v-if="checkDetail">
                  <i-col span="12">
                    <FormItem label="采购数量">
                        <Input :number="true" v-model="checkFormItem.buyOrderQuality"  disabled/>
                    </FormItem>
                  </i-col>
                  <i-col span="12">
                    <FormItem label="到货数量">
                        <Input :number="true" v-model="checkFormItem.receiveQuality" disabled/>
                    </FormItem>
                  </i-col>
              </Row>
              <Row v-if="checkDetail">
                  <i-col span="12">
                    <FormItem label="入库数量">
                        <Input :number="true" v-model="checkFormItem.inCount" disabled/>
                    </FormItem>
                  </i-col>
                  <i-col span="12">
                    <FormItem label="抽样数量">
                        <Input :number="true" v-model="checkFormItem.surveyQuality" disabled/>
                    </FormItem>
                  </i-col>
              </Row>

              <Row v-if="checkDetail">
                  <i-col span="12">
                    <FormItem label="合格数量">
                        <Input :number="true" v-model="checkFormItem.rightCount" @on-change="checkRightCountChange" />
                    </FormItem>
                  </i-col>
                  <i-col span="12">
                    <FormItem label="不合格数量">
                        <Input :number="true" v-model="checkFormItem.errorCount" disabled/>
                    </FormItem>
                  </i-col>
              </Row>
              <Row v-if="checkDetail">
                  <i-col span="12">
                    <FormItem label="不合格品处置方案">
                        <Input v-model="checkFormItem.errorPlan" />
                    </FormItem>
                  </i-col>
                  <i-col span="12">
                    <FormItem label="营销人员证书">
                        <Input v-model="checkFormItem.saleCert" />
                    </FormItem>
                  </i-col>
              </Row>
              <Row v-if="checkDetail">
                  <i-col span="24">
                    <FormItem label="不合格品原因">
                        <Input v-model="checkFormItem.errorReason" />
                    </FormItem>
                  </i-col>
              </Row>
              <Row>
                  <i-col span="8">
                    <FormItem label="温控方式验收">
                        <option-select optionType="TEMPER_CONTROL" v-model="checkFormItem.checkTempMethod"></option-select>
                    </FormItem>
                  </i-col>
                  <i-col span="8">
                    <FormItem label="验收员" :error="checkUserError">
                        <Row >
                          <Input v-model="checkFormItem.checkUser" style="width: 90%;" placeholder="张三;李四"  />
                          <Tooltip transfer >
                                <Icon type="ios-information-outline" size="20"></Icon>
                                <div slot="content" >
                                    <strong>用于登记验收人员姓名</strong><br/>
                                    <strong>提示:如果是多人验收,请使用“;”号分割多个人的姓名, 例如：张三;李四</strong><br/>
                                    <strong style="color: red;">注意：如果商品列表中存在有“特殊管理药品”标识的商品，至少需要双人验收</strong>
                                </div>
                            </Tooltip>
                        </Row>
                    </FormItem>
                  </i-col>
                  <i-col span="8">
                    <FormItem label="验收时间">
                        <DatePicker format="yyyy-MM-dd HH:mm" type="datetime" v-model="checkFormItem.checkTime" />
                    </FormItem>
                  </i-col>
              </Row>
              <Row>
                  <i-col span="24">
                    <FormItem label="验收结果">
                        <Input v-model="checkFormItem.checkResult" />
                    </FormItem>
                  </i-col>
              </Row>
          </Form>

          <ButtonGroup slot="footer">
            <Button type="success" icon="checkmark" @click="checkSuccess">提交</Button>
            <Button type="ghost" icon="reply" @click="checkCancel">取消</Button>
          </ButtonGroup>
      </Modal>

      <Modal v-model="checkFileModal" title="检验报告档案" :footerHide="true" :mask-closable="false" width="50">
          <file-detail :fileNo="checkFileNo" @add-file-success="addFileSuccess" ></file-detail>
      </Modal>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment, { isMoment } from "moment";
import supplierSelect from "@/views/selector/supplier-select.vue";
import warehouseSelect from "@/views/selector/warehouse-select.vue";
import inSurveyCheck from "./in-survey-check.vue";
import optionSelect from "@/views/selector/option-select.vue";
import goodSelect from "@/views/selector/good-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";
import goodsSpecTags from "../goods/goods-spec-tabs.vue";

export default {
  name: "in-quality-check",
  components: {
    supplierSelect,
    warehouseSelect,
    inSurveyCheck,
    optionSelect,
    goodSelect,
    goodsSpecTags,
    fileDetail
  },
  data() {
    return {
      statusOptions: [
        { key: "ALL", name: "所有" },
        { key: "CHECKING", name: "未验收" },
        { key: "CHECKED", name: "已验收" }
      ],
      query: {
        warehouseId: "",
        supplierId: "",
        status: "CHECKING"
      },
      dateRange: [
        moment()
          .add(-1, "w")
          .format("YYYY-MM-DD"),
        moment().format("YYYY-MM-DD")
      ],
      orderLoading: false,
      orderList: [],
      orderListColumns: [
        {
          type: "index",
          width: 60
        },
        {
          title: "系统单号",
          key: "orderNumber",
          width: 180
        },
        {
          title: "收货时间",
          key: "receiveDate",
          width: 150,
          render: (h, params) => {
            let receiveDate = params.row.receiveDate;
            return h(
              "span",
              receiveDate ? moment(receiveDate).format("YYYY-MM-DD") : ""
            );
          }
        },
        {
          title: "状态",
          key: "status",
          width: 130,
          render: (h, params) => {
            let color = params.row.status === "INIT" ? "#ff9900" : "#19be6b";
            let label = params.row.status === "INIT" ? "未验收" : "已验收";
            return h(
              "Tag",
              {
                props: {
                  type: "dot",
                  color: color
                }
              },
              label
            );
          }
        },
        {
          title: "验收温度",
          key: "checkTemp",
          width: 120,
          render: (h, params) => {
            var self = this;
            let oldCheckTemp = params.row.checkTemp;
            return h("Input", {
              props: {
                number: true,
                value: self.orderList[params.index][params.column.key]
              },
              on: {
                "on-blur"(event) {
                  let row = self.orderList[params.index];
                  row[params.column.key] = event.target.value;
                  if (
                    event.target.value != oldCheckTemp &&
                    !isNaN(event.target.value)
                  ) {
                    self.chanageOrderCheckTemp(
                      params.row.id,
                      event.target.value
                    );
                  }
                }
              }
            });
          }
        },
        {
          title: "到货温度",
          key: "receiveTemp",
          width: 100
        },
        {
          title: "入库方式",
          key: "refTypeName",
          width: 150
        },
        {
          title: "入库仓库",
          key: "warehouseName",
          width: 150
        },
        {
          title: "供应商",
          key: "supplierName",
          width: 200
        },
        {
          title: "供应商代表",
          key: "supplierContactName",
          width: 140
        },
        {
          title: "采购员",
          key: "saleNickName",
          width: 150,
          render: (h, params) => {
            let saleNickName = params.row.saleNickName;
            let saleRealName = params.row.saleRealName;
            if (saleNickName && saleRealName) {
              return h("span", saleNickName + " - [" + saleRealName + "]");
            } else {
              return h("span", saleNickName);
            }
          }
        },
        {
          title: "总计入库数量",
          key: "totalQuantity",
          width: 150
        },
        {
          title: "总计金额",
          key: "totalAmount",
          width: 150
        },
        {
          title: "收货员",
          key: "receiveUser",
          width: 150,
          render: (h, params) => {
            let receiveUser = params.row.receiveUser;
            if (!receiveUser) {
              return h("span", params.row.createBy);
            } else {
              return h("span", receiveUser);
            }
          }
        },
        {
          title: "采购属性",
          key: "buyTypeName",
          width: 130
        },
        {
          title: "到货时间",
          key: "shipEndDate",
          width: 150,
          render: (h, params) => {
            let shipEndDate = params.row.shipEndDate;
            return h(
              "span",
              shipEndDate ? moment(shipEndDate).format("YYYY-MM-DD") : ""
            );
          }
        }
      ],
      currentChooseOrder: {},
      detailLoading: false,
      detailList: [],
      detailColumns: [
        {
          type: "index",
          width: 60
        },
        {
          title: "验收状态",
          key: "checkStatus",
          width: 140,
          render(h, params) {
            let checkStatus = params.row.checkStatus;
            if (checkStatus) {
              return h(
                "Tag",
                { props: { type: "dot", color: "green" } },
                "已验收"
              );
            } else {
              return h(
                "Tag",
                { props: { type: "dot", color: "red" } },
                "未验收"
              );
            }
          }
        },
        {
          title: "商品名称",
          key: "goodsName",
          width: 160,
          render: (h, params) => {
            return h("span", params.row.goods.name);
          }
        },
        {
          title: "产地",
          key: "origin",
          width: 140,
          render: (h, params) => {
            return h("span", params.row.goods.origin);
          }
        },
        {
          title: "规格",
          key: "goodsSpecs",
          width: 150,
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
          width: 140,
          render: (h, params) => {
            return h("span", params.row.goods.factoryName);
          }
        },
        {
          title: "单位",
          key: "unitName",
          width: 120,
          render: (h, params) => {
            return h("span", params.row.goods.unitName);
          }
        },
        {
          title: "批号",
          key: "batchCode",
          width: 140
        },
        {
          title: "生产日期",
          key: "productDate",
          width: 140,
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
          title: "库区",
          key: "warehouseLocation",
          width: 150
        },
        {
          title: "采购数量",
          key: "buyOrderQuality",
          width: 130
        },
        {
          title: "拒收",
          width: 150,
          key: "rejectQuality",
          render: (h, params) => {
            let rejectQuality = params.row.rejectQuality
              ? params.row.rejectQuality
              : "0";
            let rejectComment = params.row.rejectComment
              ? params.row.rejectComment
              : "";
            return h("div", [
              h("h4", "数量:" + rejectQuality),
              h("span", "原因:" + rejectComment)
            ]);
          }
        },
        {
          title: "收货数量",
          width: 130,
          key: "receiveQuality"
        },
        {
          title: "赠送数量",
          width: 120,
          key: "free"
        },
        {
          title: "单价",
          width: 120,
          key: "price"
        },
        {
          title: "金额",
          width: 140,
          key: "amount"
        },
        {
          title: "入库数量",
          key: "inCount",
          width: 140
        },
        {
          title: "合格数量",
          key: "rightCount",
          width: 140
        },
        {
          title: "不合格数量",
          key: "errorCount",
          width: 120
        },
        {
          title: "抽样数量",
          key: "surveyQuality",
          width: 140,
          render: (h, params) => {
            let surveyQuality = params.row.surveyQuality
              ? params.row.surveyQuality
              : 0;
            let surveyDate = params.row.surveyDate
              ? moment(params.row.surveyDate).format("YYYY-MM-DD HH:mm")
              : "";
            let surveyUser = params.row.surveyUser ? params.row.surveyUser : "";
            return h("div", [
              h("h4", "数量:" + surveyQuality),
              h("span", surveyUser + "  " + surveyDate)
            ]);
          }
        },
        {
          title: "验收温控方式",
          key: "checkTempMethodName",
          width: 140
        },
        {
          title: "验收意见",
          key: "checkResult",
          width: 150
        },
        {
          title: "验收员",
          key: "checkUser",
          width: 140
        },
        {
          title: "验收时间",
          key: "checkTime",
          width: 140,
          render: (h, params) => {
            let checkTime = params.row.checkTime;
            return h(
              "span",
              checkTime ? moment(checkTime).format("YYYY-MM-DD HH:mm") : ""
            );
          }
        }
      ],
      checkUserError: "",
      currChooseDetail: {},
      totalAmount: 0,
      totalReceiveCount: 0,
      totalInCount: 0,
      totalRightCount: 0,
      totalErrorCount: 0,
      totalSurveyQuality: 0,
      surveyModal: false,
      checkDetail: false,
      checkFormItem: {},
      checkModal: false,
      checkFileNo: "",
      checkFileModal: false,
      haveFNFlow: false,
      currFlowContent: "抽样检查商品质量"
    };
  },
  watch: {
    detailList(data) {
      if (!data || data.length <= 0) {
        this.totalAmount = 0;
        this.totalReceiveCount = 0;
        this.totalInCount = 0;
        this.totalRightCount = 0;
        this.totalErrorCount = 0;
        this.totalSurveyQuality = 0;
      } else {
        this.totalAmount = data.reduce((total, item) => {
          let itemAmount = item.amount ? parseFloat(item.amount) : 0;
          return parseFloat((total + itemAmount).toFixed(2));
        }, 0);
        this.totalReceiveCount = data.reduce((total, item) => {
          let itemReceiveQuality = item.receiveQuality
            ? parseFloat(item.receiveQuality)
            : 0;
          return parseFloat((total + itemReceiveQuality).toFixed(3));
        }, 0);
        this.totalInCount = data.reduce((total, item) => {
          let itemInCount = item.inCount ? parseFloat(item.inCount) : 0;
          return parseFloat((total + itemInCount).toFixed(3));
        }, 0);
        this.totalRightCount = data.reduce((total, item) => {
          let itemRightCount = item.rightCount
            ? parseFloat(item.rightCount)
            : 0;
          return parseFloat((total + itemRightCount).toFixed(3));
        }, 0);
        this.totalErrorCount = data.reduce((total, item) => {
          let itemErrorCount = item.errorCount
            ? parseFloat(item.errorCount)
            : 0;
          return parseFloat((total + itemErrorCount).toFixed(3));
        }, 0);
        this.totalSurveyQuality = data.reduce((total, item) => {
          let itemSurveyQuality = item.surveyQuality
            ? parseFloat(item.surveyQuality)
            : 0;
          return parseFloat((total + itemSurveyQuality).toFixed(3));
        }, 0);
      }
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      //获取系统流程配置
      this.loadSystemConfig();
      //获取页面数据
      this.refreshOrder();
    },

    loadSystemConfig() {
      //获取一些系统配置中的信息
      util.ajax
        .get("/config/list")
        .then(response => {
          let data = response.data;
          //终审流程是否需要
          let flowFN = data["BUY_FINAL_CHECK"];
          if ("open" === flowFN.keyValue) {
            this.haveFNFlow = true;
          } else {
            this.haveFNFlow = false;
          }
          if (!this.haveFNFlow) {
            this.currFlowContent = "抽样检查商品质量, 保存后直接入库且修改库存";
          }
        })
        .catch(error => {
          console.log(error);
          util.errorProcessor(this, error);
        });
    },

    refreshOrder() {
      let statusList = [];
      if (this.query.status === "CHECKING") {
        statusList = ["INIT"];
      } else if (this.query.status == "CHECKED") {
        statusList = ["CHECKED"];
      } else {
        statusList = ["INIT", "CHECKED"];
      }
      let reqData = {
        statusList: statusList,
        warehouseId: this.query.warehouseId,
        supplierId: this.query.supplierId,
        startReceiveDate: this.dateRange[0],
        endReceiveDate: this.dateRange[1]
      };
      this.orderLoading = true;
      util.ajax
        .post("/repertory/in/list", reqData)
        .then(response => {
          this.orderLoading = false;
          this.orderList = response.data;
        })
        .catch(error => {
          this.orderLoading = false;
          util.errorProcessor(this, error);
        });
      this.currentChooseOrder = {};
      this.currChooseDetail = {};
      this.detailList = [];
    },
    chanageOrderCheckTemp(orderId, newCheckTemp) {
      if (!orderId || !newCheckTemp) {
        return;
      }
      let reqData = {
        orderId: orderId,
        checkTemp: newCheckTemp
      };
      util.ajax
        .put("/repertory/in/set/checkTemp", reqData)
        .then(response => {
          this.$Message.success("验收温度设定成功");
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    handleSelectOrder(rowData) {
      if (!rowData || !rowData.id) {
        this.currentChooseOrder = {};
        this.detailList = [];
        return;
      }
      this.currentChooseOrder = rowData;
      this.reloadOrderDetail();
      this.checkFileNo = "";
    },

    handleSelectDetail(rowData) {
      if (!rowData || !rowData.id) {
        this.currChooseDetail = {};
      } else {
        this.currChooseDetail = rowData;
      }
    },

    samplingSurveyBtnClick() {
      if (!this.currChooseDetail || !this.currChooseDetail.id) {
        this.$Message.warning("请先选择需要抽样验收的商品");
        return;
      }
      this.surveyModal = true;
    },
    surveyClose() {
      this.surveyModal = false;
    },
    surveySuccess() {
      this.surveyModal = false;
      this.reloadOrderDetail();
    },

    reloadOrderDetail() {
      this.detailLoading = true;
      util.ajax
        .get("/repertory/in/detail/" + this.currentChooseOrder.id)
        .then(response => {
          this.detailLoading = false;
          let data = response.data;
          if (data) {
            this.detailList = data;
            this.currChooseDetail = {};
          }
        })
        .catch(error => {
          this.detailLoading = false;
          util.errorProcessor(this, error);
        });
    },

    checkOneOrderBtn() {
      if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
        this.$Message.warning("请先选择一条需要验收的订单信息");
        return;
      }
      this.checkFormItem = {
        orderId: this.currentChooseOrder.id,
        checkTime: moment().format("YYYY-MM-DD HH:mm")
      };
      this.checkDetail = false;
      this.checkModal = true;
    },
    checkOneDetailBtn() {
      if (!this.currChooseDetail || !this.currChooseDetail.id) {
        this.$Message.warning("请先选择需要验收的商品");
        return;
      }
      let receiveQuality = this.currChooseDetail.receiveQuality
        ? this.currChooseDetail.receiveQuality
        : 0;
      let rightCount = this.currChooseDetail.rightCount
        ? this.currChooseDetail.rightCount
        : receiveQuality;
      if (rightCount > receiveQuality) {
        this.$Message.error("合格数量不能大于收货数量");
        return;
      }
      let inCount = rightCount;
      let errorCount = receiveQuality - rightCount;
      this.checkFormItem = {
        detailId: this.currChooseDetail.id,
        buyOrderQuality: this.currChooseDetail.buyOrderQuality,
        receiveQuality: receiveQuality,
        surveyQuality: this.currChooseDetail.surveyQuality,
        inCount: rightCount,
        rightCount: rightCount,
        errorCount: errorCount,
        checkTime: moment().format("YYYY-MM-DD HH:mm")
      };
      this.checkDetail = true;
      this.checkModal = true;
    },
    checkRightCountChange() {
      let receiveQuality = this.checkFormItem.receiveQuality;
      let rightCount = this.checkFormItem.rightCount
        ? this.checkFormItem.rightCount
        : receiveQuality;
      let inCount = rightCount;
      let errorCount = receiveQuality - rightCount;
      this.checkFormItem.inCount = inCount;
      this.checkFormItem.errorCount = errorCount;
    },
    checkCancel() {
      this.checkFormItem = {
        orderId: "",
        detailId: ""
      };
      this.checkModal = false;
    },
    checkSuccess() {
      this.checkUserError = "";
      //需要验证商品是否有特殊经营管理的标识，如果有，需要验证是否为双人验收
      let checkUser = this.checkFormItem.checkUser;
      let haveSpecialGoods = false;
      //看看商品是否为特殊管理药品
      if (this.checkFormItem.detailId && this.currChooseDetail) {
        //单笔详情验收
        let goods = this.currChooseDetail.goods;
        if (goods.specialManage) {
          haveSpecialGoods = true;
        }
      } else {
        //整单验收时，循环整个明细列表
        for (let i = 0; i < this.detailList.length; i++) {
          let goods = this.detailList[i].goods;
          if (goods && goods.specialManage) {
            haveSpecialGoods = true;
            break;
          }
        }
      }
      if (haveSpecialGoods) {
        if (
          !checkUser ||
          (checkUser.indexOf(";") < 0 && checkUser.indexOf("；") < 0)
        ) {
          this.checkUserError = "商品存在“药品特殊经营”标识，需要双人验收.";
          return;
        }
      }
      let reqData = JSON.parse(JSON.stringify(this.checkFormItem));
      if (reqData.checkTime) {
        reqData.checkTime = moment(reqData.checkTime, "YYYY-MM-DD HH:mm:ss");
      }
      this.detailLoading = true;
      let self = this;
      util.ajax
        .put("/repertory/in/set/check", reqData)
        .then(response => {
          self.detailLoading = false;
          self.$Message.success("验收成功");
          let refresh = response.data.refresh;
          console.log(refresh);
          if (refresh > 0) {
            self.refreshOrder();
          } else {
            self.reloadOrderDetail();
          }
          self.checkCancel();
        })
        .catch(error => {
          self.detailLoading = false;
          util.errorProcessor(self, error);
        });
    },
    unCheckOneOrderBtn() {
      if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
        this.$Message.warning("请先选择需要取消验证的订单");
        return;
      }
      util.ajax
        .put("/repertory/in/set/check/order/" + this.currentChooseOrder.id)
        .then(response => {
          this.$Message.success("取消成功");
          this.refreshOrder();
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    unCheckOneDetailBtn() {
      if (!this.currChooseDetail || !this.currChooseDetail.id) {
        this.$Message.warning("请先选择需要取消验证的订单");
        return;
      }
      util.ajax
        .put("/repertory/in/set/check/detail/" + this.currChooseDetail.id)
        .then(response => {
          this.$Message.success("取消成功");
          this.reloadOrderDetail();
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    showCheckFile() {
      if (!this.currentChooseOrder || !this.currentChooseOrder.id) {
        this.$Message.warning("请先选择对应订单信息");
        return;
      }
      let fileNo = this.currentChooseOrder.fileNo;
      this.checkFileNo = fileNo;
      this.checkFileModal = true;
    },
    addFileSuccess(data) {
      if (!data || !data.fileNo) {
        this.$Notice.error({
          title: "系统错误",
          desc: "添加档案成功后获取档案信息失败, 请联系技术人员查询原因."
        });
        return;
      }
      let reqData = {
        orderId: this.currentChooseOrder.id,
        fileNo: data.fileNo
      };
      util.ajax
        .put("/repertory/in/order/fileNo", reqData)
        .then(response => {
          this.currentChooseOrder.fileNo = data.fileNo;
          for (let i = 0; i < this.orderList.length; i++) {
            let row = this.orderList[i];
            if (this.currentChooseOrder.id === row.id) {
              row.fileNo = data.fileNo;
              break;
            }
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
          this.$Notice.error({
            title: "系统错误",
            desc: "添加档案成功后绑定档案信息失败, 请联系技术人员查询原因."
          });
        });
    }
  }
};
</script>

<style >
.ivu-form-item {
  margin-bottom: 5px;
}
.detail-div {
  margin-top: 10px;
}
.detail-count-content {
  margin-left: 10px;
}
.detail-count-content-b {
  margin-left: 40px;
}
.add-goods-class {
  margin-left: 10px;
  width: 300px;
}
</style>

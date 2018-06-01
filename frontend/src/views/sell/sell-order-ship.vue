<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        
    <Form ref="form" :model="formItem" :label-width="100" >
		<Row type="flex" justify="start">
			<i-col span="6">
			    <FormItem label="发货时间">
					<DatePicker v-model="formItem.issuanceDate" type="datetime" 
                        format="yyyy-MM-dd HH:mm" placeholder="请选择发货日期" ></DatePicker>
				</FormItem>
			</i-col>
			<i-col span="6">
				<FormItem label="货单号">
					<Input type="text" v-model="formItem.shipNumber" placeholder="请输入货单号"/>
				</FormItem>
			</i-col>
			<i-col span="12">
				<FormItem label="发货地址">
					<Input type="text" v-model="formItem.shipAddress" placeholder="请输入发货地址"/>
				</FormItem>
			</i-col>
		</Row>
        <Row type="flex" justify="start">
            <i-col span="6">
			    <FormItem label="收货人">
					<Input type="text" v-model="formItem.receiveUserName" placeholder="请输入货单号"/>
				</FormItem>
			</i-col>
			<i-col span="6">
				<FormItem label="收货人电话">
					<Input type="text" v-model="formItem.receiveUserPhone" placeholder="请输入货单号"/>
				</FormItem>
			</i-col>
			<i-col span="12">
				<FormItem label="收货地址">
					<Input type="text" v-model="formItem.receiveAddress" placeholder="请输入发货地址"/>
				</FormItem>
			</i-col>
        </Row>
		<Row type="flex" justify="start">
			<i-col span="6">
				<FormItem label="温控方式">
                    <option-select optionType="TEMPER_CONTROL" v-model="formItem.temperControlId" ></option-select>
				</FormItem>
			</i-col>
			<i-col span="6">
                <FormItem label="运输方式">
                    <option-select optionType="SHIP_METHOD" v-model="formItem.shipMethod" ></option-select>
				</FormItem>
			</i-col>
			<i-col span="6">
				<FormItem label="承运单位" prop="shipCompanyId">
                    <ship-company-select v-model="formItem.shipCompanyId"></ship-company-select>
				</FormItem>
			</i-col>
            <i-col span="6">
				<FormItem label="承运电话">
					<Input type="text" v-model="formItem.shipPhone" placeholder="请输入承运电话"/>
				</FormItem>
			</i-col>
		</Row>
		<Row type="flex" justify="start">
            <i-col span="6">
				<FormItem label="委托经办人">
					<Input type="text" v-model="formItem.operator" placeholder="请输入委托经办人"/>
				</FormItem>
			</i-col>
			<i-col span="6">
				<FormItem label="运输工具">
                    <option-select optionType="SHIP_TOOL" v-model="formItem.shipToolId" ></option-select>
				</FormItem>
			</i-col>
            <i-col span="6">
				<FormItem label="车牌号">
					<Input type="text" v-model="formItem.carNumber" placeholder="请输入车牌号"/>
				</FormItem>
			</i-col>
			<i-col span="6">
				<FormItem label="驾驶员">
					<Input type="text" v-model="formItem.driverName" placeholder="请输入驾驶员"/>
				</FormItem>
			</i-col>
		</Row>
		<Row type="flex" justify="start">
            <i-col span="6">
				<FormItem label="驾驶证档案">
					<i-input type="text" v-model="formItem.driverFileNo" readonly>
                        <Button slot="append" type="text" icon='upload' @click="uploadFileInfo(formItem.driverFileNo)"></Button>
                    </i-input>
				</FormItem>
			</i-col>
			<i-col span="6">
				<FormItem label="起运温度(℃)">
					<Input number v-model="formItem.shipTemper" placeholder="请输入车牌号"/>
				</FormItem>
			</i-col>
			<i-col span="6">
				<FormItem label="件数">
					<Input number v-model="formItem.shipQuantity" />
				</FormItem>
			</i-col>
			
            <i-col span="6">
				<FormItem label="运行里程(km)">
					<Input number v-model="formItem.mileage" />
				</FormItem>
			</i-col>
		</Row>
		<Row type="flex" justify="start">
			<i-col span="6">
				<FormItem label="计划启运时间">
					<DatePicker v-model="formItem.shipStartTime" 
						type="datetime" format="yyyy-MM-dd HH:mm" placeholder="请选择计划启运时间" >
					</DatePicker>
				</FormItem>
			</i-col>
			<i-col span="6">
				<FormItem label="计划到货时间">
			    	<DatePicker v-model="formItem.shipEndTime" 
						type="datetime" format="yyyy-MM-dd HH:mm" placeholder="请选择计划到货时间" >
					</DatePicker>
				</FormItem>
			</i-col>
			<i-col span="12">
				<FormItem label="备注">
					<Input type="text" v-model="formItem.comment" />
				</FormItem>
			</i-col>
		</Row>
        <Row type="flex" justify="center">
            <Button type="primary" icon="checked" :loading="tabLoading" @click="addShipRecord">添加</Button>
        </Row>
	</Form>

    <Row class="title-show">
        <hr/>
    </Row>
    <Row type="flex" justify="center">
        <Table ref="table" size="small" :data="tabData" :columns="tabColumns" 
            border highlight-row :loading="tabLoading" style="width: 100%;">
        </Table>
    </Row>

    <Modal v-model="fileUploadModal" title="客户档案上传" :mask-closable="false" width="50" class="file-upload-modal" >
        <file-detail :fileNo="fileNo" @add-file-success="addFileSuccess" ></file-detail>
        <div slot="footer"></div>
    </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import shipCompanySelect from "@/views/selector/ship-company-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";

export default {
  name: "sell-order-ship",
  props: {
    order: {
      type: Object,
      required: true
    }
  },
  components: {
    shipCompanySelect,
    optionSelect,
    fileDetail
  },
  data() {
    return {
      formItem: {
        sellOrderId: this.order.id,
        issuanceDate: moment().format("YYYY-MM-DD HH:mm")
      },
      tabLoading: false,
      tabData: [],
      tabColumns: [
        {
          title: "货单号",
          key: "shipNumber",
          width: 150,
          align: "center"
        },
        {
          title: "发货时间",
          key: "issuanceDate",
          width: 150,
          align: "center",
          render: (h, params) => {
            let issuanceDate = params.row.issuanceDate;
            let label = issuanceDate
              ? moment(issuanceDate).format("YYYY-MM-DD HH:mm")
              : "";
            return h("span", label);
          }
        },
        {
          title: "承运公司",
          key: "shipCompanyName",
          width: 200,
          align: "center"
        },
        {
          title: "发货地址",
          key: "shipAddress",
          width: 200,
          align: "center"
        },
        {
          title: "收货人",
          key: "receiveUserName",
          width: 200,
          align: "center"
        },
        {
          title: "收货人电话",
          key: "receiveUserPhone",
          width: 150,
          align: "center"
        },
        {
          title: "收货地址",
          key: "receiveAddress",
          width: 200,
          align: "center"
        },
        {
          title: "温控方式",
          key: "temperControlName",
          width: 150,
          align: "center"
        },
        {
          title: "运输工具",
          key: "shipToolName",
          width: 100,
          align: "center"
        },
        {
          title: "运输方式",
          key: "shipMethodName",
          width: 100,
          align: "center"
        },
        {
          title: "承运电话",
          key: "shipPhone",
          width: 100,
          align: "center"
        },
        {
          title: "经办人",
          key: "operator",
          width: 100,
          align: "center"
        },
        {
          title: "车牌号",
          key: "carNumber",
          width: 100,
          align: "center"
        },
        {
          title: "驾驶员",
          key: "driverName",
          width: 100,
          align: "center"
        },
        {
          title: "驾驶员档案",
          key: "driverFileNo",
          width: 180,
          align: "center",
          render: (h, params) => {
            let driverFileNo = params.row.driverFileNo;
            return h(
              "Button",
              {
                props: {
                  type: "text",
                  size: "small",
                  icon: "upload"
                },
                on: {
                  click: () => {
                    this.uploadFileInfo(driverFileNo);
                  }
                }
              },
              driverFileNo
            );
          }
        },
        {
          title: "起运温度",
          key: "shipTemper",
          width: 100,
          align: "center"
        },
        {
          title: "运输数量",
          key: "shipQuantity",
          width: 100,
          align: "center"
        },
        {
          title: "运行里程(km)",
          key: "mileage",
          width: 100,
          align: "center"
        },
        {
          title: "计划启运时间",
          key: "shipStartTime",
          width: 120,
          align: "center",
          render: (h, params) => {
            let shipStartTime = params.row.shipStartTime;
            let label = shipStartTime
              ? moment(shipStartTime).format("YYYY-MM-DD HH:mm")
              : "";
            return h("span", label);
          }
        },
        {
          title: "计划到货时间",
          key: "shipEndTime",
          width: 120,
          align: "center",
          render: (h, params) => {
            let shipEndTime = params.row.shipEndTime;
            let label = shipEndTime
              ? moment(shipEndTime).format("YYYY-MM-DD HH:mm")
              : "";
            return h("span", label);
          }
        },
        {
          title: "备注",
          key: "comment",
          width: 120,
          align: "center"
        },
        {
          title: "操作",
          width: 100,
          align: "center",
          fixed: "right",
          render: (h, params) => {
            return h(
              "Button",
              {
                props: {
                  type: "error",
                  size: "small"
                },
                on: {
                  click: () => {
                    this.removeShipRecord(params.row.id);
                  }
                }
              },
              "删除"
            );
          }
        }
      ],
      fileUploadModal: false,
      fileNo: ""
    };
  },
  watch: {
    order(data) {
      this.refreshTableData();
    }
  },
  methods: {
    addShipRecord() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          this.$Message.warning("请检查必输项信息");
          return;
        }
        this.tabLoading = true;
        this.formItem.sellOrderId = this.order.id;
        util.ajax
          .post("/sell/order/ship/save", this.formItem)
          .then(response => {
            this.tabLoading = false;
            this.$Message.success("新建成功");
            this.refreshTableData();
            this.initFormItem();
          })
          .catch(error => {
            this.tabLoading = false;
            util.errorProcessor(this, error);
          });
      });
    },

    initFormItem() {
      this.formItem = {
        sellOrderId: this.order.id,
        issuanceDate: moment().format("YYYY-MM-DD HH:mm"),
        temperControlId: this.order.temperControlId,
        shipMethod: this.order.shipMethod,
        shipToolId: this.order.shipTool,
        shipCompanyId: this.order.shipCompanyId,
        receiveUserName: this.order.customerRepName,
        receiveUserPhone: this.order.customerRepContactPhone,
        receiveAddress: this.order.customerRepRepertoryAddress
      };
    },

    refreshTableData() {
      if (!this.order.id || this.order.id < 0) {
        return;
      }
      this.initFormItem();
      this.tabLoading = true;
      util.ajax
        .get("/sell/order/ship/list", { params: { orderId: this.order.id } })
        .then(response => {
          this.tabLoading = false;
          this.tabData = response.data;
        })
        .catch(error => {
          this.tabLoading = false;
          util.errorProcessor(this, error);
        });
    },

    removeShipRecord(id) {
      if (!id) {
        return;
      }
      util.ajax
        .delete("/sell/order/ship/remove/" + id)
        .then(reponse => {
          this.$Message.success("删除成功");
          this.refreshTableData();
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    uploadFileInfo(data) {
      this.fileUploadModal = true;
      this.fileNo = data;
    },
    addFileSuccess(data) {
      this.formItem.driverFileNo = data.fileNo;
    }
  }
};
</script>

<style >
.title-show > hr {
  width: 100%;
  height: 2px;
  color: #5cadff;
  margin-top: 10px;
}

.file-upload-modal {
  position: fixed;
  z-index: 1000;
}
</style>


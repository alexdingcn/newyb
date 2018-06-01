
<template>
  <div>
      <Form ref="detailForm" :model="formItem" :label-width="90" :rules="formValidate">
          <Row type="flex" justify="center">
              <i-col span="12">
                <FormItem label="收货日期">
                    <DatePicker type="date" v-model="formItem.receiveDate" size="small" disabled/>
                </FormItem>
              </i-col>
              <i-col span="12" >
                <FormItem label="仓库名称">
                    <Input v-model="formItem.warehouseName" size="small" disabled/>
                </FormItem>
              </i-col>
          </Row>
          <Row type="flex" justify="center">
              <i-col span="12">
                <FormItem label="供应商">
                    <Input v-model="formItem.supplierName" size="small" disabled/>
                </FormItem>
              </i-col>
              <i-col span="12">
                <FormItem label="收货人">
                    <Input v-model="formItem.receiveUser" size="small" disabled/>
                </FormItem>
              </i-col>
          </Row>
          <Row type="flex" justify="center">
              <i-col span="12">
                <FormItem label="商品名称">
                    <Input v-model="formItem.goodsName" size="small" disabled/>
                </FormItem>
              </i-col>
              <i-col span="12">
                <FormItem label="生产企业">
                    <Input v-model="formItem.factory" size="small" disabled/>
                </FormItem>
              </i-col>
          </Row>
          <Row type="flex" justify="center">
              <i-col span="12">
                <FormItem label="品牌">
                    <Input v-model="formItem.brandName" size="small" disabled/>
                </FormItem>
              </i-col>
              <i-col span="12">
                <FormItem label="单位">
                    <Input v-model="formItem.unitName" size="small" disabled/>
                </FormItem>
              </i-col>
          </Row>
          <Row type="flex" justify="center">
              <i-col span="12">
                <FormItem label="生产日期">
                    <DatePicker type="date" v-model="formItem.productDate" size="small" disabled/>
                </FormItem>
              </i-col>
              <i-col span="12">
                <FormItem label="有效期至">
                    <DatePicker type="date" v-model="formItem.expDate" size="small" disabled/>
                </FormItem>
              </i-col>
          </Row>
          <Row type="flex" justify="center">
              <i-col span="12">
                <FormItem label="批次号">
                    <Input v-model="formItem.batchCode" size="small" disabled/>
                </FormItem>
              </i-col>
              <i-col span="12">
                <FormItem label="收货数量">
                    <Input v-model="formItem.receiveQuality" size="small" disabled/>
                </FormItem>
              </i-col>
          </Row>
          <h4 style="margin-top:20px;">抽样检查信息</h4>
          <hr size=1 style="margin-bottom: 10px; width: 80%"/>
          <Row type="flex" justify="center">
              <i-col span="12">
                <FormItem label="抽样日期" prop="surveyDate">
                    <DatePicker format="yyyy-MM-dd HH:mm" type="datetime" v-model="formItem.surveyDate" size="small" />
                </FormItem>
              </i-col>
              <i-col span="12">
                <FormItem label="抽样数量" prop="surveyQuality">
                    <Input v-model="formItem.surveyQuality" :number="true" size="small" />
                </FormItem>
              </i-col>
          </Row>
          <Row type="flex" justify="center">
              <i-col span="8">
                <FormItem label="抽样人" prop="surveyUser">
                    <Input v-model="formItem.surveyUser" size="small" />
                </FormItem>
              </i-col>
              <i-col span="16">
                <FormItem label="抽样地址">
                    <Input v-model="formItem.surveyAddress" size="small" />
                </FormItem>
              </i-col>
          </Row>
          <Row type="flex" justify="center">
              <i-col span="24">
                <FormItem label="抽样结论">
                    <Input v-model="formItem.surveyResult" size="small" />
                </FormItem>
              </i-col>
          </Row>
          <Row type="flex" justify="center">
              <i-col span="24">
                <FormItem label="抽样目的">
                    <Input v-model="formItem.surveyTarget" size="small" />
                </FormItem>
              </i-col>
          </Row>
      </Form>
      <Row type="flex" justify="center">
          <ButtonGroup size="small">
              <Button type="success" icon="checkmark" @click="submit">确定</Button>
              <Button type="ghost" icon="reply" @click="doback">取消</Button>
          </ButtonGroup>
      </Row>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";

export default {
  name: "in-survey-check",
  props: {
    order: {
      type: Object,
      require: true
    },
    detail: {
      type: Object,
      require: true
    }
  },
  data() {
    return {
      formItem: {
        receiveDate: "",
        warehouseName: "",
        supplierName: "",
        receiveUser: "",
        unitName: "",
        goodsName: "",
        factory: "",
        brandName: "",
        batchCode: "",
        productDate: "",
        expDate: "",
        surveyDate: moment().format("YYYY-MM-DD HH:mm"),
        receiveQuality: "",
        surveyQuality: "",
        surveyUser: "",
        surveyAddress: "",
        surveyResult: "",
        surveyTarget: ""
      },
      formValidate: {
        surveyQuality: [
          {
            required: true,
            type: "number",
            message: "抽样数量必输",
            trigger: "blur"
          }
        ],
        surveyUser: [
          {
            required: true,
            type: "string",
            message: "抽样人必输",
            trigger: "blur"
          }
        ]
      }
    };
  },
  watch: {
    order(data) {
      if (!data && !data.id) {
        return;
      }
      this.formItem.receiveDate = data.receiveDate
        ? moment(data.receiveDate).format("YYYY-MM-DD")
        : "";
      this.formItem.productDate = data.productDate
        ? moment(data.productDate).format("YYYY-MM-DD")
        : "";
      this.formItem.expDate = data.expDate
        ? moment(data.expDate).format("YYYY-MM-DD")
        : "";
      this.formItem.warehouseName = data.warehouseName;
      this.formItem.supplierName = data.supplierName;
      this.formItem.receiveUser = data.receiveUser
        ? data.receiveUser
        : data.createBy;
    },
    detail(data) {
      if (!data || !data.id) {
        return;
      }
      let goods = data.goods;
      this.formItem.detailId = data.id;
      this.formItem.unitName = goods.unitName;
      this.formItem.goodsName = goods.name;
      this.formItem.factory = goods.factoryName;
      this.formItem.brandName = goods.brandName;
      this.formItem.batchCode = data.batchCode;
      this.formItem.receiveQuality = data.receiveQuality;
      this.formItem.productDate = data.productDate
        ? moment(data.productDate).format("YYYY-MM-DD")
        : "";
      this.formItem.expDate = data.expDate
        ? moment(data.expDate).format("YYYY-MM-DD")
        : "";
      this.formItem.surveyDate = data.surveyDate
        ? moment(data.surveyDate).format("YYYY-MM-DD HH:mm")
        : moment().format("YYYY-MM-DD HH:mm");
      this.formItem.surveyQuality = data.surveyQuality
        ? data.surveyQuality
        : data.receiveQuality;
      this.formItem.surveyUser = data.surveyUser;
      this.formItem.surveyAddress = data.surveyAddress;
      this.formItem.surveyResult = data.surveyResult;
      this.formItem.surveyTarget = data.surveyTarget;
    }
  },
  methods: {
    doback() {
      this.$emit("close");
    },
    submit() {
      this.$refs.detailForm.validate(valid => {
        if (!valid) {
          this.$Message.warning("必输项未输入完整");
          return;
        } else {
          let reqData = {
            detailId: this.formItem.detailId,
            surveyDate: this.formItem.surveyDate
              ? moment(this.formItem.surveyDate)
              : moment(),
            surveyQuality: this.formItem.surveyQuality,
            surveyUser: this.formItem.surveyUser,
            surveyAddress: this.formItem.surveyAddress,
            surveyResult: this.formItem.surveyResult,
            surveyTarget: this.formItem.surveyTarget
          };
          console.log(reqData);

          util.ajax
            .post("/repertory/in/set/survey", reqData)
            .then(response => {
              this.$Message.success("添加抽样检查结论成功");
              this.$emit("survey-success");
            })
            .catch(error => {
              util.errorProcessor(this, error);
            });
        }
      });
    }
  }
};
</script>

<style >
.ivu-form-item {
  margin-bottom: 5px;
}
</style>

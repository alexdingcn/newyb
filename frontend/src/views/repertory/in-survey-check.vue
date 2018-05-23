
<template>
  <div>
      <Form ref="detailForm" :model="formItem" :label-width="90" :rules="formValidate">
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="收货日期">
                    <DatePicker type="date" v-model="formItem.receiveDate" size="small" :readonly="true"/>
                </FormItem>
              </Col>
              <Col span="12" >
                <FormItem label="仓库名称">
                    <Input v-model="formItem.warehouseName" size="small" :readonly="true"/>
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="供应商">
                    <Input v-model="formItem.supplierName" size="small" :readonly="true"/>
                </FormItem>
              </Col>
              <Col span="12">
                <FormItem label="收货人">
                    <Input v-model="formItem.receiveUser" size="small" :readonly="true"/>
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="单位">
                    <Input v-model="formItem.unitName" size="small" :readonly="true"/>
                </FormItem>
              </Col>
              <Col span="12">
                <FormItem label="件装量">
                    <Input v-model="formItem.bigPack" size="small" :readonly="true"/>
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="商品名称">
                    <Input v-model="formItem.goodsName" size="small" :readonly="true"/>
                </FormItem>
              </Col>
              <Col span="12">
                <FormItem label="规格">
                    <Input v-model="formItem.spec" size="small" :readonly="true"/>
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="生产企业">
                    <Input v-model="formItem.factory" size="small" :readonly="true"/>
                </FormItem>
              </Col>
              <Col span="12">
                <FormItem label="注册商标">
                    <Input v-model="formItem.brandNo" size="small" :readonly="true"/>
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="批注文号">
                    <Input v-model="formItem.permit" size="small" :readonly="true"/>
                </FormItem>
              </Col>
              <Col span="12">
                <FormItem label="存储条件">
                    <Input v-model="formItem.storageCondition" size="small" :readonly="true"/>
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="批次号">
                    <Input v-model="formItem.batchCode" size="small" :readonly="true"/>
                </FormItem>
              </Col>
              <Col span="12">
                <FormItem label="收货数量">
                    <Input v-model="formItem.receiveQuality" size="small" :readonly="true"/>
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="生产日期">
                    <DatePicker type="date" v-model="formItem.productDate" size="small" :readonly="true"/>
                </FormItem>
              </Col>
              <Col span="12">
                <FormItem label="有效期至">
                    <DatePicker type="date" v-model="formItem.expDate" size="small" :readonly="true"/>
                </FormItem>
              </Col>
          </Row>
          <h4 style="margin-top:20px;">抽样检查信息</h4>
          <hr size=1 style="margin-bottom: 10px; width: 80%"/>
          <Row type="flex" justify="center">
              <Col span="12">
                <FormItem label="抽样日期" prop="surveyDate">
                    <DatePicker format="yyyy-MM-dd HH:mm" type="datetime" v-model="formItem.surveyDate" size="small" />
                </FormItem>
              </Col>
              <Col span="12">
                <FormItem label="抽样数量" prop="surveyQuality">
                    <Input v-model="formItem.surveyQuality" :number="true" size="small" />
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="8">
                <FormItem label="抽样人" prop="surveyUser">
                    <Input v-model="formItem.surveyUser" size="small" />
                </FormItem>
              </Col>
              <Col span="16">
                <FormItem label="抽样地址">
                    <Input v-model="formItem.surveyAddress" size="small" />
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="24">
                <FormItem label="抽样结论">
                    <Input v-model="formItem.surveyResult" size="small" />
                </FormItem>
              </Col>
          </Row>
          <Row type="flex" justify="center">
              <Col span="24">
                <FormItem label="抽样目的">
                    <Input v-model="formItem.surveyTarget" size="small" />
                </FormItem>
              </Col>
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
import moment from 'moment';

export default {
    name: 'in-survey-check',
    props: {
        order: {
            type:Object,
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
                receiveDate: '',
                warehouseName: '',
                supplierName: '',
                receiveUser: '',
                unitName: '',
                bigPack: '',
                goodsName: '',
                spec: '',
                factory: '',
                certId: '',
                permit: '',
                storageCondition: '',
                batchCode: '',
                inCount: '',
                productDate: '',
                expDate: '',
                surveyDate: moment().format('YYYY-MM-DD HH:mm'),
                surveyQuality: '',
                surveyUser: '',
                surveyAddress: '',
                surveyResult: '',
                surveyTarget: '',
            },
            formValidate: {
                surveyQuality: [
                    { required: true, type: 'number', message: '抽样数量必输', trigger: 'blur' }
                ],
                surveyUser: [
                    { required: true, type: 'string', message: '抽样人必输', trigger: 'blur' }
                ]
            },
        }
    },
    watch: {
        order(data) {
            if(!data && !data.id) {
                return;
            }
            this.formItem.receiveDate = data.receiveDate ? moment(data.receiveDate).format('YYYY-MM-DD') : '';
            this.formItem.productDate = data.productDate ? moment(data.productDate).format('YYYY-MM-DD') : '';
            this.formItem.expDate = data.expDate ? moment(data.expDate).format('YYYY-MM-DD') : '';
            this.formItem.warehouseName = data.warehouseName;
            this.formItem.supplierName = data.supplierName;
            this.formItem.receiveUser = data.createBy;
        },
        detail(data) {
            if(!data || !data.id) {
                return;
            }
            this.formItem.detailId = data.id;
            this.formItem.unitName = data.unitName;
            this.formItem.bigPack = data.bigPack;
            this.formItem.goodsName = data.goodsName;
            this.formItem.spec = data.spec;
            this.formItem.factory = data.factory;
            this.formItem.brandNo = data.brandNo;
            this.formItem.permit = data.permit;
            this.formItem.storageCondition = data.storageCondition;
            this.formItem.batchCode = data.batchCode;
            this.formItem.receiveQuality = data.receiveQuality;
            this.formItem.productDate = data.productDate ? moment(data.productDate).format('YYYY-MM-DD') : '';
            this.formItem.expDate = data.expDate ? moment(data.expDate).format('YYYY-MM-DD') : '';
            this.formItem.surveyDate = data.surveyDate ? moment(data.surveyDate).format('YYYY-MM-DD HH:mm') : moment().format('YYYY-MM-DD HH:mm');
            this.formItem.surveyQuality = data.surveyQuality;
            this.formItem.surveyUser = data.surveyUser;
            this.formItem.surveyAddress = data.surveyAddress;
            this.formItem.surveyResult = data.surveyResult;
            this.formItem.surveyTarget = data.surveyTarget;
        }
    },
    methods: {
        doback() {
            this.$emit('close');
        },
        submit() {
            this.$refs.detailForm.validate((valid) => {
                if (!valid) {
                    this.$Message.warning('必输项未输入完整');
                    return;
                }else {
                    console.log(this.formItem.surveyDate);
                    console.log(moment());
                    let reqData = {
                        detailId: this.formItem.detailId,
                        surveyDate: this.formItem.surveyDate ? this.formItem.surveyDate : moment(),
                        surveyQuality: this.formItem.surveyQuality,
                        surveyUser: this.formItem.surveyUser,
                        surveyAddress: this.formItem.surveyAddress,
                        surveyResult: this.formItem.surveyResult,
                        surveyTarget: this.formItem.surveyTarget
                    };
                    
                    util.ajax.post("/repertory/in/set/survey", reqData)
                        .then((response) => {
                            this.$Message.success('添加抽样检查结论成功');
                            this.$emit('survey-success');
                        })
                        .catch((error) => {
                            util.errorProcessor(this, error);
                        });
                }
            });
        }
    }
  
}
</script>

<style>
.ivu-form-item {
    margin-bottom: 5px;
}

</style>

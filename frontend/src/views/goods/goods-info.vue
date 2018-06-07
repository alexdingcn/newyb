<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div>
        <Form ref="form" :model="formData" :rules="formRules" :label-width="90">
            <Tabs v-model="currTabs" :animated="false" @on-click="switchTabPane">
                <div slot="extra">
                    <ButtonGroup size="small">
                        <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveGoodsInfo" >确认保存</Button>
                    </ButtonGroup>
                </div>
                    <TabPane icon="cube" name="general" label="基础信息">
                        <h2 style="margin-bottom: 10px;">商品基础信息</h2>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="商品名称" prop="name">
                                    <Input type="text" v-model="formData.name" @on-blur="onChangeName" />
                                </FormItem>
                            </i-col>
                            <i-col span="10">
                                <FormItem label="商品编号" prop="goodsNo">
                                    <Input type="text" v-model="formData.goodsNo" placeholder="由系统自动生成" disabled/>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="商品类别" prop="categoryId">
                                    <goods-category-select v-model="formData.categoryId"></goods-category-select>
                                </FormItem>
                            </i-col>
                            <i-col span="10">
                                <FormItem label="商品品牌" prop="brandId">
                                    <goods-brand-select v-model="formData.brandId" ></goods-brand-select>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="拼音" prop="pinyin">
                                    <Input type="text" v-model="formData.pinyin" />
                                </FormItem>
                            </i-col>
                            <i-col span="10">
                                <FormItem label="生产企业" prop="factoryId">
                                    <factory-select v-model="formData.factoryId"></factory-select>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="产地" prop="origin">
                                    <Input type="text" v-model="formData.origin" />
                                </FormItem>
                            </i-col>
                            <i-col span="10">
                                <FormItem label="供应商" prop="supplierId">
                                    <supplier-select v-model="formData.supplierId"></supplier-select>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="是否启用" prop="enable">
                                    <RadioGroup v-model="formData.enable">
                                         <Radio :label="1">
                                            <span>启用</span>
                                        </Radio>
                                        <Radio :label="0">
                                            <span>禁用</span>
                                        </Radio>
                                    </RadioGroup>
                                </FormItem>
                            </i-col>
                            <i-col span="10">
                                <FormItem label="备注" prop="comment">
                                    <Input type="text" v-model="formData.comment" placeholder="备注信息"/>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="16">
                                <FormItem label="商品标签" prop="tagList">
                                    <CheckboxGroup v-model="formData.tagList">
                                        <Checkbox label="NEW_GOODS">
                                            <span>新品</span>
                                        </Checkbox>
                                        <Checkbox label="RECOMMEND">
                                            <span>推荐</span>
                                        </Checkbox>
                                        <Checkbox label="HOT_SALE">
                                            <span>热销</span>
                                        </Checkbox>
                                        <Checkbox label="PREZZIE">
                                            <span>赠品</span>
                                        </Checkbox>
                                    </CheckboxGroup>
                                </FormItem>
                            </i-col>
                            
                        </Row>

                        <h2 style="margin-bottom: 10px;">商品单位</h2>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="小单位" prop="unit">
                                    <option-select placement="top" v-model="formData.unit" optionType='GOODS_UNIT' ></option-select>
                                </FormItem>
                            </i-col>
                            <i-col span="10">
                                <FormItem label="条形码" prop="barCode">
                                    <Input type="text" v-model="formData.barCode" placeholder="最小单位的条形码"/>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="中件单位" prop="mediumUnit">
                                    <option-select placement="top" v-model="formData.mediumUnit" optionType='GOODS_UNIT' ></option-select>
                                </FormItem>
                            </i-col>
                            <i-col span="10">
                                <FormItem label="中件单位换算" prop="mediumPack">
                                    <InputNumber :min="0" v-model="formData.mediumPack"  style="width: 100%;" />
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="大件单位" prop="packUnit">
                                    <option-select placement="top" v-model="formData.packUnit" optionType='GOODS_UNIT' ></option-select>
                                </FormItem>
                            </i-col>
                            <i-col span="10">
                                <FormItem label="大件单位换算" prop="bigPack">
                                    <InputNumber :min="0" v-model="formData.bigPack" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                        </Row>
                        <Alert type="success">
                            <p><strong>单位换算提示: </strong></p>
                            <ul style="padding-left:30px;">
                                <li>小单位是与价格有关的基础单位</li>
                                <li>中单位和大单位就是小单位的一个倍数单位，也称为整箱单位</li>
                            </ul>
                        </Alert>

                    </TabPane>
                    <TabPane icon="social-yen" name="spec-price" label="多规格/基础价格">
                        <h2 style="margin-bottom: 10px;">多规格/基础价格设置</h2>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="是否使用多规格" :label-width="100" prop="useSpec">
                                     <i-switch v-model="formData.useSpec" ></i-switch>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="6">
                                <FormItem label="批发价" :label-width="100" prop="batchPrice">
                                     <Input v-model="formData.batchPrice" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="市场价" :label-width="100" prop="retailPrice">
                                     <Input v-model="formData.retailPrice" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="参考进货价" :label-width="100" prop="inPrice">
                                     <Input v-model="formData.inPrice" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                        </Row>

                        <div v-if="!formData.useSpec">
                            <Row class="row-margin-bottom">
                                <i-col span="6">
                                    <FormItem label="单规格描述" :label-width="100" prop="useSpec">
                                            <Input v-model="formData.specDesc" style="width: 100%;" />
                                    </FormItem>
                                </i-col>
                            </Row>
                        </div>

                        <div v-if="formData.useSpec" >
                            <Alert type="success" v-if="goodsSpesList.length<=0">
                                <strong>温馨提示: </strong>还没有维护商品多规格配置，请先维护商品多规格配置信息!
                            </Alert>
                            <Row class="row-margin-bottom" >
                                <FormItem label="添加商品规格">
                                    <Select transfer v-model="chooseParentValue" style="width: 250px" @on-change="chooseParentSpecChange">
                                        <Option size="" v-for="item in goodsSpesList" :value="item.parentId" :key="item.parentId">{{ item.parentName }}</Option>
                                    </Select>
                                </FormItem>
                            </Row>
                            <Row class="row-margin-bottom">
                                <Tag v-for="item in currParentSpecs" closable color="green" :key="item.parentId" :name="item.parentId" @on-close="specTagChose">{{item.parentName}}</Tag>
                            </Row>
                            <Row class="row-margin-bottom" v-for="(item, index) in currParentSpecs" :key="item.parentId"  >
                                <i-col span="24" >
                                    <FormItem :label="item.parentName" :label-width="100">
                                        <CheckboxGroup v-model="currSpecIds[index]" @on-change="chooseSpecChange" >
                                            <Checkbox v-for="subItem in item.subGoodsSpecs" :key="subItem.id" :label="subItem.id">
                                                <span>{{subItem.specName}}</span>
                                            </Checkbox>
                                        </CheckboxGroup>
                                    </FormItem>
                                </i-col>
                            </Row>
                            
                            <Table v-if="goodsDetails.length > 0" ref="goodsDetailTable" highlight-row 
                                :columns="goodsDetailColumn" :data="goodsDetails" 
                                size="small" height="350">
                            </Table>
                        </div>
                    </TabPane>
                    <TabPane icon="ios-pricetags" name="attribute" label="预警/扩展属性">
                        <h2 style="margin-bottom: 10px;">商品养护</h2>
                        <Row class="row-margin-bottom">
                            <i-col span="24" >
                                <FormItem label="是否需养护" :label-width="100">
                                    <Checkbox v-model="formData.needCare"></Checkbox>
                                </FormItem>
                            </i-col>
                        </Row>

                        <h2 v-if="isMedicine" style="margin-bottom: 10px;">药品特殊管理</h2>
                        <Row v-if="isMedicine" class="row-margin-bottom">
                            <i-col span="4" >
                                <FormItem label="是否特殊管理药品" :label-width="120">
                                    <Checkbox v-model="formData.specialManage"></Checkbox>
                                </FormItem>
                            </i-col>
                            <i-col span="4" >
                                <FormItem label="是否冷链经营药品" :label-width="120">
                                    <Checkbox v-model="formData.coldManage"></Checkbox>
                                </FormItem>
                            </i-col>
                        </Row>

                        <h2 style="margin-bottom: 10px;">库存预警信息</h2>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="到期预警天数" >
                                    <InputNumber :min="0" v-model="formData.warningDays"  />
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="最小起订量" >
                                    <InputNumber :min="0" v-model="formData.minOrderLimit" />
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="安全库存" >
                                    <InputNumber :min="0" v-model="formData.normalLimit" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="库存下限" >
                                    <InputNumber :min="0" v-model="formData.lowLimit" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="库存上限" >
                                    <InputNumber :min="0" v-model="formData.highLimit" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                        </Row>
                        <h2 style="margin-bottom: 10px;">
                            自定义属性
                            <Select ref="attSelect" clearable transfer v-model="chooseAttValue" placement="top" style="width: 250px" @on-change="chooseAttChange">
                                <Option size="" v-for="item in goodsAttributes" :value="item.id" :key="item.id">{{ item.attName }}</Option>
                            </Select>
                        </h2>
                        <Row class="row-margin-bottom" v-for="(item, index) in currAttributes" :key="item.attId" :gutter="20">
                            <i-col span="12" >
                                <FormItem :label="item.attName" :label-width="100" >
                                    <Input type="text" v-model="item.attValue" />
                                </FormItem>
                            </i-col>
                            <i-col span="1">
                                <Button type="text" icon="close" @click="removeOneAttribute(index)"></Button>
                            </i-col>
                        </Row>
                        
                    </TabPane>
                    <TabPane icon="document-text" name="file" label="档案管理">
                        <h2 style="margin-bottom: 10px;">档案信息管理</h2>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="证件1名称">
                                    <Input type="text" v-model="formData.cert1Name"/>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="证件1编号">
                                    <Input type="text" v-model="formData.cert1No"/>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="证件1有效期">
                                    <DatePicker type="date" transfer v-model="formData.cert1ExpDate" format="yyyy-MM-dd" placeholder="有效期至" style="width: 100%"></DatePicker>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="证件2名称">
                                    <Input type="text" v-model="formData.cert2Name"/>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="证件2编号">
                                    <Input type="text" v-model="formData.cert2No"/>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="证件2有效期">
                                    <DatePicker type="date" transfer v-model="formData.cert2ExpDate" format="yyyy-MM-dd" placeholder="有效期至" style="width: 100%"></DatePicker>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="证件3名称">
                                    <Input type="text" v-model="formData.cert3Name"/>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="证件3编号">
                                    <Input type="text" v-model="formData.cert3No"/>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="证件3有效期">
                                    <DatePicker type="date" transfer v-model="formData.cert3ExpDate" format="yyyy-MM-dd" placeholder="有效期至" style="width: 100%"></DatePicker>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="10">
                                <FormItem label="档案附件">
                                    <i-input v-model="formData.fileNo" readonly>
                                        <Button slot="append" type="text" icon="edit" @click="openFileUpload(formData.fileNo)"></Button>
                                    </i-input>
                                </FormItem>
                            </i-col>
                        </Row>
                    </TabPane>
                    <TabPane icon="eye-disabled" name="black-list" label="经营范围屏蔽" >
                        <goods-black-list v-model="goodsBlackListValue"></goods-black-list>
                    </TabPane>
            </Tabs>
        </Form>

        <Modal v-model="fileUploadModal" title="商品档案管理" :footerHide="true" :mask-closable="false" width="50" class="file-upload-low">
            <file-detail :fileNo="uploadFileNo" @add-file-success="addFileSuccess" ></file-detail>
        </Modal>
        <Spin fix size="large" v-if="loading">
            别着急，商品加载中...
        </Spin>
    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import goodsCategorySelect from "@/views/selector/goods-category-select.vue";
import goodsBrandSelect from "@/views/selector/goods-brand-select.vue";
import factorySelect from "@/views/selector/factory-select.vue";
import supplierSelect from "@/views/selector/supplier-select.vue";
import optionSelect from "@/views/selector/option-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";
import goodsBlackList from "./goods-black-list.vue";

export default {
  name: "goods-info",
  props: {
    goodsInfoId: {
      type: String | Number,
      default: ""
    }
  },
  components: {
    goodsBlackList,
    goodsCategorySelect,
    goodsBrandSelect,
    factorySelect,
    supplierSelect,
    optionSelect,
    fileDetail
  },
  data() {
    return {
      loading: false,
      currTabs: "general",
      saveLoading: false,
      formData: {
        name: "",
        goodsNo: "",
        pinyin: "",
        enable: 1,
        ifConser: 1,
        tagList: [],
        useSpec: false,
        batchPrice: 0,
        retailPrice: 0,
        inPrice: 0,
        needCare: false,
        goodsDetails: [],
        fileNo: "",
        attributeRefs: []
      },
      formRules: {
        name: [
          { required: true, message: "商品名称不能为空", trigger: "blur" }
        ],
        unit: [
          {
            required: true,
            type: "number",
            message: "商品最小单位不能为空",
            trigger: "blur"
          }
        ]
      },
      goodsSpesList: [],
      chooseParentValue: "",
      currParentSpecs: [],
      currSpecIds: [[], [], []],
      goodsDetails: [],
      goodsDetailColumn: [],
      fileUploadModal: false,
      uploadFileNo: "",
      goodsAttributes: [],
      chooseAttValue: "",
      currAttributes: [],
      customerCategoryList: [],
      goodsBlackListValue: {
        customerCategoryIds: [],
        regions: [],
        customerIds: []
      },
      isMedicine: false
    };
  },
  mounted() {
    this.loadSystemConfig();
    this.loadGoodsSpecs();
    this.loadGoodsAttribute();
  },
  watch: {
    goodsInfoId: function(id) {
      this.currTabs = "general";
      if (id && id > 0) {
        this.loadGoodsInfo(id);
      } else {
        this.formData = {};
      }
    }
  },
  methods: {
    loadSystemConfig() {
      //获取一些系统配置中的商品相关的配置信息
      util.ajax
        .get("/config/list")
        .then(response => {
          let data = response.data;
          let valueInfo = data["COMPANY_TYPE"];
          if (valueInfo.keyValue === "medicine") {
            this.isMedicine = true;
          } else {
            this.isMedicine = false;
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    addViewOpen() {
      //初始化一些新增产品的清空数据数据信息
      this.currTabs = "general";
      this.formData = {
        name: "",
        goodsNo: "",
        pinyin: "",
        enable: 1,
        ifConser: 1,
        tagList: [],
        useSpec: false,
        batchPrice: 0,
        retailPrice: 0,
        inPrice: 0,
        goodsDetails: [],
        fileNo: "",
        attributeRefs: []
      };
      this.currParentSpecs = [];
      (this.currSpecIds = [[], [], []]), (this.goodsDetails = []);
      this.goodsDetailColumn = [];
      //获取默认的自定义属性
      util.ajax
        .get("/goods/defaultAttr")
        .then(response => {
          this.currAttributes = response.data;
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    switchTabPane(tabName) {
      if (tabName === "black-list") {
        // TODO: load black list first
        this.loadGoodsBlackList();
      }
    },
    loadGoodsBlackList() {
      if (this.goodsInfoId) {
        util.ajax
          .get("/goods/blacklist/" + this.goodsInfoId)
          .then(response => {
            if (response.data) {
              this.goodsBlackListValue = response.data;
            }
          })
          .catch(error => {
            util.errorProcessor(this, error);
          });
      }
    },
    loadGoodsInfo(id) {
      var self = this;
      this.loading = true;
      util.ajax
        .get("/goods/" + id)
        .then(response => {
          self.loading = false;
          let data = response.data;
          data.enable = data.enable ? 1 : 0;
          data.ifConser = data.ifConser ? 0 : 1;
          data.cert1ExpDate = data.cert1ExpDate
            ? moment(data.cert1ExpDate).format("YYYY-MM-DD")
            : "";
          data.cert2ExpDate = data.cert2ExpDate
            ? moment(data.cert2ExpDate).format("YYYY-MM-DD")
            : "";
          data.cert3ExpDate = data.cert3ExpDate
            ? moment(data.cert3ExpDate).format("YYYY-MM-DD")
            : "";
          this.formData = data;
          //需要重新初始化多规格数据信息，如果使用了多规格的，再根据数据生成
          this.currParentSpecs = [];
          (this.currSpecIds = [[], [], []]), (this.goodsDetails = []);
          this.goodsDetailColumn = [];
          if (this.formData.useSpec) {
            this.editShowSpecForm(this.formData.goodsDetails);
          }
          //重新赋值自定义属性信息
          this.currAttributes = this.formData.attributeRefs;
        })
        .catch(error => {
          self.loading = false;
          util.errorProcessor(this, error);
        });
    },
    loadGoodsSpecs() {
      util.ajax
        .get("/goods/spec/list")
        .then(response => {
          this.goodsSpesList = response.data;
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    loadGoodsAttribute() {
      util.ajax
        .get("/goods/attribute/list")
        .then(response => {
          this.goodsAttributes = response.data;
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    onChangeName() {
      if (this.formData.name && this.formData.name.length > 0) {
        var self = this;
        util.ajax
          .post("/util/pinyinAbbr", { name: this.formData.name })
          .then(function(response) {
            self.formData.pinyin = response.data;
          })
          .catch(function(error) {
            util.errorProcessor(self, error);
          });
      }
    },

    editShowSpecForm(details) {
      //根据规格详情，制造出规格表和选择标签
      this.currParentSpecs = [];
      let haveParentIds = [];
      let specOneIds = [];
      let specTwoIds = [];
      let specThreeIds = [];
      for (let i = 0; i < details.length; i++) {
        let detailItem = details[i];
        let formItem1 = this.getSpecFormItemBySubId(detailItem.specOneId);
        let formItem2 = this.getSpecFormItemBySubId(detailItem.specTwoId);
        let formItem3 = this.getSpecFormItemBySubId(detailItem.specThreeId);
        if (
          formItem1.parentId &&
          haveParentIds.indexOf(formItem1.parentId) < 0
        ) {
          this.currParentSpecs.push(formItem1);
          haveParentIds.push(formItem1.parentId);
        }
        if (specOneIds.indexOf(detailItem.specOneId) < 0) {
          specOneIds.push(detailItem.specOneId);
        }

        if (
          formItem2.parentId &&
          haveParentIds.indexOf(formItem2.parentId) < 0
        ) {
          this.currParentSpecs.push(formItem2);
          haveParentIds.push(formItem2.parentId);
        }
        if (specTwoIds.indexOf(detailItem.specTwoId) < 0) {
          specTwoIds.push(detailItem.specTwoId);
        }

        if (
          formItem3.parentId &&
          haveParentIds.indexOf(formItem3.parentId) < 0
        ) {
          this.currParentSpecs.push(formItem3);
          haveParentIds.push(formItem3.parentId);
        }
        if (specThreeIds.indexOf(detailItem.specThreeId) < 0) {
          specThreeIds.push(detailItem.specThreeId);
        }
      }
      this.currSpecIds = [specOneIds, specTwoIds, specThreeIds];

      this.goodsDetailColumn = this.makeSpecTableColumns();
      this.goodsDetails = details;
    },
    getSpecFormItemBySubId(id) {
      if (!id || id <= 0) {
        return {};
      }
      for (let i = 0; i < this.goodsSpesList.length; i++) {
        let temp = this.goodsSpesList[i];
        if (temp.subGoodsSpecs.length > 0) {
          for (let j = 0; j < temp.subGoodsSpecs.length; j++) {
            if (id === temp.subGoodsSpecs[j].id) {
              return temp;
            }
          }
        }
      }
      return {};
    },
    getSpecFormItemByParentId(parentId) {
      if (!parentId) {
        return {};
      }
      for (let i = 0; i < this.goodsSpesList.length; i++) {
        let temp = this.goodsSpesList[i];
        if (parentId === temp.parentId) {
          return temp;
        }
      }
      return {};
    },
    chooseParentSpecChange(parentId) {
      let specForm = this.getSpecFormItemByParentId(parentId);
      if (!specForm || !specForm.parentId) {
        this.chooseParentValue = "";
        return;
      }
      //判断是否已经存在已选择的规格中
      for (let i = 0; i < this.currParentSpecs.length; i++) {
        let item = this.currParentSpecs[i];
        if (parentId === item.parentId) {
          this.chooseParentValue = "";
          return; //已经选择过了，不需要不能再次选择
        }
      }
      if (this.currParentSpecs.length >= 3) {
        this.chooseParentValue = "";
        this.$Message.info("规格配置最多能加三层");
        return;
      }
      this.currParentSpecs.push(specForm);
      this.goodsDetailColumn = this.makeSpecTableColumns();
    },
    specTagChose(event, parentId) {
      if (!parentId) {
        return;
      }
      for (let i = 0; i < this.currParentSpecs.length; i++) {
        let item = this.currParentSpecs[i];
        if (parentId === item.parentId) {
          //删除这个节点
          this.currParentSpecs.splice(i, 1);
          this.currSpecIds.splice(i, 1);
          this.goodsDetailColumn = this.makeSpecTableColumns();
          this.chooseSpecChange();
          break;
        }
      }
    },

    chooseSpecChange(data) {
      let details = this.makeSpecTableData();
      //如果当前的goodsInfoId存在，且大于0，证明是修改操作，如果是修改操作，把规格数据按后台返回的formData.details做数据赋值操作
      if (
        this.goodsInfoId &&
        this.formData.useSpec &&
        this.formData.goodsDetails.length > 0
      ) {
        //做数值赋值操作，做赋值操作之前，需要先把details的每一个skuKey的值按规则生成出来
        for (let i = 0; i < details.length; i++) {
          let detail = details[i];
          let skuKey = this.makeSkuKey(detail, this.goodsInfoId);
          detail.skuKey = skuKey;
          //根据skuKey 的值从formData.goodsDetails中查询相同值进行赋值
          let oldDetail = this.getDataFormGoodsDetailsBySkuKey(skuKey);
          if (oldDetail.id) {
            detail.id = oldDetail.id;
            detail.barCode = oldDetail.barCode;
            detail.batchPrice = oldDetail.batchPrice;
            detail.retailPrice = oldDetail.retailPrice;
            detail.inPrice = oldDetail.inPrice;
            detail.lastUsedTime = oldDetail.lastUsedTime;
            detail.usedCount = oldDetail.usedCount;
          }
        }
        this.goodsDetails = details; //赋值
      } else {
        this.goodsDetails = details; //如果没存在的，直接赋值，认为是新的数据
      }
    },

    getDataFormGoodsDetailsBySkuKey(skuKey) {
      for (let i = 0; i < this.formData.goodsDetails.length; i++) {
        let item = this.formData.goodsDetails[i];
        if (skuKey === item.skuKey) {
          return item;
        }
      }
      return {};
    },

    //根据详情和infoId生成skuKey
    makeSkuKey(detail, goodsInfoId) {
      let oneId = detail.specOneId ? detail.specOneId : -1;
      let twoId = detail.specTwoId ? detail.specTwoId : -1;
      let threeId = detail.specThreeId ? detail.specThreeId : -1;
      let result = goodsInfoId + "";

      let arr = [oneId, twoId, threeId]; //从小到大排序
      arr.sort((a, b) => {
        return a - b;
      });
      for (let i = 0; i < arr.length; i++) {
        if (arr[i] > 0) {
          result = result + "-" + arr[i];
        }
      }
      return result;
    },

    makeSpecTableColumns() {
      let culumns = [];
      let self = this;
      if (this.currParentSpecs.length <= 0) {
        culumns = [];
        return;
      }
      let indexItem = {
        title: "序号",
        type: "index",
        widht: 60
      };
      culumns.push(indexItem);
      for (let i = 0; i < this.currParentSpecs.length; i++) {
        let item = this.currParentSpecs[i];
        if (i === 0) {
          let temp = {
            title: item.parentName,
            key: "specOneId",
            width: 100,
            render: (h, params) => {
              return self.tableSpecRender(h, params.row.specOneId);
            }
          };
          culumns.push(temp);
        } else if (i === 1) {
          let temp = {
            title: item.parentName,
            key: "specTwoId",
            width: 100,
            render: (h, params) => {
              return self.tableSpecRender(h, params.row.specTwoId);
            }
          };
          culumns.push(temp);
        } else if (i === 2) {
          let temp = {
            title: item.parentName,
            key: "specThreeId",
            width: 100,
            render: (h, params) => {
              return self.tableSpecRender(h, params.row.specThreeId);
            }
          };
          culumns.push(temp);
        }
      }
      culumns.push({
        title: "条码",
        key: "barCode",
        widht: 150,
        render: (h, params) => {
          return h("Input", {
            props: {
              value: self.goodsDetails[params.index][params.column.key]
            },
            on: {
              "on-blur"(event) {
                let row = self.goodsDetails[params.index];
                row[params.column.key] = event.target.value;
              }
            }
          });
        }
      });
      culumns.push({
        title: "批发价",
        key: "batchPrice",
        widht: 120,
        render: (h, params) => {
          return self.inputRender(h, params);
        }
      });
      culumns.push({
        title: "市场价",
        key: "retailPrice",
        widht: 120,
        render: (h, params) => {
          return self.inputRender(h, params);
        }
      });
      culumns.push({
        title: "参考进货价",
        key: "inPrice",
        widht: 120,
        render: (h, params) => {
          return self.inputRender(h, params);
        }
      });
      culumns.push({
        title: "操作",
        key: "action",
        widht: 100,
        render: (h, params) => {
          let usedCount = params.row.usedCount;
          if (usedCount && usedCount > 0) {
            return h("span", "不可删除");
          } else {
            return h(
              "Button",
              {
                props: {
                  type: "text",
                  icon: "close"
                },
                on: {
                  click: () => {
                    self.removeDetailItem(params.row, params.index);
                  }
                }
              },
              ""
            );
          }
        }
      });
      return culumns;
    },

    tableSpecRender(h, id) {
      let item = {};
      let label = "";
      for (let i = 0; i < this.currParentSpecs.length; i++) {
        let subItems = this.currParentSpecs[i].subGoodsSpecs;
        if (subItems.length <= 0) {
          continue;
        }
        let matchItem = subItems.find(function(subItem) {
          if (id === subItem.id) {
            return subItem;
          }
        });
        if (matchItem && matchItem.id) {
          label = matchItem.specName;
          break;
        }
      }
      return h("span", label);
    },

    inputRender(h, params) {
      let self = this;
      return h("Input", {
        props: {
          value: self.goodsDetails[params.index][params.column.key]
        },
        on: {
          "on-blur"(event) {
            let row = self.goodsDetails[params.index];
            row[params.column.key] = event.target.value;
          }
        }
      });
    },

    removeDetailItem(row, index) {
      if (row.id) {
        //关联有后端的数据,ajax做删除操作
        util.ajax
          .delete("/goods/detail/remove/" + row.id)
          .then(response => {
            //删除成功时把表中的数据去除
            this.goodsDetails.splice(index, 1);
          })
          .catch(error => {
            util.errorProcessor(this, error);
          });
      } else {
        //没有关联到后天数据，直接考虑删除
        this.goodsDetails.splice(index, 1);
      }
    },

    //展开选择的规格明细展开成一个表格数据
    makeSpecTableData() {
      let specTableData = [];
      if (this.currParentSpecs.length <= 0) {
        specTableData = [];
        return;
      }
      for (let i = 0; i < this.currParentSpecs.length; i++) {
        let idList = this.currSpecIds[i];

        if (!idList || idList.length <= 0) {
          continue;
        }
        specTableData = this.makeSpecTableDataItem(i, idList, specTableData);
      }
      return specTableData;
    },

    makeSpecTableDataItem(index, idList, resultList) {
      let self = this;
      if (index === 0) {
        let datailList = idList.map(id => {
          let item = {
            specOneId: id,
            specTwoId: "",
            specThreeId: "",
            barCode: "",
            batchPrice: self.formData.batchPrice ? self.formData.batchPrice : 0,
            retailPrice: self.formData.retailPrice
              ? self.formData.retailPrice
              : 0,
            inPrice: self.formData.inPrice ? self.formData.inPrice : 0
          };
          return item;
        });
        resultList = [...datailList];
        return resultList;
      } else {
        let tempList = [];
        for (let i = 0; i < resultList.length; i++) {
          let result = resultList[i];
          for (let j = 0; j < idList.length; j++) {
            let temp = {
              specOneId: result.specOneId,
              specTwoId: result.specTwoId,
              specThreeId: result.specThreeId,
              barCode: result.barCode,
              batchPrice: result.batchPrice,
              retailPrice: result.retailPrice,
              inPrice: result.inPrice
            };
            let id = idList[j];
            if (index === 1) {
              temp.specTwoId = id;
            } else if (index === 2) {
              temp.specThreeId = id;
            }
            tempList.push(temp);
          }
        }
        return tempList;
      }
    },

    saveGoodsInfo() {
      let self = this;
      if (this.goodsBlackListValue) {
        this.formData.blackList = JSON.stringify(this.goodsBlackListValue);
      }
      this.$refs.form.validate(valid => {
        if (!valid) {
          return;
        } else {
          self.$Modal.confirm({
            title: "提交保存确认",
            content: "是否确认商品信息已经维护正确？",
            onOk: () => {
              self.saveLoading = true;
              self.formData.goodsDetails = self.goodsDetails;
              self.formData.attributeRefs = self.currAttributes;

              util.ajax
                .post("/goods/save", self.formData)
                .then(response => {
                  self.saveLoading = false;
                  self.$Message.success("商品信息保存成功");
                  self.$emit("save-ok");
                })
                .catch(error => {
                  self.saveLoading = false;
                  util.errorProcessor(self, error);
                });
            }
          });
        }
      });
    },

    chooseAttChange(data) {
      if (!data) {
        this.chooseAttValue = "";
        this.$refs.attSelect.clearSingleSelect();
        return;
      }
      let attItem = this.getGoodsAttributeItem(data);
      if (!attItem.id) {
        this.chooseAttValue = "";
        this.$refs.attSelect.clearSingleSelect();
        return;
      }
      //判断是否已经存在，如果已经存在，不进行添加
      if (!this.isAttributeHaveExist(data)) {
        let item = {
          attId: attItem.id,
          attName: attItem.attName,
          attValue: ""
        };
        this.currAttributes.push(item);
      }
      this.chooseAttValue = ""; //清除，让其再次可选
      this.$refs.attSelect.clearSingleSelect();
    },

    isAttributeHaveExist(attId) {
      for (let i = 0; i < this.currAttributes.length; i++) {
        if (attId === this.currAttributes[i].attId) {
          return true;
        }
      }
      return false;
    },

    getGoodsAttributeItem(attId) {
      for (let i = 0; i < this.goodsAttributes.length; i++) {
        let item = this.goodsAttributes[i];
        if (attId === item.id) {
          return item;
        }
      }
      return {};
    },

    removeOneAttribute(index) {
      this.currAttributes.splice(index, 1);
    },

    openFileUpload(fileNo) {
      this.uploadFileNo = fileNo;
      this.fileUploadModal = true;
    },
    addFileSuccess(data) {
      this.formData.fileNo = data.fileNo;
    }
  }
};
</script>


<style lang="less">
@import "../../styles/common.less";
@import "./supplier.less";
</style>

<template>
	<div>
		<Card>
        <p slot="title">
            <Icon type="compose"></Icon>
            {{ formItem.name || '供应商' }}
        </p>

        <div slot="extra">
            <Button type="success" @click="submitSupplier('edit')" :loading="supplierLoading" v-show="formItem.id">保存</Button>
            <Button type="primary" @click="submitSupplier('add')" :loading="supplierLoading" v-show="!formItem.id">提交</Button>
        </div>

        <Form :model="formItem" :rules="ruleValidate" :label-width="110">
            <Spin size="large" fix v-if="supplierLoading"></Spin>
            <Row>
                <i-col span="8">
                    <FormItem label="企业名称" prop="name">
                        <Input v-model="formItem.name" placeholder="供应商名称" @on-blur="onChangeName"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="拼音代码" prop="pinyin">
                        <Input v-model="formItem.pinyin" placeholder="例如 SXRZ"/>
                    </FormItem>
                </i-col>
                <i-col span="4">
                    <FormItem label="是否可用" prop="enabled">
                        <Checkbox v-model="formItem.enabled"></Checkbox>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="可以经营特殊商品" prop="canSpecial">
                        <Checkbox v-model="formItem.canSpecial"></Checkbox>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="账期" prop="term">
                        <Input number v-model="formItem.term"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="邮编" prop="postcode">
                        <Input v-model="formItem.postcode"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="城市" prop="placeCodes">
                        <al-cascader v-model="formItem.placeCodes" level="2" />
                    </FormItem>
                </i-col>
                <i-col span="16">
                    <FormItem label="详细地址" prop="address">
                        <Input v-model="formItem.address" placeholder="例如 某大道1号1203室"/>
                    </FormItem>
                </i-col>
                
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="传真" prop="fax">
                        <Input v-model="formItem.fax"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="电话" prop="phone">
                        <Input v-model="formItem.phone"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="Email" prop="email">
                        <Input v-model="formItem.email"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="联系人" prop="contact">
                        <Input v-model="formItem.contact"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="联系人电话" prop="contactPhone">
                        <Input v-model="formItem.contactPhone"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="负责人" prop="employee">
                        <Input v-model="formItem.employee"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="法人代表" prop="legalPerson">
                        <Input v-model="formItem.legalPerson"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="折扣率" prop="discount">
                        <Input v-model="formItem.discount"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                <FormItem label="税号" prop="taxNumber">
                    <Input v-model="formItem.taxNumber"/>
                </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="银行账户" prop="bankAccount">
                        <Input v-model="formItem.bankAccount"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="开户行" prop="bankName">
                        <Input v-model="formItem.bankName"/>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="银行账号" prop="bankNumber">
                        <Input v-model="formItem.bankNumber"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="12">
                    <FormItem label="仓库地址" prop="warehouseAddress">
                        <Input v-model="formItem.warehouseAddress"/>
                    </FormItem>
                </i-col>
                <i-col span="12">
                    <FormItem label="经营范围" >
                        <Select v-model="businessScopeChooseList" multiple >
                            <Option v-for="item in businessScopeList" :value="item.id" :key="item.id">{{ item.value }}</Option>
                        </Select>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="结算方式" prop="billingMethodId">
                        <optionSelect v-model="formItem.billingMethodId" optionType='BILLING_METHOD' ></optionSelect>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="类型" prop="supplierTypeId">
                        <optionSelect v-model="formItem.supplierTypeId" optionType='SUPPLIER_TYPE' ></optionSelect>
                    </FormItem>
                </i-col>
                <i-col span="8">
                    <FormItem label="检查首营档案" prop="checkFirst">
                        <Checkbox v-model="formItem.checkFirst"></Checkbox>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="4">
                    <FormItem label="是否有印章" prop="haveStamp">
                        <Checkbox v-model="formItem.haveStamp"></Checkbox>
                    </FormItem>
                </i-col>
                <i-col span="4">
                    <FormItem label="有票据格式模板" prop="haveBillTemplate">
                        <Checkbox v-model="formItem.haveBillTemplate"></Checkbox>
                    </FormItem>
                </i-col>
                <i-col span="4">
                    <FormItem label="是否生产企业" prop="factory">
                        <Checkbox v-model="formItem.factory"></Checkbox>
                    </FormItem>
                </i-col>
                <i-col span="4">
                    <FormItem label="是否直调供应商" prop="directSupplier">
                        <Checkbox v-model="formItem.directSupplier"></Checkbox>
                    </FormItem>
                </i-col>
                <i-col span="4">
                    <FormItem label="是否冷链经营" prop="coldBusiness">
                        <Checkbox v-model="formItem.coldBusiness"></Checkbox>
                    </FormItem>
                </i-col>
            </Row>



            <Tabs value="cert" type="card" @on-click="changeTabs">
                <TabPane label="证件" name="cert" icon="document-text">
                    <Row>
                        <i-col span="10">
                            <FormItem label="档案编号" prop="fileNo">
                                <i-input v-model="formItem.fileNo" readonly>
                                    <Button slot="append" type="text" icon="upload" @click="uploadFileInfo"></Button>
                                </i-input>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="10">
                            <FormItem label="营业执照号" prop="license">
                                <Input v-model="formItem.license"/>
                            </FormItem>
                        </i-col>
                        <i-col span="10">
                            <FormItem label="营业执照有效期" prop="licenseExp" :label-width="140">
                                <DatePicker type="date"  placeholder="营业执照有效期至" v-model="formItem.licenseExp" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="10">
                            <FormItem label="组织机构代码证" prop="organizationNo">
                                <Input v-model="formItem.organizationNo"/>
                            </FormItem>
                        </i-col>
                        <i-col span="10">
                            <FormItem label="组织机构代码证有效期" prop="organizationExp" :label-width="140">
                                <DatePicker type="date" placeholder="组织机构代码证有效期" v-model="formItem.organizationExp" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="10">
                            <FormItem label="GPS/GMP证书号" prop="gspGmpNo">
                                <Input v-model="formItem.gspGmpNo"/>
                            </FormItem>
                        </i-col>
                        <i-col span="10">
                            <FormItem label="GPS/GMP证书有效期" prop="gspGmpExp" :label-width="140">
                                <DatePicker type="date" placeholder="GPS/GMP证书有效期" v-model="formItem.gspGmpExp" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="10">
                            <FormItem label="质量保证协议" prop="qualityProtocolNo">
                                <Input v-model="formItem.qualityProtocolNo"/>
                            </FormItem>
                        </i-col>
                        <i-col span="10">
                            <FormItem label="质量保证协议有效期" prop="qualityProtocolExp" :label-width="140">
                                <DatePicker type="date" placeholder="质量保证协议有效期" v-model="formItem.qualityProtocolExp" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="10">
                            <FormItem label="购销合同协议" prop="saleProtocolNo">
                                <Input v-model="formItem.saleProtocolNo"/>
                            </FormItem>
                        </i-col>
                        <i-col span="10">
                            <FormItem label="购销合同协议有效期" prop="saleProtocolExp" :label-width="140">
                                <DatePicker type="date" placement="top-start" placeholder="购销合同协议有效期" v-model="formItem.saleProtocolExp" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="10">
                            <FormItem label="法人委托书" prop="legalProtocolNo">
                                <Input v-model="formItem.legalProtocolNo"/>
                            </FormItem>
                        </i-col>
                        <i-col span="10">
                            <FormItem label="法人委托书有效期" prop="legalProtocolExp" :label-width="140">
                                <DatePicker type="date" placement="top-start" placeholder="法人委托书有效期" v-model="formItem.legalProtocolExp" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="10">
                            <FormItem label="其他协议" prop="otherProtocolNo">
                                <Input v-model="formItem.otherProtocolNo"/>
                            </FormItem>
                        </i-col>
                        <i-col span="10">
                            <FormItem label="其他协议有效期" prop="otherProtocolExp" :label-width="140">
                                <DatePicker type="date" placement="top-start" placeholder="法人委托书有效期" v-model="formItem.otherProtocolExp" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <hr style="width:90%;margin-top:25px;margin-bottom:10px;" size="1"/>
                    <Row >
                        <i-col span="8">
                            <FormItem label="创建人" prop="createdBy">
                                <Input v-model="formItem.createdBy" disabled/>
                            </FormItem>
                        </i-col>
                        <i-col span="8">
                            <FormItem label="创建时间" prop="createdTime">
                                <DatePicker type="datetime" format="yyyy-MM-DD HH:mm" disabled v-model="formItem.createdTime" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row style="margin-bottom:65px;">
                        <i-col span="8">
                            <FormItem label="修改人" prop="updatedBy">
                                <Input v-model="formItem.updatedBy" disabled/>
                            </FormItem>
                        </i-col>
                        <i-col span="8">
                            <FormItem label="修改人时间" prop="updatedTime">
                                <DatePicker type="datetime" format="yyyy-MM-DD HH:mm" disabled v-model="formItem.updatedTime" transfer/>
                            </FormItem>
                        </i-col>
                    </Row>
                </TabPane>

                <TabPane label="业务代表" name="agent" icon="person-stalker" :disabled="!formItem.id">
                    <Spin size="large" fix v-if="contactLoading"></Spin>
                    <Card :bordered="false" dis-hover>
                        <p slot="title">
                            <Icon type="information-circled"></Icon>
                            <span class="help">业务代表是指上游单位和我方接洽的业务员</span>
                        </p>
                        <div slot="extra">
                            <ButtonGroup>
                                <Button type="primary" icon="android-add-circle" @click="addContact" size="small">添加</Button>
                            </ButtonGroup>
                        </div>
                        <Row type="flex" justify="center" align="middle">
                            <Table border :columns="contactColumns" :data="contactData" ref="contactTable" size="small"></Table>
                        </Row>
                    </Card>
                </TabPane>
            </Tabs>
        </Form>
    </Card>
    	<Modal v-model="addContactModal" width="360" :mask-closable="false">
			<p slot="header">
				<Icon type="ios-plus-outline"></Icon>
				<span>新增联系人</span>
			</p>
			<div>
				<Form :model="contact" :label-width="60">
					<FormItem label="姓名" prop="name">
						<Input v-model="contact.name"/>
					</FormItem>
                    <FormItem label="是否可用" prop="name">
						<Checkbox v-model="contact.enabled"></Checkbox>
					</FormItem>
					<FormItem label="电话" prop="phone">
						<Input v-model="contact.phone"/>
					</FormItem>
					<FormItem label="身份证" prop="idcard">
						<Input v-model="contact.idcard"/>
					</FormItem>
                    <FormItem label="城市" prop="placeCodes">
						<al-cascader v-model="contact.placeCodes" level="2" />
					</FormItem>
                    <FormItem label="经营区域" prop="businessScope">
						<Input v-model="contact.businessScope"/>
					</FormItem>
					<FormItem label="备注" prop="comment">
						<Input v-model="contact.comment"/>
					</FormItem>
				</Form>
			</div>
			<div slot="footer">
				<Button type="primary" @click="saveContact">保存</Button>
			</div>
		</Modal>

        <Modal v-model="fileUploadModal" title="供应商档案上传" :mask-closable="false" width="50" >
            <file-detail :fileNo="formItem.fileNo" @add-file-success="addFileSuccess" ></file-detail>
            <div slot="footer"></div>
        </Modal>
	</div>
</template>

<script>
import Vue from "vue";
import iviewArea from "iview-area";
import util from "@/libs/util.js";
import optionSelect from "@/views/selector/option-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";

Vue.use(iviewArea);

export default {
  name: "supplier-info",
  props: {
    supplierId: {
      type: String | Number,
      default: ""
    }
  },
  components: {
    optionSelect,
    fileDetail
  },
  data() {
    return {
      supplierLoading: false,
      businessScopeList: [],
      businessScopeChooseList: [],
      addContactModal: false,
      contactData: [],
      contact: {},
      formItem: {},
      contactLoading: false,
      contactColumns: [
        {
          key: "name",
          title: "姓名",
          align: "center"
        },
        {
          key: "phone",
          title: "电话",
          align: "center"
        },
        {
          key: "city",
          title: "城市",
          align: "center",
          render: (h, params) => {
            let rawCodes = params.row.placeCodes;
            let label = "";
            if (rawCodes && rawCodes.length > 0) {
              for (let i = 0; i < rawCodes.length; i++) {
                label += rawCodes[i].name + " ";
              }
            }
            return h("span", label);
          }
        },
        {
          key: "enabled",
          title: "启用/禁用",
          align: "center",
          width: 120,
          render: (h, params) => {
            let color = params.row.enabled ? "green" : "red";
            let label = params.row.enabled ? "启用" : "禁用";
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
          key: "comment",
          title: "备注",
          align: "center",
          width: 200
        },
        {
          title: "操作",
          align: "center",
          width: 200,
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "text",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.contact = params.row;
                      var rawCodes = params.row.placeCodes;
                      let placeCodes = [];
                      if (rawCodes) {
                        for (var i = 0; i < rawCodes.length; i++) {
                          placeCodes.push(rawCodes[i].code);
                        }
                      }
                      this.contact.placeCodes = placeCodes;
                      this.addContactModal = true;
                    }
                  }
                },
                "修改"
              ),
              h(
                "Button",
                {
                  props: {
                    type: "text",
                    size: "small"
                  },
                  on: {
                    click: () => {
                      this.removeContact(params.row);
                    }
                  }
                },
                "删除"
              )
            ]);
          }
        }
      ],
      ruleValidate: {
        name: [{ required: true, message: "名称不能为空", trigger: "blur" }]
      },
      fileUploadModal: false
    };
  },
  watch: {
    supplierId() {
      this.getSupplier();
    },
    businessScopeChooseList(data) {
      this.formItem.businessScopeIdList = data;
    }
  },
  methods: {
    getBusinessScopeList() {
      let reqData = ["GOODS_SCOPE"];
      util.ajax
        .post("/options/list", reqData)
        .then(response => {
          if (response.data && response.data.GOODS_SCOPE) {
            this.businessScopeList = response.data.GOODS_SCOPE;
          } else {
            this.businessScopeList = [];
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    getSupplier() {
      var self = this;
      if (this.supplierId) {
        self.supplierLoading = true;
        util.ajax
          .get("/supplier/" + this.supplierId)
          .then(function(response) {
            self.supplierLoading = false;
            self.formItem = response.data;
            var placeCodes = [];
            var rawCodes = response.data.placeCodes;
            if (rawCodes) {
              for (var i = 0; i < rawCodes.length; i++) {
                placeCodes.push(rawCodes[i].code);
              }
            }
            self.formItem.placeCodes = placeCodes;
            self.businessScopeChooseList = self.formItem.businessScopeIdList
              ? self.formItem.businessScopeIdList
              : [];
          })
          .catch(function(error) {
            self.supplierLoading = false;
            util.errorProcessor(self, error);
          });
      } else {
        this.formItem = {
          pinyin: ""
        };
        this.businessScopeChooseList = [];
      }
    },
    submitSupplier(action) {
      var self = this;
      self.supplierLoading = true;
      util.ajax
        .post("/supplier/save", this.formItem)
        .then(function(response) {
          self.supplierLoading = false;
          self.formItem = response.data;
          self.$Message.success("供应商" + self.formItem.name + "保存成功");
          self.$emit("save-ok", self.formItem, action);
        })
        .catch(function(error) {
          self.supplierLoading = false;
          util.errorProcessor(self, error);
        });
    },
    onChangeName() {
      if (this.formItem.name && this.formItem.name !== "") {
        var self = this;
        util.ajax
          .post("/util/pinyinAbbr", { name: this.formItem.name })
          .then(function(response) {
            self.formItem.pinyin = response.data;
          })
          .catch(function(error) {
            util.errorProcessor(self, error);
          });
      }
    },
    uploadFileInfo() {
      this.fileUploadModal = true;
    },
    addFileSuccess(data) {
      this.formItem.fileNo = data.fileNo;
    },
    addContact() {
      this.addContactModal = true;
      this.contact = {};
    },
    changeTabs(tabName) {
      if (tabName === "agent") {
        this.loadContacts();
      }
    },
    loadContacts() {
      var self = this;
      if (this.formItem.id) {
        util.ajax
          .get("/supplier/contact/list", {
            params: { supplierId: this.formItem.id }
          })
          .then(function(response) {
            self.contactData = response.data;
          })
          .catch(function(error) {
            util.errorProcessor(self, error);
          });
      }
    },
    saveContact() {
      var self = this;
      if (this.formItem.id) {
        this.contact.supplierId = this.formItem.id;
        util.ajax
          .post("/supplier/contact/save", this.contact)
          .then(function(response) {
            self.$Message.success(
              "供应商业务代表" + self.contact.name + "保存成功"
            );
            self.addContactModal = false;
            self.loadContacts();
          })
          .catch(function(error) {
            util.errorProcessor(self, error);
          });
      } else {
        this.$Message.warning("请先保存供应商");
      }
    },

    removeContact(row) {
      var self = this;
      this.$Modal.confirm({
        title: "删除确认",
        content: "是否确认删除" + row.name + "供应商代表?",
        onOk: () => {
          util.ajax
            .post("/supplier/contact/remove/" + row.id)
            .then(function(response) {
              self.$Message.success("删除成功");
              self.loadContacts();
            })
            .catch(function(error) {
              util.errorProcessor(self, error);
            });
        },
        onCancel: () => {}
      });
    }
  },
  mounted() {
    this.getSupplier();
    this.getBusinessScopeList();
  },
  activated() {
    this.getSupplier();
    this.getBusinessScopeList();
  }
};
</script>

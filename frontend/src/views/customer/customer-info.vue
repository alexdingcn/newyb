<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row>
          <Card>
              <p slot="title">
                  <Icon :type="titleDispayIcon"></Icon>
                  {{titleDispayName || '客户详情'}}
              </p>

              <ButtonGroup slot="extra">
                  <Button type="primary" :loading="submitBtnLoading" @click="submitCustomer">
                    <span v-if="!submitBtnLoading">提交</span>
                    <span v-else>正在提交...</span>
                  </Button>
              </ButtonGroup>

              <Form ref="customerForm" :model="formItem" :rules="ruleValidate" :label-width="100">
                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="客户分组" prop="categoryId">
                      <Select v-model="formItem.categoryId" placeholder="请选择客户分组">
                        <Option v-for="item in categorys" :value="item.id" :key="item.id">{{ item.name }}</Option>
                      </Select>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="客户编码" prop="customerNo">
                      <Input type="text" v-model="formItem.customerNo" placeholder="请输入客户编码"></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="客户名称" prop="name">
                      <Input type="text" v-model="formItem.name" placeholder="请输入客户名称"></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="简称" >
                      <Input type="text" v-model="formItem.shorName" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="拼音简码" >
                      <Input type="text" v-model="formItem.pinyin" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="是否禁用" >
                      <Checkbox v-model="formItem.enabled"></Checkbox>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="可经营特殊管理药品" :label-width="150" >
                      <Checkbox v-model="formItem.canSaleSpecial"></Checkbox>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="含麻黄碱药品限购" :label-width="150">
                      <Checkbox v-model="formItem.limitSpecial"></Checkbox>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="是否直调企业" :label-width="150">
                      <Checkbox v-model="formItem.direction"></Checkbox>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="所在城市" >
                      <Input type="text" v-model="formItem.city" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="16">
                    <FormItem label="注册地址" >
                      <Input type="text" v-model="formItem.address" ></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="法定代表人" >
                      <Input type="text" v-model="formItem.legalPersion" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="邮编" >
                      <Input type="text" v-model="formItem.postcode" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="电话" >
                      <Input type="text" v-model="formItem.contactPhone" ></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="负责人" >
                      <Input type="text" v-model="formItem.employee" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="传真" >
                      <Input type="text" v-model="formItem.contactFax" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="E-mail" >
                      <Input type="text" v-model="formItem.email" ></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="销售区域" >
                      <Input type="text" v-model="formItem.saleArea" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="业务员" >
                      <Input type="text" v-model="formItem.salesman" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="会员价等级" >
                      <Input type="text" v-model="formItem.memberLevel" ></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="分类属性1" >
                      <Input type="text" v-model="formItem.classAttOne" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="分类属性2" >
                      <Input type="text" v-model="formItem.classAttTwo" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="印章模板" >
                      <Input type="text" v-model="formItem.sealModel" placeholder="请输入印章模板档案编号" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="票据格式模板" >
                      <Input type="text" v-model="formItem.billModel" placeholder="请输入票据格式模板档案编号" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="账期(天)" >
                      <Input type="text" v-model="formItem.accountPeriod" ></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="24">
                    <FormItem label="经营范围" >
                      <Input type="text" v-model="formItem.businessScope" ></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="24">
                    <FormItem label="备注" >
                      <Input type="text" v-model="formItem.comment" ></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Tabs type="card">
                  <TabPane label="账户信息" name="accountInfo">
                    <Row type="flex" justify="start">
                      <Col span="8">
                        <FormItem label="账户名称" >
                          <Input type="text" v-model="formItem.accountName" ></Input>
                        </FormItem>
                      </Col>
                      <Col span="8">
                        <FormItem label="开户银行" >
                          <Input type="text" v-model="formItem.bankName" ></Input>
                        </FormItem>
                      </Col>
                    </Row>
                    <Row type="flex" justify="start">
                      <Col span="8">
                        <FormItem label="账号" >
                          <Input type="text" v-model="formItem.bankAccount" ></Input>
                        </FormItem>
                      </Col>
                      <Col span="8">
                        <FormItem label="税号" >
                          <Input type="text" v-model="formItem.dutyAccount" ></Input>
                        </FormItem>
                      </Col>
                    </Row>
                    <Row type="flex" justify="start">
                      <Col span="8">
                        <FormItem label="资格审查" >
                          <Input type="text" v-model="formItem.quaCheck" ></Input>
                        </FormItem>
                      </Col>
                      <Col span="8">
                        <FormItem label="档案号" >
                          <Input type="text" v-model="formItem.fileNo" ></Input>
                        </FormItem>
                      </Col>
                    </Row>
                    <Row type="flex" justify="start">
                      <Col span="8">
                        <FormItem label="电子监管码" >
                          <Input type="text" v-model="formItem.superviseNo" ></Input>
                        </FormItem>
                      </Col>
                    </Row>
                  </TabPane>
                  <TabPane label="证件信息" name="certInfo">
                    <Row type="flex" justify="start">
                        <ButtonGroup size="small" >
                          <Button style="width: 90px;" @click="certAddBtnClick">
                            <Icon type="plus-round" color="green"></Icon>
                            添加
                          </Button>
                          <Button style="width: 90px;" :loanding="certDelBtnLoading" @click="certDelBtnClick" >
                            <Icon type="trash-a" color="red"></Icon>
                            删除
                          </Button>
                        </ButtonGroup>
                        <Table ref="certTab" border highlight-row size="small" 
                          :columns="certTabColumns" :data="certTabData" 
                          :loading="certTabLoading" 
                          @on-selection-change="certTabSelecttionChange" 
                          >
                        </Table>
                    </Row>
                  </TabPane>
                  <TabPane label="代表人信息" name="repInfo">
                    <Row type="flex" justify="start">
                        <ButtonGroup size="small" >
                          <Button style="width: 90px;" @click="repAddBtnClick">
                            <Icon type="plus-round" color="green"></Icon>
                            添加
                          </Button>
                          <Button style="width: 90px;" :loading="repDelBtnLoading" @click="repDelBtnClick" >
                            <Icon type="trash-a" color="red"></Icon>
                            删除
                          </Button>
                        </ButtonGroup>
                        <Table ref="repTab" border highlight-row size="small" 
                          :columns="repTabColumns" :data="repTabData" 
                          :loading="repTabLoading" 
                          @on-selection-change="repTabSelecttionChange" 
                          >
                        </Table>
                    </Row>
                  </TabPane>
                </Tabs>
              </Form>
          </Card>
      </Row>

      <Modal v-model="certModalShow">
          <p slot="header">
              <Icon type="ios-plus-outline"></Icon>
              <span>新增客户证件</span>
          </p>
          
          <Form ref="certForm" :model="certFormItem" :label-width="100" :rules="certFormValidate">
                <FormItem label="证件名称" prop="licenseName">
                  <Input type="text" v-model="certFormItem.licenseName" ></Input>
                </FormItem>
                <FormItem label="有效期至" prop="licenseExp">
                  <DatePicker type="date" v-model="certFormItem.licenseExp" placeholder="请选择有效期" ></DatePicker>
                </FormItem>
                <FormItem label="档案编号" prop="imageNo">
                  <Input type="text" v-model="certFormItem.imageNo" ></Input>
                </FormItem>
                <FormItem label="备注" >
                  <Input type="text" v-model="certFormItem.comment" ></Input>
                </FormItem>
          </Form>

          <div slot="footer">
            <Row >
              <Col span="6" offset="6">
                <Button type="primary" :loading="certSubmitBtnLoading" @click="certSubmitBtnClick" long>
                  <span v-if="!certSubmitBtnLoading">提交</span>
                  <span v-else>正在提交...</span>
                </Button>
              </Col>
              <Col span=6 class="padding-left-10">
                <Button @click="certCancelBtnClick" long>取消</Button>
              </Col>
            </Row>
          </div>
      </Modal>

      <Modal v-model="repModalShow">
          <p slot="header">
              <Icon type="ios-plus-outline"></Icon>
              <span>新增客户代表人信息</span>
          </p>
          
          <Form ref="repForm" :model="repFormItem" :label-width="100" :rules="repFormValidate">
                <FormItem label="姓名" prop="name">
                  <Input type="text" v-model="repFormItem.name" ></Input>
                </FormItem>
                <FormItem label="联系电话" prop="contactPhone">
                  <Input type="text" v-model="repFormItem.contactPhone" ></Input>
                </FormItem>
                <FormItem label="收货地址" prop="repertoryAddress">
                  <Input type="text" v-model="repFormItem.repertoryAddress" ></Input>
                </FormItem>
                <FormItem label="邮编" >
                  <Input type="text" v-model="repFormItem.postcode" ></Input>
                </FormItem>
                <FormItem label="是否禁用" >
                  <Checkbox v-model="repFormItem.enabled"></Checkbox>
                </FormItem>
                <FormItem label="是否默认使用" >
                  <Checkbox v-model="repFormItem.isDefault"></Checkbox>
                </FormItem>
                <FormItem label="备注" >
                  <Input type="text" v-model="repFormItem.comment" ></Input>
                </FormItem>
          </Form>

          <div slot="footer">
            <Row >
              <Col span="6" offset="6">
                <Button type="primary" :loading="repSubmitBtnLoading" @click="repSubmitBtnClick" long>
                  <span v-if="!repSubmitBtnLoading">提交</span>
                  <span v-else>正在提交...</span>
                </Button>
              </Col>
              <Col span=6 class="padding-left-10">
                <Button @click="repCancelBtnClick" long>取消</Button>
              </Col>
            </Row>
          </div>
      </Modal>

  </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";

export default {
  name: "customer-info",
  data() {
    return {
      showView: 'add',
      showTitle: '新建客户信息',
      editId: '',
      submitBtnLoading: false,
      categorys: [],
      formItem: {
        id: '',
        categoryId: '',
        customerNo: '',
        name: '',
        shorName: '',
        pinyin: '',
        enabled: 0,
        canSaleSpecial: 0,
        limitSpecial: 0,
        direction: 0,
        city: '',
        address: '',
        legalPersion: '',
        postcode: '',
        contactPhone: '',
        employee: '',
        contactFax: '',
        email: '',
        saleArea: '',
        salesman: '',
        memberLevel: '',
        classAttOne: '',
        classAttTwo: '',
        sealModel: '',
        billModel: '',
        accountPeriod: '',
        businessScope: '',
        comment: '',
        accountName: '',
        bankName: '',
        bankAccount: '',
        dutyAccount: '',
        quaCheck: '',
        fileNo: '',
        superviseNo: ''
      },
      certDelBtnLoading: false,
      certTabLoading: false,
      certTabData: [],
      certTabSelectedData: [],
      certTabColumns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          type: "index",
          width: 80,
          title: "序号",
          align: "center"
        },
        {
          title: "证书名称",
          key: "licenseName",
          align: "center"
        },
        {
          title: "有效期至",
          key: "licenseExp",
          align: "center",
          render: (h, params) => {
            let date = params.licenseExp;
            if (!date || isNaN(date)) {
              return h('span', '');
            }else {
              return h('span', dataConver.formatDate(new Date(date), 'yyyy-MM-dd'));
            }
          }
        },
        {
          title: "档案编号",
          key: "imageNo",
          align: "center"
        },
        {
          title: "备注",
          key: "comment",
          align: "center"
        },
        {
          title: "操作人",
          key: "updateBy",
          align: "center"
        },
        {
          title: "操作时间",
          key: "updateTime",
          align: "center",
          render: (h, params) => {
            let date = params.updateTime;
            if (!date || isNaN(date)) {
              return h('span', '');
            }else {
              return h('span', dataConver.formatDate(new Date(date), 'yyyy-MM-dd'));
            }
          }
        }
      ],
      certModalShow: false,
      certFormItem: {
        customerId: '',
        licenseName: '',
        licenseExp: '',
        imageNo: '',
        comment: ''
      },
      certSubmitBtnLoading: false,
      repTabLoading: false,
      repTabData: [],
      repTabSelectedData: [],
      repTabColumns: [
        {
          type: "selection",
          width: 60,
          align: "center",
        },
        {
          type: "index",
          width: 80,
          title: "序号",
          align: "center"
        },
        {
          title: "代表人姓名",
          key: "name",
          align: "center", 
          render: (h, params) => {
            return h(
              "Button",
              {
                props: {
                  type: "text",
                },
                on: {
                  click: () => {
                    this.repEditBtnClick(params.row.id);
                  }
                }
              },
              params.row.name
            );
          }
        },
        {
          title: "联系电话",
          key: "contactPhone",
          align: "center"
        },
        {
          title: "收货地址",
          width: 300,
          key: "repertoryAddress",
          align: "center"
        },
        {
          title: "是否禁用",
          key: "enabled",
          align: "center",
          sortable: true,
          render: (h, params) => {
            let isTrue = params.row.enabled;
            return h("div", [
              h("Icon", {
                props: {
                  type: isTrue ? "checkmark-circled" : "close-circled",
                  color: isTrue ? "#00a854" : "#e96500"
                }
              }),
              h("strong", isTrue ? "启用" : "已禁用")
            ]);
          }
        },
        {
          title: "是否默认使用",
          key: "isDefault",
          align: "center",
          sortable: true,
          render: (h, params) => {
            let isTrue = params.row.isDefault;
            return h("div", [
              h("Icon", {
                props: {
                  type: isTrue ? "checkmark-circled" : "minus-circled",
                  color: isTrue ? "#00a854" : ''
                }
              }),
              h("strong", isTrue ? "是" : "否")
            ]);
          }
        },
        {
          title: "备注",
          key: "comment",
          align: "center"
        }
      ],
      repDelBtnLoading: false,
      repModalShow: false,
      repModalType: '',
      repFormItem: {
        id: '',
        customerId: '',
        name: '',
        contactPhone: '',
        repertoryAddress: '',
        postcode: '',
        enabled: 0,
        isDefault: 1,
        comment: ''
      },
      repSubmitBtnLoading: false,
      ruleValidate: {
        categoryId: [
          {required: true, type:'number', message: '客户分组必输', trigger: 'change'}
        ],
        customerNo: [
          {required: true, message: '客户编码必输', trigger: 'blur'}
        ],
        name: [
          {required: true, message: '客户名称必输', trigger: 'blur'}
        ]
      },
      certFormValidate: {
        licenseName: [
          {required: true, message: '证件名称必输', trigger: 'blur'}
        ],
        licenseExp: [
          {required: true, type:'date', message: '证件有效期必输', trigger: 'change'}
        ],
        imageNo: [
          {required: true, message: '证件档案编号必输', trigger: 'blur'}
        ]
      },
      repFormValidate: {
        name: [
          {required: true, message: '代表人姓名必输', trigger: 'blur'}
        ],
        contactPhone: [
          {required: true, message: '联系方式必输', trigger: 'blur'}
        ],
        repertoryAddress: [
          {required: true, message: '收货地址必输', trigger: 'blur'}
        ]
      }
    };
  },
  mounted() {
    this.initData();
  },
  computed: {
    titleDispayIcon() {
      if (this.showView === "add") {
        return "plus-round";
      } else {
        return "compose";
      }
    },
    titleDispayName() {
      return this.showTitle;
    }
  },
  watch: {
    
  },
  methods: {
    initData() {
      this.getAllCategorys();
    },

    getAllCategorys() {
      util.ajax
        .get("/customer/category/list")
        .then((res) => {
          this.categorys = res.data;
        })
        .catch((error) => {
          util.errorProcessor(this, error);
        });
    },

    changeToEditModal(data) {
      if (!data || !data.id) {
        this.$Notice.warn("获取编辑模式下的客户标识失败");
        return
      }
      this.formItem = data;
      //enabled 后台1代表可用，0代表禁用，前端显示刚好取反
      this.formItem.enabled = !data.enabled;
      console.log(data);
      this.editId = data.id;
      this.showView = 'edit';
      this.showTitle = data.name;
    },

    submitCustomer() {
      this.submitBtnLoading = true;
      this.$refs.customerForm.validate(valid => {
        if (!valid) {
          this.submitBtnLoading = false;
          this.$Message.warn("请检查必输项是否输入");
          return;
        }else {
          if (this.showView === 'add') {
            this.doAddCustomer();
          }else {
            this.doUpdateCustomer();
          }
          this.submitBtnLoading = false;
        }
      });
    },

    doAddCustomer() {
      let reqData = this.formItem;
      //enabled 后台1代表可用，0代表禁用，前端显示刚好取反
      reqData.enabled = !this.formItem.enabled;
      util.ajax.post("/customer/add", reqData, {headers:{'Content-Type': 'application/json'}})
        .then((response) => {
          this.$Message.success("新建客户成功");
          this.changeToEditModal(response.data);
        })
        .catch((error) => {
          util.errorProcessor(this, error);
        });
    },

    doUpdateCustomer() {
      let reqData = this.formItem;
      if(!reqData || !reqData.id || reqData.id === '') {
        this.$Notice.error({
          ttile: '系统异常',
          desc: '获取修改客户ID失败'
        });
        return;
      }
      //enabled 后台1代表可用，0代表禁用，前端显示刚好取反
      reqData.enabled = !this.formItem.enabled;
      util.ajax.post("/customer/update", reqData, {headers:{'Content-Type': 'application/json'}})
        .then((response) => {
          this.$Message.success("修改客户成功");
          changeToEditModal(response.data);
        })
        .catch((error) => {
          console.log(error.response);
          util.errorProcessor(this, error);
        });
    },

    certAddBtnClick() {
      this.certModalShow = true;
      this.certSubmitBtnLoading = false; 
    },

    refreshCertData(customerId) {
      if (!customerId || customerId === '') {
        return;
      }
      this.certTabLoading = true;
      let reqData = {customerId: customerId};
      util.ajax.get("/customer/cert/list", {params: reqData})
        .then((response) => {
          this.certTabData = response.data;
        })
        .catch((error) => {
          util.errorProcessor(this, error);
        })
      this.certTabLoading = false;
    },

    certSubmitBtnClick() {
      this.certSubmitBtnLoading = true;
      this.$refs.certForm.validate(valid => {
        if (!valid) {
          this.$Message.warn('请检查证件必输项');
          this.certSubmitBtnLoading = false;
          return;
        }else {
          let addCustomerId = this.editId;
          console.log('addCustomerId:' + addCustomerId);
          if (!addCustomerId || addCustomerId === '') {
            this.$Notice.info({title: '操作提醒', desc: '请先提交客户基本信息建立客户'});
            this.certSubmitBtnLoading = false;
            return;
          }else {
            this.certFormItem.customerId = addCustomerId;
            util.ajax.post("/customer/cert/add", this.certFormItem)
              .then((response) => {
                this.$Message.success('新建客户证件信息成功');
                this.refreshCertData(addCustomerId);
                this.certModalShow = false;
              })
              .catch((error) => {
                util.errorProcessor(this, error);
              });
            this.certSubmitBtnLoading = false;
          }
        }
      });
    },

    certCancelBtnClick() {
      this.certModalShow = false;
      this.certSubmitBtnLoading = false;
    },

    certDelBtnClick() {
      this.certDelBtnLoading = true;
      let delCertIds = [];
      if (!this.certTabSelectedData || this.certTabSelectedData.length <= 0) {
        this.$Message.warn('请先选择需要删除的证件信息');
        this.certDelBtnLoading = false;
        return;
      }
      for (let i=0; i<this.certTabSelectedData.length; i++) {
        delCertIds.push(this.certTabSelectedData[i].id);
      }
      util.ajax.post("/customer/cert/delete", delCertIds, {headers:{'Content-Type': 'application/json'}})
        .then((response) => {
          this.$Message.success('删除客户证件信息成功');
          this.refreshCertData(this.editId);
        })
        .catch((error) => {
          util.errorProcessor(this, error);
        });
      this.certDelBtnLoading = false;
    },

    certTabSelecttionChange(data) {
      this.certTabSelectedData = data;
    },

    repTabSelecttionChange(data) {
      this.repTabSelectedData = data;
    },

    refreshRepData(customerId) {
      if (!customerId || customerId === '') {
        return;
      }
      this.repTabLoading = true;
      let reqData = {customerId: customerId};
      util.ajax.get("/customer/rep/list", {params: reqData})
        .then((response) => {
          this.repTabData = response.data;
        })
        .catch((error) => {
          util.errorProcessor(this, error)
        });
      this.repTabLoading = false;
    },

    repDelBtnClick() {
      this.repDelBtnLoading = true;
      if (!this.repTabSelectedData || this.repTabSelectedData.length <= 0) {
        this.$Message.warn('请先选择需要删除的代表人信息');
        this.repDelBtnLoading = false;
        return;
      }else {
        let delIds = [];
        for (let i=0; i<this.repTabSelectedData.length; i++) {
          delIds.push(this.repTabSelectedData[i].id);
        }
        util.ajax.post("/customer/rep/delete", delIds, {headers:{'Content-Type': 'application/json'}})
          .then((respose) => {
            this.$Message.success('删除客户代表人成功');
            this.refreshRepData(this.editId);
          })
          .catch((error) => {
            util.errorProcessor(this, error);
          });
        this.repDelBtnLoading = false;
      }
    },

    repAddBtnClick() {
      this.repModalShow = true;
      this.repModalType = 'add';
    },

    repEditBtnClick(repId) {
      this.repFormItem.id = repId;
      this.repModalShow = true;
      this.repModalType = 'edit';
    },

    repSubmitBtnClick() {
      if (this.editId && this.repModalType === 'add') {
        this.doAddCustomerRep();
      }else if (this.editId && this.repModalType === 'edit') {
        this.doEditCustomerRep();
      }else {
        this.$Notice.info({title: '操作提醒', desc: '请先提交客户基本信息建立客户'});
        return;
      }
    },

    doAddCustomerRep() {
      let customerId = this.editId;
      this.repSubmitBtnLoading = true;
      this.$refs.repForm.validate(valid => {
        if (!valid) {
          this.$Message.warn('请检查表单必输项信息');
          this.repSubmitBtnLoading = false;
          return;
        }else {
          this.repFormItem.customerId = customerId;
          util.ajax.post("/customer/rep/add", this.repFormItem, {headers:{'Content-Type': 'application/json'}})
            .then((response) => {
              this.$Message.success('新建客户代表人信息成功');
              this.refreshRepData(customerId);
              this.repModalShow = false;
            })
            .catch((error) => {
              util.errorProcessor(this, error);
            });
          this.repSubmitBtnLoading = false;
        }
      });
    },

    doEditCustomerRep() {
      let customerId = this.editId;
      this.repSubmitBtnLoading = true;
      if (!customerId) {
        this.$Notice.info({title: '操作提醒', desc: '请先提交客户基本信息建立客户'});
        this.repSubmitBtnLoading = false;
        return;
      }
      if (!this.repFormItem.id || this.repFormItem.id === '') {
        this.$$Notice.warn({title:'系统异常', desc:'获取需要修改的代表人编号失败，请重新选择'});
        this.repSubmitBtnLoading = false;
        return;
      }
      util.ajax.post("/customer/rep/update", this.repFormItem, {headers:{'Content-Type': 'application/json'}})
        .then((response) => {
          this.$Message.success('修改代表人信息成功');
          this.refreshRepData(customerId);
          this.repModalShow = false;
        })
        .catch((error) => {
          util.errorProcessor(this, error);
        });
      this.repSubmitBtnLoading = false;
    },

    repCancelBtnClick() {
      this.repModalShow = false;
    }


  }
};
</script>


<style>

</style>


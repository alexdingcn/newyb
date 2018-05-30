<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row>
          <Card>
              <p slot="title">
                  <Icon :type="formItem.id ? 'compose' : 'plus-round'"></Icon>
                  {{formItem.name || '客户详情'}}
              </p>

              <ButtonGroup v-if="!isReadOnly" slot="extra">
                  <Button type="success" :loading="submitBtnLoading" v-if="formItem.id" @click="doUpdateCustomer">保存</Button>
                  <Button type="primary" :loading="submitBtnLoading" v-if="!formItem.id" @click="doAddCustomer">提交</Button>
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
                    <FormItem label="客户名称" prop="name">
                      <Input type="text" v-model="formItem.name" placeholder="请输入客户名称"  @on-blur="onChangeName"></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="客户编码" prop="customerNo">
                      <Input type="text" v-model="formItem.customerNo" disabled placeholder="系统自动生成"></Input>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="拼音简码" >
                      <Input type="text" v-model="formItem.pinyin" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="是否启用" >
                      <Checkbox v-model="formItem.enabled"></Checkbox>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="是否直调企业" >
                      <Checkbox v-model="formItem.direction"></Checkbox>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="6">
                    <FormItem label="可经营特殊管理药品" :label-width="150" >
                      <Checkbox v-model="formItem.canSaleSpecial"></Checkbox>
                    </FormItem>
                  </Col>
                  <Col span="6">
                    <FormItem label="含麻黄碱药品限购" :label-width="150">
                      <Checkbox v-model="formItem.limitSpecial"></Checkbox>
                    </FormItem>
                  </Col>
                  <Col span="6">
                    <FormItem label="是否冷链经营" >
                      <Checkbox v-model="formItem.coldBusiness"></Checkbox>
                    </FormItem>
                  </Col>
                  <Col span="6">
                    <FormItem label="是否两票制" >
                      <Checkbox v-model="formItem.twoTicket"></Checkbox>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="所在城市" >
                      <al-cascader v-model="placeCodeLsit" level="2" />
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
                      <Input type="text" v-model="formItem.legalPerson" ></Input>
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
                      <Input type="text" v-model="formItem.saleMan" ></Input>
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
                    <FormItem label="经营范围" >
                      <Select v-model="businessScopeChooseList" multiple >
                            <Option v-for="item in businessScopeList" :value="item.id" :key="item.id">{{ item.value }}</Option>
                       </Select>
                    </FormItem>
                  </Col>
                </Row>

                <Row type="flex" justify="center">
                  <Col span="8">
                    <FormItem label="印章模板" >
                      <Input type="text" v-model="formItem.stampTemplate" placeholder="请输入印章模板编号" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                    <FormItem label="票据格式模板" >
                      <Input type="text" v-model="formItem.billTemplate" placeholder="请输入票据格式模板编号" ></Input>
                    </FormItem>
                  </Col>
                  <Col span="8">
                        <FormItem label="档案号" >
                            <Input type="text" v-model="formItem.fileNo" readonly >
                                <Button slot="append" type="text" icon='upload' @click="uploadFileInfo"></Button>
                            </Input>
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

                <Tabs type="card" @on-click="changeTabs">
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
                      <Col span="8">
                        <FormItem label="账号" >
                          <Input type="text" v-model="formItem.bankAccount" ></Input>
                        </FormItem>
                      </Col>
                    </Row>
                    <Row type="flex" justify="start">
                      <Col span="8">
                        <FormItem label="税号" >
                          <Input type="text" v-model="formItem.taxAccount" ></Input>
                        </FormItem>
                      </Col>
                      <Col span="8">
                            <FormItem label="账期(天)" >
                                <Input number v-model="formItem.accountTerm" ></Input>
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
                        <FormItem label="电子监管码" >
                          <Input type="text" v-model="formItem.superviseNo" ></Input>
                        </FormItem>
                      </Col>
                    </Row>
                  </TabPane>
                  <TabPane label="地址信息" name="repInfo" :disabled="!formItem.id">
                    <Row type="flex" justify="start">
                        <ButtonGroup v-if="!isReadOnly" size="small" >
                          <Button type="primary" @click="addRepModal" icon="plus-round">
                            添加
                          </Button>
                        </ButtonGroup>
                        <Table ref="repTab" border highlight-row size="small" 
                            :columns="repTabColumns" :data="repTabData" 
                            :loading="repTabLoading" 
                            @on-row-dblclick="repDelBtnClick"
                        >
                        </Table>
                    </Row>
                  </TabPane>
                </Tabs>
              </Form>
          </Card>
      </Row>

      <customer-rep :value="repFormItem" :showModal="repModalShow" :customer="formItem.id"
        @on-closed="onRepUpdated">
      </customer-rep>

      <Modal v-model="fileUploadModal" title="客户档案上传" :mask-closable="false" width="50" >
            <file-detail :fileNo="formItem.fileNo" @add-file-success="addFileSuccess" ></file-detail>
            <div slot="footer"></div>
        </Modal>

  </div>
</template>

<script>
import util from '@/libs/util.js';
import dataConver from '@/libs/data-conver.js';
import Vue from 'vue';
import iviewArea from 'iview-area';
import optionSelect from '@/views/selector/option-select.vue';
import fileDetail from "@/views/basic-data/file-detail.vue";
import customerRep from './customer-rep.vue';

Vue.use(iviewArea);

export default {
    name: 'customer-info',
    props: {
        action: {
            type: String,
            validator: function (value) {
                return value === 'see';
            }
        },
        categorys: {
            type: Array,
            required: true,
            default: []
        },
        customerId: {
            type: String|Number,
            default: ''
        }
    },
    components: {
        optionSelect,
        customerRep,
        fileDetail
    },
    data () {
        return {
            businessScopeList: [],
            businessScopeChooseList: [],
            isReadOnly: false,
            submitBtnLoading: false,
            formItem: {
                placeCodes: [],
                pinyin: ''
            },
            placeCodeLsit: [],
            fileUploadModal: false,
            repTabLoading: false,
            repTabData: [],
            repTabColumns: [
                {
                    title: '联系人',
                    key: 'name',
                    align: 'center',
                },
                {
                    title: '联系电话',
                    key: 'contactPhone',
                    align: 'center'
                },
                {
                    title: '收货地址',
                    key: 'repertoryAddress',
                    align: 'center'
                },
                {
                    title: '启用/禁用',
                    key: 'enabled',
                    align: 'center',
                    width: 120,
                    sortable: true,
                    render: (h, params) => {
                        let color = params.row.enabled ? 'green' : 'red';
                        let label = params.row.enabled ? '启用' : '禁用';
                        return h('Tag', {
                            props: {
                                type: 'dot',
                                color: color
                            }
                        }, label);
                    }
                },
                {
                    title: '默认地址',
                    key: 'isDefault',
                    align: 'center',
                    sortable: true,
                    render: (h, params) => {
                        let isTrue = params.row.isDefault;
                        return h('div', [
                            h('Icon', {
                                props: {
                                    type: isTrue ? 'checkmark-circled' : 'minus-circled',
                                    color: isTrue ? '#00a854' : ''
                                }
                            }),
                            h('strong', isTrue ? '是' : '否')
                        ]);
                    }
                },
                {
                    title: '备注',
                    key: 'comment',
                    align: 'center'
                },
                {
                    title: '操作',
                    width: 100,
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'primary',
                                icon: 'edit',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    this.editRepModal(params.row);
                                }
                            }
                        }, '修改')
                    }
                }
            ],
            repDelBtnLoading: false,
            repModalShow: false,
            repFormItem: {},
            ruleValidate: {
                categoryId: [
                    {required: true, type: 'number', message: '客户分组必输', trigger: 'change'}
                ],
                name: [
                    {required: true, message: '客户名称必输', trigger: 'blur'}
                ]
            }
        };
    },
    watch: {
        action (data) {
            if (data === 'see') {
                this.isReadOnly = false;
            }
        },
        businessScopeChooseList(data) {
            this.formItem.businessScopeIdList = data;
        },
        customerId() {
            this.reload();
        }
    },
    mounted () {
        this.getBusinessScopeList();
        this.reload();
    },
    methods: {
        onChangeName () {
            if (this.formItem.name && this.formItem.name !== '') {
                var self = this;
                util.ajax.post('/util/pinyinAbbr', { name: this.formItem.name })
                    .then(function (response) {
                        self.formItem.pinyin = response.data;
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            }
        },

        getBusinessScopeList() {
            let reqData = ["GOODS_SCOPE"];
            util.ajax.post('/options/list', reqData)
                .then((response) => {
                    if(response.data && response.data.GOODS_SCOPE) {
                        this.businessScopeList = response.data.GOODS_SCOPE;
                    }else {
                        this.businessScopeList = [];
                    }
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        },

        reload() {
            if (this.customerId) {
                util.ajax.get("/customer/" + this.customerId)
                    .then((response) => {
                        this.formItem = response.data;
                        var placeCodes = [];
                        var rawCodes = response.data.placeCodes;
                        if (rawCodes) {
                            for (var i = 0; i < rawCodes.length; i++) {
                                placeCodes.push(rawCodes[i].code);
                            }
                        }
                        this.placeCodeLsit = placeCodes;
                        this.businessScopeChooseList = this.formItem.businessScopeIdList ? this.formItem.businessScopeIdList : [];
                        this.refreshRepData();
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    })
            }else {
                this.formItem = {
                    pinyin: ''
                };
                this.businessScopeChooseList = [];
            }
        },

        onRepUpdated(data) {
            this.repModalShow = false;
            if (data) {
                this.refreshRepData();
            }
        },

        doAddCustomer () {
            this.submitBtnLoading = true;
            this.formItem.placeCodes = this.placeCodeLsit;
            util.ajax.post('/customer/add', this.formItem)
                .then((response) => {
                    this.submitBtnLoading = false;
                    this.$Message.success('新建客户成功');
                    this.formItem = response.data;
                    this.$emit('add-success', response.data);
                })
                .catch((error) => {
                    this.submitBtnLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        doUpdateCustomer () {
            this.formItem.placeCodes = this.placeCodeLsit;
            let reqData = this.formItem;
            if (!this.formItem.id) {
                this.$Notice.error({
                    ttile: '系统异常',
                    desc: '获取修改客户ID失败'
                });
                return;
            }
            this.submitBtnLoading = true;
            util.ajax.post('/customer/update', reqData)
                .then((response) => {
                    this.submitBtnLoading = false;
                    this.$Message.success('修改客户成功');
                    this.$emit('update-success', response.data);
                })
                .catch((error) => {
                    this.submitBtnLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        uploadFileInfo() {
            this.fileUploadModal = true;
        },

        addFileSuccess(data) {
            this.formItem.fileNo = data.fileNo;
        },

        changeTabs (tabName) {
        	if (tabName === 'repInfo') {
        		this.refreshRepData();
        	}
        },

        refreshRepData () {
            if (!this.formItem.id) {
                return;
            }
            this.repTabLoading = true;
            let reqData = {customerId: this.formItem.id};
            util.ajax.get('/customer/rep/list', {params: reqData})
                .then((response) => {
                    this.repTabLoading = false;
                    this.repTabData = response.data;
                })
                .catch((error) => {
                    this.repTabLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        repDelBtnClick (row) {
            let self = this;
            this.$Modal.confirm({
                title: '删除确认',
                content: '是否确认删除' + row.name + '客户代表？',
                onOk: () => {
                    util.ajax.delete('/customer/rep/delete/' + row.id)
                        .then((respose) => {
                            self.$Message.success('删除客户代表人成功');
                            self.refreshRepData();
                        })
                        .catch((error) => {
                            util.errorProcessor(self, error);
                        });
                },
                onCancel: () => {}
            });
        },

        addRepModal () {
            this.repModalShow = true;
            this.repFormItem = {};
        },

        editRepModal (row) {
            // clone this row
            this.repFormItem = JSON.parse(JSON.stringify(row));
            this.repModalShow = true;
        },
    }
};
</script>


<style scoped>

</style>


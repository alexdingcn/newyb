<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
        <Row class="search-div">
            <Form ref="searchForm" :model="searchForm" :label-width="100">
            <Col span="8">
                <FormItem label="名称">
                    <Input type="text" v-model="searchForm.name"></Input>
                </FormItem>
            </Col>
            <Col span="8">
                <FormItem label="营业执照">
                    <Input type="text" v-model="searchForm.license"></Input>
                </FormItem>
            </Col>
            <Col span="2" offset="1">
                <Button type="primary" icon="ios-search" 
                    :loading="searching" @click="searchBtnClick"> 查询 </Button>
            </Col>
            <Col span="5"></Col>
            </Form>
        </Row>
      <div class="body-div">
          <Row type="flex" justify="start">
              <Button type="success" size="small" icon="plus-round" @click="addBtnClick">新建承运公司</Button>
          </Row>
          <Row type="flex" justify="center">
              <Table border highlight-row :columns="tabColumns" :data="tabData" 
                        :loading="searching" 
                        ref="table" style="width: 100%;" size="small">
                </Table>
          </Row>
          <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
      </div>

    <Modal v-model="showShipModal" :width="75" :title="shipModalTitle" :rules="shipValidate" :closable="false" :mask-closable="false">
        <Form ref="shipForm" :model="shipFormItem" :label-width="100">
            <Row type="flex" justify="center">
                <Col span="8">
                    <FormItem label="名称" prop="name">
                        <Input type="text" v-model="shipFormItem.name" placeholder="请输入名称"></Input>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="是否禁用">
                        <Checkbox v-model="shipFormItem.disabled"></Checkbox>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="营业执照">
                        <Input type="text" v-model="shipFormItem.license" placeholder="请输入营业执照"></Input>
                    </FormItem>
                </Col>
            </Row>
            <Row type="flex" justify="center">
                <Col span="8">
                    <FormItem label="执照到期日">
                        <DatePicker v-model="shipFormItem.licenseExp" type="date" 
                            format="yyyy-MM-dd" placeholder="请选择执照到期日" ></DatePicker>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="执照档案编号">
                        <Input type="text" v-model="shipFormItem.licenseFileNo" placeholder="请输入执照档案编号"></Input>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="负责人">
                        <Input type="text" v-model="shipFormItem.employee" placeholder="请输入负责人"></Input>
                    </FormItem>
                </Col>
            </Row>
            <Row type="flex" justify="center">
                <Col span="8">
                    <FormItem label="固定电话">
                        <Input type="text" v-model="shipFormItem.phone" placeholder="请输入固定电话"></Input>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="传真">
                        <Input type="text" v-model="shipFormItem.fax" placeholder="请输入传真"></Input>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="电子邮件">
                        <Input type="text" v-model="shipFormItem.email" placeholder="请输入电子邮件"></Input>
                    </FormItem>
                </Col>
            </Row>
            <Row type="flex" justify="center">
                <Col span="8">
                    <FormItem label="联系人">
                        <Input type="text" v-model="shipFormItem.contactUser" placeholder="请输入联系人"></Input>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="联系电话">
                        <Input type="text" v-model="shipFormItem.contactPhone" placeholder="请输入联系电话"></Input>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="邮编">
                        <Input type="text" v-model="shipFormItem.posecode" placeholder="请输入邮编"></Input>
                    </FormItem>
                </Col>
            </Row>
            <Row type="flex" justify="center">
                <Col span="12">
                    <FormItem label="地址">
                        <Input type="text" v-model="shipFormItem.address" placeholder="请输入地址"></Input>
                    </FormItem>
                </Col>
                <Col span="12">
                    <FormItem label="备注">
                        <Input type="text" v-model="shipFormItem.comment" placeholder="请输入备注"></Input>
                    </FormItem>
                </Col>
            </Row>
        </Form>
        <div slot="footer">
            <ButtonGroup shape="circle">
                <Button type="success" icon="checkmark-round" :loading="saveLoading" @click="saveShipInfo">保存</Button>
                <Button type="ghost" @click="shipModalClose">取消</Button>
            </ButtonGroup>
        </div>
    </Modal>

  </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";

export default {
    name: 'ship',
    data() {
        return {
            searchForm: {
                name: '',
                license: ''
            },
            searching: false,
            tabData: [],
            totalCount: 0,
            currentPage: 1,
            pageSize: 20,
            tabColumns: [
                {
                    type: 'index',
                    width: 60,
                    align: 'center',
                    fixed: 'left'
                },
                {
                    title: '名称',
                    key: 'name',
                    width: 200,
                    align: 'center',
                    fixed: 'left'
                },
                {
                    title: '是否禁用',
                    key: 'disabled',
                    width: 100,
                    align: 'center',
                    render: (h, params) => {
                        let disableVal = params.row.disabled;
                        return h("div", [
                            h("Icon", {
                                props: {
                                type: disableVal ? "close-circled" : "checkmark-circled",
                                color: disableVal ? "#e96500" : "#00a854"
                                }
                            }),
                            h("strong", disableVal ? "已禁用" : "启用")
                        ]);
                    }
                }, 
                {
                    title: '营业执照',
                    key: 'license',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '执照过期时间',
                    key: 'licenseExp',
                    width: 120,
                    align: 'center',
                    render: (h, params) => {
                        return this.dateFormat(params.row.licenseExp);
                    }
                },
                {
                    title: '执照档案',
                    key: 'licenseFileNo',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '负责人',
                    key: 'employee',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '固定电话',
                    key: 'phone',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '传真',
                    key: 'fax',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '邮箱',
                    key: 'email',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '联系人',
                    key: 'contactUser',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '联系电话',
                    key: 'contactPhone',
                    width: 120,
                    align: 'center'
                },
                {
                    title: '邮编',
                    key: 'posecode',
                    width: 100,
                    align: 'center'
                },
                {
                    title: '地址',
                    key: 'address',
                    width: 200,
                    align: 'center'
                },
                {
                    title: '备注',
                    key: 'comment',
                    width: 200,
                    align: 'center'
                },
                {
                    title: '操作',
                    width: 120,
                    align: 'center',
                    fixed: 'right',
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'primary',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    this.updateShipBtnClick(params.row);
                                }
                            }
                        }, '修改')
                    }
                }
            ],
            showShipModal: false,
            shipModalTitle: '',
            shipFormItem: {
                id: '',
                name: '',
                disabled: false,
                license: '',
                licenseExp: '',
                licenseFileNo: '',
                employee: '',
                phone: '',
                fax: '',
                email: '',
                contactUser: '',
                contactPhone: '',
                posecode: '',
                address: '',
                comment: ''
            },
            shipValidate: {
                name: [
                    {required: true, message: '名称必输', trigger: 'blur'}
                ]
            },
            saveLoading: false
        }
    },
    methods: {
        dateFormat(data) {
          if (!data && isNaN(data)) {
              return '';
          }
          return dataConver.formatDate(new Date(data), 'yyyy-MM-dd');
        },

        searchBtnClick() {
            this.currentPage = 1;
            this.refreshShipList();
        },
        pageChange(data) {
            this.currentPage = data;
            this.refreshShipList();
        },
        refreshShipList() {
            let reqData = {
                name: this.searchForm.name,
                license: this.searchForm.license,
                page: this.currentPage,
                size: this.pageSize
            };
            this.searching = true;
            util.ajax.get("/ship/list", {params: reqData})
                .then((response) => {
                    this.tabData = response.data.data;
                    this.totalCount = response.data.count;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
            this.searching = false;
        },

        updateShipBtnClick(data) {
            this.showShipModal = true;
            this.shipModalTitle = '维护' + data.name + '信息';
            this.shipFormItem = data;
        },
        addBtnClick() {
            this.showShipModal = true;
            this.shipModalTitle = "新建承运公司信息";
            this.initShipFormData();
        },
        initShipFormData() {
            this.shipFormItem = {
                id: '',
                name: '',
                disabled: false,
                license: '',
                licenseExp: '',
                licenseFileNo: '',
                employee: '',
                phone: '',
                fax: '',
                email: '',
                contactUser: '',
                contactPhone: '',
                posecode: '',
                address: '',
                comment: ''
            };
        },
        shipModalClose() {
            this.showShipModal = false;
        },
        saveShipInfo() {
            this.$refs.shipForm.validate(valid => {
                if(!valid) {
                    this.$Message.warning('请检查必输项信息');
                    return;
                }
                this.saveLoading = true;
                util.ajax.post("/ship/save", this.shipFormItem)
                    .then((response) => {
                        this.$Message.success('保存成功');
                        this.showShipModal = false;
                        this.refreshShipList();
                    })
                    .catch((error) => {
                        util.errorProcessor(this, error);
                    });
                this.saveLoading = false;
            });
        }

    }
  
}
</script>

<style>
.ivu-form-item {
    margin-bottom: 5px;
}
.search-div {
    background-color: #fff;
    padding-top: 10px;
    padding-bottom: 10px;
}
.body-div {
    margin-top: 10px;
}

</style>


<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <Row>
            <Card>
                <p slot="title">
                    <Icon type="compose"></Icon>
                     {{ formItem.name || '商品详情' }}
                </p>

                <ButtonGroup slot="extra">
                    <Button type="primary" @click="submitGoods">提交</Button>
                </ButtonGroup>

                <Form :model="formItem" :rules="ruleValidate" :label-width="80">
                    <Row>
                        <Col span="6">
                            <FormItem label="通用名称" prop="name">
                                <Input v-model="formItem.name" placeholder="通用名称" @on-blur="onChangeName"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="商品类别" prop="categoryId">
                                <Select v-model="formItem.categoryId" filterable>
                                    <Option v-for="item in categoryList" :value="item.id" :key="item.id">{{ item.title }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="商品编号" prop="serial">
                                <Input v-model="formItem.serial" placeholder="例如 GO000198320138"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="货号" prop="code">
                                <Input v-model="formItem.code" placeholder="例如 73928192"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="6">
                            <FormItem label="规格" prop="spec">
                                <Input v-model="formItem.spec" placeholder="例如 15g*10袋*120"/>
                            </FormItem>
                        </Col>
                        <Col span="12">
                            <Row>
                                <Col span="18">
                                    <FormItem label="生产企业" prop="factory">
                                        <Select v-model="formItem.factoryId" placeholder="例如 某制药有限公司" filterable>
                                            <Option v-for="item in factoryList" :value="item.id" :label="item.name" :key="item.id">
                                                {{ item.name }}
                                            </Option>
                                        </Select>
                                    </FormItem>
                                </Col>
                                <Col span="5" class="padding-left-5">
                                    <Button type="success" icon="plus" @click="clickNewFactory">新建</Button>
                                </Col>
                            </Row>
                        </Col>
                        <Col span="6">
                            <FormItem label="产地" prop="origin">
                                <Input v-model="formItem.origin" placeholder="例如 陕西"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="6">
                            <FormItem label="计量单位" prop="unit">
                                <Select v-model="formItem.unit" placeholder="例如 盒" @on-change="onChangeUnit">
                                    <Option v-for="item in unitList" :value="item.id" :label="item.name" :key="item.id">
                                        {{ item.name }}
                                    </Option>
                                    <Option value="newUnit" key="newUnit" label="">
                                        <span>新增单位</span>
                                        <span style="float:right;color:#ccc"><i class="ivu-icon ivu-icon-share"></i></span>
                                    </Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="整件单位" prop="packUnit">
                                <Select v-model="formItem.packUnit" placeholder="例如 箱" @on-change="onChangeBigUnit">
                                    <Option v-for="item in unitList" :value="item.id" :label="item.name" :key="item.id">
                                        {{ item.name }}
                                    </Option>
                                    <Option value="newUnit" key="newUnit">
                                        <span>新增单位</span>
                                        <span style="float:right;color:#ccc"><i class="ivu-icon ivu-icon-share"></i></span>
                                    </Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="中件装量" prop="mediumPack">
                                <InputNumber :min="0" v-model="formItem.mediumPack" placeholder="例如 10"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="大件装量" prop="bigPack">
                                <InputNumber :min="0" v-model="formItem.bigPack" placeholder="例如 100"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="12">
                            <FormItem label="备注" prop="comment">
                                <Input v-model="formItem.comment"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="可销售">
                                <i-switch v-model="formItem.enable" size="large">
                                    <span slot="open">可用</span>
                                    <span slot="close">禁用</span>
                                </i-switch>
                            </FormItem>
                        </Col>
                    </Row>

                    <Tabs value="general" type="card">
                        <TabPane label="基本信息" name="general" icon="ios-paper">
                            <Row>
                                <Col span="6">
                                    <FormItem label="入库验收">
                                        <Checkbox v-model="formItem.inCheck"></Checkbox>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="首营检查">
                                        <Checkbox v-model="formItem.firstCheck"></Checkbox>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="特殊管理">
                                        <Checkbox v-model="formItem.specialManaged"></Checkbox>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="需要养护">
                                        <Checkbox v-model="formItem.needCare"></Checkbox>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="8">
                                    <FormItem label="商品名称" prop="fullName">
                                        <Input v-model="formItem.fullName" placeholder=""/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="拼音简码" prop="pinyin">
                                        <Input v-model="formItem.pinyin"/>
                                    </FormItem>
                                </Col>
                                <Col span="10">
                                    <FormItem label="批准文号" prop="permit">
                                        <Input v-model="formItem.permit">
                                        <Select value="gyz" slot="prepend" style="width: 100px">
                                            <Option value="gyz">国药准字Z</Option>
                                            <Option value="gyh">国药准字H</Option>
                                        </Select>
                                        </Input>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="6">
                                    <FormItem label="进项税率" prop="inTax">
                                        <InputNumber :min="0" v-model="formItem.inTax"/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="销项税率" prop="outTax">
                                        <InputNumber :min="0" v-model="formItem.outTax"/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="有效期限(月)" prop="validMonths">
                                        <InputNumber :min="1" v-model="formItem.validMonths"/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="预警天数" prop="warningDays">
                                        <InputNumber :min="0" v-model="formItem.warningDays"/>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="6">
                                    <FormItem label="储藏条件" prop="storageCondition">
                                        <Input v-model="formItem.storageCondition"/>
                                    </FormItem>
                                </Col>

                                <Col span="12">
                                    <FormItem label="联系方式" prop="contact">
                                        <Input v-model="formItem.contact"/>
                                    </FormItem>
                                </Col>
                            </Row>
                        </TabPane>
                        <TabPane label="扩展信息" name="expanded" icon="paperclip">
                            <Row>
                                <Col span="6">
                                    <FormItem label="注册商标" prop="brand">
                                        <Input v-model="formItem.brand"/>
                                    </FormItem>
                                </Col>

                                <Col span="6">
                                    <FormItem label="档案号" prop="archiveNumber">
                                        <Input v-model="formItem.archiveNumber"/>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="20">
                                    <FormItem label="主治功能" prop="cureRange">
                                        <Input v-model="formItem.cureRange"/>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="20">
                                    <FormItem label="销售政策" prop="salePolicy">
                                        <Input v-model="formItem.salePolicy"/>
                                    </FormItem>
                                </Col>
                            </Row>

                            <Row>
                                <Col span="4">
                                <FormItem label="处方药" prop="isPrescription">
                                    <i-switch v-model="formItem.isPrescription">
                                        <span slot="open">是</span>
                                        <span slot="close">否</span>
                                    </i-switch>
                                </FormItem>
                                </Col>

                                <Col span="4">
                                <FormItem label="进口药" prop="isForeign">
                                    <i-switch v-model="formItem.isForeign">
                                        <span slot="open">是</span>
                                        <span slot="close">否</span>
                                    </i-switch>
                                </FormItem>
                                </Col>

                                <Col span="4">
                                <FormItem label="中西药" prop="isTcm">
                                    <i-switch v-model="formItem.isTcm" size="large">
                                        <span slot="open">中药</span>
                                        <span slot="close">西药</span>
                                    </i-switch>
                                </FormItem>
                                </Col>
                            </Row>

                            <Row>
                                <Col span="6">
                                <FormItem label="特殊管理属性" prop="specialManageId">
                                    <Select v-model="formItem.factoryId" placeholder="例如 某制药有限公司" filterable>
                                        <Option v-for="item in factoryList" :value="item.id" :label="item.name" :key="item.id">
                                            {{ item.name }}
                                        </Option>
                                    </Select>
                                </FormItem>
                                </Col>
                                <Col span="2">
                                <Button type="ghost" icon="ios-settings-strong" @click="clickNewFactory"></Button>
                                </Col>

                                <Col span="6">
                                <FormItem label="剂型属性" prop="specialManageId">
                                    <Select v-model="formItem.factoryId" placeholder="例如 某制药有限公司" filterable>
                                        <Option v-for="item in factoryList" :value="item.id" :label="item.name" :key="item.id">
                                            {{ item.name }}
                                        </Option>
                                    </Select>
                                </FormItem>
                                </Col>
                                <Col span="2">
                                <Button type="ghost" icon="ios-settings-strong" @click="clickNewFactory"></Button>
                                </Col>
                            </Row>

                        </TabPane>
                        <TabPane label="证书信息" name="cert" icon="bookmark">证书信息</TabPane>
                        <TabPane label="价格信息" name="price" icon="pricetags">价格信息</TabPane>
                    </Tabs>
                </Form>
            </Card>
        </Row>

        <Modal v-model="unitModalShow" width="360">
            <p slot="header">
                <Icon type="ios-plus-outline"></Icon>
                <span>新增单位</span>
            </p>
            <div style="text-align:center">
                <Row>
                    <Input v-model="newUnitName" placeholder="单位名称" style="width: 200px"/>
                    <Button type="primary" @click="handleAddUnit">增加</Button>
                </Row>
                <Row class="margin-top-10 searchable-table-con1">
                    <Table :columns="unitColumns" :data="unitList"></Table>
                </Row>
            </div>
            <div slot="footer">
            </div>
        </Modal>
    </div>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'goods-info',
    data () {
        return {
            newUnitName: '',
            unitModalShow: false,
            formItem: {
                inTax: 16,
                outTax: 16
            },
            categoryList: [],
            factoryList: [],
            unitList: [],
            factoryMap: {},
            unitColumns: [
                {
                    key: 'name',
                    title: '单位'
                },
                {
                    key: 'action',
                    title: '操作',
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'error',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    var self = this;
                                    util.ajax.post('/unit/remove/' + params.row.id)
                                        .then(function (response) {
                                            self.loadUnitList();
                                        })
                                        .catch(function (error) {
                                            console.log(error);
                                        });
                                }
                            }
                        }, '删除');
                    }
                }
            ],
            ruleValidate: {
                name: [
                    {required: true, message: '通用名称不能为空', trigger: 'blur'}
                ],
                spec: [
                    {required: true, message: '规格不能为空', trigger: 'blur'}
                ],
                factoryId: [
                    {required: true, message: '生产企业不能为空', trigger: 'blur'}
                ]
            }
        };
    },
    methods: {
        loadUnitList () {
            var self = this;
            util.ajax.get('/unit/list')
                .then(function (response) {
                    self.unitList = response.data;
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        init () {
            var self = this;
            this.loadUnitList();

            util.ajax.get('/good/category/tree')
                .then(function (response) {
                    self.categoryList = response.data;
                })
                .catch(function (error) {
                    console.log(error);
                });
            util.ajax.get('/factory/list')
                .then(function (response) {
                    var factoryArr = [];
                    if (response.data) {
                        for (var i = 0; i < response.data.length; i++) {
                            var factory = response.data[i];
                            factoryArr.push({
                                name: factory.name,
                                id: factory.id
                            });
                            self.factoryMap[factory.id] = factory.name;
                        }
                    }
                    self.factoryList = factoryArr;
                })
                .catch(function (error) {
                    console.log(error);
                });

            if (this.$route.params && this.$route.params.goods_id) {
                util.ajax.get('/goods/' + this.$route.params.goods_id)
                    .then(function (response) {
                        self.formItem = response.data;
                        util.setCurrentPageTitle(self, response.data.name, true);
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            } else {
                this.formItem = {};
            }
        },
        submitGoods () {
            var self = this;
            util.ajax.post('/goods/add', this.formItem)
                .then(function (response) {
                    self.$Message.info('保存商品成功');
                })
                .catch(function (error) {
                    console.log(error);
                });
        },
        clickNewFactory () {
            let argu = { factory_id: 0, closeOnAdd: true };
            this.$router.push({
                name: 'factory-info',
                params: argu
            });
        },
        onChangeName () {
            if (this.formItem.name && this.formItem.name.length > 0) {
                if (!this.formItem.fullName) {
                    this.formItem.fullName = this.formItem.name;
                }
                util.setCurrentPageTitle(this, this.formItem.name, true);
                var self = this;
                util.ajax.post('/util/pinyinAbbr', { name: this.formItem.name })
                    .then(function (response) {
                        self.formItem.pinyin = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        },
        onChangeUnit (item) {
            if (item && item === 'newUnit') {
                this.newUnitName = '';
                this.unitModalShow = true;
                this.formItem.unit = -1;
            }
        },
        onChangeBigUnit (item) {
            if (item && item === 'newUnit') {
                this.newUnitName = '';
                this.unitModalShow = true;
                this.formItem.packUnit = -1;
            }
        },
        handleAddUnit () {
            var self = this;
            util.ajax.post('/unit/add', { name: this.newUnitName })
                .then(function (response) {
                    self.unitModalShow = false;
                    self.loadUnitList();
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
    },
    mounted () {
        this.init();
    },
    activated () {
        this.init();
    }
};
</script>

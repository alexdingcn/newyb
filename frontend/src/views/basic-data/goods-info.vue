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
                    <Button type="primary" :loading="savebtnLoading" @click="submitGoods">提交</Button>
                </ButtonGroup>

                <Form :model="formItem" :rules="ruleValidate" :label-width="90">
                    <Row>
                        <Col span="6">
                            <FormItem label="通用名称" prop="name">
                                <Input v-model="formItem.name" placeholder="通用名称" @on-blur="onChangeName"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="商品类别" prop="categoryId">
                                <Select v-model="formItem.categoryId" filterable>
                                    <Option v-for="item in categoryList" :value="item.id" :key="item.id">{{ item.name }}</Option>
                                </Select>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="货号" prop="code">
                                <Input v-model="formItem.id" disabled placeholder="系统自动生成"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="条码" prop="serial">
                                <Input v-model="formItem.serial" placeholder="例如 GO000198320138"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="6">
                            <FormItem label="规格" prop="spec">
                                <Input v-model="formItem.spec" placeholder="例如 15g*10袋*120"/>
                            </FormItem>
                        </Col>
                        <Col span="10">
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
                        <Col span="8">
                            <FormItem label="产地" prop="origin">
                                <Input v-model="formItem.origin" placeholder="例如 陕西"/>
                            </FormItem>
                        </Col>
                    </Row>
                    <Row>
                        <Col span="6">
                            <FormItem label="计量单位" prop="unit">
                                <option-select v-model="formItem.unit" optionType='GOODS_UNIT' ></option-select>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="整件单位" prop="packUnit">
                                <option-select v-model="formItem.packUnit" optionType='GOODS_UNIT' ></option-select>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="中件装量" prop="mediumPack">
                                <InputNumber :min="0" v-model="formItem.mediumPack" placeholder="例如 10"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="大件装量" prop="bigPack">
                                <InputNumber :min="0"  v-model="formItem.bigPack" placeholder="例如 100"/>
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
                            <FormItem label="是否可用">
                                <i-switch v-model="formItem.enable" size="large">
                                    <span slot="open">可用</span>
                                    <span slot="close">禁用</span>
                                </i-switch>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="代销">
                                <i-switch v-model="formItem.proxy" size="large">
                                    <span slot="open">是</span>
                                    <span slot="close">否</span>
                                </i-switch>
                            </FormItem>
                        </Col>
                    </Row>

                    <Tabs value="general" type="card" style="height: 450px;">
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
                                <Col span="6">
                                    <FormItem label="商品名称" prop="fullName">
                                        <Input v-model="formItem.fullName" placeholder=""/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="拼音简码" prop="pinyin">
                                        <Input v-model="formItem.pinyin"/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="批准文号" prop="permitNo">
                                        <Input v-model="formItem.permitNo"></Input>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="6">
                                    <FormItem label="注册商标" prop="brandNo">
                                        <Input v-model="formItem.brandNo"/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="档案号" prop="archiveNo">
                                        <Input v-model="formItem.archiveNo"/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="储藏条件" prop="storageCondition">
                                        <optionSelect v-model="formItem.storageCondition" optionType='STORAGE_METHOD' ></optionSelect>
                                    </FormItem>
                                </Col>

                                <Col span="6">
                                    <FormItem label="联系方式" prop="contact">
                                        <Input v-model="formItem.contact"/>
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
                        </TabPane>
                        <TabPane label="扩展信息" name="expanded" icon="paperclip">
                            
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
                                <Col span="5">
                                    <FormItem label="处方/非处方" prop="prescriptionId">
                                        <option-select v-model="formItem.prescriptionId" optionType="PERSCRIPTION" ></option-select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="进口药" prop="isForeign">
                                        <Select v-model="formItem.isForeign">
                                            <Option :value="1">进口</Option>
                                            <Option :value="0">国产</Option>
                                        </Select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="中/西药属性" prop="medTypeId">
                                        <option-select v-model="formItem.medTypeId" optionType="GOODS_MED_TYPE" ></option-select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="社保目录" prop="shebao">
                                        <Select v-model="formItem.shebao">
                                            <Option :value="1">社保目录药</Option>
                                            <Option :value="0">非社保目录药</Option>
                                        </Select>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="5">
                                    <FormItem label="特殊管理属性" prop="specificMedId">
                                        <option-select v-model="formItem.specificMedId" optionType="SPECIAL_MED" ></option-select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="剂型属性" prop="jxId">
                                        <option-select v-model="formItem.jxId" optionType="GOODS_JX" ></option-select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="药基属性" prop="baseMedId">
                                        <option-select v-model="formItem.baseMedId" optionType="GOODS_BASE_MED" ></option-select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="功能分类属性" prop="funcCatId">
                                        <option-select v-model="formItem.funcCatId" optionType="GOODS_FUNC_CAT" ></option-select>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="5">
                                    <FormItem label="给药途径属性" prop="medicationId">
                                        <option-select v-model="formItem.medicationId " optionType="GOODS_MEDICATION" ></option-select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="养护标志属性" prop="careTimeId">
                                        <option-select v-model="formItem.careTimeId" optionType="GOODS_CARE_TIME" ></option-select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="GMP认证属性" prop="gmpTypeId">
                                        <option-select v-model="formItem.gmpTypeId" optionType="GOODS_GMP_TYPE" ></option-select>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="ABC属性" prop="abcTypeId">
                                        <option-select v-model="formItem.abcTypeId" optionType="GOODS_ABC_TYPE" ></option-select>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="5">
                                    <FormItem label="经营范围属性" prop="scopeId">
                                        <option-select v-model="formItem.scopeId" optionType="GOODS_SCOPE" ></option-select>
                                    </FormItem>
                                </Col>
                                
                                <Col span="5">
                                    <FormItem label="新特药属性" prop="newTypeId">
                                        <option-select v-model="formItem.newTypeId" optionType="GOODS_NEW_TYPE" ></option-select>
                                    </FormItem>
                                </Col>
                            </Row>

                        </TabPane>
                        <TabPane label="证书信息" name="cert" icon="bookmark" >
                            <Row>
                                <Col span="1">
                                    <strong>注册证书:</strong>
                                </Col>
                                <Col span="5">
                                    <FormItem label="注册证书编号" prop="certNo">
                                        <Input v-model="formItem.certNo"/>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="证书有效期至" prop="certExpDate">
                                        <DatePicker type="date" v-model="formItem.certExpDate"></DatePicker>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="证书档案编号" prop="certFileNo">
                                        <Input v-model="formItem.certFileNo">
                                            <Button slot="append" type="text" icon="edit" @click="certFileInfo(formItem.certFileNo)"></Button>
                                        </Input>
                                    </FormItem>
                                </Col>
                            </Row>
                            <hr style="width:75%; margin-bottom: 15px;" size="2"/>
                            <Row>
                                <Col span="1">
                                    <strong >注册商标: </strong>
                                </Col>
                                <Col span="5">
                                    <FormItem label="商标编号" prop="brandNo">
                                        <Input v-model="formItem.brandNo"/>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="证书有效期至" prop="certExpDate">
                                        <DatePicker type="date" v-model="formItem.brandExpDate"></DatePicker>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="证书档案编号" prop="brandFileNo">
                                        <Input  v-model="formItem.brandFileNo">
                                            <Button slot="append" type="text" icon="edit" @click="brandFileInfo(formItem.brandFileNo)"></Button>
                                        </Input>
                                    </FormItem>
                                </Col>
                            </Row>
                            <hr style="width:75%; margin-bottom: 15px;" size="2"/>
                            <Row >
                                <Col span="1">
                                    <strong >批注文号: </strong>
                                </Col>
                                <Col span="5">
                                    <FormItem label="批注文号" prop="permitNo">
                                        <Input v-model="formItem.permitNo"/>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="证书有效期至" prop="certExpDate">
                                        <DatePicker type="date" v-model="formItem.permitExpDate"></DatePicker>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="证书档案编号" prop="permitFileNo">
                                        <Input v-model="formItem.permitFileNo" >
                                            <Button slot="append" type="text" icon="edit" @click="permitFileInfo(formItem.permitFileNo)"></Button>
                                        </Input>
                                    </FormItem>
                                </Col>
                            </Row>
                        </TabPane>
                        <TabPane label="价格信息" name="price" icon="pricetags">
                            <Row>
                                <Col span="5">
                                    <FormItem label="零售价" prop="retailPrice">
                                        <Input number v-model="formItem.retailPrice"/>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="批发价" prop="batchPrice">
                                        <Input number v-model="formItem.batchPrice" />
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="会员价" prop="memberPrice">
                                        <Input number v-model="formItem.memberPrice"/>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="挂网价" prop="onlinePrice">
                                        <Input number v-model="formItem.onlinePrice"/>
                                    </FormItem>
                                </Col>
                            </Row>
                            <Row>
                                <Col span="5">
                                    <FormItem label="拆零价" prop="splitPrice">
                                        <Input number v-model="formItem.splitPrice"/>
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="最低价" prop="lowPrice">
                                        <Input number v-model="formItem.lowPrice" />
                                    </FormItem>
                                </Col>
                                <Col span="5">
                                    <FormItem label="最高限价" prop="hightPrice">
                                        <Input number v-model="formItem.hightPrice"/>
                                    </FormItem>
                                </Col>
                            </Row>
                        </TabPane>
                    </Tabs>
                </Form>
            </Card>
        </Row>

        <Modal v-model="fileUploadModal" :title="uploadModalTitle" :mask-closable="false" width="50" class="file-upload-modal">
            <file-detail :fileNo="uploadFileNo" @add-file-success="addFileSuccess" ></file-detail>
            <div slot="footer"></div>
        </Modal>

    </div>
</template>

<script>
import util from '@/libs/util.js';
import optionSelect from "../selector/option-select.vue";
import fileDetail from "@/views/basic-data/file-detail.vue";

export default {
    name: 'goods-info',
    components: {
        optionSelect,
        fileDetail
    },
    props: {
        goodsId: {
            type:Number|String,
            default: ''
        }
    },
    data () {
        return {
            savebtnLoading: false,
            formItem: {
                inTax: 16,
                outTax: 16
            },
            categoryList: [],
            factoryList: [],
            factoryMap: {},
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
            },
            fileUploadType: '',
            fileUploadModal: false,
            uploadModalTitle: '档案信息',
            uploadFileNo: ''

        };
    },
    watch: {
        goodsId: function() {
            this.loadGoods();
        }
    },
    methods: {
        init () {
            var self = this;
            util.ajax.get('/good/category/list')
                .then(function (response) {
                    self.categoryList = response.data;
                })
                .catch(function (error) {
                    util.errorProcessor(self, error);
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
                    util.errorProcessor(self, error);
                });
        },

        loadGoods() {
            let self = this;
            if (self.goodsId) {
                util.ajax.get('/goods/' + self.goodsId)
                    .then(function (response) {
                        self.formItem = response.data;
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            } else {
                self.formItem = {};
            }
        },

        submitGoods () {
            var self = this;
            self.savebtnLoading = true;
            util.ajax.post('/goods/save', this.formItem)
                .then(function (response) {
                    self.savebtnLoading = false;
                    self.$Message.success('保存商品成功');
                    self.$emit('save-ok');
                })
                .catch(function (error) {
                    self.savebtnLoading = false;
                    util.errorProcessor(self, error);
                });
        },
        clickNewFactory () {
            let argu = { factory_id: 0, closeOnAdd: true };
            this.$router.push({
                name: 'factory',
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
                        util.errorProcessor(self, error);
                    });
            }
        },

        addFileSuccess(data) {
            if (this.fileUploadType === 'brand') {
                this.formItem.brandFileNo = data.fileNo;
            }
            else if (this.fileUploadType === "cert") {
                this.formItem.certFileNo = data.fileNo;
            }
            else if (this.fileUploadType == "permit") {
                this.formItem.permitFileNo = data.fileNo;
            }
            else {
                this.$Message.error('系统关联错误.')
            }
        },

        certFileInfo(fileNo) {
            this.uploadFileNo = ''; //先清理一次
            this.fileUploadType = "cert";
            this.uploadModalTitle = '注册证书档案',
            this.uploadFileNo = fileNo;
            this.fileUploadModal = true;
        },

        brandFileInfo(fileNo) {
            this.uploadFileNo = ''; //先清理一次
            this.fileUploadType = "brand";
            this.uploadModalTitle = '注册商标档案',
            this.uploadFileNo = fileNo;
            this.fileUploadModal = true;
        },

        permitFileInfo(fileNo) {
            this.uploadFileNo = ''; //先清理一次
            this.fileUploadType = "permit";
            this.uploadModalTitle = '批准文号档案',
            this.uploadFileNo = fileNo;
            this.fileUploadModal = true;
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

<style>

.ivu-form-item {
    margin-bottom: 20px;
}

.file-upload-modal {
    position: fixed;
    z-index: 3000;
}

</style>


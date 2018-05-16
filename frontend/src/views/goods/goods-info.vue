<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <Form ref="form" :model="formData" :rules="formRules" :label-width="90">
            <Tabs value="general" :animated="false">
                <div slot="extra">
                    <ButtonGroup size="small">
                        <Button type="success" icon="checkmark" :loading="saveLoading" @click="saveGoodsInfo" >确认保存</Button>
                    </ButtonGroup>
                </div>
                    <TabPane icon="cube" name="general" label="基础信息">
                        <h2 style="margin-bottom: 10px;">商品基础信息</h2>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="商品名称" prop="name">
                                    <Input type="text" v-model="formData.name" @on-blur="onChangeName" />
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="商品编号" prop="goodsNo">
                                    <Input type="text" v-model="formData.goodsNo" placeholder="由系统自动生成" disabled/>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="商品类别" prop="categoryId">
                                    <goods-category-select v-model="formData.categoryId"></goods-category-select>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="商品品牌" prop="brandId">
                                    <goods-brand-select v-model="formData.brandId" ></goods-brand-select>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="拼音" prop="pinyin">
                                    <Input type="text" v-model="formData.pinyin" />
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="生产企业" prop="factoryId">
                                    <factory-select v-model="formData.factoryId"></factory-select>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="产地" prop="origin">
                                    <Input type="text" v-model="formData.origin" />
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="供应商" prop="supplierId">
                                    <supplier-select v-model="formData.supplierId"></supplier-select>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
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
                            <i-col span="8">
                                <FormItem label="备注" prop="comment">
                                    <Input type="text" v-model="formData.comment" placeholder="备注信息"/>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="12">
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
                            <i-col span="8">
                                <FormItem label="小单位" prop="unit">
                                    <option-select placement="top" v-model="formData.unit" optionType='GOODS_UNIT' ></option-select>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="条形码" prop="barCode">
                                    <Input type="text" v-model="formData.barCode" placeholder="最小单位的条形码"/>
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="中件单位" prop="mediumUnit">
                                    <option-select placement="top" v-model="formData.mediumUnit" optionType='GOODS_UNIT' ></option-select>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
                                <FormItem label="中件单位换算" prop="mediumPack">
                                    <InputNumber :min="0" v-model="formData.mediumPack"  style="width: 100%;" />
                                </FormItem>
                            </i-col>
                        </Row>
                        <Row class="row-margin-bottom">
                            <i-col span="8">
                                <FormItem label="大件单位" prop="packUnit">
                                    <option-select placement="top" v-model="formData.packUnit" optionType='GOODS_UNIT' ></option-select>
                                </FormItem>
                            </i-col>
                            <i-col span="8">
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
                                <FormItem label="批发价" :label-width="100" prop="useSpec">
                                     <InputNumber :min="0" v-model="formData.batchPrice" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="市场价" :label-width="100" prop="useSpec">
                                     <InputNumber :min="0" v-model="formData.retailPrice" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                            <i-col span="6">
                                <FormItem label="参考进货价" :label-width="100" prop="useSpec">
                                     <InputNumber :min="0" v-model="formData.inPrice" style="width: 100%;" />
                                </FormItem>
                            </i-col>
                        </Row>

                        <div v-if="formData.useSpec" >
                            <Alert type="succuess" v-if="goodsSpesList.length<=0">
                                <strong>温馨提示: </strong>还没有维护商品多规格配置，请先维护商品多规格配置信息!
                            </Alert>
                            <Row class="row-margin-bottom" >
                                <FormItem label="添加商品规格">
                                    <Select v-model="chooseParentValue" placement="top" style="width: 250px" @on-change="chooseParentSpecChange">
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
                    <TabPane icon="ios-pricetags" name="attribute" label="自定义属性">
                        <p>自定义属性</p>
                    </TabPane>
                    <TabPane icon="document-text" name="file" label="档案管理">
                        <p>档案管理</p>
                    </TabPane>
                    <!-- <TabPane icon="medkit" name="medication" label="药品扩展">
                        <p>药品扩展</p>
                    </TabPane> -->
            </Tabs>
        </Form>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import goodsCategorySelect from '@/views/selector/goods-category-select.vue';
import goodsBrandSelect from '@/views/selector/goods-brand-select.vue';
import factorySelect from '@/views/selector/factory-select.vue';
import supplierSelect from '@/views/selector/supplier-select.vue';
import optionSelect from '@/views/selector/option-select.vue';

export default {
    name: 'goods-info',
    props: {
        goodsInfoId: {
            type: String|Number,
            default: ''
        }
    },
    components: {
        goodsCategorySelect,
        goodsBrandSelect,
        factorySelect,
        supplierSelect,
        optionSelect
    },
    data() {
        return {
            saveLoading: false,
            formData: {
                name: '',
                goodsNo: '',
                pinyin: '',
                enable: 1,
                useSpec: false,
                batchPrice: 0,
                retailPrice: 0,
                inPrice: 0,
                goodsDetails: [],
            },
            formRules: {
                name: [
                    {required: true, message: '商品名称不能为空', trigger: 'blur'}
                ],
                unit: [
                    {required: true, type: 'number', message: '商品最小单位不能为空', trigger: 'blur'}
                ]
            },
            goodsSpesList: [],
            chooseParentValue:'',
            currParentSpecs: [],
            currSpecIds: [[], [], []],
            goodsDetails: [],
            goodsDetailColumn: [],
        }
    },
    mounted() {
        this.loadGoodsSpecs();
    },
    watch: {
        goodsInfoId: function(id) {
            if(id && id > 0) {
                this.loadGoodsInfo(id);
            }
        }
    },
    methods: {
        loadGoodsInfo(id) {
            util.ajax.get('/goods/' + id)
                .then((response) => {
                    let data = response.data;
                    data.enable = data.enable ? 1 : 0;
                    this.formData = data;
                    if(this.formData.useSpec) {
                        this.editShowSpecForm(this.formData.goodsDetails);
                    }
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
            
        },
        loadGoodsSpecs() {
            util.ajax.get('/goods/spec/list')
                .then((response) => {
                    this.goodsSpesList = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        },
        gotoGoodsSpecPage() {
            this.$router.push({
                name: 'goods-spec'
            });
        },
        onChangeName() {
            if (this.formData.name && this.formData.name.length > 0) {
                var self = this;
                util.ajax.post('/util/pinyinAbbr', { name: this.formData.name })
                    .then(function (response) {
                        self.formData.pinyin = response.data;
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            }
        },

        editShowSpecForm(details) {
            //根据规格详情，制造出规格表和选择标签
            this.currParentSpecs = [];

            // TODO 

        },
        getSpecFormItemBySubId(id) {
            if (!id || id<=0) {
                return {};
            }
            for (let i=0; i< this.goodsSpesList.length; i++) {
                let temp = this.goodsSpesList[i];
                if (temp.subGoodsSpecs.length > 0) {
                    for (let j=0; j<temp.subGoodsSpecs.length; j++) {
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
            for (let i=0; i<this.goodsSpesList.length; i++) {
                let temp = this.goodsSpesList[i];
                if (parentId === temp.parentId) {
                    return temp;
                }
            }
            return {};
        },
        chooseParentSpecChange(parentId) {
            let specForm = this.getSpecFormItemByParentId(parentId);
            if(!specForm || !specForm.parentId) {
                this.chooseParentValue = '';
                return;
            }
            //判断是否已经存在已选择的规格中
            for (let i=0; i<this.currParentSpecs.length; i++) {
                let item = this.currParentSpecs[i];
                if (parentId === item.parentId) {
                    this.chooseParentValue = '';
                    return; //已经选择过了，不需要不能再次选择
                }
            }
            if (this.currParentSpecs.length >= 3) {
                this.chooseParentValue = '';
                this.$Message.info('规格配置最多能加三层');
                return;
            }
            this.currParentSpecs.push(specForm);
            this.goodsDetailColumn = this.makeSpecTableColumns();
        },
        specTagChose(event, parentId) {
            if(!parentId) {
                return;
            }
            for (let i=0; i<this.currParentSpecs.length; i++) {
                let item = this.currParentSpecs[i];
                if(parentId === item.parentId) {
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
            this.goodsDetails = this.makeSpecTableData();
        },

        makeSpecTableColumns() {
            let culumns = [];
            let self = this;
            if (this.currParentSpecs.length <=0) {
                culumns = [];
                return;
            }
            let indexItem = {
                title: '序号',
                type: 'index',
                widht: 60
            };
            culumns.push(indexItem);
            for (let i=0; i<this.currParentSpecs.length; i++) {
                let item = this.currParentSpecs[i];
                if (i === 0) {
                    let temp = {
                        title: item.parentName,
                        key: 'specOneId',
                        width: 100,
                        render: (h, params) => {
                            return self.tableSpecRender(h, params.row.specOneId);
                        }
                    };
                    culumns.push(temp);
                }else if(i===1) {
                    let temp = {
                        title: item.parentName,
                        key: 'specTwoId',
                        width: 100,
                        render: (h, params) => {
                            return self.tableSpecRender(h, params.row.specTwoId);
                        }
                    };
                    culumns.push(temp);
                }else if(i===2) {
                    let temp = {
                        title: item.parentName,
                        key: 'specThreeId',
                        width: 100,
                        render: (h, params) => {
                            return self.tableSpecRender(h, params.row.specThreeId);
                        }
                    };
                    culumns.push(temp);
                }
            }
            culumns.push({
                title: '条码',
                key: 'barCode',
                widht: 150,
                render: (h, params) => {
                    return h('Input', {
                        props: {
                            value: self.goodsDetails[params.index][params.column.key]
                        },
                        on: {
                            'on-blur' (event) {
                                let row = self.goodsDetails[params.index];
                                row[params.column.key] = event.target.value;
                            }
                        }
                    });
                }
            });
            culumns.push({
                title: '批发价',
                key: 'batchPrice',
                widht: 120,
                render: (h, params) => {
                    return self.inputRender(h, params);
                }
            });
            culumns.push({
                title: '市场价',
                key: 'retailPrice',
                widht: 120,
                render: (h, params) => {
                    return self.inputRender(h, params);
                }
            });
            culumns.push({
                title: '参考进货价',
                key: 'inPrice',
                widht: 120,
                render: (h, params) => {
                    return self.inputRender(h, params);
                }
            });
            return culumns;
        },

        tableSpecRender(h, id) {
            let item = {};
            let label = '';
            for (let i=0; i<this.currParentSpecs.length; i++) {
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
            return h('span', label);
        },

        inputRender (h, params) {
            let self = this;
            return h('InputNumber', {
                props: {
                    min: 0,
                    value: self.goodsDetails[params.index][params.column.key]
                },
                on: {
                    'on-blur' (event) {
                        let row = self.goodsDetails[params.index];
                        row[params.column.key] = event.target.value;
                    }
                }
            });
        },

        //展开选择的规格明细展开成一个表格数据
        makeSpecTableData() {
            let specTableData = [];
            if(this.currParentSpecs.length <= 0) {
                specTableData = [];
                return;
            }
            for (let i=0; i<this.currParentSpecs.length; i++) {
                let idList = this.currSpecIds[i];
                
                if (!idList || idList.length<=0) {
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
                        specTwoId: '',
                        specThreeId: '',
                        barCode: '',
                        batchPrice: self.formData.batchPrice ? self.formData.batchPrice : 0 ,
                        retailPrice: self.formData.retailPrice ? self.formData.retailPrice : 0 ,
                        inPrice: self.formData.inPrice ? self.formData.inPrice : 0 ,
                    };
                    return item;
                });
                resultList = [...datailList];
                return resultList;
            }else {
                let tempList = [];
                for (let i=0; i<resultList.length; i++ ) {
                    let result = resultList[i];
                    for (let j=0; j<idList.length; j++) {
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
                        }else if (index === 2){
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
            this.$refs.form.validate(valite => {
                if(!valite) {
                    return;
                }else {
                    self.$Modal.confirm({
                        title: '提交保存确认',
                        content: '是否确认商品信息已经维护正确？',
                        onOk: () => {
                            self.saveLoading = true;
                            self.formData.goodsDetails = self.goodsDetails;
                            util.ajax.post('/goods/save', self.formData)
                                .then((response) => {
                                    self.saveLoading = false;
                                    self.$Message.success('商品信息保存成功');
                                    self.$emit('save-ok');
                                })
                                .catch((error) => {
                                    self.saveLoading = false;
                                    util.errorProcessor(self, error);
                                });
                        }
                    });
                }
            });
        }

    }
  
}
</script>


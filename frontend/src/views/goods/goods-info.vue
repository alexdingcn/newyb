<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div>
        <Form ref="form" :model="formData" :rules="formRules" :label-width="90">
            <Tabs value="general" :animated="false">
                <div slot="extra">
                    <ButtonGroup size="small">
                        <Button type="success" icon="checkmark" >确认保存</Button>
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
                                <FormItem label="上架下架" prop="status">
                                    <RadioGroup v-model="formData.status">
                                         <Radio label="ON-SALE">
                                            <span>上架</span>
                                        </Radio>
                                        <Radio label="OFF-SALE">
                                            <span>下架</span>
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
                                <FormItem label="商品标签" prop="tags">
                                    <CheckboxGroup v-model="formData.tags">
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
                        <Row v-if="!formData.useSpec" class="row-margin-bottom">
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
                            <Row  class="row-margin-bottom">
                                <i-col spap=20>
                                    <FormItem label="选择规格" :label-width="100">
                                        <Select placement="top" v-model="useParentSpecIdList" multiple style="width: 50%;" @on-change="addGoodsSpec">
                                            <Option v-for="item in goodsSpesList" :value="item.parentId" :key="item.parentId">{{ item.parentName }}</Option>
                                        </Select>
                                    </FormItem>
                                </i-col>
                            </Row>
                            <Row v-for="(item, index) in useParentSpecList" :key="item.parentId" class="row-margin-bottom">
                                <FormItem :label="item.parentName" :label-width="100">
                                    <CheckboxGroup v-model="chooseSpecList[index]" >
                                        <Checkbox v-for="subItem in item.subGoodsSpecs" :key="subItem.id" :label="subItem">
                                            <span>{{subItem.specName}}</span>
                                        </Checkbox>
                                    </CheckboxGroup>
                                </FormItem>
                            </Row>


                        </div>
                    </TabPane>
                    <TabPane icon="ios-pricetags" name="attribute" label="自定义属性">
                        <p>自定义属性</p>
                    </TabPane>
                    <TabPane icon="document-text" name="file" label="档案管理">
                        <p>档案管理</p>
                    </TabPane>
                    <TabPane icon="medkit" name="medication" label="药品扩展">
                        <p>药品扩展</p>
                    </TabPane>
                
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
            formData: {
                name: '',
                goodsNo: '',
                pinyin: '',
                status: 'ON_SALE',
                useSpec: false,
                specList: []
            },
            formRules: {},
            goodsSpesList: [],
            useParentSpecIdList: [],
            useParentSpecList: [],
            chooseSpecList: [[], [], []]
        }
    },
    mounted() {
        this.loadGoodsSpecs();
    },
    watch: {
        chooseSpecList: function (values) {
            console.log(values);
            let specForms = [];
            if(values.length <= 0) {
                return;
            }
            
        }
    },
    methods: {
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
        addGoodsSpec(valuse) {
            if(this.useParentSpecIdList.length <= 3) {
                let tempList = [];
                for(let i=0;i<this.goodsSpesList.length; i++) {
                    let item = this.goodsSpesList[i];
                    if (valuse.indexOf(item.parentId) >= 0) {
                         tempList.push(item);
                    }
                }
                this.useParentSpecList = tempList;
            }else {
                //最多三层
                this.useParentSpecIdList.splice(this.useParentSpecIdList.length-1, 1); //去除最后一个
                this.$Message.info('产品多规格最多三层');
            }
        },
        
        
    }
  
}
</script>


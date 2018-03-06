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
                                <Input v-model="formItem.name" placeholder="通用名称"/>
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
                            <FormItem label="生产企业" prop="factory">
                                <Input v-model="formItem.factory" placeholder="例如 某制药有限公司"/>
                            </FormItem>
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
                                <Input v-model="formItem.unit" placeholder="例如 盒"/>
                            </FormItem>
                        </Col>
                        <Col span="6">
                            <FormItem label="整件单位" prop="packUnit">
                                <Input v-model="formItem.packUnit" placeholder="例如 箱"/>
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
                        <TabPane label="基本信息" name="general">
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
                                <Col span="12">
                                    <FormItem label="商品名称">
                                        <Input v-model="formItem.fullName" placeholder=""/>
                                    </FormItem>
                                </Col>
                                <Col span="6">
                                    <FormItem label="拼音简码">
                                        <Input v-model="formItem.pinyin"/>
                                    </FormItem>
                                </Col>
                            </Row>
                        </TabPane>
                        <TabPane label="扩展信息" name="expanded">标签二的内容</TabPane>
                        <TabPane label="证书信息" name="cert">证书信息</TabPane>
                        <TabPane label="价格信息" name="price">价格信息</TabPane>
                    </Tabs>
                </Form>
            </Card>
        </Row>


    </div>
</template>

<script>
import axios from 'axios';
import util from '@/libs/util.js';

export default {
    name: 'goods-info',
    data () {
        return {
            formItem: {},
            categoryList: [],
            ruleValidate: {
                name: [
                    {required: true, message: '通用名称不能为空', trigger: 'blur'}
                ],
                spec: [
                    {required: true, message: '规格不能为空', trigger: 'blur'}
                ],
                factory: [
                    {required: true, message: '生产企业不能为空', trigger: 'blur'}
                ],
            }
        };
    },
    methods: {
        init () {
            var self = this;
            if (this.$route.params && this.$route.params.goods_id) {
                util.ajax.get('/goods/' + this.$route.params.goods_id)
                    .then(function (response) {
                        self.formItem = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            } else {
                this.formItem = {};
            }
        },
        submitGoods() {
            var self = this;
            util.ajax.post('/goods/add', this.formItem)
                    .then(function (response) {
                        self.$Message.info("创建成功");
                    })
                    .catch(function (error) {
                        console.log(error);
                    })
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

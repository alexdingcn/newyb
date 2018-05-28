<style lang="less">
@import "../../../styles/common.less";
@import "../config.less";
</style>

<template>
    <Card >
        <p slot="title">公司类型配置信息</p>
        <div slot="extra"> 
        </div>

        <Form ref="form" :model="formData" :label-width="90">
                <Alert type="success" class="alert-desc">
                    <p>公司类型配置信息, 主要是表示公司的主要营业类型信息，控制商品中的一些特殊数据.</p>
                    <p>例如：如果选择了“医药经营类”, 则在商品中配置信息中，会多出一个药品特殊配置信息，如“是否冷链标识”、“是否特殊监管药品”.</p>
                    <p>“冷链经营”如果选择是，会影响到采购和销售收货出货时，必须要登记对应的“温控条件”、“收货/出货温度”;</p>
                    <p>“是否特殊监管药品”如果选择是，则在采购收货和销售出库时，流程上必须是双人审核通过够，才能算审核通过;</p>
                </Alert>
                <Row>
                    <FormItem label="公司类型">
                        <RadioGroup v-model="formData.keyValue">
                            <Radio label="medicine">
                                <span>药品经营类</span>
                            </Radio>
                            <Radio label="others">
                                <span>其他</span>
                            </Radio>
                        </RadioGroup>
                    </FormItem>
                    <Spin size="large" fix v-if="saveLoading"></Spin>
                </Row>
        </Form>

        <Row class="button-footer">
            <ButtonGroup>
                <Button type="success" icon="checkmark" :loading="saveLoading" @click="save">保存</Button>
                <Button type="ghost" icon="reply" @click="back">返回</Button>
            </ButtonGroup>
        </Row>

        <hr size="1" color="#4d515a" class="desc-split-line" />

    </Card>
    
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'configCompanyType',
    props: {},
    components: {},
    data() {
        return {
            formData: {
                keyName: 'COMPANY_TYPE',
                keyValue: 'others'
            },
            saveLoading: false
        }
    },
    methods: {
        init() {
            this.saveLoading = true;
            util.ajax.get('/config/list')
                .then((response) => {
                    this.saveLoading = false;
                    let data = response.data;
                    let valueInfo = data['COMPANY_TYPE'];
                    if (valueInfo) {
                        this.formData = valueInfo;
                    }
                })
                .catch((error) => {
                    this.saveLoading = false;
                    util.errorProcessor(this, error);
                });
        },
        back() {
            this.$emit('back');
        },

        save() {
            this.saveLoading = true;
            util.ajax.post('/config/save/one', this.formData)
                .then((response) => {
                    this.saveLoading = false;
                    this.$Message.success('保存成功');
                })
                .catch((error) => {
                    this.saveLoading = false;
                    util.errorProcessor(this, error);
                });
        }

    }
}
</script>


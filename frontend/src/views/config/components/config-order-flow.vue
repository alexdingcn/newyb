<style lang="less">
@import "../../../styles/common.less";
@import "../config.less";
</style>

<template>
    <Card >
        <p slot="title">订单流程配置</p>
        <div slot="extra"></div>
        
        <Form  ref="form" :model="formData" :label-width="90">
            <Alert type="success" class="alert-desc">
                请设置适合您公司业务特征的流程，以提高您业务处理的效率。修改了配置，请刷新整个页面确保系统功能已经修改正确。
            </Alert>

            <h3 class="flow-title">采购流程</h3>
            <Steps :current="5" >
                <Step style="width: 200px;" title="采购制单" icon="ios-circle-filled"></Step>
                <Step @click.native="handleClick('BUY_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="采购单审核" 
                    :status="formData.BUY_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.BUY_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step style="width: 200px;" title="采购收货" icon="ios-circle-filled"></Step>
                <Step @click.native="handleClick('BUY_QUALITY_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="入库质量验收" 
                    :status="formData.BUY_QUALITY_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.BUY_QUALITY_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step @click.native="handleClick('BUY_FINAL_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="入库终审" 
                    :status="formData.BUY_FINAL_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.BUY_FINAL_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
            </Steps>

            <h3 class="flow-title">采购退回流程</h3>
            <Steps :current="5" >
                <Step style="width: 200px;" title="退回单申请" icon="ios-circle-filled"></Step>
                <Step @click.native="handleClick('BUY_BACK_BM_CHECK')"  style="width: 200px; cursor: pointer;" 
                    title="退回单审核" content="采购经理审核确认" 
                    :status="formData.BUY_BACK_BM_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.BUY_BACK_BM_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step @click.native="handleClick('BUY_BACK_QA_CHECK')"  style="width: 200px; cursor: pointer;" 
                    title="退回单审核" content="质管经理审核确认" 
                    :status="formData.BUY_BACK_QA_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.BUY_BACK_QA_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step @click.native="handleClick('BUY_BACK_QUALITY_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="退回质量审核" 
                    :status="formData.BUY_BACK_QUALITY_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.BUY_BACK_QUALITY_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step style="width: 200px;" title="退回终审" icon="ios-circle-filled"></Step>
            </Steps>

            <h3 class="flow-title">销售流程</h3>
            <Steps :current="3" >
                <Step style="width: 200px;" title="销售制单" icon="ios-circle-filled"></Step>
                <Step @click.native="handleClick('SALE_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="出库质量审核" 
                    :status="formData.SALE_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.SALE_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step style="width: 200px;" title="销售终审" icon="ios-circle-filled"></Step>
            </Steps>

            <h3 class="flow-title">销售退回流程</h3>
            <Steps :current="6" >
                <Step style="width: 200px;" title="退回单申请" icon="ios-circle-filled"></Step>
                <Step @click.native="handleClick('SALE_BACK_SM_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="退回单审核" content="销售经理审核确认" 
                    :status="formData.SALE_BACK_SM_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.SALE_BACK_SM_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step @click.native="handleClick('SALE_BACK_QA_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="退回单审核" content="质管经理审核确认" 
                    :status="formData.SALE_BACK_QA_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.SALE_BACK_QA_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step style="width: 200px;" title="退回收货" icon="ios-circle-filled"></Step>
                <Step @click.native="handleClick('SALE_BACK_QUALITY_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="退回质量审核" 
                    :status="formData.SALE_BACK_QUALITY_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.SALE_BACK_QUALITY_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
                <Step @click.native="handleClick('SALE_BACK_FINAL_CHECK')" style="width: 200px; cursor: pointer;" 
                    title="退回终审" 
                    :status="formData.SALE_BACK_FINAL_CHECK.keyValue === 'open' ? 'finish' : 'error'" 
                    :icon="formData.SALE_BACK_FINAL_CHECK.keyValue === 'open' ? 'ios-checkmark': 'ios-minus'">
                </Step>
            </Steps>

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
    name: 'configOrderFlow',
    props: {},
    components: {},
    data() {
        return {
            formData: {
                BUY_CHECK: {keyName: 'BUY_CHECK', keyValue: 'close'},
                BUY_QUALITY_CHECK: {keyName: 'BUY_QUALITY_CHECK', keyValue: 'close'},
                BUY_FINAL_CHECK: {keyName: 'BUY_FINAL_CHECK', keyValue: 'close'},
                BUY_BACK_BM_CHECK: {keyName: 'BUY_BACK_BM_CHECK', keyValue: 'close'},
                BUY_BACK_QA_CHECK: {keyName: 'BUY_BACK_QA_CHECK', keyValue: 'close'},
                BUY_BACK_QUALITY_CHECK: {keyName: 'BUY_BACK_QUALITY_CHECK', keyValue: 'close'},
                SALE_CHECK: {keyName: 'SALE_CHECK', keyValue: 'close'},
                SALE_BACK_SM_CHECK: {keyName: 'SALE_BACK_SM_CHECK', keyValue: 'close'},
                SALE_BACK_QA_CHECK: {keyName: 'SALE_BACK_QA_CHECK', keyValue: 'close'},
                SALE_BACK_QUALITY_CHECK: {keyName: 'SALE_BACK_QUALITY_CHECK', keyValue: 'close'},
                SALE_BACK_FINAL_CHECK: {keyName: 'SALE_BACK_FINAL_CHECK', keyValue: 'close'},
            },
            saveLoading: false
        }
    },
    methods: {
        back() {
            this.$emit('back');
        },

        init() {
            this.saveLoading = true;
            util.ajax.get('/config/list')
                .then((response) => {
                    this.saveLoading = false;
                    let data = response.data;
                    if (data['BUY_CHECK']) {
                        this.formData.BUY_CHECK = data['BUY_CHECK'];
                    }
                    if (data['BUY_QUALITY_CHECK']) {
                        this.formData.BUY_QUALITY_CHECK = data['BUY_QUALITY_CHECK'];
                    }
                    if (data['BUY_FINAL_CHECK']) {
                        this.formData.BUY_FINAL_CHECK = data['BUY_FINAL_CHECK'];
                    }
                    if (data['BUY_BACK_BM_CHECK']) {
                        this.formData.BUY_BACK_BM_CHECK = data['BUY_BACK_BM_CHECK'];
                    }
                    if (data['BUY_BACK_QA_CHECK']) {
                        this.formData.BUY_BACK_QA_CHECK = data['BUY_BACK_QA_CHECK'];
                    }
                    if (data['BUY_BACK_QUALITY_CHECK']) {
                        this.formData.BUY_BACK_QUALITY_CHECK = data['BUY_BACK_QUALITY_CHECK'];
                    }
                    if (data['SALE_CHECK']) {
                        this.formData.SALE_CHECK = data['SALE_CHECK'];
                    }
                    if (data['SALE_BACK_SM_CHECK']) {
                        this.formData.SALE_BACK_SM_CHECK = data['SALE_BACK_SM_CHECK'];
                    }
                    if (data['SALE_BACK_QA_CHECK']) {
                        this.formData.SALE_BACK_QA_CHECK = data['SALE_BACK_QA_CHECK'];
                    }
                    if (data['SALE_BACK_QUALITY_CHECK']) {
                        this.formData.SALE_BACK_QUALITY_CHECK = data['SALE_BACK_QUALITY_CHECK'];
                    }
                    if (data['SALE_BACK_FINAL_CHECK']) {
                        this.formData.SALE_BACK_FINAL_CHECK = data['SALE_BACK_FINAL_CHECK'];
                    }
                })
                .catch((error) => {
                    this.saveLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        handleClick(keyName) {
            console.log('step handleClick:' + keyName);
            this.formData[keyName].keyValue = this.formData[keyName].keyValue === 'open' ? 'close' : 'open'; //取反
        },

        save() {
            console.log('save order flow.');
            
            //组织成一个列表，一次全部提交
            let list = [
                this.formData.BUY_CHECK,
                this.formData.BUY_QUALITY_CHECK,
                this.formData.BUY_FINAL_CHECK,
                this.formData.BUY_BACK_BM_CHECK,
                this.formData.BUY_BACK_QA_CHECK,
                this.formData.BUY_BACK_QUALITY_CHECK,
                this.formData.SALE_CHECK,
                this.formData.SALE_BACK_SM_CHECK,
                this.formData.SALE_BACK_QA_CHECK,
                this.formData.SALE_BACK_QUALITY_CHECK,
                this.formData.SALE_BACK_FINAL_CHECK
            ];

            console.log(list);
            this.saveLoading = true;
            util.ajax.post('/config/save/list', list)
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


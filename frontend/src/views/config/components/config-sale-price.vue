<style lang="less">
@import "../../../styles/common.less";
@import "../config.less";
</style>

<template>
    <Card>
        <p slot="title">销售单特批价调整</p>
        <div slot="extra"></div>

        <Form  ref="form" :model="formData" :label-width="90">
            <Alert type="success" class="alert-desc">
                启用后，制作销售订单时，可以根据商品的销售总价格上做整单的打折或直接修改总销售金额。
            </Alert>

            <Row >
                <Checkbox v-model="checkValue" @on-change="checkChange">是否启用特批价调整</Checkbox>
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
    name: 'configSalePrice',
    data() {
        return {
            formData: {
                keyName: 'SALE_PRICE',
                keyValue: 'close'
            },
            checkValue: false,
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
                    let valueInfo = data['SALE_PRICE'];
                    if (valueInfo) {
                        this.formData = valueInfo;
                    }
                    if (this.formData.keyValue === 'open') {
                        this.checkValue = true;
                    }else {
                        this.checkValue = false;
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

        checkChange(data) {
            this.checkValue = data;
            if (this.checkValue === 'open') {
                this.formData.keyValue = 'open';
            }else {
                this.formData.keyValue = 'close';
            }
        },

        save() {
            this.saveLoading = true;
            if (this.checkValue) {
                this.formData.keyValue = 'open';
            }else {
                this.formData.keyValue = 'close';
            }
            console.log(this.formData);
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


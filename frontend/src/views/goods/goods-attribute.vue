<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="ios-pricetags"></Icon>
              商品自定义属性
          </p>
          <div slot="extra">
              <ButtonGroup size="small">
                  <Button type="success" icon="checkmark" :loading="loading" @click="save" >保存</Button>
              </ButtonGroup>
          </div>

           <Alert>
                自定义字段设置说明：用于补充默认商品字段中不足的地方，设置后，可在“商品详情页”增加一个自定义文本字段进行描述，
                如：医药类产品的“主治功能”，设置后，可在每个“商品详情页”的自定义字段增加一个自定义的“主治功能”的字段。最多支持6个自定义字段
            </Alert>

          <Form ref="form" :model="formData" :label-width="90">
              <Row class="row-margin-bottom">
                  <i-col span="6">
                      <FormItem label="自定义属性1">
                          <Input type="text" v-model="formData.attName1"/>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="自定义属性2">
                          <Input type="text" v-model="formData.attName2"/>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="自定义属性3">
                          <Input type="text" v-model="formData.attName3"/>
                      </FormItem>
                  </i-col>
              </Row>
              <Row class="row-margin-bottom">
                  <i-col span="6">
                      <FormItem label="自定义属性4">
                          <Input type="text" v-model="formData.attName4"/>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="自定义属性5">
                          <Input type="text" v-model="formData.attName5"/>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="自定义属性6">
                          <Input type="text" v-model="formData.attName6"/>
                      </FormItem>
                  </i-col>
              </Row>
          </Form>
      </Card>

  </div>
</template>

<script>
import util from '@/libs/util.js';

export default {
  name: 'goods-attribute',
  data() {
      return {
          loading: false,
          formData: {
              attName1: '',
              attName2: '',
              attName3: '',
              attName4: '',
              attName5: '',
              attName6: '',
          },
          attributes: [
                {id: '', attName: ''},
                {id: '', attName: ''},
                {id: '', attName: ''},
                {id: '', attName: ''},
                {id: '', attName: ''},
                {id: '', attName: ''}
           ]
      }
  },
  mounted() {
      this.refreshGoodsAttribute();
  },
  methods: {
      refreshGoodsAttribute() {
          util.ajax.get('/goods/attribute/list')
            .then((response) => {
                let data = response.data;
                if(data && data.length>0) {
                    for (let i=0;i<6;i++) {
                        let item = data[i];
                        if (item && item.id) {
                            this.attributes[i] = item;
                        }
                    }
                    this.formData = {
                        attName1: this.attributes[0] ? this.attributes[0].attName : '',
                        attName2: this.attributes[1] ? this.attributes[1].attName : '',
                        attName3: this.attributes[2] ? this.attributes[2].attName : '',
                        attName4: this.attributes[3] ? this.attributes[3].attName : '',
                        attName5: this.attributes[4] ? this.attributes[4].attName : '',
                        attName6: this.attributes[5] ? this.attributes[5].attName : '',
                    }
                }
            })
            .catch((error) => {
                util.errorProcessor(this, error);
            })
      },
      save() {
          this.loading = true;
          this.attributes[0].attName = this.formData.attName1;
          this.attributes[1].attName = this.formData.attName2;
          this.attributes[2].attName = this.formData.attName3;
          this.attributes[3].attName = this.formData.attName4;
          this.attributes[4].attName = this.formData.attName5;
          this.attributes[5].attName = this.formData.attName6;
          let reqData = {
              attributes: this.attributes
          }
          util.ajax.post('/goods/attribute/save', reqData)
            .then((response) => {
                this.loading = false;
                this.$Message.success('保存成功.');
            })
            .catch((error) => {
                this.loading = false;
                util.errorProcessor(this, error);
            })
      }
  }

}
</script>


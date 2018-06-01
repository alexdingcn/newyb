
<template>
  <div>
        <Row>
            <i-col span="11">
                <goodscarelist  ref="goodscarelist"  @choose-good="chooseGood"></goodscarelist >
            </i-col>
            <i-col span="11" style="margin-left: 50px;">
                <Card>
                    <p slot="title">
                        <Icon type="person"></Icon>
                        {{'商品养护'}}
                    </p>
                    <div slot="extra">
                        <ButtonGroup size="small" >
                            <Button type="success" icon="checkmark"  @click="save">保存</Button>
                        </ButtonGroup>
                    </div>
                    <div>
                        <Form ref="goodForm" :model="goodForm" :label-width="100">
                            <FormItem label="商品名称：" prop="name" >
                                <Input v-model="goodForm.name"  />
                            </FormItem>
                            <FormItem label="养护员：" prop="carePerson" >
                                <Input  v-model="goodForm.carePerson" />
                            </FormItem>
                            <FormItem label="养护记录：" prop="careResult" >
                                <Input  v-model="goodForm.careResult"/>
                            </FormItem>
                            <FormItem label="温度记录：" prop="temperature">
                                <Input v-model="goodForm.temperature" />
                            </FormItem>
                            <FormItem  label="下次养护：" prop="xiayici" >
                                <DatePicker type="date" placeholder="Select date" style="width: 200px"></DatePicker>
                            </FormItem>
                            <FormItem  prop="goodsId" v-show="false">
                                <Input v-model="goodForm.goodsId" />
                            </FormItem>
                        </Form>
                    </div>
                </Card>
                <card style="margin-top: 20px;">
                    <div>
                        <goodsCareRecord ref="carelist" ></goodsCareRecord>
                    </div>
                </card>
            </i-col>
        </Row>
  </div>
</template>

<script>
import util from '@/libs/util.js';
import goodscarelist from './goods-care-list.vue';
import goodsCareRecord from './goods-care-record.vue';

export default {
    name: 'goods-care',
    data() {
        return {
            currGood:{},
            goodForm: {},
            goodsId:'',
        }
    },
    components: {
        goodscarelist,
        goodsCareRecord,
    },
    methods:{
        chooseGood(good){
            this.currGood = good;
            this.$options.methods.updateCareList();
        },
        updateCareList(){
            //this.$refs.carelist.refreshRecordList();
        },
        save(){
            let saveData = {
                goodsId : this.goodForm.goodsId,
                careResult : this.goodForm.careResult,
                carePerson : this.goodForm.carePerson,
                temperature : this.goodForm.temperature
            }
            util.ajax.post('/goods_care/save',saveData)
                .then((response) => {
                  this.$Message.success("养护记录添加成功！");  
                  this.$refs.goodForm.resetFields();
                  updateCareList();
            }).catch(function (error) {
                        util.errorProcessor(this, error);
                    });
        }
    } ,
    watch:{
        currGood(data) {
            
            if (data && data.id > 0) {
                this.goodForm = {
                    name: data.name,
                    goodsId: data.id,
                };
            }
        },
    }
}
</script>

<style >
.ivu-form-item {
    margin-bottom: 20px;
}

</style>

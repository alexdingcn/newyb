
<template>
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
            <FormItem  label="下次养护：" prop="nextDate" >
                <DatePicker type="date" v-model="goodForm.nextDate" placeholder="Select date" style="width: 200px"></DatePicker>
            </FormItem>
            <FormItem  prop="goodsId" v-show="false">
                <Input v-model="goodForm.goodsId" />
            </FormItem>
        </Form>
        <Row slot="footer" type="flex" justify="end">
            <ButtonGroup shape="circle" >
                <Button type="success" icon="checkmark"   @click="save">确认保存</Button>
            </ButtonGroup>
        </Row>          
  </div>
</template>

<script>
import util from "@/libs/util.js";
import goodscarelist from "./goods-care-list.vue";
import goodsCareRecord from "./goods-care-record.vue";

export default {
  name: "goods-care",
  data() {
    return {
      currGood: {},
      goodForm: {},
      /**goodsId: "",
      name: ""*/
    };
  },
  props: {
    goodId: {
      type: String | Number,
      default: ""
    },
    name: {
      type: String,
      default: ""
    }
  },
  components: {
    goodscarelist,
    goodsCareRecord
  },
  methods: {
    /**chooseGood(good) {
      this.currGood = good;
      this.$options.methods.updateCareList();
    },*/
    //updateCareList() {
      //this.$refs.carelist.refreshRecordList();
    //},
    save() {
      let saveData = {
        goodsId: this.goodForm.goodsId,
        careResult: this.goodForm.careResult,
        carePerson: this.goodForm.carePerson,
        temperature: this.goodForm.temperature,
        nextDate: this.goodForm.nextDate,
      };
      util.ajax
        .post("/goods_care/save", saveData)
        .then(response => {
          this.$Message.success("养护记录添加成功！");
          //util.closeCurrentTab(this);
          this.$refs.goodForm.resetFields();
          this.$emit("save-ok"); 
          //updateCareList();
        })
        .catch(function(error) {
          util.errorProcessor(this, error);
        });
    }
  },
  watch: {
    goodId:function(data) {
      if (data && data.id > 0) {
        this.goodForm = {
          name: data.name,
          goodsId: data.id
        };
      }
    }
  }
};
</script>

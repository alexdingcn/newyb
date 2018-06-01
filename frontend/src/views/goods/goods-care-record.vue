
<template>
<div>
    <card style="margin-left: 20px;">
            <p slot="title">
                <Icon type="person-stalker"></Icon> 养护列表
            </p>
            <div slot="extra">
                <ButtonGroup class="padding-right-20"  size="small">
                    <Button type="primary" icon="android-add-circle" @click="validGoods">添加养护</Button>
                </ButtonGroup>
            </div>
            
            <Table size="small" highlight-row height="800" :loading="tabLoading"
                :columns="goodColumns" :data="careList"  >
            </Table>
            
            
            <Modal v-model="locationModalShow" width="360" :mask-closable="false">
            <p slot="header">
                <Icon type="ios-plus-outline"></Icon>
                <span>新增养护</span>
            </p>
            <div>
                        <Form ref="goodForm" :model="goodForm" :label-width="100">
                            <FormItem label="养护员：" prop="carePerson" >
                                <Input  v-model="goodForm.carePerson" />
                            </FormItem>
                            <FormItem label="养护记录：" prop="careResult" >
                                <Input  v-model="goodForm.careResult"/>
                            </FormItem>
                            <FormItem label="温度记录：" prop="temperature">
                                <Input v-model="goodForm.temperature" />
                            </FormItem>
                            <FormItem  label="下次养护：" prop="careTime" >
                                <DatePicker type="date" v-model="goodForm.careTime" placeholder="Select date" style="width: 200px"></DatePicker>
                            </FormItem>
                            <FormItem  prop="goodsId" v-show="false">
                                <Input v-model="goodForm.goodsId" />
                            </FormItem>
                        </Form>
                    </div>
                     <div slot="footer">
                <Button type="primary" @click="save(true)">增加</Button>
                <Button type="primary" @click="save(false)">增加并关闭</Button>
            </div>
        </Modal>
    </card>
    
    </div>
    
</template>

<script>
import util from "@/libs/util.js";
import bus from "@/libs/bus";

export default {
  name: "goodsCareRecord",
  data() {
    return {
      searchValue: "",
      currGood: {},
      goodForm: {},
      tabLoading: false,
      careList: [],
      goodsId: "",
      searchid:'',
      locationModalShow: false,
      goodColumns: [
        {
          title: "商品名称",
          key: "name",
          align: "center"
        },
        {
          title: "养护记录",
          key: "careResult",
          align: "center"
        },
        {
          title: "养护温度",
          key: "temperature",
          align: "center"
        },
        {
          title: "养护人",
          key: "carePerson",
          align: "center"
        }
      ]
    };
  },
  watch: {
    goodsId: function(data) {
      this.searchid = data;
      let reqData = {
        goodsId: data
      };
      this.tabLoading = true;
      var self = this;
      util.ajax
        .get("/goods_care/recordList", { params: reqData })
        .then(response => {
          self.tabLoading = false;
          self.careList = response.data.data;
          console.log("later---" + self.careList);
        })
        .catch(error => {
          this.tabLoading = false;
          util.errorProcessor(this, error);
        });
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.refreshRecordList();
    },
    validGoods(){
        if(this.goodsId == ""){
            this.$Message.warning("请选择商品！");
        }else{
            this.locationModalShow = true;
        }
        
    },
    save(closeModal) {
      let saveData = {
        goodsId: this.searchid,
        careResult: this.goodForm.careResult,
        carePerson: this.goodForm.carePerson,
        temperature: this.goodForm.temperature,
        careTime: this.goodForm.careTime
      };
      util.ajax
        .post("/goods_care/save", saveData)
        .then(response => {
          this.$Message.success("养护记录添加成功！");
          this.$refs.goodForm.resetFields();
          this.locationModalShow = closeModal;
          this.refreshRecordList();
        })
        .catch(function(error) {
          util.errorProcessor(this, error);
        });
    },
    changePage(data) {
      this.currentPage = data;
      refreshRecordList();
    },
    refreshRecordList() {
      this.tabLoading = true;
      var self = this;
      util.ajax
        .get("/goods_care/recordList")
        .then(response => {
          self.tabLoading = false;
          self.careList = response.data.data;
          //self.totalCount = response.data.count;
          console.log("end--" + self.careList);
        })
        .catch(error => {
          this.tabLoading = false;
          util.errorProcessor(this, error);
        });
    }
  }
};
</script>

<style >
</style>

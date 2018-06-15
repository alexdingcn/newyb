
<template>
<div>

            <Table size="small" highlight-row height="400" :loading="tabLoading"
                :columns="goodColumns" :data="careList"  >
            </Table>
    
    </div>
    
</template>

<script>
import util from "@/libs/util.js";
import bus from "@/libs/bus";

export default {
  name: "goodsCareRecord",
  data() {
    return {
      tabLoading: false,
      careList: [],
      goodsId: "",
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
          title: "养护日期",
          key: "createDate",
          align: "center"
        },
        {
          title: "下次养护日期",
          key: "nextDate",
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
  methods: {
  }
};
</script>

<style >
</style>

<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row>
          <Card>
              <p slot="title">
                  <Icon :type="titleDispayIcon"></Icon>
                  {{titleDispayName || '客户详情'}}
              </p>

              <ButtonGroup slot="extra">
                  <Button type="primary" @click="submitCustomer">提交</Button>
              </ButtonGroup>

              <Form ref="custormerForm" :model="formItem" :rules="ruleValidate" :label-width="100">

              </Form>
          </Card>
      </Row>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";

export default {
  name: "customer-info",
  data() {
    return {
      showView: this.$route.params.showView,
      showTitle: this.$route.params.showTitle,
      editId: this.$route.params.id,
      categorys: [],
      formItem: {},
      ruleValidate: {}
    };
  },
  mounted() {
    this.initData();
  },
  computed: {
    titleDispayIcon() {
      if (this.showView === "add") {
        return "plus-round";
      } else {
        return "compose";
      }
    },
    titleDispayName() {
      return this.showTitle;
    }
  },
  watch: {
    '$route': this.initData()
  },
  methods: {
    initData() {
      showView = this.$route.params.showView;
      showTitle: this.$route.params.showTitle;
      this.getAllCategorys();
    },

    getAllCategorys() {
      util.ajax
        .get("/customer/category/list")
        .then(res => {
          this.categorys = res.data;
        })
        .catch(error => {
          console.log(error);
        });
    },

    submitCustomer() {}

  }
};
</script>


<style>

</style>


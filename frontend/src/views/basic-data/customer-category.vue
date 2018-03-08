<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <Modal v-model="isShowModal" :closable="false" >
        <Form ref="custCatForm" :model="custCatFormData" :rules="ruleValidate" label-position="right" :label-width="100" class="margin-right-10">
            <FormItem label="分类编码" prop="categoryNo">
                <Input v-model="custCatFormData.categoryNo" placeholder="输入客户类编码"></Input>
            </FormItem>
            <FormItem label="类别名称" prop="name">
                <Input v-model="custCatFormData.name" placeholder="输入客户类名称"></Input>
            </FormItem>
            <FormItem label="上级类别">
                <Select v-model="custCatFormData.parentId" placeholder="选择上级类别">
                    <Option v-for="item in parentCateList" :value="item.id" :key="item.id">{{ item.name }} </Option>
                </Select>
            </FormItem>
            <FormItem label="备注">
                <Input v-model="custCatFormData.comment" ></Input>
            </FormItem>
        </Form>
        <div slot="footer">
          <Row >
            <Col span="6" offset="6">
              <Button type="primary" :loading="loading" @click="ok" long>
                <span v-if="!loading">提交</span>
                <span v-else>正在提交...</span>
              </Button>
            </Col>
            <Col span=6 class="padding-left-10">
              <Button @click="closedModal" long>取消</Button>
            </Col>
          </Row>
            
        </div>
    </Modal>
</template>

<script>
import util from "@/libs/util.js";

export default {
  name: "customer-category",
  props: {
    action: {
      type: String,
      required: true,
      validator: function(value) {
        return value === "add" || value === "edit";
      }
    },
    categorys: {
      type: Array,
      default: []
    },
    editeData: Object,
    showModal: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      isShowModal: false,
      loading: false,
      custCatFormData: {
        id: "",
        categoryNo: "",
        name: "",
        parentId: -1,
        comment: ""
      },
      parentCateList: [
        {
          id: -1,
          name: "顶级"
        },
        ...this.categorys
      ],
      ruleValidate: {
        categoryNo: [
          { required: true, message: "请输入类别编码", trigger: "blue" }
        ],
        name: [{ required: true, message: "请输入类别编码", trigger: "blue" }]
      }
    };
  },
  mounted() {
    console.log(this);
    this.initData();
  },
  watch: {
    showModal(data) {
      this.isShowModal = data;
    },
    isShowModal(data) {
      if (!data) {
        this.$emit("dialog-closed");
      }
    }
  },
  methods: {
    initData() {
      console.log(this.action + ", " + this.showModal + ", " + this.editeData);
      if (this.action === "edit" && this.editeData) {
        this.custCatFormData.id = this.editeData.id;
        this.custCatFormData.categoryNo = this.editeData.categoryNo;
        this.custCatFormData.name = this.editeData.name;
        this.custCatFormData.parentId = this.editeData.parentId;
        this.custCatFormData.comment = this.editeData.comment;
      } else {
        this.$refs.custCatForm.resetFields();
      }
    },

    ok() {
      this.loading = true;
      this.$refs.custCatForm.validate(valid => {
        if (!valid) {
          this.loading = false;
          return;
        } else {
          let actionVal = this.action;
          let categoryData = this.custCatFormData;
          if (actionVal === "add") {
            this.doAddCategory(categoryData);
          } else if (actionVal === "edit") {
            this.doUpdateCategory(categoryData);
          } else {
            this.$Message.error("系统异常, 操作类型错误");
            this.loading = false;
            return;
          }
        }
      });
    },

    closedModal() {
      this.$refs.custCatForm.resetFields();
      this.isShowModal = false;
    },

    submitSuccessEvent() {
      let resultData = {
        action: this.action,
        formData: this.custCatFormData
      };
      this.$emit("category-submit", resultData);
    },

    doAddCategory(data) {
      console.log("do add a category info:" + data);
      util.ajax
        .post("/customer/category/add")
        .then(respones => {
          this.$Message.success("新建客户类成功");
          submitSuccessEvent();
          this.isShowModal = false;
        })
        .catch(error => {
          this.loading = false;
          console.log(error);
        });
    },

    doUpdateCategory(data) {
      console.log("do update a category info:" + data);
      util.ajax
        .post("/customer/category/update")
        .then(respones => {
          this.$Message.success("客户类信息修改成功");
          submitSuccessEvent();
          this.isShowModal = false;
        })
        .catch(error => {
          this.loading = false;
          console.log(error);
        });
    }
  }
};
</script>

<style>

</style>



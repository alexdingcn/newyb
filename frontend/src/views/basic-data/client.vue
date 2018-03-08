<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row>
          <Col span="6">
            <Card>
                <p slot="title">
                    <Icon type="person-stalker"></Icon> 客户分类
                </p>

                <ButtonGroup slot="extra">
                    <Button type="primary" icon="android-add-circle" size="small" @click="openAddCategoryModal">添加</Button>
                    <Button type="success" icon="edit" size="small" @click="openEditCategoryModal" :disabled="disableDelCategory"></Button>
                    <Button type="error" icon="android-remove-circle" size="small" @click="delCategory" :disabled="disableDelCategory"></Button>
                </ButtonGroup>

                <div>
                    <Tree :data="customerCat" @on-select-change="onTreeNodeSelected" ref="customerCatTree"></Tree>
                </div>
                
            </Card>
          </Col>
        
        <Col span="18" class="padding-left-10">
            <Card>
                <p slot="title">
                    <Icon type="person"></Icon> 客户 {{selectedCategory}}
                </p>

                <ButtonGroup slot="extra" >
                    <Button type="primary" icon="android-add-circle" size="small" @click="addCustomer">添加</Button>
                    <Button type="error" icon="android-remove-circle" size="small" @click="delCustomers">删除</Button>
                </ButtonGroup>

                <Row type="flex" justify="center" align="middle" >
                    <Input v-model="searchVal" style="width: 55%;">
                        <Select v-model="searchSelectVal" slot="prepend" style="width: 80px">
                            <Option value="searchByName">客户名称</Option>
                            <Option value="searchByNo">客户编号</Option>
                        </Select>
                        <Button slot="append" icon="ios-search" @click="searchBtnClicked"></Button>
                    </Input>
                </Row>

                <Row type="flex" justify="center" align="middle" class="margin-top-20">
                    <Table border highlight-row :columns="orderColumns" :data="customersData" 
                        ref="customersTable" style="width: 100%;" height="450" size="small">
                    </Table>
                    <Row type="flex" justify="end">
                        <Page :total="customersCount" show-total></Page>
                    </Row>
                </Row>
            </Card>
        </Col>
      </Row>

      <customer-category 
                    :showModal="categoryModal" 
                    :action="customerCategoryAction" 
                    :categorys="categorys" 
                    :editeData="selectedCategory" 
                    @category-submit="onCategorySubmit"
                    @dialog-closed='onDialogClosed'>
        </customer-category>

        <Spin size="large" fix v-if="spinShow">
            <Icon type="load-c" size=18 class="spin-icon-load"></Icon>
        </Spin>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";
import customerCategory from "./customer-category.vue";

export default {
  name: "client",
  components: {
    customerCategory
  },
  data() {
    return {
      spinShow: false,
      disableDelCategory: true,
      categoryModal: false,
      customerCategoryAction: "add",
      categorys: [],
      selectedCategory: null,
      searchSelectVal: "searchByName",
      searchVal: "",
      customersData: [],
      orderColumns: [
        {
          type: "selection",
          width: 60,
          align: "center",
          fixed: "left"
        },
        {
          type: "index",
          width: 80,
          title: "序号",
          align: "center",
          fixed: "left"
        },
        {
          width: 150,
          title: "客户名称",
          key: "name",
          align: "center",
          fixed: "left",
          sortable: true,
          render: (h, params) => {
            return h(
              "Button",
              {
                props: {
                  type: "text",
                  size: "small"
                },
                on: {
                  click: () => {
                    let argu = { customer_id: params.row.id };
                    this.$router.push({
                      name: "customer-info",
                      params: argu
                    });
                  }
                }
              },
              params.row.name
            );
          }
        },
        {
          width: 120,
          title: "客户编号",
          key: "customerNo",
          align: "center",
          sortable: true,
          render: (h, params) => {
            return h(
              "Button",
              {
                props: {
                  type: "text",
                  size: "small"
                },
                on: {
                  click: () => {
                    let argu = { customer_id: params.row.id };
                    this.$router.push({
                      name: "customer-info",
                      params: argu
                    });
                  }
                }
              },
              params.row.customerNo
            );
          }
        },
        {
          width: 120,
          title: "是否禁用",
          key: "enabled",
          align: "center",
          sortable: true,
          render: (h, params) => {
            let isTrue = params.row.enabled;
            return h("div", [
              h("Icon", {
                props: {
                  type: isTrue ? "checkmark-circled" : "close-circled",
                  color: isTrue ? "#00a854" : "#e96500"
                }
              }),
              h("strong", isTrue ? "已启用" : "已禁用")
            ]);
          }
        },
        {
          width: 100,
          title: "客户类别",
          key: "categoryId",
          align: "center",
          render: (h, params) => {
            let categoryItem = this.selectObjectById(
              params.row.categoryId,
              this.categorys
            );
            return h("span", categoryItem.name);
          }
        },
        {
          width: 100,
          title: "城市",
          key: "city",
          align: "center"
        },
        {
          width: 100,
          title: "法定代表人",
          key: "legalPersion",
          align: "center"
        },
        {
          width: 100,
          title: "负责人",
          key: "employee",
          align: "center"
        },
        {
          width: 100,
          title: "联系电话",
          key: "contactPhone",
          align: "center"
        },
        {
          width: 100,
          title: "经营范围",
          key: "businessScope",
          align: "center"
        },
        {
          width: 100,
          title: "创建人",
          key: "createBy",
          align: "center"
        }
      ]
    };
  },
  mounted() {
    this.getCategoryArr();
  },
  computed: {
    customersCount() {
      return this.customersData.length;
    },
    customerCat() {
      let attr = {
        rootId: null,
        idKey: "id",
        titleKey: "name",
        parentKey: "parentId",
        expand: false
      };
      return dataConver.arryToIviewTreeData(this.categorys, attr);
    }
  },
  methods: {
    getCategoryArr() {
      this.spinShow = true;
      util.ajax
        .get("/customer/category/list")
        .then(res => {
          this.categorys = res.data;
        })
        .catch(error => {
          console.log(error);
        });
      this.spinShow = false;
    },

    openAddCategoryModal() {
      this.customerCategoryAction = "add";
      this.categoryModal = true;
      console.log(
        "click open add category modal, categoryModal:" + this.categoryModal
      );
    },

    openEditCategoryModal() {
      this.customerCategoryAction = "edit";
      this.categoryModal = true;
    },

    onTreeNodeSelected(data) {
      console.log(data);
      this.disableDelCategory = false;
      this.selectedCategory = selectObjectById(data.id, this.categorys);
    },

    onCategorySubmit(submitData) {
      console.log("category modal ok btn click" + submitData);
      this.categoryModal = false;
      this.getCategoryArr();
    },

    onDialogClosed() {
      this.categoryModal = false;
    },

    delCategory() {
      let delData = this.selectedCategory;
      if (!delData || !delData.id || delData.id <= 0) {
        console.log("delete category but not choose category.");
        return;
      }
      this.spinShow = true;
      util.ajax
        .delete("customer/category/delete")
        .then(response => {
          this.$Message.success("客户类信信息删除成功");
          this.getCategoryArr();
        })
        .catch(error => {
          console.log(error);
        });
      this.spinShow = false;
    },

    searchBtnClicked() {},

    addCustomer() {},

    delCustomers() {},

    selectObjectById(id, arrs) {
      let result = null;
      if (!arrs || arrs.length <= 0) {
        return result;
      }
      for (let item in arrs) {
        if (id === item.id) {
          result = item;
          break;
        }
      }
      return result;
    }
  }
};
</script>


<style>
.spin-icon-load {
  animation: ani-demo-spin 1s linear infinite;
}
</style>



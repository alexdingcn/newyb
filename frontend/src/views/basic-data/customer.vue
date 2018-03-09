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
                    <Icon type="person"></Icon> 客户 <strong>{{selectCategoryName}}</strong>
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
                        :loading="customerTableLoading" 
                        @on-selection-change="tableSelecttionChange" 
                        ref="customersTable" style="width: 100%;" size="small">
                    </Table>
                    <Row type="flex" justify="end">
                        <Page :total="customersCount" show-total show-sizer 
                          @on-change="pageChange"
                          @on-page-size-change="pageSizeChange"></Page>
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
  </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";
import customerCategory from "@/views/customer/customer-category.vue";

export default {
  name: "customer",
  components: {
    customerCategory
  },
  data() {
    return {
      categoryModal: false,
      customerCategoryAction: "add",
      categorys: [],
      selectedCategory: null,
      searchSelectVal: "searchByName",
      searchVal: "",
      customerTableLoading: false,
      customersCount: 0,
      tableCurrPage: 1,
      tableCurrPageSize: 10,
      tableSelectionData: [],
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
                    let argu = { 
                      showView:'edit', 
                      showTitle: params.row.name,
                      id: params.row.id 
                    };
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
                    let argu = { 
                      showView:'edit', 
                      showTitle: params.row.name,
                      id: params.row.id 
                    };
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
          width: 120,
          title: "客户类别",
          key: "categoryId",
          align: "center",
          render: (h, params) => {
            let categoryItem = dataConver.selectObjectById(
              params.row.categoryId,
              this.categorys
            );
            return h("span", categoryItem.name);
          }
        },
        {
          width: 120,
          title: "城市",
          key: "city",
          align: "center"
        },
        {
          width: 120,
          title: "法定代表人",
          key: "legalPersion",
          align: "center"
        },
        {
          width: 120,
          title: "负责人",
          key: "employee",
          align: "center"
        },
        {
          width: 120,
          title: "联系电话",
          key: "contactPhone",
          align: "center"
        },
        {
          width: 150,
          title: "经营范围",
          key: "businessScope",
          align: "center"
        },
        {
          width: 120,
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
    disableDelCategory() {
      return !(this.selectedCategory && this.selectedCategory.id > 0);
    },
    selectCategoryName() {
      if (this.selectedCategory && this.selectedCategory.name) {
        return this.selectedCategory.name;
      } else {
        return "";
      }
    },
    customerCat() {
      let attr = {
        rootId: -1,
        idKey: "id",
        titleKey: "name",
        parentKey: "parentId",
        expand: true
      };
      let allChild = dataConver.arryToIviewTreeData(this.categorys, attr);
      return [
        {
          id: -1,
          title: '所有',
          expand: attr.expand,
          children: allChild
        }
      ];
    }
  },
  watch: {
    categorys() {
      this.selectedCategory = null;
    }
  },
  methods: {
    getCategoryArr() {
      util.ajax
        .get("/customer/category/list")
        .then(res => {
          this.categorys = res.data;
        })
        .catch(error => {
          console.log(error);
        });
    },

    openAddCategoryModal() {
      this.customerCategoryAction = "add";
      this.categoryModal = true;
    },

    openEditCategoryModal() {
      this.customerCategoryAction = "edit";
      this.categoryModal = true;
    },

    onTreeNodeSelected(data) {
      let chooseData = data[0];
      if (!chooseData) {
        return;
      }
      if (chooseData.id <= 0) {
        this.selectCustomerByCategory();
        return;
      }
      this.selectedCategory = dataConver.selectObjectById(data[0].id, this.categorys);
      this.selectCustomerByCategory(this.selectedCategory.id);
    },

    onCategorySubmit(submitData) {
      this.categoryModal = false;
      this.getCategoryArr();
    },

    onDialogClosed() {
      this.categoryModal = false;
    },

    delCategory() {
      let delData = this.selectedCategory;
      if (!delData || !delData.id || delData.id <= 0) {
        return;
      }
      let removeUrl = "/customer/category/remove/" + delData.id;
      util.ajax
        .delete(removeUrl)
        .then(response => {
          this.$Message.success("客户类信信息删除成功");
          this.getCategoryArr();
        })
        .catch(error => {
          console.log(error);
        });
    },

    selectCustomerByCategory(categoryId) {
      this.loadCustomerList(this.tableCurrPage, this.tableCurrPageSize, categoryId);
    },

    pageChange(data) {
      this.tableCurrPage = data;
      let currCategoryId = this.selectedCategory
        ? this.selectedCategory.id
        : "";
      this.loadCustomerList(this.tableCurrPage, this.tableCurrPageSize, currCategoryId)
    },

    pageSizeChange(data) {
      this.tableCurrPageSize = data;
      let currCategoryId = this.selectedCategory
        ? this.selectedCategory.id
        : "";
      this.loadCustomerList(1, this.tableCurrPageSize, currCategoryId)
    },

    loadCustomerList(page, size, categoryId) {
      this.customerTableLoading = true;
      let reqPage = page && page > 0 ? page : 1;
      let reqSize = size && size > 0 ? size : 10;
      let reqCustomerNo = null;
      let reqData = {
        page: reqPage,
        size: reqSize,
        categoryId: categoryId
      };
      if (this.searchVal && this.searchVal != "") {
        if (this.searchSelectVal === "searchByName") {
          reqData.customerName = this.searchVal;
        } else if (this.searchSelectVal === "searchByNo") {
          reqData.customerNo = this.searchVal;
        }
      }
      util.ajax
        .get("/customer/list", { params: reqData })
        .then(response => {
          let result = response.data;
          this.customersData = result.data;
          this.customersCount = result.count;
          this.customerTableLoading = false;
        })
        .catch(error => {
          console.log(error);
          this.customerTableLoading = false;
        });
    },

    searchBtnClicked() {
      let currCategoryId = this.selectedCategory
        ? this.selectedCategory.id
        : "";
      this.loadCustomerList(this.tableCurrPage, this.tableCurrPageSize, currCategoryId);
    },

    tableSelecttionChange(data) {
      console.log(data);
      this.tableSelectionData = data;
    },

    delCustomers() {
      if (this.tableSelectionData.length <= 0) {
        this.$Message.warning('请先选择需要删除的客户信息');
        return;
      }
      this.customerTableLoading = true;
      let delData = [];
      for (let i=0; i< this.tableSelectionData.length; i++) {
        let item = this.tableSelectionData[i];
        delData.push(item.id);
      }
      console.log(delData);
      util.ajax
        .post('/customer/delete', delData, {headers:{'Content-Type': 'application/json'}})
        .then(response => {
          this.customerTableLoading = false;
          this.searchBtnClicked();
          let delCount = response.data.count;
          this.$Message.success('成功删除' + delCount + '条客户信息');
        })
        .catch(error => {
          this.customerTableLoading = false;
          console.log(error);
        })
    },

    addCustomer() {
      let argus = {
        showView: 'add',
        showTitle: '新建客户信息',
        id: ''
      };
      this.$router.push({
        name: 'customer-info',
        params: argus
      });
    }

  }
};
</script>


<style>
.spin-icon-load {
  animation: ani-demo-spin 1s linear infinite;
}
</style>



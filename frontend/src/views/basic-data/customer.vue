<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row>
          <i-col span="7">
            <Card>
              <p slot="title">
                <Icon type="person-stalker" ></Icon> 客户分组
              </p>
              <ButtonGroup size="small" slot="extra">
                  <Button type="primary" icon="android-add-circle" @click="openAddCategoryModal">添加</Button>
                  <Button type="success" icon="edit"  @click="openEditCategoryModal" :disabled="disableDelCategory">编辑</Button>
                  <Button type="error" icon="android-remove-circle" @click="delCategory" :disabled="disableDelCategory">删除</Button>
              </ButtonGroup>

              <Row>
                <Dropdown trigger="custom" :visible="dropDawnVisible" placement="bottom-start" style="width:80%;">
                    <Button type="ghost" size="small" long @click="openDropDownClick">
                        选择客户分类
                        <Icon type="arrow-down-b"></Icon>
                    </Button>
                    <DropdownMenu slot="list" >
                      <div>
                        <Tree :data="customerCat" @on-select-change="onTreeNodeSelected" ref="customerCatTree"></Tree>
                      </div>
                    </DropdownMenu>
                </DropDown>
              </Row>
              <Row type="flex" justify="start" class="margin-top-8">
                <Breadcrumb separator=">">
                  <BreadcrumbItem v-for="item in breadcrumbList" :key="item.id"> {{item.name}} </BreadcrumbItem>
                </Breadcrumb>
              </Row>
            </Card>
            <Card>
                <p slot="title">
                    <Icon type="person"></Icon> 客户 
                </p>

                <ButtonGroup slot="extra" >
                    <Button type="primary" icon="android-add-circle" size="small" @click="addCustomer">添加</Button>
                    <Button type="error" icon="android-remove-circle" size="small" @click="delCustomers">删除</Button>
                </ButtonGroup>

                <Row type="flex" justify="center" align="middle" >
                    <i-input v-model="searchVal" placeholder="名称/编号/拼音" @on-enter="searchBtnClicked" >
                        <Button slot="append" icon="ios-search" @click="searchBtnClicked"></Button>
                    </i-input>
                </Row>

                <Row type="flex" justify="center" align="middle" class="margin-top-20">
                    <Table border highlight-row :columns="orderColumns" :data="customersData" 
                        :loading="customerTableLoading" 
                        @on-row-click="tableRowClick"  
                        @on-selection-change="tableSelecttionChange" 
                        ref="customersTable" style="width: 100%;" size="small">
                    </Table>
                </Row>
                <Row type="flex" justify="end">
                    <span class="margin-right-5">共 {{customersCount}} 条 </span>
                    <Page :total="customersCount" simple :current="tableCurrPage" :page-size="tableCurrPageSize" 
                      @on-change="pageChange">
                    </Page>
                </Row>
            </Card>
          </i-col>
        
        <i-col span="17" class="padding-left-10">
            <customer-info 
              :categorys="categorys" 
              :customerId="currCustomerId" 
              @add-success="customerAddSuccess" 
              @update-success="customerUpdateSuccess" 
              ></customer-info>
        </i-col>
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
import customerInfo from "@/views/customer/customer-info.vue";

export default {
  name: "customer",
  components: {
    customerCategory,
    customerInfo
  },
  data() {
    return {
      dropDawnVisible: false,
      breadcrumbList: [],
      categoryModal: false,
      customerCategoryAction: "add",
      categorys: [],
      selectedCategory: null,
      searchVal: "",
      customerTableLoading: false,
      customersCount: 0,
      tableCurrPage: 1,
      tableCurrPageSize: 20,
      tableSelectionData: [],
      currCustomerId: "",
      customersData: [],
      orderColumns: [
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          title: "客户名称",
          key: "name",
          align: "center",
          sortable: true
        }
      ]
    };
  },
  mounted() {
    this.getCategoryArr();
    this.loadCustomerList();
  },
  computed: {
    disableDelCategory() {
      return !(this.selectedCategory && this.selectedCategory.id > 0);
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
          title: "所有",
          expand: attr.expand,
          children: allChild
        }
      ];
    }
  },
  watch: {
    categorys() {
      this.selectedCategory = null;
    },
    selectedCategory() {
      this.changeSelectedCategory();
    }
  },
  methods: {
    openDropDownClick() {
      this.dropDawnVisible = !this.dropDawnVisible;
    },

    changeSelectedCategory() {
      this.changebreadcrumbList();
      if (this.selectedCategory && this.selectedCategory.id) {
        this.selectCustomerByCategory(this.selectedCategory.id);
      }
    },

    changebreadcrumbList() {
      if (this.selectedCategory && this.selectedCategory.id) {
        let result = [];
        let curr = this.selectedCategory;
        if (!curr) {
          this.breadcrumbList = [];
        }
        result.push(curr);
        let haveParent = curr.parentId && curr.parentId > 0;
        while (haveParent) {
          let parent = this.getParentCategory(curr);
          if (parent) {
            curr = parent;
            result.push(parent);
          } else {
            haveParent = false;
          }
        }
        this.breadcrumbList = result.reverse();
      } else {
        this.breadcrumbList = [];
      }
    },

    getParentCategory(data) {
      if (!data || data.parentId === undefined || this.categorys.length <= 0) {
        return;
      }
      for (let i = 0; i < this.categorys.length; i++) {
        let item = this.categorys[i];
        if (data.parentId === item.id) {
          return item;
        }
      }
    },

    getCategoryArr() {
      util.ajax
        .get("/customer/category/list")
        .then(res => {
          this.categorys = res.data;
        })
        .catch(error => {
          util.errorProcessor(this, error);
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
        this.dropDawnVisible = false;
        this.selectCustomerByCategory(); // 全选
        return;
      }
      this.dropDawnVisible = false;
      this.selectedCategory = dataConver.selectObjectById(
        data[0].id,
        this.categorys
      );
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
      util.ajax
        .delete("/customer/category/remove/" + delData.id)
        .then(response => {
          this.$Message.success("客户类信信息删除成功");
          this.getCategoryArr();
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    selectCustomerByCategory(categoryId) {
      this.loadCustomerList(
        this.tableCurrPage,
        this.tableCurrPageSize,
        categoryId
      );
    },

    pageChange(data) {
      this.tableCurrPage = data;
      let currCategoryId = this.selectedCategory
        ? this.selectedCategory.id
        : "";
      this.loadCustomerList(
        this.tableCurrPage,
        this.tableCurrPageSize,
        currCategoryId
      );
    },

    loadCustomerList(page, size, categoryId) {
      this.customerTableLoading = true;
      let reqPage = page && page > 0 ? page : 1;
      let reqSize = size && size > 0 ? size : this.tableCurrPageSize;
      let reqCustomerNo = null;
      let reqData = {
        page: reqPage,
        size: reqSize,
        categoryId: categoryId,
        search: this.searchVal
      };
      util.ajax
        .get("/customer/list", { params: reqData })
        .then(response => {
          let result = response.data;
          this.customersData = result.data;
          this.customersCount = result.count;
          this.customerTableLoading = false;
          this.currCustomerId = "";
          this.$refs.customersTable.clearCurrentRow();
        })
        .catch(error => {
          util.errorProcessor(this, error);
          this.customerTableLoading = false;
        });
    },

    searchBtnClicked() {
      let currCategoryId = this.selectedCategory
        ? this.selectedCategory.id
        : "";
      this.loadCustomerList(
        this.tableCurrPage,
        this.tableCurrPageSize,
        currCategoryId
      );
    },

    tableSelecttionChange(data) {
      this.tableSelectionData = data;
    },

    tableRowClick(data) {
      this.currCustomerId = data.id;
    },

    addCustomer() {
      this.currCustomerId = "";
      this.$refs.customersTable.clearCurrentRow();
    },

    customerAddSuccess(data) {
      this.searchBtnClicked();
    },

    customerUpdateSuccess(data) {
      // 刷新客户列表数据
      let self = this;
      this.customersData.forEach((item, index) => {
        if (data.id === item.id) {
          self.$set(self.customersData, index, data);
        }
      });
    },

    delCustomers() {
      if (this.tableSelectionData.length <= 0) {
        this.$Message.warning("请先选择需要删除的客户信息");
        return;
      }
      this.customerTableLoading = true;
      let delData = [];
      for (let i = 0; i < this.tableSelectionData.length; i++) {
        let item = this.tableSelectionData[i];
        delData.push(item.id);
      }
      let self = this;
      this.$Modal.confirm({
        title: "删除确认",
        content: "确认删除选着的客户信息？",
        onOk: () => {
          util.ajax
            .post("/customer/delete", delData)
            .then(response => {
              self.customerTableLoading = false;
              self.searchBtnClicked();
              let delCount = response.data.count;
              self.$Message.success("成功删除" + delCount + "条客户信息");
            })
            .catch(error => {
              self.customerTableLoading = false;
              util.errorProcessor(self, error);
            });
        },
        onCancel: () => {}
      });
    }
  }
};
</script>


<style >
.spin-icon-load {
  animation: ani-demo-spin 1s linear infinite;
}
</style>



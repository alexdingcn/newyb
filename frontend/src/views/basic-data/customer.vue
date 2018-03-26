<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Row>
          <Col span="7">
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
                <Dropdown trigger="custom" :visible="dropDawnVisible" placement="bottom-start" style="width:100%;">
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
                    <Input v-model="searchVal" >
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
          </Col>
        
        <Col span="17" class="padding-left-10">
            <customer-info 
              :action="customerAction" 
              :categorys="categorys" 
              :editCustomer="tableCurrClickData" 
              @add-success="customerAddSuccess" 
              @update-success="customerUpdateSuccess" 
              @add-to-edit="customerAddToEditView" 
              ></customer-info>
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
import util from '@/libs/util.js';
import dataConver from '@/libs/data-conver.js';
import customerCategory from '@/views/customer/customer-category.vue';
import customerInfo from '@/views/customer/customer-info.vue';

export default {
    name: 'customer',
    components: {
        customerCategory,
        customerInfo
    },
    data () {
        return {
            dropDawnVisible: false,
            breadcrumbList: [],
            categoryModal: false,
            customerCategoryAction: 'add',
            categorys: [],
            selectedCategory: null,
            searchSelectVal: 'searchByName',
            searchVal: '',
            customerTableLoading: false,
            customersCount: 0,
            tableCurrPage: 1,
            tableCurrPageSize: 20,
            tableSelectionData: [],
            tableCurrClickData: {},
            customersData: [],
            orderColumns: [
                {
                    type: 'selection',
                    width: 60,
                    align: 'center',
                    fixed: 'left'
                },
                {
                    title: '客户名称',
                    key: 'name',
                    align: 'center',
                    fixed: 'left',
                    sortable: true
                }
            ],
            customerAction: 'add'
        };
    },
    mounted () {
        this.getCategoryArr();
    },
    computed: {
        disableDelCategory () {
            return !(this.selectedCategory && this.selectedCategory.id > 0);
        },
        customerCat () {
            let attr = {
                rootId: -1,
                idKey: 'id',
                titleKey: 'name',
                parentKey: 'parentId',
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
        categorys () {
            this.selectedCategory = null;
        },
        selectedCategory () {
            this.changeSelectedCategory();
        }
    },
    methods: {
        openDropDownClick () {
            this.dropDawnVisible = !this.dropDawnVisible;
        },

        changeSelectedCategory () {
            this.changebreadcrumbList();
            if (this.selectedCategory && this.selectedCategory.id) {
                this.selectCustomerByCategory(this.selectedCategory.id);
            }
        },

        changebreadcrumbList () {
            if (this.selectedCategory && this.selectedCategory.id) {
                let result = [];
                let curr = this.selectedCategory;
                if (!curr) {
                    this.breadcrumbList = [];
                }
                result.push(curr);
                let haveParent = (curr.parentId && curr.parentId > 0);
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

        getParentCategory (data) {
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

        getCategoryArr () {
            util.ajax
                .get('/customer/category/list')
                .then((res) => {
                    this.categorys = res.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        openAddCategoryModal () {
            this.customerCategoryAction = 'add';
            this.categoryModal = true;
        },

        openEditCategoryModal () {
            this.customerCategoryAction = 'edit';
            this.categoryModal = true;
        },

        onTreeNodeSelected (data) {
            let chooseData = data[0];
            if (!chooseData) {
                return;
            }
            if (chooseData.id <= 0) {
                this.selectCustomerByCategory(); // 全选
                return;
            }
            this.dropDawnVisible = false;
            this.selectedCategory = dataConver.selectObjectById(data[0].id, this.categorys);
        },

        onCategorySubmit (submitData) {
            this.categoryModal = false;
            this.getCategoryArr();
        },

        onDialogClosed () {
            this.categoryModal = false;
        },

        delCategory () {
            let delData = this.selectedCategory;
            if (!delData || !delData.id || delData.id <= 0) {
                return;
            }
            let removeUrl = '/customer/category/remove/' + delData.id;
            util.ajax
                .delete(removeUrl)
                .then((response) => {
                    this.$Message.success('客户类信信息删除成功');
                    this.getCategoryArr();
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        selectCustomerByCategory (categoryId) {
            this.loadCustomerList(this.tableCurrPage, this.tableCurrPageSize, categoryId);
        },

        pageChange (data) {
            this.tableCurrPage = data;
            let currCategoryId = this.selectedCategory
                ? this.selectedCategory.id
                : '';
            this.loadCustomerList(this.tableCurrPage, this.tableCurrPageSize, currCategoryId);
        },

        loadCustomerList (page, size, categoryId) {
            this.customerTableLoading = true;
            let reqPage = page && page > 0 ? page : 1;
            let reqSize = size && size > 0 ? size : this.tableCurrPageSize;
            let reqCustomerNo = null;
            let reqData = {
                page: reqPage,
                size: reqSize,
                categoryId: categoryId
            };
            if (this.searchVal && this.searchVal != '') {
                if (this.searchSelectVal === 'searchByName') {
                    reqData.customerName = this.searchVal;
                } else if (this.searchSelectVal === 'searchByNo') {
                    reqData.customerNo = this.searchVal;
                }
            }
            util.ajax
                .get('/customer/list', { params: reqData })
                .then((response) => {
                    let result = response.data;
                    this.customersData = result.data;
                    this.customersCount = result.count;
                    this.customerTableLoading = false;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                    this.customerTableLoading = false;
                });
        },

        searchBtnClicked () {
            let currCategoryId = this.selectedCategory
                ? this.selectedCategory.id
                : '';
            this.loadCustomerList(this.tableCurrPage, this.tableCurrPageSize, currCategoryId);
        },

        tableSelecttionChange (data) {
            this.tableSelectionData = data;
        },

        tableRowClick (data) {
            this.tableCurrClickData = data;
            this.customerAction = 'edit';
        },

        addCustomer () {
            this.tableCurrClickData = null;
            this.customerAction = 'add';
        },

        customerAddToEditView () {
            this.customerAction = 'edit';
        },

        customerAddSuccess (data) {
            if (this.changeSelectedCategory) {
                // 新建客户信息成功，只有在选择了产品组的时候才刷新列表
                this.searchBtnClicked();
            }
        },

        customerUpdateSuccess (data) {
            this.searchBtnClicked(); // 刷新客户列表数据
        },

        delCustomers () {
            if (this.tableSelectionData.length <= 0) {
                this.$Message.warning('请先选择需要删除的客户信息');
                return;
            }
            this.customerTableLoading = true;
            let delData = [];
            for (let i = 0; i < this.tableSelectionData.length; i++) {
                let item = this.tableSelectionData[i];
                delData.push(item.id);
            }
            util.ajax
                .post('/customer/delete', delData, {headers: {'Content-Type': 'application/json'}})
                .then((response) => {
                    this.customerTableLoading = false;
                    this.searchBtnClicked();
                    let delCount = response.data.count;
                    this.$Message.success('成功删除' + delCount + '条客户信息');
                })
                .catch((error) => {
                    this.customerTableLoading = false;
                    util.errorProcessor(this, error);
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



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
                    <Button type="primary" icon="android-add-circle" size="small" @click="addCategory">添加</Button>
                    <Button type="success" icon="edit" size="small" @click="editCategory" :disabled="disableDelCategory"></Button>
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
                    <Input v-model="value13" style="width: 55%;">
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
                        <Page :total="customersCount" show-total show-sizer></Page>
                    </Row>
                </Row>
            </Card>
        </Col>
      </Row>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import dataConver from "@/libs/data-conver.js";

export default {
  name: "client",
  data() {
    return {
      disableDelCategory: true,
      categorys: [],
      selectedCategory: "",
      searchSelectVal: "searchByName",
      customersData: [],
      orderColumns: [
        {
          type: "selection",
          width: 60,
          align: "center",
          fixed: 'left'
        },
        {
            type: 'index',
            width: 80,
            title: '序号',
            align: 'center',
            fixed: 'left'
        },
        {
            width: 150,
            title: '客户名称',
            key: 'name',
            align: 'center',
            fixed: 'left',
            sortable: true,
            render: (h, params) => {
                return h('Button',{
                    props: {
                        type: 'text',
                        size: 'small'
                    },
                    on: {
                        click: () => {
                            let argu = {customer_id: params.row.id};
                            this.$router.push({
                                name: 'customer-info',
                                params: argu
                            });
                        }
                    }
                }, params.row.name);
            }
        },
        {
            width: 120,
            title: '客户编号',
            key: 'customerNo',
            align: 'center',
            sortable: true,
            render: (h, params) => {
                return h('Button',{
                    props: {
                        type: 'text',
                        size: 'small'
                    },
                    on: {
                        click: () => {
                            let argu = {customer_id: params.row.id};
                            this.$router.push({
                                name: 'customer-info',
                                params: argu
                            });
                        }
                    }
                }, params.row.customerNo);
            }
        },
        {
            width: 120,
            title: '是否禁用',
            key: 'enabled',
            align: 'center',
            sortable: true,
            render: (h, params) => {
                let isTrue = params.row.enabled;
                return h('div', [
                    h('Icon', {
                        props: {
                            type: isTrue ? 'checkmark-circled' : 'close-circled',
                            color: isTrue ? '#00a854' : '#e96500'
                        }
                    }),
                    h('strong', isTrue ? '已启用' : '已禁用')
                ]);
            }
        },
        {
            width: 100,
            title: '客户类别',
            key: 'categoryId',
            align: 'center',
            render: (h, params) => {
                let categoryItem = this.selectObjectById(params.row.categoryId, this.categorys);
                return h('span', categoryItem.name);
            }
        },
        {
            width: 100,
            title: '城市',
            key: 'city',
            align: 'center'
        },
        {
            width: 100,
            title: '法定代表人',
            key: 'legalPersion',
            align: 'center'
        },
        {
            width: 100,
            title: '负责人',
            key: 'employee',
            align: 'center'
        },
        {
            width: 100,
            title: '联系电话',
            key: 'contactPhone',
            align: 'center'
        },
        {
            width: 100,
            title: '经营范围',
            key: 'businessScope',
            align: 'center'
        },
        {
            width: 100,
            title: '创建人',
            key: 'createBy',
            align: 'center'
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
            idKey: 'id',
            titleKey: 'name',
            parentKey: 'parentId',
            expand: false
        };
        return dataConver.arryToIviewTreeData(this.categorys, attr);
    }
  },
  methods: {
    getCategoryArr() {
        util.ajax.get('/customer/category/list')
            .then((res) => {
                this.categorys = res.data;
            })
            .catch((error) => {
                console.log(error);
            });
    },

    addCategory() {},

    editCategory() {},

    delCategory() {},

    onTreeNodeSelected() {},

    searchBtnClicked() {},

    addCustomer() {},

    delCustomers() {},

    selectObjectById(id, arrs) {
        let result = null;
        if (!arrs || arrs.length <= 0) {
            return result;
        }
        for(let item in arrs) {
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

</style>



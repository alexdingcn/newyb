<style lang="less">
    @import '../../styles/common.less';
    @import './supplier.less';
</style>

<template>
    <div class="access">
        <Row>
            <Card>
                <p slot="title">
                    <Icon type="android-contacts"></Icon> 供应商
                </p>
                <div slot="extra">
                    <Input v-model="searchSupplierVal" placeholder="企业名称/联系人/拼音简称" clearable style="width: 300px"
                           @on-enter="validateSearch"></Input>
                    <Button type="primary" shape="circle" icon="ios-search" @click="searchSupplier"></Button>
                    <ButtonGroup class="padding-left-20">
                        <Button type="primary" icon="android-add-circle" @click="addSupplier">添加</Button>
                        <Button type="error" icon="android-remove-circle"  @click="delSupplier">删除</Button>
                    </ButtonGroup>
                </div>

                <Row type="flex" justify="center" align="middle" class="advanced-router margin-top-8">
                    <Table border highlight-row :columns="orderColumns" :data="supplierData" ref="supplierTable" style="width: 100%;" size="small"></Table>
                </Row>
            </Card>
        </Row>
    </div>
</template>

<script>
    import axios from 'axios';
    import util from '@/libs/util.js';

    export default {
        name: 'supplier',
        data () {
            return {
                searchSupplierVal: '',
                supplierData: [],
                orderColumns: [
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center'
                    },
                    {
                        type: 'index',
                        title: '',
                        width: 40,
//                    fixed: 'left'
                    },
                    {
                        title: '名称',
                        key: 'name',
                        align: 'center',
                        width: 200,
                        sortable: true,
//                    fixed: 'left',
                        render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'text',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                let argu = { supplier_id: params.row.id };
            this.$router.push({
                name: 'supplier-info',
                params: argu
            });
        }
        }
        }, params.row.name);
        }
        },
            {
                title: '产地',
                        key: 'origin',
                    width: 100,
                    align: 'center',
                    sortable: true
            },
            {
                title: '拼音简称',
                        key: 'pinyin',
                    width: 120,
                    align: 'center',
                    sortable: true
            },
            {
                title: '负责人',
                        key: 'employee',
                    width: 80,
                    align: 'center',
            },
            {
                title: '联系人',
                        key: 'contact',
                    width: 200,
                    align: 'center',
                    sortable: true
            },
            {
                title: '联系人电话',
                        key: 'contactPhone',
                    width: 200,
                    align: 'center'
            }
        ]
        };
        },
        mounted() {
            this.loadSupplierList();
        },
        computed: {

        },
        methods: {
            validateSearch: function(e) {
                this.searchSupplier();
            },
            searchSupplier() {
                var self = this;
                util.ajax.post('/supplier/search', {search: this.searchSupplierVal})
                        .then(function (response) {
                            self.supplierData = response.data;
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
            },
            loadSupplierList () {
                var self = this;
                util.ajax.get('/supplier/list')
                        .then(function (response) {
                            self.supplierData = response.data;
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
            },
            addSupplier() {
                this.$router.push({ name: 'supplier-info' });
            },
            delSupplier() {
                // TODO add confirm
                var row = this.$refs.supplierTable.getSelection();
                if (row && row.length > 0) {
                    var self = this;
                    var delArr = [];
                    for (var i=0; i< row.length; i++) {
                        delArr.push(row[i].id);
                    }
                    util.ajax.post('/supplier/remove', {ids: delArr} )
                            .then(function (response) {
                                self.loadSupplierList();
                            })
                            .catch(function (error) {
                                console.log(error);
                            })
                } else {
                    this.$Message.warning("请选择一个供应商后操作");
                }
            }
        }
    };
</script>

<style>

</style>

<style lang="less">
    @import '../../styles/common.less';
    /*@import './good.less';*/
</style>

<template>
    <div class="access">
        <Row>
            <Card>
                <p slot="title">
                    <Icon type="ios-flask-outline"></Icon> 生产企业
                </p>
                <div slot="extra">
                    <Input v-model="searchFactoryVal" placeholder="企业名称/联系人/拼音简称" clearable style="width: 300px"
                           @on-enter="validateSearch"></Input>
                    <Button type="primary" shape="circle" icon="ios-search" @click="searchFactory"></Button>
                    <ButtonGroup class="padding-left-20">
                        <Button type="primary" icon="android-add-circle" @click="addFactory">添加</Button>
                        <Button type="error" icon="android-remove-circle"  @click="delFactory">删除</Button>
                    </ButtonGroup>
                </div>

                <Row type="flex" justify="center" align="middle" class="advanced-router margin-top-8">
                    <Table border highlight-row :columns="orderColumns" :data="factoryData" ref="factoryTable" style="width: 100%;" size="small"></Table>
                </Row>
            </Card>
        </Row>
    </div>
</template>

<script>
    import axios from 'axios';
    import util from '@/libs/util.js';

    export default {
        name: 'factory',
        data () {
            return {
                searchFactoryVal: '',
                factoryData: [],
                orderColumns: [
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center'
                    },
                    {
                        type: 'index',
                        title: '',
                        width: 40
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
                                        let argu = { factory_id: params.row.id };
                                        this.$router.push({
                                            name: 'factory-info',
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
                        align: 'center'
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
        mounted () {
            this.loadFactoryList();
        },
        computed: {

        },
        methods: {
            validateSearch: function (e) {
                this.searchFactory();
            },
            searchFactory () {
                var self = this;
                util.ajax.post('/factory/search', {search: this.searchFactoryVal})
                    .then(function (response) {
                        self.factoryData = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            loadFactoryList () {
                var self = this;
                util.ajax.get('/factory/list')
                    .then(function (response) {
                        self.factoryData = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            addFactory () {
                this.$router.push({ name: 'factory-info' });
            },
            delFactory () {
                // TODO add confirm
                var row = this.$refs.factoryTable.getSelection();
                if (row && row.length > 0) {
                    var self = this;
                    var delArr = [];
                    for (var i = 0; i < row.length; i++) {
                        delArr.push(row[i].id);
                    }
                    util.ajax.post('/factory/remove', {ids: delArr})
                        .then(function (response) {
                            self.loadFactoryList();
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                } else {
                    this.$Message.warning('请选择一个生产企业后操作');
                }
            }
        }
    };
</script>

<style>

</style>

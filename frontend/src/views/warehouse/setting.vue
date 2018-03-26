<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div class="access">
        <Row>
            <Col span="16">
                <Card>
                    <p slot="title">
                        <Icon type="social-buffer"></Icon> 仓库点
                    </p>
                    <div slot="extra">
                        <ButtonGroup class="padding-left-20">
                            <Button type="primary" icon="android-add-circle" @click="warehouseModalShow = true">添加</Button>
                            <Button type="error" icon="android-remove-circle"  @click="delWarehouse">删除</Button>
                        </ButtonGroup>
                    </div>

                    <Row type="flex" justify="center" align="middle" class="advanced-router margin-top-8">
                        <Table border highlight-row :columns="orderColumns" :data="warehouseData" ref="warehouseTable" style="width: 100%;" size="small"></Table>
                    </Row>
                </Card>
            </Col>
        </Row>
        <Modal v-model="warehouseModalShow" width="360">
            <p slot="header">
                <Icon type="ios-plus-outline"></Icon>
                <span>新增仓库</span>
            </p>
            <div style="text-align:center">
                <Row>
                    <Input v-model="newWarehouse.name" placeholder="仓库名" />
                    <br>
                    <Input v-model="newWarehouse.description" placeholder="描述" />
                </Row>
            </div>
            <div slot="footer">
                <Button type="primary" @click="handleAddWarehouse">增加</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
    import axios from 'axios';
    import moment from 'moment';
    import util from '@/libs/util.js';

    export default {
        name: 'warehouse',
        data () {
            return {
                warehouseModalShow: false,
                warehouseData: [],
                newWarehouse: {
                    name: '',
                    description: ''
                },
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
                    },
                    {
                        title: '名称',
                        key: 'name',
                        align: 'center',
                        width: 150
                    },
                    {
                        title: '描述',
                        key: 'description',
                        align: 'center',
                        width: 250
                    },
                    {
                        title: '创建时间',
                        key: 'createdTime',
                        align: 'center',
                        render: (h, params) => {
                            return moment(params.row.createdTime).format('YYYY-MM-DD hh:mm:ss');
                        }
                    }
                ]
            };
        },
        mounted () {
            this.loadWarehouseList();
        },
        computed: {

        },
        methods: {
            validateSearch: function (e) {
                this.searchSupplier();
            },
            loadWarehouseList () {
                var self = this;
                util.ajax.get('/warehouse/list')
                    .then(function (response) {
                        self.warehouseData = response.data;
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            handleAddWarehouse () {
                var self = this;
                util.ajax.post('/warehouse/add', this.newWarehouse)
                    .then(function (response) {
                        self.loadWarehouseList();
                        self.warehouseModalShow = false;
                        self.newWarehouse = {};
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            },
            delWarehouse () {
                // TODO add confirm
                var row = this.$refs.warehouseTable.getSelection();
                if (row && row.length > 0) {
                    var self = this;
                    var delArr = [];
                    for (var i = 0; i < row.length; i++) {
                        delArr.push(row[i].id);
                    }
                    util.ajax.post('/warehouse/remove', {ids: delArr})
                        .then(function (response) {
                            self.loadWarehouseList();
                        })
                        .catch(function (error) {
                            console.log(error);
                        });
                } else {
                    this.$Message.warning('请选择一个仓库后操作');
                }
            }
        }
    };
</script>

<style>

</style>

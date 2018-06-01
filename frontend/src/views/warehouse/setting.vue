<style lang="less">
    @import '../../styles/common.less';
</style>

<template>
    <div class="access">
        <Row :gutter="16">
            <i-col span="14">
                <Card>
                    <p slot="title">
                        <Icon type="social-buffer"></Icon> 仓库点
                    </p>
                    <div slot="extra">
                        <ButtonGroup class="padding-left-20" size="small">
                            <Button type="primary" icon="android-add-circle" @click="warehouseModalShow = true">添加</Button>
                            <Button type="success" icon="edit" @click="doEdit">修改</Button>
                            <Button type="error" icon="android-remove-circle"  @click="delWarehouse">删除</Button>
                        </ButtonGroup>
                    </div>

                    <Row type="flex" justify="center" align="middle" class="advanced-router margin-top-8">
                        <Table border highlight-row :columns="warehouseColumns" :data="warehouseData"
                            ref="warehouseTable" style="width: 100%;" size="small" 
                            @on-row-click="warehouseTableRowClick"
                            no-data-text="点击上方添加按钮新建仓库"
                         ></Table>
                    </Row>
                </Card>
            </i-col>
            <i-col span="10">
                <Card>
                    <p slot="title">
                        <Icon type="ios-albums"></Icon> 库位 {{warehouseNameTitle ||  ''}}
                    </p>
                    <div slot="extra">
                        <ButtonGroup class="padding-left-20"  size="small">
                            <Button type="primary" icon="android-add-circle" @click="locationModalShow = true">添加库位</Button>
                            <Button type="error" icon="android-remove-circle"  @click="delWarehouseLocation">删除库位</Button>
                        </ButtonGroup>
                    </div>
                    <Row type="flex" justify="center" align="middle" class="advanced-router margin-top-8">
                        <Table border highlight-row :columns="locationColumns" :data="locationData"
                         ref="locationTab" style="width: 100%;" size="small" :loading="locationDataLoading" 
                         @on-selection-change="locationSelectedChange" 
                         no-data-text='点击上方添加库位按钮新建仓库库位'
                         ></Table>
                    </Row>
                </Card>
            </i-col>
        </Row>
        <Modal v-model="warehouseModalShow" width="360" :mask-closable="false">
            <p slot="header">
                <Icon type="ios-plus-outline"></Icon>
                <span>新增仓库</span>
            </p>
            <div style="text-align:center">
                <Input v-model="newWarehouse.name" placeholder="仓库名" />
                <Input v-model="newWarehouse.description" placeholder="描述" class="margin-top-10"/>
                <Input v-model="newWarehouse.address" placeholder="地址" class="margin-top-10"/>
            </div>
            <div slot="footer">
                <Button type="primary" @click="handleAddWarehouse" v-if="!newWarehouse.id">增加</Button>
                <Button type="success" @click="handleAddWarehouse" v-if="newWarehouse.id">保存</Button>
            </div>
        </Modal>

        <Modal v-model="locationModalShow" width="360" :mask-closable="false">
            <p slot="header">
                <Icon type="ios-plus-outline"></Icon>
                <span>新增仓库库位</span>
            </p>
            <div style="text-align:center">
                <Input v-model="newLocation.location" placeholder="库位名称" />
                <Input v-model="newLocation.comment" placeholder="描述" class="margin-top-10"/>
            </div>
            <div slot="footer">
                <Button type="primary" @click="handleAddLocation(true)">增加</Button>
                <Button type="primary" @click="handleAddLocation(false)">增加并关闭</Button>
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
                    description: '',
                    address: ''
                },
                warehouseColumns: [
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center'
                    },
                    {
                        title: '名称',
                        key: 'name',
                        align: 'center',
                        width: 120
                    },
                    {
                        title: '状态',
                        key: 'status',
                        align: 'center',
                        width: 120,
                        render: (h, params) => {
                            let status = params.row.status;
                            switch (status) {
                                case 'NORMAL':
                                    return h('Tag', {props: {type: 'dot', color: 'green'}}, '正常');
                                case 'FROZEN': 
                                    return h('Tag', {props: {type: 'dot', color: 'red'}}, '冻结');
                                default: 
                                    return h('span', '');
                            }
                        }
                    },
                    {
                        title: '描述',
                        key: 'description',
                        align: 'center',
                        width: 200
                    },
                    {
                        title: '地址',
                        key: 'address',
                        width: 250
                    },
                    {
                        title: '创建时间',
                        key: 'createdTime',
                        align: 'center',
                        width: 150,
                        render: (h, params) => {
                            return moment(params.row.createdTime).format('YYYY-MM-DD hh:mm:ss');
                        }
                    }
                ],
                warehouseNameTitle: '',
                currWarehouseId: '',
                locationModalShow: false,
                newLocation: {
                    location: '',
                    comment: ''
                },
                locationSelectList: [],
                locationDataLoading: false,
                locationData: [],
                locationColumns: [
                    {
                        type: 'selection',
                        width: 60,
                        align: 'center'
                    },
                    {
                        title: '名称',
                        key: 'location',
                        align: 'center',
                    },
                    {
                        title: '描述',
                        key: 'comment',
                        align: 'center',
                    }, 
                    {
                        title: '创建者',
                        key: 'createBy',
                        align: 'center'
                    },
                    {
                        title: '创建时间',
                        key: 'createTime',
                        align: 'center',
                        width: 150,
                        render: (h, params) => {
                            return params.row.createTime ? moment(params.row.createTime).format('YYYY-MM-DD hh:mm:ss') : '';
                        }
                    }
                ],
                location
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
                        util.errorProcessor(self, error);
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
                        util.errorProcessor(self, error);
                    });
            },
            delWarehouse () {
                this.$Modal.confirm({
                    title: '操作确认',
                    content: '是否确认删除，删除后不可恢复',
                    onOk: () => {
                        this.doDelete();
                    },
                    onCancel: () => {}
                });
            },

            doEdit() {
                var rows = this.$refs.warehouseTable.getSelection();
                if (rows && rows.length > 0) {
                    var self = this;
                    self.newWarehouse = rows[0];
                    self.warehouseModalShow = true;
                }
            },

            doDelete() {
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
                            util.errorProcessor(self, error);
                        });
                } else {
                    this.$Message.warning('请选择一个仓库后操作');
                }
            },

            loadWarehouseLocationList(warehouseId) {
                if (!warehouseId) {
                    this.locationData = [];
                    return;
                }
                var self = this;
                this.locationDataLoading = true;
                util.ajax.get('/warehouse/location/list', {params: {warehouseId: warehouseId}})
                    .then((response) => {
                        this.locationDataLoading = false;
                        self.locationData = response.data;
                    })
                    .catch((error) => {
                        this.locationDataLoading = false;
                        util.errorProcessor(self, error);
                    })
            },

            warehouseTableRowClick(rowData, index) {
                if (!rowData) {
                    this.currWarehouseId = '';
                    this.warehouseNameTitle = '';
                    this.locationData = [];
                    return;
                }
                this.currWarehouseId = rowData.id;
                this.warehouseNameTitle = rowData.name;
                this.loadWarehouseLocationList(rowData.id);
            },

            delWarehouseLocation() {
                this.$Modal.confirm({
                    title: '操作确认',
                    content: '是否确认删除库位，删除后不可恢复',
                    onOk: () => {
                        this.doDeleteLocation();
                    },
                    onCancel: () => {}
                });
            },

            doDeleteLocation() {
                if (!this.locationSelectList || this.locationSelectList.length <= 0) {
                    this.$Message.warning('请选择需要删除的仓库库位后操作');
                    return;
                }
                var self = this;
                var delArr = [];
                for (var i = 0; i < self.locationSelectList.length; i++) {
                    delArr.push(self.locationSelectList[i].id);
                }
                util.ajax.post('/warehouse/location/remove', {ids: JSON.stringify(delArr)})
                    .then(function (response) {
                        self.$Message.success('删除成功');
                        self.loadWarehouseLocationList(self.currWarehouseId);
                        
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            },

            locationSelectedChange(data) {
                this.locationSelectList = data;
            },

            handleAddLocation(closeModal) {
                var self = this;
                if (!this.currWarehouseId) {
                    this.$Message.warning('请先点击对应仓库选择需要添加库位的仓库');
                    return;
                }
                let reqData = {
                    warehouseId: this.currWarehouseId,
                    location: this.newLocation.location,
                    comment: this.newLocation.comment
                };
                util.ajax.post('/warehouse/location/add', reqData)
                    .then(function (response) {
                        self.$Message.success('添加成功');
                        self.loadWarehouseLocationList(self.currWarehouseId);
                        self.locationModalShow = closeModal;
                        self.newLocation = {};
                    })
                    .catch(function (error) {
                        util.errorProcessor(self, error);
                    });
            }

        }
    };
</script>

<style >

</style>

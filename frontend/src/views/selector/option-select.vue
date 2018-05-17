<template>
    <div>
        <div :class="wrapClasses">
            <Select v-model="optionId" clearable filterable :size="size" transfer 
                    :placeholder="selectPlaceHolder" @on-change="onChange">
                <Option v-for="item in optionList" :value="item.id" :key="item.id">{{ item.value }}</Option>
            </Select>
            <div class="ivu-input-group-append">
                <Button type="ghost" size="small"
                        icon="ios-settings-strong"
                        @click="optionModalShow = true;"></Button>
            </div>
        </div>

        <Modal v-model="optionModalShow" :footerHide="true" class="option-modal-position-show">
            <p slot="header">
                <Icon type="ios-settings-strong"></Icon>
                <span>{{showTitle || '属性配置'}}</span>
            </p>
            <div style="text-align:center">
                <Row type="flex" justify="end">
                    <Button type="primary" size="small" @click="openAddModal" >新增</Button>
                </Row>
                <Row class="margin-top-10 searchable-table-con1">
                    <Table ref="optionTab" size="small" style="width: 100%" border highlight-row 
                        :columns="unitColumns" :data="optionList">
                    </Table>
                </Row>
            </div>
        </Modal>

        <Modal v-model="optionModalAdd" width="300px" class="option-modal-position-add">
            <p slot="header">
                <Icon type="ios-settings-strong"></Icon>
                <span>{{newProperty.id ? '维护属性' : '新增属性'}}</span>
            </p>
            <div style="text-align:center">
                <Form>
                    <FormItem label="属性值" prop="value" >
                        <Input v-model="newProperty.value" placeholder="属性值" />
                    </FormItem>
                    <FormItem label="描述" prop="description" >
                        <Input v-model="newProperty.description" placeholder="描述" />
                    </FormItem>
                </Form>
            </div>
            <div slot="footer">
                <Button type="primary" @click="addNewProperty">保存</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import canEditTable from '../tables/components/canEditTable.vue';

const prefixCls = 'ivu-input';

export default {
    name: 'optionSelect',
    components: {
        canEditTable
    },
    props: {
        value: {
            type: Number|String,
            default: ''
        },
        disabled: {
            type: Boolean,
            default: false
        },
        optionType: {
            type: String,
            required: true
        },
        size: {
            type: String,
            default: 'default'
        },
        placement: {
            type: String,
            default: 'bottom'
        }
    },
    watch: {
        value(data) {
            this.optionId = data;
        }
    },
    data () {
        return {
            allTypes: [],
            prefixCls: prefixCls,
            optionId: '',
            newPropertyName: '',
            optionList: [],
            newProperty: {
                type: this.optionType
            },
            unitColumns: [
                {
                    key: 'value',
                    title: '属性名称'
                },
                {
                    key: 'description',
                    title: '属性名称'
                },
                {
                    title: '操作',
                    align: 'center',
                    width: 180,
                    render: (h, params) => {
                        return h('Button', {
                            props: {
                                type: 'text',
                                icon: 'edit',
                                size: 'small'
                            },
                            on: {
                                click: () => {
                                    this.newProperty = params.row;
                                    this.optionModalAdd = true;
                                }
                            }
                        }, '修改');
                    }
                }
            ],
            optionModalAdd: false,
            optionModalShow: false,
            selectPlaceHolder: this.placeholder,
        };
    },
    mounted() {
        this.initOptionType();
        this.loadOptionList();
    },
    computed: {
        wrapClasses () {
            return [
                `${prefixCls}-wrapper`,
                {
                    [`${prefixCls}-group`]: true,
                    [`${prefixCls}-group-with-append`]: true
                }
            ];
        },
        showTitle () {
            if (this.optionType && this.allTypes && this.allTypes.length > 0) {
                for (let i=0; i<this.allTypes.length; i++) {
                    if (this.optionType === this.allTypes[i].value) {
                        return this.allTypes[i].desc;
                    }
                }
            }
        }
    },
    methods: {
        initOptionType() {
            util.ajax.get('/options/type')
                .then ((response) => {
                    this.allTypes = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                })
        },

        loadOptionList() {
            let reqData = [this.optionType];
            let typeName = this.optionType;
            util.ajax.post("/options/list", reqData)
                .then((response) => {
                    this.newProperty = {
                        type: this.optionType
                    };
                    if (response.data && response.data[typeName]) {
                        this.optionList = response.data[typeName];
                    }else {
                        this.optionList = [];
                    }
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },
        onChange (data) {
            let items = this.optionList.filter(item => item.id === data);
            let item = '';
            if(items && items[0]) {
                item = items[0];
            }
            this.$emit('input', data);
            this.$emit('on-change', data, item);
        },
        openAddModal() {
            this.optionModalAdd = true;
            this.newProperty = {
                type: this.optionType
            };
        },
        addNewProperty() {
            util.ajax.post("/options/add", this.newProperty)
                .then(response => {
                    this.optionModalAdd = false;
                    this.loadOptionList();
                })
                .catch(error => {
                    util.errorProcessor(this, error);
                });
        }
    }

};
</script>
<style lang="less">

.option-modal-position-show {
    position: fixed;
    z-index: 2000;
}

.option-modal-position-add {
    position: fixed;
    z-index: 3000;
}

</style>

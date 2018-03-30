<template>
    <div :class="wrapClasses">
        <Select v-model="optionId" :placeholder="selectPlaceHolder" @on-change="onChange">
            <Option v-for="item in optionList" :value="item.id" :key="item.id">{{ item.value }}</Option>
        </Select>
        <div :class="[prefixCls + '-group-append']">
            <Button type="ghost" icon="ios-settings-strong" @click="optionModalShow = true;"></Button>
        </div>

        <Modal v-model="optionModalShow">
            <p slot="header">
                <Icon type="ios-settings-strong"></Icon>
                <span>商品属性配置</span>
            </p>

            <div style="text-align:center">
                <Row type="flex" justify="end">
                    <Button type="primary" @click="optionModalAdd = true" >新增</Button>
                </Row>
                <Row class="margin-top-10 searchable-table-con1">
                    <can-edit-table
                            size="small"
                            refs="table4"
                            v-model="optionList"
                            @on-change="handleChange"
                            :editIncell="true"
                            :columns-list="unitColumns"
                    ></can-edit-table>
                </Row>
            </div>
            <div slot="footer">
            </div>
        </Modal>

        <Modal v-model="optionModalAdd" width="300px">
            <p slot="header">
                <Icon type="ios-settings-strong"></Icon>
                <span>新增商品属性</span>
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
        disabled: {
            type: Boolean,
            default: false
        },
        optionType: {
            type: String,
            required: true
        },
        size: {
            validator (value) {
                return oneOf(value, ['small', 'large', 'default']);
            }
        },
    },
    data () {
        return {
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
                    title: '属性名称',
                    editable: true
                },
                {
                    key: 'description',
                    title: '属性名称',
                    editable: true
                },
                {
                    title: '操作',
                    align: 'center',
                    width: 180,
                    key: 'handle',
                    handle: ['edit','delete']
                }
            ],
            optionModalAdd: false,
            optionModalShow: false,
            selectPlaceHolder: this.placeholder,
        };
    },
    mounted() {
        this.loadOptionList();
    },
    computed: {
        wrapClasses () {
            return [
                `${prefixCls}-wrapper`,
                {
                    [`${prefixCls}-wrapper-${this.size}`]: !!this.size,
                    [`${prefixCls}-group`]: true,
                    [`${prefixCls}-group-${this.size}`]: !!this.size,
                    [`${prefixCls}-group-with-append`]: true,
                }
            ];
        },
    },
    methods: {
        loadOptionList() {
            let reqData = [this.optionType];
            util.ajax.post("/options/list", reqData)
                .then(response => {
                    this.optionList = response.data[this.optionType];
                })
                .catch(error => {
                    util.errorProcessor(this, error);
                });
        },
        handleCellChange (val, index, key) {
            this.$Message.success('修改了第 ' + (index + 1) + ' 行列名为 ' + key + ' 的数据');
        },
        handleChange (val, index) {
            this.$Message.success('修改了第' + (index + 1) + '行数据');
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
<style>
</style>

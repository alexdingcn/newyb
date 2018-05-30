
<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="log-out"></Icon>
              采购退出申请
          </p>
          <div slot="extra">
              <ButtonGroup size="small">
                  <Button type="success" icon="checkmark" :loading="saveLoading" @click="applySave" >确认保存</Button>
              </ButtonGroup>
          </div>

            <Form ref="applyForm" :model="formItem" :label-width="100" >
                <Row class="form-item-class">
                    <i-col span="6">
                        <FormItem label="仓库点" :error="warehouseError">
                            <warehouse-select ref="warehouseSelect" v-model="formItem.warehouseId" @on-change="onWarehouseChange"></warehouse-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="供应商">
                            <Input type="text" v-model="formItem.supplierName" disabled placeholder="根据选择的商品自动选择" />
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="供应商代表">
                            <supplier-contact-select :supplierId="formItem.supplierId" v-model="formItem.supplierContactId"></supplier-contact-select>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="采购员" :error="buyerError">
                            <buyer-select v-model="formItem.buyerId" ></buyer-select>
                        </FormItem>
                    </i-col>
                </Row>
                <Row class="form-item-class">
                    <i-col span="6">
                        <FormItem label="商品" :error="goodsError">
                            <Input type="text" placeholder="请选择退出商品" readonly @on-focus="chooseGoods"/>
                        </FormItem>
                    </i-col>
                    <i-col span="6">
                        <FormItem label="退货日期">
                            <DatePicker type="datetime" format="yyyy-MM-dd HH:mm:ss" placeholder="退货日期" v-model="formItem.backTime"></DatePicker>
                        </FormItem>
                    </i-col>
                    <i-col span="12">
                        <FormItem label="退货原因">
                            <Input type="text" placeholder="请输入退货原因" v-model="formItem.keyWord" />
                        </FormItem>
                    </i-col>
                </Row>

                <Table ref="table" border highlight-row size="small" 
                    no-data-text="在商品输入框选择后添加" :loading="saveLoading" 
                    :columns="tableColumns" :data="tableData">
                </Table> 

            </Form>
      </Card>

      <Modal v-model="selectRepertoryModal" width="70" :mask-closable="false" title="选择库存商品" >
          <repertory-info-select ref="repertoryGoodsSelector" :warehouse="currWarehouse" @on-choosed="repertoryInfoChoosed" ></repertory-info-select>
          <span slot="footer"></span>
      </Modal>

  </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';
import warehouseSelect from '@/views/selector/warehouse-select.vue';
import repertoryInfoSelect from '@/views/selector/repertory-info-select.vue';
import buyerSelect from '@/views/selector/buyer-select.vue';
import supplierContactSelect from '@/views/selector/supplier-contact-select.vue';

export default {
    name: 'back-apply',
    components: {
        warehouseSelect,
        repertoryInfoSelect,
        buyerSelect,
        supplierContactSelect
    },
    data() {
        return {
            saveLoading: false,
            currWarehouse: {},
            formItem: {
                warehouseId: '',
                supplierId: '',
                supplierContactId: '',
                supplierName: '',
                buyerId: '',
                backTime: moment().format('YYYY-MM-DD HH:mm:ss'),
                keyWord: '',
                details: []
            },
            tableData: [],
            tableColumns: [
                {
                    title: '货号',
                    key: 'goodsId',
                    width: 110
                },
                {
                    title: '商品名称',
                    key: 'goodsName',
                    width: 200,
                    sortable: true
                },
                {
                    title: '批次号',
                    key: 'batchCode',
                    width: 180
                },
                {
                    title: '产地',
                    key: 'origin',
                    width: 150
                },
                {
                    title: '剂型',
                    key: 'jx',
                    width: 120
                },
                {
                    title: '规格',
                    key: 'spec',
                    width: 120
                },
                {
                    title: '生产企业',
                    key: 'factoryName',
                    width: 150
                },
                {
                    title: '供应商',
                    key: 'supplierName',
                    width: 200
                },
                {
                    title: '单位',
                    key: 'unitName',
                    width: 80
                },
                {
                    title: '当前库存量',
                    key: 'quantity',
                    width: 100
                },
                {
                    title: '在单数量',
                    key: 'onWayQuantity',
                    width: 100
                },
                {
                    title: '采购数量',
                    key: 'inQuantity',
                    width: 100
                },
                {
                    title: '退出数量',
                    key: 'backQuantity',
                    width: 120,
                    render: (h, params) => {
                        var self = this;
                        return h('Input', {
                            props: {
                                value: self.tableData[params.index][params.column.key],
                                number: true
                            },
                            on: {
                                'on-change' (event) {
                                    var row = self.tableData[params.index];
                                    row[params.column.key] = event.target.value;
                                },
                                'on-blur' (event) {
                                    var row = self.tableData[params.index];
                                    var price = row['buyPrice'];
                                    var qty = event.target.value;
                                    if (!isNaN(qty) && !isNaN(price)) {
                                        row.amount = (qty * price).toFixed(2);
                                        self.$set(self.tableData, params.index, row);
                                    }
                                }
                            }
                        });
                    }
                },
                {
                    title: '单价',
                    key: 'buyPrice',
                    width: 120,
                    render: (h, params) => {
                        var self = this;
                        return h('Input', {
                            props: {
                                value: self.tableData[params.index][params.column.key],
                                number: true
                            },
                            on: {
                                'on-change' (event) {
                                    var row = self.tableData[params.index];
                                    row[params.column.key] = event.target.value;
                                },
                                'on-blur' (event) {
                                    var row = self.tableData[params.index];
                                    var qty = row['backQuantity'];
                                    var price = event.target.value;
                                    if (!isNaN(qty) && !isNaN(price)) {
                                        row.amount = (qty * price).toFixed(2);
                                        self.$set(self.tableData, params.index, row);
                                    }
                                }
                            }
                        });
                    }
                },
                {
                    title: '金额',
                    key: 'amount',
                    width: 120
                },
                {
                    title: '生产日期',
                    width: 120,
                    key: 'productData',
                    render: (h, params) => {
                        return h('span', moment(params.row.productDate).format('YYYY-MM-DD'));
                    }
                },
                {
                    title: '有效期至',
                    key: 'expDate',
                    width: 120,
                    render: (h, params) => {
                        return h('span', moment(params.row.expDate).format('YYYY-MM-DD'));
                    }
                },
                {
                    title: '库位',
                    key: 'location',
                    width: 120
                },
                {
                  title: '批准文号',
                  key: 'permitNo',
                  width: 140
              },
              {
                  title: '存储条件',
                  key: 'storageConditionName',
                  width: 120
              }
            ],
            selectRepertoryModal: false,
            warehouseError: '',
            buyerError: '',
            goodsError: ''
        }
    },
    computed: {
        repertotyIds () {
            let ids = [];
            if (!this.tableData || this.tableData.length <= 0) {
                return ids;
            }else {
                for (let i=0; i<this.tableData.length; i++) {
                    ids.push(this.tableData[i].repertoryId);
                }
                return ids;
            }
        }
    },
    methods: {
        onWarehouseChange(warehouseId, warehouse) {
            this.tableData = [];
            this.warehouseError = '';
            this.buyerError = '';
            this.goodsError = '';
            this.formItem = {
                warehouseId: '',
                supplierId: '',
                supplierContactId: '',
                supplierName: '',
                buyerId: '',
                backTime: moment().format('YYYY-MM-DD HH:mm:ss'),
                keyWord: '',
                details: []
            };
            if (warehouse.id) {
                this.currWarehouse = warehouse;
                this.formItem.warehouseId = warehouse.id;
            } else {
                this.currWarehouse = {};
                this.formItem.warehouseId = '';
            }
        },
        chooseGoods() {
            if (!this.formItem.warehouseId) {
                this.$Message.warning('请先选择对应仓库');
                return;
            }
            this.$refs.repertoryGoodsSelector.searchBtnClicked();
            this.selectRepertoryModal = true;
        },
        repertoryInfoChoosed(data) {
            this.selectRepertoryModal = false;
            if (!data || data.length <= 0) {
                return;
            }
            let supplierId = data[0].supplierId; //直接取第一个作为比较值
            let supplierName = data[0].supplierName;
            let supplierContactId = data[0].supplierContactId;
            if (this.formItem.supplierId > 0 && this.formItem.supplierId !== supplierId) {
                this.$Modal.warning({
                    title: '添加商品提示',
                    content: '选择的商品中只能时同一个供应商的商品信息, 当前选择的产品中存在不同的供应商!'
                });
                return;
            }
            this.formItem.supplierId = supplierId; 
            this.formItem.supplierName = supplierName;
            this.formItem.buyerId = data[0].buyerId;
            let self = this;
            setTimeout(() => {
                self.formItem.supplierContactId = supplierContactId;
            }, 600);
            //验证当前supplierId是否存在，如果存在，只能添加对应供应上的商品。
            //返回列表中的所有供应上必须是同一个
            for (let i=0; i<data.length; i++) {
                let item = data[i];
                if (supplierId !== item.supplierId) {
                    this.$Modal.warning({
                        title: '添加商品提示',
                        content: '选择的商品中只能时同一个供应商的商品信息, 当前选择的产品中存在不同的供应商!'
                    });
                    return;
                }
                //如果当前列表中已经存在当前
                if(util.oneOf(item.id, this.repertotyIds)) {
                    continue; //已经存在的直接跳过
                }
                item['backQuantity'] = 0;
                item['repertoryInfoId'] = item.id;
                this.tableData.push(item);
            }
        },

        applySave() {
            this.warehouseError = '';
            this.buyerError = '';
            this.goodsError = '';
            if (!this.formItem.warehouseId) {
                this.warehouseError = '请选择对应的仓库';
                return;
            }
            if (!this.formItem.buyerId) {
                this.buyerError = '请选择对应的采购员';
                return;
            }
            this.formItem.details = this.tableData;
            if (!this.formItem.details || this.formItem.details.length <= 0) {
                this.goodsError = '请添加需要退出的商品信息';
                return;
            }
            for(let i=0; i<this.formItem.details.length; i++) {
                let item = this.formItem.details[i];
                if (!item.backQuantity || isNaN(item.backQuantity) || item.backQuantity <= 0) {
                    this.$Message.warning('商品对应的退出数量不能小于等于0');
                    return;
                }
                let onWayCount = item.onWayQuantity ? item.onWayQuantity : 0;
                let count = item.quantity ? item.quantity : 0;
                console.log('onWayCount=' + onWayCount + ', quantity=' + count);
                if (item.backQuantity > (count- onWayCount)) {
                    this.$Message.warning('退货数不能大于存库数减去在单数.');
                    return;
                }
            }
            //验证通过后，直接弹出确认框
            let self = this;
            this.$Modal.confirm({
                title: '采购退出申请保存确认',
                content: '退出商品信息是否已经确认填写完成并且数量正确?',
                onCancel: () => {},
                onOk: () => {
                    self.saveLoading = true;
                    util.ajax.post('/buy/back/add', self.formItem)
                        .then((response) => {
                            self.saveLoading = false;
                            self.$Message.success('采购退出申请保存成功.');
                            self.$refs.warehouseSelect.clearSingleSelect();
                        })
                        .catch((error) => {
                            self.saveLoading = false;
                            util.errorProcessor(self, error);
                        })
                }
            });
            
        }
    }
  
}
</script>

<style scoped>
.form-item-class {
    margin-bottom: 1.2em;
}
</style>


<style lang="less">
    @import '../../styles/common.less';
    @import './goods-list.less';
</style>

<template>
  <div class="goods-list">
          <div class="list-sider" :style="{width: showSider ? '200px' : '0px'}">
            <goods-category v-show="showSider" @on-choose="categoryChoose" ></goods-category>
          </div>
          <div class="list-body" :style="{left: showSider ? '200px': '0px'}">
              <Card>
                    <p slot="title">
                        <a href="javascript:void(0)" @click="changeSiderShow" style="margin-right: 5px;" >
                            <Icon v-if="showSider" type="chevron-left"></Icon>
                            <Icon v-else type="chevron-right"></Icon>
                        </a>
                        <Icon type="bag"></Icon>
                        商品列表
                    </p>
                    <Row>
                        <i-col span="19">
                            <Row type="flex" justify="start">
                                <Input type="text" v-model="searchValue" placeholder="商品名称/拼音/编号" icon="search" @on-clik="refreshGoodsList" style="width: 250px"/>
                                <goods-brand-select v-model="searchGoodsBrandId" style="width: 150px; margin-left:10px; margin-right:10px;"></goods-brand-select>
                                <Select v-model="searchStatus" style="width: 90px; margin-right:10px;">
                                    <Option v-for="item in statusList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                                </Select>
                                <supplier-select v-model="searchSupplierId" style="width: 230px"></supplier-select>
                            </Row>
                        </i-col>
                        <i-col span="5">
                            <Row type="flex" justify="end">
                                <Button type="primary" icon="plus" @click="createGoods">新增商品</Button>
                            </Row>
                        </i-col>
                    </Row>
                </Card>
          </div>

          <Modal v-model="goodsModal" title="商品信息维护" :footerHide="true" :mask-closable="false" width="60">
              <goods-info ></goods-info>
          </Modal>
          
  </div>
</template>

<script>
import util from '@/libs/util.js';
import goodsCategory from './goods-category.vue';
import goodsBrandSelect from '@/views/selector/goods-brand-select.vue';
import supplierSelect from '@/views/selector/supplier-select.vue';
import goodsInfo from './goods-info.vue';

export default {
    name: 'goods-list',
    components: {
        goodsCategory,
        goodsBrandSelect,
        supplierSelect,
        goodsInfo,
    },
    data() {
        return {
            statusList: [{value:'ALL', label:'全部'}, {value:'ON_SALE', label: '上架'}, {value:'OFF_SALE', label: '下架'}],
            showSider: false,
            searchCategoryId: '',
            searchValue: '',
            searchGoodsBrandId: '',
            searchSupplierId: '',
            searchStatus: 'ALL',
            goodsModal: false,
        }
    },
    mounted() {
      this.refreshGoodsList();  
    },
    methods: {
        changeSiderShow() {
            this.showSider = !this.showSider;
        },
        categoryChoose(categoryId) {
            this.searchCategoryId = categoryId;
        },
        refreshGoodsList() {},

        createGoods() {
            this.goodsModal = true;
        }
    }

  
}
</script>


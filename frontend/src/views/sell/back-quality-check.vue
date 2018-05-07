
<template>
    <div>
        <Card >
            <p slot="title">
                <Icon type="erlenmeyer-flask"></Icon>
                销售退出质量复核
            </p>
            <div slot="extra">
                <ButtonGroup size="small">
                    <Button type="primary" icon="search" :loading="loading">查询</Button>
                    <Button type="success" icon="checkmark" :loading="loading">复核一单</Button>
                  <Button type="warning" icon="close" :loading="loading" >取消复核</Button>
                    <Button type="error" icon="trash-a" :loading="loading" >删除</Button>
                </ButtonGroup>
            </div>
            <Form ref="searchForm" :label-width="100">
              <Row type="flex" justify="start">
                  <i-col span="6">
                      <FormItem label="制单日期">
                          <DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="制单日期" style="width:180px"></DatePicker>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="仓库">
                          <warehouse-select v-model="warehouseId"></warehouse-select>
                      </FormItem>
                  </i-col>
                  <i-col span="6">
                      <FormItem label="客户">
                          <customer-select v-model="customerId"></customer-select>
                      </FormItem>
                  </i-col>
              </Row>
          </Form>

        </Card>
    </div>
</template>

<script>
import util from '@/libs/util.js';
import moment from 'moment';
import warehouseSelect from '@/views/selector/warehouse-select.vue';
import customerSelect from '@/views/selector/customer-select.vue';

export default {
    name: 'sell-back-quality-check',
    components: {
        warehouseSelect,
        customerSelect  
    },
    data() {
        return {
            loading: false,
            dateRange: [
                moment().add(-1, 'w').format('YYYY-MM-DD'),
                moment().add(1, 'd').format('YYYY-MM-DD')
            ],
            warehouseId: '',
            customerId: '',
        }
    }
  
}
</script>

<style>

</style>

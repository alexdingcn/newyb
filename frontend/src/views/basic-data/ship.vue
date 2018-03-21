<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <div class="search-div">
          <Row type="flex" justify="center">
              <Form ref="searchForm" :model="searchForm" :label-width="100">
                <Col span="8">
                    <FormItem label="名称">
                        <Input type="text" v-model="searchForm.name"></Input>
                    </FormItem>
                </Col>
                <Col span="8">
                    <FormItem label="营业执照">
                        <Input type="text" v-model="searchForm.license"></Input>
                    </FormItem>
                </Col>
                <Col span="2">
                    <Button type="primary" icon="ios-search" :loading="searching" @Click="searchBtnClick"> 查询 </Button>
                </Col>
                <Col span="6"></Col>
              </Form>
          </Row>
      </div>
      <div class="body-div">
          <Row type="flex" justify="center">
              <Table border highlight-row :columns="tabColumns" :data="tabData" 
                        :loading="searching" 
                        ref="table" style="width: 100%;" size="small">
                </Table>
          </Row>
          <Row type="flex" justify="end">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>
      </div>
  </div>
</template>

<script>
import util from "@/libs/util.js";

export default {
    name: 'ship',
    data() {
        return {
            searchForm: {
                name: '',
                license: ''
            },
            searching: false,
            tabData: [],
            totalCount: 0,
            currentPage: 1,
            pageSize: 20,
            tabColumns: [

            ]

        }
    },
    methods: {
        searchBtnClick() {
            this.currentPage = 1;
            this.refreshShipList();
        },
        pageChange(data) {
            this.currentPage = data;
            this.refreshShipList();
        },
        refreshShipList() {
            let reqData = {
                name: this.searchForm.name,
                license: this.searchForm.license,
                page: this.currentPage,
                size: this.pageSize
            }
            this.searching = true;
            util.ajax.get("/ship/list", {params: reqData})
                .then((response) => {
                    this.tabData = response.data.data;
                    this.totalCount = response.data.count;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
            this.searching = false;
        }


    }
  
}
</script>

<style>

</style>


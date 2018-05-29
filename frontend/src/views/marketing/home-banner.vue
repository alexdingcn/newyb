<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Card>
            <p slot="title">
                <Icon type="ios-compose-outline"></Icon>
                首页广告位
            </p>
            <div slot="extra">
                <Button type="success" icon="plus" :loading="loading" @click="addBtnClick">添加</Button>
            </div>

            <Table ref="bannerTbl" highlight-row size="small" border :loading="loading" 
                :columns="tableColumns" :data="tableData" @on-row-click="chooseRow">
            </Table>
            <Row type="flex" justify="end" class="margin-top-10">
                <Page size="small" :total="totalCount" :current="currentPage" :page-size="pageSize" show-total
                    @on-change="pageChange">
                </Page>
            </Row>

            <Modal v-model="addBannerModal" title="新建广告" :mask-closable="false" width="50" >
                <Form ref="addForm" :model="addFormItem" :label-width="80">
                    <Row>
                        <i-col span="12">
                            <FormItem label="开始时间">
                                <DatePicker type="date" v-model="addFormItem.startDate" transfer></DatePicker>
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="结束时间">
                                <DatePicker type="date" v-model="addFormItem.endDate" transfer></DatePicker>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="24">
                            <FormItem label="内容">
                                <Input v-model="addFormItem.content" />
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="24">
                            <FormItem label="图片链接" >
                                <Input v-model="addFormItem.imageUrl"/>
                            </FormItem>
                        </i-col>
                    </Row>
                    <Row>
                        <i-col span="16" offset="3">
                            <Upload
                                multiple
                                type="drag"
                                action="//jsonplaceholder.typicode.com/posts/">
                                <div style="padding: 20px 0">
                                    <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
                                    <p>点击或者拖入图片</p>
                                </div>
                            </Upload>
                        </i-col>
                    </Row>
                    <Row >
                        <i-col span="12">
                            <FormItem label="关联商品" >
                                <good-select v-model="addFormItem.goodsId"></good-select>
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="备注" >
                                <Input v-model="addFormItem.comment"/>
                            </FormItem>
                        </i-col>
                    </Row>
                </Form>

                <Row slot="footer" type="flex" justify="end">
                    <ButtonGroup shape="circle">
                        <Button type="success" icon="checkmark" @click="saveBanner" :loading="addLoading" >确认保存</Button>
                        <Button type="ghost" icon="reply" @click="saveCancel">取消</Button>
                    </ButtonGroup>
                </Row>
            </Modal>

      </Card>
  </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";
import goodSelect from "@/views/selector/good-select.vue";

const initBanner = {
  startDate: moment().format("YYYY-MM-DD HH:mm:ss"),
  endDate: moment().format("YYYY-MM-DD HH:mm:ss"),
  goodsId: "",
  content: "",
  comment: "",
  imageUrl: ""
};

export default {
  name: "home-banner",
  components: {
    goodSelect
  },
  data() {
    return {
      loading: false,
      currChooseRow: {},
      tableData: [],
      tableColumns: [
        {
          type: "index",
          width: 60
        },
        {
          title: "内容",
          key: "content"
        },
        {
          title: "图像URL",
          key: "imageUrl"
        },
        {
          title: "商品",
          key: "goodsId",
          width: 100
        },
        {
          title: "开始时间",
          key: "startDate",
          width: 120
        },
        {
          title: "结束时间",
          key: "endDate",
          width: 120
        },
        {
          title: "顺序",
          key: "order",
          width: 170
        }
      ],
      totalCount: 0,
      currentPage: 1,
      pageSize: 20,
      fileUploadModal: false,
      uploadfileNo: "",
      uploadFileType: "",

      addLoading: false,
      addBannerModal: false,
      addFormItem: initBanner
    };
  },
  mounted() {
    this.refreshTableData();
  },
  methods: {
    pageChange(data) {
      this.currentPage = data;
      this.refreshTableData();
    },
    refreshTableData() {
      let reqData = {
        page: this.currentPage,
        pageSize: this.pageSize
      };
      this.loading = true;
      util.ajax
        .post("/home/banner/list", reqData)
        .then(response => {
          this.loading = false;
          console.log(response);
          this.tableData = response.data.data;
          this.totalCount = response.data.count;
          this.currChooseRow = {};
          this.$refs.bannerTbl.clearCurrentRow();
        })
        .catch(error => {
          this.loading = false;
          util.errorProcessor(this, error);
        });
    },
    chooseRow(row) {
      this.currChooseRow = row;
    },

    openUploadFile(type, fileNo) {
      this.uploadFileType = type;
      this.uploadfileNo = fileNo;
      this.fileUploadModal = true;
    },
    addFileSuccess(data) {
      if (this.uploadFileType === "ADD") {
        this.addFormItem.fileNo = data.fileNo;
      }
    },

    addBtnClick() {
      this.addFormItem = initBanner;
      console.log(this.addFormItem);
      this.addBannerModal = true;
    },

    saveCancel() {
      this.addFormItem = initBanner;
      this.addBannerModal = false;
    },

    saveBanner() {
      let self = this;
      this.$Modal.confirm({
        title: "首页广告位",
        content: "确认添加？",
        onCancel: () => {},
        onOk: () => {
          self.addLoading = true;
          util.ajax
            .post("/home/banner/add", self.addFormItem)
            .then(response => {
              self.addLoading = false;
              self.$Message.success("添加首页广告成功");
              self.addBannerModal = false;
              self.refreshTableData();
            })
            .catch(error => {
              self.addLoading = false;
              util.errorProcessor(self, error);
            });
        }
      });
    }
  }
};
</script>

<style>
.ivu-table td {
  height: 2.5em;
}
.file-upload-modal {
  position: fixed;
  z-index: 1000;
}
</style>



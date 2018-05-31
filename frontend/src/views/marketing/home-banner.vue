<style lang="less">
@import "../../styles/common.less";
.banner-tbl {
  .actions .ivu-btn {
    display: none;
  }
  .ivu-table-row-hover .actions {
    .ivu-btn {
      display: block;
    }
  }
}
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

            <Table ref="bannerTbl" highlight-row size="small" border :loading="loading" class="banner-tbl"
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
                                <DatePicker type="datetime" v-model="addFormItem.startDate" transfer></DatePicker>
                            </FormItem>
                        </i-col>
                        <i-col span="12">
                            <FormItem label="结束时间">
                                <DatePicker type="datetime" v-model="addFormItem.endDate" transfer></DatePicker>
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
                                :on-success="handleUploadSuccess"
                                :format="['jpg','jpeg','png']"
                                :max-size="2048"
                                :on-format-error="handleFormatError"
                                :on-exceeded-size="handleMaxSize"
                                type="drag"
                                action=""
                                :before-upload="handleUpload">
                                <div style="padding: 20px 0">
                                    <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
                                    <p>点击或者拖入图片</p>
                                </div>
                            </Upload>
                        </i-col>
                    </Row>
                    <Row >
                        <i-col span="18">
                            <FormItem label="跳转地址" >
                                <Input v-model="addFormItem.url" />
                            </FormItem>
                        </i-col>
                        <i-col span="6">
                            <FormItem label="顺序" >
                                <Input v-model="addFormItem.sequence" />
                            </FormItem>
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
          width: 50
        },
        {
          title: "标题",
          key: "content"
        },
        {
          title: "图像URL",
          key: "imageUrl",
          render: (h, params) => {
            return h(
              "a",
              {
                attrs: {
                  href: params.row.imageUrl ? params.row.imageUrl : "#",
                  target: "_blank"
                }
              },
              [
                h("img", {
                  attrs: {
                    src: params.row.imageUrl
                      ? params.row.imageUrl + "?x-oss-process=style/resize200"
                      : ""
                  }
                })
              ]
            );
          }
        },
        {
          title: "商品",
          key: "goodsId",
          width: 100
        },
        {
          title: "跳转地址",
          key: "url",
          width: 100,
          render: (h, params) => {
            return h(
              "a",
              {
                attrs: {
                  href: params.row.url,
                  target: "_blank"
                }
              },
              params.row.url
            );
          }
        },
        {
          title: "开始时间",
          key: "startDate",
          width: 150,
          render: (h, params) => {
            return h(
              "span",
              moment(params.row.startDate).format("YYYY-MM-DD HH:mm:ss")
            );
          }
        },
        {
          title: "结束时间",
          key: "endDate",
          width: 150,
          render: (h, params) => {
            return h(
              "span",
              moment(params.row.endDate).format("YYYY-MM-DD HH:mm:ss")
            );
          }
        },
        {
          title: "顺序",
          key: "sequence",
          width: 80,
          render: (h, params) => {
            var self = this;
            let textSpan = h("span", params.row.sequence);
            let buttonE = h(
              "Button",
              {
                props: {
                  type: "ghost"
                },
                on: {
                  click: () => {
                    self.editBanner(params.row);
                  }
                }
              },
              "编辑"
            );
            let buttonH = h(
              "Button",
              {
                props: {
                  type: "error"
                },
                on: {
                  click: () => {
                    self.removeBanner(params.row.id);
                  }
                }
              },
              "删除"
            );
            return h(
              "div",
              {
                attrs: {
                  class: "actions"
                }
              },
              [textSpan, buttonE, buttonH]
            );
          }
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
      addFormItem: this.initBanner()
    };
  },
  mounted() {
    this.refreshTableData();
  },
  methods: {
    initBanner() {
      return {
        startDate: moment().format("YYYY-MM-DD HH:mm:ss"),
        endDate: moment()
          .add(1, "w")
          .format("YYYY-MM-DD HH:mm:ss"),
        goodsId: "",
        content: "",
        comment: "",
        imageUrl: "",
        url: "",
        sequence: 0
      };
    },
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
          console.log(response);
          this.loading = false;
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

    handleUpload(file) {
      let reqData = new FormData();
      var self = this;
      reqData.append("file", file);
      util.ajax
        .post("/file/upload", reqData, {
          headers: { "Content-Type": "multipart/form-data" }
        })
        .then(response => {
          if (response.status === 200) {
            self.addFormItem.imageUrl = response.data.url;
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
      return false;
    },
    handleUploadSuccess(res, file) {},
    handleFormatError(file) {
      this.$Notice.warning({
        title: "文件格式错误",
        desc: "文件格式必须是jpg或者png"
      });
    },
    handleMaxSize(file) {
      this.$Notice.warning({
        title: "Exceeding file size limit",
        desc: "文件 " + file.name + " 太大,请勿超过2M."
      });
    },

    addBtnClick() {
      this.addFormItem = this.initBanner();
      this.addBannerModal = true;
    },
    editBanner(row) {
      this.addFormItem = JSON.parse(JSON.stringify(row));
      console.log(this.addFormItem);
      this.addFormItem.startDate = moment(this.addFormItem.startDate).format(
        "YYYY-MM-DD HH:mm:ss"
      );
      this.addFormItem.endDate = moment(this.addFormItem.endDate).format(
        "YYYY-MM-DD HH:mm:ss"
      );
      this.addBannerModal = true;
    },
    removeBanner(bannerId) {
      var self = this;
      this.$Modal.confirm({
        title: "广告位",
        content: "确认删除该广告位",
        onOk: () => {
          util.ajax
            .delete("/home/banner/" + bannerId)
            .then(response => {
              self.$Message.success("采购单删除成功");
              self.refreshTableData();
            })
            .catch(error => {
              util.errorProcessor(self, error);
            });
        }
      });
    },

    saveCancel() {
      this.addFormItem = this.initBanner();
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


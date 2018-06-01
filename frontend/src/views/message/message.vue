<style lang="less">
@import "./message.less";
</style>

<template>
    <div class="message-main-con">
        
        <div class="message-mainlist-con">
            <div>
                <Button @click="setCurrentMesType('unprocess')" size="large" long type="text">
                    <transition name="mes-current-type-btn">
                        <Icon v-show="currentMessageType === 'unprocess'" type="checkmark"></Icon>
                    </transition>
                    <span class="mes-type-btn-text">未处理消息</span>
                    <Badge class="message-count-badge-outer" class-name="message-count-badge" :count="unreadCount"></Badge>
                </Button>
            </div>
            <div>
                <Button @click="setCurrentMesType('processed')" size="large" long type="text">
                    <transition name="mes-current-type-btn">
                        <Icon v-show="currentMessageType === 'processed'" type="checkmark"></Icon>
                    </transition>
                    <span class="mes-type-btn-text">已处理消息</span>
                    <Badge class="message-count-badge-outer" class-name="message-count-badge" :count="hasreadCount"></Badge>
                </Button>
            </div>
        </div>
        <div class="message-content-con">
            <Row v-if="showMesTitleList" type="flex" justify="end" style="margin-top: 5px;">
                <DatePicker v-model="dateRange" type="daterange" placement="bottom-end" placeholder="请选定消息的时间范围" style="width:300px"></DatePicker>
            </Row>
            <transition name="view-message">
                <div v-if="showMesTitleList" class="message-title-list-con">
                    <Table ref="messageList" :columns="mesTitleColumns" :data="currentMesList" :no-data-text="noDataText" :loading="dataLoading"></Table>
                </div>
            </transition>
            <transition name="back-message-list">
                <div v-if="!showMesTitleList" class="message-view-content-con">
                    <Row style="margin-top: 10px;">
                        <i-col span="2">
                            <Row type="flex" justify="start">
                                <span><Button type="text" @click="backMesTitleList"><Icon type="chevron-left"></Icon>&nbsp;&nbsp;返回</Button></span>
                            </Row>
                        </i-col>
                        <i-col span="16">
                            <Row type="flex" justify="center">
                                <h3>{{ mes.title }}</h3>
                            </Row>
                        </i-col>
                        <i-col span="6">
                            <Row type="flex" justify="end">
                                <ButtonGroup size="small">
                                    <Button type="primary" icon="plus-round" @click="addOptionsBtnClick(mes.id)">添加评论信息</Button>
                                    <Button v-if="currentMessageType === 'unprocess'" type="success" icon="checkmark" @click="doMarkProcess(mes.id)">标为已处理</Button>
                                </ButtonGroup>
                            </Row>
                        </i-col>
                    </Row>

                    <p class="mes-time-con"><Icon type="android-time"></Icon>&nbsp;&nbsp;{{ mes.time }}</p>
                    <div class="message-content-body">
                        <p class="message-content">{{ mes.content }}</p>

                        <Tabs style="margin-top: 70px;">
                            <TabPane label="消息处理详情" icon="ios-chatboxes">
                               <div style="margin-left: 20px; margin-bottom: 20px;" v-for="item in mes.options" :key="item.id">
                                   <Row type="flex" justify="start">
                                       <strong>{{item.nickName}}&nbsp;{{item.realName ? '[' + item.realName + ']': ''}} &nbsp;&nbsp;&nbsp;&nbsp; {{item.optionStatus === 'PROCESSED' ? '标识已处理' : ''}} </strong> &nbsp;&nbsp;&nbsp;&nbsp;<Icon type="android-time"></Icon>&nbsp;{{ item.createTime | timeFilter }}
                                   </Row>
                                   <p class="message-content">{{ item.optionResult }}</p>
                               </div>
                            </TabPane>
                        </Tabs>
                    </div>
                </div>
            </transition>
        </div>

        <Modal v-model="addOptionModal" title="添加评论信息" :mask-closable="false" width="50" @on-ok="submitOption" @on-cancel="cancelOption">
            <Form ref="optionForm" :model="optionFormItem" :label-width="90"> 
                <Row type="flex" justify="center">
                    <i-col span="24">
                        <FormItem label="评论信息:">
                            <Input v-model="optionFormItem.optionResult" />
                        </FormItem>
                    </i-col>
                </Row>
            </Form>
        </Modal>

    </div>
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";

export default {
  name: "message",
  filters: {
    timeFilter(time) {
      return time ? moment(time).format("YYYY-MM-DD HH:mm:ss") : "";
    }
  },
  data() {
    const markProcessBtn = (h, params) => {
      return h(
        "Button",
        {
          props: {
            size: "small"
          },
          on: {
            click: () => {
              //标识为处理
              this.doMarkProcess(params.row.id);
            }
          }
        },
        "标为已处理"
      );
    };
    return {
      dateRange: [
        moment()
          .subtract(1, "M")
          .format("YYYY-MM-DD"),
        moment()
          .add(1, "d")
          .format("YYYY-MM-DD")
      ],
      dataLoading: false,
      currentMesList: [],
      unreadMesList: [],
      hasreadMesList: [],
      currentMessageType: "unprocess",
      showMesTitleList: true,
      unreadCount: 0,
      hasreadCount: 0,
      recyclebinCount: 0,
      noDataText: "暂无未处理消息",
      mes: {
        id: "",
        title: "",
        time: "",
        content: "",
        options: []
      },
      mesTitleColumns: [
        {
          title: " ",
          key: "title",
          align: "left",
          ellipsis: true,
          render: (h, params) => {
            return h(
              "a",
              {
                on: {
                  click: () => {
                    this.showMesTitleList = false;
                    this.mes.id = params.row.id;
                    this.mes.title = params.row.title;
                    this.mes.time = params.row.createTime
                      ? moment(params.row.createTime).format(
                          "YYYY-MM-DD HH:mm:ss"
                        )
                      : "";
                    this.mes.content = params.row.content;
                    this.mes.options = params.row.options;
                  }
                }
              },
              params.row.title
            );
          }
        },
        {
          title: " ",
          key: "time",
          align: "right",
          width: 300,
          render: (h, params) => {
            let timeStr = params.row.createTime
              ? moment(params.row.createTime).format("YYYY-MM-DD HH:mm:ss")
              : "";
            if (this.currentMessageType === "unprocess") {
              return h("div", [
                h("Icon", {
                  props: {
                    type: "android-time",
                    size: 12
                  },
                  style: {
                    margin: "0 5px"
                  }
                }),
                h(
                  "span",
                  {
                    props: {
                      type: "android-time",
                      size: 12
                    },
                    style: {
                      margin: "0 10px"
                    }
                  },
                  timeStr
                ),
                markProcessBtn(h, params)
              ]);
            } else {
              return h("span", [
                h("Icon", {
                  props: {
                    type: "android-time",
                    size: 12
                  },
                  style: {
                    margin: "0 5px"
                  }
                }),
                h(
                  "span",
                  {
                    props: {
                      type: "android-time",
                      size: 12
                    }
                  },
                  timeStr
                )
              ]);
            }
          }
        }
      ],
      addOptionModal: false,
      optionFormItem: {
        messageId: "",
        optionResult: ""
      }
    };
  },
  methods: {
    backMesTitleList() {
      this.showMesTitleList = true;
    },
    setCurrentMesType(type) {
      this.showMesTitleList = true;
      this.currentMessageType = type;
      if (type === "unprocess") {
        this.noDataText = "暂无未处理消息";
        this.currentMesList = this.unreadMesList;
      } else {
        this.noDataText = "暂无已处理消息";
        this.currentMesList = this.hasreadMesList;
      }
    },

    getMessageList() {
      let reqDate = {
        startDate: this.dateRange[0],
        endDate: this.dateRange[1]
      };
      this.dataLoading = true;
      util.ajax
        .post("/message/list", reqDate)
        .then(response => {
          this.dataLoading = false;
          let data = response.data;
          if (data) {
            this.setData(data);
          }
        })
        .catch(error => {
          this.dataLoading = false;
          util.errorProcessor(this, error);
        });
    },
    setData(data) {
      this.unreadMesList = data.unProcessList;
      this.currentMesList = this.unreadMesList;
      this.hasreadMesList = data.processedList;
    },

    submitProcess(action, messageId, status, result) {
      if (!messageId || !status) {
        this.$Message.warning("参数错误");
        return;
      }
      let reqData = {
        messageId: messageId,
        status: status,
        optionResult: result,
        startDate: this.dateRange[0],
        endDate: this.dateRange[1]
      };

      util.ajax
        .post("/message/process", reqData)
        .then(response => {
          this.$Message.success("提交成功");
          if (action === "mark") {
            this.getMessageList();
          } else {
            let data = response.data;
            this.mes.id = data.id;
            this.mes.title = data.title;
            this.mes.time = data.createTime
              ? moment(data.createTime).format("YYYY-MM-DD HH:mm:ss")
              : "";
            this.mes.content = data.content;
            this.mes.options = data.options;
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },

    doMarkProcess(messageId) {
      this.submitProcess("mark", messageId, "PROCESSED", "");
    },

    addOptionsBtnClick(messageId) {
      this.optionFormItem.id = messageId;
      this.addOptionModal = true;
    },
    submitOption() {
      if (!this.optionFormItem.id || !this.optionFormItem.optionResult) {
        this.$Message.warning("参数错误，请关闭后重新操作");
        return;
      }
      this.submitProcess(
        "option",
        this.optionFormItem.id,
        "UNPROCESS",
        this.optionFormItem.optionResult
      );
    },
    cancelOption() {
      this.optionFormItem.id = "";
      this.addOptionModal = false;
    }
  },
  mounted() {
    this.getMessageList();
  },
  watch: {
    unreadMesList(arr) {
      this.unreadCount = arr.length;
      this.$store.commit("setMessageCount", this.unreadCount);
    },
    hasreadMesList(arr) {
      this.hasreadCount = arr.length;
    }
  }
};
</script>


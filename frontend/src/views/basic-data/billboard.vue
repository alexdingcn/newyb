<template>
    <div>
        <Row :gutter="16">
            <i-col span="11" >
                <Card>
                    <p slot="title">
                        <Icon type="person-stalker"></Icon> 公告列表
                    </p>
                    <div slot="extra">
                        <ButtonGroup size="small" >
                            <Button type="primary" icon="levels" @click="modal2 = true">自定义排序</Button>
                        </ButtonGroup>
                    </div>
                    <Modal :visible.sync="modal2" width="360">
                        <p slot="header" style="color:#f60;text-align:center">
                            <Icon type="information-circled"></Icon>
                            <span>删除确认</span>
                        </p>    
                    </Modal>
                    <Table ref="billboardTab"  size="small" highlight-row height="600"
                        :columns="billboardColumns" :data="billboardList" @on-row-click="chooseBillboard">
                    </Table>
                </Card>
            </i-col>
            <i-col span="11" class="margin-left-20">
                <Card>
                    <p slot="title">
                        <Icon type="person-stalker"></Icon> 公告详情
                    </p>
                    <div slot="extra">
                        <ButtonGroup size="small" >
                            <Button type="primary" icon="plus" @click="init">新建公告</Button>
                            <Button type="error" icon="trash-b" @click="del">删除</Button>
                            <Button type="success" icon="checkmark" @click="save" >保存</Button>
                        </ButtonGroup>
                    </div>
                    <div>
                        <Form ref="billboardForm" :model="billboardForm" :rules="billboardFormRule" :label-width="100">
                            <FormItem label="标题：" prop="title" >
                                <Input  :maxlength="30" v-model="billboardForm.title"/>
                            </FormItem>
                            <FormItem label="状态：" prop="status">
                                <Radio-group v-model="billboardForm.status">
                                    <Radio :label="1"><span>上架</span></Radio>
                                    <Radio :label="0"><span>下架</span></Radio>
                                </Radio-group>
                            </FormItem>
                            <FormItem label="公告内容：" prop="content">
                                <Input type="textarea" :rows="4" v-model="billboardForm.content"/>
                            </FormItem>
                            <FormItem label="创建人：" prop="createBy" >
                                <Input  :maxlength="11" :disabled ="enable" v-model="billboardForm.createBy"/>
                            </FormItem>
                            <FormItem label="创建时间：" prop="createTime">
                                <Input v-model="billboardForm.createTime" :disabled ="enable"/>
                            </FormItem>  
                        </Form>
                    </div>
                </Card>
            </i-col>
        </Row>
        <Modal v-model="modal2" width="400" okText= '保存' on-ok="sort" on-cancel="cancel" cancelText='取消'>
            <Table ref="modal2Tab"  size="small" highlight-row height="300"
                :columns="modal2Columns" :data="modal2List">
            </Table>
        </Modal>
    </div>   
</template>

<script>
import util from "@/libs/util.js";
import moment from "moment";

export default {
  name: "billboard",
  data() {
    return {
      billboardList: [],
      modal2List:[],
      billboardForm: {},
      enable: true,
      currData: [],
      modal2: false,
      billboardFormRule: {
        title: [
          { required: true, message: "请输入标题", trigger: "blur" }
        ],
        content: [
          { required: true, message: "请输入公告内容", trigger: "blur" }
        ]
      },
      billboardColumns: [
        {
          title: "序号",
          key: "number",
          align: "center"
        },
        {
          title: "标题",
          key: "title",
          align: "center"
        },
        {
          title: "状态",
          key: "status",
          align: "center",
          render: (h, params) => {
            let label = params.row.status ? "上架" : "下架";
            let color = params.row.status ? "green" : "red";
            return h(
              "Tag",
              {
                props: {
                  type: "dot",
                  color: color
                }
              },
              label
            );
          }
        },
        {
          title: "创建时间",
          key: "createTime",
          align: "center",
          render: (h, params) => {
            let createTime = params.row.createTime;
            return h(
              "span",
              createTime ? moment(createTime).format("YYYY-MM-DD HH:mm:ss") : ""
            );
          }
        }
      ],
      modal2Columns:[
          {
          title: "序号",
          key: "number",
          align: "center",
          render: (h,params) => {
              let number = params.row.number;
              return h(
                  "Input",
                  {
                      props: {
                            value: number,
                        }
                  }
              )
              
          },
          
        },
        {
          title: "标题",
          key: "title",
          align: "center"
        },
      ]
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    chooseBillboard(data, index) {
      let currData1 = JSON.parse(JSON.stringify(data));
      currData1.status = currData1.status ? 1 : 0;
      currData1.createTime = moment(currData1.createTime).format(
        "YYYY-MM-DD HH:mm:ss"
      );
      this.currData = currData1;
      this.billboardForm = currData1;
    },
    init() {
      this.billboardForm = {};
    },
    del() {
      this.$Modal.confirm({
        title: "删除公告确认",
        content: "是否确认进行公告删除，删除后无法恢复",
        onOk: () => {
          util.ajax
            .delete("/billboard/delete/" + this.billboardForm.id)
            .then(response => {
              if (response.status == 200) {
                this.$Message.success("删除成功！");
                this.getList();
                this.init();
              }
            })
            .catch(error => {
              util.errorProcessor(self, error);
            });
        },
        onCancel: () => {}
      });
    },
    save() {
        if(!this.billboardForm.title){
            this.$Message.warning("请输入标题后再进行保存！");
            return;
            
        }else if(!this.billboardForm.status){
            this.$Message.warning("请选择状态后再进行保存！");
            return ;
        }else if(!this.billboardForm.content){
            this.$Message.warning("请输入公告内容后再进行保存！");
            return ;
        }
      if (this.billboardForm.id == null) {
        this.insert();
      } else {
        this.update();
      }
    },
    getList() {
      util.ajax
        .get("/billboard/getList")
        .then(response => {
          this.billboardList = response.data.data;
          this.modal2List = response.data.data;
        })
        .catch(error => {
          util.errorProcessor(self, error);
        });
    },
    insert() {
      util.ajax
        .post("/billboard/insert", this.billboardForm)
        .then(response => {
          if (response.status == 200) {
            this.$Message.success("公告添加成功！");
            this.getList();
            this.init();
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    update() {
      this.currData.title = this.billboardForm.title;
      this.currData.status = this.billboardForm.status;
      this.currData.content = this.billboardForm.content;
      this.currData.createTime = "";
      util.ajax
        .put("/billboard/update", this.currData)
        .then(response => {
          if (response.status == 200) {
            this.$Message.success("公告修改成功！");
            this.getList();
            this.init();
          }
        })
        .catch(error => {
          util.errorProcessor(this, error);
        });
    },
    sort(){
        console.log("dianjileok");
        util.ajax
        .put("/billboard/sort", this.modal2List)
        .then(response => {
            if(response.status == 200){
                this.$Message.success("公告排序完成！");
            }
            this.getList();
            this.init();
        })
        .catch(error => {
            util.errorProcessor(this, error);
        })
    },
    cancel(){
        this.modal2List = this.billboardList;
    }
  }
};
</script>
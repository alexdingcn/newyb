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
                            <Button type="primary" icon="levels">自定义排序</Button>
                        </ButtonGroup>
                    </div>
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
                            <Button type="primary" icon="plus" >新建公告</Button>
                            <Button type="error" icon="trash-b" >删除</Button>
                            <Button type="success" icon="checkmark" @click="save">保存</Button>
                        </ButtonGroup>
                    </div>
                    <div>
                        <Form ref="billboardForm" :model="billboardForm" :label-width="100">
                            <FormItem label="标题：" prop="title" >
                                <Input  :maxlength="30" v-model="billboardForm.title"/>
                            </FormItem>
                            <FormItem label="状态：" prop="status">
                                <Radio-group v-model="billboardForm.status">
                                    <Radio :label="true"><span>上架</span></Radio>
                                    <Radio :label="false"><span>下架</span></Radio>
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
      billboardForm: {},
      enable: true,
      currData:[],
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
              createTime ? moment(createTime).format("YYYY-MM-DD HH:mm") : ""
            );
          }
        }
      ]
    };
  },
  mounted() {
      this.getList();
  },
  methods: {
    chooseBillboard(data, index) {
        this.currData = data;
        this.billboardForm = data;
        this.billboardForm.createTime = moment(data.createTime).format("YYYY-MM-DD HH:mm")
    },
    save(){
        if(this.billboardForm.id == null){
            this.insert();
        }else{
            this.update();
        }
    },
    getList() {
        util.ajax
        .get("/billboard/getList")
        .then(response => {
            this.billboardList= response.data.data;
        })
        .catch(error => {
            util.errorProcessor(self, error);
        })                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
    },
    insert(){
        util.ajax
        .post("/billboard/insert", this.billboardForm )
        .then(response => {
            if(response.status == 200){
                this.$message.success("公告添加成功！");
            }
        })
        .catch(error => {
            util.errorProcessor(this, error);
        });
    },
    update(){
        this.currData.title = this.billboardForm.title;
        this.currData.status = this.billboardForm.status;
        this.currData.content = this.billboardForm.content;
        this.currData.createTime = "";
        util.ajax
        .post("/billboard/update",this.currData )
        .then(response => {
            if(response.status == 200){
                this.$message.success("公告修改成功！");
                this.getList();
            }
        })
        .catch(error => {
            util.errorProcessor(this, error);
        })
    }
  }
};
</script>
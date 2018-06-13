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
                            <Button type="primary" icon="levels" @click="createUserBtnClick">自定义排序</Button>
                        </ButtonGroup>
                    </div>
                    <Table ref="billboardTab"  size="small" highlight-row height="600" :loading="tabLoading"
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
                            <Button type="primary" icon="plus" @click="createUserBtnClick">新建公告</Button>
                            <Button type="success" icon="checkmark" @click="save" :loading="saveLoading">保存</Button>
                        </ButtonGroup>
                    </div>
                    <div>
                        <Form ref="billboardForm" :model="billboardForm" :rules="billboardFormRule" :label-width="100">
                            <FormItem label="标题："  >
                                <Input  :maxlength="30" :disabled="editView" />
                            </FormItem>
                            <FormItem label="状态：" >
                                <Radio-group >
                                    <Radio value="上架" disabled></Radio>
                                    <Radio value="下架"></Radio>
                                </Radio-group>
                            </FormItem>
                            <FormItem label="创建人：" >
                                <Input  :maxlength="11" :disabled="editView"/>
                            </FormItem>
                            <FormItem label="创建时间：" >
                                <Input />
                            </FormItem>
                            <FormItem label="最后修改人：" >
                                <Input :disabled="editView"/>
                            </FormItem>
                            <FormItem label="最后修改时间：" >
                                <Input />
                            </FormItem>
                            <FormItem label="公告内容：" >
                                <Input type="textarea" :rows="4"/>
                            </FormItem>  
                        </Form>
                    </div>
                </Card>
            </i-col>
        </Row>
    </div>   
</template>

<script>
export default {
    name: "billboard",
    data() {
        return {
            billboardColumns: [
                {
                    title: "序号",
                    key: "",
                    align: "center"
                },
                {
                    title:"标题",
                    key:"",
                    align: "center"
                },
                {
                    title:"状态",
                    key:"",
                    align: "center"
                },
                {
                    title:"创建时间",
                    key:"",
                    align:"center"
                }
            ]
        }
    }
}
</script>
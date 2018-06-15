<style lang="less">
@import "../../styles/common.less";
</style>

<template>
    <div class="margin-left-20">
        <Form ref="formItem" :model="formItem" :rules="ruleValidate" :label-width="80">
            <h3>公司信息</h3>
            <hr style="text-align:left; width: 45%; margin-bottom:15px;" size="1"/>
            <Row>
                <i-col span="8">
                    <FormItem label="公司名称" prop="name">
                        <Input  v-model="formItem.name" />
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="所属行业" prop="industry">
                    <Select v-model="formItem.industry" >
                            <Option v-for="item in cityList" :value="item.value" :key="item.value">{{ item.label }}</Option>
                    </Select>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="公司地址" prop="placeCodes">
                        <al-cascader v-model="formItem.placeCodeList" level="2" />
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                <FormItem  prop="address">
                        <Input  placeholder="详细地址：如 某某大道12号1203室" v-model="formItem.address"/>
                </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="邮箱" prop="email">
                        <Input  v-model="formItem.email"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="传真" prop="fax">
                        <Input  v-model="formItem.fax"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="联系人" prop="contactPerson">
                        <Input v-model="formItem.contactPerson"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="联系电话" prop="contactPhone" >
                        <Input v-model="formItem.contactPhone" />
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="公司介绍" prop="introduce">
                        <Input type="textarea" :rows="3" v-model="formItem.introduce" :maxlength="200" placeholder="200字以内"/>
                    </FormItem>
                </i-col>
            </Row>
            <h3 style="margin-top:10px;">财务</h3>
            <hr style="text-align:left;width: 45%; margin-bottom:15px;" size="1"/>
            <Row>
                <i-col span="8">
                    <FormItem label="开户名称" prop="accountName">
                        <Input v-model="formItem.accountName"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="开户银行" prop="accountBank">
                        <Input v-model="formItem.accountBank" />
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="银行账号" prop="accountNum">
                        <Input v-model="formItem.accountNum"/>
                    </FormItem>
                </i-col>
            </Row>
            <Row>
                <i-col span="8">
                    <FormItem label="开票抬头" prop="billing">
                        <Input v-model="formItem.billing"/>
                    </FormItem>
                </i-col>
            </Row>
        </Form>
        <hr style="text-align:left;width: 45%; margin-bottom:15px;" size="1"/>
        <Row>
            <Button type="success" icon="checkmark" @click="submit">确定</Button>
            <Button type="ghost" icon="reply" @click="cancel">取消</Button>
        </Row>
    </div>
    
</template>
<script>
import moment from "moment";
import util from "@/libs/util.js";
import alCascader from "@/views/my-components/area-linkage/components/al-cascader.vue";

export default {
  name: "company",
  components: {
    alCascader
  },
  data() {
    const valideMobile = (rule, value, callback) => {
      if (!value) {
        callback(new Error("手机号必输"));
      } else {
        var re = /^1[0-9]{10}$/;
        if (!re.test(value)) {
          callback(new Error("请输入正确格式的手机号"));
        } else {
          callback();
        }
      }
    };
    return {
      spinShow: false,
      loading: false,
      formItem: {},
      categoryList: [],
      ruleValidate: {
        name: [{ required: true, message: "名称不能为空", trigger: "blur" }],
        contactPhone: [
          { required: true, message: "请输入手机号", trigger: "blur" },
          { validator: valideMobile, trigger: "blur" }
        ]
      },
      fileUploadModal: false,
      cityList: [
        {
          value: "农林牧渔",
          label: "农林牧渔"
        },
        {
          value: "医药卫生",
          label: "医药卫生"
        },
        {
          value: "建筑建材",
          label: "建筑建材"
        },
        {
          value: "冶金矿产",
          label: "冶金矿产"
        },
        {
          value: "石油化工",
          label: "石油化工"
        },
        {
          value: "水利水电",
          label: "水利水电"
        },
        {
          value: "旅游休闲",
          label: "旅游休闲材"
        },
        {
          value: "信息产业",
          label: "信息产业"
        },
        {
          value: "机械机电",
          label: "机械机电"
        },
        {
          value: "轻工食品",
          label: "轻工食品"
        },
        {
          value: "服装纺织",
          label: "服装纺织"
        },
        {
          value: "专业服务",
          label: "专业服务"
        },
        {
          value: "安全防护",
          label: "安全防护"
        },
        {
          value: "环保绿化",
          label: "环保绿化"
        },
        {
          value: "办公文教",
          label: "办公文教"
        },
        {
          value: "电子电工",
          label: "电子电工"
        },
        {
          value: "玩具礼品",
          label: "玩具礼品"
        },
        {
          value: "家居用品",
          label: "家居用品"
        },
        {
          value: "物资",
          label: "物资"
        },
        {
          value: "包装",
          label: "包装"
        },
        {
          value: "体育",
          label: "体育"
        },
        {
          value: "办公",
          label: "办公"
        }
      ]
    };
  },
  mounted() {
    util.ajax
      .get("/company/initInfo")
      .then(response => {
        this.formItem = response.data;
        var placeCodes = [];
        var rawCodes = response.data.placeCodes;
        if (rawCodes) {
          for (var i = 0; i < rawCodes.length; i++) {
            placeCodes.push(rawCodes[i].code);
          }
        }
        this.formItem.placeCodeList = placeCodes;
      })
      .catch(function(error) {
        util.errorProcessor(this, error);
      });
  },
  methods: {
    submit() {
      this.$refs.formItem.validate(valid => {
        if (!valid) {
          return;
        }
        this.doSubmit();
      });
    },
    doSubmit() {
      util.ajax
        .post("/company/updateCompany", this.formItem)
        .then(response => {
          this.$Message.success("公司信息修改成功！");
        })
        .catch(function(error) {
          util.errorProcessor(this, error);
        });
    },
    cancel() {
      util.ajax
        .get("/company/initInfo")
        .then(response => {
          this.formItem = response.data;
          var placeCodes = [];
          var rawCodes = response.data.placeCodes;
          if (rawCodes) {
            for (var i = 0; i < rawCodes.length; i++) {
              placeCodes.push(rawCodes[i].code);
            }
          }
          this.formItem.placeCodeList = placeCodes;
        })
        .catch(function(error) {
          util.errorProcessor(self, error);
        });
    }
  }
};
</script>


<style >
.ivu-form-item {
  margin-bottom: 20px;
}
.file-upload-modal {
  position: fixed;
  z-index: 3000;
}
</style>


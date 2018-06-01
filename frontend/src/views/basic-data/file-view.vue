<style lang="less">
@import "../../styles/common.less";
</style>

<template>
  <div>
      <Card>
          <p slot="title">
              <Icon type="images"></Icon>
              档案 {{showTitle}}
            </p>
            <div slot="extra">
                <Button type="primary" size="small" :disabled="uploadBtnDisabled" @click="uploadBtnClick">
                    <Icon type="ios-cloud-download"></Icon>下载文件
                </Button>
            </div>
            <Row>
                <Carousel v-model="currCarousel" @on-change="carouselChange">
                    <CarouselItem v-for="item in fileList" :key="item.id">
                        <Row type="flex" justify="center" >
                            <img v-if="isImageFile(item.location)" style="width:400px;height:400px" :src="item.loadUrl" :alt="item.comment" />
                            <Icon v-else type="document-text" size="150"></Icon>
                        </Row>
                    </CarouselItem>
                </Carousel>
            </Row>
      </Card>
  </div>
</template>

<script>
import util from '@/libs/util.js';

export default {
    name: 'file-view',
    props: {
        fileId: {
            type: Number
        },
        fileNo: {
            type: String
        },
        fileInfo: {
            type: Object,
            default: null
        },
        change:{
            type: Number,
            default: -1
        }
    },
    data () {
        return {
            currCarousel: 0,
            showTitle: '',
            uploadBtnDisabled: true,
            imageTypeList: ['.jpg', '.jpeg', '.png', '.gif', '.tiff'],
            currCarouselItem: {},
            fileDetail: {},
            fileList: []
        };
    },
    mounted() {
        this.initDate();
    },
    watch: {
        fileId () {
            this.initDate();
        },
        fileNo () {
            this.initDate();
        },
        fileInfo () {
            this.initDate();
        },
        change() { 
            //主要是当fileId 或者fileNo 或者fileInfo没有变动时，进行变动
            this.initDate();
        },
        fileDetail () {
            if (this.fileDetail && this.fileDetail.id) {
                this.fileList = this.fileDetail.fileUploads;
                this.getcurrCarouselItem(0);
                this.currCarousel = 0;
            }else {
                this.fileList = [];
                this.currCarouselItem = {};
            }
        },
        currCarousel () {
            this.getcurrCarouselItem(this.currCarousel);
        },
        currCarouselItem () {
            let item = this.currCarouselItem;
            if (!item || !item.id) {
                this.uploadBtnDisabled = true;
                this.showTitle = '';
                return;
            }
            this.showTitle = this.fileDetail.fileName + ' ' + item.location;
            if (this.isImageFile(item.location)) {
                this.uploadBtnDisabled = true;
            } else {
                this.uploadBtnDisabled = false;
            }
        }
    },
    methods: {
        isImageFile (key) {
            if (!key && (typeof key) !== 'string') {
                return false;
            }
            let endStr = key.substring(key.lastIndexOf('.'));
            for (let i = 0; i < this.imageTypeList.length; i++) {
                if (endStr === this.imageTypeList[i]) {
                    return true;
                }
            }
            return false;
        },

        carouselChange (oldValue, value) {
            this.currCarousel = value;
        },

        initDate () {
            if (!this.fileId && !this.fileNo && !this.fileInfo) {
                this.fileDetail = {};
                return;
            }
            if (this.fileInfo) {
                this.fileDetail = this.fileInfo;
            } else if (this.fileId) {
                this.getFileInfoById(this.fileId);
            } else {
                this.getFileInfoByFileNo(this.fileNo);
            }
        },

        getFileInfoById (id) {
            util.ajax.get('/file/fileid', {params: {fileId: id}})
                .then((response) => {
                    this.fileDetail = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        getFileInfoByFileNo (fileNo) {
            util.ajax.get('/file/fileno', {params: {fileNo: fileNo}})
                .then((response) => {
                    this.fileDetail = response.data;
                })
                .catch((error) => {
                    util.errorProcessor(this, error);
                });
        },

        getcurrCarouselItem (index) {
            if (!this.fileList || this.fileList.length <= 0) {
                this.currCarouselItem = {};
            } else {
                this.currCarouselItem = this.fileList[index];
            }
        },

        uploadBtnClick () {
            if (this.currCarouselItem && this.currCarouselItem.loadUrl) {
                let iframe = document.createElement('iframe');
                iframe.style.display = 'none';
                iframe.src = this.currCarouselItem.loadUrl;
                iframe.onload = function () {
                    document.body.removeChild(iframe);
                };
                document.body.appendChild(iframe);
            } else {
                this.$Message.warning('获取文件下载链接失败');
            }
        }
    }

};
</script>

<style >

</style>


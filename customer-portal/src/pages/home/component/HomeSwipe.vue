<template>
  <div class="home-swiper">
        <mt-swipe :auto="4000">
          <mt-swipe-item v-for="(list,index) in swiper" :key="index">
            <img :src="list.img">
          </mt-swipe-item>
        </mt-swipe>
  </div>
</template>

<script>
import util from "@/libs/util.js";

export default {
  name: "HomeSwipe",
  data() {
    return {
      swiper: [
        {
          img:
            "https://shopstatic.vivo.com.cn/vivoshop/commodity/20180418/20180418104131830678_original.jpg"
        }
      ]
    };
  },
  created() {
    let reqData = {
      page: 1,
      pageSize: 10
    };
    var self = this;
    util.ajax.post("/home/banner/list", reqData).then(function(res) {
      if (res.status === 200 && res.data && res.data.data.length > 0) {
        var bannerList = [];
        for (var i = 0; i < res.data.data.length; i++) {
          bannerList.push({ img: res.data.data[i].imageUrl });
        }
        self.swiper = bannerList;
      }
    });
  }
};
</script>


<style lang="stylus" scoped>
.home-swiper {
  height: 6.5rem;
  margin-top: 1px;

  img {
    width: 100%;
    height: 6.5rem;
  }
}
</style>
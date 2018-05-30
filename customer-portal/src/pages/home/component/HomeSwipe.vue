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
        },
        {
          img:
            "https://shopstatic.vivo.com.cn/vivoshop/commodity/20180430/20180430232146894398_original.jpg"
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
      console.log(res);
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
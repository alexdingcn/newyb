<template>
    <div @click="refreshPage" class="page-refresh-con">
        <Tooltip content="刷新页面" placement="bottom">
            <Icon type="refresh" :size="22"></Icon>
        </Tooltip>
    </div>
</template>

<script>
import util from '@/libs/util.js';
export default {
    name: 'pageRefresh',
    computed: {
        currPageName () {
            return this.$store.state.app.currentPageName;
        },
    },
    methods: {
        refreshPage () {
            let params = !this.$route.params ? {} : this.$route.params;
            let query = !this.$route.query ? {} : this.$route.query;
            let routeVal = {
                name: this.currPageName,
                params: params,
                query: query
            };
            console.log(routeVal);
            this.$router.go(routeVal);
        }
    }
};
</script>

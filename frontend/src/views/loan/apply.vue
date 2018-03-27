<style lang="less">
</style>

<template>
    <div>{{ result}}</div>
</template>

<script>
    import Cookies from 'js-cookie';
    import util from '@/libs/util.js';

    export default {
        name: 'loan-apply',
        data () {
            return {
                result: ''
            };
        },
        mounted () {
            this.getFaceToken();
        },
        methods: {
            getFaceToken () {
                var self = this;
                util.ajax.post('/loan/face/token')
                    .then(function (response) {
                        if (response.status === 200) {
                            self.result = response.data;
                            Cookies.set('face_token', response.data.biz_id, { expires: 1/96 });
                            window.location = response.data;
                        }
                    })
                    .catch(function (error) {
                        console.log(error);
                    });
            }
        }

    };
</script>


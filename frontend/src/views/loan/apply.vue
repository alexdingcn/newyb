<style lang="less">
</style>

<template>
    <div>{{ result}}</div>

</template>

<script>
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
                        alert(response.data);
                        if (response.status === 200) {
                            self.result = response.data;
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


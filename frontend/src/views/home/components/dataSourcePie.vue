<template>
    <div style="width:100%;height:100%;" id="data_source_con"></div>
</template>

<script>
import echarts from 'echarts';

export default {
    name: 'dataSourcePie',
    data () {
        return {
            dataSourcePie: {}
        };
    },
    props: {
        content: {
            type: Array,
            required: true
        }
    },
    mounted () {
        this.dataSourcePie = echarts.init(document.getElementById('data_source_con'));
    },
    methods: {
        draw(val) {
            var data = [];
            var legends = [];
            var colors = ['#9bd598', '#ffd58f', '#abd5f2', '#ab8df2', '#e14f60'];
            for (let i = 0; i < val.length; i++) {
                data.push({
                    value: val[i].amount,
                    name: val[i].goodsName
                });

                legends.push(val[i].goodsName);
            }

            const option = {
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b} : {c} ({d}%)'
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    legends: legends
                },
                series: [
                    {
                        name: '销量',
                        type: 'pie',
                        radius: ['40%', '70%'],
                        avoidLabelOverlap: false,
                        data: data
                    }
                ]
            };
            this.dataSourcePie.setOption(option);
            window.addEventListener('resize', function () {
                this.dataSourcePie.resize();
            });
        }
    },
    watch: {
        content (val) {
            this.draw(val);
        }
    }
};
</script>

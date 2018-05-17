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
            var self = this;
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
                    x: 'right',
                    data: legends
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
                self.dataSourcePie.resize();
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

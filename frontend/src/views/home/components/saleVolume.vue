<template>
    <div style="width:100%;height:100%;" id="sale_volume_con"></div>
</template>

<script>
import echarts from 'echarts';
import moment from 'moment';

export default {
    name: 'saleVolume',
    data () {
        return {
            saleVolume: {}
        };
    },
    props: {
        content: {
            type: Array,
            required: true
        }
    },
    mounted () {
        this.saleVolume = echarts.init(document.getElementById('sale_volume_con'));
    },
    methods: {
        draw (content) {
            var self = this;
            let xAxisData = [];
            let yAxisData = [];
            for (let i = 0; i < content.length; i++) {
                xAxisData.push(moment(content[i].tradeDate).format('MM-DD'));
                yAxisData.push(content[i].amount);
            }
            const option = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                grid: {
                    top: '2%',
                    left: '2%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                yAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                },
                xAxis: {
                    type: 'category',
                    data: xAxisData,
                    nameTextStyle: {
                        color: '#c3c3c3'
                    }
                },
                series: [
                    {
                        name: '金额',
                        type: 'bar',
                        data: yAxisData
                    }
                ]
            };

            this.saleVolume.setOption(option);

            window.addEventListener('resize', function () {
                self.saleVolume.resize();
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

<style lang="less">
    @import './main-help.less';
</style>

<template>
    <Card class="help-main">
        <p slot="title"><Icon type="ios-help-outline"></Icon> 帮助中心</p>
        
        <a href="https://segmentfault.com/ls/1650000011074057" target="_blank" class="wrapper-aside">
            <img src="https://file.iviewui.com/dist/e047b9b151ec39dbe25757108d68f9c8.png">
        </a>

        <header class="divider-header">
            <span>联系方式</span>
        </header>

        <Timeline class="margin-top-10">
            <TimelineItem color="invisible">
                <FontIcon type="icon-qq" slot="dot"></FontIcon>
                <a target="_blank" href="http://wpa.qq.com/msgrd?V=1&uin=9756669&Site=医伴ERP&Menu=yes">
                <img src="http://wpa.qq.com/pa?p=1:9756669:1" alt="帮助" >
                </a>
            </TimelineItem>
            <TimelineItem color="invisible">
                <FontIcon type="icon-weixin" slot="dot"></FontIcon>
                微信公众号
                <img src="https://www.yibanmed.com/images/wx-qr.png" >
            </TimelineItem>
            
            <TimelineItem color="invisible">
                <FontIcon type="icon-woyaoqingsu" slot="dot"></FontIcon>
                <h3>40077-40088</h3>
            </TimelineItem>
        </Timeline>
        
        <Row>
            
        </Row>
        
    </Card>
</template>

<script>
export default {
  name: 'main-help',

}
</script>
<style lang="less">
.wrapper-aside {
    display: block;
    width: 98%;
    border: 1px solid #e9eaec;
    border-radius: 3px;
    margin: 10px 0;
    cursor: pointer;
    
    img {
        width: 100%;
        border-radius: 3px;
        opacity: .8;
        transition: opacity .2s ease-in-out;
    }
}

.divider-header {
    font-weight: 500;
    margin: 30px 0 10px;
    position: relative;
    
    &:before {
        content: "";
        display: block;
        width: 100%;
        height: 1px;
        background: #eee;
        position: absolute;
        top: 10px;
        left: 0;
    }
    span {
        display: inline-block;
        background: #fff;
        padding: 0 5px 0 18px;
        position: relative;
        margin-left: 30px;
        font-size: 14px;
    }
}
</style>


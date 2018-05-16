<template>
    <div>
        <Tag v-for="item in tags" :color="color" :key="item.id" :name="item.id">{{ item.specName }}</Tag>
    </div>
</template>
<script>
    export default {
        props: {
            tags: Array,
            color: {
                type: String,
                default: ''
            }
        }
    };
</script>
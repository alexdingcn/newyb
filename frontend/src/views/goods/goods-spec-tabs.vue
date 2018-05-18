<template>
    <div v-if="Array.isArray(tags)">
        <Tag v-for="item in tags" :color="color" :key="item.id" :name="item.id">{{ item.specName }}</Tag>
    </div>
    <div v-else>
        {{ tags }}
    </div>
</template>
<script>
    export default {
        props: {
            tags: Array | String,
            color: {
                type: String,
                default: ''
            }
        }
    };
</script>

<template>
    <Modal v-model="showModal" :mask-closable="false" title="选择仓库仓位" 
        ok-text='选择' width="40" @on-ok="chooseLocation" @on-cancel="closeShowModal" >
        <Row type="flex" justify="center">
            <span>当前选中: <strong>{{currLocation.location || ''}}</strong></span>
        </Row>
        <Row>
            <Table border highlight-row :columns="locationColumns" :data="locationData"
                ref="locationTab" style="width: 100%;" height="300" size="small" :loading="locationDataLoading" 
                @on-row-click="onClickRow" 
                @on-row-dblclick="chooseLocation" 
                no-data-text='当前仓库没有维护有库位'
            ></Table>
        </Row>
    </Modal>
</template>

<script>
import util from "@/libs/util.js";

export default {
    name: "warehouse-location-modal",
    props: {
        warehouseId: {
            type: Number|String,
            required: true
        },
        openModal: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            showModal: false,
            currLocation: {},
            locationData: [],
            locationColumns: [
                {
                    title: '名称',
                    key: 'location',
                    align: 'center',
                },
                {
                    title: '描述',
                    key: 'comment',
                    align: 'center',
                }
            ],
            locationDataLoading: false
        }
    },
    mounted() {
        this.getLocationList();
    },
    watch: {
        openModal(value) {
            if (value) {
                this.showModal = value;
            }
        },
        warehouseId(newValue) {
            this.warehouseId = newValue;
            this.getLocationList();
        }
    },
    methods: {
        getLocationList() {
            if (!this.warehouseId) {
                this.locationData = [];
                this.currLocation = {};
                return;
            }
            this.locationDataLoading = true;
            util.ajax
                .get("/warehouse/location/list", {params: {warehouseId: this.warehouseId}})
                .then(response => {
                    this.locationDataLoading = false;
                    this.locationData = response.data;
                    this.currLocation = {};
                })
                .catch(error => {
                    this.locationDataLoading = false;
                    util.errorProcessor(this, error);
                });
        },

        onClickRow(rowData) {
            if (!rowData) {
                this.currLocation = {};
                return;
            }
            this.currLocation = rowData;
        },

        closeShowModal() {
            this.showModal = false;
            this.$emit('on-close');
        },

        chooseLocation() {
            if(!this.currLocation || !this.currLocation.id) {
                this.$Message.warning('请先选择需要的库位');
                return;
            }
            this.showModal = false;
            this.$emit('on-ok', this.currLocation);
        }
    }
  
}
</script>

<style >

</style>

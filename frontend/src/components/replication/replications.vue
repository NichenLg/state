<template>
    <div>
        <graph-line-chart :nodeData="replicationData" @graph-line-click="graphClick"></graph-line-chart>
        <Modal
            v-model="modalShow"
            :title="modalTitle"
            :styles="{top: '20px'}"
            @on-ok="hideModal"
            @on-cancel="hideModal"
            width="70">
            <p class="input-area">
                <Input v-model.trim="filterText" placeholder="输入筛选内容"></Input>
            </p>
            <p>
                <Tabs v-if="slaves.length">
                    <TabPane :label="slave.label"  v-for="(slave, index) in slavesFiltered" :key="index">
                        <Table  :columns="slave.columns" 
                                :data="slave.data" 
                                height="400"
                                :show-header="false"></Table>
                    </TabPane>
                </Tabs>
                <Loading v-if="slaves.length === 0"></Loading>
            </p>
        </Modal>
    </div>
</template>
<script>
    import ExpandRow from './replication-expand-row'
    import { mapMutations, mapGetters } from 'vuex'
    import * as types from 'store/mutation-types'
    import { replicationPath, CODE_OK } from 'constants/constants'
    import GraphLineChart from 'base/echarts/graph-line'
    import { getReplicationByHost } from 'api/replication'
    import Loading from 'base/loading/loading'

    export default {
        data() {
            return {
                modalShow: false,
                modalTitle: '',
                filterText: '',
                slaves: []
            }
        },
        computed: {
            slavesFiltered() {
                let filteredData = []
                let filterText = this.filterText
                if (filterText) {
                    this.slaves.forEach((slave) => {
                        let filteredSlave = Object.assign({}, slave)
                        filteredSlave.data = []
                        slave.data.forEach((slaveProperty) => {
                            if (~slaveProperty.key.toLowerCase().indexOf(filterText.toLowerCase())) {
                                filteredSlave.data.push(slaveProperty)
                            }
                        })
                        filteredData.push(filteredSlave)
                    })
                } else {
                    filteredData = this.slaves
                }
                return filteredData
            },
            ...mapGetters([
                'replicationData',
                'menuDesc'
            ])
        },
        activated() {
            this.setTitle(this.menuDesc[replicationPath])
        },
        methods: {
            hideModal() {
                this.slaves = []
            },
            graphClick(text) {
                if (text) {
                    this.modalTitle = text
                    this.filterText = ''
                    this.modalShow = true
                    getReplicationByHost(text).then((res) => {
                        if (res.code === CODE_OK) {
                            this.slaves = [] // 有其他地方依赖
                            let columns = [
                                {
                                    title: '变量',
                                    width: 200,
                                    key: 'key'
                                },
                                {
                                    title: '值',
                                    key: 'value'
                                }
                            ]
                            let datas = res.data
                            datas.forEach((item) => {
                                let data = []
                                let label = ''
                                let labelTitle = ''
                                let hasError = false
                                for (let key in item) {
                                    if (key === 'host') {
                                        labelTitle = item[key]
                                        continue
                                    }
                                    if (key === 'hasError') {
                                        hasError = item[key]
                                        continue
                                    }
                                    data.push({
                                        key,
                                        value: item[key]
                                    })
                                }
                                if (hasError) {
                                    label = (h) => {
                                        return h('div', [
                                            h('Badge', {
                                                props: {
                                                    dot: true
                                                },
                                                scopedSlots: {
                                                    default: function (props) {
                                                        return h('span', labelTitle)
                                                    }
                                                }
                                            })
                                        ])
                                    }
                                } else {
                                    label = labelTitle
                                }
                                this.slaves.push({
                                    columns,
                                    data,
                                    label,
                                    hasError
                                })
                            })
                        }
                    })
                }
            },
            ...mapMutations({
                setTitle: types.SET_TITLE
            })
        },
        components: {
            ExpandRow,
            GraphLineChart,
            Loading
        }
    }
</script>
<style lang="scss" type="stylesheet/scss" scoped>
    .ivu-table .table-warn-row td { 
        background-color: #f90;
        color: #fff;
        font-size: 16px;
    }
    .ivu-table .table-error-row td {
        background-color: #ff6600;
        color: #fff;
        font-size: 16px;
    }
    .ivu-badge {
        vertical-align: text-top;
    }
</style>

<template>
    <div>
        <h3 v-if="rooms.length===0" class="text-center">
            请您配置房间列表
        </h3>
        <Row class="row">
            <Col :md="5" :lg="4" :xs="12" class="room-item-box text-center" v-for="(room,i) in rooms" :key="i">
                <Card class="room-item">
                    <h5 slot="title" class="text-left">{{room.title}}
                        <strong v-if="room.current===-1" class="color-red">未知</strong>
                    </h5>
                    <div class="level-list mt-4">
                        <div class="title-line">
                            <span class="port">端口</span>
                            <span class="sep">|</span>
                            <span class="device">设备</span>
                        </div>
                        <div v-for="(lv,t) in room.outputs" class="color-blue level-item cursor-pointer"
                             @click="change(room,lv)">
                            <div class=" port">
                                <span class="value">{{lv.port}}</span>
                                <span class="status-val none" v-if="lv.value===2">未知</span>
                                <slot v-if="lv.kind===1">
                                    <span class="status-val active" v-if="lv.value===0"></span>
                                </slot>
                                <slot v-else>
                                    <span class="status-val active" v-if="lv.value===1"></span>
                                </slot>
                            </div>
                            <div class="sep">|</div>
                            <div class="device">
                                <span class="value">{{lv.device}}</span>
                                <span class="status-val none" v-if="lv.value===2">未知</span>
                                <span class="status-val active" v-if="lv.value===0"></span>
                            </div>
                        </div>
                    </div>
                </Card>
            </Col>
        </Row>
    </div>
</template>
<style lang="scss" scoped>
    .device, .port {
        display: inline-block !important;

        .status-val {
            line-height: 20px;
            display: inline-block;
            margin-left: 5px;

            &.active {
                width: 8px;
                height: 8px;
                margin-top: 6px;
                background: #00ab00;
            }

            &.none {
                font-size: 12px;
                line-height: 20px;
                color: red;
            }
        }
    }

    .port {
        display: inline-block;
        position: relative;
    }
</style>

<script>
    import {http} from "../../services/api";

    export default {
        name: "game",
        data() {
            return {}
        },
        computed: {
            rooms() {
                return this.$store.state.rooms;
            },
            gameId() {
                return this.$route.params.id;
            }
        },
        methods: {
            /**
             * 修改当前状态
             * @param room
             * @param lv
             * @returns {Promise<void>}
             */
            async change(room, lv) {
                if (false === confirm(`是否要切换【${lv.port}】${lv.device}的状态？`)) return;
                const val = lv.value == 0 ? 1 : 0;
                const res = await http.post(`/game/${this.gameId}/setOutput`, {
                    room: room.id,
                    ...lv,
                    value: val
                });
                if (!res.ok) {
                    this.$Message.error("服务器发送失败");
                }
            },

        },
        async created() {
        }
    }
</script>

<style scoped lang="scss">

    .tip {
        color: red;
    }

    .buttons {
        text-align: left;
        padding: 10px;

        button {
            margin-right: 10px;
            margin-bottom: 8px;
        }
    }

    .color-blue {
        color: blue;
        line-height: 30px;
        margin-bottom: 10px;
    }

    .page-title {
        line-height: 60px;
        text-align: center;
    }

    .container-fluid {
        padding: 0 15px;
    }

    .room-item-box {
        padding: 10px;

        .room-item {
            min-height: 100px;
        }
    }

    .cursor-pointer {
        cursor: pointer;
    }

    .color-red {
        color: red;
        font-size: 16px;
        margin-left: 10px;
    }

    .title-line, .level-item {
        display: flex;

        .port {
            flex: 1;
        }

        .sep {
            width: 30px;
            flex: none;
        }

        .device {
            flex: 2;
        }
    }

    .title-line {
        margin-bottom: 20px;
    }

    .level-item {
        line-height: 20px !important;
        position: relative;


        a {
            &:hover {
                color: red;
            }

            .active {
                color: #00ab00;
            }
        }
    }

    .active {
        color: #00ab00;
        right: 15px;
        position: absolute;
    }
</style>

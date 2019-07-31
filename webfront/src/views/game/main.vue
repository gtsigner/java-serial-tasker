<template>
    <div>
        <h3 v-if="rooms.length===0" class="text-center">
            请您配置房间列表
        </h3>
        <Row class="row">
            <Col :md="3" :lg="3" :xs="12" class="room-item-box text-center" v-for="(room,i) in rooms" :key="i">
                <Card class="room-item">
                    <h5 slot="title" class="text-left">{{room.title}}
                        <strong v-if="room.current===-1" class="color-red">未知</strong>
                    </h5>
                    <div class="level-list mt-4">
                        <p v-for="(lv,t) in room.levels" class="color-blue level-item">
                            <a class="cursor-pointer " @click="change(room,lv)">{{lv.title}} </a>
                            <span v-if="lv.value===room.current" class="active color-success">●</span>
                        </p>
                    </div>
                </Card>
            </Col>
        </Row>
    </div>
</template>


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
             * @param level
             * @returns {Promise<void>}
             */
            async change(room, level) {
                const res = await http.post(`/game/${this.gameId}/set`, {
                    room: room.id,
                    level: level.value
                });
                if (!res.ok) {
                    this.$Message.error("服务器发送失败");
                }

                const message = `切换房间：${room.title} 到关卡: ${level.title} 中....`;
                this.$Notice.info({
                    title: '切换关卡',
                    desc: message
                })
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


    .level-item {
        line-height: 25px !important;
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

<template>
    <div class="game container-fluid">
        <h1 class="page-title text-center">{{game.title}} 控制页面 {{counter}}</h1>
        <p class="mb-2 text-center">
            <router-link to="/">返回首页</router-link>
        </p>
        <p class="tip text-center" v-if="counter>=3">[ {{game.title}} ] 跳关PLC与服务器的连接已掉线，请检查线路或重启服务器。</p>
        <div class="buttons">
            <i-button type="error" @click="reset">一键复位</i-button>
            <i-button type="warning" @click="setReady">一键准备</i-button>
            <i-button type="success" @click="setStart">一键开始</i-button>
            <i-button type="success" @click="testLine">测试线路</i-button>
            <i-button type="primary" @click="refresh">立即同步</i-button>
            <i-button type="primary" @click="outputControl">输出控制</i-button>
            <i-button type="primary" @click="inputStatus">输入状态</i-button>
        </div>
        <div class="room-container mt-5">
            <router-view></router-view>
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
    </div>
</template>

<script>
    import {http} from "../services/api";

    export default {
        name: "game",
        data() {
            return {
                levels: [],
                isConnected: false,
                rooms: [],
                game: {
                    title: '加载中...'
                },
                timer: 0,
                timerNotice: 0,
                loading: false,
                gameId: 0,
                counter: 0,
            }
        },
        methods: {
            //一键复位
            async reset() {
                if (false === confirm("确认进行一键复位操作？")) {
                    return false;
                }
                const res = await http.post('/game/' + this.gameId + '/reset');
            },
            //一键准备
            async setReady() {
                if (false === confirm("确认进行一键准备操作？")) {
                    return false;
                }
                const res = await http.post('/game/' + this.gameId + '/ready');
            },
            //一键开始
            async setStart() {
                if (false === confirm("确认进行一键开始操作？")) {
                    return false;
                }
                const res = await http.post('/game/' + this.gameId + '/start');
            },
            //测试线路
            async testLine() {
                if (false === confirm("确认进行测试线路操作？")) {
                    return false;
                }
                const res = await http.post('/game/' + this.gameId + '/test');
            },
            async getGame() {
                this.counter++;
                const res = await http.get('/game/' + this.gameId);
                if (!res.ok) {
                    this.$Message.error("服务器发送失败");
                }
                const data = res.data.data;
                this.counter = 0;
                this.rooms = [...data.rooms];
                //截取一条写入到通知栏中
            },
            //刷新
            async refresh() {
                await this.getGame();
                this.$Message.success("数据同步成功");
            },
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
            //获取系统通知
            async getNotices() {
                const res = await http.get(`/game/message`);
                if (!res.ok) {
                    return this.$Message.error("服务器获取失败");
                }
                const data = res.data.data;
                data.forEach(info => {

                    if (info.theme === 'error') {
                        this.$Notice.error({title: '服务器通知', desc: info.message});
                    } else if (info.theme === 'primary') {
                        this.$Notice.info({title: '服务器通知', desc: info.message});
                    }


                });
            },
            outputControl() {
                this.$router.push(`/game/${this.gameId}/control`);
            },
            inputStatus() {
                this.$router.push(`/game/${this.gameId}/status`);
            }
        },
        //卸载timer
        beforeDestroy() {
            this.timerNotice && clearInterval(this.timerNotice);
            this.timer && clearInterval(this.timer);
        },
        async created() {
            const id = this.$route.params.id;
            this.game.id = id;
            this.gameId = id;
            this.isConnected = true;
            //其实这个无所谓的，因为游戏状态是存在内存的,所以可以直接对内存进行获取一下就OK
            this.timer = setInterval(async () => {
                await this.getGame();
            }, 1000);//每秒请求一次
            this.timerNotice = setInterval(async () => {
                await this.getNotices();
            }, 1500);
            await this.getGame();
            await this.getNotices();
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

<template>
    <div class="container">
        <h1 class="text-center page-title">魔方密室 v0.0.1</h1>
        <Row class="row">
            <div class="col" v-if="games.length===0">
                <h3 class="card-title text-danger mt-5">对不起,系统没有获取到任何游戏，请检查一下服务端链接是否正常</h3>
            </div>
            <Col class="game-item" :md="6" v-for="(gm,i) in games" :key="i">
                <Card class="card">
                    <h4 slot="title">{{gm.title}}</h4>
                    <div class="card-body">
                        <p class="card-text">{{gm.note||'暂无游戏描述'}}</p>

                        <div class="action">
                            <router-link :to="'/game/'+gm.id" target="_blank" v-if="gm.enable">
                                <i-button type="primary">查看游戏</i-button>
                            </router-link>
                            <i-button v-if="!gm.enable" disabled type="primary">已关闭</i-button>
                        </div>
                    </div>
                </Card>
            </Col>
        </Row>
    </div>
</template>

<script>
    import {http} from '../services/api';

    export default {
        name: 'home',
        components: {},
        data() {
            return {
                games: [],
            };
        },
        computed: {},
        methods: {
            async getGames() {
                const res = await http.get('/game/config');
                if (!res.ok) {
                    return false;
                }
                const data = res.data.data;
                this.games = [...data.games];
            }
        },
        created() {
            this.getGames();

        }
    };
</script>
<style scoped lang="scss">
    .container {
        margin: 0 auto;
        max-width: 1180px;
    }

    .card-text {
        color: gray;
        font-size: 11px;
    }

    .game-item {
        padding: 10px;
    }

    .action {
        text-align: right;
        margin-top: 20px;
    }

    .page-title {
        font-size: 26px;
        text-align: center;
        line-height: 80px;
    }
</style>

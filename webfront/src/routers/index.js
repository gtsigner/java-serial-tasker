import Vue from 'vue';
import Router from 'vue-router';
import main from '../views/main'

Vue.use(Router);

const router = new Router({
    routes: [
        {
            path: '/',
            redirect: "/home",
            component: main
        },
        {
            path: '/home',
            name: 'home',
            meta: {
                title: '首页',
            },
            component: () => import( '../views/home'),
        },
        {
            path: '/game/:id',
            name: 'game',
            meta: {
                title: '游戏',
            },
            component: () => import('../views/game'),
            children: [
                {
                    path: '/main',
                    name: 'game-main',
                    meta: {
                        title: '游戏控制',
                    },
                    component: () => import( '../views/game/main'),
                },
                {
                    path: '/control',
                    name: 'game-control',
                    meta: {
                        title: '输出控制',
                    },
                    component: () => import( '../views/game/control'),
                },
                {
                    path: '/status',
                    name: 'game-status',
                    meta: {
                        title: '输入状态',
                    },
                    component: () => import( '../views/game/status'),
                },
            ]
        }
    ],
});

//钩子声明周期
router.beforeEach((to, from, next) => {
    if (to.meta && to.meta.title) window.document.title = to.meta.title + " - 魔方密室";
    next();
});

router.afterEach((to) => {

});
export default router;

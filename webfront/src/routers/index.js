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
            component: () => import(/* webpackChunkName: "about" */ '../views/home'),
        },
        {
            path: '/game/:id',
            name: 'game',
            meta: {
                title: '游戏',
            },
            component: () => import(/* webpackChunkName: "about" */ '../views/game'),
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

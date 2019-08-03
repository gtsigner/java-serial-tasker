import Vue from 'vue'
import App from './App.vue';
import router from './routers';
import store from './store/index';
import * as dayjs from 'dayjs';

import IView from 'iview'


import 'iview/dist/styles/iview.css'
import './assets/index.scss'

Vue.config.productionTip = false;

Vue.use(IView, {
});
Vue.filter('formatDate', (timestamp, formatStr) => {
    return dayjs(timestamp * 1000).format(formatStr || 'YYYY-MM-DD HH:mm');
});

new Vue({
    router,
    store,
    render: h => h(App)
}).$mount('#app');

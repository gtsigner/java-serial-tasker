import axios from 'axios';
import qs from 'qs';
import Config from '../config';
import {getAccessToken} from './index';

const configs = {
    baseURL: Config.API_HOST + Config.API_BASE_PATH,
    version: 'v1',
    uploadURL: Config.API_HOST + 'uploads'
};

const instance = axios.create({
    baseURL: configs.baseURL,
    timeout: 5000,
    withCredentials: false,
    /*Http Header*/
    headers: {}
});

//加入Token在Request拦截器中
instance.interceptors.request.use(config => {
    const accessToken = getAccessToken();
    if (null !== accessToken && accessToken !== undefined && accessToken !== 'undefined') {
        config.headers['Authorization'] = 'Bearer ' + accessToken;
    }
    return config;
});

// http response 拦截器,标准的restful请求
instance.interceptors.response.use(
    response => {
        const res = {
            data: response.data,
            status: 200
        };
        res.ok = response.data.code !== codes.Fail;
        return res;
    },
    error => {
        if (!error.response) {
            error.response = {
                data: {message: 'Server Request Fail'},
                status: 502
            };
        }
        const res = {
            ...error.response.data,
            ok: false,
            error_msg: '访问服务器失败',
            status: 500
        };
        if (!error.response) {
            res.status = 500;
            res.error_msg = 'API服务器访问失败';
        } else {
            const data = error.response.data;
            res.status = error.response.status;
            res.error_msg = data.message || 'API服务器访问失败';
        }
        switch (res.status) {
            case 401:
                break;
            case 400:
                //错误
                break;
        }
        return Promise.resolve(res);
    }
);

export const http = instance;
export const codes = {Success: 1001, Fail: 1004};
export const config = configs;

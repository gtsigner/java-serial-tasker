const config = {
    API_HOST: 'http://localhost:8080',
    API_BASE_PATH: '/api/',
};
const isProd = process.env.NODE_ENV === 'production';
/**
 * build 后的产品环境配置
 */
if (isProd === true) {
    config.API_HOST = '';//域名
}
export default config;


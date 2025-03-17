import axios from '../utils/axios'

export function getSecKillList(params) {
    return axios.get('/seckill/list', { params });
}

export function getSecKillDetail(seckillId) {
    return axios.get(`/seckill/${seckillId}`);
}

export function seckillExecution(seckillId, md5) {
    return axios.post(`/seckillExecution/${seckillId}/${md5}`);
}

export function exposerUrl(seckillId) {
    return axios.post(`/seckill/${seckillId}/exposer`);
}

export function createSeckillOrder(seckillSuccessId, seckillSecretKey) {
    return axios.post(`/saveSeckillOrder/${seckillSuccessId}/${seckillSecretKey}`);
}
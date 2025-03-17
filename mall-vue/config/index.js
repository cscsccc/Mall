export default {
  mode: 'hash',
  development: {
    baseUrl: '//127.0.0.1:9003' // 测试接口域名
  },
  beta: {
    // baseUrl: '//backend-api-02.newbee.ltd/manage-api/v1' // 测试接口域名
    baseUrl: '//172.18.199.32:9003'
  },
  release: {
    baseUrl: '//123.56.14.241:9003' // 正式接口域名
  }
}


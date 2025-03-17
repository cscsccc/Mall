<!--
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2020 陈尼克 all rights reserved.
 * 版权所有，侵权必究！
 *
-->

<template>
  <s-header :name="'秒杀商品'" :noback="false"></s-header>
  <div class="content">
    <van-pull-refresh v-model="state.refreshing" @refresh="onRefresh" class="product-list-refresh">
      <van-list v-model:loading="state.loading" :finished="state.finished"
        :finished-text="'没有更多秒杀商品了'" @load="onLoad" @offset="10">
        <div class="good-item" v-if="state.productList.length" v-for="(item, index) in state.productList" :key="index">
          <div class="good-img"><img :src="prefix(item.goodsCoverImg)" :alt="item.goodsName"></div>
          <div class="good-desc">
            <div class="good-title">
              <span class="goods-name">{{ item.goodsName }}</span>
              <div class="seckill-price">秒杀价格：<span>¥{{ item.seckillPrice }}</span></div>
            </div>
            <div class="good-btn">
              <div class="seckill-time">
                <van-icon name="clock-o" size="20" />
                <p style="font-size: 8px;">秒杀时间：{{ item.seckillBeginTime }} -> {{ item.seckillEndTime }}</p>
              </div>
              <van-button square type="danger"
                :disabled="!isWithinSeckillTime(item.seckillBegin, item.seckillEnd)"
                @click="gotoDetail(item.seckillId)">
                <span v-if="new Date().getTime() < item.seckillBegin">未开启</span>
                <span v-else-if="new Date().getTime() > item.seckillEnd">已结束</span>
                <span v-else>查看详情</span>
              </van-button>
            </div>
          </div>
        </div>

        <img class="empty" v-else src="https://s.yezgea02.com/1604041313083/kesrtd.png" alt="搜索">
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSecKillList } from '../service/secKill';
import sHeader from '@/components/SimpleHeader.vue'

const route = useRoute()
const router = useRouter()
const state = reactive({
  refreshing: false,
  list: [],
  loading: false,
  finished: false,
  productList: [],
  totalPage: 0,
  page: 1,
  pageSize: 20,
  orderBy: ''
})

const onMounted = () => {
  init();
}
const init = async () => {
  state.loading = true;
  const { data } = await getSecKillList({ pageNumber: state.page, pageSize: state.pageSize })
  // console.log(data)
  state.productList = state.productList.concat(data.list)
  state.totalPage = data.totalPage
  // console.log(state.totalPage)
  state.loading = false;
  if (state.page >= data.totalPage) state.finished = true
}

const gotoDetail = (seckillId) => {
  router.push({ path: `/seckill/${seckillId}` })
}

const isWithinSeckillTime = (startTime, endTime) => {
  const now = new Date().getTime()
  return now >= startTime && now <= endTime
}

//   const productDetail = (item) => {
//     router.push({ path: `/product/${item.goodsId}` })
//   }

const onLoad = () => {
  if (!state.refreshing && state.page < state.totalPage) {
    state.page = state.page + 1
  }
  if (state.refreshing) {
    state.productList = [];
    state.refreshing = false;
  }
  init()
}

const onRefresh = () => {
  state.refreshing = true
  state.finished = false
  state.loading = true
  state.page = 1
  onLoad()
}
const prefix = (url) => {
  if (url) {
    // console.log("url", url)
    return url.startsWith('http') ? url : `http://backend-api-02.newbee.ltd${url}`;
  }
}

</script>

<style scoped lang="less">
.good-item {
  display: flex;
  margin-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  padding: 10px 0;

  .good-img img {
    width: 100px;
    height: 100px;
    border-radius: 8px;
  }

  .good-desc {
    flex: 1;
    padding-left: 15px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;

    .good-title {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .goods-name {
        font-size: 8px;
        font-weight: bold;
        color: #333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .seckill-price {
        font-size: 18px;
        font-weight: bold;
        color: #e34d4a;
        padding-right: 20px;

        span {
          font-size: 22px;
        }
      }
    }

    .good-btn {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-right: 20px;

      .seckill-time {
        font-size: 14px;
        color: #888;
        display: flex;
        align-items: center;

        .van-icon {
          margin-right: 5px;
        }
      }
    }
  }
}
</style>
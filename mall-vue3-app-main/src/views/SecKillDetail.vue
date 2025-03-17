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
    <div class="product-detail">
      <s-header :name="'秒杀商品详情'"></s-header>
      <div class="detail-content">
        <div class="detail-swipe-wrap">
          <van-swipe class="my-swipe" indicator-color="#1baeae">
            <van-swipe-item>
              <img :src="prefix(state.detail.goodsCoverImg)" alt="">
            </van-swipe-item>
          </van-swipe>
        </div>
        <div class="product-info">
          <div class="product-title">
            {{ state.detail.goodsName || '' }}
          </div>
          <div class="product-desc">{{ state.detail.goodsIntro || '' }}</div>
          <div class="product-price">
            <span>¥{{ state.detail.seckillPrice || '' }}</span>
          </div>
        </div>
        <div class="product-intro">
          <ul>
            <li>概述</li>
            <li>参数</li>
            <li>安装服务</li>
            <li>常见问题</li>
          </ul>
          <div class="product-content" v-html="state.detail.goodsDetailContent || ''"></div>
        </div>
      </div>
      <van-action-bar>
        <van-action-bar-icon icon="chat-o" text="客服" />
        <van-action-bar-button type="danger" @click="buyNow" text="立即秒杀" />
      </van-action-bar>
    </div>
  </template>
  
  <script setup>
  import { reactive, onMounted, nextTick } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { useCartStore } from '@/stores/cart'
  import { getSecKillDetail, seckillExecution, exposerUrl, createSeckillOrder } from '@/service/secKill'
  import { addCart } from '@/service/cart'
  import sHeader from '@/components/SimpleHeader.vue'
  import { showFailToast, showSuccessToast } from 'vant'
  import { prefix } from '@/common/js/utils'
import md5 from 'js-md5'
import { get } from 'vant/lib/utils'
import { createOrder } from '../service/order'
  const route = useRoute()
  const router = useRouter()
  const cart = useCartStore()
  
  const state = reactive({
    detail: {
    }
  })
  
  onMounted(async () => {
    const { seckillId } = route.params
    const { data } = await getSecKillDetail(seckillId)
    state.detail = data
  })
  
  nextTick(() => {
    // 一些和DOM有关的东西
    const content = document.querySelector('.detail-content')
    content.scrollTop = 0
  })
  
  const goBack = () => {
    router.go(-1)
  }
  
  const goTo = () => {
    router.push({ path: '/cart' })
  }
  
  const handleAddCart = async () => {
    const { resultCode } = await addCart({ goodsCount: 1, goodsId: state.detail.goodsId })
    console.log(resultCode)
    if (resultCode == 200 ) showSuccessToast('添加成功')
    cart.updateCart()
  }
  
  const buyNow = async () => {

    // 先获取 加密路径
    const exposer = await exposerUrl(state.detail.seckillId)
    console.log(exposer)
      if(exposer.data.seckillStatusEnum == "START"){

          var md5 = exposer.data.md5
          const data = await seckillExecution(state.detail.seckillId, md5)

          // const result = await createSeckillOrder(data.data.seckillSuccessId, data.data.md5)
          showSuccessToast('秒杀成功')
          
          router.push({ path: '/order'})
        }
      else{
        showFailToast(exposer.data.seckillStatusEnum)
      }
    
    // const {data} = await seckillExecution(state.detail.seckillId, md5(String(state.detail.seckillId)))
    // console.log(data)

    
    // router.push({ path: '/create-seckill-order', query: { seckillId: state.detail.seckillId} })
    
    // router.push({ path: '/create-seckill-order', query: { seckillId: state.detail.seckillId } })
    
    
  }
  
  </script>
  
  <style lang="less">
    @import '../common/style/mixin';
    .product-detail {
      .detail-header {
        position: fixed;
        top: 0;
        left: 0;
        z-index: 10000;
        .fj();
        .wh(100%, 44px);
        line-height: 44px;
        padding: 0 10px;
        .boxSizing();
        color: #252525;
        background: #fff;
        border-bottom: 1px solid #dcdcdc;
        .product-name {
          font-size: 14px;
        }
      }
      .detail-content {
        height: calc(100vh - 50px);
        overflow: hidden;
        overflow-y: auto;
        .detail-swipe-wrap {
          .my-swipe .van-swipe-item {
            img {
              width: 100%;
              // height: 300px;
            }
          }
        }
        .product-info {
          padding: 0 10px;
          .product-title {
            font-size: 18px;
            text-align: left;
            color: #333;
          }
          .product-desc {
            font-size: 14px;
            text-align: left;
            color: #999;
            padding: 5px 0;
          }
          .product-price {
            .fj();
            span:nth-child(1) {
              color: #F63515;
              font-size: 22px;
            }
            span:nth-child(2) {
              color: #999;
              font-size: 16px;
            }
          }
        }
        .product-intro {
          width: 100%;
          padding-bottom: 50px;
          ul {
            .fj();
            width: 100%;
            margin: 10px 0;
            li {
              flex: 1;
              padding: 5px 0;
              text-align: center;
              font-size: 15px;
              border-right: 1px solid #999;
              box-sizing: border-box;
              &:last-child {
                border-right: none;
              }
            }
          }
          .product-content {
            padding: 0 20px;
            img {
              width: 100%;
            }
          }
        }
      }
      .van-action-bar-button--warning {
        background: linear-gradient(to right,#6bd8d8, @primary)
      }
      .van-action-bar-button--danger {
        background: linear-gradient(to right, #0dc3c3, #098888)
      }
    }
  </style>
  
<template>
    <el-card class="index-container">
      <template #header>
        <div class="header">
          <el-button type="primary" :icon="Plus" @click="handleAdd">增加</el-button>
          <el-popconfirm
            title="确定删除吗？"
            confirmButtonText='确定'
            cancelButtonText='取消'
            @confirm="handleDelete"
          >
            <template #reference>
              <el-button type="danger" :icon="Delete">批量删除</el-button>
            </template>
          </el-popconfirm>
        </div>
      </template>
      <el-table
        :load="state.loading"
        ref="multipleTable"
        :data="state.tableData"
        tooltip-effect="dark"
        style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column
          type="selection"
          width="55">
        </el-table-column>
        <el-table-column
          prop="goodsId"
          label="商品编号"
          width="100"
        >
        </el-table-column>
        <el-table-column
          prop="seckillPrice"
          label="秒杀价格"
          width="100"
        >
        </el-table-column>
        <el-table-column
          prop="seckillNum"
          label="秒杀数量"
          width="100"
        >
        </el-table-column>
        <el-table-column
          prop="seckillBegin"
          label="开始日期"
          width="180"
        >
        </el-table-column>
        <el-table-column
          prop="seckillEnd"
          label="截止日期"
          width="180"
        >
        </el-table-column>
        
        <el-table-column
          prop="seckillStatus"
          label="状态"
          width="100"
        >
        </el-table-column>
        <el-table-column
          prop="seckillRank"
          label="排序值"
          width="100"
        >
        </el-table-column>
        
        <el-table-column
          prop="createTime"
          label="添加时间"
          width="180"
        >
        </el-table-column>
        <el-table-column
          label="操作"
          width="120"
        >
          <template #default="scope">
            <a style="cursor: pointer; margin-right: 10px" @click="handleEdit(scope.row.seckillId)">修改</a>
            <el-popconfirm
              title="确定删除吗？"
              confirmButtonText='确定'
              cancelButtonText='取消'
              @confirm="handleDeleteOne(scope.row.seckillId)"
            >
              <template #reference>
                <a style="cursor: pointer">删除</a>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <!--总数超过一页，再展示分页器-->
      <el-pagination
        background
        layout="prev, pager, next"
        :total="state.total"
        :page-size="state.pageSize"
        :current-page="state.currentPage"
        @current-change="changePage"
      />
    </el-card>
    <DialogAddSecKill ref='addSeckill' :reload="getSecKillList" :type="state.type"/>
  </template>
  
  <script setup>
  import { onMounted, reactive, ref, toRefs } from 'vue'
  import { ElMessage } from 'element-plus'
  import { Plus, Delete } from '@element-plus/icons-vue'
  import axios from '@/utils/axios'
  import DialogAddSecKill from '@/components/DialogAddSecKill.vue'
 
  const multipleTable = ref(null)
  const addSeckill = ref(null)
  const state = reactive({
    loading: false,
    tableData: [], // 数据列表
    multipleSelection: [], // 选中项
    total: 0, // 总条数
    currentPage: 1, // 当前页
    pageSize: 10, // 分页大小
    type: 'add', // 操作类型
  })

  // 初始化
  onMounted(() => {

    getSecKillList()
  })
  // 首页热销商品列表
  const getSecKillList = () => {
    state.loading = true
    axios.get('/seckill/list', {
      params: {
        pageNumber: state.currentPage,
        pageSize: state.pageSize,
      }
    }).then(res => {
      state.tableData = res.list
      state.total = res.totalCount
      state.currentPage = res.currPage
      state.loading = false
    })
  }
  // 添加商品
  const handleAdd = () => {
    state.type = 'add'
    addSeckill.value.open()
  }
  // 修改商品
  const handleEdit = (id) => {
    state.type = 'edit'
    addSeckill.value.open(id)
  }
  // 选择项
  const handleSelectionChange = (val) => {
    state.multipleSelection = val
  }
  // 删除
  const handleDelete = () => {
    if (!state.multipleSelection.length) {
      ElMessage.error('请选择项')
      return
    }
    axios.delete('/seckill', {
      data:{
        ids: state.multipleSelection.map(i => i.seckillId)
      }
    }).then(() => {
      ElMessage.success('删除成功')
      getSecKillList()
    })
  }
  // 单个删除
  const handleDeleteOne = (id) => {
    axios.delete('/seckill', {
      data:{
        ids: [id]
      }
    }).then(() => {
      ElMessage.success('删除成功')
      getSecKillList()
    })
  }
  const changePage = (val) => {
    state.currentPage = val
    getSecKillList()
  }
  </script>
  
  <style scoped>
    .index-container {
      min-height: 100%;
    }
    .el-card.is-always-shadow {
      min-height: 100%!important;
    }
  </style>
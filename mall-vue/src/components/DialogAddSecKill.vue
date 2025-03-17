<template>
  <el-dialog
    :title="type == 'add' ? '添加秒杀商品' : '修改秒杀商品'"
    v-model="state.visible"
    width="400px"
  >
    <el-form :model="state.ruleForm" :rules="state.rules" ref="formRef" label-width="100px" class="good-form">
      <el-form-item label="商品编号" prop="goodsId">
        <el-input type="text" v-model="state.ruleForm.goodsId"></el-input>
      </el-form-item>
      <el-form-item label="秒杀价格" prop="seckillPrice">
        <el-input type="text" v-model="state.ruleForm.seckillPrice"></el-input>
      </el-form-item>
      <el-form-item label="秒杀数量" prop="seckillNum">
        <el-input type="text" v-model="state.ruleForm.seckillNum"></el-input>
      </el-form-item>
      <el-form-item label="排序值" prop="seckillRank">
        <el-input type="number" v-model="state.ruleForm.seckillRank"></el-input>
      </el-form-item>
      <el-form-item label="秒杀状态" prop="seckillStatus">
        <el-select v-model="state.ruleForm.seckillStatus" placeholder="选择秒杀状态">
          <el-option label="开启" :value="true"></el-option>
          <el-option label="关闭" :value="false"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="开始时间" prop="seckillBegin">
        <el-date-picker 
          v-model="state.ruleForm.seckillBegin" 
          type="datetime" 
          placeholder="选择开始时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间" prop="seckillEnd">
        <el-date-picker 
          v-model="state.ruleForm.seckillEnd" 
          type="datetime" 
          placeholder="选择结束时间">
        </el-date-picker>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="state.visible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'

const props = defineProps({
  type: String, // 用于判断是添加还是编辑
  reload: Function // 添加或修改完后，刷新列表页
})

const formRef = ref(null)
const route = useRoute()
const state = reactive({
  visible: false,
  ruleForm: {
    goodsId: '',
    seckillPrice: '',
    seckillNum: '',
    seckillRank: '',
    seckillStatus: '',
    seckillBegin: '',
    seckillEnd: ''
  },
  rules: {
    goodsId: [
      { required: 'true', message: '商品编号不能为空', trigger: ['change'] }
    ],
    seckillPrice: [
      { required: 'true', message: '秒杀价格不能为空', trigger: ['change'] }
    ],
    seckillNum: [
      { required: 'true', message: '秒杀数量不能为空', trigger: ['change'] }
    ],
    seckillStatus: [
      { required: 'true', message: '状态不能为空', trigger: ['change'] }
    ],
    seckillRank: [
      { required: 'true', message: '编号不能为空', trigger: ['change'] }
    ],
    seckillBegin: [
      { required: 'true', message: '开始时间不能为空', trigger: ['change'] }
    ],
    seckillEnd: [
      { required: 'true', message: '结束时间不能为空', trigger: ['change'] }
    ]
  },
  id: ''
})
// 获取详情
const getDetail = (id) => {
  axios.get(`/seckill/${id}`).then(res => {
    console.log(res)
    state.ruleForm = {
      goodsId: res.goodsId,
      seckillPrice: res.seckillPrice,
      seckillNum: res.seckillNum,
      seckillRank: res.seckillRank,
      seckillStatus: res.seckillStatus,
      seckillBegin: res.seckillBegin,
      seckillEnd: res.seckillEnd
    }
  })
}
// 开启弹窗
const open = (id) => {
  state.visible = true
  if (id) {
    state.id = id
    // 如果是有 id 传入，证明是修改模式
    getDetail(id)
  } else {
    // 否则为新增模式
    state.ruleForm = {
      goodsId: '',
      seckillPrice: '',
      seckillNum: '',
      seckillRank: '',
      seckillStatus: '',
      seckillBegin: '',
      seckillEnd: ''
    }
  }
}
// 关闭弹窗
const close = () => {
  state.visible = false
}
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (new Date(state.ruleForm.seckillBegin) >= new Date(state.ruleForm.seckillEnd)) {
        ElMessage.error('开始时间必须小于结束时间');
        return; // 终止提交
      }
    
      if (props.type == 'add') {
        // 添加方法
        axios.post('/seckill', {
          goodsId: state.ruleForm.goodsId,
          seckillPrice: state.ruleForm.seckillPrice,
          seckillNum: state.ruleForm.seckillNum,
          seckillRank: state.ruleForm.seckillRank,
          seckillStatus: state.ruleForm.seckillStatus,
          seckillBegin: formatDate(state.ruleForm.seckillBegin),
          seckillEnd: formatDate(state.ruleForm.seckillEnd)
        }).then(() => {
          ElMessage.success('添加成功')
          state.visible = false
          // 接口回调之后，运行重新获取列表方法 reload
          if (props.reload) props.reload()
        })
      } else {
        // 修改方法
        axios.put('/seckill', {
          seckillId: state.id,
          goodsId: state.ruleForm.goodsId,
          seckillPrice: state.ruleForm.seckillPrice,
          seckillNum: state.ruleForm.seckillNum,
          seckillRank: state.ruleForm.seckillRank,
          seckillStatus: state.ruleForm.seckillStatus,
          seckillBegin: formatDate(state.ruleForm.seckillBegin),
          seckillEnd: formatDate(state.ruleForm.seckillEnd)
        }).then(() => {
          ElMessage.success('修改成功')
          state.visible = false
          // 接口回调之后，运行重新获取列表方法 reload
          if (props.reload) props.reload()
        })
      }
    }
  })
}

function formatDate(dateString) {
    const date = new Date(dateString); // 创建 Date 对象
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 月份从 0 开始
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

defineExpose({ open, close })
</script>
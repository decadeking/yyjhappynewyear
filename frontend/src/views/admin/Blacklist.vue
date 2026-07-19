<template>
  <div class="blacklist-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>黑名单管理</span>
          <el-button type="primary" @click="showAddDialog" style="background-color: #67C23A; border-color: #67C23A;">
            <el-icon><Plus /></el-icon>添加黑名单
          </el-button>
        </div>
      </template>
      <el-table :data="blacklist" stripe>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="userId" label="用户ID" width="120"></el-table-column>
        <el-table-column prop="reason" label="原因" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'danger' : 'info'" size="small">
              {{ scope.row.status === 1 ? '生效中' : '已解除' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button v-if="scope.row.status === 1" type="success" text size="small" @click="removeFromBlacklist(scope.row.id)">
              解除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="10"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadBlacklist"
        />
      </div>
    </el-card>

    <el-dialog v-model="addDialogVisible" title="添加黑名单" width="500px">
      <el-form :model="addForm" label-width="100px">
        <el-form-item label="用户ID">
          <el-input v-model="addForm.userId" placeholder="请输入用户ID"></el-input>
        </el-form-item>
        <el-form-item label="原因">
          <el-input v-model="addForm.reason" type="textarea" :rows="4" placeholder="请输入拉黑原因..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAdd" style="background-color: #67C23A; border-color: #67C23A;">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const blacklist = ref([])
const total = ref(0)
const pageNum = ref(1)
const addDialogVisible = ref(false)
const addForm = reactive({
  userId: '',
  reason: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const loadBlacklist = async () => {
  try {
    const res = await request.get('/api/blacklist/list', {
      params: { pageNum: pageNum.value, pageSize: 10 }
    })
    blacklist.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const showAddDialog = () => {
  addForm.userId = ''
  addForm.reason = ''
  addDialogVisible.value = true
}

const confirmAdd = async () => {
  if (!addForm.userId || !addForm.reason) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    await request.post('/api/blacklist/add', addForm)
    ElMessage.success('已加入黑名单')
    addDialogVisible.value = false
    loadBlacklist()
  } catch (error) {
    console.error(error)
  }
}

const removeFromBlacklist = async (id) => {
  try {
    await ElMessageBox.confirm('确认解除该用户的黑名单？', '确认')
    await request.put('/api/blacklist/remove/' + id)
    ElMessage.success('已解除黑名单')
    loadBlacklist()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(() => {
  loadBlacklist()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-header span {
  font-size: 16px;
  font-weight: bold;
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>

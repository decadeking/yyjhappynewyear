<template>
  <div class="review-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>信息审核</span>
          <div class="filter-bar">
            <el-select v-model="statusFilter" placeholder="按状态筛选" clearable @change="loadItems">
              <el-option label="待审核" :value="0"></el-option>
              <el-option label="公示中" :value="2"></el-option>
              <el-option label="已驳回" :value="1"></el-option>
            </el-select>
            <el-input v-model="keyword" placeholder="搜索标题..." clearable @keyup.enter="loadItems" style="width: 200px;"></el-input>
            <el-button type="primary" @click="loadItems" style="background-color: #67C23A; border-color: #67C23A;">搜索</el-button>
          </div>
        </div>
      </template>
      <el-table :data="items" stripe>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 0 ? 'warning' : 'success'" size="small">
              {{ scope.row.type === 0 ? '寻物' : '招领' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button type="primary" text size="small" @click="viewDetail(scope.row)">查看</el-button>
            <el-button v-if="scope.row.status === 0" type="success" text size="small" @click="approveItem(scope.row.id)">通过</el-button>
            <el-button v-if="scope.row.status === 0" type="warning" text size="small" @click="showRejectDialog(scope.row.id)">驳回</el-button>
            <el-button v-if="scope.row.status === 2" type="danger" text size="small" @click="removeItem(scope.row.id)">下架</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="10"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadItems"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="物品详情" width="600px">
      <div v-if="currentItem">
        <p><strong>标题：</strong>{{ currentItem.title }}</p>
        <p><strong>类型：</strong>{{ currentItem.type === 0 ? '寻物' : '招领' }}</p>
        <p><strong>分类：</strong>{{ currentItem.category }}</p>
        <p><strong>地点：</strong>{{ currentItem.location }}</p>
        <p><strong>描述：</strong>{{ currentItem.description }}</p>
      </div>
    </el-dialog>

    <el-dialog v-model="rejectDialogVisible" title="驳回信息" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="驳回原因">
          <el-input v-model="rejectForm.reason" type="textarea" :rows="4" placeholder="请输入驳回原因..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="confirmReject" style="background-color: #E6A23C; border-color: #E6A23C;">确认驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const items = ref([])
const total = ref(0)
const pageNum = ref(1)
const statusFilter = ref(null)
const keyword = ref('')
const detailDialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const currentItem = ref(null)
const rejectItemId = ref(null)
const rejectForm = reactive({ reason: '' })

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'danger', 2: 'success', 3: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已驳回', 2: '公示中', 3: '已完结' }
  return texts[status] || '未知'
}

const loadItems = async () => {
  try {
    const res = await request.get('/api/item/admin/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: 10,
        status: statusFilter.value,
        keyword: keyword.value
      }
    })
    items.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const viewDetail = (item) => {
  currentItem.value = item
  detailDialogVisible.value = true
}

const approveItem = async (id) => {
  try {
    await ElMessageBox.confirm('确认通过该物品信息？', '确认')
    await request.put('/api/item/admin/approve/' + id)
    ElMessage.success('已通过审核')
    loadItems()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const showRejectDialog = (id) => {
  rejectItemId.value = id
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.reason.trim()) {
    ElMessage.warning('请输入驳回原因')
    return
  }
  try {
    await request.put('/api/item/admin/reject/' + rejectItemId.value, rejectForm)
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    loadItems()
  } catch (error) {
    console.error(error)
  }
}

const removeItem = async (id) => {
  try {
    await ElMessageBox.confirm('确认下架该物品信息？', '确认')
    await request.put('/api/item/admin/remove/' + id)
    ElMessage.success('已下架')
    loadItems()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(() => {
  loadItems()
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
.filter-bar {
  display: flex;
  gap: 10px;
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>

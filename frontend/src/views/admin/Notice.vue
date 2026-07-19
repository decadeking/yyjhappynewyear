<template>
  <div class="notice-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="showAddDialog" style="background-color: #67C23A; border-color: #67C23A;">
            <el-icon><Plus /></el-icon>新增公告
          </el-button>
        </div>
      </template>
      <el-table :data="notices" stripe>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="标题" width="250" show-overflow-tooltip></el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" text size="small" @click="showEditDialog(scope.row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="deleteNotice(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="pageNum"
          :page-size="10"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadNotices"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '新增公告'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入公告标题"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容..."></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSave" style="background-color: #67C23A; border-color: #67C23A;">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const notices = ref([])
const total = ref(0)
const pageNum = ref(1)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  title: '',
  content: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const loadNotices = async () => {
  try {
    const res = await request.get('/api/notice/list', {
      params: { pageNum: pageNum.value, pageSize: 10 }
    })
    notices.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const showAddDialog = () => {
  isEdit.value = false
  form.id = null
  form.title = ''
  form.content = ''
  dialogVisible.value = true
}

const showEditDialog = (notice) => {
  isEdit.value = true
  form.id = notice.id
  form.title = notice.title
  form.content = notice.content
  dialogVisible.value = true
}

const confirmSave = async () => {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写完整信息')
    return
  }
  try {
    if (isEdit.value) {
      await request.put('/api/notice/update', form)
      ElMessage.success('公告已更新')
    } else {
      await request.post('/api/notice/add', form)
      ElMessage.success('公告已添加')
    }
    dialogVisible.value = false
    loadNotices()
  } catch (error) {
    console.error(error)
  }
}

const deleteNotice = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该公告？', '确认')
    await request.delete('/api/notice/delete/' + id)
    ElMessage.success('公告已删除')
    loadNotices()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

onMounted(() => {
  loadNotices()
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

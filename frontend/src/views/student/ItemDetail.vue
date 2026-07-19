<template>
  <div class="item-detail-page">
    <el-card v-if="item" shadow="never">
      <el-row :gutter="24">
        <el-col :span="12">
          <div class="image-gallery">
            <el-carousel v-if="imageList.length > 0" height="400px" indicator-position="outside">
              <el-carousel-item v-for="(img, index) in imageList" :key="index">
                <img :src="img" class="carousel-image" @error="handleImageError" />
              </el-carousel-item>
            </el-carousel>
            <el-empty v-else description="暂无图片" />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="item-info">
            <div class="item-header">
              <h2>{{ item.title }}</h2>
              <el-tag :type="item.type === 0 ? 'warning' : 'success'" size="large">
                {{ item.type === 0 ? '寻物' : '招领' }}
              </el-tag>
            </div>
            <div class="info-row">
              <span class="label">分类：</span>
              <span>{{ item.category }}</span>
            </div>
            <div class="info-row">
              <span class="label">地点：</span>
              <span>{{ item.location || '未知' }}</span>
            </div>
            <div class="info-row">
              <span class="label">时间：</span>
              <span>{{ formatDate(item.lostFoundTime) }}</span>
            </div>
            <div class="info-row">
              <span class="label">发布时间：</span>
              <span>{{ formatDate(item.createTime) }}</span>
            </div>
            <div class="info-row">
              <span class="label">浏览次数：</span>
              <span>{{ item.viewCount }}</span>
            </div>
            <div class="info-row">
              <span class="label">状态：</span>
              <el-tag :type="getStatusType(item.status)">{{ getStatusText(item.status) }}</el-tag>
            </div>
            <div class="description">
              <h4>详细描述</h4>
              <p>{{ item.description }}</p>
            </div>
            <div class="actions" v-if="item.status === 2 && currentUser.id !== item.userId">
              <el-button type="primary" @click="showClaimDialog" style="background-color: #67C23A; border-color: #67C23A;">
                <el-icon><Check /></el-icon>申请认领
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="comments-section" shadow="never">
      <template #header>
        <div class="card-header">
          <span>留言 ({{ messages.length }})</span>
        </div>
      </template>
      <div class="comment-input">
        <el-input
          v-model="newMessage"
          type="textarea"
          :rows="3"
          placeholder="请输入留言内容..."
        ></el-input>
        <el-button type="primary" @click="submitMessage" style="margin-top: 10px; background-color: #67C23A; border-color: #67C23A;">
          发表留言
        </el-button>
      </div>
      <div class="comment-list">
        <div v-for="msg in messages" :key="msg.id" class="comment-item">
          <div class="comment-header">
            <span class="comment-user">用户 {{ msg.userId }}</span>
            <span class="comment-time">{{ formatDate(msg.createTime) }}</span>
          </div>
          <div class="comment-content">{{ msg.content }}</div>
        </div>
        <el-empty v-if="messages.length === 0" description="暂无留言" />
      </div>
    </el-card>

    <el-dialog v-model="claimDialogVisible" title="申请认领" width="500px">
      <el-form :model="claimForm" label-width="100px">
        <el-form-item label="认领说明">
          <el-input
            v-model="claimForm.claimInfo"
            type="textarea"
            :rows="4"
            placeholder="请描述您如何证明该物品属于您..."
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="claimDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitClaim" style="background-color: #67C23A; border-color: #67C23A;">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'
import { parseImages, handleImageError } from '../../utils/imageHelper'

const route = useRoute()
const item = ref(null)
const messages = ref([])
const newMessage = ref('')
const claimDialogVisible = ref(false)
const claimForm = ref({ claimInfo: '' })
const currentUser = ref({})

const imageList = computed(() => {
  if (!item.value || !item.value.images) return []
  return parseImages(item.value.images)
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'danger', 2: 'success', 3: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待审核', 1: '已驳回', 2: '公示中', 3: '已完结' }
  return texts[status] || '未知'
}

const loadItemDetail = async () => {
  try {
    const res = await request.get('/api/item/detail/' + route.params.id)
    item.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadMessages = async () => {
  try {
    const res = await request.get('/api/message/list/' + route.params.id)
    messages.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const submitMessage = async () => {
  if (!newMessage.value.trim()) {
    ElMessage.warning('请输入留言内容')
    return
  }
  try {
    await request.post('/api/message/add', {
      itemId: route.params.id,
      content: newMessage.value
    })
    ElMessage.success('留言发表成功')
    newMessage.value = ''
    loadMessages()
  } catch (error) {
    console.error(error)
  }
}

const showClaimDialog = () => {
  claimForm.value = { claimInfo: '' }
  claimDialogVisible.value = true
}

const submitClaim = async () => {
  if (!claimForm.value.claimInfo.trim()) {
    ElMessage.warning('请填写认领说明')
    return
  }
  try {
    await request.post('/api/claim/submit', {
      itemId: route.params.id,
      claimInfo: claimForm.value.claimInfo
    })
    ElMessage.success('认领申请提交成功')
    claimDialogVisible.value = false
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    currentUser.value = JSON.parse(storedUser)
  }
  loadItemDetail()
  loadMessages()
})
</script>

<style scoped>
.item-detail-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.image-gallery {
  border-radius: 4px;
  overflow: hidden;
}
.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: #F5F7FA;
}
.item-info {
  padding: 20px;
}
.item-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}
.item-header h2 {
  margin: 0;
  color: #303133;
}
.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}
.info-row .label {
  color: #909399;
  width: 100px;
}
.description {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #E4E7ED;
}
.description h4 {
  margin-bottom: 12px;
  color: #303133;
}
.description p {
  color: #606266;
  line-height: 1.6;
}
.actions {
  margin-top: 24px;
}
.comments-section {
  margin-top: 20px;
}
.card-header {
  font-size: 16px;
  font-weight: bold;
}
.comment-input {
  margin-bottom: 24px;
}
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.comment-item {
  padding: 16px;
  background-color: #F5F7FA;
  border-radius: 4px;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}
.comment-user {
  font-weight: bold;
  color: #303133;
}
.comment-time {
  color: #909399;
  font-size: 12px;
}
.comment-content {
  color: #606266;
  line-height: 1.6;
}
</style>


<template>
  <div class="home-page">
    <div class="notice-section">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><Bell /></el-icon>
            <span>系统公告</span>
          </div>
        </template>
        <div v-if="notices.length === 0" class="empty-notice">暂无公告</div>
        <div v-else class="notice-list">
          <div v-for="notice in notices" :key="notice.id" class="notice-item">
            <div class="notice-title">{{ notice.title }}</div>
            <div class="notice-content">{{ notice.content }}</div>
            <div class="notice-time">{{ formatDate(notice.createTime) }}</div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background-color: #E1F3D8;">
              <el-icon color="#67C23A" :size="32"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalItems || 0 }}</div>
              <div class="stat-label">物品总数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background-color: #FDE2E2;">
              <el-icon color="#F56C6C" :size="32"><Search /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.lostItems || 0 }}</div>
              <div class="stat-label">寻物信息</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background-color: #E1F3D8;">
              <el-icon color="#67C23A" :size="32"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.foundItems || 0 }}</div>
              <div class="stat-label">招领信息</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background-color: #E6A23C20;">
              <el-icon color="#E6A23C" :size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
              <div class="stat-label">注册用户</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="items-section">
      <div class="section-header">
        <h3>最新物品</h3>
        <el-button type="primary" text @click="$router.push('/items')">查看全部</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6" v-for="item in latestItems" :key="item.id">
          <el-card shadow="hover" class="item-card" @click="$router.push('/item/' + item.id)">
            <div class="item-image">
              <img :src="getFirstImage(item.images)" alt="物品图片" @error="handleImageError" />
            </div>
            <div class="item-info">
              <div class="item-title">{{ item.title }}</div>
              <div class="item-meta">
                <el-tag :type="item.type === 0 ? 'warning' : 'success'" size="small">
                  {{ item.type === 0 ? '寻物' : '招领' }}
                </el-tag>
                <span class="item-category">{{ item.category }}</span>
              </div>
              <div class="item-time">{{ formatDate(item.createTime) }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-if="latestItems.length === 0" description="暂无物品信息" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { parseImages, getFirstImage, handleImageError } from '../../utils/imageHelper'

const notices = ref([])
const stats = ref({})
const latestItems = ref([])

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString()
}


onMounted(async () => {
  try {
    const [noticeRes, statsRes, itemsRes] = await Promise.all([
      request.get('/api/notice/list', { params: { pageNum: 1, pageSize: 5 } }),
      request.get('/api/home/stats'),
      request.get('/api/item/list', { params: { pageNum: 1, pageSize: 8 } })
    ])
    notices.value = noticeRes.data.records || []
    stats.value = statsRes.data || {}
    latestItems.value = itemsRes.data.records || []
  } catch (error) {
    console.error(error)
  }
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}
.empty-notice {
  text-align: center;
  color: #909399;
  padding: 20px;
}
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.notice-item {
  padding: 12px;
  border-bottom: 1px solid #E4E7ED;
}
.notice-item:last-child {
  border-bottom: none;
}
.notice-title {
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}
.notice-content {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}
.notice-time {
  color: #909399;
  font-size: 12px;
}
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}
.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.section-header h3 {
  margin: 0;
  color: #303133;
}
.item-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.item-card:hover {
  transform: translateY(-4px);
}
.item-image {
  width: 100%;
  height: 160px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 12px;
}
.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.item-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.item-category {
  color: #909399;
  font-size: 12px;
}
.item-time {
  color: #909399;
  font-size: 12px;
}
</style>

<template>
  <div class="item-list-page">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="filters">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="搜索物品..." clearable></el-input>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="filters.type" placeholder="全部类型" clearable>
            <el-option label="寻物" :value="0"></el-option>
            <el-option label="招领" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filters.category" placeholder="全部分类" clearable>
            <el-option label="电子产品" value="电子产品"></el-option>
            <el-option label="书籍文具" value="书籍文具"></el-option>
            <el-option label="衣物配饰" value="衣物配饰"></el-option>
            <el-option label="证件卡片" value="证件卡片"></el-option>
            <el-option label="生活用品" value="生活用品"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadItems" style="background-color: #67C23A; border-color: #67C23A;">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="20" class="items-grid">
      <el-col :span="6" v-for="item in items" :key="item.id">
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
            <div class="item-location">
              <el-icon><Location /></el-icon>
              <span>{{ item.location || '未知' }}</span>
            </div>
            <div class="item-time">{{ formatDate(item.createTime) }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="items.length === 0" description="未找到相关物品" />

    <div class="pagination-wrapper" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadItems"
        @current-change="loadItems"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '../../utils/request'
import { parseImages, getFirstImage, handleImageError } from '../../utils/imageHelper'

const items = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const filters = reactive({
  keyword: '',
  type: null,
  category: ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}


const loadItems = async () => {
  try {
    const res = await request.get('/api/item/list', {
      params: {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        keyword: filters.keyword,
        type: filters.type,
        category: filters.category
      }
    })
    items.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const resetFilters = () => {
  filters.keyword = ''
  filters.type = null
  filters.category = ''
  pageNum.value = 1
  loadItems()
}

onMounted(() => {
  loadItems()
})
</script>

<style scoped>
.item-list-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.filter-card {
  margin-bottom: 10px;
}
.items-grid {
  min-height: 400px;
}
.item-card {
  cursor: pointer;
  transition: transform 0.2s;
  margin-bottom: 20px;
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
  background-color: #F5F7FA;
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
.item-location {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
}
.item-time {
  color: #909399;
  font-size: 12px;
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>

<template>
  <div class="statistics-page">
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.totalItems || 0 }}</div>
          <div class="stat-label">物品总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.publishedItems || 0 }}</div>
          <div class="stat-label">已发布物品</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.completedItems || 0 }}</div>
          <div class="stat-label">已认领物品</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">用户总数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">物品分类分布</div>
          </template>
          <div ref="pieChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">月度发布趋势</div>
          </template>
          <div ref="barChartRef" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import request from '../../utils/request'

const stats = ref({})
const pieChartRef = ref(null)
const barChartRef = ref(null)
let pieChart = null
let barChart = null

const loadStats = async () => {
  try {
    const res = await request.get('/api/home/stats')
    stats.value = res.data || {}
  } catch (error) {
    console.error(error)
  }
}

const loadPieChart = async () => {
  try {
    const res = await request.get('/api/home/category-stats')
    const data = res.data || []

    if (pieChart) {
      pieChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        color: ['#67C23A', '#E6A23C', '#909399', '#409EFF', '#F56C6C'],
        series: [{
          type: 'pie',
          radius: '60%',
          data: data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      })
    }
  } catch (error) {
    console.error(error)
  }
}

const loadBarChart = async () => {
  try {
    const res = await request.get('/api/home/monthly-stats')
    const data = res.data || { months: [], counts: [] }

    if (barChart) {
      barChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: data.months || [],
          axisLabel: { rotate: 30 }
        },
        yAxis: { type: 'value' },
        series: [{
          data: data.counts || [],
          type: 'bar',
          itemStyle: {
            color: '#67C23A'
          }
        }]
      })
    }
  } catch (error) {
    console.error(error)
  }
}

const initCharts = () => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
  }
  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value)
  }

  window.addEventListener('resize', () => {
    pieChart && pieChart.resize()
    barChart && barChart.resize()
  })
}

onMounted(async () => {
  initCharts()
  await loadStats()
  await loadPieChart()
  await loadBarChart()
})

onBeforeUnmount(() => {
  pieChart && pieChart.dispose()
  barChart && barChart.dispose()
})
</script>

<style scoped>
.statistics-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.stats-cards {
  margin-bottom: 10px;
}
.stat-card {
  text-align: center;
  padding: 20px;
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #67C23A;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #909399;
}
.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style>

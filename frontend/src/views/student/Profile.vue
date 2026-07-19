<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="never" class="menu-card">
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="my-items">
              <el-icon><Box /></el-icon>
              <span>我的发布</span>
            </el-menu-item>
            <el-menu-item index="my-claims">
              <el-icon><Check /></el-icon>
              <span>我的认领</span>
            </el-menu-item>
            <el-menu-item index="received-claims">
              <el-icon><Document /></el-icon>
              <span>收到的认领</span>
            </el-menu-item>
            <el-menu-item index="profile-settings">
              <el-icon><User /></el-icon>
              <span>个人设置</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card shadow="never" v-if="activeMenu === 'my-items'">
          <template #header>
            <div class="card-header">我发布的物品</div>
          </template>
          <el-table :data="myItems" stripe>
            <el-table-column prop="title" label="标题" width="200"></el-table-column>
            <el-table-column prop="type" label="类型" width="100">
              <template #default="scope">
                <el-tag :type="scope.row.type === 0 ? 'warning' : 'success'" size="small">
                  {{ scope.row.type === 0 ? '寻物' : '招领' }}
                </el-tag>
              </template>
            </el-table-column>
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
            <el-table-column label="操作" width="150">
              <template #default="scope">
                <el-button type="primary" text size="small" @click="$router.push('/item/' + scope.row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrapper" v-if="myItemsTotal > 0">
            <el-pagination
              v-model:current-page="myItemsPage"
              :page-size="10"
              :total="myItemsTotal"
              layout="total, prev, pager, next"
              @current-change="loadMyItems"
            />
          </div>
        </el-card>

        <el-card shadow="never" v-if="activeMenu === 'my-claims'">
          <template #header>
            <div class="card-header">我的认领申请</div>
          </template>
          <el-table :data="myClaims" stripe>
            <el-table-column prop="itemId" label="物品ID" width="100"></el-table-column>
            <el-table-column prop="claimInfo" label="认领说明" show-overflow-tooltip></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getClaimStatusType(scope.row.status)" size="small">
                  {{ getClaimStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="提交时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.createTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="never" v-if="activeMenu === 'received-claims'">
          <template #header>
            <div class="card-header">收到的认领申请</div>
          </template>
          <el-table :data="receivedClaims" stripe>
            <el-table-column prop="itemId" label="物品ID" width="100"></el-table-column>
            <el-table-column prop="claimUserId" label="认领人ID" width="120"></el-table-column>
            <el-table-column prop="claimInfo" label="认领说明" show-overflow-tooltip></el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getClaimStatusType(scope.row.status)" size="small">
                  {{ getClaimStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <template v-if="scope.row.status === 0">
                  <el-button type="success" size="small" @click="approveClaim(scope.row.id)">通过</el-button>
                  <el-button type="danger" size="small" @click="rejectClaim(scope.row.id)">拒绝</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card shadow="never" v-if="activeMenu === 'profile-settings'">
          <template #header>
            <div class="card-header">个人设置</div>
          </template>
          <el-form :model="profileForm" label-width="120px" style="max-width: 500px;">
            <el-form-item label="用户名">
              <el-input v-model="profileForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="profileForm.nickname"></el-input>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone"></el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateProfile" style="background-color: #67C23A; border-color: #67C23A;">保存修改</el-button>
            </el-form-item>
          </el-form>
          <el-divider></el-divider>
          <h4 style="margin-bottom: 20px;">修改密码</h4>
          <el-form :model="passwordForm" label-width="150px" style="max-width: 500px;">
            <el-form-item label="原密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword" style="background-color: #67C23A; border-color: #67C23A;">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../utils/request'

const activeMenu = ref('my-items')
const myItems = ref([])
const myItemsTotal = ref(0)
const myItemsPage = ref(1)
const myClaims = ref([])
const receivedClaims = ref([])

const profileForm = reactive({
  username: '',
  nickname: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: ''
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

const getClaimStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getClaimStatusText = (status) => {
  const texts = { 0: '待处理', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
}

const handleMenuSelect = (index) => {
  activeMenu.value = index
  if (index === 'my-items') loadMyItems()
  else if (index === 'my-claims') loadMyClaims()
  else if (index === 'received-claims') loadReceivedClaims()
}

const loadMyItems = async () => {
  try {
    const res = await request.get('/api/item/my', {
      params: { pageNum: myItemsPage.value, pageSize: 10 }
    })
    myItems.value = res.data.records || []
    myItemsTotal.value = res.data.total || 0
  } catch (error) {
    console.error(error)
  }
}

const loadMyClaims = async () => {
  try {
    const res = await request.get('/api/claim/my', {
      params: { pageNum: 1, pageSize: 50 }
    })
    myClaims.value = res.data.records || []
  } catch (error) {
    console.error(error)
  }
}

const loadReceivedClaims = async () => {
  try {
    const res = await request.get('/api/claim/received', {
      params: { pageNum: 1, pageSize: 50 }
    })
    receivedClaims.value = res.data.records || []
  } catch (error) {
    console.error(error)
  }
}

const approveClaim = async (id) => {
  try {
    await ElMessageBox.confirm('确认通过该认领申请？物品将标记为已完结。', '确认')
    await request.put('/api/claim/approve/' + id)
    ElMessage.success('已通过认领')
    loadReceivedClaims()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const rejectClaim = async (id) => {
  try {
    await ElMessageBox.confirm('确认拒绝该认领申请？', '确认')
    await request.put('/api/claim/reject/' + id)
    ElMessage.success('已拒绝认领')
    loadReceivedClaims()
  } catch (error) {
    if (error !== 'cancel') console.error(error)
  }
}

const updateProfile = async () => {
  try {
    await request.put('/api/user/update', profileForm)
    ElMessage.success('个人信息已更新')
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    Object.assign(user, profileForm)
    localStorage.setItem('user', JSON.stringify(user))
  } catch (error) {
    console.error(error)
  }
}

const changePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请填写完整的密码信息')
    return
  }
  try {
    await request.put('/api/user/password', passwordForm)
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  Object.assign(profileForm, user)
  loadMyItems()
})
</script>

<style scoped>
.profile-page {
  min-height: 600px;
}
.menu-card {
  position: sticky;
  top: 20px;
}
.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>

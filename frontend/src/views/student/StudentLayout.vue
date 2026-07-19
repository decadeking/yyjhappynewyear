<template>
  <div class="student-layout">
    <el-header class="header">
      <div class="header-content">
        <div class="logo" @click="$router.push('/home')">
          <el-icon><School /></el-icon>
          <span>校园失物招领</span>
        </div>
        <div class="nav-menu">
          <el-menu mode="horizontal" :default-active="$route.path" router background-color="transparent" text-color="#606266" active-text-color="#67C23A">
            <el-menu-item index="/home">首页</el-menu-item>
            <el-menu-item index="/items">浏览物品</el-menu-item>
            <el-menu-item index="/publish">
              <el-button type="primary" size="small" style="background-color: #67C23A; border-color: #67C23A;">
                <el-icon><Plus /></el-icon>发布信息
              </el-button>
            </el-menu-item>
            <el-menu-item index="/profile">个人中心</el-menu-item>
          </el-menu>
        </div>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-icon><User /></el-icon>
              <span>{{ user.nickname || user.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-main class="main-content">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const user = ref({})

onMounted(() => {
  const storedUser = localStorage.getItem('user')
  if (storedUser) {
    user.value = JSON.parse(storedUser)
  }
})

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  background-color: #F5F7FA;
}
.header {
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 0;
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 20px;
  font-weight: bold;
  color: #67C23A;
  cursor: pointer;
}
.nav-menu {
  flex: 1;
  margin-left: 40px;
}
.nav-menu .el-menu {
  border-bottom: none;
}
.user-info {
  display: flex;
  align-items: center;
}
.user-dropdown {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #606266;
}
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
}
</style>

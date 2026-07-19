<template>
  <div class="settings-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>管理员设置</span>
        </div>
      </template>
      <el-form :model="profileForm" label-width="150px" style="max-width: 600px;">
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
      <el-form :model="passwordForm" label-width="150px" style="max-width: 600px;">
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
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

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
})
</script>

<style scoped>
.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
</style>

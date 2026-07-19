<template>
  <div class="publish-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>发布失物/招领信息</span>
        </div>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" style="max-width: 600px; margin: 0 auto;">
      <el-form-item label="信息类型" prop="type">
        <el-radio-group v-model="form.type">
          <el-radio :value="0">寻物启事</el-radio>
          <el-radio :value="1">招领启事</el-radio>
        </el-radio-group>
      </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入物品标题" maxlength="100" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="物品分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option label="电子产品" value="电子产品"></el-option>
            <el-option label="书籍文具" value="书籍文具"></el-option>
            <el-option label="衣物配饰" value="衣物配饰"></el-option>
            <el-option label="证件卡片" value="证件卡片"></el-option>
            <el-option label="生活用品" value="生活用品"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="form.location" placeholder="物品丢失/拾获地点"></el-input>
        </el-form-item>
        <el-form-item label="时间" prop="lostFoundTime">
          <el-date-picker
            v-model="form.lostFoundTime"
            type="datetime"
            placeholder="请选择日期时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="5"
            placeholder="请详细描述物品特征..."
            maxlength="500"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="上传图片" prop="images">
          <el-upload
            v-model:file-list="fileList"
            action="/api/file/upload"
            :headers="uploadHeaders"
            list-type="picture-card"
            :on-success="handleUploadSuccess"
            :on-remove="handleUploadRemove"
            :before-upload="beforeUpload"
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="upload-tip">最多上传5张图片，每张不超过10MB</div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" style="background-color: #67C23A; border-color: #67C23A;">提交发布</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const formRef = ref(null)
const fileList = ref([])
const uploadedImages = ref([])

const uploadHeaders = computed(() => {
  return { token: localStorage.getItem('token') || '' }
})

const form = reactive({
  type: 0,
  title: '',
  category: '',
  location: '',
  lostFoundTime: '',
  description: '',
  images: ''
})

const rules = {
  type: [{ required: true, message: '请选择信息类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
  lostFoundTime: [{ required: true, message: '请选择时间', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件')
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB')
  }
  return isImage && isLt10M
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    uploadedImages.value.push(response.data)
  } else {
    ElMessage.error('上传失败')
  }
}

const handleUploadRemove = (file) => {
  const url = file.response?.data
  if (url) {
    const index = uploadedImages.value.indexOf(url)
    if (index > -1) {
      uploadedImages.value.splice(index, 1)
    }
  }
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        form.images = JSON.stringify(uploadedImages.value)
        await request.post('/api/item/publish', form)
        ElMessage.success('发布成功，等待审核')
        router.push('/items')
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const resetForm = () => {
  formRef.value.resetFields()
  fileList.value = []
  uploadedImages.value = []
}
</script>

<style scoped>
.publish-page {
  max-width: 800px;
  margin: 0 auto;
}
.card-header {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}
.upload-tip {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
}
</style>


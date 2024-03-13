<script setup>

import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive, ref} from "vue";
import {ElMessage} from "element-plus";
import {post} from "@/net/index.js";


const form = reactive({
    username : '',
    password : '',
    password_repeat : '',
    email:'',
    code:''
})

const validateUsername = (rule , value ,  callback, ) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('用户名不能包含特殊字符，只能是中文/英文'));
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value!==form.password) {
    callback(new Error("两次输入的密码不一致"))
  } else {
    callback()
  }
}

const rules = {
  username: [
      { validator: validateUsername, trigger: ['blur', 'change'] },
      { min: 2, max: 8, message: '用户名长度必须在2-8个字符', trigger: ['blur', 'change'] }
  ],
  password: [
      { required: true, message: '请输入密码'},
      { min: 6, max: 16, message: '密码长度必须在6-16个字符', trigger: ['blur', 'change'] }
  ],
  password_repeat: [

      { validator: validatePassword, trigger: ['blur', 'change'] }
  ],
  email: [
      { required: true, message: '请输入电子邮件地址'},
      {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change'],},
  ],
  code:[
      { required: true, message: '请输入验证码', trigger: ['blur', 'change'] },
      { min: 6, max: 6, message: '验证码长度必须为6位', trigger: ['blur', 'change'] }
  ]
}


const formRef = ref(null)


const isEmailValid = ref(false)


const coldTime = ref(0);

const onValidate = (prop,isValid) => {
  if (prop === 'email') {
    isEmailValid.value = isValid
  }
}

const register = () => {
  formRef.value.validate((isValid) =>{
    if (isValid) {
        post('/api/auth/register',{
          username: form.username,
          password: form.password,
          email: form.email,
          code: form.code
        },(message) => {
          ElMessage.success(message)
          router.push('/')
        })
    } else {
      ElMessage.warning('请填写完整信息')
    }
  })
}

const validateEmail = () => {
  coldTime.value = 60;
  post('/api/auth/valid-register-email',{
    email: form.email
  },(message) =>{
    ElMessage.success(message)
    setInterval(() => coldTime.value--,1000);
  },(message)=>{
    ElMessage.warning(message)
    coldTime.value = 0
  })
}

</script>

<template>



  <div style="text-align: center;margin: 0 -30px ">
    <div style="margin-top: 100px">
      <div style="font-size: 25px; font-weight: bold">注册新用户</div>
      <div style="font-size: 14px;color: grey">欢迎注册我们的学习平台，请在下方填写相关信息</div>
    </div>


    <div style="margin: 50px;">
        <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">

          <el-form-item prop="username">
            <el-input v-model="form.username" :maxlength="8" type="text" placeholder="用户名" >
              <template #prefix>
                <el-icon>
                  <User/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>


          <el-form-item prop="password">
            <el-input  v-model="form.password" :maxlength="16"  type="password" placeholder="密码" >
              <template #prefix>
                <el-icon>
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password_repeat">
            <el-input v-model="form.password_repeat" :maxlength="16" type="password" placeholder="重复密码" >
              <template #prefix>
                <el-icon>
                  <Lock/>
                </el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="email">
            <el-input v-model="form.email" type="email" placeholder="电子邮件地址" >
              <template #prefix>
                <el-icon><Message/></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="code">
            <el-row  :gutter="10" style="width: 100%">
              <el-col :span="17">
                <el-input v-model="form.code" :maxlength="6" type="text" placeholder="请输入验证码" >
                  <template #prefix>
                    <el-icon><EditPen/></el-icon>
                  </template>
                </el-input>
              </el-col>
              <el-col :span="5">
                <el-button @click="validateEmail" type="primary" style="width: 160%"
                           :disabled="!isEmailValid || coldTime >0">{{ coldTime > 0?'请稍后'+coldTime+ '秒':'获取验证码'}}</el-button>

              </el-col>
            </el-row>

          </el-form-item>
        </el-form>


      <div style="margin-top: 40px">
        <el-button style="width: 100%" type="primary" @click="register">立即注册</el-button>
      </div>

      <div style="margin-top: 20px">
        <span style="font-size: 14px;;line-height: 15px; color: grey">已有账号？</span>
        <el-link type="primary" style="translate:0 -1px " @click="router.push('/')">立即登录</el-link>
      </div>
    </div>




  </div>
</template>

<style scoped>

</style>
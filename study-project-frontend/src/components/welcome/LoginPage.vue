<script setup>

import {Lock, User} from "@element-plus/icons-vue";
import {reactive} from "vue"
import {ElMessage} from "element-plus";
import {get, post} from "@/net";
import router from "@/router";
import {useStore} from "@/stores/index.js";

const store = useStore();

const from = reactive({
  username: '',
  password: '',
  remember: false,
})

const login = () => {
  if (!from.username || !from.password){
    ElMessage.warning('请输入用户名和密码')
  }else {
    post('/api/auth/login',{
      username: from.username,
      password: from.password,
      remember: from.remember
    },(message)=>{
      ElMessage.success(message);
      get('/api/user/me',(message)=>{
        store.auth.user = message
        router.push('/index')
      },()=>{
        store.auth.user = null
      });
    })
  }
}
</script>

<template>
  <div style="text-align: center;margin: 0 20px">

    <div style="margin-top: 150px">
      <div style="font-size: 25px; font-weight: bold">登录</div>
      <div style="font-size: 14px;color: grey">在进入系统之前请先输入用户名和密码进行登录</div>
    </div>

    <div style="margin-top: 50px">
      <el-input v-model="from.username" type="text" placeholder="用户名/邮箱">
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>

      <el-input v-model="from.password" type="password" style="margin-top: 10px" placeholder="密码">
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
    </div>

    <el-row style="margin-top: 5px">
      <el-col :span="12" style="text-align: left">
        <el-checkbox v-model="from.remember"  label="记住我"/>
      </el-col>
      <el-col :span="12" style="text-align: right">
        <el-link @click="router.push('/forget')">忘记密码</el-link>
      </el-col>
    </el-row>

    <div style="margin-top: 40px">
      <el-button @click="login()" type="primary" style="width: 100%;margin-top: 10px">立即登录</el-button>
    </div>

    <el-divider>
      <span style="color: grey">没有账号</span>
    </el-divider>

    <div>
      <el-button @click="router.push('/register')" type="text" style="width: 100%;margin-top: 10px">立即注册</el-button>
    </div>

  </div>
</template>

<style scoped>

</style>
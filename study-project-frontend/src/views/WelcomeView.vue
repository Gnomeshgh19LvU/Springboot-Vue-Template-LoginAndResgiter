<script setup>


</script>

<template>

  <div style="width: 100vw;height: 100vh;overflow: hidden;display: flex">

    <!--    页面左边 -> 图片页面-->
    <div style="flex: 1">
      <el-image style="width: 100%;height: 100%" fit="cover"
                src="https://ts1.cn.mm.bing.net/th/id/R-C.f23301faaa645ddb2c90381bb368b778?rik=HGiferJo1Msdeg&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fwallpaper%2f1208%2f09%2fc1%2f12788328_1344484585308.jpg&ehk=HM8tHpdH2UPCwT65cB5GTQ0m3QJKjN%2bRVQz3NfP%2bU2c%3d&risl=&pid=ImgRaw&r=0"/>
    </div >

    <div class="welcome-title">
      <div style="font-size: 30px;font-weight: bold">欢迎来到我们的学习平台</div>
      <div style="margin-top: 10px">在这里你可以学习如何使用java，如何搭建网站。并且与java之父密切交流</div>
    </div>

    <!--    页面右边 ->登录页面-->
    <div style="width: 400px;background-color: white;z-index: 1">



      <router-view v-slot="{ Component }">
        <transition name="el-fade-in-linear"  style="height: 100%"  mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>


    </div>
  </div>

</template>

<style scoped>
 .welcome-title {
  position: absolute;
  bottom: 30px;
  left: 30px;
  color: white;
  text-shadow: 0 0 10px black;
}
</style>
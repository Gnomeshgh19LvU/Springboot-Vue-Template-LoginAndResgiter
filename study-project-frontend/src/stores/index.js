import { defineStore } from 'pinia'
import {reactive} from 'vue'

export const useStore = defineStore('counter', () => {
 const auth = reactive({
   user: null
 })

  return { auth }
})

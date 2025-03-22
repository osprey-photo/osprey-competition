import { reactive, ref, type Reactive, type Ref } from 'vue'
import { defineStore } from 'pinia'
import type { CompetitionImage, DisplayMessage } from '@/types'

export const useDisplayStore = defineStore('display', () => {
  const displayType = ref('')
  const socket = new WebSocket('ws://localhost:9000/display')
  const images: Ref<Array<CompetitionImage>> = ref([])

  const showTitle = ref(false)

  socket.onopen = (event) => {
    console.log('On open ' + JSON.stringify(event))
  }

  socket.onerror = (event) => {
    console.log('On error ' + JSON.stringify(event))
  }

  socket.onclose = (event) => {
    console.log('On close ' + JSON.stringify(event))
  }

  socket.onmessage = (event) => {
    const message: DisplayMessage = JSON.parse(event.data)
    console.log(message)

    displayType.value = message.displayType
    images.value = message.images

    if (message.displayType == 'full_image') {
      showTitle.value = true
    }

    setTimeout(() => (showTitle.value = false), 2000)
  }

  return { images, showTitle, displayType }
})

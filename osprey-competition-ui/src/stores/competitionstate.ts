import { ref, type Ref, reactive, type Reactive } from 'vue'
import { defineStore } from 'pinia'
import {
  FULL_IMAGE,
  HELD_BACK,
  LIGHT_BOX_IMAGES,
  REJECTED,
  UNSEEN,
  FIRST,
  type CompetitionImage,
  type CompetitionSettings,
  HC,
  THIRD,
  SECOND,
} from '@/types'
import { BACKEND_URI } from '@/config'
import axios, { type AxiosResponse } from 'axios'

export const useCompetitionStore = defineStore('competition', () => {
  let displayedImageIndex = -1
  const data: Ref<Array<CompetitionImage>> = ref([])
  const displayImageId = ref('')

  const competitionSettings: Reactive<CompetitionSettings> = reactive({
    orderedValueScores: [] as Array<string>,
    randomised: true,
    numberScoresAvailable: {} as Map<string, number>,
    imageSrc: '',
  })

  /** Load new images from the server */
  async function updateList() {
    const resp = await axios.get(`${BACKEND_URI}/images`)

    for (const img of resp.data) {
      const compImage: CompetitionImage = img
      compImage.state = { kept: '', place: '', score: -1 }
      data.value.push(compImage)
    }

    console.log(data.value)
  }

  /** Move to the next image to display */
  async function next() {
    displayedImageIndex++
    if (displayedImageIndex === data.value.length) {
      displayedImageIndex = 0
    }
    displayImageId.value = data.value[displayedImageIndex].id

    // tell server to move on to the next image
    await axios.post(`${BACKEND_URI}/action`, {
      action: 'full_image',
      payload: data.value[displayedImageIndex].id,
    })
  }

  /** move to the previous iamge */
  async function previous() {
    displayedImageIndex--
    if (displayedImageIndex < 0) {
      displayedImageIndex = data.value.length - 1
    }

    displayImageId.value = data.value[displayedImageIndex].id

    await axios.post(`${BACKEND_URI}/action`, {
      action: 'full_image',
      payload: data.value[displayedImageIndex].id,
    })
  }

  async function setSelected(row: string) {
    displayedImageIndex = findImage(row)
    displayImageId.value = row
    // tell server to move on to the next image
    await axios.post(`${BACKEND_URI}/action`, {
      action: 'full_image',
      payload: data.value[displayedImageIndex].id,
    })
  }

  /** Give an images id, return the index in the array */
  function findImage(fid: string) {
    for (let index = 0; index < data.value.length; index++) {
      if (data.value[index].id === fid) {
        return index
      }
    }
    return -1
  }

  /** Request results be displayed */
  async function initCatalog(): Promise<string> {
    return await axios.get(`${BACKEND_URI}/images/catalog`)
  }

  /** Request results be displayed */
  async function getImageSrc(): Promise<string> {
    const resp: AxiosResponse = await axios.get(`${BACKEND_URI}/action/imagesrc`)
    if (resp.status == 200) {
      return resp.data
    }
    return ''
  }

  /** Request results be displayed */
  async function setResults() {
    await axios.post(`${BACKEND_URI}/action`, {
      action: 'light_box_results',
      payload: '',
    })
  }

  /** Request the image be displayed full screen */
  async function setDisplayFullImage() {
    await axios.post(`${BACKEND_URI}/action`, {
      action: FULL_IMAGE,
      payload: data.value[displayedImageIndex].id,
    })
  }

  /** Send the list of images to show in a light box to the server */
  async function setLightBoxFiltered(filters: { [filter: string]: boolean }) {
    const filteredImgIds = data.value
      .filter((compImg) => {
        if (filters[HELD_BACK] === true && compImg.state.kept === HELD_BACK) {
          return true
        }
        if (
          filters[UNSEEN] === true &&
          (compImg.state.kept === UNSEEN || compImg.state.kept === '')
        ) {
          return true
        }

        if (filters[REJECTED] === true && compImg.state.kept === REJECTED) {
          return true
        }

        return false
      })
      .map((compImg) => compImg.id)
      .join(',')

    console.log('Images filtered to ' + filteredImgIds)
    await axios.post(`${BACKEND_URI}/action`, {
      action: LIGHT_BOX_IMAGES,
      payload: filteredImgIds,
    })
  }

  function atFirst(): boolean {
    return displayedImageIndex == 0
  }

  function atLast(): boolean {
    return displayedImageIndex == data.value.length - 1
  }

  function resetIndex() {
    displayedImageIndex = -1
  }

  async function placeImg(id: string, place: string) {
    data.value[findImage(id)].state.place = place
    await axios.post(`${BACKEND_URI}/images/state/${id}`, data.value[findImage(id)].state)
  }

  async function scoreCurrent(kept: string) {
    data.value[displayedImageIndex].state.kept = kept
    await axios.post(
      `${BACKEND_URI}/images/state/${data.value[displayedImageIndex].id}`,
      data.value[displayedImageIndex].state,
    )
  }

  return {
    data,
    competitionSettings,
    next,
    previous,
    updateList,
    displayImageId,
    setSelected,
    atFirst,
    atLast,
    resetIndex,
    scoreCurrent,
    setDisplayFullImage,
    setLightBoxFiltered,
    placeImg,
    setResults,
    initCatalog,
    getImageSrc,
  }
})

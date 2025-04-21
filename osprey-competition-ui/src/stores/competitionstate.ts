import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'
import {
  FIRST,
  FULL_IMAGE,
  HC,
  HELD_BACK,
  LIGHT_BOX_IMAGES,
  REJECTED,
  SECOND,
  THIRD,
  UNSEEN,
  type CompetitionImage,
  type CompetitionSettings,
} from '@/types'
import { BACKEND_URI } from '@/config'
import axios, { type AxiosResponse } from 'axios'

export const useCompetitionStore = defineStore('competition', () => {
  let displayedImageIndex = -1
  const data: Ref<Array<CompetitionImage>> = ref([])
  const displayImageId = ref('')

  const competitionSettings: Ref<CompetitionSettings> = ref({
    orderedValueScores: [] as Array<string>,
    randomised: true,
    numberScoresAvailable: {} as Map<string, number>,
    imageSrc: '',
  })

  const availableScores = ref([FIRST, SECOND, THIRD, HC, HELD_BACK, REJECTED])
  const numberScoresAvailable: Map<string, number> = new Map([
    [FIRST, 1],
    [SECOND, 1],
    [THIRD, 1],
    [HC, 3],
    [HELD_BACK, -1],
    [REJECTED, -1],
  ])

  async function persistSettings() {
    // competitionSettings.value = settings
    return await axios.post(`${BACKEND_URI}/action/persistconfig`, competitionSettings.value)
  }

  async function getSettings() {
    const data = await axios.get(`${BACKEND_URI}/action/persistconfig`)
    console.log(data)
    competitionSettings.value = data.data
  }

  async function initCatalog() {
    return await axios.get(`${BACKEND_URI}/images/load`)
  }

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

        return filters[compImg.state.kept] === true
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

  async function scoreCurrent(kept: string) {
    await scoreImg(displayedImageIndex, kept)
    // data.value[displayedImageIndex].state.kept = kept
    // await axios.post(
    //   `${BACKEND_URI}/images/state/${data.value[displayedImageIndex].id}`,
    //   data.value[displayedImageIndex].state,
    // )
  }
  async function placeImg(id: string, score: string) {
    const imageId = findImage(id)
    await scoreImg(imageId, score)

    // data.value[imageId].state.kept = 'placed'
    // await axios.post(`${BACKEND_URI}/images/state/${id}`, data.value[imageId].state)
  }
  async function scoreImg(imageIndex: number, score: string) {
    if (availableScores.value.includes(score)) {
      const keptType =
        score === FIRST || score === SECOND || score === THIRD || score === HC ? 'placed' : score
      data.value[imageIndex].state.kept = keptType
      data.value[imageIndex].state.place = keptType === 'placed' ? score : ''

      await axios.post(
        `${BACKEND_URI}/images/state/${data.value[imageIndex].id}`,
        data.value[imageIndex].state,
      )

      const available: number = numberScoresAvailable?.get(score) || 0
      if (available > 0) {
        numberScoresAvailable?.set(score, available - 1)
        availableScores.value = availableScores.value.filter(
          (s) => numberScoresAvailable.get(s) != 0,
        )
      }
    }
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
    persistSettings,
    getImageSrc,
    initCatalog,
    getSettings,
    availableScores,
  }
})

import { ref, type Ref } from 'vue'
import { defineStore } from 'pinia'
import {
  FIRST,
  FULL_IMAGE,
  FULL_IMAGE_RESULTS,
  HC,
  HELD_BACK,
  LIGHT_BOX_IMAGES,
  LIGHT_BOX_IMAGES_RESULTS,
  REJECTED,
  SECOND,
  THIRD,
  UNSEEN,
  type Competition,
  type CompetitionImage,
  type CompetitionSettings,
} from '@/types'
import { BACKEND_URI } from '@/config'
import axios, { type AxiosResponse } from 'axios'
import { orderPlace } from '@/helpers'

export const useCompetitionStore = defineStore('competition', () => {
  let displayedImageIndex = -1
  const data: Ref<Array<CompetitionImage>> = ref([])
  const displayImageId = ref('')

  const competitionSettings: Ref<CompetitionSettings> = ref({
    competitions: {},
    clubName: '',
  })

  const selectedCompetition: Ref<Competition> = ref({
    orderedValueScores: [],
    numberScoresAvailable: new Map<string, number>(),
    randomised: false,
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

  async function getSettings(): Promise<CompetitionSettings> {
    const data = await axios.get(`${BACKEND_URI}/action/persistconfig`)
    console.log(data)
    competitionSettings.value = data.data
    return competitionSettings.value;
  }

  async function initCatalog() {
    return await axios.get(`${BACKEND_URI}/images/load`)
  }

  /** Load new images from the server */
  async function updateList() {
    const resp = await axios.get(`${BACKEND_URI}/images`)
    data.value = []
    for (const img of resp.data) {
      const compImage: CompetitionImage = img
      compImage.state = { kept: '', place: '', score: -1 }
      compImage.tempHidden = false
      data.value.push(compImage)
    }

    console.log(data.value)
  }

  /** Move to the next image to display */
  async function next(showDetails: boolean) {
    displayedImageIndex++
    if (displayedImageIndex === data.value.length) {
      displayedImageIndex = 0
    }
    displayImageId.value = data.value[displayedImageIndex].id

    // tell server to move on to the next image
    await axios.post(`${BACKEND_URI}/action`, {
      action: showDetails ? FULL_IMAGE_RESULTS : FULL_IMAGE,
      payload: data.value[displayedImageIndex].id,
    })
  }

  /** move to the previous iamge */
  async function previous(showDetails: boolean) {
    displayedImageIndex--
    if (displayedImageIndex < 0) {
      displayedImageIndex = data.value.length - 1
    }

    displayImageId.value = data.value[displayedImageIndex].id

    await axios.post(`${BACKEND_URI}/action`, {
      action: showDetails ? FULL_IMAGE_RESULTS : FULL_IMAGE,
      payload: data.value[displayedImageIndex].id,
    })
  }

  async function setSelected(row: string, showDetails: boolean) {
    displayedImageIndex = findImage(row)
    displayImageId.value = row
    // tell server to move on to the next image
    await axios.post(`${BACKEND_URI}/action`, {
      action: showDetails ? FULL_IMAGE_RESULTS : FULL_IMAGE,
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
      selectedCompetition.value.imageSrc = resp.data
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
  async function setDisplayFullImage(showDetails: boolean) {
    await axios.post(`${BACKEND_URI}/action`, {
      action: showDetails ? FULL_IMAGE_RESULTS : FULL_IMAGE,
      payload: data.value[displayedImageIndex].id,
    })
  }

  /** Request the blank image page, just club title */
  async function setBlankDisplay() {
    await axios.post(`${BACKEND_URI}/action`, {
      action: 'blank',
      payload: data.value[displayedImageIndex].id,
    })
  }

  /** Send the list of images to show in a light box to the server */
  async function setLightBoxFiltered(filters: { [filter: string]: boolean }, showDetails: boolean) {
    const filteredImgIds = data.value
      .filter((compImg) => {
        if (compImg.tempHidden) {
          return false
        }

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
      action: showDetails ? LIGHT_BOX_IMAGES_RESULTS : LIGHT_BOX_IMAGES,
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

  function sort(reversedPlace: boolean) {
    console.log(data.value)
    data.value.sort((a: CompetitionImage, b: CompetitionImage) => {
      if (a.state.kept === '' || a.state.kept === 'rejected') {
        return 1
      }
      if (b.state.kept === '' || b.state.kept === 'rejected') {
        return -1
      }

      if (a.state.kept === 'placed') {
        if (b.state.kept === 'placed') {
          if (reversedPlace) {
            return orderPlace(a.state.place, b.state.place) * -1
          } else {
            return orderPlace(a.state.place, b.state.place)
          }
        }
      }
      return 0
    })
    console.log(data.value)
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
    sort,
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
    setBlankDisplay,
    availableScores,
  }
})

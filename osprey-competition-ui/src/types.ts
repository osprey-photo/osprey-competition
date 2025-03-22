export type CompetitionImage = {
  title: string
  photographer: string
  id: string
  thumbnailB64: string
  halfishB64: string
  fullImageB64: string
  state: State
}

export type State = {
  kept: string
  place: string
  score: number
}

export type DisplayImage = {
  title: string
  imgB64: string
}

export const HELD_BACK = 'held_back'
export const REJECTED = 'rejected'
export const FIRST = 'first'
export const SECOND = 'second'
export const THIRD = 'third'
export const HC = 'hc'
export const UNSEEN = 'unseen'
export const FULL_IMAGE = 'full_image'
export const LIGHT_BOX_IMAGES = 'light_box_images'

export type CompetitionSettings = {
  orderedValueScores: Array<string>
  numberScoresAvailable: Map<string, number>
}

export type DisplayMessage = {
  displayType: string
  images: Array<CompetitionImage>
}

export type Action = {
  action: string
  payload: string
}

export type Filter = {
  unseen: boolean
  held_back: boolean
  rejected: boolean
  placed: boolean
  scored: boolean
}

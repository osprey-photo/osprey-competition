export type CompetitionImage = {
  title: string
  photographer: string
  id: string
  thumbnailB64: string
  halfishB64: string
  fullImageB64: string
  state: State
  tempHidden: boolean
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
export const FULL_IMAGE_RESULTS = 'full_image_results'
export const LIGHT_BOX_IMAGES_RESULTS = 'light_box_images_results'


export type Competition = {
  scoringSystem : ScoringSystem
  competitionNames: Array<string>
  imageSrc: string
}
export type ScoringSystem = {
  id: string,
  description: string,
  orderedValueScores: Array<string>
  numberScoresAvailable: Map<string, number>
  randomised: boolean
  heldBackList: boolean
}

export type CompetitionSettings = {
  competitions: {[name:string]:Competition}
  clubName: string
  scoringSystems: Array<ScoringSystem>
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

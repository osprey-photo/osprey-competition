import { FIRST, SECOND, THIRD, HC, HELD_BACK, REJECTED } from './types'

export function placeStyle(place: string) {
  // console.log(place)
  switch (place) {
    case FIRST:
      return 'is-link is-light'
    case SECOND:
      return 'is-link is-light'
    case THIRD:
      return 'is-link is-light'
    case HC:
      return 'is-success is-light'
    case HELD_BACK:
      return 'is-primary is-light'
    case REJECTED:
      return 'is-danger is-light'
    default:
      return ''
  }
}

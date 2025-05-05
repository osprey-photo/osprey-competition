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

export function orderPlace(a: string, b: string): number {
  if (a === FIRST) {
    return -1
  } else if (b === FIRST) {
    return 1
  } else if (a === HC) {
    return 1
  } else if (b === HC) {
    return -1
  } else if (a === THIRD) {
    if (b === FIRST || b === SECOND) {
      return 1
    } else {
      return -1
    }
  } else if (b === THIRD) {
    if (a === FIRST || a === SECOND) {
      return -1
    } else {
      return 11
    }
  }

  return 0
}

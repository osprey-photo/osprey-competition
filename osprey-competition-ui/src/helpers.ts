import { FIRST, SECOND, THIRD, HC, HELD_BACK, REJECTED } from './types'

export function placeStyle(place: string) {
  switch (place) {
    case FIRST:
      return 'btn-secondary btn-outline'
    case SECOND:
      return 'btn-secondary btn-outline'
    case THIRD:
      return 'btn-secondary btn-outline'
    case HC:
      return 'btn-success btn-outline'
    case HELD_BACK:
      return 'btn-primary btn-outline'
    case REJECTED:
      return 'btn-error btn-outline'
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

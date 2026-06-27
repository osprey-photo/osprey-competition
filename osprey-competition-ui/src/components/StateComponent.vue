<script setup lang="ts">
import type { State } from '@/types'
import { placeStyle } from '@/helpers'
defineProps<{
  state: State
}>()
</script>

<template>
  <div>
    <span class="btn btn-sm m-1" v-show="state.kept != '' && state.kept !== 'placed'" :class="placeStyle(state.kept)">
      {{ state.kept === 'rejected' ? 'Pass' : state.kept === 'held_back' ? 'Hold Back' : state.kept }}
    </span>
    <span class="btn btn-sm m-1" v-show="state.place != ''" :class="placeStyle(state.place)">
      {{ state.place }}
    </span>
  </div>
</template>

<style scoped></style>

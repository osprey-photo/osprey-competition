<script setup lang="ts">
import type { State } from '@/types';
import { placeStyle } from '@/helpers';
defineProps<{
  state: State
}>()
</script>

<template>
  <div>
    <span class="button is-small m-1" v-show="state.kept != ''" :class="placeStyle(state.kept)">
      {{ state.kept }}
    </span>
    <span class="button is-small m-1" v-show="state.place != ''" :class="placeStyle(state.place)">
      {{ state.place }}
    </span>
  </div>
</template>

<style scoped></style>

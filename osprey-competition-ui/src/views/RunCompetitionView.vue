<script setup lang="ts">
import LoadImagesComponent from '@/components/LoadImagesComponent.vue'
import NavBarComponent from '@/components/NavBarComponent.vue'
import StateComponent from '@/components/StateComponent.vue'
import StatusComponent from '@/components/StatusComponent.vue'
import { placeStyle } from '@/helpers'
import { useCompetitionStore } from '@/stores/competitionstate'
import { HELD_BACK, REJECTED, type CompetitionImage, type Filter } from '@/types'
import SvgIcon from '@jamescoyle/vue-icon'
import { mdiEyeOffOutline, mdiEyeOutline, mdiSortAscending, mdiSortDescending } from '@mdi/js'
import { storeToRefs } from 'pinia'
import { computed, onMounted, onUnmounted, onUpdated, reactive, ref } from 'vue'
const runthroughTime = ref(3000)
const comp = useCompetitionStore()

// ── Critique timer ────────────────────────────────────────────────
const critiqueStartTime = ref<Date | null>(null)
const critiqueElapsed = ref(0)
const critiquePaused = ref(false)
const critiquePausedAt = ref(0)   // elapsed secs at moment of pause
const currentTime = ref(new Date())
let clockInterval: ReturnType<typeof setInterval> | null = null

function formatDuration(secs: number): string {
  const h = Math.floor(secs / 3600)
  const m = Math.floor((secs % 3600) / 60)
  const s = secs % 60
  return [h, m, s].map(n => String(n).padStart(2, '0')).join(':')
}

function formatTime(d: Date): string {
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false })
}

function pauseResumeTimer() {
  if (!critiqueStartTime.value) return
  if (critiquePaused.value) {
    // resume: shift startTime forward by the paused duration
    const pausedFor = critiqueElapsed.value - critiquePausedAt.value
    critiqueStartTime.value = new Date(critiqueStartTime.value.getTime() + pausedFor * 1000)
    critiquePaused.value = false
  } else {
    critiquePausedAt.value = critiqueElapsed.value
    critiquePaused.value = true
  }
}

function resetTimer() {
  critiqueStartTime.value = null
  critiqueElapsed.value = 0
  critiquePaused.value = false
  critiquePausedAt.value = 0
}

onMounted(() => {
  if (Object.keys(comp.competitionSettings.competitions).length === 0) {
    comp.getSettings()
  }
  clockInterval = setInterval(() => {
    currentTime.value = new Date()
    if (critiqueStartTime.value && !critiquePaused.value) {
      critiqueElapsed.value = Math.floor((currentTime.value.getTime() - critiqueStartTime.value.getTime()) / 1000)
    }
  }, 1000)
})

onUnmounted(() => {
  if (clockInterval) clearInterval(clockInterval)
})

async function selectCompetition(compId: string) {
  if (!compId) return
  comp.selectedCompetition = comp.competitionSettings.competitions[compId]
  await comp.initCatalog(compId)
  await comp.updateList()
}
const statusIndicator = reactive({
  runthrough: false,
  critique: false,
  loadImages: false,
  loadImagesDialog: false,
})

const displayState = ref('full')
const showDetails = ref(false)

async function display(d: string) {
  displayState.value = d
  switch (d) {
    case 'full':
      comp.setDisplayFullImage(showDetails.value)
      break
    case 'lightbox':
      comp.setLightBoxFiltered(filterState, showDetails.value)
      break
    case 'results':
      comp.setResults()
      break
    case 'calibration':
      comp.setCalibrationDisplay()
      break
    default:
      comp.setBlankDisplay()
  }
}

/** Refresh the displayed state, if it's on that already */
async function refreshDisplay(d: string) {
  if (displayState.value !== d) {
    return
  }
  switch (d) {
    case 'full':
      comp.setDisplayFullImage(showDetails.value)
      break
    case 'lightbox':
      comp.setLightBoxFiltered(filterState, showDetails.value)
      break
    case 'results':
      comp.setResults()
      break
    default:
      comp.setBlankDisplay()
  }
}

async function rowSelected(row: string) {
  console.log('Row selected ' + row)
  comp.setSelected(row, showDetails.value)
}

const filterState: Filter = reactive({
  unseen: true,
  held_back: true,
  rejected: false,
  placed: false,
  scored: false,
})

function imageKeptFiltered(img: CompetitionImage) {
  if (img.state.kept == '' && !filterState['unseen']) {
    return false
  }

  if (img.state.kept == HELD_BACK && !filterState['held_back']) {
    return false
  }

  if (img.state.kept == REJECTED && !filterState['rejected']) {
    return false
  }

  if (img.state.kept == 'placed' && !filterState['placed']) {
    return false
  }

  return true
}

async function action(cmd: string) {
  switch (cmd) {
    case 'next':
      comp.next(showDetails.value)
      break
    case 'previous':
      comp.previous(showDetails.value)
      break
    case HELD_BACK:
    case REJECTED:
      comp.scoreCurrent(cmd)
      comp.next(showDetails.value)

      // const { displayImageId } = storeToRefs(comp)
      // const el = document.getElementById(displayImageId.value);
      // el?.scrollIntoView({ behavior: "smooth" });
      onUpdated(() => {
        // text content should be the same as current `count.value`
        console.log('hello......')
      })

      break

    default:
      break
  }
}

// async function abortLoadImages() {
//   statusIndicator.loadImagesDialog = false;
//   statusIndicator.loadImages = false;

// }

// async function doneLoadImages() {
//   statusIndicator.loadImagesDialog = false;
//   await comp.initCatalog();
//   await comp.updateList();
//   statusIndicator.loadImages = false;

// }

async function runthrough() {
  comp.resetIndex()
  statusIndicator.runthrough = true
  await comp.next(false)
  const intervalId = setInterval(async () => {
    await comp.next(false)
    if (comp.atLast()) {
      clearInterval(intervalId)

      // move back tostart
      setTimeout(async () => {
        comp.next(showDetails.value)
        statusIndicator.runthrough = false
      }, runthroughTime.value)
    }
  }, runthroughTime.value)
}

async function startCritque() {
  critiqueStartTime.value = new Date()
  critiqueElapsed.value = 0
  comp.resetIndex()
  comp.next(false)
}

async function tempHide(img: CompetitionImage) {
  img.tempHidden = !img.tempHidden
  refreshDisplay('lightbox')
}

async function awardScore(img: CompetitionImage, score: string) {
  comp.placeImg(img.id, score)
}

function scoreLabel(score: string): string {
  if (score === REJECTED) return 'Pass'
  if (score === HELD_BACK) return 'Hold Back'
  return score
}

const numberHeldBack = computed(() => {
  const x = comp.data.filter((i: CompetitionImage) => {
    return i.state.kept === HELD_BACK
  })
  return x.length
})

const numberRejected = computed(() => {
  const x = comp.data.filter((i: CompetitionImage) => {
    return i.state.kept === REJECTED
  })
  return x.length
})

const numberUnscored = computed(() => {
  const x = comp.data.filter((i: CompetitionImage) => {
    return i.state.kept === ''
  })
  return x.length
})

function imageForScore(place: string): Array<CompetitionImage> {
  const filtered = comp.data.filter((i: CompetitionImage) => {
    return i.state.place === place
  })

  // if (filtered.length == 0) {
  //   return [{ title: '', photographer: '' }]
  // }
  return filtered
}

const { displayImageId } = storeToRefs(comp)
onUpdated(() => {
  const el = document.getElementById(displayImageId.value)
  el?.scrollIntoView({ behavior: 'smooth' })
  // text content should be the same as current `count.value`
})

//
// watch(displayImageId, (d) => {
//
//
// });
</script>

<template>
  <NavBarComponent>
    <select
      class="select select-sm bg-neutral text-neutral-content border-neutral-focus"
      @change="selectCompetition(($event.target as HTMLSelectElement).value)"
    >
      <option value="">Select competition...</option>
      <option
        v-for="(item, key) in comp.competitionSettings.competitions"
        :key="key"
        :value="key"
        :selected="comp.selectedCompetition.competitionNames[0] === item.competitionNames[0]"
      >
        {{ item.competitionNames[0] }}
      </option>
    </select>
  </NavBarComponent>
  <section class="p-6 bg-base-200">
    <div class="flex gap-4">
      <!-- Left column: filters + image list -->
      <div class="flex-1">
        <div class="bg-base-100 rounded-lg shadow p-4 mb-4">
          <div class="flex gap-4 flex-wrap">
            <label class="label cursor-pointer gap-2">
              <input type="checkbox" class="checkbox checkbox-sm" v-model="filterState['unseen']" />
              <span class="label-text text-sm">Unseen</span>
            </label>
            <label class="label cursor-pointer gap-2">
              <input type="checkbox" class="checkbox checkbox-sm" v-model="filterState['held_back']" />
              <span class="label-text text-sm">Held Back</span>
            </label>
            <label class="label cursor-pointer gap-2">
              <input type="checkbox" class="checkbox checkbox-sm" v-model="filterState['rejected']" />
              <span class="label-text text-sm">Passed</span>
            </label>
            <label class="label cursor-pointer gap-2">
              <input type="checkbox" class="checkbox checkbox-sm" v-model="filterState['placed']" />
              <span class="label-text text-sm">Placed</span>
            </label>
          </div>
        </div>

        <div class="container mx-auto">
          <div class="overflow-x-auto table-container">
            <table class="table table-bordered">
              <tr
                :id="item.id"
                @click="rowSelected(item.id)"
                ref="items"
                :class="{ 'bg-primary/20 outline outline-1 outline-primary': comp.displayImageId == item.id }"
                v-for="item in comp.data"
                :key="item.id"
                v-show="imageKeptFiltered(item) == true"
              >
                <td>{{ item.title }}</td>
                <td>{{ item.photographer }}</td>
                <td>
                  <svg-icon
                    @click.stop="tempHide(item)"
                    type="mdi"
                    size="20"
                    :path="item.tempHidden ? mdiEyeOffOutline : mdiEyeOutline"
                  ></svg-icon>
                </td>
                <td>
                  <StateComponent :state="item.state" />
                </td>
                <td>
                  <img
                    v-if="item.thumbnailB64"
                    :class="{ greyscale: item.tempHidden }"
                    :src="`data:image/jpeg;base64,${item.thumbnailB64}`"
                    alt=" Red dot"
                  />
                </td>
                <td>
                  <span
                    v-show="item.state.place == ''"
                    v-for="score in comp.availableScores"
                    class="btn btn-sm m-2"
                    :class="placeStyle(score)"
                    :key="score"
                    @click.stop="awardScore(item, score)"
                  >
                    {{ scoreLabel(score) }}</span
                  >
                </td>
              </tr>
            </table>
          </div>
        </div>
      </div>

      <!-- Right column: controls + status -->
      <div class="w-1/3 flex flex-col gap-4">

        <!-- ── Display group ─────────────────────────────────── -->
        <div class="bg-base-100 rounded-lg shadow p-4 flex flex-col gap-3">
          <p class="text-xs font-semibold uppercase tracking-wide text-base-content/50">Display</p>

          <!-- Display mode toggle -->
          <div class="join w-full">
            <button
              class="btn join-item flex-1"
              :class="{ 'btn-active': displayState === 'full' }"
              @click="display('full')"
            >Full Image</button>
            <button
              class="btn join-item flex-1"
              :class="{ 'btn-active': displayState === 'lightbox' }"
              @click="display('lightbox')"
            >Lightbox</button>
            <button
              class="btn join-item flex-1"
              :class="{ 'btn-active': displayState === 'blank' }"
              @click="display('blank')"
            >Blank</button>
            <button
              class="btn join-item flex-1"
              :class="{ 'btn-active': displayState === 'calibration' }"
              @click="display('calibration')"
            >Calibration</button>
          </div>

          <!-- Photographer name + sort – affect what the display shows -->
          <div class="flex items-center justify-between flex-wrap gap-2">
            <label class="label cursor-pointer gap-2">
              <input type="checkbox" class="checkbox checkbox-sm" v-model="showDetails" />
              <span class="label-text text-sm">Show photographer name</span>
            </label>
            <div class="flex items-center gap-1">
              <span class="label-text text-xs text-base-content/60">Sort</span>
              <button class="btn btn-ghost btn-sm btn-square" title="Sort ascending" @click="comp.sort(false)">
                <svg-icon type="mdi" size="18" :path="mdiSortAscending"></svg-icon>
              </button>
              <button class="btn btn-ghost btn-sm btn-square" title="Sort descending" @click="comp.sort(true)">
                <svg-icon type="mdi" size="18" :path="mdiSortDescending"></svg-icon>
              </button>
            </div>
          </div>
        </div>

        <!-- ── Navigation ────────────────────────────────────── -->
        <div class="bg-base-100 rounded-lg shadow p-4 flex flex-col gap-3">
          <p class="text-xs font-semibold uppercase tracking-wide text-base-content/50">Navigation</p>

          <!-- Start / auto-advance -->
          <div class="flex gap-2 flex-wrap">
            <button
              class="btn btn-info flex-1"
              :disabled="statusIndicator.runthrough"
              @click="runthrough"
            >
              <span v-if="statusIndicator.runthrough" class="loading loading-spinner loading-sm"></span>
              <span v-else>▶</span>
              Run through
            </button>
            <button
              class="btn btn-info flex-1"
              :disabled="statusIndicator.runthrough"
              @click="startCritque"
            >
              ▶ Critique
            </button>
          </div>

          <!-- Critique timer -->
          <div v-if="critiqueStartTime" class="bg-base-200 rounded p-3 text-center font-mono text-sm">
            <div class="text-base-content/60 text-xs uppercase tracking-wide mb-1">Critique Timer</div>
            <div class="text-2xl font-bold tabular-nums" :class="{ 'opacity-40': critiquePaused }">
              {{ formatDuration(critiqueElapsed) }}
            </div>
            <div class="text-base-content/50 text-xs mt-1">Started {{ formatTime(critiqueStartTime) }} · Now {{ formatTime(currentTime) }}</div>
            <div class="flex gap-2 mt-2">
              <button class="btn btn-xs flex-1" :class="critiquePaused ? 'btn-success' : 'btn-warning'" @click="pauseResumeTimer">
                {{ critiquePaused ? '▶ Resume' : '⏸ Pause' }}
              </button>
              <button class="btn btn-xs btn-ghost flex-1" @click="resetTimer">↺ Reset</button>
            </div>
          </div>

          <!-- Step navigation – large targets -->
          <div class="flex gap-2">
            <button
              class="btn btn-secondary flex-1"
              :disabled="statusIndicator.runthrough"
              @click="action('previous')"
            >
              ← Previous
            </button>
            <button
              class="btn btn-secondary flex-1"
              :disabled="statusIndicator.runthrough"
              @click="action('next')"
            >
              Next →
            </button>
          </div>
        </div>

        <!-- ── Judging ────────────────────────────────────────── -->
        <div class="bg-base-100 rounded-lg shadow p-4 flex flex-col gap-3">
          <p class="text-xs font-semibold uppercase tracking-wide text-base-content/50">Judging</p>

          <div class="flex gap-2">
            <button
              class="btn btn-warning flex-1"
              :disabled="statusIndicator.runthrough"
              @click="action(HELD_BACK)"
            >
              Hold Back
            </button>
            <button
              class="btn btn-error flex-1"
              :disabled="statusIndicator.runthrough"
              @click="action(REJECTED)"
            >
              Pass
            </button>
          </div>

          <!-- Counts -->
          <div class="flex gap-4 text-sm">
            <div class="flex flex-col items-center flex-1 bg-base-200 rounded p-2">
              <span class="font-bold text-lg">{{ numberHeldBack }}</span>
              <span class="text-base-content/60 text-xs">Held back</span>
            </div>
            <div class="flex flex-col items-center flex-1 bg-base-200 rounded p-2">
              <span class="font-bold text-lg">{{ numberRejected }}</span>
              <span class="text-base-content/60 text-xs">Passed</span>
            </div>
            <div class="flex flex-col items-center flex-1 bg-base-200 rounded p-2">
              <span class="font-bold text-lg">{{ numberUnscored }}</span>
              <span class="text-base-content/60 text-xs">Unseen</span>
            </div>
          </div>
        </div>

        <!-- Scores table -->
        <div class="bg-base-100 rounded-lg shadow p-4">
          <table class="table table-sm w-full">
            <tbody v-for="s in comp.selectedCompetition.scoringSystem.orderedValueScores" :key="s">
              <tr v-for="i in imageForScore(s)" :key="i.id">
                <td class="p-1">
                  <span class="text-base">{{ s }}</span>
                </td>
                <td class="p-1">
                  <span class="font-semibold">{{ i.title }}</span>
                </td>
                <td class="p-1">
                  <span class="italic">{{ i.photographer }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="bg-base-100 rounded-lg shadow p-4">
          <StatusComponent />
        </div>

        <!-- ── Save – prominent action at bottom ─────────────── -->
        <div class="bg-base-100 rounded-lg shadow p-4">
          <button class="btn btn-success w-full" @click="comp.setResults()">
            💾 Save Results CSV
          </button>
        </div>

      </div>
    </div>
  </section>
</template>

<style lang="css">
.table-container {
  max-height: 80vh;
  overflow-y: auto;
}

.pulse {
  /* Button default styles, customize them to match your button */

  animation: pulse 1s infinite;
}

.greyscale {
  opacity: 0.4;
  filter: alpha(opacity=40);
}

@keyframes pulse {
  0% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(0, 0, 0, 0.7);
  }

  70% {
    transform: scale(1);
    box-shadow: 0 0 0 10px rgba(0, 0, 0, 0);
  }

  100% {
    transform: scale(0.95);
    box-shadow: 0 0 0 0 rgba(0, 0, 0, 0);
  }
}
</style>

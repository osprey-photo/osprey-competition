<script setup lang="ts">
import { useCompetitionStore } from '@/stores/competitionstate'
import type { Competition } from '@/types'

import { reactive, ref, type Ref } from 'vue'

const props = defineProps<{
  active: boolean
}>()

const emit = defineEmits(['done', 'abort'])
const comp = useCompetitionStore()

const scoring = ref('')

async function defaultPicked() {
  console.log(scoring.value)
}

const newCompetition: Ref<Competition> = ref({
  competitionNames: [],
  scoringSystem: {
    id: 'score1',
    description: 'descl',
    heldBackList: true,
    orderedValueScores: [],
    randomised: true,
    numberScoresAvailable: new Map(),
  },
  imageSrc: '',
})

// eslint-disable-next-line @typescript-eslint/no-explicit-any
async function selectDefault(event: any) {
  const v = event.target.value
  console.log('Select Default ' + v)
  const defaultComp = comp.competitionSettings.scoringSystems.filter((i) => i.id === v)
  console.log(defaultComp[0])

  newCompetition.value.scoringSystem = defaultComp[0]
}

const manualPathError = ref('')

async function load() {
  status.loadImages = true
  manualPathError.value = ''
  try {
    const d = await comp.getImageSrc()
    newCompetition.value.imageSrc = d
  } catch {
    status.dialogFailed = true
    manualPathError.value = 'Native dialog unavailable — enter path manually.'
  }
  status.loadImages = false
}

async function closeUpdate() {
  const compId = newCompetition.value.competitionNames[0]
  comp.competitionSettings.competitions[compId] = newCompetition.value
  await comp.persistSettings()
  emit('done')
}

const marks = ref('placed')
const hcplaces = ref('4')
const runthroughTime = ref(3000)
const heldbacks = ref(-1)

const status = reactive({
  loadImages: false,
  dialogFailed: false,
})

const randomised = ref(true)

const numberScoresAvailable = ref({
  Second: -1,
  '9': -1,
  '8': -1,
  '7': -1,
  '6': -1,
  '5': -1,
  '4': -1,
  '3': -1,
  '2': -1,
  '1': -1,
})
</script>

<template>
  <div>
    <div id="modal-js-example" class="modal" :class="{ 'modal-open': active }">
      <div class="modal-backdrop" @click="$emit('abort')"></div>

      <div class="modal-box max-w-3xl">
        <!-- Header -->
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-bold">Create Critique</h3>
          <button @click="$emit('abort')" class="btn btn-sm btn-circle btn-ghost" aria-label="close">✕</button>
        </div>

        <!-- Body -->
        <div>
          <!-- Competition Name -->
          <div class="flex items-center gap-4 mb-3">
            <label class="label w-40 shrink-0">
              <span class="label-text">Competition Name</span>
            </label>
            <div class="flex-1">
              <input
                class="input input-bordered input-success w-full"
                type="text"
                v-model="newCompetition.competitionNames[0]"
              />
            </div>
          </div>

          <!-- Subtitle -->
          <div class="flex items-center gap-4 mb-3">
            <label class="label w-40 shrink-0">
              <span class="label-text">Subtitle eg round</span>
            </label>
            <div class="flex-1">
              <input
                class="input input-bordered input-success w-full"
                type="text"
                v-model="newCompetition.competitionNames[1]"
              />
            </div>
          </div>

          <!-- Image Directory -->
          <div class="flex items-center gap-4 mb-3">
            <label class="label w-40 shrink-0">
              <span class="label-text">Image Directory</span>
            </label>
            <div class="flex-1">
              <input
                class="input input-bordered input-success w-full"
                type="text"
                placeholder="Full Directory Path"
                v-model="newCompetition.imageSrc"
              />
              <p v-if="manualPathError" class="text-error text-sm mt-1">{{ manualPathError }}</p>
            </div>
            <div v-if="!status.dialogFailed">
              <button
                class="btn"
                :class="{ loading: status.loadImages }"
                @click="load"
              >
                Select directory...
              </button>
            </div>
          </div>

          <!-- Default Scoring -->
          <div class="flex items-center gap-4 mb-3">
            <label class="label w-40 shrink-0">
              <span class="label-text">Default Scoring</span>
            </label>
            <div class="flex-1">
              <select class="select select-bordered w-full" v-model="scoring" @change="selectDefault($event)">
                <option value="">please select...</option>
                <option
                  v-for="system in comp.competitionSettings.scoringSystems"
                  :value="system.id"
                  :key="system.id"
                >
                  {{ system.description }}
                </option>
              </select>
            </div>
          </div>

          <!-- Marks or places -->
          <div class="flex items-center gap-4 mb-3">
            <label class="label w-40 shrink-0">
              <span class="label-text">Marks or places</span>
            </label>
            <div class="flex-1">
              <select class="select select-bordered w-full" v-model="marks">
                <option value="placed">1st, 2nd, 3rd, HC</option>
                <option value="ten">1-10</option>
                <option value="five">2,3,4,5</option>
              </select>
            </div>
          </div>

          <!-- Score Places grid -->
          <div class="flex items-start gap-4 mb-3">
            <label class="label w-40 shrink-0 mt-1">
              <span class="label-text">Score Places</span>
            </label>
            <div class="flex-1">
              <div class="grid grid-cols-5 gap-2">
                <div v-for="(v, k) in numberScoresAvailable" :key="k">
                  <label class="label">
                    <span class="label-text text-xs">{{ k }}</span>
                  </label>
                  <select class="select select-bordered select-sm w-full" v-model="numberScoresAvailable[k]">
                    <option value="-1">No limit</option>
                    <option>1</option>
                    <option>2</option>
                    <option>3</option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          <!-- Heldback Limit -->
          <div class="flex items-center gap-4 mb-3">
            <label class="label w-40 shrink-0">
              <span class="label-text">Heldback Limit</span>
            </label>
            <div>
              <select class="select select-bordered" v-model="heldbacks">
                <option value="-1">No limit</option>
                <option>5</option>
                <option>10</option>
                <option>15</option>
              </select>
            </div>
          </div>

          <!-- Runthrough Image time -->
          <div class="flex items-center gap-4 mb-3">
            <label class="label w-40 shrink-0">
              <span class="label-text">Runthrough Image time</span>
            </label>
            <div>
              <select class="select select-bordered" v-model="runthroughTime">
                <option value="3000">3s</option>
                <option value="4000">4s</option>
                <option value="5000">5s</option>
                <option value="6000">6s</option>
                <option value="8000">8s</option>
                <option value="10000">10s</option>
              </select>
            </div>
          </div>

          <!-- Randomise -->
          <div class="flex items-center gap-4 mb-3">
            <span class="label-text w-40 shrink-0">Randomise</span>
            <div class="flex gap-6">
              <label class="flex items-center gap-2 cursor-pointer">
                <input type="radio" class="radio radio-sm" name="member" value="true" v-model="randomised" />
                <span class="label-text">Yes</span>
              </label>
              <label class="flex items-center gap-2 cursor-pointer">
                <input type="radio" class="radio radio-sm" name="member" value="false" v-model="randomised" />
                <span class="label-text">No</span>
              </label>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="modal-action">
          <button class="btn btn-success" @click="closeUpdate">Close and update list</button>
          <button class="btn" @click="$emit('abort')">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>

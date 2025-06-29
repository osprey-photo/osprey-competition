<script setup lang="ts">
import { useCompetitionStore } from '@/stores/competitionstate';
import { onMounted, reactive, ref } from 'vue';

import { FIRST, HC, SECOND, THIRD, type CompetitionSettings } from '@/types';
import StatusComponent from './StatusComponent.vue';
const props = defineProps<{
  active: boolean
}>()

const emit = defineEmits(['done', 'abort'])
const comp = useCompetitionStore();


const filename = ref("")

onMounted(() => {
  comp.getSettings();
  console.log(comp.competitionSettings)
})

async function load() {
  status.loadImages = true
  const d = await comp.getImageSrc();
  status.loadImages = false
}

async function closeUpdate() {
  await comp.persistSettings();
  emit("done")
}

const status = reactive({
  loadImages: false
})
const randomised = ref(true)
const selected = ref("placed")
const hcs = ref(3)
</script>

<template>
  <div>
    <div id="modal-js-example" class="modal" :class="{ 'is-active': active }">
      <div class="modal-background"></div>

      <div class="modal-card">
        <header class="modal-card-head">
          <p class="modal-card-title">Create Critique</p>
          <button @click="$emit('abort')" class="delete" aria-label="close"></button>
        </header>

        <div class="modal-card-body">
          <div class="columns">
            <div class="column">
              <div class="field is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">Compeittion Name</label>
                </div>
                <div class="field-body">
                  <div class="field ">
                    <input class="input" type="text" />
                  </div>
                </div>
              </div>
              <div class="field is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">Subtitle, round, date etc.</label>
                </div>
                <div class="field-body">
                  <div class="field ">
                    <input class="input" type="text" />
                  </div>
                </div>
              </div>
              <div class="field is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">Image Directory</label>
                </div>
                <div class="field-body">
                  <div class="field">
                    <input class="input is-success" type="text" placeholder="Full Directory Path"
                      v-model="comp.competitionSettings.imageSrc">
                    <button class="button " @click="load" :class="{ 'is-loading': status.loadImages }">Select
                      directory...</button>
                  </div>
                </div>
              </div>

              <div class="field is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">Scoring, marks or places</label>
                </div>
                <div class="field-body">
                  <div class="field is-narrow">
                    <div class="control">
                      <div class="select is-fullwidth">
                        <select v-model="scoring">
                          <option value="placed">1st, 2nd, 3rd, HC</option>
                          <option value="ten">1-10</option>
                          <option value="tenhalf">1-10 with halfs</option>
                          <option value="five">2,3,4,5</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="field is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">Highly Commended Places</label>
                </div>
                <div class="field-body">
                  <div class="field is-narrow">
                    <div class="control">
                      <div class="select is-fullwidth">
                        <select v-model="hcs">
                          <option>0</option>
                          <option>1</option>
                          <option>2</option>
                          <option>3</option>
                          <option>4</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="field is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">Heldback Limit</label>
                </div>
                <div class="field-body">
                  <div class="field is-narrow">
                    <div class="control">
                      <input class="input" type="text" />
                    </div>
                  </div>
                </div>
              </div>
              <div class="field  is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">Runthrough Image time</label>
                </div>
                <div class="field-body">
                  <div class="field is-narrow">
                    <div class="control">
                      <div class="select">
                        <select v-model="runthroughTime">
                          <option value="3000">3s</option>

                          <option value="4000">4s</option>
                          <option value="5000">5s</option>
                          <option value="6000">6s</option>
                          <option value="8000">8s</option>
                          <option value="10000">10s</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>

              </div>
              <div class="field is-horizontal">
                <div class="field-label">
                  <label class="label">Randomise Images</label>
                </div>
                <div class="field-body">
                  <div class="field ">
                    <div class="control">
                      <label class="radio">
                        <input type="radio" name="member" value='true' v-model="randomised">
                        Yes
                      </label>
                      <label class="radio">
                        <input type="radio" name="member" value='false' v-model="randomised">
                        No
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>



          <footer class="modal-card-foot">
            <div class="buttons">
              <button class="button is-success" @click="closeUpdate">Close and update list</button>
              <button class="button" @click="$emit('abort')">Cancel</button>
            </div>
          </footer>

        </div>

      </div>

      <!-- <button @click="$emit('done')" class="modal-close is-large" aria-label="close"></button> -->
    </div>
  </div>
</template>

<style scoped></style>

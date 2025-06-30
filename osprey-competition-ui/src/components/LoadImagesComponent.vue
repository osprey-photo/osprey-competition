<script setup lang="ts">
import { useCompetitionStore } from '@/stores/competitionstate';
import type { Competition } from '@/types';
import { mdiMapMarkerStar } from '@mdi/js';
import { reactive, ref, type Ref } from 'vue';

const props = defineProps<{
  active: boolean
}>()

const emit = defineEmits(['done', 'abort'])
const comp = useCompetitionStore();

const scoring = ref("")

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
    numberScoresAvailable: new Map()
  },
  imageSrc: ''
});

async function selectDefault(event){
  const v = event.target.value;
  console.log("Select Default " +v);
  const defaultComp = comp.competitionSettings.scoringSystems.filter(i=>i.id === v);
  console.log(defaultComp[0])

  newCompetition.value.scoringSystem = defaultComp[0];


}

async function load() {
  status.loadImages = true
  const d = await comp.getImageSrc();
  newCompetition.value.imageSrc = d;
  status.loadImages = false
}

async function closeUpdate() {
  const compId = newCompetition.value.competitionNames[0];
  comp.competitionSettings.competitions[compId]=newCompetition.value;
  await comp.persistSettings();
  emit("done")
}

const marks = ref("placed")
const hcplaces = ref("4")
const runthroughTime = ref(3000)
const heldbacks = ref(-1)

const status = reactive({
  loadImages: false
})

const randomised = ref(true)


const numberScoresAvailable = ref({
  "Second": -1,
  "9": -1,
  "8": -1,
  "7": -1,
  "6": -1,
  "5": -1,
  "4": -1,
  "3": -1,
  "2": -1,
  "1": -1
})
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
                <div class="field-label">
                  <label class="label">Competition Name</label>
                </div>
                <div class="field-body">
                  <div class="control">
                    <input class="input is-fullwidth is-success" type="text" v-model="newCompetition.competitionNames[0]" />
                  </div>
                </div>
              </div>
              <div class="field is-horizontal">
                <div class="field-label">
                  <label class="label">Subtitle eg round</label>
                </div>
                <div class="field-body">
                  <div class="control ">
                    <input class="input  is-success" type="text" v-model="newCompetition.competitionNames[1]" />
                  </div>
                </div>
              </div>
              <div class="field is-horizontal has-addons">
                <div class="field-label">
                  <label class="label">Image Directory</label>
                </div>
                <div class="field-body">

                  <div class="control"> <input class="input is-success" type="text" placeholder="Full Directory Path"
                      v-model="newCompetition.imageSrc"></div>

                  <div class="control"> <button class="button " @click="load"
                      :class="{ 'is-loading': status.loadImages }">Select
                      directory...</button></div>

                </div>

              </div>

              <div class="field is-horizontal">
                <div class="field-label ">
                  <label class="label">Default Scoring</label>
                </div>
                <div class="field-body">

                  <div class="control is-expanded">
                    <div class="select is-fullwidth">
                      <select v-model="scoring" @change="selectDefault($event)">
                        <option value="">please select...</option>
                        <option v-for="(system) in comp.competitionSettings.scoringSystems" :value="system.id"
                          :key="system.id">
                          {{ system.description }} </option>
                      </select>
                    </div>
                  </div>

                </div>
              </div>


              <div class="field is-horizontal">
                <div class="field-label ">
                  <label class="label">Marks or places</label>
                </div>
                <div class="field-body">

                  <div class="control">
                    <div class="select is-fullwidth">
                      <select v-model="marks">
                        <option value="placed">1st, 2nd, 3rd, HC</option>
                        <option value="ten">1-10</option>
                        <option value="five">2,3,4,5</option>
                      </select>
                    </div>
                  </div>
                </div>

              </div>

              <div class="field is-horizontal">
                <div class="field-label ">
                  <label class="label">Score Places</label>
                </div>
                <div class="field-body">
                  <div class="fixed-grid has-5-cols">
                    <div class="grid">
                      <div v-for="(v, k) in numberScoresAvailable" :key="k" class="cell">
                        <div class="field">
                          <div class="field-label">
                            <label class="label">{{ k }}</label>
                          </div>
                          <div class="field-body">

                            <div class="control">
                              <select v-model="numberScoresAvailable[k]">
                                <option value="-1">No limit</option>
                                <option>1</option>
                                <option>2</option>
                                <option>3</option>
                              </select>
                            </div>

                          </div>
                        </div>

                      </div>

                    </div>
                  </div>
                </div>
              </div>
              <div class="field is-horizontal">
                <div class="field-label">
                  <label class="label">Heldback Limit</label>
                </div>
                <div class="field-body">

                  <div class="control">
                    <div class="select">
                      <select v-model="heldbacks">
                        <option value="-1">No limit</option>
                        <option>5</option>
                        <option>10</option>
                        <option>15</option>
                      </select>
                    </div>

                  </div>
                </div>
              </div>
              <div class="field  is-horizontal">
                <div class="field-label">
                  <label class="label">Runthrough Image time</label>
                </div>
                <div class="field-body">
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
              <div class="field is-horizontal">
                <div class="field-label">
                  <label class="label">Randomise</label>
                </div>
                <div class="field-body">

                  <div class="control">
                    <label class="radio">
                      <input type="radio" name="member" value='true' v-model="randomised" />
                      Yes
                    </label>
                    <label class="radio">
                      <input type="radio" name="member" value='false' v-model="randomised" />
                      No
                    </label>
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

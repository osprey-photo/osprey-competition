<script setup lang="ts">

import { storeToRefs } from 'pinia'
import { useCompetitionStore } from '@/stores/competitionstate';
import { computed, onMounted, onUpdated, reactive, ref, watch } from 'vue';
import NavBarComponent from '@/components/NavBarComponent.vue';
import SvgIcon from '@jamescoyle/vue-icon'
import { mdiSortAscending, mdiSortDescending, mdiEyeOutline, mdiEyeOffOutline } from '@mdi/js';
const comp = useCompetitionStore();

const statusIndicator = reactive({
  runthrough: false,
  critique: false,
  loadImages: false,
  loadImagesDialog: false
})

const activeSection = ref('competition')
const scoring = ref('placed')
const hcs = ref(4)
const randomised = ref(true)
const runthroughTime = ref(3000)

onMounted(() => {
  comp.getSettings()
})

async function save(){
  await comp.persistSettings();
}

</script>


<template>
  <NavBarComponent />
  <div class="section  has-background-light">
    <h1 class="title">Manage Competitions</h1>
    <div class="container">
      <div class="level">
        <div class="level-left">
          <div class="level-item">
            <p class="subtitle is-5">
              Club Name
            </p>
          </div>
          <div class="level-item">
            <div class="field">
              <div class="field-body">
                <div class="field">
                  <input class="input is-medium" type="text" v-model="comp.competitionSettings.clubName"/>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="level-right">


          <button class="button">New Critique...</button>
          <button class="button" @click="save">Save</button>
        </div>
      </div>
      <div class="table-container">
        <table class="table is-striped is-fullwidth is-size-7">
          <thead>
            <tr class="has-background-white">
              <th class="px-6 py-3">
                <span class="is-size-5">Compeitition Title</span>

              </th>
              <th class="px-6 py-3">
                <span class="is-size-5">Subtitle</span>

              </th>
              <th class="px-6 py-3">
                <span class="is-size-5">Image paths</span>

              </th>
              <th class="px-6 py-3">
                <span class="is-size-5">Summary</span>
              </th>

            </tr>
          </thead>
          <tbody>
            <tr v-for="(item,key) in comp.competitionSettings.competitions" :key="key" >
              <td>item.orderedValueScores</td>
              <td class="px-6 py-5 has-text-dark" style="border: none;">08.04.2021</td>
              <td class="px-6 py-5 has-text-dark" style="border: none;">Code 5928MD01</td>
              <td class="px-6 py-5 has-text-dark" style="border: none;">$2500.00</td>
              <td class="px-6 py-5 has-text-dark" style="border: none;">$2500.00</td>
            </tr>

          </tbody>
        </table>
      </div>
    </div>

    <div class="container" v-show="activeSection === 'general'">
      <hr />
      <div class="panel">
        <div class="field is-horizontal">
          <div class="field-label is-normal">
            <label class="label">Club nane</label>
          </div>
          <div class="field-body">
            <div class="field ">
              <input class="input" type="text" />
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>

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

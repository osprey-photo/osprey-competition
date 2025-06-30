<script setup lang="ts">

import NavBarComponent from '@/components/NavBarComponent.vue';
import { useCompetitionStore } from '@/stores/competitionstate';
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiDeleteOutline, mdiPresentationPlay } from '@mdi/js';
import { onMounted, reactive, ref } from 'vue';
const comp = useCompetitionStore();
const router = useRouter();
import LoadImagesComponent from '@/components/LoadImagesComponent.vue';
import { useRouter } from 'vue-router';

const statusIndicator = reactive({

  loadImagesDialog: false
})

const activeSection = ref('competition')

onMounted(() => {
  comp.getSettings()
})

async function save() {
  await comp.persistSettings();
}

async function abortLoadImages() {
  statusIndicator.loadImagesDialog = false;
}

async function doneLoadImages() {
  statusIndicator.loadImagesDialog = false;
}

async function deleteComp(compId:string){
  delete  comp.competitionSettings.competitions[compId];
  await comp.persistSettings();
}

async function startComp(compId:string){
  console.log("Starting comp "+compId);
  comp.selectedCompetition = comp.competitionSettings.competitions[compId];
  await comp.initCatalog(compId)
  await comp.updateList()
  router.push("runcomp")
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
                  <input class="input is-medium" type="text" v-model="comp.competitionSettings.clubName" />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="level-right">


          <button class="button" @click="statusIndicator.loadImagesDialog = true">New Critique...</button>
          <button class="button" @click="save">Save</button>
        </div>
      </div>
      <div class="table-container">
        <table class="table is-striped is-fullwidth">
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
              <th class="px-6 py-3">
                <span class="is-size-5">Actions</span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, key) in comp.competitionSettings.competitions" :key="key">
              <td>{{ item.competitionNames[0] }}</td>
              <td>{{ item.competitionNames[1] }}</td>
              <td>{{ item.imageSrc }}</td>
              <td>{{ item.scoringSystem.id }}</td>
              <td>
                <!-- <a class="" @click="editComp(key)"><svg-icon type="mdi" size="20"
                    :path="mdiNoteEditOutline"></svg-icon></a> -->
                <a class="" @click="deleteComp(key as string)"><svg-icon type="mdi" size="20"
                    :path="mdiDeleteOutline"></svg-icon></a>
                <a class="" @click="startComp(key as string)"><svg-icon type="mdi" size="20"
                    :path="mdiPresentationPlay"></svg-icon></a>
              </td>
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
    <LoadImagesComponent :active="statusIndicator.loadImagesDialog" @done="doneLoadImages" @abort="abortLoadImages" />
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

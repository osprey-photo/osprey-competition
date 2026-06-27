<script setup lang="ts">
import NavBarComponent from '@/components/NavBarComponent.vue'
import { useCompetitionStore } from '@/stores/competitionstate'
import SvgIcon from '@jamescoyle/vue-icon'
import { mdiDeleteOutline, mdiPresentationPlay } from '@mdi/js'
import { onMounted, reactive, ref } from 'vue'
const comp = useCompetitionStore()
const router = useRouter()
import LoadImagesComponent from '@/components/LoadImagesComponent.vue'
import { useRouter } from 'vue-router'

const statusIndicator = reactive({
  loadImagesDialog: false,
})

const activeSection = ref('competition')

onMounted(() => {
  comp.getSettings()
})

async function save() {
  await comp.persistSettings()
}

async function abortLoadImages() {
  statusIndicator.loadImagesDialog = false
}

async function doneLoadImages() {
  statusIndicator.loadImagesDialog = false
}

async function deleteComp(compId: string) {
  delete comp.competitionSettings.competitions[compId]
  await comp.persistSettings()
}

async function startComp(compId: string) {
  console.log('Starting comp ' + compId)
  comp.selectedCompetition = comp.competitionSettings.competitions[compId]
  await comp.initCatalog(compId)
  await comp.updateList()
  router.push('runcomp')
}
</script>

<template>
  <NavBarComponent />
  <section class="p-6 bg-base-200">
    <h1 class="text-2xl font-bold mb-4">Manage Competitions</h1>
    <div class="container mx-auto">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center gap-2">
          <span class="text-base font-semibold">Club Name</span>
          <input
            class="input input-bordered input-md"
            type="text"
            v-model="comp.competitionSettings.clubName"
          />
        </div>
        <div class="flex items-center gap-2">
          <button class="btn" @click="statusIndicator.loadImagesDialog = true">New Critique...</button>
          <button class="btn" @click="save">Save</button>
        </div>
      </div>

      <div class="overflow-x-auto table-container">
        <table class="table table-zebra w-full">
          <thead>
            <tr class="bg-white">
              <th class="px-6 py-3"><span class="text-xl">Competition Title</span></th>
              <th class="px-6 py-3"><span class="text-xl">Subtitle</span></th>
              <th class="px-6 py-3"><span class="text-xl">Image paths</span></th>
              <th class="px-6 py-3"><span class="text-xl">Summary</span></th>
              <th class="px-6 py-3"><span class="text-xl">Actions</span></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(item, key) in comp.competitionSettings.competitions" :key="key">
              <td>{{ item.competitionNames[0] }}</td>
              <td>{{ item.competitionNames[1] }}</td>
              <td>{{ item.imageSrc }}</td>
              <td>{{ item.scoringSystem.id }}</td>
              <td>
                <a class="" @click="deleteComp(key as string)"
                  ><svg-icon type="mdi" size="20" :path="mdiDeleteOutline"></svg-icon
                ></a>
                <a class="" @click="startComp(key as string)"
                  ><svg-icon type="mdi" size="20" :path="mdiPresentationPlay"></svg-icon
                ></a>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="container mx-auto" v-show="activeSection === 'general'">
      <hr class="my-4" />
      <div class="card card-bordered p-4">
        <div class="flex items-center gap-4">
          <label class="label w-32 shrink-0">
            <span class="label-text">Club name</span>
          </label>
          <input class="input input-bordered flex-1" type="text" />
        </div>
      </div>
    </div>

    <LoadImagesComponent
      :active="statusIndicator.loadImagesDialog"
      @done="doneLoadImages"
      @abort="abortLoadImages"
    />
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

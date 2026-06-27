<script setup lang="ts">
import SvgIcon from '@jamescoyle/vue-icon'
import { mdiSortAscending } from '@mdi/js'
import { ref } from 'vue'
const mdi = ref(mdiSortAscending)
</script>

<template>
  <main>
    <section class="p-12">
      <div class="flex justify-center">
        <img
          style="width: 30vw"
          src="@/assets/Invert-Solid-Osprey-logo-v3-trans.png"
        />
      </div>
      <p class="text-2xl font-bold text-center text-white mt-4">Osprey Competition</p>
    </section>

    <section class="p-6">
      <div class="flex justify-center gap-8">
        <div class="text-center">
          <RouterLink to="/managecomp">
            <span class="btn btn-lg">Manage Competitions</span>
          </RouterLink>
        </div>
        <div class="text-center">
          <RouterLink to="/runcomp">
            <span class="btn btn-lg">Run Competition</span>
          </RouterLink>
        </div>
        <div class="text-center">
          <RouterLink to="/display">
            <span class="btn btn-lg">Display</span>
          </RouterLink>
        </div>
      </div>
    </section>
  </main>
</template>

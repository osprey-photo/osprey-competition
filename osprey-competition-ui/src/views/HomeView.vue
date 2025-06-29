<script setup lang="ts">
import SvgIcon from '@jamescoyle/vue-icon'
import { mdiSortAscending } from '@mdi/js';
import { ref } from 'vue';
const mdi = ref(mdiSortAscending)
</script>



<template>
  <main>
    <section class="hero">
      <div class="hero-body">

        <div class="level">
          <div class="level-item">
            <img class="image" style="width:30vw;" src="@/assets/Invert-Solid-Osprey-logo-v3-trans.png">
          </div>
        </div>
        <p class="title has-text-centered has-text-white">Osprey Competition</p>
      </div>
    </section>

    <section class="hero ">
      <div class="hero-body">

        <div class="level">
          <div class="level-item has-text-centered">
            <RouterLink to="/managecomp"><span class="button is-large">Manage Competitions</span></RouterLink>
          </div>
          <div class="level-item has-text-centered">
            <RouterLink to="/runcomp"><span class="button is-large">Run Competition</span></RouterLink>
          </div>
          <div class="level-item has-text-centered">
            <RouterLink to="/display"><span class="button is-large">Display</span></RouterLink>
          </div>
        </div>
      </div>
    </section>



  </main>
</template>

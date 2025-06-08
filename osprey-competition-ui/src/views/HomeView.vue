<script setup lang="ts">
import SvgIcon from '@jamescoyle/vue-icon'
import { mdiSortAscending  } from '@mdi/js';
import { ref } from 'vue';
const mdi = ref(mdiSortAscending )
</script>



<template>
  <main>
    <section class="hero ">
      <div class="hero-body">
        <p class="title">
        <div class="level">
          <div class="level-item">
            <img class="image" style="width:50vw;"
              src="@/assets/Invert-Solid-Osprey-logo-v3-trans.png">
          </div>

<svg-icon type="mdi" :path="mdi"></svg-icon>asd

           
        </div>

<span class="icon-text has-text-info">
  <span class="icon">
    <i class="mdi mdi-sort-ascending"></i>
  </span>
  <span>Info</span>
</span>

        <p class="title has-text-centered">Competition</p>
        </p>
      </div>
    </section>

    <section class="hero ">
      <div class="hero-body">

        <div class="level">
          <div class="level-item has-text-centered">
            <RouterLink to="/summary"><span class="button is-large">Summary</span></RouterLink>
          </div>
          <div class="level-item has-text-centered">
            <RouterLink to="/display"><span class="button is-large">Display</span></RouterLink>
          </div>
        </div>
      </div>
    </section>



  </main>
</template>

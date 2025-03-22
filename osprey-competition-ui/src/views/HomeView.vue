<script setup lang="ts">

</script>

<template>
  <main>
    <section class="hero ">
      <div class="hero-body">
        <p class="title">
        <div class="level">
          <div class="level-item">
            <img class="image" style="width:50vw;"
              src="/home/matthew/github.com/osprey-photo/osprey-competition/osprey-competition-ui/src/assets/Invert-Solid-Osprey-logo-v3-trans.png">
          </div>
        </div>
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
            <RouterLink to="/singledisplay"><span class="button is-large">Display</span></RouterLink>
          </div>
        </div>
      </div>
    </section>



  </main>
</template>

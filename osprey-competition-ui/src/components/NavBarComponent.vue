<script setup lang="ts">
</script>

<template>
   <nav class="navbar  is-dark" role="navigation" aria-label="main navigation">
    <div class="navbar-brand">
      <a class="navbar-item" href="https://bulma.io">
        Osprey Compentition
      </a>
    </div>

    <div id="navbarBasicExample" class="navbar-menu">
      <div class="navbar-start">
        <a class="navbar-item">
          <RouterLink class="button" to="managecomp">Manage</RouterLink>
        </a>
        <a class="navbar-item">
          <RouterLink class="button" to="runcomp">Competition</RouterLink>
        </a>
        <a class="navbar-item">
          <RouterLink class="button" to="display">Open Display</RouterLink>
        </a>
      </div>

      <div class="navbar-end">
      </div>
    </div>
  </nav>
</template>

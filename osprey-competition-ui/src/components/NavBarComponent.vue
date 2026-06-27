<script setup lang="ts"></script>

<template>
  <div class="navbar bg-neutral text-neutral-content" role="navigation" aria-label="main navigation">
    <div class="navbar-start">
      <span class="text-lg font-semibold px-2">Osprey Competition</span>
    </div>

    <div class="navbar-center">
      <RouterLink class="btn btn-ghost btn-sm text-neutral-content hover:bg-neutral-focus" to="managecomp">Manage</RouterLink>
      <RouterLink class="btn btn-ghost btn-sm text-neutral-content hover:bg-neutral-focus" to="runcomp">Competition</RouterLink>
      <a class="btn btn-ghost btn-sm text-neutral-content hover:bg-neutral-focus" href="/#/display" target="_blank" rel="noopener">Open Display</a>
    </div>

    <div class="navbar-end">
      <slot />
    </div>
  </div>
</template>

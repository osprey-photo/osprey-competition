<script setup lang="ts">
import { ref, type Ref } from 'vue'
const socket = new WebSocket('ws://localhost:9000/status')

const msgs: Ref<Array<string>> = ref([])

socket.onopen = (event) => {
  console.log('On open ' + JSON.stringify(event))
}

socket.onerror = (event) => {
  console.log('On error ' + JSON.stringify(event))
}

socket.onclose = (event) => {
  console.log('On close ' + JSON.stringify(event))
}

socket.onmessage = (event) => {
  msgs.value.unshift(event.data);
}
</script>

<template>
  <div class="status-container">
    <h4>Status Messages</h4>

    <table class="table is-scrollable">
      <tr :id="evt" v-for="(evt, index) in msgs" :key="index">

        <td>{{ evt }}
        </td>
      </tr>

    </table>
  </div>

</template>
<style lang="css">
.status-container {
  max-height: 20vh;
  overflow-y: auto;
}
</style>

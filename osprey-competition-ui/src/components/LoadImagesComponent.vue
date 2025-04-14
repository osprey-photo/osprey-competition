<script setup lang="ts">
import { useCompetitionStore } from '@/stores/competitionstate';
import { reactive, ref } from 'vue';

// import type { State } from '@/types';
// import { placeStyle } from '@/helpers';
defineProps<{
  active: boolean
}>()

const emit = defineEmits(['done'])
const comp = useCompetitionStore();

const status = reactive({
  loadImages: false
})

const filename = ref("")


async function load() {

  filename.value = await comp.getImageSrc();

}

async function closeUpdate() {

  // comp.competitionSettings.numberScoresAvailable =

  await comp.updateList();
  emit("done")
}
const randomised = ref(true)
const selected = ref("placed")
const hcs = ref(3)
</script>

<template>
  <div>
    <div id="modal-js-example" class="modal" :class="{ 'is-active': active }">
      <div class="modal-background"></div>

      <div class="modal-card">
        <header class="modal-card-head">
          <p class="modal-card-title">Create Critique</p>
          <button @click="$emit('done')" class="delete" aria-label="close"></button>
        </header>

        <div class="modal-card-body">

          <div class="field is-horizontal">
            <div class="field-label is-normal">
              <label class="label">Image Directory</label>
            </div>
            <div class="field-body">
              <div class="field">
                <button class="button " @click="load" :class="{ 'is-loading': status.loadImages }">Select
                  directory...</button>
              </div>
              <div class="field">
                {{ filename }}
              </div>
            </div>
          </div>

          <div class="field is-horizontal">
            <div class="field-label is-normal">
              <label class="label">Scoring</label>
            </div>
            <div class="field-body">
              <div class="field is-narrow">
                <div class="control">
                  <div class="select is-fullwidth">
                    <select v-model="selected">
                      <option value="placed">1st, 2nd, 3rd, HC</option>
                    </select>
                  </div>
                </div>
              </div>
              <div class="field is-horizontal">
                <div class="field-label is-normal">
                  <label class="label">HC Places</label>
                </div>
                <div class="field-body">
                  <div class="field is-narrow">
                    <div class="control">
                      <div class="select is-fullwidth">
                        <select v-model="hcs">
                          <option>1</option>
                          <option>2</option>
                          <option>3</option>
                          <option>4</option>
                        </select>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>



          <div class="field is-horizontal">
            <div class="field-label">
              <label class="label">Randomise Images</label>
            </div>
            <div class="field-body">
              <div class="field is-narrow">
                <div class="control">
                  <label class="radio">
                    <input type="radio" name="member" value='true' v-model="randomised">
                    Yes
                  </label>
                  <label class="radio">
                    <input type="radio" name="member" value='false' v-model="randomised">
                    No
                  </label>
                </div>
              </div>
            </div>
          </div>
          <footer class="modal-card-foot">
            <div class="buttons">
              <button class="button is-success" @click="closeUpdate">Close and update list</button>
              <button class="button">Cancel</button>
            </div>
          </footer>

        </div>

      </div>

      <button @click="$emit('done')" class="modal-close is-large" aria-label="close"></button>
    </div>
  </div>
</template>

<style scoped></style>

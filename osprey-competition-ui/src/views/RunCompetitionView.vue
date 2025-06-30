<script setup lang="ts">

import LoadImagesComponent from '@/components/LoadImagesComponent.vue';
import NavBarComponent from '@/components/NavBarComponent.vue';
import StateComponent from '@/components/StateComponent.vue';
import StatusComponent from '@/components/StatusComponent.vue';
import { placeStyle } from '@/helpers';
import { useCompetitionStore } from '@/stores/competitionstate';
import { HELD_BACK, REJECTED, type CompetitionImage, type Filter } from '@/types';
import SvgIcon from '@jamescoyle/vue-icon';
import { mdiEyeOffOutline, mdiEyeOutline, mdiSortAscending, mdiSortDescending } from '@mdi/js';
import { storeToRefs } from 'pinia';
import { computed, onUpdated, reactive, ref } from 'vue';
const runthroughTime = ref(3000);
const comp = useCompetitionStore();
const statusIndicator = reactive({
  runthrough: false,
  critique: false,
  loadImages: false,
  loadImagesDialog: false
})

const displayState = ref("full");
const showDetails = ref(false);

async function display(d: string) {
  displayState.value = d;
  switch (d) {
    case "full":
      comp.setDisplayFullImage(showDetails.value);
      break;
    case "lightbox":
      comp.setLightBoxFiltered(filterState, showDetails.value);
      break;
    case "results":
      comp.setResults();
      break;
    default:
      comp.setBlankDisplay();
  }

}

/** Refresh the displayed state, if it's on that already */
async function refreshDisplay(d: string) {
  if (displayState.value !== d) {
    return
  }
  switch (d) {
    case "full":
      comp.setDisplayFullImage(showDetails.value);
      break;
    case "lightbox":
      comp.setLightBoxFiltered(filterState, showDetails.value);
      break;
    case "results":
      comp.setResults();
      break;
    default:
      comp.setBlankDisplay();
  }
}

async function rowSelected(row: string) {
  console.log("Row selected " + row);
  comp.setSelected(row, showDetails.value)
}



const filterState: Filter = reactive({ 'unseen': true, 'held_back': true, 'rejected': false, placed: false, scored: false })

function imageKeptFiltered(img: CompetitionImage) {

  if (img.state.kept == '' && !filterState['unseen']) {
    return false;
  }

  if (img.state.kept == HELD_BACK && !filterState['held_back']) {
    return false;
  }

  if (img.state.kept == REJECTED && !filterState['rejected']) {
    return false;
  }

  if (img.state.kept == 'placed' && !filterState['placed']) {
    return false;
  }

  return true;

}

async function action(cmd: string) {
  switch (cmd) {
    case "next":
      comp.next(showDetails.value)
      break;
    case "previous":
      comp.previous(showDetails.value)
      break;
    case HELD_BACK:
    case REJECTED:
      comp.scoreCurrent(cmd);
      comp.next(showDetails.value);

      // const { displayImageId } = storeToRefs(comp)
      // const el = document.getElementById(displayImageId.value);
      // el?.scrollIntoView({ behavior: "smooth" });
      onUpdated(() => {
        // text content should be the same as current `count.value`
        console.log("hello......")
      })

      break;

    default:
      break;
  }
}

// async function abortLoadImages() {
//   statusIndicator.loadImagesDialog = false;
//   statusIndicator.loadImages = false;

// }

// async function doneLoadImages() {
//   statusIndicator.loadImagesDialog = false;
//   await comp.initCatalog();
//   await comp.updateList();
//   statusIndicator.loadImages = false;

// }

async function runthrough() {

  comp.resetIndex();
  statusIndicator.runthrough = true
  await comp.next(false);
  const intervalId = setInterval(async () => {
    await comp.next(false);
    if (comp.atLast()) {
      clearInterval(intervalId);

      // move back tostart
      setTimeout(async () => {
        comp.next(showDetails.value);
        statusIndicator.runthrough = false
      }, runthroughTime.value)

    }
  }, runthroughTime.value);

}

async function startCritque() {
  comp.resetIndex()
  comp.next(false)
}

async function tempHide(img: CompetitionImage) {
  img.tempHidden = !img.tempHidden;
  refreshDisplay('lightbox')
}

async function awardScore(img: CompetitionImage, score: string) {
  comp.placeImg(img.id, score);
}

const numberHeldBack = computed(() => {
  const x = comp.data.filter((i: CompetitionImage) => {
    return i.state.kept === HELD_BACK
  });
  return x.length;
});

const numberRejected = computed(() => {
  const x = comp.data.filter((i: CompetitionImage) => {
    return i.state.kept === REJECTED
  });
  return x.length;
});

const numberUnscored = computed(() => {
  const x = comp.data.filter((i: CompetitionImage) => {
    return i.state.kept === ""
  });
  return x.length;
});

function imageForScore(place: string): Array<CompetitionImage> {
  const filtered = comp.data.filter((i: CompetitionImage) => {
    return i.state.place === place
  })

  // if (filtered.length == 0) {
  //   return [{ title: '', photographer: '' }]
  // }
  return filtered;
}

const { displayImageId } = storeToRefs(comp)
onUpdated(() => {
  const el = document.getElementById(displayImageId.value);
  el?.scrollIntoView({ behavior: "smooth" });
  // text content should be the same as current `count.value`

})


//
// watch(displayImageId, (d) => {
//
//
// });



</script>


<template>
  <NavBarComponent />
  <div class="section has-background-light">
    <div class="columns ">
      <div class="column">
        <div class="box">

          <div class="level">
            <div class="level-left">
              <div class="checkboxes">
                <label class="checkbox">
                  <input type="checkbox" v-model="filterState['unseen']" />
                  Unseen
                </label>

                <label class="checkbox">
                  <input type="checkbox" v-model="filterState['held_back']" />
                  Held Back
                </label>

                <label class="checkbox">
                  <input type="checkbox" v-model="filterState['rejected']" />
                  Rejected
                </label>

                <label class="checkbox">
                  <input type="checkbox" v-model="filterState['placed']" />
                  Placed
                </label>

              </div>
            </div>
            <div class="level-right">

              <label class="checkbox">
                <input type="checkbox" v-model="showDetails" />
                Photographer Name
              </label>
              <label> Sort Results</label>


              <span class="button level-item" @click="comp.sort(false)"><svg-icon type="mdi" size="20"
                  :path="mdiSortAscending"></svg-icon></span>
              <span class="button level-item" @click="comp.sort(true)"><svg-icon type="mdi" size="20"
                  :path="mdiSortDescending"></svg-icon></span>
            </div>
          </div>

        </div>
        <div class="container">
          <div class="table-container">
            <table class="table is-bordered is-scrollable">
              <tr :id="item.id" @click="rowSelected(item.id)" ref="items"
                :class="{ 'has-background-grey-lighter': comp.displayImageId == item.id }" v-for="(item) in comp.data"
                :key="item.id" v-show="imageKeptFiltered(item) == true">
                <td>{{ item.title }}</td>
                <td>{{ item.photographer }}</td>
                <td><svg-icon @click.stop="tempHide(item)" type="mdi" size="20"
                    :path="item.tempHidden ? mdiEyeOffOutline : mdiEyeOutline"></svg-icon></td>
                <td>
                  <StateComponent :state='item.state' />
                </td>
                <td>
                  <img :class="{ 'greyscale': item.tempHidden }" :src="`data:image/png;base64,${item.thumbnailB64}`"
                    alt=" Red dot" />
                </td>
                <td>
                  <span v-show="item.state.place == ''" v-for="(score) in comp.availableScores"
                    class="button is-small m-2" :class="placeStyle(score)" :key="score"
                    @click.stop="awardScore(item, score)">
                    {{ score }}</span>
                </td>
              </tr>

            </table>
          </div>
        </div>
      </div>
      <div class="column is-4">
        <div class="block">
          <div class="level">

            <span class="button level-item" :class="{ 'is-focused': displayState === 'full' }"
              @click="display('full')">Full
              Image</span>
            <button class="button level-item" :class="{ 'is-focused': displayState === 'lightbox' }"
              @click="display('lightbox')">Lightbox</button>
            <span class="button level-item" :class="{ 'is-focused': displayState === 'blank' }"
              @click="display('blank')">[blank]</span>


          </div>

        </div>
        <div class="block">
          <div class="level">
            <button class="level-item button is-info" :class="{ 'is-loading': statusIndicator.runthrough }"
              @click="runthrough">Start runthrough</button>
            <button class="level-item button is-info" v-bind:disabled="statusIndicator.runthrough"
              @click="startCritque">Start Critique</button>
            <button class="level-item button is-link" v-bind:disabled="statusIndicator.runthrough"
              @click="action('previous')">Previous</button>
            <button class="level-item button is-link" v-bind:disabled="statusIndicator.runthrough"
              @click="action('next')">Next</button>
          </div>
        </div>
        <div class="block">
          <div class="level">
            <button class="level-item button is-success" v-bind:disabled="statusIndicator.runthrough"
              @click="action(HELD_BACK)">Hold Back</button>
            <button class="level-item button is-danger" v-bind:disabled="statusIndicator.runthrough"
              @click="action(REJECTED)">Reject</button>
          </div>
        </div>

        <div class="block">
          <div class="level">
            <button class="level-item button is-success" v-bind:disabled="false" @click="comp.setResults()">Save results
              CSV</button>

          </div>
        </div>
        <div class="block">
          <div class="field is-grouped">
            <div class="field is-grouped">
              <div class="label">Heldback</div>
              <div class="control">
                {{ numberHeldBack }}
              </div>
            </div>

            <div class="field is-grouped">
              <div class="label">Rejected</div>
              <div class="control">

                {{ numberRejected }}

              </div>
            </div>
            <div class="field is-grouped">
              <div class="label">Unscored</div>
              <div class="control">
                {{ numberUnscored }}
              </div>
            </div>

          </div>


        </div>

        <div class="block">
          <table>
            <tbody v-for="s in comp.selectedCompetition.scoringSystem.orderedValueScores" :key="s">
              <tr v-for="i in imageForScore(s)" :key="i.id">
                <td class="p-1"><span class="subtitle">{{ s }}</span></td>
                <td class="p-1"> <span class="subtitle has-text-weight-semibold">{{ i.title }}</span>
                </td>
                <td class="p-1"><span class="subtitle is-italic">{{ i.photographer }}</span>
                </td>
              </tr>
            </tbody>

          </table>
        </div>

        <div class="box">
          <StatusComponent />
        </div>

      </div>


    </div>

  </div>
  <!-- <LoadImagesComponent :active="statusIndicator.loadImagesDialog" @done="doneLoadImages" @abort="abortLoadImages" /> -->
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

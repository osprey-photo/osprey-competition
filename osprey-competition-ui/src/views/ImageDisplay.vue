<script setup lang="ts">
import { useDisplayStore } from '@/stores/displaystrore';
import { FIRST, SECOND, THIRD, HC, HELD_BACK, REJECTED } from '@/types';
const comp = useDisplayStore();
function placeStyle(place: string) {
  // console.log(place)
  switch (place) {
    case FIRST:
      return 'is-link '
    case SECOND:
      return 'is-link '
    case THIRD:
      return 'is-link '
    case HC:
      return 'is-success '
    case HELD_BACK:
      return 'is-primary is-light'
    case REJECTED:
      return 'is-danger is-light'
    default:
      return ''
  }
}

</script>

<template>
  <div>
    <div v-if="comp.displayType == 'full_image'" class="singleimg">

      <img class="si" :src="`data:image/png;base64,${comp.images[0].fullImageB64}`" alt=" Red dot" />
      <div v-show="comp.showTitle" class="notification img-title title">
        {{ comp.images[0].title }}
      </div>
    </div>
    <div v-else-if="comp.displayType == 'light_box_images'" class="">

      <div class="grid is-col-min-15 lightbox p-6">
        <div class="cell " v-for="i in comp.images" :key="i.id">

          <div class="card">

            <div class="card-image">

              <img class="image p-3" :src="`data:image/png;base64,${i.halfishB64}`" alt=" Red dot" />

            </div>
            <div class="card-content">
              <p class="is-italic is-size-4 has-text-weight-semibold">{{ i.title }} </p>

            </div>
            <div class="card-footer">
              <span class="button is-large m-1 mb-4" v-show="i.state.kept != ''" :class="placeStyle(i.state.kept)">
                {{ i.state.kept }}
              </span>
              <span class="button is-large m-1 mb-4" v-show="i.state.place != ''" :class="placeStyle(i.state.place)">
                {{ i.state.place }}
              </span>
            </div>
          </div>


        </div>

      </div>
    </div>
    <div v-else-if="comp.displayType == 'light_box_results'" class="">

      <div class="grid is-col-min-15 lightbox p-6">
        <div class="cell " v-for="i in comp.images" :key="i.id">

          <div class="card">

            <div class="card-image">

              <img class="image p-3" :src="`data:image/png;base64,${i.halfishB64}`" alt=" Red dot" />

            </div>
            <div class="card-content">
              <p class="is-italic is-size-4 has-text-weight-bold">{{ i.title }} </p>
              <p class="is-size-4 has-text-weight-semibold">{{ i.photographer }} </p>
            </div>
            <div class="card-footer">
              <span class="button is-large m-1 mb-4" v-show="i.state.place != ''" :class="placeStyle(i.state.place)">
                {{ i.state.place }}
              </span>
            </div>
          </div>


        </div>

      </div>
    </div>
    <div v-else>

      <section class="hero">
        <div class="hero-body">
          <figure class="image is-128x128">
            <img src="@/assets/logo-white-bkg-200x200.png" />
          </figure>
          <p class="title">Bishop's Waltham Photographic Society</p>
          <p class="title">PDI Competition</p>
        </div>
      </section>
    </div>
  </div>

</template>
<style lang="css">
.lightbox {
  background-color: #808080;
}

html {
  background-color: #808080;
  overflow: hidden;
}

.singleimg {

  margin: 0;
  background-color: #808080;
  /* Mid-grey background */
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.si {
  width: auto;
  height: 90vh;
  object-fit: contain;
  /* Ensures the image fills the space without distortion */
}

.img-title {

  position: fixed;
  /* Stays in the same spot even when scrolling */
  top: 15%;
  /* Vertically centers the box */
  left: 50%;
  /* Horizontally centers the box */
  transform: translate(-50%, -50%);
  /* Adjusts position to be fully centered */
  /* width: 200px; */
  /* Sets the box width */
  /* height: 100px; */
  /* Sets the box height */
  background-color: rgba(0, 0, 0, 0.8);
  /* Semi-transparent black background */
  color: white;
  /* White text color */
  text-align: center;
  /* Centers text */
  /* line-height: 100px; */
  /* Vertically centers single-line text */
  border-radius: 8px;
  /* Adds rounded corners */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  /* Adds a shadow for depth */

}
</style>

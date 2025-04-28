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

function placeText(place: string){
  switch (place) {
    case FIRST:
      return 'First'
    case SECOND:
      return 'Second'
    case THIRD:
      return 'Third'
    case HC:
      return 'HC'
    case HELD_BACK:
      return 'Held-back'
    case REJECTED:
      return ''
    default:
      console.log("Place text original "+place)
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
              <img class="image is-1by1 p-3" :src="`data:image/png;base64,${i.halfishB64}`" alt=" Red dot" />

            </div>
            <div class="card-content">
              <p class="is-italic is-size-4 has-text-weight-semibold">{{ i.title }} </p>

            </div>
            <div class="card-footer">
              <span class="button is-large m-1 mb-4" v-show="placeText(i.state.kept) != ''" :class="placeStyle(i.state.kept)">
                {{ placeText(i.state.kept) }}
              </span>
              <span class="button is-large m-1 mb-4" v-show="placeText(i.state.place) != ''" :class="placeStyle(i.state.place)">
                {{ placeText(i.state.place) }}
              </span>
            </div>
          </div>


        </div>

      </div>
    </div>
    <div v-else-if="comp.displayType == 'light_box_images_results'" class="">

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
              <span class="button is-large m-1 mb-4" v-show="placeText(i.state.place) != ''" :class="placeStyle(i.state.place)">
                {{ placeText(i.state.place) }}
              </span>
            </div>
          </div>


        </div>

      </div>
    </div>
    <div v-else-if="comp.displayType == 'full_image_results'" class="singleimg">

      <img class="si" :src="`data:image/png;base64,${comp.images[0].fullImageB64}`" alt=" Red dot" />
      <div class="notification img-title title">
        <p>{{ comp.images[0].title }}</p>
        <p><br /></p>
        <p class="has-text-weight-semibold is-italic">{{ comp.images[0].photographer }}</p>
        <p><br /></p>
        <p>
          <span class="button is-large m-1 mb-4" v-show="placeText(comp.images[0].state.place) != ''"
            :class="placeStyle(comp.images[0].state.place)">
            {{ placeText(comp.images[0].state.place) }}
          </span>
        </p>
      </div>
    </div>
    <div v-else>

      <section class="hero">
        <div class="hero-body">
          <figure class="image is-128x128">
            <img src="@/assets/logo-white-bkg-200x200.png" />
          </figure>
          <p><br></p>
          <p class="title has-text-weight-semibold  has-text-light">Bishop's Waltham Photographic Society</p>
          <p class="title is-3 has-text-weight-semibold has-text-light">PDI Competition</p>
        </div>
      </section>
    </div>
  </div>

</template>
<style lang="css">
.lightbox {
  background-color: #505050;
  max-height: 100vh;
  overflow-y: auto;
}

html {
  background-color: #505050;
  overflow-y: hidden !important;
}

.singleimg {

  margin: 0;
  background-color: #505050;
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

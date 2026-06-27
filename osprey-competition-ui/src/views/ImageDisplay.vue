<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useDisplayStore } from '@/stores/displaystrore'
import { FIRST, SECOND, THIRD, HC, HELD_BACK, REJECTED } from '@/types'
const comp = useDisplayStore()

function placeStyle(place: string) {
  switch (place) {
    case FIRST:
      return 'btn-secondary btn-outline'
    case SECOND:
      return 'btn-secondary btn-outline'
    case THIRD:
      return 'btn-secondary btn-outline'
    case HC:
      return 'btn-success btn-outline'
    case HELD_BACK:
      return 'btn-primary btn-outline'
    case REJECTED:
      return 'btn-error btn-outline'
    default:
      return ''
  }
}

function placeText(place: string) {
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
      return ''
  }
}

/**
 * Compute a CSS grid-template-columns value that fits all images on-screen
 * without any scrolling. Recalculates whenever the image list changes.
 *
 * Strategy: start with the most columns possible (all in one row) and
 * reduce until cells become taller than wide. The last c where
 * cellW >= cellH is the most-columns balanced layout.
 */
const lightboxGrid = computed(() => {
  const count = comp.images.length
  if (count === 0) return 'repeat(1, 1fr)'

  const vw = window.innerWidth
  const vh = window.innerHeight
  // Reserve ~28px per cell for the meta strip
  const metaH = 28

  // Start at max columns and reduce until cells are landscape/square
  for (let c = count; c >= 1; c--) {
    const rows = Math.ceil(count / c)
    const cellW = vw / c
    const cellH = vh / rows - metaH
    if (cellW >= cellH) {
      return `repeat(${c}, 1fr)`
    }
  }

  return 'repeat(1, 1fr)'
})
</script>

<template>
  <div class="display-root">
    <!-- Full image single view -->
    <div v-if="comp.displayType == 'full_image'" class="singleimg">
      <img class="si" :src="`data:image/png;base64,${comp.images[0].fullImageB64}`" alt="competition image" />
      <transition name="fade">
        <div v-show="comp.showTitle" class="img-title">
          <p class="img-title-name">{{ comp.images[0].title }}</p>
          <p v-if="comp.displayType == 'full_image_results'" class="img-title-photographer">{{ comp.images[0].photographer }}</p>
        </div>
      </transition>
    </div>

    <!-- Lightbox grid view -->
    <div v-else-if="comp.displayType == 'light_box_images'" class="lightbox-wrap">
      <div
        class="lightbox-grid"
        :style="{ gridTemplateColumns: lightboxGrid }"
      >
        <div v-for="i in comp.images" :key="i.id" class="lb-cell">
          <div class="lb-card">
            <div class="lb-img-wrap">
              <img :src="`data:image/png;base64,${i.halfishB64}`" alt="competition image" class="lb-img" />
              <!-- Place banner overlay (shown when image has a placement) -->
              <div
                v-if="placeText(i.state.place) != ''"
                class="lb-place-banner"
                :class="`lb-place-${i.state.place}`"
              >
                {{ placeText(i.state.place) }}
              </div>
            </div>
            <div class="lb-meta">
              <p class="lb-title">{{ i.title }}</p>
              <p v-if="comp.displayType == 'light_box_images_results'" class="lb-photographer">{{ i.photographer }}</p>
              <div class="lb-badges" v-show="placeText(i.state.kept) != ''">
                <span class="btn btn-xs" :class="placeStyle(i.state.kept)">
                  {{ placeText(i.state.kept) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Lightbox results grid view -->
    <div v-else-if="comp.displayType == 'light_box_images_results'" class="lightbox-wrap">
      <div
        class="lightbox-grid"
        :style="{ gridTemplateColumns: lightboxGrid }"
      >
        <div v-for="i in comp.images" :key="i.id" class="lb-cell">
          <div class="lb-card">
            <div class="lb-img-wrap">
              <img :src="`data:image/png;base64,${i.halfishB64}`" alt="competition image" class="lb-img" />
              <div
                v-if="placeText(i.state.place) != ''"
                class="lb-place-banner"
                :class="`lb-place-${i.state.place}`"
              >
                {{ placeText(i.state.place) }}
              </div>
            </div>
            <div class="lb-meta">
              <p class="lb-title">{{ i.title }}</p>
              <p class="lb-photographer">{{ i.photographer }}</p>
              <div class="lb-badges" v-if="placeText(i.state.place) != ''">
                <span class="btn btn-xs" :class="placeStyle(i.state.place)">
                  {{ placeText(i.state.place) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Full image results view -->
    <div v-else-if="comp.displayType == 'full_image_results'" class="singleimg">
      <img class="si" :src="`data:image/png;base64,${comp.images[0].fullImageB64}`" alt="competition image" />
      <!-- Place banner at top — same gradient style as lightbox, but bigger -->
      <div
        v-if="placeText(comp.images[0].state.place) != ''"
        class="lb-place-banner si-place-banner"
        :class="`lb-place-${comp.images[0].state.place}`"
      >
        {{ placeText(comp.images[0].state.place) }}
      </div>
      <div class="img-title">
        <p class="img-title-name">{{ comp.images[0].title }}</p>
        <p class="img-title-photographer">{{ comp.images[0].photographer }}</p>
      </div>
    </div>

    <!-- Calibration chart -->
    <div v-else-if="comp.displayType == 'calibration'" class="singleimg">
      <img src="@/assets/Calibration-Chart-1400.jpg" class="si" alt="calibration chart" />
    </div>

    <!-- Idle / blank screen -->
    <div v-else class="singleimg">
      <div class="idle-content">
        <img src="@/assets/logo-white-bkg-200x200.png" class="idle-logo" />
        <p class="idle-club">Bishop's Waltham Photographic Society</p>
        <p class="idle-event">PDI Competition</p>
      </div>
    </div>
  </div>
</template>

<style lang="css">
/* ── Root ─────────────────────────────────────────────────────────────── */
html, body {
  margin: 0;
  padding: 0;
  background-color: #000;
  overflow: hidden;
}

.display-root {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #000;
}

/* ── Single image view ───────────────────────────────────────────────── */
.singleimg {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #000;
  position: relative;
}

.si {
  max-width: 100vw;
  max-height: 100vh;
  width: auto;
  height: auto;
  object-fit: contain;
}

/* ── Floating title overlay ──────────────────────────────────────────── */
.img-title {
  position: absolute;
  bottom: 2rem;
  left: 50%;
  transform: translateX(-50%);
  background-color: rgba(0, 0, 0, 0.72);
  color: #eceff4; /* nord snow-storm */
  padding: 0.8rem 2rem;
  border-radius: 6px;
  text-align: center;
  white-space: nowrap;
  border: 1px solid rgba(255,255,255,0.08);
}

.img-title-name {
  font-size: 2rem;
  font-weight: 700;
  color: #eceff4;
  margin: 0;
  line-height: 1.2;
}

.img-title-photographer {
  font-size: 1.3rem;
  font-weight: 400;
  font-style: italic;
  color: rgba(255,255,255,0.70);
  margin: 0.3rem 0 0;
  line-height: 1.2;
}

.fade-enter-active, .fade-leave-active { transition: opacity 0.4s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }

/* ── Lightbox wrapper ────────────────────────────────────────────────── */
.lightbox-wrap {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background-color: #000;
  display: flex;
  align-items: stretch;
}

/* ── Lightbox grid ───────────────────────────────────────────────────── */
.lightbox-grid {
  display: grid;
  width: 100%;
  height: 100%;
  gap: 3px;
  padding: 3px;
  box-sizing: border-box;
}

/* ── Individual cell ─────────────────────────────────────────────────── */
.lb-cell {
  min-height: 0;
  min-width: 0;
  display: flex;
}

/* ── Card ────────────────────────────────────────────────────────────── */
.lb-card {
  display: flex;
  flex-direction: column;
  width: 100%;
  background-color: #060606;
  border-radius: 4px;
  overflow: hidden;
}

/* ── Image section of card ───────────────────────────────────────────── */
.lb-img-wrap {
  flex: 1 1 0;
  min-height: 0;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.lb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* ── Meta strip ──────────────────────────────────────────────────────── */
.lb-meta {
  flex: 0 0 auto;
  padding: 4px 6px;
  background-color: #050505;
  border-top: 1px solid #111;
}

.lb-title {
  font-size: 0.7rem;
  font-weight: 600;
  color: #d8dee9; /* nord snow-storm */
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.lb-photographer {
  font-size: 0.65rem;
  color: #81a1c1; /* nord frost */
  margin: 0 0 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.lb-badges {
  display: flex;
  gap: 3px;
  margin-top: 2px;
  flex-wrap: wrap;
}

/* ── Place banner overlay ────────────────────────────────────────────── */
.lb-place-banner {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  padding: 8px 8px;
  font-size: 0.75rem;
  font-weight: 800;
  text-align: center;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  color: #fff;
  text-shadow: 0 1px 3px rgba(0,0,0,0.6);
  pointer-events: none;
}

/* Per-place colour strips — multi-stop glint gradients */
.lb-place-first {
  background: linear-gradient(
    105deg,
    #7a5000 0%,
    #c8860a 20%,
    #ffe066 40%,
    #ffd700 50%,
    #ffe066 60%,
    #c8860a 80%,
    #7a5000 100%
  );
}
.lb-place-second {
  background: linear-gradient(
    105deg,
    #2a2a2a 0%,
    #777 20%,
    #d8d8d8 40%,
    #f0f0f0 50%,
    #d8d8d8 60%,
    #777 80%,
    #2a2a2a 100%
  );
  color: #111;
  text-shadow: 0 1px 2px rgba(255,255,255,0.4);
}
.lb-place-third {
  background: linear-gradient(
    105deg,
    #4a2000 0%,
    #8b4513 20%,
    #e08040 40%,
    #cd7f32 50%,
    #e08040 60%,
    #8b4513 80%,
    #4a2000 100%
  );
}
.lb-place-hc      { background: rgba(56, 139, 66, 0.90); }
.lb-place-held_back { background: rgba(59, 130, 212, 0.90); }

/* ── Full-image place banner (bigger override) ───────────────────────── */
.si-place-banner {
  padding: 1rem 2rem;
  font-size: 2rem;
  letter-spacing: 0.15em;
}

/* ── Idle screen ─────────────────────────────────────────────────────── */
.idle-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.idle-logo {
  width: 7rem;
  height: 7rem;
  object-fit: contain;
  opacity: 0.5;
}

.idle-club {
  font-size: 1.3rem;
  font-weight: 500;
  color: rgba(255,255,255,0.5);
  margin: 0;
}

.idle-event {
  font-size: 1.7rem;
  font-weight: 600;
  color: rgba(255,255,255,0.5);
  margin: 0;
}
</style>

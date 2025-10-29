<template>
  <div class="layout">
    <Nuxt/>

  </div>
</template>
<script>

export default {
};
</script>
<style lang="scss">
* {
  margin: 0;
  padding: 0;
}

html, body {
  min-height: 100vh;
  font-size: 62.5%;
  font-family: eurofonc;
  font-weight: 400;
  word-break: break-word;
  overflow: hidden;
  overflow-y: auto;
}

html {
  word-spacing: 1px;
  -ms-text-size-adjust: 100%;
  -webkit-text-size-adjust: 100%;
  -moz-osx-font-smoothing: grayscale;
  -webkit-font-smoothing: antialiased;
  box-sizing: border-box;

}

.layout {
  width: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
}

// 禁用样式
.disabled {
  -webkit-filter: grayscale(100%);
  -moz-filter: grayscale(100%);
  -ms-filter: grayscale(100%);
  -o-filter: grayscale(100%);
  filter: grayscale(100%);
  filter: gray;
  pointer-events: none;
}

.show {
  display: block !important;
}

.hide {
  display: none !important;
}

</style>

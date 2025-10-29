<template>
  <div class="container">

  </div>
</template>

<script lang="js">

export default {
  data() {
    return {
    }
  },
  methods: {
  },
  mounted() {
    this.$router.push('/Start');
    // ios 页面禁用缩放 -- 待测 -------------
    document.addEventListener('touchstart', function(event) {
    if (event.touches.length > 1) {
      event.preventDefault()
    }
    })
    document.addEventListener('gesturestart', function(event) {
    event.preventDefault()
    // ------------
 })
  },
  computed: {},
}
</script>
<style scoped lang="scss">
.container {
  width: 100%;
  height: 100%;
}

</style>

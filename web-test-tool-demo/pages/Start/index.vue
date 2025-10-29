<template>
  <div class="container">
    <div class="containerBtn">
      <div class="scanBtn" @click="toScanNo">扫码测试</div>
    </div>
  </div>
</template>
<script>
export default {
  data () {
    return {}
  },
  mounted () {},
  methods: {
    toScanNo () {
      this.$router.push({
        name: 'Scan',
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  width: 100%;
  height: 100%;
  background: #fff;
  .containerBtn {
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    margin-top: 80%;
    .scanBtn {
      background: #1aad19;
      width: calc(50%);
      height: 50px;
      line-height: 52px;
      border-radius: 5px;
      color: #fff;
      font-size: 22px;
    }
  }
}
</style>

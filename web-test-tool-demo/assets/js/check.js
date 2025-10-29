/**
 * 校验金额
 * @param val
 */
export const amount = (val) => {
  if (!val) {
    return
  }
  console.log(`val: ${val}, typeof: ${typeof val}`)
  // 清除"数字"/"."/"-"以外的字符
  val = val.replace(/[^\d.-]/g, '')
  // 只保留第一个.清除多余的
  val = val.replace(/\.{2,}/g, '.')
  // 只保留第一个-清除多余的
  val = val.replace(/-{2,}/g, '-')
  val = val.replace('.', '$#$').replace(/\./g, '').replace('$#$', '.')
  // 只能输入两个小数
  val = val.replace(/^(-)*(\d+)\.(\d\d).*$/, '$1$2.$3')
  return val
}

/**
 * 校验手机号
 * @param val
 */
export const phone = (val) => {
  if (!val) {
    return
  }
  console.log(`val: ${val}, typeof: ${typeof val}`)
  val = val.replace(/\D/g, '')
  if (val.length > 15) {
    val = val.slice(0, 15)
  }
  return val
}

/**
 * 校验密码
 * @param val
 */
export const password = (val) => {
  if (!val) {
    return
  }
  console.log(`val: ${val}, typeof: ${typeof val}`)
  val = val.replace(/[^a-zA-Z0-9]/g, '')
  if (val.length > 20) {
    val = val.slice(0, 20)
  }
  return val
}

/**
 * 校验卡号
 * @param val
 */
export const card = (val) => {
  if (!val) {
    return
  }
  console.log(`val: ${val}, typeof: ${typeof val}`)
  val = val.replace(/\D/g, '')
  if (val.length > 20) {
    val = val.slice(0, 20)
  }
  return val
}

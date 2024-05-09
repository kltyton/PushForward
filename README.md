# This is a lightweight mod！
Its icon comes from _Demon Slayer_ **『鬼滅の刃（きめつのやいば）』**
（这是一个轻量级的模组！
它的图标来自于《鬼灭之刃》『鬼滅の刃（きめつのやいば）』。）
## This mod is inspired by goats.
It allows each entity to fly enemies like a goat!
（这个模组的灵感来自山羊。
它使得每个实体都能像山羊一样飞跃敌人！）
### Of course, it's all configurable!
（当然，一切都可以配置！）
```
{
  // "damageRateOfMultiplication"：伤害倍率，决定了撞击伤害的计算公式中的乘数
  // "damageRateOfMultiplication": Damage rate of multiplication, determines the multiplier in the calculation formula of collision damage
  "damageRateOfMultiplication": 5.0,

  // "knockbackRateOfMultiplication"：击退倍率，决定了击退效果的计算公式中的乘数
  // "knockbackRateOfMultiplication": Knockback rate of multiplication, determines the multiplier in the calculation formula of knockback effect
  "knockbackRateOfMultiplication": 1.0,

  // "velocityThreshold"：速度阈值，当实体的速度超过这个值时，才会触发撞击伤害和击退效果
  // "velocityThreshold": Velocity threshold, when the speed of the entity exceeds this value, the collision damage and knockback effect will be triggered
  "velocityThreshold": 0.0,

  // "maxDamage"：最大伤害，撞击伤害的上限
  // "maxDamage": Maximum damage, the upper limit of collision damage
  "maxDamage": 20.0,

  // "minDamage"：最小伤害，撞击伤害的下限
  // "minDamage": Minimum damage, the lower limit of collision damage
  "minDamage": 0.0,

  // "maxKnockback"：最大击退，击退效果的上限
  // "maxKnockback": Maximum knockback, the upper limit of knockback effect
  "maxKnockback": 5.0,

  // "minKnockback"：最小击退，击退效果的下限
  // "minKnockback": Minimum knockback, the lower limit of knockback effect
  "minKnockback": 0.0,

  // "hurtBackRateOfMultiplication"：反伤倍率，决定了反伤的计算公式中的乘数
  // "hurtBackRateOfMultiplication": Hurt back rate of multiplication, determines the multiplier in the calculation formula of hurt back
  "hurtBackRateOfMultiplication": 1.0,

  // "hurtBack"：是否反伤，如果为true，实体在撞击其他实体时也会受到伤害
  // "hurtBack": Whether to hurt back, if true, the entity will also be damaged when it hits other entities
  "hurtBack": false,

  // "blackListEnabled"：是否启用黑名单，如果为true，黑名单中的实体不会受到撞击伤害和击退效果
  // "blackListEnabled": Whether to enable the blacklist, if true, entities in the blacklist will not be subject to collision damage and knockback effect
  "blackListEnabled": false,

  // "whiteListEnabled"：是否启用白名单，如果为true，只有白名单中的实体会受到撞击伤害和击退效果
  // "whiteListEnabled": Whether to enable the whitelist, if true, only entities in the whitelist will be subject to collision damage and knockback effect
  "whiteListEnabled": false,

  // "blackList"：黑名单，列表中的实体不会受到撞击伤害和击退效果
  // "blackList": Blacklist, entities in the list will not be subject to collision damage and knockback effect
  "blackList": [],

  // "whiteList"：白名单，只有列表中的实体会受到撞击伤害和击退效果
  // "whiteList": Whitelist, only entities in the list will be subject to collision damage and knockback effect
  "whiteList": []
}

```
Only version 1.20.1 of Minecraft is currently supported，
but I will update to more versions later.
（目前仅支持Minecraft的1.20.1版本，
但我会在之后更新到更多的版本。）
[我的BiliBili主页](https://space.bilibili.com/353872260

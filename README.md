# Soul Out Plus (灵魂出窍 Plus)

灵魂出窍自由视角 + 远程操作：出窍状态下准星直接放置（方块朝向跟随灵魂视角）、挖掘、方块交互、攻击。

Tweakeroo 的自由视角与投影打印机（litematica printer）的远程操作思路结合的独立 Fabric mod。

## 功能

- **灵魂出窍**：身体留在原地不动，灵魂（相机）自由飞行、穿墙
- **远程放置**：出窍时对准星直接右键放置，方块朝向 = 灵魂视角朝向
- **远程挖掘**：出窍时对准星左键挖掘
- **shift+右键 = 放置**：对着音符盒/活板门等可交互方块，shift+右键是放置而不是打开
- **实体交互/攻击**：出窍时对实体交互与攻击
- **距离独立配置**：放置/挖掘/实体攻击距离分开，单人自动调高交互属性
- **幽灵预览**：准星处实时显示待放置方块轮廓与朝向
- **可达性指示**：准星旁三色点（绿=可操作 / 黄=超配置但服务端允许 / 红=不可达）
- **被拒红闪**：放置被服务端拒绝时方块位置闪红
- **吸附六向**：按住热键灵魂视角吸附到最近六向，放朝向敏感方块防手抖
- **5 栏配置界面**：malilib 同款（热键 / 通用 / 距离 / 放置与挖掘 / 渲染），支持单条重置
- **ModMenu 集成**：模组菜单直接打开配置

## 环境要求

- Minecraft **1.21.11**（也兼容 1.21.9 / 1.21.10）
- Fabric Loader ≥ 0.17.0
- **MaLiLib** ≥ 0.26.8
- Fabric API
- ModMenu（可选）

## 安装

把 jar 丢进 `mods` 文件夹。默认热键未绑定，进游戏后到 ModMenu → Soul Out Plus → 热键 栏绑定：

| 热键 | 说明 |
|---|---|
| 灵魂出窍开关 | 切换出窍 / 回魂 |
| 打开配置界面 | 同上 |
| 回到本体 | 灵魂瞬移回本体脚下 |
| 相机吸附六向 | 按住时视角吸附最近六向 |

## 交互源三态

| 模式 | 行为 |
|---|---|
| SOUL（默认） | 射线从灵魂眼睛打出，放置朝向用灵魂视角 |
| BODY | 射线从本体眼睛打出（Tweakeroo freeCameraPlayerInputs 效果） |
| OFF | 出窍时屏蔽左右键（Tweakeroo 默认行为） |

## 构建

```
gradlew build
```

产物在 `build/libs/`。

## 致谢

- [Tweakeroo](https://github.com/maruohon/tweakeroo)（maruohon）— 自由视角实现参考
- [litematica-printer-autyism](https://github.com/Autyism/litematica-printer-autyism)（Autyism）— 远程操作思路参考
- MaLiLib（masa）— 配置系统

## License

LGPL-3.0

activate-power-mode
====

activate-power-mode for Intellij IDEA
-------

![PREVIEW](https://github.com/ViceFantasyPlace/activate-power-mode/blob/master/ActivatePowerModePreview.gif?raw=true) 
![PREVIEW_2](https://github.com/ViceFantasyPlace/activate-power-mode/blob/master/ActivatePowerModePreview-2.gif?raw=true) 

Features.
-------
 - 粒子的颜色根据文本字体的颜色
 - 把Combo, MaxCombo 可视化出来
 - 做设置数据持久化
 - 增加设置参数界面
 - 持续更新 API 到 2019.3.x 版本
 

使用
-------
安装 activate-power-mode 之后 到 Window > activate-power-mode 就可以开启或者关闭相对应的效果

建议配合 [Nyan Progress Bar 插件](https://plugins.jetbrains.com/plugin/8575-nyan-progress-bar) 使用


安装
-------

#### 下载jar包
[直接在 release 下载](https://github.com/ViceFantasyPlace/activate-power-mode/releases)

[~~在Jetbrains plugin repositories上下载（官方仓库版本的作者已放弃维护）~~](https://plugins.jetbrains.com/plugin/8330?pr=idea)

#### 安装 Plugin jar包
>Preferences/Plugins <br>
>-> Install plugin from disk <br>
>-> 选择 jar文件 <br>
>-> Apply <br>
>-> 重启编辑器 <br>

开发
-------

#### 下载
>git clone https://github.com/SinarPandora/activate-power-mode.git

#### 第一次运行如果没有运行配置需要创建一个Plugin的Configuration
>Edit Run/Debug Configurations <br>
>-> add a new Plugin Configuration <br>
>-> 在 Use classpath of module 选择 activate-power-mode <br>
>-> Apply <br>

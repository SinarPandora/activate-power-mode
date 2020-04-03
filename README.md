activate-power-mode-x
====

activate-power-mode for Jetbrains IDEs, forked from [ViceFantasyPlace/activate-power-mode](https://github.com/ViceFantasyPlace/activate-power-mode)
-------

![PREVIEW](https://raw.githubusercontent.com/SinarPandora/activate-power-mode/master/editor.gif?raw=true) 
![PREVIEW_2](https://raw.githubusercontent.com/SinarPandora/activate-power-mode/master/counter.gif?raw=true) 

Features.
-------
 - 粒子的颜色根据文本字体的颜色
 - 把Combo, MaxCombo 可视化出来
 - 做设置数据持久化
 - 增加设置参数界面
 - 持续更新 API 到 2019.3.x 版本
 

使用
-------
安装插件之后 到 Window > Activate Power Mode! 就可以开启或者关闭相对应的效果

在配置菜单 Other Settings > Activate Power Mode 中提供了修改效果的选项

截图中的进度条来自 [Nyan Progress Bar 插件](https://plugins.jetbrains.com/plugin/8575-nyan-progress-bar)


安装
-------

#### 下载jar包
[直接在 release 下载](https://github.com/SinarPandora/activate-power-mode/releases)

[在Jetbrains plugin repositories上下载](https://plugins.jetbrains.com/plugin/14000-activate-power-mode-x)

#### 安装 Plugin jar包
>Preferences > Plugins <br>
>-> Install plugin from disk <br>
>-> 选择 jar 文件 <br>
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

TODO
-------
[x] 自定义字体  
[ ] 可配置的震动、粒子触发时机  
[ ] 更平滑的进度条增长动画  
[ ] 类似 Atom 插件，达到一定数量后改变计数器颜色及弹出文本  

# QStoryCLoud 自动云更新

### 项目介绍
该项目主要用于快捷的为QStory进行内置更新，减少用户手动更新模块的麻烦
---
### 功能
自动检测QStory的更新，并且自动更新，加载到QQ
---
### 使用的技术栈
- SQLite
- XPosed
- 跨进程通信（跨应用）ContentProvider
- 热更新（基于DexClassLoader)
- 设计模式
---
### 项目主要知识要点
 - 如何动态加载模块并进行Hook：[ModuleLoader](./app/src/main/java/top/linl/qstorycloud/hook/moduleloader/ModuleLoader.java)  
 - 如何进行模块的更新,主要通过观察者模式实现定时拉取更新：[update](./app/src/main/java/top/linl/qstorycloud/hook/update) 
 - 数据存储怎么做的,主要看本地模块信息记录：[ModuleInfoDAO](./app/src/main/java/top/linl/qstorycloud/db/ModuleInfoDAO.java) ，拉取到的更新记录[UpdateInfoDAO](./app/src/main/java/top/linl/qstorycloud/db/UpdateInfoDAO.java)
 - 如何使模块和QQ进行跨进程通讯采用的是[ContentProvider](./app/src/main/java/top/linl/qstorycloud/provider/AppContentProvider.java)
---



# 命令行构建教程

* 使用Android Studio的GUI式构建是方便而且推荐的做法。但是在其他不得不使用命令行界面的场景，也许有本教程的用武之地。
* 本教程也可以作为参考资料用来命令行构建其他应用。

## 环境要求

项目|参数|说明
---|---|---
指令集|`amd64`|`arm64`经测试无法构建（Android设备，使用[Termux](https://termux.com)）
最低内存|`3G`|理论上也许支持更低的内存
最低存储|`10G`|实测整个构建过程完成一次后，硬盘占用6.6G
操作系统|`Ubuntu Server 20.04.3 LTS`|使用[AOSP所要求的软件环境](https://source.android.google.cn/setup/build/requirements#software-requirements)以确保构建成功，使用服务器版以降低资源占用

## 步骤

### 准备软件包

更新并重启，以增加构建过程的成功率

```shell
sudo apt update && sudo apt upgrade -y
sudo reboot # 可选
```

安装以下软件包
* java：用的是java 11，AndResGuard暂不支持java 17
* zip：用来解压文件
* 7z：可选，AndResGuard依赖以进一步压缩APK
```shell
sudo apt install openjdk-11-jre-headless zip p7zip-full -y
```

### 拉取代码

在用户目录拉取代码，由于仅用来构建，所以[仅拉取最近一次commit](https://git-scm.com/docs/git-clone#Documentation/git-clone.txt---depthltdepthgt)以节省下载时间

```shell
cd ~
git clone --depth=1 https://github.com/ryuunoakaihitomi/rebootmenu.git
```

### 配置构建环境

#### 下载Android命令行工具

下载地址获取方式：在[Android Developer的Android Studio下载页面](https://developer.android.google.cn/studio#downloads)**往下翻**，翻到Command line tools only标题处，找到linux项之后同意条款复制链接

```shell
wget https://dl.google.com/android/repository/commandlinetools-linux-7583922_latest.zip # 如果下载地址有变，在这里修改
unzip commandlinetools-linux-*_latest.zip
```

#### 配置Android SDK

新建一个目录作为SDk根目录

```shell
mkdir android_sdk
export ANDROID_SDK_ROOT=$HOME/android_sdk
```

安装Android 12的SDK，compileSdkVersion用的是这个，用法参考[sdkmanager用户指南](https://developer.android.google.cn/studio/command-line/sdkmanager?hl=zh_cn)

```shell
cd cmdline-tools/bin # 命令行工具目录
./sdkmanager --sdk_root=$HOME/android_sdk "platforms;android-31"
```

按`y`并回车同意协议

#### 安装Gradle

参考
* https://gradle.org/install
* https://sdkman.io/install

安装的版本是`7.3.3`（经测试可用，这是教程发布时最新的版本）

```shell
cd ~
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle 7.3.3
```

### 构建

#### 准备keystore

生成（[参考文档](https://docs.oracle.com/en/java/javase/11/tools/keytool.html)）

```shell
cd rebootmenu/app
keytool -genkey -alias a -dname CN=_ -storepass passwd -keypass passwd -keyalg RSA -keystore android.keystore
```

写入配置文件`secret.properties`

```shell
cd ..
echo KEY_ALIAS=a >> secret.properties
echo KEY_PWD=passwd >> secret.properties
echo STORE_PWD=passwd >> secret.properties
echo STORE_FILE=android.keystore >> secret.properties
```

这里可以自由发挥，比如不生成而是将自己的keystore传上去，然后配置文件的信息改成自己keystore的alias、密码和路径

#### 执行构建gradle任务

```shell
gradle resguardFlossRelease
```

等待片刻，输出最后出现`BUILD SUCCESSFUL`即构建成功，apk文件在`app/build/outputs/apk/floss/release`下
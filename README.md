# Sugar

<img src="./.idea/icon.png" alt="Sugar LOGO" width="" height="auto"></img>

[![Gradle](https://img.shields.io/badge/Gradle-8%2E9-g?logo=gradle&style=flat-square)](https://gradle.org/)
[![Zulu JDK](https://img.shields.io/badge/Zulu%20JDK-1.8-blue?style=flat-square)](https://www.azul.com/downloads/?package=jdk#zulu)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/ideal-state/sugar?style=flat-square&logo=github)
[![Discord](https://img.shields.io/discord/1191122625389396098?style=flat-square&logo=discord)](https://discord.gg/DdGhNzAu2r)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/ideal-state/sugar/release.yml?style=flat-square)
![GitHub Release](https://img.shields.io/github/v/release/ideal-state/sugar?style=flat-square)

<a href="https://github.com/ideal-state/sugar/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=ideal-state/sugar" alt="contributor" width="36px" height="auto" />
</a>

### 简介

> 待补充

### 如何使用

#### Maven
```xml
<!--pom.xml-->
<dependency>
    <groupId>team.idealstate.sugar</groupId>
    <artifactId>sugar</artifactId>
    <version>${version}</version>
</dependency>
```

#### Gradle
```groovy
// build.gradle
dependencies {
    implementation "team.idealstate.sugar:sugar:${version}"
}
```
```kotlin
// build.gradle.kts
dependencies {
    implementation("team.idealstate.sugar:sugar:${version}")
}
```

### 如何构建

```shell
# 1. 克隆项目到本地
git clone https://github.com/ideal-state/sugar.git
# 2. 进入项目根目录
cd ./sugar
# 3. 构建项目
./gradlew jar
```

### 怎样成为贡献者

在贡献之前，你需要了解[相应的规范](https://github.com/ideal-state/.github/blob/main/profile/README.md)。

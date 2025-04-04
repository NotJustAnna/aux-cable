import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("com.gradleup.shadow") version "8.3.3"
}

group = "net.notjustanna"
version = "2.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.linecorp.armeria:armeria:1.32.3")
    implementation("com.linecorp.armeria:armeria-graphql:1.32.3")
    implementation("com.linecorp.armeria:armeria-logback:1.32.3")
    implementation("com.github.webview:webview_java:1.3.0")
    implementation("net.dv8tion:JDA:5.1.1")
    implementation("club.minnced:udpqueue-native-win-x86-64:0.2.9")
    implementation("io.reactivex.rxjava3:rxjava:3.1.10")

    runtimeOnly("ch.qos.logback:logback-classic:1.5.17")
//    runtimeOnly(project(":frontend"))
}

application {
    mainClass.set("net.notjustanna.auxcable.MainKt")
    applicationDefaultJvmArgs = "-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1".split(" ")
}

kotlin {
    jvmToolchain(21)
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("app")
//    minimize {
//        exclude(dependency("ch.qos.logback:logback-classic:.*"))
//    }
}

distributions.named("shadow") {
    distributionBaseName.apply { set(get().removeSuffix("-shadow")) }

    @Suppress("UnstableApiUsage")
    distributionClassifier.set("app")
}
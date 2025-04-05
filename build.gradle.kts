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
    implementation("com.linecorp.armeria:armeria-rxjava3:1.32.3")
//    implementation("com.linecorp.armeria:armeria-kotlin:1.32.3")
    implementation("com.linecorp.armeria:armeria-logback:1.32.3")
    implementation("com.github.webview:webview_java:1.3.0")
    implementation("net.dv8tion:JDA:5.1.1")
    implementation("club.minnced:udpqueue-native-win-x86-64:0.2.9")
    implementation("club.minnced:udpqueue-native-win-x86:0.2.9")
    implementation("club.minnced:udpqueue-native-linux-x86-64:0.2.9")
    implementation("club.minnced:udpqueue-native-linux-x86:0.2.9")
    implementation("club.minnced:udpqueue-native-linux-musl-x86-64:0.2.9")
    implementation("club.minnced:udpqueue-native-linux-arm:0.2.9")
    implementation("club.minnced:udpqueue-native-linux-aarch64:0.2.9")
    implementation("club.minnced:udpqueue-native-linux-musl-aarch64:0.2.9")
    implementation("club.minnced:udpqueue-native-darwin:0.2.9")
    implementation("io.reactivex.rxjava3:rxjava:3.1.10")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.+")

    runtimeOnly("ch.qos.logback:logback-classic:1.5.17")
//    runtimeOnly(project(":frontend")) // FIXME uncomment on prod
}

application {
    mainClass.set("net.notjustanna.auxcable.MainKt")
    applicationDefaultJvmArgs = "-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1".split(" ")
}

kotlin {
    jvmToolchain(21)
    compilerOptions { javaParameters = true }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("app")
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude(
        "META-INF/maven/**",
        "META-INF/license/**",
        "META-INF/micrometer-*.properties",
        "META-INF/LGPL2.1",
        "META-INF/AL2.0",
        "META-INF/*-LICENSE",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/*-NOTICE",
        "META-INF/NOTICE",
        "META-INF/NOTICE.txt",
        "META-INF/COPYRIGHT",
        "META-INF/com.android.tools/**",
        "META-INF/proguard/**",
        "META-INF/native-image/**"
    )
//    minimize {
//        exclude(dependency("ch.qos.logback:logback-classic:.*"))
//    }
}

distributions.named("shadow") {
    distributionBaseName.apply { set(get().removeSuffix("-shadow")) }

    @Suppress("UnstableApiUsage")
    distributionClassifier.set("app")
}
plugins {
    id("io.micronaut.application") version "4.5.0"
    id("com.gradleup.shadow") version "8.3.6"
    id("io.micronaut.aot") version "4.5.0"
}

allprojects {
    group = "net.notjustanna"
    version = "3.0"

    repositories {
        mavenCentral()
        maven {
            url = uri("https://github.com/NotJustAnna/webview_java/raw/maven")
            content { includeGroup("net.notjustanna.webview") }
        }
    }
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micrometer:context-propagation")
    implementation("io.micronaut:micronaut-websocket")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    runtimeOnly("ch.qos.logback:logback-classic")

    implementation("net.notjustanna.webview:webview_java:1.2.0+wv0.12.0-nightly.1")
    implementation("net.notjustanna.webview:webview_java-all-natives:1.2.0+wv0.12.0-nightly.1")
    implementation("net.notjustanna.webview:webview_java-interop-jackson:1.2.0+wv0.12.0-nightly.1")

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

    runtimeOnly(project(":frontend"))
}


application {
    mainClass = "net.notjustanna.Application"
}
java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}


graalvmNative.toolchainDetection = false

micronaut {
    enableNativeImage(false)
    runtime("netty")
    processing {
        incremental(true)
        annotations("net.notjustanna.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

tasks.shadowJar {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude(
        "META-INF/maven/**",
        "META-INF/license/**",
        "META-INF/versions/9/**",
        "META-INF/versions/10/**",
        "META-INF/versions/11/**",
        "META-INF/versions/12/**",
        "META-INF/versions/13/**",
        "META-INF/versions/14/**",
        "META-INF/versions/15/**",
        "META-INF/versions/16/**",
        "META-INF/versions/17/**",
        "META-INF/versions/18/**",
        "META-INF/versions/19/**",
        "META-INF/versions/20/**",
        "META-INF/micrometer-*.properties",
        "META-INF/LGPL2.1",
        "META-INF/AL2.0",
        "META-INF/*-LICENSE",
        "META-INF/LICENSE",
        "META-INF/LICENSE.txt",
        "META-INF/LICENSE.md",
        "META-INF/*-NOTICE",
        "META-INF/NOTICE",
        "META-INF/NOTICE.md",
        "META-INF/NOTICE.txt",
        "META-INF/COPYRIGHT",
        "META-INF/com.android.tools/**",
        "META-INF/proguard/**",
        "META-INF/native-image/**"
    )
}

//distributions.named("shadow") {
//    distributionBaseName.apply { set(get().removeSuffix("-shadow")) }
//
//    @Suppress("UnstableApiUsage")
//    distributionClassifier.set("app")
//}
//

//
//tasks {
//    build.get().dependsOn(createAllExecutables)
//    createAllExecutables.get().dependsOn(shadowJar)
//}
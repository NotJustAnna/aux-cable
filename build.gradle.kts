plugins {
    id("io.micronaut.application") version "4.5.0"
    id("com.gradleup.shadow") version "8.3.6"
    id("io.micronaut.aot") version "4.5.0"
    id("edu.sc.seis.launch4j") version "3.0.6"
}

allprojects {
    group = "net.notjustanna"
    version = "3.1"

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

    implementation("net.notjustanna.webview:webview_java:1.3.1+wv0.12.0-nightly.1")
    implementation("net.notjustanna.webview:webview_java-all-natives:1.3.1+wv0.12.0-nightly.1")
    implementation("net.notjustanna.webview:webview_java-interop-jackson:1.3.1+wv0.12.0-nightly.1")

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
        "META-INF/maven/**", "META-INF/license/**", "META-INF/versions/9/**", "META-INF/versions/10/**",
        "META-INF/versions/11/**", "META-INF/versions/12/**", "META-INF/versions/13/**", "META-INF/versions/14/**",
        "META-INF/versions/15/**", "META-INF/versions/16/**", "META-INF/versions/17/**", "META-INF/versions/18/**",
        "META-INF/versions/19/**", "META-INF/versions/20/**", "META-INF/micrometer-*.properties",
        "META-INF/LGPL2.1", "META-INF/AL2.0", "META-INF/*-LICENSE", "META-INF/LICENSE", "META-INF/LICENSE.txt",
        "META-INF/LICENSE.md", "META-INF/*-NOTICE", "META-INF/NOTICE", "META-INF/NOTICE.md", "META-INF/NOTICE.txt",
        "META-INF/COPYRIGHT", "META-INF/com.android.tools/**", "META-INF/proguard/**", "META-INF/native-image/**"
    )
}

val mainClass = "net.notjustanna.Application"

val veryOptimizedJvmOptions = "-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1"

val jlinkOptions = "--verbose --strip-native-commands --strip-debug --no-header-files --no-man-pages --compress=zip-9"

val nativeFolders = mapOf(
    "natives" to listOf("darwin", "linux-aarch64", "linux-arm", "linux-musl-aarch64", "linux-musl-x86-64", "linux-x86",
        "linux-x86-64", "win-x86", "win-x86-64", "win32-x86", "win32-x86-64"),
    "com/sun/jna" to listOf("aix-ppc", "aix-ppc64", "darwin-aarch64", "darwin-x86-64", "dragonflybsd-x86-64", "freebsd-aarch64",
        "freebsd-x86", "freebsd-x86-64", "linux-aarch64", "linux-arm", "linux-armel", "linux-loongarch64", "linux-mips64el",
        "linux-ppc", "linux-ppc64le", "linux-riscv64", "linux-s390x", "linux-x86", "linux-x86-64", "openbsd-x86", "openbsd-x86-64",
        "sunos-sparc", "sunos-sparcv9", "sunos-x86", "sunos-x86-64", "win32-aarch64", "win32-x86", "win32-x86-64"),
    "net/notjustanna/webview/natives" to listOf("darwin", "linux-x86-64", "windows-arm64", "windows-x86", "windows-x86-64")
)

val allowedNatives = mapOf(
    "darwin" to listOf("darwin", "darwin-aarch64", "darwin-x86-64"),
    "win-x64" to listOf("win-x86-64", "win32-x86-64", "windows-x86-64"),
    "linux-x64" to listOf("linux-x86-64", "linux-musl-x86-64")
)

val requiredModules = listOf("java.net.http","java.desktop","java.logging","java.naming","jdk.unsupported","java.sql","jdk.crypto.ec","jdk.zipfs")

val osSpecific = let {
    val osName = System.getProperty("os.name").lowercase()

    when {
        osName.contains("windows") -> listOf("win-x64")
        osName.contains("linux") -> listOf("linux-x64")
        osName.contains("mac") -> listOf("darwin")
        else -> emptyList()
    }
}

project(":packaging").subprojects {
    apply(plugin = "base")
    if (project.name.startsWith("win-")) {
        apply(plugin = "edu.sc.seis.launch4j")
    }

    val optimizedJar: Zip by tasks.creating(Zip::class) {
        dependsOn(rootProject.tasks.shadowJar)
        archiveBaseName = rootProject.name
        archiveVersion = project.version.toString()
        archiveClassifier = project.name
        archiveExtension = "jar"
        destinationDirectory = project.layout.buildDirectory.dir("libs")

        val allowed = allowedNatives.getValue(project.name)

        from(zipTree(rootProject.tasks.shadowJar.get().outputs.files.singleFile))
        val nativesFromOtherArchs = nativeFolders
            .flatMap { (k,v) -> v.map { k to it } }
            .filterNot { (_,v) -> allowed.contains(v) }
            .map { (k,v) -> "$k/$v/**" }
            .toTypedArray()

        exclude(*nativesFromOtherArchs)
    }

    tasks.assemble {
        dependsOn(optimizedJar)
    }

    if (project.name.startsWith("win-")) {
        launch4j {
            outfile = "${rootProject.name}-${project.version}-${project.name}.exe"
            outputDir = "distributions"
            mainClassName = mainClass
            copyConfigurable = listOf<Any>()
            setJarTask(optimizedJar)
            stayAlive = true
            jreMinVersion = "21"
            priority = "high"
            productName = "AuxCable"
            jvmOptions.addAll(veryOptimizedJvmOptions.split(" "))
            version = project.version.toString()
            description = "An open-source, cross-platform, and lightweight Aux Cable for your Discord servers."
        }

        tasks.createExe {
            dependsOn(optimizedJar)
        }

        tasks.assemble {
            dependsOn(tasks.createExe)
        }
    }

    if (project.name in osSpecific) {
        val jpackageDir = project.layout.buildDirectory.dir("jpackage")

        val prepareJpackage: Copy by tasks.creating(Copy::class) {
            dependsOn(optimizedJar)
            from(optimizedJar)
            into(jpackageDir.get().dir("lib"))
        }

        val cleanJpackage: Delete by tasks.creating(Delete::class) {
            delete(jpackageDir.get().dir("out"))
        }

        val jpackage: Exec by tasks.creating(Exec::class) {
            dependsOn(prepareJpackage, cleanJpackage)
            val outputDir = jpackageDir.get().dir("out")
            val name = "AuxCable"
            extra.set("name", name)
            val resourceDir = rootProject.layout.projectDirectory.dir("jpackage-res")

            commandLine(
                "jpackage", "--verbose", "--type", "app-image", "--name", name,
                "--input", prepareJpackage.destinationDir.absolutePath,
                "--resource-dir", resourceDir.asFile.absolutePath,
                "--dest", outputDir.asFile.absolutePath,
                "--main-jar", optimizedJar.archiveFile.get().asFile.name,
                "--main-class", mainClass,
                "--jlink-options", jlinkOptions,
                "--java-options", veryOptimizedJvmOptions,
                "--add-modules", requiredModules.joinToString(",")
            )
            outputs.dir(outputDir)
        }

        // val postJpackage: Delete by tasks.creating(Delete::class) {
        //     dependsOn(jpackage)
        //     val name = jpackage.extra.get("name").toString()
        //     delete(jpackageDir.get().dir("out/$name/runtime/legal"))
        // }

        val distJpackage: Zip by tasks.creating(Zip::class) {
            dependsOn(jpackage)
            archiveBaseName = rootProject.name
            archiveVersion = project.version.toString()
            archiveClassifier = project.name
            archiveExtension = "zip"
            destinationDirectory = project.layout.buildDirectory.dir("distributions")
            from(jpackageDir.get().dir("out"))
        }

        tasks.assemble {
            dependsOn(distJpackage)
        }
    }
}

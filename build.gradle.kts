plugins {
    id("io.micronaut.application") version "4.5.0"
    id("com.gradleup.shadow") version "8.3.6"
    id("io.micronaut.aot") version "4.5.0"
    id("edu.sc.seis.launch4j") version "3.0.6"
}

allprojects {
    group = "net.notjustanna"
    version = "3.2"

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

    implementation("net.notjustanna.webview:webview_java:1.5.0+wv0.12.0-nightly.1")
    implementation("net.notjustanna.webview:webview_java-all-natives:1.5.0+wv0.12.0-nightly.1")
    implementation("net.notjustanna.webview:webview_java-interop-jackson:1.5.0+wv0.12.0-nightly.1")

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
    applicationDefaultJvmArgs = providers.gradleProperty("aux-cable.jvmOptions").get().split(" ")
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

val shadowJar = tasks.shadowJar.also { task ->
    task {
        mergeServiceFiles()
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude(File(projectDir, "shadowJar.exclude").readLines())
    }
}

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

val osSpecific = let {
    val osName = System.getProperty("os.name").lowercase()

    when {
        osName.contains("windows") -> listOf("win-x64")
        osName.contains("linux") -> listOf("linux-x64")
        osName.contains("mac") -> listOf("darwin")
        else -> emptyList()
    }
}

fun Zip.configureZipTask(project: Project, extension: String, destDir: String) {
    archiveBaseName = rootProject.name
    archiveVersion = project.version.toString()
    archiveClassifier = project.name
    archiveExtension = extension
    destinationDirectory = project.layout.buildDirectory.dir(destDir)
}

project(":packaging").subprojects {
    apply(plugin = "base")
    if (project.name.startsWith("win-")) {
        apply(plugin = "edu.sc.seis.launch4j")
    }

    val appJvmOptions = providers.gradleProperty("aux-cable.jvmOptions").get().let { def ->
        providers.gradleProperty("aux-cable.packaging.editions.${project.name}.jvmOptions")
            .map { "$it $def" }.getOrElse(def)
    }

    val optimizedJar: Zip by tasks.creating(Zip::class) {
        dependsOn(shadowJar)
        configureZipTask(project, "jar", "libs")

        val allowed = allowedNatives.getValue(project.name)

        from(zipTree(shadowJar.get().outputs.files.singleFile))
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
            mainClassName = rootProject.application.mainClass
            copyConfigurable = listOf<Any>()
            setJarTask(optimizedJar)
            stayAlive = true
            jreMinVersion = "21"
            priority = "high"
            productName = providers.gradleProperty("aux-cable.packaging.name").get()
            jvmOptions.addAll(appJvmOptions.split(" "))
            version = project.version.toString()
            description = providers.gradleProperty("aux-cable.packaging.description").get()
            icon = File(rootProject.projectDir, "jpackage-res/AuxCable.ico").absolutePath
        }

        tasks {
            createExe { dependsOn(optimizedJar) }
            assemble { dependsOn(createExe) }
        }
    }

    if (project.name in osSpecific) {
        val jpackageDir = project.layout.buildDirectory.dir("jpackage").get()
        val jpackageOut = jpackageDir.dir("out")

        tasks {
            val prepareJpackage: Copy by creating(Copy::class) {
                dependsOn(optimizedJar)
                from(optimizedJar)
                into(jpackageDir.dir("lib"))
            }

            val cleanJpackage: Delete by creating(Delete::class) {
                delete(jpackageOut)
            }

            val jpackage: Exec by creating(Exec::class) {
                dependsOn(prepareJpackage, cleanJpackage)

                commandLine(
                    "jpackage", "--verbose", "--type", "app-image", "--name", providers.gradleProperty("aux-cable.packaging.name").get(),
                    "--description", providers.gradleProperty("aux-cable.packaging.description").get(),
                    "--app-version", project.version.toString(),
                    "--input", prepareJpackage.destinationDir.absolutePath,
                    "--resource-dir", File(rootProject.projectDir, "jpackage-res").absolutePath,
                    "--dest", jpackageOut.asFile.absolutePath,
                    "--main-jar", optimizedJar.archiveFile.get().asFile.name,
                    "--main-class", rootProject.application.mainClass,
                    "--jlink-options", providers.gradleProperty("aux-cable.jlinkOptions").get(),
                    "--java-options", appJvmOptions,
                    "--add-modules", providers.gradleProperty("aux-cable.requiredModules").get()
                )
                outputs.dir(jpackageOut)
            }

            val distJpackage: Zip by creating(Zip::class) {
                dependsOn(jpackage)
                configureZipTask(project, "zip", "distributions")
                from(jpackageOut)
            }

            assemble { dependsOn(distJpackage) }
        }
    }
}

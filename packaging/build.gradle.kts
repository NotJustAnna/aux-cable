plugins {
    java
    id("com.gradleup.shadow") version "8.3.6"
    id("edu.sc.seis.launch4j") version "3.0.6"
}

val mainClass = "net.notjustanna.Application"

val veryOptimizedJvmOptions = "-XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1"

val jlinkOptions = "--strip-native-commands --strip-debug --no-header-files --no-man-pages --compress=zip-9"

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
    "win-x86" to listOf("win-x86", "win32-x86", "windows-x86"),
    "win-x64" to listOf("win-x86-64", "win32-x86-64", "windows-x86-64"),
    "linux-x64" to listOf("linux-x86-64", "linux-musl-x86-64")
)

val requiredModules = listOf("java.net.http","java.desktop","java.logging","java.naming","jdk.unsupported","java.sql","jdk.crypto.ec","jdk.zipfs")

subprojects {
    if (project.name.startsWith("win-")) {
        apply(plugin = "edu.sc.seis.launch4j")
    }

    val optimizedJar: Zip by tasks.creating(Zip::class) {
        archiveBaseName = rootProject.name
        archiveVersion = rootProject.version.toString()
        archiveAppendix = project.name
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

    if (project.name.startsWith("win-")) {
        launch4j {
            outfile = "${rootProject.name}-${project.name.split('-').last()}-${project.version}.exe"
            outputDir = "distributions"
            mainClassName = mainClass
            copyConfigurable = listOf<Any>()
            setJarTask(optimizedJar)
            stayAlive = true
            jreMinVersion = "21"
            priority = "high"
            productName = "AuxCable"
            jvmOptions.addAll(veryOptimizedJvmOptions.split(" "))
            version = rootProject.version.toString()
            description = "An open-source, cross-platform, and lightweight Aux Cable for your Discord servers."
        }
    }

    val prepareJpackage: Copy by tasks.creating(Copy::class) {
        dependsOn(optimizedJar)
        from(optimizedJar)
        into(project.layout.buildDirectory.dir("jpackage/lib"))
    }

    val jpackage: Exec by tasks.creating(Exec::class) {
        dependsOn(prepareJpackage)
        commandLine(
            "jpackage",
            "--type", "app-image",
            "--name", "AuxCable",
            "--input", prepareJpackage.destinationDir.absolutePath,
            "--dest", project.layout.buildDirectory.dir("jpackage/out").get().asFile.absolutePath,
            "--main-jar", optimizedJar.archiveFile.get().asFile.name,
            "--main-class", mainClass,
            "--jlink-options", jlinkOptions,
            "--java-options", veryOptimizedJvmOptions,
            "--add-modules", requiredModules.joinToString(",")
        )
    }

    val postJpackage: Delete by tasks.creating(Delete::class) {
        dependsOn(jpackage)
        delete(project.layout.buildDirectory.dir("jpackage/out/AuxCable/runtime/legal"))
    }

    val distJpackage: Zip by tasks.creating(Zip::class) {
        dependsOn(jpackage, postJpackage)
        archiveBaseName = rootProject.name
        archiveVersion = rootProject.version.toString()
        archiveAppendix = project.name
        archiveExtension = "zip"
        destinationDirectory = project.layout.buildDirectory.dir("distributions")
        from(project.layout.buildDirectory.dir("jpackage/out/AuxCable"))
    }
}

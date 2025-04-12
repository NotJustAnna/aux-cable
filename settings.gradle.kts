plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "aux-cable"

val supportedPackages = listOf("win-x86", "win-x64", "darwin", "linux-x64")
    .map { ":packaging:$it" }

include(":frontend", ":packaging", *supportedPackages.toTypedArray())

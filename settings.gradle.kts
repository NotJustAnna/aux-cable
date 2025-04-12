plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "aux-cable"

val supportedPackages = listOf("darwin", "linux-x64", "win-x64")
    .map { ":packaging:$it" }

include(":frontend", ":packaging", *supportedPackages.toTypedArray())

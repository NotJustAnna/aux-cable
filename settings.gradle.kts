plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "aux-cable"

include(
    ":frontend",
    ":packaging",
    *providers.gradleProperty("aux-cable.packaging.editions").get()
        .split(",").map { ":packaging:$it" }.toTypedArray()
)

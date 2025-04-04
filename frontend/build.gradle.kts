import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node") version "7.1.0"
}

/*
 * This is a convenience/integration buildscript to allow Gradle to understand how to build the frontend and
 * integrate it with the backend.
 */

node {
    download = true
    version = "22.14.0"
}

tasks {
    val assemble by getting

    val files by creating(NpmTask::class) {
        inputs.dir("src")
        inputs.dir("public")
        inputs.files(
            "package.json", "package-lock.json", "yarn.lock", "index.html", "tsconfig.json", "tsconfig.app.json",
            "tsconfig.node.json", "vite.config.ts"
        )
        outputs.dir(File(projectDir, "dist").absolutePath)
        npmCommand = listOf("run", "build")
    }

    val jar by creating(Zip::class) {
        archiveExtension = "jar"
        dependsOn(files)
        includeEmptyDirs = false
        from(files.outputs.files)
        into("net/notjustanna/auxcable/frontend")
    }

    assemble.dependsOn(jar)
    artifacts.add("default", jar)
}

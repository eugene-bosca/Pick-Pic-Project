// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false

    // Add the dependency for Dagger Hilt plugin
    id("com.google.dagger.hilt.android") version "2.53.1" apply false
}

tasks.register("addPreCommitGitHookOnBuild") {
    doLast {
        println("Running addPreCommitGitHookOnBuild")
        val osName = System.getProperty("os.name").lowercase()

        if (osName.contains("win")) {
            exec {
                commandLine("cmd", "/c", "copy", ".\\scripts\\pre-commit-win-wrapper.ps1", "..\\.git\\hooks\\pre-commit")
            }
        } else {
            exec {
                commandLine("cp", "./scripts/pre-commit-linux.sh", "../.git/hooks/pre-commit")
            }
        }
        println("Successfully added pre-commit git hook")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

tasks.register("addPreCommitGitHookOnBuild") {
    doLast {
        println("Running addPreCommitGitHookOnBuild")
        val osName = System.getProperty("os.name").lowercase()

        if (osName.contains("win")) {
            exec {
                commandLine("cmd", "/c", "copy", ".\\scripts\\pre-commit-win", ".\\.git\\hooks\\pre-commit")
            }
        } else {
            exec {
                commandLine("cp", "./scripts/pre-commit-linux", "./.git/hooks/pre-commit")
            }
        }

        println("Successfully added pre-commit git hook")
    }
}

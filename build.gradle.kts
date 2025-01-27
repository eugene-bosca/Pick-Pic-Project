// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

task("addPreCommitGitHookOnBuild") {
    println("Running addPreCommitHookScriptOnBuild")
    exec {
        commandLine("cp", "./scripts/pre-commit", "./.git/hooks")
    }
    println("Successfully added pre-commit git hook")
}

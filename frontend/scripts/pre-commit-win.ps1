#!/usr/bin/env pwsh

Write-Host "Pre-commit hook: running gradlew check"
frontend\gradlew check

if ($LASTEXITCODE -ne 0) {
    Write-Host "Checks failed, commit aborted."
    exit 1
}

Write-Host "Pre-commit hook: running gradlew lint"
frontend\gradlew lint

if ($LASTEXITCODE -ne 0) {
    Write-Host "Linting failed, commit aborted."
    exit 1
}

Write-Host "Pre-commit hook passed, proceeding with commit."
exit 0


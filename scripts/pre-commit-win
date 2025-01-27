Write-Host "Precommit hook: running gradlew check"
gradlew check

if ($LASTEXITCODE -ne 0) {
    Write-Host "Checks failed, commit aborted."
    exit 1
}

Write-Host "Precommit hook: running gradlew lint"
gradlew lint

if ($LASTEXITCODE -ne 0) {
    Write-Host "Linting failed, commit aborted."
    exit 1
}

Write-Host "Precommit hook passed, proceeding with commit."
exit 0

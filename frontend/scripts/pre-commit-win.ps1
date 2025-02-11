$prev = Get-Location
$gitRoot = git rev-parse --show-toplevel
$frontend = Join-Path $gitRoot 'frontend'

Write-Host "Running frontend checks."
Set-Location $frontend

Write-Host "Pre-commit hook: running gradlew check"
gradlew check

if ($LASTEXITCODE -ne 0) {
    Write-Host "Checks failed, commit aborted."
    exit 1
}

Write-Host "Pre-commit hook: running gradlew lint"
gradlew lint

if ($LASTEXITCODE -ne 0) {
    Write-Host "Linting failed, commit aborted."
    exit 1
}

Set-Location $prev

Write-Host "Pre-commit hook passed, proceeding with commit."
exit 0


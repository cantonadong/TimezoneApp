param(
    [Parameter(Mandatory = $true)][string]$Tag,
    [Parameter(Mandatory = $true)][string]$Apk,
    [Parameter(Mandatory = $true)][string]$Notes
)

$ErrorActionPreference = 'Stop'
. "$PSScriptRoot\github-env.ps1"

$projectRoot = Split-Path -Parent $PSScriptRoot
$gitDir = Join-Path $projectRoot '.local\git'
$apkPath = (Resolve-Path -LiteralPath $Apk).Path
$notesPath = (Resolve-Path -LiteralPath $Notes).Path

if (-not (Test-Path -LiteralPath $gitDir -PathType Container)) {
    throw "Local Git metadata is missing: $gitDir"
}

gh auth status
if ($LASTEXITCODE -ne 0) { throw 'GitHub authentication is unavailable.' }

$branch = git --git-dir=$gitDir --work-tree=$projectRoot branch --show-current
if ($LASTEXITCODE -ne 0 -or $branch -ne 'main') { throw 'The local branch must be main.' }

$changes = git --git-dir=$gitDir --work-tree=$projectRoot status --porcelain
if ($LASTEXITCODE -ne 0 -or $changes) { throw 'Commit all source changes before publishing.' }

git -c http.sslBackend=openssl --git-dir=$gitDir --work-tree=$projectRoot push -u origin main
if ($LASTEXITCODE -ne 0) { throw 'Pushing main failed.' }

gh release create $Tag $apkPath --repo cantonadong/TimezoneApp --target main --title "Timezone $($Tag.TrimStart('v'))" --notes-file $notesPath
if ($LASTEXITCODE -ne 0) { throw 'Creating the GitHub Release failed.' }

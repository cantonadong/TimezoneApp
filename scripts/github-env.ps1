$projectRoot = Split-Path -Parent $PSScriptRoot
$localRoot = Join-Path $projectRoot '.local'

$env:GH_CONFIG_DIR = Join-Path $localRoot 'gh'
$env:GIT_CONFIG_GLOBAL = Join-Path $localRoot 'gitconfig'

New-Item -ItemType Directory -Path $env:GH_CONFIG_DIR -Force | Out-Null

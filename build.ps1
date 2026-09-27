$ErrorActionPreference = 'Stop'
$projectRoot = $PSScriptRoot
$sdkRoot = 'D:\Android\sdk'
$jdkRoot = 'D:\Program\Java'
$buildTools = Join-Path $sdkRoot 'build-tools\36.0.0'
$androidJar = Join-Path $sdkRoot 'platforms\android-36\android.jar'
$env:JAVA_HOME = $jdkRoot
$env:PATH = "$jdkRoot\bin;$env:PATH"
Set-Location $projectRoot
$appBuild = [System.IO.Path]::GetFullPath((Join-Path $projectRoot 'build/app'))
$buildRoot = [System.IO.Path]::GetFullPath((Join-Path $projectRoot 'build'))
if (!$appBuild.StartsWith($buildRoot + [System.IO.Path]::DirectorySeparatorChar, [System.StringComparison]::OrdinalIgnoreCase)) { throw 'App build path is outside the build directory.' }
if (Test-Path -LiteralPath $appBuild) { Remove-Item -LiteralPath $appBuild -Recurse -Force }
New-Item -ItemType Directory -Force build,build/app,build/app/classes,build/app/dex,build/app/generated,build/app/assets,dist | Out-Null
function Run-Native([string]$Executable, [string[]]$Arguments) {
    & $Executable @Arguments
    if ($LASTEXITCODE -ne 0) { throw "Build command failed: $Executable ($LASTEXITCODE)" }
}
Run-Native "$buildTools/aapt2.exe" @('compile','--dir','app/res','-o','build/app/resources.zip')
Run-Native "$buildTools/aapt2.exe" @('link','-o','build/app/unsigned.apk','--manifest','app/AndroidManifest.xml','-I',$androidJar,'--java','build/app/generated','-A','build/app/assets','build/app/resources.zip')
$sources = @(Get-ChildItem 'app/src','build/app/generated' -Filter '*.java' -Recurse | ForEach-Object { $_.FullName })
Run-Native "$jdkRoot/bin/javac.exe" (@('-encoding','UTF-8','--release','8','-classpath',$androidJar,'-d','build/app/classes') + $sources)
$classFiles = @(Get-ChildItem 'build/app/classes' -Filter '*.class' -Recurse | ForEach-Object { $_.FullName })
Run-Native "$buildTools/d8.bat" (@('--lib',$androidJar,'--min-api','26','--output','build/app/dex') + $classFiles)
Push-Location build/app/dex
try { Run-Native "$buildTools/aapt.exe" @('add','../unsigned.apk','classes.dex') } finally { Pop-Location }
Run-Native "$buildTools/zipalign.exe" @('-f','4','build/app/unsigned.apk','build/app/aligned.apk')
if (!(Test-Path 'build/preview.keystore')) { throw 'Original signing key missing: build/preview.keystore. Restore it to preserve APK update compatibility.' }
Run-Native "$buildTools/apksigner.bat" @('sign','--ks','build/preview.keystore','--ks-key-alias','preview','--ks-pass','pass:android','--key-pass','pass:android','--out','dist/TimezoneApp.apk','build/app/aligned.apk')
Run-Native "$buildTools/apksigner.bat" @('verify','--verbose','dist/TimezoneApp.apk')
Write-Host "APK: $projectRoot\dist\TimezoneApp.apk"

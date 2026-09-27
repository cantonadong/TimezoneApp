$ErrorActionPreference = 'Stop'
$projectRoot = $PSScriptRoot
$sdkRoot = 'D:\Android\sdk'
$jdkRoot = 'D:\Program\Java'
$buildTools = Join-Path $sdkRoot 'build-tools\36.0.0'
$androidJar = Join-Path $sdkRoot 'platforms\android-36\android.jar'
$env:JAVA_HOME = $jdkRoot
$env:PATH = "$jdkRoot\bin;$env:PATH"
Set-Location $projectRoot
New-Item -ItemType Directory -Force build,build/classes,build/dex,dist | Out-Null
function Run-Native([string]$Executable, [string[]]$Arguments) {
    & $Executable @Arguments
    if ($LASTEXITCODE -ne 0) { throw "Build command failed: $Executable ($LASTEXITCODE)" }
}
Copy-Item -LiteralPath '2.png' -Destination 'preview/assets/city-reference.png' -Force
Run-Native "$jdkRoot/bin/javac.exe" @('-encoding','UTF-8','--release','8','-classpath',$androidJar,'-d','build/classes','preview/src/com/timezoneapp/preview/PreviewActivity.java')
$classFiles = @(Get-ChildItem 'build/classes' -Filter '*.class' -Recurse | ForEach-Object { $_.FullName })
Run-Native "$buildTools/d8.bat" (@('--lib',$androidJar,'--min-api','26','--output','build/dex') + $classFiles)
Run-Native "$buildTools/aapt.exe" @('package','-f','-M','preview/AndroidManifest.xml','-I',$androidJar,'-A','preview/assets','-F','build/unsigned.apk')
Push-Location build/dex
try { Run-Native "$buildTools/aapt.exe" @('add','../unsigned.apk','classes.dex') } finally { Pop-Location }
Run-Native "$buildTools/zipalign.exe" @('-f','4','build/unsigned.apk','build/aligned.apk')
if (!(Test-Path 'build/preview.keystore')) {
    Run-Native "$jdkRoot/bin/keytool.exe" @('-genkeypair','-keystore','build/preview.keystore','-storepass','android','-keypass','android','-alias','preview','-keyalg','RSA','-keysize','2048','-validity','10000','-dname','CN=Timezone UI Preview, O=Local Development, C=NZ')
}
Run-Native "$buildTools/apksigner.bat" @('sign','--ks','build/preview.keystore','--ks-key-alias','preview','--ks-pass','pass:android','--key-pass','pass:android','--out','dist/TimezoneApp.apk','build/aligned.apk')
Run-Native "$buildTools/apksigner.bat" @('verify','--verbose','dist/TimezoneApp.apk')
Write-Host "APK: $projectRoot\dist\TimezoneApp.apk"

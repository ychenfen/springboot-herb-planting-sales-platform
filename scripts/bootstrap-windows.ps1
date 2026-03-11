param(
    [ValidateSet("Init", "Start", "Backend", "Frontend")]
    [string]$Mode = "Start",
    [switch]$SkipDatabase,
    [switch]$SkipFrontendInstall,
    [switch]$SkipBackendBuild,
    [switch]$ForceDatabaseReset
)

$ErrorActionPreference = "Stop"

$script:RootDir = Split-Path -Parent $PSScriptRoot
$script:RuntimeDir = Join-Path $script:RootDir ".runtime"
$script:ToolsDir = Join-Path $script:RuntimeDir "tools"
$script:DownloadsDir = Join-Path $script:RuntimeDir "downloads"
$script:LogsDir = Join-Path $script:RuntimeDir "logs"
$script:StateDir = Join-Path $script:RuntimeDir "state"
$script:RunDir = Join-Path $script:RuntimeDir "run"
$script:BackendDir = Join-Path $script:RootDir "backend"
$script:FrontendDir = Join-Path $script:RootDir "frontend"
$script:SchemaFile = Join-Path $script:RootDir "database\schema.sql"
$script:DataFile = Join-Path $script:RootDir "database\data.sql"
$script:UpgradeFile = Join-Path $script:RootDir "database\update_20260311_feature_upgrade.sql"
$script:DbName = "herb_platform"
$script:DbHost = "127.0.0.1"
$script:DbPort = 13306
$script:DbUser = "herb_local"
$script:DbPassword = "herb123"
$script:MySqlVersion = "8.4.6"
$script:MySqlZipName = "mysql-8.4.6-winx64.zip"
$script:MySqlUrl = "https://cdn.mysql.com/Downloads/MySQL-8.4/$($script:MySqlZipName)"

function Write-Step([string]$Message) {
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Ensure-Dir([string]$Path) {
    if (-not (Test-Path $Path)) {
        New-Item -ItemType Directory -Force -Path $Path | Out-Null
    }
}

function Download-File([string]$Url, [string]$OutFile) {
    if (Test-Path $OutFile) {
        return
    }
    Ensure-Dir (Split-Path -Parent $OutFile)
    Write-Step "Downloading $(Split-Path $OutFile -Leaf)"
    Invoke-WebRequest -Uri $Url -OutFile $OutFile
}

function Expand-ZipIfNeeded([string]$ZipFile, [string]$Destination) {
    if ((Test-Path $Destination) -and (Get-ChildItem -Force $Destination | Measure-Object).Count -gt 0) {
        return
    }
    Ensure-Dir $Destination
    Write-Step "Extracting $(Split-Path $ZipFile -Leaf)"
    tar -xf $ZipFile -C $Destination
}

function Get-FirstDirectory([string]$ParentPath, [string]$Pattern = "*") {
    $dir = Get-ChildItem -Path $ParentPath -Directory | Where-Object { $_.Name -like $Pattern } |
        Sort-Object Name | Select-Object -First 1
    if (-not $dir) {
        throw "Directory not found under $ParentPath"
    }
    return $dir.FullName
}

function Ensure-VisualCppRuntime {
    $runtimeDll = Join-Path $env:WINDIR "System32\vcruntime140.dll"
    if (Test-Path $runtimeDll) {
        return
    }

    $installer = Join-Path $script:DownloadsDir "vc_redist.x64.exe"
    Download-File "https://aka.ms/vs/17/release/vc_redist.x64.exe" $installer

    Write-Step "Installing Visual C++ runtime"
    try {
        $proc = Start-Process -FilePath $installer -ArgumentList "/install", "/quiet", "/norestart" -PassThru -Wait
        if ($proc.ExitCode -ne 0) {
            throw "VC++ runtime installer exited with code $($proc.ExitCode)"
        }
    } catch {
        Write-Warning "VC++ runtime install failed. MySQL may require it on some Windows machines."
    }
}

function Ensure-Java {
    Ensure-Dir $script:ToolsDir
    $extractDir = Join-Path $script:ToolsDir "jdk"
    $zipFile = Join-Path $script:DownloadsDir "jdk11.zip"

    if (-not (Test-Path $extractDir)) {
        Ensure-Dir $extractDir
    }
    if ((Get-ChildItem -Path $extractDir -Directory -ErrorAction SilentlyContinue | Measure-Object).Count -eq 0) {
        Download-File "https://api.adoptium.net/v3/binary/latest/11/ga/windows/x64/jdk/hotspot/normal/eclipse" $zipFile
        Expand-ZipIfNeeded $zipFile $extractDir
    }

    $javaHome = Get-FirstDirectory $extractDir "jdk-*"
    return $javaHome
}

function Ensure-Node {
    Ensure-Dir $script:ToolsDir
    $extractDir = Join-Path $script:ToolsDir "node"
    if ((Get-ChildItem -Path $extractDir -Directory -ErrorAction SilentlyContinue | Measure-Object).Count -eq 0) {
        Ensure-Dir $extractDir
        Write-Step "Resolving latest Node.js LTS"
        $nodeIndex = Invoke-RestMethod "https://nodejs.org/dist/index.json"
        $release = $nodeIndex |
            Where-Object { $_.lts -and $_.files -contains "win-x64-zip" } |
            Select-Object -First 1
        if (-not $release) {
            throw "Unable to resolve Node.js LTS release."
        }
        $zipName = "node-$($release.version)-win-x64.zip"
        $zipFile = Join-Path $script:DownloadsDir $zipName
        $zipUrl = "https://nodejs.org/dist/$($release.version)/$zipName"
        Download-File $zipUrl $zipFile
        Expand-ZipIfNeeded $zipFile $extractDir
    }

    $nodeHome = Get-FirstDirectory $extractDir "node-*"
    return $nodeHome
}

function Get-MySqlPaths {
    $baseDir = Get-FirstDirectory (Join-Path $script:ToolsDir "mysql") "mysql-*"
    $dataDir = Join-Path $script:RuntimeDir "data\mysql"
    $myIni = Join-Path $script:RunDir "mysql-local.ini"
    $errorLog = Join-Path $script:LogsDir "mysql-error.log"
    $initSql = Join-Path $script:RunDir "mysql-bootstrap.sql"

    return @{
        BaseDir = $baseDir
        DataDir = $dataDir
        MyIni = $myIni
        InitSql = $initSql
        MysqldExe = Join-Path $baseDir "bin\mysqld.exe"
        MysqlExe = Join-Path $baseDir "bin\mysql.exe"
        MysqlAdminExe = Join-Path $baseDir "bin\mysqladmin.exe"
        ErrorLog = $errorLog
    }
}

function Ensure-MySqlArchive {
    Ensure-Dir $script:ToolsDir
    $extractDir = Join-Path $script:ToolsDir "mysql"
    if ((Get-ChildItem -Path $extractDir -Directory -ErrorAction SilentlyContinue | Measure-Object).Count -gt 0) {
        return
    }

    Ensure-Dir $extractDir
    Ensure-VisualCppRuntime

    $zipFile = Join-Path $script:DownloadsDir $script:MySqlZipName
    Download-File $script:MySqlUrl $zipFile
    Expand-ZipIfNeeded $zipFile $extractDir
}

function Write-MySqlConfig($paths) {
    Ensure-Dir (Split-Path -Parent $paths.MyIni)
    Ensure-Dir $paths.DataDir
    Ensure-Dir (Split-Path -Parent $paths.ErrorLog)

    $config = @"
[mysqld]
basedir=$($paths.BaseDir.Replace('\', '/'))
datadir=$($paths.DataDir.Replace('\', '/'))
port=$($script:DbPort)
bind-address=127.0.0.1
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
default-time-zone=+08:00
mysqlx=0
skip-name-resolve
log-error=$($paths.ErrorLog.Replace('\', '/'))
"@
    Set-Content -Path $paths.MyIni -Value $config -Encoding ASCII
}

function Write-MySqlBootstrapScript($paths) {
    $bootstrapSql = @"
CREATE USER IF NOT EXISTS '$($script:DbUser)'@'127.0.0.1' IDENTIFIED BY '$($script:DbPassword)';
ALTER USER '$($script:DbUser)'@'127.0.0.1' IDENTIFIED BY '$($script:DbPassword)';
GRANT ALL PRIVILEGES ON *.* TO '$($script:DbUser)'@'127.0.0.1' WITH GRANT OPTION;
CREATE USER IF NOT EXISTS '$($script:DbUser)'@'localhost' IDENTIFIED BY '$($script:DbPassword)';
ALTER USER '$($script:DbUser)'@'localhost' IDENTIFIED BY '$($script:DbPassword)';
GRANT ALL PRIVILEGES ON *.* TO '$($script:DbUser)'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;
"@
    Set-Content -Path $paths.InitSql -Value $bootstrapSql -Encoding ASCII
}

function Test-MySqlReady($paths) {
    try {
        $oldPwd = $env:MYSQL_PWD
        $env:MYSQL_PWD = $script:DbPassword
        $result = & $paths.MysqlExe --protocol=tcp -N -s -h $script:DbHost -P $script:DbPort `
            -u $script:DbUser -e "SELECT 1;" 2>$null
        if ($LASTEXITCODE -ne 0) {
            return $false
        }
        $first = ($result | Select-Object -First 1)
        return ($first -ne $null) -and ($first.ToString().Trim() -eq "1")
    } catch {
        return $false
    } finally {
        $env:MYSQL_PWD = $oldPwd
    }
}

function Test-TcpPortOpen([string]$HostName, [int]$Port) {
    try {
        $client = New-Object System.Net.Sockets.TcpClient
        $async = $client.BeginConnect($HostName, $Port, $null, $null)
        $wait = $async.AsyncWaitHandle.WaitOne(1000, $false)
        if (-not $wait) {
            $client.Close()
            return $false
        }
        $client.EndConnect($async)
        $client.Close()
        return $true
    } catch {
        return $false
    }
}

function Wait-MySqlReady($paths, [int]$TimeoutSeconds = 180) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Test-MySqlReady $paths) {
            return
        }
        Start-Sleep -Seconds 2
    }
    throw "MySQL did not become ready. Check $($paths.ErrorLog)"
}

function Wait-TcpPortOpen([string]$HostName, [int]$Port, [int]$TimeoutSeconds = 120) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Test-TcpPortOpen $HostName $Port) {
            return
        }
        Start-Sleep -Seconds 2
    }
    throw "TCP port $HostName`:$Port did not open in time."
}

function Initialize-MySqlDataDir($paths) {
    $systemDbDir = Join-Path $paths.DataDir "mysql"
    if (Test-Path $systemDbDir) {
        return
    }

    Write-Step "Initializing local MySQL data directory"
    Ensure-Dir $paths.DataDir
    & $paths.MysqldExe "--defaults-file=$($paths.MyIni)" "--initialize-insecure" "--console"
    if ($LASTEXITCODE -ne 0) {
        throw "mysqld --initialize-insecure failed."
    }
}

function Start-MySqlServer($paths) {
    if (Test-MySqlReady $paths) {
        return
    }

    Get-Process mysqld -ErrorAction SilentlyContinue |
        Where-Object { $_.Path -like "$($paths.BaseDir)*" } |
        Stop-Process -Force -ErrorAction SilentlyContinue

    Start-Sleep -Seconds 2

    Write-Step "Starting local MySQL on port $($script:DbPort)"
    Start-Process -FilePath $paths.MysqldExe `
        -ArgumentList "--defaults-file=$($paths.MyIni)", "--init-file=$($paths.InitSql)", "--console" `
        -WorkingDirectory $paths.BaseDir `
        -WindowStyle Hidden | Out-Null

    Wait-TcpPortOpen $script:DbHost $script:DbPort
    Wait-MySqlReady $paths
}

function Invoke-MySqlScript($paths, [string]$ScriptPath) {
    $oldPwd = $env:MYSQL_PWD
    try {
        $env:MYSQL_PWD = $script:DbPassword
        $normalizedPath = $ScriptPath.Replace("\", "/")
        & $paths.MysqlExe --protocol=tcp -h $script:DbHost -P $script:DbPort -u $script:DbUser `
            -e "SOURCE $normalizedPath" 2>$null
        if ($LASTEXITCODE -ne 0) {
            throw "Failed to import $ScriptPath"
        }
    } finally {
        $env:MYSQL_PWD = $oldPwd
    }
}

function Get-MySqlScalar($paths, [string]$Query) {
    $oldPwd = $env:MYSQL_PWD
    try {
        $env:MYSQL_PWD = $script:DbPassword
        $result = & $paths.MysqlExe --protocol=tcp -N -s -h $script:DbHost -P $script:DbPort `
            -u $script:DbUser -e $Query 2>$null
        if ($LASTEXITCODE -ne 0) {
            return $null
        }
        $first = ($result | Select-Object -First 1)
        if ($first -eq $null) {
            return $null
        }
        return $first.ToString().Trim()
    } finally {
        $env:MYSQL_PWD = $oldPwd
    }
}

function Ensure-Database($paths) {
    if ($ForceDatabaseReset) {
        throw "ForceDatabaseReset is not supported while MySQL may already be running. Delete .runtime\\data\\mysql manually if you need a full reset."
    }

    $schemaExists = Get-MySqlScalar $paths "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='$($script:DbName)';"
    if (-not $schemaExists) {
        Write-Step "Importing fresh database schema and seed data"
        Invoke-MySqlScript $paths $script:SchemaFile
        Invoke-MySqlScript $paths $script:DataFile
        return
    }

    $userCount = Get-MySqlScalar $paths "SELECT COUNT(*) FROM $($script:DbName).sys_user;"
    if ($userCount -eq "0") {
        Write-Step "Importing seed data into existing local schema"
        Invoke-MySqlScript $paths $script:DataFile
        return
    }

    $knowledgeTableExists = Get-MySqlScalar $paths @"
SELECT COUNT(*)
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = '$($script:DbName)'
  AND TABLE_NAME = 'herb_knowledge';
"@

    if ($knowledgeTableExists -eq "0" -and (Test-Path $script:UpgradeFile)) {
        Write-Step "Upgrading local database to current schema"
        Invoke-MySqlScript $paths $script:UpgradeFile
    }
}

function Ensure-FrontendEnvFile {
    $envLocal = Join-Path $script:FrontendDir ".env.local"
    $envExample = Join-Path $script:FrontendDir ".env.example"
    if ((-not (Test-Path $envLocal)) -and (Test-Path $envExample)) {
        Copy-Item $envExample $envLocal
    }
}

function Ensure-FrontendDependencies($nodeHome) {
    if ($SkipFrontendInstall) {
        return
    }

    $nodeModules = Join-Path $script:FrontendDir "node_modules"
    if (Test-Path $nodeModules) {
        return
    }

    Write-Step "Installing frontend dependencies"
    Push-Location $script:FrontendDir
    try {
        & (Join-Path $nodeHome "npm.cmd") install
        if ($LASTEXITCODE -ne 0) {
            throw "npm install failed."
        }
    } finally {
        Pop-Location
    }
}

function Build-Backend($javaHome) {
    if ($SkipBackendBuild) {
        return
    }

    Write-Step "Building backend"
    $oldJavaHome = $env:JAVA_HOME
    $oldPath = $env:Path
    try {
        $env:JAVA_HOME = $javaHome
        $env:Path = "$javaHome\bin;$oldPath"
        Push-Location $script:BackendDir
        & (Join-Path $script:BackendDir "mvnw.cmd") clean package -DskipTests
        if ($LASTEXITCODE -ne 0) {
            throw "Backend package failed."
        }
    } finally {
        Pop-Location
        $env:JAVA_HOME = $oldJavaHome
        $env:Path = $oldPath
    }
}

function Start-BackendWindow {
    Write-Step "Starting backend window"
    Start-Process -FilePath "powershell.exe" `
        -WorkingDirectory $script:RootDir `
        -ArgumentList @(
            "-NoExit",
            "-ExecutionPolicy", "Bypass",
            "-File", (Join-Path $PSScriptRoot "bootstrap-windows.ps1"),
            "-Mode", "Backend"
        ) | Out-Null
}

function Start-FrontendWindow {
    Write-Step "Starting frontend window"
    Start-Process -FilePath "powershell.exe" `
        -WorkingDirectory $script:RootDir `
        -ArgumentList @(
            "-NoExit",
            "-ExecutionPolicy", "Bypass",
            "-File", (Join-Path $PSScriptRoot "bootstrap-windows.ps1"),
            "-Mode", "Frontend"
        ) | Out-Null
}

function Show-Summary {
    Write-Host ""
    Write-Host "Local MySQL : $($script:DbHost):$($script:DbPort)" -ForegroundColor Green
    Write-Host "Frontend    : http://localhost:5173" -ForegroundColor Green
    Write-Host "Backend API : http://localhost:8080/api" -ForegroundColor Green
    Write-Host "Docs        : http://localhost:8080/api/doc.html" -ForegroundColor Green
    Write-Host ""
    Write-Host "Demo accounts:" -ForegroundColor Green
    Write-Host "admin     / admin123"
    Write-Host "farmer001 / admin123"
    Write-Host "buyer001  / admin123"
    Write-Host "user001   / admin123"
}

function Invoke-InitFlow {
    Ensure-Dir $script:RuntimeDir
    Ensure-Dir $script:DownloadsDir
    Ensure-Dir $script:LogsDir
    Ensure-Dir $script:StateDir
    Ensure-Dir $script:RunDir

    $javaHome = Ensure-Java
    $nodeHome = Ensure-Node
    Ensure-FrontendEnvFile

    if (-not $SkipDatabase) {
        Ensure-MySqlArchive
        $paths = Get-MySqlPaths
        Write-MySqlConfig $paths
        Write-MySqlBootstrapScript $paths
        Initialize-MySqlDataDir $paths
        Start-MySqlServer $paths
        Ensure-Database $paths
    }

    Ensure-FrontendDependencies $nodeHome
    Build-Backend $javaHome

    return @{
        JavaHome = $javaHome
        NodeHome = $nodeHome
    }
}

switch ($Mode) {
    "Init" {
        Write-Step "Initialize local Windows runtime"
        Invoke-InitFlow | Out-Null
        Show-Summary
        break
    }
    "Start" {
        Write-Step "Prepare local runtime and services"
        Invoke-InitFlow | Out-Null
        Start-BackendWindow
        Start-Sleep -Seconds 10
        Start-FrontendWindow
        Show-Summary
        break
    }
    "Backend" {
        $javaHome = Ensure-Java
        $jarFile = Join-Path $script:BackendDir "target\herb-platform-1.0.0.jar"
        if (-not (Test-Path $jarFile)) {
            Build-Backend $javaHome
        }

        $env:JAVA_HOME = $javaHome
        $env:Path = "$javaHome\bin;$env:Path"
        $env:DB_HOST = $script:DbHost
        $env:DB_PORT = "$($script:DbPort)"
        $env:DB_NAME = $script:DbName
        $env:DB_USERNAME = $script:DbUser
        $env:DB_PASSWORD = $script:DbPassword

        Write-Host "Backend starting with local DB $($script:DbHost):$($script:DbPort)" -ForegroundColor Yellow
        & (Join-Path $javaHome "bin\java.exe") "-Dfile.encoding=UTF-8" "-jar" $jarFile
        break
    }
    "Frontend" {
        $nodeHome = Ensure-Node
        Ensure-FrontendEnvFile
        $env:Path = "$nodeHome;$env:Path"
        Push-Location $script:FrontendDir
        try {
            & (Join-Path $nodeHome "npm.cmd") run dev
        } finally {
            Pop-Location
        }
        break
    }
}

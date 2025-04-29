# 设置输出颜色函数
function Write-ColorOutput($ForegroundColor) {
    $fc = $host.UI.RawUI.ForegroundColor
    $host.UI.RawUI.ForegroundColor = $ForegroundColor
    if ($args) {
        Write-Output $args
    }
    $host.UI.RawUI.ForegroundColor = $fc
}

# 添加依赖检查
function Check-Dependencies {
    Write-ColorOutput Yellow "检查必要的依赖..."
    
    # 检查 Docker
    if (-not (Get-Command "docker" -ErrorAction SilentlyContinue)) {
        Write-ColorOutput Red "Docker未安装，请先安装Docker"
        exit 1
    }
    
    # 检查 Maven
    if (-not (Get-Command "mvn" -ErrorAction SilentlyContinue)) {
        Write-ColorOutput Red "Maven未安装，请先安装Maven"
        exit 1
    }
    
    # 检查 Node.js
    if (-not (Get-Command "node" -ErrorAction SilentlyContinue)) {
        Write-ColorOutput Red "Node.js未安装，请先安装Node.js"
        exit 1
    }
    
    # 检查 Yarn
    if (-not (Get-Command "yarn" -ErrorAction SilentlyContinue)) {
        Write-ColorOutput Red "Yarn未安装，请先安装Yarn"
        exit 1
    }
    
    Write-ColorOutput Green "所有依赖检查完成！"
}

# 在主程序开始前调用依赖检查
Check-Dependencies

# 检查Docker是否运行
Write-ColorOutput Green "正在检查Docker状态..."
$dockerStatus = docker info 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-ColorOutput Red "Docker未运行，正在启动Docker Desktop..."
    Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    Write-ColorOutput Yellow "等待Docker启动完成（约30秒）..."
    Start-Sleep -Seconds 30
}
Write-ColorOutput Green "Docker已就绪！"

# 检查并启动必要的Docker容器
Write-ColorOutput Green "正在检查并启动必要的Docker容器..."
$projectRoot = Split-Path -Parent $PSScriptRoot
docker-compose -f "$projectRoot\src\main\docker\docker-compose.yml" up -d
if ($LASTEXITCODE -ne 0) {
    Write-ColorOutput Red "Docker容器启动失败，请检查docker-compose配置"
    exit 1
}
Write-ColorOutput Green "Docker容器已启动！"

# 启动后端服务
Write-ColorOutput Green "正在启动后端服务..."
Set-Location $projectRoot
Start-Process "cmd.exe" "/c mvn spring-boot:run" -NoNewWindow
Write-ColorOutput Yellow "等待后端服务启动（约20秒）..."
Start-Sleep -Seconds 20
Write-ColorOutput Green "后端服务已启动！"

# 启动前端服务
Write-ColorOutput Green "正在启动前端服务..."
Set-Location "$projectRoot\frontend"
# 检查是否需要安装依赖
if (-not (Test-Path "node_modules")) {
    Write-ColorOutput Yellow "正在安装前端依赖..."
    yarn install
    if ($LASTEXITCODE -ne 0) {
        Write-ColorOutput Red "前端依赖安装失败"
        exit 1
    }
}
# 启动前端开发服务器
Start-Process "cmd.exe" "/c yarn dev" -NoNewWindow
Write-ColorOutput Green "前端服务已启动！"

# 输出访问信息
Write-ColorOutput Cyan "================================================"
Write-ColorOutput Cyan "所有服务已启动完成！"
Write-ColorOutput Cyan "前端访问地址: http://localhost:5173"
Write-ColorOutput Cyan "后端接口地址: http://localhost:8080"
Write-ColorOutput Cyan "================================================"

# 等待用户输入以保持窗口打开
Write-ColorOutput Yellow "按任意键关闭所有服务..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# 关闭服务
Write-ColorOutput Yellow "正在关闭所有服务..."
# 先关闭应用服务
Get-Process | Where-Object { $_.ProcessName -match 'java|node' } | Stop-Process -Force
# 再关闭 Docker 容器
docker-compose -f "$projectRoot\src\main\docker\docker-compose.yml" down
Write-ColorOutput Green "所有服务已关闭！"
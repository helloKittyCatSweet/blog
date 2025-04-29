#!/bin/bash

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 输出带颜色的文本
print_color() {
    color=$1
    message=$2
    echo -e "${color}${message}${NC}"
}

# 检查必要的服务是否安装
check_dependencies() {
    print_color "$YELLOW" "检查必要的依赖..."
    
    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        print_color "$RED" "Docker未安装，请先安装Docker"
        exit 1
    fi
    
    # 检查 Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_color "$RED" "Docker Compose未安装，请先安装Docker Compose"
        exit 1
    fi
    
    # 检查 Maven
    if ! command -v mvn &> /dev/null; then
        print_color "$RED" "Maven未安装，请先安装Maven"
        exit 1
    fi
    
    # 检查 Node.js
    if ! command -v node &> /dev/null; then
        print_color "$RED" "Node.js未安装，请先安装Node.js"
        exit 1
    fi
    
    # 检查 yarn
    if ! command -v yarn &> /dev/null; then
        print_color "$RED" "Yarn未安装，请先安装Yarn"
        exit 1
    fi
    
    print_color "$GREEN" "所有依赖检查完成！"
}

# 启动Docker服务
start_docker() {
    print_color "$GREEN" "正在检查Docker服务状态..."
    if ! systemctl is-active --quiet docker; then
        print_color "$YELLOW" "Docker服务未启动，正在启动..."
        sudo systemctl start docker
        sleep 5
    fi
    print_color "$GREEN" "Docker服务已就绪！"
}

# 启动Docker容器
start_containers() {
    print_color "$GREEN" "正在启动Docker容器..."
    # 使用脚本所在目录的父目录作为项目根目录
    local project_root="$(dirname "$(dirname "$(readlink -f "$0")")")"
    docker-compose -f "${project_root}/src/main/docker/docker-compose.yml" up -d
    if [ $? -ne 0 ]; then
        print_color "$RED" "Docker容器启动失败，请检查docker-compose配置"
        exit 1
    fi
    print_color "$GREEN" "Docker容器已启动！"
}

# 启动后端服务
start_backend() {
    print_color "$GREEN" "正在启动后端服务..."
    cd ~/blog
    nohup mvn spring-boot:run > backend.log 2>&1 &
    backend_pid=$!
    print_color "$YELLOW" "等待后端服务启动（约20秒）..."
    sleep 20
    if ps -p $backend_pid > /dev/null; then
        print_color "$GREEN" "后端服务已启动！"
    else
        print_color "$RED" "后端服务启动失败，请检查backend.log文件"
        exit 1
    fi
}

# 启动前端服务
start_frontend() {
    print_color "$GREEN" "正在启动前端服务..."
    # 使用脚本所在目录的父目录作为项目根目录
    local project_root="$(dirname "$(dirname "$(readlink -f "$0")")")"
    cd "${project_root}/frontend"
    
    # 检查是否需要安装依赖
    if [ ! -d "node_modules" ]; then
        print_color "$YELLOW" "正在安装前端依赖..."
        yarn install
        if [ $? -ne 0 ]; then
            print_color "$RED" "前端依赖安装失败"
            exit 1
        fi
    fi
    
    # 启动前端开发服务器，指定 host 为 0.0.0.0
    nohup yarn dev --host 0.0.0.0 > frontend.log 2>&1 &
    frontend_pid=$!
    sleep 5
    if ps -p $frontend_pid > /dev/null; then
        print_color "$GREEN" "前端服务已启动！"
    else
        print_color "$RED" "前端服务启动失败，请检查frontend.log文件"
        exit 1
    fi
}

# 清理函数
cleanup() {
    print_color "$YELLOW" "正在关闭所有服务..."
    
    # 关闭前端服务
    pkill -f "node.*dev" || true
    
    # 关闭后端服务
    pkill -f "spring-boot:run" || true
    
    # 关闭Docker容器
    local project_root="$(dirname "$(dirname "$(readlink -f "$0")")")"
    docker-compose -f "${project_root}/src/main/docker/docker-compose.yml" down || true
    
    print_color "$GREEN" "所有服务已关闭！"
    exit 0
}

# 设置清理钩子
trap cleanup SIGINT SIGTERM

# 主程序
main() {
    # 检查依赖
    check_dependencies
    
    # 启动所有服务
    start_docker
    start_containers
    start_backend
    start_frontend
    
    # 输出访问信息
    print_color "$CYAN" "================================================"
    print_color "$CYAN" "所有服务已启动完成！"
    print_color "$CYAN" "前端访问地址: http://localhost:5173"
    print_color "$CYAN" "后端接口地址: http://localhost:8080"
    print_color "$CYAN" "================================================"
    print_color "$YELLOW" "按 Ctrl+C 关闭所有服务..."
    
    # 保持脚本运行
    while true; do
        sleep 1
    done
}

# 执行主程序
main 
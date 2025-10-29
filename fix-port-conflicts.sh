#!/bin/bash

# Port Conflict Resolution Script
# Helps resolve port conflicts before deployment

echo "🔍 Power Bank System - Port Conflict Resolution"
echo "=============================================="

# Our required ports
REQUIRED_PORTS=(3307 6380 10030 8084 8083 5672 15672)

echo ""
echo "📋 Checking required ports..."

conflicts_found=false
conflicted_ports=()

for port in "${REQUIRED_PORTS[@]}"; do
    if netstat -tuln 2>/dev/null | grep -q ":$port "; then
        echo "   ❌ Port $port is in use"
        conflicts_found=true
        conflicted_ports+=($port)
    else
        echo "   ✅ Port $port is available"
    fi
done

if [ "$conflicts_found" = false ]; then
    echo ""
    echo "✅ No port conflicts detected! You're ready to deploy."
    exit 0
fi

echo ""
echo "⚠️  Port conflicts detected on: ${conflicted_ports[*]}"
echo ""

# Check if conflicts are from our own containers
echo "🔍 Checking if conflicts are from our containers..."
our_containers=$(docker compose ps --quiet 2>/dev/null)

if [ -n "$our_containers" ]; then
    echo "   Found our containers running"
    echo ""
    echo "🛠️  Resolution options:"
    echo "   1. Stop our containers (recommended)"
    echo "   2. Show detailed port usage"
    echo "   3. Exit"
    echo ""
    echo -n "Choose option (1/2/3): "
    read -r choice
    
    case $choice in
        1)
            echo ""
            echo "🛑 Stopping our containers..."
            docker compose down
            echo "✅ Containers stopped"
            
            # Recheck ports
            echo ""
            echo "🔍 Rechecking ports..."
            all_clear=true
            for port in "${conflicted_ports[@]}"; do
                if netstat -tuln 2>/dev/null | grep -q ":$port "; then
                    echo "   ❌ Port $port still in use (external process)"
                    all_clear=false
                else
                    echo "   ✅ Port $port now available"
                fi
            done
            
            if [ "$all_clear" = true ]; then
                echo ""
                echo "✅ All port conflicts resolved! You can now deploy."
            else
                echo ""
                echo "⚠️  Some ports still in use by external processes"
                echo "   You may need to stop those processes manually"
            fi
            ;;
        2)
            echo ""
            echo "📊 Detailed port usage:"
            for port in "${conflicted_ports[@]}"; do
                echo ""
                echo "Port $port:"
                netstat -tulpn 2>/dev/null | grep ":$port " || echo "   No detailed info available"
            done
            ;;
        3|*)
            echo "Exiting..."
            exit 1
            ;;
    esac
else
    echo "   No containers from our project found"
    echo ""
    echo "📊 Detailed port usage:"
    for port in "${conflicted_ports[@]}"; do
        echo ""
        echo "Port $port:"
        netstat -tulpn 2>/dev/null | grep ":$port " || echo "   No detailed info available"
    done
    
    echo ""
    echo "💡 You may need to manually stop the processes using these ports"
    echo "   Or check if they are from other Docker containers"
fi

echo ""
echo "📝 Useful commands:"
echo "   - Check all Docker containers: docker ps -a"
echo "   - Stop all containers: docker stop \$(docker ps -q)"
echo "   - Check port usage: netstat -tulpn | grep :<port>"
echo "   - Kill process on port: sudo kill -9 \$(sudo lsof -t -i:<port>)"
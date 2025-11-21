#!/bin/bash

echo "🔍 Pre-Deployment Verification"
echo "================================"

# Check if new files exist
echo ""
echo "📁 Checking new files..."
FILES=(
    "device-util-demo/brezze-communication/src/main/java/com/brezze/share/communication/config/MqttConfig.java"
    "device-util-demo/brezze-communication/src/main/java/com/brezze/share/communication/service/MqttService.java"
    "device-util-demo/brezze-communication/src/main/java/com/brezze/share/communication/listener/MqttMessageListener.java"
)

for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "✅ $file"
    else
        echo "❌ $file - MISSING!"
        exit 1
    fi
done

# Check if YbtServiceImpl was modified
echo ""
echo "📝 Checking YbtServiceImpl modifications..."
if grep -q "mqttService.publish" "device-util-demo/brezze-communication/src/main/java/com/brezze/share/communication/cabinet/service/impl/YbtServiceImpl.java"; then
    echo "✅ YbtServiceImpl uses mqttService"
else
    echo "❌ YbtServiceImpl still uses IotUtil!"
    exit 1
fi

# Check environment variables
echo ""
echo "⚙️  Checking environment configuration..."
if grep -q "MQTT_BROKER=ub0bc614" ".env.production"; then
    echo "✅ MQTT_BROKER configured"
else
    echo "❌ MQTT_BROKER not configured!"
    exit 1
fi

if grep -q "YBT_ENABLE=false" ".env.production"; then
    echo "✅ YBT_ENABLE=false (correct)"
else
    echo "⚠️  YBT_ENABLE should be false"
fi

echo ""
echo "✅ All checks passed! Ready to deploy."
echo ""
echo "Run: ./deploy-mqtt-fix.sh"

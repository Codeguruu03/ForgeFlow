@echo off
echo ========================================================
echo Launching ForgeFlow Enterprise Platform v1.0
echo ========================================================
mvn compile exec:java -Dexec.mainClass="com.forgeflow.ForgeFlowApplication"

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "Launching ForgeFlow Enterprise Platform v1.0" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan

mvn compile exec:java -Dexec.mainClass="com.forgeflow.ForgeFlowApplication"

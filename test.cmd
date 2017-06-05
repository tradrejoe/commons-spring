@echo off
setlocal
REM java -cp "C:/projects/commons-spring/target/commons-spring-1.0.11.jar" org.lc.js.Beautifier "C:/projects/fusioncharts/js/fusioncharts.js"
java -cp "C:/projects/commons-spring/target/commons-spring-1.0.11.jar" org.lc.js.Beautifier "C:/projects/fusioncharts/js/fusioncharts.powercharts.min.js" "C:/projects/fusioncharts/js/fusioncharts.powercharts.js"
endlocal
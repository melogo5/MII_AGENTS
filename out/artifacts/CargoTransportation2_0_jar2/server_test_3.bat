echo "Launching Server with a DispatcherAgent"
echo "* * * * * * * * * * * * * * *"
echo "Stations = 7"
echo "* * * * * * * * * * * * * * *"
echo "Attainability Matrix ="
echo "0-6-5-0-0-2-0"
echo "6-0-4-0-0-0-8"
echo "5-4-0-8-0-0-6"
echo "0-0-8-0-4-5-0"
echo "0-0-0-4-0-3-4"
echo "2-0-0-5-3-0-0"
echo "0-8-6-0-4-0-0"
cd %CD%
java -cp CargoTransportation2.0.jar jade.Boot -gui -port 1099 ^
Disp:com.company.DispatcherAgent("7","St1","St2","St3","St4","St5","St6","St7","0-6-5-0-0-2-0","6-0-4-0-0-0-8","5-4-0-8-0-0-6","0-0-8-0-4-5-0","0-0-0-4-0-3-4","2-0-0-5-3-0-0","0-8-6-0-4-0-0");^
pause
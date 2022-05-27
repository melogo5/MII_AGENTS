echo "Launching Server with a DispatcherAgent"
echo "* * * * * * * * * * * * * * *"
echo "Stations = 3"
echo "* * * * * * * * * * * * * * *"
echo "Attainability Matrix ="
echo "0-5-3"
echo "5-0-0"
echo "3-0-0"
echo "* * * * * * * * * * * * * * *"
cd %CD%
java -cp CargoTransportation2.0.jar jade.Boot -gui -port 1099 ^
Disp:com.company.DispatcherAgent("3","St1","St2","St3","0-5-3","5-0-0","3-0-0");^
pause
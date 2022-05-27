echo "Launching Server with a DispatcherAgent"
echo "* * * * * * * * * * * * * * *"
echo "Stations = 5"
echo "* * * * * * * * * * * * * * *"
echo "Attainability Matrix ="
echo "0-6-0-0-0"
echo "6-0-2-4-8"
echo "0-2-0-0-0"
echo "0-4-0-0-3"
echo "0-8-0-3-0"
echo "* * * * * * * * * * * * * * *"
cd %CD%
java -cp CargoTransportation2.0.jar jade.Boot -gui -port 1099 ^
Disp:com.company.DispatcherAgent("5","St1","St2","St3","St4","St5","0-6-0-0-0","6-0-2-4-8","0-2-0-0-0","0-4-0-0-3","0-8-0-3-0");^
pause
echo "Launching Server with a DispatcherAgent"
echo "* * * * * * * * * * * * * * *"
echo "Stations = 4"
echo "* * * * * * * * * * * * * * *"
echo "Attainability Matrix ="
echo "0-2-0-0"
echo "2-0-5-6"
echo "0-5-0-0"
echo "0-6-0-0"
echo "* * * * * * * * * * * * * * *"
cd %CD%
java -cp CargoTransportation2.0.jar jade.Boot -gui -port 1099 ^
Disp:com.company.DispatcherAgent("4","St1","St2","St3","St4","0-2-0-0","2-0-5-6","0-5-0-0","0-6-0-0");^
pause
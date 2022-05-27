echo "Launching Client with StationAgents, LocomotiveAgents, Wagons, CargoAgents"
echo "* * * * * * * * * * * * * * *"
echo "Stations = 3"
echo "Locomotives = 1"
echo "Wagons & Cargos = 3"
echo "* * * * * * * * * * * * * * *"
cd %CD%
java -cp CargoTransportation2.0.jar jade.Boot -container -host localhost -port 1099 ^
St1:com.company.StationAgent("3","w1","w2","w3");^
St2:com.company.StationAgent("0","loc1");^
St3:com.company.StationAgent("0");^
loc1:com.company.LocomotiveAgent("St2");^
w1:com.company.Wagon("St1");^
w2:com.company.Wagon("St1");^
w3:com.company.Wagon("St1");^
Cr1:com.company.CargoAgent("St1","St3","07:00:00","12:00:00");^
Cr2:com.company.CargoAgent("St1","St3","06:00:00","10:00:00");^
Cr3:com.company.CargoAgent("St1","St2","08:00:00","13:00:00");^
pause
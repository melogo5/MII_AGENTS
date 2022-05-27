echo "Launching Client with StationAgents, LocomotiveAgents, Wagons, CargoAgents"
echo "* * * * * * * * * * * * * * *"
echo "Stations = 4"
echo "Locomotives = 4"
echo "Wagons & Cargos = 2"
echo "* * * * * * * * * * * * * * *"
cd %CD%
java -cp CargoTransportation2.0.jar jade.Boot -container -host localhost -port 1099 ^
St1:com.company.StationAgent("2","w1","w2");^
St2:com.company.StationAgent("0","loc2");^
St3:com.company.StationAgent("0","loc1","loc3");^
St4:com.company.StationAgent("0","loc4");^
loc1:com.company.LocomotiveAgent("St3");^
loc2:com.company.LocomotiveAgent("St2");^
loc3:com.company.LocomotiveAgent("St3");^
loc4:com.company.LocomotiveAgent("St4");^
w1:com.company.Wagon("St1");^
w2:com.company.Wagon("St1");^
Cr1:com.company.CargoAgent("St1","St4","10:00:00","15:00:00");^
Cr2:com.company.CargoAgent("St1","St4","10:00:00","14:00:00");^
pause
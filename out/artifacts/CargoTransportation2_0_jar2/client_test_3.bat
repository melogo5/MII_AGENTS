echo "Launching Client with StationAgents, LocomotiveAgents, Wagons, CargoAgents"
echo "* * * * * * * * * * * * * * *"
echo "Stations = 7"
echo "Locomotives = 2"
echo "Wagons & Cargos = 6"
echo "* * * * * * * * * * * * * * *"
cd %CD%
java -cp CargoTransportation2.0.jar jade.Boot -container -host localhost -port 1099 ^
St1:com.company.StationAgent("3","w1","w2","w3");^
St2:com.company.StationAgent("1","w5");^
St3:com.company.StationAgent("1","w6");^
St4:com.company.StationAgent("1","w4");^
St5:com.company.StationAgent("0","loc1");^
St6:com.company.StationAgent("0");^
St7:com.company.StationAgent("0","loc2");^
loc1:com.company.LocomotiveAgent("St5");^
loc2:com.company.LocomotiveAgent("St7");^
w1:com.company.Wagon("St1");^
w2:com.company.Wagon("St1");^
w3:com.company.Wagon("St1");^
w4:com.company.Wagon("St4");^
w5:com.company.Wagon("St2");^
w6:com.company.Wagon("St3");^
Cr1:com.company.CargoAgent("St1","St4","08:00:00","12:00:00");^
Cr2:com.company.CargoAgent("St1","St4","04:00:00","09:00:00");^
Cr3:com.company.CargoAgent("St1","St3","01:00:00","07:00:00");^
Cr4:com.company.CargoAgent("St4","St5","18:00:00","22:00:00");^
Cr5:com.company.CargoAgent("St2","St6","10:00:00","14:30:00");^
Cr6:com.company.CargoAgent("St3","St2","10:00:00","15:00:00");^
pause
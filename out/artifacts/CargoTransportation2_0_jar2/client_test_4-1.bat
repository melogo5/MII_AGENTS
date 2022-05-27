echo "Launching Client with StationAgents, LocomotiveAgents, Wagons, CargoAgents"
echo "* * * * * * * * * * * * * * *"
echo "Stations = 5"
echo "Locomotives = 1"
echo "Wagons & Cargos = 4"
echo "* * * * * * * * * * * * * * *"
cd %CD%
java -cp CargoTransportation2.0.jar jade.Boot -container -host localhost -port 1099 ^
St1:com.company.StationAgent("2","w2","w3");^
St2:com.company.StationAgent("0");^
St3:com.company.StationAgent("1","w4","loc1");^
St4:com.company.StationAgent("1","w1");^
St5:com.company.StationAgent("0");^
loc1:com.company.LocomotiveAgent("St3");^
w1:com.company.Wagon("St4");^
w2:com.company.Wagon("St1");^
w3:com.company.Wagon("St1");^
w4:com.company.Wagon("St3");^
Cr1:com.company.CargoAgent("St4","St3","10:00:00","14:00:00");^
Cr2:com.company.CargoAgent("St1","St5","10:00:00","15:00:00");^
Cr3:com.company.CargoAgent("St1","St4","10:00:00","15:00:00");^
Cr4:com.company.CargoAgent("St3","St5","10:00:00","12:00:00");^
pause
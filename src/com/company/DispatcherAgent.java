package com.company;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Admin on 18.04.2018.
 */
public class DispatcherAgent extends Agent {
    static String name;

    String log = "";

    int vNum;

    int INF = Integer.MAX_VALUE / 2;

    ArrayList<String> repliesFromAllStations = new ArrayList();

     int[][] stationsInfo;
     ArrayList<String> stationsName = new ArrayList<>();

    static int counter = 0;

    boolean isStart = false;

    String startTimeLoc;
    String startTimeGlob;
    int modificator = 300;

    //String pathToWagons = "E:\\CargoTransportation\\Schedule\\WagonsSchedule.txt";
    //String pathToCargos = "E:\\CargoTransportation\\Schedule\\CargosSchedule.txt";
    //String pathToLocomotives = "E:\\CargoTransportation\\Schedule\\LocomotivesSchedule.txt";

    String pathToWagons = "__WagonsSchedule.txt";
    String pathToCargos = "__CargosSchedule.txt";
    String pathToLocomotives = "__LocomotivesSchedule.txt";
    String pathToCommonSchedule = "___CommonSchedule.txt";

    Agent thisAgent;

    @Override
    public void setup() {

        Object args[] = getArguments();

        vNum = Integer.parseInt(args[0] + "");

        stationsInfo = new int[vNum][vNum];

        int i = 1;
        for(; i <= vNum; i++) {
            stationsName.add(args[i] + "");
        }

        int tmp = i;
        //i = 0;

        for(; i < args.length; i++ ) {
            String[] data = ((String)args[i]).split("-");
            for(int j = 0; j < data.length; j++) {
                if(Integer.parseInt(data[j]) == 0)
                    stationsInfo[i - tmp][j] = INF;
                else
                    stationsInfo[i - tmp][j] = Integer.parseInt(data[j]);
            }
        }
        /*
        stationsInfo = new int[args.length][args.length];

        vNum = args.length;

        for(int i = 0; i < args.length; i++) {
            String[] data = ((String)args[i]).split("-");
            for(int j = 0; j < data.length; j++) {
                if(Integer.parseInt(data[j]) == 0)
                    stationsInfo[i][j] = INF;
                else
                    stationsInfo[i][j] = Integer.parseInt(data[j]);
            }
        }
        */

        name = this.getLocalName();

        thisAgent = this;


        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null) {
                    String content = msg.getContent();

                    if(content.indexOf("Запрос информации о станциях") != -1) {
                        String sender = msg.getSender().getLocalName();
                        sendInfo(sender);
                    }

                    if(content.indexOf("Готов к отправке") != -1){

                        String sender = msg.getSender().getLocalName();
                        String currentStation = content.split(":")[1];
                        String targetStation = content.split(":")[2];
                        String arrivalTime = content.split(":")[3];
                        String departureTime = content.split(":")[4].replace("-",":");


                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String currentTime = sdf.format(cal.getTime());

                        if(isStart == false) {
                            startTimeLoc = currentTime;
                            startTimeGlob = departureTime;
                            isStart = true;
                        }


                        //log += currentTime + " Груз " + sender + " готов к отправке\n";
                        log += departureTime + " Груз " + sender + " готов к отправке\n";

                        getFreeWagonFromStationMessage(sender, currentStation, targetStation, arrivalTime);


                    }

                    if(content.indexOf("Свободный вагон") != -1) {
                        String fromStation = content.split(":")[1];
                        String toStation = content.split(":")[2];
                        String cargoName = content.split(":")[3];
                        String wagon = content.split(":")[4];
                        String arrivalTime = content.split(":")[5];

                        sendLoadingCargoMessage(fromStation, toStation, cargoName, wagon, arrivalTime);
                    }

                    if(content.indexOf("Вагон погружен") != -1) {

                        String fromStation = content.split(":")[1];
                        String toStation = content.split(":")[2];
                        String cargo = content.split(":")[3];
                        String wagon = content.split(":")[4];
                        String arrivalTime = content.split(":")[5];

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String currentTime = sdf.format(cal.getTime());

                        String newTime = transformTime(currentTime);

                        //log += currentTime + " Груз " + cargo + " погружен в вагон " + wagon + "\n";
                        log += newTime + " Груз " + cargo + " погружен в вагон " + wagon + "\n";

                        sendReadyToGoMessage(fromStation,toStation,cargo,wagon,arrivalTime);

                       // System.out.println(log);


                    }

                    if(content.indexOf("Станция инициализирована") != -1) {
                        stationsName.add(msg.getSender().getLocalName());
                    }

                    if(content.indexOf("Нет свободных вагонов") != -1) {
                        String fromStation = content.split(":")[1];
                        String toStation = content.split(":")[2];
                        String cargoName = content.split(":")[3];
                        String sender = msg.getSender().getLocalName();

                        sendFindFreeWagonsMessage(fromStation,toStation,cargoName,sender);

                    }

                    if(content.indexOf("На этой станции есть вагон") != -1) {
                        repliesFromAllStations.add(content);

                        if(repliesFromAllStations.size() == stationsName.size() - 1) {
                            ArrayList<Integer> indexes = new ArrayList<>();
                            int pointer = 0;

                            for (int i = 0; i < repliesFromAllStations.size(); i++) {
                                String[] data = repliesFromAllStations.get(i).split(":");
                                String stationName = data[5];
                                indexes.add(stationsName.indexOf(stationName));
                            }

                            //int i = indexes.get(0);
                            pointer = 0;
                           // String name = stationsName.get(i);
                            String data = repliesFromAllStations.get(pointer);

                            String station = data.split(":")[5];
                            String to = data.split(":")[1];
                            String wagon = data.split(":")[4];
                            String cargo = data.split(":")[3];


                            sendWagonToStationMessage(station, to, wagon, cargo);


                        }
                    }

                    if(content.indexOf("Локомотив свободен и стоит на станции") != -1) {
                        String locName = content.split(":")[1];
                        String fromStation = content.split(":")[2];
                        String toStation = content.split(":")[3];
                        String cargoName = content.split(":")[4];
                        String wagonName = content.split(":")[5];

                        attachToLocomotiveMessage(locName,fromStation,toStation,cargoName,wagonName);
                        updateCargoStatusToActive(cargoName);
                    }

                    if(content.indexOf("Вагон прикреплен") != -1) {
                        String locName = content.split(":")[1];
                        String fromStation = content.split(":")[2];
                        String toStation = content.split(":")[3];
                        String cargoName = content.split(":")[4];
                        String wagonName = content.split(":")[5];


                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String currentTime = sdf.format(cal.getTime());

                        String newTime = transformTime(currentTime);

                        log += newTime + " Вагон " + wagonName + " с грузом " + cargoName + " прикреплен к локомотиву " + locName + "\n";
                        //log += currentTime + " Вагон " + wagonName + " с грузом " + cargoName + " прикреплен к локомотиву " + locName + "\n";

                        removeLoadedWagonFromStation(fromStation, wagonName);

//                        System.out.println(log);

                    }

                    if(content.indexOf("Локомотив движется в другом направлении") != -1) {
                        String locName = content.split(":")[1];
                        String fromStation = content.split(":")[2];
                        String toStation = content.split(":")[3];
                        String cargoName = content.split(":")[4];
                        String wagonName = content.split(":")[5];

                        locomotiveNotSuitable(locName,fromStation,toStation,cargoName,wagonName);
                    }

                    if(content.indexOf("Локомотив готов к отправке") != -1) {
                        String locName = content.split(":")[1];
                        String fromStation = content.split(":")[2];
                        String toStation = content.split(":")[3];

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String currentTime = sdf.format(cal.getTime());

                        String newTime = transformTime(currentTime);

                        log += newTime + " Локомотив " + locName + " отправляется со станции " + fromStation + " на станцию " + toStation + "\n";

                        removeLocomotiveFromStation(fromStation, locName);
                        addLocomotiveToStation(toStation, locName, fromStation, toStation);

//                        System.out.println(log);

                    }

                    if(content.indexOf("Новая текущая станция") != - 1) {
                        String station = content.split(":")[1];
                        String locName = content.split(":")[2];
                        String toStation = content.split(":")[3];

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String currentTime = sdf.format(cal.getTime());

                        String newTime = transformTime(currentTime);

                        log += newTime + " Локомотив " + locName + " прибыл на станцию " + toStation + "\n";

  //                      System.out.println(log);

                    }

                    if(content.indexOf("Локомотив отцепил вагон") != -1) {
                        String station = content.split(":")[3];
                        String locName = content.split(":")[1];
                        String wagon = content.split(":")[2];

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String currentTime = sdf.format(cal.getTime());

                        String newTime = transformTime(currentTime);

                        log += newTime + " Локомотив " + locName + " оставил вагон " + wagon + " на станции " + station + "\n";
                    }

                    if(content.indexOf("Груз разгружен") != -1) {
                        String station = content.split(":")[1];
                        String locName = content.split(":")[2];
                        String wagon = content.split(":")[3];
                        String cargo = content.split(":")[4];
                        String arrivalTime = content.split(":")[5].replace("-",":");

                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String currentTime = sdf.format(cal.getTime());

                        String newTime = transformTime(currentTime);

                        System.out.println(newTime + "   " + arrivalTime);
                        boolean isInTime = compareTime(newTime,arrivalTime);

                        String verdict = "";

                        if(isInTime == true)
                            verdict = "(Груз доставлен вовремя)";
                        else
                            verdict = "(Груз доставлен с опозданием)";

                        log += newTime + " Груз " + cargo + " разгружен на станции " + station + " " + verdict + "\n";



                        cargoUnloadedToWagonMessage(station, wagon);
                       // wagonIsEmptyMessage(locName, wagon,station);

                        //cargoConsiderMessage(locName);

    //                    System.out.println(log);



                    }

                    if(content.indexOf("Есть недоставленные вагоны?") != -1) {
                        String locName = content.split(":")[1];
                        counter = stationsName.size();

                        for(int i = 0; i < stationsName.size(); i++) {
                            findUndeliveredWagons(locName, stationsName.get(i));
                        }
                    }

                    if(content.indexOf("Нет неразгруженных вагонов") != -1) {
                        counter--;
                        if(counter == 0) {
                            addBehaviour(new TickerBehaviour(thisAgent,1000) {
                                int timer = 2;
                                @Override
                                protected void onTick() {
                                    timer--;
                                    if (timer <= 0){

                                        writeSchedule();
                                        System.out.println(log);

                                        stop();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void getLocomotivesMessage(String station, int pointer) {
        String text = "Дай список локомотивов";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + pointer);
        msg.addReceiver(new AID(station,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void writeSchedule() {
        List str = new ArrayList(Arrays.asList(log.split("\n")));
        Set<String> s = new LinkedHashSet<>(str);
        Iterator<String> iterator = s.iterator();

        ArrayList<String> cargos = new ArrayList();
        ArrayList<String> wagons = new ArrayList();
        ArrayList<String> locomotives = new ArrayList();

        while (iterator.hasNext()) {
            String line = iterator.next();
            String[] parts = line.split(" ");
            String marker = parts[1];

            if(marker.equals("Локомотив") && line.indexOf("оставил вагон") != -1) {
                wagons.add(line);
            }

            else if(marker.equals("Груз")) {
                cargos.add(line);
            }
            else if(marker.equals("Вагон")) {
                wagons.add(line);
            }
            else if(marker.equals("Локомотив") && line.indexOf("оставил вагон") == -1) {
                locomotives.add(line);
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathToCommonSchedule,false));
            iterator = s.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                writer.write(line + "\r\n");
            }
            writer.close();


            writer = new BufferedWriter(new FileWriter(pathToCargos,false));
            iterator = cargos.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                writer.write(line + "\r\n");
            }
            writer.close();


            writer = new BufferedWriter(new FileWriter(pathToWagons,false));
            iterator = wagons.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                writer.write(line + "\r\n");
            }
            writer.close();

            writer = new BufferedWriter(new FileWriter(pathToLocomotives,false));
            iterator = locomotives.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                writer.write(line + "\r\n");
            }
            writer.close();


            ArrayList<String> locNames = new ArrayList<>();

            for(int i = 0; i < locomotives.size(); i++) {
                String[] data = locomotives.get(i).split(" ");
                String name = data[2];
                if(locNames.indexOf(name) == -1)
                    locNames.add(name);
            }

            for(int i = 0; i < locNames.size(); i++) {
                writer = new BufferedWriter(new FileWriter("_"+locNames.get(i) + ".txt",false));
                iterator = locomotives.iterator();
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    String[] data = line.split(" ");
                    if(data[2].equals(locNames.get(i)))
                        writer.write(line + "\r\n");
                }
                writer.close();
            }

            ArrayList<String> cargoNames = new ArrayList<>();

            for(int i = 0; i < cargos.size(); i++) {
                String[] data = cargos.get(i).split(" ");
                String name = data[2];
                if(cargoNames.indexOf(name) == -1)
                    cargoNames.add(name);
            }

            for(int i = 0; i < cargoNames.size(); i++) {
                writer = new BufferedWriter(new FileWriter("_"+cargoNames.get(i) + ".txt",false));
                iterator = cargos.iterator();
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    String[] data = line.split(" ");
                    if(data[2].equals(cargoNames.get(i)))
                        writer.write(line + "\r\n");
                }
                writer.close();
            }

            ArrayList<String> wagonNames = new ArrayList<>();

            for(int i = 0; i < wagons.size(); i++) {
                String[] data = wagons.get(i).split(" ");
                String name = data[2];
                if(!data[2].equals("Локомотив") && wagonNames.indexOf(name) == -1)
                    wagonNames.add(name);
            }

            for(int i = 0; i < wagonNames.size(); i++) {
                writer = new BufferedWriter(new FileWriter("_"+wagonNames.get(i) + ".txt",false));
                iterator = wagons.iterator();
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    String[] data = line.split(" ");
                    if(data[2].equals(wagonNames.get(i)) || data[5].equals(wagonNames.get(i)))
                        writer.write(line + "\r\n");
                }
                writer.close();
            }


        }
        catch (Exception e) {;}
    }

    public void sendInfo(String locName) {
        String text = "Информация о маршрутах:" + vNum + "&";
        String inf = "";
        for(int i = 0; i < vNum; i++){
            for(int j = 0; j < vNum; j++) {
                if(j == vNum - 1)
                    inf += stationsInfo[i][j];
                else
                    inf += stationsInfo[i][j] + "-";
            }

            if(i != vNum - 1)
                inf += "&";
        }

        text += inf + "#";


        for(int i = 0; i < stationsName.size(); i++) {
            if(i == stationsName.size() - 1)
                text += stationsName.get(i);
            else
                text += stationsName.get(i) + "@";
        }

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);
        msg.addReceiver(new AID(locName, AID.ISLOCALNAME));

        System.out.println(text);

        send(msg);
    }

    public void findUndeliveredWagons(String locName, String station) {
        String text = "Есть недоставленные вагоны?";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + station);
        msg.addReceiver(new AID(station,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void updateCargoStatusToActive(String cargo) {
        String text = "Груз отправляется в путь";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + cargo);
        msg.addReceiver(new AID(cargo,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void removeLoadedWagonFromStation(String station, String wagon) {
        String text = "Вагон прицпелен к локомотиву";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + wagon);
        msg.addReceiver(new AID(station,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void cargoConsiderMessage(String locName) {
        String text = "Вагон учтен";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void wagonIsEmptyMessage(String locName, String wagon, String station) {
        String text = "Отцепи вагон";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + wagon + ":" + station);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void cargoUnloadedToWagonMessage(String station, String wagon) {
        String text = "Вагон свободен";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void addLocomotiveToStation(String station, String locName, String fromStation, String toStation) {
        String text = "Добавь локомотив";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName + ":" + fromStation + ":" + toStation);
        msg.addReceiver(new AID(station,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void removeLocomotiveFromStation(String station, String locName) {
        String text = "Удали локомотив";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName);
        msg.addReceiver(new AID(station,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void locomotiveNotSuitable(String locName, String fromStation, String toStation, String cargo, String wagon) {
        String text = "Локомотив не подходит для перевозки";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void attachToLocomotiveMessage(String locName, String fromStation, String toStation, String cargo, String wagon) {
        String text = "Прикрепись к локомотиву";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void sendReadyToGoMessage(String from, String to, String cargo, String wagon, String arrivalTime) {
        String text = "Жду локомотив";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + from + ":" + to + ":" + cargo + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(from,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void sendWagonToStationMessage(String fromStation, String toStation, String wagonName, String cargoName) {
        String text = "Отправь вагон когда прибудет курсирующий локомотив";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + fromStation + ":" + toStation + ":" + cargoName + ":" + wagonName);
        msg.addReceiver(new AID(fromStation,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);

    }

    public void sendFindFreeWagonsMessage(String fromStation, String toStation, String cargoName, String sender) {
        String text = "Есть свободные вагоны у остальных станций?";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + fromStation + ":" + toStation + ":" + cargoName);

        for(int i = 0; i < stationsName.size(); i++) {
            if(!stationsName.get(i).equals(sender))
                msg.addReceiver(new AID(stationsName.get(i),AID.ISLOCALNAME));
        }

        System.out.println(msg.getContent());

        send(msg);
    }


    //Погрузка в вагон:[from]:[to]:[cargoName]:[wagon]
    public void sendLoadingCargoMessage(String fromStation, String toStation, String cargoName, String wagon, String arrivalTime) {
        String text = "Погрузка в вагон";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + fromStation + ":" + toStation + ":" + cargoName + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(cargoName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    //Есть свободные вагоны?:[from]:[to]:[name]
    public void getFreeWagonFromStationMessage(String sender, String currentStation, String targetStation, String arrivalTime){
        String text = "Есть свободные вагоны?";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + currentStation + ":" + targetStation + ":" + sender + ":" + arrivalTime);
        msg.addReceiver(new AID(currentStation,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public boolean compareTime(String currentTime, String arrivalTime) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date d = new Date();
        try {
            d = dateFormat.parse(currentTime);
            long currentMillis = d.getTime();
            d = dateFormat.parse(arrivalTime);
            long arrivalMillis = d.getTime();

            long delta = arrivalMillis - currentMillis;

            if(delta >= 0)
                return true;
            else
                return false;
        }
        catch (Exception ex){System.out.print(ex);}

        return false;
    }

    public String transformTime(String currentTime) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date d = new Date();
        try {
            d = dateFormat.parse(currentTime);
            long currentMillis = d.getTime();
            d = dateFormat.parse(startTimeLoc);
            long startLocMillis = d.getTime();

            long delta = (currentMillis - startLocMillis) * modificator;

            d = dateFormat.parse(startTimeGlob);
            long startGlobMillis = d.getTime();

            long result = delta + startGlobMillis;

            dateFormat.format(result);

            return  dateFormat.format(result);
        }
        catch (Exception ex){System.out.print(ex);}

        return null;
    }

}

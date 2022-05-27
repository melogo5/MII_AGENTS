package com.company;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Admin on 18.04.2018.
 */
public class Wagon extends Agent {

    String currentStation;

    String cargo = null;

    String cargoArrivalTime = "";

    String dispatcherName = "Disp";

    int[][] stationsInfo;
    int vNum;
    ArrayList<String> stationsName = new ArrayList();

    ArrayList<String> resultingInfo = new ArrayList<>();

    int stationPointer = 0;
    int locPointer = 0;

    String[] findedlocs;

    String status = "free";

    int indexTo;

    @Override
    public void setup() {

        Object args[] = getArguments();
        currentStation = args[0] + "";

        sendStationsInfo();

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null){
                    String content = msg.getContent();

                    if(content.indexOf("Проверь груз") != -1) {
                        if(cargo != null) {
                            tryToAwakeCargo(cargo);
                        }
                    }

                    if(content.indexOf("Свяжись с диспетчером") != -1) {
                        String fromStation = content.split(":")[1];
                        String toStation = content.split(":")[2];
                        String cargoName = content.split(":")[3];
                        String wagon = content.split(":")[4];
                        String arrivalTime = content.split(":")[4];

                        sendDispetcherFreeWagonMessage(fromStation, toStation, cargoName, wagon, arrivalTime);
                    }

                    if(content.indexOf("Груз погружен в выгон") != -1) {
                        String cargoName = content.split(":")[3];
                        String wagonName = content.split(":")[4];

                        String from = content.split(":")[1];
                        String to = content.split(":")[2];

                        String arrivalTime = content.split(":")[5];

                        cargo = cargoName;

                        sendDispatcherWagonLoadedMessage(from,to,cargoName, wagonName, arrivalTime);
                    }

                    if(content.indexOf("Прикрепись к локомотиву") != -1) {
                        String locName = content.split(":")[1];
                        String fromStation = content.split(":")[2];
                        String toStation = content.split(":")[3];
                        String cargoName = content.split(":")[4];
                        String wagonName = content.split(":")[5];

                        permissionToAttachMessage(locName, fromStation, toStation, cargoName, wagonName);
                    }

                    if(content.indexOf("Локомотив не подходит для перевозки") != -1) {
                        // Дописать ситуациию отсутствия локомотива на станции
                    }

                    if(content.indexOf("Разгрузись") != -1) {
                        String station = content.split(":")[1];
                        String locName = content.split(":")[2];
                        String wagon = content.split(":")[3];

                        if(cargo != null)
                            cargoUnloadMessage(station, locName, wagon, cargo);
                        else
                            cargoConsiderMessage(station, locName, wagon, cargo);
                    }

                    if(content.indexOf("Вагон свободен") != -1) {
                        String station = content.split(":")[1];
                        cargo = null;
                        currentStation = station;
                    }

                    if(content.indexOf("Попробуй прикрепиться") != -1) {
                        String wagon = content.split(":")[1];
                        String locName = content.split(":")[2];

                        getCargoInformation(wagon,locName,cargo);
                    }

                    if(content.indexOf("Информация о маршрутах") != -1) {
                        String[] pre = content.split(":");
                        String prepre = pre[1];

                        String[] pre3 = prepre.split("#");

                        String[] data = pre3[0].split("&");

                        vNum = Integer.parseInt(data[0]);

                        stationsInfo = new int[vNum][vNum];

                        for(int i = 1; i < data.length; i++) {
                            String[] tmp = data[i].split("-");
                            for(int j = 0; j < tmp.length; j++) {
                                stationsInfo[i - 1][j] = Integer.parseInt(tmp[j]);
                            }
                        }

                        String[] stInfo = pre3[1].split("@");

                        for(int i = 0; i < stInfo.length; i++) {
                            stationsName.add(stInfo[i]);
                        }

                    }


                    if(content.indexOf("Нет локомотивов на станции") != -1 ) {

                        String from = content.split(":")[1];
                        String to = content.split(":")[2];
                        String cargo = content.split(":")[3];
                        String wagon = content.split(":")[4];
                        String arrivalTime = content.split(":")[5];

                        stationPointer = 0;
                        locPointer = 0;

                        if(!status.equals("reserved")) {

                            int indexFrom = stationsName.indexOf(from);
                            indexTo = stationsName.indexOf(to);

                            resultingInfo = new ArrayList<>();

                            for (int j = 0; j < stationsName.size(); j++) {
                                if (!stationsName.get(j).equals(stationsName.get(indexFrom))) {
                                    int thisIndex = stationsName.indexOf(stationsName.get(j));
                                    ArrayList<Integer> route = FindWay.dijkstra(thisIndex, indexFrom, vNum, stationsInfo);
                                    int length = FindWay.getLength(stationsInfo, route);
                                    resultingInfo.add(stationsName.get(j) + ":" + length);
                                }
                            }

                            FindWay.sortRoutes(resultingInfo);

                            //getLocomotivesMessage(from, resultingInfo.get(stationPointer).split(":")[0], wagon, arrivalTime, cargo);
                            getLocomotivesMessage(from, resultingInfo.get(0).split(":")[0], wagon, arrivalTime, cargo);
                        }
                    }

                    if(content.indexOf("Информация о локомотивах") != -1) {
                        String from = content.split(":")[2];
                        String to = content.split(":")[3];

                        String wagon = content.split(":")[4];
                        String arrivalTime = content.split(":")[5];
                        String cargo = content.split(":")[6];

                        if(!content.split(":")[1].equals("FAIL")) {
                            String tmp = content.split(":")[1];
                            findedlocs = tmp.split("&");

                            locPointer = 0;

                            String locName = findedlocs[locPointer];


                            getLocomotiveInfoMessage(locName, from, to, cargo, wagon, arrivalTime,indexTo);
                            //getLocomotiveInfoMessage(locName, to, from, cargo, wagon, arrivalTime,"flag");
                        }
                        else {
                            stationPointer++;
                            locPointer = 0;

                            if(stationPointer != stationsName.size() - 1) {
                                getLocomotivesMessage(from,resultingInfo.get(stationPointer).split(":")[0],wagon,arrivalTime,cargo);
                            }
                            else {
                                stationPointer = 0;
                                locPointer=0;
                            }
                        }
                    }

                    if(content.indexOf("Локомотив свободен для резервирования") != -1) {
                        String from = content.split(":")[2];
                        String to = content.split(":")[3];
                        String locName = content.split(":")[1];
                        String cargo = content.split(":")[4];
                        String wagon = content.split(":")[5];

                        status = "reserved";
                        reserveSpace(locName,from,to,cargo,wagon);

                        stationPointer = 0;
                        locPointer = 0;
                    }

                    if(content.indexOf("Локомотив не подходит для резервирования") != -1) {
                        String from = content.split(":")[2];
                        String to = content.split(":")[3];
                        //String locName = content.split(":")[1];
                        String cargo = content.split(":")[4];
                        String wagon = content.split(":")[5];
                        String arrivalTime = content.split(":")[6];

                        locPointer++;


                        if(locPointer == findedlocs.length) {

                            stationPointer++;
                            locPointer = 0;

                            if(stationPointer != stationsName.size() - 1) {
                                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                //getLocomotivesMessage(from,resultingInfo.get(stationPointer).split(":")[0],wagon,arrivalTime,cargo);
                                getLocomotivesMessage(to,resultingInfo.get(stationPointer).split(":")[0],wagon,arrivalTime,cargo);
                                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                            }
                            else {
                                locPointer = 0;
                                stationPointer = 0;
                            }
                        }
                        else {
                           //-------------------------------

                                String locName = findedlocs[locPointer];
                                getLocomotiveInfoMessage(locName, to, from, cargo, wagon, arrivalTime, indexTo);
                        }
                    }

                    if(content.indexOf("Сними резервирование") != -1) {
                        status = "free";
                    }

                }
            }
        });
    }

    public void tryToAwakeCargo(String cargo) {
        String text = "Время отправляться?";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);
        msg.addReceiver(new AID(cargo,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void reserveSpace(String locName, String from, String to, String cargo, String wagon) {
        String text = "Запрос на резервирование";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + from + ":" + to + ":" + cargo + ":" + wagon);
        //msg.setContent(text + ":" + locName + ":" + to + ":" + from + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void getLocomotiveInfoMessage(String name, String fromStation, String toStation, String cargo, String wagon, String arrivalTime,int param) {
        String text = "Запрос информации о локомотиве";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + name + ":" + toStation + ":" + fromStation + ":" + cargo + ":" + wagon + ":" + arrivalTime + ":" + param);
        msg.addReceiver(new AID(name,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void getLocomotivesMessage(String from,String to, String wagon, String arrivalTime, String cargo) {
        String text = "Дай список локомотивов";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + from + ":" + to + ":" + wagon + ":" + arrivalTime + ":" + cargo);
        msg.addReceiver(new AID(to,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void sendStationsInfo() {
        String text = "Запрос информации о станциях";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);

        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void getCargoInformation(String wagon, String locName, String cargo) {
        String text = "Подходит локомотив?";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + wagon + ":" + locName + ":" + cargo);
        msg.addReceiver(new AID(cargo,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void cargoConsiderMessage(String station, String locName, String wagon, String cargo) {
        String text = "Вагон учтен";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName + ":" + wagon + ":" + cargo);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void cargoUnloadMessage(String station, String locName, String wagon, String cargo) {
        String text = "Попробуй разгрузиться";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName + ":" + wagon + ":" + cargo);
        msg.addReceiver(new AID(cargo,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void permissionToAttachMessage(String locName, String fromStation, String toStation, String cargoName, String wagon) {
        String text = "Запрос на прикрепление";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    //Вагон погружен:[cargoName]:[wagonName]
    public void sendDispatcherWagonLoadedMessage(String from, String to, String cargo, String wagon, String arrivalTime) {
        String text = "Вагон погружен";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + from + ":" + to + ":" +cargo + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    //Свободный вагон:[fromStation]:[toStation]:[cargoName]:[wagonName]
    public void sendDispetcherFreeWagonMessage(String fromStation, String toStation, String cargoName, String wagon, String arrivalTime) {
        String text = "Свободный вагон";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + fromStation + ":" + toStation + ":" + cargoName + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }
}

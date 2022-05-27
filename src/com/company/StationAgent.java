package com.company;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;

public class StationAgent extends Agent {

    ArrayList<String> freeWagons = new ArrayList();
    ArrayList<String> loadedWagons = new ArrayList<>();

    ArrayList<String> locomotives = new ArrayList<>();

    String dispatcherName = "Disp";

    ArrayList<String> requests = new ArrayList();

    String status = "normal";

    ArrayList<String> resultingInfo = new ArrayList<>();


    int[][] stationsInfo;
    int vNum;
    ArrayList<String> stationsName = new ArrayList();

    ArrayList<Integer> findLocFlag = new ArrayList<>();
    ArrayList<Integer> findedlocs = new ArrayList<>();

   // int locPointer = 0;
   // int locMax;
  //  String[] findedlocs;

    @Override
    public void setup() {

        Object args[] = getArguments();
        int wagonCount = Integer.parseInt(args[0] + "");

        for(int i = 1; i <= wagonCount; i++) {
            freeWagons.add(args[i] + "");
        }

        for(int i = 1 + wagonCount; i < args.length; i++) {
            locomotives.add(args[i] + "");
        }

        sendStationsInfo();

        Agent thisAgent = this;

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null){
                    String content = msg.getContent();

                    if(content.indexOf("Есть свободные вагоны?") != -1) {
                        int count = freeWagons.size();

                        String fromStation = content.split(":")[1];
                        String toStation = content.split(":")[2];
                        String cargoName = content.split(":")[3];
                        String arrivalTime = content.split(":")[4];


                        if(count > 0) {
                            String wagon = freeWagons.get(count - 1);
                            freeWagons.remove(count - 1);
                            loadedWagons.add(wagon);

                            sendHaveFreeWagonsMessage(fromStation, toStation, cargoName, wagon, arrivalTime);

                        }
                        else {
                            sendNoFreeWagonMessage(fromStation, toStation, cargoName);
                        }
                    }

                    if(content.indexOf("Есть свободные вагоны у остальных станций?") != -1) {
                        int count = freeWagons.size();

                        String fromStation = content.split(":")[1];
                        String toStation = content.split(":")[2];
                        String cargoName = content.split(":")[3];


                        if(count > 0) {
                            String wagon = freeWagons.get(count - 1);

                            sendHaveFreeWagonsAllMessage(fromStation, toStation, cargoName, wagon, thisAgent.getLocalName());

                        }
                        /*
                        else {
                            sendNoFreeWagonMessage(fromStation, toStation, cargoName);
                        }
                        */
                    }

                    if(content.indexOf("Жду локомотив") != -1) {
                        String from = content.split(":")[1];
                        String to = content.split(":")[2];
                        String cargo = content.split(":")[3];
                        String wagon = content.split(":")[4];
                        String arrivalTime = content.split(":")[5];


                        if(locomotives.size() > 0) {
                            for(int i = 0; i < locomotives.size(); i++) {
                                String name = locomotives.get(i);
                                getLocomotiveInfoMessage(name, thisAgent.getLocalName(), to, cargo, wagon, arrivalTime);
                            }
                        }
                        else {

                            noLocomotivesMessage(from,to,cargo,wagon,arrivalTime);


                        }
                    }







                    if(content.indexOf("Удали локомотив") != -1) {
                        String locName = content.split(":")[2];

                        locomotives.remove(locName);
                    }

                    if(content.indexOf("Добавь локомотив") != -1) {
                        String station = content.split(":")[1];
                        String locName = content.split(":")[2];
                        String fromStation = content.split(":")[3];
                        String toStation = content.split(":")[4];


                        locomotives.add(locName);
                        updateStationMessage(fromStation, locName, toStation);

                        //unloadWagonsMessage(station, locName);

                    }

                    if(content.indexOf("Прибыл пустой вагон") != -1) {
                        String wagon = content.split(":")[1];
                        String station = content.split(":")[2];

                        freeWagons.add(wagon);
                    }

                    if(content.indexOf("Вагон прицпелен к локомотиву") != -1) {
                        String wagon = content.split(":")[2];
                        String station = content.split(":")[1];

                        loadedWagons.remove(wagon);
                    }

                    if(content.indexOf("Есть загруженные вагоны?") != -1) {
                        String locName = content.split(":")[1];

                        if(loadedWagons.size() == 0) {
                            noLoadedWagonsMessage(locName);
                        }
                        else {
                            numOfLoadedWagonsMessage(locName, loadedWagons.size());
                            for (int i = 0; i < loadedWagons.size(); i++) {
                                getWagonInfoMessage(loadedWagons.get(i), locName);
                               // String wagon = loadedWagons.get(i);
                               // awakeCargo(wagon);
                            }
                        }
                    }

                    if(content.indexOf("Есть недоставленные вагоны?") != -1) {
                        String locName = content.split(":")[1];
                        String station = content.split(":")[2];

                        if(loadedWagons.size() > 0) {

                            for(int i = 0; i < loadedWagons.size();i++) {
                                String wagon = loadedWagons.get(i);
                                awakeCargo(wagon);
                            }


                            //------------------------

                          // sendLocomotiveHereMessage(locName, station);


                            //-------------------------
                        }

                        else {
                            dontSendLocomotiveHereMessage();
                        }
                    }

                    if(content.indexOf("Дай список локомотивов") != -1) {
                        String from = content.split(":")[1];
                        String to = content.split(":")[2];
                        String wagon = content.split(":")[3];
                        String arrivalTime = content.split(":")[4];
                        String cargo = content.split(":")[5];

                        String result = "";
                        if(locomotives.size() > 0) {
                            for(int i = 0; i < locomotives.size(); i++) {
                                if(i == locomotives.size() - 1)
                                    result += locomotives.get(i);
                                else
                                    result += locomotives.get(i) + "&";
                            }
                        }
                        else
                            result = "FAIL";

                        locomotivesInfoMessage(from,result,to,wagon,arrivalTime,cargo);
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

                }
            }
        });
    }

    public void awakeCargo(String wagon) {
        String text = "Проверь груз";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void noLocomotivesMessage(String from, String to, String cargo, String wagon, String arrivalTime) {
        String text = "Нет локомотивов на станции";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + from + ":" + to + ":" + cargo + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void getLocomotivesMessage(String from,String to, String wagon, int pointer, String arrivalTime, String cargo) {
        String text = "Дай список локомотивов";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + from + ":" + to + ":" + wagon + ":" + pointer + ":" + arrivalTime + ":" + cargo);
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

    public static void sort(ArrayList<String> arr) {
        for (int min = 0; min < arr.size() - 1; min++) {
            int least = min;
            for (int j = min + 1; j < arr.size(); j++) {
                if (Integer.parseInt(arr.get(j).split(":")[6]) < Integer.parseInt(arr.get(least).split(":")[6])) {
                    least = j;
                }
            }
            if (least != min) {
                String tmp = arr.get(min);
                arr.set(min,arr.get(least));
                arr.set(least,tmp);
            }
        }
    }

    public void locomotivesInfoMessage(String from, String info, String to, String wagon,String arrivalTime, String cargo) {
        String text = "Информация о локомотивах";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + info + ":" + from + ":" + to + ":" + wagon + ":" + arrivalTime + ":" + cargo);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void dontSendLocomotiveHereMessage() {
        String text = "Нет неразгруженных вагонов";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void sendLocomotiveHereMessage(String locName, String station) {
        String text = "Здесь есть неразгруженный вагон";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + station);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void numOfLoadedWagonsMessage(String locName, int num) {
        String text = "Количество загруженных вагонов";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + num);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void noLoadedWagonsMessage(String locName) {
        String text = "Нет загруженных вагонов";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void getWagonInfoMessage(String wagon, String locName) {
        String text = "Попробуй прикрепиться";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + wagon + ":" + locName);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void unloadWagonsMessage(String station, String locName) {
        String text = "Разгрузи вагоны";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void updateStationMessage(String station, String locName, String toStation) {
        String text = "Новая текущая станция";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName + ":" + toStation);

        //msg.addReceiver(new AID(DispatcherAgent.name, false));
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void noFreeLocomotivesMessage(String stationName, String to, String cargo, String wagon) {
        String text = "На этой станции нет свободных локомотивов";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + stationName + ":" + to  + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void getLocomotiveInfoMessage(String name, String fromStation, String toStation, String cargo, String wagon, String arrivalTime,String flag) {
        String text = "Запрос информации о локомотиве";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + name + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon + ":" + arrivalTime + flag);
        msg.addReceiver(new AID(name,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void getLocomotiveInfoMessage(String name, String fromStation, String toStation, String cargo, String wagon, String arrivalTime) {
        String text = "Запрос информации о локомотиве";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + name + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(name,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    //Есть вагон:[from]:[to]:[cargoName]:[wagon]:[stationName]
    public void sendHaveFreeWagonsAllMessage(String fromStation, String toStation, String cargoName, String wagon, String station) {
        String text = "На этой станции есть вагон";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + fromStation + ":" + toStation + ":" + cargoName + ":" + wagon + ":" + station);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void sendMyInfoToDisp() {
        String text = "Станция инициализирована";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    //Нет свободных вагонов:[from]:[to]:[cargoName]
    public void sendNoFreeWagonMessage(String fromStation, String toStation, String cargoName) {
        String text = "Нет свободных вагонов";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + fromStation + ":" + toStation + ":" + cargoName);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    //Свяжись с диспетчером:[from]:[to]:[cargoName]:[wagon]
    public void sendHaveFreeWagonsMessage(String fromStation, String toStation, String cargoName, String wagon, String arrivalTime) {
        String text = "Свяжись с диспетчером";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + fromStation + ":" + toStation + ":" + cargoName + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }
}

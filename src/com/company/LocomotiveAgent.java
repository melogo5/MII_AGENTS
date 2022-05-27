package com.company;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

public class LocomotiveAgent extends Agent {

    String dispatcherName = "Disp";

    ArrayList<String> wagons = new ArrayList<>();
    ArrayList<String> reservedWagons = new ArrayList<>();

    ArrayList<Integer> route = new ArrayList<>();

    String currentStation;
    String destination;

    int freeSpace = 2;
    int reservedSpace = 0;


    int attachTime = 1;
    int waitTime = 1;


    int tmpWagonCount = 0;
    int tmpWagon = 0;


    int requestCount = 0;

    int loadedWagonCount = 0;
    int loadedCounter = 0;

    Agent thisAgent;

    ArrayList<String> requests = new ArrayList();

    int[][] stationsInfo;
    int vNum;
    ArrayList<String> stationsName = new ArrayList();

    @Override
    public void setup() {

        Object args[] = getArguments();
        currentStation = args[0] + "";

        thisAgent = this;

        sendStationsInfo();

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null) {
                    String content = msg.getContent();

                    if(content.indexOf("Запрос информации о локомотиве") != -1) {
                        if(freeSpace - reservedSpace > 0) {

                            String locName = content.split(":")[1];
                            String fromStation = content.split(":")[2];
                            String toStation = content.split(":")[3];
                            String cargo = content.split(":")[4];
                            String wagon = content.split(":")[5];
                            String arrivalTime = "";

                            int finalStation = 0;


                            Boolean flag = false;

                            if(content.split(":").length == 7)
                                arrivalTime = content.split(":")[6];
                            if(content.split(":").length == 8) {
                                flag = true;
                                arrivalTime = content.split(":")[6];
                                finalStation = Integer.parseInt(content.split(":")[7]);
                            }
                            ArrayList<Integer> tmp = new ArrayList<>();

                            if(destination != null) {

                                if(destination.equals("waiting")) {
                                    requests.add(content);
                                }
                                else {

                                /*
                                if (route.indexOf(stationsName.indexOf(toStation)) != -1 && route.indexOf(stationsName.indexOf(toStation)) != 0) {
                                    System.out.println("MARK");
                                    if(flag == false)
                                        sendFreeNotActiveMessage(fromStation, toStation, locName, cargo, wagon);
                                    else {
                                        String tmp1 = toStation;
                                        toStation = fromStation;
                                        fromStation = tmp1;

                                        reservedSpace++;
                                        sendFreeReservedMessage(fromStation, toStation, locName, cargo, wagon);
                                    }
                                }
                                */

                                //else {

                                    /*
                                    if(flag == false)
                                        tmp = FindWay.dijkstra(stationsName.indexOf(fromStation), stationsName.indexOf(toStation),
                                            vNum, stationsInfo);
                                    else {
                                        tmp = FindWay.dijkstra(stationsName.indexOf(toStation), stationsName.indexOf(fromStation),
                                                vNum, stationsInfo);
                                    }
                                    */
                                    if(flag == false)
                                        tmp = FindWay.dijkstra(stationsName.indexOf(fromStation), stationsName.indexOf(toStation),
                                            vNum, stationsInfo);
                                    else {

                                        if(stationsName.indexOf(fromStation) == finalStation) {
                                            ArrayList<Integer> tmp1 = FindWay.dijkstra(stationsName.indexOf(fromStation),
                                                    stationsName.indexOf(toStation),
                                                    vNum,stationsInfo);

                                            ArrayList<Integer> tmp2 = new ArrayList<>(tmp1);
                                            Collections.reverse(tmp1);
                                            tmp1.remove(0);

                                            tmp = new ArrayList<>(tmp2);
                                            tmp.addAll(tmp1);

                                        }
                                        else {
                                            tmp = FindWay.dijkstra(stationsName.indexOf(fromStation),
                                                    finalStation,
                                                    vNum, stationsInfo);

                                            if (!stationsName.get(tmp.get(0)).equals(fromStation) || (tmp.indexOf(stationsName.indexOf(toStation)) == -1)) {

                                                tmp = FindWay.dijkstra(stationsName.indexOf(fromStation),
                                                        stationsName.indexOf(toStation),
                                                        vNum, stationsInfo);
                                            }
                                        }
                                    }

                                    String a = "";
                                    for (int i = 0; i < tmp.size(); i++)
                                        a += tmp.get(i);
                                    String b = "";
                                    for (int i = 0; i < route.size(); i++)
                                        b += route.get(i);


                                    if (b.equals("")) {
                                        tmp = FindWay.dijkstra(stationsName.indexOf(fromStation), stationsName.indexOf(toStation),
                                                vNum, stationsInfo);

                                        if (FindWay.check(a, stationsName.indexOf(destination) + "")) {
                                            route = tmp;

                                            if(flag == false)
                                                sendFreeNotActiveMessage(fromStation, toStation, locName, cargo, wagon);
                                            else {
                                                String tmp1 = toStation;
                                                toStation = fromStation;
                                                fromStation = tmp1;
                                                reservedSpace++;
                                                sendFreeReservedMessage(fromStation, toStation, locName, cargo, wagon);
                                            }
                                        }
                                    } else if (FindWay.check(a, b)) {
                                        if (a.length() > b.length()) {
                                            route = tmp;
                                        }
                                        /*
                                        b = "";
                                        for (int i = 0; i < route.size(); i++)
                                            b += route.get(i);
                                        */

                                        if(flag == false)
                                            sendFreeNotActiveMessage(fromStation, toStation, locName, cargo, wagon);
                                        else {
                                            String tmp1 = toStation;
                                            toStation = fromStation;
                                            fromStation = tmp1;
                                            reservedSpace++;
                                            sendFreeReservedMessage(fromStation, toStation, locName, cargo, wagon);
                                        }
                                    }
                                    else {
                                        if(flag == false)
                                            sendWrongDestinationMessage(locName, fromStation, toStation, cargo, wagon);
                                        else {
                                            String tmp1 = toStation;
                                            toStation = fromStation;
                                            fromStation = tmp1;
                                            wrongReserv(fromStation,toStation,locName,cargo,wagon,arrivalTime);
                                        }
                                        if (loadedWagonCount != 0) {
                                            loadedCounter++;
                                            if (loadedCounter == loadedWagonCount) {
                                                loadedCounter = 0;
                                                loadedWagonCount = 0;

                                                moveForward();
                                            }
                                        }
                                    }
                                //}

                            }
                            }

                            else {

                                if(flag == false)
                                    tmp = FindWay.dijkstra(stationsName.indexOf(fromStation), stationsName.indexOf(toStation),
                                            vNum, stationsInfo);
                                else {
                                    tmp = FindWay.dijkstra(stationsName.indexOf(fromStation), finalStation,
                                            vNum, stationsInfo);
                                }


                                if(tmp.size() == 1 && tmp.get(0) == -1) {
                                    if(flag == false)
                                        sendWrongDestinationMessage(locName, fromStation, toStation, cargo, wagon);
                                    else {
                                        String tmp1 = toStation;
                                        toStation = fromStation;
                                        fromStation = tmp1;
                                        wrongReserv(fromStation,toStation,locName,cargo,wagon,arrivalTime);//wrongReserv(String fromStation, String toStation, String locName, String cargo, String wagon)
                                    }
                                    if(loadedWagonCount != 0) {
                                        loadedCounter++;
                                        if(loadedCounter == loadedWagonCount) {
                                            loadedCounter = 0;
                                            loadedWagonCount = 0;

                                            moveForward();
                                        }
                                    }
                                }

                                else {

                                    requests.add(content);
                                    destination = "waiting";

                                    addBehaviour(new TickerBehaviour(thisAgent,100) {
                                        int timer = 1;
                                        @Override
                                        protected void onTick() {
                                            timer--;
                                            if (timer <= 0){
                                                Calendar cal = Calendar.getInstance();
                                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                                String currentTime = sdf.format(cal.getTime());

                                                for(int i = 0; i < requests.size(); i++) {

                                                    String[] data = requests.get(i).split(":");
                                                    String arrivalTime = data[6].replace("-",":");
                                                    int delta = CargoAgent.getTimeDelta(currentTime,arrivalTime);
                                                    String ad = requests.get(i) + ":" + delta;
                                                    requests.set(i,ad);
                                                }

                                                sort(requests);

                                                System.out.println("REQUESTS");
                                                Iterator<String> iter = requests.iterator();
                                                while(iter.hasNext()) {
                                                    String s = iter.next();
                                                    System.out.println(s);
                                                }
                                                System.out.println("---------------------");

                                                destination = requests.get(0).split(":")[3];

                                                String[] result = requests.get(0).split(":");


                                                Boolean flag = false;

                                                if(result.length == 9)
                                                    flag = true;

                                                ArrayList<Integer> tmp;

                                                if(flag == false)
                                                    tmp = FindWay.dijkstra(stationsName.indexOf(result[2]),
                                                        stationsName.indexOf(result[3]),
                                                        vNum,stationsInfo);
                                                else {

                                                    if(stationsName.indexOf(result[2]) == Integer.parseInt(result[7])) {
                                                        ArrayList<Integer> tmp1 = FindWay.dijkstra(stationsName.indexOf(result[2]),
                                                                stationsName.indexOf(result[3]),
                                                                vNum,stationsInfo);

                                                        ArrayList<Integer> tmp2 = new ArrayList<>(tmp1);
                                                        Collections.reverse(tmp1);
                                                        tmp1.remove(0);

                                                        tmp = new ArrayList<>(tmp2);
                                                        tmp.addAll(tmp1);

                                                    }
                                                    else {
                                                        tmp = FindWay.dijkstra(stationsName.indexOf(result[2]),
                                                                Integer.parseInt(result[7]),
                                                                vNum, stationsInfo);

                                                        if (!stationsName.get(tmp.get(0)).equals(result[2]) || (tmp.indexOf(stationsName.indexOf(result[3])) == -1)) {
                                                            tmp = FindWay.dijkstra(stationsName.indexOf(result[2]),
                                                                    stationsName.indexOf(result[3]),
                                                                    vNum, stationsInfo);
                                                        }
                                                    }
                                                }

                                                route = tmp;



                                                if(flag == true) {
                                                    String tmp1 = result[3];
                                                    result[3] = result[2];
                                                    result[2] = tmp1;
                                                }

                                                if(flag == false)
                                                    sendFreeNotActiveMessage(result[2], result[3], result[1], result[4], result[5]);
                                                else {
                                                    reservedSpace++;
                                                    sendFreeReservedMessage(result[2], result[3], result[1], result[4], result[5]);
                                                }

                                                if(requests.size() > 1)
                                                for(int i = 1; i < requests.size(); i++) {
                                                    String[] data = requests.get(i).split(":");
                                                    if(data.length == 9)
                                                        resendInformationMessage(data[1],data[2],data[3],data[4],data[5],data[6],data[7]);
                                                    else
                                                        resendInformationMessage(data[1],data[2],data[3],data[4],data[5],data[6]);

                                                }

                                                requests = new ArrayList();

                                                stop();
                                            }

                                        }
                                    });
                                }
                            }
                        }
                        else {
                            String locName = content.split(":")[1];
                            String fromStation = content.split(":")[2];
                            String toStation = content.split(":")[3];
                            String cargo = content.split(":")[4];
                            String wagon = content.split(":")[5];
                            String arrivalTime = "";


                            Boolean flag = false;

                            if(content.split(":").length == 7)
                                arrivalTime = content.split(":")[6];
                            if(content.split(":").length == 8) {
                                flag = true;
                                arrivalTime = content.split(":")[6];
                            }

                            if(flag == false)
                                sendWrongDestinationMessage(locName, fromStation, toStation, cargo, wagon);
                            else {
                                String tmp1 = toStation;
                                toStation = fromStation;
                                fromStation = tmp1;
                                wrongReserv(fromStation,toStation,locName,cargo,wagon,arrivalTime);//wrongReserv(String fromStation, String toStation, String locName, String cargo, String wagon)

                            }
                            if(loadedWagonCount != 0) {
                                loadedCounter++;
                                if(loadedCounter == loadedWagonCount) {
                                    loadedCounter = 0;
                                    loadedWagonCount = 0;

                                    moveForward();
                                }
                            }
                        }
                    }

                    if(content.indexOf("Запрос на прикрепление") != -1) {
                        String locName = content.split(":")[1];
                        String fromStation = content.split(":")[2];
                        String toStation = content.split(":")[3];
                        String cargoName = content.split(":")[4];
                        String wagonName = content.split(":")[5];

                        freeSpace = freeSpace - 1;

                        addBehaviour(new TickerBehaviour(thisAgent,1000) {
                            int timer = attachTime;
                            @Override
                            protected void onTick() {
                                timer--;
                                if (timer <= 0){
                                    wagons.add(wagonName);
                                    wagonAttachedMessage(locName, fromStation, toStation, cargoName, wagonName);
                                    stop();
                                }

                            }
                        });

                            addBehaviour(new TickerBehaviour(thisAgent, 1000) {
                                int timer = waitTime;

                                @Override
                                protected void onTick() {
                                    timer--;
                                    if (timer <= 0) {
                                        requestCount--;
                                        if(requestCount == 0) {

                                            if(route.size() > 1) {
                                                if(stationsName.get(route.get(0)).equals(currentStation)) {
                                                    route.remove(0);
                                                }
                                                String to = stationsName.get(route.get(0));
                                                destination = to;
                                                route.remove(0);
                                                locomotiveReadyToGoMessage(locName, fromStation, to);

                                            }

                                            else {
                                                String to = stationsName.get(route.get(0));
                                                destination = to;
                                                route.remove(0);
                                                locomotiveReadyToGoMessage(locName, fromStation, to);


                                            }
                                        }
                                        stop();
                                    }
                                }
                            });
                    }

                    if(content.indexOf("Запрос на резервирование") != -1) {
                        String locName = content.split(":")[1];
                        String fromStation = content.split(":")[2];
                        String toStation = content.split(":")[3];
                        String cargoName = content.split(":")[4];
                        String wagonName = content.split(":")[5];

                        //reservedSpace++;

                        reservedWagons.add(wagonName + ":" + toStation);

                        addBehaviour(new TickerBehaviour(thisAgent, 1000) {
                            int timer = waitTime;

                            @Override
                            protected void onTick() {
                                timer--;
                                if (timer <= 0) {
                                    requestCount--;
                                    if(requestCount == 0) {

                                        if(route.size() > 1) {

                                            if(stationsName.get(route.get(0)).equals(fromStation)) {
                                                route.remove(0);
                                            }

                                            String to = stationsName.get(route.get(0));
                                            destination = to;
                                            route.remove(0);
                                            locomotiveReadyToGoMessage(locName, fromStation, to);

                                        }

                                        else if(route.size() == 0) {
                                            ;
                                        }


                                        else {
                                            String to = stationsName.get(route.get(0));
                                            destination = to;
                                            route.remove(0);
                                            locomotiveReadyToGoMessage(locName, fromStation, to);


                                        }
                                    }
                                    stop();
                                }
                            }
                        });
                    }


                    if(content.indexOf("Новая текущая станция") != -1) {
                        String station = content.split(":")[1];
                        String locName = content.split(":")[2];
                        String toStation = content.split(":")[3];


                        currentStation = toStation;

                        tmpWagon = wagons.size();

                        addBehaviour(new TickerBehaviour(thisAgent,1000) {

                            int timer = stationsInfo[stationsName.indexOf(station)]
                                    [stationsName.indexOf(toStation)];
                            @Override
                            protected void onTick() {
                                timer--;

                                if (timer <= 0){

                                    locomotiveIsArrivedMessage(station, locName, toStation);

                                    /*
                                    if(destination != null)
                                    if(destination.equals(currentStation)) {
                                        destination = null;
                                    }
                                    */


                                    Iterator<String> iter = reservedWagons.iterator();
                                    while (iter.hasNext()) {
                                        String s = iter.next();
                                        String data = s.split(":")[1];
                                        if (data.equals(currentStation)) {
                                            iter.remove();
                                            reservedSpace--;
                                            unreserveWagonMessage(s.split(":")[0]);
                                        }
                                    }

                                    if(wagons.size() > 0) {
                                        for (int i = 0; i < wagons.size(); i++) {
                                            tryUnloadWagonMessage(toStation, locName, wagons.get(i));
                                        }
                                    }

                                    else {
                                        checkLoadedWagons(currentStation, thisAgent.getLocalName());
                                    }

                                    stop();
                                }

                            }
                        });



                    }

                    if(content.indexOf("Вагон учтен") != -1) {
                        tmpWagonCount++;


                        if(tmpWagonCount == tmpWagon) {
                            tmpWagonCount = 0;
                           //tmpWagon = 0;

                            checkLoadedWagons(currentStation, thisAgent.getLocalName());

                        }
                    }

                    if(content.indexOf("Отцепи вагон") != -1) {
                        String locName = content.split(":")[1];
                        String wagon = content.split(":")[2];
                        String station = content.split(":")[3];

                        wagons.remove(wagon);
                        freeSpace++;

                        addEmptyWagonToStation(wagon,station);
                    }

                    if(content.indexOf("Нет загруженных вагонов") != -1) {
                        // Если маршрут не нулевый двигаемся дальше, иначе чекаем нужждающихся
                        moveForward();
                    }

                    if(content.indexOf("Количество загруженных вагонов") != -1) {
                        loadedWagonCount = Integer.parseInt(content.split(":")[1]);
                    }

                    if(content.indexOf("Здесь есть неразгруженный вагон") != -1) {
                        String locName = content.split(":")[1];
                        String station = content.split(":")[2];

                        route = FindWay.dijkstra(stationsName.indexOf(currentStation),
                                stationsName.indexOf(station),vNum,
                                stationsInfo);


                        route.remove(0);
                        moveForward();
                    }

                    /*
                    if(content.indexOf("Уменьши свободное место") != -1) {
                        freeSpace--;
                    }
                    */

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

    public void unreserveWagonMessage(String wagon) {
        String text = "Сними резервирование";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void resendInformationMessage(String name, String fromStation, String toStation, String cargo, String wagon, String arrivalTime, String flag) {
        String text = "Запрос информации о локомотиве";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + name + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon + ":" + arrivalTime + ":" + flag);
        msg.addReceiver(new AID(name,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void resendInformationMessage(String name, String fromStation, String toStation, String cargo, String wagon, String arrivalTime) {
        String text = "Запрос информации о локомотиве";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + name + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(name,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public static void sort(ArrayList<String> arr) {
        for (int min = 0; min < arr.size() - 1; min++) {
            int least = min;
            for (int j = min + 1; j < arr.size(); j++) {
                int jIndex = 0;
                int leastIndex = 0;
                if(arr.get(j).split(":").length == 9)
                    jIndex = 8;
                else
                    jIndex = 7;
                if(arr.get(least).split(":").length == 9)
                    leastIndex = 8;
                else
                    leastIndex = 7;

                if (Integer.parseInt(arr.get(j).split(":")[jIndex]) < Integer.parseInt(arr.get(least).split(":")[leastIndex])) {
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


    public void sendStationsInfo() {
        String text = "Запрос информации о станциях";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text);

        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void moveForward() {
        if(route == null || route.size() == 0) {
            destination = null;
            route = null;

            addBehaviour(new TickerBehaviour(thisAgent, 1000) {
                int timer = waitTime;

                @Override
                protected void onTick() {
                    timer--;
                    if (timer <= 0) {
                        findUndeliveredCargo();
                        stop();
                    }
                }
            });


           // findUndeliveredCargo();
        }
        else {

            if(stationsName.get(route.get(0)).equals(currentStation)) {
                route.remove(0);
            }

            String toStation = stationsName.get(route.get(0));
            route.remove(0);
            destination = toStation;
            locomotiveReadyToGoMessage(thisAgent.getLocalName(), currentStation, toStation);
        }
        /*
        if(route.size() > 0) {
            String toStation = stationsName.get(route.get(0));
            route.remove(0);
            destination = toStation;
            locomotiveReadyToGoMessage(thisAgent.getLocalName(), currentStation, toStation);
        }
        else {
            destination = null;
            route = null;
            findUndeliveredCargo();
        }
        */
    }

    public void findUndeliveredCargo() {
        String text = "Есть недоставленные вагоны?";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + thisAgent.getLocalName());

        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void checkLoadedWagons(String station, String locName) {
        String text = "Есть загруженные вагоны?";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName);

        msg.addReceiver(new AID(station,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void addEmptyWagonToStation(String wagon, String station) {
        String text = "Прибыл пустой вагон";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + wagon + ":" + station);

        msg.addReceiver(new AID(station,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void locomotiveIsArrivedMessage(String station, String locName, String toStation) {
        String text = "Новая текущая станция";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName + ":" + toStation);

        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        //System.out.println(msg.getContent());

        send(msg);
    }

    public void tryUnloadWagonMessage(String station, String locName, String wagon) {
        String text = "Разгрузись";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName + ":" + wagon);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void locomotiveReadyToGoMessage(String locName, String fromStation, String toStation) {
        String text = "Локомотив готов к отправке";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + fromStation + ":" + toStation);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }


    public void sendWrongDestinationMessage(String locName, String fromStation, String toStation, String cargo, String wagon) {
        String text = "Локомотив движется в другом направлении";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void wrongReserv(String fromStation, String toStation, String locName, String cargo, String wagon, String arrivalTime) {
        String text = "Локомотив не подходит для резервирования";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
       // msg.setContent(text + ":" + locName + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon + ":" + arrivalTime);
        msg.setContent(text + ":" + locName + ":" +toStation + ":" + fromStation + ":" + cargo + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void wagonAttachedMessage(String locName, String fromStation, String toStation, String cargo, String wagon) {
        String text = "Вагон прикреплен";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void sendFreeNotActiveMessage(String fromStation, String toStation, String locName, String cargo, String wagon) {
        String text = "Локомотив свободен и стоит на станции";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" +fromStation + ":" + toStation + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        requestCount++;

        send(msg);
    }

    public void sendFreeReservedMessage(String fromStation, String toStation, String locName, String cargo, String wagon) {
        String text = "Локомотив свободен для резервирования";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + toStation + ":" + fromStation + ":" + cargo + ":" + wagon);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        requestCount++;

        send(msg);
    }
}

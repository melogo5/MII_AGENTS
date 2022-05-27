package com.company;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CargoAgent extends Agent {

    String currentStation;
    String destination;

    String departureTime;
    String currentTime;
    String arrivalTime;

    int cargoLoadTime = 1;
    int cargoUnloadTime = 1;

    String dispatcherName = "Disp";

    String status = "Ожидает погрузки";

    @Override
    public void setup(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        currentTime = sdf.format(cal.getTime());

        //cal.add(Calendar.MILLISECOND,3000);

        Object args[] = getArguments();

        currentStation = args[0] + "";
        destination = args[1] + "";
        departureTime = args[2] + "";
        arrivalTime = args[3] + "";

        //int delta = getTimeDelta(currentTime, departureTime);
        int delta = 0;

        Agent thisAgent = this;

        if(delta > 0) {
            addBehaviour(new TickerBehaviour(this,1000) {
                int timer = delta;
                @Override
                protected void onTick() {
                    timer--;
                    if (timer <= 0){
                       sendMessageToDispatcher();
                       stop();
                    }
                }
            });
        }
        else {
           sendMessageToDispatcher();
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if(msg != null){
                    String content = msg.getContent();

                    if(content.indexOf("Время отправляться?") != -1) {
                        if(status.equals("Погружен")) {
                            String from = currentStation;
                            String to = destination;
                            String cargo = thisAgent.getLocalName();
                            String wagon = msg.getSender().getLocalName();
                            String t = arrivalTime.replace(":","-");
                            findLoc(from,to,cargo,wagon,t);
                        }
                    }

                    if(content.indexOf("Погрузка в вагон") != -1) {
                        addBehaviour(new TickerBehaviour(thisAgent,1000) {
                            int timer = cargoLoadTime;
                            @Override
                            protected void onTick() {
                                timer--;
                                if (timer <= 0){
                                    String cargoName = content.split(":")[3];
                                    String wagon = content.split(":")[4];

                                    String from = content.split(":")[1];
                                    String to = content.split(":")[2];

                                    status = "Погружен";

                                    String t = arrivalTime.replace(":","-");

                                    sendLoadingOverMessage(from,to,cargoName,wagon, t);

                                    stop();
                                }
                            }
                        });
                    }

                    if(content.indexOf("Попробуй разгрузиться") != -1) {
                        String station = content.split(":")[1];
                        String locName = content.split(":")[2];
                        String wagon = content.split(":")[3];
                        String cargo = content.split(":")[4];

                        if(station.equals(destination)) {

                            currentStation = destination;

                            cargoConsiderMessage(locName);

                            wagonIsEmptyMessage(locName,wagon,station);


                            updateLogWagonRemove(locName,wagon,station);

                            addBehaviour(new TickerBehaviour(thisAgent,1000) {
                                int timer = cargoUnloadTime;
                                @Override
                                protected void onTick() {
                                    timer--;
                                    if (timer <= 0){
                                        status = "Разгружен";
                                        cargoIsUnloaded(station, locName, wagon, cargo);

                                        stop();
                                    }
                                }
                            });
                        }

                        else {
                            cargoConsiderMessage(station,locName,wagon,cargo);
                        }
                    }

                    if(content.indexOf("Подходит локомотив?") != -1) {
                        String wagon = content.split(":")[1];
                        String locName = content.split(":")[2];
                        String cargo = content.split(":")[3];
                        String t = arrivalTime.replace(":","-");

                        tryToAttachMessage(locName, currentStation, destination,cargo, wagon, t);
                    }

                    if(content.indexOf("Груз отправляется в путь") != -1) {
                        status = "В пути";
                    }
                }
            }
        });

    }

    public void updateLogWagonRemove(String locName,String wagon,String station) {
        String text = "Локомотив отцепил вагон";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + wagon + ":" + station);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

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

    public void findLoc(String from, String to, String cargo, String wagon, String arrivalTime) {
        String text = "Нет локомотивов на станции";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + from + ":" + to + ":" + cargo + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(wagon,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    public void tryToAttachMessage(String locName, String fromStation, String toStation, String cargo, String wagon, String arrivalTime) {
        String text = "Запрос информации о локомотиве";
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + locName + ":" + fromStation + ":" + toStation + ":" + cargo + ":" + wagon + ":" + arrivalTime);
        msg.addReceiver(new AID(locName,AID.ISLOCALNAME));

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

    public void cargoIsUnloaded(String station, String locName, String wagon, String cargo) {
        String text = "Груз разгружен";

        String t = arrivalTime.replace(":","-");

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + station + ":" + locName + ":" +wagon + ":" + cargo + ":" + t);
        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    //Груз погружен в вагон:[from]:[to]:[cargoName]:[wagonName]
    public void sendLoadingOverMessage(String from, String to, String cargoName, String wagonName, String arrivalTime) {
        String text = "Груз погружен в выгон";

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setContent(text + ":" + from + ":" + to + ":" +cargoName + ":" + wagonName + ":" + arrivalTime);
        msg.addReceiver(new AID(wagonName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());

        send(msg);
    }

    //Готов к отправке:[from]:[to]
    public void sendMessageToDispatcher(){
        String text = "Готов к отправке";
        String reciever = DispatcherAgent.name;

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        String t = arrivalTime.replace(":","-");
        String dep = departureTime.replace(":","-");

        msg.setContent(text + ":" + currentStation + ":" + destination + ":" + dep + ":" + t);
        //msg.addReceiver(new AID(reciever,false));

        //msg.addReceiver(new AID("Disp" + Address.address));

        msg.addReceiver(new AID(dispatcherName,AID.ISLOCALNAME));

        System.out.println(msg.getContent());


        send(msg);
    }

    static public int getTimeDelta(String startTime, String endTime){
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date d = new Date();
        try {
            d = dateFormat.parse(startTime);
            long startMills = d.getTime();
            d = dateFormat.parse(endTime);
            long endMillis = d.getTime();
            long delta = endMillis - startMills;

            return  (int)(delta / 1000);
        }
        catch (Exception ex){System.out.print(ex);}
        return 0;
    }
}
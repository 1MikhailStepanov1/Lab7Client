import request.AnswerReader;
import request.RequestSender;
import userPart.Session;
import userPart.UserManager;
import utility.CommandReader;
import utility.Console;
import utility.Invoker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        InetAddress serverAddress = null;
        InetAddress clientAddress = null;
        int port = 9898;
        try {
            serverAddress = InetAddress.getLocalHost();
            clientAddress = InetAddress.getLocalHost();
            if (args.length != 0 && args[0].contains(":")){
                serverAddress = InetAddress.getByName(args[0].split(":")[0]);
                port = Integer.parseInt(args[0].split(":")[1]);
            }else {
                System.out.println("Server IP wasn't found. Default value localhost:9898 will be used.");
            }
        } catch (UnknownHostException exception) {
            System.out.println(exception.getMessage());
            return;
        } catch (NumberFormatException exception){
            System.out.println("Incorrect format of port.");
        }
        SocketAddress socketAddress = new InetSocketAddress(serverAddress, port);
        DatagramChannel datagramChannel;
        try {
            datagramChannel = DatagramChannel.open();
            datagramChannel.bind(new InetSocketAddress(clientAddress, 0));
            datagramChannel.configureBlocking(false);
            System.out.println(datagramChannel.getLocalAddress());
        } catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
        Session session = null;
        AnswerReader answerReader = new AnswerReader(datagramChannel, socketAddress);
        Scanner scanner = new Scanner(System.in);
        Console console = new Console(scanner, answerReader);
        Invoker invoker = new Invoker();
        invoker.initMap(datagramChannel, socketAddress, console, invoker);
        System.out.println("To work with database you need to register/authorize in the system. Enter one letter of action you will do: r/a.");
        String line = console.readln();
        while (session == null) {
            while (!line.trim().equals("r") && !line.trim().equals("a")) {
                System.out.println("Input is incorrect.");
                line = console.readln();
            }
            if (line.equals("r")) {
                if (UserManager.registerUser(console, new RequestSender(datagramChannel, socketAddress), answerReader)) {
                    System.out.println("User registered successfully. Please, authorize.");
                    session = UserManager.authorizeUser(console, new RequestSender(datagramChannel, socketAddress), answerReader);
                    session.setAuthorized(true);
                }
            } else {
                session = UserManager.authorizeUser(console, new RequestSender(datagramChannel, socketAddress), answerReader);
                session.setAuthorized(true);
            }
        }
        invoker.setSession(session);
        CommandReader commandReader = new CommandReader(console, invoker, answerReader);
        commandReader.activeMode();
        }
}

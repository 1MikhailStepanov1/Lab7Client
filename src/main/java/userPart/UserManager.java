package userPart;

import exceptions.ServerIsNotAvailableException;
import request.AnswerReader;
import request.RequestSender;
import request.SerializationFromClient;
import utility.Console;

public class UserManager {
    public static Session authorizeUser(Console console, RequestSender requestSender, AnswerReader answerReader) {
        System.out.println("Enter your name:");
        String name = console.readln();
        requestSender.sendRequest(new SerializationFromClient("isRegister", null, null, name, null));
        try {
            if (answerReader.readValidation()){
                int count = 3;
                System.out.println("Hello, " + name + "! Enter your password (you have 3 attempts):");
                while (count != 0){
                    String password = console.readln();
                    requestSender.sendRequest(new SerializationFromClient("authorize", null, null, name, password));
                    if (answerReader.readValidation()){
                        System.out.println("Welcome!");
                        return new Session(name, password);
                    } else System.out.println("Hmm... May be you will try again? You have " + (count-1) + " more attempts.");
                    count--;
                }
                System.out.println("Sorry, you haven't enter correct password 3 times. Program will be closed.");
                System.exit(0);
            }
        } catch (ServerIsNotAvailableException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static boolean registerUser(Console console, RequestSender requestSender, AnswerReader answerReader) {
        System.out.println("Enter your name:");
        String name = console.readln();
        requestSender.sendRequest(new SerializationFromClient("isRegister", null, null, name, null));
        try {
            if (!answerReader.readValidation()){
                System.out.println("Enter your password:");
                String password = console.readln();
                requestSender.sendRequest(new SerializationFromClient("register", null,null, name, password));
                return answerReader.readValidation();
            } else {
                System.out.println("User with the same name is already registered. Please, try again.");
                return false;
            }
        } catch (ServerIsNotAvailableException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}

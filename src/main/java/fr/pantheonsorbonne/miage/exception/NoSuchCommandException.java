package fr.pantheonsorbonne.miage.exception;

public class NoSuchCommandException extends Exception{
    public NoSuchCommandException (String command){
        super("The command " + command + "doesn't exist.");
    }
}

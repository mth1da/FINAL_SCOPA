package fr.pantheonsorbonne.miage.exception;

public class NoSuchPlayerException extends Throwable {
    public NoSuchPlayerException(String playerName){
        super("No player called <"+playerName+ "> in game");
    }
    
}

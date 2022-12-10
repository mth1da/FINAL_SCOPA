package fr.pantheonsorbonne.miage.exception;

public class TotalCollectedCardException extends Exception {
    public TotalCollectedCardException(int count){
        super("Total collected card is "+count+" (must be 40)");
    }
}
package Dominio;

public class IntWrapper {
    private int value;

    public IntWrapper(){}

    public IntWrapper(int value){ this.value = value; }

    public int getValue(){ return value; }

    public void setValue(int value){ this.value = value; }

    public void addToValue(int num) { value += num;}
}

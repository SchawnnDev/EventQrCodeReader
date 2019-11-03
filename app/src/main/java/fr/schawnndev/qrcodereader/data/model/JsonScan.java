package fr.schawnndev.qrcodereader.data.model;

public class JsonScan {

    private String firstName;
    private String lastName;
    private boolean hasPaid;
    private boolean alreadyScanned;
    private double toPay;

    public JsonScan(String firstName, String lastName, boolean hasPaid, boolean alreadyScanned, double toPay)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.hasPaid = hasPaid;
        this.alreadyScanned = alreadyScanned;
        this.toPay = toPay;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isHasPaid() {
        return hasPaid;
    }

    public boolean isAlreadyScanned() {
        return alreadyScanned;
    }

    public double getToPay() {
        return toPay;
    }
}

package fr.schawnndev.qrcodereader.data.model;

public class JsonScan {

    private String firstName;
    private String lastName;
    private boolean hasPaid;
    private boolean alreadyScanned;
    private double toPay;
    private String lastScanDate;

    public JsonScan() {
    }

    public JsonScan(String firstName, String lastName, boolean hasPaid, boolean alreadyScanned, double toPay, String lastScanDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.hasPaid = hasPaid;
        this.alreadyScanned = alreadyScanned;
        this.toPay = toPay;
        this.lastScanDate = lastScanDate;
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

    public String getLastScanDate() {
        return lastScanDate;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setHasPaid(boolean hasPaid) {
        this.hasPaid = hasPaid;
    }

    public void setAlreadyScanned(boolean alreadyScanned) {
        this.alreadyScanned = alreadyScanned;
    }

    public void setToPay(double toPay) {
        this.toPay = toPay;
    }

    public void setLastScanDate(String lastScanDate) {
        this.lastScanDate = lastScanDate;
    }
}

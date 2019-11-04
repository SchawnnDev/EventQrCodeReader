package fr.schawnndev.qrcodereader.data.model;

public class JsonStats {

    private int ticketsCount;
    private int scannedTicketsCount;
    private int payedTicketsCount;
    private int scannedPayedTicketsCount;

    public JsonStats(int ticketsCount, int scannedTicketsCount, int payedTicketsCount, int scannedPayedTicketsCount) {
        this.ticketsCount = ticketsCount;
        this.scannedTicketsCount = scannedTicketsCount;
        this.payedTicketsCount = payedTicketsCount;
        this.scannedPayedTicketsCount = scannedPayedTicketsCount;
    }

    public int getTicketsCount() {
        return ticketsCount;
    }

    public int getScannedTicketsCount() {
        return scannedTicketsCount;
    }

    public int getPayedTicketsCount() {
        return payedTicketsCount;
    }

    public int getScannedPayedTicketsCount() {
        return scannedPayedTicketsCount;
    }
}

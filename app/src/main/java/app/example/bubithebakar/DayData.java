package app.example.bubithebakar;

public class DayData {
    private int tikufim;
    private int knasot;
    private int bakarot;

    public DayData() {
    }

    public DayData(int tikufim, int knasot, int bakarot) {
        this.tikufim = tikufim;
        this.knasot = knasot;
        this.bakarot = bakarot;

    }

    public int getTikufim() {
        return tikufim;
    }

    public int getKnasot() {
        return knasot;
    }

    public int getBakarot() {
        return bakarot;
    }

    @Override
    public String toString() {
        return "בקרות: " + bakarot + ", תיקופים: " + tikufim + ", קנסות: " + knasot;
    }
}

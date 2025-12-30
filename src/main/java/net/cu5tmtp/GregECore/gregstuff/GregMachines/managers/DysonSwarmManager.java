package net.cu5tmtp.GregECore.gregstuff.GregMachines.managers;

public class DysonSwarmManager {

    private static double totalSails = 0;

    public static void addSail() {
        totalSails++;
    }

    public static double getTotalSails() {
        return totalSails;
    }

    public static void setTotalSails(double number) {
        DysonSwarmManager.totalSails = number + totalSails;
    }

    public static double getBoost() {
        if (totalSails > 500000) return 500;
        if (totalSails > 150000) return 50;
        if (totalSails > 50) return 5;
        return 1;
    }
}
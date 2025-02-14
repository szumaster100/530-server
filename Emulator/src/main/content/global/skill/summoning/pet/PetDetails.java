package content.global.skill.summoning.pet;

public final class PetDetails {

    private double hunger = 0.0;

    private double growth = 0.0;

    private int individual;

    public PetDetails(double growth) {
        this.growth = growth;
    }

    public void updateHunger(double amount) {
        hunger += amount;
        if (hunger < 0.0) {
            hunger = 0.0;
        }
    }

    public void updateGrowth(double amount) {
        growth += amount;
        if (growth < 0.0) {
            growth = 0.0;
        } else if (growth > 100.0) {
            growth = 100.0;
        }
    }

    public double getHunger() {
        return hunger;
    }

    public double getGrowth() {
        return growth;
    }

    public void setIndividual(int individual) {
        this.individual = individual;
    }

    public int getIndividual() {
        return individual;
    }
}

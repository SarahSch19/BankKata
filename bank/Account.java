package bank;

class Account {

    private String name;
    private int balance;
    private int threshold;
    private Boolean blocked;

    public Account(String name, int balance, int threshold) {
        this.name = name;
        this.balance = balance;
        this.threshold = threshold;
    }

    // Methods
    // TODO

    public String toString() {
        // TODO
        return this.getName() + " | " + this.getBalance() + " | " +
                this.getThreshold() + " | " + this.getBlocked();
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public int getThreshold() {
        return threshold;
    }

    public Boolean getBlocked() {
        return blocked;
    }


    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }
}

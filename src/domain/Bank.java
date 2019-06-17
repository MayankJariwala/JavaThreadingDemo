package domain;


/**
 * This class will be a banks thread which will be started by master thread and
 * this class method will be responsible to notify master thread
 *
 * @author Mayank Jariwala
 * @version 0.1.0
 */
public class Bank implements Runnable {

    private String bankName;
    private int loanAmount;
    private volatile boolean isFinished = false;


    Bank(String bankName, int loanAmount) {
        this.bankName = bankName;
        this.loanAmount = loanAmount;
    }

    String getBankName() {
        return bankName;
    }

    int getLoanAmount() {
        return loanAmount;
    }

    synchronized boolean processLoanRequest(Customer customer, int requestedAmount) {
        if (this.loanAmount >= requestedAmount) {
            this.loanAmount -= requestedAmount;
            Master.printMessageFromUser(this.getBankName() + " approves a loan of " + requestedAmount + " dollar(s) from " + customer.getCustomerName());
            return true;
        }
        if (this.loanAmount <= 0) {
            isFinished = true;
        }
        //bank has balance but cannot help specific customer with certain amount
        Master.printMessageFromUser(this.getBankName() + " denies a loan of " + requestedAmount + " dollar(s) from " + customer.getCustomerName());
        return false;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "bankName='" + bankName + '\'' +
                ", loanAmount=" + loanAmount +
                '}';
    }

    @Override
    public void run() {
        while (!isFinished) {
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
        }
        if (loanAmount > 0)
            Master.messagesList.add(this.getBankName() + " bank has " + this.loanAmount + " dollar(s) remaining");
    }
}

package domain;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class will be a customers thread which will be started by master thread and
 * this class method will be responsible to notify master thread
 *
 * @author Mayank Jariwala
 * @version 0.1.0
 */
public class Customer implements Runnable {

    private Random random = new Random();
    private String customerName;
    private int loanNeeded, initialLoanAmount, bankIndex = -1;
    private ArrayList<Bank> bankSet = new ArrayList<>();

    Customer(String customerName, int loanNeeded) {
        this.customerName = customerName;
        this.loanNeeded = loanNeeded;
        this.initialLoanAmount = loanNeeded;
    }

    String getCustomerName() {
        return customerName;
    }

    int getLoanNeeded() {
        return loanNeeded;
    }

    void setBankSet(ArrayList<Bank> bankSet) {
        this.bankSet = bankSet;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerName='" + customerName + '\'' +
                ", loanNeeded=" + loanNeeded +
                '}';
    }

    @Override
    public void run() {
        while (bankSet.size() != 0) {
            if (this.loanNeeded <= 0)
                break;
            if (bankIndex == -1) {
                int totalBank = bankSet.size();
                bankIndex = random.nextInt(totalBank);
            }
            try {
                Bank randomChosenBank = bankSet.get(bankIndex);
                int loanAmountRequested = getRandomDollars(loanNeeded);
                Thread.sleep(1300);
                Master.printMessageFromUser(this.customerName + " requests a loan of " + loanAmountRequested + " dollar(s) from " + randomChosenBank.getBankName());
                boolean requestStatus = randomChosenBank.processLoanRequest(this, loanAmountRequested);
                if (requestStatus) {
                    this.loanNeeded -= loanAmountRequested;
                } else {
                    bankSet.remove(randomChosenBank);
                    System.err.println(this.customerName + " removed  bank " + randomChosenBank.getBankName() + " " + this.bankSet);
                    bankIndex = -1;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IndexOutOfBoundsException e) {
//                System.err.println("Exception Occurred " + e.getLocalizedMessage());
                bankIndex = -1;
            }
        }
        String data;
        if (this.loanNeeded == 0)
            data = this.customerName + " has reached the objective of " + this.initialLoanAmount + " dollar(s). Woo Hoo!";
        else
            data = this.customerName + " was only able to borrow " + Math.abs(this.initialLoanAmount - this.loanNeeded) + " dollar(s). Boo Hoo!";
        Master.notifyStatus(data);
    }

    private int getRandomDollars(int balance) {
        if (balance >= 50)
            return random.nextInt((50 - 1) + 1) + 1;
        else
            return random.nextInt((balance - 1) + 1) + 1;
    }
}

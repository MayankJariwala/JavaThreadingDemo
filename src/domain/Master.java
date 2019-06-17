package domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * This class will be a master thread process which is responsible for having spawing
 * child threads call bank and customer
 *
 * @author Mayank Jariwala
 * @version 0.1.0
 */
public class Master implements Runnable {

    private static int noOfCustomers = 0;
    private ArrayList<Bank> banksList = new ArrayList<>(3);
    static ArrayList<String> messagesList = new ArrayList<>();
    private ArrayList<Customer> customerList = new ArrayList<>(3);
    private static Set<Thread> bankProcess = new HashSet<>(3);
    private static Set<Thread> customerProcess = new HashSet<>(3);

    // Called by main thread
    @Override
    public void run() {
        File bankFile = new File("banks.txt");
        File customerFile = new File("customers.txt");
        readFile(bankFile, "bank");
        readFile(customerFile, "customers");
        printSituation("banks");
        printSituation("customers");
        customerProcess.forEach(Thread::start);
        bankProcess.forEach(Thread::start);
    }


    // Simply ReadFile and create list of customer and bank
    private void readFile(File file, String user) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String readLine;
            while ((readLine = bufferedReader.readLine()) != null) {
                readLine = readLine.replaceAll("[{}.]", "");
                String[] splittedData = readLine.split(",", 2);
                if (user.equals("bank")) {
                    Bank bank = new Bank(splittedData[0], Integer.parseInt(splittedData[1]));
                    banksList.add(bank);
                } else {
                    Customer customer = new Customer(splittedData[0], Integer.parseInt(splittedData[1]));
                    customerList.add(customer);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
        }
    }

    // Simply print situation of what bank with how much loan and customer with amount required
    private void printSituation(String user) {
        if (user.equals("banks")) {
            System.out.println("* Bank and financial resources **");
            banksList.forEach(bank -> {
                bankProcess.add(new Thread(bank, bank.getBankName()));
                System.out.println(bank.getBankName() + ": " + bank.getLoanAmount());
            });
        } else {
            System.out.println("** Customers and loan objectives **");
            customerList.forEach(customer -> {
                customerProcess.add(new Thread(customer, customer.getCustomerName()));
                ArrayList<Bank> customerBankList = new ArrayList<>(banksList);
                customer.setBankSet(customerBankList);
                System.out.println(customer.getCustomerName() + ": " + customer.getLoanNeeded());
            });
            System.out.print(System.lineSeparator());
        }
    }

    // Print whatever child thread says 
    static void printMessageFromUser(String message) {
        System.out.println(message);
    }

    /* Child once completed will notify master and master will persist their response and once every customer
       is finished it will stop all banks externally and print the value persisted by master */
    static void notifyStatus(String message) {
        messagesList.add(message);
        ++noOfCustomers;
        if (noOfCustomers == customerProcess.size()) {
            Master.bankProcess.forEach(thread -> {
                if (!thread.getState().equals(Thread.State.TERMINATED)) {
                    thread.interrupt();
                }
            });
            messagesList.forEach(System.out::println);
        }
    }
}

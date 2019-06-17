import domain.Master;

/**
 * Simple Driver Class
 *
 * @author Mayank Jariwala
 * @version 0.1.0
 */
public class Driver {

    // Start Master thread
    public static void main(String[] args) {
        Master master = new Master();
        master.run();
    }

}

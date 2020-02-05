import java.io.IOException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws IOException {
        Service service = new Service();
        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();
        while (i < 2) {
            service.run(i);
            i = sc.nextInt();
        }
    }

}

import model.Territory;

import java.text.MessageFormat;
import java.util.Scanner;

public class TestProgram {

    public static void main(String[] args) {
        Territory t = new Territory(9, 10);

        t.placeActor(7, 5);

        t.placeShelf(5, 5);
        t.placeShelf(3, 5);
        t.placeShelf(3, 6);
        t.placeShelf(3, 7);
        t.placeShelf(3, 8);

        t.placeCart(1, 2);
        t.placeCart(6, 4);

        t.placePresent(1, 1);
        t.placePresent(5, 5);
        t.placePresent(1, 7);
        t.placePresent(2, 3);
        t.placePresent(1, 4);
        t.placePresent(1, 2);

        t.print();
        System.out.println();
        t.resizeTerritory(6, 7);
        t.print();
        System.out.println();
        t.resizeTerritory(9, 12);

        Scanner s = new Scanner(System.in);
        boolean value = true;
        while (value) {
            t.print();

            System.out.println(MessageFormat.format("Wall ahead:\t\t{0}", t.wallAhead()));
            System.out.println(MessageFormat.format("cart ahead:\t\t{0}", t.cartAhead()));
            System.out.println(MessageFormat.format("pushable:\t\t{0}", t.pushable()));
            System.out.println(MessageFormat.format("present here:\t{0}", t.presentHere()));
            System.out.println(MessageFormat.format("basket empty:\t{0}", t.basketEmpty()));

            char x = s.next().charAt(0);

            switch (x) {
                case 'Q':
                    value = false;
                    break;
                case 'q':
                    t.turnLeft();
                    break;
                case 'e':
                    t.turnRight();
                    break;
                case 'w':
                    t.forward();
                    break;
                case 'a':
                    t.pickUp();
                    break;
                case 'd':
                    t.putDown();
                    break;
            }
        }
        s.close();
    }
}

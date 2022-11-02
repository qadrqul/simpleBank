package kg.kadyr.utils;
import kg.kadyr.Main;

import java.util.*;


public class LuhnAlgorithm {
    private static final Random random = new Random();
    private static ArrayList<Integer> cardStartNumbers = null;
    private static Long convertListToLong(List<Integer> list) {
        StringBuilder numberStringBuilder = new StringBuilder();

        for (int i : list) {
            numberStringBuilder.append(i);
        }

        return Long.parseLong(numberStringBuilder.toString());
    }
    public static long generate() {
        if (cardStartNumbers == null) {
            cardStartNumbers = new ArrayList<>();

            short counter = 0;
            for (long i : (ArrayList<Long>) Main.config.get("cardStartNumbers")) {
                counter++;

                if (counter > 6) {
                    break;
                } else {
                    cardStartNumbers.add((int) i);
                }
            }
        }
        ArrayList<Integer> cardNumberList = (ArrayList<Integer>) cardStartNumbers.clone(),
                multipliedOddNumbersCardNumberList = new ArrayList<>();
        short sum, index = 0;
        int finalNumber = 0;

        for (int i = cardNumberList.toArray().length; i < 15; i++) {
            cardNumberList.add(random.nextInt(10));
        }

        for (int i : cardNumberList) {
            if (index % 2 == 0 || index == 0) {
                if (i * 2 > 9) {
                    i = ((i * 2 - 10) + 1);
                } else {
                    i *= 2;
                }
            }
            multipliedOddNumbersCardNumberList.add(i);
            index++;
        }

        sum = (short) multipliedOddNumbersCardNumberList.stream().mapToInt(x -> x).sum();

        if (sum % 10 != 0) {
            while (sum % 10 != 0) {
                sum++; finalNumber++;
            }
        }

        cardNumberList.add(finalNumber);
        return convertListToLong(cardNumberList);
    }
    public static boolean numberLuhnAlgorithmCheck(long number) {
        ArrayList<Integer> cardNumberList = new ArrayList<>(),
                multipliedOddNumbersCardNumberList = new ArrayList<>();;
        short sum, index = 0;

        for (char i : String.valueOf(number).toCharArray()) {
            cardNumberList.add(Integer.parseInt(String.valueOf(i)));
        }

        for (int i : cardNumberList) {
            if (index % 2 == 0 || index == 0) {
                if (i * 2 > 9) {
                    i = ((i * 2 - 10) + 1);
                } else {
                    i *= 2;
                }
            }
            multipliedOddNumbersCardNumberList.add(i);
            index++;
        }

        sum = (short) multipliedOddNumbersCardNumberList.stream().mapToInt(a -> a).sum();

        return sum % 10 == 0;
    }
}

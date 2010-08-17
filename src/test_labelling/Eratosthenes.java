package test_labelling;

import java.util.ArrayList;
import java.util.List;

public class Eratosthenes {

    public static void sift(List<Integer> numbers, int prime, int start) {
        for (int i = start + 1; i < numbers.size(); i++) {
            int number = numbers.get(i);
            if (number % prime == 0) {
                numbers.remove(i);
                i++;
            }
        }
    }
    
    public static void sift(int max) {
        List<Integer> numbers = new ArrayList<Integer>();
        for (int i = 2; i <= max; i++) {
            numbers.add(i);
        }
        int prime = 2;
        int start = 0;
        while (prime * prime < max) {
            sift(numbers, prime, start);
            prime = numbers.get(start);
            start++;
        }
        System.out.println(numbers);
    }
    
    public static void main(String[] args) {
        Eratosthenes.sift(100);
    }
    
}

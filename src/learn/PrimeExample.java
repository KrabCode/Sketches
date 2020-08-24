package learn;

public class PrimeExample {
    public static void main(String[] args) {
        checkPrime(1);
        checkPrime(2);
        checkPrime(3);
        checkPrime(4);
        checkPrime(5);
        checkPrime(6);
        checkPrime(7);
        checkPrime(8);
        checkPrime(36);
        checkPrime(37);
        checkPrime(38);
    }

    static void checkPrime(int n) {
        boolean isPrime = true; // assume it is prime before disproving it
        if (n < 2) {
            isPrime = false;
        } else {
            for (int i = 2; i < n; i++) {
                if (n % i == 0) {
                    isPrime = false;
                    break;
                }
            }
        }
        if (isPrime) {
            System.out.println(n + " is good prime boy");
        } else {
            System.out.println(n + " is not prime");
        }
    }
}

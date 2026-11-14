
package task;

import java.util.BitSet;

public class TaskPrimeNumbers extends TaskImpl {

    @Override
    public void execute() {
        try {
            int limit = Integer.parseInt(input.trim());

            if (limit < 2 || limit > 100_000_000) {
                result = "Error: The limit must be between 2 and 100000000.";
                return;
            }

            BitSet isPrime = new BitSet(limit + 1);

            // Initially, consider all numbers from 2 to limit as prime.
            isPrime.set(2, limit + 1);

            // Apply the Sieve of Eratosthenes.
            for (int p = 2; p <= limit / p; p++) {

                if (isPrime.get(p)) {

                    for (int multiple = p * p;
                         multiple <= limit;
                         multiple += p) {

                        isPrime.clear(multiple);
                    }
                }
            }

            int primeCount = isPrime.cardinality();

            result = "Number of prime numbers between 2 and "
                    + limit + " is: " + primeCount;

        } catch (NumberFormatException e) {
            result = "Error: Please enter a valid integer.";
        }
    }
}

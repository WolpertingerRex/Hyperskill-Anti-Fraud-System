package antifraud.business.creditcard;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Stream;

public class CreditCardValidator implements ConstraintValidator<CreditCardConstraint, String> {

    @Override
    public void initialize(CreditCardConstraint constraint) {
    }

    @Override
    public boolean isValid(String number, ConstraintValidatorContext context) {

        if(number == null || number.length() < 16 || !number.matches("[0-9]+")) return false;

        int[] array = Stream.of(number.split("")).mapToInt(Integer::parseInt).toArray();

        int[] copy = Arrays.copyOfRange(array, 0, 15);
        int checkSum = checkSum(copy);
        return checkSum == array[15];
    }


    private int checkSum(int[] array) {
        int sum = 0;
        for (int i = 0; i < array.length; i++) {
            if (i % 2 == 0) array[i] = array[i] * 2;
            if (array[i] > 9) array[i] -= 9;
            sum += array[i];
        }
        int checkSum = 0;
        while ((sum + checkSum) % 10 != 0) {
            checkSum++;
        }
        return checkSum;
    }
}

package antifraud.business.ip;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IPValidator implements ConstraintValidator<IPConstraint, String> {
    private static final String IPV4_PATTERN =
            "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

    @Override
    public void initialize(IPConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(String ip, ConstraintValidatorContext context) {
        if (ip == null) return false;
        return ip.matches(IPV4_PATTERN);
    }
}

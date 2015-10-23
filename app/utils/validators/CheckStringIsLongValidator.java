package utils.validators;

import play.data.validation.Constraints;
import play.libs.F;

public class CheckStringIsLongValidator extends Constraints.Validator<String> {

    @Override
    public boolean isValid(String str) {
        try {
            long l = Long.parseLong(str);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    @Override
    public F.Tuple<String, Object[]> getErrorMessageKey() {
        return new F.Tuple<>("page.badRequest", new Object[0]);
    }
}

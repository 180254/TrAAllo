package utils.other;


import org.apache.commons.lang3.StringUtils;
import play.data.Form;
import play.i18n.Messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ValidationErrorsHelper {

    protected List<String> errors = new ArrayList<>();

    public ValidationErrorsHelper(String labelPrefix, Form<?> form) {

        form.globalErrors().forEach(gError -> {
            errors.add(Messages.get(gError.message(), gError.arguments().toArray()));
        });

        form.data().forEach((field, value) ->
                Stream.of(form.error(field)).forEach(
                        err -> {
                            if (err != null) {
                                errors.add(
                                        String.format("%s: %s",
                                                Messages.get(labelPrefix + field),
                                                Messages.get(err.message(), err.arguments().toArray()))
                                );

                            }
                        }
                ));
    }

    public static ValidationErrorsHelper get(String labelPrefix, Form<?> form) {
        return new ValidationErrorsHelper(labelPrefix, form);
    }

    public List<String> get() {
        return Collections.unmodifiableList(errors);
    }

    public String getWithNL() {
        return StringUtils.join(errors, "\n");
    }

    public String getWithNLAsBR() {
        return StringUtils.join(errors, "<br/>\n");
    }

    public String getAsList() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>").append("\n");

        errors.stream().forEach(error -> {
            sb.append("<li>");
            sb.append(error);
            sb.append("</li>");
        });

        sb.append("<li>").append("\n");
        return sb.toString();
    }

}

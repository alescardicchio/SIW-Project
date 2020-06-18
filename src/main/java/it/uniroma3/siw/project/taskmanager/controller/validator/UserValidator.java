package it.uniroma3.siw.project.taskmanager.controller.validator;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import it.uniroma3.siw.project.taskmanager.model.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final int MAX_NAME_LENGTH = 100;
    private final int MIN_NAME_LENGTH = 2;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User)o;
        String firstName = user.getFirstName().trim();
        String lastName = user.getLastName().trim();

        if (StringUtils.isBlank(firstName))
            errors.rejectValue("firstName", "required");
        else if (firstName.length() > MAX_NAME_LENGTH || firstName.length() < MIN_NAME_LENGTH)
            errors.rejectValue("lastName", "size");

        if(StringUtils.isBlank(lastName))
            errors.rejectValue("lastName", "required");
        else if (lastName.length() > MAX_NAME_LENGTH || lastName.length() < MIN_NAME_LENGTH)
            errors.rejectValue("lastName", "size");
    }
}

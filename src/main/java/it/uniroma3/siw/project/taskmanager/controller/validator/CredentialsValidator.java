package it.uniroma3.siw.project.taskmanager.controller.validator;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import it.uniroma3.siw.project.taskmanager.model.Credentials;
import it.uniroma3.siw.project.taskmanager.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CredentialsValidator implements Validator {

    private final int MAX_PASSWORD_LENGTH = 20;
    private final int MIN_PASSWORD_LENGTH = 6;
    private final int MAX_USERNAME_LENGTH = 20;
    private final int MIN_USERNAME_LENGTH = 4;

    @Autowired
    private CredentialsRepository credentialsRepository;


    @Override
    public boolean supports(Class<?> aClass) {
        return Credentials.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Credentials credentials = (Credentials)o;
        String userName = credentials.getUserName().trim();
        String password = credentials.getPassword().trim();

        if (StringUtils.isBlank(userName))
            errors.rejectValue("userName","required");
        else if (userName.length() > MAX_USERNAME_LENGTH || userName.length() < MIN_USERNAME_LENGTH)
            errors.rejectValue("userName", "size");
        else if (this.credentialsRepository.findByUserName(userName).isPresent())
            errors.rejectValue("userName", "duplicate");

        if(StringUtils.isBlank(password))
            errors.rejectValue("password", "required");
        else if (password.length() > MAX_PASSWORD_LENGTH || password.length() < MIN_PASSWORD_LENGTH)
            errors.rejectValue("password","size");
    }
}

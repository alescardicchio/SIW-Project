package it.uniroma3.siw.project.taskmanager.controller.validator;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import it.uniroma3.siw.project.taskmanager.model.Project;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProjectValidator implements Validator {

    private final int MAX_NAME_LENGTH = 100;
    private final int MIN_NAME_LENGTH = 2;

    @Override
    public boolean supports(Class<?> aClass) {
        return Project.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Project project = (Project)o;
        String name = project.getName().trim();

        if(StringUtils.isBlank(name))
            errors.rejectValue("name","required");
        else if(name.length() > MAX_NAME_LENGTH || name.length() < MIN_NAME_LENGTH)
            errors.rejectValue("name", "size");
    }
}

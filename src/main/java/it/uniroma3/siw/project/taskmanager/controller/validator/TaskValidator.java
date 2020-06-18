package it.uniroma3.siw.project.taskmanager.controller.validator;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import it.uniroma3.siw.project.taskmanager.model.Task;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TaskValidator implements Validator {

    private final int MAX_NAME_LENGTH = 100;
    private final int MIN_NAME_LENGTH = 2;
    private final int MAX_DESCRIPTION_LENGTH = 1000;


    @Override
    public boolean supports(Class<?> aClass) {
        return Task.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Task task = (Task)o;
        String name = task.getName().trim();
        String description = task.getDescription().trim();

        if(StringUtils.isBlank(name))
            errors.rejectValue("name", "required");
        else if(name.length() > MAX_NAME_LENGTH || name.length() < MIN_NAME_LENGTH)
            errors.rejectValue("name", "size");

        if(!StringUtils.isBlank(description)) {
            if (description.length() > MAX_DESCRIPTION_LENGTH)
                errors.rejectValue("description", "size");
        }
    }
}

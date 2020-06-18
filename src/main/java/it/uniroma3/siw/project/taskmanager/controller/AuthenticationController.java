package it.uniroma3.siw.project.taskmanager.controller;

import it.uniroma3.siw.project.taskmanager.controller.validator.CredentialsValidator;
import it.uniroma3.siw.project.taskmanager.controller.validator.UserValidator;
import it.uniroma3.siw.project.taskmanager.model.Credentials;
import it.uniroma3.siw.project.taskmanager.model.User;
import it.uniroma3.siw.project.taskmanager.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private CredentialsValidator credentialsValidator;

    @Autowired
    private UserValidator userValidator;


    /**
     * This method is called when a GET request is sent by the user to url '/users/register'.
     * This method prepares and dispatches the User registration view.
     *
     * @param model the request model
     * @return the name of "registerUser" view
     */
    @GetMapping(value = "users/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("credentialsForm", new Credentials());
        model.addAttribute("userForm", new User());

        return "registerUser";
    }

    @PostMapping(value = "users/register")
    public String registerUser(@Valid @ModelAttribute("credentialsForm") Credentials credentials,
                               BindingResult credentialsBindingResult,
                               @Valid @ModelAttribute("userForm") User user,
                               BindingResult userBindingResult,
                               Model model) {

        this.credentialsValidator.validate(credentials, credentialsBindingResult);
        this.userValidator.validate(user, userBindingResult);

        if(!credentialsBindingResult.hasErrors() && !userBindingResult.hasErrors()) {
            credentials.setUser(user);
            this.credentialsService.saveCredentials(credentials);
            return "registerSuccessful";
        }
        return "registerUser";
    }
}

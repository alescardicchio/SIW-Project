package it.uniroma3.siw.project.taskmanager.controller;

import it.uniroma3.siw.project.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.project.taskmanager.controller.validator.UserValidator;
import it.uniroma3.siw.project.taskmanager.model.User;
import it.uniroma3.siw.project.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private SessionData sessionData;

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator userValidator;


    /**
     * This method is called when a GET request is sent by the user to url '/home'.
     * This method prepares and dispatches the homepage.
     *
     * @param model the request model
     * @return the name of "home" view
     */
    @GetMapping(value = "/home")
    public String homePage(Model model) {
        model.addAttribute("loggedUser", this.sessionData.getLoggedUser());
        return "home";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/admin'.
     * This method prepares and dispatches the reserved admin page.
     *
     * @param model the request model
     * @return the name of "admin" view
     */
    @GetMapping(value = "/admin")
    public String adminPage(Model model) {
        model.addAttribute("loggedUser", this.sessionData.getLoggedUser());
        return "admin";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/users/me'.
     * This method prepares and dispatches the User profile view.
     *
     * @param model the request model
     * @return the name of "userProfile" view
     */
    @GetMapping(value = "/users/me")
    public String profile(Model model) {
        model.addAttribute("loggedUser", this.sessionData.getLoggedUser());
        model.addAttribute("credentials", this.sessionData.getLoggedUserCredentials());
        return "userProfile";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/users/me/edit'.
     * This method prepares and dispatches the view for editing User profile.
     *
     * @param model the request model
     * @return the name of "editUserProfile" view
     */
    @GetMapping(value = "/users/me/edit")
    public String showEditProfileForm(Model model) {
        model.addAttribute("userForm", new User());
        return "editUserProfile";
    }

    @PostMapping(value = "/users/me/edit")
    public String editUserProfile(@Valid @ModelAttribute("userForm") User fakeUser,
                                  BindingResult bindingResult) {

        this.userValidator.validate(fakeUser, bindingResult);

        if(!bindingResult.hasErrors()) {
            User userToEdit = this.sessionData.getLoggedUser();
            userToEdit.setFirstName(fakeUser.getFirstName());
            userToEdit.setLastName(fakeUser.getLastName());
            this.userService.saveUser(userToEdit);
            return "redirect:/users/me/";
        }
        return "editUserProfile";
    }
}

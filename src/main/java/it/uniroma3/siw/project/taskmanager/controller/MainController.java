package it.uniroma3.siw.project.taskmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    /**
     * This method is called when a GET request is sent by the user to url '/' or '/index'.
     * This method prepares and dispatches the index view.
     *
     * @return the name of "index" view
     */
    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index";
    }
}

package it.uniroma3.siw.project.taskmanager.controller;

import it.uniroma3.siw.project.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.project.taskmanager.controller.validator.ProjectValidator;
import it.uniroma3.siw.project.taskmanager.model.*;
import it.uniroma3.siw.project.taskmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectValidator projectValidator;

    @Autowired
    private UserService userService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TagService tagService;

    @Autowired
    private SessionData sessionData;


    /**
     * This method is called when a GET request is sent by the user to url '/projects'.
     * This method prepares and dispatches the view showing all the projects owned by the logged user.
     *
     * @param model the request model
     * @return the name of "myOwnedProjects" view
     */
    @GetMapping(value = "")
    public String myOwnedProjects(Model model) {
        User user = this.sessionData.getLoggedUser();
        List<Project> projectList = this.projectService.getAllProjectsOwnedByUser(user);
        model.addAttribute("projectList", projectList);
        return "myOwnedProjects";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}'.
     * This method prepares and dispatches the view showing in detail a project,
     * only if the logged in user is the owner or a member of the project.
     *
     * @param model the request model
     * @return the name of "projectDetails" view
     */
    @GetMapping(value = "/{projectId}")
    public String projectDetails(@PathVariable Long projectId,
                                 Model model) {

        Project projectToShow = this.projectService.getProject(projectId);

        if(projectToShow == null)
            return "redirect:/projects";

        User user = this.sessionData.getLoggedUser();
        List<User> projectMembers = this.userService.getMembers(projectToShow);

        if (!projectToShow.getOwner().equals(user) &&
                !projectMembers.contains(user))
            return "redirect:/projects";

        model.addAttribute("project", projectToShow);
        model.addAttribute("members", projectMembers);
        model.addAttribute("loggedUserCredentials", this.sessionData.getLoggedUserCredentials());
        return "projectDetails";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/add'.
     * This method prepares and dispatches the creation project view.
     *
     * @param model the request model
     * @return the name of "createProject" view
     */
    @GetMapping(value = "/add")
    public String showCreationProjectForm(Model model) {
        model.addAttribute("projectForm", new Project());
        model.addAttribute("loggedUser", this.sessionData.getLoggedUser());
        return "createProject";
    }

    @PostMapping(value = "/add")
    public String createProject(@Valid @ModelAttribute("projectForm") Project project,
                                BindingResult projectBindingResult,
                                Model model) {

        this.projectValidator.validate(project, projectBindingResult);

        if(!projectBindingResult.hasErrors()) {
            project.setOwner(this.sessionData.getLoggedUser());
            this.projectService.saveProject(project);
            return "redirect:/projects/" + project.getId();
        }

        model.addAttribute("loggedUser", this.sessionData.getLoggedUser());
        return "createProject";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/visible'.
     * This method prepares and dispatches the view showing all the projects visible by the logged user.
     *
     * @param model the request model
     * @return the name of "myVisibleProjects" view
     */
    @GetMapping(value = "/visible")
    public String myVisibleProjects(Model model) {
        List<Project> visibleProjects = this.projectService.getAllProjectsVisibleByUser(this.sessionData.getLoggedUser());
        model.addAttribute("visibleProjects", visibleProjects);
        return "myVisibleProjects";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/edit'.
     * This method prepares and dispatches the view for editing project.
     *
     * @param model the request model
     * @return the name of "editProject" view
     */
    @GetMapping(value = "/{projectId}/edit")
    public String showEditProjectForm(@PathVariable Long projectId,
                                      Model model) {

        model.addAttribute("projectToEditId", projectId);
        model.addAttribute("projectForm", new Project());
        return "editProject";
    }

    @PostMapping(value = "/{projectId}/edit")
    public String editProject(@PathVariable Long projectId,
                              @Valid @ModelAttribute("projectForm") Project projectForm,
                              BindingResult bindingResult,
                              Model model) {

        Project project = this.projectService.getProject(projectId);
        this.projectValidator.validate(projectForm, bindingResult);

        if(project != null) {
            if (!bindingResult.hasErrors()) {
                if(this.sessionData.getLoggedUserCredentials().getRole().equals(Credentials.ADMIN_ROLE) ||
                        project.getOwner().equals(this.sessionData.getLoggedUser())) {

                    project.setName(projectForm.getName());
                    this.projectService.saveProject(project);
                    return "redirect:/projects/" + project.getId();
                }
            }
        }
        return "myOwnedProjects";
    }


    /**
     * This method is called when a POST request is sent by the user to url '/projects/{projectId}/delete'.
     * This method deletes a project.
     *
     */
    @PostMapping(value = "/{projectId}/delete")
    public String deleteProject(@PathVariable Long projectId) {
        Project projectToDelete = this.projectService.getProject(projectId);
        if(projectToDelete != null) {
            if(this.sessionData.getLoggedUserCredentials().getRole().equals(Credentials.ADMIN_ROLE) ||
                    projectToDelete.getOwner().equals(this.sessionData.getLoggedUser())) {

                this.projectService.deleteProject(projectToDelete);
                return "redirect:/projects";
            }
        }
        return "myOwnedProjects";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/share'.
     * This method prepares and dispatches the view for sharing project.
     *
     * @param model the request model
     * @return the name of "shareProject" view
     */
    @GetMapping(value = "/{projectId}/share")
    public String showShareProjectForm(@PathVariable Long projectId,
                                       Model model) {

        model.addAttribute("projectToShareId", projectId);
        model.addAttribute("credentialsForm", new Credentials());
        return "shareProject";
    }


    @PostMapping(value = "/{projectId}/share")
    public String shareProject(@PathVariable Long projectId,
                               @ModelAttribute("credentialsForm") Credentials credentialsForm) {

        Project projectToShare = this.projectService.getProject(projectId);
        User receivingUser = this.credentialsService.getCredentials(credentialsForm.getUserName()).getUser();

        if(projectToShare != null && receivingUser != null) {
            if(projectToShare.getOwner().equals(this.sessionData.getLoggedUser())) {
                this.projectService.shareProjectWithUser(projectToShare, receivingUser);
                return "redirect:/projects/" + projectToShare.getId();
            }
        }
        return "myOwnedProjects";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/tags/add'.
     * This method prepares and dispatches the creation tag view.
     *
     * @param model the request model
     * @return the name of "createTag" view
     */
    @GetMapping(value = "/{projectId}/tags/add")
    public String showAddTagForm(@PathVariable Long projectId,
                                 Model model) {

        model.addAttribute("receiverProjectId", projectId);
        model.addAttribute("tagForm", new Tag());
        return "createTag";
    }

    @PostMapping(value = "/{projectId}/tags/add")
    public String addTag(@PathVariable Long projectId,
                         @ModelAttribute("tagForm") Tag tag) {

        Project receiverProject = this.projectService.getProject(projectId);
        if(receiverProject != null) {
            if(receiverProject.getOwner().equals(this.sessionData.getLoggedUser())) {
                this.projectService.addTag(receiverProject, tag);
                return "redirect:/projects/" + receiverProject.getId();
            }
        }
        return "myOwnedProjects";
    }

    /**
     * This method is called when a POST request is sent by the user to url '/projects/{projectId}/tags/{tagId}/delete'.
     * This method deletes a project's tag.
     *
     */
    @PostMapping(value = "/{projectId}/tags/{tagId}/delete")
    public String deleteTag(@PathVariable Long projectId,
                            @PathVariable Long tagId) {

        Project project = this.projectService.getProject(projectId);
        Tag tagToDelete = this.tagService.getTag(tagId);

        if(project != null) {
            if(project.getOwner().equals(this.sessionData.getLoggedUser())) {

                project.getTasks().forEach(task -> this.taskService.removeTag(task, tagToDelete));
                this.projectService.removeTag(project, tagToDelete);

                return "redirect:/projects/" + project.getId();
            }
        }
        return "myOwnedProjects";
    }
}

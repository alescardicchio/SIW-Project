package it.uniroma3.siw.project.taskmanager.controller;

import it.uniroma3.siw.project.taskmanager.controller.session.SessionData;
import it.uniroma3.siw.project.taskmanager.controller.validator.TaskValidator;
import it.uniroma3.siw.project.taskmanager.model.*;
import it.uniroma3.siw.project.taskmanager.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/projects/{projectId}/tasks")
public class TaskController {

    @Autowired
    private TaskValidator taskValidator;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CredentialsService credentialsService;

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

    @Autowired
    private SessionData sessionData;


    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/tasks/{taskId}'.
     * This method prepares and dispatches the view showing in detail a project's task,
     * only if the logged in user is the owner or a member of the project.
     *
     * @param model the request model
     * @return the name of "taskDetails" view
     */
    @GetMapping(value = "/{taskId}")
    public String taskDetails(@PathVariable("projectId") Long projectId,
                       @PathVariable("taskId") Long taskId,
                       Model model) {

        User user = this.sessionData.getLoggedUser();
        Project project = this.projectService.getProject(projectId);
        Task taskToShow = this.taskService.getTask(taskId);

        if(taskToShow == null)
            return "redirect:/projects/" + project.getId();

        if (!project.getOwner().equals(user) &&
                !this.userService.getMembers(project).contains(user))
            return "redirect:/projects" + project.getId();

        List<Tag> tagList = this.tagService.getAllTagsOfTask(taskToShow);

        model.addAttribute("task", taskToShow);
        model.addAttribute("tagList", tagList);
        model.addAttribute("projectId", projectId);
        model.addAttribute("loggedUserCredentials", this.sessionData.getLoggedUserCredentials());
        model.addAttribute("project", project);
        return "taskDetails";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/tasks/add'.
     * This method prepares and dispatches the creation task view.
     *
     * @param model the request model
     * @return the name of "createTask" view
     */
    @GetMapping(value = "/add")
    public String showAddTaskForm(@PathVariable Long projectId,
                                     Model model) {

        model.addAttribute("receiverProjectId", projectId);
        model.addAttribute("taskForm", new Task());
        return "createTask";
    }

    @PostMapping(value = "/add")
    public String addTask(@PathVariable Long projectId,
                          @Valid @ModelAttribute("taskForm") Task taskForm,
                          BindingResult bindingResult,
                          Model model) {

        this.taskValidator.validate(taskForm, bindingResult);
        Project receiverProject = this.projectService.getProject(projectId);

        if(!bindingResult.hasErrors()) {
            if (receiverProject != null) {
                if (receiverProject.getOwner().equals(this.sessionData.getLoggedUser())) {
                    this.projectService.addTask(receiverProject, taskForm);
                    return "redirect:/projects/" + receiverProject.getId();
                }
            }
        }
        return "myOwnedProjects";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/tasks/{taskId}/edit'.
     * This method prepares and dispatches the view for editing task.
     *
     * @param model the request model
     * @return the name of "editTask" view
     */
    @GetMapping(value = "/{taskId}/edit")
    public String showEditTaskForm(@PathVariable("projectId") Long projectId,
                                   @PathVariable("taskId") Long taskId,
                                   Model model) {

        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("taskForm", new Task());
        return "editTask";
    }

    @PostMapping(value = "/{taskId}/edit")
    public String editTask(@PathVariable("projectId") Long projectId,
                    @PathVariable("taskId") Long taskId,
                    @Valid @ModelAttribute("taskForm") Task taskForm,
                    BindingResult bindingResult,
                    Model model) {

        Project project = this.projectService.getProject(projectId);
        Task taskToEdit = this.taskService.getTask(taskId);

        this.taskValidator.validate(taskForm, bindingResult);

        if(project != null && taskToEdit != null) {
            if(!bindingResult.hasErrors()) {
                if(project.getOwner().equals(this.sessionData.getLoggedUser())) {
                    taskToEdit.setName(taskForm.getName());
                    taskToEdit.setDescription(taskForm.getDescription());
                    this.taskService.saveTask(taskToEdit);
                    return "redirect:/projects/" + project.getId() + "/tasks/" + taskToEdit.getId();
                }
            }
        }
        return "myOwnedProjects";
    }

    /**
     * This method is called when a POST request is sent by the user to url '/projects/{projectId}/tasks/{taskId}/delete'.
     * This method deletes a task.
     *
     */
    @PostMapping(value = "/{taskId}/delete")
    public String deleteTask(@PathVariable("projectId") Long projectId,
                             @PathVariable("taskId") Long taskId,
                             Model model) {

        Project project = this.projectService.getProject(projectId);
        Task taskToDelete = this.taskService.getTask(taskId);

        if(project != null && taskToDelete != null) {
            if(project.getOwner().equals(this.sessionData.getLoggedUser())) {
                project.getTasks().remove(taskToDelete);
                this.taskService.deleteTask(taskToDelete);
                return "redirect:/projects/" + project.getId();
            }
        }
        return "ownedProjects";
    }


    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/tasks/{taskId}/share'.
     * This method prepares and dispatches the view for assigning task.
     *
     * @param model the request model
     * @return the name of "assignTask" view
     */
    @GetMapping("/{taskId}/assign")
    public String showAssignTaskForm(@PathVariable("projectId") Long projectId,
                                     @PathVariable("taskId") Long taskId,
                                     Model model) {

        model.addAttribute("projectId", projectId);
        model.addAttribute("taskToAssignId", taskId);
        model.addAttribute("credentialsForm", new Credentials());
        return "assignTask";
    }


    @PostMapping("/{taskId}/assign")
    public String assignTask(@PathVariable("projectId") Long projectId,
                             @PathVariable("taskId") Long taskId,
                             @ModelAttribute("credentialsForm") Credentials credentialsForm) {

        Project project = this.projectService.getProject(projectId);
        Task taskToAssign = this.taskService.getTask(taskId);
        User receivingUser = this.credentialsService.getCredentials(credentialsForm.getUserName()).getUser();

        if(project != null && receivingUser != null && taskToAssign != null) {

            if(project.getOwner().equals(this.sessionData.getLoggedUser()) &&
                    this.userService.getMembers(project).contains(receivingUser)) {

                this.taskService.assignTaskToUser(taskToAssign, receivingUser);
                return "redirect:/projects/" + project.getId() + "/tasks/" + taskToAssign.getId();
            }
        }
        return "myOwnedProjects";
    }

    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/tasks/{taskId}/tags/add'.
     * This method prepares and dispatches the view showing the list of tags of the related project.
     *
     * @param model the request model
     * @return the name of "pickTagToAddToTask" view
     */
    @GetMapping(value = "/{taskId}/tags/add")
    public String showAddTagSelection(@PathVariable("projectId") Long projectId,
                                      @PathVariable("taskId") Long taskId,
                                      Model model) {

        List<Tag> allProjectTags = this.projectService.getProject(projectId).getTags();
        Task task = this.taskService.getTask(taskId);

        List<Tag> validTags = allProjectTags.stream()
                .filter(tag -> !task.getTags().contains(tag))
                .collect(Collectors.toList());


        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("validTags", validTags);
        return "pickTagToAddToTask";
    }

    @PostMapping(value = "/{taskId}/tags/add/{tagId}")
    public String addTag(@PathVariable Long tagId,
                         @PathVariable Long projectId,
                         @PathVariable Long taskId) {

        Project project = this.projectService.getProject(projectId);
        Task task = this.taskService.getTask(taskId);
        Tag tagToAdd = this.tagService.getTag(tagId);

        if(project != null && task != null && tagToAdd != null) {
            if(project.getOwner().equals(this.sessionData.getLoggedUser())) {

                this.taskService.addTag(task, tagToAdd);
                return "redirect:/projects/" + project.getId() + "/tasks/" + task.getId();
            }
        }
        return "myOwnedProjects";
    }


    /**
     * This method is called when a GET request is sent by the user to url '/projects/{projectId}/tasks/{taskId}/comments/add'.
     * This method prepares and dispatches the creation comment view.
     *
     * @param model the request model
     * @return the name of "createComment" view
     */
    @GetMapping(value = "/{taskId}/comments/add")
    public String showAddCommentForm(@PathVariable Long projectId,
                                     @PathVariable Long taskId,
                                     Model model) {

        model.addAttribute("projectId", projectId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("commentForm", new Comment());
        return "createComment";
    }

    @PostMapping(value = "/{taskId}/comments/add")
    public String addComment(@PathVariable Long projectId,
                             @PathVariable Long taskId,
                             @ModelAttribute("commentForm") Comment comment,
                             Model model) {

        Project project = this.projectService.getProject(projectId);
        Task task = this.taskService.getTask(taskId);

        if(project != null && task != null) {
            if(this.userService.getMembers(project).contains(this.sessionData.getLoggedUser())) {

                comment.setCreator(this.sessionData.getLoggedUser());
                this.taskService.addComment(task,comment);
                return "redirect:/projects/" + project.getId() + "/tasks/" + task.getId();
            }
        }
        return "myOwnedProjects";
    }

    /**
     * This method is called when a POST request is sent by the user to url '/projects/{projectId}/tasks/{taskId}/tags/{tagId}/delete'.
     * This method deletes a task's tag.
     *
     */
    @PostMapping(value = "/{taskId}/tags/{tagId}/delete")
    public String deleteTag(@PathVariable Long projectId,
                            @PathVariable Long taskId,
                            @PathVariable Long tagId) {

        Project project = this.projectService.getProject(projectId);
        Task task = this.taskService.getTask(taskId);
        Tag tagToDelete = this.tagService.getTag(tagId);

        if(project != null && task != null && tagToDelete != null) {
            if(project.getOwner().equals(this.sessionData.getLoggedUser())) {

                this.taskService.removeTag(task, tagToDelete);
                return "redirect:/projects/" + project.getId() + "/tasks/" + task.getId();
            }
        }
        return "myOwnedProjects";
    }
}
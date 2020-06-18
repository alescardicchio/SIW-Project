package it.uniroma3.siw.project.taskmanager.service;

import it.uniroma3.siw.project.taskmanager.model.Project;
import it.uniroma3.siw.project.taskmanager.model.Tag;
import it.uniroma3.siw.project.taskmanager.model.Task;
import it.uniroma3.siw.project.taskmanager.model.User;
import it.uniroma3.siw.project.taskmanager.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Store a new Project in the database
     * @param project that's need to be saved
     * @return the stored Project
     */
    @Transactional
    public Project saveProject(Project project) {
        return this.projectRepository.save(project);
    }

    /**
     * Retrieve a Project from the database by its id
     * @param id of Project to retrieve
     * @return the Project requested if it's present in the database, null otherwise
     */
    @Transactional
    public Project getProject(Long id) {
        return this.projectRepository.findById(id).orElse(null);
    }

    /**
     * Remove a Project from the database
     * @param project to be removed
     */
    @Transactional
    public void deleteProject(Project project) {
        this.projectRepository.delete(project);
    }

    /**
     * Retrieve the list of Projects that the User owns.
     * @param user member
     * @return list of Projects owned by User
     */
    @Transactional
    public List<Project> getAllProjectsOwnedByUser(User user) {
        return this.projectRepository.findByOwner(user);
    }

    /**
     * Retrieve the list of Projects of which the User has visibility.
     * @param user member
     * @return list of Projects visible by User
     */
    @Transactional
    public List<Project> getAllProjectsVisibleByUser(User user) {
        return this.projectRepository.findByMembers(user);
    }

    /**
     * Share the Project with a User, adding the User to its list of members.
     * @param project to be shared
     * @param user who will get the visibility
     */
    @Transactional
    public void shareProjectWithUser(Project project, User user) {
        project.addMember(user);
        this.projectRepository.save(project);
    }

    /**
     * Add a Tag to a Project.
     * @param project to which the Tag will be added
     * @param tag to add
     */
    @Transactional
    public Project addTag(Project project, Tag tag) {
        project.addTag(tag);
        return this.projectRepository.save(project);
    }

    /**
     * Remove a Tag from Project
     * @param project to which the Tag will be removed
     * @param tag to remove
     * @return
     */
    @Transactional
    public Project removeTag(Project project, Tag tag) {
        project.removeTag(tag);
        return this.projectRepository.save(project);
    }

    /**
     * Add a Task to a Project.
     * @param project to which the Task will be added
     * @param task to add
     */
    @Transactional
    public Project addTask(Project project, Task task) {
        project.addTask(task);
        return this.projectRepository.save(project);
    }
}

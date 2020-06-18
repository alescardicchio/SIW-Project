package it.uniroma3.siw.project.taskmanager.service;

import it.uniroma3.siw.project.taskmanager.model.*;
import it.uniroma3.siw.project.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;


    /**
     * Store a new Task in the database
     * @param task that's need to be saved
     * @return the stored Task
     */
    @Transactional
    public Task saveTask(Task task) {
        return this.taskRepository.save(task);
    }

    /**
     * Retrieve a Task from the database by its id
     * @param id of Task
     * @return the Task retrieved from db
     */
    public Task getTask(Long id) {
        return this.taskRepository.findById(id).orElse(null);
    }

    /**
     * Remove a Task from the database
     * @param task to be removed
     */
    @Transactional
    public void deleteTask(Task task) {
        this.taskRepository.delete(task);
    }

    /**
     * Assign a Task to the User
     * @param task to be assigned
     * @param user to assign it to
     */
    @Transactional
    public void assignTaskToUser(Task task, User user) {
        task.setAssignedUser(user);
        this.taskRepository.save(task);
    }

    /**
     * Add a Tag to a Task
     * @param task to which the Tag will be added
     * @param tag to add
     */
    @Transactional
    public void addTag(Task task, Tag tag) {
        task.addTag(tag);
        this.taskRepository.save(task);
    }

    /**
     * Remove a Tag from a Task
     * @param task to which the Tag will be removed
     * @param tag to remove
     */
    public void removeTag(Task task, Tag tag) {
        task.removeTag(tag);
        this.taskRepository.save(task);
    }

    /**
     * Add a Comment to a Task
     * @param task to which the Comment will be added
     * @param comment to add
     */
    @Transactional
    public void addComment(Task task, Comment comment) {
        task.addComment(comment);
        this.taskRepository.save(task);
    }
}

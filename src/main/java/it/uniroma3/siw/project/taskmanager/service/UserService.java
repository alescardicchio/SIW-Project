package it.uniroma3.siw.project.taskmanager.service;

import it.uniroma3.siw.project.taskmanager.model.Project;
import it.uniroma3.siw.project.taskmanager.model.User;
import it.uniroma3.siw.project.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Store a new User in the database
     * @param user that's need to be saved
     * @return the stored User
     */
    @Transactional
    public User saveUser(User user) {
        return this.userRepository.save(user);
    }

    /**
     * Retrieve a User from the database by its id
     * @param id of User to retrieve
     * @return the User requested if it's present in the database, null otherwise
     */
    @Transactional
    public User getUser(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    /**
     * Retrieve every User stored in the database
     * @return a list of users
     */
    @Transactional
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        this.userRepository.findAll().forEach(result::add);
        return result;
    }

    /**
     *
     * @param project
     * @return
     */
    @Transactional
    public List<User> getMembers(Project project) {
        List<User> result = new ArrayList<>();
        this.userRepository.findByVisibleProjects(project).forEach(result::add);
        return result;
    }
}

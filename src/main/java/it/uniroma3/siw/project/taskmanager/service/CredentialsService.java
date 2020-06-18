package it.uniroma3.siw.project.taskmanager.service;

import it.uniroma3.siw.project.taskmanager.model.Credentials;
import it.uniroma3.siw.project.taskmanager.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CredentialsService {

    @Autowired
    private CredentialsRepository credentialsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Retrieve a Credentials from the database by its id
     * @param id of Credentials to retrieve
     * @return the retrieved Credentials
     */
    @Transactional
    public Credentials getCredentials(Long id) {
        return this.credentialsRepository.findById(id).orElse(null);
    }

    /**
     * Retrieve a Credentials from the database by its username
     * @param userName of Credentials to retrieve
     * @return the retrieved Credentials
     */
    @Transactional
    public Credentials getCredentials(String userName) {
        return this.credentialsRepository.findByUserName(userName).orElse(null);
    }

    /**
     * Retrieve all Credentials from the database
     * @return list of Credentials
     */
    @Transactional
    public List<Credentials> getAllCredentials() {
        List<Credentials> result = new ArrayList<>();
        this.credentialsRepository.findAll().forEach(result::add);
        return result;
    }

    /**
     * Save a Credentials in the database,
     * having first set the role to default and having encrypted the password.
     *
     * @param credentials to save
     * @return the saved Credentials
     */
    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialsRepository.save(credentials);
    }

    /**
     * Remove a Credentials from the database by its Username
     * @param userName
     */
    public void deleteCredentials(String userName) {
        Credentials credentialsToDelete = this.getCredentials(userName);
        this.credentialsRepository.delete(credentialsToDelete);
    }
}

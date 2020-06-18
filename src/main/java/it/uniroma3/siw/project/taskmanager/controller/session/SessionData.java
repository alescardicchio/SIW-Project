package it.uniroma3.siw.project.taskmanager.controller.session;

import it.uniroma3.siw.project.taskmanager.model.Credentials;
import it.uniroma3.siw.project.taskmanager.model.User;
import it.uniroma3.siw.project.taskmanager.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SessionData {

    private Credentials credentials;

    private User user;

    @Autowired
    private CredentialsRepository credentialsRepository;

    public Credentials getLoggedUserCredentials() {
        if (this.credentials == null)
            update();

        return this.credentials;
    }

    public User getLoggedUser() {
        if (this.user == null)
            update();

        return this.user;
    }

    private void update() {
        Object obj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails)obj;
        this.credentials = this.credentialsRepository.findByUserName(userDetails.getUsername()).get();

        this.credentials.setPassword("PROTECTED");
        this.user = this.credentials.getUser();
    }
}

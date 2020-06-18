package it.uniroma3.siw.project.taskmanager.repository;

import it.uniroma3.siw.project.taskmanager.model.Credentials;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

    Optional<Credentials> findByUserName(String userName);
}

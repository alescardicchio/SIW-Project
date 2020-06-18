package it.uniroma3.siw.project.taskmanager.repository;

import it.uniroma3.siw.project.taskmanager.model.Project;
import it.uniroma3.siw.project.taskmanager.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findByVisibleProjects(Project project);
}

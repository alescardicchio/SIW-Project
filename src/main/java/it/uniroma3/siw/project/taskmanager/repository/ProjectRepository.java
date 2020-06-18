package it.uniroma3.siw.project.taskmanager.repository;

import it.uniroma3.siw.project.taskmanager.model.Project;
import it.uniroma3.siw.project.taskmanager.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    List<Project> findByMembers(User user);

    List<Project> findByOwner(User user);
}

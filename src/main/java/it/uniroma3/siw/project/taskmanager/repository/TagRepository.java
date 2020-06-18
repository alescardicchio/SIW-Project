package it.uniroma3.siw.project.taskmanager.repository;

import it.uniroma3.siw.project.taskmanager.model.Tag;
import it.uniroma3.siw.project.taskmanager.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Long> {


    List<Tag> findByTasks(Task task);
}

package it.uniroma3.siw.project.taskmanager.service;

import it.uniroma3.siw.project.taskmanager.model.Tag;
import it.uniroma3.siw.project.taskmanager.model.Task;
import it.uniroma3.siw.project.taskmanager.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;


    @Transactional
    public Tag getTag(Long id) {
        return this.tagRepository.findById(id).orElse(null);
    }

    @Transactional
    public List<Tag> getAllTagsOfTask(Task task) {
        return this.tagRepository.findByTasks(task);
    }
}

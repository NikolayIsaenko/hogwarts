package ru.hogwarts.school.service.impl;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repasitory.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacultyServiceImpl implements FacultyService {
    private static final Logger logger = LogManager.getLogger(FacultyServiceImpl.class);
    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty create(Faculty faculty) {
        logger.info("Вызван метод для создания факультета");
        return facultyRepository.save(faculty);
    }

    @Override
    public Faculty read(Long id) {
        logger.info("Вызван метод для чтения факультета с id: {}", id);
        return facultyRepository.findById(id).orElse(null);
    }

    @Override
    public Faculty update(Long id, Faculty faculty) {
        logger.info("Вызван метод для обновления факультета с id: {}", id);
        return facultyRepository.findById(id).map(facultyFromDb -> {
            facultyFromDb.setName(faculty.getName());
            facultyFromDb.setColor(faculty.getColor());
            facultyRepository.save(facultyFromDb);
            return facultyFromDb;
        }).orElse(null);
    }

    @Override
    public Faculty delete(Long id) {
        logger.info("Вызван метод для удаления факультета с id: {}", id);
        return facultyRepository.findById(id).map(faculty -> {
            facultyRepository.deleteById(id);
            return faculty;
        }).orElse(null);
    }

    @Override
    public List<Faculty> filterByColor(String color) {
        logger.info("Вызван метод для фильтрации факультетов по цвету: {}", color);
        return facultyRepository.findAlByColor(color);
    }

    @Override
    public List<Faculty> findByColorOrNameIgnoreCase(String name, String color) {
        logger.info("Вызван метод для поиска факультетов по цвету или имени (без учета регистра): {} или {}", name, color);
        return facultyRepository.findAll().stream()
                .filter(faculty -> faculty.getName().equalsIgnoreCase(name) || faculty.getColor().equalsIgnoreCase(color))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> getStudents(Long facultyId) {
        logger.info("Вызван метод для получения студентов факультета с id: {}", facultyId);
        return facultyRepository.findById(facultyId).map(Faculty::getStudents).orElse(null);
    }
}

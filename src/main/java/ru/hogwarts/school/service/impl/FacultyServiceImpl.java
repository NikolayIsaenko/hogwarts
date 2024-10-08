package ru.hogwarts.school.service.impl;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repasitory.FacultyRepository;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

@Service
public class FacultyServiceImpl implements FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyServiceImpl(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    @Override
    public Faculty create(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    @Override
    public Faculty read(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    @Override
    public Faculty update(Long id, Faculty faculty) {
        return facultyRepository.findById(id).map(facultyFromDb -> {
            facultyFromDb.setName(faculty.getName());
            facultyFromDb.setColor(faculty.getColor());
            facultyRepository.save(facultyFromDb);
            return facultyFromDb;
        }).orElse(null);
    }

    @Override
    public Faculty delete(Long id) {
        return facultyRepository.findById(id).map(faculty -> {
            facultyRepository.deleteById(id);
            return faculty;
        }).orElse(null);
    }

    @Override
    public List<Faculty> filterByColor(String color) {
        return facultyRepository.findAlByColor(color);
    }

    @Override
    public List<Faculty> findByColorOrNameIgnoreCase(String name, String color) {
        return facultyRepository.findByColorOrNameIgnoreCase(name, color);
    }

    @Override
    public List<Student> getStudents(Long facultyId) {
        return facultyRepository.findById(facultyId).map(faculty -> {
            return faculty.getStudents();
        }).orElse(null);
    }
}

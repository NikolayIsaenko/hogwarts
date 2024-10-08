package ru.hogwarts.school.controller;

import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;


@RestController
@RequestMapping("faculties")
public class FacultyController {
    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public Faculty create(@RequestBody Faculty faculty) {
        return facultyService.create(faculty);
    }

    @GetMapping("{id}")
    public Faculty read(@PathVariable Long id) {
        return facultyService.read(id);
    }

    @PutMapping("{id}")
    public Faculty update(@PathVariable Long id, @RequestBody Faculty faculty) {
        return facultyService.update(id, faculty);
    }

    @DeleteMapping("{id}")
    public Faculty delete(@PathVariable Long id) {
        return facultyService.delete(id);
    }

    @GetMapping
    public List<Faculty> filterByAge(@RequestParam String color) {
        return facultyService.filterByColor(color);
    }

    @GetMapping("byNameOrColor")
    public List<Faculty> findByColorOrNameIgnoreCase(String name, String color) {
        return facultyService.findByColorOrNameIgnoreCase(name, color);
    }
    @GetMapping("{id}/students")
    public List<Student> getStudents(@PathVariable Long id) {
        return facultyService.getStudents(id);
    }
}

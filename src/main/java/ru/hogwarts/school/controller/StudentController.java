package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student create(@RequestBody Student student) {
        return studentService.create(student);
    }

    @GetMapping("{id}")
    public Student read(@PathVariable Long id) {
        return studentService.read(id);
    }

    @PutMapping("{id}")
    public Student update(@PathVariable Long id, @RequestBody Student student) {
        return studentService.update(id, student);
    }

    @DeleteMapping("/{id}")
    public Student delete(@PathVariable Long id) {
        return studentService.delete(id);
    }

    @GetMapping
    public List<Student> filterByAge(@RequestParam int age) {
        return studentService.filterByAge(age);
    }

    @GetMapping("betweenByAge")
    public List<Student> findAllByAgeBetween(int fromAge, int toAge) {
        return studentService.findAllByAgeBetween(fromAge, toAge);
    }

    @GetMapping("{id}/faculty")
    public Faculty getFacultyStudent(@PathVariable Long id) {
        return studentService.getFacultyStudent(id);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countAllStudents() {
        long count = studentService.countAllStudents();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/averageAge")
    public ResponseEntity<Double> getAverageAgeOfStudents() {
        Double averageAge = studentService.getAverageAgeOfStudents();
        return ResponseEntity.ok(averageAge);
    }

    @GetMapping("/top5")
    public ResponseEntity<List<Student>> findTop5Students() {
        List<Student> students = studentService.findTop5Students();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/namesStartingWithA")
    public ResponseEntity<List<String>> getNamesStartingWithA() {
        List<Student> allStudents = studentService.findAll();
        List<String> namesStartingWithA = allStudents.stream()
                .map(Student::getName)
                .filter(name -> name.startsWith("A") || name.startsWith("a"))
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());
        return ResponseEntity.ok(namesStartingWithA);
    }

    @GetMapping("/averageAllAge")
    public ResponseEntity<Double> getAverageAgeOfAllStudents() {
        List<Student> allStudents = studentService.findAll();
        double averageAge = allStudents.stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0.0);
        return ResponseEntity.ok(averageAge);
    }

    @GetMapping("/print-parallel")
    public void printStudentsParallel() {

        List<Student> allStudents = studentService.findAll();

        if (allStudents.size() < 6) {
            throw new IllegalArgumentException("Должно быть как минимум 6 студентов для параллельного вывода.");
        }

        System.out.println(allStudents.get(0).getName());
        System.out.println(allStudents.get(1).getName());

        new Thread(() -> {
            System.out.println(allStudents.get(2).getName());
            System.out.println(allStudents.get(3).getName());
        }).start();

        new Thread(() -> {
            System.out.println(allStudents.get(4).getName());
            System.out.println(allStudents.get(5).getName());
        }).start();
    }

    @GetMapping("/print-synchronized")
    public void printStudentsSynchronized() {
        List<Student> allStudents = studentService.findAll();

        if (allStudents.size() < 6) {
            throw new IllegalArgumentException("Должно быть как минимум 6 студентов для синхронного вывода.");
        }

        // Вывод первых двух имен в основном потоке
        printSynchronized(allStudents.get(0).getName());
        printSynchronized(allStudents.get(1).getName());

        // Вывод третьего и четвертого имен в параллельном потоке
        new Thread(() -> {
            printSynchronized(allStudents.get(2).getName());
            printSynchronized(allStudents.get(3).getName());
        }).start();

        // Вывод пятого и шестого имен в еще одном параллельном потоке
        new Thread(() -> {
            printSynchronized(allStudents.get(4).getName());
            printSynchronized(allStudents.get(5).getName());
        }).start();
    }

    private synchronized void printSynchronized(String name) {
        System.out.println(name);
    }
}

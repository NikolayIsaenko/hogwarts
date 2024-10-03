package ru.hogwarts.school.service.impl;

import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repasitory.StudentRepository;
import ru.hogwarts.school.service.StudentService;
import org.apache.logging.log4j.LogManager;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = LogManager.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student create(Student student) {
        logger.info("Метод, для создания студента");
        return studentRepository.save(student);
    }

    @Override
    public Student read(Long id) {
        logger.info("Метод, для получения студента");
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student update(Long id, Student student) {
        logger.info("Метод, для обновления студента");
        return studentRepository.findById(id).map(studentFromDb -> {
            studentFromDb.setName(student.getName());
            studentFromDb.setAge(student.getAge());
            studentRepository.save(studentFromDb);
            return studentFromDb;
        }).orElse(null);
    }

    @Override
    public Student delete(Long id) {
        logger.info("Метод, для удаления студента");
        return studentRepository.findById(id).map(student -> {
            studentRepository.deleteById(id);
            return student;
        }).orElse(null);
    }

    @Override
    public List<Student> findAll() {
        logger.info("Вызван метод для получения всех студентов");
        return studentRepository.findAll();
    }

    @Override
    public List<Student> filterByAge(int age) {
        logger.info("Метод, для фильтрации студента по возрасту");
        return studentRepository.findAllByAge(age);
    }

    @Override
    public List<Student> findAllByAgeBetween(int fromAge, int toAge) {
        logger.info("Метод, для поиска студента по возрасту");
        return studentRepository.findAllByAgeBetween(fromAge, toAge);
    }

    @Override
    public Faculty getFacultyStudent(Long studentId) {
        logger.info("Метод, для поиска студента по факультету");
        return studentRepository.findById(studentId)
                .map(Student::getFaculty).orElse(null);

    }

    @Override
    public List<Student> findTop5Students() {
        logger.info("Метод, для поиска ТОП5 студентов");
        return studentRepository.findTop5Students();
    }

    @Override
    public Double getAverageAgeOfStudents() {
        logger.info("Метод, для получения среднего возраста студентов");
        return studentRepository.getAverageAgeOfStudents();
    }

    @Override
    public long countAllStudents() {
        logger.info("Метод, для получения всех студентов");
        return studentRepository.countAllStudents();
    }

    @Override
    public void printStudentsParallel() {
        List<Student> allStudents = findAll();

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

    @Override
    public void printStudentsSynchronized() {
        List<Student> allStudents = findAll();

        if (allStudents.size() < 6) {
            throw new IllegalArgumentException("Должно быть как минимум 6 студентов для синхронного вывода.");
        }

        printSynchronized(allStudents.get(0).getName());
        printSynchronized(allStudents.get(1).getName());

        new Thread(() -> {
            printSynchronized(allStudents.get(2).getName());
            printSynchronized(allStudents.get(3).getName());
        }).start();

        new Thread(() -> {
            printSynchronized(allStudents.get(4).getName());
            printSynchronized(allStudents.get(5).getName());
        }).start();
    }

    private synchronized void printSynchronized(String name) {
        System.out.println(name);
    }
}

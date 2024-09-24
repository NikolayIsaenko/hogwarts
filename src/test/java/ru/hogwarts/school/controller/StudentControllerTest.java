package ru.hogwarts.school.controller;

import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repasitory.FacultyRepository;
import ru.hogwarts.school.repasitory.StudentRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();
    private Student student1;
    private Student student2;

    @AfterEach
    public void cleanup() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }
    @BeforeEach
    public void beforeEach() {
        Faculty faculty1 = createFaculty();
        Faculty faculty2 = createFaculty();

        student1 = createStudent(faculty1);
        student2 = createStudent(faculty2);
    }


    private String buildUrl(String uriStartWithSlash) {
        return "http://localhost:%d%s".formatted(port, uriStartWithSlash);
    }

    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Test");
        faculty.setColor("Test");
        return facultyRepository.save(faculty);
    }

    private Student createStudent(Faculty faculty) {
        Student student = new Student();
        student.setFaculty(faculty);
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(13, 19));
        return studentRepository.save(student);
    }


    private void createStudentTest(Student student) {
        ResponseEntity<Student> responseEntity = testRestTemplate.postForEntity(
                buildUrl("/students"),
                student,
                Student.class
        );
        Student created = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created).isNotNull();
        assertThat(created).usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(student);
        assertThat(created.getId()).isNotNull();

        Optional<Student> fromDb = studentRepository.findById(created.getId());

        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .ignoringFields("id", "faculty.students")
                .isEqualTo(created);
    }

    @Test
    public void createStudentTestPositive() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(13, 19));
        student.setName(faker.harryPotter().character());

        createStudentTest(student);
    }

    @Test
    public void createStudentWithFacultyTest() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(13, 19));
        student.setName(faker.harryPotter().character());

        Faculty facultyTest = createFaculty();
        student.setFaculty(facultyTest);
        createStudentTest(student);
    }

    private void assertStudentEquals(Student expected, Student actual) {
        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getName()).isEqualTo(expected.getName());
        assertThat(actual.getAge()).isEqualTo(expected.getAge());
        assertThat(actual.getFaculty().getId()).isEqualTo(expected.getFaculty().getId());
    }

    @Test
    public void createStudentTestNegative() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(13, 19));
        student.setName(faker.harryPotter().character());

        Faculty faculty = new Faculty();
        faculty.setId(-1L);

        student.setFaculty(faculty);

        ResponseEntity<Student> responseEntity = testRestTemplate.postForEntity(
                buildUrl("/students"),
                student,
                Student.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void readTest() {
        ResponseEntity<Student> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/students/" + student1.getId()),
                Student.class
        );

        Student created = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created).isNotNull();
        assertThat(created).usingRecursiveComparison()
                .isEqualTo(student1);
    }

    @Test
    public void testUpdateStudent() {
        Student updatedStudent = new Student();
        updatedStudent.setName("Updated Name");
        updatedStudent.setAge(20);

        ResponseEntity<Student> responseEntity = testRestTemplate.exchange(
                buildUrl("/students/" + student1.getId()),
                HttpMethod.PUT,
                new HttpEntity<>(updatedStudent),
                Student.class
        );
    }
    @Test
    public void testDeleteStudent() {
        ResponseEntity<Student> responseEntity = testRestTemplate.exchange(
                buildUrl("/students/" + student1.getId()),
                HttpMethod.DELETE,
                null,
                Student.class
        );

        Student deleted = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertStudentEquals(student1, deleted);

        Optional<Student> fromDb = studentRepository.findById(student1.getId());

        assertThat(fromDb).isNotPresent();
    }
    @Test
    public void testFilterByAge() {
        ResponseEntity<Student[]> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/students?age=" + student1.getAge()),
                Student[].class
        );

        List<Student> students = Arrays.asList(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students).isNotNull();
        assertThat(students).contains(student1);
    }
    @Test
    public void testFindAllByAgeBetween() {
        int fromAge = 13;
        int toAge = 19;

        ResponseEntity<Student[]> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/students/betweenByAge?fromAge=" + fromAge + "&toAge=" + toAge),
                Student[].class
        );

        List<Student> students = Arrays.asList(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students).isNotNull();
        assertThat(students).contains(student1, student2);
    }
    @Test
    public void testGetFacultyStudent() {
        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/students/" + student1.getId() + "/faculty"),
                Faculty.class
        );

        Faculty faculty = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(faculty).isNotNull();
        assertThat(faculty.getId()).isEqualTo(student1.getFaculty().getId());
    }
}

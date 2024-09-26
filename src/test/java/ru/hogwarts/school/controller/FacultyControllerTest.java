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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FacultyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    private final Faker faker = new Faker();
    private Faculty faculty1;
    private Faculty faculty2;
    private Student student1;
    private Student student2;

    @AfterEach
    public void cleanup() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        faculty1 = createFaculty("Griffindor", "Red");
        faculty2 = createFaculty("Slytherin", "Green");

        student1 = createStudent(faculty1, "Harry Potter", 15);
        student2 = createStudent(faculty2, "Draco Malfoy", 16);
    }

    private String buildUrl(String uriStartWithSlash) {
        return "http://localhost:%d%s".formatted(port, uriStartWithSlash);
    }

    private Faculty createFaculty(String name, String color) {
        Faculty faculty = new Faculty();
        faculty.setName(name);
        faculty.setColor(color);
        return facultyRepository.save(faculty);
    }

    private Student createStudent(Faculty faculty, String name, int age) {
        Student student = new Student();
        student.setFaculty(faculty);
        student.setName(name);
        student.setAge(age);
        return studentRepository.save(student);
    }

    private void assertFacultyEquals(Faculty expected, Faculty actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getColor(), actual.getColor());
    }

    @Test
    void createFacultyTest() {
        Faculty faculty = new Faculty("Griffindor", "red");

        ResponseEntity<Faculty> facultyResponseEntity = restTemplate.postForEntity(buildUrl("/faculties"), faculty, Faculty.class);

        assertNotNull(facultyResponseEntity);
        assertEquals(HttpStatus.OK, facultyResponseEntity.getStatusCode());

        Faculty actualFaculty = facultyResponseEntity.getBody();
        assertNotNull(actualFaculty.getId());
        assertEquals(faculty.getName(), actualFaculty.getName());
        assertEquals(faculty.getColor(), actualFaculty.getColor());
    }

    @Test
    void updateFacultyTest() {
        Faculty faculty = new Faculty("test", "test");
        faculty = facultyRepository.save(faculty);

        Faculty facultyForUpdate = new Faculty("test1", "test1");

        HttpEntity<Faculty> entity = new HttpEntity<>(facultyForUpdate);
        ResponseEntity<Faculty> facultyResponseEntity = restTemplate.exchange(buildUrl("/faculties/" + faculty.getId()), HttpMethod.PUT, entity, Faculty.class);

        assertNotNull(facultyResponseEntity);
        assertEquals(HttpStatus.OK, facultyResponseEntity.getStatusCode());

        Faculty actualFaculty = facultyResponseEntity.getBody();
        assertNotNull(actualFaculty);
        assertEquals(faculty.getId(), actualFaculty.getId());
        assertEquals(facultyForUpdate.getName(), actualFaculty.getName());
        assertEquals(facultyForUpdate.getColor(), actualFaculty.getColor());
    }

    @Test
    void readFacultyTest() {
        Faculty faculty = new Faculty("test", "test");
        faculty = facultyRepository.save(faculty);

        ResponseEntity<Faculty> facultyResponseEntity = restTemplate.getForEntity(buildUrl("/faculties/" + faculty.getId()), Faculty.class);
        assertNotNull(facultyResponseEntity);
        assertEquals(HttpStatus.OK, facultyResponseEntity.getStatusCode());

        Faculty actualFaculty = facultyResponseEntity.getBody();
        assertFacultyEquals(faculty, actualFaculty);
    }

    @Test
    void deleteFacultyTest() {
        Faculty faculty = new Faculty("test", "test");
        faculty = facultyRepository.save(faculty);

        ResponseEntity<Faculty> facultyResponseEntity = restTemplate.exchange(buildUrl("/faculties/" + faculty.getId()), HttpMethod.DELETE, null, Faculty.class);
        assertNotNull(facultyResponseEntity);
        assertEquals(HttpStatus.OK, facultyResponseEntity.getStatusCode());
        assertThat(facultyRepository.findById(faculty.getId())).isNotPresent();
    }

    @Test
    public void testFilterByColor() {
        ResponseEntity<Faculty[]> responseEntity = restTemplate.getForEntity(buildUrl("/faculties?color=Red"), Faculty[].class);

        List<Faculty> faculties = Arrays.asList(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(faculties).isNotNull();
        assertThat(faculties).contains(faculty1);
        assertThat(faculties).doesNotContain(faculty2);
    }

    @Test
    public void testFindByColorOrNameIgnoreCase() {
        ResponseEntity<Faculty[]> responseEntity = restTemplate.getForEntity(
                buildUrl("/faculties/byNameOrColor?name=griffindor&color=green"),
                Faculty[].class
        );

        List<Faculty> faculties = Arrays.asList(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(faculties).isNotNull();
        assertThat(faculties).contains(faculty1, faculty2);
    }

    @Test
    public void testGetStudents() {
        ResponseEntity<Student[]> responseEntity = restTemplate.getForEntity(buildUrl("/faculties/" + faculty1.getId() + "/students"), Student[].class);

        List<Student> students = Arrays.asList(responseEntity.getBody());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students).isNotNull();
        assertThat(students).contains(student1);
        assertThat(students).doesNotContain(student2);
    }
}



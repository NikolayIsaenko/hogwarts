package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repasitory.StudentRepository;
import ru.hogwarts.school.service.AvatarService;
import ru.hogwarts.school.service.StudentService;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StudentController.class)
public class StudentsMvcTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private StudentService studentService;
    @MockBean
    private AvatarService avatarService;

    private final Faker faker = new Faker();

    private Student createStudent(Long id) {
        Student student = new Student();
        student.setId(id);
        student.setName(faker.harryPotter().character());
        student.setAge(faker.random().nextInt(13, 19));
        return student;
    }

    @Test
    public void createStudentTest() throws Exception {
        Long studentId = 1L;
        Student student = new Student("Tuii", 30);
        Student saveStudent = new Student("Baun", 13);
        saveStudent.setId(studentId);

        when(studentService.create(student)).thenReturn(saveStudent);

        ResultActions perform = mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)));


        perform
                .andExpect(jsonPath("$.id").value(saveStudent.getId()))
                .andExpect(jsonPath("$.name").value(saveStudent.getName()))
                .andExpect(jsonPath("$.age").value(saveStudent.getAge()))
                .andDo(print());
    }

    @Test
    public void readTest() throws Exception {
        Student student = createStudent(1L);
        when(studentService.read(student.getId())).thenReturn(student);

        mockMvc.perform(get("/students/" + student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(student.getAge()))
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.id").value(student.getId()));
    }

    @Test
    public void updateTest() throws Exception {
        Student student = createStudent(1L);

        when(studentService.update(1L, student)).thenReturn(student);

        ResultActions perform = mockMvc.perform(put("/students/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)));

        perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andExpect(jsonPath("$.age").value(student.getAge()))
                .andDo(print());
    }

    @Test
    public void testDeleteStudent() throws Exception {
        Student student = createStudent(1L);

        when(studentService.create(student)).thenReturn(student);
        when(studentService.delete(1L)).thenReturn(student);

        when(studentRepository.existsById(1L)).thenReturn(false);

        assertNotNull(student.getId());

        mockMvc.perform(delete("/students/{id}", student.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(student.getId()))
                .andExpect(jsonPath("$.name").value(student.getName()))
                .andDo(print());

        equals(studentRepository.existsById(student.getId()));
    }


    @Test
    public void testDeleteNonExistentStudent() throws Exception {
        mockMvc.perform(delete("/students/{id}", 999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testFilterByAge() throws Exception {
        Student student1 = createStudent(1L);
        Student student2 = createStudent(2L);

        List<Student> students = Arrays.asList(student1, student2);

        when(studentService.filterByAge(15)).thenReturn(students);

        mockMvc.perform(get("/students")
                        .param("age", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value(student1.getName()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value(student2.getName()))
                .andDo(print());
    }

    @Test
    public void testFindAllByAgeBetween() throws Exception {
        Student student1 = createStudent(1L);
        Student student2 = createStudent(2L);
        Student student3 = createStudent(3L);

        List<Student> students = Arrays.asList(student1, student2, student3);

        when(studentService.findAllByAgeBetween(13, 19)).thenReturn(students);

        mockMvc.perform(get("/students/betweenByAge")
                        .param("fromAge", "13")
                        .param("toAge", "19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value(student1.getName()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value(student2.getName()))
                .andExpect(jsonPath("$[2].id").value(3L))
                .andExpect(jsonPath("$[2].name").value(student3.getName()))
                .andDo(print());
    }

    @Test
    public void testGetFacultyStudent() throws Exception {
        Student student1 = createStudent(1L);
        Faculty faculty = new Faculty("Griffindor", "red");
        student1.setFaculty(faculty);

        when(studentService.getFacultyStudent(1L)).thenReturn(faculty);

        mockMvc.perform(get("/students/{id}/faculty", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andDo(print());
    }
}





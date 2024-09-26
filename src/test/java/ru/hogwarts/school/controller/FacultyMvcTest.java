package ru.hogwarts.school.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class FacultyMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;


    @Autowired
    private ObjectMapper objectMapper;

    private Faculty createFaculty(Long id, String name, String color) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        faculty.setName(name);
        faculty.setColor(color);
        return faculty;
    }

    private Student createStudent(Long id, String name, int age) {
        Student student = new Student();
        student.setId(id);
        student.setName(name);
        student.setAge(age);
        return student;
    }

    private ResultActions performAndExpect(ResultActions perform, Faculty faculty) throws Exception {
        return perform
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(faculty.getId()))
                .andExpect(jsonPath("$.name").value(faculty.getName()))
                .andExpect(jsonPath("$.color").value(faculty.getColor()))
                .andDo(print());
    }

    @Test
    public void testCreateFaculty() throws Exception {
        Faculty faculty = createFaculty(1L, "Faculty 1", "Red");

        when(facultyService.create(faculty)).thenReturn(faculty);

        ResultActions perform = mockMvc.perform(post("/faculties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(faculty)));

        performAndExpect(perform, faculty);
    }

    @Test
    public void testReadFaculty() throws Exception {
        Faculty faculty = createFaculty(1L, "Faculty 1", "Red");

        when(facultyService.read(1L)).thenReturn(faculty);

        ResultActions perform = mockMvc.perform(get("/faculties/{id}", 1L));

        performAndExpect(perform, faculty);
    }

    @Test
    public void testUpdateFaculty() throws Exception {
        Faculty faculty = createFaculty(1L, "Faculty 1", "Red");

        when(facultyService.update(1L, faculty)).thenReturn(faculty);

        ResultActions perform = mockMvc.perform(put("/faculties/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(faculty)));

        performAndExpect(perform, faculty);
    }

    @Test
    public void testDeleteFaculty() throws Exception {
        Faculty faculty = createFaculty(1L, "Faculty 1", "Red");

        when(facultyService.delete(1L)).thenReturn(faculty);

        ResultActions perform = mockMvc.perform(delete("/faculties/{id}", 1L));

        performAndExpect(perform, faculty);
    }

    @Test
    public void testFilterByColor() throws Exception {
        Faculty faculty1 = createFaculty(1L, "Faculty 1", "Red");
        Faculty faculty2 = createFaculty(2L, "Faculty 2", "Red");

        List<Faculty> faculties = Arrays.asList(faculty1, faculty2);

        when(facultyService.filterByColor("Red")).thenReturn(faculties);

        mockMvc.perform(get("/faculties")
                        .param("color", "Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Faculty 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Faculty 2"))
                .andDo(print());
    }

    @Test
    public void testFindByColorOrNameIgnoreCase() throws Exception {
        Faculty faculty1 = createFaculty(1L, "Faculty 1", "Red");
        Faculty faculty2 = createFaculty(2L, "Faculty 2", "Blue");

        List<Faculty> faculties = Arrays.asList(faculty1, faculty2);

        when(facultyService.findByColorOrNameIgnoreCase("faculty", "red")).thenReturn(faculties);

        mockMvc.perform(get("/faculties/byNameOrColor")
                        .param("name", "faculty")
                        .param("color", "red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Faculty 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Faculty 2"))
                .andDo(print());
    }

    @Test
    public void testGetStudents() throws Exception {
        Faculty faculty = createFaculty(1L, "Faculty 1", "Red");

        Student student1 = createStudent(1L, "Student 1", 20);
        Student student2 = createStudent(2L, "Student 2", 22);

        List<Student> students = Arrays.asList(student1, student2);

        when(facultyService.getStudents(1L)).thenReturn(students);

        mockMvc.perform(get("/faculties/{id}/students", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Student 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Student 2"))
                .andDo(print());
    }
}
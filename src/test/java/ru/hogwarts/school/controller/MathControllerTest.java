package ru.hogwarts.school.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MathController.class)
public class MathControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetSum() throws Exception {
        mockMvc.perform(get("/math/sum"))
                .andExpect(status().isOk())
                .andExpect(content().string("500000500000"));
    }
}
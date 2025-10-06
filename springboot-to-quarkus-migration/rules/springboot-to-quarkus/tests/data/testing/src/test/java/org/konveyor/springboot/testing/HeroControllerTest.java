package org.konveyor.springboot.testing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(HeroController.class)
class HeroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HeroService heroService;

    @Test
    void shouldGetAllHeroes() throws Exception {
        mockMvc.perform(get("/heroes"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldCreateHero() throws Exception {
        String heroJson = "{\"name\":\"Superman\",\"level\":10,\"power\":\"flight\"}";
        
        mockMvc.perform(post("/heroes")
               .contentType(MediaType.APPLICATION_JSON)
               .content(heroJson))
               .andExpect(status().isCreated());
    }
}

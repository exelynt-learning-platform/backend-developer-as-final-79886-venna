package com.example.booking;

import com.example.booking.entity.*;
import com.example.booking.repository.AppUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired AppUserRepository users;
    @Autowired PasswordEncoder encoder;

    @BeforeEach
    void addSecondUser() {
        if (!users.existsByUsername("other")) users.save(new AppUser("other", encoder.encode("Other123!"), Role.USER));
    }

    @Test
    void rejectsAnonymousRequestsAndAllowsAuthenticatedReads() throws Exception {
        mvc.perform(get("/resources"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
        mvc.perform(get("/resources").header("Authorization", "Bearer " + token("user", "User123!")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void enforcesRolesAndReservationOwnership() throws Exception {
        String userToken = token("user", "User123!");
        mvc.perform(post("/resources").header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Nope\",\"price\":10,\"available\":true}"))
                .andExpect(status().isForbidden());

        String reservation = "{\"resourceId\":1,\"startDate\":\"2099-01-01\",\"endDate\":\"2099-01-02\"}";
        String response = mvc.perform(post("/reservations").header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content(reservation))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(response).get("id").asLong();
        mvc.perform(get("/reservations/" + id).header("Authorization", "Bearer " + token("other", "Other123!")))
                .andExpect(status().isForbidden());
        mvc.perform(get("/reservations?minPrice=40&maxPrice=60&page=0&size=5")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content[0].price").value(50.0));
    }

    private String token(String username, String password) throws Exception {
        String body = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode json = mapper.readTree(body);
        return json.get("token").asText();
    }
}

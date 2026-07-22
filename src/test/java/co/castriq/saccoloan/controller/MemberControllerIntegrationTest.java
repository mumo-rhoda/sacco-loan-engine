package co.castriq.saccoloan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import co.castriq.saccoloan.dto.MemberRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registersMemberSuccessfully() throws Exception {
        MemberRequest request = new MemberRequest("Achieng Otieno", "+254712345678", "12345678");

        mockMvc.perform(post("/api/members")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Achieng Otieno"))
                .andExpect(jsonPath("$.memberNumber").isNotEmpty());
    }

    @Test
    void rejectsInvalidPhoneNumber() throws Exception {
        MemberRequest request = new MemberRequest("Achieng Otieno", "0712345678", "87654321");

        mockMvc.perform(post("/api/members")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(org.hamcrest.Matchers.containsString("phoneNumber")));
    }
}

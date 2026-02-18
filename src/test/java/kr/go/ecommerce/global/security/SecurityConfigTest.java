package kr.go.ecommerce.global.security;

import kr.go.ecommerce.global.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class SecurityConfigTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // === Public endpoints should NOT return 401 ===

    @Test
    void publicBrandsEndpointShouldNotReturn401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/brands")).andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
    }

    @Test
    void publicProductsEndpointShouldNotReturn401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/products")).andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
    }

    @Test
    void authEndpointsShouldNotReturn401() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType("application/json")
                        .content("{\"loginId\":\"test\",\"email\":\"test@test.com\",\"password\":\"Test1234!\",\"name\":\"Test\"}"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(401);
    }

    // === Protected endpoints should return 401 without token ===

    @Test
    void usersMeShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cartShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void ordersShouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    // === Admin endpoints ===

    @Test
    void adminEndpointShouldReturn403WithUserToken() throws Exception {
        String token = jwtTokenProvider.createToken(1L, "USER");
        mockMvc.perform(get("/api-admin/v1/brands")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointShouldNotReturn403WithAdminToken() throws Exception {
        String token = jwtTokenProvider.createToken(1L, "ADMIN");
        MvcResult result = mockMvc.perform(get("/api-admin/v1/brands")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(403);
    }
}

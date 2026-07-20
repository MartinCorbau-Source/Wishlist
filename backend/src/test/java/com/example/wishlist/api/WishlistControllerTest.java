package com.example.wishlist.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
@AutoConfigureMockMvc
class WishlistControllerTest {
  @Autowired MockMvc mvc;
  @Autowired ObjectMapper json;

  @Test
  void reservationIsVisibleToGuestsButNeverToOwner() throws Exception {
    JsonNode created = body(post("/api/wishlists").contentType(MediaType.APPLICATION_JSON)
        .content("{\"ownerName\":\"Alex\"}"));
    String ownerKey = created.get("ownerKey").asText();
    String shareToken = created.get("shareToken").asText();

    JsonNode item = body(post("/api/owner/{key}/items", ownerKey).contentType(MediaType.APPLICATION_JSON)
        .content("{\"name\":\"A book\"}"));
    mvc.perform(post("/api/shared/{token}/items/{id}/reservation", shareToken, item.get("id").asText())
        .contentType(MediaType.APPLICATION_JSON).content("{\"reservedBy\":\"Sam\",\"note\":\"Hardback\"}"))
        .andExpect(status().isNoContent());

    String ownerJson = mvc.perform(get("/api/owner/{key}", ownerKey)).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    String guestJson = mvc.perform(get("/api/shared/{token}", shareToken)).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();
    assertThat(ownerJson).doesNotContain("reserved", "Sam", "Hardback");
    assertThat(guestJson).contains("\"reserved\":true", "Sam", "Hardback");
  }

  private JsonNode body(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request) throws Exception {
    return json.readTree(mvc.perform(request).andExpect(status().is2xxSuccessful())
        .andReturn().getResponse().getContentAsString());
  }
}

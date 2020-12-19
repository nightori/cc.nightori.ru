package ru.nightori.cc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.nightori.cc.config.Config.APP_DOMAIN;
import static ru.nightori.cc.config.Config.RESERVED_URLS;

@WebMvcTest(RedirectController.class)
class WebLayerTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RedirectService mockedClientService;

    @MockBean
    ClientCacheService mockedCacheService;

    @Test
    void homeRedirectTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/home"));
    }

    @Test
    void createRedirectTest() throws Exception {
        String expected = String.format("https://%s/null", APP_DOMAIN);

        mockMvc.perform(post("/api")
                .param("shortUrl", "google")
                .param("destination", "https://google.com")
                .param("password", "12345"))
                .andExpect(status().isOk())
                .andExpect(content().string(expected));
        verify(mockedClientService).createRedirect(any(), any(), any());
    }

    @Test
    void deleteRedirectTest() throws Exception {
        mockMvc.perform(delete("/api")
                .param("shortUrl", "google")
                .param("password", "12345"))
                .andExpect(status().isOk());
        verify(mockedClientService).deleteRedirect(any(), any());
    }

    @Test
    void urlRedirectTestSuccess() throws Exception {
        String expectedURL = "https://example.com";
        when(mockedClientService.getRedirectUrl(anyString())).thenReturn(expectedURL);

        mockMvc.perform(get("/randomUrl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", expectedURL));
    }

    @Test
    void urlRedirectTestReserved() throws Exception {
        String reservedUrl = "/" + RESERVED_URLS.get(0);
        mockMvc.perform(get(reservedUrl)).andExpect(status().isNotFound());
    }

    @Test
    void illegalHeaderTest() throws Exception {
        mockMvc.perform(delete("/api")
                .param("shortUrl", "google")
                .param("password", "12345")
                .header("x-forwarded-for", "127.0.0.1"))
                .andExpect(status().isBadRequest());
    }
}
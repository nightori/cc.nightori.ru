package ru.nightori.cc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.nightori.cc.service.RedirectService;
import ru.nightori.cc.service.ClientCacheService;
import ru.nightori.cc.web.RedirectController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.nightori.cc.CcApplication.APP_DOMAIN;

@WebMvcTest(RedirectController.class)
class WebTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	RedirectService mockClientService;

	@MockBean
	ClientCacheService mockCacheService;

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

		verify(mockClientService).createRedirect(any(), any(), any());
	}

	@Test
	void deleteRedirectTest() throws Exception {
		mockMvc.perform(delete("/api")
				.param("shortUrl", "google")
				.param("password", "12345"))
				.andExpect(status().isOk());

		verify(mockClientService).deleteRedirect(any(), any());
	}

	@Test
	void urlRedirectTestSuccess() throws Exception {
		String expectedURL = "https://example.com";
		when(mockClientService.getRedirectUrl(anyString())).thenReturn(expectedURL);

		mockMvc.perform(get("/randomUrl"))
				.andExpect(status().is3xxRedirection())
				.andExpect(header().string("Location", expectedURL));
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
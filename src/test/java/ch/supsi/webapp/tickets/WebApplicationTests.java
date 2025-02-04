package ch.supsi.webapp.tickets;

import ch.supsi.webapp.tickets.model.Ticket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class WebApplicationTests {

	@Autowired
	private MockMvc mvc;

	private static int id;
	private static int id2;
	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	@DisplayName("Should get empty list of tickets")
	// GET /tickets -> size == 0
	public void test01() throws Exception {
		this.mvc.perform(get("/tickets").accept("application/json"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$",  Matchers.hasSize(2)));
	}

	@Test
	@DisplayName("Should add a ticket and get it back")
	// POST /tickets
	public void test02() throws Exception {
		MvcResult mvcResult = this.mvc.perform(post("/tickets")
						.header("Content-Type", "application/json")
						.content("""
								{"title":"titolo", "description":"testo", "author":"admin"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.title").value("titolo"))
				.andExpect(jsonPath("$.description").value("testo"))
				.andExpect(jsonPath("$.author").value("admin"))
				.andExpect(jsonPath("$.id").exists())
				.andReturn();
		id = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Ticket.class).getId(); System.out.println(id);
	}

	@Test
	@DisplayName("Should get list of tickets with the newly added ticket")
	// GET /tickets -> size == 1
	public void test03() throws Exception {
		this.mvc.perform(get("/tickets").accept("application/json"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$",  Matchers.hasSize(3)))
				.andExpect(jsonPath("$[2].id").value(id))
				.andExpect(jsonPath("$[2].title").value("titolo"))
				.andExpect(jsonPath("$[2].author").value("admin"))
				.andExpect(jsonPath("$[2].description").value("testo"));
	}

	@Test
	@DisplayName("Should get the newly added ticket")
	// GET /tickets/{id}
	public void test04() throws Exception {
		this.mvc.perform(get("/tickets/"+id).accept("application/json"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.title").value("titolo"))
				.andExpect(jsonPath("$.description").value("testo"))
				.andExpect(jsonPath("$.author").value("admin"))
				.andExpect(jsonPath("$.id").value(id));
	}

	@Test
	@DisplayName("Should get 404 for a not existing ticket")
	// GET /tickets/{id}+100 -> 404
	public void test05() throws Exception {
		this.mvc.perform(get("/tickets/"+(id+100)).accept("application/json"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Should modify an existing ticket")
	// PUT /tickets/{id}
	public void test06() throws Exception {
		this.mvc.perform(put("/tickets/"+id)
						.header("Content-Type", "application/json")
						.content("""
								{"title":"titolo2", "description":"testo2", "author":"admin" }
								"""))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.title").value("titolo2"))
				.andExpect(jsonPath("$.description").value("testo2"))
				.andExpect(jsonPath("$.author").value("admin"))
				.andExpect(jsonPath("$.id").value(id));
	}

	@Test
	@DisplayName("Should get 404 when trying to modify a non existing ticket")
	// PUT /tickets/{id}+1 -> 404
	public void test07() throws Exception {
		this.mvc.perform(put("/tickets/"+(id+1))
						.header("Content-Type", "application/json")
						.content("{\"title\":\"titolo2\", \"description\":\"testo2\", \"author\":\"admin\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Should get list of tickets with the modified ticket")
	// GET /tickets -> size == 1
	public void test08() throws Exception {
		this.mvc.perform(get("/tickets").accept("application/json"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$",  Matchers.hasSize(3)))
				.andExpect(jsonPath("$[2].title").value("titolo2"))
				.andExpect(jsonPath("$[2].author").value("admin"))
				.andExpect(jsonPath("$[2].description").value("testo2"));
	}

	@Test
	@DisplayName("Should add a second ticket and get it back")
	// POST /tickets
	public void test09() throws Exception {
		MvcResult mvcResult = this.mvc.perform(post("/tickets")
						.header("Content-Type", "application/json")
						.content("""
								{"title":"titolo3", "description":"testo3", "author":"admin"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.title").value("titolo3"))
				.andExpect(jsonPath("$.description").value("testo3"))
				.andExpect(jsonPath("$.author").value("admin"))
				.andExpect(jsonPath("$.id").exists())
				.andReturn();
		id2 = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Ticket.class).getId(); System.out.println(id);
	}

	@Test
	@DisplayName("Should delete the newly added ticket")
	// DELETE /tickets/{id}
	public void test10() throws Exception {
		this.mvc.perform(delete("/tickets/"+id))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("Should get 404 when trying to delete a non existing ticket")
	// DELETE /tickets/{id}+100 -> 404
	public void test11() throws Exception {
		this.mvc.perform(delete("/tickets/"+(id+100)))
				.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Should get list of tickets without the deleted ticket")
	// GET /tickets -> size == 1
	public void test12() throws Exception {
		this.mvc.perform(get("/tickets").accept("application/json"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$",  Matchers.hasSize(3)))
				.andExpect(jsonPath("$[2].title").value("titolo3"))
				.andExpect(jsonPath("$[2].author").value("admin"))
				.andExpect(jsonPath("$[2].description").value("testo3"));
	}

	@Test
	@DisplayName("Should delete the first added ticket")
	// DELETE /tickets/{id2}
	public void test13() throws Exception {
		this.mvc.perform(delete("/tickets/"+id2))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("Should get list of empty tickets after deleting all of them")
	// GET /tickets -> size == 0
	public void test14() throws Exception {
		this.mvc.perform(get("/tickets").accept("application/json"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$",  Matchers.hasSize(2)));
	}

}
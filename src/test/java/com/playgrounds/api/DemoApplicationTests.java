package com.playgrounds.api;

import com.playgrounds.api.playground.repository.PlaygroundRepository;
import com.playgrounds.api.playground.repository.PlaygroundRepositoryImpl;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DemoApplication.class)
@WebAppConfiguration
public class DemoApplicationTests {

	private PlaygroundRepositoryImpl playgroundService;
	private PlaygroundRepository playgroundRepository;

	@Before
	public void setup() {
		playgroundRepository = Mockito.mock(PlaygroundRepository.class);
	}


	public void contextLoads() throws Exception {
		/*
		Path path = Paths.get("src/main/resources/TestFile.txt");
		System.out.println("Path is: "+path);
		String name = "TestFile.txt";
		String originalFileName = "TestFile.txt";
		String contentType = "text/plain";
		byte[] content = null;
		try {
			content = Files.readAllBytes(path);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		MockMultipartFile fileToUplaod = new MockMultipartFile(name,
				originalFileName, contentType, content);
		Boolean flag = playgroundRepository.uploadImage(fileToUplaod);
		Assert.isTrue(flag);
		*/
	}

	@Test
	public void test(){
		RestAssured.authentication = basic("57ed189a9a15cfb595c302c5", "password");

		final File file = new File("src/main/resources/city-logo.jpg");
		assertNotNull(file);
		assertTrue(file.canRead());
		given().
				param("user","57ed189a9a15cfb595c302c5").
				multiPart(file).
				expect().body(equalTo("Image upload successfully")).
				when().
				post("/playgrounds/upload/57ed2f959a15154cb247dd64");
	}

}

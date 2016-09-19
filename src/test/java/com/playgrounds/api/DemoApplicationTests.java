package com.playgrounds.api;

import com.playgrounds.api.Repository.PlaygroundOperations;
import com.playgrounds.api.Repository.PlaygroundRepository;
import com.playgrounds.api.Repository.PlaygroundRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.AssertTrue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

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
	}

	@Test
	public void test(){
		given().
				param("file","file.txt").
				multiPart("file",new File("src/main/resources/Test.txt")).
				when().
				post("/upload");

	}

}

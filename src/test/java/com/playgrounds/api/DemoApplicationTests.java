package com.playgrounds.api;

import com.playgrounds.api.playground.model.Location;
import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.repository.PlaygroundRepository;
import com.playgrounds.api.playground.repository.PlaygroundRepositoryImpl;
import io.restassured.RestAssured;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.basic;
import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
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
		RestAssured.authentication = basic("580b731ca6f5ce53392e8ee6", "password");

		final File file = new File("src/main/resources/playcenter_missing_photo.png");
		assertNotNull(file);
		assertTrue(file.canRead());
		given().
				param("user","580b731ca6f5ce53392e8ee6").
				multiPart(file).
				expect().body(equalTo("Image upload successfully")).
				when().
				post("/playgrounds/upload/580b73d1a6f5ce53392e8ee7");
	}

	@Test
	public void addNewPlaygroundTest() {
		RestAssured.authentication = basic("58bc714e73c95160c01e5e92", "password");
		String name = "TestName";
		String address = "TestAddress";
		double[] coordinates = {40.221, 29.099};
		Location location = new Location(coordinates);
		String added_by = "58bc714e73c95160c01e5e92";

		final File file = new File("src/main/resources/ic_clear_white_18dp.png");
		assertNotNull(file);
		assertTrue(file.canRead());
		try {
			byte[] encoded = Base64.encodeBase64(FileUtils.readFileToByteArray(file));
			String base64Image =  new String(encoded, StandardCharsets.US_ASCII);
			//byte[] bytes = new byte[(int) file.length()];
			//String base64Image = Base64.encodeBase64String(bytes);
			Playground playground = new Playground(name, address, location, added_by, base64Image);
			given()
					.contentType("application/json")
					.body(playground)
					.expect().statusCode(201)
					.when()
					.post("/playgrounds");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

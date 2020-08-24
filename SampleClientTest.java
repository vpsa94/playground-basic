
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SampleClientTest {

	static File file;

	// First test case to execute, this method
	@Test
	public void test1() {
		URL url = this.getClass().getResource("/last_names.txt");
		assertTrue(url != null);
		file = new File(url.getFile());
	}

	@Test
	public void testTrueReadFileWithResource() {
		assertTrue(file.exists());
	}

	@Test
	public void testEmptyFile() {
		assertFalse(file.length() == 0);
	}

	@Test
	public void testFileContent() {
		int lines = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while (reader.readLine() != null)
				lines++;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertTrue(lines == 20);
	}

}

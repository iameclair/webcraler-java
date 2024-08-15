package utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TestUtils {
    public static String loadResourceAsString(final String resourceName) throws IOException, URISyntaxException {
        final Path path = Paths.get(Objects.requireNonNull(TestUtils.class.getClassLoader().getResource(resourceName)).toURI());
        return new String(Files.readAllBytes(path));
    }
}

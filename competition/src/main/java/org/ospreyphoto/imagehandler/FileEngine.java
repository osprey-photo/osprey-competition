package org.ospreyphoto.imagehandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ospreyphoto.model.CompetitionImage;
import org.ospreyphoto.model.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEngine {
    final Logger logger = LoggerFactory.getLogger(FileEngine.class);

    private Path basePath;

    public FileEngine(String basePath) {
        this.basePath = Paths.get(basePath);
        if (!Files.exists(this.basePath)) {
            throw new RuntimeException("Path does not exist " + basePath);
        }
        if (!Files.isDirectory(this.basePath)) {
            throw new RuntimeException("It should be a directory " + basePath);
        }
    }

    public List<CompetitionImage> scanImages() {
        logger.info("Scanning {}", this.basePath);
        try (Stream<Path> stream = Files.list(this.basePath)) {
            final List<String> imgPaths = stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)

                    .map(Path::toString)
                    .filter(p -> !p.startsWith("."))
                    .filter(p -> p.endsWith(".jpg") || p.endsWith("JPG"))

                    .collect(Collectors.toList());

            return imgPaths.stream().map((String p) -> {
                try {
                    String name = p.substring(0, p.indexOf("%"));
                    var title = p.substring(p.lastIndexOf('%') + 1, p.indexOf("."));
                    var fname = Paths.get(this.basePath.toString(), p);
                    var state = new State();
                    return CompetitionImage.builder().withName(name).withTitle(title).withFilename(fname).withState(state).build();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to load iamges ", e);
                }
            }).collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Unable to load iamges ", e);
        }
    }

}

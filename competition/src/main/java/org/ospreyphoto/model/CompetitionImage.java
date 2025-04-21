package org.ospreyphoto.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompetitionImage {
    final Logger logger = LoggerFactory.getLogger(CompetitionImage.class);
    private Path filePath;

    @JsonProperty
    private String title;

    @JsonProperty
    private String photographer;

    @JsonProperty
    private String id;

    @JsonProperty
    private String thumbnailB64;

    @JsonProperty
    private String halfishB64;

    @JsonProperty
    public String fullImageB64;

    @JsonProperty
    private State state;

    public Path getFilePath() {
        return filePath;
    }

    protected CompetitionImage setFilePath(Path filePath) {
        this.filePath = filePath;
        return this;
    }

    public void setFullImageB64(String fullImageB64) {
        this.fullImageB64 = fullImageB64;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getID() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    protected CompetitionImage setID(String id) {
        this.id = id;
        return this;
    }

    protected CompetitionImage setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPhotographer() {
        return photographer;
    }

    protected CompetitionImage setPhotographer(String photograher) {
        this.photographer = photograher;
        return this;
    }

    public String toString() {
        return "(" + this.id + ") " + this.title + " " + this.photographer + " [" + this.filePath + "]";
    }

    public static final Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Path filepath;
        private String name;
        private String title;
        private State state;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withFilename(Path filepath) {
            this.filepath = filepath;
            return this;
        }

        public CompetitionImage build() throws IOException {
            String id = String.format("%x", filepath.hashCode());

            var ci = new CompetitionImage().setID(id).setFilePath(filepath)
                    .setTitle(title)
                    .setPhotographer(name);
            ci.thumbnailB64 = ci.scale(200.0f);
            ci.halfishB64 = ci.scale(500.0f);
            ci.state=state;
            return ci;

        }

        public Builder withState(State state) {
            this.state = state;
            return this;
        }

    }

    public CompetitionImage loadFullImageB64() {
        try {
            this.fullImageB64 = Base64.getEncoder().encodeToString(Files.readAllBytes(this.filePath));
            return this;
        } catch (IOException e) {
            logger.error("Failed to read", e);
            throw new RuntimeException(e);
        }

    }

   

    protected String scale(float size) {
        try {
            BufferedImage image = ImageIO.read(this.filePath.toFile());
            var width = image.getWidth();
            var height = (int) (image.getHeight() * (size / width));

            logger.info(width + " " + image.getHeight());
         
            Image tb = image.getScaledInstance((int) size, -1, Image.SCALE_DEFAULT);

            BufferedImage outputImage = new BufferedImage((int) size, height, BufferedImage.TYPE_INT_RGB);
            outputImage.getGraphics().drawImage(tb, 0, 0, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(outputImage, "jpg", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {

            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

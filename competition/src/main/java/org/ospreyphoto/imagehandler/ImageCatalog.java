package org.ospreyphoto.imagehandler;

import static org.ospreyphoto.model.Constants.FIRST;
import static org.ospreyphoto.model.Constants.HC;
import static org.ospreyphoto.model.Constants.SECOND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ospreyphoto.config.AppState;
import org.ospreyphoto.config.Config;
import org.ospreyphoto.model.Competition;
import org.ospreyphoto.model.CompetitionImage;
import org.ospreyphoto.model.State;
import org.ospreyphoto.restendpoint.ImageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
@Singleton
public class ImageCatalog {
 static final Logger logger = LoggerFactory.getLogger(ImageCatalog.class);

    @Inject
    Config cfg;

    @Inject
    EventBus bus;

    @Inject
    AppState state;

    private FileEngine fe;
    private Map<String, CompetitionImage> images;

    public List<CompetitionImage> getSummary() {
        if (images!=null){
            return this.images.values().stream().collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                Collections.shuffle(collected);
                return collected;
            }));
            //.collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public CompetitionImage getImage(String id) {
        return this.images.get(id);
    }

    public CompetitionImage getFullImage(String id) {
        return getImage(id).loadFullImageB64();
    }

    public List<CompetitionImage> getSetOfImages(String filtereIds) {
        List<CompetitionImage> imgList = new ArrayList<>();
        for (String id : filtereIds.split(",")) {
            imgList.add(this.images.get(id));
        }
        return imgList;
    }

    public ImageCatalog updateState(String id, State state) {
        var img = this.images.get(id);
        img.setState(state);
        return this;
    }

    public boolean writeSummaryCSV(){
        var results = this.images.values().stream().map(e->e.toString()).collect(Collectors.joining("\n"));
        this.fe.writeFile(results);
        return true;
    }

    public List<CompetitionImage> getResults() {
        return this.images.values().stream().filter(img -> {
            return !img.getState().place.equals("");
        }).sorted(new Comparator<CompetitionImage>() {

            @Override
            public int compare(CompetitionImage o1, CompetitionImage o2) {
                var place01 = o1.getState().place;
                var place02 = o2.getState().place;
                if (place01.equals(FIRST)) {
                    return -1;
                }
                if (place01.equals(HC)) {
                    return 1;
                }
                if (place02.equals(FIRST)) {
                    return 1;
                }
                if (place02.equals(HC)) {
                    return -1;
                }
                if (place01.equals(SECOND)) {
                    // already got the first convered for 02
                    return -1;
                }
                if (place02.equals(SECOND)) {
                    // already got the first convered for 02
                    return 1;
                }
                return 0;

            }

        }).collect(Collectors.toList());

    }

    private String loadFromDir() {
        logger.info(state.getCurrentCompetition());
        Competition comp = state.getSettings().competitions.get(state.getCurrentCompetition());
        logger.info("{}",state.getSettings().competitions.entrySet());
        String name = comp.imageSrc;
        this.fe = new FileEngine(name,this.bus);
        this.images = this.fe.scanImages().stream().collect(Collectors.toMap(CompetitionImage::getID, c -> c));
        return name;

    }

    @ConsumeEvent("catalog")
    @Blocking
    public String consume(String cmd) {

        switch (cmd) {
            case "load":
                var dir = loadFromDir();
                return dir;

        }
        return "Error";
    }
}

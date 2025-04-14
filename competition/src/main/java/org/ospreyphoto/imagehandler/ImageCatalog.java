package org.ospreyphoto.imagehandler;

import static org.ospreyphoto.model.Constants.FIRST;
import static org.ospreyphoto.model.Constants.HC;
import static org.ospreyphoto.model.Constants.SECOND;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ospreyphoto.config.Config;
import org.ospreyphoto.model.CompetitionImage;
import org.ospreyphoto.model.State;

import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import tv.wunderbox.nfd.FileDialogResult;
import tv.wunderbox.nfd.nfd.NfdFileDialog;

@Singleton
public class ImageCatalog {

    @Inject
    Config cfg;

    private FileEngine fe;
    private Map<String, CompetitionImage> images;



    public List<CompetitionImage> getSummary() {
        return this.images.values().stream().collect(Collectors.toList());
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

    public List<CompetitionImage> getResults() {
        return this.images.values().stream().filter(img->{
            return !img.getState().place.equals("");
        }).sorted(new Comparator<CompetitionImage>() {

            @Override
            public int compare(CompetitionImage o1, CompetitionImage o2) {
               var place01 = o1.getState().place;
               var place02 = o2.getState().place;
               if (place01.equals(FIRST)){
                return -1;
               }
               if (place01.equals(HC)){
                return 1;
               }
               if (place02.equals(FIRST)){
                return 1;
               }
               if (place02.equals(HC)){
                return -1;
               }
               if (place01.equals(SECOND)){
                // already got the first convered for 02
                return -1;
               }
               if (place02.equals(SECOND)){
                // already got the first convered for 02
                return 1;
               }
               return 0;

            }
            
        }).collect(Collectors.toList());
      

    }
    
    private String loadFromDir() {

        var nfd = new NfdFileDialog();
        FileDialogResult<File> r = nfd.pickDirectory(System.getProperty("user.home"));
        var s= r.toString();
        if (s.startsWith("Success")){
            var name = s.substring(s.indexOf('=')+1,s.length()-1);
            this.fe = new FileEngine(name);
            this.images = this.fe.scanImages().stream().collect(Collectors.toMap(CompetitionImage::getID, c -> c));
            return name;
        } else {
            throw new RuntimeException(s);
        }
        
      
      

    }

    @ConsumeEvent("catalog")
    @Blocking
    public String consume(String cmd){

        switch(cmd){
            case "load":
                var dir = loadFromDir();
                return dir;
              
        }
        return "Error";
    }
}

package org.ospreyphoto.config;

import org.ospreyphoto.model.CompetitionSettings;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class AppState {
    @Inject
    Config cfg;

    CompetitionSettings settings;

    public CompetitionSettings getSettings() {
        return this.settings;
    }

    public AppState updateSettings(CompetitionSettings settings){
        this.settings=settings;
        // write to disk
        return this;
    }
}

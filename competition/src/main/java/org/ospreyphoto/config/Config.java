package org.ospreyphoto.config;

import org.ospreyphoto.model.CompetitionSettings;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix="competition")
public interface Config {
    String fileLocation();
}

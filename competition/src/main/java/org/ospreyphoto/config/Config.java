package org.ospreyphoto.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix="competition")
public interface Config {
    String fileLocation();
}

package io.mywish.scanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Inf {
    Info info;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Info {
        @Getter
        @Setter
        int blocks;
    }
}

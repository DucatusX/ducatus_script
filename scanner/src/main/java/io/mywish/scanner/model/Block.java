package io.mywish.scanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Block {
    String hash;
    long size;
    long height;
    String[] tx;
}

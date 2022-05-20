package com.example.jsontestupgrade;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class mdFive {

    @JsonProperty("original")
    String originalInput;
    @JsonProperty("md5")
    String md_5;

    public mdFive(String originalInput, String md_5){
        this.originalInput = originalInput;
        this.md_5 = md_5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        mdFive mdFive = (mdFive) o;
        return Objects.equals(originalInput, mdFive.originalInput) && Objects.equals(md_5, mdFive.md_5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalInput, md_5);
    }
}

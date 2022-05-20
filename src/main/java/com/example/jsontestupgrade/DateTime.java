package com.example.jsontestupgrade;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@JsonPropertyOrder({"Date", "milliseconds_since_epoch", "Time"})
public class DateTime {
//    @JsonProperty("Date")
//    LocalDate localDate;
//    @JsonProperty("Time")
//    LocalTime localTime;
    @JsonProperty("Date")
    public String formattedDate;
    @JsonProperty("milliseconds_since_epoch")
    public long milliseconds;
    @JsonProperty("Time")
    public String formattedTime;

    public DateTime(String formattedDate, long milliseconds, String formattedTime) {
        this.formattedDate = formattedDate;
        this.milliseconds = milliseconds;
        this.formattedTime = formattedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateTime dateTime = (DateTime) o;
        return milliseconds == dateTime.milliseconds && Objects.equals(formattedDate, dateTime.formattedDate) && Objects.equals(formattedTime, dateTime.formattedTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formattedDate, milliseconds, formattedTime);
    }
}

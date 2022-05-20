package com.example.jsontestupgrade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// shall not mock annotation
@ExtendWith(MockitoExtension.class)
class JsonTestControllerTest {
    @InjectMocks
    JsonTestController jsonTestController;

    @Mock
    HttpServletRequest request;

    @Mock
    Date mockDate;

    @Test
    void itShouldReturnTheClientsIP(){
        final String ip = "1.2.3.4";
        final Ip expectedIP = new Ip(ip);
        when(request.getRemoteAddr()).thenReturn(ip);
        assertEquals(expectedIP, jsonTestController.getIpAddress(request));
    }

    @Test
    void itShouldReturnAllHeaders1(){
        // should return all the current http headers
        final Map<String, String> allHeaders = new HashMap<>();
        final Headers expectedHeader = new Headers(allHeaders);
        assertEquals(expectedHeader, jsonTestController.headers(allHeaders));
    }

    @Test
    void itShouldReturnDateTimeMilliseconds() {
        final LocalDate expectedDate = LocalDate.now();
        final DateTimeFormatter mockDateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        final String expectedDateFormatted = mockDateFormat.format(expectedDate);
        final LocalTime expectedTime = LocalTime.now();
        final DateTimeFormatter mockTimeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
        final String expectedTimeFormatted = mockTimeFormat.format(expectedTime);
        Date date1 = new Date();
        final long milliseconds = date1.getTime();

        final DateTime expectedDateTime = new DateTime(expectedDateFormatted, milliseconds, expectedTimeFormatted);
        Mockito.mockStatic(LocalDate.class).when(LocalDate::now)
                .thenReturn(expectedDate);
        Mockito.mockStatic(LocalTime.class).when(LocalTime::now)
                .thenReturn(expectedTime);
        //when(mockDate.getTime()).thenReturn(milliseconds);
        assertEquals(expectedDateTime.milliseconds, jsonTestController.dateTime().milliseconds);
    }
//    @Test
//    void itShouldReturnDateTimeMilliseconds1(){
//        final String expectedDate = "05-23-2020";
//        final String expectedTime = "05:15:05 AM";
//        final long milliseconds = 123456789123L;
//        final DateTime expectedDateTime = new DateTime(expectedDate, milliseconds, expectedTime);
//        assertEquals(expectedDateTime, jsonTestController.dateTime());
//    }

    @Test
    void itShouldReturnInputAsMD5() throws NoSuchAlgorithmException {
        String input = "hello";
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(input.getBytes());
        byte[] digest = messageDigest.digest();
        String inputHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
        mdFive expectedMd5 = new mdFive(input, inputHash);
        assertEquals(expectedMd5, jsonTestController.md5(input));
    }

}
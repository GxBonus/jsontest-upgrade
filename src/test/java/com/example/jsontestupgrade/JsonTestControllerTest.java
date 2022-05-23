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
    //JsonTestController jsonTestController = new JsonTestController();

    // if you don't need to inject then you don't need inject mock
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
        final LocalDate expectedDate = LocalDate.of(2020, 2,14);
        final DateTimeFormatter mockDateFormat = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        final String expectedDateFormatted = mockDateFormat.format(expectedDate);
        final LocalTime expectedTime = LocalTime.of(03,15,05);
        final DateTimeFormatter mockTimeFormat = DateTimeFormatter.ofPattern("hh:mm:ss a");
        final String expectedTimeFormatted = mockTimeFormat.format(expectedTime);
//        Date date1 = new Date();
//        final long milliseconds = date1.getTime();
        //System.out.println(milliseconds);
        final long milliseconds = 1234567890123L;

        final DateTime expectedDateTime = new DateTime(expectedDateFormatted, milliseconds, expectedTimeFormatted);

        final var mockedDate = Mockito.mockStatic(LocalDate.class); // prepare to change
        mockedDate.when(LocalDate::now)  // change source code
                .thenReturn(expectedDate);
        final var mockedTime= Mockito.mockStatic(LocalTime.class);
        mockedTime.when(LocalTime::now)
                .thenReturn(expectedTime);
//        Mockito.mockStatic(LocalDate.class).when(LocalDate::now)
//                .thenReturn(expectedDate);
//        Mockito.mockStatic(LocalTime.class).when(LocalTime::now)
//                .thenReturn(expectedTime);
        when(mockDate.getTime()).thenReturn(1234567890123L);
        assertEquals(expectedDateTime, jsonTestController.dateTime());
        mockedDate.close(); // undo the change
        mockedTime.close(); // undo the change
    }

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
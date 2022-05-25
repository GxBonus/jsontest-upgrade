package com.example.jsontestupgrade;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// shall not mock annotation
@ExtendWith(MockitoExtension.class)
class JsonTestControllerTest {
    //JsonTestController jsonTestController = new JsonTestController();

    // if you don't need to inject then you don't need inject mock
    @InjectMocks
    @Spy
    JsonTestController jsonTestController;

    @Mock
    HttpServletRequest request;

    @Mock
    Date mockDate;

    @Mock
    UserAccountRepository repository;

    @Mock
    HashMap<UUID, Long> tokenMap;

    @Mock
    RestTemplate rest;


    // The fizz controller will ask another controller if this user is authorized
    // URL of the other controller
    // Pass the token that the user is providing
    // Possible responses:
    // 1. OK - the user is authorized, we can proceed
    // 2. UNAUTHORIZED - we can throw UNAUTHORIZED
    // 3. Some other status code - throw INTERNAL_SERVER_ERROR

    // Checking for checkAuthorized(token) in Controller
    @Test
    void itShouldNotThrowWhenStatusIsOK(){
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:8081/isAuthorized?token=" + token;

        when(rest.getForEntity(url, Void.class)).thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        assertDoesNotThrow(() -> jsonTestController.checkAuthorized(token));

    }

    @Test
    void itShouldThrowUnautWhenStatusIsUnauth(){
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:8081/isAuthorized?token=" + token;

        when(rest.getForEntity(url, Void.class)).thenReturn(new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED));

        final ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> jsonTestController.checkAuthorized(token));
        assertEquals(HttpStatus.UNAUTHORIZED,exception.getStatus());
    }
    @Test
    void itShouldThrowInternalServerErrorWhenStatusIsOther(){
        final UUID token = UUID.randomUUID();
        String url = "http://localhost:8081/isAuthorized?token=" + token;

        when(rest.getForEntity(url, Void.class)).thenReturn(new ResponseEntity<Void>(HttpStatus.CONFLICT));

        final ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> jsonTestController.checkAuthorized(token));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,exception.getStatus());
    }

    @Test
    void itShouldReturnTheClientsIP(){
        final var token = UUID.randomUUID();
        final String ip = "1.2.3.4";
        final Ip expectedIP = new Ip(ip);
        doNothing().when(jsonTestController).checkAuthorized(token);
        when(request.getRemoteAddr()).thenReturn(ip);

        assertEquals(expectedIP, jsonTestController.getIpAddress(token, request));

    }

//    @Test
//    void itShouldThrowUnauthWhenIPIsCalledWithBadToken(){
//        final var token = UUID.randomUUID();
//        JsonTestController spyTemp = Mockito.spy(jsonTestController);
//
//        // what am I mocking?
//        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED))
//                .when(spyTemp).checkAuthorized(token);
//
//        //when(tokenMap.containsKey(token)).thenReturn(false);
//        assertThrows(ResponseStatusException.class, () -> spyTemp.getIpAddress(token, request));
//    }

    @Test
    void itShouldReturnAllHeaders1(){
        // should return all the current http headers
        final Map<String, String> allHeaders = new HashMap<>();
        final var token = UUID.randomUUID();

        doNothing().when(jsonTestController).checkAuthorized(token);
        allHeaders.put("header1", "value1");
        allHeaders.put("header2", "value2");
        final Headers expectedHeader = new Headers(allHeaders);
        assertEquals(expectedHeader, jsonTestController.headers(token ,allHeaders));
    }

//    @Test
//    void itShouldThrowUnauthWhenHeadersCalledWithBadToken(){
//        Map<String, String> expected = new HashMap<>();
//        final var token = UUID.randomUUID();
//        JsonTestController spyTemp = Mockito.spy(jsonTestController);
//        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED))
//                .when(spyTemp).checkAuthorized(token);
//        assertThrows(ResponseStatusException.class, () -> spyTemp.headers(token, expected));
//    }

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
        final var token = UUID.randomUUID();

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
        doNothing().when(jsonTestController).checkAuthorized(token);
        when(mockDate.getTime()).thenReturn(1234567890123L);
        assertEquals(expectedDateTime, jsonTestController.dateTime(token));
        mockedDate.close(); // undo the change
        mockedTime.close(); // undo the change
    }

//    @Test
//    void itShouldThrowUnauthWhenDateTimeIsCalledWithBadToken(){
//        final var token = UUID.randomUUID();
//        when(tokenMap.containsKey(token)).thenReturn(false);
//        assertThrows(ResponseStatusException.class, () -> jsonTestController.dateTime(token));
//    }

    @Test
    void itShouldReturnInputAsMD5() throws NoSuchAlgorithmException {
        final var token = UUID.randomUUID();
        final String input = "hello";
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(input.getBytes());
        byte[] digest = messageDigest.digest();
        String inputHash = DatatypeConverter.printHexBinary(digest).toLowerCase();
        mdFive expectedMd5 = new mdFive(input, inputHash);

        doNothing().when(jsonTestController).checkAuthorized(token); // mocking checkAuthorized in our MD5 method
        assertEquals(expectedMd5, jsonTestController.md5(token, input));
    }

//    @Test
//    void itShouldThrowUnauthWhenMD5IsCalledWithBadToken(){
//        final String input = "hello";
//        final var token = UUID.randomUUID();
//        JsonTestController spyController = Mockito.spy(jsonTestController);
//        doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED)).when(spyController).checkAuthorized(token);
//        assertThrows(ResponseStatusException.class, () -> spyController.md5(token, input));
//    }

    // test login when username is wrong and password is correct
    @Test
    void itShouldThrowUnauthWhenUserIsWrong(){
        final var username = "bad Username";
        final var password = "good Username";
        lenient().when(repository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.empty());
        lenient().when(repository.findByUsernameAndPassword(not(eq(username)), eq(password)))
                .thenReturn(Optional.of(new UserAccount()));

        assertThrows(ResponseStatusException.class,() -> jsonTestController.login(username, password));

    }

    @Test
    void itShouldThrowUnauthWhenPassIsWrong(){
        final var username = "good Username";
        final var password = "bad Username";
        lenient().when(repository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.empty());
        lenient().when(repository.findByUsernameAndPassword(eq(username), not(eq(password))))
                .thenReturn(Optional.of(new UserAccount()));

        assertThrows(ResponseStatusException.class,() -> jsonTestController.login(username, password));

    }

    @Test
    void itShouldMapTheUUIDToTheIdWhenLoginSuccess(){
        final var username = "good username";
        final var password = "good username";
        final Long id = (long)(Math.random() * 9999999);
        final UserAccount expected = new UserAccount();
        expected.id = id;
        expected.username = username;
        expected.password = password;
        when(repository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.of(expected));
        // read up on ArgumentCaptor
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        when(tokenMap.put(captor.capture(),eq(id))).thenReturn(0L);
        final var token = jsonTestController.login(username, password);

        assertEquals(token, captor.getValue());

    }

    @Test
    void itShouldThrowConflictWhenUsernameExist(){
        final String username = "some username";
        when(repository.findByUsername(username))
                .thenReturn(Optional.of(new UserAccount()));

        assertThrows(ResponseStatusException.class, () -> {
            jsonTestController.register(username, "");
        });
    }

    @Test
    void itShouldSaveNewUserAccountWhenUsernameIsUnique(){
        final String username = "some username";
        final String password = "some password";
        UserAccount expectedUserAccount = new UserAccount();
        expectedUserAccount.username = username;
        expectedUserAccount.password = password;
        //expectedUserAccount.id = (long)(Math.random() * 9999999);

        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        when(repository.save(captor.capture())).thenReturn(expectedUserAccount);
        Assertions.assertDoesNotThrow(() -> {
            jsonTestController.register(username, password);
        });
        // assert that new user account
        assertEquals(expectedUserAccount, captor.getValue());
    }

//    @Test
//    void itShouldRemoveTheTokenFromTokeMapWhenLogout(){
//        final var token = UUID.randomUUID();
//        HashMap<UUID, Long> previous = new HashMap<>();
//        Long id = (long)(Math.random() * 9999999);
//        previous.put(token, id);
//
//        //ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
//        when(tokenMap.remove(token)).thenReturn(id);
//
//        assertEquals(id, jsonTestController.logout(token));
//    }

    @Test
    void itShouldRemoveTheTokenFromTokenMapWhenLogout(){
        final UUID token = UUID.randomUUID();
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        when(tokenMap.remove(captor.capture())).thenReturn(0L);
        jsonTestController.logout(token);
        assertEquals(token, captor.getValue());
    }
    // example in class
//    void voidFn(String someStr){
//      if (Objects.equals(someStr, "NO"))
//          throw new RuntimeException("I don't like that");
//    }
//
//    void parent(String someStr){
//        voidFn(someStr + UUID.randomUUID());
//    }
//
//    //
//    @Test
//    void itShouldCallVoidFnWithHelloAndRandUUID(){
//        // Demo Arg Capture with void function
//        final var spy = Mockito.spy(this);
//        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
//
//
//        doNothing().when(spy).voidFn(captor.capture());
//        spy.parent("hello");
//
//        assertEquals("hello", captor.getValue().substring(0, 5));
//    }
}
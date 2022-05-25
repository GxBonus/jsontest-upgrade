package com.example.jsontestupgrade;

// Controllers are collections of function that respond to client requests
// i.e. http://jsontest.com/


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


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
import java.util.UUID;


@RestController
@RequestMapping("/")
public class JsonTestController {
    private final UserAccountRepository repository;
    private final HashMap<UUID, Long> tokenMap;

    private final Date date;
    private final RestTemplate rest;


    // Mockito
    public JsonTestController(Date date, @NonNull UserAccountRepository repository,
                              @NonNull HashMap<UUID, Long> tokenMap, RestTemplate rest){
        this.date = date;
        this.repository = repository;
        this.tokenMap = tokenMap;
        this.rest = rest;
    }
    // Spring
    @Autowired
    public JsonTestController(@NonNull UserAccountRepository repository){
        this.date = new Date();
        this.repository =repository;
        this.tokenMap = new HashMap<>();
        this.rest = new RestTemplate();
    }

    public void checkAuthorized(UUID token) {
        String url = "http://localhost:8081/isAuthorized?token=" + token;
        final ResponseEntity<Void> response = rest.getForEntity(url, Void.class);

        switch (response.getStatusCode()){
            case OK:
                return;
            case UNAUTHORIZED:
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            default:
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    @GetMapping("/ip")
    public Ip getIpAddress(UUID token, HttpServletRequest request){
        checkAuthorized(token);

        Ip ipAddress = new Ip(request.getRemoteAddr());

            return ipAddress;
    }

    @GetMapping("/headers")
    public Headers headers(UUID token,@RequestHeader Map<String, String> allHeaders){
        checkAuthorized(token);

        return new Headers(allHeaders);

    }

    @GetMapping("/date")
    public DateTime dateTime(UUID token){
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatDateObj = DateTimeFormatter.ofPattern("MM-dd-yyy");
        String localDateFormatted = formatDateObj.format(localDate);

        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatTimeObj = DateTimeFormatter.ofPattern("hh:mm:ss a");
        String localTimeFormatted = formatTimeObj.format(localTime);

//        Date date = new Date();
        long localTimeMilliseconds = date.getTime();
        checkAuthorized(token);

            return new DateTime(localDateFormatted, localTimeMilliseconds, localTimeFormatted);
    }


    @GetMapping("/md5")
    public mdFive md5(UUID token,@RequestParam String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(text.getBytes());
        byte[] digest = messageDigest.digest();
        String textHash = DatatypeConverter.printHexBinary(digest).toLowerCase();

        checkAuthorized(token);

            return new mdFive(text, textHash);
    }

    @GetMapping("/login")
    public UUID login(@RequestParam String username, @RequestParam String password){
        final var result = repository.findByUsernameAndPassword(username, password);
        if (result.isEmpty())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        final var token = UUID.randomUUID();
        tokenMap.put(token, result.get().id);

        return token;
    }

    @GetMapping("/register")
    public void register(@RequestParam String username, @RequestParam String password) {
        if (repository.findByUsername(username).isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        UserAccount result = new UserAccount();
        result.username = username;
        result.password = password;
        repository.save(result);
    }

    @GetMapping("/logout")
    public void logout(UUID token) {

        tokenMap.remove(token);
    }



}

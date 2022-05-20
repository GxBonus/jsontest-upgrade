package com.example.jsontestupgrade;

// Controllers are collections of function that respond to client requests
// i.e. http://jsontest.com/


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("/")
public class JsonTestController {
//    Date date;
//
//    @Autowired
//    public JsonTestController(Date date){
//        this.date = date;
//    }


    @GetMapping("/ip")
    public Ip getIpAddress(HttpServletRequest request){

        return new Ip(request.getRemoteAddr());
    }

    @GetMapping("/headers")
    public Headers headers(@RequestHeader Map<String, String> allHeaders){
        return new Headers(allHeaders);

    }

    @GetMapping("/date")
    public DateTime dateTime(){
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatDateObj = DateTimeFormatter.ofPattern("MM-dd-yyy");
        String localDateFormatted = formatDateObj.format(localDate);

        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatTimeObj = DateTimeFormatter.ofPattern("hh:mm:ss a");
        String localTimeFormatted = formatTimeObj.format(localTime);

        Date date = new Date();
        long localTimeMilliseconds = date.getTime();

        return new DateTime(localDateFormatted, localTimeMilliseconds, localTimeFormatted);
    }


    @GetMapping("/md5")
    public mdFive md5(@RequestParam String text) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(text.getBytes());
        byte[] digest = messageDigest.digest();
        String textHash = DatatypeConverter.printHexBinary(digest).toLowerCase();

        return new mdFive(text, textHash);
    }


}

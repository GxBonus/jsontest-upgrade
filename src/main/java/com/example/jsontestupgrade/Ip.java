package com.example.jsontestupgrade;


import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;


public class Ip{
    private String ip;

    public Ip(String ipAddress){
        this.ip = ipAddress;
    }


    public String getIp() {

        return ip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ip ip1 = (Ip) o;
        return Objects.equals(ip, ip1.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip);
    }

}

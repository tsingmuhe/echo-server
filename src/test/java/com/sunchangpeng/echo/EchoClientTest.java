package com.sunchangpeng.echo;

public class EchoClientTest {
    public static void main(String[] args) {
        EchoClient client = new EchoClient();
        client.start();
        client.shutDown();
    }
}
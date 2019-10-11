package com.sunchangpeng.echo;

public class EchoServerTest {
    public static void main(String[] args) {
        EchoServer server = new EchoServer();
        server.start();
        server.shutDown();
    }
}
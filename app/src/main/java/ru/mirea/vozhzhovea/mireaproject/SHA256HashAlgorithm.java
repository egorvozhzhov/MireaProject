package ru.mirea.vozhzhovea.mireaproject;

import android.util.Log;

public class SHA256HashAlgorithm {
    public static String hash(String input) {
        int[] K = {
                0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5,
                0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
                0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
                0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
                0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc,
                0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
                0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7,
                0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
                0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
                0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
                0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3,
                0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
                0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5,
                0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
                0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
                0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
        };

        int h[] = {
                0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
                0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
        };

        byte[] inputBytes = input.getBytes();
        byte[] padded = padMessage(inputBytes);
        int numChunks = padded.length / 64;

        for (int chunk = 0; chunk < numChunks; chunk++) {
            int[] words = new int[64];
            for (int i = 0; i < 16; i++) {
                int index = chunk * 64 + i * 4;
                words[i] = ((padded[index] & 0xFF) << 24) |
                        ((padded[index + 1] & 0xFF) << 16) |
                        ((padded[index + 2] & 0xFF) << 8) |
                        (padded[index + 3] & 0xFF);
            }

            for (int i = 16; i < 64; i++) {
                int s0 = rotateRight(words[i - 15], 7) ^ rotateRight(words[i - 15], 18) ^ (words[i - 15] >>> 3);
                int s1 = rotateRight(words[i - 2], 17) ^ rotateRight(words[i - 2], 19) ^ (words[i - 2] >>> 10);
                words[i] = words[i - 16] + s0 + words[i - 7] + s1;
            }

            int a = h[0];
            int b = h[1];
            int c = h[2];
            int d = h[3];
            int e = h[4];
            int f = h[5];
            int g = h[6];
            int h1 = h[7];

            for (int i = 0; i < 64; i++) {
                int S1 = rotateRight(e, 6) ^ rotateRight(e, 11) ^ rotateRight(e, 25);
                int ch = (e & f) ^ (~e & g);
                int temp1 = h1 + S1 + ch + K[i] + words[i];
                int S0 = rotateRight(a, 2) ^ rotateRight(a, 13) ^ rotateRight(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = S0 + maj;

                h1 = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }

            h[0] += a;
            h[1] += b;
            h[2] += c;
            h[3] += d;
            h[4] += e;
            h[5] += f;
            h[6] += g;
            h[7] += h1;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < h.length; i++) {
            String hex = Integer.toHexString(h[i]);
            while (hex.length() < 8) {
                hex = "0" + hex;
            }
            builder.append(hex);
        }

        return builder.toString();
    }

    private static byte[] padMessage(byte[] inputBytes) {

        int originalLength = inputBytes.length;
        int tailLength = originalLength % 64;
        int paddingLength = 0;
        if (tailLength < 56) {
            paddingLength = 56 - tailLength;
        } else {
            paddingLength = 120 - tailLength;
        }

        byte[] padded = new byte[originalLength + paddingLength + 8];
        System.arraycopy(inputBytes, 0, padded, 0, originalLength);
        padded[originalLength] = (byte) 0x80;
        long bitLength = (long) originalLength * 8;
        for (int i = 0; i < 8; i++) {
            padded[padded.length - 1 - i] = (byte) (bitLength >>> (i * 8));
        }
        return padded;
    }

    private static int rotateRight(int value, int distance) {
        return (value >>> distance) | (value << (32 - distance));
    }

    public static void main(String[] args) {
        String input = "Hello, World!";
        String hashed = hash(input);
        Log.d("Input: " , input);
        Log.d("Hashed: " , hashed);
    }
}

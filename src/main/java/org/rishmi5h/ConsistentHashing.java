package org.rishmi5h;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashing {


    private final TreeMap<Long, String> ring;
    private final int noOfReplicas;
    private final MessageDigest md;

    public ConsistentHashing(int noOfReplicas) throws NoSuchAlgorithmException {
        this.ring = new TreeMap<>();
        this.noOfReplicas = noOfReplicas;
        this.md = MessageDigest.getInstance("MD5");
    }

    public void addServer(String server) {
        for (int i = 0; i < noOfReplicas; i++) {
            long hash = generateHash(server + i);
            ring.put(hash, server);
        }
    }

    public void removeServer(String server) {
        for (int i = 0; i < noOfReplicas; i++) {
            long hash = generateHash(server + i);
            ring.remove(hash, server);
        }
    }

    public String getServer(String key) {

        if (ring.isEmpty()) {
            return null;
        }

        long hash = generateHash(key);

        if (!ring.containsKey(hash)) {
            SortedMap<Long, String> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);

    }


    private long generateHash(String key) {
        md.reset();
        md.update(key.getBytes());
        byte[] digest = md.digest();
        long hash = ((long) (digest[3] & 0xFF) << 24) | ((long) (digest[2] & 0xFF) << 16) | ((long) (digest[1] & 0xFF) << 8) | ((long) (digest[0] & 0xFF));
        return hash;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        ConsistentHashing consistentHashing = new ConsistentHashing(4);

        consistentHashing.addServer("s1");
        consistentHashing.addServer("s2");
        consistentHashing.addServer("s3");
        consistentHashing.addServer("s4");


        System.out.println("key1 : present on server: " + consistentHashing.getServer("key1"));
        System.out.println("key4500 : present on server: " + consistentHashing.getServer("key4500"));
        System.out.println("key345667 : present on server: " + consistentHashing.getServer("key345667"));

        consistentHashing.removeServer("s1");
        System.out.println(" After removing server ");


        System.out.println("key1 : present on server: " + consistentHashing.getServer("key1"));
        System.out.println("key4566 : present on server: " + consistentHashing.getServer("key4500"));
        System.out.println("key345667 : present on server: " + consistentHashing.getServer("key345667"));



    }
}

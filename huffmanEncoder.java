package huffcoding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;





public class huffmanEncoder {
	private class Node implements Comparable<Node> {
        char ch;
        int freq;
        Node left;
        Node right;
        Node(char c, int f, Node l, Node r) {
            this.ch = c;
            this.freq = f;
            this.left = l;
            this.right = r;
        }
        public int compareTo(Node next) {
            if (this.freq > next.freq)
                return 1;
            else if (this.freq == next.freq)
                return 0;
            else
                return -1;
        }
	}
    public String charFreq;
    public String finalMap; 
    
    @SuppressWarnings("resource")
    public void encode(String s, String encodeLocation) throws IOException, FileNotFoundException {
        RandomAccessFile fr_in = new RandomAccessFile(s, "r");
        if (fr_in.length() == 0) {
            throw new IllegalArgumentException("The string should atleast have 1 character.");
        }
        HashMap<Character, Integer> charFrequencyMap = getCharFrequencies(fr_in);
        Node root = buildTree(charFrequencyMap);
        HashMap<Character, String> charCodeWordMap = getCodeWords(root);
        String encodedMessage = encodeMessage(charCodeWordMap, fr_in);
        exportTree(root, encodeLocation);
        exportEncodedMessage(encodedMessage, encodeLocation);
        fr_in.close();
        charFreq = printCharMap(charFrequencyMap);
        finalMap = printMap(charCodeWordMap);
    }
    
    
    private HashMap<Character, Integer> getCharFrequencies(RandomAccessFile fr_in) throws IOException, FileNotFoundException {
        HashMap<Character, Integer> charFreqMap = new HashMap<Character, Integer>();
        int ch;
        while ((ch = fr_in.read()) != -1) {
            char c = (char) ch;
            charFreqMap.put(c, charFreqMap.containsKey(c) ? charFreqMap.get(c) + 1 : 1);
        } 
        fr_in.seek(0);
        return charFreqMap;
    }
    
    private Node buildTree(HashMap<Character, Integer> charFrequencies) {
        PriorityQueue<Node> myQueue = new PriorityQueue<Node>();
        for (Map.Entry<Character, Integer> kv : charFrequencies.entrySet()) {
            myQueue.add(new Node(kv.getKey(), kv.getValue(), null, null));
        } 
        while (myQueue.size() > 1) {
            Node n1 = myQueue.remove();
            Node n2 = myQueue.remove();
            Node newNode = new Node('\0', n1.freq + n2.freq, n1, n2);
            myQueue.add(newNode);
        } 
        return myQueue.remove();
    }
    
    private HashMap<Character, String> getCodeWords(Node n) {
        HashMap<Character, String> codeMap = new HashMap<Character, String>();
        createCodeWords(n, codeMap, "");
        return codeMap;
    }
    
    private void createCodeWords(Node node, HashMap<Character, String> map, String s) {
        if (node.left == null && node.right == null) {
            map.put(node.ch, s);
            return;
        }
        createCodeWords(node.left, map, s + '0');
        createCodeWords(node.right, map, s + '1');
    
    }
    private String encodeMessage(HashMap<Character, String> map, RandomAccessFile fr) throws IOException {
        String s = "";
        int ch;
        while ((ch = fr.read()) != -1) {
            s = s + map.get((char) ch);
        } 
        fr.seek(0);
        return s;
    }
    private void exportTree(Node node, String encodeLocation) throws IOException, FileNotFoundException {
        BitSet bitset = new BitSet();
        ObjectOutputStream oosTree = new ObjectOutputStream(new FileOutputStream(encodeLocation + "\\tree.txt"));
        ObjectOutputStream oosChar = new ObjectOutputStream(new FileOutputStream(encodeLocation + "\\char.txt"));
        Iterator o = new Iterator();
        preOrder(node, oosChar, bitset, o);
        oosChar.close();
        bitset.set(o.bitPosition, true);
        oosTree.writeObject(bitset);
        oosTree.close();
    }
    
    private void preOrder(Node node, ObjectOutputStream oosChar, BitSet bitset, Iterator o) throws IOException {
        if (node.left == null && node.right == null) {
            bitset.set(o.bitPosition++, false);
            oosChar.writeChar(node.ch);
            return;
        }
        bitset.set(o.bitPosition++, true);
        preOrder(node.left, oosChar, bitset, o);

        bitset.set(o.bitPosition++, true);
        preOrder(node.right, oosChar, bitset, o);
    }
    private static class Iterator {
        int bitPosition;
    }
    private void exportEncodedMessage(String s, String encodeLocation) throws FileNotFoundException, IOException {
        FileOutputStream fr_out = new FileOutputStream(encodeLocation + "\\encoded.txt");
        BitSet bitSet = new BitSet();
        int i;
        for (i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '0') {
                bitSet.set(i, false);
            } else {
                bitSet.set(i, true);
            }
        } // end for
        bitSet.set(i, true);
        byte[] bs = bitSet.toByteArray();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(bs);
        baos.writeTo(fr_out);
        fr_out.close();
    }
    private String printCharMap(HashMap<Character, Integer> map) {
        StringBuilder s = new StringBuilder();
        s.append(" Character\t\tFrequency");
        s.append("\n-----------------------------------------------------------\n");
        for (Map.Entry<Character, Integer> kv : map.entrySet()) {
            System.out.print(kv.getValue() + ",");
            s.append(" " + kv.getKey() + "\t:\t" + kv.getValue() + "\n");
        }
        return s.toString();
    }
    
    private String printMap(HashMap<Character, String> map) {
        StringBuilder s = new StringBuilder();
        s.append(" Character\t\tEncoded Word");
        s.append("\n-----------------------------------------------------------------\n");
        for (Map.Entry<Character, String> kv : map.entrySet()) {
            s.append(" " + kv.getKey() + "\t:\t" + kv.getValue() + "\n");
        } // end for
        return s.toString();
    }
    
}

package huffcoding;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;




public class huffmanDecoder {
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
	
	public void decode(String outputString, String encodedFileLocation) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileOutputStream fout_decode = new FileOutputStream(outputString);
        String trueencodeLocation = encodedFileLocation.concat("\\encoded.txt");
        Node root = unpackTree(encodedFileLocation);
        Path path = Paths.get(trueencodeLocation);
        byte[] encodedBytes = Files.readAllBytes(path);
        BitSet bitset = BitSet.valueOf(encodedBytes);
        for (int i = 0; i < bitset.length() - 1;) {
            Node tmp = root;
            while (tmp.left != null) {
                if (!bitset.get(i)) {
                    tmp = tmp.left;
                } else {
                    tmp = tmp.right;
                }
                i++;
            } 
            fout_decode.write(tmp.ch);
        }
        fout_decode.close();
    }
	
	private Node unpackTree(String parentDir) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream oisBranch = new ObjectInputStream(new FileInputStream(parentDir + "\\tree.txt"));
        ObjectInputStream oisChar = new ObjectInputStream(new FileInputStream(parentDir + "\\char.txt"));
        BitSet bitset = (BitSet) oisBranch.readObject();
        oisBranch.close();
        return rebuildTree(bitset, oisChar, new Iterator());
    }
	
	private Node rebuildTree(BitSet bitset, ObjectInputStream oisChar, Iterator o) throws IOException {
        Node node = new Node('\0', 0, null, null);
        if (!bitset.get(o.bitPosition)) {
            o.bitPosition++;
            node.ch = oisChar.readChar();
            return node;
        }
        o.bitPosition = o.bitPosition + 1;
        node.left = rebuildTree(bitset, oisChar, o);

        o.bitPosition = o.bitPosition + 1;
        node.right = rebuildTree(bitset, oisChar, o);
        return node;
    }
	
	private static class Iterator {
        int bitPosition;
    }

}

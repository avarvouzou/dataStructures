package gr.hua.datastructures2;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.ejml.simple.SimpleMatrix;

public class OpenMatrixHashTable<K, V> implements Dictionary<K, V> {

    private final static int U = 32;
    private int b = 1;
    private Entry<K, V>[] array;
    private SimpleMatrix hashMatrix; // m
    private int size;

    public OpenMatrixHashTable() {
        this.array = (Entry<K, V>[]) Array.newInstance(EntryImpl.class, (int) Math.pow(2, b));
        this.hashMatrix = createRandomBinaryMatrix(b, U).transpose();
        this.size = 0;
    }

    public void put(K key, V value) {
        if (this.size == this.array.length - 1) {
            this.rehash(b + 1);
        }
        insert(key, value);
    }

    public V remove(K key) {
        int idx = this.getIndexWithLinearProbing(key);
        if (idx == -1 ) {
            return null;
        }

        // Already empty/deleted key.
        if (array[idx] == null) {
            return null;
        }
        
        V oldVal = array[idx].getValue();

        array[idx] = null;
        this.size--;

        if (this.size < (this.array.length / 4)) {
            rehash(this.b - 1);
        }

        return oldVal;
    }

    public V get(K key) {
        int idx = this.getIndexWithLinearProbing(key);
        if (idx == -1 ) {
            return null;
        }

        if (array[idx] == null) {
            return null;
        }
        return array[idx].getValue();
    }

    public boolean contains(K key) {
        return this.get(key) != null;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public void clear() {
        Arrays.fill(this.array, null);
        this.size = 0;
    }

    public Iterator<Entry<K, V>> iterator() {
        return Arrays.asList(this.array)
            .stream()
            .filter(x -> x != null)
            .collect(Collectors.toList())
            .iterator();
    }

    private class EntryImpl<K1, V1> implements Dictionary.Entry<K1, V1> {

        private K1 key;
        private V1 value;

        public EntryImpl(K1 key, V1 value) {
            this.key = key;
            this.value = value;
        }

        public K1 getKey() {
            return this.key;
        }

        public V1 getValue() {
            return this.value;
        }
    }

    private void insert(K key, V value) {
        int hashcode = Math.abs(key.hashCode());
        int idx = getIndexFromHashcode(hashcode);

        boolean shouldIncreaseSize = true;
        // we dont care for an infinite loop since we are growing our array whenever a certain threshold is reached
        while(array[idx] != null) {
            if (array[idx].getKey().equals(key)) {
                shouldIncreaseSize = false;
                break;
            }
            idx = (idx + 1) % this.array.length;
        }
        
        array[idx] = new EntryImpl<K, V>(key, value);
        
        // if there was no key present then we increase the size of the array since we added a new entry.
        if (shouldIncreaseSize) this.size++;
    }

    private int getIndexWithLinearProbing(K key) {
        int hashcode = Math.abs(key.hashCode());
        int initialIdx = getIndexFromHashcode(hashcode);
        int lastIdx = (initialIdx + this.array.length - 1) % this.array.length;

        int idx = initialIdx;

        do {
            if (this.array[idx] != null && this.array[idx].getKey().equals(key)) {
                return idx;
            }

            idx = (idx + 1) % this.array.length;
        } while(idx != lastIdx);

        return -1;
    }

    private SimpleMatrix createRandomBinaryMatrix(int cols, int rows) {
        double[][] data = new double[cols][rows];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] = Math.round(Math.random());
            }
        }
        return new SimpleMatrix(data);
    }

    private int getIndexFromHashcode(int hashcode) {
        String paddedBinaryHashcode = this.padLeftZeros(Integer.toBinaryString(hashcode), 32);

        double[] hashcodeBinaryDoubleArr = Arrays.stream(paddedBinaryHashcode.split(""))
            .mapToDouble(Double::parseDouble).toArray();
        
        SimpleMatrix sm = new SimpleMatrix(new double[][]{hashcodeBinaryDoubleArr});

        double[][] arrIdxMatrix = simpleMatrixToArray(sm.mult(hashMatrix));

        if (arrIdxMatrix.length == 0) {
            // something went wrong.
            System.out.println("Output matrix length is 0");
            return -1;
        }

        return Integer.parseInt(
            Arrays.stream(arrIdxMatrix[0])
                .mapToInt(num -> ((int) num)%2)
                .mapToObj(num -> Integer.toString(num))
                .collect(Collectors.joining()),
            2
        ) % this.array.length;
    }
    
    // in order to read index of array in binary
    private double[][] simpleMatrixToArray(SimpleMatrix matrix) {
        double[][] array = new double[matrix.numRows()][matrix.numCols()];
        for (int r = 0; r < matrix.numRows(); r++) {
            for (int c = 0; c < matrix.numCols(); c++) {
                array[r][c] = matrix.get(r, c);
            }
        }
        return array;
    }

    private String padLeftZeros(String inputString, int length) {
        if (inputString.length() >= length) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);
    
        return sb.toString();
    }

    private void rehash(int b) {
        List<Entry<K, V>> oldList = Arrays.asList(this.array)
                                    .stream()
                                    .filter(x -> x != null)
                                    .collect(Collectors.toList());
                                    
        this.clear();
        this.array = (Entry<K, V>[]) Array.newInstance(EntryImpl.class, (int) Math.pow(2, b));
        this.b = b;
        this.hashMatrix = createRandomBinaryMatrix(b, U).transpose();

        for(Entry<K, V> e: oldList) {
            this.insert(e.getKey(), e.getValue());
        }
    }

}

package gr.hua.datastructures2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OpenMatrixHashTableTest {

    @Test
    public void testFilledHashTable() {
        OpenMatrixHashTable<Integer,Integer> dict = new OpenMatrixHashTable<>();
        assertTrue(dict.isEmpty());
        int count = 7;
        for (int i = 0; i < count; i++) {
            dict.put(i, i);
            assertTrue(dict.contains(i));
        }
    }

    @Test
    public void testEmptyHashTableAfterRemove() {
        OpenMatrixHashTable<Integer,Integer> dict = new OpenMatrixHashTable<>();
        assertTrue(dict.isEmpty());
        
        int keyVal = 1;
        dict.put(keyVal, keyVal);
        assertTrue(dict.contains(keyVal));

        dict.remove(keyVal);
        assertFalse(dict.contains(keyVal));
        assertTrue(dict.isEmpty());
    }

    @Test
    public void testClearHashTable() {
        OpenMatrixHashTable<Integer,Integer> dict = new OpenMatrixHashTable<>();
        assertTrue(dict.isEmpty());

        int count = 10;
        for (int i = 0; i < count; i++) {
            dict.put(i, i);
        }

        dict.clear();
        assertTrue(dict.isEmpty());
    }

}
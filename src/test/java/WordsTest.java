import com.tdfm.word.Translator;
import com.tdfm.word.Words;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WordsTest {
    @Test
    public void testWhenOneWordAddedThenWordCountIsOne() {
        String testWord = "hello";
        Words w = new Words(new Translator());
        w.add(testWord);

        assertEquals(1, w.countOf(testWord));

    }
}

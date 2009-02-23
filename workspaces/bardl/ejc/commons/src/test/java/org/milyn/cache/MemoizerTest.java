package org.milyn.cache;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test for {@link Memoizer}.
 *
 * @author <a href="mailto:daniel.bevenius@gmail.com">Daniel Bevenius</a>
 *
 */
public class MemoizerTest
{
    @Test (expected = IllegalArgumentException.class)
    public void constructWithNullComputable()
    {
        new Memoizer<String, String>(null);
    }

    @Test
    public void compute() throws InterruptedException
    {
        MockComputable mockComputable = new MockComputable();
        Memoizer<String, String> memorizer = new Memoizer<String, String>(mockComputable);

        String result = memorizer.compute("one");
        String result2 = memorizer.compute("one");

        assertEquals("1", result);
        assertEquals("1", result2);
        assertEquals(result.hashCode(), result2.hashCode());
    }

    @Test (expected = IllegalArgumentException.class)
    public void computeNullArgument() throws InterruptedException
    {
        MockComputable mockComputable = new MockComputable();
        Memoizer<String, String> memorizer = new Memoizer<String, String>(mockComputable);
        memorizer.compute(null);
    }

    private class MockComputable implements Computable<String, String>
    {
        public String compute(final String numberStr) throws InterruptedException
        {
            if (numberStr.equals("one"))
                return "1";
            else if (numberStr.equals("two"))
                return "2";
            else
                return "0";

        }

    }
}
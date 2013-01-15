/**
 * 
 */
package com.hp.hpl.inkml.test;

import org.junit.Test;

import com.hp.hpl.inkml.InkMLException;
import com.hp.hpl.inkml.InkMLProcessor;

/**
 * @author Charles
 */
public final class ProcessorTest {

    /**
     * Test method.
     * 
     * @throws InkMLException Parsing error
     */
    @Test
    public void test() throws InkMLException {
        final InkMLProcessor processor = new InkMLProcessor();
        processor.parseInkMLFile(this.getClass().getResourceAsStream("/inkML-example.xml"));
    }
}

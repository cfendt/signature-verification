/**
 * 
 */
package com.hp.hpl.inkml.test;

import java.io.IOException;

import org.junit.Test;

import com.hp.hpl.inkml.Ink;
import com.hp.hpl.inkml.InkMLException;
import com.hp.hpl.inkml.InkMLProcessor;
import com.hp.hpl.inkml.InkMLWriter;

/**
 * @author Charles
 */
public final class ProcessorTest {

    /**
     * Test method.
     * 
     * @throws InkMLException Parsing error
     * @throws IOException I/O Error
     */
    @Test
    public void test() throws InkMLException, IOException {
        final InkMLProcessor processor = new InkMLProcessor();
        processor.parseInkMLFile(this.getClass().getResourceAsStream("/inkML-example.xml"));
        final Ink ink = processor.getInk();

        final InkMLWriter writer = new InkMLWriter(System.out);
        ink.writeXML(writer);
    }
}

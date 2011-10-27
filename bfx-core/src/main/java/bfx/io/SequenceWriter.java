package bfx.io;

import java.util.Iterator;

import bfx.Sequence;
import bfx.utils.io.AbstractDualWriter;
import bfx.utils.io.AbstractWriter;


public interface SequenceWriter extends AbstractWriter<Iterator<Sequence>>,AbstractDualWriter<Iterator<Sequence>> {
}

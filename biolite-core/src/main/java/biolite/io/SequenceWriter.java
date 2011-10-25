package biolite.io;

import java.util.Iterator;

import biolite.Sequence;
import biolite.utils.io.AbstractDualWriter;
import biolite.utils.io.AbstractWriter;


public interface SequenceWriter extends AbstractWriter<Iterator<Sequence>>,AbstractDualWriter<Iterator<Sequence>> {
}

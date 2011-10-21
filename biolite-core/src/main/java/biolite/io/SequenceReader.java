package biolite.io;

import java.util.Iterator;

import biolite.Sequence;
import biolite.utils.io.AbstractDualReader;
import biolite.utils.io.AbstractReader;


public interface SequenceReader extends AbstractReader<Iterator<Sequence>>,
										AbstractDualReader<Iterator<Sequence>> {
}

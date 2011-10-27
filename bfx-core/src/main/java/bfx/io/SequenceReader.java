package bfx.io;

import java.util.Iterator;

import bfx.Sequence;
import bfx.utils.io.AbstractDualReader;
import bfx.utils.io.AbstractReader;


public interface SequenceReader extends AbstractReader<Iterator<Sequence>>,
										AbstractDualReader<Iterator<Sequence>> {
}

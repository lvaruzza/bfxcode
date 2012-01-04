package bfx.io;

import java.util.Iterator;

import bfx.Variant;
import bfx.utils.io.AbstractReader;


/**
 * Read VCF records
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public interface VariantReader extends AbstractReader<Iterator<Variant>> {
}

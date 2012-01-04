package bfx.io;

import java.util.Iterator;

import bfx.Variant;
import bfx.utils.io.AbstractWriter;


/**
 * Write VCF records
 * 
 * @author Leonardo Varuzza <varuzza@gmail.com>
 *
 */
public interface VariantWriter extends AbstractWriter<Iterator<Variant>> {
}

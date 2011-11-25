package bfx.io.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import bfx.GFF;
import bfx.impl.InvalidFormatException;
import bfx.io.GFFReader;
import bfx.utils.io.BaseReader;

public abstract class AbstractGFFReader extends BaseReader<Iterator<GFF>> implements GFFReader {
}

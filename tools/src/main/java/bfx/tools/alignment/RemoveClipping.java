package bfx.tools.alignment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.sf.samtools.Cigar;
import net.sf.samtools.CigarElement;
import net.sf.samtools.CigarOperator;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMRecord;
import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class RemoveClipping extends Tool {
	// private static Logger log = Logger.getLogger(Convert.class);

	@Parameter(names = { "--input", "-i" }, description = "Input File", required = true)
	public String input;

	@Parameter(names = { "--output", "-o" }, description = "Output File", required = true)
	public String output;

	@Override
	public void run() throws Exception {
		SAMFileWriterFactory samFactory = new SAMFileWriterFactory();
		ProgressMeter pm = this.getProgressMeterFactory().get();
		pm.start(String.format("Reading %s",input));
		try (SAMFileReader reader = new SAMFileReader(new File(input))) {
			SAMFileHeader header = reader.getFileHeader();
			SAMFileWriter outSam = samFactory.makeBAMWriter(header, false,
					new File(output));
			try {
				for (SAMRecord aln : reader) {
					pm.incr(1);
					List<CigarElement> cigar = aln.getCigar()
							.getCigarElements();
					CigarElement first = cigar.get(0);
					CigarElement last = cigar.get(cigar.size() - 1);
					byte[] qual = aln.getBaseQualities();
					byte[] bases = aln.getReadBases();
					if (first.getOperator() == CigarOperator.S
							|| last.getOperator() == CigarOperator.S) {
						int start = (first.getOperator() == CigarOperator.S) ? first
								.getLength() : 0;
						int end = (cigar.size() > 1 && last.getOperator() == CigarOperator.S) ? bases.length
								- last.getLength()
								: bases.length;
						bases = Arrays.copyOfRange(bases, start, end);
						qual = Arrays.copyOfRange(qual, start, end);

						List<CigarElement> newCigar = new ArrayList<CigarElement>(Collections2
								.filter(cigar, new Predicate<CigarElement>() {
									@Override
									public boolean apply(
											@Nullable CigarElement input) {
										return input.getOperator() != CigarOperator.S;
									}
								}));

						/*System.out.print(aln.getCigarString());
						System.out.print(" ");
						System.out.print(start);
						System.out.print(" ");
						System.out.print(end);
						System.out.print(" ");
						System.out.println();
						System.out.println(aln.getReadString());
						System.out.println(new String(bases));*/

						aln.setReadBases(bases);
						aln.setBaseQualities(qual);
						//aln.setAlignmentStart(aln.getAlignmentStart() + start);
						aln.setCigar(new Cigar(newCigar));
					}
					outSam.addAlignment(aln);
				}
			} finally {
				outSam.close();
			}
		}
		pm.finish();
	}

	@Override
	public String getName() {
		return "removeClipping";
	}

	@Override
	public String getGroup() {
		return "alignment";
	}

}

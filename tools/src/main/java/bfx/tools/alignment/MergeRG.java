package bfx.tools.alignment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMRecord;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfx.process.ProgressMeter;
import bfx.tools.Tool;

import com.beust.jcommander.Parameter;
import com.google.common.collect.FluentIterable;

public class MergeRG extends Tool {
	private static Logger log =  LoggerFactory.getLogger(MergeRG.class);

	@Parameter(names = { "--input", "-i" }, description = "Input File", required = true)
	public String input;

	@Parameter(names = { "--output", "-o" }, description = "Output File", required = true)
	public String output;

	@Override
	public void run() throws Exception {
		SAMRecord curAln = null;

		SAMFileWriterFactory samFactory = new SAMFileWriterFactory();
		// samFactory.setCreateMd5File(true);
		samFactory.setMaxRecordsInRam(10^7);
		samFactory.setUseAsyncIo(true);
		samFactory.setAsyncOutputBufferSize(1_000_000);
		//samFactory.setDefaultCreateIndexWhileWriting(true);
		
		File outputFile = new File(output);

		samFactory.setTempDirectory(new File(FilenameUtils
				.getFullPath(outputFile.getAbsolutePath()) + "/temp/"));

		ProgressMeter pm = this.getProgressMeterFactory().get();
		pm.start(String.format("Reading %s", input));
		try (SAMFileReader reader = new SAMFileReader(new File(input))) {
			SAMFileHeader header = reader.getFileHeader();
			List<SAMReadGroupRecord> rgs = header.getReadGroups();
			if (rgs.size() < 2) {
				log.error("You need 2 or more groups to merge");
				return;
			}
			//Map<String,String> nameMap = new TreeMap<String,String>();
			SAMReadGroupRecord merged = new SAMReadGroupRecord("merged", FluentIterable.from(rgs).first().get());
			
			SAMFileHeader outputHeader = header.clone();
			
			outputHeader.setReadGroups(Arrays.asList(merged));
			
			SAMFileWriter outSam = samFactory.makeBAMWriter(outputHeader, false,
					outputFile);
			try {
				for (final SAMRecord aln : reader) {
					aln.setAttribute("RG", "merged");
					curAln=aln;
					outSam.addAlignment(aln);
					pm.incr(1);
				}			
			} catch (Exception e) {	
				System.err.println("ERROR: " + e.getMessage());
				System.err.println("While processing " + curAln);
			} finally {
				if (outSam != null)
					outSam.close();
			}
		}
		pm.finish();
	}

	@Override
	public String getName() {
		return "mergeRG";
	}

	@Override
	public String getGroup() {
		return "alignment";
	}

}

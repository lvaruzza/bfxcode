package bfx.ncbi;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;

import bfx.Sequence;
import bfx.io.SequenceSource;

import com.google.common.collect.Iterables;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class Efetch {
	private static String baseUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
	private final WebResource base;
	private WebResource efetch;
	public SeqDatabase nucleotide;
	public SeqDatabase protein;
	
	public static class SeqDatabase {
		private final WebResource efetch;
		private final String dbname;

		public SeqDatabase(String name,WebResource efetch) {
			this.dbname = name;
			this.efetch = efetch;
		}

		private MultivaluedMap<String,String> getmap() {
			MultivaluedMap<String, String> params = new MultivaluedMapImpl();
			params.add("db", dbname);
			return params;
		}
		
 		public SequenceSource getAll(String... ids) {
 			MultivaluedMap<String, String> params = getmap();
			params.add("id", StringUtils.join(ids,","));
			params.add("rettype", "fasta");
			params.add("retmode", "text");
			WebResource r = efetch.queryParams(params);
			System.out.println(r.toString());
		    SequenceSource src = r.get(SequenceSource.class);

		    return src;
		}

 		public Sequence get(String id) {
 			SequenceSource src = getAll(id);
 			return Iterables.getFirst(src, null);
 		}
 		
 		//TODO: Parse the XML
		public String getxml(String id) {
 			MultivaluedMap<String, String> params = getmap();
			params.add("id", id);
			params.add("rettype","gbc");
			params.add("retmode","xml");

		    String text = efetch.
		    				queryParams(params).
		    				get(String.class);
		    
		    return text;
		}
	}

	public Efetch() {
		ClientConfig cc = new DefaultClientConfig();
		JerseySequenceReader reader = new JerseySequenceReader();
		cc.getSingletons().add(reader);
		Client c = Client.create(cc);
		base = c.resource(baseUrl);
		efetch = base.path("efetch.fcgi");
		 nucleotide = new SeqDatabase("nucleotide",efetch);
		 protein = new SeqDatabase("protein",efetch);
	}

}

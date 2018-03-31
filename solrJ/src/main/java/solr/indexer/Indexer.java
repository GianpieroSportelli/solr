package solr.indexer;

import java.io.IOException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;

import excel.RicettaDocument;

public class Indexer {

	static final String ZK_HOST = "jumphost.hopto.org:9983";
	static final String RICETTE_FILE_PATH = "src/main/java/excel/ricette.xls";
	static final SolrClient client = getSolrClient();
	static final String COLLECTION = "jump3_collection";

	public static void main(String[] args)
			throws EncryptedDocumentException, InvalidFormatException, IOException, SolrServerException {

		System.out.println("=== CLEAN COLLECTION START ===");
		client.deleteByQuery(COLLECTION, "*:*", 100);
		System.out.println("=== COLLECTION CLEANED ===");

		System.out.println("=== READ XLS START ===");
		List<RicettaDocument> ricette = RicettaDocument.getDoc(RICETTE_FILE_PATH);
		System.out.println("Doc readed: " + ricette.size());
		System.out.println("=== READ XLS DONE ===");

		System.out.println("=== INDEX START ===");
		for (RicettaDocument ricetta : ricette) {
			System.out.println(ricetta);
			if (!ricetta.asNull()) {
				try {
				UpdateResponse response = client.addBean(COLLECTION, ricetta, 100);
				System.out.println(response);
				client.commit(COLLECTION);
				}catch(Exception e) {
					System.err.println(e.getCause()+": "+ricetta);
				}
			} else {
				System.err.println("alcuni campi null!!! da capire come gestire bene questi schemi");
			}
		}
		System.out.println("=== INDEX STOP ===");
		
		client.close();
	}

	private static SolrClient getSolrClient() {
		CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(ZK_HOST).build();
		return client;
	}

}

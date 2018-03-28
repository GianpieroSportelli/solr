package indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

public class base {

	public static void main(String[] args) throws SolrServerException, IOException {
		final SolrClient client = getSolrClient();
		String collection = "jump3_collection";
		client.deleteByQuery(collection, "*:*", 100);
		System.out.println("document deleted");
		CollectionDocument doc = new CollectionDocument("kindle-id-4", "Amazon Kindle Paperwhite");
		UpdateResponse response = client.addBean(collection, doc, 100);
		System.out.println(response);
		client.commit(collection);
		
		doc = new CollectionDocument("kindle-id-5", "SSD Samsung 860 EVO M.2 SATA III");
		response = client.addBean(collection, doc, 100);
		System.out.println(response);
		client.commit(collection);
		
		SolrQuery query = new SolrQuery("value:Samsung");
		query.addField("id");
		query.addField("value");

		QueryResponse responseQuery = client.query(collection, query);
		System.out.println(responseQuery);
		List<CollectionDocument> products = responseQuery.getBeans(CollectionDocument.class);
		for(CollectionDocument p: products){
			System.out.println(p);
		}
		client.close();
	}

	public static class CollectionDocument {
		@Field
		public String id;
		@Field
		public ArrayList<String> value;

		public CollectionDocument(String id, String value) {
			this.id = id;
			this.value = new ArrayList<String>();
			this.value.add(value);
		}
		
		public CollectionDocument() {
		}
		
		public String toString(){
			String str="";
			for(String s:value){
				str+=s+" ";
			}
			return "{\"id\":\""+id+"\",\"value\":\""+str+"\"}";
		}
	}

	private static SolrClient getSolrClient() {
		 String zkHost = "jumphost.hopto.org:9983";
		//String url = "http://jumphost.hopto.org:8983/solr/";
		// String[] array = { "http://jumphost.hopto.org:8983/solr/*/",
		// "http://jumphost.hopto.org:8984/solr/*/",
		// "http://jumphost.hopto.org:8985/solr/*/",
		// "http://jumphost.hopto.org:8986/solr/*/" };
		// List<String> urls = Arrays.asList(array);
		// CloudSolrClient cloud = new
		// CloudSolrClient.Builder().withSolrUrl(urls).build();
		 CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(zkHost).build();
		//HttpSolrClient client = new HttpSolrClient.Builder().withBaseSolrUrl(url).build();
		return client;
	}

}

package indexing;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.httpclient.HttpClient;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.auth.HttpAuthenticator;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient.Builder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.LBHttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

public class base {

	public static void main(String[] args) throws SolrServerException, IOException {
		final CloudSolrClient client = getSolrClient();

		final SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", UUID.randomUUID().toString());
		doc.addField("name", "Amazon Kindle Paperwhite");
		final UpdateResponse updateResponse = client.add("jump3_collection", doc);
		// Indexed documents must be committed
		client.commit("jump3_collection");
	}

	private static CloudSolrClient getSolrClient() {
		String url="http://192.168.1.13:8983/solr";
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials
		 = new UsernamePasswordCredentials("rpiCapo", "SolrRocks");
		provider.setCredentials(AuthScope.ANY, credentials);
		  
		CloseableHttpClient base = HttpClientBuilder.create()
		  .setDefaultCredentialsProvider(provider)
		  .build();
		HttpSolrClient client = new HttpSolrClient.Builder(url).build();
		
		String zkHost = "192.168.1.13:9983";
		CloudSolrClient cloud = new CloudSolrClient.Builder()
                .withZkHost("192.168.1.13:9983")
                .withLBHttpSolrClientBuilder(new LBHttpSolrClient.Builder()
                    .withResponseParser(client.getParser())
                    .withHttpSolrClientBuilder(
                        new HttpSolrClient.Builder().withHttpClient(base)
                    ))
                        .build();;
		return cloud;
	}

}

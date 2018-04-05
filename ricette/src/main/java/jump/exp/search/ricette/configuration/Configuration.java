package jump.exp.search.ricette.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties()
public class Configuration {
	
	@Value("${solr.zkhost}")
	private String zkhost;
	
	@Value("${solr.collection}")
	private String collection;

	public String getZkhost() {
		return zkhost;
	}

	public void setZkhost(String zkhost) {
		this.zkhost = zkhost;
	}

	public String getCollection() {
		return collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}
	
	
}

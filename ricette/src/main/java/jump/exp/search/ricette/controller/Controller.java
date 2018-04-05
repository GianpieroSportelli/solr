package jump.exp.search.ricette.controller;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import jump.exp.search.ricette.configuration.Configuration;
import jump.exp.search.ricette.domain.RicetteDocument;

@RestController
@RequestMapping("/ricette")
public class Controller {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private String collection;
	private SolrClient solr;

	@Autowired
	public Controller(Configuration conf) {
		String zkhost = conf.getZkhost();
		log.debug("zookeper host: " + zkhost);
		collection = conf.getCollection();
		log.debug("collection: " + collection);
		solr = getSolrClient(zkhost);
	}

	@RequestMapping("/")
	public String index() {
		log.error("ERROR");
		log.warn("WARN");
		log.info("INFO");
		log.debug("DEBUG");
		return "/ricerca";
	}

	@RequestMapping(path = "/ricerca", method = RequestMethod.GET, params = { "q", "pagina", "numeroRisultati" }, produces="application/json")
	public @ResponseBody String search(@RequestParam(value = "q") String q, @RequestParam(value = "pagina") int pagina,
			@RequestParam(value = "numeroRisultati") int n) throws SolrServerException, IOException {
		String result = null;
		List<RicetteDocument> ricette = solrSearch(q, pagina, n);
		ObjectMapper objectMapper = new ObjectMapper();
		result = objectMapper.writeValueAsString(ricette);
		log.debug(result);
		return result;
	}

	private SolrClient getSolrClient(String zkHost) {
		log.debug("INIT SOLR CLIENT");
		CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(zkHost).build();
		return client;
	}

	private List<RicetteDocument> solrSearch(String q, int page, int pageResult)
			throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery(q);
		query.addField("Nome");
		query.addField("Ing_Principale");
		query.addField("Preparazione");
		query.addField("Note");
		query.addField("Ingredienti");
		query.addField("Tipo_Piatto");
		query.addField("id");
		query.addField("Persone");
		query.setStart((page-1)*pageResult);
		query.setRows(pageResult);

		QueryResponse responseQuery = solr.query(collection, query);
		log.debug("result: " + responseQuery);
		List<RicetteDocument> ricette = responseQuery.getBeans(RicetteDocument.class);
		return ricette;
	}
}

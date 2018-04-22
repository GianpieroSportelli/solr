package jump.exp.search.ricette.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import jump.exp.search.ricette.configuration.Configuration;

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

	@RequestMapping(path = "/ricerca", method = RequestMethod.GET, params = { "q", "pagina",
			"righe" }, produces = "application/json")
	public @ResponseBody String search(@RequestParam(value = "q") String q,
			@RequestParam(value = "pagina", required = false, defaultValue = "1") Integer pagina,
			@RequestParam(value = "righe", required = false, defaultValue = "1") Integer n)
			throws SolrServerException, IOException, JSONException {
		QueryResponse responseQuery = solrSearch(q, pagina, n);
		JSONObject result = new JSONObject();
		int status = responseQuery.getStatus();
		result.put("status", status);
		Map<String, FieldStatsInfo> stats = responseQuery.getFieldStatsInfo();
		if (stats != null)
			for (String key : stats.keySet()) {
				log.debug(key + ":" + stats.get(key));
			}
		SolrDocumentList solrResults =responseQuery.getResults();
		long numFound = solrResults.getNumFound();
		result.put("numFound", numFound);
		Gson gson = new Gson();
		JSONArray qResults = new JSONArray(gson.toJson(solrResults));
		result.put("results", qResults);
		log.debug(result.toString(4));
		return result.toString();
	}

	private SolrClient getSolrClient(String zkHost) {
		log.debug("INIT SOLR CLIENT");
		CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(zkHost).build();
		return client;
	}

	private QueryResponse solrSearch(String q, int page, int pageResult) throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery(q);
		query.addField("Nome");
		query.addField("Ing_Principale");
		query.addField("Preparazione");
		query.addField("Note");
		query.addField("Ingredienti");
		query.addField("Tipo_Piatto");
		query.addField("id");
		query.addField("Persone");
		query.setStart((page - 1) * pageResult);
		query.setRows(pageResult);

		QueryResponse responseQuery = solr.query(collection, query );
		return responseQuery;
	}
}

package solr.query;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Query {
	final static String ZK_HOST = "jumphost.hopto.org:9983";
	final static String COLLECTION = "ricette_collection";

	public static void main(String[] args) throws SolrServerException, IOException {
		final SolrClient client = getSolrClient(ZK_HOST);
		SolrQuery query = new SolrQuery("risotto alla milanese");
		query.addField("Nome");
		query.addField("Ing_Principale");
		query.addField("Preparazione");
		query.addField("Note");
		query.addField("Ingredienti");
		query.addField("Tipo_Piatto");
		query.addField("id");
		query.addField("Persone");
		
		QueryResponse responseQuery = client.query(COLLECTION, query);
		System.out.println("result: "+responseQuery);
		List<RicetteDocument> ricette = responseQuery.getBeans(RicetteDocument.class);
		for(RicetteDocument r: ricette){
			System.out.println(r);
		}
		client.close();
	}

	private static SolrClient getSolrClient(String zkHost) {
		System.out.println("INIT SOLR CLIENT");
		CloudSolrClient client = new CloudSolrClient.Builder().withZkHost(zkHost).build();
		return client;
	}
	
	public static class RicetteDocument {
		
		@Field
		public String id;
		
		@Field
		public String Nome;
		
		@Field
		public String Ing_Principale;
		
		@Field
		public String Preparazione;
		
		@Field
		public String Note;
		
		@Field
		public List<String> Ingredienti;
		
		@Field
		public String Tipo_Piatto;
		
		@Field
		public String Persone;

		public RicetteDocument(String id, String Nome,String Ing_Principale,String Preparazione,String Note, List<String> Ingredienti, String Tipo_Piatto, String Persone) {
			this.id = id;
			this.Ing_Principale = Ing_Principale;
			this.Ingredienti = Ingredienti;
			this.Nome = Nome;
			this.Note = Note;
			this.Preparazione = Preparazione;
			this.Tipo_Piatto = Tipo_Piatto;
			this.Persone = Persone;
		}
		
		public RicetteDocument() {
		}
		
		public String toString(){
			ObjectMapper objectMapper = new ObjectMapper();
			String result = null;
			try {
				result = objectMapper.writeValueAsString(this);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getNome() {
			return Nome;
		}

		public void setNome(String nome) {
			Nome = nome;
		}

		public String getIng_Principale() {
			return Ing_Principale;
		}

		public void setIng_Principale(String ing_Principale) {
			Ing_Principale = ing_Principale;
		}

		public String getPreparazione() {
			return Preparazione;
		}

		public void setPreparazione(String preparazione) {
			Preparazione = preparazione;
		}

		public String getNote() {
			return Note;
		}

		public void setNote(String note) {
			Note = note;
		}

		public List<String> getIngredienti() {
			return Ingredienti;
		}

		public void setIngredienti(List<String> ingredienti) {
			Ingredienti = ingredienti;
		}

		public String getTipo_Piatto() {
			return Tipo_Piatto;
		}

		public void setTipo_Piatto(String tipo_Piatto) {
			Tipo_Piatto = tipo_Piatto;
		}

		public String getPersone() {
			return Persone;
		}

		public void setPersone(String persone) {
			Persone = persone;
		}
	}
}

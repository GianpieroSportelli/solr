package jump.exp.search.ricette.domain;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RicetteDocument {
	
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

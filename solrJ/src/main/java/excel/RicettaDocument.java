package excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.solr.client.solrj.beans.Field;
import org.json.JSONObject;

public class RicettaDocument {

	@Field
	String nome;

	@Field
	String tipoPiatto;

	@Field
	String ingredientePrincipale;

	@Field
	String nPersone;

	@Field
	String note;

	@Field
	String ingredienti;

	@Field
	String preparazione;

	public RicettaDocument(String nome, String tipo, String ingredientePrincipale, String nPersone, String note,
			String ingredienti, String preparazione) {
		this.nome = nome;
		this.tipoPiatto = tipo;
		this.ingredientePrincipale = ingredientePrincipale;
		this.nPersone = nPersone;
		this.note = note;
		this.ingredienti = ingredienti;
		this.preparazione = preparazione;
	}

	public RicettaDocument() {
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipoPiatto() {
		return tipoPiatto;
	}

	public void setTipoPiatto(String tipoPiatto) {
		this.tipoPiatto = tipoPiatto;
	}

	public String getIngredientePrincipale() {
		return ingredientePrincipale;
	}

	public void setIngredientePrincipale(String ingredientePrincipale) {
		this.ingredientePrincipale = ingredientePrincipale;
	}

	public String getnPersone() {
		return nPersone;
	}

	public void setnPersone(String nPersone) {
		this.nPersone = nPersone;
	}

	public String getIngredienti() {
		return ingredienti;
	}

	public void setIngredienti(String ingredienti) {
		this.ingredienti = ingredienti;
	}

	public String getPreparazione() {
		return preparazione;
	}

	public void setPreparazione(String preparazione) {
		this.preparazione = preparazione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String toString() {
		JSONObject obj = new JSONObject();
		obj.accumulate("nome", this.nome);
		obj.accumulate("tipoPiatto", this.tipoPiatto);
		obj.accumulate("ingredientePrincipale", this.ingredientePrincipale);
		obj.accumulate("nPersone", this.nPersone);
		obj.accumulate("note", this.note);
		obj.accumulate("ingredienti", this.ingredienti);
		obj.accumulate("preparazione", this.preparazione);
		return obj.toString();
	}

	public boolean asNull() {
		boolean result = this.nome == null || this.tipoPiatto == null || this.ingredientePrincipale == null
				|| this.nPersone == null || this.note == null || this.ingredienti == null || this.preparazione == null;
		return result;
	}

	public static List<RicettaDocument> getDoc(String path)
			throws EncryptedDocumentException, InvalidFormatException, IOException {
		List<RicettaDocument> result = new ArrayList<>();
		Workbook workbook = WorkbookFactory.create(new File(path));
		// Getting the Sheet at index zero
		Sheet sheet = workbook.getSheetAt(0);

		// Create a DataFormatter to format and get each cell's value as String
		DataFormatter dataFormatter = new DataFormatter();
		boolean first = true;

		for (Row row : sheet) {
			if (!first) {
				RicettaDocument doc = new RicettaDocument();
				for (int i = 0; i < row.getRowNum(); i++) {
					Cell cell = row.getCell(i);
					String cellValue = dataFormatter.formatCellValue(cell);
					switch (i) {
					case 0:
						doc.setNome(cellValue);
						break;
					case 1:
						doc.setTipoPiatto(cellValue);
						break;
					case 2:
						doc.setIngredientePrincipale(cellValue);
						break;
					case 3:
						doc.setnPersone(cellValue);
						break;
					case 4:
						doc.setNote(cellValue);
						break;
					case 5:
						doc.setIngredienti(cellValue.replaceAll("\\\\r\\\\n", " | "));
						break;
					case 6:
						doc.setPreparazione(cellValue);
						break;
					}
				}
				result.add(doc);
			} else {
				first = false;
			}
		}

		// Closing the workbook
		workbook.close();

		return result;
	}

}

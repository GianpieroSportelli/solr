package excel;

import java.io.IOException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class TesterDoc {
	
	public static final String SAMPLE_XLSX_FILE_PATH = "src/main/java/excel/ricette.xls";

	public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException {
		
		List<RicettaDocument> list = RicettaDocument.getDoc(SAMPLE_XLSX_FILE_PATH);
		for(RicettaDocument r: list) {
			System.out.println(r);
		}
		
		System.out.println(list.size());
 	}

}

package bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class RicetteBot extends TelegramLongPollingBot {

	private String error = "Ops... Riprova c'è stato un problema";
	private Map<Long, String> lastQuery = new HashMap<>();
	private Map<Long, Integer> lastQueryPage = new HashMap<>();
	private Map<Long, JSONObject> lastQueryObj = new HashMap<>();
	private String welcome="Ciao, scrivimi gli ingredienti o il nome della ricetta che vorresti realizzare al resto ci penso io :)";

	public String getBotUsername() {
		// TODO Auto-generated method stub
		return "RicettePi";
	}

	public void onUpdateReceived(Update update) {
		// We check if the update has a message and the message has text
		if (update.hasMessage() && update.getMessage().hasText()) {
			String message_text = update.getMessage().getText();
			System.out.print("Messaggio ricevuto: " + message_text);
			Long chat_id = update.getMessage().getChatId();
			System.out.println(" da: " + chat_id);
			int pagina = 1;
			String query = "";
			if (message_text.equals("/start")) {
				startMessageResponse(chat_id);
			} else if (message_text.equals("lista ing.")) {
				listIngMessageResponse(chat_id);
			} else if (message_text.equals("back")) {
				pagina = (lastQueryPage.containsKey(chat_id) ? lastQueryPage.get(chat_id) : 1);
				lastQueryPage.put(chat_id, pagina);
				query = lastQuery.containsKey(chat_id) ? lastQuery.get(chat_id) : "(*:*)";
				searchMessageResponse(query, pagina, chat_id);
			} else if (message_text.equals("info")) {
				infoMessageResponse(chat_id);
			} else if (message_text.equals(">")) {
				pagina = (lastQueryPage.containsKey(chat_id) ? lastQueryPage.get(chat_id) : 0) + 1;
				lastQueryPage.put(chat_id, pagina);
				query = lastQuery.containsKey(chat_id) ? lastQuery.get(chat_id) : "(*:*)";
				searchMessageResponse(query, pagina, chat_id);

			} else if (message_text.equals("<")) {
				pagina = (lastQueryPage.containsKey(chat_id) ? lastQueryPage.get(chat_id) : 2) - 1;
				lastQueryPage.put(chat_id, pagina);
				query = lastQuery.containsKey(chat_id) ? lastQuery.get(chat_id) : "(*:*)";
				searchMessageResponse(query, pagina, chat_id);

			} else {
				query = message_text;
				pagina = 1;
				lastQuery.put(chat_id, message_text);
				lastQueryPage.put(chat_id, pagina);
				searchMessageResponse(query, pagina, chat_id);
			}

		}

	}

	private void startMessageResponse(Long chat_id) {
		SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
				.setChatId(chat_id).setParseMode("HTML")
				.setText(welcome);

		try {
			execute(message); // Call method to send the message
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private String detail(JSONObject response) {
		StringBuffer buf = new StringBuffer();
		if (response.getInt("status") != 0)
			return error;

		String Nome = response.getJSONArray("results").getJSONObject(0).getString("Nome");
		String TipoPiatto = response.getJSONArray("results").getJSONObject(0).getString("Tipo_Piatto");
		String Persone = response.getJSONArray("results").getJSONObject(0).getString("Persone");
		String Preparazione = response.getJSONArray("results").getJSONObject(0).getString("Preparazione");
		String IngPrincipale = response.getJSONArray("results").getJSONObject(0).getString("Ing_Principale");

		buf.append("<b>" + Nome + "</b>\n");
		buf.append("<b>Tipo piatto: </b>" + TipoPiatto + "\n");
		buf.append("<b>Ingrediente principale: </b>" + IngPrincipale + "\n");
		buf.append("<b>Persone: </b>" + Persone + "\n");
		buf.append("<b>Preparazione: </b>" + Preparazione + "\n");

		return buf.toString();
	}

	private String listaIng(JSONObject response) {
		StringBuffer buf = new StringBuffer();
		if (response.getInt("status") != 0)
			return error;

		buf.append("<b>Ingredienti</b>\n");
		JSONArray listaIng = response.getJSONArray("results").getJSONObject(0).getJSONArray("Ingredienti");
		for (int i = 0; i < listaIng.length(); i++) {
			buf.append(listaIng.getString(i) + "\n");
		}
		return buf.toString();
	}

	private String getMessage(JSONObject response) {
		StringBuffer buf = new StringBuffer();
		if (response.getInt("status") != 0)
			return error;

		String Nome = response.getJSONArray("results").getJSONObject(0).getString("Nome");
		buf.append("<b>" + Nome + "</b>");
		return buf.toString();
	}

	private JSONObject search(String query, int pagina) throws MalformedURLException, IOException {
		String url = "http://jumphost.hopto.org:89/ricette/ricerca?righe=1&pagina=" + pagina + "&q="
				+ URLEncoder.encode(query, "UTF-8");
		System.out.println("Search URL: " + url);
		URLConnection conn = (new URL(url)).openConnection();
		conn.setConnectTimeout(3*1000);
		conn.setReadTimeout(3*1000);
		conn.connect();
		String response = "";
		String line = null;
		BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = read.readLine()) != null) {
			response += line;
		}
		JSONObject result = new JSONObject(response);
		read.close();
		
		System.out.println(result.toString(4));
		return result;
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return "455538924:AAE-QDC9VmuxJV--O2yb_R0Ew9Ru-wx5oDs";
	}

	private void searchMessageResponse(String query, int pagina, long chat_id) {
		JSONObject response = null;
		try {
			response = search(query, pagina);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
				.setChatId(chat_id).setParseMode("HTML").setText(response == null ? error : getMessage(response));
		if (response != null && response.getInt("status") == 0) {
			lastQueryObj.put(chat_id, response);
			int numFound = response.getInt("numFound");
			// Create ReplyKeyboardMarkup object
			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();
			KeyboardRow row = new KeyboardRow();

			if (pagina > 1) {
				row.add("<");
			} else {
				row.add(" ");
			}

			row.add("info");

			if (pagina < numFound) {
				row.add(">");
			} else {
				row.add(" ");
			}

			keyboard.add(row);

			// Set the keyboard to the markup
			keyboardMarkup.setKeyboard(keyboard);
			// Add it to the message
			message.setReplyMarkup(keyboardMarkup);
		}

		try {
			execute(message); // Call method to send the message
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void infoMessageResponse(long chat_id) {
		SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
				.setChatId(chat_id).setParseMode("HTML")
				.setText(lastQueryObj.containsKey(chat_id) ? detail(lastQueryObj.get(chat_id)) : error);

		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow row = new KeyboardRow();

		row.add("back");
		row.add("lista ing.");

		keyboard.add(row);

		// Set the keyboard to the markup
		keyboardMarkup.setKeyboard(keyboard);
		// Add it to the message
		message.setReplyMarkup(keyboardMarkup);

		try {
			execute(message); // Call method to send the message
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void listIngMessageResponse(long chat_id) {
		SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
				.setChatId(chat_id).setParseMode("HTML")
				.setText(lastQueryObj.containsKey(chat_id) ? listaIng(lastQueryObj.get(chat_id)) : error);

		try {
			execute(message); // Call method to send the message
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

}

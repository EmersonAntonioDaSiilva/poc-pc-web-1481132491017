package poc.pc.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

/**
 * A simple REST service which is able to say hello to someone using Conversation Please take a look at the web.xml
 * where JAX-RS is enabled And notice the @PathParam which expects the URL to contain /json/David or /xml/Mary
 *
 * @author bsutter@redhat.com
 */

@Path("/hapvida")
public class ChatHapVidaManager {

	@Inject
	private Conversation conversation;

	private static String workspaceId = "d6fe397a-343b-47b5-a132-a1def577b235";
	private static String username = "fb3ea18a-08b4-48ee-ac83-630fe19a68ef";
	private static String password = "6qOUoIn3UXnD";

	private SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
	private Date manha;
	private Date tarde;
	private Date noite;

	public ChatHapVidaManager() {
		try {
			manha = parser.parse("06:00");
			tarde = parser.parse("12:00");
			noite = parser.parse("18:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("/json/{dialog}/{strContext}")
	@Produces("application/json")
	public String getDialog(@PathParam("dialog") String dialog, @PathParam("strContext") String strContext) {
		conversation.setWorkspaceId(workspaceId);
		conversation.setUsername(username);
		conversation.setPassword(password);

		if ("000".equals(strContext)) {
			strContext = null;
			dialog = periocoDia(dialog);
		}

		return formJson(conversation.createHelloMessage(dialog, strContext));
	}

	private String periocoDia(String dialog) {
		Calendar cal = Calendar.getInstance();
		if (cal.getTime().compareTo(manha) == 0) {
			dialog = "Bom dia";

		} else if (cal.getTime().compareTo(manha) == 1 && cal.getTime().compareTo(tarde) == -1) {
			dialog = "Bom dia";

		} else if (cal.getTime().compareTo(tarde) == 0) {
			dialog = "Boa tarde";

		} else if (cal.getTime().compareTo(tarde) == 1 && cal.getTime().compareTo(noite) == -1) {
			dialog = "Boa tarde";

		} else if (cal.getTime().compareTo(noite) == 0) {
			dialog = "Boa noite";

		} else if (cal.getTime().compareTo(noite) == 1 || cal.getTime().compareTo(manha) == -1) {
			dialog = "Boa noite";

		}
		return dialog;
	}

	private String formJson(MessageResponse response) {
		StringBuffer retorno = new StringBuffer();

		//		String acao = (String) (response.getOutput().containsKey("acao") == true ? response.getOutput().get("acao") : "");

		retorno.append("{\"");
		retorno.append("result\":\"" + response.getText().get(0));
		retorno.append("\",");
		//		retorno.append("\"confianca\":\"" + response.getIntents().get(0).getConfidence());
		//		retorno.append("\",");
		retorno.append("\"conversation_id\":\"" + response.getContext().get("conversation_id"));
		//		retorno.append("\",");
		//		retorno.append("\"acao\":\"" + acao);
		//		retorno.append("\",");
		//		retorno.append("\"intencao\":\"" + response.getIntents().get(0).getIntent());
		retorno.append("\"}");

		return retorno.toString();
	}

}
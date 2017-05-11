package poc.pc.manager;

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

@Path("/policia-civil")
public class ChatManager {

	@Inject
	private Conversation conversation;

	private static String workspaceId = "4e7f07c7-6320-485d-8ef0-8a6fe06d1a6e";
	private static String username = "8561b01a-106f-4dcf-b279-5fb3d71c5a80";
	private static String password = "g1SXhm7iDPep";

	@GET
	@Path("/json/{dialog}/{conversation_id}/{system}")
	@Produces("application/json")
	public String getDialog(@PathParam("dialog") String dialog, @PathParam("conversation_id") String conversation_id, @PathParam("system") String system) {
		conversation.setWorkspaceId(workspaceId);
		conversation.setUsername(username);
		conversation.setPassword(password);

		if ("000".equals(conversation_id)) {
			conversation_id = null;
			system = null;
		}

		return formJson(conversation.createHelloMessage(dialog, conversation_id, system));
	}

	private String formJson(MessageResponse response) {
		StringBuffer retorno = new StringBuffer();

		String acao = (String) (response.getOutput().containsKey("acao") == true ? response.getOutput().get("acao") : "");

		retorno.append("{\"");

		String retornoTexto = "";
		for (String texto : response.getText()) {
			retornoTexto += texto;
		}

		retorno.append("result\":\"" + retornoTexto + "\",");

		retorno.append("\"confianca\":\"" + response.getIntents().get(0).getConfidence() + "\",");
		retorno.append("\"conversation_id\":\"" + response.getContext().get("conversation_id") + "\",");
		retorno.append("\"system\":\"" + response.getContext().get("system") + "\",");
		retorno.append("\"intent\":\"" + response.getIntents().get(0).getIntent() + "\",");
		retorno.append("\"acao\":\"" + acao + "\",");
		retorno.append("\"intencao\":\"" + response.getIntents().get(0).getIntent());
		retorno.append("\"}");

		return retorno.toString();
	}

}

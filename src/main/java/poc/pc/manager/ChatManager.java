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

	private static String workspaceId = "acfc6b01-ee0a-4614-ad94-858a33807290";
	private static String username = "377ab19c-3ab6-4f2a-af10-2e461e77d7c2";
	private static String password = "XKjXU2vqxM0d";

	@GET
	@Path("/json/{dialog}/{strContext}")
	@Produces("application/json")
	public String getDialog(@PathParam("dialog") String dialog, @PathParam("strContext") String strContext) {
		conversation.setWorkspaceId(workspaceId);
		conversation.setUsername(username);
		conversation.setPassword(password);

		if ("000".equals(strContext)) {
			strContext = null;
		}

		return formJson(conversation.createHelloMessage(dialog, strContext));
	}

	private String formJson(MessageResponse response) {
		StringBuffer retorno = new StringBuffer();

		String acao = (String) (response.getOutput().containsKey("acao") == true ? response.getOutput().get("acao") : "");

		retorno.append("{\"");
		retorno.append("result\":\"" + response.getText().get(0) + "\",");
		retorno.append("\"confianca\":\"" + response.getIntents().get(0).getConfidence() + "\",");
		retorno.append("\"strContext\":\"" + response.getContext().get("conversation_id") + "\",");
		retorno.append("\"acao\":\"" + acao + "\",");
		retorno.append("\"intencao\":\"" + response.getIntents().get(0).getIntent());
		retorno.append("\"}");

		return retorno.toString();
	}

}

package poc.pc.manager;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.google.gson.Gson;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

/**
 * A simple REST service which is able to say hello to someone using Conversation Please take a look at the web.xml
 * where JAX-RS is enabled And notice the @PathParam which expects the URL to contain /json/David or /xml/Mary
 *
 * @author bsutter@redhat.com
 */

@Path("/")
public class ChatManager {

	@Inject
	private Conversation conversation;

	@GET
	@Path("/json/{dialog}/{strContext}")
	@Produces("application/json")
	public String getDialog(@PathParam("dialog") String dialog, @PathParam("strContext") String strContext) {
		Map<String, Object> context = null;

		if ("000".equals(strContext)) {
			strContext = null;
		} else {
			Gson gson = new Gson();
			context = gson.fromJson(strContext, Map.class);
		}

		return formJson(conversation.createHelloMessage(dialog, context));
	}

	private String formJson(MessageResponse response) {
		StringBuffer retorno = new StringBuffer();

		retorno.append("{\"");
		retorno.append("result\":\"" + response.getText().get(0) + "\",");
		retorno.append("\"confianca\":\"" + response.getIntents().get(0).getConfidence() + "\",");
		retorno.append("\"strContext\":\"" + response.getContext() + "\",");
		retorno.append("\"intencao\":\"" + response.getIntents().get(0).getIntent());

		retorno.append("\"}");

		return retorno.toString();
	}

}

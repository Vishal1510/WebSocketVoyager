/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.websocket;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.model.Products;





/**
 *
 * @author VISHAL
 */
@ApplicationScoped
@ServerEndpoint("/actions")
public class ProductServer {

    /*@Inject
    private ProductSessionHandler sessionHandler;*/
    
    static ProductSessionHandler sessionHandler = new ProductSessionHandler();

    @OnOpen
    public void open(Session session) {
        System.out.println("SessionHandler : " + sessionHandler);
        sessionHandler.addSession(session);
    }

    @OnClose
    public void close(Session session) {
        sessionHandler.removeSession(session);
    }

    @OnError
    public void onError(Throwable error) {

        Logger.getLogger(ProductServer.class.getName()).log(Level.SEVERE, null, error);
    }

    @OnMessage
    public void handleMessage(String message, Session session) {
        
        System.out.println("Session : " + session);

        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();

            if ("add".equals(jsonMessage.getString("action"))) {
                Products product = new Products();
                product.setName(jsonMessage.getString("name"));
                product.setDescription(jsonMessage.getString("description"));
                product.setType(jsonMessage.getString("type"));
                product.setStatus("Off");
                sessionHandler.addProducts(product);
            }

            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.removeProducts(id);
            }

            if ("toggle".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                sessionHandler.toggleProducts(id);
            }
        }
    }

}

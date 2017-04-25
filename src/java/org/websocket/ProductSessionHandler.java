/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import org.model.Products;


@ApplicationScoped

/**
 *
 * @author VISHAL
 */
public class ProductSessionHandler {
    
    private int productId = 0;
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Products> products = new HashSet<>();
    
    public void addSession(Session session){
        sessions.add(session);   
        for (Products product : products) {
            JsonObject addMessage = createAddMessage(product);
            sendToSession(session, addMessage);
        }
    }
    
    public void removeSession(Session session ){
        sessions.remove(session);   
    }
    
     public List<Products> getProducts() {
        return new ArrayList<>(products);
    }

    public void addProducts(Products product) {
        
        product.setId(productId);
        products.add(product);
        productId++;
        JsonObject addMessage = createAddMessage(product);
        sendToAllConnectedSessions(addMessage);
    }

    public void removeProducts(int id) {
        
         Products product = getProductsById(id);
        if (product != null) {
            products.remove(product);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToAllConnectedSessions(removeMessage);
        }

    }

    public void toggleProducts(int id) {
        
        JsonProvider provider = JsonProvider.provider();
        Products product = getProductsById(id);
        if (product != null) {
            if ("On".equals(product.getStatus())) {
                product.setStatus("Off");
            } else {
                product.setStatus("On");
            }
            JsonObject updateDevMessage = provider.createObjectBuilder()
                    .add("action", "toggle")
                    .add("id", product.getId())
                    .add("status", product.getStatus())
                    .build();
            sendToAllConnectedSessions(updateDevMessage);
        }
    }

    private Products getProductsById(int id) {
        
        for (Products device : products) {
            if (device.getId() == id) {
                return device;
            }
        }
        return null;
    }

    private JsonObject createAddMessage(Products product) {
        
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
                .add("action", "add")
                .add("id", product.getId())
                .add("name", product.getName())
                .add("type", product.getType())
                .add("status", product.getStatus())
                .add("description", product.getDescription())
                .build();
        return addMessage;
    }

    private void sendToAllConnectedSessions(JsonObject message) {
        
        for (Session session : sessions) {
            sendToSession(session, message);
        }
    }

    private void sendToSession(Session session, JsonObject message) {
        
         try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(ProductSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

window.onload = init();
var socket = new WebSocket("ws://localhost:8080/WebSocketVoyager/actions");
socket.onmessage = onMessage;

function onMessage(event) {
    var product = JSON.parse(event.data);
    if (product.action === "add") {
        printProductsElement(product);
    }
    if (product.action === "remove") {
        document.getElementById(product.id).remove();
        //product.parentNode.removeChild(product);
    }
    if (product.action === "toggle") {
        var node = document.getElementById(product.id);
        var statusText = node.children[2];
        if (product.status === "On") {
            statusText.innerHTML = "Status: " + product.status + " (<a href=\"#\" OnClick=toggleProducts(" + product.id + ")>Turn off</a>)";
        } else if (product.status === "Off") {
            statusText.innerHTML = "Status: " + product.status + " (<a href=\"#\" OnClick=toggleProducts(" + product.id + ")>Turn on</a>)";
        }
    }
}

function addProducts(name, type, description) {
    var ProductAction = {
        action: "add",
        name: name,
        type: type,
        description: description
    };
    socket.send(JSON.stringify(ProductAction));
}

function removeProducts(element) {
    var id = element;
    var ProductsAction = {
        action: "remove",
        id: id
    };
    socket.send(JSON.stringify(ProductsAction));
}

function toggleProducts(element) {
    var id = element;
    var ProductsAction = {
        action: "toggle",
        id: id
    };
    socket.send(JSON.stringify(ProductsAction));
}

function printProductsElement(product) {
    var content = document.getElementById("content");
    //alert(product.type);
    var productDiv = document.createElement("div");
    productDiv.setAttribute("id", product.id);
    productDiv.setAttribute("class", "product " + product.type);
    content.appendChild(productDiv);

    var productName = document.createElement("span");
    productName.setAttribute("class", "productName");
    productName.innerHTML = product.name;
    productDiv.appendChild(productName);

    var productType = document.createElement("span");
    productType.innerHTML = "<b>Type:</b> " + product.type;
    productDiv.appendChild(productType);

    var productStatus = document.createElement("span");
    if (product.status === "On") {
        productStatus.innerHTML = "<b>Status:</b> " + product.status + " (<a href=\"#\" OnClick=toggleProducts(" + product.id + ")>Turn off</a>)";
    } else if (product.status === "Off") {
        productStatus.innerHTML = "<b>Status:</b> " + product.status + " (<a href=\"#\" OnClick=toggleProducts(" + product.id + ")>Turn on</a>)";
        //productDiv.setAttribute("class", "product off");
    }
    productDiv.appendChild(productStatus);

    var productDescription = document.createElement("span");
    productDescription.innerHTML = "<b>Comments:</b> " + product.description;
    productDiv.appendChild(productDescription);

    var removeProducts = document.createElement("span");
    removeProducts.setAttribute("class", "removeProducts");
    removeProducts.innerHTML = "<a href=\"#\" OnClick=removeProducts(" + product.id + ")>Remove product</a>";
    productDiv.appendChild(removeProducts);
}

function showForm() {
    document.getElementById("addProductsForm").style.display = '';
}

function hideForm() {
    document.getElementById("addProductsForm").style.display = "none";
}

function formSubmit() {
    var form = document.getElementById("addProductsForm");
    var name = form.elements["product_name"].value;
    var type = form.elements["product_type"].value;
    var description = form.elements["product_description"].value;
    //hideForm();
    document.getElementById("addProductsForm").reset();
    console.log(type);
    addProducts(name, type, description);
}

function init() {
    hideForm();
}


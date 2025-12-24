<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!-- Recupera el bean desde la sesión -->
<jsp:useBean id="carrito" class="es.tew.CarritoBean" scope="session" />

<html>
<head>
    <title>Carrito de la compra</title>
</head>
<body>
    <h1>Carrito de la compra</h1>
    <form method="post" action="CarritoCompra">
        <select name="productos" size="1">
            <option value="010">La trampa</option>
            <option value="345">Los pájaros</option>
            <option value="554">Matrix reloaded</option>
            <option value="111">A</option>
            <option value="222">B</option>
            <option value="333">C</option>
            <option value="444">D</option>
            <option value="555">E</option>
            <option value="666">F</option>
            <option value="777">G</option>
        </select>
        <input type="submit" value="Añadir al carrito">
    </form>

    <h2>Estado actual del carrito:</h2>
    <c:choose>
        <c:when test="${empty carrito.productos}">
            <p>Tu carrito está vacío.</p>
        </c:when>
        <c:otherwise>
            <ul>
                <c:forEach var="entry" items="${carrito.productos}">
                    <li>Producto ${entry.key} → ${entry.value} unidad(es)</li>
                </c:forEach>
            </ul>
        </c:otherwise>
    </c:choose>
</body>
</html>
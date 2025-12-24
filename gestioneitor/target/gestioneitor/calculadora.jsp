<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Calculadora JSP</title>
</head>
<body>
    <h1>Calculadora JSP</h1>

    <form method="post" action="calculadora.jsp">
        <label>Primer número: </label>
        <input type="text" name="num1"><br><br>

        <label>Segundo número: </label>
        <input type="text" name="num2"><br><br>

        <label>Operación: </label>
        <select name="op">
            <option value="SUMA">Suma</option>
            <option value="RESTA">Resta</option>
            <option value="PRODUCTO">Producto</option>
            <option value="DIVISION">División</option>
        </select><br><br>

        <input type="submit" value="Calcular">
    </form>

    <hr>

    <%
        String n1 = request.getParameter("num1");
        String n2 = request.getParameter("num2");
        String operacion = request.getParameter("op");

        if (n1 != null && n2 != null && operacion != null) {
            try {
                int num1 = Integer.parseInt(n1);
                int num2 = Integer.parseInt(n2);
                int resultado = 0;
                boolean valido = true;

                switch (operacion) {
                    case "SUMA":
                        resultado = num1 + num2;
                        break;
                    case "RESTA":
                        resultado = num1 - num2;
                        break;
                    case "PRODUCTO":
                        resultado = num1 * num2;
                        break;
                    case "DIVISION":
                        if (num2 != 0) {
                            resultado = num1 / num2;
                        } else {
                            out.println("<p style='color:red'>Error: división por cero</p>");
                            valido = false;
                        }
                        break;
                }

                if (valido) {
                    out.println("<h2>Resultado: " + resultado + "</h2>");
                }

            } catch (NumberFormatException e) {
                out.println("<p style='color:red'>Error: debes introducir números enteros válidos</p>");
            }
        }
    %>
</body>
</html>
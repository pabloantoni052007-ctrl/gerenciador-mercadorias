package br.com.zerium.gerenciador.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/sistema_mercadorias?serverTimezone=UTC";

    private static final String USUARIO = "root";


    private static final String SENHA = "051016";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: ", e);
        }
    }
}
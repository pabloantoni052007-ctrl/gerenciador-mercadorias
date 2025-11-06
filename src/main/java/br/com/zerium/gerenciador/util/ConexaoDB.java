package br.com.zerium.gerenciador.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {
    // A URL é montada com o host, a porta e o nome do banco de dados.
    // O "?serverTimezone=UTC" é uma boa prática para evitar problemas de fuso horário.
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_mercadorias?serverTimezone=UTC&allowPublicKeyRetrieval=true";

    // Usuário do banco de dados (o mesmo que você usa no DBeaver)
    private static final String USUARIO = "root";

    // Senha do banco de dados (a mesma que você usa no DBeaver)
    private static final String SENHA = "1327"; // <-- TROQUE PELA SUA SENHA REAL

    // O resto do código permanece o mesmo...
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: ", e);
        }
    }
}
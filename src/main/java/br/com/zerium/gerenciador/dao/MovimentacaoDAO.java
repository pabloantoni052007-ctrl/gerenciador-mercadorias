package br.com.zerium.gerenciador.dao;

import br.com.zerium.gerenciador.model.Movimentacao;
import br.com.zerium.gerenciador.util.ConexaoDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    public void registrarMovimentacao(Movimentacao movimentacao) {
        String sql = "INSERT INTO movimentacoes (produto_id, tipo, quantidade_movida, preco_venda, observacao) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movimentacao.getProdutoId());
            stmt.setString(2, movimentacao.getTipo());
            stmt.setInt(3, movimentacao.getQuantidadeMovida());
            stmt.setDouble(4, movimentacao.getPrecoVenda()); // 👈 adicionamos o preço
            stmt.setString(5, movimentacao.getObservacao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Movimentacao> listarTodas() {
        List<Movimentacao> movimentacoes = new ArrayList<>();
        String sql = "SELECT m.*, p.nome AS nome_produto FROM movimentacoes m " +
                "JOIN produtos p ON m.produto_id = p.id ORDER BY m.data_movimentacao DESC";

        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movimentacao mov = new Movimentacao(
                        rs.getInt("produto_id"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade_movida"),
                        rs.getDouble("preco_venda"), // ✅ Agora lê o preço
                        rs.getString("observacao")
                );
                mov.setId(rs.getInt("id"));
                mov.setDataMovimentacao(rs.getTimestamp("data_movimentacao"));
                mov.setNomeProduto(rs.getString("nome_produto"));
                movimentacoes.add(mov);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movimentacoes;
    }


    public List<Movimentacao> listarVendas() {
        List<Movimentacao> vendas = new ArrayList<>();
        String sql = "SELECT m.*, p.nome AS nome_produto FROM movimentacoes m " +
                "JOIN produtos p ON m.produto_id = p.id " +
                "WHERE m.tipo = 'VENDA' ORDER BY m.data_movimentacao DESC"; // ✅ Corrigido o filtro

        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Movimentacao mov = new Movimentacao(
                        rs.getInt("produto_id"),
                        rs.getString("tipo"),
                        rs.getInt("quantidade_movida"),
                        rs.getDouble("preco_venda"), // ✅ Agora lê o preço
                        rs.getString("observacao")
                );
                mov.setId(rs.getInt("id"));
                mov.setDataMovimentacao(rs.getTimestamp("data_movimentacao"));
                mov.setNomeProduto(rs.getString("nome_produto"));
                vendas.add(mov);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vendas;
    }


    public List<Object[]> listarFaturamentoMensal() {
        List<Object[]> resultado = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(data_movimentacao, '%M') AS mes_nome, " +
                "MONTH(data_movimentacao) AS mes_num, " +
                "COUNT(*) AS total_pedidos, " +
                "SUM(preco_venda * quantidade_movida) AS total_vendas " +
                "FROM movimentacoes " +
                "WHERE tipo = 'VENDA' " +
                "GROUP BY mes_num, mes_nome " +
                "ORDER BY mes_num;";
        try (Connection conn = ConexaoDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultado.add(new Object[]{
                        rs.getString("mes_nome"),
                        rs.getInt("total_pedidos"),
                        rs.getDouble("total_vendas")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultado;
    }



}
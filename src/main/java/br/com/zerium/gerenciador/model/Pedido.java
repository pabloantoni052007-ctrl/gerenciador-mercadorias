package br.com.zerium.gerenciador.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private int id;
    private Timestamp dataPedido;
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
        this.dataPedido = new Timestamp(System.currentTimeMillis());
    }

    public void adicionarItem(Produto produto, int quantidade, double precoVenda) {
        itens.add(new ItemPedido(produto, quantidade, precoVenda));
    }

    public double calcularTotal() {
        return itens.stream()
                .mapToDouble(i -> i.getPrecoVenda() * i.getQuantidade())
                .sum();
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public Timestamp getDataPedido() {
        return dataPedido;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}

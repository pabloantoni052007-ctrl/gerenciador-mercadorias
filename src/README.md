# 🛒 Sistema de Gerenciamento de Mercadorias

Este é um sistema desktop desenvolvido em **Java (Swing + JDBC + MySQL)** para o gerenciamento de produtos, controle de estoque e registro de vendas.  
O sistema foi projetado para pequenas lojas, seguindo boas práticas de **organização de código e princípios SOLID**.

---

## ⚙️ Funcionalidades Principais

### 🧩 Módulos do Sistema
- **Gerenciar Estoque**
    - Adicionar, atualizar e remover produtos.
    - Validação de campos (nome, descrição, preço e quantidade).
    - Registro automático de movimentações (entradas e saídas).

- **Registrar Venda**
    - Seleção de produtos disponíveis.
    - Registro de venda com preço e observação.
    - Controle automático de estoque e movimentação do produto.

- **Relatório de Movimentações**
    - Histórico completo de entradas e saídas.
    - Mostra data, tipo, quantidade e observação de cada movimentação.

- **Relatório Mensal**
    - Gráfico dinâmico mostrando faturamento total e número de pedidos por mês.
    - Geração automática com base nas vendas registradas no banco de dados.

---

## 🧠 Princípios Utilizados
O projeto foi desenvolvido seguindo os princípios do **SOLID**, com foco em:
- **S (Single Responsibility):** cada classe tem apenas uma responsabilidade.
- **O (Open/Closed):** classes abertas para extensão e fechadas para modificação.
- **D (Dependency Inversion):** uso de abstrações para reduzir o acoplamento.

---

## 💾 Tecnologias Utilizadas
- **Linguagem:** Java 17+
- **Interface:** Swing (FlatLaf Look and Feel)
- **Banco de Dados:** MySQL 8.0
- **Gerenciador de Dependências:** Maven
- **IDE Recomendada:** IntelliJ IDEA

---

## 🧰 Estrutura do Projeto

src/
└── main/java/br/com/zerium/gerenciador/
├── app/ → Classe principal (Main)
├── dao/ → Camada de acesso a dados (ProdutoDAO, MovimentacaoDAO)
├── model/ → Modelos de dados (Produto, Movimentacao, Pedido)
├── util/ → Conexão com banco e validações
└── view/ → Interface gráfica (TelaGerenciamento)


---

## 🧩 Instalação e Execução

### 1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/gerenciador-mercadorias.git

2. Configure o banco de dados MySQL:

-- Apaga o banco de dados se ele já existir, para começar do zero
DROP DATABASE IF EXISTS sistema_mercadorias;

-- Cria o banco de dados novamente
CREATE DATABASE sistema_mercadorias;

-- Seleciona o banco para usar
USE sistema_mercadorias;

-- Cria a tabela principal de produtos
CREATE TABLE produtos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    preco DECIMAL(10, 2) NOT NULL,
    quantidade INT NOT NULL
);

-- Cria a tabela para o log de movimentações
CREATE TABLE movimentacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    tipo ENUM('ENTRADA', 'SAIDA') NOT NULL,
    quantidade_movida INT NOT NULL,
    data_movimentacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    observacao VARCHAR(255),
    FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE
);


3. Configure o arquivo de conexão:

No arquivo ConexaoDB.java, ajuste sua senha do MySQL:
private static final String URL = "jdbc:mysql://localhost:3306/GerenciadorMercadoria";
private static final String USUARIO = "root";
private static final String SENHA = "sua_senha_aqui";

4. Execute o projeto:

Abra o projeto na IDE → rode a classe Main.

🧪 Validações Implementadas

Nome: apenas letras e mínimo de 2 caracteres.

Preço: maior que zero.

Quantidade: maior que zero.

Descrição: até 255 caracteres.

👨‍💻 Autor

Pablo Antoni Pereira e João Pedro Ramos Inácio
📧 pablo_a_pereira@estudante.sesisenai.org.br e joao_pr_inacio@estudante.sesisenai.org.br
📦 Projeto acadêmico — Programação Orientada a Objetos
Instituição: UNISENAI
Professor: Luciano

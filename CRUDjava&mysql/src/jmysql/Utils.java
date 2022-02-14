package jmysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Utils {
	
	static Scanner teclado = new Scanner(System.in);
	
	public static Connection conectar() {
		String CLASSE_DRIVER ="com.mysql.cj.jdbc.Driver";
		String USUARIO = "seu_usuario";
		String SENHA = "sua_senha";
		String URL_SERVIDOR = "jdbc:mysql://localhost:3306/jmysql?useSSL=false";
		
		try {
			Class.forName(CLASSE_DRIVER);
			return DriverManager.getConnection(URL_SERVIDOR, USUARIO,SENHA);	
			
		} catch (Exception e) {
			if(e instanceof ClassNotFoundException) {
				e.printStackTrace();
				System.out.println("Verifique o driver de conexão.");
			} else {
				System.out.println("Verifique se o servidor está ativo.");
			} 
			System.exit(-42);
			return null;
		}
	}
	
	public static void desconectar(Connection conn) {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Não foi possível fechar a conexão.");
				e.printStackTrace();
			}
		}
	}
	
	public static void listar() {
		String BUSCAR_TODOS = "SELECT * FROM produtos";
		try {
			Connection conn = conectar();
			PreparedStatement produtos = conn.prepareStatement(BUSCAR_TODOS,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet res = produtos.executeQuery();
			
			
			res.last();
			int quantidadeProdutos = res.getRow();
			res.beforeFirst();
			if(quantidadeProdutos > 0) {
				
				System.out.println("Listando produtos...");
				while(res.next()) {
					System.out.println("ID: " + res.getInt(1));
					System.out.println("Nome: " + res.getString(2));
					System.out.println("Preço: " + res.getFloat(3));
					System.out.println("Quantidade em estoque: " + res.getInt(4));
					System.out.println("-------------");
				}
				
			} else {
				System.out.println("Não existem produtos cadastrados.");
			  }
	
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Erro buscando produtos");
			System.exit(43243);
			
		}
		
		
		
	}
	
	public static void inserir() {
		System.out.println("Insira o nome do produto:");
		String nome = teclado.nextLine();
		System.out.println("Insira o preço do produto:");
		float preco = teclado.nextFloat();
		System.out.println("Insira a quantidade no estoque:");
		int estoque = teclado.nextInt();
		String INSERIR = "INSERT INTO produtos (nome,preco,estoque) VALUES (?,?,?)";
		
		try {
			Connection conn = conectar();
			PreparedStatement inserir = conn.prepareStatement(INSERIR);
			inserir.setString(1, nome);
			inserir.setFloat(2, preco);
			inserir.setInt(3, estoque);
			
			inserir.executeUpdate();
			inserir.close();
			desconectar(conn);
			System.out.println("O produto " + nome + " foi inserido com sucesso");
			
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Erro inserindo produto.");
			System.exit(-42);
		}
		
		
	
	}
	
	public static void atualizar() {
		System.out.println("Insira o id do produto que você irá alterar:");
		int id = Integer.parseInt(teclado.nextLine());
		
		String PROCURAR = "SELECT * FROM produtos WHERE id=?";
		
		try {
			Connection conn = conectar();
			PreparedStatement procurar = conn.prepareStatement(PROCURAR,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			procurar.setInt(1, id);
			ResultSet res = procurar.executeQuery();
			res.last();
			int qtd = res.getRow();
			res.beforeFirst();
			
			if(qtd > 0) {
				System.out.println("Insira o nome do produto:");
				String nome = teclado.nextLine();
				System.out.println("Insira o preco do produto:");
				float preco = (float)teclado.nextFloat();
				System.out.println("Insira a quantidade em estoque:");
				int estoque = teclado.nextInt();
				
				String ATUALIZAR = "UPDATE produtos SET nome=?, preco=?, estoque=? WHERE id=?";
				PreparedStatement atualizar = conn.prepareStatement(ATUALIZAR);
				atualizar.setString(1, nome);
				atualizar.setFloat(2, preco);
				atualizar.setInt(3, estoque);
				atualizar.setInt(4, id);
				
				atualizar.executeUpdate();
				atualizar.close();
				desconectar(conn);
				System.out.println("O produto foi atualizado com sucesso.");
			
			} else {
				System.out.println("Não existe produto com o id " + id);
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Erro ao atualizar produto");
			System.exit(-42);
		}
		
	}
	
	public static void deletar() {
		System.out.println("Insira o id do produto que você quer deletar:");
		int id = Integer.parseInt(teclado.nextLine());
		
		try {
			 String PROCURAR = "SELECT * FROM produtos WHERE id=?";				
			 Connection conn = conectar();
			 PreparedStatement procura  = conn.prepareStatement(PROCURAR,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			 procura.setInt(1, id);
			 ResultSet res = procura.executeQuery();
			 res.last();
			 int qtd = res.getRow();
			 res.beforeFirst();
			 
			 if(qtd > 0) {
				 String DELETAR = "DELETE FROM produtos WHERE id=?";
				 PreparedStatement deletar = conn.prepareStatement(DELETAR);
				 deletar.setInt(1, id);
				 deletar.executeUpdate();
				 deletar.close();
				 desconectar(conn);
				 System.out.println("O produto com id " + id + " foi deletado com sucesso.");
				 
				 
			 } else {
				 System.out.println("Não existe produto com esse id.");
			 }
			
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println("Erro ao deletar produto.");
			System.exit(-42);;
		}
	}
	
	public static void menu() {
		System.out.println("==================Gerenciamento de Produtos===============");
		System.out.println("Selecione uma opção: ");
		System.out.println("1 - Listar produtos.");
		System.out.println("2 - Inserir produtos.");
		System.out.println("3 - Atualizar produtos.");
		System.out.println("4 - Deletar produtos.");
		
		int opcao = Integer.parseInt(teclado.nextLine());
		if(opcao == 1) {
			listar();
		}else if(opcao == 2) {
			inserir();
		}else if(opcao == 3) {
			atualizar();
		}else if(opcao == 4) {
			deletar();
		}else {
			System.out.println("Opção inválida.");
		}
	}
}

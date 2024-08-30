package com.example.bdestoque.service;

import com.example.bdestoque.model.Produto;
import com.example.bdestoque.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.hibernate.procedure.ProcedureOutputs;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService{
    private final ProdutoRepository produtoRepository;
    public ProdutoService(ProdutoRepository produtoRepository){
        this.produtoRepository = produtoRepository;
    }
    public List<Produto> buscarTodosProdutos(){
        return produtoRepository.findAll();
    }
    @Transactional
    public Produto salvarProduto(Produto produto){
        return produtoRepository.save(produto);
    }
    public Produto buscarProdutoPorId(Long id){
        return produtoRepository.findById(id).orElseThrow(()->
                new RuntimeException("Produto não encontrado"));
    }
    @Transactional
    public Optional<Produto> excluirProduto(Long id) {
        Optional<Produto> produto = produtoRepository.findById(id);
        if(produto.isPresent()){
            produtoRepository.delete(produto.get());
            return produto;
        }else{
            return Optional.empty();
        }
    }
    public List<Produto> buscarPorNome(String nome){
        return produtoRepository.findByNomeLikeIgnoreCase(nome);
    }
    public int excluirProdutoPorQuantidade(int quantidade){
        int quantExcluidos = produtoRepository.countByQuantidadeestoqueIsLessThanEqual(quantidade);
        if (quantExcluidos > 0){
            produtoRepository.deleteByQuantidadeestoqueIsLessThanEqual(quantidade);
        }
        return quantExcluidos;
    }
    public List<Produto> buscarPorNomePreco(String nome, double preco){
        return produtoRepository.findByNomeLikeIgnoreCaseAndPrecoLessThan(nome, preco);
    }

}

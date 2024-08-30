package com.example.bdestoque.repository;

import com.example.bdestoque.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

//    @Modifying
//    @Query ("DELETE FROM Produto obj WHERE: id = obj.id")
//    void deleteById(long id);
    List<Produto> findByNomeLikeIgnoreCase(String nome);
    int countByQuantidadeestoqueIsLessThanEqual(int quant);
    void deleteByQuantidadeestoqueIsLessThanEqual(int quant);
    List<Produto> findByNomeLikeIgnoreCaseAndPrecoLessThan(String nome, double preco);
}

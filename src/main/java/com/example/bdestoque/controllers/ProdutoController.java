package com.example.bdestoque.controllers;


import com.example.bdestoque.model.Produto;
import com.example.bdestoque.repository.ProdutoRepository;
import com.example.bdestoque.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/produtos")

public class ProdutoController {
    private final ProdutoService produtoService;
    private final Validator validador;

    @Autowired
    public ProdutoController(ProdutoService produtoService, Validator validador) {
        this.produtoService = produtoService;
        this.validador = validador;
    }

    @GetMapping("/selecionar")
    @Operation(summary = "Lista todos os produtos",
            description = "Retorna uma lista de todos os produtos disponíveis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content)
    })
    public List<Produto> listarProdutos() {
        return produtoService.buscarTodosProdutos();
    }

    @PostMapping("/inserir")
    @Operation(summary = "Adicionar produtos",
            description = "Adiciona um produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produtos adicionado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content)
    })
    public ResponseEntity<String> inserirProduto(@Valid @RequestBody  Produto produto, BindingResult resultado) {
        if (resultado.hasErrors()) {
            int quantityOfError = (resultado.getErrorCount());
            StringBuilder error = new StringBuilder();
            if(quantityOfError>1) {
                for(int i = 0; i < quantityOfError-1; i++) {
                    error.append(resultado.getAllErrors().get(i).getDefaultMessage()).append(" | ");
                }
                error.append((resultado.getAllErrors().get(quantityOfError-1).getDefaultMessage()));
            }
            else {
                error.append((resultado.getAllErrors().get(0).getDefaultMessage()));
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error.toString());
        }else{
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto inserido com sucesso");
        }
    }
    @DeleteMapping("/excluir/{id}")
    @Operation(summary = "Exclui os produtos por Id",
            description = "Exclui um produto disponível pelo Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto excluído com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content)
    })
    public ResponseEntity<String> excluirProduto(@PathVariable Long id){
        Optional<Produto> produtoExistente = produtoService.excluirProduto(id);
        if(produtoExistente.isPresent()){
            produtoService.excluirProduto(id);
            return ResponseEntity.ok("Produto excluído com sucesso");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Valor inserido inválido, não encontrado no Banco. Tente novamente.");
        }
    }
    @PutMapping("/atualizar/{id}")
    @Operation(summary = "Atualiza os produtos pelo Id",
            description = "Atualiza um produto disponível pelo Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content)
    })
    public ResponseEntity<String> atualizarProduto(@PathVariable Long id,
                                                   @Valid @RequestBody  Produto produtoAtualizado, BindingResult resultado){
        Produto produtoExistente = produtoService.buscarProdutoPorId(id);
        if (resultado.hasErrors()) {
                int quantityOfError = (resultado.getErrorCount());
                StringBuilder error = new StringBuilder();
            if(quantityOfError>1) {
                for(int i = 0; i < quantityOfError-1; i++) {
                    error.append(resultado.getAllErrors().get(i).getDefaultMessage()).append(" | ");
                }
                error.append((resultado.getAllErrors().get(quantityOfError-1).getDefaultMessage()));
            }
            else {
                error.append((resultado.getAllErrors().get(0).getDefaultMessage()));
            }

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error.toString());
        }else{
            Produto produto = produtoExistente;
            produto.setNome(produtoAtualizado.getNome());
            produto.setDescricao(produtoAtualizado.getDescricao());
            produto.setPreco(produtoAtualizado.getPreco());
            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoService.salvarProduto(produto);
            return ResponseEntity.ok("Produto atualizado com sucesso");
        }
    }
    @PatchMapping("/atualizarParcial/{id}")
    @Operation(summary = "Atualiza parcialmente os produtos pelo Id",
            description = "Atualiza parcialmente um produto disponível pelo Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content)
    })
    public ResponseEntity<?> atualizarProdutoParcial(@PathVariable Long id,
                                                          @Valid @RequestBody  Map<String, Object> updates, BindingResult resultado){
        try{
        Produto produto = produtoService.buscarProdutoPorId(id);
            if (updates.containsKey("nome")) {
                produto.setNome((String) updates.get("nome"));
            }
            if (updates.containsKey("descricao")) {
                produto.setDescricao((String) updates.get("descricao"));
            }
            if (updates.containsKey("preco")) {
                produto.setPreco((Double) updates.get("preco"));
            }
            if (updates.containsKey("quantidadeEstoque")) {
                produto.setQuantidadeEstoque((Integer) updates.get("quantidadeEstoque"));
            }
            //Validar os dados

            DataBinder binder = new DataBinder(produto);
            binder.setValidator(validador);
            binder.validate();
            BindingResult result = binder.getBindingResult();
            if (result.hasErrors()) {
                Map erros = validarProduto(resultado);
                return ResponseEntity.badRequest().body(erros);
//                return ResponseEntity.badRequest().body("Erro de validação");
            }

            Produto produtoSalvo = produtoService.salvarProduto(produto);
            return ResponseEntity.ok(produtoSalvo);
        }catch (RuntimeException re){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
    }
    @GetMapping("/buscarPorNome")
    @Operation(summary = "Busca o produto pelo nome",
            description = "Retorna os produtos selecionados pelo nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content)
    })
    public ResponseEntity<?> buscarPorNome(@RequestParam String nome) {
        List<Produto> listaProdutos = produtoService.buscarPorNome(nome);
        if(!listaProdutos.isEmpty()){
            return ResponseEntity.ok(listaProdutos);
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
    }
    @GetMapping("/buscarPorNomePreco")
    @Operation(summary = "Busca o produto pelo nome e preço",
            description = "Retorna os produtos selecionados pelo nome e preço")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produto retornado com sucesso",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Produto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content)
    })
    public ResponseEntity<?> buscarPorNomePreco(@RequestParam String nome,
                                                @RequestParam double preco) {
        List<Produto> listaProdutos = produtoService.buscarPorNomePreco(nome, preco);
        if(!listaProdutos.isEmpty()){
            return ResponseEntity.ok(listaProdutos);
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
    }
    public Map<String, String>  validarProduto(BindingResult resultado){
        Map<String, String> erros = new HashMap<>();
        for (FieldError error : resultado.getFieldErrors()) {
            erros.put(error.getField(), error.getDefaultMessage());
        }
        return erros;
    }
}
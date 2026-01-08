package com.analyse_crypto.app.controlleur;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.analyse_crypto.app.tables.data_crypto.repository.CryptoRepository;
import com.analyse_crypto.app.tables.utilisateurs.Compte;
import com.analyse_crypto.app.tables.utilisateurs.Portefeuille;
import com.analyse_crypto.app.tables.utilisateurs.Transaction;
import com.analyse_crypto.app.tables.utilisateurs.User;
import com.analyse_crypto.app.tables.utilisateurs.repository.CompteRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.PortefeuilleRepository;
import com.analyse_crypto.app.tables.utilisateurs.repository.TransactionRepository;
import com.analyse_crypto.app.tables.TypeTransac;
import com.analyse_crypto.app.tables.data_crypto.Crypto;

import jakarta.validation.constraints.NotBlank;

@Controller
@RequestMapping("/portfolio/")
public class PortfolioController {

    @Autowired
    PortefeuilleRepository portefeuilleRepository;

    @Autowired
    CompteRepository compteService;

    @Autowired
    TransactionRepository transactionService;

    @Autowired
    CryptoRepository cryptoRepository;


    public static class TransactionRequest {
        private Integer id_portefeuille;
        private String crypto_id;
        private String type; // ENTREE ou SORTIE
        private BigDecimal montant;

        public Integer getId_portefeuille() {
            return id_portefeuille;
        }

        public void setId_portefeuille(Integer id_portefeuille) {
            this.id_portefeuille = id_portefeuille;
        }

        public String getCrypto_id() {
            return crypto_id;
        }

        public void setCrypto_id(String crypto_id) {
            this.crypto_id = crypto_id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public BigDecimal getMontant() {
            return montant;
        }

        public void setMontant(BigDecimal montant) {
            this.montant = montant;
        }
    }

    public static class CreatePortfolioRequest {

    @NotBlank
    private String nom;

    @NotBlank
    private String crypto_id;

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCrypto_id() {
        return crypto_id;
    }

    public void setCrypto_id(String crypto_id) {
        this.crypto_id = crypto_id;
    }
}

   
    @GetMapping("/list")
    @ResponseBody
    public List<Portefeuille> listPortfolios(@AuthenticationPrincipal User user) {
        return portefeuilleRepository.findByUser(user);
    }

    @GetMapping("/{id}/comptes")
    public ResponseEntity<?> getComptes(@PathVariable int id,@AuthenticationPrincipal User user) {
        Portefeuille portefeuille = portefeuilleRepository.findByIdPortefeuilleAndUser(id, user);
        if (portefeuille == null) {
            return ResponseEntity.status(404).body("Portefeuille non trouvé");
        }
        List<Compte> comptes = compteService.findByPortefeuille(portefeuille);
        return ResponseEntity.ok(comptes);     
    }


    @GetMapping("/{id}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable int id,@AuthenticationPrincipal User user) {
        Portefeuille portefeuille = portefeuilleRepository.findByIdPortefeuilleAndUser(id, user);
        if (portefeuille == null) {
            return ResponseEntity.status(404).body("Portefeuille non trouvé");
        }
        List<Compte> comptes = compteService.findByPortefeuille(portefeuille);
        List<Transaction> transactions = transactionService.findByCompteIn(comptes);

        return ResponseEntity.ok(transactions);
    }


    @PostMapping("/transaction")
    public ResponseEntity<?> addTransaction(@RequestBody TransactionRequest req,@AuthenticationPrincipal User user) {
        Portefeuille portefeuille = portefeuilleRepository.findByIdPortefeuilleAndUser(req.getId_portefeuille(), user);
        if (portefeuille == null) {
            return ResponseEntity.status(404).body(null);
        }

        Compte compte = compteService.findByPortefeuilleAndCrypto_Symbole(portefeuille, req.getCrypto_id());

        Transaction transaction = new Transaction();
        transaction.setCompte(compte);
        transaction.setType(TypeTransac.valueOf(req.getType()));
        transaction.setMontant(req.getMontant());
        transaction.setDateTransaction(LocalDateTime.now());

        return ResponseEntity.ok(null);
    }

    @PostMapping("/create")
    @ResponseBody
    public Portefeuille createPortfolio(@RequestBody CreatePortfolioRequest req,@AuthenticationPrincipal User user) {
        Portefeuille p =new Portefeuille(user, req.getNom(),LocalDateTime.now());
        portefeuilleRepository.save(p);

        Crypto crypto = cryptoRepository.findBySymbole(req.getCrypto_id());

        Compte c =new Compte();
        c.setPortefeuille(p);
        c.setCrypto(crypto);
        c.setSolde(BigDecimal.ZERO);
        compteService.save(c);

        return p;
    }



}

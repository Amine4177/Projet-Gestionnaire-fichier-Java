/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gestionfichier;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import javafx.scene.layout.HBox;
/**
 *
 * @author SBS
 */
public class GestionFichier extends Application {
private Stage primaryStage;
    private TextField champAuteur, champTitre, champTags, champChemin;
    private TextArea champResume, champCommentaires;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
     @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("Gestionnaire de Fichiers Favoris");
       Database.creerTable();
        // Initialiser la scène principale
        Scene sceneMarquage = creerSceneMarquage();
        primaryStage.setScene(sceneMarquage);
        primaryStage.show();
    }

    // Création de la scène de marquage (Page 1)
    private Scene creerSceneMarquage() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        // Champs de saisie
        champAuteur = new TextField();
        champTitre = new TextField();
        champTags = new TextField();
        champResume = new TextArea();
        champCommentaires = new TextArea();
        champChemin = new TextField();
        champChemin.setEditable(false);

        // Bouton "Choisir le fichier"
        Button btnChoisir = new Button("Choisir le fichier");
        btnChoisir.setOnAction(e -> choisirFichier());

        // Bouton "Ajouter"
        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setOnAction(e -> ajouterFichier());

        // Boutons de navigation
        Button btnModifier = new Button("Modifier");
        btnModifier.setOnAction(e -> primaryStage.setScene(creerSceneModification()));

        Button btnAfficherTous = new Button("Afficher tous");
        btnAfficherTous.setOnAction(e -> primaryStage.setScene(creerSceneListage()));
        
        Button btnProprietes = new Button("Propriétés");
btnProprietes.setOnAction(e -> primaryStage.setScene(creerSceneProprietes()));

        // Organisation des champs
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.addRow(0, new Label("Auteur :"), champAuteur);
        grid.addRow(1, new Label("Titre* :"), champTitre);
        grid.addRow(2, new Label("Tags* (séparés par ';') :"), champTags);
        grid.addRow(3, new Label("Résumé :"), champResume);
        grid.addRow(4, new Label("Commentaires :"), champCommentaires);
        grid.addRow(5, new Label("Chemin :"), champChemin);
        grid.add(btnChoisir, 1, 6);

        root.getChildren().addAll(
            new Label("MARQUAGE DES FICHIERS"), 
            grid, 
            btnAjouter, 
            new Separator(), 
            btnModifier, 
            btnAfficherTous,
            btnProprietes
        );

        return new Scene(root, 600, 500);
    }

    // Création de la scène de modification (Page 2)
    private Scene creerSceneModification() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        // Zone d'affichage
        TextArea zoneDetails = new TextArea();
        zoneDetails.setEditable(false);
        zoneDetails.setPrefHeight(150);

        // Bouton d'affichage
        Button btnAfficher = new Button("Afficher détails");
        btnAfficher.setOnAction(e -> afficherDetails(zoneDetails));

        // Champs de modification
        TextField champId = new TextField();
        TextField champNewTitre = new TextField();
        TextField champNewAuteur = new TextField();
        TextField champNewTags = new TextField();
        TextArea champNewResume = new TextArea();
        TextArea champNewCommentaires = new TextArea();

        // Boutons d'action
        Button btnModifier = new Button("Enregistrer modifications");
        btnModifier.setOnAction(e -> modifierFichier(
            champId.getText(),
            champNewTitre.getText(),
            champNewAuteur.getText(),
            champNewTags.getText(),
            champNewResume.getText(),
            champNewCommentaires.getText()
        ));

        Button btnSupprimer = new Button("Supprimer fichier");
        btnSupprimer.setOnAction(e -> supprimerFichier(champId.getText()));

        // Organisation
        GridPane gridModif = new GridPane();
        gridModif.setVgap(5);
        gridModif.setHgap(5);
        gridModif.addRow(0, new Label("ID du fichier :"), champId);
        gridModif.addRow(1, new Label("Nouveau titre :"), champNewTitre);
        gridModif.addRow(2, new Label("Nouvel auteur :"), champNewAuteur);
        gridModif.addRow(3, new Label("Nouveaux tags :"), champNewTags);
        gridModif.addRow(4, new Label("Nouveau résumé :"), champNewResume);
        gridModif.addRow(5, new Label("Nouveaux commentaires :"), champNewCommentaires);

        // Bouton retour
        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> primaryStage.setScene(creerSceneMarquage()));

        root.getChildren().addAll(
            new Label("MODIFICATION/SUPPRESSION"),
            zoneDetails,
            btnAfficher,
            new Separator(),
            gridModif,
            btnModifier,
            btnSupprimer,
            btnRetour
        );

        return new Scene(root, 600, 650);
    }

    // Méthodes pour la scène de modification
    private void afficherDetails(TextArea zone) {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fichiers")) {

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("id"))
                  .append(" | Titre: ").append(rs.getString("titre"))
                  .append(" | Tags: ").append(rs.getString("tags"))
                  .append("\n-----------------------------------\n");
            }
            zone.setText(sb.toString());

        } catch (SQLException e) {
            System.out.println("Erreur d'affichage : " + e.getMessage());
        }
    }

    private void modifierFichier(String idStr, String newTitre, String newAuteur, 
                                String newTags, String newResume, String newCommentaires) {
        if (idStr.isEmpty()) {
            System.out.println("Erreur : ID obligatoire !");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            String sql = "UPDATE fichiers SET " +
                "titre = COALESCE(?, titre), " +
                "auteur = COALESCE(?, auteur), " +
                "tags = COALESCE(?, tags), " +
                "resume = COALESCE(?, resume), " +
                "commentaires = COALESCE(?, commentaires) " +
                "WHERE id = ?";

            try (Connection conn = Database.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, newTitre.isEmpty() ? null : newTitre);
                pstmt.setString(2, newAuteur.isEmpty() ? null : newAuteur);
                pstmt.setString(3, newTags.isEmpty() ? null : newTags);
                pstmt.setString(4, newResume.isEmpty() ? null : newResume);
                pstmt.setString(5, newCommentaires.isEmpty() ? null : newCommentaires);
                pstmt.setInt(6, id);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Modification réussie !");
                } else {
                    System.out.println("Aucun fichier trouvé avec cet ID !");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ID invalide !");
        } catch (SQLException e) {
            System.out.println("Erreur BD : " + e.getMessage());
        }
    }

    private void supprimerFichier(String idStr) {
        if (idStr.isEmpty()) {
            System.out.println("Erreur : ID obligatoire !");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            String sql = "DELETE FROM fichiers WHERE id = ?";

            try (Connection conn = Database.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, id);
                int rows = pstmt.executeUpdate();

                if (rows > 0) {
                    System.out.println("Suppression réussie !");
                } else {
                    System.out.println("Aucun fichier trouvé avec cet ID !");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("ID invalide !");
        } catch (SQLException e) {
            System.out.println("Erreur BD : " + e.getMessage());
        }
    }

    // Méthodes existantes
    private void choisirFichier() {
        FileChooser fileChooser = new FileChooser();
        File fichier = fileChooser.showOpenDialog(primaryStage);
        if (fichier != null) {
            champChemin.setText(fichier.getAbsolutePath());
        }
    }

    private void ajouterFichier() {
        if (champTitre.getText().isEmpty() || champTags.getText().isEmpty() || champChemin.getText().isEmpty()) {
            System.out.println("Erreur : Titre et Tags obligatoires !");
            return;
        }

        String sql = "INSERT INTO fichiers (chemin, titre, auteur, tags, resume, commentaires) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, champChemin.getText());
            pstmt.setString(2, champTitre.getText());
            pstmt.setString(3, champAuteur.getText());
            pstmt.setString(4, champTags.getText());
            pstmt.setString(5, champResume.getText());
            pstmt.setString(6, champCommentaires.getText());
            pstmt.executeUpdate();

            System.out.println("Fichier ajouté avec succès !");
            viderChamps();

        } catch (SQLException e) {
            System.out.println("Erreur BD : " + e.getMessage());
        }
    }

    private void viderChamps() {
        champAuteur.clear();
        champTitre.clear();
        champTags.clear();
        champResume.clear();
        champCommentaires.clear();
        champChemin.clear();
    }
    // Méthode temporaire pour la scène de listage (à compléter)

    private Scene creerSceneListage() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        // Champs de recherche
        TextField champAuteurRecherche = new TextField();
        TextField champTitreRecherche = new TextField();
        TextField champTagRecherche = new TextField();

        GridPane gridRecherche = new GridPane();
        gridRecherche.setVgap(5);
        gridRecherche.setHgap(10);
        gridRecherche.addRow(0, new Label("Auteur :"), champAuteurRecherche);
        gridRecherche.addRow(1, new Label("Titre :"), champTitreRecherche);
        gridRecherche.addRow(2, new Label("Tag :"), champTagRecherche);

        // Zone d'affichage
        TextArea zoneResultats = new TextArea();
        zoneResultats.setEditable(false);
        zoneResultats.setPrefHeight(300);
        zoneResultats.setStyle("-fx-font-family: monospace;");

        // Boutons
        Button btnAfficherTout = new Button("Afficher tout");
        btnAfficherTout.setOnAction(e -> afficherTousFichiers(zoneResultats));

        Button btnRechercher = new Button("Rechercher");
        btnRechercher.setOnAction(e -> rechercherFichiers(
            champAuteurRecherche.getText(),
            champTitreRecherche.getText(),
            champTagRecherche.getText(),
            zoneResultats
        ));

        Button btnExporter = new Button("Exporter");
        btnExporter.setOnAction(e -> exporterVersFichier(zoneResultats.getText()));

        HBox boutons = new HBox(10, btnAfficherTout, btnRechercher, btnExporter);

        root.getChildren().addAll(
            new Label("LISTAGE ET RECHERCHE"),
            gridRecherche,
            new Separator(),
            zoneResultats,
            boutons,
            new Button("Retour") {{
                setOnAction(e -> primaryStage.setScene(creerSceneMarquage()));
            }}
        );

        return new Scene(root, 700, 500);
    }

    // Nouvelles méthodes pour le listage/recherche
    private void afficherTousFichiers(TextArea zone) {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fichiers")) {

            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Titre: ").append(rs.getString("titre"))
                  .append("\nAuteur: ").append(rs.getString("auteur"))
                  .append("\nTags: ").append(rs.getString("tags"))
                  .append("\nChemin: ").append(rs.getString("chemin"))
                  .append("\n────────────────────────────────────\n");
            }
            zone.setText(sb.toString());

        } catch (SQLException e) {
            System.out.println("Erreur d'affichage : " + e.getMessage());
        }
    }

    private void rechercherFichiers(String auteur, String titre, String tag, TextArea zone) {
        String sql = "SELECT titre, chemin FROM fichiers WHERE " +
                     "(auteur LIKE ? OR ? IS NULL) AND " +
                     "(titre LIKE ? OR ? IS NULL) AND " +
                     "(tags LIKE ? OR ? IS NULL)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + auteur + "%");
            pstmt.setString(2, auteur.isEmpty() ? null : auteur);
            pstmt.setString(3, "%" + titre + "%");
            pstmt.setString(4, titre.isEmpty() ? null : titre);
            pstmt.setString(5, "%" + tag + "%");
            pstmt.setString(6, tag.isEmpty() ? null : tag);

            ResultSet rs = pstmt.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) {
                sb.append("Titre: ").append(rs.getString("titre"))
                  .append("\nChemin: ").append(rs.getString("chemin"))
                  .append("\n────────────────────────────────────\n");
            }
            zone.setText(sb.toString());

        } catch (SQLException e) {
            System.out.println("Erreur recherche : " + e.getMessage());
        }
    }

    private void exporterVersFichier(String contenu) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les résultats");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
        
        File fichier = fileChooser.showSaveDialog(primaryStage);
        if (fichier != null) {
            try (PrintWriter writer = new PrintWriter(fichier)) {
                writer.write(contenu);
                System.out.println("Export réussi !");
            } catch (Exception e) {
                System.out.println("Erreur export : " + e.getMessage());
            }
        }
    }
    private void afficherStatistiques(TextArea zone) {
    try (Connection conn = Database.getConnection()) {
        StringBuilder sb = new StringBuilder();

        // 1. Nombre total de fichiers
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM fichiers")) {
            rs.next();
            sb.append("Nombre total de fichiers : ").append(rs.getInt(1)).append("\n\n");
        }

        // 2. Liste des auteurs
        sb.append("Auteurs uniques :\n");
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT auteur FROM fichiers WHERE auteur IS NOT NULL")) {
            while (rs.next()) {
                String auteur = rs.getString("auteur");
                if(!auteur.isEmpty()) sb.append("- ").append(auteur).append("\n");
            }
            sb.append("\n");
        }

        // 3. Statistiques des tags
        sb.append("Statistiques des tags :\n");
        HashMap<String, Integer> tagCounts = new HashMap<>();
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT tags FROM fichiers")) {
            while (rs.next()) {
                String[] tags = rs.getString("tags").split(";");
                for (String tag : tags) {
                    tag = tag.trim().toLowerCase();
                    if (!tag.isEmpty()) {
                        tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                    }
                }
            }
        }

        // Tri par nombre décroissant
        tagCounts.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .forEach(entry -> {
                sb.append(String.format("- %-20s : %d fichier%s%n",
                    entry.getKey(),
                    entry.getValue(),
                    entry.getValue() > 1 ? "s" : ""));
            });

        zone.setText(sb.toString());

    } catch (SQLException ex) {
        System.out.println("Erreur statistiques : " + ex.getMessage());
    }
}
    private Scene creerSceneProprietes() {
    VBox root = new VBox(10);
    root.setPadding(new Insets(15));

    TextArea zoneStats = new TextArea();
    zoneStats.setEditable(false);
    zoneStats.setPrefHeight(400);
    zoneStats.setStyle("-fx-font-family: monospace;");

    Button btnAfficherStats = new Button("Afficher propriétés");
    btnAfficherStats.setOnAction(e -> afficherStatistiques(zoneStats));

    root.getChildren().addAll(
        new Label("STATISTIQUES DES FICHIERS"),
        zoneStats,
        btnAfficherStats,
        new Button("Retour") {{
            setOnAction(e -> primaryStage.setScene(creerSceneMarquage()));
        }}
    );

    return new Scene(root, 600, 500);
}
    
    
    
    
}

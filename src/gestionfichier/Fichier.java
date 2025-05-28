/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestionfichier;

/**
 *
 * @author SBS
 */
public class Fichier {
    private int id;
    private String chemin;
    private String titre;
    private String auteur;
    private String tags;
    private String resume;
    private String commentaires;

    public Fichier(int id, String chemin, String titre, String auteur, String tags, String resume, String commentaires) {
        this.id = id;
        this.chemin = chemin;
        this.titre = titre;
        this.auteur = auteur;
        this.tags = tags;
        this.resume = resume;
        this.commentaires = commentaires;
    }

    // Getters
    public int getId() { return id; }
    public String getChemin() { return chemin; }
    public String getTitre() { return titre; }
    public String getAuteur() { return auteur; }
    public String getTags() { return tags; }
    public String getResume() { return resume; }
    public String getCommentaires() { return commentaires; }
}

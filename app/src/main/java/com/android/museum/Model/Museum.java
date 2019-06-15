package com.android.museum.Model;

import com.android.museum.SQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Museum {
    private String id;
    private String nom;
    private String periode_ouverture;
    private String adresse;
    private String ville;
    private boolean ferme;
    private String fermeture_annuelle;
    private String site_web;
    private String cp;
    private String region;
    private String dept;

    private String time = null;

    public Museum(String id, String nom, String periode_ouverture, String adresse, String ville,
                  boolean ferme, String fermeture_annuelle, String site_web, String cp,
                  String region, String dept) {
        this.id = id;
        this.nom = nom;
        this.periode_ouverture = periode_ouverture;
        this.adresse = adresse;
        this.ville = ville;
        this.ferme = ferme;
        this.fermeture_annuelle = fermeture_annuelle;
        this.site_web = site_web;
        this.cp = cp;
        this.region = region;
        this.dept = dept;
    }

    public Museum(String id, String nom, String periode_ouverture, String adresse, String ville,
                  boolean ferme, String fermeture_annuelle, String site_web, String cp,
                  String region, String dept, String time) {
        this.id = id;
        this.nom = nom;
        this.periode_ouverture = periode_ouverture;
        this.adresse = adresse;
        this.ville = ville;
        this.ferme = ferme;
        this.fermeture_annuelle = fermeture_annuelle;
        this.site_web = site_web;
        this.cp = cp;
        this.region = region;
        this.dept = dept;
        this.time = time;
    }

    public Museum() {

    }

    public Museum(HashMap<String, String> map) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPeriode_ouverture() {
        return periode_ouverture;
    }

    public void setPeriode_ouverture(String periode_ouverture) {
        this.periode_ouverture = periode_ouverture;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String isFerme() {
        String ferme;
        if(this.ferme) {
            ferme = "Oui";
        }
        else {
            ferme = "Non";
        }
        return ferme;
    }

    public void setFerme(boolean ferme) {
        this.ferme = ferme;
    }

    public String getFermeture_annuelle() {
        return fermeture_annuelle;
    }

    public void setFermeture_annuelle(String fermeture_annuelle) {
        if(fermeture_annuelle.equals("null")) {
            this.fermeture_annuelle = "Aucune";
        } else {
            this.fermeture_annuelle = fermeture_annuelle;
        }
    }

    public String getSite_web() {
        return site_web;
    }

    public void setSite_web(String site_web) {
        this.site_web = site_web;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void save(SQLiteHelper sqLiteHelper) {
        String[] detail = new String[12];
        detail[0] = this.id;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        detail[1] = format.format(new Date());
        detail[2] = this.nom;
        detail[3] =  this.periode_ouverture;
        detail[4] =  this.adresse;
        detail[5] =  this.ville;
        detail[6] = Boolean.toString(this.ferme);
        detail[7] =  this.fermeture_annuelle;
        detail[8] = this.site_web;
        detail[9] =  this.cp;
        detail[10] =  this.region;
        detail[11] =  this.dept;
        sqLiteHelper.add_museum(detail);
    }
}
